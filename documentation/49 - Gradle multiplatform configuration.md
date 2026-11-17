# Gradle multiplatform configuration

Plugins, repositories, and dependencies for a single-module project, and how a KMP module declares its targets and dependency source sets, have both been covered already. None of that changes in a KMP project — the same `plugins {}`, `dependencies {}`, and target-declaration syntax apply. What does change is how those pieces get organized once a project is multi-module and multi-target by default, which is what this topic covers: centralizing repositories, keeping builds portable across CI hosts, and avoiding repeated configuration across modules.

## Centralizing repositories in settings.gradle.kts

The `mavenCentral()`, `mavenLocal()`, and `google()` aliases were covered earlier, declared inside a module's own `build.gradle.kts`. That works for a single-module project, but a KMP project is a multi-module project by default — `shared`, `composeApp`, and any feature modules you add each have their own `build.gradle.kts`. Repeating a `repositories {}` block in every one of them means every new repository has to be added N times.

Instead, `settings.gradle.kts` centralizes repositories for the whole project, in two separate blocks with two different jobs:



```kotlin
// settings.gradle.kts
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```



`pluginManagement`'s repositories resolve the Gradle *plugins* themselves — the Android Gradle Plugin and the Kotlin Gradle plugin both need to be fetched from somewhere before any module can apply them, which is why `gradlePluginPortal()` appears here specifically. `dependencyResolutionManagement`'s repositories resolve your project's actual library dependencies, using the same aliases already covered — just declared once for every module instead of per module.

`google()` is worth calling out on its own: it wasn't strictly necessary for a plain JVM project, but it's required here, since the Android Gradle Plugin and AndroidX libraries are published to Google's Maven repository, not Maven Central.

## Keeping target declarations portable across hosts

Targets like `iosArm64()` and `iosSimulatorArm64()`, covered earlier, compile through Kotlin/Native's Apple backend, which is only available when Gradle itself is running on macOS. If your project also builds on a Linux or Windows CI runner — common for the Android- and JVM-only parts of a build — declaring iOS targets unconditionally breaks the build there, since that runner has no way to compile them.

The standard fix is to gate iOS targets behind a host check, using Kotlin/Native's `HostManager`:



```kotlin
import org.jetbrains.kotlin.konan.target.HostManager

kotlin {
    androidTarget()
    jvm()

    if (HostManager.hostIsMac) {
        iosArm64()
        iosSimulatorArm64()
    }
}
```



With this in place, the same `build.gradle.kts` behaves correctly everywhere: on a Linux or Windows runner, only `androidTarget()` and `jvm()` get declared and built; on a macOS runner (or a developer's own Mac), the iOS targets are added too.

## Convention plugins for multi-module projects

As a KMP project grows, it's common to split shared logic across several modules — a `:core` module plus a handful of feature modules, for instance — each needing the same target declarations and much of the same `commonMain` dependency setup. Copy-pasting that `kotlin {}` block into every module's `build.gradle.kts` means every target added or dependency bumped has to be repeated in every file.

A convention plugin solves this: it's an ordinary Gradle plugin, written once and applied by each module with a single line, instead of repeating the configuration itself. It's typically defined in an included build — a small separate Gradle project, commonly named `build-logic` (the older `buildSrc` directory works the same way, though `build-logic` is now the more common choice):



```kotlin
// build-logic/convention/src/main/kotlin/kmp-feature-module.gradle.kts
plugins {
    kotlin("multiplatform")
}

kotlin {
    androidTarget()
    jvm()

    if (org.jetbrains.kotlin.konan.target.HostManager.hostIsMac) {
        iosArm64()
        iosSimulatorArm64()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(project(":core"))
        }
    }
}
```



Each feature module's own `build.gradle.kts` then shrinks to a single line:



```kotlin
// feature-tasks/build.gradle.kts
plugins {
    id("kmp-feature-module")
}
```



Adding a new target, or bumping a shared dependency, now happens once in the convention plugin rather than once per module.

## Conclusion

A KMP project doesn't need a different set of Gradle fundamentals from what's already covered — it needs the same plugins, repositories, and target declarations organized for a project that's multi-module and multi-target by default: repositories centralized in `settings.gradle.kts`, target declarations gated by host so builds stay portable across CI, and convention plugins once a project outgrows a single shared module.

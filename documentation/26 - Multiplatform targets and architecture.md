# Multiplatform targets and architecture

In the previous topic, you saw why KMP exists, how a project is set up, and how `commonMain` and platform-specific source sets work together through `expect`/`actual` declarations. Now, we'll look at exactly which platforms and architectures KMP can target, how you declare those targets in Gradle, how source sets actually form a hierarchy behind the scenes, and where developers use KMP in production today.

## Supported platforms and target architectures

Kotlin Multiplatform can compile a shared module to:

- **Android** — full access to the Android API, compiled to JVM bytecode.

- **iOS** — compiled to native binaries via Kotlin/Native. There's no single "iOS target"; you typically declare a device target (`iosArm64`) and a simulator target (`iosSimulatorArm64` for Apple silicon Macs, `iosX64` for older Intel Macs).

- **JVM** — for server-side and desktop applications.

- **Native binaries** — for Windows, macOS, and Linux, again via Kotlin/Native.

- **Web** — either Kotlin/JS, which compiles to JavaScript and is the more mature option for browser targets, or Kotlin/Wasm, which compiles to WebAssembly and is still maturing toward the same stability level as Android and iOS.

Kotlin Multiplatform has been an officially Stable technology since November 2023, and Google announced official support for using it to share business logic between Android and iOS at Google I/O 2024 — so the platform is what teams are shipping with today.

## Declaring targets in Gradle

You declare targets inside the `kotlin {}` block of your module's `build.gradle.kts`:



```kotlin
kotlin {
    androidTarget()
    jvm()
    iosArm64()
    iosSimulatorArm64()
    js {
        browser()
        nodejs()
    }
    linuxX64()
    mingwX64()
}
```



A couple of naming notes, since this is an area that's shifted over time and can trip you up when reading older tutorials:

- Use `androidTarget()`, not the older `android()`. `android()` was deprecated years ago to free up that name for a newer, separately maintained Android Gradle plugin.

- There's no single `ios()` shortcut anymore — you declare the specific architectures your project needs (`iosArm64()` for real devices, `iosSimulatorArm64()`/`iosX64()` for simulators).

Each function you call — `androidTarget()`, `jvm()`, `iosArm64()`, and so on — tells the Kotlin Gradle plugin which compiler backend to use and which binaries to produce for that platform.

## How source sets form a hierarchy

Introduction to KMP showed you `commonMain` plus one source set per platform (`androidMain`, `iosMain`, and so on). In a real project with multiple similar targets, that's not quite the full picture.

When you declare `iosArm64()` and `iosSimulatorArm64()` together, the Kotlin Gradle plugin automatically creates an **intermediate source set** called `iosMain` that sits between `commonMain` and the two device-specific source sets:



```kotlin
commonMain
   └── iosMain
         ├── iosArm64Main
         └── iosSimulatorArm64Main
```



This happens automatically — you don't need to wire up `dependsOn` relationships by hand for standard cases like this. `iosMain` is where you'll write almost all of your iOS-specific code, since device and simulator code is normally identical. The plugin can also build similar intermediate groups for other target families (for example, an `appleMain` source set shared by iOS, macOS, watchOS, and tvOS, or a `nativeMain` source set shared by every Kotlin/Native target), but it only creates the ones your declared targets actually need.

Dependencies still flow downward through this hierarchy. Anything you add to `commonMain` is available everywhere; anything added to `iosMain` is available only to the iOS-specific source sets beneath it:



```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:3.1.0")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-okhttp:3.1.0")
        }
    }
}
```



Only multiplatform-ready libraries can go in `commonMain` — the compiler needs to translate them to every target you've declared. Platform-specific source sets, on the other hand, can use libraries that only exist for that one platform.

## KMP in production

KMP's adoption has grown quickly since it went Stable — usage among surveyed developers roughly doubled between 2024 and 2025. A few examples of how teams use it:

- **Block (Cash App)** moved from a shared JavaScript layer to Kotlin Multiplatform, which made it easier for Android and iOS engineers to collaborate on the same business logic.

- **McDonald's** started by sharing payment logic across its app's Android and iOS versions, then expanded KMP to the rest of the app, moving from separate platform teams to one unified mobile team.

- **Forbes** shares more than 80% of its business logic between Android and iOS, letting it ship new features on both platforms at the same time.

The common thread across these adopters isn't UI — it's consistent business logic, reduced duplication, and being able to fix a bug once instead of twice. If you also want to share UI code, that's what Compose Multiplatform (built on top of KMP) is for, but that's a separate topic in its own right.

## Conclusion

In this topic, you've learned which platforms and architectures KMP can target, how to declare those targets with current Gradle syntax, how intermediate source sets like `iosMain` are created automatically to avoid duplicating platform-specific code, and how dependencies propagate through that hierarchy. With this deeper picture of project structure in place, you're ready to move on to setting up your tools and building your first project.

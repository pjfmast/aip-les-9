# Basic project in KMP

Understanding the intricacies of a KMP project structure is essential for maximizing productivity and effectively navigating the challenges that may arise during development. With a solid grasp of project components, you can move faster, integrate new libraries more easily, and troubleshoot issues efficiently.

Previously, we covered how you declare targets and how source sets form a hierarchy. In this topic, we walk through a complete sample project: its file layout, its Gradle configuration, and how to modify and run it end to end.

## KMP project structure

A typical KMP project consists of several key components that work together to enable cross-platform development. We'll use a sample project, HyperGreeting, as our reference. If you'd like to follow along, create a new KMP project using the [JetBrains Kotlin Multiplatform Wizard](http://kmp.jetbrains.com), targeting Android, iOS (with native UI), and Desktop.

![Creating a new project using the online project wizard.](https://ucarecdn.com/76f7b65e-128d-401c-af6b-be50381ff932/)

When opening the project in Android Studio or IntelliJ IDEA, switch the *Project tool* window view from *Android* to *Project*. This reveals the full file structure, which is more convenient for multiplatform development.

![Switching from Android to Project view.](https://ucarecdn.com/2d8a6e6a-69fb-453c-98f2-26afc11a42eb/)

Let's start at the root level of the project, where you'll find the project's Gradle build files:



```kotlin
HyperGreeting/
├── ...
├── build.gradle.kts
└── settings.gradle.kts
```



The project-level `build.gradle.kts` defines project-wide configuration. It often declares plugins with `apply false`, so each module can opt in individually:



```kotlin
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidLibrary) apply false
    alias(libs.plugins.jetbrainsCompose) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
}
```



The `settings.gradle.kts` file defines the project's module structure. Each `include` statement adds a module:



```kotlin
rootProject.name = "HyperGreeting"
// ...
include(":composeApp")
include(":shared")
```



Modules can serve different roles:

- **Platform-specific** (for example, `:androidApp` or `:desktopApp`).

- **Purpose-specific** (for example, a `:server` module running a Ktor server).

- **Shared modules for business logic** (`:shared`).

- **Shared modules for UI logic** (often `:composeApp`).

In our sample project:

- `:composeApp` is a Kotlin Multiplatform module that typically contains the UI logic, built with Compose Multiplatform. Even if the project initially targets only one platform, the module is structured to expand to others later. Since UI sits at the top of the app architecture, this module's Gradle file contains the entry points for every targeted platform.

- `:shared` contains the business logic shared across all targeted platforms — the core of the project.

The `gradle` folder holds Gradle configuration:



```kotlin
HyperGreeting/
├── gradle/            <---
│   ├── wrapper/
│   └── libs.versions.toml
├── build.gradle.kts
└── settings.gradle.kts
```



The `wrapper` folder pins a consistent Gradle version across machines and CI. `libs.versions.toml` is the version catalog, covered in Introduction to KMP.

Each module also has its own `build.gradle.kts`:



```kotlin
HyperGreeting/
├── composeApp/
│   ├── ...
│   └── build.gradle.kts  <---
├── gradle/
├── shared/
│   ├── ...
│   └── build.gradle.kts  <---
├── build.gradle.kts
└── settings.gradle.kts
```



For non-multiplatform modules, this file configures platform-specific settings. For multiplatform modules like `shared`, it includes:

- The KMP plugin.

- Target definitions.

- Source sets with dependencies.

- Additional configuration (for example, Android-specific settings).

Here's a simplified version of the `shared` module's `build.gradle.kts`:



```kotlin
plugins {
    alias(libs.plugins.kotlinMultiplatform) // The KMP plugin
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget()
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            // Multiplatform dependencies are declared here
        }
    }
}

android {
    // Android-specific settings
}
```



This configuration targets Android, JVM/desktop, and iOS, defines source sets, and specifies dependencies for each platform. As covered in Multiplatform Targets and Architecture, declaring `iosArm64()` and `iosSimulatorArm64()` together is what makes the Kotlin Gradle plugin generate the `iosMain` intermediate source set automatically.

The `iosApp` folder is a special case:



```kotlin
HyperGreeting/
├── composeApp/
├── gradle/
├── iosApp/   <---
├── shared/
├── build.gradle.kts
└── settings.gradle.kts
```



Unlike the other folders, `iosApp` isn't a Gradle module — it's an Xcode project that builds into an iOS application and depends on `shared` as an iOS framework. As described in Tools for KMP, that framework can reach the Xcode project through direct integration, CocoaPods, or Swift Package Manager; the wizard's default is direct integration.

Non-shared modules contain code tailored to a single target platform — an `androidApp` module, for instance, would contain only Android-specific code. Shared modules have a more layered structure to accommodate code used across multiple platforms:



```kotlin
HyperGreeting/
├── ...
├── shared/
│   └── src/
│       ├── androidMain/
│       ├── commonMain/
│       ├── iosMain/
│       └── jvmMain/
└── ...
```



`commonMain` holds code shared across all platform targets; `androidMain`, `iosMain`, and the other platform-specific folders are generated based on the targets declared in the module's Gradle file.

## Shared code

`commonMain` is the core of any multiplatform module — it's created automatically once the Kotlin Multiplatform plugin has at least one target declared inside the `kotlin {}` block, and any code there can compile to any of the module's targets.



```kotlin
shared/
└── src/
    ├── androidMain/
    ├── commonMain/
    │   └── ...
    │       ├── Greeting.kt
    │       └── Platform.kt
    ├── iosMain/
    └── jvmMain/
```



Dependencies needed by code in `commonMain` are declared inside the `sourceSets {}` block:



```kotlin
kotlin {
    // ... Targets are defined here

    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")
        }
    }
}
```



This adds `kotlinx-coroutines-core` to `commonMain`, making coroutines available to your common code. Only multiplatform libraries can go in `commonMain`, since the compiler needs to translate them to every declared target.

Each target also has its own source set with a directory for platform-specific code:



```kotlin
shared/
└── src/
    ├── androidMain/  <---
    ├── commonMain/
    ├── iosMain/      <---
    └── jvmMain/      <---
```



These source sets declare their own dependencies the same way:



```kotlin
kotlin {
    // ... Targets are defined here

    sourceSets {
        // ...
        androidMain.dependencies {
            // Android-specific dependencies are declared here
        }
        iosMain.dependencies {
            // iOS-specific dependencies are declared here
        }
        jvmMain.dependencies {
            // JVM-specific dependencies are declared here
        }
    }
}
```



Platform-specific source sets can use platform-specific libraries — `androidMain` can use Android libraries, `iosMain` can use iOS ones — since the compiler produces a separate binary per target by combining `commonMain` with that target's own source set.

As introduced in Introduction to KMP, the `expect`/`actual` mechanism bridges common and platform-specific code. You define a common API in `commonMain` and provide the implementation in each target's source set. For example, logging differs between Android (`Log.d()`) and iOS (`print()`):



```kotlin
// In commonMain/.../Logging.kt
expect fun log(message: String)

// In androidMain/.../Logging.android.kt
import android.util.Log

actual fun log(message: String) {
    Log.d("KMP", message)
}

// In iosMain/.../Logging.ios.kt
actual fun log(message: String) {
    print(message)
}
```



Calling `log()` in common code then uses the right implementation for each platform. When writing common code, share as much logic as possible, and reach for `expect`/`actual` only where platform-specific behavior is actually needed.

![Compile targets.](https://ucarecdn.com/e6eefb2f-feec-4a3b-b859-fc45780cc770/)

## Modify and run

Let's walk through modifying and running the HyperGreeting project.

We'll replace the wizard-generated code with a simple "Hello from $platform" implementation in `commonMain`.

Replace the content of `Greeting.kt` inside `commonMain`:



```kotlin
// shared/src/commonMain/.../Greeting.kt
class Greeting {
    fun greet(): String = "Hello from $platform!"
}

expect val platform: String
```



`platform` is declared with `expect`, since its actual value comes from each platform's implementation.

Provide the platform-specific implementations:



```kotlin
// shared/src/androidMain/.../Greeting.android.kt
actual val platform = "Android"

// shared/src/iosMain/.../Greeting.ios.kt
actual val platform = "iOS"

// shared/src/desktopMain/.../Greeting.jvm.kt
actual val platform = "Desktop"
```



When the compiler generates code for a given platform, it matches the `expect` declaration with the corresponding `actual`. For Android, it effectively produces:



```kotlin
class Greeting {
    fun greet(): String = "Hello from $platform!"
}

val platform = "Android"
```



> **Note:**

This Kotlin code is then compiled further into platform-specific binaries.

Now let's use this shared code in each platform's UI.

Update the Compose Multiplatform UI for Android and Desktop, in `composeApp/src/commonMain/.../App.kt`:



```kotlin
@Composable
@Preview
fun App() {
    MaterialTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colors.background
        ) {
            GreetingView(Greeting().greet())
        }
    }
}

@Composable
fun GreetingView(text: String) {
    Text(text = text)
}
```



Update the platform-specific entry points:



```kotlin
// composeApp/src/androidMain/.../MainActivity.kt
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

// composeApp/src/desktopMain/.../main.kt
fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "HyperGreeting",
    ) {
        App()
    }
}
```



For the iOS UI, open `iosApp/iosApp.xcodeproj/` in Xcode and replace `iosApp/iosApp/ContentView.swift` with:



```no-highlight
// iosApp/iosApp/ContentView.swift
struct ContentView: View {
    let greet = Greeting().greet()
    var body: some View {
        VStack {
            Text(greet)
            Spacer()
        }
    }
}
```



This is SwiftUI, Apple's declarative UI framework — if you're familiar with Jetpack Compose, the approach will look familiar. As covered in Tools for KMP, the Kotlin Multiplatform IDE plugin now also supports basic Swift editing directly in IntelliJ IDEA or Android Studio, so opening Xcode for a small change like this one is optional rather than required.

Finally, run each target:

**Android:**

- Make sure you have a connected Android device or a configured Android Virtual Device (AVD).

- Select the `composeApp` configuration from the run configurations dropdown.

- Click Run.

![Running the project on Android.](https://ucarecdn.com/d749adb4-4973-4bbd-81f8-ed4af8751ae2/)

**iOS:**

- Make sure Xcode is installed.

- Select the `iosApp` configuration from the run configurations dropdown in your IDE, or open `iosApp.xcodeproj` directly in Xcode.

- Select an iOS simulator or connected device, then click Run.

![Running the project on iOS.](https://ucarecdn.com/17081dd1-c94b-4b8a-acf1-3f9945124c8b/)

**Desktop:**

- Open `main.kt` inside `composeApp/desktopMain/`.

- Click the Run button in the gutter next to `fun main()` and select **Run 'MainKt'**.

- If you hit a `java.lang.ClassNotFoundException`, run `./gradlew run` from the terminal instead.

![Running the project on Desktop.](https://ucarecdn.com/b61c5933-aff0-44d1-a4f7-acd39e954fdb/)

This example shows the core idea of Kotlin Multiplatform in practice: the logic lives once in `commonMain`, and each platform uses it with only the platform-specific code it actually needs. Changing the shared code updates every platform at once, while still leaving room for platform-specific implementations where they're needed.

## Conclusion

In this topic, we walked through the structure of a basic KMP project — its build files, modules, and source sets — saw how shared code works across platforms through `expect`/`actual`, and modified and ran a simple multiplatform application on Android, iOS, and desktop. With this practical foundation in place, you're ready to start building and structuring your own KMP projects.

# Introduction to Kotlin Multiplatform (KMP)

A key strength of Kotlin lies in its ability to be compiled into different source code formats. Beyond JVM bytecode, Kotlin can also be compiled into JavaScript (or WebAssembly) and can use LLVM to produce native code. This versatility makes Kotlin a practical choice for building cross-platform applications.

Building on this versatility, **Kotlin Multiplatform (KMP)** is a feature of the Kotlin programming language that lets developers share code across multiple platforms, such as Android, iOS, web, and desktop. With KMP, you write common business logic once and reuse it across different platforms, reducing development time and effort.

In this topic, you'll explore the benefits and use cases of KMP and learn how to set up a KMP project, define shared and platform-specific code, manage dependencies, and configure builds for KMP projects.

> **Note:**

You may have also encountered the term KMM (Kotlin Multiplatform Mobile). This was an earlier label for KMP projects targeting only mobile platforms; it has since been deprecated in favor of the broader KMP name.

## Benefits and use cases of KMP

One of the main advantages of KMP is the ability to share code across multiple platforms. This lets you write common business logic — data models, algorithms, utility functions — in a **shared module** and reuse it across platforms. Sharing code this way reduces development time, keeps behavior consistent, and cuts down on duplication.

The ability to share code extends to user interfaces as well. **Compose Multiplatform (CMP)**, a declarative UI framework built on top of KMP, lets you create UI elements once and use them across multiple platforms. We'll look at CMP in more detail in a separate topic.

KMP is particularly useful when you need to build a mobile app for both Android and iOS. Instead of maintaining two separate codebases, you create a **shared module** for **common** functionality and write platform-specific UI and integration code for each platform.

Another benefit is being able to use existing Kotlin libraries and frameworks. Many popular Kotlin libraries — **Ktor** for networking, `kotlinx.serialization` for JSON, and others — offer multiplatform support, so you can use them across all your targets.

KMP is also useful for full-stack applications: a shared module containing common business logic can run on both the client side (web or mobile) and the server side (a backend API), reducing duplication and keeping client and server behavior consistent.

![Sharing code betwenn multiple platforms.](https://ucarecdn.com/d144d85a-6dc9-41ca-8282-e236d2ba0544/)

## Setting up a KMP project

To get started with KMP, you set up a new project that supports multiplatform development. The most common starting point is the [Kotlin Multiplatform Wizard](https://kmp.jetbrains.com/):

- **Create a template using the Kotlin Multiplatform Wizard.** This online tool lets you select your desired targets (Android, iOS, desktop, and so on) and specify project metadata.

![Screenshot of Kotlin multiplatform wizard.](https://ucarecdn.com/1d274bf2-ec14-42c5-8151-92aabc83bfdd/)

- **Download the generated project template.** The wizard provides a downloadable archive containing the configured project structure.

- **Open the project in IntelliJ IDEA or Android Studio.** Both IDEs support Kotlin Multiplatform through a shared plugin, so either is a reasonable choice. The next topic goes through IDE setup and the rest of the toolchain in more detail.

As an alternative to the wizard, you can create a new KMP project directly inside IntelliJ IDEA or Android Studio once the [Kotlin Multiplatform plugin](https://plugins.jetbrains.com/plugin/14936-kotlin-multiplatform) is installed.

![Screenshot of Android Studio showing KMP template after installing the Kotlin Multiplatform Plugin.](https://ucarecdn.com/5a4856e6-2f7a-4093-a4e8-8c8db7e9ae15/)

> **Warning:**

**iOS development requirement.** Running and testing iOS-specific code from a KMP project requires a Mac with Xcode installed — this is an Apple platform requirement rather than anything specific to Kotlin. See Tools for KMP for what the current toolchain looks like on both macOS and other operating systems.

Once the project is set up, you'll typically see a structure that includes:

- **A shared module**, where you write code that can be shared across all platforms — business logic, data models, algorithms.

- **Platform-specific modules**, named after the platform they target (for example, `composeApp`, `iosApp`), that interact with platform-specific APIs.The shared module consists of multiple **source sets** (`commonMain`, `androidMain`, `iosMain`, `wasmJsMain`, etc.).

The shared module itself is made up of multiple source sets (`commonMain`, `androidMain`, `iosMain`, `wasmJsMain`, and so on). A source set is a Gradle concept: a group of files that share the same dependencies. In a KMP project, each source set is designated to target one or more platforms. Source sets fall into two broad groups:

- **The common source set**, `commonMain`, which contains shared Kotlin code.

- **Platform-specific source sets**, containing code tailored to each target platform (Kotlin/JVM for `androidMain`, Kotlin/Native for `iosMain`, Kotlin/JS or Kotlin/Wasm for `wasmJsMain`, and so on).

Here's a simplified view of a KMP project targeting Android and iOS:



```no-highlight
MyKMPProject/
├── composeApp/
│   ├── src/
│   └── build.gradle.kts
├── iosApp/
├── shared/
│   ├── src/
│   │   ├── commonMain/
│   │   ├── androidMain/
│   │   └── iosMain/
│   └── build.gradle.kts
└── build.gradle.kts
```



Inside `commonMain` (in the shared module), you can define common data models, business logic, and utility functions:



```kotlin
// Shared data model
data class User(val id: Int, val name: String, val email: String)

// Shared utility function
fun isValidEmail(email: String): Boolean {
    // Email validation logic
}
```



![Illustration of code in commonMain shared between all platform specific source sets.](https://ucarecdn.com/0fe51776-49a2-4032-a981-31615b7f9ba2/)

## Running a KMP project

We'll cover setting up an environment for different targets, and the specifics of running each one, in later topics. For now, assume your environment is ready and you want to run the Android and iOS targets. Running these is typically as simple as choosing the target and clicking Run in your IDE.

![Running iOS App on a simulator.](https://ucarecdn.com/8484ce81-89dc-4d72-bf94-f989758f3775/)

## Implementing platform-specific code

While `commonMain` holds the common code, some functionality needs a platform-specific implementation. KMP provides the `expect`/`actual` mechanism for these cases.

An `expect` declaration lives in `commonMain`. It's a contract that each platform must fulfill — the signature of a function or property, without an implementation. The corresponding `actual` declaration lives in each platform-specific source set and supplies the concrete implementation.



```kotlin
// commonMain
expect fun showToast(message: String)

// androidMain
actual fun showToast(message: String) {
    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
}

// iosMain
actual fun showToast(message: String) {
    UIAlertController(title: nil, message: message, preferredStyle: .alert).show()
}
```



Here, `showToast` is declared as `expect` in `commonMain`, and `androidMain`/`iosMain` each provide their own `actual` implementation using platform-specific APIs.

![The expected/actual mechanism.](https://ucarecdn.com/30c29638-aff9-4fa7-8e79-5fd866f61100/)

## Dependency management

Managing dependencies in a KMP project works much like a regular Kotlin project, using Gradle. You declare dependencies per source set inside `shared/build.gradle.kts`: common dependencies go in `commonMain`, and platform-specific ones go in their corresponding source set.



```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.1")
        }
        androidMain.dependencies {
            implementation("io.ktor:ktor-client-android:$ktorVersion")
        }
        iosMain.dependencies {
            implementation("io.ktor:ktor-client-darwin:$ktorVersion")
        }
    }
}
```



Here, `commonMain` includes `kotlinx-datetime`, a multiplatform library for working with dates and times. `androidMain` includes `ktor-client-android` for networking on Android, while `iosMain` includes `ktor-client-darwin` for networking on iOS. The versions and libraries in this example are for illustration — what you actually include depends on your project's requirements.

Keep in mind that only multiplatform-ready libraries can go in `commonMain`, since the compiler needs to translate them to every target you declare.

## Version catalog

If you look at the Gradle files in a wizard-generated template, you'll see dependencies written as `implementation(libs..)`. This is a **version catalog** — a way to manage library and plugin versions in one place, stored in `gradle/libs.versions.toml`.

A version catalog file typically has three sections:



```kotlin
[versions]
agp = "8.2.2"
androidx-core-ktx = "1.13.1"

[libraries]
androidx-core-ktx = { group = "androidx.core", name = "core-ktx", version.ref = "androidx-core-ktx" }

[plugins]
androidApplication = { id = "com.android.application", version.ref = "agp" }
```



Without a version catalog, you might write a dependency like this:



```kotlin
dependencies {
    implementation("androidx.core:core-ktx:1.13.1")
}
```



With a version catalog, the same dependency becomes:



```kotlin
dependencies {
    implementation(libs.androidx.core)
}
```



Here, `libs.androidx.core` refers to the `androidx-core-ktx` entry in `libs.versions.toml`. IntelliJ IDEA and Android Studio can help you add entries to the catalog automatically: write a dependency as a plain version string, and the IDE will suggest extracting it to the catalog for you.

## Conclusion

In this topic, we covered:

- The benefits and use cases of KMP for cross-platform development.

- Setting up a KMP project and defining common and platform-specific code.

- Implementing platform-specific code using `expect`/`actual` declarations.

- Managing dependencies for KMP projects.

With this foundation, you're ready to look at the tools used for KMP development and the platforms and architectures KMP can target.

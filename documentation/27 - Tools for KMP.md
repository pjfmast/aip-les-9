# Tools for KMP

Kotlin Multiplatform (KMP) allows you to write code once in Kotlin and use it across various platforms — Android, iOS, web, and more. Getting the most out of KMP depends on using tools that are specifically designed for building multiplatform applications, rather than treating it as a regular single-platform project.

This topic covers the tools most commonly used in KMP development: the IDEs, Xcode and its role for iOS, an environment diagnostic tool, and the two ways to target the web.

## IDEs for KMP development

The core of Kotlin Multiplatform development lies in writing shared code that works across platforms like Android, iOS, and the Web. Both **IntelliJ IDEA** and **Android Studio** are built on the same underlying IntelliJ platform and offer full KMP support through a shared **Kotlin Multiplatform IDE plugin**. Which one you pick mostly comes down to whether you're primarily an Android developer (Android Studio) or want one IDE for everything, including backend or web modules (IntelliJ IDEA) — the KMP feature set is largely the same either way.

Here's how to set either one up:

- **Install & update.** Download the latest stable version of IntelliJ IDEA or Android Studio. You'll need at least IntelliJ IDEA 2025.2.2 or Android Studio Otter 2025.2.1 for the full plugin experience on Windows and Linux (macOS has been supported since slightly earlier versions).

- **Enable K2 mode.** Go to *Settings/Preferences → Languages & Frameworks → Kotlin* and make sure K2 mode is enabled — the KMP plugin depends on it.

- **Install the Kotlin Multiplatform plugin.** Open Settings/Preferences → Plugins, search for "Kotlin Multiplatform," and install it. Restart the IDE to finish.

Once installed, the plugin gives you a lot more than just syntax highlighting for a second platform:

- A **project wizard** that supports every target combination (Android, iOS, desktop, web, server) and lets you choose between a fully native UI or a shared one with Compose Multiplatform.

- **Preflight environment checks** that verify your OS, JDK, Android SDK, Xcode installation, and Gradle setup, and suggest fixes when something's missing.

- **Run configurations generated automatically** for every platform in your project, including iOS simulators and devices — you can build, run, and debug an iOS target directly from the IDE.

- **Cross-language support for Swift**, including basic code completion and editing, plus navigation and debugging across Kotlin and Swift code in the same project.

For a period, editing Swift code required a separate tool, since Android Studio and IntelliJ IDEA didn't support it natively. JetBrains had been developing a standalone IDE called Fleet partly to address this gap. Fleet's Kotlin Multiplatform support was deprecated in early 2025, and the product was discontinued entirely by the end of that year; its former role is now covered by the Kotlin Multiplatform IDE plugin described above, so there's no separate IDE to install for that purpose.

Xcode is still required to run and debug on an iOS simulator or device, since only Xcode can do that. It's no longer required, though, for making basic Swift edits.

## Targeting iOS with Xcode

Native iOS app development still has its own toolchain, and Xcode is central to it.

Xcode's key features:

- **Interface Builder** — a visual tool for designing your app's UI by dragging and dropping elements.

- **Simulators and devices** — test your KMP app on various screen sizes, iOS versions, and hardware directly from Xcode.

- **Debugging power** — Xcode's own debugging tools help when you're integrating Kotlin code into a Swift/Objective-C app.

Key information for KMP and Xcode:

- **macOS only.** Xcode runs exclusively on macOS.

- **Easy installation.** Download Xcode from the Mac App Store.

- **Command Line Tools.** During setup, make sure to install the Command Line Tools — they're required for managing dependencies and automating builds in KMP projects.

Your shared Kotlin module needs to reach the iOS app as a framework, and there are three common ways to do that:

- **Direct integration** — the shared module's framework is built and linked straight into the Xcode project via a build script phase, with no extra dependency manager involved. This is the simplest option and what most new KMP projects use by default.

- **CocoaPods** — if your iOS app already manages its other dependencies with CocoaPods, you can add the Kotlin framework as a Pod too. This still requires installing the CocoaPods Ruby gem.

- **Swift Package Manager (SwiftPM)** — Apple's own dependency manager. It's a good fit if you're publishing the shared framework so multiple projects or teams can consume it (a "remote distribution" workflow), rather than building it locally as part of one repository.

Which one to use depends on how your team is set up — a single-repo project usually just needs direct integration, while a shared framework consumed by several apps benefits from SwiftPM or CocoaPods.

## KDoctor

With multiple tools interacting in a KMP project, keeping your development environment healthy matters. **KDoctor** is a command-line tool, available on macOS only, that diagnoses common configuration issues in KMP projects — checking that Kotlin, Xcode, and related tools are properly installed and consistent with each other, and suggesting fixes when they're not.

![Kdoctor result inside console.](https://ucarecdn.com/80128a4f-41b7-4fb7-bfb3-5d2945fb7357/)

The IDE plugin's preflight checks now cover similar ground automatically inside IntelliJ IDEA and Android Studio, but KDoctor is still useful as a quick, IDE-independent sanity check — for example, in CI environments or before you've even opened a project. You can find KDoctor on GitHub, where installation and usage instructions are documented.

## Web targets

Kotlin Multiplatform also supports web development, letting you share code between backend, frontend, and mobile apps. Kotlin provides two ways to run code in the browser:

- **Kotlin/JS** — converts Kotlin code into JavaScript, giving you broad compatibility with modern browsers. This remains the more mature option for browser targets.

- **Kotlin/Wasm** — compiles Kotlin code into WebAssembly, a format known for strong performance in complex, compute-heavy apps. It's still maturing toward the stability level of the Android and iOS targets, so treat it as the option for early adopters rather than the default choice.

For Kotlin/Wasm, use a browser with WebAssembly garbage collection (GC) support for smooth memory management — Chrome, Firefox, and Edge all support it.

## Conclusion

In this topic, we explored the essential tools for Kotlin Multiplatform development. IntelliJ IDEA and Android Studio, powered by the shared Kotlin Multiplatform IDE plugin, cover project creation, cross-platform run configurations, and even basic Swift editing in one place. Xcode remains indispensable for iOS-specific work, with direct integration, CocoaPods, or Swift Package Manager as your options for connecting shared code to it. Don't forget KDoctor for a quick environment sanity check, and Kotlin's web capabilities through Kotlin/JS and Kotlin/Wasm if you want to extend your reach to browsers.

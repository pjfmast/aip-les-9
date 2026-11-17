# Setting up your first Ktor application

**Ktor** is a modern, open-source, multiplatform framework developed by JetBrains for building asynchronous servers and clients applications in Kotlin. Leveraging the power of Coroutines, it offers a lightweight and flexible foundation for creating high-performance applications. While relatively new, Ktor is backed by a growing community and is seeing increasing adoption for its speed, convenience, and ease of use.

![Ktor logo](https://ucarecdn.com/b2461198-3641-4fa3-95c2-45b4212fb76c/)

In this topic, we will focus on its capabilities for building the server-side of an application.

## Project settings

When creating a Ktor project, you will encounter various settings:

- **Build System:** You can choose your preferred build system from [Gradle](https://gradle.org/) (with Kotlin or Groovy DSL), [Maven](https://maven.apache.org/), or the experimental [Amper](https://github.com/JetBrains/amper). The build system automates processes like compiling source code and managing dependencies. You can also opt to use a `libs.version.toml` version catalog for Gradle and Amper projects to centralize dependency management. In this topic, we will proceed with Gradle with Kotlin DSL.

- **Project Artifact:** This defines the group and name for your project, for example, `com.example.ktor-sample`. The group is typically a reversed domain name, and the artifact name is your project's identifier.

- **Ktor Version:** This is the version of Ktor you want to use. It's usually recommended to start with the latest stable version.

- **Engine:** An engine is used to run your server. It represents the underlying request processing machinery. You can choose from Netty, Jetty, Tomcat, or CIO (Coroutine-based I/O). For now, we will stick to the default, Netty.

> **Note:**

We will explore the differences between engines in a later topic.

- **Configuration:** You can choose to specify server parameters (like port and host) either directly in code, or externally in a YAML or HOCON file. Using a configuration file provides more flexibility, allowing you to change settings without recompiling your application. We will use a YAML file for now.

- **Include Samples:** If checked, the generator will create simple "Hello World" code for any plugins you add, which can be useful for understanding how they work.

- **Plugins:** Plugins are optional building blocks that provide common functionality outside of your core application logic, such as authentication, serialization, routing, cookie support, compression, and so on.

## Project creation

There are several ways to create a Ktor project. We will go through the **web-based Ktor project generator**, the **IntelliJ IDEA Ultimate plugin**, and the new **Ktor CLI tool**.

To create a Ktor project using the official web generator, follow the steps below:

- Navigate to the [Ktor Project Generator](https://start.ktor.io/).

- In the **Project artifact** field, enter a name for your project, for example, `com.example.hello-ktor`:

![Project artifact field](https://ucarecdn.com/b7dd1140-7b95-4094-9666-a695a4f210d5/)

- Click **Configure** to expand the project settings dropdown and adjust them if needed. For this example, match the settings below:

![Project configuration](https://ucarecdn.com/15d1f2c3-3cae-47cf-b7ae-39ab2da5f77e/)

- For this example, we don't need to add any plugins. If we did, we could simply search for one, like "serialization", and click **Add**:

![Adding serialization plugin](https://ucarecdn.com/94e393a6-8366-4b80-95e3-edb9fedcb0e8/)

- Click the **Download** button. A ZIP archive containing your project will be downloaded. Unpack it into your desired project directory.

If you use IntelliJ IDEA Ultimate, you can use the integrated Ktor plugin:

- On the welcome screen, click **New Project**. Otherwise, from the main menu, select **File** → **New** → **Project**.

- Choose Ktor from the list of generators on the left:

![Project generators](https://ucarecdn.com/7fe0150d-4233-4405-acf0-d4dd193472b6/)

- Fill in your project details like **Name** and **Location**. You can configure the **Engine**, **Build System**, and other settings here. Click **Advanced Settings** to see options like the Ktor version and configuration file type. For now, match the settings and click **Next**:

![Project settings](https://ucarecdn.com/f1448716-ca19-4985-a166-80a61ec790ae/)

- On the next screen, you can add plugins to your project. We don't need any at the moment. If we did, we could simply search for one, like "serialization", and click **Add**:

![Adding plugins](https://ucarecdn.com/acc2513a-b50f-4bd3-a35d-2a63b82fafd1/)

- Click **Create**. IntelliJ IDEA will generate the project and open it for you.

For command-line enthusiasts, the Ktor CLI provides an interactive way to generate projects:

-

First, install the Ktor CLI:

For Linux and macOS, the tool can be installed via Homebrew:



```kotlin
brew install ktor
```



For Windows, the tool can be installed via WinGet:



```kotlin
winget install Jetbrains.KtorCLI
```



- Run the interactive generator with the command `ktor new`.

> **Note:**

You may be required to restart your terminal session.

- Enter your project's name when prompted. You can also change the project location.

![ktor new example](https://ucarecdn.com/ce1a6089-b98f-4b09-bd3e-da65927f51cd/)

> **Note:**

You can change the project's group once it has been generated.

- The next step allows you to search for and add plugins from different categories like Administration, Security, etc. For now, we will skip this:

![Adding plugins](https://ucarecdn.com/dcf0a719-292c-416f-8aab-7821f6ecd4d0/)

- Press **CTRL+G** to generate the project files in the specified location.

> **Note:**

At the moment, some settings such as selecting the Ktor version or server engine aren't configurable. The engine defaults to Netty, and the version used depends on the `ktor` CLI tool's version.

## Explore, and run in IntelliJ IDEA

If you created the project in IntelliJ IDEA, it's already open. If you downloaded it or generated it via Ktor CLI, click **Open** on IntelliJ's welcome screen. Otherwise, from the main menu, select **File** → **Open**, then choose your project's root folder (make sure it's extracted if downloaded).

The project has a standard layout:

- The main source code is located under `src/main/kotlin`.

- Resources, such as configuration files, are placed in `src/main/resources`.

- The build script, `build.gradle.kts`, is in the project's root directory. It defines your project's dependencies and other build-related settings.

Let's examine the generated code. You will find two Kotlin files in your `src/main/kotlin` directory:

- `Application.kt`

This file contains the entry point for the application:



```kotlin
package com.example

import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureRouting()
}
```



`main()`: The main function calls `io.ktor.server.netty.EngineMain.main(args)` which starts the Netty engine and automatically loads its configuration from a resource file (`application.yaml` in our case).

- `Application.module()` is the main module of your application. In Ktor, a module is simply a function that configures a part of the application's functionality (an extension function on `Application`). You can have more than one module to split up your code organization (e.g., one module for setting up database connections, another for defining routes). Our `module` currently just calls `configureRouting()` to set up our endpoints.

- `Routing.kt`

This file defines the application's endpoints:



```kotlin
package com.example

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}
```



This code sets up a handler for HTTP `GET` requests to the root path `/`. When a request is received, it responds with the plain text "Hello World!".

The glue that connects the server's main function your `module` function (application logic) is the configuration file found in `src/main/resources/application.yaml`:



```yaml
ktor:
    application:
        modules:
            - com.example.ApplicationKt.module
    deployment:
        port: 8080
```



This YAML file configures two areas:

- **Deployment:** The port is a virtual point where network connections start and end. Here we specify that the server should run on port `8080`. It is important that this port is free on your machine, otherwise, the application will fail to start.

- **Application:** When the application starts, it loads the specified modules. Our entry point to `com.example.ApplicationKt.module`, which is the `module()` extension function inside the `Application.kt` file.

It's worth noting that if we had chosen the '**Configuration in code**' option during project setup, this file wouldn't exist. Instead, our `main` function would manually start the server and pass parameters directly, like so:



```kotlin
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}
```



However, these two approaches are not mutually exclusive. Even when using `embeddedServer` to define core settings like port, you can still include a configuration file (YAML or HOCON) to manage other values like API keys which your application can read from.

To run your application from within IntelliJ IDEA:

- Click the run button in the top-bar (or use the default shortcut **SHIFT+F10**):

![run button](https://ucarecdn.com/1dc6fc24-603f-4753-b6ef-ea6fdf99f622/)

> **Note:**

Ensure the selected configuration is 'EngineMain'.

- Or, click the green button in the gutter next to the `main` function and choose **Run 'ApplicationKt.main()'**:

![Run button in gutter](https://ucarecdn.com/979a2a3c-d71b-47fd-bdae-e94667281e05/)

A link will appear in the Run tool window's console:

![Ktor running terminal](https://ucarecdn.com/6849a9a1-d2d8-46a9-b8ce-d44dc2ed8bbe/)

Open the link and verify that everything is working. You should see the "Hello World!" text displayed.

## Conclusion

We've created and run our first application with Ktor. As seen, getting a project up and running is quite easy and fast. Ktor allows you to build applications by composing only the features you need through plugins. It offers a compelling, lightweight, and coroutine-native choice for building modern web applications in Kotlin.

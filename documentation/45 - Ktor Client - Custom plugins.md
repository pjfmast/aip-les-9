# Ktor Client: Custom plugins

Some behavior belongs to every call your app makes: attaching an identifier header, measuring how long responses take, or logging what was sent and received. You could repeat that code before and after each call, or you could write it once and let the client apply it to every call. In Ktor, that reusable piece is a **plugin**.

In this topic, you'll learn how plugins work, how to write your own, and how a plugin can follow a call from the moment a request is created until its response is processed.

## What a plugin is

A plugin is a piece of functionality that you install once when you create the `HttpClient`, and that then takes part in every call. It can hook into different **stages** of both the request and the response: it can change a request before it is sent, look at a response when it arrives, or convert the body in either direction. You have already used some plugins, such as `ContentNegotiation`, which converts bodies to and from JSON. Another built-in one is `Logging`, which needs an extra dependency:



```kotlin
val client = HttpClient {
    install(ContentNegotiation) { json() }
    install(Logging) { level = LogLevel.HEADERS }
}
```



Plugins keep **cross-cutting concerns** out of your feature code. Your code can just call `client.get(...)` without caring how the header gets there or how the response is measured.

## Writing a plugin

Create a plugin with `createClientPlugin`, passing its name and, optionally, a configuration class. Inside, you register **handlers** for the stages of a call. This plugin adds a header to every request:



```kotlin
class ClientIdConfig {
    var clientId: String = ""
}

val ClientIdPlugin = createClientPlugin("ClientIdPlugin", ::ClientIdConfig) {
    val clientId = pluginConfig.clientId // copy the setting into a local variable

    onRequest { request, _ ->
        request.headers.append("X-Client-Id", clientId)
    }
}
```



You install it like any built-in plugin, and the lambda sets its configuration:



```kotlin
val client = HttpClient {
    install(ClientIdPlugin) {
        clientId = storedClientId
    }
}
```



## How the configuration works

The configuration class is a plain class with **mutable properties**. When you call `install(ClientIdPlugin) { ... }`, Ktor creates a `ClientIdConfig` object and runs your lambda on it, so the lambda can assign values with `clientId = ...`. That is why the properties are `var`s and have default values.

After that, the body of `createClientPlugin` runs once and gets the filled-in object as `pluginConfig`. The handlers inside it, however, run later, on every call. If they read `pluginConfig.clientId` each time, they would depend on an object that can still be modified. Copying the value into a local `val`, as the code above does, takes a **snapshot**: the handlers capture an immutable value that was fixed when the plugin was installed, and nothing can change it afterwards.

## Check for a built-in plugin first

Before you write a plugin, check whether a built-in one already solves the problem. The example above only adds a fixed header, and Ktor has the `DefaultRequest` plugin for exactly this. You configure it with the `defaultRequest` function, and it applies the settings to every request:



```kotlin
val client = HttpClient {
    defaultRequest {
        header("X-Client-Id", storedClientId)
    }
}
```



A custom plugin pays off when there is logic around the behavior, for example when the header value has to be computed for each request or when you also need to react to the response.

## The handlers

The plugin API gives names to the stages of a call, so you don't have to deal with Ktor's internal pipeline. These are the basic handlers, in the order they run for a call:

Handler

What it does

`onRequest`

Runs for each request before it is sent, so you can modify it, for example by adding headers.

`transformRequestBody`

Turns the body you passed into the content that is sent, or returns `null` if it doesn't apply.

`onResponse`

Runs for each response, for example to log it.

`transformResponseBody`

Runs when you call `.body()`, to convert the response into the requested type.

`onClose`

Runs when the client is closed, so the plugin can release resources it holds.

`onRequest` and `onResponse` are the ones you will write most often. The `transform...` handlers are what `ContentNegotiation` uses to convert bodies to and from JSON.

## Sharing data between handlers

Handlers run at different moments, so they can't share local variables. To pass a value from one handler to another within the same call, use **attributes**: a map attached to the call, where each value is stored under an `AttributeKey`.

For example, this plugin measures how long each call takes. `onRequest` stores the moment the request was created, and `onResponse` reads it back:



```kotlin
val ResponseTimePlugin = createClientPlugin("ResponseTimePlugin") {
    val startedAtKey = AttributeKey<TimeMark>("StartedAt")

    onRequest { request, _ ->
        request.attributes.put(startedAtKey, TimeSource.Monotonic.markNow())
    }

    onResponse { response ->
        val startedAt = response.call.attributes[startedAtKey]
        println("${response.call.request.url} answered in ${startedAt.elapsedNow()}")
    }
}
```



The key is created once, inside the plugin, and used in both handlers. Because attributes belong to one call, two requests running at the same time each get their own start time and don't interfere with each other. This is also why the value is stored in attributes and not in a variable of the plugin: a shared variable would be overwritten by concurrent requests.

## Practical notes

- **Keep plugins small.** One plugin should do one thing. Combine them with `install`.

- **Prefer built-ins.** Check whether `DefaultRequest`, `Logging`, `HttpTimeout`, or a retry plugin already does the job.

- **Keep handlers fast.** Handlers run for every call, so avoid heavy work in them.

- **Don't leak secrets.** If you log headers, hide sensitive ones. The `Logging` plugin has a `sanitizeHeader` option for this.

## Conclusion

A Ktor plugin is reusable behavior that you install once and that takes part in every call, in both the request and the response. You write one with `createClientPlugin`, register handlers such as `onRequest` and `onResponse`, and install it with `install`. The configuration class has mutable properties that the `install` block sets, and the plugin copies them into local variables so its handlers see a fixed snapshot. To pass a value between handlers of the same call, store it in the call's attributes under an `AttributeKey`.

Before writing a plugin, check whether a built-in one, such as `DefaultRequest`, already does the job.

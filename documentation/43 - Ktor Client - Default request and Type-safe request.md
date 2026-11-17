# Ktor Client: Default request and Type-safe request

Ktor's HTTP client gets tedious fast without help: repeating the same base URL, the same auth header, and the same hand-built path on every request. Two plugins fix that from different angles — Default Request centralizes whatever's common across every request, and Resources replaces manually built URL strings with typed classes the compiler can check.

In this topic, you will learn how to use these plugins.

## Installation

The Resources plugin is responsible for making type-safe requests in Ktor. To use the Resources plugin, you need to include the `ktor-client-resources` artifact in the build script:



```kotlin
implementation("io.ktor:ktor-client-resources:$ktor_version")
```



Also, you need to add the `Kotlin serialization` plugin using the plugin block of your build script because our plugin needs `@Serializable` behavior:



```kotlin
plugins {
    // ...
    id("org.jetbrains.kotlin.plugin.serialization") version "2.4.0"
}
```



To install the `Resources` plugin, pass it to the `install` function inside a client configuration block:



```kotlin
val client = HttpClient(CIO) {
    install(Resources)
}
```



The Default Requests plugin doesn't need any specific dependencies. You don't even need to install it, just call the `defaultRequest` function:



```kotlin
val client = HttpClient(CIO) {
    defaultRequest {
        // this: DefaultRequestBuilder
    }
}
```



But you can still install it by passing it to the `install` function inside a client configuration block:



```kotlin
val client = HttpClient(CIO) {
    install(DefaultRequest)
}
```



Now that our plugins are installed, it's time to prepare their configuration.

## Configuring Default Requests

The Default Requests plugin simplifies the configuration of default parameters for all HTTP requests made by the client. To configure the URL, you can use the base URL configuration:



```kotlin
defaultRequest {
    url("https://example.com/api/") // Set your base URL here
}
```



This base will be merged with a particular request URL according to the following rules:

- **Request URL doesn't start with a slash.**

If the base URL ends with a slash, the two strings are concatenated:



```kotlin
Base URL: https://example.com/dir/
Request URL: file.html
Result: https://example.com/dir/file.html
```



If the base URL doesn't end with a slash, the last path segment of the base URL is removed, and then the strings are concatenated:



```kotlin
Base URL: https://example.com/dir/default_file.html
Request URL: file.html
Result: https://example.com/dir/file.html
```



- **Request URL starts with a slash.**

When the request URL starts with a slash, it is used as is, and the base URL remains unmodified:



```kotlin
Base URL: https://example.com/dir/default_file.html
Request URL: /root/file.html
Result: https://example.com/root/file.html
```



You can also configure the URL using URL parameters:



```kotlin
defaultRequest {
    url {
        protocol = URLProtocol.HTTP
        host = "0.0.0.0"
        port = 8080
        encodedPath = "/api/"
        parameters.append("token", "your_token_here")
    }
}
```



Let's take a closer look at the code above.

- `protocol` — the scheme (`URLProtocol.HTTP`, `HTTPS`, etc.) every request will use.

- `host` — where requests are sent.

- `port` — defaults to 80 for HTTP and 443 for HTTPS if left unset.

- `encodedPath` — the base path merged with each request's own path, by the same rules above.

- `parameters` — query parameters appended to every request; here, a `token` parameter.

Whichever form you use, setting these once inside `defaultRequest` means every request made with that client instance picks them up automatically — no need to copy and paste the same base URL or query parameter into every call site.

## Headers

The Ktor Default Request plugin allows you to add headers to every request. Headers are commonly used for various purposes, such as authentication, specifying content types, and more. Here are some options for configuring headers:

**Adding a header**

You can add a specific header to all requests using the `header` function:



```kotlin
defaultRequest {
    header("Authorization", "Bearer your_access_token") // Add an authorization header
}
```



This is useful when you need to include headers required for every request, such as authentication tokens.

**Avoiding header duplication**

To avoid duplicating headers, you can use the following methods:

- `contains`: Checks if a header is already present to avoid duplication.

- `appendIfNameAbsent`: Appends a header if it doesn't already exist.

- `appendIfNameAndValueAbsent`: Appends a header only if both the name and value are absent.

These functions help ensure that you only add headers if they are not already present in the request:



```kotlin
defaultRequest {
    // ...
    headers.appendIfNameAbsent("X-Custom-Header", "Hello") // Add a custom header if it doesn't exist
}
```



These functions can be particularly useful when you want to add headers conditionally or when you want to avoid overwriting existing headers.

## Configuring and making type-safe requests

Ktor's Resources plugin provides a powerful way to create type-safe requests in your Kotlin applications. With this plugin, you can define resource classes that describe server resources and then make requests to these resources with confidence.

First, we need to create Resource Classes.

Resource classes are at the heart of type-safe requests. These classes describe the structure of your server resources. Below, there is an example that covers different scenarios, such as nested paths, query parameters, and path parameters.



```kotlin
// Resource with a single path segment
@Resource("/your-resource-path")
class YourResource()

// Resource with a query parameter
@Resource("/your-resource-path")
class YourResource(val queryParam: String? = "default-value")

// Resource with nested classes
@Resource("/your-resource-path")
class YourResource() {
    @Resource("nested-segment")
    class NestedResource(val parent: YourResource = YourResource())
}

// Resource with a path parameter
@Resource("/your-resource-path")
class YourResource() {
    @Resource("{id}")
    class ResourceWithId(val parent: YourResource = YourResource(), val id: Long)
}
```



Let's break down these resource classes:

- Single path segment (`YourResource`): Represents a resource at `/your-resource-path`.

- Query parameter (`YourResource` with `queryParam`): Represents a resource with an optional query parameter named `queryParam`.

- Nested classes (`YourResource` with `NestedResource`): Represents a resource with a nested path segment.

- Path parameter (`YourResource` with `ResourceWithId`): Represents a resource with a path parameter `{id}`.

Now that we've defined our resource classes, let's see how to make type-safe requests to these resources.



```kotlin
fun main() {

    val client = HttpClient(CIO) {
        install(Resources)
        // Additional client configuration...
    }

    runBlocking {
        // Make type-safe requests
        val getResource = client.get(YourResource())
        val getResourceWithQueryParam = client.get(YourResource(queryParam = "custom-value"))
        val getNestedResource = client.get(YourResource.NestedResource())
        val getResourceWithId = client.get(YourResource.ResourceWithId(id = 123))

        // Handle responses...
    }
}
```



In this code:

- We configure the HTTP client with the Resources plugin.

- We make type-safe requests to various resources using the `get` function.

- Each request corresponds to a specific resource class we defined earlier.

By structuring your code this way, you ensure type safety and maintainability in your requests, making your application more robust.

## Practice

Now, let's see how this works in practice.

First, we should create a simple server:



```kotlin
fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = false)
}

fun Application.module() {
    configureRouting()
}
```



And a simple routing:



```kotlin
data class User(val id: Long, val name: String, val email: String)

fun Application.configureRouting() {
    routing {
        get("/only-path") {
            call.respondText("RESOURCE THAT DESCRIBES ONLY PATH")
        }

        get("/path-with-query") {
            val queryParam = call.parameters["queryParam"] ?: "default-value"
            call.respondText("RESOURCE THAT DESCRIBES PATH WITH QUERY PARAM - $queryParam")
        }

        route("/parent-path") {
            get("/nested-path") {
                call.respondText("RESOURCE THAT DESCRIBES THE FOLLOWING NESTED PATH: ${call.request.path()}")
            }
        }

        get("/path-with-parameter/{id}") {
            val id = call.parameters["id"]?.toLongOrNull() ?: -1L
            if (id >= 0) {
                val user = User(id, "John Doe", "johndoe@example.com")
                call.respondText("RESOURCE WITH PATH PARAMETER THAT PASS US TO USER WITH " +
                        "ID: ${user.id} NAME: ${user.name} EMAIL: ${user.email}")
            } else {
                call.respond(HttpStatusCode.BadRequest, "Invalid ID")
            }
        }
    }
}
```



Now, let's set up a Client with the `defaultRequest` function that specifies all client requests to our server:



```kotlin
fun main() {
    // our server here

    val client = HttpClient(CIO) {
        install(Resources)
        defaultRequest {
            host = "0.0.0.0"
            port = 8080
            url { protocol = URLProtocol.HTTP }
        }
    }
}
```



Create classes according to the paths:



```kotlin
@Resource("/only-path")
class OnlyPath()

@Resource("/path-with-query")
class PathWithQuery(val queryParam: String? = "your parameter")

@Resource("/parent-path")
class ParentPath() {
    @Resource("nested-path")
    class NestedPath(val parent: ParentPath = ParentPath())
}

@Resource("/path-with-parameter")
class PathWithParameter() {
    @Resource("{id}")
    class Id(val parent: PathWithParameter = PathWithParameter(), val id: Long)
}
```



And finally, make type-safe requests:



```kotlin
fun main() {
    // our server here

    // our client here

    runBlocking {
        val onlyPath = client.get(OnlyPath())
        println(onlyPath.bodyAsText())

        val pathWithQuery = client.get(PathWithQuery())
        println(pathWithQuery.bodyAsText())

        val parentPath = client.get(ParentPath.NestedPath())
        println(parentPath.bodyAsText())

        val pathWithParameter = client.get(PathWithParameter.Id(id = 2))
        println(pathWithParameter.bodyAsText())
    }
}
```



If you run the resulting program, you'll get the appropriate response in the console (we omitted logs in the image):

![console result of requests](https://ucarecdn.com/9eaf866e-e5a2-46a3-8368-916061a9af77/)

## Conclusion

Default Request and Resources solve two different flavors of repetition in an HTTP client. Default Request centralizes settings that are the same across every request — base URL, host, headers — so they're configured once instead of copy-pasted everywhere. Resources replaces hand-built URL strings with typed classes, so a mistake in a path or parameter shows up as a compile error instead of a failed request at runtime.

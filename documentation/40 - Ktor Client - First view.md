# Ktor Client: First view

**Ktor** stands as a leading framework for building server-side applications in Kotlin. However, its capabilities extend beyond the server realm, allowing the development of client applications as well. In today's topic, we'll explore the fundamentals of using Ktor on the client-side.

## What is Ktor Client

![Ktor logo](https://ucarecdn.com/4d675f23-dccf-419d-9086-5bb99e726ba4/)

The Ktor Client is a part of the Ktor framework that is used to make HTTP requests to servers. It's a multi-platform library that can be used in different environments, such as JVM, JS, Android, and iOS. That means you can use the same codebase for all these different platforms. It's also very convenient.

The Ktor Client also supports various [plugins](https://ktor.io/docs/http-client-plugins.html) that can extend its functionality. These plugins can handle tasks such as: Authentication (Verifying the identity of users or systems), JSON serialization (Converting data to and from JSON format), Logging (Recording events and data for debugging or analysis) and many more. Thus, we can avoid wasting time implementing typical tasks provided by plugins. Ktor Client also supports you to create [your own plugins](https://ktor.io/docs/client-custom-plugins.html), if you lack ready-made plugins.

Let's take a look at creating a simple application using Ktor Client.

## Project creation

First, open IntelliJ IDEA and click on the `"New Project"` button (or do it from the context menu: File | New | Project).

![IntelliJ IDEA project creation](https://ucarecdn.com/b294323a-a2ee-4fd6-ac5b-c431c937793c/)

In the New Project wizard, choose `"New Project"` from the list on the left.

On the right pane, fill in the project name and path. The rest of the options are specified as in the image below:

![IntelliJ IDEA selecting options for a new project](https://ucarecdn.com/f528f59f-c331-46ad-9769-553538cd85f8/)

After that, click `"Create"`.

After the build is complete, you can open the `Main.kt` file and see the generated code of a simple console program. Delete it and leave the `main` function empty.

![The main function in the created project](https://ucarecdn.com/f4bb8bb7-1125-4163-b3d8-8a937a70655e/)

Thus, we created a standard Kotlin JVM project.

## Adding Ktor Client to the project

So we've created the project. Now let's add Ktor Client to it.

To do this, we need to add the appropriate dependencies to the `build.gradle.kts`. If you don't have a dependencies section, add it to the end of the file:



```kotlin
dependencies {
    implementation("io.ktor:ktor-client-core:3.5.1")
    implementation("io.ktor:ktor-client-cio:3.5.1")
}
```



- The first dependency connects the Ktor Client core.

- The second one connects the requests engine** **(in this case, `CIO`).

Since the Ktor Client can be used on different platforms (JVM, Android, JavaScript, and Native), a specific platform may require a specific engine to process requests. You can learn more about Ktor engines in the [appropriate topic for Ktor Server](https://hyperskill.org/learn/step/23358) or in the [documentation](https://ktor.io/docs/http-client-engines.html).

To install the newly added dependencies, click on the Load Gradle Changes icon in the upper right corner of the `build.gradle.kts` file:

.

![Gradle dependency update button](https://ucarecdn.com/032e4475-5b51-4c87-92c4-0d7b9d088ae1/)

After that, we can use the Ktor Client in our application!

## Making requests

To send a request, we need to create an `HttpClient` object by passing the requests engine, in our case, `CIO`, to it in the constructor.



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*

fun main(args: Array<String>) {
    val client = HttpClient(CIO)
}
```



We can then make requests using the appropriate methods available in the `client` object.

For example, to make a `GET` request, we can use the `get` method:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://jsonplaceholder.typicode.com/posts/1")
}
```



We pass the URL of the request to the `get` method as an argument.

After the request is processed, the response from the server will be stored in our `response` variable. We can get all necessary information from it, such as status, headers, and response body.

Also note that since making a requests is a [suspend operation](https://kotlinlang.org/docs/coroutines-basics.html?_gl=1*1307lpi*_ga*NTU4Mzg4MzY1LjE2MzczNDMyNTQ.*_ga_9J976DJZ68*MTY5MTM1NzgyMS44MC4xLjE2OTEzNTg1ODEuNjAuMC4w&_ga=2.158282494.575052245.1691263270-558388365.1637343254), we must put the `suspend` keyword before the main function.

Let's output the `status`, `headers`, and `body` of the response to the console for clarity:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://jsonplaceholder.typicode.com/posts/1")

    println(response.status)
    println(response.headers)
    println(response.bodyAsText())

    client.close()
}
```



In this case, we used the following fields and methods:

- `response.status`: to get HTTP response status.

- `response.headers`: to get response headers.

- `response.bodyAsText()`: to get the response body in text form.

Other data that can be obtained are described in [the documentation](https://ktor.io/docs/response.html).

Note that after we have finished the request, we should call the `close` method to release resources holding by a client.

Let's run our application!

![Running the program](https://ucarecdn.com/b68c7cb9-64a0-41d8-99de-16974cd18b2d/)

![Program console output](https://ucarecdn.com/27d2d7d7-6a3c-4d29-8360-2a5c0223fe31/)

As we can see, the Ktor Client has made a request to the server, and the application outputs the received data to the console.

In our example, we made a request to the [JSON Placeholder](https://jsonplaceholder.typicode.com/) server. This is one of the most popular "Fake APIs". It responds to our requests, acting like a real server. But we can make a request to any other server:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://hyperskill.org")

    println(response.status)
    println(response.headers)
    println(response.bodyAsText())

    client.close()
}
```



Result:

![Program console output](https://ucarecdn.com/cb36ac6f-874e-4f26-9adc-8ce1c4f7262c/)

With Ktor Client, you can make requests with any other methods: `POST`, `PUT`, `DELETE`, etc. To do this, you can use [the corresponding methods](https://ktor.io/docs/request.html) in the `client` object.

## Sending body and headers

As we said earlier, you can use the `client.post` method to make a POST request. However, POST requests usually contain specific headers and some data. The data is included in the so-called request body.

To set the headers and request body, we will use a special Kotlin syntax based on [trailing lambdas](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas) and [receivers](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver). It looks pretty simple.

Here's how we can set the headers for the POST request. The headers can be set in the same way for other HTTP methods:



```kotlin
client.post("http://example.com") {
    headers {
        append(HttpHeaders.Accept, "text/html")
        append(HttpHeaders.Authorization, "abc123")
        append(HttpHeaders.UserAgent, "ktor client")
    }
}
```



After the client request method, there is a block of curly braces where the headers section is specified, and the required headers are set using the `append` method.

The request body is set in a similar way. We use the `setBody` method for this purpose:



```kotlin
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

val response: HttpResponse = client.post("http://example.com/post") {
    setBody("Body content")
}
```



We can also send headers and request body at the same time.

As an example, consider sending a POST request to the Fake API. We will simulate a situation where a client wants to create a new todo on the server. To do this, we must send a POST request to `https://jsonplaceholder.typicode.com/todos`. The body of the request should contain a [JSON](https://hyperskill.org/learn/step/6854) object of the new todo. In response, the server will return JSON of the created object, with an ID assigned to it.

Real-world systems work similarly. Fake API helps to process such requests on the client without the need to create a server.

Here's what the request method will look like in our case:



```kotlin
val response: HttpResponse = client.post("https://jsonplaceholder.typicode.com/todos") {
    headers {
        append(HttpHeaders.ContentType, "application/json")
    }
    setBody("""{"userId": 1, "title": "My new todo!"}""")
}
```



We specified a new todo JSON object in the request body and set a `Content-Type` header that tells the server that the request body should be interpreted as a JSON object, not plain text.

We can display the response from the server in the console the same way as we did before. Here is the full program code:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*

suspend fun main(args: Array<String>) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("https://jsonplaceholder.typicode.com/todos") {
        headers {
            append(HttpHeaders.ContentType, "application/json")
        }
        setBody("""{"userId": 1, "title": "My new todo!"}""")
    }

    println(response.status)
    println(response.headers)
    println(response.bodyAsText())

    client.close()
}
```



Result:

![Program console output](https://ucarecdn.com/ae1dd7e3-4556-48cc-98ea-a6232eb67033/)

The server responded that our todo was created and assigned ID 201.

In fact, of course, no todo was created on the server because it is a Fake API.

## Conclusion

In this topic, we learned about using Ktor on the client side.

We've learned:

- What Ktor Client is and why we need it.

- How to create a Kotlin JVM project.

- How to connect Ktor Client to a project.

- How to make requests to the server.

- How to get the `status`, `headers`, and `body` of the response.

- How to set request `headers` and `body`.

Now let's put what we've learned into practice.

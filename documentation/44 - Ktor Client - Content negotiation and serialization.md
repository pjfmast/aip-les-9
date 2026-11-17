# Ktor Client: Content negotiation and serialization

You already know how to make simple requests to the server using Ktor Client and get the code and response body as text or a byte array. Today we'll look at how to send and receive more complex data in JSON and XML form.

## Interpreting the response from the server

Let's create a simple Ktor Client project where we will send a request to the [Fake API](https://jsonplaceholder.typicode.com) server.



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO)

    val response: HttpResponse = client.get("https://jsonplaceholder.typicode.com/posts/3")

    println(response.status)
    println(response.headers)
    println(response.bodyAsText())

    client.close()
}
```



In this case, we want to receive a post object with an `id` equal to `3` from the server.

Then, we output the response code, headers, and response body as text to the console.

Result:

![console output](https://ucarecdn.com/c6be8b52-78c7-45f5-b493-e843f708fccc/)

We can see that a JSON object came as the body of the response.

But why are we sure this is a JSON object and not just text from the server?

Pay attention to the `Content-Type` header, which has the value `application/json`. This header tells us we should interpret the response body as a JSON object.

The response from the server can be in other formats, for example, XML.

Let's make a request to another Fake API server:



```kotlin
val response: HttpResponse = client.get("http://restapi.adequateshop.com/api/Traveler?page=1")
```



Result:

![console output](https://ucarecdn.com/bb54d837-25e1-4123-bc08-94795e2ffedc/)

Now we can see that `Content-Type` has a value of `application/xml`. This means this time the server response should be interpreted as XML.

Note that in both cases, we get the response in text form. But the `Content-Type` header describes how we should interpret this text. It seems easy—we already know how to do everything we need.

However, what if we want to extract the desired information from the received objects? For example, we need a `title` field from a previously received JSON object. Of course, we can manually try to parse the response body string. But this is very inconvenient, especially when the received object has a complex structure. The tools that Ktor Client provides will help us solve this problem.

## Using the serializer

First, we have to learn how to do what is called serialization. That is, to convert a string in a certain format (for example, JSON) into an object and vice versa. For serialization in Kotlin, there is a widely used `kotlinx.serialization` library.

Let's plug it into the project.

`build.gradle.kts`:



```kotlin
plugins {
    // ...
    kotlin("plugin.serialization") version "2.4.0"
}
```





```kotlin
dependencies {
    // ...
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.5.1")
}
```



In code, we should import it as:



```kotlin
import kotlinx.serialization.*
import kotlinx.serialization.json.*
```



Here we have connected only the JSON serializer (since JSON is used most often in practice). You can connect the serializer for XML and other formats in the same way. You can read about it in the corresponding [section of the documentation](https://ktor.io/docs/serialization-client.html#serialization_dependency).

Let's go back to the first Fake API and see how we can work with it using a serializer. As we saw earlier, the `post` object that the server returned contains four fields: `userId`, `id`, `title`, and `body`:



```json
{
  "userId": 1,
  "id": 3,
  "title": "ea molestias quasi exercitationem repellat qui ipsa sit aut",
  "body": "et iusto sed quo iure\nvoluptatem occaecati omnis eligendi aut ad\nvoluptatem doloribus vel accusantium quis pariatur\nmolestiae porro eius odio et labore et velit aut"
}
```



Let's create a corresponding data class that describes this structure:



```kotlin
@Serializable
data class Post(
    val userId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val body: String = ""
)
```



Now, we can retrieve the `Post` class object from the response body string we received from the server:



```kotlin
val responseBody = response.bodyAsText()
val post = Json.decodeFromString<Post>(responseBody)
```



Given a `post` object, we can access any of its fields as we do with any object in Kotlin: `post.userId`, `post.id`, `post.title`, `post.body`.

Full program code:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Post(
    val userId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val body: String = ""
)

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO)

    val response: HttpResponse = client.get("https://jsonplaceholder.typicode.com/posts/3")

    val responseBody = response.bodyAsText()
    val post = Json.decodeFromString<Post>(responseBody)

    println("userId = ${post.userId}")
    println("id = ${post.id}")
    println("title = ${post.title}")
    println("body = ${post.body}")

    client.close()
}
```



Result:

![console output](https://ucarecdn.com/e83f6e27-c182-491e-9306-cacbecd9e27b/)

## Sending serialized data

We have looked at how to parse JSON objects received from the server using a serializer. We can also use a serializer to encode data in a specific format (for example, JSON) to send it to the server.

We can create an object with the parameters we need and encode it into a JSON string:



```kotlin
val post = Post(1, 0, "My new post title", "My new post content")
val postJsonString = Json.encodeToString(post)
```



Then, we can make a post request to the server `jsonplaceholder.typicode.com/posts`, sending it our object. In response, it should return the created object with the assigned ID.



```kotlin
val response: HttpResponse = client.post("https://jsonplaceholder.typicode.com/posts") {
    headers {
        append(HttpHeaders.ContentType, "application/json")
    }
    setBody(postJsonString)
}
```



Note that in addition to the request body with the JSON string, we also set the `Content-Type` header to `application/json` so that the server knows it should interpret our body as JSON. This header should always be present when receiving data from the server and when sending your data to the server.

Full program code:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Post(
    val userId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val body: String = ""
)

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO)

    val post = Post(1, 0, "My new post title", "My new post content")
    val postJsonString = Json.encodeToString(post)

    val response: HttpResponse = client.post("https://jsonplaceholder.typicode.com/posts") {
        headers {
            append(HttpHeaders.ContentType, "application/json")
        }
        setBody(postJsonString)
    }

    println(response.bodyAsText())

    client.close()
}
```



Result:

![](https://ucarecdn.com/48bf4f50-ad24-409c-8c79-499a04591786/)

The server responded that it created our post and assigned ID 201.

In fact, of course, the server created no post because it is a Fake API.

## Installing ContentNegotiation plugin

We have seen how the serializer helps work with encoded string data, in our case, JSON data. However, we still have to do some routine work. We have to call the `encodeToString` and `decodeFromString` methods manually.

Ktor Client has a plugin called ContentNegotiation that helps simplify these tasks.

First, let's add it to the `build.gradle.kts` dependency:



```kotlin
dependencies {
    // ...
    implementation("io.ktor:ktor-client-content-negotiation:3.5.1")
}
```



To initialize our plugin, we must call the `install` function when creating the `HttpClient` object:



```kotlin
import io.ktor.client.plugins.contentnegotiation.*


val client = HttpClient(CIO) {
    install(ContentNegotiation)
}
```



We can also configure it there. In our case, we have to set the JSON serializer because we work with JSON:



```kotlin
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*


val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json()
    }
}
```



You do it the same way with other formats, for example, with XML. See the [corresponding documentation section](https://ktor.io/docs/serialization-client.html#configure_serializer).

We can also set some settings as follows:



```kotlin
val client = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        })
    }
}
```



In this case, we set the `prettyPrint` and `ignoreUnknownKeys` parameters. You can find the full list of parameters and their descriptions [in the documentation](https://kotlinlang.org/api/kotlinx.serialization/kotlinx-serialization-json/kotlinx.serialization.json/-json-builder/).

## Using ContentNegotiation plugin

After connecting and configuring the ContentNegotiation plugin, we can easily receive and send data in the desired format, in our case JSON, without manually calling `encodeToString` and `decodeFromString`.

We can retrieve the data simply by calling the `body` method and specifying the type of variable into which we store the result:



```kotlin
val post: Post = client.get("https://jsonplaceholder.typicode.com/posts/3").body()
```



Now, our program that gets a post with an `id` equal to 3 will look like this:



```kotlin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Post(
    val userId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val body: String = ""
)

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    val post: Post = client.get("https://jsonplaceholder.typicode.com/posts/3").body()

    println("userId = ${post.userId}")
    println("id = ${post.id}")
    println("title = ${post.title}")
    println("body = ${post.body}")

    client.close()
}
```



This program produces a similar result to what we saw before. But as you can see, there is no need to manually call `decodeFromString`.

Sending data is also very easy. We can create an object and pass it to the `setBody` method without explicitly calling `encodeToString`:



```kotlin
val response: HttpResponse = client.post("https://jsonplaceholder.typicode.com/posts") {
    contentType(ContentType.Application.Json)
    setBody(Post(1, 0, "My new post title", "My new post content"))
}
```



Now, our program that sends a new post to the server will look like this:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.*
import kotlinx.serialization.json.*

@Serializable
data class Post(
    val userId: Int = 0,
    val id: Int = 0,
    val title: String = "",
    val body: String = ""
)

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(Json {
                prettyPrint = true
                ignoreUnknownKeys = true
            })
        }
    }

    val response: HttpResponse = client.post("https://jsonplaceholder.typicode.com/posts") {
        contentType(ContentType.Application.Json)
        setBody(Post(1, 0, "My new post title", "My new post content"))
    }

    println(response.bodyAsText())

    client.close()
}
```



This program produces a similar result to what we saw before. But as you can see, there is no need to manually call `encodeToString`.

You can always refer to the [documentation](https://ktor.io/docs/serialization-client.html) if you need to work with other data formats.

## Conclusion

In this topic, we learned how to work with encoded text data.

We've learned:

- How to detect the response format using the `Content-Type` header.

- How to receive data from the server and decode it using a serializer.

- How to encode data using a serializer before sending it to the server.

- How to install and use the ContentNegotiation plugin.

Now, let's put what we've learned into practice.

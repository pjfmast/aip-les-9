# Ktor Client: Responses

You already know how to integrate and use Ktor Client in your applications. In this topic, we will take a closer look at what data Ktor Client allows you to retrieve from a server response.

Let's remember what the simplest application using Ktor Client looks like when making a GET request to the address `hyperskill.org`:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://hyperskill.org")
    
    client.close()
}
```



We use Ktor Client to make a request to the server and get a response.

All methods that make requests, such as `client.get` and `client.post`, return the response from the server. We store it in the `response` variable, which is of type `HttpResponse`.

As you already know, the server response consists of several parts: response code, headers, and response body. You can obtain all this data from the `HttpResponse` object. Let's start with the response code and headers.

## Obtaining status code

To get the status code of the response, we use the `status` field of the `HttpResponse` object:



```kotlin
response.status
```



We can output it to the console and verify that it is indeed the response code:



```kotlin
println(response.status)
```



Result:



```no-highlight
200 OK
```



You can get the code and text description separately using the `status.value` and `status.description` fields:



```kotlin
println(response.status.value)
println(response.status.description)
```



Result:



```no-highlight
200
OK
```



You may wonder why we need to get the status code of the response when we can read the response body right away.

The point is that in some cases, we may not need the body. If there is an error, we can understand it from the response code:



```kotlin
val response: HttpResponse = client.get("https://hyperskill.org/abracadabra")
println(response.status)
```



Result:



```no-highlight
404 Not Found
```



If we receive such an error code, we may not spend computational resources to retrieve the response body.

We can configure the Ktor Client to automatically throw an exception if the response code fails (does not fall in the `200-299` range). We do this by specifying the `expectSuccess` parameter when creating the `client` object:



```kotlin
val client = HttpClient(CIO) {
    expectSuccess = true
}
```



In our case:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO) {
        expectSuccess = true
    }

    val response: HttpResponse = client.get("https://hyperskill.org/abracadabra")

    println(response.status)

    client.close()
}
```



Result (only the beginning part of the output is shown):

![Console output](https://ucarecdn.com/a91798c4-447f-480e-b8f7-390f9094aff5/)

You can read more about handling request errors in the [corresponding section](https://ktor.io/docs/response-validation.html) of the documentation.

## Obtaining headers

The response headers are also very easy to get.

The `headers` field of the `HttpResponse` object is used for this purpose:



```kotlin
print(response.headers)
```



The result will look something like this:



```no-highlight
Headers [Server=[nginx/1.21.3], Date=[Wed, 13 Sep 2023 21:16:47 GMT], Content-Type=[text/html; charset=utf-8], Content-Length=[4751], Connection=[keep-alive], Vary=[Accept-Encoding, Cookie, X-USER-ID, Authorization, origin], X-USER-ID=[545280841], X-Frame-Options=[DENY], X-Content-Type-Options=[nosniff], Referrer-Policy=[origin-when-cross-origin], Cross-Origin-Opener-Policy=[same-origin], Server-Timing=[total;dur=138.69097499991767], Set-Cookie=[csrftoken=6ZOXluIJTLmQvvNDbZXEjDHYkQ08IOrF; expires=Wed, 11 Sep 2024 21:16:47 GMT; Max-Age=31449600; Path=/; SameSite=Lax, hyperskill_version=f73ceea10cf4d5ce2a4b237c739df086d7b66e52; expires=Thu, 14 Sep 2023 21:16:47 GMT; Max-Age=86400; Path=/; SameSite=Lax, uid=1a908ef7-0e25-4639-936d-f45027c2671d; expires=Sat, 10 Sep 2033 21:16:47 GMT; Max-Age=315360000; Path=/; SameSite=Lax, sessionid=o9ducq594sr51zcpf6my3si32m8g0z5k; expires=Tue, 12 Dec 2023 21:16:47 GMT; HttpOnly; Max-Age=7776000; Path=/; SameSite=Lax]]
```



But we are usually interested in a particular header. Fortunately, the headers object is a map that uses header names as keys and maps header values to them.

So we can easily get a specific header by accessing it through square brackets:



```kotlin
println(response.headers["Content-Type"])
```



Result:



```no-highlight
text/html; charset=utf-8
```



If we refer to a header that is not present in the response, we get null:



```kotlin
println(response.headers["abracadabra"])
```



Result:



```kotlin
null
```



The situation with headers is the same as with the response code. If we get the necessary information from the headers, we don't have to process the request body.

## Obtaining the request body

The request body can be obtained using the `body()` method of the `HttpResponse` object. And we can get the body both as text and as a byte array.

To do this, assign the result of the `body()` method to a variable of the corresponding type:



```kotlin
val response: HttpResponse = client.get("https://hyperskill.org")
    
val stringBody: String = response.body()
val byteArrayBody: ByteArray = response.body()
```



Let's make sure the body receives successfully:



```kotlin
println(stringBody)
println(byteArrayBody.asList())
```



Result (only the beginning part of the output is shown):

*(text)*

![Console output](https://ucarecdn.com/65450d6d-af89-4b0e-9f28-4901912d3132/)

*(array of bytes)*

![Console output](https://ucarecdn.com/6ce4a2ce-ad3e-47bd-8714-de0856bf0e03/)

In the case of binary data, the resulting byte array can be saved to a file using the `File` class:



```kotlin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import java.io.File

suspend fun main(args: Array<String>) {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.get("https://hyperskill.org/favicon.ico")

    val byteArrayBody: ByteArray = response.body()

    val file = File.createTempFile("files", "myicon.ico")

    file.writeBytes(byteArrayBody)

    client.close()
}
```



This example shows how to get a picture (binary data) from the server and save it to a file in the temporary directory of the system.

First we make a request to the server as usual and save the response body as a byte array.

Next, we create a file in the temporary directory (on Windows it is usually located at the path `%AppData%\Local\Temp`) using the `File.createTempFile` method. Then, we use the `writeBytes` method to write the resulting request body as an array of bytes to the file.

We can see the received file in the temporary directory through Explorer:

![File in Explorer](https://ucarecdn.com/a3c11005-91ef-4586-bb01-5c6701cdcb56/)

Thus, we can perform any manipulations with the received request body.

Note that the request body as text can also be obtained using the `bodyAsText()` method.

Sometimes there are situations when the received text body is in JSON format. Then it will have to be decoded before use. The `ContentNegotiation` plugin is used for this purpose. We will consider this topic separately later.

For more details on getting data from `HttpResponse` object, you can check out the [corresponding section](https://ktor.io/docs/response.html) of the documentation.

## Redirect processing

When accessing a site, sometimes the response headers may include a so-called [redirect header](https://en.wikipedia.org/wiki/URL_redirection), which tells the client that the requested resource is available at a different address.

For example, if you type `http://gogle.com` in the browser bar, you will be redirected to `https://www.google.com`.

Ktor Client automatically follows the redirect in the same way as a browser. This means that if you repeat the same thing in Ktor Client, you will get `https://www.google.com` as a response to your request:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO)

    val response: HttpResponse = client.get("http://gogle.com")

    println(response.status)
    println(response.headers)
    println(response.bodyAsText())

    client.close()
}
```



Response (only the beginning part of the output is shown):

![Console output](https://ucarecdn.com/80f11f3f-8411-4d8d-9b08-af4f94007732/)

However, if you want to disable automatic following of redirects, you can do so by specifying the `followRedirects = false` parameter when creating the `client` object:



```kotlin
val client = HttpClient(CIO) {
    followRedirects = false
}
```



In this case, you will get the response body and the headers of the original resource (in our case, `http://gogle.com`):



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*

suspend fun main(args: Array<String>) {

    val client = HttpClient(CIO) {
        followRedirects = false
    }

    val response: HttpResponse = client.get("http://gogle.com")

    println(response.status)
    println(response.headers)
    println(response.bodyAsText())

    client.close()
}
```



Result:

![Console output](https://ucarecdn.com/684e8752-2d58-4aeb-8a1b-84b0f9c94283/)

As you can see in the response, the server gives us a redirect address (`Location` header), but Ktor Client does not go to it automatically.

## Conclusion

In this topic, we learned how to get various response data from the server using the Ktor Client.

We've learned:

- What the server response consists of.

- How to get a response code.

- How to set up exception throwing on invalid response codes.

- How to get the response headers.

- How to get the response body as text.

- How to get the response body as a byte array and save it to a file.

- How to disable automatic following of redirects.

Now let's put what we've learned into practice.

# Ktor Client: Engines

You already know how to integrate and use Ktor Client in your applications. In this topic, we will take a closer look at the engines we can use together with Ktor Client.

## Ktor Engines

When you connect Ktor Client to an application, you see that in addition to the Ktor Client core itself, you have to connect an HTTP engine. In our case, it was CIO:



```kotlin
dependencies {
    implementation("io.ktor:ktor-client-core:3.5.1")
    implementation("io.ktor:ktor-client-cio:3.5.1")
}
```



You might be wondering why we need any HTTP engine if we are using Ktor Client. Doesn't it provide the functionality we need?

The answer is that Ktor Client is a multi-platform library, meaning that it can be used not only on the Java Virtual Machine (JVM) but also on other platforms such as Android or JavaScript. For each platform there are different engines that facilitate HTTP and WebSocket requests.

The HTTP engine in Ktor is a critical component that manages the actual process of sending and receiving HTTP requests and responses, serving essentially as the core of network communication in Ktor. Ktor supports various HTTP client engines, and each engine has its own set of features and configuration options.

Instead of reinventing the wheel, the developers of Ktor Client decided to make it possible to use one of these existing engines. This decision provides flexibility and allows using the engine that best suits the specific requirements of your project and the platform it's targeting.

So, all you need to do is import the engine you need and pass it to the HttpClient constructor. This is what we did when we looked at the Ktor Client:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.cio.*

fun main(args: Array<String>) {
    val client = HttpClient(CIO)
}
```



Before importing, of course, the engine must be specified in the dependencies of `build.gradle.kts`.

We are not limited to a single CIO. There are many other engines available for us to use. Let's take a closer look at them.

## Ktor Client engines for JVM

Before you add an engine to a project, you must first select it. The choice depends on the platform and other engine-specific features.

On the JVM platform, we can use the following three main engines in addition to the CIO: Apache5, Java, Jetty.

- Jetty is typically used when [HTTP/2](https://http2.github.io/) support is needed.

- Java is useful if you need support for full-duplex communication over [WebSockets](https://hyperskill.org/learn/step/25243).

- Apache5 is a good choice if a proxy is required because Jetty does not support proxy and Java does not run on the JAVA 8 API.

All these details can be seen in the [documentation](https://ktor.io/docs/http-client-engines.html#limitations). There are handy tables that show support of various features by available engines:

![HTTP/2 and WebSockets availability for different engines](https://ucarecdn.com/ed551721-4041-4a5a-9282-55fa4aeba140/)

There, you can also see the list of available engines and platforms on which a particular engine can be used:

![Availability of engines on different platforms](https://ucarecdn.com/887b38dc-6344-4d81-88c8-a51baeba440e/)

In this topic, we will only cover connecting and configuring JVM engines, because all other engines can be connected and configured in the same way. The documentation page above explains this in detail.

## Connecting engine to project

Once you have chosen the engine you want, you need to add the corresponding dependency to `build.gradle.kts`.

For Apache5:



```kotlin
implementation("io.ktor:ktor-client-apache5:$ktor_version")
```



For Java:



```kotlin
implementation("io.ktor:ktor-client-java:$ktor_version")
```



For Jetty:



```kotlin
implementation("io.ktor:ktor-client-jetty-jakarta:$ktor_version")
```



Where `$ktor_version` is the version of Ktor Client you use. For example: `3.5.1`.

Remember that in addition to the engine, you must specify the Ktor Client core in `build.gradle.kts`:



```kotlin
implementation("io.ktor:ktor-client-core:$ktor_version")
```



Once you update the Gradle dependencies, you can import the engine into the application code.

## Importing the engine in application code

To attach the engine to the Ktor Client, simply pass it to the `HttpClient` constructor.

For Apache5:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.apache5.*

fun main(args: Array<String>) {
    val client = HttpClient(Apache5)
}
```



For Java:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.java.*

fun main(args: Array<String>) {
    val client = HttpClient(Java)
}
```



For Jetty:



```kotlin
import io.ktor.client.*
import io.ktor.client.engine.jetty.jakarta.*

fun main(args: Array<String>) {
    val client = HttpClient(Jetty)
}
```



After that, you can make requests without going into the API of a particular engine. Ktor Client will take care of the interaction with the engine.

If you need to switch to another engine or platform, you can simply pass another engine to the `HttpClient` constructor, and that's it. Your other code that uses the Ktor Client API and makes requests can remain unchanged.

By the way, if you call the `HttpClient` constructor without an argument, the client will choose an engine automatically depending on the artifacts added in the build script:



```kotlin
import io.ktor.client.*

fun main(args: Array<String>) {
    val client = HttpClient()
}
```



## Engine configuration

The last thing we need to look at is the configuration parameters of the engine. These parameters are usually unique for each engine, so the configuration is one of the few things you need to change when you switch to a different engine.

To set the configuration parameters, we will use a special Kotlin syntax based on [trailing lambdas](https://kotlinlang.org/docs/lambdas.html#passing-trailing-lambdas) and [receivers](https://kotlinlang.org/docs/lambdas.html#function-literals-with-receiver). It looks pretty simple.



```kotlin
val client = HttpClient() {
    engine {
        //configuration parameters
    }
}
```



After the `HttpClient` constructor, we create a block of curly braces in which we use the `engine` method. Inside its curly braces, we specify the engine configuration parameters.

The set of parameters depends on the engine passed to the constructor. However, some parameters are common to all engines. There are currently 3 of them. You can see them [in the documentation](https://api.ktor.io/ktor-client/ktor-client-core/io.ktor.client.engine/-http-client-engine-config/index.html?_ga=2.140106675.1215363424.1693116530-558388365.1637343254&_gl=1*nrwtov*_ga*NTU4Mzg4MzY1LjE2MzczNDMyNTQ.*_ga_9J976DJZ68*MTY5MzE2NzE5MS44OS4xLjE2OTMxNjczMjUuNjAuMC4w).

We can set them as follows:



```kotlin
val client = HttpClient() {
    engine {
        pipelining = true
        threadsCount = 4
        proxy = null
    }
}
```



`pipelining` enables HTTP pipelining advice. `threadsCount` specifies network threads count advice. `proxy` parameter is responsible for setting the proxy.

If you don't know what a particular setting is responsible for, don't specify it and Ktor Client will set it to the default value.

You specify parameters unique to a particular engine in the same way. For example, for Apache:



```kotlin
val client = HttpClient(Apache5) {
    engine {
        followRedirects = true

        socketTimeout = 10_000
        connectTimeout = 10_000
        connectionRequestTimeout = 10_000

        customizeClient {
            setProxy(HttpHost("127.0.0.1", 8080))
        }
    }
}
```



- `followRedirects` tells Apache to follow the redirects if it receives the appropriate header.

- `socketTimeout`, `connectTimeout`, and `connectionRequestTimeout` set timeouts (in milliseconds) in various situations.

- `customizeClient` method specifies the address of the proxy server to be used.

A complete list of Apache settings is [available here](https://api.ktor.io/ktor-client/ktor-client-apache/io.ktor.client.engine.apache/-apache-engine-config/index.html?_ga=2.115015559.1215363424.1693116530-558388365.1637343254&_gl=1*q7wcn8*_ga*NTU4Mzg4MzY1LjE2MzczNDMyNTQ.*_ga_9J976DJZ68*MTY5MzE2NzE5MS44OS4xLjE2OTMxNjczMjUuNjAuMC4w). For other engines, these parameters do not work.

For example, here is how to specify a proxy when working with Java:



```kotlin
val client = HttpClient(Java) {
    engine {
        proxy = ProxyBuilder.http("http://proxy-server.com/")
    }
}
```



You can find the complete list of parameters for [Java](https://api.ktor.io/ktor-client/ktor-client-java/io.ktor.client.engine.java/-java-http-config/index.html?_ga=2.177255077.1215363424.1693116530-558388365.1637343254&_gl=1*6nf7n2*_ga*NTU4Mzg4MzY1LjE2MzczNDMyNTQ.*_ga_9J976DJZ68*MTY5MzE2NzE5MS44OS4xLjE2OTMxNjczMjUuNjAuMC4w) and [Jetty](https://api.ktor.io/ktor-client/ktor-client-jetty/io.ktor.client.engine.jetty/-jetty-engine-config/index.html?_ga=2.177255077.1215363424.1693116530-558388365.1637343254&_gl=1*6nf7n2*_ga*NTU4Mzg4MzY1LjE2MzczNDMyNTQ.*_ga_9J976DJZ68*MTY5MzE2NzE5MS44OS4xLjE2OTMxNjczMjUuNjAuMC4w). For the other engines, everything works the same way.

The complete list of parameters for any engine is available [in the documentation](https://ktor.io/docs/http-client-engines.html#configure).

## Conclusion

In this topic, we learned about different Ktor engines that you can use on the client side.

We've learned:

- Why we need HTTP engines.

- Where to look for features and limitations of the engines.

- How to connect and import the engine into the application.

- How to specify engine configuration parameters.

Now, let's put what we've learned into practice.

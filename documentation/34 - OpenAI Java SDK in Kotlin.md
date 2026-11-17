# OpenAI Java SDK in Kotlin

In this topic, we will take a look at the official OpenAI Java SDK and see how it is used from Kotlin. The SDK gives you direct, low-level access to OpenAI's model APIs, which is useful when you need full control over prompts, parameters, and data handling.

One thing to flag before we start: this is a regular JVM library. It works from Kotlin because Kotlin compiles to JVM bytecode and interoperates with Java libraries directly — but that also means it only runs where your code targets the JVM, such as an `androidMain` source set, a `desktopMain` source set, or a separate backend service. It won't compile inside `commonMain`, and there's no equivalent you can call from `iosMain`. Keep that boundary in mind as you go through the examples below — we'll come back to where exactly this fits in a Kotlin Multiplatform app's architecture at the end of the topic.

## Setup

The OpenAI Java SDK (`com.openai:openai-java`) is interoperable with Kotlin out of the box. Kotlin compiles to JVM bytecode and interoperates with any Java library, so you add it as a regular Gradle dependency and use it directly:



```kotlin
dependencies {
    implementation("com.openai:openai-java:[LATEST_VERSION]")
}
```



The SDK provides `OpenAIOkHttpClient.fromEnv()` as the standard way to create a client. It reads the `OPENAI_API_KEY` environment variable automatically, which avoids hardcoding credentials in source code:



```kotlin
import com.openai.client.OpenAIClient
import com.openai.client.okhttp.OpenAIOkHttpClient

val client: OpenAIClient = OpenAIOkHttpClient.fromEnv()
```



If you need to point the client at a different base URL — for example, a self-hosted vLLM server exposing an OpenAI-compatible API — you can override it using the builder instead:



```kotlin
val client: OpenAIClient = OpenAIOkHttpClient.builder()
    .baseUrl("http://localhost:8000/v1")
    .apiKey("not-needed-for-local")
    .build()
```



## Chat completions

The most common use case is generating a response to a user message. The SDK exposes this through the Responses API:



```kotlin
import com.openai.models.responses.ResponseCreateParams

val response = client.responses().create(
    ResponseCreateParams.builder()
        .model("gpt-4o-mini")
        .input("What is retrieval-augmented generation?")
        .build()
)

println(response.outputText())
```



You can also add a system-level instruction to shape the model's behavior:



```kotlin
val response = client.responses().create(
    ResponseCreateParams.builder()
        .model("gpt-4o-mini")
        .instructions("Answer concisely, in one sentence.")
        .input("What is retrieval-augmented generation?")
        .build()
)

println(response.outputText())
// RAG enhances LLM outputs by retrieving relevant external documents
// at query time and including them as context in the prompt.
```



## What the SDK covers

Beyond chat completions, the SDK provides access to the full OpenAI API surface from a single client:

- **Embeddings** — convert text to vectors for use in similarity search and RAG pipelines.

- **File upload** — upload JSONL training files.

- **Fine-tuning** — submit and monitor fine-tuning jobs via `client.fineTuning().jobs()`.

- **Images** — generate images with DALL·E via `client.images()`.

- **Audio** — transcribe or synthesize speech via `client.audio()`.

All of these share the same client and the same builder-pattern style shown above.

Direct SDK usage is the right level of abstraction when you need tight control over a single API call — for example, submitting a fine-tuning job, calling a specific model with custom parameters, or building a small feature like a summarizer or classifier without pulling in a larger framework. When you need to compose multiple capabilities — retrieval, memory, tool calling, multi-step agents — LangChain4j and Koog provide higher-level abstractions that handle the plumbing, as covered in the rest of this course.

## Synchronous and asynchronous calls

The examples above use blocking (synchronous) calls, which is the simplest approach for scripts and small applications. The SDK also ships a fully asynchronous client (`OpenAIOkHttpClientAsync`) that returns `CompletableFuture` for every call, making it straightforward to integrate into non-blocking JVM backends:



```kotlin
import com.openai.client.okhttp.OpenAIOkHttpClientAsync

val asyncClient = OpenAIOkHttpClientAsync.fromEnv()

asyncClient.responses().create(
    ResponseCreateParams.builder()
        .model("gpt-4o-mini")
        .input("What is RAG?")
        .build()
).thenAccept { response ->
    println(response.outputText())
}
```



## Where this fits in a Kotlin Multiplatform app

Now, back to the boundary we flagged at the start. Since this SDK only runs on the JVM, you have two realistic options for where to put it in a multiplatform app:

- **Behind a backend service.** Your `commonMain` code (shared across Android, iOS, and desktop) calls your own backend over HTTP using a multiplatform HTTP client like Ktor Client. The backend — a JVM process running Ktor Server or Spring Boot — is the only place that imports and calls the OpenAI Java SDK. This is the architecture the next topic, on Spring AI, builds on directly.

- **Directly in a JVM-only source set.** If you only needed the feature on Android and desktop, you could call the SDK straight from `androidMain` or `desktopMain`. In practice this is rarely the right call for a real app: it means writing the feature twice (once for JVM targets, once for iOS through a different mechanism), and it requires shipping your OpenAI API key inside the client app, where it can be extracted from the binary. Keeping the SDK on a backend you control avoids both problems, since the client never sees the key at all.

The OpenAI Java SDK — and, in the next topic, Spring AI — describe code that runs in your backend service, not in `commonMain`, `androidMain`, or `iosMain`. Your shared client code will only ever talk to that backend over plain HTTP.

## Conclusion

In this topic, we covered how the OpenAI Java SDK works directly from Kotlin with no wrapper needed. It's a Java library, not a Kotlin one, but its fluent builder-pattern API reads naturally in Kotlin thanks to trailing lambda syntax and Kotlin's smooth Java interop. `OpenAIOkHttpClient.fromEnv()` is the standard way to initialize the client, reading the API key from the environment, and the same client can be pointed at any OpenAI-compatible endpoint by overriding `baseUrl()` in the builder. The SDK covers the full OpenAI API surface — chat completions, embeddings, file upload, fine-tuning, images, and audio — all accessible from a single client instance. Because it's JVM-only, this code belongs in a backend service or a JVM-only source set — never in `commonMain` — with your shared client code reaching it over HTTP.

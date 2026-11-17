# Introduction to Spring AI

In this topic, we will look at Spring AI — a framework that brings AI capabilities into the Spring ecosystem. We will cover what Spring AI is, how it fits into a Spring Boot Kotlin project, and how to use its central abstraction, the `ChatClient` API, to interact with language models.

Like the OpenAI Java SDK from the previous topic, Spring AI is JVM-only, and Spring Boot itself is a server-side framework — it doesn't run on Android, iOS, or in `commonMain`. So the code in this topic describes your backend service: a separate Kotlin/JVM process that your Kotlin Multiplatform app talks to over HTTP, not code that lives inside your shared module.

## What is Spring AI?

Spring AI is a Spring framework extension that provides a unified, provider-agnostic API for integrating AI models into JVM applications. It follows the same design principles as other Spring abstractions such as `RestClient` or `JdbcClient`: consistent interfaces, auto-configuration, and dependency injection — which means AI features slot naturally into any existing Spring Boot application without requiring a separate infrastructure layer.

The key benefit over using provider SDKs directly (as we did with the OpenAI Java SDK topic) is portability. Spring AI's abstractions sit between your application code and the underlying provider, so switching from OpenAI to Anthropic Claude, Google Gemini, or a local Ollama model typically requires only a configuration change rather than a rewrite. Putting this backend in context with the rest of your app, the full picture looks like this:



```kotlin
Your KMP app (commonMain networking code, via Ktor Client)
      ↓  HTTP
Your backend service (Kotlin + Spring Boot)
      ↓
Spring AI (ChatClient, EmbeddingModel, VectorStore)
      ↓
Provider (OpenAI, Anthropic, Gemini, Bedrock, Ollama, ...)
```



Everything from here on describes the second box — the backend. Your shared client code never imports Spring AI or talks to a model provider directly; it just calls your own backend's HTTP endpoints and gets back plain data.

## Setup

Spring AI integrates with Spring Boot's auto-configuration system. The quickest way to bootstrap a project is via [Spring Initializr](https://start.spring.io/) — select Kotlin, Gradle, and the relevant Spring AI starter for your provider. For OpenAI:



```kotlin
dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.ai:spring-ai-starter-model-openai:[LATEST_VERSION]")
}
```



Configure the API key and model in `application.yml`:



```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-4o-mini
```



Spring Boot reads the API key from the `OPENAI_API_KEY` environment variable via the `${}` placeholder, keeping credentials out of source code — the same pattern used in the OpenAI Java SDK topic, and one more reason this key only ever lives on the backend, never inside your mobile or desktop client.

## The ChatClient API

`ChatClient` is the central abstraction in Spring AI for interacting with language models. It provides a fluent API for building and sending prompts, similar in style to Spring's `WebClient` or `RestClient`. Spring Boot auto-configures a `ChatClient.Builder` bean, which you inject and use to build a `ChatClient` instance:



```kotlin
import org.springframework.ai.chat.client.ChatClient
import org.springframework.stereotype.Service

@Service
class AssistantService(chatClientBuilder: ChatClient.Builder) {

    private val chatClient = chatClientBuilder.build()

    fun ask(question: String): String? =
        chatClient
            .prompt()
            .user(question)
            .call()
            .content()
}
```



The fluent chain reads naturally: `.prompt()` starts a new request, `.user()` sets the user message, `.call()` sends it to the model, and `.content()` extracts the response as a plain string. A `@RestController` further up the stack would expose `ask()` over HTTP, which is the endpoint your KMP client's Ktor Client code would call.

## System prompts and prompt templates

You can set a system-level instruction to shape the model's default behavior. On `ChatClient.Builder`, `.defaultSystem()` applies it to every request made by that client instance:



```kotlin
private val chatClient = chatClientBuilder
    .defaultSystem("You are a concise Kotlin assistant. Answer in 2-3 sentences.")
    .build()
```



For prompts with dynamic parts, Spring AI provides `PromptTemplate`, which uses `{}` placeholders resolved at runtime — analogous to Spring's `UriTemplate`:



```kotlin
import org.springframework.ai.chat.prompt.PromptTemplate

val template = PromptTemplate("Summarize the following in {language}: {text}")
val prompt = template.create(mapOf("language" to "English", "text" to inputText))

val response = chatClient.prompt(prompt).call().content()
```



## Structured output

Instead of parsing plain text, `ChatClient` can map model responses directly to Kotlin data classes using the `.entity()` method. Spring AI adds instructions to the prompt automatically to guide the model into producing a parseable response:



```kotlin
data class Sentiment(val label: String, val confidence: Double)

val result: Sentiment? = chatClient
    .prompt()
    .user("Classify the sentiment of: '$review'")
    .call()
    .entity(Sentiment::class.java)

println(result?.label)      // e.g. "Positive"
println(result?.confidence) // e.g. 0.92
```



This same `Sentiment` class is a plain Kotlin data class — if you also need it on the client side, it's a natural candidate to move into a shared module and reuse as the response type your Ktor Client call deserializes into, so the backend and the KMP app agree on the same shape.

## Streaming

For long responses or real-time output, replace `.call()` with `.stream()`. It returns a `Flux`, which integrates directly with Spring WebFlux:



```kotlin
val stream: reactor.core.publisher.Flux<String> = chatClient
    .prompt()
    .user("Write a short story about Kotlin.")
    .stream()
    .content()

stream.subscribe { token -> print(token) }
```



A streaming endpoint like this is typically exposed over Server-Sent Events or a WebSocket, which Ktor Client can consume from your shared code to show tokens as they arrive, the same way you'd see them stream in a chat UI.

## Advisors

Advisors are Spring AI's middleware layer — they intercept requests and responses at the `ChatClient` level and can modify, log, or augment them. You register them with `.advisors()` on either the builder (for all requests) or an individual call (for a single request):



```kotlin
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor

// Log every request and response
private val chatClient = chatClientBuilder
    .defaultAdvisors(SimpleLoggerAdvisor())
    .build()
```



Spring AI ships several built-in advisors. `SimpleLoggerAdvisor` logs requests and responses for debugging. `QuestionAnswerAdvisor` augments prompts with documents retrieved from a `VectorStore` — this is the advisor that powers the RAG integration we will cover in the following topics. You can also write custom advisors by implementing the `CallAdvisor` interface.

## Provider portability

Switching provider requires changing the starter dependency and the `application.yml` configuration — the `ChatClient` code itself stays identical. For example, to switch to Anthropic Claude:



```kotlin
// build.gradle.kts
implementation("org.springframework.ai:spring-ai-starter-model-anthropic:[LATEST_VERSION]")
```





```yaml
# application.yml
spring:
  ai:
    anthropic:
      api-key: ${ANTHROPIC_API_KEY}
      chat:
        options:
          model: claude-sonnet-4-5
```



Your Kotlin service code — `chatClient.prompt().user(...).call().content()` — requires no changes. Nothing about this switch is visible to your KMP client either — it's still calling the same backend endpoint over HTTP.

## Spring AI vs direct SDK usage

Both approaches are valid and serve different needs:

- The OpenAI Java SDK (from the previous topic) gives you fine-grained control over a single provider's API — useful for fine-tuning jobs, provider-specific features, or minimal-footprint integrations.

- Spring AI is the better choice when you're already in the Spring Boot ecosystem, want provider portability, or need higher-level features like RAG, conversation memory, or structured output with minimal boilerplate.

Either way, this code runs in your backend service, not in your KMP client — the client only ever sees the HTTP responses your backend sends back.

## Conclusion

In this topic, we covered Spring AI, a Spring framework extension that provides a provider-agnostic API for integrating AI models, following the same conventions as other Spring abstractions. `ChatClient` is the central abstraction, offering a fluent API for building prompts, calling models synchronously or via streaming, and mapping responses to structured Kotlin types. Advisors are Spring AI's middleware layer — they intercept requests and responses and can add logging, memory, or RAG retrieval to any `ChatClient` call. Switching providers requires only a dependency and configuration change; the `ChatClient` code itself remains unchanged. In your project's architecture, all of this lives in the backend service behind your Kotlin Multiplatform app, reached over plain HTTP from your shared client code.

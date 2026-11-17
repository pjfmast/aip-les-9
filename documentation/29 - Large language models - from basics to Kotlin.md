# Large language models: from basics to Kotlin

Large language models (LLMs) write emails, answer questions, and generate code, but they also invent facts and forget instructions. To build reliable applications, you need to know what they are, what they can't do, and how an application talks to them.

In this topic, you'll learn how LLMs are trained, the two main types of models, their limitations, the ways to interact with them, and how all of this looks in Kotlin and Kotlin Multiplatform (KMP).

## What is an LLM?

A large language model is an AI model trained on vast amounts of text, typically using a neural network architecture called the transformer. The "large" refers to its parameters, which number in the billions or even trillions. By analyzing statistical relationships between words, the model learns grammar, facts, and some reasoning ability.

Training usually has two stages:

- **Pre-training** produces a **foundation model**. The model processes huge amounts of text and learns patterns such as grammar and context. This is *self-supervised*: no one labels the data. A foundation model doesn't yet solve specific tasks. Depending on its type, it either predicts text continuations or converts text to numbers.

- **Fine-tuning** trains the foundation model further on a much smaller, labeled dataset. It can teach the model a specific task (like classification) or a domain (like medical texts). The "chat" in ChatGPT comes from fine-tuning on pairs of instructions and desired responses. A sub-step called **alignment** adjusts the model's behavior, for example making it friendly.

## Two types of models

**Autoregressive (generative) models** generate text by predicting the next **token** (a word or part of a word) from all the previous ones. ChatGPT, Claude, Gemini, and Llama all work this way. That's why you see replies stream in token by token.

**Representational (embedding) models** encode a whole input into a vector, called an **embedding**, that captures its meaning. They don't generate text. Instead, they power semantic search (finding text by meaning rather than by keywords), similarity comparison, and, after fine-tuning, classification such as sentiment analysis. Providers of generative models often offer embedding models too.

The two combine in **retrieval-augmented generation (RAG)**. The system finds relevant documents using embeddings and then asks a generative model to answer based on them. This grounds the answer in verifiable data. Going a step further, a generative model can act as the reasoning engine of an **agent** that decides which tools to call, which later topics cover.

## Limitations

**Knowledge cutoff.** A model knows nothing that happened after its training data was collected. In software development, where libraries change constantly, this means it may suggest deprecated APIs or outdated setup instructions. Web search helps, but the model can still pull up an old page or misread a source, so cross-check time-sensitive answers against official documentation.

**Hallucinations.** A model can produce text that sounds right but is made up. In code, this looks like methods that don't exist, wrong arguments to real functions, invented documentation links, or code that looks perfect and doesn't compile. A fluent answer is not proof of accuracy, so verify important claims and test generated code.

**Tokens and context limits.** Models process text in tokens, and the number of tokens differs from the number of words or characters and varies between models. Every model has a **context window**: the maximum number of tokens it can handle in one request, covering your prompt, the conversation history, attached documents, and the model's own reply. A large window doesn't guarantee that the model uses everything well, since in very long inputs it can overlook an instruction. Keep prompts focused and include only the context the task needs.

**Safe use.** Match your verification to the cost of a mistake: a brainstorm needs a quick read, while security-sensitive code or financial, medical, and legal advice need authoritative sources and human review. Never share credentials, personal data, or confidential code with a system that isn't approved for it.

## Ways to use LLMs

- **Chat interfaces** such as ChatGPT suit general users who brainstorm, summarize, or translate.

- **Command-line tools and REPLs** (for example, `ollama run llama3 "Ten fun names for a pet pelican"`, or interactive tools like Claude Code) suit developers who automate tasks or run models locally.

- **APIs** let your own application send a request to a model and receive a response. They are how AI features end up inside IDEs, productivity tools, websites, and design software. A request names a model and carries the messages:



```json
{
  "model": "<model name>",
  "max_tokens": 1024,
  "messages": [
    {"role": "user", "content": "Summarize this article: ..."}
  ]
}
```



Two techniques control how the answer arrives:

- **Structured output** makes the model return data in a fixed format, typically JSON that follows a schema, so a program can parse it. This is also the basis of **tool calling**: the model returns the name of a function and its arguments, and your code runs it.

- **Streaming** delivers the reply piece by piece as it is generated, so users see text appear immediately instead of waiting for the full answer.

## LLMs in Kotlin

You can work with LLMs from Kotlin at three levels:

- **Plain HTTP.** Send the request with an HTTP client such as Ktor Client, and use kotlinx.serialization to turn Kotlin classes into JSON and back. It gives full control but means more code.

- **Provider SDKs.** For example, the official OpenAI Java SDK works from Kotlin because Kotlin runs on the JVM. Community libraries such as `openai-kotlin` are written in Kotlin and, as of this writing, support multiplatform projects.

- **Frameworks.** Spring AI and LangChain4j are JVM frameworks that handle prompts, memory, and tools for you. LangChain4j has a Kotlin module with suspend functions and a `Flow` for streaming responses. Koog, from JetBrains, is a framework for building AI agents written in Kotlin.

Several Kotlin features fit LLM work well:

- **Coroutines.** A model call takes seconds, and a `suspend` function lets you wait without blocking a thread.

- **Flow.** A streamed reply is naturally a `Flow`.

- **Data classes with serialization.** They give structured output a type.



```kotlin
interface Assistant {
    suspend fun ask(message: String): String            // one complete reply
    fun askStreaming(message: String): Flow<String>     // reply arrives in pieces
}
```





```kotlin
@Serializable
data class TicketInfo(val category: String, val priority: String, val summary: String)

private val json = Json { ignoreUnknownKeys = true }

fun parseTicket(modelOutput: String): TicketInfo? =
    try {
        json.decodeFromString<TicketInfo>(modelOutput)
    } catch (e: SerializationException) {
        null // not valid JSON for this class: retry or report an error
    }
```



Always treat structured output as untrusted input. Even with a schema, the output may not parse, so handle failure explicitly. Also, hide the model behind an interface like `Assistant`. Real responses are non-deterministic and cost money, so tests should use a fake implementation.

LLMs also *write* Kotlin, and the limitations above apply. Kotlin, Gradle plugins, Compose, and KMP libraries change quickly, so an assistant may suggest deprecated APIs, outdated build configuration, or Android-only code in a shared module. Name your versions and targets in the prompt, compile what you get, and check the official documentation.

## LLMs in Kotlin Multiplatform

In a multiplatform app, the main question is **where the LLM code runs**.

**On a backend.** JVM-only libraries, including the OpenAI Java SDK, Spring AI, and LangChain4j, can't be used in `commonMain`. They belong in a server or a JVM-only source set. The shared client code calls your server over HTTP. This also protects secrets: an API key placed in an app can be extracted from the app, while a key on your server never reaches the client. The server can also keep conversation memory and enforce limits.

**In shared client code.** Ktor Client, kotlinx.serialization, and coroutines all work in `commonMain`, so you can call an API (your own, or a provider's) once for every platform. Streaming maps to a `Flow` that your UI observes. Multiplatform libraries can also do more. Koog lists support for JVM, JS, Wasm, and iOS targets (check its documentation for the exact list, including Android), and `openai-kotlin` asks you to choose a Ktor engine for each target. The official Kotlin SDK for the Model Context Protocol (MCP), which is a standard for connecting models to tools, is also multiplatform and offers client and server modules.

**On the device.** Some models run locally. On Android, ML Kit's GenAI APIs use Gemini Nano for tasks like summarization, proofreading, and rewriting, and the MediaPipe LLM Inference API can run other open models. On-device models work offline, keep data on the device, and have no per-request cost, but they are platform-specific and smaller. In a KMP project, put an interface like `Assistant` in `commonMain` and write a separate implementation for each platform.

Where it runs

Good for

Watch out for

Your backend

Hiding keys, shared memory and tools, one implementation for every client

You run and pay for a server

Shared client code

One codebase for every platform

Never ship secrets inside the app

On the device

Offline use, privacy, no per-request cost

Platform-specific, limited models

## Conclusion

LLMs are trained in two stages, pre-training and fine-tuning. Generative models write text token by token, embedding models turn text into vectors, and RAG combines the two. Models have a knowledge cutoff, hallucinate, and are limited by their context window, so verify what matters and protect sensitive data.

Applications reach models through APIs, usually with structured output and streaming. In Kotlin, you can use plain HTTP, SDKs, or frameworks, and coroutines, `Flow`, and serializable data classes fit the job. In KMP, decide whether the LLM code runs on a backend, in shared code, or on the device, and keep it behind an interface.

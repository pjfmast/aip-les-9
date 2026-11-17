# Retrieval-augmented generation with Spring AI

In this topic, we will look at how Spring AI handles retrieval-augmented generation. We will cover the `VectorStore` abstraction, the ETL pipeline that populates it, and how to wire retrieval into `ChatClient` calls using advisors.

## The VectorStore abstraction

In Spring AI, `VectorStore` is the central interface for storing and searching document embeddings. It abstracts away the specific vector database underneath — you interact with the same interface whether you're using Qdrant, PGVector, Chroma, Pinecone, or any other supported store. Spring Boot auto-configures the correct implementation based on your dependencies and `application.yml` configuration.

The `VectorStore` interface exposes three core operations:



```kotlin
// Store documents (embeddings are generated automatically by the configured EmbeddingModel)
vectorStore.add(documents)

// Search for the most semantically similar documents to a query
val results: List<Document> = vectorStore.similaritySearch(
    SearchRequest.builder()
        .query("What platforms does Kotlin support?")
        .topK(5)
        .similarityThreshold(0.7)
        .build()
)

// Remove documents by ID
vectorStore.delete(listOf("doc-id-1", "doc-id-2"))
```



Embedding generation is handled internally by `VectorStore` — you hand it `Document` objects containing plain text, and it calls the configured `EmbeddingModel` to produce and store the vectors. You never touch the embedding step directly during ingestion.

Switching from one vector store to another requires only a dependency and configuration change. For example, to use Qdrant:



```kotlin
// build.gradle.kts
implementation("org.springframework.ai:spring-ai-starter-vector-store-qdrant:[LATEST_VERSION]")
```





```yaml
# application.yml
spring:
  ai:
    vectorstore:
      qdrant:
        host: localhost
        port: 6334
        collection-name: kotlin-docs
        initialize-schema: true
```



Your application code — `vectorStore.add(...)`, `vectorStore.similaritySearch(...)` — requires no changes.

## The ETL pipeline

Before you can retrieve anything, you need to load documents into the vector store. Spring AI models this as an ETL (Extract, Transform, Load) pipeline composed of three interfaces that chain together naturally in Kotlin:

- `DocumentReader` — extracts raw content from a source and produces a `List`. It extends `Supplier>`, so you call `.read()` (or `.get()`) on it.

- `DocumentTransformer` — processes a list of documents and returns a transformed list. It extends `Function, List>`, so you call `.transform()` (or `.apply()`) on it.

- `DocumentWriter` — stores the final documents somewhere. It extends `Consumer>`, so you call `.write()` (or `.accept()`) on it. `VectorStore` is the most common `DocumentWriter` implementation.

A minimal pipeline that reads a PDF, splits it into chunks, and stores the results looks like this:



```kotlin
import org.springframework.ai.reader.pdf.PagePdfDocumentReader
import org.springframework.ai.transformer.splitter.TokenTextSplitter
import org.springframework.core.io.ClassPathResource

val pdfReader = PagePdfDocumentReader(ClassPathResource("docs/kotlin-reference.pdf"))
val splitter = TokenTextSplitter()

// Extract → Transform → Load, chained in a single line
vectorStore.write(splitter.split(pdfReader.read()))
```



The chaining style mirrors Kotlin's own collection pipeline idiom. You can also write it using the functional aliases (`.accept()` / `.apply()` / `.get()`), but the named methods read more clearly.

## Built-in readers and transformers

Spring AI ships a range of ready-made implementations for each step of the pipeline.

Readers include `PagePdfDocumentReader` for PDFs, `TextReader` for plain text files, `JsonReader` for JSON, and `TikaDocumentReader` (via Apache Tika) for a broad range of formats including Word, Excel, HTML, and more. They all accept a Spring `Resource` in their constructor, so loading from the classpath, a file path, or a URL is handled the same way.

Transformers include `TokenTextSplitter` (splits text into chunks based on token count, the most commonly used splitter), `KeywordMetadataEnricher` (uses a `ChatModel` to extract keywords from each document and adds them as metadata), and `SummaryMetadataEnricher` (generates summaries for each document and stores them as metadata, which improves retrieval precision).

A slightly richer pipeline that also enriches documents with keywords before storing them:



```kotlin
import org.springframework.ai.enricher.KeywordMetadataEnricher

@Component
class IngestionService(
    private val vectorStore: VectorStore,
    private val chatModel: ChatModel
) {
    fun ingest(resource: Resource) {
        val documents = TextReader(resource).read()
        val chunked = TokenTextSplitter().split(documents)
        val enriched = KeywordMetadataEnricher(chatModel, 5).apply(chunked)
        vectorStore.write(enriched)
    }
}
```



## Retrieval-augmented chains with ChatClient

Once the `VectorStore` is populated, wiring RAG into your `ChatClient` calls is a single-line change — you add `QuestionAnswerAdvisor` to the advisor chain. When a request arrives, the advisor automatically embeds the user query, searches the `VectorStore` for the most similar documents, and injects them as context into the prompt before the model call:



```kotlin
import org.springframework.ai.chat.client.ChatClient
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor
import org.springframework.ai.vectorstore.SearchRequest
import org.springframework.stereotype.Service

@Service
class RagService(
    chatClientBuilder: ChatClient.Builder,
    private val vectorStore: VectorStore
) {
    private val chatClient = chatClientBuilder
        .defaultSystem(
            "Answer only from the provided context. " +
            "If the answer is not in the context, say 'I don't know.'"
        )
        .build()

    fun ask(question: String): String? =
        chatClient
            .prompt()
            .advisors(
                QuestionAnswerAdvisor.builder(vectorStore)
                    .searchRequest(
                        SearchRequest.builder()
                            .topK(5)
                            .similarityThreshold(0.7)
                            .build()
                    )
                    .build()
            )
            .user(question)
            .call()
            .content()
}
```



`topK` controls how many chunks are retrieved and added to the context. `similarityThreshold` filters out low-relevance matches — raising it toward 1.0 makes retrieval more selective, while lowering it toward 0.0 includes more (but noisier) results. A value of 0.7 is a common starting point.

If you want the same RAG advisor applied to every request rather than just one, register it on the builder using `.defaultAdvisors()`:



```kotlin
private val chatClient = chatClientBuilder
    .defaultAdvisors(
        QuestionAnswerAdvisor.builder(vectorStore).build()
    )
    .build()
```



## Filtering by metadata

`SearchRequest` supports metadata filters using a portable SQL-like expression syntax, which works across all `VectorStore` implementations. This lets you scope retrieval to a specific subset of documents — useful when multiple document collections are stored in the same vector store:



```kotlin
val results = vectorStore.similaritySearch(
    SearchRequest.builder()
        .query("coroutines")
        .topK(5)
        .filterExpression("source == 'kotlin-reference'")
        .build()
)
```



You can also update the filter at runtime per request using the `QuestionAnswerAdvisor.FILTER_EXPRESSION` advisor parameter, which is useful when the filter depends on the logged-in user or the current session.

## Spring AI vs LangChain4j for RAG

Both Spring AI and LangChain4j provide complete RAG pipeline tooling for the JVM. The practical distinction is ecosystem fit:

- Spring AI integrates naturally into existing Spring Boot applications — dependency injection, auto-configuration, and `application.yml` all work as expected. If your team is already on Spring Boot, Spring AI adds RAG without requiring any new patterns.

- LangChain4j is more lightweight and doesn't require Spring, which makes it the better fit for non-Spring JVM backends, Kotlin Multiplatform modules, or when you want a smaller dependency footprint.

The `VectorStore` interface and `QuestionAnswerAdvisor` pattern shown above correspond directly to the `EmbeddingStore` and `EmbeddingSearchRequest` pattern used in the "Building a RAG pipeline in Kotlin" topic — the concepts are the same, the API shapes differ.

## Conclusion

In this topic, we covered `VectorStore`, Spring AI's provider-agnostic interface for storing and searching document embeddings, where switching backends requires only a dependency and configuration change. The ETL pipeline — `DocumentReader` → `DocumentTransformer` → `DocumentWriter` — is Spring AI's three-step model for loading documents into a `VectorStore`; its interfaces extend standard Java functional types, making them easy to chain. `QuestionAnswerAdvisor` wires RAG into `ChatClient` calls with a single builder call, automatically retrieving relevant documents and injecting them as context before each model request.

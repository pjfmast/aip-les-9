# Building a RAG pipeline in Kotlin

In this topic, we will build a complete end-to-end RAG pipeline in Kotlin — from splitting a document into chunks and embedding them, to storing them in Qdrant, retrieving the most relevant context, and generating a grounded answer with an LLM. This brings together the tools from the previous topics: LangChain4j for chunking and embedding, Qdrant for vector storage, and an OpenAI chat model for generation.

## Setup

First, make sure Qdrant is running locally before you start:



```shell
docker run -p 6333:6333 -p 6334:6334 qdrant/qdrant
```



Add the following dependencies to your Gradle build file:



```shell
dependencies {
    implementation("dev.langchain4j:langchain4j:[LATEST_VERSION]")
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:[LATEST_VERSION]")
    implementation("dev.langchain4j:langchain4j-qdrant:[LATEST_VERSION]")
    implementation("dev.langchain4j:langchain4j-open-ai:[LATEST_VERSION]")
}
```



> **Note:**

Replace `[LATEST_VERSION]` with the current version available on Maven Central.

## Preprocessing the data

Before we can store or search anything, the raw document needs to go through three preparation steps: loading, chunking, and embedding.

We'll work with a short document about Kotlin throughout this topic. The data could be loaded from a file, a database, or a web scrape — the rest of the pipeline is identical regardless of source:



```kotlin
import dev.langchain4j.data.document.Document

val document = Document.from(
    """
    Kotlin is a modern, statically typed programming language developed by JetBrains.
    It runs on the JVM and is fully interoperable with Java, meaning existing Java
    libraries and frameworks work without modification.
    Kotlin was designed to be concise, safe, and expressive, eliminating many of the
    common sources of bugs found in Java such as null pointer exceptions.
    It became an official language for Android development in 2017.
    Kotlin Multiplatform allows sharing business logic across JVM, Android, iOS,
    and JavaScript targets from a single codebase.
    """.trimIndent()
)
```



Next, we split the document into overlapping chunks using LangChain4j's recursive character splitter. The size parameters are in characters:



```kotlin
import dev.langchain4j.data.document.splitter.DocumentSplitters
import dev.langchain4j.data.segment.TextSegment

val splitter = DocumentSplitters.recursive(
    200,  // max characters per chunk
    20    // overlap between consecutive chunks
)
val segments: List<TextSegment> = splitter.split(document)

println("Chunks: ${segments.size}")
segments.forEach { println("- ${it.text()}") }
```



Finally, each chunk is converted into a vector using the in-process `AllMiniLmL6V2EmbeddingModel`. No API key or network call is required — the model runs directly inside the JVM:



```kotlin
import dev.langchain4j.data.embedding.Embedding
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel

val embeddingModel = AllMiniLmL6V2EmbeddingModel()

val embeddings: List<Embedding> = embeddingModel.embedAll(segments).content()

println("Dimensions per vector: ${embeddings.first().vectorAsList().size}") // 384
```



The all-MiniLM-L6-v2 model produces 384-dimensional vectors. This number matters when creating the Qdrant collection in the next step.

## Storing the data in Qdrant

We create the Qdrant collection manually first (to control the distance metric and vector size), then hand the `QdrantEmbeddingStore` to LangChain4j as the storage backend. The `QdrantEmbeddingStore` talks to Qdrant over gRPC — the same port we exposed when starting Docker:



```kotlin
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import io.qdrant.client.grpc.Collections.Distance
import io.qdrant.client.grpc.Collections.VectorParams
import dev.langchain4j.store.embedding.qdrant.QdrantEmbeddingStore

const val COLLECTION = "kotlin_docs"

// Create collection if it doesn't already exist
val qdrantClient = QdrantClient(
    QdrantGrpcClient.newBuilder("localhost", 6334, false).build()
)
if (!qdrantClient.collectionExistsAsync(COLLECTION).get()) {
    qdrantClient.createCollectionAsync(
        COLLECTION,
        VectorParams.newBuilder()
            .setSize(384)           // must match the embedding model's output dimension
            .setDistance(Distance.Cosine)
            .build()
    ).get()
}

// Wrap the collection as a LangChain4j EmbeddingStore
val vectorStore = QdrantEmbeddingStore.builder()
    .host("localhost")
    .port(6334)           // gRPC port
    .collectionName(COLLECTION)
    .build()

// Insert all embeddings alongside their source segments
vectorStore.addAll(embeddings, segments)
println("Stored ${segments.size} chunks in Qdrant")
```



After this step you can inspect the stored points in the Qdrant dashboard at `http://localhost:6333/dashboard`.

## Retrieval

When a user submits a query, we embed it with the same model used during indexing, then search Qdrant for the most similar chunks. We use `EmbeddingSearchRequest` — the current LangChain4j search API:



```kotlin
import dev.langchain4j.store.embedding.EmbeddingMatch
import dev.langchain4j.store.embedding.EmbeddingSearchRequest

val query = "What platforms does Kotlin support?"

// Embed the query with the same model used during indexing
val queryEmbedding = embeddingModel.embed(query).content()

// Retrieve the top 3 most similar chunks
val searchRequest = EmbeddingSearchRequest.builder()
    .queryEmbedding(queryEmbedding)
    .maxResults(3)
    .minScore(0.0)     // include all results; raise this to filter out low-relevance matches
    .build()

val matches: List<EmbeddingMatch<TextSegment>> = vectorStore.search(searchRequest).matches()

// Assemble the retrieved chunks into a single context string
val context = matches.joinToString("\n\n") { it.embedded().text() }

println("Retrieved context:\n$context")
```



Each `EmbeddingMatch` also carries a `score()` between 0 and 1 indicating how relevant the chunk is to the query — useful for debugging or for filtering out low-quality matches by raising `minScore`.

## Generation

Finally, we combine the original query and the retrieved context into a prompt and send it to a chat model. The model is instructed to answer only from the retrieved context, which grounds the response and prevents hallucination:



```kotlin
import dev.langchain4j.model.openai.OpenAiChatModel

val chatModel = OpenAiChatModel.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .modelName("gpt-4o-mini")
    .build()

val prompt = """
    Answer the following question using only the context provided below.
    If the answer is not present in the context, say "I don't know."

    Context:
    $context

    Question: $query
""".trimIndent()

val answer = chatModel.generate(prompt)
println("Answer: $answer")
```



Example output:



```shell
Answer: Kotlin supports JVM, Android, iOS, and JavaScript targets.
Through Kotlin Multiplatform, developers can share business logic
across all of these platforms from a single codebase.
```



## The complete pipeline

Putting all five steps together, the flow is:



```shell
Document
    → DocumentSplitters.recursive()     [chunking]
    → AllMiniLmL6V2EmbeddingModel       [embedding]
    → QdrantEmbeddingStore.addAll()     [storage]

Query
    → AllMiniLmL6V2EmbeddingModel       [embed query]
    → EmbeddingSearchRequest / search() [retrieval]
    → OpenAiChatModel.generate()        [generation]
```



The `EmbeddingModel` appears in both branches — this is intentional and important: the query must be embedded with the exact same model used during indexing, otherwise the vectors live in different spaces and similarity scores become meaningless.

One of the main benefits of building the pipeline through LangChain4j's interfaces is that individual components are easy to swap without touching the rest:

- `AllMiniLmL6V2EmbeddingModel` → any other `EmbeddingModel` (e.g. an OpenAI embedding model via `langchain4j-open-ai`), as long as the Qdrant collection's vector size is updated to match.

- `QdrantEmbeddingStore` → `InMemoryEmbeddingStore` for local testing without Docker.

- `OpenAiChatModel` → any other `ChatLanguageModel` (Cohere, Anthropic, a local model via vLLM) — the `generate()` call stays identical.

- `DocumentSplitters.recursive()` → any of the chunking strategies.

## Conclusion

In this topic, we walked through a complete RAG pipeline in Kotlin: chunking with `DocumentSplitters`, embedding with `AllMiniLmL6V2EmbeddingModel`, storing in Qdrant via `QdrantEmbeddingStore`, retrieving with `EmbeddingSearchRequest`, and generating with a `ChatLanguageModel`.

Two points are worth keeping in mind: the query must be embedded with the same model used during indexing, since a different model produces incompatible vector spaces and breaks similarity search; and LangChain4j's interface-based design makes it straightforward to swap any component — embedding model, vector store, or chat model — without changing the surrounding pipeline code.

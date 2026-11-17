# Embeddings and vector stores in Spring AI

Keyword search finds text that contains the words you typed. But a user who asks for "a quiet walk with a view of the water" may not use a single word that appears in the trail description they would love. To search by meaning, you need **embeddings** and a **vector store**, a database that stores embeddings and searches them by similarity.

In this topic, you'll learn what embeddings are, how to store texts in a vector store and search them by meaning with Spring AI, and how to combine semantic search with metadata filters.

## Embeddings

An **embedding** is a list of numbers, a vector, that represents the meaning of a text. An **embedding model** turns text into such a vector, and its length (the **dimensionality**) depends on the model. Texts with similar meaning get vectors that are close to each other, and the distance between two vectors measures how similar the texts are. This is the representational type of model from the introduction to LLMs: it doesn't generate text, it encodes it.

Searching with embeddings works in two steps. First, you embed every text once and store the vectors. Then, to answer a query, you embed the query with the same model and find the stored vectors nearest to it. "A lakeside stroll" ends up close to "a path along the water's edge" even though they share few words.

Spring AI represents an embedding model as an `EmbeddingModel`. Some models are hosted (you call an API with a key), and some run inside your application. Spring AI's Transformers (ONNX) model is one of the latter: by default it uses `all-MiniLM-L6-v2`, which produces 384-dimensional vectors and needs no API key. To use it, add the Spring AI BOM, which manages the versions of all Spring AI artifacts, and the starter for the model to your `build.gradle.kts`:



```kotlin
dependencies {
    // Use the Spring AI version that your project is built for
    implementation(platform("org.springframework.ai:spring-ai-bom:$springAiVersion"))
    implementation("org.springframework.ai:spring-ai-starter-model-transformers")
}
```



With the starter on the classpath, Spring Boot auto-configures an `EmbeddingModel` bean, and no properties are needed to use the default model. To use a different ONNX model, point the model and its tokenizer to other files in `application.properties`:



```no-highlight
spring.ai.embedding.transformer.onnx.model-uri=https://huggingface.co/intfloat/e5-small-v2/resolve/main/model.onnx
spring.ai.embedding.transformer.tokenizer.uri=https://huggingface.co/intfloat/e5-small-v2/raw/main/tokenizer.json
```



When model files are fetched from a URL, they are downloaded the first time the model is used and cached locally, so the first start is slower.

A hosted model is set up the same way: you replace the starter and configure the provider. For example, with `spring-ai-starter-model-openai` you set `spring.ai.openai.api-key` and choose the embedding model with `spring.ai.openai.embedding.options.model`. If more than one embedding starter is on the classpath, select the one to use with `spring.ai.model.embedding`, for example `spring.ai.model.embedding=transformers`.

Two rules follow from how embeddings work:

- **Use the same model for the stored texts and the queries.** Vectors from different models aren't comparable.

- **If you change the model, re-embed everything.** The new vectors differ, and the dimensionality may change too.

You rarely call the `EmbeddingModel` yourself. The vector store uses it for you.

## The vector store

A **vector store** stores vectors together with the original text and metadata, and it can quickly find the vectors nearest to a query. It doesn't create embeddings itself. In Spring AI, the `VectorStore` implementation takes the `EmbeddingModel` configured in your application and uses it both when you add texts and when you search.

`VectorStore` is a common API with many implementations, such as Qdrant, PostgreSQL with pgvector, Redis, and Neo4j. Your code stays the same across them, but each implementation has its own capabilities and quirks, especially in how it handles metadata filters. This topic uses **Qdrant**, and the quirks that you'll see below are Qdrant's.

Add the Qdrant starter to the dependencies:



```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-starter-vector-store-qdrant")
}
```



Then configure the connection in `application.properties`:



```no-highlight
spring.ai.vectorstore.qdrant.host=localhost
spring.ai.vectorstore.qdrant.port=6334
spring.ai.vectorstore.qdrant.collection-name=trails
spring.ai.vectorstore.qdrant.initialize-schema=true
```



Port 6334 is Qdrant's gRPC port, which the store uses. For local development, you can run Qdrant from its `qdrant/qdrant` Docker image. `initialize-schema` is off by default. When enabled, the store creates the collection if it doesn't exist, with the dimensionality of your embedding model and cosine similarity as the distance metric. If you switch to an embedding model with a different dimensionality, you have to re-create the collection.

## Adding texts

Spring AI wraps what you store in a `Document`: an ID, text, and metadata. Don't confuse it with a file: a `Document` is whatever unit you want to find later, such as a trail description, a help article, or one section of a longer text.



```kotlin
val documents = trails.map { trail ->
    Document(
        trail.id, // a stable ID, in a format the store accepts (Qdrant takes UUIDs)
        "${trail.name}. ${trail.description}",
        mapOf(
            "region" to trail.region,
            "difficulty" to trail.difficulty, // "easy", "moderate" or "hard"
            "durationHours" to trail.durationHours,
        ),
    )
}

vectorStore.add(documents)
```



Adding is usually a separate step from serving requests, for example an import job or an admin endpoint, and not something the application does on every start. A persistent store like Qdrant keeps the vectors between runs.

The choices you make here decide how good the search is:

- **The text is what gets embedded.** Embedding models are trained on natural language, so write the text as clear, natural sentences, and include the title or heading that gives the passage its context. Text made of codes, abbreviations, or table fragments embeds poorly.

- **The metadata is what you can filter on.** Put in the fields that will be hard requirements, such as a category, a level, or a number to compare. Keep values to strings and numbers. Support for other types, such as booleans, lists, or nested objects, differs between stores, and some filters fail on them. For example, Qdrant's `==` and `!=` filters work with strings and numbers only, so a boolean field like `dogFriendly` would fail in a filter. Represent it as a string instead. Numbers can also behave differently from what you expect, for example a `Long` may be stored as a string, so test every filter on real data.

- **Decide what belongs in the store by how often it changes.** The vector and the metadata are written when you add the `Document`. Attributes that change occasionally, such as a category or a status, are fine in the metadata: when one changes, add the `Document` again with the same ID, and you can keep filtering on it inside the search. Live state that changes constantly, such as a stock level, a price, or the number of free places, is a bad fit, because updating the store on every change isn't practical. Keep it in your database and read it after the search, using the ID from the result. In general, treat the vector store as an index for finding things, not as a second copy of your database.

- **Use stable IDs.** With them, adding a `Document` with the same ID again normally replaces it instead of creating a duplicate (Qdrant upserts by ID), but check how your store behaves.

## Searching

You search with a `SearchRequest`:



```kotlin
val results: List<Document> = vectorStore.similaritySearch(
    SearchRequest.builder()
        .query("a quiet walk with a view of the water")
        .topK(5)
        .similarityThreshold(0.5)
        .filterExpression("difficulty == 'easy' && durationHours <= 3")
        .build()
)

results.forEach { doc ->
    println("${doc.score}  ${doc.text}  ${doc.metadata["durationHours"]}")
}
```



The request has these parts:

- `query` is embedded and compared with the stored vectors.

- `topK` is the maximum number of results. The default is 4.

- `similarityThreshold` is a value between 0 and 1. Results below it are dropped, so you can get fewer than `topK` results. The default is 0, which accepts everything.

- `filterExpression` restricts the search by metadata. The text syntax supports `==`, `!=`, ``, `>=`, `&&`/`AND`, `||`/`OR`, `NOT`, and `in [...]`/`nin [...]`.

Each result is a `Document` with its text, its metadata, and a `score`, which is `null` unless the store sets it during retrieval. The score is a similarity: a higher value means a closer match. But its scale is not universal. It depends on the embedding model and on the distance metric of the store, so the same number can mean different things in different setups, and a threshold of 0.5 that works in one application can be too strict or too lax in another. Don't copy thresholds from examples. Instead, run queries that have good matches in your collection and queries about things that aren't in it, look at the real scores, and choose a threshold that separates the two groups.

A search always returns the nearest texts, even if none of them is actually close. Asking for something that isn't in your collection still returns the least bad matches. That is why the threshold matters. You can also compare each score with the best one in the results, which keeps a clear winner on its own while still returning several similar items for a vague query.

## Filter in the store, not afterwards

A similarity search ranks by meaning, not by constraints. "An easy lakeside walk" is close in meaning to a demanding climb around a lake, because both are about a lake and a walk. If you search first and filter the results afterwards, the closest five might all be hard trails. The filter then removes all five, and you return nothing, even though easy trails exist further down the ranking.

The fix is to give the constraint to the store as a filter. The store then considers only the entries that qualify. Qdrant, for example, applies the filter inside the search: it checks each candidate against the filter while walking its index, so entries that fail are skipped and never occupy a place in the results.

This gives you a simple rule:

- Put the soft part of the request, the meaning, into the query.

- Put the hard requirements, the ones that must hold, into the filter.

## Building filters in code

A filter written as a text expression is just a string. If you build it by inserting a value that a model or a user provided, that value can break the expression or widen it. For example, the difficulty `easy' || difficulty != 'easy` turns `difficulty == '$difficulty'` into a filter that matches everything.

The safer way is to build the filter as an object with `FilterExpressionBuilder`. The field names are fixed in your code, and every value is passed as a single value that cannot change the structure of the expression. You should still check each value against a known list before using it, as with any argument from a model:



```kotlin
@Service
class TrailSearch(private val vectorStore: VectorStore) {

    private val difficulties = setOf("easy", "moderate", "hard")

    fun search(query: String, difficulty: String?, maxHours: Int?): List<Document> {
        require(difficulty == null || difficulty in difficulties) { "Unknown difficulty '$difficulty'" }

        val b = FilterExpressionBuilder()
        val conditions = listOfNotNull(
            difficulty?.let { b.eq("difficulty", it) },
            maxHours?.let { b.lte("durationHours", it) },
        )
        val filter = conditions.reduceOrNull { left, right -> b.and(left, right) }?.build()

        return vectorStore.similaritySearch(
            SearchRequest.builder()
                .query(query)
                .topK(5)
                .similarityThreshold(0.5)
                .filterExpression(filter) // null means no filter
                .build()
        )
    }
}
```



Only the constraints that were given become part of the filter, and `and` combines them. The builder produces Spring AI's portable `Filter.Expression`, and the store converts it into its native filter, which is the same thing that happens with a text expression.

## Connecting a vector store to a ChatClient

A vector store becomes useful for a model when the retrieved texts reach its prompt. Spring AI gives you two ways to connect a `VectorStore` to a `ChatClient`, and they differ in who decides when to search.

## Automatic retrieval with an advisor

`QuestionAnswerAdvisor` is an advisor, like the one you used for chat memory. On every request, it takes the user's message as the query, runs a similarity search, and adds the retrieved texts to the prompt before the model sees it. It comes from a separate dependency:



```kotlin
dependencies {
    implementation("org.springframework.ai:spring-ai-advisors-vector-store")
}
```





```kotlin
@Service
class GuideAssistant(chatClientBuilder: ChatClient.Builder, vectorStore: VectorStore) {

    private val chatClient = chatClientBuilder
        .defaultSystem("You answer questions about hiking trails. Use the provided context.")
        .defaultAdvisors(
            QuestionAnswerAdvisor.builder(vectorStore)
                .searchRequest(SearchRequest.builder().topK(4).similarityThreshold(0.5).build())
                .build()
        )
        .build()

    fun ask(question: String, difficulty: String): String? =
        chatClient.prompt()
            .user(question)
            .advisors { it.param(QuestionAnswerAdvisor.FILTER_EXPRESSION, "difficulty == '$difficulty'") }
            .call()
            .content()
}
```



The advisor makes retrieval automatic and predictable: every answer is grounded in the store, and you write no extra code. The `FILTER_EXPRESSION` parameter lets your code set a filter for a single request. The same rules about building filters from untrusted values apply here.

The trade-off is that the model has no say in the search. The query is the raw user message, so a follow-up like "and the second one?" makes a poor query. The search also runs on every turn, even for a greeting, which costs an embedding, a search, and extra prompt tokens. And the model cannot refine the query, pick filters, or search twice.

## Search as a tool

The other option is to wrap the search in a tool, as in the previous topic, so the model decides when to retrieve and with what. The model passes the query and the optional constraints as arguments, and your code validates them, builds the filter, trims the results, and returns a small result:



```kotlin
@Component
class TrailTools(private val trailSearch: TrailSearch) {

    @Tool(
        name = "search_trails",
        description = "Searches the trail guide by meaning. Use it when the user wants trail suggestions. " +
            "Returns up to 5 trails, best match first.",
    )
    fun searchTrails(
        @ToolParam(description = "What the user is looking for, in natural language, for example 'quiet walk with a lake view'")
        query: String,
        @ToolParam(description = "Difficulty: easy, moderate or hard", required = false)
        difficulty: String?,
        @ToolParam(description = "Maximum duration in hours", required = false)
        maxHours: Int?,
    ): Any {
        val hits = try {
            trailSearch.search(query, difficulty, maxHours)
        } catch (e: IllegalArgumentException) {
            return mapOf("error" to e.message)
        }
        if (hits.isEmpty()) return mapOf("notFound" to "No matching trails")

        return hits.map { TrailHit(it.id, it.text?.take(200), it.metadata["durationHours"]) }
    }
}

data class TrailHit(val id: String, val summary: String?, val durationHours: Any?)
```



The result holds the ID, so anything that changes, such as whether a trail is currently closed, can be read from your database and added to the result instead of being stored in the vector store.

With a tool, the model chooses whether to search at all, can rewrite a vague user message into a good query, can fill in constraints it heard in the conversation, and can search several times. The price is less predictability: it may skip the search or call it badly, so the validation in your code matters.

As a rule of thumb, use the advisor for a question-and-answer assistant that must always answer from the stored texts. Use a tool when the search is one of several things an agent can do, or when the model needs to control the query and the filter.

## Conclusion

Embeddings turn text into vectors so that search can follow meaning. A `VectorStore` keeps texts with their vectors and metadata and finds the nearest ones, using an `EmbeddingModel` that must be the same for storing and searching. The text you embed should be clear natural language, the metadata should hold the fields you want to filter on, and fast-changing state should stay in your database.

A search returns the nearest entries even when none is close, so use a similarity threshold and calibrate it on real scores. Put the meaning in the query and the hard requirements in a metadata filter that the store applies during the search, built in code with `FilterExpressionBuilder` when its values come from a model or a user. To give a model access to the store, use `QuestionAnswerAdvisor` for automatic retrieval on every turn, or wrap the search in a tool when the model should decide when and what to search for.

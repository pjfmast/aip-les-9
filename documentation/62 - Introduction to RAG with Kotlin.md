# Introduction to RAG with Kotlin

In this topic, we will look at one of the most important applications of large language models — retrieval-augmented generation (RAG), which helps language models provide more accurate and up-to-date responses by combining their capabilities with external knowledge retrieval.

## Motivation

Autoregressive LLMs have a few limitations: they confidently hallucinate non-existent information and, due to their training process (the training objective is to predict the next most probable token based on the unstructured, un-indexed data), can't point out the specific sources of their outputs. Also, they have a knowledge cut-off point, so recent information doesn't make it into the training at all.

These issues led to the development of **retrieval-augmented generation**, or **RAG** (which was originally introduced in 2020). RAG combines traditional indexing and search with an autoregressive model, increasing the response accuracy and making citation possible, and allowing the injection of up-to-date or private information into the responses without affecting the underlying LLM.

While fine-tuning of the LLM can be performed, it has several limitations besides the hardware requirements:

- LLMs typically struggle to learn new factual information through unsupervised fine-tuning

- RAG, on average, produces better results than fine-tuning for incorporating new information.

To give a brief example, say you have a bunch of internal organization documents scattered across multiple sources (e.g., GitLab, ReadTheDocs, Word, and Notion), and would like to interact (not only search through, but also perform any task that current LLMs are capable of, such as suggesting improvements, fixing errors, summarizing, etc.) with the sources through a single interface — this is a typical use case for retrieval-augmented generation.

## The naive RAG

To start, let's look at the most basic configuration for retrieval-augmented generation — a type known as the **naive RAG**:

![A naive RAG](https://ucarecdn.com/a1ce3a7c-f6e5-455e-bc26-d2be87da5553/)

The naive RAG includes 3 main steps: indexing, retrieval, and generation.

The **indexing** step starts with the preprocessing of the data, often in multiple formats (besides various text formats, it also might include non-text formats — e.g., images, but it's a bit more tricky), and then converting it into plain text. Then, the plain text data is **chunked** (typically a chunk is 256 to 512 tokens in length) for two reasons:

- Chunking helps with the search in the retrieval phase — it allows to focus on the more relevant units of the text and helps to find the relevant information across multiple documents.

- LLMs have a context window, which limits how much an LLM can retain. Additionally, the embedding model has a maximum sequence length it can process at a time. Chunking helps to bypass these limitations.

Once the chunking is complete, each chunk is passed into an embedding model and turned into a vector (an embedding). Then, the embeddings are loaded into the vector database. On the JVM, this whole pipeline — splitting, embedding, and storing — can be wired together in a few lines with LangChain4j, a Kotlin-friendly toolkit we'll use throughout this course for RAG and agent-related topics:



```kotlin
val splitter = DocumentSplitters.recursive(512, 50)
val embeddingModel = AllMiniLmL6V2EmbeddingModel()
val vectorStore = InMemoryEmbeddingStore<TextSegment>()

val pipeline = EmbeddingStoreIngestor(splitter, embeddingModel, vectorStore)
pipeline.ingest(document)
```



For production use, `InMemoryEmbeddingStore` is simply swapped for a Qdrant-backed store — the rest of the pipeline stays identical.

The indexing phase can be illustrated as follows:

![The steps for indexing](https://ucarecdn.com/734709c5-d2a1-4afd-96bf-90158315fec2/)

Then, a user makes a query, which is also turned into an embedding with the same model from the first step. Next, the similarity (some measure of a distance) between the user query and the indexed embeddings in the vector database is computed. The vector database retrieves the k most similar chunks (where k is an integer, typically 5) that later get added as the context to the prompt. This is the **retrieval** process. On the JVM, this lookup is handled by the same `EmbeddingStore` used during indexing — whether that's the in-memory store above or Qdrant, which uses HNSW internally to make the nearest-neighbor lookup efficient even over millions of vectors.

During the **generation** step, the original query and the most similar chunks are combined into a prompt, which is send to the LLM. The model's response can be restricted to the retrieved chunks or be drawn from the model itself.

![Retrieval and generation process](https://ucarecdn.com/85a7feb9-fb41-4828-8c6c-e5563e4f98d6/)

## The advanced RAG

There are a few nuances with the naive approach. Real-world queries might be convoluted (meaning that a single query contains multiple distinct questions), which the naive implementation won't handle well because the broader the query, the harder it is to match it to the relevant chunks of text.

For example, the following query

*What are the environmental and economic impacts of electric vehicles compared to traditional cars, considering both manufacturing and long-term usage, and how does this vary across different countries?*

would likely fail the naive implementation since it contains 5 different aspects (environment, economy, manufacturing, usage, geographical variations), and might require synthesis from multiple sources.

Another issue is with the retrieved chunks and their relevance, since the naive implementation can't prioritize certain details of the query. The following query

*What are the side effects of ibuprofen?*

might match many different side effects and mix the rare and the uncommon ones into a single response, skip the severity and frequency information, or include irrelevant or contradictory information.

These aspects call for the **advanced RAG**, which improves the retrieval component by combining pre-retrieval and post-retrieval optimizations (considered in the next sections).

![Advanced RAG illustration](https://ucarecdn.com/d75360f5-1fe1-4477-9b4a-f1d456719a29/)

## Pre-retrieval optimizations

Pre-retrieval optimizations can be broadly categorized into two types. **Query-side** optimizations transform the original query into one or more improved search queries that are more likely to retrieve relevant information from the vector DB. **Index-side** optimizations focus on how documents are processed, chunked, embedded, and organized in the vector DB to ensure more efficient retrieval of relevant information (which won't be covered in this topic).

One of the approaches of query-side optimizations is **query rewriting**. This is done to convert the query into an expected format, and can be done via prompts (zero shot or few shot, where former asks to provide reformulations, and the latter provides examples of possible queries and the desired formats), or a separate model fine-tuned for the task.

The query from the previous section can be decomposed into multiple questions ('What are the environmental impacts of manufacturing EVs?', 'What are the environmental impacts of manufacturing traditional cars?', etc.) — this strategy is known as **multi-query** RAG, where the requests to the DB can be send in parallel.

**Multi-hop RAG** is used when a query can be decomposed into a series of reasoning steps where an answer leads to the next query. The following query has to be solved in 2 hops (first, identify where the creator of Kotlin studied (Andrey Breslav), and second, find which university that is):

*Which university did the creator of the Kotlin programming language attend?*

## Post-retrieval optimizations

Post-retrieval optimizations can also be broadly categorized into chunk reranking and context processing.

**Chunk re-ranking** re-scores initially retrieved chunks based on their true relevance to the query. While initial retrieval typically uses vector similarity, reranking uses methods like cross-encoders or hybrid scoring approaches to better assess semantic relevance (which will be covered in the upcoming topics). Re-ranking is available for all major frameworks for RAGs — LangChain4j exposes a `ScoringModel` abstraction for exactly this purpose, and we'll use it in the upcoming topics when we build a full RAG pipeline in Kotlin.

The second optimization, **context processing**, in part stems from limited context window — when all relevant chunks are fed into the LLM directly, it might dilute the focus and include less relevant content. This is typically solved with selecting the most important information and shortening the context (for example, with summarization).

## Conclusion

In this topic, we covered the fundamentals of RAG: it addresses key limitations of traditional LLMs — hallucination, lack of source attribution, and outdated knowledge — by combining search with language models. The simplest form, naive RAG, consists of indexing, retrieval, and generation; advanced RAG works the same way but adds pre- and post-retrieval optimizations, such as query rewriting and context processing.

In the upcoming topics, we'll implement each of these stages in Kotlin, using LangChain4j for the ingestion and retrieval pipeline and Qdrant as the vector store.

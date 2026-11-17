# Introduction to vector search with Kotlin

In this topic, you'll learn about different types of vector search methods, how to convert content into searchable vectors, organize them for quick retrieval, and rank results effectively. You will also learn how to combine searching in many ways and pick and choose the best result.

## Overview

Embedding models transform text into vectors. These vectors capture the semantic meaning of the text so that similar documents are located near each other in a multi-dimensional space. Each text unit (for example, word, sentence, or a document) is mapped to a point in a multi-dimensional space. When you perform a search, your query is also converted to a vector by the same model that was used to encode the original documents. The system then finds vectors in the space that are nearest to your query vector. On the JVM, this conversion step is usually represented by an EmbeddingModel interface (as in LangChain4j or Koog), no matter which underlying provider actually computes the vector.

Basically, we map some word embeddings in a vector space as dots. You can see an example on the image below:

![Pasted illustration](https://ucarecdn.com/37ecab96-b373-4ca6-a43c-938dbdbaa686/)

## Types of vector search

Vector search comes in three main types: dense, sparse, and hybrid.

- **Dense vector search** uses embeddings obtained by encoding language models (such as BERT or modern text-embedding models from OpenAI, Cohere, or Google) to capture the meaning of text. Think of it like understanding that "car" and "automobile" mean the same thing, even though they're different words. This method is good at finding relevant matches even when exact keywords don't match.

- **Sparse vector search** works on a different kind of vectors that encode the words present in the text without focusing on their underlying meaning as much as the dense embeddings. It's like creating an inventory of words in a document, noting how often each appears and how unique they are. This approach is suitable for scenarios where exact keyword matches are important, such as technical documentation or legal documents.

- **Hybrid vector search** combines both approaches. Dense embeddings capture the context of the query and sparse embeddings put more emphasis on the exact keywords. This combination often provides more accurate results than using either method alone.

## Preparing the dataset

Before creating the vectors from the documents you want to search over, the typical NLP text preprocessing techniques are applied:

- Common words such as "the," "is," and "at" are removed because they do not add significant meaning.

- Text is lowercased and punctuation is handled to ensure consistency.

Consider the following example:

- Initial text: "The quick brown fox jumps over the lazy dog."

- After cleaning: "quick brown fox jumps lazy dog"

This preprocessing makes the subsequent embedding generation more effective by focusing on the meaningful components of the sentence.

## Naïve chunking with overlap

The next step involves creating more manageable text fragments. For long documents, splitting text into smaller pieces, or chunks, is important for maintaining context and ensuring better embedding quality. Additionally, all embedding models have a context limit - they can't encode a text that contains more than some fixed number of tokens, and will truncate the information that is present in the text beyond this limit. This approach helps in processing text more effectively.

For example, consider the following text: "Machine learning models require a lot of data to train effectively. Preprocessing the data is crucial for achieving high accuracy."

When chunked with a size of 10 words and an overlap of 3 words, it would look like this:

- Chunk 1: "Machine learning models require a lot of data to train"

- Chunk 2: "data to train effectively. Preprocessing the data is crucial"

- Chunk 3: "the data is crucial for achieving high accuracy."

The overlapping words, such as "data to train," help maintain continuity and context between the chunks, ensuring that the meaning is preserved throughout the process. This method is simple yet effective for handling long texts while retaining information. We will implement this kind of overlapping split in Kotlin in the next topic.

## Embedding generation

After preprocessing, each text chunk is transformed into a dense vector representation. This process is often done using models like BERT, or — increasingly — small models that run directly inside your JVM process without calling out to an external API. LangChain4j ships one such model (`AllMiniLmL6V2EmbeddingModel`) that you can use in Kotlin with no API key and no network call:



```kotlin
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel

val model = AllMiniLmL6V2EmbeddingModel()
val embedding = model.embed("quick brown fox jumps lazy dog").content()
// embedding is a FloatArray, e.g. [-0.2398944, 0.24637009, -0.5362498, ...]
```



In case you want to implement hybrid search, a similar conversion is done on the same chunks but with a different model focused on sparse representations (such as TF-IDF, although newer sparse-embedding models are now commonly used in production systems).

The embedding vectors are saved in the database for future indexing. Indexing is needed to perform approximate search in the vector space — without it, every query would need to be compared to every stored vector individually, which is too slow for real applications.

## Query processing

When a search query is submitted, the system processes it by converting the query into a vector and then comparing it to the vectors in the index using the same embedding model used at storage time.

Finding the nearest neighbors is typically done with approximate methods that avoid computing the distance between the query vector and every stored vector. One of the most popular approaches is HNSW (Hierarchical Navigable Small World index). Common similarity metrics include cosine similarity and Euclidean distance. In the upcoming topics on Qdrant, you'll see how this indexing and nearest-neighbor retrieval is handled in practice on the JVM — Qdrant uses HNSW internally and exposes it through its Java client.

The top-k most similar vectors are then returned as the search result, where k is the number of results you want to retrieve.

![Pasted illustration](https://ucarecdn.com/80b676fc-69cd-4710-9017-1b98353fe1bd/)

## Conclusion

In summary, vector search is an evolution from traditional keyword matching. The pipeline consists of four steps — data preprocessing, embedding generation, indexing, and query processing — each of which has a natural JVM home: LangChain4j's `EmbeddingModel` for generating vectors, and Qdrant (covered in the next topics) for storing, indexing, and querying them. This approach not only improves search accuracy in scenarios where meaning matters but also opens doors to advanced applications like recommendation systems and conversational AI.

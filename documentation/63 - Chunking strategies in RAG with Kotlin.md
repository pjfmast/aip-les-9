# Chunking strategies in RAG with Kotlin

One of the most important applications of large language models (LLMs) is retrieval-augmented generation (RAG), which enhances model outputs by integrating external knowledge. At its core, **chunking** enables efficient retrieval by breaking text into meaningful segments, preserving context, and improving accuracy. In this topic, we will examine how chunking works within RAG, exploring different chunking techniques and their impact on retrieval efficiency and response quality.

## The role of chunking in RAG

As we know, RAG helps large language models generate more accurate and relevant responses by retrieving information from external sources. It works in three phases:

- Retrieval: When the system receives a query, RAG searches a knowledge base (e.g., documents, databases) to find relevant information.

- Augmentation: The system adds the retrieved data as context for the model.

- Generation: The model uses both the query and the additional context to create a more accurate response.

Since RAG depends on retrieving relevant information, the way you store and access data plays a key role. Chunking organizes data into structured segments, improving both retrieval speed and response accuracy. As we can see in the picture, before retrieval, chunking splits large texts from the knowledge base into smaller, meaningful **chunks**, making it easier and faster to find relevant information:

![Pasted illustration](https://ucarecdn.com/a2f87370-6ec2-42aa-865d-8343965edbc2/)

Chunking also helps LLMs stay within their token limits by breaking large texts into smaller, meaningful sections. Since LLMs can only process a fixed number of tokens at a time, retrieving entire documents may exceed this limit, causing important information to be cut off. By pre-chunking the data, you ensure each retrieved section is small enough to fit within the token constraints while still containing meaningful and complete information.

Without chunking, the model might miss important details or retrieve incomplete or irrelevant information, reducing accuracy. Well-structured chunks also preserve context and logical connections, making large texts easier to manage. This leads to faster and more precise retrieval, improving the overall performance of RAG systems.

## Chunking techniques

There are various text chunking techniques, each suited to different text structures and use cases. In this section, we will explore five common chunking techniques and implement them using LangChain4j, a Kotlin-friendly JVM toolkit, together with `java.text.BreakIterator`, which is part of the standard JDK. `BreakIterator` handles basic word and sentence boundary detection out of the box, with nothing to download. For document-structured and semantic chunking, we'll additionally bring in LangChain4j's splitter classes and a small in-process embedding model.

## Fixed-size chunking

**Fixed-size chunking** breaks text into equal parts based on a set character or token limit. It ensures predictable chunk sizes and is useful when working with strict processing constraints, such as API token limits. It can be implemented as follows using `BreakIterator`.

First, let's write a small helper that tokenizes text into words. `BreakIterator` ships with the JDK, so there's nothing to install or download:



```kotlin
import java.text.BreakIterator
import java.util.Locale

fun tokenizeWords(text: String): List<String> {
    val iterator = BreakIterator.getWordInstance(Locale.getDefault())
    iterator.setText(text)
    val words = mutableListOf<String>()
    var start = iterator.first()
    var end = iterator.next()

    while (end != BreakIterator.DONE) {
        val token = text.substring(start, end)
        if (token.isNotBlank()) words.add(token) // skip whitespace-only tokens
        start = end
        end = iterator.next()
    }
    return words
}
```



Next, we define a function `fixedSizeChunking` that splits the input text into chunks of a specified size (`chunkSize`), with an optional overlap (overlap) to retain context between chunks. The text is tokenized using `tokenizeWords()`, and the chunks are created by slicing the tokenized words:



```kotlin
fun fixedSizeChunking(text: String, chunkSize: Int, overlap: Int): List<String> {
    val words = tokenizeWords(text) // Tokenize the text into words
    val chunks = mutableListOf<String>()
    var i = 0

    // Loop to create chunks of specified size with overlap
    while (i < words.size) {
        val chunk = words.subList(i, minOf(i + chunkSize, words.size)) // Slice words for chunk
        chunks.add(chunk.joinToString(" ")) // Join words into a chunk
        i += chunkSize - overlap // Update index with overlap

    }
    return chunks
}
```



We can call `fixedSizeChunking()` with the following parameters:



```kotlin
val text = "There was a cat. The cat sat. The cat sat on a mat."
val chunkSize = 5
val overlap = 1 // overlap size definition

val chunks = fixedSizeChunking(text, chunkSize, overlap)
println(chunks)
```



The result of fixed-size chunking will be:

![Pasted illustration](https://ucarecdn.com/3ca1655f-f99c-4174-8a81-39bd8e0b1855/)

This shows how the text is split into chunks of 5 tokens each, with an overlap of 1 token between consecutive chunks. However, as observed, fixed-size chunking may split sentences unnaturally and break phrases in the middle, leading to a loss of context.

## Recursive character chunking

Recursive character chunking splits text at natural delimiters as paragraphs, newlines, and spaces until the chunks fit within a size limit, keeping the text readable and meaningful. It is ideal for unstructured text and mixed-length datasets, preserving the flow of sentences and paragraphs. It can be implemented using LangChain4j as follows.

First, add the LangChain4j core dependency to your project:



```kotlin
dependencies {
    implementation("dev.langchain4j:langchain4j:[LATEST_VERSION]")
}
```



> **Note:**

Replace `[LATEST_VERSION]` with the current version available on Maven Central.

We can load a sample text:



```kotlin
val text = """There was a cat.
The cat sat.
The cat sat on a mat.""".trimIndent()
```



Here, we will use `DocumentSplitters.recursive()` to split the text. This splitter first attempts to split the text at the most meaningful delimiter (paragraphs). The size parameters are specified in characters, not tokens. If the chunks exceed the size limit, it uses smaller delimiters such as newlines and spaces:



```kotlin
import dev.langchain4j.data.document.Document
import dev.langchain4j.data.document.splitter.DocumentSplitters

val textSplitter = DocumentSplitters.recursive(25, 0) // max 25 chars, no overlap
```



Then, we can call the splitter with our sample text:



```kotlin
val documents = textSplitter.split(Document.from(text))
documents.forEach { println(it.text()) }
```



The result of running the code will be:



```kotlin
There was a cat.
The cat sat.
The cat sat on a mat.
```



Although this method does not guarantee equal chunk sizes, and if the text lacks clear breaks, it may create chunks that cut off in the middle of sentences. This can affect readability and make the text harder to understand.

## Sentence chunking

Sentence chunking breaks text into separate sentences, each containing a complete idea. This helps AI understand and process the text better, making it useful for tasks such as answering questions, summarizing, and translating. It can be implemented as follows using `BreakIterator`'s sentence-boundary mode.

We define a function `sentenceChunking` that splits the input text into individual sentences. The text is processed using `BreakIterator.getSentenceInstance()`, which detects sentence boundaries based on punctuation. Each sentence is preserved as a complete thought, and the function returns a list of these sentences:



```kotlin
fun sentenceChunking(text: String): List<String> {
    val iterator = BreakIterator.getSentenceInstance(Locale.getDefault())
    iterator.setText(text)
    val sentences = mutableListOf<String>()
    var start = iterator.first()
    var end = iterator.next()

    while (end != BreakIterator.DONE) {
        sentences.add(text.substring(start, end).trim())
        start = end
        end = iterator.next()
    }
    return sentences
}
```



Then, we can define the example text for chunking:



```kotlin
val text = "There was a cat. The cat sat. The cat sat on a mat."
```



Lastly, we can call the chunking function with our sample text and print the resulting chunks:



```kotlin
val chunks = sentenceChunking(text)
println(chunks)
```



The result of running the code will be:

![Pasted illustration](https://ucarecdn.com/a47b7e52-4b77-47f5-a1af-4fad4dc3a128/)

This demonstrates how the text is split into 3 individual sentences, preserving the structure of each statement. However, sentence chunking struggles with sub-sections or paragraphs, leading to inconsistent chunk sizes due to varying sentence lengths. `BreakIterator` handles most cases well but may incorrectly split on abbreviations such as 'Dr.' or 'e.g.' — a limitation common to rule-based sentence tokenizers in general.

## Document-structured chunking

The **document-structured chunking** technique splits text by its layout, such as paragraphs and headings, to keep things organized. It is useful for structured documents such as articles and reports, helping maintain topic flow and making it easier to process. This technique can be implemented as follows.

We split the document at double newlines and drop any empty sections:



```kotlin
fun documentStructureChunking(text: String): List<String> =
    text.split("\n\n")
        .map { it.trim() }
        .filter { it.isNotEmpty() }
```



We can reuse the `sentenceChunking()` function from the previous section to count how many sentences end up in each resulting chunk. When the code is executed, the output displays the individual chunks of the document, each representing a section, along with the number of sentences contained in each chunk.

Next, we can define the sample text for chunking:



```kotlin
val text = """Title: Cat
Intro: There was a cat.
Body: The cat sat.
Conclusion: The cat sat on a mat."""
```



Lastly, call `documentStructureChunking()` and print the result:



```kotlin
val chunks = documentStructureChunking(text)
println("Document Chunks: $chunks")

chunks.forEachIndexed { i, chunk ->
    val sentences = sentenceChunking(chunk)
    println("Chunk ${i + 1} (${sentences.size} sentences): $sentences")
}
```



The result of running the code will be:



```kotlin
Document Chunks: [Title: Cat, Intro: There was a cat., Body: The cat sat., Conclusion: The cat sat on a mat.]
Chunk 1 (1 sentences): [Title: Cat]
Chunk 2 (1 sentences): [Intro: There was a cat.]
Chunk 3 (1 sentences): [Body: The cat sat.]
Chunk 4 (1 sentences): [Conclusion: The cat sat on a mat.]
```



As observed, this technique requires well-structured text with clear paragraph markers. If the text lacks consistent formatting, it may not split properly, resulting in chunks that are either too large or too small.

## Semantic chunking

**Semantic chunking** is a method that breaks text into meaningful sections based on the content and relationships between words. It improves understanding and accuracy in tasks such as searching for information. Instead of splitting text by size or structure, it groups sentences based on their meaning using embeddings clustering. When combined with syntactic parsing, which focuses on sentence structure, it helps ensure the text is both logically connected and grammatically correct, making it more useful for chatbots and search engines. It can be implemented with LangChain4j as follows.

Getting embeddings for this kind of technique used to mean wiring up an external API (such as the LiteLLM/OpenAI setup from the original version of this topic, complete with .env files and config parsers). That's no longer the easiest path: LangChain4j ships a small embedding model, all-MiniLM-L6-v2, that runs directly inside your JVM process — no API key, no network call, and no extra configuration:



```kotlin
dependencies {
    implementation("dev.langchain4j:langchain4j-embeddings-all-minilm-l6-v2:[LATEST_VERSION]")
}
```





```kotlin
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel
import dev.langchain4j.store.embedding.CosineSimilarity

val embeddingModel = AllMiniLmL6V2EmbeddingModel()
```



Also, we can define the example text for chunking:



```kotlin
val text = "There was a cat. The cat sat. The cat sat on a mat."
```



LangChain4j doesn't ship a ready-made "semantic chunker" class, but the idea is simple enough to write directly: split the text into sentences, embed each sentence, and measure how semantically different each sentence is from the one before it (using 1 minus the cosine similarity between their embeddings). Wherever that difference exceeds a threshold, we start a new chunk:



```kotlin
fun semanticChunking(
    text: String,
    breakpointPercentile: Double = 75.0,
    minChunkSentences: Int = 1
): List<String> {
    val sentences = sentenceChunking(text)

    if (sentences.size <= 1) return sentences
    val embeddings = sentences.map { embeddingModel.embed(it).content() }

    // Semantic "distance" between each pair of consecutive sentences
    val distances = (0 until embeddings.size - 1).map { i ->
        1.0 - CosineSimilarity.between(embeddings[i], embeddings[i + 1])
    }

    // Percentile-based breakpoint: distances at or above this value start a new chunk
    val sortedDistances = distances.sorted()
    val thresholdIndex = ((breakpointPercentile / 100.0) * (sortedDistances.size - 1)).toInt()
    val threshold = sortedDistances[thresholdIndex]
    val chunks = mutableListOf<String>()
    var current = mutableListOf(sentences.first())

    for (i in distances.indices) {
        if (distances[i] >= threshold && current.size >= minChunkSentences) {
            chunks.add(current.joinToString(" "))
            current = mutableListOf()
        }
        current.add(sentences[i + 1])
    }
    chunks.add(current.joinToString(" "))
    return chunks
}
```



A higher percentile means fewer, larger chunks (less strict splitting), while a lower one means more, smaller chunks (stricter splitting). The percentile-based rule shown above is the most common choice in practice; the same overall approach also works with a standard-deviation-, interquartile-range-, or gradient-based threshold instead — only the formula used to turn distances into threshold would change.

In our code, we can call the function on the sample text and print the results:



```kotlin
val chunks = semanticChunking(text)
println("Number of chunks: ${chunks.size}")

chunks.forEachIndexed { i, chunk -> println("Chunk ${i + 1}: $chunk") }
```



The expected output of the code will be:



```kotlin
Number of chunks: 2

Chunk 1: There was a cat.
Chunk 2: The cat sat. The cat sat on a mat.
```



Although this chunking technique can be time-consuming, it may also overlook important formatting cues such as headings, paragraphs, and lists. These elements are essential in research papers, contracts, and technical documentation.

## Chunk enrichment

After chunking, each chunk undergoes an **enrichment** process to improve search accuracy and retrieval efficiency. This process consists of two key stages: text cleaning and metadata augmentation.

**Text cleaning** ensures that the content is structured, consistent, and free of unnecessary noise. This step involves:

- Converting all text to lowercase for consistency.

- Eliminating common words (e.g., "the," "and," "is") that do not add significant meaning to reduce vector dimensionality.

- Fixing misspellings to improve text matching and prevent search errors.

- Expanding contractions (e.g., changing "can't" to "cannot") and abbreviations to ensure consistency and clarity.

- Removing unnecessary symbols or Unicode characters to minimize noise.

For example, given the sample text: "There was a cat. The cat sat. The cat sat on a mat." The text cleaning process would proceed as follows:

![Pasted illustration](https://ucarecdn.com/6c5f1884-95df-40ed-aaf8-92ed19746f98/)

After text cleaning, it could transform to "cat cat sat cat sat mat".

Once the text is cleaned, **metadata** is added to provide more context and improve search efficiency. This step includes:

- Creating short summaries and titles for quick reference.

- Identifying important terms and named entities for precise filtering.

- Generating rephrased text to capture different ways users might search for the same information.

- Identifying potential questions the chunk can answer.

- Storing source and language details for filtering and citation.

For our example, the process goes as follows:

![Pasted illustration](https://ucarecdn.com/e5b17bfd-2bb3-4e7b-a35c-969a1301f1b4/)

These enhancements improve search results by making it easier to find relevant information.

## The impact of chunking quality

Chunk quality and size significantly impact retrieval accuracy and efficiency in RAG systems. Well-structured chunks maintain semantic context, ensuring relevant information is retrieved. However, imbalanced chunking can lead to biases: small chunks (100–300 tokens) improve keyword matching but may fragment context, while large chunks (500+ tokens) enhance coherence but risk overlooking critical details and increasing computational costs. To address these issues, effective chunking applies structured techniques to divide text and enriches cleaned chunks with metadata for better semantic relevance.

## Conclusion

Chunking is an essential process in retrieval-augmented generation (RAG) systems by structuring text for efficient retrieval while preserving semantic context. Chunking techniques such as fixed-size, recursive, and others can be implemented using the JDK's built-in `BreakIterator` for tokenization, LangChain4j's document splitters, and an in-process embedding model when semantic similarity is needed. When combined with enrichment strategies, effective chunking enhances retrieval accuracy, reduces biases, and improves overall system performance.

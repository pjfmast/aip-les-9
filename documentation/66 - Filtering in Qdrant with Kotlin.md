# Filtering in Qdrant with Kotlin

In this topic, we will explore filtering in Qdrant — one of its most powerful features for querying vector data based on payload attributes. We will look at the main filter clauses (`must`, `must_not`, `should`), condition helpers from `ConditionFactory`, and how to filter by text content, numeric ranges, and timestamps.

## Setup

First, we need to add the Qdrant client to our project and run the Qdrant Docker image. Add the official Java client (it works as-is from Kotlin) to your Gradle build file:



```shell
dependencies {
    implementation("io.qdrant:client:1.18.3")
}
```



To run the Docker image, use the following command (note that we expose both the REST port 6333 and the gRPC port 6334, since the Java/Kotlin client talks to Qdrant over gRPC):



```shell
docker run -p 6333:6333 -p 6334:6334 -v $(pwd)/qdrant_storage:/qdrant/storage:z qdrant/qdrant
```



Let's start by importing the required classes and instantiating the Qdrant client.



```kotlin
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient
import io.qdrant.client.grpc.Collections.Distance
import io.qdrant.client.grpc.Collections.VectorParams

const val COLLECTION_NAME = "filter_showcase"
const val QDRANT_HOST = "localhost"
const val QDRANT_GRPC_PORT = 6334
const val VECTOR_DIMENSION = 1536

val client = QdrantClient(
    QdrantGrpcClient.newBuilder(QDRANT_HOST, QDRANT_GRPC_PORT, false).build()
)
```



Next, we will create the collection with vector configuration:



```kotlin
if (!client.collectionExistsAsync(COLLECTION_NAME).get()) {
    client.createCollectionAsync(
        COLLECTION_NAME,
        VectorParams.newBuilder().setSize(VECTOR_DIMENSION).setDistance(Distance.Cosine).build()
    ).get()
}
```



Now, we can upload the data into Qdrant. We will work with a synthetic dataset to showcase some of the main filters. A single row of the dataset has the following format:



```no-highlight
{
  "id": 1,
  "datetime": "2021-05-14T08:23:45Z",
  "text": "Exploring the advancements in artificial intelligence and machine learning.",
  "category": "Technology",
  "tags": [
    "AI",
    "Machine Learning",
    "Innovation"
  ],
  "value": 75.6,
  "is_active": true,
  "embedding": [] # an OpenAI embedding with 1536 elements
}
```



## The clauses

The first clause filter we will consider is a `must` clause. This filter behaves like an AND condition: it retrieves points that match all provided criteria. Here, it selects only the points where `category` is "Technology" and `is_active` is `true`:



```kotlin
import io.qdrant.client.ConditionFactory.match
import io.qdrant.client.ConditionFactory.matchKeyword
import io.qdrant.client.grpc.Points.Filter
import io.qdrant.client.grpc.Points.ScrollPoints

val mustFilter = Filter.newBuilder()
    .addAllMust(
        listOf(
            matchKeyword("category", "Technology"),
            match("is_active", true)
        )
    )
    .build()

val resultMust = client.scrollAsync(
    ScrollPoints.newBuilder()
        .setCollectionName(COLLECTION_NAME)
        .setFilter(mustFilter)
        .build()
).get()

println(resultMust.resultList.map { it.payloadMap["id"] })
// Output: [20, 1, 17]
```



Another clause, `must_not`, works in a similar way (but negates the specified condition). Here, we use `client.scrollAsync()` — a method of the client that lets you list the items that match a given filter without looking for similar embeddings.

The `should` clause operates like an OR condition, returning points that match at least one of the given criteria. This gives you more flexibility by matching points that satisfy either one of the filters. In this case, the filter selects points with `category` "Technology" or `is_active` set to `true`.



```kotlin
val shouldFilter = Filter.newBuilder()
    .addAllShould(
        listOf(
            matchKeyword("category", "Technology"),
            match("is_active", true)
        )
    )
    .build()

val resultShould = client.scrollAsync(
    ScrollPoints.newBuilder()
        .setCollectionName(COLLECTION_NAME)
        .setFilter(shouldFilter)
        .build()
).get()

println(resultShould.resultList.map { it.payloadMap["id"] })
// Output: [10, 14, 15, 20, 4, 11, 13, 3, 6, 7]
```



## Meta: a closer look at the main building blocks

In the previous section, we used `Filter.newBuilder()`, and the `matchKeyword()` / `match()` helper functions, so let's see these and a few other helpers in more detail.

The primary purpose of the `Filter` message is to limit searches to vectors that meet specific criteria based on their payload data and combine multiple conditions to create complex filtering logic for searches.

In general, a `Filter` can include multiple clauses (such as `must` and `must_not` in that case) and uses the following shape:



```kotlin
val combinedFilter = Filter.newBuilder()
    .addMust(/* a Condition, e.g. matchKeyword(...) */)
    .addMustNot(/* another Condition */)
    .build()
```



Each `Condition` specifies criteria on a payload field for filtered searches where results must match specific criteria in addition to vector similarity. The `io.qdrant.client.ConditionFactory` class provides the static helper functions you use to build them; under the hood, every condition still has a key (the payload field name) and a match type, but in Kotlin you call the right factory function instead of constructing a class yourself:

- `matchKeyword(key, value)` performs exact matching on string field values (useful for filtering by discrete categories, tags, status values, or any other field where you need an exact match rather than a range or partial match).

- `match(key, value)` does the same for booleans, integers, and other scalar types.

Besides these, there are different helpers to deal with different search types, such as `matchText()` for full-text search, range() for filtering on numeric values with `>`, `<`, `≥`, `≤`, and `datetimeRange()`, which allows you to work with dates. We will consider those in the next section.

## The text match

Let's take a look at `matchText()`, a helper to filter and retrieve documents based on textual content within specified fields (here, we still don't search for similar vectors to some embedding, but it is also possible to use `queryAsync()` to just search with the filters without the embedding similarity):



```kotlin
import io.qdrant.client.ConditionFactory.matchText
import io.qdrant.client.grpc.Points.QueryPoints

val matchTextFilter = Filter.newBuilder()
    .addMust(matchText("text", "learning"))
    .build()

val searchResult = client.queryAsync(
    QueryPoints.newBuilder()
        .setCollectionName(COLLECTION_NAME)
        .setFilter(matchTextFilter)
        .setLimit(5)
        .build()
).get().pointsList

println(searchResult)
```



## Working with ranges

Similarly, we can query either regular ranges or time ranges. For this topic, the dataset we are working with has the `datetime` field in the RFC 3339 format (the only format Qdrant currently supports), which allows you to perform time filtering. If you wish to enable time searches in your application, you should convert the timestamps to RFC 3339.

We'll use `range()` to find all points that have a value field between 80 and 90 (including the boundaries since we use greater than or equal to and less than or equal to):



```kotlin
import io.qdrant.client.ConditionFactory.range
import io.qdrant.client.grpc.Points.Range

val selectByValue = Filter.newBuilder()
    .addMust(range("value", Range.newBuilder().setGte(80.0).setLte(90.0).build()))
    .build()

val rangeResult = client.queryAsync(
    QueryPoints.newBuilder()
        .setCollectionName(COLLECTION_NAME)
        .setFilter(selectByValue)
        .setLimit(5)
        .build()
).get().pointsList

println(rangeResult)
```



Suppose we want to find the points after November 11th, 2021



```kotlin
import io.qdrant.client.ConditionFactory.datetimeRange
import io.qdrant.client.grpc.Points.DatetimeRange
import com.google.protobuf.Timestamp
import java.time.Instant

val gt = Instant.parse("2021-11-11T00:00:00Z").epochSecond

val timeBasedFilter = Filter.newBuilder()
    .addMust(
        datetimeRange(
            "datetime",
            DatetimeRange.newBuilder().setGt(Timestamp.newBuilder().setSeconds(gt).build()).build()
        )
    )
    .build()

val timeResult = client.queryAsync(
    QueryPoints.newBuilder()
        .setCollectionName(COLLECTION_NAME)
        .setFilter(timeBasedFilter)
        .build()
).get().pointsList

println(timeResult)
```



## Conclusion

As a result, you are now familiar with the following aspects:

- Qdrant supports various filtering strategies to query vector data based on attributes alongside vector similarity searches.

- `Filter` combines multiple conditions with clauses like `must` (AND), `must_not` (negation), and `should` (OR) to create filtering logic.

- Each `Condition` specifies criteria on a payload field, built with helper functions such as `matchKeyword()` or `match()` from `ConditionFactory` rather than constructed by hand.

- `matchKeyword()` / `match()` perform exact matching on field values, useful for filtering by categories. `matchText()` filters based on textual content within specified fields, supporting full-text search capabilities.

- `range()` filters allow querying numeric values using comparison operators (greater than, less than, etc.) to find values within specific boundaries. `datetimeRange()` supports time-based filtering using RFC 3339 format timestamps, enabling queries with specific time constraints.

- The client's `scrollAsync()` lists items matching a given filter without considering vector similarity, while `queryAsync()` can be used for filtered searches, optionally combined with vector similarity when a query embedding is provided.

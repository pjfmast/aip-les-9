# Qdrant fundamentals

In this topic, we will explore Qdrant — a production-ready vector similarity search engine for efficient storage, search, and management of high-dimensional vectors. We will start by setting up and running Qdrant locally using Docker and initializing the official Java client from Kotlin. Then we will introduce key concepts such as collections and points, learn how to insert and query vectors with payloads, and look at the essential operations for managing data in a Qdrant collection.

## What is Qdrant?

**Qdrant** is a production-ready vector search engine that stores and manages high-dimensional vectors along with additional metadata (payloads) for efficient, context-aware similarity searches. It performs vector search by finding similar objects based on embedding similarity, using pre-trained models (such as [OpenAI's](https://platform.openai.com/docs/guides/embeddings) or [Cohere](https://docs.cohere.com/v2/docs/embeddings) embeddings). Qdrant uses a graph-based approach to quickly find the most relevant results without checking every single object in the database.

There are three main ways to set up Qdrant:

- Locally – Run Qdrant on a local machine using Docker.

- Using an official client library – Qdrant ships official clients for Python, JavaScript/TypeScript, Go, Rust, .NET, and Java (the Java client works directly from Kotlin since Kotlin compiles to JVM bytecode and works with Java libraries without any wrapper).

- Using Qdrant Cloud – A solution offering managed Qdrant database clusters in the cloud.

In this topic, you'll set up Qdrant locally using Docker.

Before starting, make sure Docker is installed and running on your machine. To check, open a terminal and run:



```shell
docker --version
```



This should return the installed Docker version. If not, install Docker from [https://www.docker.com](https://www.docker.com).

Once Docker is set up, the next step is to download the latest Qdrant Docker image from Docker Hub. Run the following command:



```shell
docker pull qdrant/qdrant
```



Now that you downloaded the Qdrant image, you can start the Qdrant service by running the following command. Note that you expose both port 6333 (the REST API) and port 6334 (the gRPC API, which the Java/Kotlin client uses internally and is the one you actually need here):



```shell
# Run Qdrant with exposed 6333 (REST) and 6334 (gRPC) ports
docker run -p 6333:6333 -p 6334:6334 \
    # Store data in the 'qdrant_storage' folder in the current directory
    -v "$(pwd)/qdrant_storage:/qdrant/storage:z" \
    # Use the Qdrant image
    qdrant/qdrant
```



Once the service is running, you can access Qdrant using the following links:

- [http://localhost:6333](http://localhost:6333) for the REST API, where you can send HTTP requests to perform actions such as adding, updating, or searching data in Qdrant.

- [http://localhost:6333/dashboard](http://localhost:6333/dashboard) for the Web UI, where you can visually manage and monitor data through an interactive interface.

After ensuring the service is running and accessible via these links, you can initialize the Qdrant client to interact with Qdrant programmatically.

To do this, you'll use the official Java client for Qdrant, which works without any changes from Kotlin. Add it to your Gradle build file:



```kotlin
dependencies {
    implementation("io.qdrant:client:1.18.3")
}
```



Next, to initialize the client, you need to:



```kotlin
import io.qdrant.client.QdrantClient
import io.qdrant.client.QdrantGrpcClient

// Initialize the Qdrant client. The client talks to Qdrant's gRPC port (6334),
// not the REST port (6333) that the dashboard and curl examples use.
val client = QdrantClient(
    QdrantGrpcClient.newBuilder("localhost", 6334, false).build()
)
```



With the client initialized, you can now start interacting with Qdrant by working with collections.

**Collections**

A collection in Qdrant is a container for points, and it includes a vector configuration that defines the configuration for vectors within the collection. This configuration specifies the vector size, the number of dimensions for each vector, and the distance metric used to measure the similarity between vectors. Qdrant supports the main distance metrics including dot product, cosine similarity, euclidean distance, and manhattan distance.

You can customize these parameters to optimize how Qdrant stores, compares, and searches vectors.

In Qdrant, you can create collections using two different methods:

-

The first method involves using a client library, such as the Java client, which allows you to create and manage collections programmatically. For example, you can create a collection named points_collection with 3-dimensional vectors and the dot product as the distance metric:



```kotlin
import io.qdrant.client.grpc.Collections.Distance
import io.qdrant.client.grpc.Collections.VectorParams

client.createCollectionAsync(
    "points_collection",
    VectorParams.newBuilder().setSize(3).setDistance(Distance.Dot).build()
).get()
```



-

The second method uses an HTTP request to interact directly with the Qdrant server. The following example achieves the same result as the code above:



```kotlin
# Example of a PUT request to update collection
curl -X PUT "http://localhost:6333/collections/points_collection" \
  -H 'Content-Type: application/json' \
  --data-raw '{
    "vectors": {
      "size": 3,
      "distance": "Dot"
    }
  }'
```



- Additionally, you can quickly prototype requests in the Qdrant console at [http://localhost:6333/dashboard#/console](http://localhost:6333/dashboard#/console), which allows you to test HTTP interactions directly.

After creating the collection using either the client library or an HTTP request, you can verify that Qdrant successfully created the collection by accessing the Qdrant WebUI at [http://localhost:6333/dashboard#/collections](http://localhost:6333/dashboard#/collections). On this page, you can visualize your collections, view them in a graph, and even delete a collection if needed. You should see your newly created collection, `points_collection`, listed there.

Additionally, you can use HTTP requests to interact directly with the Qdrant API from the terminal. For example, to check if your collection exists, you can use the following command:



```shell
# Example of a GET request to check if the "points_collection" exists
curl -X GET "http://localhost:6333/collections/points_collection/exists"
```



This command will return a response indicating whether the collection exists (`"exists": true`) or not.

If you want to remove a collection and all its associated data, you can use the HTTP DELETE method with the following command:



```shell
# Example of a DELETE request to remove the "points_collection"
curl -X DELETE "http://localhost:6333/collections/points_collection"
```



However, you need to keep the `points_collection`. In the next section, you'll focus on working with this collection and explore how to work with points, vectors, and payloads in Qdrant.

## Points

In Qdrant, **points** from a collection are the main entities that the vector search engine operates on. Each point represents a single data record composed of an identifier, a vector used for similarity search, and an optional payload, which stores additional metadata or structured information related to the point.

The first characteristic, the **identifier**, plays a crucial role in uniquely identifying each point within a collection. Qdrant supports two types of identifiers for points:

- 64-bit unsigned integers. Examples: `"id": 1` , `"id": 123`.

- UUID string representations can appear in formats such as simple `936DA01F9ABD4d9d80C702AF85C822A8`, hyphenated `550e8400e29b41d4a716446655440000`, or as a URN `urn:uuid:F9168C5ECEB24faab6bf329BF39FA1E4`.

The second key component of a point is the vector, which is used for similarity search. Qdrant supports multiple types of vectors: dense, sparse, and multivectors. While dense and sparse vectors are commonly used in embedding models and information retrieval, multivectors represent matrices with a fixed width and variable height. Example: `"multi_vector": [[0.11, 0.22, 0.33], [0.44, 0.55, 0.66], [0.77, 0.88, 0.99]]`.

Qdrant also allows attaching more than one type of vector to a single point using Named Vectors, each with its own configuration.

The third component of point is the **payload** that allows storing additional information alongside vectors, using a flexible payload schema in JSON format. Payload can be in various data types including:

- 64-bit signed integers, for example, `"index": 42`.

- 64-bit floating point numbers, for example, `"length": 9.5`.

- Boolean values, for example, `"exists": true`.

- String values, for example, `"name": "Alice"`.

- Geographic coordinates, for example, `"location": {"lat": 40.7128, "lon": -74.0060}`.

- Timestamp in RFC 3339 format, for example, `"created_at": "2023-02-08T10:49:00Z"`.

- UUID, for example, `"uuid": "550e8400-e29b-41d4-a716-446655440000"`.

Qdrant offers various methods for loading data into a collection via code or HTTP requests, and we will use batch loading through the Java client to insert multiple points. We can define vectors and payloads for `points_collection` directly in the code and upsert them using the `upsertAsync()` method, as shown in the following example:



```kotlin
import io.qdrant.client.PointIdFactory.id
import io.qdrant.client.ValueFactory.value
import io.qdrant.client.VectorsFactory.vectors
import io.qdrant.client.grpc.Points.PointStruct

// Define the list of points to insert into the collection
val points = listOf(
    PointStruct.newBuilder()
        .setId(id(3)) // Unique ID for the first point
        .setVectors(vectors(0.1f, 0.1f, 0.9f)) // 3D vector
        .putAllPayload(mapOf("color" to value("blue"))) // Payload
        .build(),
    PointStruct.newBuilder()
        .setId(id(4))
        .setVectors(vectors(0.5f, 0.5f, 0.0f))
        .putAllPayload(mapOf("color" to value("yellow")))
        .build()
)

// Insert the points into the specified collection using upsertAsync()
client.upsertAsync("points_collection", points).get()
```



After loading the data, you can verify it by visiting [http://localhost:6333/dashboard#/collections/points_collection](http://localhost:6333/dashboard#/collections/points_collection). On this page, you can explore the contents of `points_collection`, including stored vectors, payloads, and point IDs.

Also, to verify or inspect specific points in your collection, you can use the following command to retrieve points with `"id": 1`, `"id": 2`, `"id": 3`, and `"id": 4` in `points_collection`:



```shell
# Example of a POST request to retrieve points from the "points_collection"
curl -X POST 'http://localhost:6333/collections/points_collection/points' \
     -H 'Content-Type: application/json' \
     -d '{
           "ids": [1, 2, 3, 4],
           "with_payload": true,
           "with_vector": true
         }'
```



This request will return their vector data and payloads:



```shell
{
  "result": [
    {
      "id": 1,
      "payload": {
        "color": "red"
      },
      "vector": [0.9, 0.1, 0.1]
    },
    {
      "id": 2,
      "payload": {
        "color": "green"
      },
      "vector": [0.1, 0.9, 0.1]
    },
    {
      "id": 3,
      "payload": {
        "color": "blue"
      },
      "vector": [0.1, 0.1, 0.9]
    },
    {
      "id": 4,
      "payload": {
        "color": "yellow"
      },
      "vector": [0.5, 0.5, 0.0]
    }
  ],
  "status": "ok",
  "time": 0.000274448
}
```



Now that the `points_collection` contains all the necessary data, we can use it to perform a query. We will search for vectors most similar to a given query vector, using Qdrant's vector similarity search feature.

## Query

To retrieve vectors similar to a given query vector, Qdrant provides the `queryAsync()` method in the Java client. This method allows us to search for the most similar vectors within a specified collection by comparing them with a given query vector:



```kotlin
import io.qdrant.client.QueryFactory.nearest
import io.qdrant.client.WithPayloadSelectorFactory.enable
import io.qdrant.client.grpc.Points.QueryPoints

val searchResult = client.queryAsync(
    QueryPoints.newBuilder()
        .setCollectionName("points_collection")  // Name of the collection to search within
        .setQuery(nearest(0.1f, 0.9f, 0.1f))     // The query vector
        .setWithPayload(enable(false))           // Whether to include the payload in the results
        .setLimit(3)                             // Maximum number of similar vectors to return
        .build()
).get().pointsList

println(searchResult)
```



Example output showing the top 3 most similar vectors:



```kotlin
[
    ScoredPoint(id=2, version=0, score=0.8299999, payload={}, vector=null),
    ScoredPoint(id=4, version=1, score=0.5, payload={}, vector=null),
    ScoredPoint(id=1, version=0, score=0.19, payload={}, vector=null)
]
```



As you can see, query results are entirely determined by the vectors stored in the collection and their similarity to the input query. Therefore, performing basic operations on these points will allow us to modify and refine the search outcomes.

## Useful commands

Once the points are loaded into the collection, you can perform various operations on them to modify the search results. Here are some useful HTTP commands for working with points in Qdrant (each of these also has a corresponding method on the Java/Kotlin client, such as `setPayloadAsync()` or `deletePayloadAsync()`, if you prefer to stay in code rather than shelling out to curl):

-

If you need to update both vectors and payloads for specific points, you can use the upsert operation. This allows you to modify the vector data and payloads for the point with `"id": 1`:



```shell
# Example of a POST request to upsert points into the "points_collection"
curl -X POST 'http://localhost:6333/collections/points_collection/points/upsert' \
     -H 'Content-Type: application/json' \
     -d '{
           "points": [
             {
               "id": 1,
               "vector": [0.1, 0.2, 0.3],
               "payload": {"color": "black"}
             }
           ]
         }'
```



-

If you need to remove specific points from the collection, we can use the following command to delete the point with `"id": 2`:



```shell
# Example of a POST request to delete points from the "points_collection"
curl -X POST 'http://localhost:6333/collections/points_collection/points/delete' \
     -H 'Content-Type: application/json' \
     -d '{
           "points": [2]
         }'
```



-

If you want to remove specific named vectors from a point without deleting the point itself, you can use the following command. This only applies to collections configured with named vectors — in this example, we delete the `image_vector` and `text_vector` vectors from the point with `"id": 3`:



```shell
curl -X POST 'http://localhost:6333/collections/points_collection/points/vectors/delete' \
     -H 'Content-Type: application/json' \
     -d '{
           "points": [3],
           "vectors": ["image_vector", "text_vector"]
         }'
```



-

If you want to clear all payloads for certain points, you can use the following command to delete the payload for the point with "id": 4:



```kotlin
# Example of a POST request to clear the payload of points in the "points_collection"
curl -X POST 'http://localhost:6333/collections/points_collection/points/payload/clear' \
     -H 'Content-Type: application/json' \
     -d '{
           "points": [4]
         }'
```



By performing basic operations on the points, you can run the query again to check and compare the updated results.

## Conclusion

As a result, you are now familiar with the following:

- Qdrant is a vector similarity search engine for managing high-dimensional vectors and associated metadata. It can be used locally using Docker, accessed via an official client library (Java/Kotlin among others), or used as a managed cloud service;

- Collections in Qdrant serve as containers for points and include vector configurations that define size and distance metrics. Each point consists of a unique identifier, vectors, and optional JSON payloads;

- Qdrant allows searching for vectors similar to a query vector. Search results include similarity scores and can be configured to return vectors and payloads as needed.

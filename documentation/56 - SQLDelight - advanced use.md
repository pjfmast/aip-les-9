# SQLDelight: advanced use

In previous topics, we have seen how to query databases and learned the basics of SQLDelight. However, when working with databases, we must consider the specifics of synchronous and asynchronous approaches. SQLDelight allows us to work asynchronously and in a non-blocking way. Additionally, it will provide us with the ability to receive updates whenever our tables change through a query.

## Async mode

Working with databases, we can use synchronous and asynchronous mechanisms. Synchronous mechanisms execute operations in a blocking and sequential manner, which can result in the following problems in our applications:

- Reduced responsiveness: Synchronous database access blocks the execution of other tasks until the operation is completed, resulting in reduced responsiveness of the application. This can lead to a slower user experience, especially when dealing with complex or time-consuming queries.

- Limited concurrency: Synchronous access restricts concurrent database operations, as each operation must wait for the previous one to complete. This can lead to bottlenecks and decreased efficiency, particularly in scenarios with high concurrency requirements or heavy workloads.

- Scalability challenges: Synchronous access can pose challenges in scaling the application to handle increased user demand or larger datasets. As the number of users or data volume grows, the synchronous nature of database access can become a limiting factor, hindering the application's ability to scale effectively.

On the other hand, asynchronous mechanisms allow operations to be executed in a non-blocking and parallel manner, improving responsiveness and scalability of the application.There are several advantages to that approach:

- Improved responsiveness: Asynchronous database access allows the application to continue executing other tasks while waiting for the database operation to complete. This enhances responsiveness and ensures a smoother user experience, as the application remains interactive and doesn't get blocked by lengthy database operations.

- Enhanced concurrency: With asynchronous access, multiple database operations can be executed concurrently without blocking each other. This improves concurrency and enables better utilization of system resources, leading to higher throughput and improved performance, especially in scenarios with high levels of concurrent database requests.

- Scalability and performance: Asynchronous access facilitates scalability by enabling the application to handle increased user demand and larger workloads. By allowing parallel execution of database operations, it supports efficient utilization of resources and can effectively handle heavy workloads without sacrificing performance. This makes it easier to scale the application as the user base grows or when dealing with large datasets.

Remember, the asynchronous approach also facilitates the implementation of background operations and real-time data updates.

## Install and configure SQLDelight async extension

To work asynchronously, we need to apply all the SQLDelight configuration settings, including plugins and directory setup, to create our tables and queries in the .sq file as we saw in the previous topic.

Now, we will make use of SQLDelight extensions, which provide us with enhanced functionality. In this case, we will utilize the extension for asynchronous handling. In our `build.gradle.kts`, we will include a new dependency for this mode.



```kotlin
dependencies {
    // SQLDelight Dependency
    implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
    // Extension for Kotlin Coroutines and Async
    implementation("app.cash.sqldelight:async-extensions:2.0.0")
}
```



Now we can work asynchronously, meaning we can use coroutines to launch queries and retrieve their results using the new functionality. Remember that you should always be in a suspended function context or within a coroutine to avoid blocking the main thread.

## Selecting data as a list

To obtain data as a list, just call the select function and use `awaitAsList()`. This returns a list in a non-blocking way.



```kotlin
// Select all users
val allUsers = usersQueries.selectAll().awaitAsList()
println("All users: $allUsers")
```



## Select a value

To obtain a value, you can choose between:

- `awaitAsOne()`: it returns a non-null value; if the value does not exist, it throws an exception in a non-blocking way.

- `awaitAsOneOrNull()`: it returns a null value; if the value does not exist, it returns null in a non-blocking way.



```kotlin
// Select by id
val userById = usersQueries.selectById(2).awaitAsOne()
println("User by id: $userById")

// Delete user
usersQueries.delete(2)
val deletedUser = usersQueries.selectById(2).awaitAsOneOrNull()
println("Deleted user: $deletedUser")
```



## Reactive mode

Another extension offered by SQLDelight provides the ability to directly obtain the changes made to our tables for each query executed. To achieve this, we can consume a query as a `Flow` (Kotlin coroutines). This way, we can subscribe to and observe the changes and reactively receive them. This is a special version of the [async mode](/learn/step/33432).

That offers us many possibilities. Observable and reactive searches using `Flow` are read operations that emit new values whenever there are changes in any of the referenced tables. You can use it to keep a list of items updated as elements are inserted, updated, or removed from the underlying database.

## Install and configure SQLDelight coroutines extension

To work with flows and coroutines, we need to apply all the SQLDelight configuration settings, including plugins and directory setup, to create our tables and queries in the .sq file as we saw in the previous topic.

Now, we will make use of the SQLDelight extension for coroutines handling. In our `build.gradle.kts`, we will include a new dependency for this mode.



```kotlin
dependencies {
    // SQLDelight Dependency
    implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
    // Extension for Kotlin Coroutines and Flows
    implementation("app.cash.sqldelight:coroutines-extensions:2.0.0")
}
```



## Using flows for reactive changes

We can launch such a query once in the background. Every time we make any changes, we will always receive the current data from the table, allowing us to update our views or implement a notification system with the updated information.To perform that, we need to obtain a flow of our query using the `asFlow()` and `mapToList()` functions. Do not to forget to indicate the [Dispatcher](https://kotlinlang.org/docs/coroutine-context-and-dispatchers.html) to perform the operation.



```kotlin
// Prepare a flow of users, to get notified when the table changes
val usersFlow = usersQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
```



Then we can collect the `Flow` in a background task:



```kotlin
scope.launch {
    // collect the flow
     usersFlow
         .onStart { println("Notifications for users are ready...") }
         .collect { users ->
              println("Users are updated: $users")
         }
}
```



Now, for every change in the table, we react to the changes and obtain the actual values thanks to the `Flow`. Here we have an example:



```kotlin
// Insert users
println("Inserting users")
usersQueries.insert("John Doe")
usersQueries.insert("Jane Doe")
usersQueries.insert("John Smith")

// Update user
println("Updating a user")
usersQueries.update("Jane Doe 2", 2)

// Delete user
println("Deleting a user")
usersQueries.delete(2)
```



We have the following result:



```kotlin
Notifications for users are ready...
Users are updated: []
Inserting users
Users are updated: [User(id=1, name=John Doe), User(id=2, name=Jane Doe), User(id=3, name=John Smith)]
Updating user
Users are updated: [User(id=1, name=John Doe), User(id=2, name=Jane Doe 2), User(id=3, name=John Smith)]
Deleting user
Users are updated: [User(id=1, name=John Doe), User(id=3, name=John Smith)]
```



Also, we can use this function to obtain a `Flow` with one element:

- `mapToOne()`: returns a flow with the element.

- `mapToOneNotNull()`: returns a flow with a non-null element.

- `mapToOneOrDefault()`: returns a flow with the element or a default value.

- `mapToOneOrNull()`: returns a flow with the element or null value.

## Conclusion

In this topic, we have learned how to use SQLDelight in the asynchronous way and obtain the changes of a table when we perform a query. Now you can perform actions in a non-blocking way, code a notification system, or auto-refresh your view collecting the flow with the changes. Now is the time to do some tasks to check what you have learned. Are you ready?

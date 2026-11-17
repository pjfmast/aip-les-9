# SQLDelight

Working with large amounts of data and databases is possible thanks to JDBC, but sometimes it is expensive to figure out what queries to make and, above all, to pass information stored in tables to objects of our business logic.

In this topic, we will use SQLDelight, a library for Kotlin that generates type-safe Kotlin APIs from your SQL [statements](/learn/step/33421). It allows you to write SQL queries and then generates Kotlin code to execute those queries and map the results to custom types.

## SQLDelight

[SQLDelight](https://cashapp.github.io/sqldelight) is a tool used in Kotlin programming that creates secure Kotlin APIs from your SQL code. This means you can write SQL queries, and SQLDelight will automatically produce Kotlin code to run these queries and map the outcomes to specific types you've defined.

SQLDelight is versatile enough to be used in any environment that supports the Java Virtual Machine (JVM), Android, Native, or JS Kotlin apps. It offers a straightforward, secure method to outline how data is read from and written to your SQLite database.

In simpler terms, SQLDelight is like a translator between SQL and Kotlin. You tell it what you want to do in SQL, and it writes the Kotlin code to do it. This makes it easier to work with databases in Kotlin, especially for complex operations.

SQLDelight also supports complex SQL features like views, indexes, and transactions. It can help make your database code more robust and easier to maintain.

## Install and configure SQLDelight

In this topic, we will work with the [latest version](https://sqldelight.github.io/sqldelight/latest/) of SQLDelight for the JVM. In our `build.gradle.kts`, we first need to install the SQLDelight plugin. The SQLDelight plugin allows you to use SQLDelight's features in your project.



```kotlin
plugins {
    kotlin("jvm") version "1.9.0"
    application
    // SQLDelight Plugin
    id("app.cash.sqldelight") version "2.0.0"
}
```



Then we will include the SQLDelight dependency in the dependencies block. We will work with SQLite, but you can work with other databases, too:



```kotlin
dependencies {
    // SQLDelight Dependency
    implementation("app.cash.sqldelight:sqlite-driver:2.0.0")
    testImplementation(kotlin("test"))
}
```



Finally, we need to include the SQLDelight configuration block with the Schema and package names for the generated code. SQLDelight will generate a `Database` class with an associated `Schema` object that can be used to create your database and execute statements on it. Note, you can use the most suitable package structure for your project. However, we recommend following the one indicated in the configuration or group section of `build.gradle.kts`: `group = "dev.hyperskill"`.



```kotlin
// SQLDelight Configuration
sqldelight {
    databases {
        // Schema name
        create("Database") {
            // Package name
            packageName.set("dev.hyperskill.sqldelight")
        }
    }
}
```



After adding this dependency, you will typically need to refresh or resync your Gradle project to download and include the new library.

To work in a more fluid and easy way, we recommend using the [SQLDelight plugin for IntellIJ](https://plugins.jetbrains.com/plugin/8191-sqldelight). It will help you to generate type-safe Kotlin APIs from SQL and provide language features for SQL inside the IDE.

## Create tables and queries

To work with a database and queries, we need to create the tables and queries to use it. We will create a .sq file and a new directory under `src/main/sqldelight/`. Note, you can follow your own structure according to your package name, but this is the base directory. For instance, in our example, we need to create the structure: `src/main/sqldelight/dev/hyperskill/sqldelight`; without it, classes are not generated. Typically, the first statement in the .sq file creates a table, but you can also create indexes or set up default content. We will create a User.sq file with the data of a user: an id (autoNumeric) and a name (string). Also, we will create the queries. SQLDelight will generate a type-safe function for any labeled SQL statement in a `.sq` file.



```sql
-- Create the table User
CREATE TABLE User (
    id INTEGER PRIMARY KEY NOT NULL,
    name TEXT NOT NULL
);

-- Create the queries to work with the table User
selectAll:
SELECT * FROM User;

selectById:
SELECT * FROM User WHERE id = ?;

selectLastInserted:
SELECT * FROM User WHERE id = last_insert_rowid();

insert:
INSERT INTO User (name) VALUES (?);

update:
UPDATE User SET name = ? WHERE id = ?;

delete:
DELETE FROM User WHERE id = ?;
```



Finally, to generate the code, we will use a Gradle task: `generateSqlDelightInterface`. This Gradle task is run automatically by the SQLDelight IDE plugin when you edit a `.sq` file, and also as part of a normal Gradle build. The generated code will be in the build/generated/code/sqldelight/ folder, where you can find the User data class and the type-safe code generated from queries.

## Working with SQLDelight

The first step is configure the database driver. The constructor accepts a JDBC connection string that specifies the location of the database file. The `IN_MEMORY` constant can also be passed to the constructor to create an in-memory database. We will work with an in-memory database. Finally, we can obtain a reference for the queries. We have two options to get the reference:

- `val usersQueries: UsersQueries = Database(driver).usersQueries`, so we have the reference with the driver property.

- `val usersQueries: UsersQueries = UsersQueries(driver)`, using directly the object that encapsulates the queries.



```kotlin
// Create a SqlDriver
val driver: SqlDriver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
// Create a Database
Database.Schema.create(driver)
// Get a reference to the queries
val usersQueries: UsersQueries = Database(driver).usersQueries
// or
// val usersQueries: UsersQueries = UsersQueries(driver)
```



1. **Inserting a user**

You only need to call your function `insert` and pass the parameter data. Remember, it is a type-safe query:



```kotlin
// Insert users
usersQueries.insert("John Doe")
usersQueries.insert("Jane Doe")
usersQueries.insert("John Smith")
```



2. **Selecting last inserted**

Use `selectLastInserted`:



```kotlin
// Select last inserted
val lastInserted = usersQueries.selectLastInserted().executeAsOne()
println("Last inserted: $lastInserted") //  User(id=3, name=John Smith)
```



3. **Selecting data (users)**

Just call the `selectAll` function. It will return a list thanks to `executeAsList()`.



```kotlin
// Select all users
val allUsers = usersQueries.selectAll().executeAsList()
println("All users: $allUsers") // [User(id=1, name=John Doe), User(id=2, name=Jane Doe), User(id=3, name=John Smith)]
```



4. **Selecting by id**

Use the `selectById` function. To obtain the value, you can choose between:

- `executeAsOne()`: return a non-null value; if the value does not exist, throw an exception.

- `executeAsOneOrNull()`: return a null value; if the value does not exist, return null.



```kotlin
val userById = usersQueries.selectById(2).executeAsOne()
println("User by id: $userById") // User(id=2, name=Jane Doe)
```



5. **Updating a user**

Use the `update` function and pass the data in the parameters:



```kotlin
// Update user
usersQueries.update("Jane Doe 2", 2)
val updatedUser = usersQueries.selectById(2).executeAsOne()
println("Updated user: $updatedUser") // User(id=2, name=Jane Doe 2)
```



6. **Deleting a user**

Use the `delete` function with `executeAsOneOrNull` (recommended):



```kotlin
// Delete user
usersQueries.delete(2)
val deletedUser = usersQueries.selectById(2).executeAsOneOrNull()
println("Deleted user: $deletedUser") // null

// Select all users
val allUsers2 = usersQueries.selectAll().executeAsList()
println("All users: $allUsers2") // [User(id=1, name=John Doe), User(id=3, name=John Smith)]
```



## Conclusion

In this topic, we have learned how to use databases with Kotlin and SQLDelight, now you can handle databases easily with type-safe code. SQLDelight will give you the types and generate the code to work with your database.

Now is the time to do some tasks to check what you have learned. Are you ready?

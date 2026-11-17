# Working with JDBC

Working with large amounts of data and information is fundamental in software development. That's why databases are very important for creating efficient and secure applications where information can be shared. In this topic, we will see how to work with databases and how to create, modify, or delete information using Kotlin code.

## JDBC

JDBC (Java Database Connectivity) is a JVM API that defines how a client may access a database. It provides methods for querying and updating data in a database and is oriented towards relational databases. Using JDBC, it is easy to send SQL statements to any relational database. It establishes a connection with the database, sends SQL statements, and processes the results. The JDBC API uses JDBC drivers to connect with the database.

Now, what do we need to work with JDBC? The first thing is to know which database we are going to use and then find the driver for that database.

Throughout this topic, we are going to use [H2](https://www.h2database.com/html/main.html), with which we can easily work with in-memory, on-disk, and server databases. The concepts we will discuss are applicable to any database once you have the driver and the information to connect to the database.

## Install driver dependencies

To include the H2 database dependency in your Gradle project, you will need to add the following line to your `build.gradle` file under the dependencies section. We will use the [latest version](https://mvnrepository.com/artifact/com.h2database/h2):



```kotlin
dependencies {
    // other dependencies...

    runtimeOnly("com.h2database:h2:x.y.zzz")
}
```



After adding this dependency, you will typically need to refresh or resync your Gradle project to download and include the new library.

## Configure the connection

To work with databases, we need to configure the connection and use the database driver. We need two objects to perform these actions:

- **Connection**: this interface is a part of the java.sql package. It's one of the core interfaces in the JDBC API. It represents a session/connection with a specific database. Within the context of a connection, SQL statements are executed and results are returned. The connection interface provides methods to deal with database transactions, create statement objects, and do other things related to managing a database session.

- **DriverManager**: this class is a part of the java.sql package and acts as an intermediary between users and drivers. It keeps track of the drivers that are available and handles establishing a connection between a database and the appropriate driver. The DriverManager class works in conjunction with driver classes to connect to databases. To create a new database connection, you call one of its static `getConnection()` overloads, passing a JDBC URL and, optionally, a username and password.

We will encapsulate the configuration in an object class (singleton) to configure the connection to H2 in the memory database.



```kotlin
object DatabaseManager : AutoCloseable {
    private var connection: Connection? = null
    private val url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;"
    private val user = "sa"
    private val password = ""

    fun open(): Connection {
        try {
            connection = DriverManager.getConnection(url, user, password)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return connection!!
    }

    override fun close() {
        try {
            connection?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
```



The `open()` method in the DatabaseManager object is designed to establish a connection to a database using JDBC. To establish a connection, it calls `DriverManager.getConnection(url, user, password)`. This method is a part of the java.sql.DriverManager class and is used to create a new Connection object that represents a connection to a database.

- `url` is the JDBC URL for the database. In our case, it's set to "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1;", which represents an in-memory H2 database.

- `user` and `password` are the credentials used to connect to the database. In our case, they're set to "sa" and "" (an empty string), respectively.

If the `DriverManager.getConnection()` call is successful, it returns a new Connection object, which is then assigned to connection. Finally, `open()` returns the connection object, which can be used to interact with the database. If an error occurs while trying to establish a connection, the method catches the exception and prints the [stack trace](/learn/step/32147). This is a simple error handling mechanism. In a real-world application, you would typically want to handle errors more gracefully. The `getConnection()` method is designed to be called every time a database connection is needed. The `close()` method in the DatabaseManager object is designed to close the active database connection. This method should be called whenever you're done interacting with the database and no longer need the connection. It's important to always close database connections when they're no longer needed to avoid resource leaks and potential issues with the database.

We will implement the `AutoCloseable` interface so we can use Kotlin's `use` function to close the connection automatically.

## Statement and ResultSet

To work with a database or to perform an SQL query, we need to work with:

- **Statement**: it is a simple interface provided by the JDBC API. It is used to execute static SQL statements and return the results they produce. A Statement object is created from an active connection to a database. A Statement does not provide any parameter substitution, meaning that it's vulnerable to SQL injection attacks. Thus, we will use **PreparedStatement**, which is a subinterface of Statement. It represents a precompiled SQL statement that can be executed multiple times without the overhead of compiling the statement each time. The advantage of using PreparedStatement is that it provides a way to use parameterized queries, which makes it possible to pass parameters into SQL commands. This helps prevent SQL injection attacks because the parameters are automatically escaped by the JDBC driver. We will use it, for example, for create, insert, update, or delete queries.

- **ResultSet**: it is an object that contains the results of an SQL query. It is essentially a data structure that represents a database result set. You can iterate through the ResultSet to access each row of the returned query, and from each row, you can retrieve individual columns. The `next` method of ResultSet is used to move to the next row in the result set (it initially starts before the first row). Each call to `next` returns a boolean indicating whether there is another row to process. Inside the `while` loop, the getX method of ResultSet is used to retrieve the data from the columns of the current row. X is the type of the data (String, Int, etc.) The argument to getX is the name of the column in the database.

So, if we are working with a Statement or PreparedStatement and need to execute an SQL query, we will use `executeUpdate` and `executeQuery`, two methods used for interacting with a database. They have different purposes:

- `executeUpdate` is used for executing statements that change the database in some way, such as the `INSERT`, `UPDATE`, or `DELETE` statements. It returns an integer representing the number of rows affected by the query.

- `executeQuery` is used for executing `SELECT` statements, which retrieve data from the database. It returns a `ResultSet` object, which can be used to iterate over the rows of the data returned by the query.

Now, we will code an example to work with the user with our DataBaseManager, Statement, and ReseultSet. We will use a data class for the user info:



```kotlin
data class User(val id: Int, val name: String)
```



## Creating a table



```kotlin
fun createTable() {
    val sql = """CREATE TABLE IF NOT EXISTS users (id INT PRIMARY KEY, name VARCHAR(100))"""
    DatabaseManager.open().use { conn ->
        conn.prepareStatement(sql).use {
            it.executeUpdate()
        }
    }
}
```



In this code, `use` is called on both the `Connection` and `PreparedStatement` objects. This ensures that these resources are closed automatically once they're no longer needed, which can help prevent resource leaks.

## Inserting a user



```kotlin
fun insertData(user: User) {
    val sql = """INSERT INTO users (id, name) VALUES (?, ?)"""
    DatabaseManager.open().use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, user.id)
            stmt.setString(2, user.name)
            stmt.executeUpdate()
        }
    }
}
```



Now, you can create `User` objects and pass them to the `insertData` function:



```kotlin
val user1 = User(1, "John Doe")
val user2 = User(2, "Jane Doe")
val user3 = User(3, "John Smith")
    
insertData(user1)
insertData(user2)
insertData(user3)
```



## Updating a user



```kotlin
fun updateData(user: User) {
    val sql = """UPDATE users SET name = ? WHERE id = ?"""
    DatabaseManager.open().use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, user.name)
            stmt.setInt(2, user.id)
            stmt.executeUpdate()
        }
    }
}
```



Now, you can update `User` objects and pass them to the `updateData` function:



```kotlin
val user4 = user1.copy(name = "John Doe Updated")
updateData(user4)
```



## Deleting a user



```kotlin
fun deleteData(user: User) {
    val sql = """DELETE FROM users WHERE id = ?"""
    DatabaseManager.open().use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            stmt.setInt(1, user.id)
            stmt.executeUpdate()
        }
    }
}
```



Now, you can delete `User` objects and pass them to the `deleteData` function:



```kotlin
deleteData(user2)
```



## Selecting data (users)



```kotlin
fun selectData(): List<User> {
    val sql = """SELECT * FROM users"""
    val users = mutableListOf<User>()
    DatabaseManager.open().use { conn ->
        conn.prepareStatement(sql).use { stmt ->
            val rs = stmt.executeQuery()
            while (rs.next()) {
                users.add(User(rs.getInt("id"), rs.getString("name")))
            }
        }
    }
    return users
}
```



Now, you can get the list of `User` objects with `selectData`:



```kotlin
selectData().forEach { println(it) }
// User(id=1, name=John Doe Updated)
// User(id=3, name=John Smith)
```



## Conclusion

In this topic, we have learned how to use databases with Kotlin and JDBC, how to make a connection, perform SQL queries, and map them into objects.

Now is the time to do some tasks to check what you have learned. Are you ready?

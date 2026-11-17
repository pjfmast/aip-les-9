# Koin basics

Modern software development involves building complex applications with various components and moving parts. With such complexity, managing dependencies among components can become a challenging task. Dependency injection is a technique that simplifies the process of managing dependencies and makes code low-coupled for more maintainability and reusability.

The **Koin** framework is a dependency injection tool for Kotlin-based applications. Koin provides a simple, lightweight, and flexible way to handle dependencies in an application. It is particularly useful for building large-scale applications, where managing dependencies can become more complicated.

## Getting started

To understand why we need a dependency injection, let's write a small task-tracking application.

As an improvised database, we will use a `DataSource` class:



```kotlin
enum class Status { TODO, ONGOING, DONE }

data class Task(
    val name: String,
    var status: Status
)

class DataSource {
    val tasks: MutableList<Task> = mutableListOf()
}
```



In addition, we need to implement a list of tasks by category, finding by name, create a task, and update the status:



```kotlin
fun findTaskByName(name: String, source: DataSource): Task? = source.tasks.find { it.name == name }

fun listByStatus(status: Status, source: DataSource): List<Task> = source.tasks.filter { it.status == status }

fun createTask(task: Task, source: DataSource) {
    source.tasks.add(task)
}

fun updateTaskStatus(name: String, status: Status, source: DataSource) {
    val task = findTaskByName(name, source) ?: throw Exception("Task not found")
    task.status = status
}
```



You may notice that we need to pass a data source to each function, which would be nice to fix. One option is to combine these functions into a class in which data source will be one of the fields. Function implementations are omitted.



```kotlin
class TaskService(private val source: DataSource) {
    fun findTaskByName(name: String): Task?
    fun listByStatus(status: Status): List<Task>
    fun createTask(task: Task)
    fun updateTaskStatus(name: String, status: Status)
}
```



Now that we only need to pass the data source when initializing an instance of the class, our job has become much easier. So, we have a dependency of the `TaskService` class on the `DataSource` class.

What if there are many such `TaskService` instances? We need to pass an instance of `DataSource` every time, which also needs to be initialized somewhere. That's why our application turned out to be highly coupled. We can use dependency injection to disconnect these components.

## Installation

To install the Koin, you need to add the `koin-core` dependency to the `build.gradle.kts` file:



```kotlin
implementation("io.insert-koin:koin-core:$koin_version")
```



## Pattern explanation

In software development, one common problem is the composition of components based on dependencies. To address this issue, we need a way to provide those dependencies. There are two common approaches to solving this problem: the Injector pattern and the Locator pattern.

The **Injector** pattern involves creating a separate component responsible for providing the necessary dependencies to other components. This injector component typically takes the responsibility of instantiating and configuring the necessary dependencies, then injecting them into the dependent components.

On the other hand, the **Locator** pattern involves creating a central component responsible for locating and providing the necessary dependencies to other components. This locator component typically maintains a registry of available dependencies and is responsible for instantiating and configuring them when requested by the dependent components.

It's important to note that in practice, a single component can cover both the Injector and Locator patterns, acting as a dependency provider. The Koin framework can be seen as an example of such a component, providing dependency injection functionality and acting as a dependency provider for Kotlin-based applications.

## Main concepts

The Koin module is a space for declaring all your components:



```kotlin
val appModule = module {
    // components
}
```



We have 2 kind of components in Koin:

-

`single`: Each time we want to inject this component, Koin injects the same instance. Roughly speaking, this is a kind of singleton that exists in a single instance for as long as its module stays loaded. Unlike a plain object singleton, though, it isn't permanent — calling `unloadKoinModules()` (or `stopKoin()`) removes it from the container along with its module, and a later `loadKoinModules()` call creates a fresh instance. You can declare such a component like this:



```kotlin
single<TaskService> { TaskService() }
```



-

`factory`: Each time we want to inject this component, Koin creates a new instance. For example, we want a new `DataSource` instance to be created each time, independent of the others:



```kotlin
factory<DataSource> { DataSource() }
```



If we need to pass another component to create a component, we can use the `get()` method. The order of the component declaration is not important:



```kotlin
module {
    factory<DataSource> { DataSource() }
    single<TaskService> { TaskService(get()) }
}
```



In addition, Koin offers constructor DSL, which allows you not to specify the type of component when declaring, as well as automatically inject dependencies into the constructor:



```kotlin
module {
    factoryOf(::DataSource)
    singleOf(::TaskService)
}
```



## Injecting dependencies

To use Koin for dependency injection, we need to run an instance of `KoinApplication` into the `GlobalContext`:



```kotlin
startKoin {
    modules(module1, module2, ...)
}
```



The `GlobalContext` object in Koin helps to automatically manage Koin components, such as modules and their dependencies, by holding the Koin instance resulting from the modules and options declared in the `startKoin` function. That allows `KoinComponent` classes to access the Koin instance and retrieve dependencies without the need for manual dependency injection.

Koin offers two ways to inject dependencies:

-

**Lazy mode**: Koin injects dependencies only the first time they are used. That helps to improve the application startup time by avoiding unnecessary object creation. This can be useful in situations where initializing a dependency takes a lot of resources (for example, a request to an external database that lasts 50 seconds), but the dependency itself is not needed immediately. To use lazy mode, we can use the `by inject()` delegate:



```kotlin
// Lazy mode
val service by inject<TaskService>(TaskService::class.java)
```



-

**Eager mode**: Koin injects dependencies at application startup, regardless of whether they are used or not. This can help to catch dependency issues early on but may increase application startup time. To use eager mode, we can use the `get()` method:



```kotlin
// Eager mode
val service = get<TaskService>(TaskService::class.java)
```



We can choose between these two modes based on our specific use case.

So, our updated application may look like this:



```kotlin
fun main() {
    val appModule = module {
        factory<DataSource> { DataSource() }
        single<TaskService> { TaskService(get()) }
    }

    startKoin {
        modules(appModule)
    }

    // Lazy mode
    val service by inject<TaskService>()

    // Or eager mode:
    // val service = get<TaskService>()
}
```



## Koin components

However, the initial problem we faced was how to deal with dependency injection inside classes. To do this, we can implement the `KoinComponent` interface, which connects more convenient `inject()` and `get()` extension functions for the class into which we want to inject:



```kotlin
class TaskService : KoinComponent {
    private val source by inject<DataSource>()
    // in another style
    private val source: DataSource by inject()
}
```



We should update the declaration of components inside the module accordingly:



```kotlin
val appModule = module {
    factory<DataSource> { DataSource() }
    single<TaskService> { TaskService() }
}
```



So, once you have tagged your class as `KoinComponent`, you gain access to `inject()` and `get()` extension functions.

## Injecting with parameters

Imagine that to initialize the service, we need to pass some parameters that cannot be injected as dependencies. For example, the initial list of tasks:



```kotlin
class DataSource(
    val tasks: MutableList<Task> = mutableListOf<Task>()
)
```



In this case, we can update the description of the component using the value-parameter `ParametersHolder`, which is a kind of parameter list:



```kotlin
factory<DataSource> { params -> DataSource(params.get()) }
```



To pass this parameter during injection, we should use `parametersOf()` function:



```kotlin
private val source: DataSource by inject {
    parametersOf(
        listOf(
            Task("Create DataSource", Status.DONE),
            Task("Create TaskService", Status.ONGOING)
        )
    )
}
```



> **Warning:**

When determining which parameter to pass where, Koin is primarily guided by the type of the parameter. If there are multiple parameters of the same type, they are passed to the `parametersOf` function in the order they are passed.

## Interface binding

Interface binding in Koin is a way to provide a common interface for multiple implementations. It allows switching between different implementations of an interface without changing any code that depends on it easily.

For example, we may have multiple data sources. Most often, data sources differ by different databases, but we will consider a simpler example: in one data source, the task list is empty, and in the other, it is initially filled with some data.

However, both data sources contain tasks, so we can create an interface with the `tasks` field, which we will implement in two different ways in the future:



```kotlin
interface DataSource {
    val tasks: MutableList<Task>
}

class EmptyDataSourceImpl : DataSource {
    override val tasks: MutableList<Task> = mutableListOf()
}

class PreInitializedDataSourceImpl : DataSource {
    override val tasks: MutableList<Task> = mutableListOf(
        Task("Create DataSource", Status.DONE),
        Task("Create TaskService", Status.DONE),
        Task("Create Application", Status.ONGOING),
        Task("List tasks", Status.TODO),
    )
}
```



So, the `TaskService` depends on the interface, but not on the specific implementation. Let's update the Koin module that binds the interface to its implementations:



```kotlin
val appModule = module {
    factory<DataSource> { EmptyDataSourceImpl() }
    // Or we can choose another implementation
    // factory<DataSource> { PreInitializedDataSourceImpl() }
    ...
}
```



If we use Constructor DSL, the binding of interfaces occurs in another syntax. Note that, unlike binding in the classic version, we pass the reference to the implementation first, and the interface next:



```kotlin
val appModule = module {
    factoryOf(::EmptyDataSourceImpl) { bind<DataSource>() }
    // or
    // factoryOf(::PreInitializedDataSourceImpl) { bind<DataSource>() }
}
```



You can also use `bind` without lambda:



```kotlin
val appModule = module {
    factoryOf(::EmptyDataSourceImpl) bind DataSource::class
    // or
    // factoryOf(::PreInitializedDataSourceImpl) bind DataSource::class
}
```



However, the `TaskService` itself does not change in any way and remains the same:



```kotlin
class TaskService : KoinComponent {
    private val source: DataSource by inject()
    ...
}
```



Overall, interface binding is a tool that allows easy swapping of implementations without changing the dependent code.

## Named components

What if we want to switch between implementations right in runtime? We can use named components to determine which instance we need at that moment.

To define a named component in Koin, we use the `named` function, which takes a string argument that serves as the name of the function. We then use this named function when declaring our component:



```kotlin
val appModule = module {
    factory<DataSource>(named("emptyDataSource")) { EmptyDataSourceImpl() }
    factory<DataSource>(named("preInitializedDataSourceImpl")) { PreInitializedDataSourceImpl() }
    ...
}
```



To use the name when injecting a component, we use the `get()` method or the `inject()` delegate and pass in the name of the component as a string:



```kotlin
class TaskServiceImpl : TaskService, KoinComponent {
    val emptyDataSource = get<DataSource>(named("emptyDataSource"))
    val preInitializedDataSourceImpl by inject<DataSource>(named("preInitializedDataSourceImpl"))
    ...
}
```



Since working with strings as a qualifier is not very safe (it is quite difficult to find a bug that occurs due to a typo in the name), we can use naming using types:



```kotlin
val appModule = module {
    factory<DataSource>(named<EmptyDataSourceImpl>()) { EmptyDataSourceImpl() }
    factory<DataSource>(named<PreInitializedDataSourceImpl>()) { PreInitializedDataSourceImpl() }
    ...
}
```



Injection occurs in a similar way:



```kotlin
class TaskServiceImpl : TaskService, KoinComponent {
    val dataSource = get<DataSource>(named<EmptyDataSourceImpl>())
    val preInitializedDataSourceImpl by inject<DataSource>(named<PreInitializedDataSourceImpl>())
    ...
}
```



If we are working with Constructor DSL, the syntax will look like this:



```kotlin
val appModule = module {
    factoryOf(::PreInitializedDataSourceImpl) {
        bind<DataSource>()
        named<PreInitializedDataSourceImpl>() // or named("preInitializedDataSourceImpl")
    }
    factoryOf(::EmptyDataSourceImpl) {
        bind<DataSource>()
        named<EmptyDataSourceImpl>()  // or named("emptyDataSourceImpl")
    }
    ...
}
```



## Conclusion

We can now work with Koin, a dependency injection tool that allows to manage dependencies for Kotlin-based applications. It helps developers to target loose coupling. Here are the main points to remember:

- Dependency injection is a technique that simplifies the process of managing dependencies and makes code more maintainable and reusable.

- Koin components can be `single` or `factory`.

- Koin offers two ways to inject dependencies: using the `inject` delegate for lazy injecting and the `get` method for eager injecting.

- To deal with dependency injection inside classes, we can use the `KoinComponent` interface, which connects more convenient extension functions for the class into which we want to inject.

- Interface binding allows easy swapping of implementations without changing the dependent code.

- With the help of named components, we can switch the necessary implementations right in the runtime.

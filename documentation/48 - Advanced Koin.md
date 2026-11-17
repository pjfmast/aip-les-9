# Advanced Koin

In the previous article, we reviewed the basic principles of implementing DI through the Koin framework. Now we will look at more complex aspects of working with Koin, such as annotations, injection scopes, and so on.

## Context isolation

When developing a library or some code that can be extended by other developers, you may need to separate the areas of dependencies, as they may conflict with each other. To avoid dependency conflicts, we can isolate the context.

First, we need to declare an instance of Koin and save it in a class for further access:



```kotlin
object IsolatedContext {

    val koinApp = koinApplication {
        modules(module1, module2, ...)
    }

    val koin = koinApp.koin
}
```



Now, we can use `IsolatedContext` to define a custom `KoinComponent` that will use our isolated context:



```kotlin
abstract class IsolatedKoinComponent : KoinComponent {
    override fun getKoin(): Koin = IsolatedContext.koin
}
```



Finally, we can work within an isolated context:



```kotlin
class SomeKoinComponent : IsolatedKoinComponent()
```



## Scopes

The **scope** is a fixed period of time during which the state of an object is preserved. Once this gap expires, any objects bound under that scope cannot be injected again. In Koin, we have three kind of scopes by default:

- The `single` definition exists for the entire lifetime of the container and cannot be removed from it.

- The `factory` definition creates objects with a short lifetime, and the factory is not stored inside the container.

- The `scoped` definition creates an object whose lifetime lasts as long as the scope exists.

To declare a scope for a given type, we use the `scope` function, which gathers scoped definitions as a logical unit of time:



```kotlin
module {
    scope<SomeType> {
        scoped { Controller() }
    }
}
```



To move an instance of scope to a class, we can use `KoinScopeComponent`. You can use `createScope` to create scope from the current component's scope ID and name:



```kotlin
class OuterScopeComponent : KoinScopeComponent {
    override val scope: Scope by lazy { createScope(this) }
}

class InnerScopeComponent
```



Let's define a scope for `OuterScopeComponent`, to resolve `InnerScopeComponent`. That means that the lifetime of the `InnerScopeComponent` will be tied to the lifetime of the `OuterScopeComponent`:



```kotlin
module {
    scope<OuterScopeComponent> {
        scoped { InnerScopeComponent() }
    }
}
```



We can use an instance of `InnerScopeComponent` in this way:



```kotlin
class OuterScopeComponent : KoinScopeComponent {
    override val scope: Scope by lazy { newScope(this) }

    val inner: InnerScopeComponent by inject()

    fun close() {
        scope.close() // don't forget to close current scope
    }
}
```



Once your scope instance is complete, close it with the `close()` function.

Sometimes you may need to associate a scope with another one and then allow the combined definition space. For example:



```kotlin
module {
    single { ParentScope() }
    scope<ParentScope> {
        scoped { ChildScope() }
    }
    scope<ChildScope> {
        scoped { ChildComponent() }
    }
}
```



Now we can resolve `ChildScope`'s scope instance `ChildComponent`, directly from `ParentScope`'s scope using `linkTo()`:



```kotlin
val parent = koin.get<ParentScope>()
val child = a.scope.get<ChildScope>()
parent.scope.linkTo(child.scope)

// we got the same ChildComponent instance
assertTrue(parent.scope.get<ChildComponent>() == child.scope.get<ChildComponent>())
```



## Modules

So far, we have been working with modules, but we haven't touched on how they work. A module is the space in which you define your dependencies and can be dependent on other modules. Dependencies are lazy and are resolved only when the component requests them.

If you have two dependencies in different modules that can be defined as the same, the **last-loaded module's** dependency will override all the others:



```kotlin
val module2 = module {
    single<Service> { LocalServiceImpl() }
}

val module1 = module {
    single<Service> { RemoteServiceImpl() }
}

startKoin {
    // module2 is loaded last, so the local implementation will override the remote one
    modules(module1, module2)
}
```



You can specify in your Koin application configuration to disallow override with `allowOverride(false)`. In the case of disabling override, Koin will throw an `DefinitionOverrideException` exception on any override attempt:



```kotlin
startKoin {
    // Forbid definition override
    allowOverride(false)
}
```



The `includes()` function allows you to create a module, including other modules and all dependencies defined in them, respectively:



```kotlin
val databases = module {
    /* Definitions here */
}

val controllers = module {
    /* Definitions here */
}

val app = module {
    includes(databases, controllers)
}

startKoin { modules(app) }
```



You can also use `includes()` to add internal and private modules, thus achieving greater flexibility in the project.

## Testing support

Koin has built-in tools for working with testing. To begin with, we should visit the test class as `KoinTest` to work with it according to the same principles as with `KoinComponent`:



```kotlin
class SomeComponent

class AnotherComponent(val component: SomeComponent)

class SomeTest : KoinTest {

    val anotherComponent: AnotherComponent by inject()

    @Test
    fun `components are injected`() {
        startKoin {
            modules(
                module {
                    single { SomeComponent() }
                    single { AnotherComponent(get()) }
                }
            )
        }

        val someComponent = get<SomeComponent>()

        assertNotNull(someComponent)
        assertEquals(someComponent, anotherComponent.component)
    }
}
```



You can also declare components directly at runtime:



```kotlin
@Test
fun `runtime declaration`() {
    startKoin { }

    declare {
        factory { SomeComponent() }
    }

    Assert.assertNotEquals(get<SomeComponent>(), get<SomeComponent>())
}
```



If you use mocking in your tests, you can find out more on the official Koin [documentation page](https://insert-koin.io/docs/reference/koin-test/testing/).

## Checking modules

Sometimes there can be problems with injecting dependencies at runtime. To avoid this, Koin has historically provided a `checkModules()` feature to check dependencies in tests. The `checkModules` method goes through the definition tree and checks whether each definition is injected properly:



```kotlin
@Test
fun `check dependencies hierarchy`() {
    koinApplication {
        modules(module1, module2, ...)
        checkModules()
    }
}
```



The `checkModules` method has its own DSL, which allows to specify how to work with the following cases:

- `withInstance(value)` / `withInstance()` adds a dependency in the context of Koin, which can then be used for a dependency tree.

- `withParameter(qualifier){ qualifier -> value }` / `withParameter(qualifier){ qualifier -> parametersOf(...) }` passes parameters to the component constructor.

- `withScopeLink` is used to inject dependencies from a specific scope.

Let's say we have two scopes:



```kotlin
val someModule = module {
    scope(named("scope1")) {
        scoped { SomeComponent() }
    }
    scope(named("scope2")) {
        scoped { AnotherComponent(get()) }
    }
}
```



To implement dependencies from these scopes, we use `withScopeLink`:



```kotlin
@Test
fun `test scope linking`() {
    koinApplication {
        modules(someModule)
        checkModules() {
            withScopeLink(named("scope2"), named("scope1"))
        }
    }
}
```



`withProperty(key, value)` adds property to Koin.

**A note on currency:** `checkModules()` is deprecated as of Koin 4.0. It still works and the DSL above is accurate, but Koin now recommends the simpler `verify()` function instead, which uses annotations to infer what needs to be supplied rather than a manual DSL:



```kotlin
class SomeModuleCheck {
    @Test
    fun `verify module configuration`() {
        someModule.verify(
            // list types used via parameter injection but not declared directly
            extraTypes = listOf(SomeType::class)
        )
    }
}
```



For projects using the Koin Compiler Plugin, dependency validation happens at compile time instead, and Koin's own guidance is that `verify()`/`checkModules()` tests can be removed entirely once the plugin is in place.

## Conclusion

In this topic, we delved deeper into working with Koin and studied such nuances of working with it as:

- Context isolation to prevent conflicts between dependencies.

- Scopes for binding the lifetime of one component to another.

- Modules for better component organization.

- Testing support for using dependency injection during testing.

- Checking modules for checking the dependency graph at the testing stage (now transitioning to `verify()` and compile-time validation).

All these subtleties will help you work with Koin at a more advanced level.

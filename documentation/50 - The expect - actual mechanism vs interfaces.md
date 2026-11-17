# The expect/actual mechanism vs interfaces

Previously, you've learned about the `expect`/`actual` mechanism: a common declaration in `commonMain` paired with a platform-specific implementation in each target's source set. That's not the only way to handle platform differences, though. The other common approach is a plain Kotlin interface, implemented once per platform and wired together — often through dependency injection, which is the subject of the next topic.

Both solve the same underlying problem: shared code needs to call something whose implementation has to differ per platform. Which one you reach for depends on what that "something" looks like.

## Recap: expect/actual

Let's quickly recap what we covered before. Consider the following example:



```kotlin
// commonMain
expect fun currentTimeMillis(): Long

// androidMain
actual fun currentTimeMillis(): Long = System.currentTimeMillis()

// iosMain
actual fun currentTimeMillis(): Long = NSDate().timeIntervalSince1970.toLong() * 1000
```



The compiler enforces this pairing: if a target is missing an `actual` for some `expect` declaration, the build fails for that target. There's exactly one implementation per platform, resolved at compile time, with no runtime indirection.

## The interface-based alternative

Instead of declaring `expect fun`, you declare a regular interface in `commonMain`:



```kotlin
// commonMain
interface ClockProvider {
    fun currentTimeMillis(): Long
}
```



Each platform provides its own implementation as an ordinary class:



```kotlin
// androidMain
class AndroidClockProvider : ClockProvider {
    override fun currentTimeMillis() = System.currentTimeMillis()
}

// iosMain
class IosClockProvider : ClockProvider {
    override fun currentTimeMillis() = NSDate().timeIntervalSince1970.toLong() * 1000
}
```



Shared code doesn't construct `AndroidClockProvider` or `IosClockProvider` directly — it depends only on the `ClockProvider` interface, and receives a concrete instance from the outside, usually through a constructor parameter:



```kotlin
// commonMain
class SessionManager(private val clock: ClockProvider) {
    fun sessionAgeMillis(startedAt: Long): Long = clock.currentTimeMillis() - startedAt
}
```



Nothing here enforces that every platform actually provides a `ClockProvider` — that wiring happens wherever `SessionManager` gets constructed (manually, or through a DI container, as we'll cover next).

## Comparing the two

`expect`/`actual`

Interface

Enforcement

Compiler fails the build if a target's `actual` is missing

Nothing enforces that every platform supplies an implementation

Resolution

Compile-time, no indirection

Runtime, via whatever constructs the object

Multiple implementations per platform

Not supported — one `actual` per target

Freely supported — real, fake, test double, alternate config

Swapping for tests

Requires a test source set with its own `actual`, or testing per-platform

Pass a fake implementation directly into the constructor from `commonTest`

Best fit

Small, stateless, single-purpose platform differences

Anything with multiple methods, state, or configuration

Typical boilerplate

One `expect` + one `actual` per target

An interface + one implementing class per target, plus wiring

The compiler enforcement point cuts both ways. It's a safety net — you can't forget to implement a platform — but it's also inflexible: you can't have two different `actual` values for the same target (say, a "strict" and "lenient" version of the same function), and you can't easily substitute a fake for a unit test without duplicating the expect/actual pairing in a test source set.

Interfaces flip that trade-off. You lose the compile-time guarantee that every platform is covered, but you gain the ability to have as many implementations as you want, swap them at runtime, and test shared logic by passing in a fake — no platform-specific test source sets required.

As a starting point:

- **Use **`expect`/`actual` for small, stateless platform differences — a single function, property, or type alias with no configuration and nothing you'd ever need to fake in a test. Getting a platform name, generating a UUID, or formatting a date with a native API are typical cases.

- **Use an interface** for anything that looks like a service or a dependency — network clients, database drivers, analytics trackers, anything with more than one method, internal state, or a reason to have a test double.

## Combining both

In practice, many KMP codebases use both together: an interface for the abstraction, and `expect`/`actual` only for the factory function that creates the platform-specific instance. This keeps the compiler's "every platform must provide one" guarantee for the wiring itself, while keeping the abstraction as a regular, testable interface:



```kotlin
// commonMain
interface Logger {
    fun log(message: String)
}

expect fun createLogger(): Logger

// androidMain
actual fun createLogger(): Logger = AndroidLogger()

// iosMain
actual fun createLogger(): Logger = IosLogger()
```



Shared code depends only on the `Logger` interface. Each platform is still compiler-checked to provide a `createLogger()` implementation, but tests in `commonTest` can construct a fake `Logger` directly, without touching `expect`/`actual` at all:



```kotlin
// commonTest
class FakeLogger : Logger {
    val messages = mutableListOf<String>()
    override fun log(message: String) {
        messages.add(message)
    }
}
```



## Conclusion

`expect`/`actual` and interfaces solve the same problem from different directions: one guarantees every platform is covered at compile time, the other trades that guarantee for runtime flexibility and testability. Small, single-purpose platform differences usually call for `expect`/`actual`; anything resembling a service, with state or multiple implementations, usually calls for an interface. Combining the two — an interface for the abstraction, `expect`/`actual` for the factory — is a common middle ground, and sets up naturally for dependency injection, which is where we're headed next.

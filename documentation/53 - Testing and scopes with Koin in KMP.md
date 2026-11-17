# Testing and scopes with Koin in KMP

Setting up Koin in a KMP project means adding it to `commonMain`, supplying a `platformModule`, and starting Koin once per platform, as covered earlier. Koin's scopes and testing support carry over to a multiplatform project with very little change — both are plain Koin core, with no platform-specific artifact required. This topic looks at what stays exactly the same and the few places platforms genuinely diverge.

## Testing shared code with Koin

`koin-test` is multiplatform, so `KoinTest`, `startKoin`, and `stopKoin` are usable directly from `commonTest` — the same test then runs on every target the module builds for. Because shared code depends on interfaces rather than concrete platform classes, testing it is a matter of starting Koin with fakes in place of the real implementations:



```kotlin
// commonTest
class TaskServiceTest : KoinTest {
    @Test
    fun `creates a task`() = runTest {
        startKoin {
            modules(module {
                single<Logger> { FakeLogger() }
                singleOf(::TaskService)
            })
        }

        val service = get<TaskService>()
        // ... assertions

        stopKoin()
    }
}
```



Nothing here is platform-specific — the fake `Logger` replaces whichever `actual` implementation would normally come from `platformModule`, and the test never touches Android or iOS code at all.

If a test genuinely needs something platform-specific — a platform context type, for instance — the same `expect`/`actual` pattern used for `platformModule` applies again, just scoped to the test source sets:



```kotlin
// commonTest
expect val testPlatformModule: Module

// androidTest
actual val testPlatformModule: Module = module {
    single<PlatformContext> { TestAndroidContext() }
}

// iosTest
actual val testPlatformModule: Module = module {
    single<PlatformContext> { TestIosContext() }
}
```



`checkModules()`, covered in the general DI track for catching missing dependencies before runtime, works the same way here too — there's nothing to adapt for KMP beyond running it from `commonTest` like any other test.

## Scopes across platforms

Scopes — `scope { scoped { ... } }`, `KoinScopeComponent`, `newScope()`/`createScope()`, and `close()` — are also plain Koin core. None of that mechanism is Android- or iOS-specific, so a scope declared in `commonMain` behaves identically on every target.

What differs isn't the mechanism, it's *when a scope gets closed*. On Android, `koin-android` provides lifecycle-aware helpers that tie a scope to an Activity or Fragment and close it automatically when that component is destroyed. There's no equivalent on iOS — no shared lifecycle object for Koin to hook into — so on iOS, desktop, and other native targets, you manage the scope's lifetime explicitly. A common way to do that is to wrap it in a small shared class:



```kotlin
// commonMain
class ScreenScope : KoinScopeComponent {
    override val scope: Scope by lazy { newScope(this) }
    fun close() = scope.close()
}
```



Android code can still tie a `ScreenScope` instance to an Activity or ViewModel's own lifecycle callbacks. On iOS, you call `.close()` explicitly at the point a screen goes away — for example, from SwiftUI's `.onDisappear`. The scope itself, and everything declared inside it, is shared logic; only the trigger that closes it is platform-specific.

## Conclusion

Scopes and testing don't introduce new mechanics in a KMP project — they're the same Koin core APIs covered earlier, usable directly in `commonMain` and `commonTest`. The only KMP-specific work is the same seam used for `platformModule`: `expect`/`actual` wherever a test needs a platform-specific fake, or a scope's closing trigger needs to hook into a platform's own lifecycle.

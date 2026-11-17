# Dependency injection in KMP

Our previous topic on Koin covered how Koin modules, `single`/`factory` definitions, and interface binding work in a single-platform application. Koin's DSL doesn't change in a KMP project — a `module { }` block looks exactly the same whether it lives in a JVM app or in `commonMain`. What's new is everything around it: how Koin gets added to a multiplatform module, how you supply the pieces that genuinely differ per platform, and how you start Koin once from three (or more) different entry points instead of one.

## Adding Koin to a multiplatform module

Koin publishes a BOM (Bill of Materials) that keeps every Koin artifact's version aligned, so you only specify the BOM version once:



```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation(platform("io.insert-koin:koin-bom:4.2.0"))
            implementation("io.insert-koin:koin-core")
        }
        commonTest.dependencies {
            implementation("io.insert-koin:koin-test")
        }
        androidMain.dependencies {
            implementation("io.insert-koin:koin-android")
        }
    }
}
```



`koin-core` is multiplatform and goes in `commonMain` — the same modules, `single`/`factory` definitions, and injection you already know work there unchanged. `koin-android` is Android-only and adds convenience helpers like `androidContext()` and `androidLogger()`; there's no equivalent artifact needed for iOS or desktop, since Koin core already runs there. `koin-test` is also multiplatform, so `KoinTest` and friends are usable directly from `commonTest` — more on that in the next topics.

## The platform module pattern

Earlier, we argued for a specific split: use an interface for anything with real behavior, and reserve `expect`/`actual` for small, stateless shims — ideally just the seam that wires a platform's implementation into the interface. Koin's standard multiplatform pattern is exactly that seam, expressed as a Koin module instead of a factory function:



```kotlin
// commonMain
interface Logger {
    fun log(message: String)
}

expect val platformModule: Module

// androidMain
actual val platformModule: Module = module {
    single<Logger> { AndroidLogger() }
}

// iosMain
actual val platformModule: Module = module {
    single<Logger> { IosLogger() }
}
```



Shared code depends only on `Logger`, resolved through Koin — it never references `AndroidLogger` or `IosLogger` directly. Each platform is still compiler-checked to provide a `platformModule`, exactly like any other `expect`/`actual` pair.

You compose the platform module into your app's module using `includes()`:



```kotlin
// commonMain
fun appModule() = module {
    includes(platformModule)
    singleOf(::TaskService)
}
```



## Starting Koin from each platform

`startKoin` itself doesn't change, but where you call it from does — Android, iOS, and desktop each have a different entry point, and only one of them (Android's `Application` class) is something Koin has built-in helpers for. The common approach is to wrap the call once in `commonMain`, so every platform calls the same shared function:



```kotlin
// commonMain
fun initKoin(config: KoinAppDeclaration? = null) = startKoin {
    config?.invoke(this)
    modules(appModule())
}
```



The optional `config` lambda lets a platform add its own setup — most importantly Android, which uses it to register the app context and logger:



```kotlin
// Android app module
class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin {
            androidLogger()
            androidContext(this@MyApplication)
        }
    }
}
```



iOS has no equivalent `Application` class, so you expose a small wrapper from `iosMain` and call it once, before any UI is created:



```kotlin
// iosMain
fun initKoinIos() = initKoin()
```





```kotlin
// iOS entry point
DIHelperKt.initKoinIos()
```



For a desktop or server target, call `initKoin()` directly at the top of `main()` — no wrapper needed, since there's no platform-specific configuration to add.

Whichever platform calls it, `initKoin` needs to run exactly once per process, before anything tries to resolve a dependency. Some teams take this a step further and move the Android `Application` subclass itself into the shared module's `androidMain` source set, so the only code left in each platform's own app module is a one-line call — a matter of preference rather than a requirement.

## Conclusion

Setting up Koin in a KMP project uses the exact same DSL from the general DI track — modules, `single`/`factory`, interface binding — with two additions: a `platformModule` supplied per platform through `expect`/`actual`, and a shared `initKoin` function called once from each platform's own entry point. In the next topics, we'll look at what changes, and what doesn't, when you bring Koin's scopes and testing support into a multiplatform project.

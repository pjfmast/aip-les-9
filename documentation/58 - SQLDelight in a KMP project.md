# SQLDelight in a KMP project

Writing queries in a `.sq` file, and consuming them synchronously, asynchronously, or as a reactive `Flow`, all work the same way in a KMP project as already covered. What's missing so far is the one piece that's genuinely different: a `SqlDriver`. The driver is what actually talks to SQLite, and unlike the generated queries themselves, it isn't portable across platforms — `JdbcSqliteDriver` only exists on the JVM. Getting a real KMP module working means supplying the right driver on each target.

## Driver dependencies per platform

The SQLDelight Gradle plugin and its `sqldelight {}` configuration block don't change for a multiplatform module — only the dependencies do, and they now differ per source set:



```kotlin
kotlin {
    sourceSets {
        commonMain.dependencies {
            implementation("app.cash.sqldelight:runtime:2.0.2")
            implementation("app.cash.sqldelight:coroutines-extensions:2.0.2")
        }
        androidMain.dependencies {
            implementation("app.cash.sqldelight:android-driver:2.0.2")
        }
        iosMain.dependencies {
            implementation("app.cash.sqldelight:native-driver:2.0.2")
        }
        jvmMain.dependencies {
            implementation("app.cash.sqldelight:sqlite-driver:2.0.2")
        }
    }
}
```



The generated `Database` class and query types still live in `commonMain`, exactly as before — only the artifact that supplies a working `SqlDriver` changes per platform.

## Supplying the driver per platform

Constructing a driver needs different information on each platform: `AndroidSqliteDriver` needs an Android `Context`, `NativeSqliteDriver` needs nothing extra, and the `JdbcSqliteDriver` already covered works unchanged for a JVM or desktop target. Since that varies by platform, construction is wrapped in an `expect`/`actual` factory:



```kotlin
// commonMain
expect class DriverFactory {
    fun createDriver(): SqlDriver
}

// androidMain
actual class DriverFactory(private val context: Context) {
    actual fun createDriver(): SqlDriver =
        AndroidSqliteDriver(Database.Schema, context, "tasks.db")
}

// iosMain
actual class DriverFactory {
    actual fun createDriver(): SqlDriver =
        NativeSqliteDriver(Database.Schema, "tasks.db")
}
```



This is the same shape as the interface-plus-`expect`/`actual` pattern covered earlier — the interface being abstracted over is `SqlDriver` itself, supplied by the library rather than one you define. Each platform is still compiler-checked to provide a `DriverFactory`, and shared code never has to know which concrete driver is behind it.

## Wiring the driver through DI

`DriverFactory` slots into the same `platformModule` pattern already used for other platform-specific dependencies. Android needs the app context to construct it; iOS doesn't:



```kotlin
// androidMain
actual val platformModule: Module = module {
    single { DriverFactory(androidContext()) }
}

// iosMain
actual val platformModule: Module = module {
    single { DriverFactory() }
}
```



From there, the actual `SqlDriver` and `Database` instance are built once, in `commonMain`, using whichever `DriverFactory` Koin resolved:



```kotlin
// commonMain
fun appModule() = module {
    includes(platformModule)
    single<SqlDriver> { get<DriverFactory>().createDriver() }
    single { Database(get()) }
}
```



A `Database` obtained this way is exactly what a repository would take as a constructor dependency, rather than talking to a driver directly.

## Querying from commonMain

With `Database` available as an ordinary `commonMain` singleton, the sync, async, and reactive query styles already covered apply exactly as before — nothing about them is platform-specific. `coroutines-extensions` is itself a multiplatform artifact, so a query's `.asFlow().mapToList(Dispatchers.IO)` can be collected directly from shared code, regardless of which platform it eventually runs on:



```kotlin
// commonMain, inside a repository built on Database
fun observeTasks(): Flow<List<Task>> =
    database.taskQueries.selectAll().asFlow().mapToList(Dispatchers.IO)
```



Nothing here needed an `expect`/`actual` pair of its own — the platform-specific work was entirely contained in getting the driver constructed.

## Conclusion

Running SQLDelight in a multiplatform module needs exactly one new piece beyond what's already covered: a way to construct a platform-appropriate `SqlDriver`, following the same `expect`/`actual`-plus-DI pattern used throughout this section. Once that's wired in, the sync, async, and reactive query APIs already covered work unchanged in shared code, with no further platform-specific work required.

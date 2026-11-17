# Key-value storage (Multiplatform Settings)

Not everything worth persisting belongs in a table. An auth token, a "has seen onboarding" flag, a theme preference — each is a single, independent value, and reaching for a database to store one boolean is more machinery than the problem needs. `multiplatform-settings` fills that gap: a KMP library that wraps each platform's native key-value store — `SharedPreferences` on Android, `NSUserDefaults` on iOS, `java.util.prefs` on the JVM, and more — behind one shared `Settings` interface.

## The Settings interface

`Settings` exposes typed put/get pairs, with defaults and nullable variants, plus basic housekeeping:



```kotlin
interface Settings {
    fun putString(key: String, value: String)
    fun getString(key: String, defaultValue: String = ""): String
    fun getStringOrNull(key: String): String?
    // the same shape repeats for Int, Long, Float, Double, and Boolean

    fun hasKey(key: String): Boolean
    fun remove(key: String)
    fun clear()
}
```



Using it looks like this, wherever a `Settings` instance is available:



```kotlin
settings.putBoolean("has_seen_onboarding", true)
val hasSeenOnboarding = settings.getBoolean("has_seen_onboarding", false)
```



Anything relational — anything with more than one related field per record, or that needs querying — still belongs in the local database covered earlier. This is for the small, standalone values that don't.

## Getting a Settings instance per platform

Each platform wraps its own native store behind the same interface, using a class that takes the platform's own storage object as a constructor parameter:



```kotlin
// androidMain
val settings: ObservableSettings = SharedPreferencesSettings(
    context.getSharedPreferences("app_settings", Context.MODE_PRIVATE)
)

// iosMain
val settings: ObservableSettings = NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
```



Both return `ObservableSettings` rather than plain `Settings` — both platform implementations already support observing changes with no extra wrapping, which matters in a moment.

Wiring this in follows the same `expect`/`actual`-plus-DI shape already used for the local database driver:



```kotlin
// commonMain
expect class SettingsFactory {
    fun create(): ObservableSettings
}

// androidMain
actual class SettingsFactory(private val context: Context) {
    actual fun create(): ObservableSettings =
        SharedPreferencesSettings(context.getSharedPreferences("app_settings", Context.MODE_PRIVATE))
}

// iosMain
actual class SettingsFactory {
    actual fun create(): ObservableSettings =
        NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
}
```





```kotlin
// androidMain
actual val platformModule: Module = module {
    single { SettingsFactory(androidContext()) }
}

// iosMain
actual val platformModule: Module = module {
    single { SettingsFactory() }
}

// commonMain
fun appModule() = module {
    includes(platformModule)
    single<ObservableSettings> { get<SettingsFactory>().create() }
}
```



The library's own documentation recommends exactly this — wiring the platform delegate through whatever DI setup a project already has, with `expect`/`actual` alone as a fallback only for projects without one. A separate `multiplatform-settings-no-arg` artifact also exists if you'd rather skip writing this factory yourself; it ships a ready-made one that obtains the Android context automatically. Either way, there's no separate driver artifact to add per platform the way the local database needed — `SharedPreferencesSettings` and `NSUserDefaultsSettings` both ship inside the one core dependency, and each target simply picks up its own.

## Observing changes as a Flow

The `multiplatform-settings-coroutines` module adds `Flow`-returning extensions on `ObservableSettings`:



```kotlin
val themeFlow: Flow<String> = settings.getStringFlow("theme", "system")
```



That's the same kind of `Flow` already covered for observing changing values in Compose — collecting it drives recomposition the same way:



```kotlin
@Composable
fun ThemeAwareRoot() {
    val theme by themeFlow.collectAsState(initial = "system")
    // ...
}
```



Whenever the stored value changes — on any platform — the `Flow` emits and the UI recomposes, with no polling and no manual refresh logic. The only difference from other `Flow`-driven state already covered is where the values are coming from: persisted storage instead of in-memory business logic.

## Gradle setup



```kotlin
commonMain.dependencies {
    implementation("com.russhwolf:multiplatform-settings:1.3.0")
    implementation("com.russhwolf:multiplatform-settings-coroutines:1.3.0")
}
```



A `multiplatform-settings-test` artifact is also available, providing `MapSettings` — an in-memory `Settings` implementation meant specifically for tests, so a fake doesn't need to be written by hand for `commonTest` the way one might for a custom interface. A `multiplatform-settings-datastore` variant also exists, backed by Jetpack DataStore instead of `SharedPreferences`/`NSUserDefaults` directly, for projects that have already standardized on DataStore — not covered further here, but worth knowing it's there.

## Conclusion

`multiplatform-settings` gives shared code one interface for small, independent persisted values, wrapping each platform's native key-value store behind the same `expect`/`actual`-plus-DI pattern already used elsewhere — with one addition worth remembering: `ObservableSettings` turns any stored value into a `Flow`, so persisted state can drive Compose recomposition exactly the way in-memory state already does.

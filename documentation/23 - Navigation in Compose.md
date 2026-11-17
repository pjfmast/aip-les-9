# Navigation in Compose

Most Android apps today are not limited to just one screen. They usually consist of multiple screens that users move between by swiping, tapping buttons, or selecting options from menus. With the introduction of navigation in **Android Architecture Components**, implementing navigation in **Android** apps has become more organized and straightforward. This component now supports navigation in apps built with **Jetpack Compose**. This topic will give you a basic understanding of navigation in Compose. We'll start by learning how to use the navigation component in Jetpack Compose and then cover concepts like navigation graphs, the navigation back stack, type-safe routes, the `NavHostController` class, and the `NavHost` composable. This knowledge will enable us to implement the Navigation Component in a demonstration project. Finally, we'll explore how to pass arguments when moving between screens.

## Understanding Navigation In Jetpack Compose

Every app starts with a main screen that users see first. From here, users typically perform actions that lead to other screens. In Jetpack Compose, these screens are different composables within the project. For instance, a mail app's main screen might show a list of current messages. Users can navigate from there to the Accounts Screen, which displays a list of accounts, or to the detail screen for comprehensive email information. On this detail screen, users can choose to respond, taking them to the Reply Screen. The accounts screen might also let users navigate to screens where they can add new accounts or remove existing ones. The app's navigation flow might look like this:

![app's navigation representation](https://ucarecdn.com/91861700-600a-4d2d-9729-67ca4a1412ac/)

In Jetpack Compose, you navigate between screens using the **Navigation Component** from the **Jetpack Library**. This component helps with navigation, from simple button clicks to complex patterns like hierarchical navigation and navigation drawers.

Each screen in the app is a **destination**, usually a composable. In Jetpack Compose, with the Navigation component, a `NavHost` and a `NavController` manage navigation. The `NavHost` displays the correct composable based on the `NavController`'s destination.

The `NavController` maintains a navigation stack, tracking the history. It manages a back stack of destinations, allowing users to navigate backward through the app's hierarchy by pressing the back button.

When you navigate to a new screen, the `NavController` adds the new destination to the stack, and when you navigate back, it removes the top destination.

Here's what the navigation stack might look like if a user navigated from the Main Screen to the Accounts Screen and then to the Add Account Screen in our earlier example:

![bh](https://ucarecdn.com/976a4caa-b88e-4c9c-b393-c8a2be4ca796/)

## Implement Navigation Component

To begin using navigation in Jetpack Compose, add the navigation-compose dependency to your app's `build.gradle.kts` file. We're also going to use type-safe routes (available since Navigation 2.8.0), which represent each destination as a Kotlin type instead of a hard-coded string. This means we need the Kotlin Serialization plugin as well:



```kotlin
// build.gradle.kts (Module :app)
plugins {
    id("org.jetbrains.kotlin.plugin.serialization") version "2.0.21"
}

dependencies {
    val navVersion = "2.9.8"

    implementation("androidx.navigation:navigation-compose:$navVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3")
}
```





```kotlin
// build.gradle (Module :app)
plugins {
    id 'org.jetbrains.kotlin.plugin.serialization' version '2.0.21'
}

def navVersion = "2.9.8"

dependencies {
    implementation "androidx.navigation:navigation-compose:$navVersion"
    implementation "org.jetbrains.kotlinx:kotlinx-serialization-json:1.7.3"
}
```



Always check the official Navigation release notes for the current stable version before starting a new project, since this library is updated frequently.

- **Declare a navigation controller**

The next step is to declare the `navController`. This is an instance of the `NavHostController` class. You can use this object to navigate between screens by calling the `navigate` method to navigate to another destination. You can obtain the `NavHostController` by calling `rememberNavController` from a composable function. This creates and remembers a `NavHostController` that survives configuration changes.



```kotlin
val navController = rememberNavController()
```



> **Note:**

`NavHostController` is a subclass of the `NavController` class that provides additional functionalities for use with a `NavHost` composable.

- **Declare a navigation host**

The navigation host (`NavHost`) is a special component that acts as a container and displays the current destination of the graph. The `NavHost` links the `navController` with a **Navigation Graph** that specifies the composable destinations that you should be able to navigate between. As you navigate between composables, the content of the `NavHost` is automatically recomposed.

With type-safe routes, the starting destination is a Kotlin object rather than a string:



```kotlin
NavHost(
    navController = navController, // previously created via rememberNavController()
    startDestination = MainScreen  // this is the route object for the main screen composable
) {
    /* navigation graph destinations */
}
```



When it is called, `NavHost` must be passed a `NavHostController` instance, the starting destination of the graph, and a lambda that defines the builder for the navigation graph.

> **Warning:**

The `NavController` is always associated with a single `NavHost` composable.

- **Define your routes**

Before Navigation 2.8, each composable destination was identified by a plain string (for example `"main_screen"`), and it was easy to typo a route name or pass an argument of the wrong type without the compiler noticing. Type-safe routes fix this: each destination is represented by a `@Serializable` Kotlin object or data class instead of a string.

- Use an **object** for a destination that takes no arguments.

- Use a **data class** for a destination that takes arguments — its properties become the navigation arguments, with real Kotlin types.



```kotlin
import kotlinx.serialization.Serializable

@Serializable
object MainScreen

@Serializable
object AccountsScreen
```



Because these are just serializable Kotlin types, it's still good practice to define them together in one file so every destination in your app is easy to find in one place — but you no longer need a sealed class of string constants to achieve that; the types themselves are the single source of truth.

- **Add destinations to the navigation graph**

To add your navigation destinations, call the composable extension function for each destination, using the route type as a type parameter:



```kotlin
NavHost(
    navController = navController,
    startDestination = MainScreen
) {
    composable<MainScreen> {
        MainScreen() /* This defines the UI to be displayed when we navigate to MainScreen */
    }
    composable<AccountsScreen> {
        AccountsScreen()
    }
}
```



In this example, our navigation graph consists of two destinations: the Main Screen and the Accounts Screen. Because `composable` and `composable` take the destination's actual type, the compiler can check that you only ever navigate to a destination that really exists in the graph — this is the main advantage type-safe routes have over plain strings.

- **Navigate to destinations**

To demonstrate simple navigation between two screens, our `MainScreen` displays a button that should trigger navigation to the `AccountsScreen` when clicked.



```kotlin
@Composable
fun MainScreen() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /* other fields */
        Button(onClick = { /* navigate */ }) {
            Text(text = "Go to accounts screen")
        }
    }
}
```



The primary mechanism for triggering navigation is via calls to the `navigate` method of the `NavHostController` instance, passing an instance of the destination's route type. This requires access to the `navController` variable created previously and associated with our navigation graph. While we can just pass the `navController` as a parameter to our `MainScreen` composable, this approach is not ideal for architecting our app since it makes it hard to test in isolation, preview, and reuse our composables.

The recommended way is to pass a callback that will be executed each time the button is clicked.



```kotlin
@Composable
fun MainScreen(
    onNavigate: () -> Unit
) {
    Column(
        /* column parameters */
    ) {
        /* other fields */

        Button(onClick = { onNavigate() }) { 
            Text(text = "Go to accounts screen")
        }
    }
}
```



In the `NavHost`, update our code by defining a callback. This callback invokes the `navigate` function, passing the `AccountsScreen` object as the destination:



```kotlin
composable<MainScreen> {
    MainScreen {
        navController.navigate(AccountsScreen)
    }
}
```



Now, if we run the application and click the button on the main screen, we can see that the navigation is performed successfully:

![navigation from main screen to accounts screen](https://ucarecdn.com/aaf948c6-3028-4bac-ad79-aeaf2e68e74b/)

To navigate back from the Accounts Screen, users can press the back button located at the bottom of the screen. The same behavior can be achieved programmatically by invoking the `popBackStack` function on the navigation controller instance. This function removes the top destination from the back stack, effectively navigating back to the previous screen.

We can associate this action with the `Go Back` button in the Accounts screen by passing a callback to the Accounts Screen, similar to how we've handled navigation from the Main Screen:



```kotlin
@Composable
fun AccountsScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        /* Column parameters */
    ) {
        /* other fields */

        Button(onClick = { onNavigateBack() }) {
            Text(text = "Go Back")
        }
    }
}
```



The navigation graph can be modified accordingly to invoke the `popBackStack` function:



```kotlin
composable<AccountsScreen> {
    AccountsScreen {
        navController.popBackStack()
    }
}
```



## Navigating with arguments

As mentioned before, the main screen displays a list of recent emails. When you click on an email, you navigate to the Detail Screen to show the message body. Typically, you fetch the email details from the local cache or the network. However, before fetching the details, you need a reference to the specific email that was clicked. You can pass this reference to the Detail Screen as a navigation argument. The Detail Screen then reads this argument from its route object and uses it to fetch the email details.

> **Note:**

In general, you should pass only the minimal amount of data between destinations. In our example, rather than passing the email object itself, we pass just a reference to it, because the total space reserved for that purpose is limited on Android.

With type-safe routes, a destination that needs arguments is simply a `@Serializable` data class whose properties are the arguments — there's no separate step to declare argument names or types, and no argument placeholders to build into a route string:



```kotlin
@Serializable
data class DetailScreenRoute(val emailId: Int)
```



This one declaration replaces what previously required a string route with a placeholder (`"detail_screen/{emailId}"`) plus a separate `NamedNavArgument` built with `navArgument("emailId") { type = NavType.IntType }`. Because `emailId` is a typed Kotlin `Int` property, the compiler enforces that you can never navigate to `DetailScreenRoute` without providing a valid `Int`.

Add it to the navigation graph the same way as any other destination:



```kotlin
composable<DetailScreenRoute> { backStackEntry ->
    val route: DetailScreenRoute = backStackEntry.toRoute()
    DetailScreen(emailId = route.emailId)
}
```



The `toRoute()` extension function reconstructs the `DetailScreenRoute` instance (arguments included) from the `NavBackStackEntry`, so there's no need to manually pull values out of a `Bundle` by key.

For now, the DetailScreen will be a simple composable that takes the emailId argument and displays it inside a Text composable:



```kotlin
@Composable
fun DetailScreen(
    emailId: Int
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = emailId.toString())
    }
}
```



The final step is to pass a value for the argument when calling the navigate method from the main screen. You do this by constructing an instance of the route with the value filled in:



```kotlin
composable<MainScreen> {
    MainScreen {
        navController.navigate(DetailScreenRoute(emailId = 1234))
    }
}
```



Because `DetailScreenRoute` is a typed data class, passing the wrong argument type (a `String` instead of an `Int`, for example) is now a compile-time error instead of a bug you'd only discover at runtime.

## Conclusion

We've explored how the Navigation Component in Jetpack Compose streamlines the process of creating destinations through composable functions that fit into navigation graphs. You create a navigation graph with the `NavHost` function, linking each destination to a type-safe route — a `@Serializable` object or data class rather than a hard-coded string. This graph creation relies on the `NavHostController`, which facilitates navigation with the navigate method and keeps track of the back stack history. You define destinations in the navigation graph using the composable function with the route's type as a type parameter, which lets the compiler verify both the destination and its arguments for you.

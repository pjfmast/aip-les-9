# Hello, Compose!

Now it's time to take a closer look at Jetpack Compose and learn the details about what Composable functions are and how to create them. In addition, we will learn how to preview Composables using the `@Preview` annotation, and consider its main features. To do this, we will create a project with Jetpack Compose, perhaps the first in your life.

## Project creation

Before we start, it's worth noting that you don't have to create a new project to start using Compose, even if you're already using the View system with XML format Layouts in your app. You can use both if you wish. However, you'll likely want to gradually move all UI Layouts and their elements into Composables step by step or create everything from scratch using Compose.

> **Note:**

To start using Jetpack Compose in an existing project, you only need to add a few dependencies and some build configurations. For more detailed information, you can read the official documentation.

In this topic, we'll look at the option of creating a project from scratch with a preconfigured Jetpack Compose template in Android Studio. Let's get down to business!

When creating a new project (*File > New > New Project*), you'll see the **Empty Activity** template. This is now the single, unified starting point for a Compose project in current Android Studio releases — it comes preconfigured with Compose out of the box. The separate **Empty Compose Activity** template name from older Android Studio versions has since been folded into it, so don't worry if you don't see it listed separately.

![Preinstalled project templates](https://ucarecdn.com/4a93bc64-53e6-42ce-bd6f-473b74cf524c/-/crop/1154x841/30,38/-/preview/)

*Preinstalled project templates*

The next step is to choose a name for your application and a minimum SDK.

Don't confuse Compose's own requirement here with what your project actually gets built against. Jetpack Compose itself only needs a `minSdk` of Android API level 21 or higher — but regardless of the `minSdk` you pick, Android Studio's template also sets a `compileSdk` and `targetSdk` to the latest Android API level supported by your installed IDE version, and those values require a matching Android Gradle Plugin (AGP) and Gradle version. If you're running an older or heavily customized Android Studio install, a mismatch between `compileSdk`, AGP, and Gradle — not your `minSdk` choice — is the most common reason a fresh **Empty Activity** project fails to sync or build.

To avoid this, let Android Studio manage the Gradle wrapper and AGP version that ships with your installed release rather than picking versions manually, and keep Android Studio itself reasonably up to date. It's also worth knowing that Google Play's publishing requirements move independently of Compose's minimum: new apps and updates currently must target a recent Android API level to be published on Google Play, and that bar rises roughly once a year. So `minSdk 21` describes the *lowest device* your app could theoretically install on — it's not a number to aim for when it comes to actually publishing an app.

![Project Configuration](https://ucarecdn.com/efd08c89-ff03-4953-a966-b32b81b8f2c4/)

*Project Configuration*

This is what the `MainActivity.kt` file of the project template with Compose framework will look like:



```kotlin
class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       setContent {
           HyperskillTheme {
               // A surface container using the 'background' color from the theme
               Surface(
                   modifier = Modifier.fillMaxSize(),
                   color = MaterialTheme.colorScheme.background
               ) {
                   Greeting("Android")
               }
           }
       }
   }
}

@Composable
fun Greeting(name: String) {
   Text(text = "Hello, $name!")
}

@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
   HyperskillTheme {
       Greeting("Android")
   }
}
```



But let's first look at the `Greeting` Composable function and understand how it works and what it does.

## Composables

Composable functions, or simply composables, are regular Kotlin functions annotated with the `@Composable` annotation. The specified annotation informs the compiler that the given function describes a certain part of the application's UI. Like regular functions, composables can take data as arguments, including other composables, handle them in a specific way, and pass the specified data to internal composables.

> **Note:**

Unlike the convention for naming regular Kotlin functions, which use the camelCase style, Composable functions should be named starting with a capital letter in the PascalCase style.

Consider the already created function `Greeting` of the default layout of our project.



```kotlin
@Composable
fun Greeting(name: String) {
   Text(text = "Hello, $name!")
}
```



This function is composable because it's annotated with the corresponding annotation. It takes a `String` value as its single parameter `name`. The body of this function calls another function, `Text`, a default composable of the Jetpack Compose library, but we'll look at it in more detail in the topics to come. In turn, the Composable function `Text` takes the `String` template `"Hello, $name"` as its `text` parameter and is responsible for displaying this text element of the UI on the screen.

So, in the future, in order to display a greeting message on the screen, we don't need to recreate a greeting each time separately for each person. You can simply call the composable `Greeting` with the required name as many times as you need. As you can see in the example, the `Greeting` composable function is passed as a parameter to the higher-order function, `Surface`, which in turn is also composable. It is a surface container with a background color set by the application's theme. The theme name depends on the application name, in our case, it's a `HyperskillTheme`. For now, we will not dive deep into how it works and why. We will return to this issue in topics dedicated to Theming. In this case, `HyperskillTheme` is the root composable object in the UI hierarchy of this app's components.

![Graph of Composables](https://ucarecdn.com/a31edeb4-6aca-4eb0-9ed0-2bb09f3af6e2/-/crop/240x113/0,54/-/preview/)

*Graph of Composables*

Let's try to remove the annotation and see what happens. As you can see, the compiler immediately reports an error: "Functions which invoke Composable functions must be marked with the `@Composable` annotation". At the same time, we can freely use regular Kotlin functions inside composable ones. Also, nothing prevents you from using composable within conditions and loops. For example, to display a nice-looking greeting, if the `name` parameter is empty, we can write something like this:



```kotlin
@Composable
fun Greeting(name: String) {
    if (name.isNotEmpty()) {
        Text(text = "Hello, $name")
    } else {
        Text(text = "Hello, Compose!")
    }
}
```



## Preview

For the convenience of building a UI, an important feature is the ability to see the result of the written code quickly. Android Studio has the composable preview tool to preview the UI without having to run the app in an emulator or on a device. You have two ways to preview composables. The first one is applicable if you're not worried about  the preview matching the theme options. It should not make any difference if the Composable has no parameters or only has parameters with default arguments. In such a case, you can just add a `@Preview` annotation to this composable. Otherwise, there is another way. You can create an additional Composable function and annotate it with the `@Preview` annotation, similar to how it is done in `DefaultPreview`. The body of this function should contain the theme used by your application, in our case, it's `HyperskillTheme`. In turn, `HyperskillTheme` takes the composables that you want to see in the preview as a parameter:



```kotlin
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
   HyperskillTheme {
       Greeting("")
   }
}
```



Also, as you can see, the `@Preview` annotation has several options that allow you to customize what the preview should look like. We'll consider all of them in the topic on Preview functions.

To show or hide the preview panel, click on one of the buttons

![preview panel buttons](https://ucarecdn.com/4d7f50c7-6913-4699-aa61-06abbe189dde/-/crop/168x30/207,0/-/preview/)

, which are located on the top right side of the Android Studio window. When making changes to composables, you may need to rebuild the preview using the corresponding `Build & Refresh` button (`Ctrl+Shift+F5`).

![Preview panel](https://ucarecdn.com/80e15a8b-4d81-49eb-bb46-6fed86f5d3ec/)

## Conclusion

Composable functions are the main UI building blocks in the Jetpack Compose framework. All composables must be marked with the corresponding annotation, due to which the compiler can recognize and process them in a certain way. In addition, you should always keep in mind that composables can only be called within other composables, forming more complex UI components.

Also, an integral part of your work will be the use of Preview functions, with the help of which you can quickly visualize your code without running your application. Enjoy it!

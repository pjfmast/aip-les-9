# Jetpack Compose project structure

So, you are just getting started in Jetpack Compose, and you have created your first project. Let's take our first steps towards your mastery in Compose and figure out what the project structure looks like! In this topic, we will take a look at the key components of a Jetpack Compose project.

## Project structure

When you run a project in Android Studio, files and directories appear in the Project panel on the left side of IDE. Each time you start Android Studio, it displays your project files in the **Android** view. The IDE hides some folders for a more convenient view of the folder hierarchy. If we open the application folder, you will see a lot more files and directories.

Our first task is to change the visual representation from **Android** to **Project** view by clicking on it

![Android Project View Button](https://ucarecdn.com/1a278b75-8af4-4abe-83d4-868e39ebd1c3/)

to show all available project files, but you can use any view that's convenient for you.

![Jetpack Compose project structure](https://ucarecdn.com/d2cf3593-287b-473f-a081-22f67b2a3433/)

*Jetpack Compose project structure*

There are two main files in your Android project: `AndroidManifest.xml` and `MainActivity.kt`. Let's talk more about them.

## AndroidManifest file

The manifest file describes the essential information about your app through the eXtensible Markup Language (XML). For now, we won't go into details about how it works — we will talk about this in a separate topic dedicated to `AndroidManifest` File. We'll just look at individual elements of this file to have a general idea of its purpose.

Only in this file, we can change the application icon and its label with attributes called `icon` and `label`. These attributes specify a small icon and a text label responsible for what the user will see in the list of applications.



```xml
<application
   android:icon="@mipmap/ic_launcher"
   android:label="@string/app_name"
   ...
</application>
```



During project setup, Android Studio offers you to create a project with a single empty activity — the `MainActivity` class. `AndroidManifest.xml` should refer to the `MainActivity` class, which is the entry point to the user interface of your application:



```xml
<activity android:name=".MainActivity">
   <intent-filter>
      <action android:name="android.intent.action.MAIN" />

      <category android:name="android.intent.category.LAUNCHER" />
   </intent-filter>
</activity>
```



Note that any Activity in your app must be declared in the `` element of the manifest file.

## The MainActivity class

An activity is a kind of foundation that takes on Composable functions — the building blocks of Android applications UI. Jetpack Compose is designed to be used in single-activity applications. You can still use multiple activities, for example, when using Jetpack Compose in an existing application with XML layouts. But in this case, data transfer between activities falls on your shoulders.

The activity we'll focus on here is called `MainActivity` because it contains everything the user sees on the screen after launching the app. The Android Studio creates the `MainActivity.kt` file automatically. Let's take a peek inside the `MainActivity` class and see what it is made of:



```kotlin
class MainActivity : ComponentActivity() {
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)
       setContent {
           HyperskillTheme {
               Text("Hello, Compose!")
           }
       }
   }
}
```



In our case, it is not a very large class that inherits from `ComponentActivity`. We take a class from the activity framework and create our class that inherits from it. Then, we have a method `onCreate()`, which is called when the activity is first created. It's an entry point to our screen. Inside it, you can see the method `setContent` that composes the given Composable functions into the current activity to build the UI. Take a look at the code snippet below:



```kotlin
setContent {
    HyperskillTheme {
        Text("Hello, Compose!")
    }
}
```



`setContent` is a higher-order extension function of the `ComponentActivity` class that can take Composable functions as an argument of the `content` parameter. These Composable functions will become the root elements in the hierarchy of UI elements.

## Theme files

Next to the `MainActivity.kt` of empty activity template, by default, there is a `ui` package, inside of which you can find another one — `theme`. This package contains four files: `Color.kt`, `Shape.kt`, and `Type.kt` that contain values used by your app's theme defined in the last one, `Theme.kt`. Using a theme, in our case `HyperskillTheme`, Composable functions can inherit the styles defined by that theme, ensuring consistency in the application's appearance. We will dive deeper into how to work with themes in the relevant topic a little later. For now, simply take a look at the code snippet below and compare it to the result.



```kotlin
@Preview
@Composable
fun PreviewText() {
    HyperskillTheme {
        Surface(
            shape = MaterialTheme.shapes.small,
            color = MaterialTheme.colorScheme.primary
        ) {
            Text(
                text = "Hello, Compose!",
                color = MaterialTheme.colorScheme.onPrimary,
                style = MaterialTheme.typography.titleLarge,
            )
        }
    }
}
```



![Example of composable with theme colors](https://ucarecdn.com/1d300440-a3c8-4abb-9aff-f26d0ba6490a/)

## Resources

![Resources package](https://ucarecdn.com/fb9e2c55-c01f-4348-b505-ba9cbbadd80c/)

*Resources package*

Next to the `AndroidManifest.xml` is the `res` package that contains the resources of our application. They can be strings, images, icons, sounds, layouts (if you are using XML layouts), and so on. Each of them must be located in a corresponding folder with the exact pre-defined name. You can't create a new package inside the `res` folder and use resources files without making some tweaks to the build system, except for some of the default ones. We'll look at the resources and how to work with them in more detail in the corresponding topic on Resources.

## Conclusion

In this topic, we examined the structure of the Jetpack Compose project, which has certain differences from the project that uses XML layouts. However, the purpose of the classes and methods has not changed:

- `AndroidManifest.xml` contains information about an entry point of our application — the main activity.

- `MainActivity` is a class from which the application starts by default.

- `setContent` is an extension function of the `MainActivity` class that takes on Composable functions and composes them into the activity to build a UI.

- Theme files are used to ensure consistency in the application's appearance.

- The `res` package contains resource files of the application.

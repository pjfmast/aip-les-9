# Recomposition

The declarative approach to creating a user interface allows us, as developers, not to worry about changing the content or visibility of certain UI elements when we receive updated data. Our task is to describe how the user interface should look when certain conditions occur.

## Recomposition in essence

**Recomposition** is a process of re-calling composable functions, the state of which has been changed, to update the composition and redraw the UI according to the new data. Now, let's go in order. A **state **is any value that can be changed at any point in time during the execution of an application. **Composition** is what we see on the screen at a certain point in time.

We can say that the composables are present in the composition if they have been called during the initial composition or in recompositions. After that, the composition can be recomposed 0 or more times. And in the end, the composables that were present in the composition leave it, for example, if the user has moved to another screen in the application.

![Lifecycle of composables](https://ucarecdn.com/53547685-1f1f-4155-bce9-ccaf05321133/)

*Lifecycle of composables*

The composition can be represented as a tree-like structure of composables describing UIs. The same function called multiple times in a code will take its place in the composition tree as a separate instance.



```kotlin
Surface {
    Column {
        repeat(3) {
            Text("Repetition is the secret of perfection")
        } 
        Text("- Dr Maria Montessori.")
    }
}
```



![Visual representation of a composition](https://ucarecdn.com/92db0736-e926-474b-a3e5-445e87495141/)

*Visual representation of a composition*

Unlike the imperative approach, you do not need to do anything to change the data on the screen, just create the appropriate conditions. Jetpack Compose does the rest for you automatically. You just need to understand the rules of recomposition, and then everything will fall into place.

Well, let's go back to our composable `TextField` example.



```kotlin
var login = ""

@Composable
fun LoginField() {
    TextField(
        value = login,
        onValueChange = { input ->
            login = input
        }
    )
}
```



If you remember, in the example above, when you try to enter text into the text field, nothing changes, and the field remains empty. This is because Compose does not know that the state has changed and therefore doesn’t consider it necessary to call a recomposition.

In order to track state changes by Compose, we need to put our value inside a `State` object. In our case, it is `MutableState`, which is inherited from the `State` interface. In this case, when the state changes, Compose will be notified and automatically trigger a recomposition to update the UI and display the actual data.



```kotlin
val login = mutableStateOf("")
```



## Intelligent recomposition

The process of recomposing and redrawing the entire UI can be very computationally expensive and energy-consuming. Imagine an application that displays the current time in milliseconds, and every millisecond would have to redraw the entire UI from scratch. In this case, the load on the processor would be unreasonably high, and your device's battery life would quickly drain.

Thanks to intelligent recomposition, this problem has been solved. During recomposition, Compose re-calls only those composable functions the state of which has changed or may have changed, skipping all other functions. We can see this in action with the following example:



```kotlin
val recompositionCount = mutableStateOf(0)

@Composable
fun CustomComposable() {
    Log.d("Recomposition", "CustomComposable")

    Column {
        Log.d("Recomposition", "Column")

        Surface {
            Log.d("Recomposition", "Surface")

            Text(text = "Recomposed ${recompositionCount.value} times.")
        }

        Button(onClick = { recompositionCount.value++ }) {
            Log.d("Recomposition", "Button")

            Text(text = "Tap to recompose")
        } 

    }
}
```



And the output is the following:

![Intelligent recomposition](https://ucarecdn.com/47c09ace-055f-4f37-baad-9551f45aa8dc/)

Now let's just replace one line `Log.d("Recomposition", "CustomComposable")` with

`Log.d("Recomposition", "CustomComposable recompositions: ${recompositionCount.value}")` and take a look at the result:

![Intelligent recomposition (2)](https://ucarecdn.com/2908f105-212c-4c18-807f-1f121833f642/)

Thus, by accessing the state variable directly within the body of the `CustomComposable`, this function becomes state-dependent, even though it does not affect the screen image in any way. However, changing this state now will cause recomposition of the `CustomComposable`, `Column`, and `Surface`. Everything is clear enough with the first and last composable, but the reason why `Column` was called may raise questions. The thing is that some composables can trigger recomposition of the parent composable when the state of their child composables changes. At the same time, being child composables themselves, they will also be called when their parent composable is called during recomposition, regardless of any changes to their own state. These are such functions as `Column`, `Row`, `Box`, and others. But you don't need to memorize them, just remember that this is how it is.

This example demonstrates and provides a clear understanding of how recomposition may work.

As for the case where only new composable functions were added to the composition or, conversely, were removed from the composition when the state was changed, intelligent recomposition will still work. Compose can identify composables that were called in the previous and current recompositions and will only call those that weren't called before and skip composables the state of which hasn't changed, as shown in the example below:



```kotlin
val isLoginError = mutableStateOf(false)
val isPasswordError = mutableStateOf(false)

@Composable
fun LoginScreen() {

    LoginTextField()

    if (isLoginError) { 
        LoginErrorText()
    }

    PasswordTextField()

    if (isPasswordError) { 
        PasswordErrorText() 
    }

    LogInButton()
}
```



This is how the composable tree of the initial composition will look like:

![Representation of initial composition](https://ucarecdn.com/c741bdb1-00a7-4202-8d72-e5dccc307f05/)

*Representation of initial composition*

Suppose the state of both `isLoginError` and `isPasswordError` changed to true. In such case, it will trigger a recomposition, which in turn will re-call the `LoginScreen`, `LoginErrorText`, and `PasswordErrorText` composables. At the same time, the `LoginTextField`, `PasswordTextField`, and `LogInButton` composables will not be called since they do not depend on any state that has been changed.

![Representation of a composition tree after recomposition.](https://ucarecdn.com/edbc853c-6ab7-4034-9764-50991e33fcdd/)

*Representation of a composition tree after recomposition.*

## The Foundations of Recomposition

There are several important key points of the behavior of composable functions during initial composition and subsequent recomposition that you should always keep in mind.

**1. Composable functions can be executed in any order.**

At first glance, it might seem that the composable functions are executed sequentially. However, this isn't always the case. Jetpack Compose can prioritize some more important composables over others and render them first. You don't have to worry about which composables have priority, you just need to be aware of this feature and not rely on the order of composables in your code.

**2. Composables can run in parallel.**

Compose can run lower-priority composables on a background thread to optimize and improve performance. In this regard, composables can be executed in parallel, which should be kept in mind.

**3. Recomposition may be canceled.**

This is another important aspect, which is due to the fact that the data can change too quickly. When the data changes before the recomposition is completed, it can be canceled because it has already become outdated. Thanks to this, you can start the next recomposition with the latest data right now, without waiting for its completion, which will significantly increase the speed of the application’s UI processing.

**4. Recomposition skips as many composables as possible.**

We have already seen this in the examples above. Our task as developers is to assist recomposition, providing the best performance for our application as well as the user experience. There are a number of tricks that can help us with this, which we talk about in the following topics.

**5. Recomposition can be executed as often as every animation frame.**

In this regard, your UI thread should be as free as possible from expensive operations such as accessing the database, saving or reading files, and others. All operations that are not directly related to the UI should be executed in a separate thread to avoid UI junks. In turn, the results of these operations must be passed to composable functions using `mutableStateOf()` or `mutableStateListOf()`.

## Conclusion

Recomposition is an operation that we do not directly interact with, but we should always think about how it will behave in a given situation. To achieve the best performance, you must create conditions under which the recomposition will be triggered as rarely as possible for each of the composables. In addition, it is necessary to ensure its fastest possible execution by separating the composable functions which are responsible for creating the UI from the business logic of your application.

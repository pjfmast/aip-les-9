# Compose State

Every object or substance in our world exists in a certain state. Water can be in any of 4 states (including plasma). A person's age, marital status, and the number of steps he has taken in a day are also a state at a certain point in time. Similarly, our application and all of its structural units are in some kind of state, such as file loading state, scroll position, and so on. Each of these states needs to be tracked and handled accordingly.

## State observing

As you know, **State **is any value that can be changed at any point in time during the execution of an application. As we have already mentioned before, to update the data on the screen when a certain state changes, recomposition needs to occur. To track state changes, Jetpack Compose has its own special observable system of type `State` and `MutableState`. This system monitors the value parameter and triggers a recomposition whenever it changes.



```kotlin
val hyperskill: MutableState<String> = mutableStateOf(value = "Hyperskill")
```



Let's say we have not just one value but a whole list, for example, a shopping list of items to buy at the store. We can pass it as an argument to `mutableStateOf(value = mutableListOf(“candy”, “chocolate”, “cookies”))`, but when we add or remove elements from the list, nothing will change because `mutableStateOf()` tracks the object as a whole, not its internal content. In this case, the `mutableStateListOf("candy", "chocolate", "cookies")` function comes to our aid. We can also use the `Collection.toMutableStateListOf()` extension function to transform an existing list. Now, Compose will be notified whenever the list changes and the list displayed on the screen will be updated.

Similarly, we can use `mutableStateMapOf()` to create an observable `Map`.

> **Note:** You can also use other [observable types](https://developer.android.com/jetpack/compose/state#use-other-types-of-state-in-jetpack-compose) like `LiveData`, `Flow`, `StateFlow`, RxJava's `Observable`, but in this case, you need to map them to the State type to call recomposition when data changes automatically. To do this, you can use ready-made [libraries](https://developer.android.com/jetpack/compose/libraries#streams).

## Derived State

Let's imagine a different situation. Suppose we have `TextField`, and we need to check whether the length of the `username` matches the minimum set size with each entered character.



```kotlin
const val MIN_LENGTH = 5
val username = mutableStateOf("")

@Composable
fun UsernameBlock() {
    Log.d("Recomposition", "UsernameBlock Recomposition")

    Column {
        Log.d("Recomposition", "Column Recomposition")

        val isMinLength = username.value.length >= MIN_LENGTH

        if (!isMinLength) {
            Text("At least 5 symbols", color = Color.Red)
        }

        Surface {
            Log.d("Recomposition", "TextField Recomposition")

            TextField(value = username.value, onValueChange = { username.value = it })
        }
    }
}
```



Here's the output:

![Recompositions with mutableStateOf() ](https://ucarecdn.com/4db1ca03-3d71-4f1c-9cd1-7b75a3b1ed71/)

*Recompositions with `mutableStateOf()`*

As you can see, each time the value of the `TextField` changes, the entire `UsernameBlock` composable is recomposed. This is because the `Column` composable depends on the state of `username`. Thus, we trigger unnecessary recompositions, despite the fact that the composition on the screen does not change until the text length reaches the set minimum.

In such cases, we can optimize the performance of our application by using the `derivedStateOf()` function. This function has a single calculation parameter that takes functions as an argument that performs specific calculations and returns the result of their calculations. The `derivedStateOf()` function returns an observable object of type `State`, the value of which is changed only if the result of the current calculation differs from the previous one. Let's try to improve our code:



```kotlin
const val MIN_LENGTH = 5
val username = mutableStateOf("")
val derivedState = derivedStateOf { 
    username.value.length >= MIN_LENGTH 
}

@Composable
fun UsernameBlock() {
    Log.d("Column", "UsernameBlock Recomposition")

    Column {
        Log.d("Column", "Column Recomposition")

        if (!derivedState.value) {
            Text("At least 5 symbols", color = Color.Red)
        }

        Surface {
            Log.d("Column", "Surface Recomposition")

            TextField(value = username.value, onValueChange = { username.value = it })
        }
    }
}
```



And now, let's take a look at the result:

![Recompositions with derivedStateOf()](https://ucarecdn.com/c28738f0-6ab8-4a0b-9fb7-1324e25e610c/)

*Recompositions with `derivedStateOf()`*

Thus, even in such a simple case, we can reduce the number of recompositions by several times. However, in more complex cases, this can be your lifesaver, like observing whether scrolling passes a threshold, tracking the position of a swipeable composable, and so on.

## Custom Compose state

There is another important function that can be used to convert any non-Compose state into a Compose state, and that is `produceState()`. This function allows you to use subscription-driven states such as `Flow`, `LiveData`, or `RxJava` directly in composable ones, automatically triggering recomposition when the data changes in them. Let's try to convert the state using `LiveData` as an example now.

The first thing to note is that, unlike `mutableStateOf()` and `derivedStateOf()`, the `produceState()` function is a composable that returns an object of type `State`. As a result, this function can only be called within another composable.

For convenience and further reuse, we can create a generic extension function with a `@Composable` annotation:



```kotlin
@Composable
fun <T> LiveData<T>.observeAsState(): State<T?> { 
    TODO
}
```



> **Note:** Composables that return data should be named with lowercase letters, just like regular Kotlin functions.

Next, inside the function, we need to call the `produceState()` function, which will return an object of the `State` type. This function has two required parameters: `initialValue` of generic type and a `producer` parameter, which can be set by opening curly braces after the parentheses.



```kotlin
val liveData = this 

val state = produceState(initialValue = liveData.value) {
    TODO
}
```



Note that inside the lambda expression of the `producer` parameter you can execute both regular functions and suspended functions. This is because `produceState` launches a coroutine scoped to the composition. This means that the producer function will be launched when `produceState()` enters composition and will be canceled when it leaves composition.

Inside the lambda expression, we should call the observation of the LiveData object and pass the corresponding owner:



```kotlin
val owner = LocalLifecycleOwner.current

val state = produceState(initialValue = liveData.value) {
    liveData.observe(owner) { recentData ->
        TODO
    }
}
```



The key and final step is to assign the `LiveData` returned by the `observe` function to the `value` property and return our `State` object.



```kotlin
val state = produceState(initialValue = liveData.value) {
    liveData.observe(owner) { recentData ->
        value = recentData
    }
}

return state
```



> **Note:** If the same value is assigned to the `value` property, then the recomposition will not be triggered.

So we have the following function:



```kotlin
@Composable
fun <T> LiveData<T>.observeAsState(): State<T?> {
    val liveData = this
    val owner = LocalLifecycleOwner.current

    val state = produceState(initialValue = liveData.value) {
        liveData.observe(owner) { recentData ->
            value = recentData
        }
    }

    return state
}
```



Now we just need to call the extension function on the `LiveData` object and handle this state in the way we need, for example, as shown here:



```kotlin
val liveData = MutableLiveData(0)

@Composable
fun LiveStateBlock() {
    val liveState = liveData.observeAsState()

    Button(onClick = { liveData.value = liveData.value!! + 1 }) {
        Text("Clicks: ${liveState.value}")
    }
}
```



And the output is the following:

![](https://ucarecdn.com/2f6a126b-3d73-4a93-8a07-05de31f29714/)

## Conclusion

In this topic, you've learned why in Jetpack Compose all mutable variables need to be wrapped in special functions. We also looked at the differences between the `mutableStateOf()`, `derivedStateOf()`, and `produceStateOf()` functions. This will help you accurately determine and choose the appropriate function for a particular situation, which will positively impact the performance of your application.

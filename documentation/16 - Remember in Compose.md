# Remember in Compose

In Jetpack Compose, all composables can be easily reused, and the same composable function can be called in different parts of the application screen. Moreover, each of these functions can be independent and have its own state that doesn't depend on others. However, as you already know, composable functions, just like regular Kotlin functions, cannot remember their last state after the previous execution. In this topic, we will discuss how to store the state for each composable.

## Remember

Earlier, to save the composable's state, we declared it outside the composable function itself, and it worked. However, if we want to reuse this function, we will face some problems. The point is that a composable that has been used multiple times will consistently rely on the same state. When this state changes, all composables will be re-composed and each of them will display the new value just like all the others.

Let's look at the example of a to-do list with completion checkboxes:



```kotlin
val isChecked = mutableStateOf(false)

@Composable
fun Task() {
    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Task №${Random.nextInt()}")
        Checkbox(checked = isChecked.value, onCheckedChange = { isChecked.value = !isChecked.value})
    }
}
```





```kotlin
val taskList = List(100) { "Task №$it"}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HyperskillTheme {

                Column {
                    taskList.forEach { task ->
                        Task(description = task)
                    }
                }

            }
        }
    }
}
```



When you try to mark one of the tasks as "completed", all other tasks will also be checked:

![Incorrect work of composables ](https://ucarecdn.com/51b0e2c8-f377-4028-9d42-8649c88b29fc/)

Is there a way out? Separately creating a state for each composable is not an option since we would need to create a separate composable for each state. What we need is to move the state inside the composable itself, creating a new state every time it is reused. But the most attentive will say that the state inside the composable is not saved, and they will be right.

The point is that even if we move the line `val isChecked = mutableStateOf(false)` inside our composable, Compose will track its state and trigger recomposition when it changes. But the problem is that when the composable is re-called during recomposition, it will be called with the originally assigned values. That is, a composable function can't store its state, like any other function in Kotlin.

However, there is a neat solution that allows you to survive the recomposition and keep the state within the composable. To do this, we will use the `remember` function, which we will wrap our state with:



```kotlin
@Composable
fun Task() {
    val isChecked = remember { mutableStateOf(false) }

    Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
        Text(text = "Task №${Random.nextInt()}")

        Checkbox(checked = isChecked.value, onCheckedChange = { isChecked.value = !isChecked.value})
    }
}
```



Here's the result:

![Correct work of composables](https://ucarecdn.com/b94bfff9-89e9-42c4-8c46-ed3ed92c2ecb/)

As you can see, the state of each element of our UI is independent and survives recomposition. You can think of this state as a private variable within a class. At the same time, each UI element "observes" this state and triggers recomposition when it changes.

> **Note:** In most cases, `remember` is used together with `mutableStateOf`. You can use the `by` delegate to have direct access to the value without using the `state.value` construction, like this:



```kotlin
@Composable
fun TextBlock() {
    val text by remember { mutableStateOf("") }

    Text(text)
}
```



Using a delegate in this case requires importing: `androidx.compose.runtime.getValue` and `androidx.compose.runtime.setValue`.

## rememberSaveable

It might seem that something is still missing. Is the state saved? It is. Here's the thing though: `remember` stores objects during initial composition, and the stored value is returned during recomposition. But it forgets the object when the composable called `remember` is removed from the composition. You can see it in the example below.

This time we will use `LazyColumn` instead of `Column`. Its main difference is that it doesn't load all list items at once. It only executes as many composables as can be displayed on the screen. At the same time, composables that leave the visible area of the screen also leave the composition. We will analyze `LazyColumn` in more detail in a separate topic.

![Example with remember](https://ucarecdn.com/d950ad8f-5eb7-4bdd-91aa-cb9daa2e1e73/)

So, when the composable leaves the composition, `remember` forgets the stored object, and when it returns to the composition, its state returns to its initial state.

Another important point to remember is that even though `remember` helps you retain the state across recompositions, the state is not retained across configuration changes, such as when rotating the screen. To solve these problems, you need `rememberSaveable`, which automatically saves the value in a `Bundle`. Let's change our code a bit and look at the result. We just need to replace `remember` with `rememberSaveable` in the following line:



```kotlin
val isChecked = rememberSaveable { mutableStateOf(false) }
```



Here's the result:

![Example with rememberSaveable](https://ucarecdn.com/eb527d62-fab1-40ee-8cb2-46f48683d34c/)

`rememberSaveable` allows the state to survive not only recomposition, but also activity recreation and system-initiated process death.

## Performance improvements

A less obvious benefit of the `remember` function is that it can improve the performance and responsiveness of an application's UI. How? Let's find out!

We passed all the values that we previously passed to the `remember` function to the lambda parameter `calculations` of this function. When a composable function is initialized the first time, `remember` performs calculations on the objects declared within the lambda expression and stores the result of those calculations. During subsequent recompositions, `remember` no longer performs new calculations but only restores the result of the initial ones. It is this feature that allows you to improve application performance.

Consider this with the following example: let's say we want all our tasks sorted in a certain order. We will pass a sorted list of tasks to the composable parameter for convenience. Nothing prevents us from sorting the list inside the composable and displaying it. Everything will work as expected.

> **Note:** There are many cases where different composables need to access the same state in order to read or modify it. Also, data can be stored in the view model and must be passed to the composable function. To do this, state values must be passed to the parameters of the composable as arguments to respect the fundamental principle of a **"pure function"**. This way we get rid of the state inside the composable. This process is called **state hoisting**. We will talk about this in more detail in one of the future topics.



```kotlin
@Composable
fun SortedTaskList(
    taskList: List<String>,
    sortComparator: Comparator<String>
) {
    val sortedTasks = taskList.sortedWith(sortComparator)
    
    LazyColumn {
        items(sortedTasks.size) {
            Task(description = sortedTasks[it])
        }
    }
}
```



However, this approach has a significant drawback, which you might have already guessed. On each recomposition, this list will be sorted over and over again, even if its content doesn't change. Sorting a list is a costly operation that significantly impacts performance. At first glance, this may not be noticeable on small lists. But as soon as it is slightly increased, UI jank is guaranteed.

In this case, it is vital to use `remember`. With it, sorting will be performed only once. Note that if the state of `taskList` or `sortComparator` is changed, we must notify `remember` about it. To do this, add `key` values, and if at least one of them changes, the calculations will be called again.



```kotlin
@Composable
fun SortedTaskList(
    taskList: List<String>,
    sortComparator: Comparator<String>
) {
    val sortedTasks = remember(key1 = taskList, key2 = sortComparator) {
        taskList.sortedWith(sortComparator)
    }
    
    LazyColumn {
        items(sortedTasks.size) {
            Task(description = sortedTasks[it])
        }
    }
}
```



On each recomposition, `remember` compares the `key` values with the incoming arguments and invalidates the cached value if they differ. The number of keys is unlimited.

This gives you control over the lifetime of an object in the composition. Like `rememberSaveable`, the calculation remains valid until the inputs change, instead of until the remembered value leaves the composition.

> **Note:** Keep in mind that it's best to move the calculations outside of the composable altogether whenever possible.

## Conclusion

The `remember` and `rememberSaveable` functions are used to store an object in memory. Using `keys` allows us to control the lifetime of an object in the composition easily. It's also good practice to use `remember` to store any object or result of an operation that is expensive to initialize or calculate if it can't be done outside of composable. This will significantly improve the performance and responsiveness of your application's UI.

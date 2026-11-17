# Recomposition scope

Composable functions describe what the UI should look like, but Compose still needs a way to update only the part that actually changed when state changes — recompose scopes are how it does that.

This topic covers what a recompose scope is, how Compose decides which scope to recompose, and why inline composables like `Column`, `Row`, and `Box` behave differently.

## Recomposition scope

A recomposition scope in Compose is the smallest standalone unit of code that can be rerun when updates are needed. Compose can identify where a change in state or input occurred and recompute only that scope, without touching the rest of the composition.

Recomposition scopes are what let Compose update the UI efficiently: each scope monitors the state it reads, and when that state changes, only the connected scope is invalidated and recomposed before the next frame.

Two terms are worth distinguishing first: composable recomposition, meaning the recomposition of a composable function itself, and scope recomposition. Scope recomposition is closer to a lambda passed into a composable that can recompose on its own, without triggering recomposition of the composable around it. A "Button scope," for instance, refers to the content lambda passed to `Button` — it can recompose independently of the `Button` composable itself, as we'll see later.

![](https://ucarecdn.com/96a6e6d7-badf-4cae-bc53-26f46760b9e0/)

In the example, there are three distinct composition scopes: the `CustomComposable` scope, the `Surface` scope, and the `Button` scope. Both the `Button` scope and the `Surface` scope are nested inside the `CustomComposable` scope, but in terms of recomposition, they can each recompose independently — Compose can start recomposing any one of them without touching the others.

The `Column` scope isn't part of this list yet — that's covered later.

## Illustrating recompose scopes with an example

Here's a simple example showing how recompose scopes work in practice:



```kotlin
val recompositionCount = mutableStateOf(0)

@Composable
fun CustomComposable() {
    // This statement traces recomposition and is invoked each time it occurs
    Log.d("Recomposition", "CustomComposable")

    Button(onClick = { recompositionCount.value++ }) {
        Log.d("Recomposition", "Button Scope ")

        Text(text = "Recomposed ${recompositionCount.value} times.")
    }
}
```



There's one `Button` here with a `Text` composable in its content. Tapping the button updates the state that `Text` reads. That gives two composition scopes — `CustomComposable` and `Button` — and both can recompose independently.

As for `Text`, it recomposes every time its input changes. Whether it recomposes independently is worth examining next.

Running this example: when the button is tapped, only the `Button` scope recomposes, because it's `Text` that changes, not `CustomComposable`.

![](https://ucarecdn.com/36e8e2f8-1a86-4bc8-9f15-bc3f4f398bc6/)

But why does the `Button` scope recompose at all, when it's `Text` that actually uses `recompositionCount`? On the JVM and Android Runtime (ART), a function's arguments are evaluated before the call — so the string `"Recomposed ${recompositionCount.value} times."` is built inside the `Button` scope, before `Text` is even called. Broken down, that's:



```kotlin
val newText = "Recomposed ${recompositionCount.value} times."
Text(text = newText)
```



So it's the read of `recompositionCount` inside the `Button` scope that triggers its recomposition, and the new value passed in, `newText`, is what triggers `Text`'s.

The `CustomComposable` scope doesn't recompose, since it doesn't read any state directly, and the `Button` scope already handles its own recomposition independently.

Android Studio's Layout Inspector can be used to observe this recomposition directly:

![](https://ucarecdn.com/0838092a-65b3-457f-b5e5-af8e1fe6f774/)

Tapping the button once recomposes the `Button` composable twice — once on press, once on release. That's not universal behavior; it depends on the button's own internal state handling. `Text`, by contrast, recomposes once, matching its single input change. `CustomComposable` doesn't recompose at all: it's essentially just its content lambda (its scope), and as shown above, that scope doesn't read any state.

Now let's move the state out of the `Button`'s content scope and into a `Surface` composable instead.



```kotlin
val recompositionCount = mutableStateOf(0)

@Composable
fun CustomComposable() {
    Log.d("Recomposition", "CustomComposable Scope")

    Surface {
        Log.d("Recomposition", "Surface Scope")

        Text(text = "Recomposed ${recompositionCount.value} times.")
    }

    Button(
        onClick = { recompositionCount.value++ },
        modifier = Modifier.padding(top = 20.dp)
    ) {
        Log.d("Recomposition", "Button Scope ")

        Text(text = "Tap to recompose")
    }
}
```



Now the button only updates the state — it no longer reads it. Running this: the `Button` scope composes once, for the initial render, and after that only the `Surface` scope recomposes. `CustomComposable` doesn't need to recompose either, since the `Surface` scope recomposes independently.

![](https://ucarecdn.com/7c2228f4-fc10-435b-bea3-ad9b28cfbe76/)

> **Note:**

**About the **`onClick` lambda: its job here is just to update the state — it doesn't read any state itself. That state change is what triggers recomposition in whatever composable functions do read it.

## Inline composable functions and their parent scope impact

Notice the modifier passed to `Button`:



```kotlin
modifier = Modifier.padding(top = 20.dp)
```



This keeps `Surface` visible on screen, since it would otherwise be covered by the button. But to arrange elements vertically, `Column` is the more natural choice. Dropping the modifier and the `Surface` composable, and using `Column` instead to stack `Text` and `Button` vertically:



```kotlin
val recompositionCount = mutableStateOf(0)

@Composable
fun CustomComposable() {
    Log.d("Recomposition", "CustomComposable Scope")

    Column {
        Log.d("Recomposition", "Column Scope")
        
        Text(text = "Recomposed ${recompositionCount.value} times.")

        Button(
            onClick = { recompositionCount.value++ },
            modifier = Modifier.padding(top = 20.dp)
        ) {
            Log.d("Recomposition", "Button Scope ")

            Text(text = "Tap to recompose")
        }
    }
}
```



Here, the state is read inside the `Column` scope, so the expectation is that only the `Column` scope recomposes when the button is clicked.

Running it shows otherwise: the `Column` scope doesn't recompose on its own — it triggers recomposition of the `CustomComposable` scope as well.

![](https://ucarecdn.com/166701ef-0f93-46d1-89f6-48fe03e84d62/)

The reason is in `Column`'s own source: it's an inline function. Inline composables like `Column`, `Row`, and `Box` have their code inserted directly at the call site, so they don't get their own recompose scope at all.

![](https://ucarecdn.com/53652540-4fbc-4a83-b637-15fd2920c394/)

## Conclusion

Recompose scopes let Compose recompute only the part of the UI that actually changed, instead of the whole composition. Inline composables — `Column`, `Row`, `Box` — are the exception: because their code is inserted directly where they're called, they don't establish a recompose scope of their own.

# Modifying Composables

When you develop an app, creating a visually appealing user interface is key for both client and personal applications. In Jetpack Compose, this is possible by using modifiers. These are functions that let you tailor the look and behavior of composables, the building blocks for your UI. Modifiers are the tools used to mold and improve them. This topic will explore frequently used modifiers and provide guidance on how to use them effectively along with best practices.

## Modifiers and layout customization

When modifying composables, we utilize the `Modifier` interface, which offers extension functions to customize. Most composables come with a default modifier parameter. It takes a Modifier instance as its argument. In this topic, we will build a pet card from scratch for better understanding:

![A card containing a cat image along with two text elements](https://ucarecdn.com/3604d3a3-193d-43c6-9610-8a8006a51487/)

We start with the `Row` composable to show our card. For starters, we can specify its size, background, and shape. The background and size modifiers are more straightforward. For shaping, we use the `clip` modifier.



```kotlin
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.shape.RoundedCornerShape


val cardBackground = Color(0xFFE8DEF8)
val cardShape = RoundedCornerShape(size = 24.dp)

@Composable
fun PetCard() {
   Row(
       modifier = Modifier
           .size(width = 200.dp, height = 100.dp)
           .clip(cardShape)
           .background(cardBackground)
   ) {}
}
```



In the clip function, we gave `RoundedCornerShape` as a parameter. We used the `size` parameter to set every corner radius equally. However, you can specify every corner according to your needs. And instead of a floating–point number, you can use a percentage as a value. After the clip, we gave a background color. As a result, we got something like this:

![A rounded card with a purple color palette](https://ucarecdn.com/550bb60c-3d46-4fc8-b447-60925b6d584a/)

By default, composable layouts adapt to their content. This means they automatically increase in size to cover the content. However, hardcoding the size of a parent composable prevents its children from exceeding the boundaries of the composable. This is because composable layouts are based on **constraints**. These constraints can be thought of as a set of rules. The parent composable is responsible for specifying the constraints for its children. But, sometimes, design requirements may differ. For this, you need to override this behavior on child composable. We can use `requiredSize`, `requireHeight`, or `requireWidth` modifiers to override this.



```kotlin
@Composable
fun PetCard() {
    Row(
        modifier = Modifier
            .size(width = 400.dp, height = 100.dp)
            .background(cardBackground)
    ) {
        Image(
            /*...*/
            modifier = Modifier.requiredSize(200.dp)
        )
        Image(
            /*...*/
            modifier = Modifier.size(200.dp)
        )
    }
}
```



In this example, we used two images. We read the pictures from the resources file and specified their sizes. As a result, we got this output:

![A rounded card with two cat pictures](https://ucarecdn.com/959314ad-9d17-4746-925d-94ea77c55c24/)

As we see in the picture, the first image overflowed the parent composable, and the second didn't. Using the default `size` modifier on the second image respected the parent composable constraints. However, with the help of the `requiredSize`, we can override this behavior on the first image.

When designing user interfaces, it's important to remember that using `size`, `width`, or `height` modifiers may be necessary and appropriate. However, it's also important to avoid overusing them, as this can limit the responsiveness and flexibility of your design. Instead, it's best to embrace responsive design.

## Padding

In Jetpack Compose, the `padding` allows you to adjust the spacing between components and their surrounding elements easily.



```kotlin
@Composable
fun PetCard() {
    Row(
        modifier = Modifier.background(cardBackground),
		verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            /*...*/
            modifier = Modifier.size(100.dp).clip(CircleShape)
        )
        Column {
            Text("Name: Lucy")
            Text("Age: 13")
        }
    }
}
```



We removed the size modifier from the `Row` composable for better responsive design. However, we hardcoded the image size. The reason is that, in databases or resources, the size of images can vary. As a result, the images' inconsistent sizes create a poor user experience. As mentioned earlier, the parent composable (`Row`) height will be 100 dp because the parent wraps its children. With this code, we've got something that looks like this:

![A rounded card with a cat image on the left and text on the right](https://ucarecdn.com/bc79b909-d597-46d9-af95-fd15c03fc942/)

Now, it seems odd. We can fix it with `padding`. First, we can apply padding to the `Row`. Then, we can separate pet information from the image. For this purpose, we can utilize `horizontalArrangment` for that.



```kotlin
@Composable
fun PetCard() {
    Row(
        modifier = Modifier
            .clip(cardShape)
            .background(cardBackground)
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Image(
            /*...*/
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
        )
        Column {
            Text("Name: Lucy")
            Text("Age: 13")
        }
    }
}
```



The `horizontalArrangement` parameter does not directly apply padding. Instead, it controls how the children of a layout are arranged horizontally within the available space. Conversely, when working with a `Column` composable, children are arranged vertically with the `verticalArrangement` parameter. With this code, we got this image.

![A card containing a cat image along with two text elements](https://ucarecdn.com/3604d3a3-193d-43c6-9610-8a8006a51487/)

There are a lot of ways to achieve the same result. For instance, you can use `Spacer` to separate the image from the text. Or instead of applying padding in `Row`, you can use padding in every composable. However, doing something with less is better if it does not complicate the code. Imagine that the component count in `Row` can be much more. With the help of `horizontalArrangement`, we defined only once instead of every composable. By using this, you can eliminate redundant code and improve your app's performance.

## Filling spaces and weight

`fillMaxSize` occupies all the available space within a composable. `fillMaxHeight` takes up all the open vertical space, while `fillMaxWidth` utilizes all the available horizontal space.

![A card featuring three distinct blocks.](https://ucarecdn.com/2ec41aea-7bae-4445-9493-e80df643821e/)

These modifiers are better for responsive design. Because they can adapt to different screen sizes, for example, when you create your login screen, text fields and buttons are set to `fillMaxWidth`. Or, when you develop a map screen, you can use `fillMaxSize` to fill the entire screen. However, it's important to exercise caution. For instance, when creating a tablet app; a button can fill the entire screen width, leading to a bad user experience. This gets worse in landscape mode. Despite that, you can fix it quickly; these modifiers take `fraction` as a parameter, which takes floating–point numbers. You can specify it from zero to one, respectively. The fraction parameter applies not only to the `fillMaxWidth` but also to the `fillMaxHeight` and the `fillMaxSize`. Specifically, it represents the proportion of the parent composable's width, height, or dimensions that the child composable should occupy respectively.



```kotlin
Button(modifier = Modifier.fillMaxWidth(0.5f), onClick = { /*...*/ }) { /*...*/ }
```



Now, let's look at the weight modifier, which measures how much space an element should take up compared to others. The main difference between `fillMaxSize` and weight is that a parent layout uses weight to distribute the available space among the different composables. It applies to `RowScope` and `ColumnScope` because they can contain multiple composables. However, the weight doesn't apply to `BoxScope`, even though it can also have numerous composables. Because it's unclear how the weight would be distributed – vertically, horizontally, or both. Therefore, the `weight` modifier does not work on `BoxScope`.

For instance, when you create a blog post screen. You can divide the screen into header, content, and footer sections. Then, you can specify the weight of each section.



```kotlin
@Composable
fun BlogPage() {
    Column (modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("Header", modifier = Modifier.fillMaxWidth().weight(1f))
        Text("Content", modifier = Modifier.fillMaxWidth().weight(5f))
        Text("Footer", modifier = Modifier.fillMaxWidth().weight(1f))
    }
}
```



In the example, the second text will be five times bigger than the first and last texts. With the help of weight, the screen can adapt to dynamic data. It's suitable for creating proportional layouts and responsive design. However, remember that weight can increase the complexity of layouts. Also, on smaller devices, if the content is too crowded, it can overlap.

## Click action

Some composables have an `onClick` function like Button or FAB. However, you can give every composable click interaction with a `clickable` modifier. It takes parameters like `interactionSource` for handling interactions and gestures, such as clicks, long presses, and drags. To use this, you need to utilize it with `remember` and `MutableInteractionSource`, and then you can pass it as an argument. Another argument is `enabled`. By default, it's set to true, but you can disable the click behavior. There is another argument called `indication`. By default, it gives a ripple effect when you click. You can turn it off with `null`. Also, it has an `onClick` lambda function. For example, you can navigate to another screen or open a dialog.



```kotlin
@Composable
fun PetCard() {
    val interactionSource = remember { MutableInteractionSource() }
    Row(
        modifier = Modifier
            /*...*/
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { /* Navigate to pet screen */ }
            )
    ) {/*...*/}
}
```



## Order of modifiers

Chaining modifiers in Jetpack Compose involves applying multiple modifiers to a single UI element in a specific order. The order is essential because each modifier builds upon the previous one. That means when you specify the size to 300, every other modifier is built upon it. For example, we can use our pet card to illustrate the distinction.



```kotlin
@Composable
fun PetCard() {
    Row(
        modifier = Modifier
            .clip(cardShape)
            .background(cardBackground),
    ) { /*...*/ }
}
```



We first apply the `clip` modifier to shape our card and then set the background. If we swap these, it will first apply to the background of our card, then give a curved shape. However, in this case, a rounded shape is not visible in the UI due to the order of modifiers.

![Two cards, each consisting of a cat image and accompanying text](https://ucarecdn.com/e5408419-52ee-4ccb-9641-838edc32f4d2/)

This code is one example of the importance of order. For a simpler explanation, let's use an example to illustrate the correct sequence of modifiers. We can start with layout modifiers like `fillMaxSize` or `padding`. Then, we can apply size modifiers like `width` and `height` to control dimensions. After that, we can specify `align`, `background`, and `border`. Next, to enable a click event on a composable, placing the `clickable` modifier as the last one in the chain is better. However, there is no recommended order for all modifier chains; it depends on the specific needs of your UI component. Planning the UI before writing the code is always a good practice.

> **Note:**

Remember, we used `padding` as the final modifier in the example above. When you define `padding` first in the modifier, it behaves as padding; if you define it last, it acts like a margin.

## Creating custom modifiers

Custom modifiers in Jetpack Compose are a powerful tool that allows us to extract complex UI logic and create reusable, modular modifier components. This process promotes the single responsibility principle, enhances code maintainability, and enables the abstraction of UI logic. Jetpack Compose leverages Kotlin's extension functions, a quick and efficient way to add new functionality to existing classes. In our case, we extend the Modifier class to create custom modifiers. One practical example of this is:



```kotlin
fun Modifier.roundedCorners(radius: Dp): Modifier =
    clip(RoundedCornerShape(radius))

@Composable
fun CustomModifiers() {
    Box(
        modifier = Modifier.size(300.dp)
            .roundedCorners(16.dp)
            .background(Color.Blue)
    )
}
```



The custom modifier in this example is **stateless** — it's just a plain extension function that chains an existing modifier (`clip`) together, which is often the simplest way to build a custom modifier if you don't need any new behavior.

It's also possible to create custom modifiers that hold their own state, such as one that changes the color of a composable depending on whether it's been clicked. There are two APIs for this: the older `composed {}` function, and `Modifier.Node`.

`composed {}` used to be the common choice, since it lets you call `remember` and other composable functions directly inside a modifier factory. However, Google's Compose documentation now states plainly that `composed {}` **is no longer recommended, due to the performance issues it creates** — its state is reallocated on every recomposition of the composable it's attached to, and it resolves `CompositionLocal` values at the place the modifier is *defined* rather than where it's *used*, which can produce surprising results. `Modifier.Node` is the lower-level API that Compose's own built-in modifiers are implemented with, and it's now the recommended way to build a stateful custom modifier.

A `Modifier.Node`-based custom modifier has three parts:

- A `Modifier.Node` implementation, which holds the state and the actual behavior (drawing, measuring, handling pointer input, and so on). Unlike a `composed {}` block, a `Modifier.Node` instance survives across recompositions and can be reused instead of reallocated.

- A `ModifierNodeElement`, an immutable holder that knows how to create and update instances of your node.

- A **modifier factory**, the public extension function you actually chain onto a composable — the same role the factory played in every other example in this topic.

Let's rebuild a "change color when clicked" modifier, this time with `Modifier.Node`:



```kotlin
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifierNode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.pointer.SuspendingPointerInputModifierNode
import androidx.compose.ui.node.DelegatingNode
import androidx.compose.ui.node.ModifierNodeElement

// Modifier.Node: holds the state and the drawing logic
private class ChangeColorOnClickNode : DelegatingNode(), DrawModifierNode {

    private var isClicked = false

    // Delegate pointer-input handling to a SuspendingPointerInputModifierNode
    private val pointerInputNode = delegate(
        SuspendingPointerInputModifierNode {
            detectTapGestures(
                onTap = {
                    isClicked = !isClicked
                    invalidateDraw() // request a redraw using the new color
                }
            )
        }
    )

    override fun ContentDrawScope.draw() {
        drawRect(color = if (isClicked) Color.Red else Color.Blue)
    }
}

// ModifierNodeElement: this modifier takes no parameters, so a plain object works
private data object ChangeColorOnClickElement : ModifierNodeElement<ChangeColorOnClickNode>() {
    override fun create() = ChangeColorOnClickNode()
    override fun update(node: ChangeColorOnClickNode) {}
}

// Modifier factory: the public API you chain onto a composable
fun Modifier.changeColorWhenClicked(): Modifier = this then ChangeColorOnClickElement
```



The usage is exactly the same as it would be with any other modifier:



```kotlin
@Composable
fun ColourfulSquare() {
    Box(
        modifier = Modifier
            .size(150.dp)
            .changeColorWhenClicked()
    )
}
```



![](https://ucarecdn.com/9fc2bc4f-ff65-4d3b-84dc-7b488bed2a87/)

A few things to note about this version:

- `isClicked` lives inside the `ChangeColorOnClickNode` instance itself, not inside composition, so the node can be reused across recompositions instead of being thrown away and rebuilt.

- We delegate the actual tap detection to a `SuspendingPointerInputModifierNode`, which lets us use the familiar `detectTapGestures` suspend function without needing a full custom `PointerInputModifierNode` implementation.

- Calling `invalidateDraw()` tells Compose to redraw this node — and only this node's drawing — the next time it needs to reflect the new `isClicked` value, without affecting the rest of the UI.

Compare this to what the same modifier looked like written with the now-discouraged `composed {}` approach:



```kotlin
// Not recommended anymore — shown only for comparison
fun Modifier.changeColorWhenClicked() = composed {
    var isClicked by remember { mutableStateOf(false) }
    val color = if (isClicked) Color.Red else Color.Blue

    then(
        Modifier
            .background(color = color)
            .clickable { isClicked = !isClicked }
    )
}
```



This older version is shorter to write, but every recomposition of whatever composable uses it re-invokes the whole `composed {}` block, which is exactly the performance cost `Modifier.Node` was designed to avoid.

`then` in both versions accepts a `Modifier` and returns it, allowing the modifier chain to keep extending beyond your custom modifier.

Integrating custom modifiers into our app offers the advantage of creating reusable and modular components and empowers us to abstract complex layouts and styling details from the core UI logic. This abstraction, in turn, simplifies the main UI codebase, making it more manageable and improving the overall code quality.

## Conclusion

Modifiers are essential for building UI components in Jetpack Compose. They allow developers to customize the appearance and behavior of their composables, providing greater flexibility and control over the layout. In this topic, we covered the following:

- How to use size, requiredSize, clip, and padding modifiers for shaping and spacing.

- Using fillMaxSize, fillMaxWidth, fillMaxHeight, and weight for responsive design.

- To enable the click functionality in composables, we use the clickable modifier.

- The importance of the order of modifiers.

- Creating custom modifiers to encapsulate complex styling and layout logic, promoting reusability and modularity in UI components — including how to use `Modifier.Node`, the currently recommended API for stateful custom modifiers, in place of the older `composed {}` function.

By mastering these techniques, developers can create responsive, user–friendly, and visually appealing apps with Jetpack Compose.

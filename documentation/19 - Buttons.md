# Buttons

You have poked them on websites, within apps... perhaps they've even inspired the occasional sigh of frustration ("Why isn't it working?!"). Buttons serve as one of the primary ways for users to interact with apps, triggering specific actions within the application when clicked. In this topic, you will learn how to create various types of buttons, style them, and customize how they work.

## Button overview

Buttons are a common UI element, typically found throughout an app's UI in places like dialogs, forms, cards, and other similar components. Jetpack Compose provides us with five main button types, each suited for a slightly different task:

![types of buttons](https://ucarecdn.com/5f16a241-8115-4511-96fd-72a7444dc870/)

- **Filled Button**: `Button` This is the default button type! It's meant to have a bold visual impact. Use it for*** ***primary actions - things like "Submit", "Download", or "Add to cart".

- **Filled Tonal Button**: `FilledTonalButton` This button shares a similar look to the filled button but has a more subtle color variation. It's a medium-emphasis button; can be used where a lower-priority button requires slightly more emphasis.

- **Elevated Button**: `ElevatedButton` This button type stands out with a subtle shadow, great when you need to separate a button on a busy screen visually. Shares similar roles with a filled button.

- **Outlined Button**: `OutlinedButton` The outlined button, just like the name suggests, has an outline (border) around it with a transparent fill. It is useful for secondary actions. Things like "Cancel", "Back", or "Edit". Outlined buttons visually recede in the interface creating a clear hierarchy when used alongside filled, tonal, or elevated buttons.

- **Text Button**: `TextButton` This is a minimalist button, just text with no background or border, ideal for less important actions or places where you want a less button-like feel, like navigation links.

Let's get hands-on and make a simple filled button with the text "Create". To create a filled button, we use the composable `Button` as shown above. The composable requires two arguments:

- `onClick: () -> Unit`: This lambda function defines the action to execute when the button is clicked.

- `content: @Composable RowScope.() -> Unit`: This is a slot** **for the button's content. Usually, this will contain the `Text` composable with the button's text. As the lambda's type indicates, composables placed here will be laid out in a row.

> **Note:**

Note that `RowScope` provides access to functions specifically designed for use within a row layout.



```kotlin
Button(onClick = { /*TODO*/ }) {
    Text("Create")
}
```



> **Warning:**

While we'll use text directly here for simplicity, remember: in production apps, always use string resources for easier translation and management.

We passed the `content` argument as a trailing lambda. As you'd expect, this will give us the following output:

![](https://ucarecdn.com/468a8d5a-92cc-4141-b1f4-9af210c604b3/)

Right now, this button has no action; the `onClick` lambda is empty. Go ahead and experiment with creating the different button types discussed above. Note that the same two arguments will be required.

> **Note:**

Here's an interesting fact: Under the hood, all the other button types listed above call the default `Button`, providing different arguments to achieve their unique look.

## Button parameters

Let's take a look at the parameters that the `Button` composable provides:



```kotlin
@Composable
fun Button(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    shape: Shape = ButtonDefaults.shape,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable RowScope.() -> Unit
)
```



> **Warning:**

Note that some default arguments may differ depending on which library the composables are from: **Material** (`androidx.compose.material.*`) or **Material 3** (`androidx.compose.material3.*`).

Remember, all button types in Jetpack Compose are fundamentally the same. They mainly differ in the default colors, elevation, etc. provided by the `ButtonDefaults` object. For example, the default colors argument for a filled tonal button is `ButtonDefaults.filledTonalButtonColors()`. For simplicity, we will only refer to the default `Button` in the upcoming sections with the understanding that everything you learn can be applied to the other types.

## Button customization

The parameters have clear names, making them easy to understand. In this section, we'll use most of them to customize the style of the "Create" button we created earlier:

![transformation](https://ucarecdn.com/982d351e-300f-4feb-9aea-e5a25d27eaa6/)

First, let's address the `modifier`. It's a common parameter across many Compose elements and behaves just like you'd expect. You can pass in modifiers to change the button's size, add padding to it, change its layout behavior, etc. In this section, we'll focus on the core button concepts, but feel free to play around with the modifiers you already know.

> **Note:**

When changing the button's size, make sure the container is large enough to accommodate the button's content. You can set the button's width to be responsive and expand across its parent composable. In this case, the content will be centered.

Let's start by customizing the button's colors. Notice that we customize multiple colors - one for the container (background) and one for the content (text, icons, etc.). Let's take a step back for a moment. Did you notice that our simple button from the previous section had white text even though we didn't set that? Also, why does the button have a purple background? This is indirectly thanks to the `ButtonDefaults.buttonColors()` default argument passed to the `colors` parameter. The `buttonColors` method sets the button's container color to the theme's `primary` color, and the content's color to the theme's `onPrimary` color. In a new Android Studio project, these are purple and white respectively. To adjust the colors, we use the same method and override the defaults:

> **Note:**

Understanding how content color is set involves knowing about `CompositionLocal`. It allows data to be passed down through a composition implicitly. You'll learn more about it in a future topic.



```kotlin
Button(
    ...,
    colors = ButtonDefaults.buttonColors(
        containerColor = Color(0xFF008300),
        contentColor = Color(0xFFBEFFBE)
    )
) {
    ...
}
```



This will give us the output:

![](https://ucarecdn.com/621f0218-5329-4071-a964-c8a7a0c676e5/)

> **Note:**

The `ButtonDefaults` object contains the default arguments for each of the different button types. For example, as stated earlier, for a filled tonal button, we get the default colors from the method `filledTonalButtonColors`. This is also true for the other types.

Hopefully, you are getting familiar with `ButtonDefaults`. It provides defaults (including colors, elevation, shapes, etc.) for various button types, which we can override to match a custom design. In addition to the colors specified above, we can also change the colors for when the button is disabled. This happens when the `enabled` parameter is set to `false`, meaning the button won't respond to clicks. Notice how we only customized what we needed - the rest falls back to those handy defaults.

Next, let's change the shape and add a border to the button; we used a `RoundedCornerShape` with `8.dp` across all corners for the shape:



```kotlin
val shape = RoundedCornerShape(8.dp)

Button(
    ...,
    border = BorderStroke(2.dp, Brush.linearGradient(
        listOf(
            Color(0xFF00DA00),
            Color(0xFF004E00)
        )
    )),
    shape = shape
) {
    ...
}
```



With these changes, we get the output:

![](https://ucarecdn.com/1853a573-8c6e-4d38-862d-53a92983fc5b/)

The `shape` parameter is used to specify the shape of the container, border (if not null), and shadow (if elevation greater than `0.dp` is provided). For the `border` parameter, which expects a `BorderStroke`, we used the `BorderStroke` class to create a border with the width `2.dp` and a linear gradient brush with a list of colors for the gradient. You can also use a `Color` or other types of brushes for a different style.

Finally, we'll change the content padding, add elevation, and then add an icon to our button, `ButtonDefaults` provides sensible spacing values for different scenarios:



```kotlin
...
Button(
    ...,
    elevation = ButtonDefaults.buttonElevation(defaultElevation = 8.dp),
    contentPadding = ButtonDefaults.ButtonWithIconContentPadding,
) {
    Icon(
        Icons.Rounded.Create,
        contentDescription = null,
        modifier = Modifier.size(ButtonDefaults.IconSize)
    )
    Spacer(Modifier.width(ButtonDefaults.IconSpacing))
    Text("Create")
}
```



This will give us the output we were targeting:

![](https://ucarecdn.com/36531783-fac7-4909-8613-7eb07c880ac1/)

Here, `elevation` creates a subtle shadow, giving the button that raised effect. Note that we provided the `defaultElevation`, this is used when the button is enabled and has no other interactions. Different elevation values will be used for hover, press, focus, and disabled states - all customizable within the `buttonElevation` method. The `ButtonElevation` values will also animate when changing between those different states. On the other hand, `contentPadding` controls internal spacing within the button. Once again, we relied on the `ButtonDefaults` for padding values that cater to an icon.

To add an icon, we used the `Icon` composable with the `Icons.Rounded.Create` icon provided by default by Jetpack Compose. We also changed it's size to an appropriate size for an icon in a button, defined in `ButtonDefaults.IconSize`. To make sure the icon and text don't feel crowded, we added a `Spacer`. The width is conveniently provided by `ButtonDefaults.IconSpacing`. Remember composable in the content slot are placed in a row.

We are now done with our button, but there is one parameter left, the `interactionSource`. It's essentially used to track interactions (press, release, etc.) and customize press indications. This will be covered in a separate topic.

## Icon buttons

Icon buttons offer a compact and visually clear way to represent actions within an app. They are ideal for secondary actions or places with limited space, like toolbars.

The composable used to make an icon button has a surprisingly straightforward name: `IconButton`:



```kotlin
@Composable
fun IconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: IconButtonColors = IconButtonDefaults.iconButtonColors(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit
)
```



Much like the previous buttons, `IconButton` expects an `onClick` lambda and a `content` lambda that normally is meant to take an `Icon` composable:



```kotlin
IconButton(onClick = { /*TODO*/ }) {
    Icon(Icons.Default.Settings, contentDescription = "Settings")
}
```



> **Note:**

As good practice, make sure you include a content description for the icon you supply for accessibility services. This text description helps users with visual impairments understand the purpose of the button and how it interacts with the app. It's crucial to ensure your app is usable by everyone, regardless of their abilities. A well-written content description should clearly and concisely convey the action the button performs.

This produces the following output:

![settings icon](https://ucarecdn.com/580213e6-6976-45b9-b012-e9ab9f39bdd5/)

The rest of the parameters work as you'd expect. The only difference is that the defaults come from `IconButtonDefaults`.

There are two main types of icon buttons: a **standard** icon button and a **contained** icon button. The one you just saw above is the former. The difference between the two is the colors they use. The latter has a background or an outline; typically a filled, filled tonal and outlined variations. Here is an example of the filled variation:



```kotlin
FilledIconButton(onClick = { /*TODO*/ }) {
    Icon(Icons.Default.Settings, contentDescription = "Settings")
}
```



This produces the output:

![settings icon contained](https://ucarecdn.com/16f7b7bf-c85a-4b2a-ba94-61f041bf49d5/)

> **Note:**

Note that icon buttons are circular by default.

To make the filled tonal and the outlined variant, we use the `FilledTonalIconButton` and the `OutlinedIconButton` composables respectively. They all take similar parameters with the addition of the `shape` parameter as they have a visible background. The outlined variant takes an additional `border` parameter which works as you'd expect.

## Floating Action Buttons (FABs)

Floating Action Buttons (FABs) are special buttons designed to draw attention to the most important action(s) on a screen. Their prominence and raised appearance, as if floating above other elements, makes them stand out in the UI. Jetpack Compose offers two main types of FABs:

- **The standard FAB**:

Use the `FloatingActionButton` composable to create the standard FAB:



```kotlin
@Composable
fun FloatingActionButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    shape: Shape = FloatingActionButtonDefaults.shape,
    containerColor: Color = FloatingActionButtonDefaults.containerColor,
    contentColor: Color = contentColorFor(containerColor),
    elevation: FloatingActionButtonElevation = FloatingActionButtonDefaults.elevation(),
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    content: @Composable () -> Unit,
)
```



Did you figure out which object provides the defaults? If your answer was `FloatingActionButtonDefaults`, then you are right! By now, you should be familiar with the parameters. Here, instead of the colors being combined, the container color is provided separately from the content color. The content lambda usually takes in an `Icon`. Here is an example:



```kotlin
FloatingActionButton(onClick = { /*TODO*/ }) {
    Icon(Icons.Filled.Add, contentDescription = "Add a note")
}
```



This produces the output:

![FAB](https://ucarecdn.com/74044429-7507-4681-8852-0b3de925335d/)

There are two variants of the standard FAB that differ in size. The `SmallFloatingActionButton` and `LargeFloatingActionButton` which take the same parameters as the standard FAB.

- **Extended FAB:**

The Extended FAB, as the name suggests, is an FAB with extra space for text. To make an Extended FAB, we use `ExtendedFloatingActionButton`:



```kotlin
@Composable
fun ExtendedFloatingActionButton(
    text: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    expanded: Boolean = true,
    // ...other customization options...
)

// or

@Composable
fun ExtendedFloatingActionButton(
    onClick: () -> Unit,
    // ...other customization options...
    content: @Composable RowScope.() -> Unit,
)

// Defaults provided by FloatingActionButtonDefaults
```



There are two overloads of the `ExtendedFloatingActionButton` as shown in the snippet. One allows us to specify the `Text` composable and `Icon` composable separately. This variant also has the `expanded` state, if `true`, it displays both the text and icon; otherwise, only the icon is displayed. There is also a nice animation when transitioning between the two possible states; `true` and `false`. The second variant provides more flexibility; we can specify the content composables in the `content` lambda, which will be placed in a row. Here is an example where we used the first overload:



```kotlin
ExtendedFloatingActionButton(
    onClick = { /*TODO*/ },
    icon = { Icon(Icons.Filled.Add, contentDescription = null) },
    text = { Text("Add a note")}
)
```



This gives us the output:

![extended FAB](https://ucarecdn.com/e45163d3-bb6c-4c10-b86a-a5375a2f4b1b/)

> **Note:**

Floating Action Buttons are typically placed in a `Scaffold`. A Scaffold is a layout structure that acts as a foundation for building app's screens. It provides a place for common components like the app bar (top of the screen), bottom navigation bar, and a FAB. You will learn more about it in a separate topic.

## Conclusion

You now have what is needed to build buttons that guide your users with functionality and style! Remember, good button design isn't just about appearances - it's crucial for shaping the user experience. To fine-tune your button mastery, you are highly recommended to follow the official Material Design guidelines for [common buttons](https://m3.material.io/components/buttons/guidelines), [icon buttons](https://m3.material.io/components/icon-buttons/guidelines), [FABs](https://m3.material.io/components/floating-action-button/guidelines), and [Extended FABs](https://m3.material.io/components/extended-fab/guidelines). These guidelines offer insights into appropriate use cases, sizing, accessibility, and much more.

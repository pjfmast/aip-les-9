# Text Composable

Text is one of the essential and most used UI elements. However, many different styles can be used to display text in the same application. Jetpack Compose allows you to quickly and easily customize text style without having to rewrite or extend large classes with a vast number of parameters to achieve the desired result.

## Text composable overview

The purpose of a composable function `Text` is, certainly, to display the text received as a required parameter `text` of the `String` or `AnnotatedString` type. You can use either the keyboard shortcut **Ctrl+Q** to get a list of options with a brief description of each, or **Ctrl+P** to display a list of parameters for each function implementation. If you want to explore the source implementation of the `Text` composable, use **Ctrl + click** on the `Text` function in your code. Let's look at the parameters that this function provides us:



```kotlin
@Composable
fun Text(
    text: String, // or AnnotatedString
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current
)
```



As you can see, the `Text` composable function has several parameters with which you can flexibly customize the style of displaying its content. Well, now let's figure out what some of these parameters do and how to use them properly.

> **Warning:**

Note that all functions, classes, and instances discussed below refer to the **androidx.compose.ui.*** library. If you import entities from other libraries, you will most likely get an error and not be able to compile the application.

## Text styling

Before we continue, it's worth mentioning that instead of using String values, you should use String resources in a real application. This allows you to use the same values in different composables. Also, in the future, you can easily translate your application into other languages without fixing all your code. However, we will use String values in our examples for simplicity and clarity. Now, let's continue!

- **color** — yes, you're right, this parameter sets the text color. As an argument, you can use the default values of the Color class or pass an object of this class, specifying the exact color in its constructor.



```kotlin
Text(
    text = "Wildfire",
    color = Color(0xFF1FBC86)
)
```



Output:

![Colored text](https://ucarecdn.com/aca1d4b6-d754-443f-9afe-38bcf81265c3/)

- **fontSize** — you might be surprised, but this argument is responsible for the size of the text. However, you can't determine the font size in pixels using basic data types such as Int, Double, or Float. Thing is, the same value, expressed in pixels, can be displayed completely differently on screens with different pixel densities per inch.

![Example of the same image on phones with different pixel densities](https://ucarecdn.com/c0300e06-2288-49db-9402-3fe7f09507d5/)

*The same image on phones with different pixel densities*

To avoid this and ensure that text is rendered equally on devices with different pixel densities, scaled pixels (SP) must be used.

![Example of the scalable image on phones with different pixel densities](https://ucarecdn.com/38bcaa00-f749-470e-9593-7b313a7db740/)

*The scalable image on phones with different pixel densities*

Scaled pixels (SP) is a virtual pixel unit equal to one pixel on the screen with a density of 160 pixels per inch. This density is average for mobile devices, so it is considered "basic". When rendering the UI, the system independently translates the specified SP value into the corresponding number of real pixels on a particular device, taking into account the preferred font size set by the user on their device.

SP applies to basic data types such as Int, Double, and Float (for example, 10.sp or 12.5.sp).



```kotlin
Text(
    text = "Size does matter",
    fontSize = 20.sp
)
```



Output:

![Font size example](https://ucarecdn.com/703173a6-8e69-4bd3-8158-60cb9d1f3d6f/)

- **letterSpacing **— the name speaks for itself: this argument is for the distance between letters, specified similarly to `fontSize` in scaled pixels (SP). It will be good training to try it yourself.

- **lineHeight** — it's an argument that, in turn, sets the spacing between lines. It takes a TextUnit data type as an argument, like `fontSize` or `letterSpacing`. Also, for finer tuning, instead of "sp", it is sometimes better to use "em" — a unit of measurement relative to the font size. For example, with a font size of 10.sp, a `lineHeight` value of 1.5.em would be equivalent to 15.sp.



```kotlin
Text(
    text = "My desires are...\nUnconventional",
    fontSize = 10.sp,
    lineHeight = 1.69.em
)
```



Output:

![LineHeight example](https://ucarecdn.com/7a22730e-7ca6-4f61-9639-c973fb48d62f/)

> **Note:**

Note that the `fontSize`, `letterSpacing`, `lineHeight`, and `color` parameters are all set to `Unspecified` by default. This instance is used if you want to set them to their default value or have these options inherited from parent compostables.

- **fontStyle** provides the ability to display text in italics. To do this, you just need to use the already-created `FontStyle.Italic` instance.

- **fontWeight** is another parameter that sets the thickness of the glyphs. As an argument, you can use default instances of the `FontWeight` class, such as `Thin`, `SemiBold` and others. Or, you can pass an object of this class as an argument, specifying the desired fineness value in the range of 1 to 1000 in its constructor.



```kotlin
Text(
    text = "I'm not fat, just BOLD",
    fontWeight = FontWeight.Bold
)
```



Output:

![Font Weight example](https://ucarecdn.com/a1f1461c-11be-4aee-9546-58d181f4b428/)

- **fontFamily** is an important parameter that allows you to set a custom font to text, different from the one set for the current theme of your application. We will talk about typography and fonts of themes in future topics. For now, we'll just look at how you can add your font to the application.

To do this, you can simply copy it to the appropriate folder `font` of your application's resources (***res/font***), or create one if it doesn't exist yet. Keep in mind that the folder with fonts should only be called "font" and nothing else.

![Font resources folder](https://ucarecdn.com/46f51479-1a16-4b31-b4e2-3adf727d2d36/)

In this case, you can easily use font resources in the `Font` class constructor. In turn, the object of the `Font` class is passed as an argument to the `FontFamily` class constructor.



```kotlin
Text(
    text = "The night is dark",
    fontFamily = FontFamily(Font(R.font.cinzel_variable_font_wght))
)
```



Output:

![Font family example](https://ucarecdn.com/1c813070-782f-4e43-a576-76aa4fedda01/)

- **maxLines** — this parameter allows you to set the maximum number of lines of text displayed on the screen. All other lines will be omitted and not shown on the screen. However, in some cases, different ways of cutting off overflowed text are necessary so the composable `Text` has one more parameter — `overflow`.

- **overflow** — a parameter that determines what needs to be done with extra text. There are three basic instances of the `TextOverflow` class to choose from:

a) `Clip` — cuts off the text that goes beyond the limits in accordance with the established boundaries;

b) `Ellipsis` — is displayed at the end of the visible part of the text to indicate that the other part has been cut off;

c) `Visible` — displays overflow text outside the bounds set by the composables.

- **softWrap** — this parameter only determines whether the text should be wrapped to a new line when it reaches the bounds of the composable.



```kotlin
ChalkboardCustomSurface {
    Text(
        text = "I will not do anything bad ever again.".repeat(10),
        maxLines = 3,
        softWrap = true,
        overflow = TextOverflow.Ellipsis,
        color = Color.White,
    )
}
```



Output:

![SoftWrap, maxLines and overflow example](https://ucarecdn.com/35f2fc8b-b150-4ef4-bcb1-d779c74c24d5/)

- **textDecoration** is present to draw a horizontal line over or below the text.

- **style** is another parameter that takes a `TextStyle` object as an argument. Using this object, you can configure various text display options discussed above, as well as some other special parameters, such as text direction, shadow, geometric transformation, and so on. Let's take a look at an example of creating a shadow for our text:



```kotlin
Text(
    text = "Shadow baby",
    style = TextStyle(
        shadow = Shadow(
            color = Color.Black,
            offset = Offset(5f, 5f),
            blurRadius = 5f
        )
    )
)
```



Output:

![Text shadow example](https://ucarecdn.com/b2a5e816-a5de-4c36-823c-5d7e4b4d8ef2/)

## Multiple text styles

Of course, we can't create separate composables for each style. This is where `AnnotatedString` comes in handy. It's a string that can have any number of different styles. For the convenience of using `AnnotatedString`, this class already has a type-safe `buildAnnotatedString` builder.

`buildAnnotatedString` is a high-order function because its single parameter `builder` takes a function as an argument, so we can write it as follows:



```kotlin
Text(
    text = buildAnnotatedString {
        // some styles
    }
)
```



To display text on the screen, inside this function, you need to call the `append` function, which takes a string value as an argument. However, the text will be no different if we simply used an ordinary String value for the `Text` composable.



```kotlin
Text(
    text = buildAnnotatedString {
        append("unstyled text")
    }
)
```



Inside the lambda expression, you can use functions such as `withStyle` or `addStyle` to style a specific part of this text. Both functions have a `style` parameter that accepts arguments of types `SpanStyle` or `ParagraphStyle`, depending on which text display options you want to change.

Using the `addStyle` function, you need to specify exactly the start and end index of the range of string for which you want to apply the style. Of course, this approach is not the most convenient, so `withStyle` comes to the rescue. This function has a `block` parameter that takes a lambda expression as an argument. Inside it, we can call the `append` function mentioned above with a string value to which we want to apply the style. It's also worth noting that you can nest `withStyle` functions inside the lambda expression of another `withStyle` function if required, as shown below:



```kotlin
Text(
    text = buildAnnotatedString {
        withStyle(style = ParagraphStyle(lineHeight = 25.sp)) {
            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                append("Unbowed\n")
            }
            withStyle(
                style = SpanStyle(color = Color(0xFFFCBA43))
            ) {
                append("Unbent\n")
            }
            append("Unbroken")
        }
    }
)
```



Output:

![Annotated String example](https://ucarecdn.com/7a04e559-884f-4920-85e8-23957cc4c0db/)

## Conclusion

`Text` composable is one of the commonly used functions. In this topic, we've looked at most of the parameters that cover the basic needs of text styling — as for the rest, we will cover in the next topics. You will most likely need to change the text style to suit your needs often, so each option will come in handy. But be careful with importing entities and pay attention to the library with which you are importing them!

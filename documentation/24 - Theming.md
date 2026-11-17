# Theming

Theming allows developers to customize the look and feel of their apps. Theming in Jetpack Compose offers developers a toolset for creating visually stunning and consistent user interfaces. In this topic, we will explore theming in Jetpack Compose with Material Design 3.

## Basics of theming in Material Design

[**Material Design**](https://m3.material.io/) is a design system by Google used in Android apps to create high-quality user experiences. **Material Theming** systematically customizes an app's design to reflect a brand's identity. Jetpack Compose lets you build UIs with its implementation of Material Design, with the latest supported version being **Material Design 3**.

Material Design 3 has three subsystems: **color**, **typography**, and **shape**.

A color scheme consists of colors that map to different parts of the UI.

![Sample color scheme show various colors such as primary, secondary, and on primary](https://ucarecdn.com/d0893c0d-9461-4164-b117-b0ef164677e4/)

Typography refers to text styling, including size and font. For instance, we can define headings to use the font Roboto, be 22 sp (scale-independent pixels), and be bold.

Shapes define the physical appearance of containers like buttons and cards. For example, you can choose whether buttons have rounded or cut corners.

## Custom colors

To start working with Material Design 3 in Jetpack Compose, we need to add its dependency to the `build.gradle.kts` file. Make sure to remove any old Material dependencies if needed.



```kotlin
 implementation("androidx.compose.material3:material3")
```



By default, Android Studio places the theme code in the `ui.theme` package, and this is where we'll add Material Theming. The package contains themes (`Theme.kt`), typography (`Type.kt`), and color schemes (`Color.kt`).

For this demo, we have this simple `MainActivity.kt`. Here we create a card in the middle of the screen with the texts "HEADING" and "Paragraph" inside it. Below the card is a button that says "Click me". Note that we have wrapped our UI components with `MyApplicationTheme`. We'll get back to this detail soon.



```kotlin
package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                Scaffold { innerPadding ->
                    Column (
                        modifier = Modifier.padding(innerPadding).fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Card(
                        ) {
                            Text(
                                text = "HEADING",
                                modifier = Modifier.padding(24.dp),
                            )
                            Text(
                                text = "Paragraph",
                                modifier = Modifier.padding(24.dp),
                            )
                        }
                        Button(
                            onClick = {},
                        ) {
                            Text(text = "Click me")
                        }
                    }
                }
            }
        }
    }
}
```



Let's come up with a color palette. You can create a custom color scheme or build one with [Material Theme Builder](https://m3.material.io/theme-builder#/custom), which allows you to export to a Jetpack Compose theme file. Once we have picked our color palette, we can specify them in the `Color.kt` file. Here's an example.



```kotlin
package com.example.myapplication.ui.theme

import androidx.compose.ui.graphics.Color

val md_theme_light_primary = Color(0xFF3F5AA9)
val md_theme_light_onPrimary = Color(0xFFFFFFFF)
val md_theme_light_primaryContainer = Color(0xFFDBE1FF)
val md_theme_light_onPrimaryContainer = Color(0xFF00174C)
val md_theme_light_secondary = Color(0xFF785A00)
val md_theme_light_onSecondary = Color(0xFFFFFFFF)
val md_theme_light_secondaryContainer = Color(0xFFFFDF9D)
val md_theme_light_onSecondaryContainer = Color(0xFF251A00)
val md_theme_light_background = Color(0xFFFEFBFF)
val md_theme_light_onBackground = Color(0xFF1B1B1F)
val md_theme_light_surface = Color(0xFFBEA6C7)
val md_theme_light_onSurface = Color(0xFF1B1B1F)


val md_theme_dark_primary = Color(0xFFB4C5FF)
val md_theme_dark_onPrimary = Color(0xFF012978)
val md_theme_dark_primaryContainer = Color(0xFF244290)
val md_theme_dark_onPrimaryContainer = Color(0xFFDBE1FF)
val md_theme_dark_secondary = Color(0xFFF2BF42)
val md_theme_dark_onSecondary = Color(0xFF3F2E00)
val md_theme_dark_secondaryContainer = Color(0xFF5B4300)
val md_theme_dark_onSecondaryContainer = Color(0xFFFFDF9D)
val md_theme_dark_background = Color(0xFF1B1B1F)
val md_theme_dark_onBackground = Color(0xFFE4E2E6)
val md_theme_dark_surface = Color(0xFF2F2F42)
val md_theme_dark_onSurface = Color(0xFFE4E2E6)
```



We can refer to these colors in the `Theme.kt` file. To create a light color scheme, we assign colors from our color palette to the parameters of `lightColorScheme`. The same applies to the dark color scheme.

Next, in `MyApplicationTheme`, we'll specify that we want to use the dark color scheme when dark mode is on and the light color scheme otherwise. We use the `isSystemInDarkTheme()` function to check if the system is in dark mode. If it is, we use the dark color scheme; otherwise, we use the light one.



```kotlin
package com.example.myapplication.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = md_theme_light_primary,
    onPrimary = md_theme_light_onPrimary,
    primaryContainer = md_theme_light_primaryContainer,
    onPrimaryContainer = md_theme_light_onPrimaryContainer,
    secondary = md_theme_light_secondary,
    onSecondary = md_theme_light_onSecondary,
    secondaryContainer = md_theme_light_secondaryContainer,
    onSecondaryContainer = md_theme_light_onSecondaryContainer,
    background = md_theme_light_background,
    onBackground = md_theme_light_onBackground,
    surface = md_theme_light_surface,
    onSurface = md_theme_light_onSurface,
    surfaceVariant = md_theme_light_surface,
    onSurfaceVariant = md_theme_light_onSurface,
)

private val DarkColorScheme = darkColorScheme(
    primary = md_theme_dark_primary,
    onPrimary = md_theme_dark_onPrimary,
    primaryContainer = md_theme_dark_primaryContainer,
    onPrimaryContainer = md_theme_dark_onPrimaryContainer,
    secondary = md_theme_dark_secondary,
    onSecondary = md_theme_dark_onSecondary,
    secondaryContainer = md_theme_dark_secondaryContainer,
    onSecondaryContainer = md_theme_dark_onSecondaryContainer,
    background = md_theme_dark_background,
    onBackground = md_theme_dark_onBackground,
    surface = md_theme_dark_surface,
    onSurface = md_theme_dark_onSurface,
    surfaceVariant = md_theme_dark_surface,
    onSurfaceVariant = md_theme_dark_onSurface,
)

@Composable
fun MyApplicationTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (!useDarkTheme) {
        LightColorScheme
    } else {
        DarkColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
```



As we saw in `MainActivity.kt`, we wrap our UI components with `MyApplicationTheme`, a name we chose for our `MaterialTheme`. Inside `MyApplicationTheme`, we set up the Material 3 theme for our screen.

We can see what the default theme looks like if we don't specify the `colorScheme` parameter.

![UI from our sample app with default theming: light purple card and dark purple button both with default sans serif fonts.](https://ucarecdn.com/cced090a-1b8f-421d-ace9-a6479f995165/)

This is what our app looks like with our custom color palettes during light and dark mode, respectively. Note that by default, cards use the surface variant color and filled buttons use the primary color.

![Our app in light mode with custom color scheme: purple card, blue button](https://ucarecdn.com/e16a73cd-5a43-40c1-a8e7-bbd130c53191/)

![Our app in dark mode with custom color scheme: dark background, dark blue card, light blue button](https://ucarecdn.com/c5a5700b-861a-4aec-b65c-e91365335e50/)

## Dynamic theming

A new feature in Material 3 is **Dynamic theming**. Available for Android 12 and above, dynamic theming uses the user's wallpaper to derive and apply colors to apps.

Let's enable dynamic theming in our app, but if dynamic colors are unavailable, we'll fall back to our custom light and dark color schemes.



```kotlin
package com.example.myapplication.ui.theme

import android.os.Build
...
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
...
import androidx.compose.ui.platform.LocalContext

...


@Composable
fun MyApplicationTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val isDynamicColorAvailable = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val colors = when {
        isDynamicColorAvailable && useDarkTheme -> dynamicDarkColorScheme(LocalContext.current)
        isDynamicColorAvailable && !useDarkTheme -> dynamicLightColorScheme(LocalContext.current)
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colors,
        content = content
    )
}
```



We specified four behaviors. If dynamic color is available and the system is in dark mode, we use the dynamic dark color scheme. If dynamic color is available and the system is in light mode, we use the dynamic light color scheme. If dynamic color isn't available, we fall back to our custom dark or light color scheme depending on the system's mode.

As an example, suppose we have this wallpaper on our phone that has dynamic color:

![A phone's wallpaper](https://ucarecdn.com/5eba7f5e-afcc-4252-b7c7-231a1b3a9349/)

Our light and dark themes will look like the following after applying dynamic theming using this wallpaper:

![Our app in light mode with dynamic theming: the colors are derived from the phone's wallpaper](https://ucarecdn.com/b447c054-9c70-4e8d-8768-3ac087b75a4f/)

![Our app in dark mode with dynamic theming: the colors are derived from the phone's wallpaper](https://ucarecdn.com/f8ab7fba-d59f-487a-9295-52bc4429511c/)

## Custom typography

We can override the default typography by specifying parameters of the `Typography` class. We can add our custom typography to `Type.kt`.



```kotlin
package com.example.myapplication.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

val typography = Typography(
        headlineMedium = TextStyle(
                fontFamily = FontFamily.SansSerif,
                fontWeight = FontWeight.Normal,
                fontSize = 22.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
        ),
        bodyLarge = TextStyle(
                fontFamily = FontFamily.Serif,
                fontWeight = FontWeight.Normal,
                fontSize = 16.sp,
                lineHeight = 24.sp,
                letterSpacing = 1.5.sp
        )
)
```



Here, we specified the font family, font weight, font size, line height, and letter spacing, but there are more values in `TextStyle` that you can override. To use this typography, we need to add it to our `MaterialTheme`.



```kotlin
  MaterialTheme(
        colorScheme = colors,
        typography = typography,
        content = content
    )
```



Additionally, we need to apply the desired typography to our Text components in `MainActivity.kt`.



```kotlin
                            Text(
                                text = "HEADING",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.headlineLarge
                            )
                            Text(
                                text = "Paragraph",
                                modifier = Modifier.padding(24.dp),
                                style = MaterialTheme.typography.bodyLarge
                            )
```



This is what our app looks like after we apply our custom typography.

![Our app's UI with custom typography](https://ucarecdn.com/5417e4fc-012d-4eff-8df4-7f9a8e974077/)

But what if we want to use our own font family? We would need to add the font files to the `res/font` folder.

![Folder structure with font files inside res/font folder](https://ucarecdn.com/bed1e9e8-535b-43c6-b1ee-567ad02bebb4/)

Then we can use those files to define a font family.



```kotlin
val dancingScriptFamily = FontFamily(
        Font(R.font.dancing_script_regular, FontWeight.Light),
        Font(R.font.dancing_script_medium, FontWeight.Normal),
        Font(R.font.dancing_script_semibold, FontWeight.Medium),
        Font(R.font.dancing_script_bold, FontWeight.Bold)
)
```



We can refer to this font family in our custom typography.



```kotlin
val typography = Typography(
...
        bodyMedium = TextStyle(
                fontFamily = dancingScriptFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 14.sp
        )
)
```



Let's apply this to our button.



```kotlin
                        Button(
                            onClick = {},
                        ) {
                            Text(
                                text = "Click me",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
```



Here's what the light and dark UIs look like now.

![Our app in light theme with custom typography and own font files](https://ucarecdn.com/0403fc60-b0fe-490d-90b7-305e9c22f2fc/)

![Our app in dark theme with custom typography and own font files](https://ucarecdn.com/746709b3-32fe-49d3-a988-79cafd78e886/)

## Custom shapes

Let's create a new file called `Shape.kt`. Here we can customize the shape scale. The shape scale parameters require a `CornerBasedShape`, such as `CutCornerShape` or `RoundedCornerShape`, to be passed in.



```kotlin
package com.example.myapplication.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.shape.CutCornerShape;
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val shapes = Shapes(
    medium = CutCornerShape(16.dp),
    large = RoundedCornerShape(24.dp)
)
```



Let's pass `shapes` to our `MaterialTheme`.



```kotlin
    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
```



By default, buttons have a fully rounded corner shape style, and cards use the medium rounded corner shape style. Let's apply a different shape to the button.



```kotlin
                        Button(
                            onClick = {},
                            shape = MaterialTheme.shapes.extraSmall
                        ) {
                            Text(text = "Click me")
                        }
```



Here's what our final UIs look like.

![Our app in light theme with custom shapes](https://ucarecdn.com/cf29e0be-dc5d-4e99-81b1-da872d4cb127/)

![Our app in dark theme with custom shapes](https://ucarecdn.com/a64b9b6a-0e24-4ced-96c0-54580a43c7b9/)

## Conclusion

In our discussion about Material 3 theming in Jetpack Compose, we covered the fundamentals of UI customization, including colors, typography, and shapes. Theming is crucial for creating consistent and visually appealing UIs across various screens and devices. By mastering Material 3 theming, you can enhance your UIs and provide captivating and immersive app experiences for your users.

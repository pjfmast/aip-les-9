# Shapes in Compose

Shapes are fundamental building blocks of any user interface, yet, they are often overlooked. They play a crucial role in shaping a user experience that is both aesthetically pleasing and functional.

In this topic, we will explore the power of shapes in Jetpack Compose. We'll dive into the built-in options like circles, rounded corners and cut corners, before venturing into creating custom shapes to unlock truly unique design possibilities.

## Built-in shapes

Compose offers a variety of pre-defined shapes that can be readily used in your app's design. Let's explore these shapes:

- `CircleShape`**:**

As the name suggests, this creates a circle. Use it for scenarios where you need a circular element, such as profile pictures, or some decorative elements:



```kotlin
Image(
    painter = painterResource(R.drawable.profile_picture),
    contentDescription = "Profile Picture",
    contentScale = ContentScale.Crop,
    modifier = Modifier
        .clip(CircleShape) // Apply the CircleShape
        .size(100.dp)
)
```



![Kitten sitting on a blanket.](https://ucarecdn.com/561d919e-7a07-49ea-9722-2e92f4deb7c5/)

You might have noticed we don't have parenthesis after the shape's name. This is because, behind the scenes, it is just a variable! This applies to one other built-in shape we'll explore in this topic.

When working with `CircleShape`, there's a key point to remember: for the circle to be truly "perfect," the composable it's applied to should have equal width and height - essentially, it should be a square. If the composable has unequal dimensions, `CircleShape` will result in an oval or elliptical shape.

-

`RoundedCornerShape`**:**

This allows you to create rectangles with rounded corners. This versatile shape can be customized to achieve a wide range of looks from subtly rounded buttons to pill-shaped containers:



```kotlin
Button(
    onClick = { /* Do something */ },
    shape = RoundedCornerShape(8.dp) // Round corners by 8.dp
) {
    Text("Click me")
}
```



![](https://ucarecdn.com/54d837b2-e210-46b7-b833-e53197c6fb28/)

The `8.dp` argument specifies the radius of each corner, creating a smooth, rounded appearance. `RoundedCornerShape` offers various ways to control the corner radius:

**Uniform corner radius:** Pass a single `size: Dp`, `corner: CornerSize`, `size: Float`, or `percent: Int` (values ranging from 0 to 100) value to apply the same radius to all corners. In the code snippet above we passed a single `Dp` value.

We mentioned `CornerSize` as one of the ways to specify the appearance of rounded corners. But what exactly is this `CornerSize`? Simply put, it allows you to specify the size of a corner (as the name suggests), much like all of the previously mentioned ways. You can pass in a single `Dp`, `Float`, or `Int` (as percent) value:



```kotlin
RoundedCornerShape(CornerSize(8.dp))
```



- **Individual corner radii:** Use the `topStart`, `topEnd`, `bottomEnd`, and `bottomStart` parameters passing either `Dp`, `Float`, or `CornerSize` values to specify different radii for each corner. You can also pass percent values as integers using `topStartPercent`, `topEndPercent`, and so on.

The parameters are optional and default to 0 if not specified except for the overload that takes in `CornerSize` arguments, in which case, you must pass in all the arguments.

Knowing this, you can easily create a shape with rounded top corners and sharp bottom corners:



```kotlin
RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
```



![](https://ucarecdn.com/0c6402b0-3094-4a21-9450-1e0a1b8fb6bc/)

> **Note:**

Here's an interesting tidbit about `CircleShape`: it is essentially a `RoundedCornerShape` with an argument of 50 as a percent!

- `RectangleShape`**:**

This represents a simple rectangle with sharp corners. It's a fundamental shape often used for containers, backgrounds, or whenever a straight-edged look is desired:



```kotlin
OutlinedCard(
    shape = RectangleShape
) {
    // Card content
}
```



![Wooden bridge in a forest with text overlay at the bottom.](https://ucarecdn.com/922da66c-2091-4195-99d5-c69a21bcde74/)

- `CutCornerShape`**:**

This allows you to create rectangles with cut corners, adding a distinctive and modern touch to your UI elements. Mirroring `RoundedCornerShape`, `CutCornerShape` provides the exact same parameters and overloads, giving you identical control over corner customization:



```kotlin
OutlinedCard(
    shape = CutCornerShape(topStart = 24.dp)
) {
    // Card content
}
```



![](https://ucarecdn.com/4df7eb97-aedf-44b4-9a41-086aec7909d7/)

Here, we apply `CutCornerShape` to an `OutlineCard` composable, cutting the top-start corner by `24.dp` and leaving the remaining corners sharp.

These built-in shapes provide a solid foundation for designing your app's UI. However, your design vision may necessitate the creation of custom shapes tailored to your specific needs. We'll explore that next.

## Custom shapes

Sometimes you need a unique shape that perfectly fits your design requirements. That's where custom shapes come in. In Compose, you have the freedom to define and implement your own shapes, opening up a world of creative possibilities.

To create a custom shape, you need to extend the `Shape` interface and implement the `createOutline` function. This function is responsible for defining the shape's outline, essentially drawing its boundaries:



```kotlin
// Import `Shape` from here
import androidx.compose.ui.graphics.Shape

class MyCustomShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        // Define your custom shape here
    }
}
```



Let's break down the parameters of the `createOutline` function:

- **size:** The `Size` object provides the width and height of the area where the shape will be drawn.

- **layoutDirection:** This indicates the layout direction (left-to-right or right-to-left) of the composable using the shape. You can use this information to create shapes that adapt to different language directions.

- **density:** This `Density` object provides information about the screen density, which can be useful for scaling your shape appropriately across different devices.

Within the `createOutline` function, you'll use the `Outline` class and its various functions to describe the shape's contour. The `Outline` class acts as a container for your shape's path data.

Before we start creating custom shapes, you need to understand the coordinate system. Compose employs a Cartesian coordinate system, where the position of any point on the screen is defined by two values: X and Y (as Floats). The X-axis runs horizontally, with positive values increasing towards the right, while the Y-axis runs vertically, with positive values increasing downwards.

The origin (0, 0) of this coordinate system is located at the top-left corner of the composable you're working on:

![Blank grid graph with labeled X and Y axes.](https://ucarecdn.com/554ce671-349d-4094-bf88-4e3d8946a4b2/)

Let's start by creating a simple rectangle shape. While this may seem similar to the built-in `RectangleShape`, we get more flexibility. For instance, we can create a rectangle shape that's half the size of the composable it's applied to:



```kotlin
class HalfSizeRectangleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val halfWidth = size.width / 2 // size.width is a Float
        val halfHeight = size.height / 2 // size.height is also a Float
        val rect = Rect(
            left = halfWidth / 2,
            top = halfHeight / 2,
            right = halfWidth + halfWidth / 2,
            bottom = halfHeight + halfHeight / 2
        )
        return Outline.Rectangle(rect)
    }
}
```



![Blue square centered inside a black square with a yellow border.](https://ucarecdn.com/c98f82b0-c1b6-4c4b-8fa4-d4654fa344d3/)

> **Note:**

Note: The golden border is simply for illustrative purposes and shows the actual size of the composable.

The `Outline` class has three nested classes, the first of which is used in the code snippet above, `Outline.Rectangle`. This class allows you to create rectangular outlines with sharp corners. It takes a single argument, a `Rect` object, which defines the position and dimensions of the rectangle as demonstrated above.

> **Note:**

The `Rect` function comes with multiple variations. To view them in Android Studio, position your cursor within the function’s parentheses and hit **Ctrl + P**. This will display the different overloads you can use. While we won’t detail every single one, you can apply this method to explore overloads for other functions mentioned.

The custom shape `HalfSizeRectangleShape` creates a rectangle centered within the composable's bounds with half its width and height. You can further customize this based on layout direction, creating different shapes for left-to-right, and right-to-left layouts.

Now, let's add some curves. `Outline.Rounded`, the second nested class, allows us to create rounded rectangles just like the built-in `RoundedCornerShape`, but with more flexibility. It takes a `RoundedRect` object as its argument, which defines the rectangle and its corner radii. Similar to `Rect`, you can construct a `RoundedRect` instance by providing the necessary position, dimensions, and corner radii information:



```kotlin
class CustomRoundedRectangleShape(private val cornerRadius: Dp) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val cornerRadiusPx = with(density) {
            cornerRadius.toPx()
        }
        val roundedRect = RoundRect(
            left = 0f,
            top = 0f,
            right = size.width,
            bottom = size.height,
            radiusX = cornerRadiusPx,
            radiusY = cornerRadiusPx
        )
        return Outline.Rounded(roundedRect)
    }
}
```



![Blue square with rounded corners.](https://ucarecdn.com/953f8071-e0ec-431f-9eb3-780605ce1715/)

In this example, we take a `cornerRadius` argument as `Dp` in the constructor, allowing us to specify the desired corner radius when using the shape. An important aspect to remember is that while the shape definition necessitates values in pixels (as a Float), we initially provide these measurements in `Dp` for ease of use. The conversion is handled by the `density` parameter. This ensures that the corner radius is scaled appropriately for different screen densities, providing a consistent visual appearance across all devices.

The real magic of custom shapes lies in `Outline.Generic` (the third and last nested class), and the `Path` interface. Imagine drawing a shape on paper with a pen. You move the pen from one point to another, creating straight lines or curves, and finally close the shape. `Path` in Compose works similarly. You can do so using methods like:

- `moveTo(x, y)`**:** Moves the "drawing pen" to the specified coordinates.

- `lineTo(x, y)`**:** Draws a straight line from the current position of the path to the specified coordinates.

- `quadraticBezierTo(x1, y1, x2, y2)`**:** Draws a [quadratic Bézier](https://blog.maximeheckel.com/posts/cubic-bezier-from-math-to-motion/#:~:text=we%20described%20above.-,Quadratic%20B%C3%A9zier,-Let%27s%20add%20another) curve, which is a smooth curve defined by a start point, an end point, and a control point.

- `cubicTo(x1, y1, x2, y2, x3, y3)`**:** Draws a [cubic Bézier](https://blog.maximeheckel.com/posts/cubic-bezier-from-math-to-motion/#:~:text=%7D%3B-,Cubic%20B%C3%A9zier,-Now%2C%20if%20we) curve, which offers more control over the curve's shape with two control points.

- `close`**:** Closes the current path, connecting the last point to the starting point.

Additionally, you can leverage utility functions like `addPath`, `addOval`, `addArc`, and many more useful [utility functions](https://developer.android.com/reference/kotlin/androidx/compose/ui/graphics/Path) to help create the shape you are aiming for. The methods listed above are enough to get you started. For instance, to create a triangle:



```kotlin
class TriangleShape : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        val path = Path().apply {
            moveTo(size.width / 2f, 0f) // Move to top center
            lineTo(size.width, size.height) // Draw line to bottom right
            lineTo(0f, size.height) // Draw line to bottom left
            close() // Close the path
        }
        return Outline.Generic(path)
    }
}
```



![Blue triangle on a black background.](https://ucarecdn.com/3716a9f1-1d8e-4bf3-8644-17ca594e2824/)

Creating complex shapes often involves some math, especially for curves. Bézier curves, for example, use control points to define the curvature of the path. Understanding basic geometric principles and mathematical functions can greatly expand your custom shape possibilities.

With `Outline.Generic` and `Path`, you are limited only by your imagination. Explore, experiment, and create unique shapes that bring your design to life.

Compose offers a handy class called `GenericShape` that simplifies the creation of custom shapes with paths. It allows you to define a shape using a lambda expression that receives a `Path` object and takes in size and layout direction arguments. This can be particularly useful when you want to create a one-time custom shape without defining a whole new class or when simply creating custom shapes using paths:



```kotlin
Box(
    modifier = Modifier
        .size(100.dp)
        .clip(GenericShape {size, layoutDirection ->
            // Bottom right quadrant shape
            moveTo(size.width, 0f)
            quadraticBezierTo(size.width, size.height, 0f, size.height)
            lineTo(0f, 0f)
        })
        .background(Color.Blue)
)
```



![Blue circle on black background](https://ucarecdn.com/14c349f8-32a9-48fe-9838-b3cd52ba454c/)

## Conclusion

Shapes play a key role in the design of an app's interface, affecting how it feels and looks. Jetpack Compose offers a variety of ready-to-use shapes and allows you to create your own to fit your design needs.

You can use simple shapes like circles and rectangles, or get creative with shapes that have rounded or cut corners. Compose gives you the tools to make your app not just look good, but also be easy and intuitive to use. You have the freedom to use the standard shapes or design unique ones with the Path API.

Using shapes well in Compose can make your app more attractive and user-friendly. It's up to you to shape the experience of your app!

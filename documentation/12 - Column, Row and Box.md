# Column, Row and Box

In most cases, application screens contain a huge number of UI elements and all of them must be arranged in a specific order. At the same time, this order should be preserved regardless of the screen size of the user's device. In this topic, we'll look at a few basic ways of arranging composables in an application.

## Arrangement of UI elements

Let's assume we want to create an account registration screen for users that contains text information about this screen, as well as two text fields for the user to enter a username and password. The code for this screen will be something like this (the arguments of value parameters have been hardcoded for simplicity of the example):



```kotlin
@Composable
fun RegisterScreen() {
	Text("Register account:")
	OutlinedTextField(value = "Login", onValueChange = ::setLoginField)
	OutlinedTextField(value = "Password", onValueChange = ::setPasswordField)
}
```



However, you will definitely not like the result of this screen:

![Element placement without arrangement](https://ucarecdn.com/9e56d13c-119e-48b9-95ba-a7653ea2a90a/)

This is because we haven't provided any information on how to arrange all these elements on the screen. In this case, they will be drawn one on top of the other in the order they are called in the code, that is, the most recently called composable will be drawn on top of all others. To avoid this, we need to specify how these elements must be located on the screen, and for this, the `Column`, `Row` and `Box` composables will come to our aid.

![Example of Column, Row and Box](https://ucarecdn.com/78f24874-2df0-420b-91cc-a18507969692/)

## Content arrangement

Well, let's now modify our code and add a `Column` composable to it. All composables that we want to lay out in a column should be added to the lambda expression of the content parameter of the `Column` composable, as shown below:



```kotlin
@Composable
fun RegisterScreen() {
	Column() {
    	    Text("Register account:")
    	    OutlinedTextField(value = "Login", onValueChange = ::setLoginField)
      	    OutlinedTextField(value = "Password", onValueChange = ::setPasswordField)
	}
}
```



Output:

![Column composable](https://ucarecdn.com/62487567-200a-4571-a33e-091ce8bc7bcc/)

Now the result looks much better than before, doesn't it? However, it can be improved by using the `verticalArrangement` and `horizontalAlignment` parameters to fine-tune the position of elements.

`verticalArrangement` accepts arguments of `Arrangement.Vertical` and `Arrangement.HorizontalOrVertical` interfaces. Let's use the `spacedBy()` function with a value of 20 DP, for example, and run the app on a real device. You can use any other entities that are suitable in your particular case.

> **Note:** If you don't want to run the application on a real device, or if you don't have the ability to, you can use the following `Preview` annotation: `@Preview(showSystemUi = true)`



```kotlin
@Composable
fun RegisterScreen() {
	Column(
    	modifier = Modifier.fillMaxSize(),
    	verticalArrangement = Arrangement.spacedBy(20.dp),
	) { … }
}
```



For clarity, in this example, we used the `Modifier.fillMaxSize()` function, which sets the size of this composable to match the device's screen size the app is running on. The `Modifier` object and its entities deserve a separate topic, in which we will consider its purpose and capabilities.

The result is better, but still not perfect:

![Example of Arrangement.spacedBy function](https://ucarecdn.com/9be3e9d1-32a4-4fde-a614-a7137d18697c/)

## Content alignment

Composables must be aligned both **vertically** and **horizontally**. For vertical alignment, you can use the alignment parameter of the `spacedBy()` function mentioned above or another special `aligned()` function. In the case of horizontal alignment, you must use a separate `horizontalAlignment` parameter of the `Column` composable. The listed functions and parameters accept entities of the `Alignment.Vertical` or `Arrangement.Horizontal` interfaces, such as `Alignment.CenterVertically` and `Alignment.CenterHorizontally`:



```kotlin
@Composable
fun RegisterScreen() {
	Column(
    	modifier = Modifier.fillMaxSize(),
    	verticalArrangement = Arrangement.spacedBy(20.dp, Alignment.CenterVertically),
    	horizontalAlignment = Alignment.CenterHorizontally
	) { … }
}
```



Output:

![Example of content alignment](https://ucarecdn.com/533b4cf2-f7e5-4520-8f5d-4c0a005d7b73/)

Now the registration screen looks quite acceptable, only the buttons are missing, but for now, we will not add them for simplicity. Similarly, UI elements can be arranged in a row using the corresponding composable `Row`. In order not to repeat, you can try out this composable in use yourself. The only difference is that `Row` uses the `Alignment` entities for the vertical and the `Arrangement` entities for the horizontal, like here:



```kotlin
@Composable
fun TailsRow() {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(4) { count ->
            Tail(count)
        }
    }
}
```



Output:

![Example of Row composable](https://ucarecdn.com/72c3bdf5-7fd9-48f4-8004-99d32c4d17a1/)

## Arrangement options

Let's take a moment to discuss the differences between the predefined options for the arrangement of the child elements of the `Column` and `Row` composables. For better understanding, it will be enough to look at how they change when changing the width of the `Row` in the following example:

![Various arrangement options of Row](https://ucarecdn.com/b22260c4-c2b9-4dcc-b4d6-56ae91882cb8/)

In the same way, you can set the vertical arrangement for the `Column` composable. The result will be similar, but vertically:

![Various arrangement options of Column](https://ucarecdn.com/b79abd82-7960-440e-9a35-2b909e20f09a/)

## Box

However, it is not always necessary to place UI elements in a horizontal or vertical sequence. Sooner or later, you will need to place some composables on top of others. Here you can't do without the `Box` composable.

Let's assume that we already have a composable `DefaultAvatar` and also a `Counter` composable.

![DefaultAvatar composable](https://ucarecdn.com/c0a06c01-eac3-48ad-a510-1575f22ea17b/)

![Counter composable](https://ucarecdn.com/cdb83621-cbf3-4141-97b3-94c4b1e7f85d/)

We need to place the last one in the upper right corner of `DefaultAvatar`. To do this, both composables must be added to the lambda expression of the content parameter of the `Box`. In addition, a variable from the companion object of `Alignment` interface must be passed to the `contentAlignment` parameter (in our case it is the `TopEnd` variable):



```kotlin
@Composable
fun AvatarWithCounter() {
	Box(contentAlignment = Alignment.TopEnd) {
  	    DefaultAvatar(name = "Superman")
        Counter(value = 25)
	}
}
```



The result will be something like this:

![AvatarWithCounter composable](https://ucarecdn.com/7abbf885-963f-4160-ae78-4a7b502dfaf0/)

Note that the order in which the composables are called is important: those that are called later will be displayed on top of those composables that were called before.

## Conclusion

In this topic, we've covered three basic composables: `Column`, `Row`, and `Box`. Each of these composables can be fine-tuned to position UI elements on the screen according to your design, regardless of screen size or aspect ratio. You can only achieve the desired result by properly combining all three of these composables, calling one within the other an unlimited number of times. Practice and you will succeed!

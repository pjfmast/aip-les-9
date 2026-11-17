# TextField Composable

In most apps, whether it's a simple note app or a full-fledged messenger, user interaction is required one way or another, from the signup/login screen to the ability to send messages, search for notes, and so on. In this topic, we will look at a way to receive incoming data from the user via a text field.

## TextField overview

To accept input from the user in console applications, we use the `readln()` function you are already familiar with. However, in a real application, you won't use it. To receive data from the user, Jetpack Compose provides `TextField` and `OutlinedTextField` composables, which allow the user to enter text or numbers. Both composables contain the same set of parameters and provide the same functionality for developers and users. The differences are only in the appearance of these composables.

![Example of TextField composable](https://ucarecdn.com/4d4d1a9a-c3b5-4757-b093-5eadc1eddbdf/)

![Example of OutlinedTextField composable](https://ucarecdn.com/832b3e5d-122a-417d-85be-2b25b1d19bd0/)

Now let's take a look at the parameters that these composables provide us with in the `OutlinedTextField` example:



```kotlin
@Composable
fun OutlinedTextField(
    value: String, // or TextFieldValue
    onValueChange: (String) -> Unit, // or (TextFieldValue) -> Unit
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    textStyle: TextStyle = LocalTextStyle.current,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions(),
    singleLine: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    shape: Shape = OutlinedTextFieldDefaults.shape,
    colors: TextFieldColors = OutlinedTextFieldDefaults.colors()
)
```



> **Warning:**

Note that some default arguments may differ depending on which library the composables are from: Material (`androidx.compose.material.*`) or Material 3 (`androidx.compose.material3.*`).

Everything we will talk about next applies to both `TextField` and `OutlinedTextField`, but for convenience, we'll refer to both as `TextField`.

## The value of TextField

The required parameter `value` of the composable `TextField` must contain the text that the user enters (or default text). As an argument, this parameter can take a `String` or a `TextFieldValue`, which in turn can also take values of type `String` and the already learned `AnnotatedString`.

You're probably wondering why you can't just use an `AnnotatedString` as an argument, like a `Text` composable? Of course, it would be logical. However, a `TextFieldValue` instance provides significantly more options for handling user-entered text. First of all, `TextFieldValue` contains information not only about the text the user entered but also about the current cursor position and the range of text the user selected. For convenience in processing the specified data, this class contains several functions, such as `getSelectedText()`, `getTextBeforeSelection()`, or `getTextAfterSelection()`.

The second and last required parameter of the composable `TextField` is `onValueChange`, which takes a lambda expression or a function with a single parameter of the `String` type. It's the callback that triggers when the user changes input values in `TextField` and returns the changed value, which you need to assign to the `value` parameter. Let's do it!



```kotlin
var login = TextFieldValue("")

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



If you tried to run this code and enter text in the emulator or on the device and nothing happens, this is absolutely normal. Those who are already somewhat familiar with Jetpack Compose know the reason.

We'll discuss this feature in one of the future topics on Recomposition. For now, to make it work, you need to pass a `String` or `TextFieldValue` as an argument to the `mutableStateOf()` function, which returns an object of the `MutableState` type. And to get or change the value of this instance, call its `value` variable as shown below:



```kotlin
var login = mutableStateOf(TextFieldValue(""))

@Composable
fun LoginField() {
    TextField(
        value = login.value,
        onValueChange = { input ->
            login.value = input
        }
    )
}
```



It's useful to note that you should use function references instead of lambda expressions to improve code quality. This will increase code readability and ensure the convenience of its further maintenance, thanks to the separation of concerns improved by the use of function references.



```kotlin
var login = mutableStateOf(TextFieldValue(""))

fun setLoginField(value: TextFieldValue) {
    login.value = value
}

@Composable
fun LoginField() {
    TextField(
        value = login.value,
        onValueChange = ::setLoginField
    )
}
```



## Hints and icons

The `TextField` composable has several options that let you visually highlight each text field with icons and text that serve as a hint to users and help them easily identify what each text field is responsible for. To do this, optional parameters are provided, such as `label` and `placeholder`, which accept a lambda expression that contains composables. In addition, `TextField` also has the `leadingIcon` and `trailingIcon` parameters, which also accept a lambda expression, often with the `Icon` composable, and display them at the beginning and end of the text box, respectively. We will talk about them in future topics, and below is just an example of using these parameters and the result obtained:



```kotlin
@Composable
fun LoginField() {
    OutlinedTextField(
        value = login.value,
        onValueChange = ::setLoginField,
        label = { Text("Label") },
        placeholder = { Text("Placeholder") },
        leadingIcon = { Icon(Icons.Default.Email, null) },
        trailingIcon = { Icon(Icons.Default.Clear, null) },
    )
}
```



Output:

![Example of Hints and Icons of TextField composable](https://ucarecdn.com/4c51b0b2-b482-4917-924e-7d75add3cee0/)

## TextField customization

Now let's assume that we need to make a password field. However, we don't want other people to see the content of this field. Jetpack Compose developers have already taken care of this for us by adding the `visualTransformation` parameter of the `TextField` composable. In this case, we can use the existing `PasswordVisualTransformation()` class that inherits from the `VisualTransformation` interface and, under the hood, replaces the text with "•" (char code `'\u2022'`).



```kotlin
@Composable
fun PasswordField() {
    OutlinedTextField(
        value = password.value,
        onValueChange = ::setPasswordField,
        leadingIcon = { Icon(Icons.Default.Lock, null) },
        visualTransformation = PasswordVisualTransformation()
    )
}
```



Output:

![Example of Password TextField](https://ucarecdn.com/7dbde330-007c-4274-808f-05379a10ea25/)

In addition, we can change the appearance of the `TextField`, namely its **colors** and **shapes**, using the parameters of this composable with the same name. By default, colors of type `TextFieldColors` take the value from the `TextFieldDefaults` object for `TextField`, or from the `OutlinedTextFieldDefaults` object for `OutlinedTextField`. Using the named parameters of the called `TextFieldDefaults.colors()` or `OutlinedTextFieldDefaults.colors()`, we can subtly change individual values without having to respecify all other colors. For example, you can change the color of `leadingIcon` like this:



```kotlin
@Composable
fun PasswordField() {
    OutlinedTextField(
        …
        colors = OutlinedTextFieldDefaults.colors(
            focusedLeadingIconColor = Color(0xFF007BFF),
            unfocusedLeadingIconColor = Color(0xFF007BFF)
        )
    )
}
```



Output:

![TextField Icons coloring example](https://ucarecdn.com/39898809-ac25-4a1e-8ba8-82e9036f0ae3/)

To change the shape of a `TextField`, you can use functions such as `RoundedCornerShape()`, `CutCornerShape()`, or others, specifying the size in DP for each corner separately or for all corners together. The DP (Density Independent Pixel) is a virtual unit based on the physical density of the screen, similar to the SP unit, but it doesn't depend on the preferred font size the user sets in the device settings. So, we can change the shape of `TextField` as follows:



```kotlin
@Composable
fun PasswordField() {
    OutlinedTextField(
        …
        shape = RoundedCornerShape(15.dp)
    )
}
```



Output:

![TextField shape example](https://ucarecdn.com/7df21437-0df8-4af4-8f1e-ec5f7c0afa0a/)

It's worth noting that an important element when creating a `TextField` is the correct configuration of the `keyboardOptions` and `keyboardActions` parameters. But you'll learn about this in a separate topic. We recommend you familiarize yourself with the remaining parameters and the possibilities they represent and try them yourself. Some of them will already be familiar to you from the topic on the `Text` composable.

> **Note:**

If you want to improve your UI/UX design skills, you can check out the basics of [Input fields and field labels](https://hyperskill.org/learn/step/15447).

## Conclusion

So, let's sum it up:

- In this topic, we covered the main features of the `TextField` and `OutlinedTextField` composables;

- Identified the benefits of `TextFieldValue` over using a regular `String` value;

- Got acquainted with the `MutableState` interface, which you need to use to work with mutable values in Jetpack Compose;

- Explored ways to change the appearance of a `TextField` and its elements.

Now, let's consolidate the material we covered with some practice. Good luck!

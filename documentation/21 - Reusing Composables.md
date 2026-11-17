# Reusing Composables

In **Jetpack Compose**, composable functions are used to render UI elements. Sometimes, you need to display the same element in several locations, each with different sizes and behaviors. This could make you wonder if you can use composables in different places with custom functionalities.

This topic focuses on creating reusable composable functions by introducing the concepts of modifiers and state hoisting. It explains how these concepts make composables more reusable. Additionally, it explores how slotting and the use of generic functions can improve reusability.

## Understanding the benefits of reusable components

Jetpack Compose significantly improves modularity and reusability. In this section, we'll explore how isolating a single text field into a separate composable helps improve code readability and organization. Imagine a scenario where you need a login field for users to input their usernames on the login screen. One approach is to call the `TextField` composable within the `LoginScreen` function, just like a basic setup:



```kotlin
@Composable
fun LoginScreen() {
    var username by remember {
        mutableStateOf("")
    }
    TextField(
        value = username,
        onValueChange = { username = it },
        label = { Text(text = "username") }
    )

    // other fields...
}
```



This setup works well, as shown in the following UI:

![A simple text field showing a username label](https://ucarecdn.com/0c5acc3e-ed8a-4138-a4a8-4dbb32bc790a/-/preview/-/enhance/100/)

However, the code becomes complex and hard to read as you introduce additional features like `placeholders` and `leadingIcon`:



```kotlin
@Composable
fun LoginScreen() {
    var username by remember {
        mutableStateOf("")
    }
    TextField(
        value = username,
        onValueChange = { username = it },
        label = { Text(text = "username") },
        placeholder = { Text(text = "e.g., john_doe123") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "person icon"
            )
        }
    )

    // other fields...
}
```



![A text field showing a label and a placeholder and a leading icon](https://ucarecdn.com/28ed34ec-1970-4c18-a39f-cf25da64d4e0/)

As you add more features to the screen, especially with additional text fields, the code becomes redundant, leading to extra boilerplate. This redundancy makes understanding the purpose of each `TextField` on the screen more difficult. The issue gets worse when creating a **sign-up screen** that requires a username field with similar functionality. Reusing the same repetitive code is not the best solution.

In response to this challenge, a better option emerges: creating a separate composable named `UserNameField`:



```kotlin

@Composable
fun LoginScreen() {
    var username by remember {
        mutableStateOf("")
    }
    UserNameField(username = username) {
        username = it
    }

    // other fields...
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserNameField(
    username: String,
    onUserNameChanged: (String) -> Unit
) {
    TextField(
        value = username,
        onValueChange = { onUserNameChanged(it) },
        /* ... */

    )
}
```



Doing this makes the code more modular. You can call the `UserNameField` from various composables, which improves the overall readability. While this separation improves the organization of the code, a new challenge arises. The caller function (e.g., `LoginScreen`) cannot affect the layout of the `UserNameField`. This limitation becomes apparent when different composables need diverse alignments or sizes for the text field. We can address these issues by introducing the concept of modifiers. Modifiers provide a way to customize the appearance and behavior of the composable according to the specific requirements of the caller function.

## Enhancing reusability with modifiers

The visual aspects and behavior of composables are determined by parameters, modifiers, or a combination of both. **Modifier** is a built-in object in Compose that stores configuration settings for composables. It provides various methods for configuring properties like borders, padding, background, size requirements, event handlers, and gestures. After declaring a Modifier, you can pass it to other composables to modify their appearance and behavior. For example,

- `height()`

- `fillMaxHeight()`

- `fillMaxSize()`

These modifiers control the height and size of the corresponding UI element. Meanwhile, `background` can set a background color and shape, and `clickable` allows the user to interact with the composable function by clicking on the UI element. In principle, these modifiers can be assigned to one of several categories, such as **Actions** (`draggable`), **Alignment** (`alignByBaseline`), or **Drawing** (`paint`).
Jetpack Compose provides an extensive selection of modifiers. You can find a list of modifiers, grouped by category, at [List of Compose modifiers](https://developer.android.com/jetpack/compose/modifiers-list).

> **Note:**

You define a modifier chain by combining several modifiers; for example:



```kotlin
Modifier.fillMaxSize().clickable { }
```



To get more familiar with modifiers, let's go back to our `UserNameField` example to see how a composable can receive a modifier parameter:



```kotlin
@Composable
fun UserNameField(
    modifier: Modifier = Modifier,
    username: String,
    onUserNameChanged: (String) -> Unit
) {

    TextField(
        modifier = modifier,
        value = username,
        onValueChange = { onUserNameChanged(it) },
        /* ... */

    )
}
```



This way, a composable can receive a modifier chain from the caller. If none are provided, `Modifier` acts as a new empty chain. In both scenarios, the composable can add additional modifiers, such as `padding()` in the previous code snippet.

Compose introduces layout-specific modifiers through their respective content scopes. For instance, the `Box` layout provides the `matchParentSize()` modifier; however, its usage is only within the scope defined by the `Box` itself:



```kotlin
Box() {
    // inside Box scope

    Text(
         // matchParentSize is only available from BoxScope
         modifier = Modifier.matchParentSize(),
         text = "Username"
    )
}
```



It's important to note that `Box` isn't the only layout that provides its own set of modifiers. The `LazyRow/LazyColumn` layouts provide the `fillParentMaxSize()` modifier within their item scope, `ConstraintLayout` introduces `constrainAs()`, and similar scoped modifiers are present across various layouts. You should carefully consider these modifiers to ensure proper scoping and adherence to Compose's design principles of layout.

Now, you can call `UserNameField` with different measurements and appearances:



```kotlin
@Composable
fun LoginScreen() {
    var identifier by remember {
        mutableStateOf("")
    }
    var username by remember {
        mutableStateOf("")
    }

    Column {
        UserNameField(
            modifier = Modifier
                .width(400.dp)
                .border(BorderStroke(width = 2.dp, color = Color.Black)),
            username = identifier
        ) { identifier = it }

        Spacer(modifier = Modifier.height(20.dp))
        
        UserNameField(
            modifier = Modifier.width(300.dp),
            username = username
        ) { username = it }
    }

    // other fields...
}
```



![A screenshot showing two text fields with differents measurments](https://ucarecdn.com/810a23a8-3867-4da1-a6b1-b1c078198a88/)

And there you have it:

- Now, the caller decides how to measure the `UserNameField` .

- The `UserNameField` becomes versatile and can be used in different contexts with distinct behaviors.

- It simplifies the integration of layout-specific modifiers, making it more accessible.

## State hoisting for composable reusability

In our previous example of the `LoginScreen`, you'll notice the use of the state hoisting pattern:



```kotlin
@Composable
fun LoginScreen() {
    var username by remember {
        mutableStateOf("")
    }
    UserNameField(
        username = username
    ) {
        username = it
    }

    // other fields...
}
```



Here, `username` and `onValueChange` are extracted from `UserNameField` and moved up the hierarchy to the `LoginScreen` composable, showcasing the state hoisting pattern. With the state hoisted to the parent function, `UserNameField` becomes a stateless, reusable composable that can be called upon and passed any state and event handler.

To illustrate how this pattern enhances the reusability of `UserNameField`, let's add a login button to our screen. On narrow screens, this button will appear under the username field.

![vertical textfield and button](https://ucarecdn.com/298e306d-b260-4e00-ac0e-ff96d74e0074/)

Meanwhile, on wider screens, the button will be displayed beside the username field.

![hotizontal textfield and button](https://ucarecdn.com/a08026eb-bc99-480e-976b-bf39d8bf00c7/)

Here is how we can implement this on the `LoginScreen`:



```kotlin
@Composable
fun LoginScreen() {
    val orientation = LocalConfiguration.current.orientation // ORIENTATION_PORTRAIT, ORIENTATION_LANDSCAP

    var username by rememberSaveable { // this is used to survive configuration changes
        mutableStateOf("")
    }

    if (orientation == ORIENTATION_PORTRAIT) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            UserNameField(
                username = username
            ) { username = it }

            Spacer(modifier = Modifier.height(10.dp))

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Login")
            }

        }
    } else {
        Row(verticalAlignment = Alignment.CenterVertically) {
            UserNameField(
                username = username
            ) { username = it }

            Spacer(modifier = Modifier.width(10.dp)) // notice the usage of 'width'

            Button(onClick = { /*TODO*/ }) {
                Text(text = "Login")
            }

        }
    }
    /* ... */
}
```



In this example, we dynamically adapt the layout based on the screen orientation. The `UserNameField` is used in both portrait and landscape modes, with changes made by both text fields affecting the same state. If the `username` state is not hoisted to the `LoginScreen`, the `UserNameField` displayed in landscape mode won't have access to the text already typed, and vice versa.

> **Note:**

The `rememberSaveable` API behaves similarly to `remember` because it retains state across recompositions, as well as across activity or process recreation using the saved instance state mechanism. For example, this happens when the screen is rotated.

![orientation change screen recording](https://ucarecdn.com/dcaa893b-31ee-456d-893d-ea695f99d3df/)

The animation above demonstrates how the hoisted state allows users to seamlessly rotate the phone while entering their username, thereby preserving the entered text for a consistent login process.

## Achieving flexible layouts: using slot-based layout

Consider a scenario where you wish to generalize the `UserNameField` to an `IdentifierField` for use in screens where users need to enter their email instead of a username. To achieve this, the function can be modified to accept parameters specifying the text for the `label` and `placeholder` fields:



```kotlin
@Composable
fun IdentifierField(
    modifier: Modifier = Modifier,
    identifier: String,
    label: String,
    placeholder: String,
    onIdentifierChange: (String) -> Unit
) {
    TextField(
        modifier = modifier.padding(10.dp),
        value = identifier,
        onValueChange = { onIdentifierChange(it) },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "person icon"
            )
        }
    )
}
```



Despite these modifications, the function still displays a `Person` icon in the `leadingIcon`. However, for an email field, we may want to display an email icon retrieved from drawable resources, passing it as a painter, not an image vector:



```kotlin
Icon(
    painter = painterResource(id = R.drawable.baseline_email_24),
    contentDescription = "person icon"
)
```



Currently, the function cannot display anything other than the predetermined icon. To address this limitation, we open up the `leadingIcon` composable as a slot, allowing any other composable to be placed within it when the function is called. This approach provides a slot API (Application Programming Interface) for the composable. In this context, it means adding a programming interface to our composable that allows the caller to specify which composable will appear within a slot.



```kotlin
@Composable
fun IdentifierField(
    modifier: Modifier = Modifier,
    identifier: String,
    label: String,
    placeholder: String,
    onIdentifierChange: (String) -> Unit,
    leadingIcon: @Composable () -> Unit
) {
    TextField(
        modifier = modifier.padding(10.dp),
        value = identifier,
        onValueChange = { onIdentifierChange(it) },
        label = { Text(text = label) },
        placeholder = { Text(text = placeholder) },
        leadingIcon = {
            leadingIcon()
        }
    )
}
```



Now, the `IdentifierField` can be called as follows, allowing for a custom icon:



```kotlin
@Composable
fun LoginScreen() {
    var identifier by remember {
        mutableStateOf("")
    }
    IdentifierField(
        identifier = identifier,
        label = "email",
        placeholder = "enter your email",
        onIdentifierChange = {
            identifier = it
        },
        leadingIcon = {
            Icon(
                painter = painterResource(id = R.drawable.baseline_email_24),
                contentDescription = ""
            )
        }
    )

    // other fields..
}
```



![A text fields with an 'email' label and a leading icon](https://ucarecdn.com/986e759f-8f6c-4d71-bd11-6c1d8a94441a/)

By employing a slot API, we have significantly enhanced the customizability and reusability of our `IdentifierField` composable.

## Making reusable composables using generics

In Jetpack Compose, you can enhance the reusability of your code by using generics when creating composable functions. This is particularly useful when designing user interface (UI) forms that handle different data types.

Autofill serves as a handy mechanism that enables users to quickly select and auto-populate saved information, such as usernames or accounts, eliminating the need for manual input. In this context, we aim to present the user with a spinner, allowing them to choose from a selection of saved accounts for login. These accounts come in different types: some accounts include email information, while others provide phone numbers.

For instance, an email account is represented by the following data class:



```kotlin
data class EmailAccount(
    val email: String,
    val name: String,
    val profileImageUrl: String? = null,
    val lastLoginDate: String? = null
) {
    override fun toString(): String {
        return "$name \n $email"
    }
}
```



On the other hand, a phone account is represented by:



```kotlin
data class PhoneAccount(
    val phoneNumber: String,
    val name: String,
    val countryCode: String? = null,
    val isTwoFactorEnabled: Boolean = false
) {
    override fun toString(): String {
        return "$name \n $phoneNumber"
    }
}
```



The spinner must be capable of managing both types of accounts. For email accounts in the spinner items, we should display the email and the name. Conversely, for phone accounts, the display should include the name and the phone number.

If we develop a spinner composable that accepts a list of `EmailAcount` this way:



```kotlin
@Composable
fun AccountSpinner(
    items: List<EmailAccount>
    /* ... */
) {
```



Then, the spinner would only be able to handle `EmailAccount`. That's why it's essential to create a generic composable that can accept any type:



```kotlin
@Composable
fun <T> AccountSpinner(
    items: List<T>,
    selectedItem: T?,
    onItemSelected: (T) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Row {
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach {  item ->
                DropdownMenuItem(
                    onClick = {
                        onItemSelected(item)
                        expanded = false
                    },
                    modifier = Modifier.padding(4.dp),
                    text = {
                        Text(item.toString()) // Display appropriate information about the account
                    }
                )
            }
        }
        // Text field with dropdown arrow
        OutlinedTextField(
            value = selectedItem?.toString() ?: "",
            onValueChange = {},
            readOnly = true,
            label = { Text("Select Account") },
            trailingIcon = {
                IconButton(
                    onClick = {
                        expanded = !expanded
                    }
                ) {
                    Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                }
            }
        )
    }
}
```



The generic composable `AccountSpinner` accepts a list of accounts, `items`, of any type `T`, the currently selected item `selectedItem`, and a lambda function `onItemSelected` to handle the selection. This allows us to pass a list containing both types of accounts to the spinner.

Here is an example implementation of this spinner:



```kotlin
@Composable
fun AccountSpinnerExample() {
    var selectedAccount by remember { mutableStateOf<Any?>(null) }

    val accounts = remember {
        listOf(
            EmailAccount("john.doe@email.com", "John Doe"),
            EmailAccount("jane.smith@email.com", "Jane Smith"),

            PhoneAccount("123-456-7890", "John Doe"),
            PhoneAccount("987-654-3210", "Jane Smith")
        )

    }


    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        AccountSpinner(
            items = accounts,
            selectedItem = selectedAccount,
            onItemSelected = { selectedAccount = it }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Display selected account information
        selectedAccount?.let {
            Text("Selected Account: $it")
        }
    }
}
```



![spinner showing a list of accounts](https://ucarecdn.com/3e5e9a07-671a-4d6b-b8c2-2efca447daba/)

![Spinner with selected item](https://ucarecdn.com/24daac11-a2d8-4f60-bdf8-2579d5d6a9de/)

The images above demonstrate that the spinner works well with a list of diverse accounts, handling them effectively despite their different types. This implementation illustrates how we can create a single generic composable to display various types of data, thereby enhancing the reusability and flexibility of composables, and showcasing the power of generics in Jetpack Compose.

## Conclusion

In conclusion, exploring Jetpack Compose's reusability concepts offers practical techniques for building dynamic UIs. From addressing code boilerplate to understanding state hoisting, slot-based layouts, and generics, we've learned how to create adaptable and maintainable user interfaces. As you tackle the practical tasks, keep these principles in mind:

- State hoisting for clarity and reusability.

- Slot-based layouts for dynamic customization.

- Generics for seamlessly handling diverse data types.

These skills will simplify your code and contribute to an efficient and adaptable codebase, enhancing your proficiency in Jetpack Compose.

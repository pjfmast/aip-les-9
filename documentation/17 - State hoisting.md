# State hoisting

State is what makes a Jetpack Compose UI respond to user interactions or data changes rather than staying static. Managing it gets harder as UI hierarchies grow, though — and that's what state hoisting is for: it centralizes state management, keeping your Compose code organized and easier to maintain.

This topic covers the basics of state hoisting, its effect on screen-level UI state, and how it applies to the state of individual UI elements.

## Basics

State, here, means the data that changes over time and affects what a program does or shows — the text in a text field, whether a checkbox is checked. Hoisting the state means moving it up the hierarchy, so it's managed by a parent composable or the screen, rather than by individual UI elements.

Understanding state hoisting starts with the difference between **stateful** and **stateless** functions. A stateful function includes some state storage mechanism — a `remember` or `rememberSaveable` call, for instance — and can manage, store, and share that state with child or grandchild composables. A stateless function does neither: it just displays whatever state a parent composable gives it, or sends events back up to that parent. The basic state flow can be visualised as follows:

![](https://ucarecdn.com/3b4124dd-c9c2-4fa3-9842-8cba884c575a/)

It's also worth being clear on what an event is: a user action, like typing into a text field or clicking a button. Managing events well gets harder in large projects with complex UIs made of many interacting components — but unrelated elements shouldn't share state or events with each other. Mixing states and events indiscriminately tends to degrade performance and complicate the code. State hoisting solves this by letting you manage user events at the right level in the composable hierarchy — though picking the correct level matters. Take a card on screen that expands when a button is clicked: the button sends the event to the card, and the card adjusts its own height. The state belongs on the card, the parent, not the button, the child — and hoisting it all the way to the screen level isn't necessary either, since the logic is specific to the card, not the whole screen.

This sets out clear responsibilities: the parent composable is stateful, since it owns and controls the state, while the child composable is stateless, focused only on presenting UI and dispatching user interactions, with no state management of its own.

## Screen UI state

State hoisting's effect on screen-level state: in a Compose app, a screen is made up of multiple UI elements, each with potentially its own state. State hoisting lets you delegate managing all of that to the screen, or higher.

Take a simple form with several text fields. Each field can hold its own state — its current text — but the form (or the screen containing it) usually needs the combined picture: the data entered across every field. State hoisting lets you manage all of that at the form level instead, which simplifies validation and makes it easier to act on the complete set of data at once.



```kotlin
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun FormScreen() {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    Column {
        AppTextField(
            label = "Email",
            value = email,
            onValueChange = { email = it }
        )
        AppTextField(
            label = "Password",
            value = password,
            onValueChange = { password = it }
        )
        Button(onClick = { /* Handle business logic */ }) {
            Text("Submit")
        }
    }
}

@Composable
fun AppTextField(label: String, value: String, onValueChange: (String) -> Unit) {
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) }
    )
}
```



`FormScreen` holds two pieces of state here, `email` and `password`, both managed at the form level with `remember` and `mutableStateOf`. Each is passed down to an `AppTextField` composable, along with an `onValueChange` callback that updates the state as the text changes.

`AppTextField` is a thin wrapper around `TextField`: it takes a label, the current value, and an `onValueChange` callback as parameters. That makes each text field a pure, stateless composable, with no internal state management of its own. Here's what that setup looks like as a diagram:

![](https://ucarecdn.com/1591076a-b610-4add-abd5-01a84699e522/)

Everything starts with `FormScreen`, which uses `mutableStateOf` and `remember` to create state for `email` and `password` that survives recompositions, then passes that state down to the `TextField` composables. `FormScreen` is responsible for managing the state; the `TextField` composables handle displaying the input fields and updating the state as the user types, via the `onValueChange` callback.

The orange arrows in the diagram represent events like `onValueChange` and `onClick` — these are what actually trigger updates to `email` and `password`. Without them, the state would never change, and the `TextField` composables would never see the new values.

Data flows one way here — from `FormScreen` down to `TextField` — while events that update state, like user input, flow the other way. This is **unidirectional data flow**, and it's what makes it straightforward to track how data and events move through the app, which in turn makes debugging easier. It also heads off bugs that come from data changing in unexpected ways or without your knowledge, a common problem with **bidirectional data flow**, where data can move in both directions at once.

For more complex UI logic, it often helps to encapsulate the state and its related logic inside a state-holder class, which manages the state and exposes functions for modifying it — a cleaner, more manageable way to organize UI code.

## UI element state

Individual UI elements have their own state worth looking at too — the properties of a component that determine its current behavior and appearance. Take a `Checkbox`: it has a `checked` state that's either true or false. Without hoisting, the `Checkbox` would manage that state internally. With hoisting, you pass the checked state down from the parent instead, and changes come back up through a callback.

That extra step matters for two reasons. First, the `Checkbox` becomes a pure function of its state — no side effects, no hidden behavior — which makes it reusable anywhere in the app without worrying about it misbehaving. Second, it separates UI logic from UI presentation, which makes both easier to test and maintain.

In code, that looks like this:



```kotlin
@Composable
fun MyCheckbox(isChecked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    Checkbox(
        checked = isChecked,
        onCheckedChange = onCheckedChange
    )
}
```



`MyCheckbox` takes both the current state and a way to update it as parameters — a small change from the default `Checkbox`, but one that changes how you can manage state across the rest of the app.

## State holder class example

Here's a state holder class for a hypothetical user profile form — one that holds the profile field values, but also includes validation logic and a method to update the profile, going beyond just holding values to managing the logic tied to them:



```kotlin
enum class ValidationState { Valid, Invalid, Unvalidated }

data class UserProfileUiState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: ValidationState = ValidationState.Unvalidated,
    val isPasswordValid: ValidationState = ValidationState.Unvalidated
)
```



`ValidationState` represents the validation state of an email or password field, with three possible values:

- `Unvalidated`: the field hasn't been validated yet.

- `Valid`: the field meets the predefined rules.

- `Invalid`: the field doesn't meet them.

`UserProfileUiState` holds the state for the whole form — email and password, plus their validation states. By default, both fields start as empty strings, and both validation states start as `Unvalidated`.



```kotlin
class UserProfileStateHolder() {
    var userProfile by mutableStateOf(UserProfileUiState())
        private set

    fun updateEmail(email: String) {
        userProfile = userProfile.copy(email = email)
    }

    fun updatePassword(password: String) {
        userProfile = userProfile.copy(password = password)
    }

    // Validation logic
    private fun checkEmailValidity(): Boolean { /*...*/ }
    private fun checkPasswordValidity(): Boolean { /*...*/ }

    fun submitProfileUpdates() {
        val isEmailValid = checkEmailValidity()
        val isPasswordValid = checkPasswordValidity()
        if (isEmailValid && isPasswordValid) {
            // handle valid state 
        } else {
            // handle invalid state
        }
    }
}
```



`UserProfileStateHolder` bundles both the state and the behavior for the user profile UI. It uses `mutableStateOf` to create a `MutableState` object, so the UI reactively updates whenever that state changes. The `userProfile` property stays mutable inside the class but can't be modified from outside it, because of the `private set` modifier — so the state can only ever be updated from within the state holder.

`updateEmail` and `updatePassword` update their respective fields by creating a new, immutable copy of `UserProfileUiState` with the changed value — a pattern that helps prevent unintended side effects and keeps state management simpler.

`checkEmailValidity` and `checkPasswordValidity` hold the logic for validating each field, typically checking formatting and any security requirements. They're private, since they're internal implementation details of the state holder.

When `submitProfileUpdates` runs, it calls those validation methods to check both fields. If both pass, it proceeds to handle the valid state — potentially calling out to a repository or service to update the profile in a database or backend. If validation fails, it handles the invalid state instead, possibly by updating `UserProfileUiState` with error messages to show the user.

Keeping the UI state and validation logic separate from the UI components themselves is what makes this a clean separation of concerns — and what makes the codebase easier to maintain and test.



```kotlin
@Composable
fun UserProfileScreen() {
    val userProfileStateHolder = remember { UserProfileStateHolder() }
    UserProfileForm(userProfileStateHolder)
}

@Composable
fun UserProfileForm(userProfileStateHolder: UserProfileStateHolder) {
    val userProfile = userProfileStateHolder.userProfile
    Column {
        
        OutlinedTextField(
            value = userProfile.email,
            onValueChange = userProfileStateHolder::updateEmail,
            label = { Text("Email") },
            isError = userProfile.isEmailValid == ValidationState.Invalid
        )
        
        OutlinedTextField(
            value = userProfile.password,
            onValueChange = userProfileStateHolder::updatePassword,
            label = { Text("Password") },
            isError = userProfile.isPasswordValid == ValidationState.Invalid
        )
        
        Button(onClick = userProfileStateHolder::submitProfileUpdates) {
            Text("Update Profile")
        }
    }
}
```



Two composables here: `UserProfileScreen` manages the user profile's state, and `UserProfileForm` renders the UI elements that let users interact with that data.

`UserProfileScreen` creates a `UserProfileStateHolder` instance using `remember`, so it survives recompositions instead of getting lost on every UI update. `UserProfileStateHolder` holds the profile state and exposes the methods for updating it — `updateEmail`, `updatePassword`, `submitProfileUpdates`.

`UserProfileForm` is stateless: it takes a `UserProfileStateHolder` instance as a parameter, and uses the state and event handlers it provides to render the UI —

`OutlinedTextField` handles each input field. Its `value` is bound to the current email or password from `userProfile`; `onValueChange` calls the matching update method on `UserProfileStateHolder` whenever the user edits the text; `label` gives each field its text label; and `isError` reflects whether that field's current validation state is `Invalid`.

The `Button` submits the profile update, calling `submitProfileUpdates` on click.

`UserProfileScreen` is stateful here — it owns and manages the profile state through `UserProfileStateHolder`. `UserProfileForm` is stateless: it holds no state of its own, and just renders UI based on the state and event handlers passed down from its parent.

Structured this way, the separation of concerns is clear: `UserProfileScreen` handles business logic and state management, while `UserProfileForm` focuses only on presenting the UI.

## Conclusion

State hoisting centralizes state management instead of scattering it across individual composables. Applying it consistently keeps composables pure and declarative, which is what makes the resulting code easier to test, maintain, and scale.

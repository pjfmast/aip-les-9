# Tool calling in Spring AI

In the agentic loop, the model never runs anything itself. It asks for a **tool** by name with arguments, and your application executes it and returns the result. Spring AI takes care of the plumbing: you write an ordinary Kotlin function and describe it, and the framework sends the description to the model, executes the calls the model requests, and feeds the results back.

In this topic, you'll learn how to define a tool, how to write descriptions the model can use, how to return results and errors, and how to register tools with a `ChatClient`.

## From a function to a tool

The model can't see your code. What it sees is a **tool definition**: a name, a description, and a schema of the arguments (names, types, descriptions, and which are required). Spring AI builds this from your function's signature and annotations. Roughly, the model receives something like:



```json
{
  "name": "get_opening_hours",
  "description": "Returns the opening hours of a store. ...",
  "parameters": {
    "type": "object",
    "properties": {
      "city": {"type": "string", "description": "City of the store, for example 'Berlin'"},
      "day": {"type": "string", "description": "Day of the week ..."}
    },
    "required": ["city"]
  }
}
```



The exact format depends on the provider, but the idea is the same. From these few lines, the model has to decide whether the tool is relevant, when to call it, and which arguments to pass. The quality of the definition largely determines whether it does this well.

## Defining a tool

Annotate a function with `@Tool` and its parameters with `@ToolParam`:



```kotlin
@Component
class StoreTools(private val stores: StoreRepository) {

    @Tool(
        name = "get_opening_hours",
        description = "Returns the opening hours of a store in a given city. " +
            "Use it when the user asks when a store is open. " +
            "Without a day, the hours for the whole week are returned.",
    )
    fun getOpeningHours(
        @ToolParam(description = "City of the store, for example 'Berlin'")
        city: String,
        @ToolParam(
            description = "Day of the week: monday, tuesday, wednesday, thursday, friday, saturday or sunday",
            required = false,
        )
        day: String?,
    ): Any {
        val parsedDay = day?.let { d -> DayOfWeek.entries.find { it.name.equals(d.trim(), ignoreCase = true) } }
        if (day != null && parsedDay == null) {
            return mapOf("error" to "Unknown day '$day'")
        }

        val hours = stores.openingHours(city) // Map<DayOfWeek, String>?
            ?: return mapOf("notFound" to "No store in '$city'")

        val selected = if (parsedDay == null) hours else hours.filterKeys { it == parsedDay }
        return OpeningHours(city, selected.mapKeys { it.key.name.lowercase() })
    }
}

data class OpeningHours(val city: String, val hours: Map<String, String>)
```



Here is what the annotations control:

- `name` defaults to the method name. It must be unique among the tools you give to a model. Set it explicitly when you want a stable name in snake_case that doesn't change if you rename the function.

- `description` tells the model what the tool does and when to use it. Always provide one: without it, the model has no guidance.

- `@ToolParam(description = ...)` describes one argument.

- `@ToolParam(required = ...)` marks the argument as **required** (the default) or optional.

Be deliberate about `required`. If you mark a parameter as required and the model has no way to know its value, the model will often make one up. Mark an argument as optional when the tool works without it.

For an optional argument, set `required = false` explicitly, even when the Kotlin type is nullable, like `String?`. Spring AI does take Kotlin nullability into account, but it looks at `@ToolParam` first, and the `required` attribute of that annotation defaults to `true`. So a `String?` parameter that has `@ToolParam` without `required = false` is still sent to the model as required.

## Writing descriptions the model can use

The model reads descriptions the way a new colleague reads documentation. Compare:



```kotlin
@Tool(description = "Gets hours")
fun hours(city: String, day: String?): Any
```





```kotlin
@Tool(
    name = "get_opening_hours",
    description = "Returns the opening hours of a store in a given city. Use it when the user asks when a store is open.",
)
fun getOpeningHours(
    @ToolParam(description = "City of the store, for example 'Berlin'") city: String,
    @ToolParam(description = "Day of the week, in English, for example 'monday'", required = false) day: String?,
): Any
```



The first leaves the model guessing: whose hours, which format, and is `day` a name or a date? The second says what the tool does and when to use it, and it gives an example for each argument. When an argument accepts only certain values, list them in the description, as the `day` description does.

Also keep the number of tools and the length of descriptions in check. Tool definitions are sent to the model with every request, so they take up context just like the conversation does.

## Results

Whatever the function returns is converted to text before it goes back to the model. By default, Spring AI serializes the result to JSON using Jackson. So return a data class or a map, not a hand-built string, and include only what the model needs to answer. A smaller result is cheaper, faster, and gives the model less to get confused about. Return fresh data, too: a tool is the right place to read the current state of the world.

## Errors and bad arguments

The model fills in arguments, and it can get them wrong. Treat them as untrusted input: validate them, as `getOpeningHours` does with `day`. When something is wrong, return a clear message that names the problem, for example `{"error": "Unknown day 'funday'"}`. The model reads it as the result of the call and can try again with a valid value.

You can also throw an exception. By default, Spring AI sends the message of any `RuntimeException` back to the model, so it can react to it. It re-throws checked exceptions and errors instead, which fails the whole request, so make sure your tool handles exceptions from libraries that can throw them. Returning an error object gives you control over exactly what the model sees, so it is usually the better choice for problems you expect.

A tool that doesn't find anything should say so explicitly (`"notFound"` in the example) instead of returning an empty result. That way, the model can tell the user plainly that the thing doesn't exist.

## Registering tools

Give the tools to a `ChatClient`, either as defaults for every request or for a single call. To register tools as **defaults**, pass them to the builder with `defaultTools`:



```kotlin
@Service
class AssistantService(chatClientBuilder: ChatClient.Builder, storeTools: StoreTools) {

    private val chatClient = chatClientBuilder
        .defaultSystem("You help users with questions about our stores. Use the tools to look up facts.")
        .defaultTools(storeTools)
        .build()

    fun ask(question: String): String? =
        chatClient.prompt()
            .user(question)
            .call()
            .content()
}
```



To register tools for a **single call**, pass them to `.tools(...)` on the request. They are added to the defaults for that call only. Suppose that `OrderTools` is another class, like `StoreTools`, with a `@Tool` function that cancels an order:



```kotlin
@Service
class SupportService(
    chatClientBuilder: ChatClient.Builder,
    storeTools: StoreTools,
    private val orderTools: OrderTools,
) {
    private val chatClient = chatClientBuilder
        .defaultTools(storeTools)
        .build()

    fun ask(question: String): String? =
        chatClient.prompt()
            .user(question)
            .call()
            .content()

    fun askWithOrderChanges(question: String): String? =
        chatClient.prompt()
            .user(question)
            .tools(orderTools) // available for this call only
            .call()
            .content()
}
```



Prefer registering risky tools, such as those that modify data, per call and not as defaults. A default tool is offered to the model in every request the client makes, so the model can call it in any conversation, even where the user never asked for a change. The model can misread a question, or be steered by instructions hidden in text it reads, such as a product review or a document. A read-only tool can at worst return wrong information, but a tool that cancels an order can do real damage. With per-call registration, your code decides for each request whether the tool is available at all, for example only after the user is authenticated and has confirmed the action.

You don't write the loop. A `ChatClient` that has tools sends their definitions to the model, executes the calls it requests, appends the results, and repeats until the model produces an answer.

## Practical notes

- **Test tools without the model.** A tool is a plain function, so you can call it in an ordinary unit test with valid and invalid arguments.

- **Stick to simple types.** `Optional`, `Future`, reactive types, `Flow`, and function types are not supported as tool parameters or results. Use plain functions and data classes.

- **Guide, but verify.** A system prompt can tell the model to use tools before answering, but it can still skip them or pass odd arguments, so anything that must hold belongs in the tool code.

## Conclusion

In Spring AI, a tool is an annotated Kotlin function. `@Tool` gives it a name and a description, and `@ToolParam` describes and marks the arguments. Set `required = false` explicitly for optional arguments, even when the Kotlin type is nullable. The model sees only those descriptions, so clear ones, with examples and valid values, make the difference between a tool that is used correctly and one that isn't.

Results are serialized to JSON and should be small and current. Validate every argument, and return an error that names the problem, so the model can correct itself. Register tools on a `ChatClient`, which runs the whole loop for you: as defaults for every request, or per call for tools that modify data.

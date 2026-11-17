# The agentic loop

A chatbot answers in one step: a question goes in, and a reply comes out. An agent works differently. To answer "Is there a dairy-free cream for pasta?", it may need to search a catalog, look at the results, and perhaps look up one product in more detail before it replies. These steps happen in a cycle called the **agentic loop**.

In this topic, you'll learn the three steps of the loop, who performs each of them, how the loop ends, and what the code behind it looks like.

## Think, act, observe

The loop has three steps that repeat:

- **Think.** The model looks at everything in its context (the instructions, the conversation, and any results so far) and decides what to do next: answer, or use a tool.

- **Act.** If it needs a tool, it asks for one by returning the tool's name and arguments. The tool is then executed.

- **Observe.** The result of the tool is added to the context, and the loop starts again from the first step.

The loop ends when the model decides that it has enough information and produces a final answer instead of a tool request. This pattern is widely known as **ReAct** (reasoning and acting), after a 2022 research paper, and it is the basis of most LLM agents.

Here is what it looks like for the question above (the products and tool names are illustrative):



```text
User:    Is there a dairy-free cream for pasta, and what is in it?

Think:   I should search the catalog before answering.
Act:     search_products(query = "dairy free cream for pasta")
Observe: Oat cooking cream (HM-OAT-250), Coconut cream (HM-COCO-400)

Think:   The user asked what is in it. I need the details of the first product.
Act:     get_product(sku = "HM-OAT-250")
Observe: Oat cooking cream. Ingredients: oats, water, sunflower oil, salt.

Think:   I have what I need.
Answer:  Try the oat cooking cream. It contains oats, water, sunflower oil, and salt.
```



Notice that nobody wrote down this sequence in advance. The model chose to search first and to look up a product afterwards. With a different question, or even with the same one on another day, the sequence might differ. This is the non-determinism of agents you've already met.

The "think" step is not always visible. With tool-calling APIs, the model's reasoning is often implicit: all you see is which tool it chose. Some models also produce reasoning text, but you shouldn't count on it.

## Who does what

The most important point about the loop is that **the model never runs anything**. It only produces text: either a final answer or a request that says "call this tool with these arguments". Your application, or the framework you use, executes the tool, appends the result, and calls the model again. The model plans, and your code acts.

In terms of the message roles you've seen before, the context grows like this:



```text
system:    You help users find products in the catalog. ...
user:      Is there a dairy-free cream for pasta?
assistant: (tool request) search_products(query = "dairy free cream for pasta")
tool:      {"products": [ ... ]}
assistant: Try the oat cooking cream. ...
```



Remember that models are stateless. Every pass through the loop is a separate model call, and each call sends the whole list again, including the results of earlier tools. A question that needs two tool calls costs three model calls, and the context gets longer each time. This is why the size of tool results matters, which is a topic you'll return to when designing tools.

## The loop in code

The core of the loop is surprisingly short. This is simplified pseudocode with placeholder types, not the API of a particular library:



```kotlin
fun runAgent(question: String): String {
    val messages = mutableListOf<Message>(
        SystemMessage(SYSTEM_PROMPT),
        UserMessage(question),
    )

    repeat(MAX_STEPS) {
        val reply = model.call(messages, tools)         // Think: the model decides
        messages += reply

        if (reply.toolRequests.isEmpty()) return reply.text // no tool needed: final answer

        for (request in reply.toolRequests) {
            val result = execute(request)                // Act: your code runs the tool
            messages += ToolMessage(request.id, result)  // Observe: the result joins the context
        }
    }
    return "I couldn't finish this request."
}
```



Each line maps to a step. `model.call` is the thinking, `execute` is the acting, and appending the `ToolMessage` is the observing. The loop's memory is nothing more than the `messages` list.

## Observing results, including errors

The model sees a tool's result as text in the context, and it reads that text during the next pass. This gives you a useful property: **an error is also a result**. Suppose the model calls a tool with an invalid argument, such as a category that doesn't exist. If your tool returns a clear message like `{"error": "Unknown category FRUITZ"}`, the model can read it and try again with a correct value. If the tool throws an exception that crashes the whole request, the model never gets the chance.

So the observe step is where you decide what the model gets to know. A result that is too long wastes tokens and can distract the model, a result that is too vague leaves it guessing, and a clear error message lets it recover.

## Guiding the loop

You can't script the sequence of steps, but you can influence it in two places:

- **The system prompt** tells the model when to use tools and what rules to follow, for example: "Always search the catalog before saying whether something is sold. Only mention products returned by the tools."

- **The tool descriptions**, which the model reads when deciding whether and how to call a tool.

Both guide the model, but neither guarantees its behavior. A model can skip a rule or pass a wrong argument. Anything that must hold, such as valid argument values, should be checked in code.

## When does the loop end?

The normal ending is a reply without a tool request. But a loop that only ends when the model decides to stop can run for a long time, for example if the model keeps repeating a failing call. So real agents add limits:

- a maximum number of steps or tool calls;

- a time limit;

- a token budget.

Decide in advance what happens when a limit is reached. Usually it's better to return a clear message than to leave the user waiting.

## Frameworks run the loop for you

You rarely write the loop yourself. Frameworks implement it: you register tools, and the framework sends them to the model, executes the requested calls, appends the results, and repeats until a final answer arrives. In Spring AI, for example, a `ChatClient` with tools registered does this automatically. Many frameworks also apply default limits on the number of tool calls.

Understanding the loop still pays off. It explains why a single question can be slow and expensive, why the context keeps growing, and where to look when an agent behaves strangely: the system prompt, the tool descriptions, or the tool results.

## Conclusion

An agent works in a loop of three steps: think, act, and observe. The model decides what to do and asks for tools, your code executes them, and the results are added to the context for the next pass. The loop ends when the model answers without requesting a tool, or when a limit is reached.

The model never executes anything itself. Every pass is a separate model call that resends the whole context, so long loops and large tool results cost time and money. The system prompt and tool descriptions guide the model, errors can be returned as results so that the model can correct itself, and anything that must be guaranteed should be checked in your own code.

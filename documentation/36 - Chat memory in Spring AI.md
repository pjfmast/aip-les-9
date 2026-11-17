# Chat memory in Spring AI

In the previous topic, you used `ChatClient` to send prompts to a model. There is one catch: every `.call()` is independent. Ask "My name is Alice" and then "What's my name?", and the model has no idea, because LLMs are **stateless**. As you've seen, the application has to resend the earlier conversation with every request.

Spring AI does this for you with **chat memory**. In this topic, you'll learn how it is organized, how to attach it to a `ChatClient`, and what to watch out for in a real application.

## Messages, ChatMemory, and ChatMemoryRepository

A conversation with a model is a list of **messages**. Spring AI represents each message as an object that implements the `Message` interface. Three types matter here:

- `UserMessage` holds what the user said.

- `AssistantMessage` holds what the model answered. Its text is available through the `text` property.

- `SystemMessage` holds instructions for the model, such as "answer briefly".

Spring AI splits the handling of these messages into two parts:

- `ChatMemory` decides which messages to keep and when to drop old ones. For example, it might keep the last N messages. It has three methods, and each one takes a conversation ID:

`add(conversationId, messages)` appends a list of messages to the conversation.

- `get(conversationId)` returns the messages that the model should see, as a `List`.

- `clear(conversationId)` deletes the whole conversation.

- `ChatMemoryRepository` only stores and retrieves messages. The default one, `InMemoryChatMemoryRepository`, keeps them in a map in your application's memory.

Everything is keyed by the **conversation ID**, so one application can hold many separate conversations.

Note that chat memory is not the same as **chat history**. Memory holds what the model should see, and it gets trimmed. If you need a complete record of everything that was said, store it separately, for example with Spring Data.

## The message window

The standard `ChatMemory` implementation is `MessageWindowChatMemory`. It keeps a **sliding window** of recent messages, which is the strategy from the previous memory topic. The size of the window is set with `maxMessages`: the maximum number of messages kept for one conversation.

- The default window size is 20 messages, which is 10 question and answer pairs.

- Old messages are evicted in **whole turns**, starting from a user message, so the window never begins with an answer whose question was cut off. This means `maxMessages` is an upper bound, and the actual number can be lower.

- Trimming happens when messages are added, not when they are read.

If a `SystemMessage` is stored in the memory, it is never evicted, and a new system message replaces the old one. With `ChatClient`, however, you normally don't store the system prompt in memory at all, as you'll see below. In this topic, the memory holds only `UserMessage` and `AssistantMessage` objects.

Spring Boot auto-configures both a `ChatMemoryRepository` bean and a `ChatMemory` bean, so you can inject them right away. To change the window size, define your own `ChatMemory` and give it the repository:



```kotlin
@Configuration
class ChatMemoryConfig {

    @Bean
    fun chatMemory(chatMemoryRepository: ChatMemoryRepository): ChatMemory =
        MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(20)
            .build()
}
```



Passing the auto-configured `ChatMemoryRepository` is important. If you call `.build()` without it, the window falls back to `InMemoryChatMemoryRepository`, even when you have configured a different repository elsewhere. Defining your own `ChatMemory` bean normally replaces the default one.

## Adding memory to ChatClient

You connect memory to a `ChatClient` with an **advisor**, the middleware mechanism from the previous topic. `MessageChatMemoryAdvisor` loads the conversation's history into each prompt and saves the new messages afterwards:



```kotlin
@Service
class AssistantService(
    chatClientBuilder: ChatClient.Builder,
    private val chatMemory: ChatMemory,
) {
    private val chatClient = chatClientBuilder
        .defaultSystem("You are a friendly Kotlin tutor. Answer briefly and use simple words.")
        .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
        .build()

    fun ask(conversationId: String, question: String): String? =
        chatClient.prompt()
            .user(question)
            .advisors { it.param(ChatMemory.CONVERSATION_ID, conversationId) }
            .call()
            .content()

    fun forget(conversationId: String) = chatMemory.clear(conversationId)
}
```



A few points about this code:

- The conversation ID is passed with every call as an advisor parameter. There is no default conversation ID: a call without one throws an `IllegalArgumentException`.

- The system prompt set with `defaultSystem` is attached to each request, but it is never saved in `ChatMemory`. The memory stores only the `UserMessage` and `AssistantMessage` turns, so the system prompt doesn't count against the window.

- `chatMemory.clear(conversationId)` deletes one conversation.

## Choosing the conversation ID

Every conversation ID must be unique to one user or session. If two users share an ID, they read each other's messages. In a multi-user application, derive the ID on the server, for example from the authenticated user or the session. If the ID comes from a request header instead, treat it like a password: anyone who knows it can continue that conversation.

## Where messages are stored

By default, messages live in the memory of your application. This is fast and needs no setup, but they disappear when the server restarts, and several instances of the server would each have their own copy.

For **persistence**, Spring AI provides other `ChatMemoryRepository` implementations, including `JdbcChatMemoryRepository` for relational databases, and others for Cassandra, MongoDB, and Neo4j. Each of them comes from a separate Spring AI **starter dependency**. For example, JDBC support is added with `spring-ai-starter-model-chat-memory-repository-jdbc`. Once the starter is on the classpath, its repository is auto-configured, and the `ChatMemory` bean from the previous section picks it up through the injected `ChatMemoryRepository`. Your service code stays the same.

## When a model call fails

Look carefully at when `MessageChatMemoryAdvisor` saves things. In the current implementation, it stores the user's message **before** the model is called and the model's reply **after** the call returns. If the call throws, for example because the provider is down, the reply is never saved, but the user's message already is.

The history now contains a question that was never answered. The model will see it on every later request, and the user may ask the same thing again. Whether this matters depends on your application. If it does, take control of the order yourself. You can stay on `ChatClient`, so that its defaults such as the system prompt still apply, drop the advisor, and work with `ChatMemory` directly, so that nothing is saved until the call succeeds:



```kotlin
@Service
class ManualAssistantService(
    chatClientBuilder: ChatClient.Builder,
    private val chatMemory: ChatMemory,
) {
    private val chatClient = chatClientBuilder
        .defaultSystem("You are a friendly Kotlin tutor. Answer briefly and use simple words.")
        .build()

    fun ask(conversationId: String, question: String): String? {
        val answer = chatClient.prompt()
            .messages(chatMemory.get(conversationId))
            .user(question)
            .call()
            .content() // throws if the call fails

        if (answer != null) {
            chatMemory.add(conversationId, listOf(UserMessage(question), AssistantMessage(answer)))
        }
        return answer
    }
}
```



Here, `chatMemory.get()` reads the history, and `.messages(...)` puts it into the prompt before the new question. The model is called, and only then `chatMemory.add()` stores the `UserMessage` and the `AssistantMessage` together. A failed call leaves the memory untouched. Note that this client has no `MessageChatMemoryAdvisor`: with both in place, every message would be saved twice. You give up the convenience of the advisor, but you gain precise control. Check how your Spring AI version behaves, since this is an implementation detail that can change.

## Practical notes

- **Concurrent requests.** Adding messages reads the stored ones, merges the new ones, and saves the result. Two simultaneous requests in the same conversation can therefore overwrite each other's changes. If this can happen in your application, handle one request per conversation at a time.

- **Window size.** A larger window gives the model more context but costs more and slows down every call. Choose the smallest size that serves your use case.

- **Test the behavior.** Send a message, ask about it in a follow-up, then clear the conversation and ask again. Tests of memory should check that the follow-up works, that the cleared conversation forgets, and that a second conversation sees none of it.

## Conclusion

Spring AI separates deciding what to remember (`ChatMemory`) from storing it (`ChatMemoryRepository`). Memory holds `UserMessage` and `AssistantMessage` objects per conversation ID and offers `add`, `get`, and `clear`. `MessageWindowChatMemory` keeps the last 20 messages by default and drops whole turns. You attach memory to a `ChatClient` with `MessageChatMemoryAdvisor` and pass a server-derived conversation ID with every call. The system prompt from `defaultSystem` is sent with each request and is not stored in memory. The default storage is in-memory, so add a persistent repository starter if conversations must survive a restart.

The advisor saves the user's message before the model call, so a failed call leaves an unanswered question in the history. If that is not acceptable, keep using `ChatClient`, read the history with `chatMemory.get()`, and add both messages with `chatMemory.add()` only after the call succeeds.

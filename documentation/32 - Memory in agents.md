# Memory in agents

If LLMs are stateless, how do agents maintain continuity across interactions? Not only within the same conversation, or across conversations? The answer is **memory systems**. Memory is what transforms a simple LLM into an intelligent agent capable of learning, adapting, and maintaining coherent long-term interactions.

## Two types of memory

We work with two distinct memory mechanisms, differentiated by the scope and duration of interaction they need to support.

When we want an agent to remember how we started the current dialogue or recall what we wrote in the previous message, we're dealing with **short-term memory**. However, when we work with more complex concepts short-term memory isn't sufficient. Such concepts could be user preferences in communication style, facts they've shared about themselves across multiple conversations, or purchase history in a shopping assistant agent.

We need a mechanism for **persistent data storage** about the user. This is why we have **long-term memory**.

## Short-term Memory (working memory)

- **What it is**: Information actively held in the current context

- **Where it lives**: In the conversation history passed to the LLM

- **Characteristics**:

Limited by context window

- Immediately accessible

- Lost when conversation ends

- High cost (every token counts)



```python
class ShortTermMemory:
    def __init__(self, max_messages=50):
        self.conversation_history = []
        self.max_messages = max_messages

    def add_message(self, role, content):
        self.conversation_history.append({
            "role": role,
            "content": content
        })
        # Trim old messages if exceeding limit
        if len(self.conversation_history) > self.max_messages:
            self.conversation_history = self.conversation_history[-self.max_messages:]
```





```kotlin
data class Message(val role: String, val content: String)

class ShortTermMemory(private val maxMessages: Int = 50) {
    private val history = mutableListOf<Message>()
    val conversationHistory: List<Message> get() = history

    fun addMessage(role: String, content: String) {
        history.add(Message(role, content))
        // Trim old messages if exceeding limit
        if (history.size > maxMessages) {
            repeat(history.size - maxMessages) { history.removeAt(0) }
        }
    }
}
```



## Long-term memory

- **What it is**: Persistent storage of information across sessions

- **Where it lives**: External storage (databases, files, vector stores)

- **Characteristics**:

Unlimited capacity

- Requires retrieval mechanisms

- Survives between conversations

- Lower cost (not in every request)



```python
class LongTermMemory:
    def __init__(self, storage_backend):
        self.storage = storage_backend

    def store(self, key, value, metadata=None):
        """Store information with optional metadata"""
        self.storage.save(key, {
            "value": value,
            "metadata": metadata,
            "timestamp": datetime.now()
        })

    def retrieve(self, query):
        """Retrieve relevant memories based on query"""
        return self.storage.search(query)
```





```kotlin
import java.time.Instant

data class StoredMemory(val value: Any?, val metadata: Map<String, Any?>?, val timestamp: Instant)

interface StorageBackend {
    fun save(key: String, entry: StoredMemory)
    fun search(query: String): List<StoredMemory>
}

class LongTermMemory(private val storage: StorageBackend) {

    /** Store information with optional metadata */
    fun store(key: String, value: Any?, metadata: Map<String, Any?>? = null) {
        storage.save(key, StoredMemory(value, metadata, Instant.now()))
    }

    /** Retrieve relevant memories based on query */
    fun retrieve(query: String): List<StoredMemory> = storage.search(query)
}
```



Both short-term and long-term memory require active management. Short-term memory management is more straightforward, though it still presents challenges.

## Short-term memory management

With short-term memory, we have complete control since we're managing what goes into the context. Let's explore four common strategies:

**Strategy 1: Keep everything**



```python
def keep_everything(conversation_history, new_message):
    """Simple approach: retain all messages"""
    conversation_history.append(new_message)

    return conversation_history
```





```kotlin
fun keepEverything(conversationHistory: MutableList<Message>, newMessage: Message): List<Message> {
    // Simple approach: retain all messages
    conversationHistory.add(newMessage)
    return conversationHistory
}
```



- **Advantages**: Simple implementation, no information loss, complete context always available.

- **Disadvantages**: Expensive (high token usage), can exceed context limits, increasing latency with each message.

- **When to use**: For short conversations or when budget and context window aren't constraints.

**Strategy 2: Sliding window**



```python
def sliding_window(conversation_history, new_message, window_size=10):
    """Keep only the last N messages"""
    conversation_history.append(new_message)
    if len(conversation_history) > window_size:
        conversation_history = conversation_history[-window_size:]

    return conversation_history
```





```kotlin
fun slidingWindow(
    conversationHistory: MutableList<Message>,
    newMessage: Message,
    windowSize: Int = 10
): List<Message> {
    // Keep only the last N messages
    conversationHistory.add(newMessage)
    return if (conversationHistory.size > windowSize) {
        conversationHistory.takeLast(windowSize)
    } else {
        conversationHistory
    }
}
```



- **Advantages**: Predictable cost, simple to implement, focuses on recent context.

- **Disadvantages**: Loses early conversation context, may forget important information from the beginning.

- **When to use**: For ongoing conversations where recent context matters more than historical data.

**Strategy 3: Importance-based filtering**



```python
def importance_based(conversation_history, new_message):
    """Retain only messages marked as important"""
    if is_important(new_message):
        conversation_history.append(new_message)

    return conversation_history
```





```kotlin
fun importanceBased(
    conversationHistory: MutableList<Message>,
    newMessage: Message,
    isImportant: (Message) -> Boolean
): List<Message> {
    // Retain only messages marked as important
    if (isImportant(newMessage)) {
        conversationHistory.add(newMessage)
    }
    return conversationHistory
}
```



- **Advantages**: Focuses on significant information, reduces noise, efficient token usage.

- **Disadvantages**: Requires reliable importance detection, may miss subtly important context, adds complexity.

- **When to use**: When you can reliably identify important messages and want to maximize signal-to-noise ratio.

**Strategy 4: Summarization**



```python
def summarization_strategy(conversation_history, threshold=20):
    """Summarize old messages when history grows too long"""
    summary = summarize(conversation_history[:threshold])
    conversation_history = [summary] + conversation_history[threshold:]

    return conversation_history
```





```kotlin
fun summarizationStrategy(
    conversationHistory: List<Message>,
    threshold: Int = 20,
    summarize: (List<Message>) -> Message
): List<Message> {
    // Summarize old messages when history grows too long
    val summary = summarize(conversationHistory.take(threshold))
    return listOf(summary) + conversationHistory.drop(threshold)
}
```



- **Advantages**: Preserves key information from entire conversation, balances context and cost.

- **Disadvantages**: Summarization costs tokens, may lose nuanced details, adds latency.

- **When to use**: For long conversations where both early and recent context matter.

## Long-term memory challenges

Long-term memory introduces complex questions:

- **What to store?** Not everything is worth remembering

- **How to store it?** Raw text, embeddings, structured data?

- **How to retrieve it?** Keyword search, semantic search, time-based?

- **When to retrieve it?** Every request, or only when needed?

- **How to update it?** Overwrite, append, version control?

- **When to forget?** Memory decay, relevance scoring?

## Memory in practice: personal assistant example

Let's see how both memory types work together:



```python
# User: "I'm planning a trip to Tokyo next month"
# Short-term memory: Immediately available in conversation
# Long-term memory: Store user preference for Tokyo travel

# Next conversation (days later)
# User: "What should I pack?"
# Agent retrieves from long-term memory: User planning Tokyo trip
# Response: "For your Tokyo trip next month, I recommend..."

# The agent remembers across sessions!
```



## The memory hierarchy

A way to think of agent memory is to use computer memory:

Memory type

Computer analogy

Agent equivalent

Speed

Capacity

Cost

Register

CPU Registers

Current thought

Instant

Tiny

Highest

L1 Cache

CPU Cache

Active context

Very Fast

Small

High

RAM

System memory

Short-term memory

Fast

Medium

Medium

SSD

Storage

Long-term memory

Slower

Large

Low

## Conclusion

In this topic, we've focused on the fundamentals of both memory types and explored short-term memory management strategies. Here are the key takeaways from the topic:

- Memory transforms stateless LLMs into stateful agents.

- Short-term memory is limited but immediately accessible.

- Long-term memory provides persistence but requires retrieval.

- Effective agents use both memory types strategically.

- Memory management directly impacts cost, speed, and quality.

In the following topics, we'll dive deeper into long-term memory, exploring the details of storage, retrieval, updates, and memory decay in agent systems.

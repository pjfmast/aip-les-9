# Prompting fundamentals

When you chat with an assistant, the reply feels like part of one continuous conversation. Behind the scenes, the application sends the model your text together with some instructions and earlier messages. All of this is the **prompt**, and its quality largely decides how useful the reply is.

In this topic, you'll learn how chat prompts are structured, what a good system prompt contains, how to write and improve prompts, which settings affect the output, and how to handle data safely.

## Chat prompts and roles

LLMs predict the most likely continuation of the text they receive. Early models simply continued a single block of text, like a smart autocomplete. Modern applications use **chat prompts**: a list of messages, each with a role.

- **System**: instructions written by the developer, usually hidden from the end user. They define who the model is and how it should behave throughout the conversation.

- **User**: the human's input, such as a question or command.

- **Assistant**: the model's previous replies.

- **Tool**: the result of an action the model asked the application to perform, such as a database query. You'll use this role later in the project.

## The model has no memory

LLMs are **stateless**: every call is independent, and the model doesn't remember anything from the previous one. The "memory" you see in chat apps is created by the application, which resends the conversation with every request:



```text
Request 1: [system, user: "Is a green avocado ripe?"]
Request 2: [system, user: "Is a green avocado ripe?",
            assistant: "Not necessarily! Softness is a better sign.",
            user: "What did I just ask you?"]
```



This has practical consequences:

- **The prompt keeps growing.** More text means higher cost, slower replies, and eventually the model's **context window** (the maximum amount of text it can process at once) is exceeded. That's why applications usually keep only the last N messages. The system prompt is sent on every call and is normally not counted as part of that history.

- **Each user needs their own history.** Otherwise, one shopper's conversation would leak into another's.

- **Failed calls need care.** If a request fails, saving the user's message leaves a question without an answer in the history.

## Anatomy of a system prompt

A well-built prompt combines four components. In a chat application, they are spread across the message list.

- **Persona** defines the role, expertise, and tone of the model. It keeps the model sounding consistent instead of slipping back into a generic style.

- **Context** is everything the model needs to know about your situation. Without it, the model guesses. It can come from the conversation history, documents inserted into the prompt (Retrieval-Augmented Generation, or RAG), tool results, or details you add to the system prompt.

- **Task** is a clear instruction describing what to produce, including the subject and scope. In a chat application, this is usually the user's message.

- **Constraints** are the rules for the output: its structure (JSON, a numbered list), length, and style. They make the output predictable, which is essential when an application consumes the reply.

Persona and constraints usually go into the system prompt. Here is one for a grocery shop assistant:



```text
You are a friendly shop assistant for an online grocery store.
Help shoppers with questions about food and cooking.
Keep your replies to three sentences at most.
If you don't know something, say so.
```



The first two lines set the persona and the job, and the last two are constraints. Not every prompt needs all four components: `You are a helpful assistant. Keep your replies short.` is just a persona and a constraint. Start simple and add components only when the output needs them.

A system prompt **guides** the model, but it doesn't guarantee strict behavior. The model can still ignore or misread an instruction, so don't rely on it as the only protection for rules that really matter.

## Writing and improving prompts

Three qualities make a prompt effective:

- **Clarity.** Use direct commands and skip filler like "if you don't mind". Such words add noise and consume tokens.

- **Specificity.** Replace words the model has to interpret with concrete requirements. "Short" could mean three words or three paragraphs.

- **No ambiguity.** Words like "bank" have several meanings, and the model lacks situational cues, so state the domain explicitly.

Compare these two prompts:



```text
Explain how to tell if an avocado is ripe.
Explain how to tell if an avocado is ripe in two sentences, for a beginner cook.
```



Your first prompt rarely works perfectly, so prompting is a cycle: draft, test, analyze the output, and refine. A poor result shows which part of the instruction was unclear. Match the fix to the problem:

- The style is wrong: refine the persona or tone.

- The facts are wrong: add accurate context, and explicitly allow the model to say "I don't know".

- The answer is off-topic: clarify your goal and why you need it.

- The format is wrong: add an example of the desired output.

If one prompt does too much, such as summarizing, translating, and formatting at once, split it into steps. This is called **prompt chaining**, and it also makes problems easier to locate. Avoid over-engineering: a longer prompt isn't automatically better, so change one thing at a time and check whether it helped.

## Model settings

Besides the prompt, the model's behavior depends on its settings. Two matter most:

- **Temperature** controls randomness. Low values make the output consistent and focused, which suits facts and code. High values make it more varied and creative.

- **Max output tokens** limits the reply length, which helps control cost and speed.

These two work differently from a prompt instruction. "Keep your replies short" shapes the answer so that it ends naturally, while a token limit just stops generation, possibly mid-sentence. Use the prompt for brevity and the limit as a safety net for cost.

Other settings exist, such as Top P and Top K (which restrict word choices), frequency and presence penalties (which discourage repetition), and stop sequences (which end generation at a given string). Which settings are available, and their allowed ranges, depend on the provider and the model, so check the documentation of the one you use. In a typical application, you often keep the defaults.

## Working with data responsibly

Prompts are sent to a model hosted by a provider, so treat their content as data leaving your system. Never include personally identifiable information (PII), such as names, addresses, or phone numbers, or confidential company data unless you are sure how the provider handles it. Where possible, **sanitize** the data first: replace real values with placeholders like `User_A`, send the prompt, and map the placeholders back locally. Also keep API keys out of code and prompts, and load them from environment variables.

## Conclusion

Chat prompts use roles (system, user, assistant, and tool) to organize the input. Because models are stateless, the application provides memory by resending the conversation history, which is why history size, the context window, and per-user separation matter.

A good system prompt combines a persona, context, a task, and constraints. It should be clear, specific, and unambiguous, and you improve it through testing rather than guessing. Settings like temperature and max tokens further shape the output, and responsible handling of data keeps your users safe.

# Introduction to Jetpack Compose

Our world is constantly evolving. This is because, on the one hand, the demands on what we already have are increasing. On the other hand, to survive the competitive struggle, entrepreneurs have to fight for each client or user. As a result, modern technologies appear designed to satisfy the needs of entrepreneurs for cost optimization, as well as the needs of users for new opportunities. An example of that is applications we use every day, and without which we can no longer imagine our life.

Today, for us as users, an application that just works isn't enough. We want this app to be fast, convenient, and beautiful, of course. We often judge a book by its cover, and an application by its appearance, as they say, "good clothes open all doors". So, our task as developers is to create a convenient and aesthetically pleasant interface for our application, and **Jetpack Compose** can help us with it.

## What is Compose?

Jetpack Compose is a modern framework developed by Google to create UI for Android apps. It was created to replace the way of building Android UI using XML — Extensible Markup Language, known to all Android developers. So now, thanks to Jetpack Compose, you don't need to learn XML for the sole purpose of creating a UI. You just need to know the syntax of Kotlin, which is now the primary language for developing Android applications recommended by Google. Compose is written in Kotlin, so you can't use another language to develop UI with this framework.

It's worth knowing that Jetpack Compose itself targets Android only. The multiplatform story comes from a separate, closely related project called **Compose Multiplatform**, built by JetBrains on top of the same Compose compiler and APIs, which extends this same declarative approach to desktop, web, and iOS. Since you're learning Compose in the context of Kotlin Multiplatform, this distinction is worth keeping in mind: the concepts and much of the syntax you learn here for Android carry over directly if you later build UIs with Compose Multiplatform, but `Jetpack Compose` and `Compose Multiplatform` are two different — if closely aligned — tools, maintained by two different companies.

Despite this, Compose doesn't depend on new releases of the Android platform, which avoids a significant number of errors associated with innovations in Android, but, at the same time, it uses all Android advantages, such as Material Design, dynamic themes, and much more.

## Declarative UI

You have probably already come across the concept of **declarative programming**, which has become very popular recently. If not yet, in a nutshell, this is an approach with which we only need to describe what we want to achieve. In contrast, with the **imperative approach**, we have to write a sequence of actions necessary to obtain it. To clarify, let's take a better look at the example of a messenger application with a chat list. Suppose we have a list, each element of which is a separate chat with one of our friends. Each item contains the name of our friend and their last message. Also, for unread messages, a badge appears with their number. If among these messages there are important ones, the badge will be red, and the text with the number of messages will be white.

![Chats list example](https://ucarecdn.com/41e4720e-b6ca-4fc2-b903-10e2864d1c27/)

With an imperative UI, you would most likely have to write a function like this to update the UI data:



```kotlin
val chatItem = findViewById(R.id.chat_item)

fun updateChatItem(name: String, lastMessage: String, unread: Int, hasImportant: Boolean) {
    chatItem.setNameText(name)
    chatItem.setMessageText(lastMessage)
    if (unread > 0 && !chatItem.hasCountBadge()) {
        chatItem.addCountBadge()
        if (hasImportant) {
            chatItem.setBadgeBackgroundColor(Color.Red)
            chatItem.setBadgeTextColor(Color.White)
        } else {
            chatItem.setBadgeBackgroundColor(Color.Blue)
            chatItem.setBadgeTextColor(Color.Gray)
        }
    } else if (unread == 0 && chatItem.hasCountBadge()) {
        chatItem.removeCountBadge()
    }
    if (unread > 0) {
        if (unread <= 99) {
            chatItem.setCountBadgeText("$unread")
        } else {
            chatItem.setCountBadgeText("99+")
        }
    }
}
```



So, when we receive new messages, we must ensure that our UI is updated according to the received data. But even in such a simple case, it is easy to make a mistake without considering all possible states.

However, if we rewrite the code above in a declarative style using Compose, the result will be something like this:



```kotlin
@Composable
fun ChatItem(name: String, lastMessage: String, unread: Int, hasImportant: Boolean) {
    Username(name = name)
    Message(message = lastMessage)
    if (unread > 0) {
        Badge(
            text = if (unread > 99) "99+" else "$unread", 
            textColor = if (hasImportant) Colors.White else Colors.Gray, 
            backgroundColor = if (hasImportant) Colors.Red else Colors.Blue
        )
    }
}
```



The declarative approach in describing the UI allows us not to worry about its previous state and whether anything needs to be changed. Jetpack Compose determines what changes are necessary and when they need to be made. This makes the code more concise and understandable, simplifying your work and saving you precious time.

## Power of Kotlin

A huge advantage of Compose is that it uses the full power of Kotlin. For example, to display a list of messages, you can use the collection-standard function `forEach` and just one **Composable** function that takes the message text as a parameter and displays it on the screen:



```kotlin
messageList.forEach { message ->
    Text(message)
}
```



As you might have guessed, we can reuse a Composable function and call it with different data. Each Composable function is a regular Kotlin function that defines the app's UI programmatically. More complex Composable functions internally call other Composable functions, forming a UI hierarchy.

Compose uses intuitive and familiar APIs interfaces of Kotlin, through which the Composable function accepts data, uses it, or passes it to other functions as parameters. Preferably, when properly implemented, these functions should mostly be stateless, that is, not have a state. Following this approach, they can remain dynamic, flexible, and reusable, like any other Kotlins' functions. In addition, since composable functions are regular Kotlin functions at their core, you can use the same refactoring tools as you would use for regular Kotlin code.

## Prerequisites

Before you start creating a UI, you should know the basics of OOP and familiarize yourself with such concepts as functions, parameters, annotations, variables, lambda expressions, and some others in detail. Without a clear understanding of these basics, you will most likely find it difficult to understand how it works and why, so future topics will be dedicated to the above issues. However, if you already have this knowledge, you can just refresh your memory or skip these topics.

## Conclusion

Jetpack Compose is a convenient tool that makes it much easier to create UI elements that, being isolated and independent, can be reused more than once on different screens. Compose allows for avoiding many unforeseen bugs by effectively separating concerns in your code. With it, you, as a developer, can focus on creating a convenient and beautiful user interface in less time and with less code. And if you're just a beginner application developer, you've probably never had to deal with the classic way to create UI using XML, and hopefully, you won't, because it's a real pain in the neck! Good luck with your further studies!

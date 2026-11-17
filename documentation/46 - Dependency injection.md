# Dependency injection

When working with OOP, you may have noticed certain relations between objects. Often, these relations need to be structured, described, and properly utilized. One way to do this is by using dependency injection. What is dependency injection? Well, let's try to answer this question.

## Dependency and dependency injection

First, what is a **dependency**? It's a relation between two classes in which one class uses the functionality of the other class, i.e. one class is dependent on the other.

![a dependency between two classes](https://ucarecdn.com/2b7dd4bc-641b-4e9a-b6d6-ffbf8e454869/)

In OOP, dependencies appear in the form of instance creation inside a class. Through this instance, one class can access the methods of another class. This way classes become dependent.

Now, we will depict an example of a dependency in form of pseudocode. Here we create an instance of the `Email()` class inside of `EmailService()`:



```no-highlight
class Email is
  method sendMessage() is
    ...

class EmailService is
  Email gmail = new Email()

  method sendMessage() is
    gmail.sendMessage()
```



**Dependency injection** or **DI** is the process of transferring the task of object creation to other parts of code. This means making a dependency that you can use. This allows the creation of loose coupling in our code.

> **Note:**

You can take a look at [Coupling and Cohesion](https://hyperskill.org/learn/step/21478) to understand how loose coupling can be beneficial.

There is no agreed point on whether DI is considered a design pattern or not. Considering this as an introduction to DI, the explanation is simplified. This makes it more subjective and debatable. So, for more context, you should learn more about DI and look into inversion of control (IoC) and design patterns to form your own opinion.

## DI example

In the first snippet, the `EmailService` class relies on the `Email` class. With this dependency, you cannot use different types of `Email`. Its instance is constructed inside `EmailService`, so you can't use different variations of it. Also, you can't test different `Email` instances.

To overcome this, let's try to use one type of DI on this code:



```no-highlight
interface Email is
  method sendMessage()


class GmailService implements Email is
  method sendMessage() is ...
  ...


class EmailService is
  method sendMessage(Email service) is
    service.sendMessage()
```



Now, `EmailService` relies on the interface instead of a class. There are two classes that use our `Email` interface as a pattern and build different implementations. Object creation is now moved to other classes and the interface provides EmailService with different implementations. You can also set the implementation you want to access through the `sendMessage()` method:



```no-highlight
EmailService gmail = new EmailService()
gmail.sendMessage(new GmailService())
```



But this is just the tip of the iceberg. There are different types of dependency injection that you need to take into consideration.

## Types of injection

There are three main types of DI. Let's look at them.

- **Method(Interface) Injection **— dependencies are passed through methods that the class can access via interface or another class. The previous snippet of pseudocode was an example of method injection.

-

**Property(Setter) Injection** — dependencies are passed through a separate setter method. Implementation of this injector varies depending on the language. Here is a bare-bones example of this injection in pseudocode:



```no-highlight
class EmailService is
  Email service
  
  method getService() is
    return service

  method setService(Email service) is
    this.service = service


class Main is
  EmailService email = new EmailService()
  email.setService(new GmailService())

  Email gmail = email.getService()
  gmail.sendMessage()
```



Depending on the language, you can work with the `set` method in different ways. For example, in Java, using Spring annotations, you can include parameters from config files into the `set` method. However, this pseudocode is meant to be as simple as possible.

-

**Constructor Injection **— dependencies are provided through the class constructor. Here we have our email example, where we can construct our `EmailService()` with different implementations of `Email()`:



```no-highlight
class EmailService is
  Email email
  
  constructor of EmailService(Email email) is
    this.email = email
 
  method sendMessage() is
    email.sendMessage()


class Main is
   EmailService gmail = new EmailService(new GmailService())
   gmail.sendMessage()
```



## Why do we need DI?

DI allows us to make code, parts of which are less interdependent on each other. Through DI, we can release some classes from the object creation process and pass them to injectors that can provide said objects. This way, one class will know nothing about the object creation process in the other. It allows you to reuse dependent classes without adjustment for each new instance of an object.

This comes in handy when you need to extend and modify your code. But, it can also improve your testing process. With DI, you can provide different instances with different tweaks of the same object, you can test how your code will react to it.

But, there are some disadvantages to DI. This can be addressed to the complexity of its implementation. It includes the steep learning curve for DI, problems caused by the overuse of DI, and troubles implementing DI with different frameworks and libraries.

## Conclusion

Let's summarize this topic:

- The main point of DI is to loosen coupling. It helps you work with dependencies.

- By using DI you can increase code flexibility and simplify the testing process.

- It's a complex topic with different implementations based on the scenario.

- DI in different languages has peculiarities that can affect how you work with it.

Dependency injection can turn out to be a rather complex process. But, you now have an idea of what it is and how it works. You can also learn more about DI and its implementation on your own.

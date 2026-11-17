# Kotlin/Native

Kotlin/Native is what makes the iOS target in Kotlin Multiplatform possible at all. There's no JVM on iOS, so `iosMain` code can't run as bytecode the way `androidMain` code does — Kotlin/Native solves that by compiling Kotlin straight to native machine code through an LLVM-based backend, producing a binary that runs with no virtual machine involved. The same backend also targets watchOS, tvOS, and several desktop platforms (Linux, Windows, and macOS), but iOS is where it matters most for our scope.

That's the core distinction worth holding onto: Kotlin/JVM compiles to bytecode the JVM interprets or JIT-compiles at runtime, while Kotlin/Native compiles ahead-of-time to the target platform's actual instructions. In a typical KMP project you're already relying on this without necessarily naming it — every line in `iosMain` compiles through it, using the same language, the same `expect`/`actual` pattern for genuine platform differences (covered earlier), and mostly the same standard library. What differs is what happens underneath and at the edges: how the compiled output reaches Xcode, how it talks to platform APIs, and how it manages memory. Those are the three things this topic covers.

## Getting Kotlin code into an iOS app

Kotlin/Native compiles `commonMain` and `iosMain` into an Apple framework — a `.framework` bundle Xcode links against like any other native dependency. Every public Kotlin class, function, and property gets an Objective-C-compatible signature generated automatically, which is what lets Swift call `MyKotlinClass()` as though it were native. Kotlin types map onto their closest Objective-C/Swift equivalent — `String` to `NSString`/`String`, `List` to `NSArray`, nullable types to Optionals — and that mapping is why a handful of Kotlin APIs, default arguments among them, read a little differently once they show up on the Swift side.

## Calling the other direction: cinterop

Objective-C/Swift interop is generated automatically because Kotlin/Native is producing the framework in that direction. Going the other way — calling an existing C or Objective-C library from Kotlin — takes an explicit step, since that library isn't a Kotlin/Native output at all. That's what the `cinterop` tool is for: point it at a library's headers with a small `.def` file, and it generates Kotlin bindings you can call directly.

// libcurl.def
headers = curl/curl.h
package = libcurl

Wired into a target's Gradle configuration, this makes the library's functions callable as ordinary Kotlin functions in `iosMain` or any other native target. Most projects never touch `cinterop` directly — Ktor, SQLDelight, and the other libraries this course uses are already multiplatform — but it's the mechanism underneath any Kotlin/Native library that does wrap a native dependency.

## Memory management

Versions before Kotlin 1.7.20 used a model built on reference counting and object "freezing": mutable state had to be explicitly frozen before it could safely cross threads, and getting that wrong was a common source of runtime crashes. If a tutorial or Stack Overflow answer mentions `freeze()`, `@SharedImmutable`, or restrictions on sharing mutable state across threads, it's describing this old model — freezing was deprecated in 1.7.20 and the legacy memory manager was removed from the compiler entirely in 1.9.20.

Kotlin/Native now uses a tracing garbage collector, conceptually the same approach the JVM uses. There's no freezing step, and mutable state can be shared across threads the same way it would be in JVM or JS code. Coroutines, `Mutex`, and the other concurrency primitives you'd use in `commonMain` work the same way in `iosMain` — no platform-specific synchronization tricks required to keep shared logic thread-safe on iOS.

## Performance characteristics

Because there's no VM to start or warm up, Kotlin/Native binaries run at full speed immediately — useful for anything sensitive to startup latency, including mobile apps where cold-start time matters. The tradeoff sits on the compiler side: ahead-of-time compilation is slower than the JVM's, so `iosMain`-heavy builds and simulator runs tend to take longer than the equivalent Android build. Worth factoring in when deciding how much logic to iterate on from the Android side before compiling it for iOS.

## Conclusion

Kotlin/Native is the compiler backend that makes iOS — and other non-JVM native targets — possible in Kotlin Multiplatform: it compiles shared code straight to native instructions, packages the result as a framework Xcode can consume, and generates Objective-C-compatible signatures in the process. `cinterop` handles the rarer reverse case, wrapping an existing C or Objective-C library for use from Kotlin. The detail most worth carrying forward is the memory model: Kotlin/Native has used a tracing garbage collector with no freezing since Kotlin 1.7.20, so shared mutable state behaves the same way it does everywhere else in a KMP project.

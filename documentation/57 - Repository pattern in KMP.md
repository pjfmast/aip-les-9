# Repository pattern in KMP

Serialization gets a network response into a typed Kotlin object — but that object is still shaped by the wire format it came from: nullable fields for anything the API might omit, naming that matches a JSON schema rather than your app's own vocabulary, and quirks that come and go with API versions. Used directly throughout an app, that shape becomes a liability: a backend field rename or a schema change ripples into every screen that touched it. The repository pattern is how you contain that — a boundary between what the network (or a local database) hands you and what the rest of the app actually works with.

## Three layers

A typical shared data layer has three pieces:

- A **DTO** (data transfer object) — the `@Serializable` class matching the API's shape, exactly as covered when parsing responses.

- A **domain model** — a plain class shaped around what your app actually needs, independent of any particular backend.

- A **repository** — the thing the rest of your app talks to, which fetches DTOs and hands back domain models.

A small mapper function does the translation between the two:



```kotlin
// commonMain
@Serializable
data class TaskDto(
    val id: Int,
    val name: String,
    val status: String,
    val due_date: String? = null
)

data class Task(
    val id: Int,
    val name: String,
    val status: TaskStatus,
    val dueDate: LocalDate?
)

enum class TaskStatus { TODO, ONGOING, DONE }

fun TaskDto.toDomain(): Task = Task(
    id = id,
    name = name,
    status = TaskStatus.valueOf(status.uppercase()),
    dueDate = due_date?.let { LocalDate.parse(it) }
)
```



Nothing outside this mapping ever sees `due_date` as a nullable string or `status` as a raw string — by the time data reaches the rest of the app, it's already in the shape the app actually wants.

## Defining the repository as an interface

The repository itself is declared as an interface, with the concrete implementation depending on it rather than the other way around:



```kotlin
// commonMain
interface TaskRepository {
    suspend fun getTasks(): List<Task>
    suspend fun getTask(id: Int): Task?
    suspend fun createTask(task: Task)
}

class KtorTaskRepository(private val client: HttpClient) : TaskRepository {
    override suspend fun getTasks(): List<Task> =
        client.get("/tasks").body<List<TaskDto>>().map { it.toDomain() }

    override suspend fun getTask(id: Int): Task? =
        client.get("/tasks/$id").body<TaskDto?>()?.toDomain()

    override suspend fun createTask(task: Task) {
        client.post("/tasks") { setBody(task.toDto()) }
    }
}
```



This ordering matters more than it might look: the rest of your app depends only on `TaskRepository`, never on `KtorTaskRepository` directly. Business logic that calls `getTasks()` doesn't know or care whether the answer came from a live network call, a local cache, or — in a test — a hardcoded list. That's the whole point of putting an interface at this boundary: whatever sits behind it can change completely without anything on the other side noticing.

This is also a good, concrete case for the interface-over-`expect`/`actual` decision covered earlier: a repository has multiple methods, no platform-specific behavior at all, and an obvious need for a fake substitute in tests — exactly the profile that calls for an interface wired through DI, not a platform seam. Wiring it in is the same pattern already covered:



```kotlin
// commonMain
val dataModule = module {
    single<TaskRepository> { KtorTaskRepository(get()) }
}
```



Worth noticing what's *not* here: no `expect`, no `actual`, no platform-specific source sets at all. Ktor and kotlinx.serialization are both multiplatform, so a repository built on them typically lives entirely in `commonMain`. Most of the platform differences covered earlier in this section exist because something — Android's context, iOS's native APIs — genuinely differs per platform. A repository built on multiplatform networking and serialization libraries has no such difference to resolve, which is worth calling out explicitly: not every piece of shared code is a platform-differences problem.

## Testing behind the interface

Because the rest of the app depends on the interface, testing it means substituting a fake — no network, no serialization, no platform involved:



```kotlin
// commonTest
class FakeTaskRepository : TaskRepository {
    val tasks = mutableListOf<Task>()
    override suspend fun getTasks(): List<Task> = tasks
    override suspend fun getTask(id: Int): Task? = tasks.find { it.id == id }
    override suspend fun createTask(task: Task) { tasks.add(task) }
}
```



Swapped in through the same Koin module override pattern covered earlier, this lets you test anything that depends on `TaskRepository` without touching a network at all.

## Conclusion

A repository's job is to be the one place in a shared codebase where wire-format DTOs get translated into the domain models the rest of the app actually uses, so that a backend's shape never leaks past that one boundary. Declaring it as an interface — with the network- or database-backed implementation depending on that interface, not the reverse — is what makes the data layer swappable and testable, and it's a case where a KMP data layer often needs none of the platform-specific machinery covered elsewhere in this section at all.

# Offline-first architecture

A repository with a single data source only has one job: fetch from the network, map to a domain model, hand it back. Once a local database enters the picture as a second data source, a repository has a real decision to make — which one does the UI actually depend on? Getting this decision right is what "offline-first" means in practice, and it turns out to be a fairly small change to a pattern already covered, not a new architecture bolted on top of it.

## Local as the source of truth

A common but flawed approach has the repository try the network first and fall back to a local cache on failure. This means the UI sometimes receives a live network response and sometimes receives cached data, the app behaves differently depending on connectivity in ways that leak into the UI layer, and every read is a potential network call with its own latency and failure handling.

The offline-first alternative flips this: the UI never reads from the network directly. It only ever reads from the local database, and the network's only job is to keep that database up to date. Reading becomes a database query — specifically, the same kind of reactive query already covered — rather than a network call:



```kotlin
// commonMain
interface TaskRepository {
    fun observeTasks(): Flow<List<Task>>
    suspend fun refresh()
    suspend fun createTask(task: Task)
}

class OfflineFirstTaskRepository(
    private val api: TaskApi,
    private val database: Database
) : TaskRepository {

    override fun observeTasks(): Flow<List<Task>> =
        database.taskQueries.selectAll()
            .asFlow()
            .mapToList(Dispatchers.IO)
            .map { rows -> rows.map { it.toDomain() } }

    override suspend fun refresh() {
        val tasksFromApi = api.getTasks()
        database.transaction {
            tasksFromApi.forEach { dto -> database.taskQueries.upsert(dto.toEntity()) }
        }
    }

    override suspend fun createTask(task: Task) {
        database.taskQueries.insert(task.toEntity())
        api.createTask(task.toDto())
    }
}
```



`observeTasks()` is what the UI collects, and it's always local. `refresh()` is what triggers a network fetch — the UI calls it to trigger a sync, not to "get" data, and whatever it writes to the database flows back to the UI automatically through the `Flow` that's already being collected. This shape sometimes goes by the name "network-bound resource" in Android architecture guides, though the idea is the same regardless of what it's called: the database is the one thing the UI trusts, and the network is one of the things that keeps it current.

`refresh()` itself gets called from the obvious places — when a screen first appears, on a pull-to-refresh gesture, on a timer, or when connectivity comes back after being offline. None of that logic is unusual enough to need its own walkthrough.

## Handling writes made while offline

Reads are the easy half. Writes need a bit more care, since a `createTask()` call like the one above still fails outright if the network request throws while offline. A common fix is to write locally first — so the UI updates immediately regardless of connectivity — and track whether that write has actually reached the server yet:



```mysql
CREATE TABLE Task (
    id TEXT PRIMARY KEY NOT NULL,
    name TEXT NOT NULL,
    updated_at INTEGER NOT NULL,
    sync_status TEXT NOT NULL DEFAULT 'SYNCED'
);
```





```kotlin
override suspend fun createTask(task: Task) {
    database.taskQueries.insert(task.toEntity(syncStatus = "PENDING"))
    try {
        api.createTask(task.toDto())
        database.taskQueries.updateSyncStatus(task.id, "SYNCED")
    } catch (e: IOException) {
        // stays PENDING — a later sync pass will retry it
    }
}
```



A separate sync pass — triggered the same way `refresh()` is — can then query for anything still `PENDING` and retry pushing it, without the UI needing to know any of this happened.

## Conflict resolution

Writing locally first raises an obvious question: what happens if the same record changes both locally, while offline, and on the server — from another device, say — before the two are reconciled? The simplest workable answer is last-write-wins, using an `updated_at` timestamp to decide which version survives:



```kotlin
suspend fun mergeIncoming(remote: TaskDto) {
    val local = database.taskQueries.selectById(remote.id).executeAsOneOrNull()
    if (local == null || remote.updatedAt > local.updated_at) {
        database.taskQueries.upsert(remote.toEntity())
    }
    // otherwise the local copy is newer — keep it, and let the pending sync push it up later
}
```



Worth being upfront about this: last-write-wins can silently discard a legitimate concurrent edit, with nothing telling either side that it happened. That's an acceptable trade-off for plenty of apps and a genuinely bad one for others — anything truly collaborative usually needs field-level merging, a user-facing conflict prompt, or something more structured like CRDTs. Those are each substantial topics in their own right; last-write-wins is a reasonable, honest starting point, not a complete answer.

## Tracking sync state

A single "last synced at" timestamp for the whole dataset — as opposed to the per-row `updated_at` used above — doesn't belong in the database at all; it's exactly the kind of standalone value key-value storage already covers:



```kotlin
settings.putLong("tasks_last_synced_at", Clock.System.now().toEpochMilliseconds())
```



That value is what a screen would check to decide whether a `refresh()` is actually due when it appears, rather than syncing on every single visit regardless of how recently it last succeeded.

## Conclusion

Offline-first isn't a separate architecture — it's what the repository pattern already implies once a second data source exists: the UI depends on the local database's `Flow`-based queries and nothing else, the network's job shrinks to "populate the database," and a small amount of bookkeeping — a `sync_status` column for pending writes, an `updated_at` column for resolving conflicts, and a last-synced timestamp in key-value storage — is what turns "the database is the source of truth" from a slogan into something that actually works while offline.

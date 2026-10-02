# LearnPath

Android learning dashboard. Kotlin, Coroutines, Flow, MVVM, Jetpack Compose.

## Run

Open in Android Studio and run `app`, or `./gradlew assembleDebug`. Debug APK: `app/build/outputs/apk/debug/`.

Demo login: any valid email and a password of at least 6 characters. Password `wrongpass` returns the login API error. Open the dashboard once while online, then turn the network off. The course list, course details, and marking a lesson complete still work from the local database. Completing a lesson updates that row and the course progress on both screens.

Progress is always `completed lessons / total`, so the checklist and the percentage cannot drift. The sample Generative AI value of 40% seeds 6 of 16 lessons, which is **37%** with integer division. Python is 13/20 = 65%. Full Stack is 7/28 = 25%.

## 1. Architecture

UI → ViewModel → repository interfaces → mock remote source + Room.

One app module, split into `domain`, `data`, and `presentation`. Each screen exposes one `StateFlow` of sealed state (loading, success, empty, error) and one-shot navigation events on a `SharedFlow`. Compose depends only on repository interfaces, so the mock sources can be swapped for Retrofit without touching the UI. `AppContainer` wires the graph by hand. At this size that is easier to follow than Hilt, and it is the same graph Hilt would hide.

Room is the source of truth. A refresh writes the network payload into the database, then screens collect that data. The dashboard and the detail screen therefore stay in sync when a lesson is marked complete.

## 2. Offline support

`NetworkMonitor` listens to `ConnectivityManager`. The mock course API fails immediately when there is no active network. `CourseRepository` expands `assets/courses.json` into courses and lessons, stores them in Room, and keeps any lesson already completed on the device when a later refresh arrives (`local OR remote`). If refresh fails and Room already has rows, the UI keeps showing them and marks the screen offline. If refresh fails and the cache is empty, the screen shows an error and a retry. Marking a lesson complete is a local write and does not need the network. This is a cache, not a sync engine.

## 3. Security

The mock access token is held only in memory and cleared on logout. In production I would keep the short-lived access token in memory, store the refresh token in Encrypted DataStore or EncryptedSharedPreferences backed by the Android Keystore, and never put tokens in Room, plaintext SharedPreferences, logs, or backups. Auth traffic would be HTTPS with certificate pinning. Logout would also wipe that user's local catalog so the next person on the device cannot read it.

## 4. Scale

For 1 million users and hundreds of courses I would:

- Page a course-summary endpoint (id, title, instructor, progress, lesson count) with Paging 3 and RemoteMediator, and load lessons only on the detail screen.
- Split `:domain`, `:data`, and feature modules, and add a real sync contract: `updatedSince` or ETag, WorkManager refresh, and a small outbox for lesson completions.
- Add crash and trace reporting, baseline profiles, and R8. Scope the database by user id.
- Rotate tokens, pin the auth host, and revoke sessions on the server.
- Run unit tests plus one instrumented path (login → complete a lesson) on every pull request, then ship with a staged Play rollout.

## 5. iOS / macOS

Same boundaries in Swift. SwiftUI views bind to an `@Observable` model. Repositories stay protocols. `URLSession` replaces the mock remote source, or a Kotlin Multiplatform data layer if one implementation should serve both clients. SwiftData or Core Data replaces Room, still as the source of truth, with `NWPathMonitor` for reachability. Tokens go in the Keychain. `NavigationStack` covers login, dashboard, and detail. Completing a lesson writes locally and both screens read progress from that store. A macOS target can share the SwiftUI views and widen the dashboard layout.

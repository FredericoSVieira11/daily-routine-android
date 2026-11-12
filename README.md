# Daily Routine

A small Android habit tracker built as a weekly practice project to exercise modern Android architecture and tooling.

## Status

- [x] Project setup, Hilt, theme, type-safe navigation
- [x] Room database, repository and use cases
- [x] Habit list and create/edit screens
- [ ] Stats (streaks, weekly completion)
- [ ] DataStore preferences and WorkManager reminders
- [ ] Unit and UI tests

## Tech stack

- Kotlin 2.2, Jetpack Compose, Material 3
- Hilt (dependency injection, with KSP)
- Navigation Compose with type-safe routes (kotlinx.serialization)
- Room (local persistence)
- Coroutines and Flow / StateFlow
- Android Gradle Plugin 9 (built-in Kotlin), version catalog

## Architecture

Single module, organised by layer inside each feature. Clean Architecture with MVVM and unidirectional data flow.

    com.fsv.dailyroutine
    ├── core
    │   ├── designsystem   (theme, shared components)
    │   ├── di             (Hilt modules)
    │   └── navigation     (routes and NavHost)
    └── feature
        └── habits
            ├── data           (Room, DAOs, repository implementation)
            ├── domain         (models, repository interface, use cases)
            └── presentation   (ViewModels, UI state, Compose screens)

The dependency rule points towards `domain`, which has no Android dependencies, so use cases stay trivial to unit test.

## Technical decisions

- **Single module:** for a one-week project, multi-module would be overhead. Modularisation is planned for a later project.
- **KSP instead of kapt:** faster and the current standard for Hilt and Room.
- **`android.disallowKotlinSourceSets=false`:** with AGP 9's built-in Kotlin, the KSP plugin still registers generated sources through the legacy `kotlin.sourceSets` API, which AGP blocks by default. This flag is a temporary workaround and can be removed once KSP no longer needs it.
- **Type-safe navigation:** routes are `@Serializable` objects/classes instead of string routes.
- **Explicit `kotlinx-coroutines-android` dependency:** instrumented tests run the app and test code in separate APKs, so a newer `coroutines-test` can crash with `NoSuchMethodError` against an older transitive `coroutines-core`. Declaring the app's coroutines version explicitly keeps both aligned.
- - **Single source of truth:** the UI never keeps its own copy of completion state. Toggling writes to Room, and the list updates because the Room `Flow` re-emits.
- **State vs. route composables:** `HabitListRoute` talks to the ViewModel, while `HabitListScreen` only receives state and callbacks, which keeps it previewable and testable.

## Running the project

Open in a recent Android Studio, sync Gradle and run the `app` configuration (minSdk 26).

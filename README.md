# Daily Routine

A small Android habit tracker built as a weekly practice project to exercise modern Android architecture and tooling.

## Status

- [x] Project setup, Hilt, theme, type-safe navigation
- [x] Room database, repository and use cases
- [x] Habit list and create/edit screens
- [x] Unit tests (use cases, ViewModels) and instrumented DAO tests
- [ ] Stats (streaks, weekly completion)
- [ ] DataStore preferences and WorkManager reminders
- [ ] Compose UI tests

## Tech stack

- Kotlin 2.2, Jetpack Compose, Material 3
- Hilt (dependency injection, with KSP)
- Navigation Compose with type-safe routes (kotlinx.serialization)
- Room (local persistence)
- Coroutines and Flow / StateFlow
- Android Gradle Plugin 9 (built-in Kotlin), version catalog
- Testing: JUnit, Turbine, kotlinx-coroutines-test, in-memory fakes, and an in-memory Room database for DAO tests

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
- **Single source of truth:** the UI never keeps its own copy of completion state. Toggling writes to Room, and the list updates because the Room `Flow` re-emits.
- **State vs. route composables:** `HabitListRoute` talks to the ViewModel, while `HabitListScreen` only receives state and callbacks, which keeps it previewable and testable.
- **Validation in the domain layer:** `CreateHabitUseCase` and `UpdateHabitUseCase` share the same input rules and return sealed results, so the ViewModel only maps outcomes to UI state and the compiler forces every case to be handled.
- **One-shot events via `Channel`:** "saved" is delivered exactly once and is not replayed after rotation. The alternative is modelling it as consumable state.
- **One form for create and edit:** the `HabitForm` route carries an optional `habitId`, and the ViewModel switches mode from `SavedStateHandle`. Updates only touch name, description and reminder, so completion history is never lost.
- **Row tap vs. checkbox:** tapping a row opens the habit and the checkbox toggles completion. A `toggleable` row merges child semantics, which would hide any nested action from TalkBack.
- **Fakes over mocks, injected `Clock`:** repositories are tested with in-memory fakes and time comes from an injected `Clock`, so tests are deterministic and survive refactors.

## Running the project

Open in a recent Android Studio, sync Gradle and run the `app` configuration (minSdk 26).

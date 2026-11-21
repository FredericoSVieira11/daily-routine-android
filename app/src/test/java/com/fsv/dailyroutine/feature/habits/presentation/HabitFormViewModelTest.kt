package com.fsv.dailyroutine.feature.habits.presentation

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.fsv.dailyroutine.feature.habits.domain.FakeHabitRepository
import com.fsv.dailyroutine.feature.habits.domain.usecase.CreateHabitUseCase
import com.fsv.dailyroutine.feature.habits.domain.usecase.GetHabitUseCase
import com.fsv.dailyroutine.feature.habits.domain.usecase.HabitNameError
import com.fsv.dailyroutine.feature.habits.domain.usecase.UpdateHabitUseCase
import com.fsv.dailyroutine.util.MainDispatcherRule
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset

class HabitFormViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 11, 10)
    private val clock = Clock.fixed(Instant.parse("2026-11-10T10:00:00Z"), ZoneOffset.UTC)
    private val repository = FakeHabitRepository()

    private fun createViewModel(habitId: Long? = null) = HabitFormViewModel(
        savedStateHandle = SavedStateHandle(listOfNotNull(habitId?.let { "habitId" to it }).toMap()),
        getHabit = GetHabitUseCase(repository),
        createHabit = CreateHabitUseCase(repository, clock),
        updateHabit = UpdateHabitUseCase(repository),
    )

    // ---- create mode ----

    @Test
    fun blankNameShowsErrorAndDoesNotSave() = runTest {
        val viewModel = createViewModel()
        viewModel.onNameChange("   ")

        viewModel.onSaveClick()

        assertEquals(HabitNameError.Blank, viewModel.uiState.value.nameError)
        assertFalse(viewModel.uiState.value.isSaving)
        assertTrue(repository.observeActiveHabits().first().isEmpty())
    }

    @Test
    fun tooLongNameShowsError() = runTest {
        val viewModel = createViewModel()
        viewModel.onNameChange("a".repeat(CreateHabitUseCase.MAX_NAME_LENGTH + 1))

        viewModel.onSaveClick()

        assertEquals(HabitNameError.TooLong, viewModel.uiState.value.nameError)
    }

    @Test
    fun editingTheNameClearsTheError() = runTest {
        val viewModel = createViewModel()
        viewModel.onSaveClick()
        assertEquals(HabitNameError.Blank, viewModel.uiState.value.nameError)

        viewModel.onNameChange("R")

        assertNull(viewModel.uiState.value.nameError)
    }

    @Test
    fun validFormStoresHabitAndEmitsSavedEvent() = runTest {
        val viewModel = createViewModel()
        viewModel.onNameChange("  Run  ")
        viewModel.onDescriptionChange("   ")
        viewModel.onReminderChange(LocalTime.of(8, 30))

        viewModel.events.test {
            viewModel.onSaveClick()

            assertEquals(HabitFormEvent.Saved, awaitItem())
        }

        val habit = repository.observeActiveHabits().first().single()
        assertEquals("Run", habit.name)
        assertNull(habit.description)
        assertEquals(LocalTime.of(8, 30), habit.reminderTime)
    }

    @Test
    fun reminderCanBeSetAndCleared() = runTest {
        val viewModel = createViewModel()

        viewModel.onReminderChange(LocalTime.of(7, 0))
        assertEquals(LocalTime.of(7, 0), viewModel.uiState.value.reminderTime)

        viewModel.onReminderChange(null)
        assertNull(viewModel.uiState.value.reminderTime)
    }

    // ---- edit mode ----

    @Test
    fun editModeLoadsTheExistingHabitIntoTheForm() = runTest {
        val id = repository.createHabit("Run", "5 km", today, LocalTime.of(7, 30))

        val state = createViewModel(habitId = id).uiState.value

        assertTrue(state.isEditing)
        assertFalse(state.isLoading)
        assertEquals("Run", state.name)
        assertEquals("5 km", state.description)
        assertEquals(LocalTime.of(7, 30), state.reminderTime)
    }

    @Test
    fun editModeSavesChangesWithoutLosingCompletions() = runTest {
        val id = repository.createHabit("Run", null, today, null)
        repository.setCompleted(id, today, true)
        val viewModel = createViewModel(habitId = id)
        viewModel.onNameChange("Run 5 km")

        viewModel.events.test {
            viewModel.onSaveClick()

            assertEquals(HabitFormEvent.Saved, awaitItem())
        }

        val habit = repository.observeActiveHabits().first().single()
        assertEquals("Run 5 km", habit.name)
        assertTrue(habit.isCompletedOn(today))
    }

    @Test
    fun editModeWithBlankNameShowsErrorAndKeepsStoredHabit() = runTest {
        val id = repository.createHabit("Run", null, today, null)
        val viewModel = createViewModel(habitId = id)
        viewModel.onNameChange("  ")

        viewModel.onSaveClick()

        assertEquals(HabitNameError.Blank, viewModel.uiState.value.nameError)
        assertEquals("Run", repository.observeActiveHabits().first().single().name)
    }

    @Test
    fun missingHabitEmitsNotFound() = runTest {
        val viewModel = createViewModel(habitId = 999)

        viewModel.events.test {
            assertEquals(HabitFormEvent.HabitNotFound, awaitItem())
        }
    }
}
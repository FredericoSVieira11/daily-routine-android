package com.fsv.dailyroutine.feature.habits.presentation

import app.cash.turbine.test
import com.fsv.dailyroutine.feature.habits.domain.FakeHabitRepository
import com.fsv.dailyroutine.feature.habits.domain.usecase.CreateHabitUseCase
import com.fsv.dailyroutine.feature.habits.domain.usecase.HabitNameError
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
import java.time.LocalTime
import java.time.ZoneOffset

class CreateHabitViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val clock = Clock.fixed(Instant.parse("2026-11-10T10:00:00Z"), ZoneOffset.UTC)
    private val repository = FakeHabitRepository()

    private fun createViewModel() = CreateHabitViewModel(CreateHabitUseCase(repository, clock))

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
        viewModel.onSaveClick() // nome vazio -> erro
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

            assertEquals(CreateHabitEvent.Saved, awaitItem())
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
}
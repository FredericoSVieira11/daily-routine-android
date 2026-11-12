package com.fsv.dailyroutine.feature.habits.presentation

import app.cash.turbine.test
import com.fsv.dailyroutine.feature.habits.domain.FakeHabitRepository
import com.fsv.dailyroutine.feature.habits.domain.usecase.ObserveHabitsUseCase
import com.fsv.dailyroutine.feature.habits.domain.usecase.SetHabitCompletionUseCase
import com.fsv.dailyroutine.util.MainDispatcherRule
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class HabitListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val today = LocalDate.of(2026, 11, 10)
    private val clock = Clock.fixed(Instant.parse("2026-11-10T10:00:00Z"), ZoneOffset.UTC)
    private val repository = FakeHabitRepository()

    private fun createViewModel() = HabitListViewModel(
        observeHabits = ObserveHabitsUseCase(repository),
        setHabitCompletion = SetHabitCompletionUseCase(repository, clock),
        clock = clock,
    )

    @Test
    fun initialStateIsLoading() {
        val viewModel = createViewModel()

        assertEquals(HabitListUiState.Loading, viewModel.uiState.value)
    }

    @Test
    fun emitsEmptyWhenThereAreNoHabits() = runTest {
        val viewModel = createViewModel()

        viewModel.uiState.test {
            assertEquals(HabitListUiState.Empty, expectMostRecentItem())
        }
    }

    @Test
    fun mapsHabitsAndCountsOnlyTodaysCompletions() = runTest {
        val water = repository.createHabit("Drink water", null, today, null)
        val run = repository.createHabit("Run", "5 km", today, null)
        repository.setCompleted(water, today, true)
        repository.setCompleted(run, today.minusDays(1), true)

        val viewModel = createViewModel()

        viewModel.uiState.test {
            val state = expectMostRecentItem() as HabitListUiState.Content
            assertEquals(1, state.completedToday)
            assertEquals(listOf(true, false), state.habits.map { it.completedToday })
            assertEquals("5 km", state.habits[1].description)
        }
    }

    @Test
    fun toggleUpdatesStateThroughTheRepository() = runTest {
        val id = repository.createHabit("Drink water", null, today, null)
        val viewModel = createViewModel()

        viewModel.uiState.test {
            val before = expectMostRecentItem() as HabitListUiState.Content
            assertFalse(before.habits.single().completedToday)

            viewModel.onToggle(id, true)

            val after = awaitItem() as HabitListUiState.Content
            assertTrue(after.habits.single().completedToday)
            assertEquals(1, after.completedToday)
        }
    }
}
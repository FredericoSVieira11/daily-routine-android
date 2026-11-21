package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.FakeHabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.LocalDate
import java.time.LocalTime

class UpdateHabitUseCaseTest {

    private val today = LocalDate.of(2026, 11, 10)
    private val repository = FakeHabitRepository()
    private val useCase = UpdateHabitUseCase(repository)

    @Test
    fun blankNameIsRejectedAndHabitIsUnchanged() = runTest {
        val id = repository.createHabit("Run", null, today, null)

        val result = useCase(id, name = "  ", description = null, reminderTime = null)

        assertEquals(UpdateHabitResult.InvalidName(HabitNameError.Blank), result)
        assertEquals("Run", repository.observeActiveHabits().first().single().name)
    }

    @Test
    fun validUpdateTrimsFieldsAndKeepsCompletions() = runTest {
        val id = repository.createHabit("Run", "old", today, null)
        repository.setCompleted(id, today, true)

        val result = useCase(id, name = "  Run 5 km ", description = "  ", reminderTime = LocalTime.of(7, 30))

        assertEquals(UpdateHabitResult.Success, result)
        val habit = repository.observeActiveHabits().first().single()
        assertEquals("Run 5 km", habit.name)
        assertNull(habit.description)
        assertEquals(LocalTime.of(7, 30), habit.reminderTime)
        assertTrue(habit.isCompletedOn(today))
    }

    @Test
    fun unknownHabitReturnsNotFound() = runTest {
        val result = useCase(habitId = 999, name = "Run", description = null, reminderTime = null)

        assertEquals(UpdateHabitResult.NotFound, result)
    }
}
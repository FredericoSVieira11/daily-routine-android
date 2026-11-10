package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.FakeHabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class CreateHabitUseCaseTest {

    private val clock = Clock.fixed(Instant.parse("2026-11-10T10:00:00Z"), ZoneOffset.UTC)
    private val repository = FakeHabitRepository()
    private val useCase = CreateHabitUseCase(repository, clock)

    @Test
    fun blankNameIsRejected() = runTest {
        val result = useCase(name = "   ", description = null, reminderTime = null)

        assertEquals(CreateHabitResult.InvalidName(HabitNameError.Blank), result)
        assertTrue(repository.observeActiveHabits().first().isEmpty())
    }

    @Test
    fun tooLongNameIsRejected() = runTest {
        val name = "a".repeat(CreateHabitUseCase.MAX_NAME_LENGTH + 1)

        val result = useCase(name = name, description = null, reminderTime = null)

        assertEquals(CreateHabitResult.InvalidName(HabitNameError.TooLong), result)
    }

    @Test
    fun validHabitIsStoredWithTrimmedFieldsAndTodayDate() = runTest {
        val result = useCase(name = "  Drink water  ", description = "   ", reminderTime = null)

        assertTrue(result is CreateHabitResult.Success)
        val habit = repository.observeActiveHabits().first().single()
        assertEquals("Drink water", habit.name)
        assertNull(habit.description)
        assertEquals(LocalDate.of(2026, 11, 10), habit.createdAt)
    }
}
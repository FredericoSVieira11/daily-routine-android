package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.FakeHabitRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneOffset

class SetHabitCompletionUseCaseTest {

    private val today = LocalDate.of(2026, 11, 10)
    private val clock = Clock.fixed(Instant.parse("2026-11-10T10:00:00Z"), ZoneOffset.UTC)
    private val repository = FakeHabitRepository()
    private val useCase = SetHabitCompletionUseCase(repository, clock)

    private suspend fun createHabit(): Long = repository.createHabit("Run", null, today, null)

    private suspend fun isCompleted(date: LocalDate): Boolean =
        repository.observeActiveHabits().first().single().isCompletedOn(date)

    @Test
    fun marksHabitAsCompletedToday() = runTest {
        val id = createHabit()

        val applied = useCase(habitId = id, completed = true)

        assertTrue(applied)
        assertTrue(isCompleted(today))
    }

    @Test
    fun unmarksHabitWhenCompletedIsFalse() = runTest {
        val id = createHabit()
        useCase(habitId = id, completed = true)

        useCase(habitId = id, completed = false)

        assertFalse(isCompleted(today))
    }

    @Test
    fun futureDatesAreIgnored() = runTest {
        val id = createHabit()
        val tomorrow = today.plusDays(1)

        val applied = useCase(habitId = id, completed = true, date = tomorrow)

        assertFalse(applied)
        assertFalse(isCompleted(tomorrow))
    }
}
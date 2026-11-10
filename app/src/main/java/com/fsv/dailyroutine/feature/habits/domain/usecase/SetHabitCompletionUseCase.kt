package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

class SetHabitCompletionUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val clock: Clock,
) {
    /** Returns false (and does nothing) when [date] is in the future. */
    suspend operator fun invoke(
        habitId: Long,
        completed: Boolean,
        date: LocalDate = LocalDate.now(clock),
    ): Boolean {
        if (date.isAfter(LocalDate.now(clock))) return false
        repository.setCompleted(habitId, date, completed)
        return true
    }
}
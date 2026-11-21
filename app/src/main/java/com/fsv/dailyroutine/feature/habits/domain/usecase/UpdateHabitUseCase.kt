package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import java.time.LocalTime
import javax.inject.Inject

sealed interface UpdateHabitResult {
    data object Success : UpdateHabitResult
    data class InvalidName(val error: HabitNameError) : UpdateHabitResult
    data object NotFound : UpdateHabitResult
}

class UpdateHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(
        habitId: Long,
        name: String,
        description: String?,
        reminderTime: LocalTime?,
    ): UpdateHabitResult {
        validateHabitName(name)?.let { return UpdateHabitResult.InvalidName(it) }

        val updated = repository.updateHabit(
            id = habitId,
            name = name.trim(),
            description = description.normalizedDescription(),
            reminderTime = reminderTime,
        )
        return if (updated) UpdateHabitResult.Success else UpdateHabitResult.NotFound
    }
}
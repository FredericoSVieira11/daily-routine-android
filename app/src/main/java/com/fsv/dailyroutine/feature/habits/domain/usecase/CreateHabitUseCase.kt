package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import java.time.Clock
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

enum class HabitNameError { Blank, TooLong }

sealed interface CreateHabitResult {
    data class Success(val habitId: Long) : CreateHabitResult
    data class InvalidName(val error: HabitNameError) : CreateHabitResult
}

class CreateHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
    private val clock: Clock,
) {
    suspend operator fun invoke(
        name: String,
        description: String?,
        reminderTime: LocalTime?,
    ): CreateHabitResult {
        validateHabitName(name)?.let { return CreateHabitResult.InvalidName(it) }

        val id = repository.createHabit(
            name = name.trim(),
            description = description.normalizedDescription(),
            createdAt = LocalDate.now(clock),
            reminderTime = reminderTime,
        )
        return CreateHabitResult.Success(id)
    }

    companion object {
        const val MAX_NAME_LENGTH = 50
    }
}
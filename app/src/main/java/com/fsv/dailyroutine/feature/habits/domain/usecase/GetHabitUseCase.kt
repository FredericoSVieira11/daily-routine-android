package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.model.Habit
import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import javax.inject.Inject

class GetHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(habitId: Long): Habit? = repository.getHabit(habitId)
}
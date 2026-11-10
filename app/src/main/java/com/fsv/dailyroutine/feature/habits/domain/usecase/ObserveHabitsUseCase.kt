package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.model.Habit
import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveHabitsUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    operator fun invoke(): Flow<List<Habit>> = repository.observeActiveHabits()
}
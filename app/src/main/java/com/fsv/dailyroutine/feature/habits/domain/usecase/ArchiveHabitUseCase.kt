package com.fsv.dailyroutine.feature.habits.domain.usecase

import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import javax.inject.Inject

class ArchiveHabitUseCase @Inject constructor(
    private val repository: HabitRepository,
) {
    suspend operator fun invoke(habitId: Long) = repository.archiveHabit(habitId)
}
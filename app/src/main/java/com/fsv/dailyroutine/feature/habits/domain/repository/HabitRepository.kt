package com.fsv.dailyroutine.feature.habits.domain.repository

import com.fsv.dailyroutine.feature.habits.domain.model.Habit
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime

interface HabitRepository {
    fun observeActiveHabits(): Flow<List<Habit>>

    suspend fun createHabit(
        name: String,
        description: String?,
        createdAt: LocalDate,
        reminderTime: LocalTime?,
    ): Long

    suspend fun archiveHabit(id: Long)

    suspend fun setCompleted(habitId: Long, date: LocalDate, completed: Boolean)
}
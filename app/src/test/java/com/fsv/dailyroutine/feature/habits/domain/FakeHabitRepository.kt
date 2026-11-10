package com.fsv.dailyroutine.feature.habits.domain

import com.fsv.dailyroutine.feature.habits.domain.model.Habit
import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import java.time.LocalTime

class FakeHabitRepository : HabitRepository {

    private val habits = MutableStateFlow<List<Habit>>(emptyList())
    private var nextId = 1L

    override fun observeActiveHabits(): Flow<List<Habit>> = habits

    override suspend fun createHabit(
        name: String,
        description: String?,
        createdAt: LocalDate,
        reminderTime: LocalTime?,
    ): Long {
        val id = nextId++
        habits.update { it + Habit(id, name, description, createdAt, reminderTime, emptySet()) }
        return id
    }

    override suspend fun archiveHabit(id: Long) {
        habits.update { list -> list.filterNot { it.id == id } }
    }

    override suspend fun setCompleted(habitId: Long, date: LocalDate, completed: Boolean) {
        habits.update { list ->
            list.map { habit ->
                if (habit.id != habitId) habit
                else habit.copy(
                    completedDates = if (completed) habit.completedDates + date
                    else habit.completedDates - date,
                )
            }
        }
    }
}
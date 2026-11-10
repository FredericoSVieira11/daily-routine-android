package com.fsv.dailyroutine.feature.habits.data.repository

import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitCompletionDao
import com.fsv.dailyroutine.feature.habits.data.local.dao.HabitDao
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitCompletionEntity
import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitEntity
import com.fsv.dailyroutine.feature.habits.data.mapper.toDomain
import com.fsv.dailyroutine.feature.habits.domain.model.Habit
import com.fsv.dailyroutine.feature.habits.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.time.LocalDate
import java.time.LocalTime
import javax.inject.Inject

class HabitRepositoryImpl @Inject constructor(
    private val habitDao: HabitDao,
    private val completionDao: HabitCompletionDao,
) : HabitRepository {

    override fun observeActiveHabits(): Flow<List<Habit>> =
        habitDao.observeActiveWithCompletions().map { list -> list.map { it.toDomain() } }

    override suspend fun createHabit(
        name: String,
        description: String?,
        createdAt: LocalDate,
        reminderTime: LocalTime?,
    ): Long = habitDao.insert(
        HabitEntity(
            name = name,
            description = description,
            createdAt = createdAt,
            reminderTime = reminderTime,
            archived = false,
        ),
    )

    override suspend fun archiveHabit(id: Long) = habitDao.archive(id)

    override suspend fun setCompleted(habitId: Long, date: LocalDate, completed: Boolean) {
        if (completed) {
            completionDao.insert(HabitCompletionEntity(habitId = habitId, date = date))
        } else {
            completionDao.delete(habitId, date)
        }
    }
}
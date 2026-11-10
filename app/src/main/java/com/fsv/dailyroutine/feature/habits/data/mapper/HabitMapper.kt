package com.fsv.dailyroutine.feature.habits.data.mapper

import com.fsv.dailyroutine.feature.habits.data.local.entity.HabitWithCompletions
import com.fsv.dailyroutine.feature.habits.domain.model.Habit

fun HabitWithCompletions.toDomain(): Habit = Habit(
    id = habit.id,
    name = habit.name,
    description = habit.description,
    createdAt = habit.createdAt,
    reminderTime = habit.reminderTime,
    completedDates = completions.map { it.date }.toSet(),
)
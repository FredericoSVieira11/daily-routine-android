package com.fsv.dailyroutine.feature.habits.data.local.entity

import androidx.room.Embedded
import androidx.room.Relation

data class HabitWithCompletions(
    @Embedded val habit: HabitEntity,
    @Relation(parentColumn = "id", entityColumn = "habit_id")
    val completions: List<HabitCompletionEntity>,
)
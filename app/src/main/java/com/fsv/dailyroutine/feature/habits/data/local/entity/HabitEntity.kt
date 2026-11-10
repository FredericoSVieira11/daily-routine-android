package com.fsv.dailyroutine.feature.habits.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.LocalTime

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?,
    @ColumnInfo(name = "created_at") val createdAt: LocalDate,
    @ColumnInfo(name = "reminder_time") val reminderTime: LocalTime?,
    val archived: Boolean,
)
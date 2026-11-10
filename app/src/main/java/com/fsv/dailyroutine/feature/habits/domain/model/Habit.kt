package com.fsv.dailyroutine.feature.habits.domain.model

import java.time.LocalDate
import java.time.LocalTime

data class Habit(
    val id: Long,
    val name: String,
    val description: String?,
    val createdAt: LocalDate,
    val reminderTime: LocalTime?,
    val completedDates: Set<LocalDate>,
) {
    fun isCompletedOn(date: LocalDate): Boolean = date in completedDates
}
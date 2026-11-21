package com.fsv.dailyroutine.feature.habits.presentation

import com.fsv.dailyroutine.feature.habits.domain.usecase.HabitNameError
import java.time.LocalTime

data class CreateHabitUiState(
    val name: String = "",
    val description: String = "",
    val reminderTime: LocalTime? = null,
    val nameError: HabitNameError? = null,
    val isSaving: Boolean = false,
)

sealed interface CreateHabitEvent {
    data object Saved : CreateHabitEvent
}
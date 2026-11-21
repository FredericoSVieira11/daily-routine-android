package com.fsv.dailyroutine.feature.habits.presentation

import com.fsv.dailyroutine.feature.habits.domain.usecase.HabitNameError
import java.time.LocalTime

data class HabitFormUiState(
    val name: String = "",
    val description: String = "",
    val reminderTime: LocalTime? = null,
    val nameError: HabitNameError? = null,
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
)

sealed interface HabitFormEvent {
    data object Saved : HabitFormEvent
    data object HabitNotFound : HabitFormEvent
}
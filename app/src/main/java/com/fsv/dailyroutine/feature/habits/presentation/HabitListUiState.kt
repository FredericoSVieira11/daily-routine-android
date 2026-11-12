package com.fsv.dailyroutine.feature.habits.presentation

sealed interface HabitListUiState {
    data object Loading : HabitListUiState
    data object Empty : HabitListUiState
    data class Content(
        val habits: List<HabitItemUi>,
        val completedToday: Int,
    ) : HabitListUiState
}

data class HabitItemUi(
    val id: Long,
    val name: String,
    val description: String?,
    val completedToday: Boolean,
)
package com.fsv.dailyroutine.feature.habits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsv.dailyroutine.feature.habits.domain.model.Habit
import com.fsv.dailyroutine.feature.habits.domain.usecase.ObserveHabitsUseCase
import com.fsv.dailyroutine.feature.habits.domain.usecase.SetHabitCompletionUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Clock
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class HabitListViewModel @Inject constructor(
    observeHabits: ObserveHabitsUseCase,
    private val setHabitCompletion: SetHabitCompletionUseCase,
    private val clock: Clock,
) : ViewModel() {

    val uiState: StateFlow<HabitListUiState> = observeHabits()
        .map { habits -> habits.toUiState(today = LocalDate.now(clock)) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS),
            initialValue = HabitListUiState.Loading,
        )

    fun onToggle(habitId: Long, completed: Boolean) {
        viewModelScope.launch { setHabitCompletion(habitId, completed) }
    }

    private companion object {
        const val STOP_TIMEOUT_MILLIS = 5_000L
    }
}

private fun List<Habit>.toUiState(today: LocalDate): HabitListUiState {
    if (isEmpty()) return HabitListUiState.Empty
    val items = map { habit ->
        HabitItemUi(
            id = habit.id,
            name = habit.name,
            description = habit.description,
            completedToday = habit.isCompletedOn(today),
        )
    }
    return HabitListUiState.Content(items, completedToday = items.count { it.completedToday })
}
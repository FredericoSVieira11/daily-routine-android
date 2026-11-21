package com.fsv.dailyroutine.feature.habits.presentation

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsv.dailyroutine.feature.habits.domain.usecase.CreateHabitResult
import com.fsv.dailyroutine.feature.habits.domain.usecase.CreateHabitUseCase
import com.fsv.dailyroutine.feature.habits.domain.usecase.GetHabitUseCase
import com.fsv.dailyroutine.feature.habits.domain.usecase.HabitNameError
import com.fsv.dailyroutine.feature.habits.domain.usecase.UpdateHabitResult
import com.fsv.dailyroutine.feature.habits.domain.usecase.UpdateHabitUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalTime
import javax.inject.Inject

@HiltViewModel
class HabitFormViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getHabit: GetHabitUseCase,
    private val createHabit: CreateHabitUseCase,
    private val updateHabit: UpdateHabitUseCase,
) : ViewModel() {

    private val habitId: Long? = savedStateHandle.get<Long>(HABIT_ID_KEY)

    private val _uiState = MutableStateFlow(
        HabitFormUiState(isEditing = habitId != null, isLoading = habitId != null),
    )
    val uiState: StateFlow<HabitFormUiState> = _uiState.asStateFlow()

    private val _events = Channel<HabitFormEvent>(Channel.BUFFERED)
    val events: Flow<HabitFormEvent> = _events.receiveAsFlow()

    init {
        if (habitId != null) loadHabit(habitId)
    }

    fun onNameChange(value: String) {
        _uiState.update { it.copy(name = value, nameError = null) }
    }

    fun onDescriptionChange(value: String) {
        _uiState.update { it.copy(description = value) }
    }

    fun onReminderChange(time: LocalTime?) {
        _uiState.update { it.copy(reminderTime = time) }
    }

    fun onSaveClick() {
        val current = _uiState.value
        if (current.isLoading || current.isSaving) return
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            if (habitId == null) {
                when (val result = createHabit(current.name, current.description, current.reminderTime)) {
                    is CreateHabitResult.Success -> _events.send(HabitFormEvent.Saved)
                    is CreateHabitResult.InvalidName -> showNameError(result.error)
                }
            } else {
                when (val result = updateHabit(habitId, current.name, current.description, current.reminderTime)) {
                    UpdateHabitResult.Success -> _events.send(HabitFormEvent.Saved)
                    is UpdateHabitResult.InvalidName -> showNameError(result.error)
                    UpdateHabitResult.NotFound -> _events.send(HabitFormEvent.HabitNotFound)
                }
            }
        }
    }

    private fun loadHabit(id: Long) {
        viewModelScope.launch {
            val habit = getHabit(id)
            if (habit == null) {
                _events.send(HabitFormEvent.HabitNotFound)
            } else {
                _uiState.update {
                    it.copy(
                        name = habit.name,
                        description = habit.description.orEmpty(),
                        reminderTime = habit.reminderTime,
                        isLoading = false,
                    )
                }
            }
        }
    }

    private fun showNameError(error: HabitNameError) {
        _uiState.update { it.copy(isSaving = false, nameError = error) }
    }

    private companion object {
        const val HABIT_ID_KEY = "habitId"
    }
}
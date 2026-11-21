package com.fsv.dailyroutine.feature.habits.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fsv.dailyroutine.feature.habits.domain.usecase.CreateHabitResult
import com.fsv.dailyroutine.feature.habits.domain.usecase.CreateHabitUseCase
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
class CreateHabitViewModel @Inject constructor(
    private val createHabit: CreateHabitUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateHabitUiState())
    val uiState: StateFlow<CreateHabitUiState> = _uiState.asStateFlow()

    private val _events = Channel<CreateHabitEvent>(Channel.BUFFERED)
    val events: Flow<CreateHabitEvent> = _events.receiveAsFlow()

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
        if (current.isSaving) return
        _uiState.update { it.copy(isSaving = true) }

        viewModelScope.launch {
            when (val result = createHabit(current.name, current.description, current.reminderTime)) {
                is CreateHabitResult.Success -> _events.send(CreateHabitEvent.Saved)
                is CreateHabitResult.InvalidName ->
                    _uiState.update { it.copy(isSaving = false, nameError = result.error) }
            }
        }
    }
}
package com.fsv.dailyroutine.feature.habits.domain.usecase

internal fun validateHabitName(name: String): HabitNameError? {
    val trimmed = name.trim()
    return when {
        trimmed.isEmpty() -> HabitNameError.Blank
        trimmed.length > CreateHabitUseCase.MAX_NAME_LENGTH -> HabitNameError.TooLong
        else -> null
    }
}

internal fun String?.normalizedDescription(): String? = this?.trim()?.takeIf { it.isNotEmpty() }
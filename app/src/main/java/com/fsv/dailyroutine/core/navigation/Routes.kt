package com.fsv.dailyroutine.core.navigation

import kotlinx.serialization.Serializable

@Serializable
data object HabitList

@Serializable
data class HabitForm(val habitId: Long? = null)
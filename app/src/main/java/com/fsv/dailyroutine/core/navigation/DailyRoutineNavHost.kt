package com.fsv.dailyroutine.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fsv.dailyroutine.feature.habits.presentation.HabitFormRoute
import com.fsv.dailyroutine.feature.habits.presentation.HabitListRoute

@Composable
fun DailyRoutineNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    NavHost(
        navController = navController,
        startDestination = HabitList,
        modifier = modifier,
    ) {
        composable<HabitList> {
            HabitListRoute(
                onAddClick = { navController.navigate(HabitForm()) },
                onHabitClick = { habitId -> navController.navigate(HabitForm(habitId = habitId)) },
            )
        }
        composable<HabitForm> {
            HabitFormRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
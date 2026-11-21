package com.fsv.dailyroutine.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fsv.dailyroutine.feature.habits.presentation.CreateHabitRoute
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
            HabitListRoute(onAddClick = { navController.navigate(CreateHabit) })
        }
        composable<CreateHabit> {
            CreateHabitRoute(onNavigateBack = { navController.popBackStack() })
        }
    }
}
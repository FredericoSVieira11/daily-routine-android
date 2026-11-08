package com.fsv.dailyroutine.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fsv.dailyroutine.feature.habits.presentation.CreateHabitScreen
import com.fsv.dailyroutine.feature.habits.presentation.HabitListScreen

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
            HabitListScreen(onAddClick = { navController.navigate(CreateHabit) })
        }
        composable<CreateHabit> {
            CreateHabitScreen(onBack = { navController.popBackStack() })
        }
    }
}
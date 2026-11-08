package com.fsv.dailyroutine

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.fsv.dailyroutine.core.designsystem.theme.DailyRoutineTheme
import com.fsv.dailyroutine.core.navigation.DailyRoutineNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            DailyRoutineTheme {
                DailyRoutineNavHost()
            }
        }
    }
}
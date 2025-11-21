package com.chpark.kronos.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.chpark.kronos.data.entity.ExecutionHistoryEntity
import com.chpark.kronos.screen.AlarmListScreen
import com.chpark.kronos.screen.ExecutionHistoryDetailScreen
import com.chpark.kronos.screen.ExecutionHistoryScreen
import com.chpark.kronos.screen.SettingsScreen
import com.chpark.kronos.ui.AppDestinations
import com.chpark.kronos.ui.edit.AlarmEditScreen
import com.chpark.kronos.ui.viewmodel.ExecutionHistoryViewModel

@Composable
fun KronosNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {

    NavHost(
        navController = navController,
        startDestination = AppDestinations.HOME.route,
        modifier = modifier
    ) {

        // HOME
        composable(AppDestinations.HOME.route) {
            AlarmListScreen(navController)
        }

        // HISTORY 목록
        composable(AppDestinations.HISTORY.route) {
            ExecutionHistoryScreen(
                onItemClick = { entity ->
                    navController.navigate("history_detail/${entity.id}")
                }
            )
        }
        // Alarm Edit
        composable(
            route = "alarm_edit/{id}",
            arguments = listOf(
                navArgument("id") { type = NavType.LongType }
            )
        ) {
            val id = it.arguments?.getLong("id") ?: 0L
            AlarmEditScreen(
                alarmId = id,
                onBack = { navController.popBackStack() }
            )
        }


        // HISTORY 상세 화면
        composable(
            route = "history_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType })
        ) { backStack ->
            val historyViewModel: ExecutionHistoryViewModel = hiltViewModel()
            val id = backStack.arguments?.getLong("id")!!
            val entity by produceState<ExecutionHistoryEntity?>(initialValue = null, id) {
                value = historyViewModel.getById(id)
            }
            if (entity != null) {
                ExecutionHistoryDetailScreen(
                    entity = entity!!,
                    onBack = { navController.popBackStack() }
                )
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // SETTINGS
        composable(AppDestinations.SETTINGS.route) {
            SettingsScreen()
        }
    }
}
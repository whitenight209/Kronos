package com.chpark.kronos.ui.components

import android.app.Activity
import androidx.activity.ComponentActivity
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
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
import com.chpark.kronos.screen.AlarmEditScreen
import com.chpark.kronos.screen.CodeEditorScreen
import com.chpark.kronos.ui.viewmodel.ExecutionHistoryViewModel
import com.chpark.kronos.viewmodel.CodeEditorViewModel

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun KronosNavHost(
    navController: NavHostController,
    activity: ComponentActivity,
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

        // HISTORY 목록 (Left → Right, Activity 스타일)
        composable(
            AppDestinations.HISTORY.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            }
        ) {
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
            ),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            }
        ) { backStack ->
            val id = backStack.arguments?.getLong("id") ?: 0L
            val editorVm: CodeEditorViewModel = hiltViewModel(activity)
            AlarmEditScreen(
                alarmId = id,
                navController = navController,
                editorVm = editorVm,
                onBack = { navController.popBackStack() }
            )
        }

        // HISTORY 상세
        composable(
            route = "history_detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.LongType }),
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            }
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
                    Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }

        // SETTINGS
        composable(
            AppDestinations.SETTINGS.route,
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            }
        ) {
            SettingsScreen()
        }
        composable(
            "code_editor",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(250)
                )
            },
            popEnterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            },
            popExitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(250)
                )
            }
        ) {
            val editorVm: CodeEditorViewModel = hiltViewModel(activity)
            CodeEditorScreen(
                navController = navController,
                editorVm = editorVm,
            )
        }
    }
}
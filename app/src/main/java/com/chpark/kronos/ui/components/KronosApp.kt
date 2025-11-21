package com.chpark.kronos.ui.components


import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.chpark.kronos.ui.AppDestinations

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KronosApp() {
    val navController = rememberNavController()

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { destination ->
                item(
                    icon = { Icon(destination.icon, contentDescription = destination.label) },
                    label = { Text(destination.label) },
                    selected = destination == currentDestination,
                    onClick = {
                        val navBackStackEntry = navController.currentBackStackEntry
                        val currentRoute = navBackStackEntry?.destination?.route

                        if (currentRoute != destination.route) {
                            currentDestination = destination
                            navController.navigate(destination.route) {
                                popUpTo(AppDestinations.HOME.route)
                                launchSingleTop = true
                            }
                        }
                    }
                )
            }
        }
    ) {

        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Kronos") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    )
                )
            },
            floatingActionButton = {
                if (currentRoute == AppDestinations.HOME.route) {
                    FloatingActionButton(
                        onClick = { navController.navigate("alarm_edit/0") }
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Alarm")
                    }
                }
            }
        ) { innerPadding ->
            KronosNavHost(
                navController = navController,
                modifier = Modifier.padding(innerPadding)
            )
        }
    }
}
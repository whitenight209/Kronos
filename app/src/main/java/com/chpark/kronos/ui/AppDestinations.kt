package com.chpark.kronos.ui


import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val route: String
) {
    HISTORY("History", Icons.Default.DocumentScanner, "history"),
    HOME("Home", Icons.Default.Home, "home"),
    SETTINGS("Settings", Icons.Default.Settings, "settings")
}
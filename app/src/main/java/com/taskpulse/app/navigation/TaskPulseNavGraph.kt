package com.taskpulse.app.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.taskpulse.app.ui.screens.AnalyticsScreen
import com.taskpulse.app.ui.screens.AuthScreen
import com.taskpulse.app.ui.screens.BatteryScreen
import com.taskpulse.app.ui.screens.DashboardScreen
import com.taskpulse.app.ui.screens.FocusScreen
import com.taskpulse.app.ui.screens.OnboardingScreen
import com.taskpulse.app.ui.screens.ProfileScreen
import com.taskpulse.app.ui.screens.ReportsScreen
import com.taskpulse.app.ui.screens.RamScreen
import com.taskpulse.app.ui.screens.SettingsScreen
import com.taskpulse.app.ui.screens.SplashScreen
import com.taskpulse.app.ui.screens.UsageScreen

sealed class TaskPulseScreen(val route: String, val title: String, val icon: ImageVector) {
    object Splash : TaskPulseScreen("splash", "Splash", Icons.Default.Home)
    object Onboarding : TaskPulseScreen("onboarding", "Onboarding", Icons.Default.Home)
    object Auth : TaskPulseScreen("auth", "Login", Icons.Default.Home)
    object Dashboard : TaskPulseScreen("dashboard", "Dashboard", Icons.Default.Home)
    object Usage : TaskPulseScreen("usage", "Usage", Icons.Default.BarChart)
    object Analytics : TaskPulseScreen("analytics", "Analytics", Icons.Default.BarChart)
    object Battery : TaskPulseScreen("battery", "Battery", Icons.Default.Menu)
    object Ram : TaskPulseScreen("ram", "Memory", Icons.Default.Menu)
    object Focus : TaskPulseScreen("focus", "Focus", Icons.Default.Timer)
    object Reports : TaskPulseScreen("reports", "Reports", Icons.Default.Menu)
    object Profile : TaskPulseScreen("profile", "Profile", Icons.Default.Menu)
    object Settings : TaskPulseScreen("settings", "Settings", Icons.Default.Settings)
}

private val bottomScreens = listOf(
    TaskPulseScreen.Dashboard,
    TaskPulseScreen.Usage,
    TaskPulseScreen.Analytics,
    TaskPulseScreen.Focus,
    TaskPulseScreen.Profile
)

@Composable
fun TaskPulseNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route
    val showBottomBar = currentDestination != null && bottomScreens.any { it.route == currentDestination }

    Scaffold(
        bottomBar = {
            if (showBottomBar) TaskPulseBottomBar(navController)
        }
    ) { innerPadding ->
        Surface(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = TaskPulseScreen.Splash.route
            ) {
            composable(TaskPulseScreen.Splash.route) {
                SplashScreen(onContinue = { navController.navigate(TaskPulseScreen.Onboarding.route) {
                    popUpTo(TaskPulseScreen.Splash.route) { inclusive = true }
                } })
            }
            composable(TaskPulseScreen.Onboarding.route) {
                OnboardingScreen(onContinue = { navController.navigate(TaskPulseScreen.Auth.route) {
                    popUpTo(TaskPulseScreen.Onboarding.route) { inclusive = true }
                } })
            }
            composable(TaskPulseScreen.Auth.route) {
                AuthScreen(onLoginSuccess = {
                    navController.navigate(TaskPulseScreen.Dashboard.route) {
                        popUpTo(TaskPulseScreen.Auth.route) { inclusive = true }
                    }
                })
            }
            composable(TaskPulseScreen.Dashboard.route) {
                DashboardScreen(
                    viewModel = hiltViewModel(),
                    onNavigate = { route -> navController.navigate(route) }
                )
            }
            composable(TaskPulseScreen.Usage.route) {
                UsageScreen(viewModel = hiltViewModel(), onNavigate = { route -> navController.navigate(route) })
            }
            composable(TaskPulseScreen.Analytics.route) {
                AnalyticsScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(TaskPulseScreen.Battery.route) {
                BatteryScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(TaskPulseScreen.Ram.route) {
                RamScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(TaskPulseScreen.Focus.route) {
                FocusScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(TaskPulseScreen.Reports.route) {
                ReportsScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(TaskPulseScreen.Profile.route) {
                ProfileScreen(onNavigate = { route -> navController.navigate(route) })
            }
            composable(TaskPulseScreen.Settings.route) {
                SettingsScreen(viewModel = hiltViewModel())
            }
        }
    }
}

@Composable
fun TaskPulseBottomBar(navController: NavHostController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    NavigationBar {
        bottomScreens.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = screen.title) },
                label = { Text(screen.title) },
                selected = currentDestination == screen.route,
                onClick = {
                    if (currentDestination != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                }
            )
        }
    }
}

package com.serova.parkingapp.presentation.navigation

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.serova.parkingapp.R
import com.serova.parkingapp.presentation.screen.AuthScreen
import com.serova.parkingapp.presentation.screen.MainTabsScreen

sealed class Screen(
    val route: String,
    @StringRes val titleRes: Int,
    val icon: ImageVector?,
    val tabIndex: Int = 0
) {
    object Auth : Screen(
        route = "auth",
        titleRes = R.string.space,
        icon = null
    )

    object Main : Screen(
        route = "main",
        titleRes = R.string.space,
        icon = null
    )

    object Map : Screen(
        route = "map",
        titleRes = R.string.map_screen_title,
        icon = Icons.Default.Place,
        tabIndex = 1
    )

    object BookingRequest : Screen(
        route = "booking_request",
        titleRes = R.string.request_screen_title,
        icon = Icons.Default.Add,
        tabIndex = 2
    )

    object MyBookings : Screen(
        route = "my_bookings",
        titleRes = R.string.my_bookings_screen_title,
        icon = Icons.Default.DateRange,
        tabIndex = 3
    )

    object Settings : Screen(
        route = "settings",
        titleRes = R.string.settings_screen_title,
        icon = Icons.Default.Settings,
        tabIndex = 4
    )

    companion object {
        fun getBottomNavItems(): List<Screen> {
            return listOf(Map, BookingRequest, MyBookings, Settings)
        }

        fun getBottomNavRoutes(): List<String> {
            return getBottomNavItems().map { it.route }
        }
    }
}

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Auth.route
    ) {
        composable(Screen.Auth.route) {
            AuthScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Auth.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Main.route) {
            MainTabsScreen(globalNavController = navController)
        }
    }
}
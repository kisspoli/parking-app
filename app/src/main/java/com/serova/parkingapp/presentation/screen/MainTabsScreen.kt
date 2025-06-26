package com.serova.parkingapp.presentation.screen

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.serova.parkingapp.presentation.navigation.Screen
import com.serova.parkingapp.presentation.navigation.Screen.Companion.getBottomNavItems
import com.serova.parkingapp.presentation.navigation.Screen.Companion.getBottomNavRoutes
import com.serova.parkingapp.presentation.viewmodel.BookingRequestViewModel
import com.serova.parkingapp.presentation.viewmodel.MapViewModel
import com.serova.parkingapp.presentation.viewmodel.MyBookingsViewModel
import com.serova.parkingapp.presentation.viewmodel.SettingsViewModel

@Composable
fun MainTabsScreen(
    globalNavController: NavController
) {
    val navController = rememberNavController()
    val tabs = getBottomNavItems()
    val currentRoute = navController
        .currentBackStackEntryAsState()
        .value
        ?.destination
        ?.route
        ?: Screen.Map.route

    Scaffold(
        bottomBar = {
            if (shouldShowBottomBar(navController)) {
                NavigationBar {
                    tabs.forEach { screen ->
                        val isSelected = currentRoute == screen.route
                        val contentColor by animateColorAsState(
                            targetValue = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            animationSpec = tween(durationMillis = 200)
                        )

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    screen.icon ?: Icons.Default.Info,
                                    contentDescription = null,
                                    tint = contentColor
                                )
                            },
                            label = { Text(stringResource(screen.titleRes), color = contentColor) },
                            selected = currentRoute == screen.route,
                            onClick = {
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.surfaceContainerHighest
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.Map.route
            ) {
                composable(
                    route = Screen.Map.route,
                    enterTransition = { enterSlideAnimation() },
                    exitTransition = { exitSlideAnimation() }
                ) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Map.route)
                    }
                    val mapViewModel = hiltViewModel<MapViewModel>(parentEntry)
                    MapScreen(
                        globalNavController = globalNavController,
                        viewModel = mapViewModel
                    )
                }
                composable(
                    route = Screen.BookingRequest.route,
                    enterTransition = { enterSlideAnimation() },
                    exitTransition = { exitSlideAnimation() }
                ) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.BookingRequest.route)
                    }
                    val bookingRequestViewModel =
                        hiltViewModel<BookingRequestViewModel>(parentEntry)
                    BookingRequestScreen(
                        globalNavController = globalNavController,
                        viewModel = bookingRequestViewModel
                    )
                }
                composable(
                    route = Screen.MyBookings.route,
                    enterTransition = { enterSlideAnimation() },
                    exitTransition = { exitSlideAnimation() }
                ) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.MyBookings.route)
                    }
                    val myBookingsViewModel = hiltViewModel<MyBookingsViewModel>(parentEntry)
                    MyBookingsScreen(
                        globalNavController = globalNavController,
                        viewModel = myBookingsViewModel
                    )
                }
                composable(
                    route = Screen.Settings.route,
                    enterTransition = { enterSlideAnimation() },
                    exitTransition = { exitSlideAnimation() }
                ) { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry(Screen.Settings.route)
                    }
                    val settingsViewModel = hiltViewModel<SettingsViewModel>(parentEntry)
                    SettingsScreen(
                        globalNavController = globalNavController,
                        viewModel = settingsViewModel
                    )
                }
            }
        }
    }
}

private fun shouldShowBottomBar(navController: NavController): Boolean {
    val currentRoute = navController.currentDestination?.route
    return currentRoute in getBottomNavRoutes()
}

private fun routeToTabIndex(route: String): Int {
    return when (route) {
        Screen.Map.route -> Screen.Map.tabIndex
        Screen.BookingRequest.route -> Screen.BookingRequest.tabIndex
        Screen.MyBookings.route -> Screen.MyBookings.tabIndex
        Screen.Settings.route -> Screen.Settings.tabIndex
        else -> 0
    }
}

private fun NavBackStackEntry?.tabIndex(): Int {
    return this?.destination?.route?.let { routeToTabIndex(it) } ?: -1
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.enterSlideAnimation(): EnterTransition? {
    val initialIndex = initialState.tabIndex()
    val targetIndex = targetState.tabIndex()

    if (initialIndex < 0 || targetIndex < 0) return null

    return if (targetIndex > initialIndex) {
        slideInHorizontally(
            initialOffsetX = { it },
            animationSpec = tween(300)
        )
    } else {
        slideInHorizontally(
            initialOffsetX = { -it },
            animationSpec = tween(300)
        )
    }
}

private fun AnimatedContentTransitionScope<NavBackStackEntry>.exitSlideAnimation(): ExitTransition? {
    val initialIndex = initialState.tabIndex()
    val targetIndex = targetState.tabIndex()

    if (initialIndex < 0 || targetIndex < 0) return null

    return if (targetIndex > initialIndex) {
        slideOutHorizontally(
            targetOffsetX = { -it },
            animationSpec = tween(300)
        )
    } else {
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(300)
        )
    }
}
//// file: com/dholsagar/app/presentation/main/ProviderMainScreen.kt
//package com.dholsagar.app.presentation.main
//
//import androidx.compose.foundation.layout.padding
//import androidx.compose.material.icons.Icons
//import androidx.compose.material.icons.filled.*
//import androidx.compose.material.icons.outlined.*
//import androidx.compose.material3.Icon
//import androidx.compose.material3.NavigationBar
//import androidx.compose.material3.NavigationBarItem
//import androidx.compose.material3.Scaffold
//import androidx.compose.material3.Text
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.navigation.NavDestination.Companion.hierarchy
//import androidx.navigation.NavGraph.Companion.findStartDestination
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.currentBackStackEntryAsState
//import androidx.navigation.compose.rememberNavController
//import com.dholsagar.app.core.navigation.Route
//import com.dholsagar.app.presentation.home_provider.ProviderBookingsScreen
//import com.dholsagar.app.presentation.home_provider.ProviderDashboardScreen
//import com.dholsagar.app.presentation.home_provider.ProviderProfileScreen
//
//@Composable
//fun ProviderMainScreen() {
//    val items = listOf(
//        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, Route.PROVIDER_DASHBOARD),
//        BottomNavItem("Bookings", Icons.Filled.List, Icons.Outlined.List, Route.PROVIDER_BOOKINGS),
//        BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, Route.PROVIDER_PROFILE),
//    )
//
//    val navController = rememberNavController()
//
//    Scaffold(
//        bottomBar = {
//            NavigationBar {
//                val navBackStackEntry by navController.currentBackStackEntryAsState()
//                val currentDestination = navBackStackEntry?.destination
//
//                items.forEach { screen ->
//                    NavigationBarItem(
//                        icon = {
//                            Icon(
//                                if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
//                                    screen.selectedIcon
//                                } else {
//                                    screen.unselectedIcon
//                                },
//                                contentDescription = screen.title
//                            )
//                        },
//                        label = { Text(screen.title) },
//                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
//                        onClick = {
//                            navController.navigate(screen.route) {
//                                popUpTo(navController.graph.findStartDestination().id) {
//                                    saveState = true
//                                }
//                                launchSingleTop = true
//                                restoreState = true
//                            }
//                        }
//                    )
//                }
//            }
//        }
//    ) { innerPadding ->
//        NavHost(
//            navController = navController,
//            startDestination = Route.PROVIDER_DASHBOARD,
//            modifier = Modifier.padding(innerPadding)
//        ) {
//            composable(Route.PROVIDER_DASHBOARD) { ProviderDashboardScreen() }
//            composable(Route.PROVIDER_BOOKINGS) { ProviderBookingsScreen() }
//            composable(Route.PROVIDER_PROFILE) { ProviderProfileScreen() }
//        }
//    }
//}

/// file: com/dholsagar/app/presentation/main/ProviderMainScreen.kt
package com.dholsagar.app.presentation.main

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.dholsagar.app.core.navigation.Route
import com.dholsagar.app.presentation.home_provider.ProviderBookingsScreen
import com.dholsagar.app.presentation.home_provider.ProviderDashboardScreen
import com.dholsagar.app.presentation.home_provider.ProviderProfileScreen

@Composable
fun ProviderMainScreen(
    rootNavController: NavController
) {
    val items = listOf(
        BottomNavItem("Home", Icons.Filled.Home, Icons.Outlined.Home, Route.PROVIDER_DASHBOARD),
        BottomNavItem("Bookings", Icons.Filled.List, Icons.Outlined.List, Route.PROVIDER_BOOKINGS),
        BottomNavItem("Profile", Icons.Filled.Person, Icons.Outlined.Person, Route.PROVIDER_PROFILE),
    )

    // This controller manages ONLY the bottom tabs
    val tabNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val navBackStackEntry by tabNavController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                items.forEach { screen ->
                    NavigationBarItem(
                        icon = {
                            Icon(
                                if (currentDestination?.hierarchy?.any { it.route == screen.route } == true) {
                                    screen.selectedIcon
                                } else {
                                    screen.unselectedIcon
                                },
                                contentDescription = screen.title
                            )
                        },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        onClick = {
                            tabNavController.navigate(screen.route) {
                                popUpTo(tabNavController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = tabNavController,
            startDestination = Route.PROVIDER_DASHBOARD,
            modifier = Modifier.padding(innerPadding)
        ) {
            // --- THIS WAS THE MISSING PARAMETER ---
            composable(Route.PROVIDER_DASHBOARD) {
                ProviderDashboardScreen(navController = rootNavController)
            }

            composable(Route.PROVIDER_BOOKINGS) {
                ProviderBookingsScreen()
            }

            composable(Route.PROVIDER_PROFILE) {
                ProviderProfileScreen(rootNavController = rootNavController)
            }
        }
    }
}
//// file: com/dholsagar/app/core/navigation/AppNavigation.kt
//package com.dholsagar.app.core.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import com.dholsagar.app.presentation.auth.AuthScreen
//import com.dholsagar.app.presentation.auth.OtpScreen
//import com.dholsagar.app.presentation.auth.PhoneAuthScreen
//import com.dholsagar.app.presentation.auth.UserTypeSelectionScreen
//import com.dholsagar.app.presentation.home_provider.ProviderHomeScreen
//import com.dholsagar.app.presentation.home_user.UserHomeScreen
//import com.dholsagar.app.presentation.splash.SplashScreen
//
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = Screen.SplashScreen.route
//    ) {
//        composable(route = Screen.SplashScreen.route) {
//            SplashScreen(navController = navController)
//        }
//        composable(route = Screen.UserTypeSelectionScreen.route) {
//            UserTypeSelectionScreen(navController = navController)
//        }
//        composable(
//            route = Screen.AuthScreen.route,
//            arguments = listOf(navArgument("userType") { type = NavType.StringType })
//        ) {
//            AuthScreen(navController = navController)
//        }
//        composable(route = Screen.PhoneAuthScreen.route) {
//            // FIX: Pass the navController
//            PhoneAuthScreen(navController = navController)
//        }
//        composable(route = Screen.OtpScreen.route) {
//            // FIX: Pass the navController
//            OtpScreen(navController = navController)
//        }
//        composable(route = Screen.UserHomeScreen.route) {
//            UserHomeScreen()
//        }
//        composable(route = Screen.ProviderHomeScreen.route) {
//            ProviderHomeScreen()
//        }
//    }
//}

// file: com/dholsagar/app/core/navigation/AppNavigation.kt
package com.dholsagar.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.dholsagar.app.presentation.auth.*
import com.dholsagar.app.presentation.home_provider.ProviderHomeScreen
import com.dholsagar.app.presentation.home_user.UserHomeScreen
import com.dholsagar.app.presentation.splash.SplashScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.SplashScreen.route
    ) {
        composable(route = Screen.SplashScreen.route) {
            SplashScreen(navController = navController)
        }
        composable(route = Screen.UserTypeSelectionScreen.route) {
            UserTypeSelectionScreen(navController = navController)
        }

        // ===================================================================
        // == NESTED AUTHENTICATION GRAPH
        // ===================================================================
        // All screens inside this block (`AuthScreen`, `PhoneAuthScreen`, `OtpScreen`)
        // will share the exact same instance of AuthViewModel.
        navigation(
            // The graph starts at AuthScreen and needs the userType argument
            startDestination = Screen.AuthScreen.route,
            route = Route.AUTH_GRAPH,
            arguments = listOf(navArgument("userType") { type = NavType.StringType })
        ) {
            composable(route = Screen.AuthScreen.route) { backStackEntry ->
                // 1. Get the backStackEntry of the parent navigation graph
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.AUTH_GRAPH)
                }
                // 2. Use the parent's entry to create the shared ViewModel
                val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
                // 3. Pass the shared ViewModel to the screen
                AuthScreen(navController = navController, viewModel = authViewModel)
            }

            composable(route = Screen.PhoneAuthScreen.route) { backStackEntry ->
                // Do the same for PhoneAuthScreen to get the *same* ViewModel instance
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.AUTH_GRAPH)
                }
                val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
                PhoneAuthScreen(navController = navController, viewModel = authViewModel)
            }

            composable(route = Screen.OtpScreen.route) { backStackEntry ->
                // And again for OtpScreen
                val parentEntry = remember(backStackEntry) {
                    navController.getBackStackEntry(Route.AUTH_GRAPH)
                }
                val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
                OtpScreen(navController = navController, viewModel = authViewModel)
            }
        }

        // --- Other Screens ---
        composable(route = Screen.UserHomeScreen.route) {
            UserHomeScreen()
        }
        composable(route = Screen.ProviderHomeScreen.route) {
            ProviderHomeScreen()
        }
    }
}
//// file: com/dholsagar/app/core/navigation/AppNavigation.kt
//package com.dholsagar.app.core.navigation
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.remember
//import androidx.hilt.navigation.compose.hiltViewModel
//import androidx.navigation.NavGraphBuilder
//import androidx.navigation.NavHostController
//import androidx.navigation.NavType
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import androidx.navigation.navArgument
//import androidx.navigation.navigation
//import com.dholsagar.app.presentation.auth.*
//import com.dholsagar.app.presentation.home_provider.ProviderHomeScreen
//import com.dholsagar.app.presentation.home_user.UserHomeScreen
//import com.dholsagar.app.presentation.onboarding_provider.ProviderOnboardingScreen
//import com.dholsagar.app.presentation.onboarding_user.UserOnboardingScreen
//import com.dholsagar.app.presentation.splash.SplashScreen
//
//@Composable
//fun AppNavigation() {
//    val navController = rememberNavController()
//
//    NavHost(
//        navController = navController,
//        startDestination = Route.SPLASH
//    ) {
//        composable(route = Route.SPLASH) {
//            SplashScreen(navController = navController)
//        }
//
//        // The entire authentication flow is now a self-contained, nested graph.
//        authGraph(navController)
//
//        // --- Screens outside the authentication flow ---
//        composable(route = Route.USER_HOME) {
//            UserHomeScreen()
//        }
//        composable(route = Route.PROVIDER_HOME) {
//            ProviderHomeScreen()
//        }
//
//    }
//}
//
//// This extension function defines the authentication sub-graph.
//// All screens inside this block share the same instance of AuthViewModel.
//fun NavGraphBuilder.authGraph(navController: NavHostController) {
//    navigation(
//        startDestination = Route.USER_TYPE_SELECTION,
//        route = Route.AUTH_GRAPH
//    ) {
//        composable(route = Route.USER_TYPE_SELECTION) {
//            UserTypeSelectionScreen(navController = navController)
//        }
//
//        composable(
//            route = Screen.AuthScreen.route,
//            arguments = listOf(navArgument("userType") { type = NavType.StringType })
//        ) { backStackEntry ->
//            // Get the backStackEntry of the parent graph (auth_graph)
//            val parentEntry = remember(backStackEntry) {
//                navController.getBackStackEntry(Route.AUTH_GRAPH)
//            }
//            // Create the shared ViewModel using the parent's entry
//            val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
//            AuthScreen(navController = navController, viewModel = authViewModel)
//        }
//
//        composable(route = Route.PHONE_AUTH) { backStackEntry ->
//            val parentEntry = remember(backStackEntry) {
//                navController.getBackStackEntry(Route.AUTH_GRAPH)
//            }
//            val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
//            PhoneAuthScreen(navController = navController, viewModel = authViewModel)
//        }
//
//        composable(route = Route.OTP) { backStackEntry ->
//            val parentEntry = remember(backStackEntry) {
//                navController.getBackStackEntry(Route.AUTH_GRAPH)
//            }
//            val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
//            OtpScreen(navController = navController, viewModel = authViewModel)
//        }
//
//        composable(route = Route.USER_ONBOARDING) {
//            UserOnboardingScreen(navController = navController)
//        }
//        composable(route = Route.PROVIDER_ONBOARDING) {
//            ProviderOnboardingScreen(navController = navController)
//        }
//    }
//}


/// file: com/dholsagar/app/core/navigation/AppNavigation.kt
package com.dholsagar.app.core.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.dholsagar.app.presentation.auth.*
//import com.dholsagar.app.presentation.home_provider.ProviderHomeScreen
import com.dholsagar.app.presentation.home_user.UserHomeScreen
import com.dholsagar.app.presentation.main.ProviderMainScreen
import com.dholsagar.app.presentation.onboarding_provider.ProviderOnboardingScreen
import com.dholsagar.app.presentation.onboarding_user.UserOnboardingScreen
import com.dholsagar.app.presentation.splash.SplashScreen
import com.dholsagar.app.presentation.provider_details.ProviderDetailsScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Route.SPLASH
    ) {
        composable(route = Route.SPLASH) {
            SplashScreen(navController = navController)
        }

        composable(route = Route.USER_TYPE_SELECTION) {
            UserTypeSelectionScreen(navController = navController)
        }

        authGraph(navController)

        // --- Screens outside the authentication flow ---
        composable(route = Route.USER_HOME) {  UserHomeScreen(navController = navController) }
//        composable(route = Route.PROVIDER_HOME) { ProviderHomeScreen() }
        composable(
            route = Screen.ProviderDetailsScreen.route,
            arguments = listOf(navArgument("providerId") { type = NavType.StringType })
        ) {
            ProviderDetailsScreen(navController = navController)
        }
        composable(route = Route.PROVIDER_HOME) {
            ProviderMainScreen()
        }
    }
}

fun NavGraphBuilder.authGraph(navController: NavHostController) {
    navigation(
        startDestination = Route.AUTH,
        route = Screen.AuthGraph.route, // FIX: Must be Screen.AuthGraph.route
        arguments = listOf(navArgument("userType") { type = NavType.StringType }),
        deepLinks = emptyList()
    ) {
        composable(route = Route.AUTH) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                // FIX: Must be Screen.AuthGraph.route
                navController.getBackStackEntry(Screen.AuthGraph.route)
            }
            val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
            AuthScreen(navController = navController, viewModel = authViewModel)
        }

        composable(route = Route.PHONE_AUTH) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                // FIX: Must be Screen.AuthGraph.route
                navController.getBackStackEntry(Screen.AuthGraph.route)
            }
            val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
            PhoneAuthScreen(navController = navController, viewModel = authViewModel)
        }

        composable(route = Route.OTP) { backStackEntry ->
            val parentEntry = remember(backStackEntry) {
                // FIX: Must be Screen.AuthGraph.route
                navController.getBackStackEntry(Screen.AuthGraph.route)
            }
            val authViewModel = hiltViewModel<AuthViewModel>(parentEntry)
            OtpScreen(navController = navController, viewModel = authViewModel)
        }

        composable(route = Route.USER_ONBOARDING) {
            UserOnboardingScreen(navController = navController)
        }
        composable(route = Route.PROVIDER_ONBOARDING) {
            ProviderOnboardingScreen(navController = navController)
        }
    }
}
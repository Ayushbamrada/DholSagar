//package com.dholsagar.app.core.navigation
//
//sealed class Screen(val route: String) {
//    data object SplashScreen : Screen("splash_screen")
//    data object UserTypeSelectionScreen : Screen("user_type_selection_screen") // ADD THIS
//    data object AuthScreen : Screen("auth_screen/{userType}") {
//        fun createRoute(userType: String) = "auth_screen/$userType"
//    }
//    data object PhoneAuthScreen : Screen("phone_auth_screen")
//    data object OtpScreen : Screen("otp_screen")
//    data object UserHomeScreen : Screen("user_home_screen")
//    data object ProviderHomeScreen : Screen("provider_home_screen")
//    data class BookingScreen(val providerId: String) : Screen("booking_screen/$providerId") {
//        fun createRoute() = "booking_screen/$providerId"
//    }
//    // Add all other screens from the architecture doc here
//}

// file: com/dholsagar/app/core/navigation/Screen.kt
package com.dholsagar.app.core.navigation

// Holds the string constants for our routes to avoid magic strings
object Route {
    const val SPLASH = "splash"
    const val USER_TYPE_SELECTION = "user_type_selection"

    // This is the route for the entire nested authentication graph
    const val AUTH_GRAPH = "auth_graph"
    const val AUTH = "auth" // The starting screen of the auth graph

    const val PHONE_AUTH = "phone_auth"
    const val OTP = "otp"
    const val USER_HOME = "user_home"
    const val PROVIDER_HOME = "provider_home"
}

// Sealed class to define our screens and their specific route structures
sealed class Screen(val route: String) {
    object SplashScreen : Screen(Route.SPLASH)
    object UserTypeSelectionScreen : Screen(Route.USER_TYPE_SELECTION)

    // The AuthScreen requires a userType argument to be passed to it
    object AuthScreen : Screen("${Route.AUTH}/{userType}") {
        fun createRoute(userType: String) = "${Route.AUTH}/$userType"
    }

    object PhoneAuthScreen : Screen(Route.PHONE_AUTH)
    object OtpScreen : Screen(Route.OTP)
    object UserHomeScreen : Screen(Route.USER_HOME)
    object ProviderHomeScreen : Screen(Route.PROVIDER_HOME)
}
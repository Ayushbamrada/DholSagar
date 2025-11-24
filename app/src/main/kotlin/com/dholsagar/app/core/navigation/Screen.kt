//// file: com/dholsagar/app/core/navigation/Screen.kt
//package com.dholsagar.app.core.navigation
//
//// Holds the string constants for our routes to avoid magic strings
//object Route {
//    const val SPLASH = "splash"
//    const val USER_TYPE_SELECTION = "user_type_selection"
//
//    // This is the route for the entire nested authentication graph
//    const val AUTH_GRAPH = "auth_graph"
//    const val AUTH = "auth" // The starting screen of the auth graph
//
//    const val PHONE_AUTH = "phone_auth"
//    const val OTP = "otp"
//    const val USER_HOME = "user_home"
//    const val PROVIDER_HOME = "provider_home"
//    const val USER_ONBOARDING = "user_onboarding"
//    const val PROVIDER_ONBOARDING = "provider_onboarding"
//}
//
//// Sealed class to define our screens and their specific route structures
//sealed class Screen(val route: String) {
//    object SplashScreen : Screen(Route.SPLASH)
//    object UserTypeSelectionScreen : Screen(Route.USER_TYPE_SELECTION)
//
//    // The AuthScreen requires a userType argument to be passed to it
//    object AuthScreen : Screen("${Route.AUTH}/{userType}") {
//        fun createRoute(userType: String) = "${Route.AUTH}/$userType"
//    }
//
//    object PhoneAuthScreen : Screen(Route.PHONE_AUTH)
//    object OtpScreen : Screen(Route.OTP)
//    object UserHomeScreen : Screen(Route.USER_HOME)
//    object ProviderHomeScreen : Screen(Route.PROVIDER_HOME)
//    data object UserOnboardingScreen : Screen(Route.USER_ONBOARDING)
//    data object ProviderOnboardingScreen : Screen(Route.PROVIDER_ONBOARDING)
//}

// file: com/dholsagar/app/core/navigation/Screen.kt
package com.dholsagar.app.core.navigation

object Route {
    const val SPLASH = "splash"
    const val USER_TYPE_SELECTION = "user_type_selection"
    const val AUTH_GRAPH = "auth_graph"
    const val AUTH = "auth"
    const val PHONE_AUTH = "phone_auth"
    const val OTP = "otp"
    const val USER_HOME = "user_home"
    const val PROVIDER_HOME = "provider_home"
    const val USER_ONBOARDING = "user_onboarding"
    const val PROVIDER_ONBOARDING = "provider_onboarding"
    const val PROVIDER_DETAILS = "provider_details"
    const val PROVIDER_DASHBOARD = "provider_dashboard"
    const val PROVIDER_BOOKINGS = "provider_bookings"
    const val PROVIDER_PROFILE = "provider_profile"
}

sealed class Screen(val route: String) {
    data object SplashScreen : Screen(Route.SPLASH)
    data object UserTypeSelectionScreen : Screen(Route.USER_TYPE_SELECTION)

    // The Auth Graph now correctly accepts the {userType} argument
    data object AuthGraph : Screen("${Route.AUTH_GRAPH}/{userType}") {
        fun createRoute(userType: String) = "${Route.AUTH_GRAPH}/$userType"
    }

    data object AuthScreen : Screen(Route.AUTH)
    data object PhoneAuthScreen : Screen(Route.PHONE_AUTH)
    data object OtpScreen : Screen(Route.OTP)
    data object UserHomeScreen : Screen(Route.USER_HOME)
    data object ProviderHomeScreen : Screen(Route.PROVIDER_HOME)
    data object UserOnboardingScreen : Screen(Route.USER_ONBOARDING)
    data object ProviderOnboardingScreen : Screen(Route.PROVIDER_ONBOARDING)
    data object ProviderDetailsScreen : Screen("${Route.PROVIDER_DETAILS}/{providerId}") {
        fun createRoute(providerId: String) = "${Route.PROVIDER_DETAILS}/$providerId"
    }
    data object ProviderDashboardScreen : Screen(Route.PROVIDER_DASHBOARD)
    data object ProviderBookingsScreen : Screen(Route.PROVIDER_BOOKINGS)
    data object ProviderProfileScreen : Screen(Route.PROVIDER_PROFILE)
}
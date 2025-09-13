//// file: com/dholsagar/app/presentation/splash/SplashViewModel.kt
//package com.dholsagar.app.presentation.splash
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.dholsagar.app.core.navigation.Route
////import com.dholsagar.app.core.navigation.Screen
//import com.dholsagar.app.core.navigation.Screen
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.delay
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//@HiltViewModel
//class SplashViewModel @Inject constructor() : ViewModel() {
//
//    private val _isLoading = MutableStateFlow(true)
//    val isLoading = _isLoading.asStateFlow()
//
//    private val _startDestination = MutableStateFlow(Route.USER_TYPE_SELECTION)
//    val startDestination = _startDestination.asStateFlow()
//
//    init {
//        viewModelScope.launch {
//            // Here you would inject and call a use case like:
//            // val user = checkAuthStatusUseCase()
//            // For now, we simulate a check and assume the user is not logged in.
//            delay(2000) // Simulate network call or asset loading
//
//            // Logic to add later:
//            // if (user != null) {
//            //    _startDestination.value = Screen.UserHomeScreen.route // or ProviderHomeScreen
//            // } else {
//            //    _startDestination.value = Screen.AuthScreen.route
//            // }
//
//            _isLoading.value = false
//        }
//    }
//}

// file: com/dholsagar/app/presentation/splash/SplashViewModel.kt
package com.dholsagar.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.navigation.Route
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class SplashEvent {
    data class Navigate(val route: String): SplashEvent()
}

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: AuthRepository
) : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _eventChannel = MutableStateFlow<SplashEvent?>(null)
    val eventFlow = _eventChannel.asStateFlow()

    init {
        viewModelScope.launch {
            delay(2000) // Keep the splash visible for a minimum duration

            val currentUser = repository.currentUser
            if (currentUser != null) {
                // User is logged in, check their profile in Firestore
                when(val profileResult = repository.getUserProfile(currentUser.uid)) {
                    is Resource.Success -> {
                        val user = profileResult.data!!
                        val destination = if (user.userType == "PROVIDER") {
                            Route.PROVIDER_HOME
                        } else {
                            Route.USER_HOME
                        }
                        _eventChannel.value = SplashEvent.Navigate(destination)
                    }
                    is Resource.Error -> {
                        // Profile not found or error, send to login
                        _eventChannel.value = SplashEvent.Navigate(Route.USER_TYPE_SELECTION)
                    }
                    else -> {}
                }
            } else {
                // User is not logged in, send to login
                _eventChannel.value = SplashEvent.Navigate(Route.USER_TYPE_SELECTION)
            }
            _isLoading.value = false
        }
    }
}
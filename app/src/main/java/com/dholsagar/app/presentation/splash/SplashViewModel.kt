// file: com/dholsagar/app/presentation/splash/SplashViewModel.kt
package com.dholsagar.app.presentation.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.navigation.Route
//import com.dholsagar.app.core.navigation.Screen
import com.dholsagar.app.core.navigation.Screen
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _isLoading = MutableStateFlow(true)
    val isLoading = _isLoading.asStateFlow()

    private val _startDestination = MutableStateFlow(Route.USER_TYPE_SELECTION)
    val startDestination = _startDestination.asStateFlow()

    init {
        viewModelScope.launch {
            // Here you would inject and call a use case like:
            // val user = checkAuthStatusUseCase()
            // For now, we simulate a check and assume the user is not logged in.
            delay(2000) // Simulate network call or asset loading

            // Logic to add later:
            // if (user != null) {
            //    _startDestination.value = Screen.UserHomeScreen.route // or ProviderHomeScreen
            // } else {
            //    _startDestination.value = Screen.AuthScreen.route
            // }

            _isLoading.value = false
        }
    }
}
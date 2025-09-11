// file: com/dholsagar/app/presentation/onboarding_user/UserOnboardingViewModel.kt
package com.dholsagar.app.presentation.onboarding_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.navigation.Route
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.repository.AuthRepository
import com.dholsagar.app.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class OnboardingState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class OnboardingEvent {
    data class Navigate(val route: String) : OnboardingEvent()
}

@HiltViewModel
class UserOnboardingViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(OnboardingState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<OnboardingEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    init {
        // Pre-fill fields if user signed in with Google
        authRepository.currentUser?.let { user ->
            _name.value = user.displayName ?: ""
            _email.value = user.email ?: ""
        }
    }

    fun onNameChange(newName: String) {
        _name.value = newName
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }


    fun onSaveClick() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                _state.update { it.copy(error = "User not found. Please log in again.") }
                return@launch
            }

            _state.update { it.copy(isLoading = true) }

            // Step 1: Update the user's profile with the new name and email
            val updateResult = userRepository.updateUserProfile(uid, _name.value, _email.value)

            if (updateResult is Resource.Success) {
                // Step 2: After successful update, get the user's role from Firestore
                when (val profileResult = authRepository.getUserProfile(uid)) {
                    is Resource.Success -> {
                        val user = profileResult.data!!
                        // Step 3: Navigate based on the user's role
                        val destination = if (user.userType == "PROVIDER") {
                            Route.PROVIDER_ONBOARDING
                        } else {
                            Route.USER_HOME
                        }
                        _eventChannel.send(OnboardingEvent.Navigate(destination))
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = profileResult.message) }
                    }
                    else -> { _state.update { it.copy(isLoading = false) } }
                }
            } else {
                _state.update { it.copy(isLoading = false, error = updateResult.message) }
            }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }
}
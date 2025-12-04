// file: com/dholsagar/app/presentation/home_provider/ProviderProfileViewModel.kt
package com.dholsagar.app.presentation.home_provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.User
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

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
    val error: String? = null
)

sealed class ProfileEvent {
    data object NavigateToLogin : ProfileEvent()
}

@HiltViewModel
class ProviderProfileViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProfileState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<ProfileEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val uid = authRepository.currentUser?.uid
            if (uid != null) {
                when(val result = userRepository.getUserProfile(uid)) {
                    is Resource.Success -> {
                        _state.update { it.copy(isLoading = false, user = result.data) }
                    }
                    is Resource.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    else -> {}
                }
            } else {
                _state.update { it.copy(isLoading = false, error = "User not logged in") }
            }
        }
    }

    fun onLogoutClick() {
        viewModelScope.launch {
            authRepository.signOut()
            _eventChannel.send(ProfileEvent.NavigateToLogin)
        }
    }
}
// file: com/dholsagar/app/presentation/auth/AuthViewModel.kt
package com.dholsagar.app.presentation.auth

import android.app.Activity
import android.content.IntentSender
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.navigation.Route // Correct import
import com.dholsagar.app.core.navigation.Screen
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.repository.AuthRepository
import com.dholsagar.app.domain.repository.GoogleSignInResult
import com.dholsagar.app.domain.repository.PhoneAuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class AuthUiEvent {
    data class LaunchGoogleSignIn(val intentSender: IntentSender) : AuthUiEvent()
    data class Navigate(val route: String) : AuthUiEvent()
    data class NavigateAndPopUpTo(val route: String, val popUpTo: String) : AuthUiEvent()
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userType: String? = savedStateHandle.get<String>("userType")

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    private val _eventChannel = Channel<AuthUiEvent>()
    val eventFlow = _eventChannel.receiveAsFlow()

    private val _phoneNumber = MutableStateFlow("")
    val phoneNumber = _phoneNumber.asStateFlow()

    private val _otp = MutableStateFlow("")
    val otp = _otp.asStateFlow()

    private var currentVerificationId: String? = null

    fun onPhoneNumberChange(newValue: String) {
        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
            _phoneNumber.value = newValue
        }
    }

    fun onOtpChange(newValue: String) {
        if (newValue.all { it.isDigit() } && newValue.length <= 6) {
            _otp.value = newValue
        }
    }

    fun onSendOtpClick(activity: Activity) {
        if (_phoneNumber.value.length < 10) {
            _state.update { it.copy(error = "Please enter a valid 10-digit phone number.") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val fullPhoneNumber = "+91${_phoneNumber.value}"
            repository.sendOtp(fullPhoneNumber, activity).collect { result ->
                when (result) {
                    is PhoneAuthResult.CodeSent -> {
                        _state.update { it.copy(isLoading = false) }
                        currentVerificationId = result.verificationId
                        _eventChannel.send(AuthUiEvent.Navigate(Screen.OtpScreen.route))
                    }
                    is PhoneAuthResult.Error -> {
                        _state.update { it.copy(isLoading = false, error = result.message) }
                    }
                    is PhoneAuthResult.VerificationCompleted -> {
                        _state.update { it.copy(isLoading = false) }
                    }
                }
            }
        }
    }

    fun onVerifyOtpClick() {
        val verificationId = currentVerificationId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.verifyOtp(verificationId, _otp.value)) {
                is Resource.Success -> handleSuccessfulSignIn(result.data!!.user!!)
                is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> { /* No-op, isLoading is already true */ }
            }
        }
    }

    fun onGoogleSignInClick() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when (val result = repository.getGoogleSignInIntentSender()) {
                is GoogleSignInResult.Success -> {
                    _eventChannel.send(AuthUiEvent.LaunchGoogleSignIn(result.intentSender))
                }
                is GoogleSignInResult.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
            }
        }
    }

    fun onGoogleSignInResult(idToken: String?) {
        if (idToken == null) {
            _state.update { it.copy(isLoading = false, error = "Google Sign-In failed.") }
            return
        }
        viewModelScope.launch {
            when (val result = repository.signInWithGoogle(idToken)) {
                is Resource.Success -> handleSuccessfulSignIn(result.data!!.user!!)
                is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
                is Resource.Loading -> { /* No-op */ }
            }
        }
    }

    private fun handleSuccessfulSignIn(firebaseUser: FirebaseUser) {
        val finalUserType = userType
        if (finalUserType == null) {
            _state.update { it.copy(isLoading = false, error = "User type is missing. Please restart the process.") }
            return
        }

        viewModelScope.launch {
            when (val userExistsResult = repository.checkUserExists(firebaseUser.uid)) {
                is Resource.Success -> {
                    if (userExistsResult.data == true) {
                        navigateToHome(firebaseUser.uid)
                    } else {
                        createNewUserProfile(firebaseUser, finalUserType)
                    }
                }
                is Resource.Error -> _state.update { it.copy(isLoading = false, error = userExistsResult.message) }
                is Resource.Loading -> { /* No-op */ }
            }
        }
    }

    private suspend fun navigateToHome(uid: String) {
        when (val profileResult = repository.getUserProfile(uid)) {
            is Resource.Success -> {
                val user = profileResult.data!!
                val destination = if (user.userType == "PROVIDER") Screen.ProviderHomeScreen.route else Screen.UserHomeScreen.route
                _eventChannel.send(AuthUiEvent.NavigateAndPopUpTo(destination, Route.AUTH_GRAPH))
            }
            is Resource.Error -> _state.update { it.copy(isLoading = false, error = profileResult.message) }
            is Resource.Loading -> { /* No-op */ }
        }
    }

    private suspend fun createNewUserProfile(firebaseUser: FirebaseUser, finalUserType: String) {
        when (repository.createUserProfile(firebaseUser, finalUserType)) {
            is Resource.Success -> {
                val destination = if (finalUserType == "PROVIDER") Screen.ProviderHomeScreen.route else Screen.UserHomeScreen.route
                _eventChannel.send(AuthUiEvent.NavigateAndPopUpTo(destination, Route.AUTH_GRAPH))
            }
            is Resource.Error -> _state.update { it.copy(isLoading = false, error = "Failed to create user profile.") }
            is Resource.Loading -> { /* No-op */ }
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }
}
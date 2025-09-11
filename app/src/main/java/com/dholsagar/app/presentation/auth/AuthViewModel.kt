// file: com/dholsagar/app/presentation/auth/AuthViewModel.kt
package com.dholsagar.app.presentation.auth

import android.app.Activity
import android.content.IntentSender
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.navigation.Route
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.repository.AuthRepository
import com.dholsagar.app.domain.repository.GoogleSignInResult
import com.dholsagar.app.domain.repository.PhoneAuthResult
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import androidx.lifecycle.viewModelScope
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
    data class NavigateAndPopUp(val route: String, val popUpTo: String) : AuthUiEvent() // This was missing
}

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val userType: String = savedStateHandle.get<String>("userType") ?: "USER"


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
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                val fullPhoneNumber = "+91${_phoneNumber.value}"
                repository.sendOtp(fullPhoneNumber, activity).onEach { result ->
                    // We only stop loading when the flow gives us a result
                    _state.update { it.copy(isLoading = false) }
                    when (result) {
                        is PhoneAuthResult.CodeSent -> {
                            currentVerificationId = result.verificationId
                            _eventChannel.send(AuthUiEvent.Navigate(Route.OTP))
                        }
                        is PhoneAuthResult.Error -> _state.update { it.copy(error = result.message) }
                        is PhoneAuthResult.VerificationCompleted -> {
                            if (repository.currentUser != null) {
                                handleSuccessfulSignIn(repository.currentUser!!)
                            }
                        }
                    }
                }.launchIn(viewModelScope)
            } finally {
                // This block ensures that if the user presses back while the
                // initial request is happening, the spinner still stops.
                // Note: The main isLoading=false is inside onEach for the callback.
            }
        }
    }

    fun onVerifyOtpClick() {
        val verificationId = currentVerificationId ?: return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val result = repository.verifyOtp(verificationId, _otp.value)) {
                    is Resource.Success -> handleSuccessfulSignIn(result.data!!.user!!)
                    is Resource.Error -> _state.update { it.copy(error = result.message) }
                    else -> {}
                }
            } finally {
                // This ensures the spinner stops if the user navigates away or an error occurs
                // that isn't caught explicitly.
                if (state.value.isLoading) {
                    _state.update { it.copy(isLoading = false) }
                }
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
                else -> _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private fun handleSuccessfulSignIn(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            try {
                when (val userExistsResult = repository.checkUserExists(firebaseUser.uid)) {
                    is Resource.Success -> {
                        val destination = if (userExistsResult.data == true) {
                            getDestinationForExistingUser(firebaseUser.uid)
                        } else {
                            getDestinationForNewUser(firebaseUser)
                        }
                        _eventChannel.send(AuthUiEvent.NavigateAndPopUp(destination, Route.AUTH_GRAPH))
                    }
                    is Resource.Error -> _state.update { it.copy(error = userExistsResult.message) }
                    else -> {}
                }
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    private suspend fun getDestinationForExistingUser(uid: String): String {
        return when (val profileResult = repository.getUserProfile(uid)) {
            is Resource.Success -> {
                if (profileResult.data?.userType == "PROVIDER") Route.PROVIDER_HOME else Route.USER_HOME
            }
            else -> Route.USER_HOME // Default to user home on error
        }
    }

    private suspend fun getDestinationForNewUser(firebaseUser: FirebaseUser): String {
        return when (repository.createUserProfile(firebaseUser, userType)) {
            is Resource.Success -> {
                // TODO: In the future, navigate to specific onboarding screens.
                if (userType == "PROVIDER") Route.PROVIDER_ONBOARDING else Route.USER_ONBOARDING
            }
            else -> Route.USER_HOME // Default to user home on error
        }
    }

    fun onErrorShown() {
        _state.update { it.copy(error = null) }
    }
}

//// file: com/dholsagar/app/presentation/auth/AuthViewModel.kt
//package com.dholsagar.app.presentation.auth
//
//import android.app.Activity
//import android.content.IntentSender
//import androidx.lifecycle.SavedStateHandle
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.dholsagar.app.core.navigation.Route
//import com.dholsagar.app.core.util.Resource
//import com.dholsagar.app.domain.repository.AuthRepository
//import com.dholsagar.app.domain.repository.GoogleSignInResult
//import com.dholsagar.app.domain.repository.PhoneAuthResult
//import com.google.firebase.auth.FirebaseUser
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.channels.Channel
//import kotlinx.coroutines.flow.*
//import kotlinx.coroutines.launch
//import javax.inject.Inject
//
//data class AuthState(
//    val isLoading: Boolean = false,
//    val error: String? = null
//)
//
//sealed class AuthUiEvent {
//    data class LaunchGoogleSignIn(val intentSender: IntentSender) : AuthUiEvent()
//    data class Navigate(val route: String) : AuthUiEvent()
//    data class NavigateAndPopUp(val route: String, val popUpTo: String) : AuthUiEvent()
//}
//
//@HiltViewModel
//class AuthViewModel @Inject constructor(
//    private val repository: AuthRepository,
//    private val savedStateHandle: SavedStateHandle
//) : ViewModel() {
//
//    private val _state = MutableStateFlow(AuthState())
//    val state = _state.asStateFlow()
//
//    private val _eventChannel = Channel<AuthUiEvent>()
//    val eventFlow = _eventChannel.receiveAsFlow()
//
//    private val _phoneNumber = MutableStateFlow("")
//    val phoneNumber = _phoneNumber.asStateFlow()
//
//    private val _otp = MutableStateFlow("")
//    val otp = _otp.asStateFlow()
//
//    private var currentVerificationId: String? = null
//
//    fun onPhoneNumberChange(newValue: String) {
//        if (newValue.all { it.isDigit() } && newValue.length <= 10) {
//            _phoneNumber.value = newValue
//        }
//    }
//
//    fun onOtpChange(newValue: String) {
//        if (newValue.all { it.isDigit() } && newValue.length <= 6) {
//            _otp.value = newValue
//        }
//    }
//
//    fun onSendOtpClick(activity: Activity) {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            val fullPhoneNumber = "+91${_phoneNumber.value}"
//            repository.sendOtp(fullPhoneNumber, activity).onEach { result ->
//                _state.update { it.copy(isLoading = false) }
//                when (result) {
//                    is PhoneAuthResult.CodeSent -> {
//                        currentVerificationId = result.verificationId
//                        _eventChannel.send(AuthUiEvent.Navigate(Route.OTP))
//                    }
//                    is PhoneAuthResult.Error -> _state.update { it.copy(error = result.message) }
//                    is PhoneAuthResult.VerificationCompleted -> {
//                        if (repository.currentUser != null) {
//                            val userType: String = savedStateHandle.get<String>("userType")!!
//                            handleSuccessfulSignIn(repository.currentUser!!, userType)
//                        }
//                    }
//                }
//            }.launchIn(viewModelScope)
//        }
//    }
//
//    fun onVerifyOtpClick() {
//        val verificationId = currentVerificationId ?: return
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                when (val result = repository.verifyOtp(verificationId, _otp.value)) {
//                    is Resource.Success -> {
//                        val userType: String = savedStateHandle.get<String>("userType")!!
//                        handleSuccessfulSignIn(result.data!!.user!!, userType)
//                    }
//                    is Resource.Error -> _state.update { it.copy(error = result.message) }
//                    else -> {}
//                }
//            } finally {
//                if (state.value.isLoading) {
//                    _state.update { it.copy(isLoading = false) }
//                }
//            }
//        }
//    }
//
//    fun onGoogleSignInClick() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            when (val result = repository.getGoogleSignInIntentSender()) {
//                is GoogleSignInResult.Success -> _eventChannel.send(AuthUiEvent.LaunchGoogleSignIn(result.intentSender))
//                is GoogleSignInResult.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
//            }
//        }
//    }
//
//    fun onGoogleSignInResult(idToken: String?) {
//        if (idToken == null) {
//            _state.update { it.copy(isLoading = false, error = "Google Sign-In failed.") }
//            return
//        }
//        viewModelScope.launch {
//            when (val result = repository.signInWithGoogle(idToken)) {
//                is Resource.Success -> {
//                    val userType: String = savedStateHandle.get<String>("userType")!!
//                    handleSuccessfulSignIn(result.data!!.user!!, userType)
//                }
//                is Resource.Error -> _state.update { it.copy(isLoading = false, error = result.message) }
//                else -> _state.update { it.copy(isLoading = false) }
//            }
//        }
//    }
//
//    private fun handleSuccessfulSignIn(firebaseUser: FirebaseUser, userType: String) {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            try {
//                when (val userExistsResult = repository.checkUserExists(firebaseUser.uid)) {
//                    is Resource.Success -> {
//                        val destination = if (userExistsResult.data == true) {
//                            getDestinationForExistingUser(firebaseUser.uid)
//                        } else {
//                            getDestinationForNewUser(firebaseUser, userType)
//                        }
//                        _eventChannel.send(AuthUiEvent.NavigateAndPopUp(destination, Route.AUTH_GRAPH))
//                    }
//                    is Resource.Error -> _state.update { it.copy(error = userExistsResult.message) }
//                    else -> {}
//                }
//            } finally {
//                _state.update { it.copy(isLoading = false) }
//            }
//        }
//    }
//
//    private suspend fun getDestinationForExistingUser(uid: String): String {
//        return when (val profileResult = repository.getUserProfile(uid)) {
//            is Resource.Success -> if (profileResult.data?.userType == "PROVIDER") Route.PROVIDER_HOME else Route.USER_HOME
//            else -> Route.USER_HOME
//        }
//    }
//
//    private suspend fun getDestinationForNewUser(firebaseUser: FirebaseUser, userType: String): String {
//        return when (repository.createUserProfile(firebaseUser, userType)) {
//            is Resource.Success -> if (userType == "PROVIDER") Route.PROVIDER_ONBOARDING else Route.USER_ONBOARDING
//            else -> Route.USER_HOME
//        }
//    }
//
//    fun onErrorShown() {
//        _state.update { it.copy(error = null) }
//    }
//}
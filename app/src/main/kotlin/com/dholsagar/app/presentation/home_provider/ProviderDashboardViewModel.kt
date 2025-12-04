package com.dholsagar.app.presentation.home_provider

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.AdBanner
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.domain.repository.AuthRepository
import com.dholsagar.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderDashboardState(
    val isLoading: Boolean = false,
    val provider: ServiceProvider? = null,
    val error: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val adBanner: AdBanner? = null,
    val isUploading: Boolean = false // New state for portfolio upload
)

@HiltViewModel
class ProviderDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProviderDashboardState())
    val state = _state.asStateFlow()

    // Text fields state
    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()
    private val _specialty = MutableStateFlow("")
    val specialty = _specialty.asStateFlow()
    private val _perDayCharge = MutableStateFlow("")
    val perDayCharge = _perDayCharge.asStateFlow()
    private val _chargeDescription = MutableStateFlow("")
    val chargeDescription = _chargeDescription.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                _state.update { it.copy(isLoading = false, error = "User not found.") }
                return@launch
            }

            // Fetch Ad
            when (val adResult = providerRepository.getDashboardAd()) {
                is Resource.Success -> { _state.update { it.copy(adBanner = adResult.data) } }
                else -> {}
            }

            // Fetch Details
            fetchProviderDetails(uid)
        }
    }

    private suspend fun fetchProviderDetails(uid: String) {
        when (val result = providerRepository.getProviderDetails(uid)) {
            is Resource.Success -> {
                val provider = result.data!!
                _state.update { it.copy(isLoading = false, provider = provider) }
                _description.value = provider.description
                _specialty.value = provider.specialty
                _perDayCharge.value = provider.perDayCharge
                _chargeDescription.value = provider.chargeDescription
            }
            is Resource.Error -> {
                _state.update { it.copy(isLoading = false, error = result.message) }
            }
            else -> { _state.update { it.copy(isLoading = false) } }
        }
    }

    // --- Text Field Updaters ---
    fun onDescriptionChange(text: String) { _description.value = text }
    fun onSpecialtyChange(text: String) { _specialty.value = text }
    fun onPerDayChargeChange(text: String) { _perDayCharge.value = text }
    fun onChargeDescriptionChange(text: String) { _chargeDescription.value = text }

    fun onSaveDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, saveSuccess = false) }
            val uid = authRepository.currentUser?.uid ?: return@launch

            val result = providerRepository.updateProviderDetails(
                uid, _description.value, _specialty.value, _perDayCharge.value, _chargeDescription.value
            )

            when(result) {
                is Resource.Success -> {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                    fetchProviderDetails(uid) // Refresh data
                }
                is Resource.Error -> _state.update { it.copy(isSaving = false, error = result.message) }
                else -> {}
            }
        }
    }

    fun onSaveSuccessShown() {
        _state.update { it.copy(saveSuccess = false) }
    }

    // --- NEW: Portfolio Logic ---

    fun uploadPortfolioImage(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true) }
            val uid = authRepository.currentUser?.uid ?: return@launch

            when(val result = providerRepository.addPortfolioImage(uid, uri)) {
                is Resource.Success -> {
                    fetchProviderDetails(uid) // Refresh UI
                    _state.update { it.copy(isUploading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isUploading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun deletePortfolioImage(url: String) {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            providerRepository.removePortfolioImage(uid, url)
            fetchProviderDetails(uid) // Refresh immediately
        }
    }

    fun uploadPortfolioVideo(uri: Uri) {
        viewModelScope.launch {
            _state.update { it.copy(isUploading = true) }
            val uid = authRepository.currentUser?.uid ?: return@launch

            when(val result = providerRepository.updatePortfolioVideo(uid, uri)) {
                is Resource.Success -> {
                    fetchProviderDetails(uid)
                    _state.update { it.copy(isUploading = false) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isUploading = false, error = result.message) }
                }
                else -> {}
            }
        }
    }

    fun deletePortfolioVideo() {
        viewModelScope.launch {
            val uid = authRepository.currentUser?.uid ?: return@launch
            providerRepository.removePortfolioVideo(uid)
            fetchProviderDetails(uid)
        }
    }
}
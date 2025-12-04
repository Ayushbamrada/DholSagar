// file: com/dholsagar/app/presentation/home_provider/ProviderDashboardViewModel.kt
package com.dholsagar.app.presentation.home_provider

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
    val adBanner: AdBanner? = null
)

@HiltViewModel
class ProviderDashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProviderDashboardState())
    val state = _state.asStateFlow()

    // --- State for the text fields ---
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

            // 1. Fetch Dynamic Ad Banner
            // We fetch this first or in parallel so it's ready for the UI
            when (val adResult = providerRepository.getDashboardAd()) {
                is Resource.Success -> {
                    _state.update { it.copy(adBanner = adResult.data) }
                }
                else -> {
                    // If ad fetch fails, we just don't show it, no need to show error to user
                }
            }

            // 2. Fetch Provider Details
            when (val result = providerRepository.getProviderDetails(uid)) {
                is Resource.Success -> {
                    val provider = result.data!!
                    _state.update { it.copy(isLoading = false, provider = provider) }

                    // --- Pre-fill the text fields with existing data ---
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
    }

    // --- Functions to update text field state ---
    fun onDescriptionChange(text: String) { _description.value = text }
    fun onSpecialtyChange(text: String) { _specialty.value = text }
    fun onPerDayChargeChange(text: String) { _perDayCharge.value = text }
    fun onChargeDescriptionChange(text: String) { _chargeDescription.value = text }

    // --- Function to save the data ---
    fun onSaveDetails() {
        viewModelScope.launch {
            _state.update { it.copy(isSaving = true, saveSuccess = false) }
            val uid = authRepository.currentUser?.uid
            if (uid == null) {
                _state.update { it.copy(isSaving = false, error = "User not found.") }
                return@launch
            }

            val result = providerRepository.updateProviderDetails(
                uid = uid,
                description = _description.value,
                specialty = _specialty.value,
                perDayCharge = _perDayCharge.value,
                chargeDescription = _chargeDescription.value
            )

            when(result) {
                is Resource.Success -> {
                    _state.update { it.copy(isSaving = false, saveSuccess = true) }
                    // Reload data to reflect changes immediately in the UI if needed
                    // loadDashboardData()
                }
                is Resource.Error -> {
                    _state.update { it.copy(isSaving = false, error = result.message) }
                }
                else -> { _state.update { it.copy(isSaving = false) } }
            }
        }
    }

    fun onSaveSuccessShown() {
        _state.update { it.copy(saveSuccess = false) }
    }
}
// file: com/dholsagar/app/presentation/home_provider/ProviderDashboardViewModel.kt
package com.dholsagar.app.presentation.home_provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.util.Resource
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
    val saveSuccess: Boolean = false
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

            when (val result = providerRepository.getProviderDetails(uid)) {
                is Resource.Success -> {
                    val provider = result.data!!
                    _state.update { it.copy(isLoading = false, provider = provider) }
                    // --- Pre-fill the text fields ---
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
// file: com/dholsagar/app/presentation/provider_details/ProviderDetailsViewModel.kt
package com.dholsagar.app.presentation.provider_details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class ProviderDetailsState(
    val isLoading: Boolean = false,
    val provider: ServiceProvider? = null,
    val error: String? = null
)

@HiltViewModel
class ProviderDetailsViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _state = MutableStateFlow(com.dholsagar.app.presentation.provider_details.ProviderDetailsState())
    val state = _state.asStateFlow()

    init {
        savedStateHandle.get<String>("providerId")?.let { providerId ->
            loadProviderDetails(providerId)
        }
    }

    private fun loadProviderDetails(providerId: String) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val result = providerRepository.getProviderDetails(providerId)) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, provider = result.data) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> { _state.update { it.copy(isLoading = false) } }
            }
        }
    }
}
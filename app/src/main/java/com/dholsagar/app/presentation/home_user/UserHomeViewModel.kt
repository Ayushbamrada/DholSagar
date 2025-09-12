// file: com/dholsagar/app/presentation/home_user/UserHomeViewModel.kt
package com.dholsagar.app.presentation.home_user

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.ServiceProvider
import com.dholsagar.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class UserHomeState(
    val isLoading: Boolean = false,
    val providers: List<ServiceProvider> = emptyList(),
    val error: String? = null
)

@HiltViewModel
class UserHomeViewModel @Inject constructor(
    private val providerRepository: ProviderRepository // Inject repository
) : ViewModel() {

    private val _state = MutableStateFlow(UserHomeState())
    val state = _state.asStateFlow()

    init {
        loadProviders()
    }

    fun loadProviders() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            when(val result = providerRepository.getProviders()) {
                is Resource.Success -> {
                    _state.update { it.copy(isLoading = false, providers = result.data ?: emptyList()) }
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> { _state.update { it.copy(isLoading = false) } }
            }
        }
    }
}
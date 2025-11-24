// file: com/dholsagar/app/presentation/home_provider/ProviderBookingsViewModel.kt
package com.dholsagar.app.presentation.home_provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.Booking
import com.dholsagar.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

data class ProviderBookingsState(
    val isLoading: Boolean = false,
    val filteredBookings: List<Booking> = emptyList(), // UI shows this list
    val error: String? = null,
    val selectedDate: Date = Date() // Today's date
)

@HiltViewModel
class ProviderBookingsViewModel @Inject constructor(
    private val providerRepository: ProviderRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ProviderBookingsState())
    val state = _state.asStateFlow()

    // This list holds ALL bookings, unfiltered
    private var allBookings: List<Booking> = emptyList()

    init {
        loadBookings()
    }

    private fun loadBookings() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            when (val result = providerRepository.getProviderBookings()) {
                is Resource.Success -> {
                    allBookings = result.data ?: emptyList()
                    // Initially, filter for the selected date (today)
                    filterBookingsForDate(allBookings, _state.value.selectedDate)
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> {
                    _state.update { it.copy(isLoading = false) }
                }
            }
        }
    }

    fun onDateSelected(newDate: Date) {
        // Update the state and re-filter the list
        filterBookingsForDate(allBookings, newDate)
    }

    private fun filterBookingsForDate(bookings: List<Booking>, selectedDate: Date) {
        val cal1 = Calendar.getInstance().apply { time = selectedDate }

        val filtered = bookings.filter { booking ->
            val cal2 = Calendar.getInstance().apply { time = booking.startDate }
            // Compare year, month, and day
            cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                    cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
        }

        _state.update { it.copy(
            isLoading = false,
            selectedDate = selectedDate,
            filteredBookings = filtered
        )}
    }
}
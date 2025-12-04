//package com.dholsagar.app.presentation.home_provider
//
//import androidx.lifecycle.ViewModel
//import androidx.lifecycle.viewModelScope
//import com.dholsagar.app.core.util.Resource
//import com.dholsagar.app.domain.model.Booking
//import com.dholsagar.app.domain.repository.ProviderRepository
//import dagger.hilt.android.lifecycle.HiltViewModel
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.asStateFlow
//import kotlinx.coroutines.flow.update
//import kotlinx.coroutines.launch
//import java.util.Date
//import javax.inject.Inject
//
//data class ProviderBookingsState(
//    val isLoading: Boolean = false,
//    val requests: List<Booking> = emptyList(),
//    val upcoming: List<Booking> = emptyList(),
//    val completed: List<Booking> = emptyList(),
//    val selectedBooking: Booking? = null,
//    val error: String? = null,
//    val isCalendarVisible: Boolean = false
//)
//
//@HiltViewModel
//class ProviderBookingsViewModel @Inject constructor(
//    private val providerRepository: ProviderRepository
//) : ViewModel() {
//
//    private val _state = MutableStateFlow(ProviderBookingsState())
//    val state = _state.asStateFlow()
//
//    private var allBookings: List<Booking> = emptyList()
//
//    init {
//        loadBookings()
//    }
//
//    private fun loadBookings() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//
//            when (val result = providerRepository.getProviderBookings()) {
//                is Resource.Success -> {
//                    allBookings = result.data ?: emptyList()
//                    categorizeBookings(allBookings)
//                }
//                is Resource.Error -> {
//                    _state.update { it.copy(isLoading = false, error = result.message) }
//                }
//                else -> { _state.update { it.copy(isLoading = false) } }
//            }
//        }
//    }
//
//    private fun categorizeBookings(bookings: List<Booking>) {
//        val requests = bookings.filter { it.status == "REQUESTED" || it.status == "PENDING" }
//        val upcoming = bookings.filter { it.status == "CONFIRMED" && it.startDate.after(Date()) }
//        val completed = bookings.filter { it.status == "COMPLETED" || it.status == "CANCELLED" || it.startDate.before(Date()) && it.status == "CONFIRMED" }
//
//        _state.update { it.copy(
//            isLoading = false,
//            requests = requests,
//            upcoming = upcoming,
//            completed = completed
//        )}
//    }
//
//    fun toggleCalendar() {
//        _state.update { it.copy(isCalendarVisible = !it.isCalendarVisible) }
//    }
//
//    fun getBookingDetails(bookingId: String) {
//        val booking = allBookings.find { it.bookingId == bookingId }
//        _state.update { it.copy(selectedBooking = booking) }
//    }
//
//
//
//    // --- NEW FUNCTIONS ---
//
//    fun createDummyData() {
//        viewModelScope.launch {
//            _state.update { it.copy(isLoading = true) }
//            // Use current user ID or a fallback
//            val uid = "4815tVBRXoQeht67ckGVvIe0U443" // Or fetch from AuthRepository if available
//
//            providerRepository.generateDummyBookings(uid)
//            loadBookings() // Reload list to show new data
//        }
//    }
//
//    fun onAcceptBooking(bookingId: String) {
//        updateStatus(bookingId, "CONFIRMED")
//    }
//
//    fun onDeclineBooking(bookingId: String) {
//        updateStatus(bookingId, "CANCELLED")
//    }
//
//    private fun updateStatus(bookingId: String, status: String) {
//        viewModelScope.launch {
//            providerRepository.updateBookingStatus(bookingId, status)
//            loadBookings() // Refresh the list to move the card to the correct tab
//        }
//    }
//}

package com.dholsagar.app.presentation.home_provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dholsagar.app.core.util.Resource
import com.dholsagar.app.domain.model.Booking
import com.dholsagar.app.domain.repository.AuthRepository // <-- Import this
import com.dholsagar.app.domain.repository.ProviderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date
import javax.inject.Inject

data class ProviderBookingsState(
    val isLoading: Boolean = false,
    val requests: List<Booking> = emptyList(),
    val upcoming: List<Booking> = emptyList(),
    val completed: List<Booking> = emptyList(),
    val selectedBooking: Booking? = null,
    val error: String? = null,
    val isCalendarVisible: Boolean = false
)

@HiltViewModel
class ProviderBookingsViewModel @Inject constructor(
    private val providerRepository: ProviderRepository,
    private val authRepository: AuthRepository // <-- INJECT THIS
) : ViewModel() {

    private val _state = MutableStateFlow(ProviderBookingsState())
    val state = _state.asStateFlow()

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
                    categorizeBookings(allBookings)
                }
                is Resource.Error -> {
                    _state.update { it.copy(isLoading = false, error = result.message) }
                }
                else -> { _state.update { it.copy(isLoading = false) } }
            }
        }
    }

    private fun categorizeBookings(bookings: List<Booking>) {
        val requests = bookings.filter { it.status == "REQUESTED" || it.status == "PENDING" }
        val upcoming = bookings.filter { it.status == "CONFIRMED" && it.startDate.after(Date()) }
        val completed = bookings.filter { it.status == "COMPLETED" || it.status == "CANCELLED" || (it.startDate.before(Date()) && it.status == "CONFIRMED") }

        _state.update { it.copy(
            isLoading = false,
            requests = requests,
            upcoming = upcoming,
            completed = completed
        )}
    }

    fun toggleCalendar() {
        _state.update { it.copy(isCalendarVisible = !it.isCalendarVisible) }
    }

    fun getBookingDetails(bookingId: String) {
        val booking = allBookings.find { it.bookingId == bookingId }
        _state.update { it.copy(selectedBooking = booking) }
    }

    // --- UPDATED DEBUG FUNCTION ---
    fun createDummyData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            // FIX: Use the REAL User ID from AuthRepository
            val uid = authRepository.currentUser?.uid

            if (uid != null) {
                providerRepository.generateDummyBookings(uid)
                loadBookings() // Reload list to show new data immediately
            } else {
                _state.update { it.copy(isLoading = false, error = "Login required to gen data") }
            }
        }
    }

    fun onAcceptBooking(bookingId: String) {
        updateStatus(bookingId, "CONFIRMED")
    }

    fun onDeclineBooking(bookingId: String) {
        updateStatus(bookingId, "CANCELLED")
    }

    private fun updateStatus(bookingId: String, status: String) {
        viewModelScope.launch {
            providerRepository.updateBookingStatus(bookingId, status)
            loadBookings()
        }
    }
}
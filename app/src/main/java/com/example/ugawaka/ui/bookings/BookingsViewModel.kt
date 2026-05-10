package com.example.ugawaka.ui.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugawaka.data.model.Booking
import com.example.ugawaka.data.remote.BookingDto
import com.example.ugawaka.data.repository.UgaWakaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class BookingsViewModel(private val repository: UgaWakaRepository = UgaWakaRepository()) : ViewModel() {
    private val _bookings = MutableStateFlow<List<Booking>>(emptyList())
    val bookings: StateFlow<List<Booking>> = _bookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        fetchBookings()
    }

    fun fetchBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            val result = repository.fetchBookings()
            if (result.isSuccess) {
                _bookings.value = result.getOrNull()?.map { it.toBooking() } ?: emptyList()
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to fetch bookings"
            }
            _isLoading.value = false
        }
    }

    fun cancelBooking(bookingId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.cancelBooking(bookingId)
            if (result.isSuccess) {
                fetchBookings() // Refresh list
            } else {
                _error.value = result.exceptionOrNull()?.message ?: "Failed to cancel booking"
            }
            _isLoading.value = false
        }
    }

    private fun BookingDto.toBooking(): Booking {
        return Booking(
            id = this.id.toString(),
            providerName = this.provider?.name ?: "Unknown Provider",
            serviceName = this.service?.name ?: "Service",
            date = this.scheduled_on.substringBefore('T'),
            status = this.status.replaceFirstChar { it.uppercase() },
            amount = this.provider?.rate?.let { "UGX $it" } ?: "UGX 0",
            location = "" // Location not in the new API payload/response yet
        )
    }
}

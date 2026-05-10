package com.example.ugawaka.ui.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugawaka.data.remote.BookingCreateRequest
import com.example.ugawaka.data.repository.UgaWakaRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class BookingViewModel(private val repository: UgaWakaRepository = UgaWakaRepository()) : ViewModel() {
    private val _providerName = MutableStateFlow("Loading...")
    val providerName: StateFlow<String> = _providerName.asStateFlow()

    private val _providerRate = MutableStateFlow<String?>(null)
    val providerRate: StateFlow<String?> = _providerRate.asStateFlow()

    private val _serviceName = MutableStateFlow("Loading...")
    val serviceName: StateFlow<String> = _serviceName.asStateFlow()

    private val _selectedDate = MutableStateFlow(LocalDate.now())
    val selectedDate: StateFlow<LocalDate> = _selectedDate.asStateFlow()

    private val _selectedLocation = MutableStateFlow(LatLng(0.3476, 32.5825))
    val selectedLocation: StateFlow<LatLng> = _selectedLocation.asStateFlow()

    init {
        viewModelScope.launch {
            repository.userLocation.collect { location ->
                location?.let {
                    _selectedLocation.value = it
                }
            }
        }
    }

    private val _locationName = MutableStateFlow("Fetching location...")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes.asStateFlow()

    private val _selectedPayment = MutableStateFlow("Cash")
    val selectedPayment: StateFlow<String> = _selectedPayment.asStateFlow()

    private val _isSubmitting = MutableStateFlow(false)
    val isSubmitting: StateFlow<Boolean> = _isSubmitting.asStateFlow()

    private val _bookingResult = MutableStateFlow<Result<Unit>?>(null)
    val bookingResult: StateFlow<Result<Unit>?> = _bookingResult.asStateFlow()

    fun onDateSelected(date: LocalDate) {
        _selectedDate.value = date
    }

    fun onLocationSelected(latLng: LatLng, name: String) {
        _selectedLocation.value = latLng
        _locationName.value = name
    }

    fun onNotesChanged(newNotes: String) {
        _notes.value = newNotes
    }

    fun onPaymentMethodSelected(method: String) {
        _selectedPayment.value = method
    }

    fun confirmBooking(providerId: Int) {
        viewModelScope.launch {
            _isSubmitting.value = true
            val request = BookingCreateRequest(
                provider_id = providerId,
                scheduled_on = _selectedDate.value.toString(),
                description = _notes.value.ifBlank { "Booking for service" },
                latitude = _selectedLocation.value.latitude,
                longitude = _selectedLocation.value.longitude
            )
            val result = repository.createBooking(request)
            if (result.isSuccess) {
                _bookingResult.value = Result.success(Unit)
            } else {
                _bookingResult.value = Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
            _isSubmitting.value = false
        }
    }

    fun loadBookingDetails(providerId: Int, serviceId: Int) {
        // In a real app, we would fetch these from the repository
        // For now, let's just simulate it or use the repository if it has the data
        viewModelScope.launch {
             repository.fetchProviderDetail(providerId)
             repository.fetchServiceDetail(serviceId)
             
             repository.selectedProvider.collect { provider ->
                 provider?.let { 
                     _providerName.value = it.name 
                     _providerRate.value = it.rate?.toString()
                 }
             }
        }
        viewModelScope.launch {
            repository.selectedService.collect { service ->
                service?.let { _serviceName.value = it.name }
            }
        }
    }
}

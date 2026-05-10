package com.example.ugawaka.ui.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugawaka.data.repository.UgaWakaRepository
import com.example.ugawaka.data.remote.BookingDto
import com.example.ugawaka.data.remote.ProviderHomeResponse
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ProviderViewModel(private val repository: UgaWakaRepository = UgaWakaRepository()) : ViewModel() {
    // Dashboard State
    private val _earnings = MutableStateFlow("UGX 0")
    val earnings: StateFlow<String> = _earnings.asStateFlow()

    private val _balance = MutableStateFlow("UGX 0")
    val balance: StateFlow<String> = _balance.asStateFlow()

    private val _completedBookings = MutableStateFlow(0)
    val completedBookings: StateFlow<Int> = _completedBookings.asStateFlow()

    private val _pendingBookings = MutableStateFlow(0)
    val pendingBookings: StateFlow<Int> = _pendingBookings.asStateFlow()

    private val _rating = MutableStateFlow("0.0")
    val rating: StateFlow<String> = _rating.asStateFlow()

    private val _recentBookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val recentBookings: StateFlow<List<BookingDto>> = _recentBookings.asStateFlow()

    private val _allBookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val allBookings: StateFlow<List<BookingDto>> = _allBookings.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    init {
        loadDashboard()
    }

    fun loadDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.fetchProviderHome().onSuccess { response ->
                _earnings.value = "UGX ${response.total_earnings}"
                _balance.value = "UGX ${response.balance}"
                _completedBookings.value = response.completed_bookings
                _pendingBookings.value = response.pending_bookings
                _rating.value = response.rating
                _recentBookings.value = response.recent_bookings
            }.onFailure { e ->
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun loadBookings() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            repository.fetchProviderBookings().onSuccess { bookings ->
                _allBookings.value = bookings
            }.onFailure { e ->
                _error.value = e.message
            }
            _isLoading.value = false
        }
    }

    fun acceptBooking(id: Int) {
        viewModelScope.launch {
            repository.acceptBooking(id).onSuccess {
                loadDashboard()
                loadBookings()
            }
        }
    }

    fun rejectBooking(id: Int) {
        viewModelScope.launch {
            repository.rejectBooking(id).onSuccess {
                loadDashboard()
                loadBookings()
            }
        }
    }

    fun startBooking(id: Int) {
        viewModelScope.launch {
            repository.startBooking(id).onSuccess {
                loadDashboard()
                loadBookings()
            }
        }
    }

    fun completeBooking(id: Int) {
        viewModelScope.launch {
            repository.completeBooking(id).onSuccess {
                loadDashboard()
                loadBookings()
            }
        }
    }

    // Setup State
    private val _selectedCategory = MutableStateFlow("")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _hourlyRate = MutableStateFlow("")
    val hourlyRate: StateFlow<String> = _hourlyRate.asStateFlow()

    private val _bio = MutableStateFlow("")
    val bio: StateFlow<String> = _bio.asStateFlow()

    private val _providerLocation = MutableStateFlow(LatLng(0.3476, 32.5825))
    val providerLocation: StateFlow<LatLng> = _providerLocation.asStateFlow()

    private val _locationName = MutableStateFlow("Fetching location...")
    val locationName: StateFlow<String> = _locationName.asStateFlow()

    fun onCategorySelected(category: String) {
        _selectedCategory.value = category
    }

    fun onHourlyRateChanged(rate: String) {
        _hourlyRate.value = rate
    }

    fun onBioChanged(newBio: String) {
        _bio.value = newBio
    }

    fun onLocationSelected(latLng: LatLng, name: String) {
        _providerLocation.value = latLng
        _locationName.value = name
    }

    fun completeSetup(onComplete: () -> Unit) {
        viewModelScope.launch {
            // Mock API call
            onComplete()
        }
    }
}

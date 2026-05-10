package com.example.ugawaka.data.repository

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.Color
import com.example.ugawaka.data.model.BookingRequest
import com.example.ugawaka.data.model.ServiceCategory
import com.example.ugawaka.data.remote.BookingCreateRequest
import com.example.ugawaka.data.remote.BookingDto
import com.example.ugawaka.data.remote.BookingResponse
import com.example.ugawaka.data.remote.ProviderDetailResponse
import com.example.ugawaka.data.remote.ProviderDto
import com.example.ugawaka.data.remote.ProviderHomeResponse
import com.example.ugawaka.data.remote.RetrofitClient
import com.example.ugawaka.data.remote.ServiceDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext

class UgaWakaRepository {
    private val _userLocation = MutableStateFlow<com.google.android.gms.maps.model.LatLng?>(null)
    val userLocation: StateFlow<com.google.android.gms.maps.model.LatLng?> = _userLocation.asStateFlow()

    fun setUserLocation(location: com.google.android.gms.maps.model.LatLng) {
        _userLocation.value = location
    }

    private val _services = MutableStateFlow<List<ServiceDto>>(emptyList())
    val services: StateFlow<List<ServiceDto>> = _services.asStateFlow()

    private val _selectedService = MutableStateFlow<ServiceDto?>(null)
    val selectedService: StateFlow<ServiceDto?> = _selectedService.asStateFlow()

    private val _selectedProvider = MutableStateFlow<ProviderDto?>(null)
    val selectedProvider: StateFlow<ProviderDto?> = _selectedProvider.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    suspend fun fetchServices() {
        _isLoading.value = true
        _error.value = null
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getServices()
                if (response.isSuccessful) {
                    _services.value = response.body()?.services ?: emptyList()
                } else {
                    _error.value = "Failed to fetch services. Please try again."
                }
            } catch (e: Exception) {
                _error.value = "Connection error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun fetchServiceDetail(id: Int) {
        _isLoading.value = true
        _error.value = null
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getServiceDetail(id)
                if (response.isSuccessful) {
                    _selectedService.value = response.body()?.service
                } else {
                    _error.value = "Failed to fetch service details. Please try again."
                }
            } catch (e: Exception) {
                _error.value = "Connection error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun fetchProviderDetail(id: Int) {
        _isLoading.value = true
        _error.value = null
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getProviderDetail(id)
                if (response.isSuccessful) {
                    _selectedProvider.value = response.body()?.profile ?: response.body()?.provider
                } else {
                    _error.value = "Failed to fetch provider details. Please try again."
                }
            } catch (e: Exception) {
                _error.value = "Connection error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun fetchMyProviderProfile() {
        _isLoading.value = true
        _error.value = null
        withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getMyProviderProfile()
                if (response.isSuccessful) {
                    _selectedProvider.value = response.body()?.profile ?: response.body()?.provider
                } else {
                    _error.value = "Failed to fetch your profile. Please try again."
                }
            } catch (e: Exception) {
                _error.value = "Connection error: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    suspend fun createBooking(request: BookingCreateRequest): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.createBooking(request)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    val errorJson = response.errorBody()?.string()
                    val errorMessage = try {
                        val errorResponse = com.google.gson.Gson().fromJson(errorJson, com.example.ugawaka.data.remote.LoginResponse::class.java)
                        val errors = errorResponse.errors
                        if (errors != null && errors.isNotEmpty()) {
                            errors.values.flatten().joinToString("\n")
                        } else {
                            errorResponse.message ?: "Failed to create booking"
                        }
                    } catch (e: Exception) {
                        "Failed to create booking. Please try again."
                    }
                    Result.failure(Exception(errorMessage))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchBookings(): Result<List<BookingDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getBookings()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.bookings)
                } else {
                    Result.failure(Exception("Failed to fetch bookings"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun cancelBooking(id: Int): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.cancelBooking(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to cancel booking"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getBookingDetail(id: Int): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getBookingDetail(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch booking detail"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchProviderHome(): Result<ProviderHomeResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getProviderHome()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to fetch dashboard data"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun fetchProviderBookings(): Result<List<BookingDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.getProviderBookings()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.bookings)
                } else {
                    Result.failure(Exception("Failed to fetch provider bookings"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun acceptBooking(id: Int): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.acceptBooking(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to accept booking"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun rejectBooking(id: Int): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.rejectBooking(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to reject booking"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun startBooking(id: Int): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.startBooking(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to start booking"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun completeBooking(id: Int): Result<BookingResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = RetrofitClient.authService.completeBooking(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to complete booking"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    private val _allCategories = listOf(
        ServiceCategory("Plumbing", Icons.Default.WaterDrop, Color(0xFFE3F2FD)),
        ServiceCategory("Electrical", Icons.Default.ElectricBolt, Color(0xFFFFF9C4)),
        ServiceCategory("Cleaning", Icons.Default.AutoAwesome, Color(0xFFE8F5E9)),
        ServiceCategory("Mechanics", Icons.Default.DirectionsCar, Color(0xFFFFEBEE)),
        ServiceCategory("Carpentry", Icons.Default.Handyman, Color(0xFFF3E5F5)),
        ServiceCategory("Painting", Icons.Default.FormatPaint, Color(0xFFE0F2F1)),
        ServiceCategory("Gardening", Icons.Default.Park, Color(0xFFF1F8E9)),
        ServiceCategory("Laundry", Icons.Default.LocalLaundryService, Color(0xFFE1F5FE))
    )

    private val _bookings = MutableStateFlow(listOf(
        BookingRequest("1", "Sarah Nakato", "Plumbing Repair", "Today, 2:00 PM", "Nakasero, Plot 45", "UGX 35,000"),
        BookingRequest("2", "James Okello", "Pipe Installation", "Tomorrow, 10:00 AM", "Kololo, Hill Lane", "UGX 120,000"),
        BookingRequest("3", "Grace Akello", "Leaking Tap", "Wed, 15 Oct", "Ntinda, Stage 2", "UGX 25,000")
    ))

    fun getCategories() = _allCategories
    
    fun getBookings(): StateFlow<List<BookingRequest>> = _bookings.asStateFlow()

    fun updateBookingStatus(id: String, newStatus: String) {
        val currentList = _bookings.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }
        if (index != -1) {
            currentList[index] = currentList[index].copy(status = newStatus)
            _bookings.value = currentList
        }
    }
}

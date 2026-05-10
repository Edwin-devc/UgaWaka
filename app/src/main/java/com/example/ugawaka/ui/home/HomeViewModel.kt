package com.example.ugawaka.ui.home

import androidx.lifecycle.ViewModel
import com.example.ugawaka.data.model.ServiceCategory
import com.example.ugawaka.data.repository.UgaWakaRepository

import androidx.lifecycle.viewModelScope
import com.example.ugawaka.data.remote.RetrofitClient
import com.example.ugawaka.data.remote.ServiceDto
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repository: UgaWakaRepository = UgaWakaRepository()) : ViewModel() {
    val userName: String = RetrofitClient.userName
    val services: StateFlow<List<ServiceDto>> = repository.services
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

    fun updateUserLocation(location: LatLng) {
        repository.setUserLocation(location)
    }

    init {
        viewModelScope.launch {
            repository.fetchServices()
        }
    }

    fun retryFetch() {
        viewModelScope.launch {
            repository.fetchServices()
        }
    }

    // Keep categories for now as fallback or UI placeholders if needed
    val featuredServices: List<ServiceCategory> = repository.getCategories().take(4)
}

package com.example.ugawaka.ui.services

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugawaka.data.remote.ServiceDto
import com.example.ugawaka.data.repository.UgaWakaRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ServiceDetailViewModel(private val repository: UgaWakaRepository = UgaWakaRepository()) : ViewModel() {
    val serviceDetail: StateFlow<ServiceDto?> = repository.selectedService
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error
    val userLocation: StateFlow<LatLng?> = repository.userLocation

    fun fetchServiceDetail(id: Int) {
        viewModelScope.launch {
            repository.fetchServiceDetail(id)
        }
    }
}

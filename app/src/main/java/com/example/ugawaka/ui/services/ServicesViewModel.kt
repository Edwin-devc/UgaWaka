package com.example.ugawaka.ui.services

import androidx.lifecycle.ViewModel
import com.example.ugawaka.data.model.ServiceCategory
import com.example.ugawaka.data.repository.UgaWakaRepository

import androidx.lifecycle.viewModelScope
import com.example.ugawaka.data.remote.RetrofitClient
import com.example.ugawaka.data.remote.ServiceDto
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ServicesViewModel(private val repository: UgaWakaRepository = UgaWakaRepository()) : ViewModel() {
    val userName: String = RetrofitClient.userName
    val services: StateFlow<List<ServiceDto>> = repository.services
    val isLoading: StateFlow<Boolean> = repository.isLoading
    val error: StateFlow<String?> = repository.error

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

    val allCategories: List<ServiceCategory> = repository.getCategories()
}

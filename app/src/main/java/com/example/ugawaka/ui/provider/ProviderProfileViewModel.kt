package com.example.ugawaka.ui.provider

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ugawaka.data.remote.ProviderDto
import com.example.ugawaka.data.repository.UgaWakaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProviderProfileUiState(
    val provider: ProviderDto? = null,
    val userLocation: com.google.android.gms.maps.model.LatLng? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProviderProfileViewModel(private val repository: UgaWakaRepository = UgaWakaRepository()) : ViewModel() {
    private val _uiState = MutableStateFlow(ProviderProfileUiState())
    val uiState: StateFlow<ProviderProfileUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            repository.selectedProvider.collect { provider ->
                _uiState.value = _uiState.value.copy(provider = provider)
            }
        }
        viewModelScope.launch {
            repository.userLocation.collect { location ->
                _uiState.value = _uiState.value.copy(userLocation = location)
            }
        }
        viewModelScope.launch {
            repository.isLoading.collect { isLoading ->
                _uiState.value = _uiState.value.copy(isLoading = isLoading)
            }
        }
        viewModelScope.launch {
            repository.error.collect { error ->
                _uiState.value = _uiState.value.copy(error = error)
            }
        }
    }

    fun loadProvider(id: Int) {
        viewModelScope.launch {
            repository.fetchProviderDetail(id)
        }
    }

    fun loadMyProfile() {
        viewModelScope.launch {
            repository.fetchMyProviderProfile()
        }
    }
}

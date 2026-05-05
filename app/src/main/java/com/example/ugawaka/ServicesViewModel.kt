package com.example.ugawaka

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID
import java.util.Date
import java.text.SimpleDateFormat
import java.util.Locale

class ServicesViewModel(
    private val repository: IProviderRepository
) : ViewModel() {
    
    var uiState by mutableStateOf(ServicesUiState(categories = repository.getCategories()))
        private set
        
    var searchQuery by mutableStateOf("")
        private set

    private var allProviders = listOf<Provider>()

    init {
        viewModelScope.launch {
            repository.initializeData()
            repository.getAllProviders().collectLatest { providers ->
                allProviders = providers
                updateFilteredProviders()
            }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        searchQuery = newQuery
        updateFilteredProviders()
    }

    private fun updateFilteredProviders() {
        val filtered = if (searchQuery.isEmpty()) {
            allProviders
        } else {
            allProviders.filter {
                it.name.contains(searchQuery, ignoreCase = true) || 
                it.categoryName.contains(searchQuery, ignoreCase = true)
            }
        }
        uiState = uiState.copy(providers = filtered)
    }

    fun getProvidersForCategory(categoryName: String): List<Provider> {
        return allProviders.filter { it.categoryName.equals(categoryName, ignoreCase = true) }
    }

    fun getProviderByName(name: String): Provider? {
        return allProviders.find { it.name == name }
    }

    fun getReviewsForProvider(providerId: String) = repository.getReviewsForProvider(providerId)

    fun bookService(provider: Provider) {
        uiState = uiState.copy(isLoading = true)
        
        viewModelScope.launch {
            // Create a new booking entry
            val newBooking = Booking(
                id = UUID.randomUUID().toString(),
                providerId = provider.id,
                userId = "current_user_id", // In a real app, this would be the logged-in user's ID
                serviceName = provider.categoryName,
                status = "Pending",
                date = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()).format(Date())
            )
            
            // Save to database
            repository.insertBooking(newBooking)
            
            kotlinx.coroutines.delay(800) // Simulate network/processing
            uiState = uiState.copy(
                isLoading = false,
                bookingStatus = "Booking confirmed with ${provider.name}!"
            )
        }
    }

    fun clearBookingStatus() {
        uiState = uiState.copy(bookingStatus = null)
    }
}

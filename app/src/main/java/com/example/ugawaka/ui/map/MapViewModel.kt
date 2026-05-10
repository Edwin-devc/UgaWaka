package com.example.ugawaka.ui.map

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng

data class MapUiState(
    val providerLocation: LatLng = LatLng(0.3550, 32.5900),
    val userLocation: LatLng = LatLng(0.3400, 32.5700),
    val providerName: String = "John Katumba",
    val serviceType: String = "Plumber",
    val etaMinutes: Int = 5
)

class MapViewModel : ViewModel() {
    var uiState by mutableStateOf(MapUiState())
        private set

    // In a real app, we would observe location updates here
}

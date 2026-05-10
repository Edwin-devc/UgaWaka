package com.example.ugawaka.data.model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.google.android.gms.maps.model.LatLng

data class ServiceCategory(
    val name: String,
    val icon: ImageVector,
    val color: Color
)

data class BookingRequest(
    val id: String,
    val clientName: String,
    val service: String,
    val date: String,
    val location: String,
    val amount: String,
    val status: String = "Pending"
)

data class UserProfile(
    val name: String,
    val email: String = "",
    val role: String,
    val location: String,
    val profileImage: Int? = null
)

data class Provider(
    val name: String,
    val rating: Double,
    val distance: String,
    val price: String
)

data class Booking(
    val id: String,
    val providerName: String,
    val serviceName: String,
    val date: String,
    val status: String,
    val amount: String,
    val location: String = ""
)

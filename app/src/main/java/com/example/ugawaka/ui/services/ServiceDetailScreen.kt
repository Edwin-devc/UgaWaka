package com.example.ugawaka.ui.services

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ugawaka.data.model.Provider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.example.ugawaka.R
import com.example.ugawaka.UgaWakaBottomNavigation
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

// Provider data class moved to Models.kt

import androidx.compose.runtime.LaunchedEffect
import com.example.ugawaka.data.remote.ProviderDto
import com.example.ugawaka.data.remote.ServiceDto
import com.google.android.gms.maps.model.LatLng
import kotlin.math.*
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.FormatPaint
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.LocalLaundryService

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceId: Int,
    onBack: () -> Unit,
    onProviderClick: (Int) -> Unit,
    onBookClick: (Int) -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: ServiceDetailViewModel = viewModel()
) {
    val serviceDetail by viewModel.serviceDetail.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()

    LaunchedEffect(serviceId) {
        viewModel.fetchServiceDetail(serviceId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = serviceDetail?.name ?: "Service Details", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = { UgaWakaBottomNavigation(currentScreen = "services", onNavigate = onNavigate) }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = UgaGreen)
                }
            }
            error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = error!!, color = Color.Red)
                    Button(onClick = { viewModel.fetchServiceDetail(serviceId) }, colors = ButtonDefaults.buttonColors(containerColor = UgaGreen)) {
                        Text("Retry")
                    }
                }
            }
            serviceDetail != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFF8FBF9))
                ) {
                    item {
                        ServiceDescription(serviceDetail!!)
                    }
                    item {
                        Text(
                            text = "Available Providers",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                    val providers = serviceDetail!!.providers ?: emptyList()
                    if (providers.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text(text = "No providers available for this service yet.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(providers) { provider ->
                            ProviderCard(
                                provider = provider,
                                userLocation = userLocation,
                                onClick = { onProviderClick(provider.id) },
                                onBookClick = { onBookClick(provider.id) }
                            )
                        }
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun ServiceDescription(service: ServiceDto) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "About ${service.name} Service",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UgaGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = service.description ?: "Professional ${service.name} services tailored for your needs in Kampala. Our verified experts handle everything from emergency repairs to routine maintenance with quality guaranteed.",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ProviderCard(provider: ProviderDto, userLocation: LatLng?, onClick: () -> Unit, onBookClick: () -> Unit) {
    val distanceText = remember(provider, userLocation) {
        if (userLocation != null && provider.latitude != null && provider.longitude != null) {
            val distance = calculateDistance(
                userLocation.latitude, userLocation.longitude,
                provider.latitude, provider.longitude
            )
            if (distance < 1.0) {
                "${(distance * 1000).toInt()}m away"
            } else {
                "${"%.1f".format(distance)}km away"
            }
        } else {
            "Nearby"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ugawaka_logo),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = UgaGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = provider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Text(text = " 4.5", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = distanceText, fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = "UGX ${provider.rate ?: "N/A"}", fontSize = 14.sp, color = UgaGreen, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onBookClick,
                colors = ButtonDefaults.buttonColors(containerColor = UgaGreen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("Book", fontSize = 14.sp)
            }
        }
    }
}

fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val r = 6371 // Earth radius in km
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = sin(dLat / 2) * sin(dLat / 2) +
            cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
            sin(dLon / 2) * sin(dLon / 2)
    val c = 2 * atan2(sqrt(a), sqrt(1 - a))
    return r * c
}

@Preview(showBackground = true)
@Composable
fun ServiceDetailScreenPreview() {
    UgaWakaTheme {
        ServiceDetailScreen(
            serviceId = 1,
            onBack = {},
            onProviderClick = {},
            onBookClick = {},
            onNavigate = {}
        )
    }
}

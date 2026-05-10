package com.example.ugawaka.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ugawaka.R
import com.example.ugawaka.UgaWakaBottomNavigation
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

import androidx.compose.runtime.remember
import com.example.ugawaka.ui.services.calculateDistance
import com.google.android.gms.maps.model.LatLng

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderProfileScreen(
    providerId: Int,
    onBack: () -> Unit,
    onBook: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit = {},
    isOwnProfile: Boolean = false,
    viewModel: ProviderProfileViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val distanceText = remember(uiState.provider, uiState.userLocation) {
        val provider = uiState.provider
        val userLocation = uiState.userLocation
        if (userLocation != null && provider?.latitude != null && provider.longitude != null) {
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

    LaunchedEffect(providerId, isOwnProfile) {
        if (isOwnProfile) {
            viewModel.loadMyProfile()
        } else if (providerId > 0) {
            viewModel.loadProvider(providerId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOwnProfile) "My Profile" else "Provider Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOwnProfile) {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Column {
                if (!isOwnProfile) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        Button(
                            onClick = onBook,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UgaGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Book Now", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
                        }
                    }
                }
                UgaWakaBottomNavigation(
                    currentScreen = if (isOwnProfile) "profile" else "services",
                    isProvider = isOwnProfile,
                    onNavigate = onNavigate
                )
            }
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = UgaGreen)
                }
            }
            uiState.error != null -> {
                Column(
                    modifier = Modifier.fillMaxSize().padding(paddingValues).padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(text = uiState.error!!, color = Color.Red)
                    Button(onClick = { viewModel.loadProvider(providerId) }, colors = ButtonDefaults.buttonColors(containerColor = UgaGreen)) {
                        Text("Retry")
                    }
                }
            }
            uiState.provider != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .background(Color(0xFFF8FBF9))
                ) {
                    item {
                        ProviderHeader(
                            uiState.provider?.name ?: "Unknown",
                            if (isOwnProfile) "Account Status: ${uiState.provider?.status?.replaceFirstChar { it.uppercase() } ?: "Pending"}" 
                            else uiState.provider?.service_name ?: "Professional Provider",
                            distanceText
                        )
                    }
                    
                    if (isOwnProfile) {
                        item {
                            Card(
                                modifier = Modifier.padding(16.dp),
                                shape = RoundedCornerShape(16.dp),
                                colors = CardDefaults.cardColors(containerColor = Color.White)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text("Account Details", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                                    Spacer(modifier = Modifier.height(16.dp))
                                    ProfileDetailItem("Email", uiState.provider?.email ?: "", Icons.Default.Email)
                                    ProfileDetailItem("Base Rate", "UGX ${uiState.provider?.rate ?: "0"} / hr", Icons.Default.Payments)
                                    ProfileDetailItem("Service Category", uiState.provider?.service_name ?: "N/A", Icons.Default.Work)
                                    ProfileDetailItem("Phone", uiState.provider?.phone_number ?: "N/A", Icons.Default.Phone)
                                }
                            }
                        }
                    } else {
                        item {
                            ProviderStats("4.9", "120+", "5 Yrs", "UGX ${uiState.provider?.rate ?: "0"}")
                        }
                    }

                    item {
                        ProviderBio(uiState.provider?.about ?: "No bio available.")
                    }
                    
                    if (!isOwnProfile) {
                        item {
                            ReviewsSection()
                        }
                    }
                }
            }
            else -> {
                Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                    Text("No provider data found")
                }
            }
        }
    }
}

@Composable
fun ProviderHeader(name: String, specialty: String, location: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ugawaka_logo),
                contentDescription = null,
                modifier = Modifier.padding(20.dp),
                tint = UgaGreen
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = specialty, color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = UgaGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = location, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProviderStats(rating: String, jobs: String, experience: String, rate: String? = null) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "Rating", value = rating, icon = Icons.Default.Star, iconColor = Color(0xFFFFB300))
        StatItem(label = "Jobs", value = jobs, icon = Icons.Default.Work, iconColor = UgaGreen)
        if (rate != null) {
            StatItem(label = "Rate/hr", value = rate, icon = Icons.Default.Payments, iconColor = UgaGreen)
        } else {
            StatItem(label = "Exp.", value = experience, icon = Icons.Default.History, iconColor = Color.Blue)
        }
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = iconColor)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ProviderBio(bio: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "About", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = bio,
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ReviewsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Reviews", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "See All", color = UgaGreen, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Sample Review
        ReviewItem("Nathan K.", 5, "Excellent work! Arrived on time and fixed the leak quickly.")
        ReviewItem("Sarah M.", 4, "Very professional and knew exactly what they were doing.")
    }
}

@Composable
fun ReviewItem(reviewer: String, rating: Int, comment: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color.LightGray) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = reviewer, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    repeat(rating) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProfileDetailItem(label: String, value: String, icon: ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = UgaGreen, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(text = value, fontSize = 16.sp, fontWeight = FontWeight.Medium)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProviderProfileScreenPreview() {
    UgaWakaTheme {
        ProviderProfileScreen(providerId = 1, onBack = {}, onBook = {}, onNavigate = {}, onLogout = {})
    }
}

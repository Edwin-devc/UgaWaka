package com.example.ugawaka.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import java.time.LocalTime
import com.example.ugawaka.UgaWakaBottomNavigation
import com.example.ugawaka.data.remote.BookingDto
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigate: (String) -> Unit,
    viewModel: ProviderViewModel = viewModel()
) {
    val earnings by viewModel.earnings.collectAsState()
    val balance by viewModel.balance.collectAsState()
    val completedCount by viewModel.completedBookings.collectAsState()
    val pendingCount by viewModel.pendingBookings.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val recentBookings by viewModel.recentBookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    val greeting = remember {
        val hour = LocalTime.now().hour
        when (hour) {
            in 0..11 -> "Good morning"
            in 12..16 -> "Good afternoon"
            else -> "Good evening"
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Provider Dashboard") },
                actions = {
                    IconButton(onClick = onProfileClick) {
                        Icon(Icons.Default.AccountCircle, contentDescription = "Profile")
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.Default.Logout, contentDescription = "Logout")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            UgaWakaBottomNavigation(currentScreen = "home", isProvider = true, onNavigate = onNavigate)
        }
    ) { paddingValues ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize().padding(paddingValues), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = UgaGreen)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FBF9))
                    .padding(16.dp)
            ) {
                item {
                    Text(
                        text = "$greeting!",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }

                item {
                    EarningsOverview(
                        totalEarnings = earnings,
                        balance = balance,
                        completedCount = completedCount.toString(),
                        pendingCount = pendingCount.toString(),
                        rating = rating
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                }

                item {
                    Text(
                        text = "Recent Booking Requests",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }

                items(recentBookings) { booking ->
                    BookingRequestCard(
                        booking = booking,
                        onAccept = { viewModel.acceptBooking(booking.id) },
                        onReject = { viewModel.rejectBooking(booking.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun EarningsOverview(
    totalEarnings: String,
    balance: String,
    completedCount: String,
    pendingCount: String,
    rating: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = UgaGreen)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text("Total Earnings", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text(totalEarnings, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Available Balance", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Text(balance, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
                }
            }
            Spacer(modifier = Modifier.height(20.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EarningStat("Completed", completedCount, Icons.Default.CheckCircle)
                EarningStat("Pending", pendingCount, Icons.Default.Pending)
                EarningStat("Rating", rating, Icons.Default.Star)
            }
        }
    }
}

@Composable
fun EarningStat(label: String, value: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
        Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(label, color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
    }
}

@Composable
fun BookingRequestCard(
    booking: BookingDto,
    onAccept: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(40.dp), shape = CircleShape, color = Color(0xFFE8F5E9)) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = UgaGreen, modifier = Modifier.padding(8.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Customer ID: ${booking.customer_id}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(booking.service?.name ?: "General Service", color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                // Text(booking.amount, fontWeight = FontWeight.Bold, color = UgaGreen)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(booking.scheduled_on.substringBefore('T'), fontSize = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(booking.location_name ?: "Location Coordinates available in details", fontSize = 13.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (booking.status == "pending") {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = onReject,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Decline")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = onAccept,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UgaGreen)
                    ) {
                        Text("Accept")
                    }
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = if (booking.status == "accepted" || booking.status == "in_progress" || booking.status == "completed") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = booking.status.replaceFirstChar { it.uppercase() },
                        modifier = Modifier.padding(8.dp),
                        color = if (booking.status == "accepted" || booking.status == "in_progress" || booking.status == "completed") UgaGreen else Color.Red,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProviderDashboardScreenPreview() {
    UgaWakaTheme {
        ProviderDashboardScreen(onLogout = {}, onProfileClick = {}, onNavigate = {})
    }
}

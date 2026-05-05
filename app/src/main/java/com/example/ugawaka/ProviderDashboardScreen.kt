package com.example.ugawaka

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
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderDashboardScreen(
    onLogout: () -> Unit,
    onProfileClick: () -> Unit,
    onNavigate: (String) -> Unit
) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FBF9))
                .padding(16.dp)
        ) {
            item {
                Text(
                    text = "Welcome back, John!",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            item {
                EarningsOverview()
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

            items(sampleBookings) { booking ->
                BookingRequestCard(booking)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun EarningsOverview() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = UgaGreen)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text("Total Earnings", color = Color.White.copy(alpha = 0.8f), fontSize = 14.sp)
            Text("UGX 450,000", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(16.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                EarningStat("Completed", "12", Icons.Default.CheckCircle)
                EarningStat("Pending", "3", Icons.Default.Pending)
                EarningStat("Rating", "4.9", Icons.Default.Star)
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

data class BookingRequest(
    val id: String,
    val clientName: String,
    val service: String,
    val date: String,
    val location: String,
    val amount: String
)

val sampleBookings = listOf(
    BookingRequest("1", "Sarah Nakato", "Plumbing Repair", "Today, 2:00 PM", "Nakasero, Plot 45", "UGX 35,000"),
    BookingRequest("2", "James Okello", "Pipe Installation", "Tomorrow, 10:00 AM", "Kololo, Hill Lane", "UGX 120,000"),
    BookingRequest("3", "Grace Akello", "Leaking Tap", "Wed, 15 Oct", "Ntinda, Stage 2", "UGX 25,000")
)

@Composable
fun BookingRequestCard(booking: BookingRequest) {
    var status by remember { mutableStateOf("Pending") }

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
                    Text(booking.clientName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(booking.service, color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(booking.amount, fontWeight = FontWeight.Bold, color = UgaGreen)
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CalendarToday, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(booking.date, fontSize = 13.sp, color = Color.DarkGray)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(booking.location, fontSize = 13.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (status == "Pending") {
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { status = "Declined" },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red)
                    ) {
                        Text("Decline")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = { status = "Accepted" },
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
                    color = if (status == "Accepted") Color(0xFFE8F5E9) else Color(0xFFFFEBEE)
                ) {
                    Text(
                        text = status,
                        modifier = Modifier.padding(8.dp),
                        color = if (status == "Accepted") UgaGreen else Color.Red,
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

package com.example.ugawaka.ui.provider

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ugawaka.data.remote.BookingDto
import com.example.ugawaka.UgaWakaBottomNavigation
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderBookingsScreen(
    onNavigate: (String) -> Unit,
    viewModel: ProviderViewModel = viewModel()
) {
    val jobs by viewModel.allBookings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadBookings()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Jobs", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            UgaWakaBottomNavigation(currentScreen = "bookings", isProvider = true, onNavigate = onNavigate)
        }
    ) { paddingValues ->
        if (isLoading && jobs.isEmpty()) {
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
                items(jobs) { job ->
                    ProviderJobItemCard(
                        job = job,
                        onAccept = { viewModel.acceptBooking(job.id) },
                        onReject = { viewModel.rejectBooking(job.id) },
                        onStart = { viewModel.startBooking(job.id) },
                        onComplete = { viewModel.completeBooking(job.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun ProviderJobItemCard(
    job: BookingDto,
    onAccept: () -> Unit,
    onReject: () -> Unit,
    onStart: () -> Unit,
    onComplete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    modifier = Modifier.size(40.dp),
                    shape = CircleShape,
                    color = Color(0xFFE8F5E9)
                ) {
                    Icon(
                        Icons.Default.Person,
                        contentDescription = null,
                        tint = UgaGreen,
                        modifier = Modifier.padding(8.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text("Customer ID: ${job.customer_id}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(job.service?.name ?: "General Service", color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                ProviderStatusBadge(job.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(job.scheduled_on.substringBefore('T'), fontSize = 13.sp, color = Color.DarkGray)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    Icons.Default.LocationOn,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(job.location_name ?: "Location Coordinates", fontSize = 13.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F1F1))
            Spacer(modifier = Modifier.height(12.dp))

            if (job.status == "pending") {
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
            } else if (job.status == "accepted") {
                Button(
                    onClick = onStart,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UgaGreen)
                ) {
                    Text("Start Work")
                }
            } else if (job.status == "in_progress") {
                Button(
                    onClick = onComplete,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UgaGreen)
                ) {
                    Text("Complete Work")
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Status", fontSize = 13.sp, color = Color.Gray)
                    Text(job.status.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
                }
            }
        }
    }
}

@Composable
fun ProviderStatusBadge(status: String) {
    val backgroundColor = when (status) {
        "completed" -> Color(0xFFE8F5E9)
        "pending" -> Color(0xFFFFF8E1)
        "accepted" -> Color(0xFFE3F2FD)
        "in_progress" -> Color(0xFFE1F5FE)
        "rejected" -> Color(0xFFFFEBEE)
        else -> Color.LightGray
    }
    val contentColor = when (status) {
        "completed" -> Color(0xFF2E7D32)
        "pending" -> Color(0xFFF57F17)
        "accepted" -> Color(0xFF1976D2)
        "in_progress" -> Color(0xFF0288D1)
        "rejected" -> Color(0xFFC62828)
        else -> Color.DarkGray
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status.replaceFirstChar { it.uppercase() },
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            color = contentColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ProviderBookingsScreenPreview() {
    UgaWakaTheme {
        ProviderBookingsScreen(onNavigate = {})
    }
}

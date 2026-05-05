package com.example.ugawaka

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
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

data class ProviderJob(
    val id: String,
    val clientName: String,
    val serviceName: String,
    val date: String,
    val status: String,
    val amount: String,
    val location: String
)

val sampleProviderJobs = listOf(
    ProviderJob("1", "Nathan Kayeera", "Plumbing", "Oct 12, 2023 - 10:00 AM", "Completed", "UGX 35,000", "Nakasero, Plot 45"),
    ProviderJob("2", "Sarah Nakato", "Plumbing Repair", "Today, 2:00 PM", "Pending", "UGX 35,000", "Kololo, Hill Lane"),
    ProviderJob("3", "James Okello", "Pipe Installation", "Tomorrow, 10:00 AM", "Accepted", "UGX 120,000", "Ntinda, Stage 2")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderBookingsScreen(
    onNavigate: (String) -> Unit
) {
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FBF9))
                .padding(16.dp)
        ) {
            items(sampleProviderJobs) { job ->
                ProviderJobItemCard(job)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun ProviderJobItemCard(job: ProviderJob) {
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
                    Text(job.clientName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(job.serviceName, color = Color.Gray, fontSize = 14.sp)
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
                Text(job.date, fontSize = 13.sp, color = Color.DarkGray)
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
                Text(job.location, fontSize = 13.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F1F1))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Earnings", fontSize = 13.sp, color = Color.Gray)
                Text(job.amount, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun ProviderStatusBadge(status: String) {
    val backgroundColor = when (status) {
        "Completed" -> Color(0xFFE8F5E9)
        "Pending" -> Color(0xFFFFF8E1)
        "Accepted" -> Color(0xFFE3F2FD)
        "Cancelled" -> Color(0xFFFFEBEE)
        else -> Color.LightGray
    }
    val contentColor = when (status) {
        "Completed" -> Color(0xFF2E7D32)
        "Pending" -> Color(0xFFF57F17)
        "Accepted" -> Color(0xFF1976D2)
        "Cancelled" -> Color(0xFFC62828)
        else -> Color.DarkGray
    }

    Surface(
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = status,
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

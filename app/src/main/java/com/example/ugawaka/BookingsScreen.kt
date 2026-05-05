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

data class Booking(
    val id: String,
    val providerName: String,
    val serviceName: String,
    val date: String,
    val status: String,
    val amount: String
)

val sampleClientBookings = listOf(
    Booking("1", "John Katumba", "Plumbing", "Oct 12, 2023 - 10:00 AM", "Completed", "UGX 35,000"),
    Booking("2", "Sarah Namukasa", "Laundry", "Oct 15, 2023 - 2:00 PM", "Pending", "UGX 20,000"),
    Booking("3", "Mike Mukasa", "Electrical", "Oct 20, 2023 - 9:00 AM", "Cancelled", "UGX 50,000")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsScreen(
    onNavigate: (String) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        },
        bottomBar = {
            UgaWakaBottomNavigation(currentScreen = "bookings", onNavigate = onNavigate)
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FBF9))
                .padding(16.dp)
        ) {
            items(sampleClientBookings) { booking ->
                BookingItemCard(booking)
                Spacer(modifier = Modifier.height(12.dp))
            }
        }
    }
}

@Composable
fun BookingItemCard(booking: Booking) {
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
                    Text(booking.providerName, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(booking.serviceName, color = Color.Gray, fontSize = 14.sp)
                }
                Spacer(modifier = Modifier.weight(1f))
                StatusBadge(booking.status)
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
                Text(booking.date, fontSize = 13.sp, color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFFF1F1F1))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Total Amount", fontSize = 13.sp, color = Color.Gray)
                Text(booking.amount, fontWeight = FontWeight.Bold, color = Color.Black, fontSize = 15.sp)
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val backgroundColor = when (status) {
        "Completed" -> Color(0xFFE8F5E9)
        "Pending" -> Color(0xFFFFF8E1)
        "Cancelled" -> Color(0xFFFFEBEE)
        else -> Color.LightGray
    }
    val contentColor = when (status) {
        "Completed" -> Color(0xFF2E7D32)
        "Pending" -> Color(0xFFF57F17)
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
fun BookingsScreenPreview() {
    UgaWakaTheme {
        BookingsScreen(onNavigate = {})
    }
}

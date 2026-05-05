package com.example.ugawaka

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    providerName: String,
    serviceName: String,
    onBack: () -> Unit,
    onBookingConfirm: () -> Unit
) {
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    var notes by remember { mutableStateOf("") }
    var showSuccessDialog by remember { mutableStateOf(false) }

    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { 
                showSuccessDialog = false
                onBookingConfirm()
            },
            confirmButton = {
                TextButton(onClick = { 
                    showSuccessDialog = false
                    onBookingConfirm()
                }) {
                    Text("OK", color = UgaGreen)
                }
            },
            title = { Text("Booking Successful!") },
            text = { Text("Your appointment with $providerName for $serviceName has been scheduled for ${selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))}.") },
            shape = RoundedCornerShape(16.dp),
            containerColor = Color.White
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Book Service") },
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
        bottomBar = {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shadowElevation = 8.dp,
                color = Color.White
            ) {
                Button(
                    onClick = { showSuccessDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UgaGreen),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Confirm Booking", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF8FBF9))
                .padding(20.dp)
        ) {
            // Provider Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier.size(50.dp),
                        shape = CircleShape,
                        color = Color(0xFFE8F5E9)
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = UgaGreen,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(text = providerName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text(text = serviceName, color = Color.Gray, fontSize = 14.sp)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Select Date
            Text(text = "Select Date", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            
            val dates = (0..6).map { LocalDate.now().plusDays(it.toLong()) }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(dates) { date ->
                    DateChip(
                        date = date,
                        isSelected = selectedDate == date,
                        onClick = { selectedDate = date }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Location Section
            Text(text = "Service Location", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.LocationOn, contentDescription = null, tint = UgaGreen)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(text = "Home Address", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(text = "Plot 45, Kampala Road, Nakasero", color = Color.Gray, fontSize = 13.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Text(text = "Edit", color = UgaGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notes Section
            Text(text = "Additional Notes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("E.g. The gate code is 1234...", fontSize = 14.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UgaGreen,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Payment Method
            Text(text = "Payment Method", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            var selectedPayment by remember { mutableStateOf("Cash") }
            val paymentMethods = listOf(
                "Cash" to Icons.Default.Payments,
                "Mobile Money" to Icons.Default.Smartphone
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                paymentMethods.forEach { (method, icon) ->
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedPayment = method },
                        shape = RoundedCornerShape(12.dp),
                        color = if (selectedPayment == method) Color(0xFFE8F5E9) else Color.White,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (selectedPayment == method) UgaGreen else Color.LightGray.copy(alpha = 0.5f)
                        )
                    ) {
                        Row(
                            modifier = Modifier.padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                icon,
                                contentDescription = null,
                                tint = if (selectedPayment == method) UgaGreen else Color.Gray
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Text(
                                text = method,
                                fontWeight = if (selectedPayment == method) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedPayment == method) UgaGreen else Color.Black
                            )
                            Spacer(modifier = Modifier.weight(1f))
                            RadioButton(
                                selected = selectedPayment == method,
                                onClick = { selectedPayment = method },
                                colors = RadioButtonDefaults.colors(selectedColor = UgaGreen)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Price Summary
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F4))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Service Fee", color = Color.Gray)
                        Text("UGX 35,000", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Transport", color = Color.Gray)
                        Text("UGX 5,000", fontWeight = FontWeight.Bold)
                    }
                    Divider(modifier = Modifier.padding(vertical = 12.dp), color = Color.LightGray.copy(alpha = 0.5f))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        Text("UGX 40,000", fontWeight = FontWeight.Bold, fontSize = 18.sp, color = UgaGreen)
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DateChip(date: LocalDate, isSelected: Boolean, onClick: () -> Unit) {
    val dayFormatter = DateTimeFormatter.ofPattern("EEE")
    val dateFormatter = DateTimeFormatter.ofPattern("dd")
    
    Surface(
        modifier = Modifier
            .width(65.dp)
            .height(85.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        color = if (isSelected) UgaGreen else Color.White,
        border = if (isSelected) null else androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = date.format(dayFormatter).uppercase(),
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color.Gray,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = date.format(dateFormatter),
                fontSize = 20.sp,
                color = if (isSelected) Color.White else Color.Black,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BookingScreenPreview() {
    UgaWakaTheme {
        BookingScreen(providerName = "John Katumba", serviceName = "Plumbing", onBack = {}, onBookingConfirm = {})
    }
}

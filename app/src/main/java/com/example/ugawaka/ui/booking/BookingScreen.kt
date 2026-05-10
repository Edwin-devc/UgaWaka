package com.example.ugawaka.ui.booking

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.ugawaka.LocationPickerDialog
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(
    providerId: Int,
    serviceId: Int,
    onBack: () -> Unit,
    onBookingConfirm: () -> Unit,
    viewModel: BookingViewModel = viewModel()
) {
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }
    
    val selectedDate by viewModel.selectedDate.collectAsState()
    val selectedLocation by viewModel.selectedLocation.collectAsState()
    val locationName by viewModel.locationName.collectAsState()
    val notes by viewModel.notes.collectAsState()
    val selectedPayment by viewModel.selectedPayment.collectAsState()
    val providerRate by viewModel.providerRate.collectAsState()
    val providerName by viewModel.providerName.collectAsState()
    val serviceName by viewModel.serviceName.collectAsState()
    val isSubmitting by viewModel.isSubmitting.collectAsState()
    val bookingResult by viewModel.bookingResult.collectAsState()

    LaunchedEffect(providerId, serviceId) {
        viewModel.loadBookingDetails(providerId, serviceId)
    }

    var showSuccessDialog by remember { mutableStateOf(false) }

    LaunchedEffect(bookingResult) {
        if (bookingResult?.isSuccess == true) {
            showSuccessDialog = true
        }
    }
    var showMapDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        val latLng = LatLng(it.latitude, it.longitude)
                        val name = "Current Location (${String.format(Locale.US, "%.4f", it.latitude)}, ${String.format(Locale.US, "%.4f", it.longitude)})"
                        viewModel.onLocationSelected(latLng, name)
                    }
                }
            } catch (e: SecurityException) {
                viewModel.onLocationSelected(selectedLocation, "Permission denied")
            }
        } else {
            viewModel.onLocationSelected(selectedLocation, "Location permission denied")
        }
    }

    LaunchedEffect(Unit) {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    val latLng = LatLng(it.latitude, it.longitude)
                    val name = "Current Location (${String.format(Locale.US, "%.4f", it.latitude)}, ${String.format(Locale.US, "%.4f", it.longitude)})"
                    viewModel.onLocationSelected(latLng, name)
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    if (showMapDialog) {
        LocationPickerDialog(
            initialLocation = selectedLocation,
            onLocationSelected = { latLng ->
                val name = "Pinned Location (${String.format(Locale.US, "%.4f", latLng.latitude)}, ${String.format(Locale.US, "%.4f", latLng.longitude)})"
                viewModel.onLocationSelected(latLng, name)
                showMapDialog = false
            },
            onDismiss = { showMapDialog = false }
        )
    }

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
                    onClick = { viewModel.confirmBooking(providerId) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = UgaGreen),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isSubmitting
                ) {
                    if (isSubmitting) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                    } else {
                        Text("Confirm Booking", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
                    }
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
            if (bookingResult?.isFailure == true) {
                Text(
                    text = bookingResult?.exceptionOrNull()?.message ?: "An error occurred",
                    color = Color.Red,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

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
                        onClick = { viewModel.onDateSelected(date) }
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
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = "Drop-off Location", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = locationName,
                            color = Color.Gray,
                            fontSize = 13.sp,
                            maxLines = 1,
                            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                        )
                    }
                    TextButton(onClick = { showMapDialog = true }) {
                        Icon(
                            Icons.Default.Map,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp),
                            tint = UgaGreen
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Map", color = UgaGreen, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Notes Section
            Text(text = "Additional Notes", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = notes,
                onValueChange = { viewModel.onNotesChanged(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("E.g. The gate code is 1234...", fontSize = 14.sp) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = UgaGreen,
                    unfocusedBorderColor = Color.LightGray
                )
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Rate Information
            Text(text = "Pricing Information", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF1F8F4))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Rate", fontWeight = FontWeight.Medium)
                        Text(
                            text = "UGX ${providerRate ?: "0"}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp,
                            color = UgaGreen
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Note: This is the base rate for the service. The final amount will depend on the total number of hours worked. The more hours, the more you pay.",
                        fontSize = 13.sp,
                        color = Color.Gray
                    )
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
        BookingScreen(providerId = 1, serviceId = 1, onBack = {}, onBookingConfirm = {})
    }
}

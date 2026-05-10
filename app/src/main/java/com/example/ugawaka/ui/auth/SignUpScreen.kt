package com.example.ugawaka.ui.auth

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ugawaka.R
import com.example.ugawaka.SectionLabel
import com.example.ugawaka.RoleButton
import com.example.ugawaka.CustomTextField
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*
import java.util.Locale
import androidx.lifecycle.viewmodel.compose.viewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpScreen(
    onSignInClick: () -> Unit,
    onSignUpSuccess: (String) -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val selectedRole = viewModel.selectedRole
    val fullName = viewModel.fullName
    val email = viewModel.email
    val phoneNumber = viewModel.phoneNumber
    val password = viewModel.password
    val confirmPassword = viewModel.confirmPassword
    val scrollState = rememberScrollState()
    val context = LocalContext.current
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    location?.let {
                        viewModel.updateLocation(it.latitude, it.longitude)
                    }
                }
            } catch (e: SecurityException) {
                // Handle exception
            }
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
                    viewModel.updateLocation(it.latitude, it.longitude)
                }
            }
        } else {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(scrollState)
    ) {
        // Top Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 60.dp, bottomEnd = 60.dp))
                .background(UgaGreen)
                .padding(top = 60.dp, bottom = 40.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        modifier = Modifier.size(64.dp),
                        shape = CircleShape,
                        color = Color.White
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ugawaka_logo),
                            contentDescription = "Logo",
                            modifier = Modifier.padding(8.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "UGAWAKA",
                        color = Color.White,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Join the trusted services community",
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
        ) {
            Text(
                text = "Sign Up",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "Create your UGAWAKA account",
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            if (viewModel.errorMessage != null) {
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
            }

            // ROLE Section
            SectionLabel("JOIN AS")
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                RoleButton(
                    text = "Client",
                    isSelected = selectedRole == "Client",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onRoleSelected("Client") }
                )
                Spacer(modifier = Modifier.width(16.dp))
                RoleButton(
                    text = "Provider",
                    isSelected = selectedRole == "Provider",
                    modifier = Modifier.weight(1f),
                    onClick = { viewModel.onRoleSelected("Provider") }
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FULL NAME Section
            SectionLabel("FULL NAME")
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(
                value = fullName,
                onValueChange = { viewModel.onFullNameChanged(it) },
                placeholder = "Enter your full name",
                leadingIcon = Icons.Default.Person
            )

            Spacer(modifier = Modifier.height(24.dp))

            // EMAIL ADDRESS Section
            SectionLabel("EMAIL ADDRESS")
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(
                value = email,
                onValueChange = { viewModel.onEmailChanged(it) },
                placeholder = "Enter your email",
                leadingIcon = Icons.Default.Email,
                keyboardType = KeyboardType.Email
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PHONE NUMBER Section
            SectionLabel("PHONE NUMBER")
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(
                value = phoneNumber,
                onValueChange = { viewModel.onPhoneChanged(it) },
                placeholder = "Enter your phone number",
                leadingIcon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone
            )

            Spacer(modifier = Modifier.height(24.dp))

            // PASSWORD Section
            SectionLabel("PASSWORD")
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(
                value = password,
                onValueChange = { viewModel.onPasswordChanged(it) },
                placeholder = "Create a password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            Spacer(modifier = Modifier.height(24.dp))

            // CONFIRM PASSWORD Section
            SectionLabel("CONFIRM PASSWORD")
            Spacer(modifier = Modifier.height(12.dp))
            CustomTextField(
                value = confirmPassword,
                onValueChange = { viewModel.onConfirmPasswordChanged(it) },
                placeholder = "Confirm your password",
                leadingIcon = Icons.Default.Lock,
                keyboardType = KeyboardType.Password,
                isPassword = true
            )

            if (selectedRole == "Provider") {
                Spacer(modifier = Modifier.height(24.dp))

                // NIN Section
                SectionLabel("NATIONAL ID NUMBER (NIN)")
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField(
                    value = viewModel.nin,
                    onValueChange = { viewModel.onNinChanged(it) },
                    placeholder = "Enter your 14-character NIN",
                    leadingIcon = Icons.Default.CreditCard
                )

                Spacer(modifier = Modifier.height(24.dp))

                // SERVICE Section
                SectionLabel("OFFERED SERVICE")
                Spacer(modifier = Modifier.height(12.dp))
                
                val services by viewModel.services.collectAsState()
                var expanded by remember { mutableStateOf(false) }
                val selectedService = services.find { it.id == viewModel.serviceId }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedService?.name ?: "Select a service",
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        leadingIcon = { Icon(Icons.Default.Work, contentDescription = null, tint = UgaGreen) },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        shape = RoundedCornerShape(20.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = UgaGreen,
                            unfocusedBorderColor = Color.LightGray
                        )
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(Color.White)
                    ) {
                        services.forEach { service ->
                            DropdownMenuItem(
                                text = { Text(service.name) },
                                onClick = {
                                    viewModel.onServiceSelected(service.id)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // RATE Section
                SectionLabel("BASE RATE (UGX)")
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField(
                    value = viewModel.rate,
                    onValueChange = { viewModel.onRateChanged(it) },
                    placeholder = "e.g. 20000",
                    leadingIcon = Icons.Default.Payments,
                    keyboardType = KeyboardType.Number
                )

                Spacer(modifier = Modifier.height(24.dp))

                // ABOUT Section
                SectionLabel("ABOUT (OPTIONAL)")
                Spacer(modifier = Modifier.height(12.dp))
                CustomTextField(
                    value = viewModel.about,
                    onValueChange = { viewModel.onAboutChanged(it) },
                    placeholder = "Tell clients about your expertise",
                    leadingIcon = Icons.Default.Info,
                    modifier = Modifier.height(120.dp),
                    singleLine = false
                )

                Spacer(modifier = Modifier.height(24.dp))

                // LOCATION Section
                SectionLabel("WORK LOCATION")
                Spacer(modifier = Modifier.height(12.dp))
                
                var showMap by remember { mutableStateOf(false) }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showMap = true },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FBF9)),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.LocationOn,
                            contentDescription = null,
                            tint = if (viewModel.latitude != null) UgaGreen else Color.Gray
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = if (viewModel.latitude != null) 
                                    "Location: ${String.format(Locale.US, "%.4f", viewModel.latitude)}, ${String.format(Locale.US, "%.4f", viewModel.longitude)}"
                                else 
                                    "Tap to select work location",
                                color = if (viewModel.latitude != null) Color.Black else Color.Gray,
                                fontWeight = FontWeight.Medium
                            )
                            if (viewModel.latitude != null) {
                                Text(
                                    text = "Tap to change",
                                    color = UgaGreen,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                if (showMap) {
                    AlertDialog(
                        onDismissRequest = { showMap = false },
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        properties = androidx.compose.ui.window.DialogProperties(usePlatformDefaultWidth = false)
                    ) {
                        Surface(
                            modifier = Modifier.fillMaxSize(),
                            shape = RoundedCornerShape(20.dp),
                            color = Color.White
                        ) {
                            Box(modifier = Modifier.fillMaxSize()) {
                                val initialPos = if (viewModel.latitude != null) {
                                    LatLng(viewModel.latitude!!, viewModel.longitude!!)
                                } else {
                                    LatLng(0.3476, 32.5825) // Kampala
                                }
                                val cameraPositionState = rememberCameraPositionState {
                                    position = CameraPosition.fromLatLngZoom(initialPos, 15f)
                                }

                                GoogleMap(
                                    modifier = Modifier.fillMaxSize(),
                                    cameraPositionState = cameraPositionState,
                                    properties = MapProperties(isMyLocationEnabled = true),
                                    onMapClick = { latLng ->
                                        viewModel.updateLocation(latLng.latitude, latLng.longitude)
                                    }
                                ) {
                                    viewModel.latitude?.let { lat ->
                                        viewModel.longitude?.let { lon ->
                                            Marker(
                                                state = MarkerState(position = LatLng(lat, lon)),
                                                title = "Selected Location"
                                            )
                                        }
                                    }
                                }

                                Column(
                                    modifier = Modifier
                                        .align(Alignment.TopCenter)
                                        .padding(16.dp)
                                        .background(Color.White.copy(alpha = 0.8f), RoundedCornerShape(20.dp))
                                        .padding(12.dp)
                                ) {
                                    Text("Tap on the map to select your work location", fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { showMap = false },
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(24.dp)
                                        .fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = UgaGreen)
                                ) {
                                    Text("Confirm Location")
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            Button(
                onClick = { viewModel.signUp(onSignUpSuccess) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !viewModel.isLoading,
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(containerColor = UgaGreen)
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text(
                        text = "Create Account",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(text = "Already have an account? ", color = Color.Gray)
                Text(
                    text = "Sign In",
                    color = UgaGreen,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.clickable { onSignInClick() }
                )
            }
            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SignUpScreenPreview() {
    UgaWakaTheme {
        SignUpScreen(onSignInClick = {}, onSignUpSuccess = {})
    }
}

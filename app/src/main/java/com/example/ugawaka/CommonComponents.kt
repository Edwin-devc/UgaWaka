package com.example.ugawaka

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Handyman
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ugawaka.ui.theme.UgaGreen

@Composable
fun UgaWakaBottomNavigation(
    currentScreen: String,
    isProvider: Boolean = false,
    onNavigate: (String) -> Unit
) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = currentScreen == "home",
            onClick = {
                if (isProvider) onNavigate("providerDashboard") else onNavigate("home")
            },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
        )
        if (!isProvider) {
            NavigationBarItem(
                icon = { Icon(Icons.Default.Handyman, contentDescription = null) },
                label = { Text("Services") },
                selected = currentScreen == "services",
                onClick = { onNavigate("services") },
                colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
            )
        }
        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Bookings") },
            selected = currentScreen == "bookings",
            onClick = {
                if (isProvider) onNavigate("providerBookings") else onNavigate("bookings")
            },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = currentScreen == "profile",
            onClick = {
                if (isProvider) onNavigate("myProviderProfile") else onNavigate("profile")
            },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
        )
    }
}

@Composable
fun SectionLabel(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Gray,
        modifier = modifier
    )
}

@Composable
fun RoleButton(text: String, isSelected: Boolean, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Surface(
        modifier = modifier
            .height(56.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) UgaGreen else Color(0xFFF2F2F2),
        border = if (isSelected) null else BorderStroke(1.dp, Color.LightGray)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = text,
                color = if (isSelected) Color.White else Color.Gray,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun CustomTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    isPassword: Boolean = false,
    singleLine: Boolean = true
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth()
            .then(if (singleLine) Modifier.height(64.dp) else Modifier),
        placeholder = { Text(text = placeholder, color = Color.Gray) },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = UgaGreen,
                modifier = Modifier.size(24.dp)
            )
        },
        visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        shape = RoundedCornerShape(20.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = UgaGreen,
            unfocusedBorderColor = Color.LightGray,
            cursorColor = UgaGreen
        ),
        singleLine = singleLine
    )
}

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
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

@Composable
fun ServicesScreen(
    onServiceClick: (String) -> Unit,
    viewModel: ServicesViewModel = viewModel(factory = ViewModelFactory)
) {
    val categories = viewModel.uiState.categories
    val searchQuery = viewModel.searchQuery

    Scaffold(
        bottomBar = { UgaWakaBottomNavigation(currentScreen = "services") }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .background(Color(0xFFF8FBF9))
        ) {
            // Header Section
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(bottomStart = 32.dp, bottomEnd = 32.dp))
                    .background(UgaGreen)
                    .padding(20.dp)
            ) {
                Column {
                    Text(
                        text = "CLIENT EXPERIENCE",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            modifier = Modifier.size(48.dp),
                            shape = CircleShape,
                            color = Color.LightGray
                        ) {
                            Icon(Icons.Default.Person, contentDescription = null)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(text = "Good morning", color = Color.White, fontSize = 12.sp)
                            Text(
                                text = "KAYEERA NATHAN",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Available providers near Nakasero",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 11.sp
                            )
                        }
                        IconButton(onClick = { /* TODO */ }) {
                            Icon(Icons.Outlined.Notifications, contentDescription = null, tint = Color.White)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Kampala, Uganda", color = Color.White, fontSize = 14.sp)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    // Search Bar
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        color = Color.White.copy(alpha = 0.2f)
                    ) {
                        TextField(
                            value = searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            modifier = Modifier.fillMaxWidth(),
                            placeholder = { Text(text = "Search services...", color = Color.White.copy(alpha = 0.7f)) },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color.White) },
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                cursorColor = Color.White,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            ),
                            singleLine = true
                        )
                    }
                }
            }

            // Featured Card
            Card(
                modifier = Modifier
                    .padding(20.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFFE8F5E9), Color(0xFFFFFFFF))
                            )
                        )
                        .padding(20.dp)
                ) {
                    Text(
                        text = "FEATURED TODAY",
                        color = UgaGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Emergency plumbing slots open in Nakasero",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row {
                        AssistChip(
                            onClick = { },
                            label = { Text("Avg arrival 20 mins", fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color.White)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        AssistChip(
                            onClick = { },
                            label = { Text("Service from UGX 35k", fontSize = 11.sp) },
                            colors = AssistChipDefaults.assistChipColors(containerColor = Color.White)
                        )
                    }
                }
            }

            // Browse Categories
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Browse Categories", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text(text = "View All", color = UgaGreen, fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(categories) { category ->
                    CategoryItem(category) { onServiceClick(category.name) }
                }
            }
            
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun CategoryItem(category: ServiceCategoryData, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { onClick() }
    ) {
        Surface(
            modifier = Modifier.size(72.dp),
            shape = RoundedCornerShape(20.dp),
            color = category.color
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = category.icon,
                    contentDescription = null,
                    tint = Color.DarkGray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = category.name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun UgaWakaBottomNavigation(currentScreen: String) {
    NavigationBar(
        containerColor = Color.White,
        tonalElevation = 8.dp
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = null) },
            label = { Text("Home") },
            selected = currentScreen == "home",
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = null) },
            label = { Text("Services") },
            selected = currentScreen == "services",
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.CalendarMonth, contentDescription = null) },
            label = { Text("Bookings") },
            selected = currentScreen == "bookings",
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = null) },
            label = { Text("Profile") },
            selected = currentScreen == "profile",
            onClick = { /* TODO */ },
            colors = NavigationBarItemDefaults.colors(selectedIconColor = UgaGreen, selectedTextColor = UgaGreen)
        )
    }
}

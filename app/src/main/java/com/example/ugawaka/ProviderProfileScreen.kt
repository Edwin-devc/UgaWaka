package com.example.ugawaka

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderProfileScreen(
    providerName: String,
    onBack: () -> Unit,
    onBook: () -> Unit,
    onNavigate: (String) -> Unit,
    onLogout: () -> Unit = {},
    isOwnProfile: Boolean = false
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isOwnProfile) "My Profile" else "Provider Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (isOwnProfile) {
                        IconButton(onClick = onLogout) {
                            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = "Logout", tint = Color.Red)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.White,
                    titleContentColor = Color.Black
                )
            )
        },
        bottomBar = {
            Column {
                if (!isOwnProfile) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        shadowElevation = 4.dp,
                        color = Color.White
                    ) {
                        Button(
                            onClick = onBook,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = UgaGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Book Now", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
                        }
                    }
                }
                UgaWakaBottomNavigation(
                    currentScreen = if (isOwnProfile) "profile" else "services",
                    isProvider = isOwnProfile,
                    onNavigate = onNavigate
                )
            }
        }
    )
{ paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FBF9))
        ) {
            item {
                ProviderHeader(providerName)
            }
            item {
                ProviderStats()
            }
            item {
                ProviderBio()
            }
            item {
                ReviewsSection()
            }
        }
    }
}

@Composable
fun ProviderHeader(name: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            shape = CircleShape,
            color = Color.LightGray
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ugawaka_logo),
                contentDescription = null,
                modifier = Modifier.padding(20.dp),
                tint = UgaGreen
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Professional Plumber", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = UgaGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "Nakasero, Kampala", fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProviderStats() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(Color.White)
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "Rating", value = "4.9", icon = Icons.Default.Star, iconColor = Color(0xFFFFB300))
        StatItem(label = "Jobs", value = "120+", icon = Icons.Default.Work, iconColor = UgaGreen)
        StatItem(label = "Exp.", value = "5 Yrs", icon = Icons.Default.History, iconColor = Color.Blue)
    }
}

@Composable
fun StatItem(label: String, value: String, icon: ImageVector, iconColor: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(icon, contentDescription = null, tint = iconColor)
        Text(text = value, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        Text(text = label, fontSize = 12.sp, color = Color.Gray)
    }
}

@Composable
fun ProviderBio() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Text(text = "About", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "I am a certified professional with over 5 years of experience in providing high-quality services in Kampala. I specialize in emergency repairs and routine maintenance. Quality and customer satisfaction are my top priorities.",
            fontSize = 14.sp,
            color = Color.DarkGray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ReviewsSection() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Reviews", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "See All", color = UgaGreen, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        // Sample Review
        ReviewItem("Nathan K.", 5, "Excellent work! Arrived on time and fixed the leak quickly.")
        ReviewItem("Sarah M.", 4, "Very professional and knew exactly what they were doing.")
    }
}

@Composable
fun ReviewItem(reviewer: String, rating: Int, comment: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(32.dp), shape = CircleShape, color = Color.LightGray) {
                    Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(4.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = reviewer, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.weight(1f))
                Row {
                    repeat(rating) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ProviderProfileScreenPreview() {
    UgaWakaTheme {
        ProviderProfileScreen(providerName = "John Katumba", onBack = {}, onBook = {}, onNavigate = {}, onLogout = {})
    }
}

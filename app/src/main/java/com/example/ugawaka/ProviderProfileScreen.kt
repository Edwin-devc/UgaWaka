package com.example.ugawaka

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ugawaka.ui.theme.UgaGreen
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.tooling.preview.Preview
import com.example.ugawaka.ui.theme.UgaWakaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProviderProfileScreen(
    providerName: String,
    onBack: () -> Unit,
    viewModel: ServicesViewModel = viewModel(factory = ViewModelFactory)
) {
    val uiState = viewModel.uiState
    val provider = viewModel.getProviderByName(providerName)
    
    // Collect reviews flow from database as state
    val reviews by if (provider != null) {
        viewModel.getReviewsForProvider(provider.id).collectAsState(initial = emptyList())
    } else {
        androidx.compose.runtime.remember { androidx.compose.runtime.mutableStateOf(emptyList<Review>()) }
    }
    
    val snackbarHostState = androidx.compose.runtime.remember { SnackbarHostState() }

    LaunchedEffect(uiState.bookingStatus) {
        uiState.bookingStatus?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearBookingStatus()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Provider Profile") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Column {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shadowElevation = 4.dp,
                    color = Color.White
                ) {
                    Button(
                        onClick = { provider?.let { viewModel.bookService(it) } },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = UgaGreen),
                        shape = RoundedCornerShape(12.dp),
                        enabled = !uiState.isLoading
                    ) {
                        if (uiState.isLoading) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                        } else {
                            Text("Book Now", modifier = Modifier.padding(8.dp), fontSize = 16.sp)
                        }
                    }
                }
                UgaWakaBottomNavigation(currentScreen = "services")
            }
        }
    ) { paddingValues ->
        if (provider == null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "Provider not found")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color(0xFFF8FBF9))
            ) {
                item { ProviderHeader(provider) }
                item { ProviderStats(provider) }
                item { ProviderBio(provider) }
                item { ReviewsSection(reviews) }
            }
        }
    }
}

@Composable
fun ProviderHeader(provider: Provider) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(modifier = Modifier.size(100.dp), shape = CircleShape, color = Color.LightGray) {
            Icon(painter = painterResource(id = R.drawable.ugawaka_logo), contentDescription = null, modifier = Modifier.padding(20.dp), tint = UgaGreen)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = provider.name, fontSize = 24.sp, fontWeight = FontWeight.Bold)
        Text(text = "Professional ${provider.categoryName}", color = Color.Gray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.LocationOn, contentDescription = null, tint = UgaGreen, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = provider.location, fontSize = 14.sp, color = Color.Gray)
        }
    }
}

@Composable
fun ProviderStats(provider: Provider) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).clip(RoundedCornerShape(20.dp)).background(Color.White).padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StatItem(label = "Rating", value = provider.rating.toString(), icon = Icons.Default.Star, iconColor = Color(0xFFFFB300))
        StatItem(label = "Jobs", value = "${provider.jobsCompleted}+", icon = Icons.Default.Work, iconColor = UgaGreen)
        StatItem(label = "Exp.", value = provider.experience, icon = Icons.Default.History, iconColor = Color.Blue)
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
fun ProviderBio(provider: Provider) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Text(text = "About", fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = provider.bio, fontSize = 14.sp, color = Color.DarkGray, lineHeight = 20.sp)
    }
}

@Composable
fun ReviewsSection(reviews: List<Review>) {
    Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "Reviews", fontWeight = FontWeight.Bold, fontSize = 18.sp)
            Text(text = "See All", color = UgaGreen, fontSize = 14.sp)
        }
        Spacer(modifier = Modifier.height(12.dp))
        if (reviews.isEmpty()) {
            Text(text = "No reviews yet", color = Color.Gray, fontSize = 14.sp)
        } else {
            reviews.forEach { review -> ReviewItem(review.reviewerName, review.rating, review.comment) }
        }
    }
}

@Composable
fun ReviewItem(reviewer: String, rating: Int, comment: String) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
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
                Row { repeat(rating) { Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp)) } }
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = comment, fontSize = 13.sp, color = Color.Gray)
        }
    }
}

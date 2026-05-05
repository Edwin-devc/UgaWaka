package com.example.ugawaka

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ugawaka.ui.theme.UgaGreen
import com.example.ugawaka.ui.theme.UgaWakaTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServiceDetailScreen(
    serviceName: String,
    onBack: () -> Unit,
    onProviderClick: (String) -> Unit,
    viewModel: ServicesViewModel = viewModel(factory = ViewModelFactory)
) {
    // This will now be reactive as the ViewModel collects from the DB flow
    val providers = viewModel.getProvidersForCategory(serviceName)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = serviceName, fontWeight = FontWeight.Bold) },
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
        bottomBar = { UgaWakaBottomNavigation(currentScreen = "services") }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(Color(0xFFF8FBF9))
        ) {
            item {
                ServiceDescription(serviceName)
            }
            item {
                Text(
                    text = "Available Providers",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )
            }
            if (providers.isEmpty()) {
                item {
                    Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                        Text(text = "No providers available for this category yet.", color = Color.Gray)
                    }
                }
            } else {
                items(providers) { provider ->
                    ProviderCard(provider, onClick = { onProviderClick(provider.name) })
                }
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
fun ServiceDescription(serviceName: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(16.dp)
    ) {
        Text(
            text = "About $serviceName Service",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = UgaGreen
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Professional $serviceName services tailored for your needs in Kampala. Our verified experts handle everything from emergency repairs to routine maintenance with quality guaranteed.",
            fontSize = 14.sp,
            color = Color.Gray,
            lineHeight = 20.sp
        )
    }
}

@Composable
fun ProviderCard(provider: Provider, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(60.dp),
                shape = CircleShape,
                color = Color.LightGray
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ugawaka_logo),
                    contentDescription = null,
                    modifier = Modifier.padding(12.dp),
                    tint = UgaGreen
                )
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = provider.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Star, contentDescription = null, tint = Color(0xFFFFB300), modifier = Modifier.size(16.dp))
                    Text(text = " ${provider.rating}", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = provider.distance, fontSize = 12.sp, color = Color.Gray)
                }
                Text(text = provider.price, fontSize = 14.sp, color = UgaGreen, fontWeight = FontWeight.Bold)
            }
            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(containerColor = UgaGreen),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text("View", fontSize = 14.sp)
            }
        }
    }
}

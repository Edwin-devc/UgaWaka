package com.example.ugawaka

import androidx.compose.runtime.Composable
import com.example.ugawaka.ui.auth.SignInScreen
import com.example.ugawaka.ui.auth.SignUpScreen
import com.example.ugawaka.ui.booking.BookingScreen
import com.example.ugawaka.ui.home.HomeScreen
import com.example.ugawaka.ui.services.ServicesScreen
import com.example.ugawaka.ui.services.ServiceDetailScreen
import com.example.ugawaka.ui.provider.ProviderSetupScreen
import com.example.ugawaka.ui.provider.ProviderDashboardScreen
import com.example.ugawaka.ui.provider.ProviderBookingsScreen
import com.example.ugawaka.ui.provider.ProviderProfileScreen
import com.example.ugawaka.ui.profile.UserProfileScreen
import com.example.ugawaka.ui.bookings.BookingsScreen
import com.example.ugawaka.ui.map.LiveMapScreen
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument

@Composable
fun UgaWakaApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "signin") {
        composable("signin") {
            SignInScreen(
                onSignUpClick = { navController.navigate("signup") },
                onLoginSuccess = { role ->
                    if (role == "Provider") {
                        navController.navigate("providerDashboard") {
                            popUpTo("signin") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("signin") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignInClick = { navController.navigate("signin") },
                onSignUpSuccess = { role ->
                    if (role == "Provider") {
                        // Redirect providers to sign in after registration for approval
                        navController.navigate("signin") {
                            popUpTo("signup") { inclusive = true }
                        }
                    } else {
                        navController.navigate("home") {
                            popUpTo("signup") { inclusive = true }
                        }
                    }
                }
            )
        }
        composable("providerSetup") {
            ProviderSetupScreen(
                onSetupComplete = {
                    navController.navigate("providerDashboard") {
                        popUpTo("providerSetup") { inclusive = true }
                    }
                }
            )
        }
        composable("providerDashboard") {
            ProviderDashboardScreen(
                onLogout = {
                    navController.navigate("signin") {
                        popUpTo("providerDashboard") { inclusive = true }
                    }
                },
                onProfileClick = {
                    // Navigate to provider's own profile
                    navController.navigate("myProviderProfile")
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("profile") {
            UserProfileScreen(
                onBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate("signin") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onBookingHistoryClick = {
                    navController.navigate("bookings")
                }
            )
        }
        composable("bookings") {
            BookingsScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("map") {
            LiveMapScreen(
                onBack = { navController.popBackStack() },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("providerBookings") {
            ProviderBookingsScreen(
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("home") {
            HomeScreen(
                onServiceClick = { serviceId ->
                    navController.navigate("serviceDetail/$serviceId")
                },
                onViewAllClick = {
                    navController.navigate("services")
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("services") {
            ServicesScreen(
                onServiceClick = { serviceId ->
                    navController.navigate("serviceDetail/$serviceId")
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(
            route = "serviceDetail/{serviceId}",
            arguments = listOf(navArgument("serviceId") { type = NavType.IntType })
        ) { backStackEntry ->
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: 0
            ServiceDetailScreen(
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onProviderClick = { providerId ->
                    navController.navigate("providerProfile/$providerId/$serviceId")
                },
                onBookClick = { providerId ->
                    navController.navigate("booking/$providerId/$serviceId")
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable("myProviderProfile") {
            ProviderProfileScreen(
                providerId = 1, // This should be the logged in provider's ID
                isOwnProfile = true,
                onBack = { navController.popBackStack() },
                onBook = {}, // Not needed for own profile
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onLogout = {
                    navController.navigate("signin") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = "providerProfile/{providerId}/{serviceId}",
            arguments = listOf(
                navArgument("providerId") { type = NavType.IntType },
                navArgument("serviceId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getInt("providerId") ?: 0
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: 0
            ProviderProfileScreen(
                providerId = providerId,
                onBack = { navController.popBackStack() },
                onBook = {
                    navController.navigate("booking/$providerId/$serviceId")
                },
                onNavigate = { route ->
                    navController.navigate(route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
        composable(
            route = "booking/{providerId}/{serviceId}",
            arguments = listOf(
                navArgument("providerId") { type = NavType.IntType },
                navArgument("serviceId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            val providerId = backStackEntry.arguments?.getInt("providerId") ?: 0
            val serviceId = backStackEntry.arguments?.getInt("serviceId") ?: 0
            BookingScreen(
                providerId = providerId,
                serviceId = serviceId,
                onBack = { navController.popBackStack() },
                onBookingConfirm = {
                    // For now, just go back to services or show success
                    navController.navigate("services") {
                        popUpTo("services") { inclusive = true }
                    }
                }
            )
        }
    }
}

package com.example.ugawaka

import androidx.compose.runtime.Composable
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
                onLoginSuccess = {
                    navController.navigate("services") {
                        popUpTo("signin") { inclusive = true }
                    }
                }
            )
        }
        composable("signup") {
            SignUpScreen(
                onSignInClick = { navController.navigate("signin") },
                onSignUpSuccess = {
                    navController.navigate("services") {
                        popUpTo("signup") { inclusive = true }
                    }
                }
            )
        }
        composable("services") {
            ServicesScreen(
                onServiceClick = { serviceName ->
                    navController.navigate("serviceDetail/$serviceName")
                }
            )
        }
        composable(
            route = "serviceDetail/{serviceName}",
            arguments = listOf(navArgument("serviceName") { type = NavType.StringType })
        ) { backStackEntry ->
            val serviceName = backStackEntry.arguments?.getString("serviceName") ?: ""
            ServiceDetailScreen(
                serviceName = serviceName,
                onBack = { navController.popBackStack() },
                onProviderClick = { providerName ->
                    navController.navigate("providerProfile/$providerName")
                }
            )
        }
        composable(
            route = "providerProfile/{providerName}",
            arguments = listOf(navArgument("providerName") { type = NavType.StringType })
        ) { backStackEntry ->
            val providerName = backStackEntry.arguments?.getString("providerName") ?: ""
            ProviderProfileScreen(
                providerName = providerName,
                onBack = { navController.popBackStack() }
            )
        }
    }
}

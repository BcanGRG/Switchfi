package com.bcan.switchfi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import com.bcan.switchfi.ui.NetworksScreen
import com.bcan.switchfi.ui.onboarding.OnboardingScreen
import com.bcan.switchfi.ui.detail.NetworkDetailScreen

@Serializable object OnboardingRoute
@Serializable object NetworksRoute

@Serializable
data class NetworkDetailRoute(val ssid: String)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = OnboardingRoute) {
        composable<OnboardingRoute> {
            OnboardingScreen(onContinue = { navController.navigate(NetworksRoute) { popUpTo(OnboardingRoute) { inclusive = true } } })
        }
        composable<NetworksRoute> { NetworksScreen() }
        composable<NetworkDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<NetworkDetailRoute>()
            NetworkDetailScreen(ssid = args.ssid)
        }
    }
}



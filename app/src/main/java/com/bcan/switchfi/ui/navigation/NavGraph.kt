package com.bcan.switchfi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import com.bcan.switchfi.ui.NetworksScreen
import com.bcan.switchfi.ui.detail.NetworkDetailScreen
import com.bcan.switchfi.ui.onboarding.OnboardingScreen
import com.bcan.switchfi.ui.settings.SettingsScreen
import com.bcan.switchfi.ui.known.KnownNetworksScreen

@Serializable object OnboardingRoute
@Serializable object NetworksRoute
@Serializable object SettingsRoute
@Serializable object KnownNetworksRoute

@Serializable
data class NetworkDetailRoute(val ssid: String)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = OnboardingRoute) {
        composable<OnboardingRoute> {
            OnboardingScreen(onContinue = { navController.navigate(NetworksRoute) { popUpTo(OnboardingRoute) { inclusive = true } } })
        }
        composable<NetworksRoute> { NetworksScreen() }
        composable<SettingsRoute> { SettingsScreen() }
        composable<KnownNetworksRoute> { KnownNetworksScreen() }
        composable<NetworkDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<NetworkDetailRoute>()
            NetworkDetailScreen(ssid = args.ssid)
        }
    }
}



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
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.currentBackStackEntryAsState

@Serializable object OnboardingRoute
@Serializable object NetworksRoute
@Serializable object SettingsRoute
@Serializable object KnownNetworksRoute

@Serializable
data class NetworkDetailRoute(val ssid: String)

@Composable
fun AppNavGraph(navController: NavHostController) {
    val items = listOf(NetworksRoute, KnownNetworksRoute, SettingsRoute)
    Scaffold(
        bottomBar = {
            val backStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = backStackEntry?.destination?.route
            NavigationBar {
                items.forEach { route ->
                    val (label, icon) = when (route) {
                        is NetworksRoute -> Pair("Networks", Icons.Default.Home)
                        is KnownNetworksRoute -> Pair("Known", Icons.Default.Star)
                        is SettingsRoute -> Pair("Settings", Icons.Default.Settings)
                        else -> Pair("", Icons.Default.Home)
                    }
                    val selected = currentRoute == route::class.qualifiedName
                    NavigationBarItem(
                        selected = selected,
                        onClick = { navController.navigate(route) { launchSingleTop = true } },
                        icon = { Icon(icon, contentDescription = label) },
                        label = { Text(label) }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = OnboardingRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<OnboardingRoute> {
                OnboardingScreen(onContinue = { navController.navigate(NetworksRoute) { popUpTo(OnboardingRoute) { inclusive = true } } })
            }
            composable<NetworksRoute> { NetworksScreen(navController = navController) }
            composable<SettingsRoute> { SettingsScreen() }
            composable<KnownNetworksRoute> { KnownNetworksScreen() }
            composable<NetworkDetailRoute> { backStackEntry ->
                val args = backStackEntry.toRoute<NetworkDetailRoute>()
                NetworkDetailScreen(ssid = args.ssid)
            }
        }
    }
}



package com.bcan.switchfi.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import com.bcan.switchfi.ui.NetworksScreen

@Serializable
object NetworksRoute

@Serializable
data class NetworkDetailRoute(val ssid: String)

@Composable
fun AppNavGraph(navController: NavHostController) {
    NavHost(navController = navController, startDestination = NetworksRoute) {
        composable<NetworksRoute> {
            NetworksScreen()
        }
        composable<NetworkDetailRoute> { backStackEntry ->
            val args = backStackEntry.toRoute<NetworkDetailRoute>()
            // TODO: NetworkDetailScreen(args)
        }
    }
}



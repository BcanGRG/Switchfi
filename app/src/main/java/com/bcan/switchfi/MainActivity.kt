package com.bcan.switchfi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.navigation.compose.rememberNavController
import com.bcan.switchfi.ui.NetworksScreen
import com.bcan.switchfi.ui.navigation.AppNavGraph
import com.bcan.switchfi.ui.theme.SwitchfiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SwitchfiTheme {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
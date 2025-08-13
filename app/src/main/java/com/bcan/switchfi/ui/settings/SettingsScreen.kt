package com.bcan.switchfi.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcan.switchfi.ui.theme.ThemeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(themeVm: ThemeViewModel = hiltViewModel()) {
    val isDark by themeVm.isDark.collectAsState()
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Settings") }) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(16.dp)) {
            Text(text = "Theme")
            androidx.compose.material3.Switch(checked = isDark, onCheckedChange = { themeVm.setDarkMode(it) })

            // Placeholders for RSSI threshold and hysteresis
            Text("RSSI threshold (dBm)")
            OutlinedTextField(value = "-75", onValueChange = {}, modifier = Modifier)
            Text("Hysteresis (dB)")
            OutlinedTextField(value = "8", onValueChange = {}, modifier = Modifier)
        }
    }
}



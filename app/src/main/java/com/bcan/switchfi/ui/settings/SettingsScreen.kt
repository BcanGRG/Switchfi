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
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcan.switchfi.ui.theme.ThemeViewModel
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Button
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.core.os.LocaleListCompat
import com.bcan.switchfi.ui.settings.SettingsViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(themeVm: ThemeViewModel = hiltViewModel(), settingsVm: SettingsViewModel = hiltViewModel()) {
    val isDark by themeVm.isDark.collectAsState()
    val rssi by settingsVm.rssiThreshold.collectAsState()
    val hyst by settingsVm.hysteresis.collectAsState()
    var expanded by remember { mutableStateOf(false) }
    val locales = listOf("en", "tr")
    Scaffold(topBar = { LargeTopAppBar(title = { Text("Settings") }) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(16.dp)) {
            Text(text = "Theme")
            Switch(checked = isDark, onCheckedChange = { themeVm.setDarkMode(it) })

            Text("RSSI threshold (dBm)")
            OutlinedTextField(value = rssi.toString(), onValueChange = { it.toIntOrNull()?.let(settingsVm::setRssiThreshold) })
            Text("Hysteresis (dB)")
            OutlinedTextField(value = hyst.toString(), onValueChange = { it.toIntOrNull()?.let(settingsVm::setHysteresis) })

            Text("Language")
            androidx.compose.material3.SegmentedButtonRow {
                locales.forEach { tag ->
                    val selected = androidx.core.os.ConfigurationCompat.getLocales(androidx.compose.ui.platform.LocalContext.current.resources.configuration)[0]?.language == tag
                    androidx.compose.material3.SegmentedButton(
                        selected = selected,
                        onClick = { AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(tag)) }
                    ) { Text(if (tag == "tr") "TR" else "EN") }
                }
            }
        }
    }
}



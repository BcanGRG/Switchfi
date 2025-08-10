package com.bcan.switchfi.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bcan.switchfi.ui.permission.isGrantedCompat
import com.bcan.switchfi.ui.permission.rememberWifiPermission
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun OnboardingScreen(onContinue: () -> Unit) {
    val wifiPermission = rememberWifiPermission()
    val hasPermission = wifiPermission.isGrantedCompat()

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Switchfi needs Wi‑Fi permissions to suggest and monitor networks.",
            style = MaterialTheme.typography.titleMedium
        )

        if (!hasPermission) {
            Button(onClick = { wifiPermission.launchPermissionRequest() }) {
                Text("Grant permission")
            }
        } else {
            Button(onClick = onContinue) {
                Text("Continue")
            }
        }
    }
}



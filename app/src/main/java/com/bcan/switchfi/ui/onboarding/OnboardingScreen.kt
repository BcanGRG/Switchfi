package com.bcan.switchfi.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.bcan.switchfi.R
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
        Text(text = stringResource(id = R.string.onboarding_permission_title))

        if (!hasPermission) {
            Button(onClick = { wifiPermission.launchPermissionRequest() }) {
                Text(stringResource(id = R.string.btn_grant_permission))
            }
        } else {
            Button(onClick = onContinue) {
                Text(stringResource(id = R.string.btn_continue))
            }
        }
    }
}



package com.bcan.switchfi.ui.permission

import android.os.Build
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberWifiPermissionsState(): MultiplePermissionsState {
    val permissions = if (Build.VERSION.SDK_INT >= 33) listOf(
        android.Manifest.permission.NEARBY_WIFI_DEVICES,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) else listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    return rememberMultiplePermissionsState(permissions = permissions)
}

@OptIn(ExperimentalPermissionsApi::class)
fun MultiplePermissionsState.allGranted(): Boolean = this.permissions.all { it.status.isGranted }



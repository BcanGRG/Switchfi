package com.bcan.switchfi.ui.permission

import android.os.Build
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberWifiPermissions(): List<PermissionState> {
    val permissions = if (Build.VERSION.SDK_INT >= 33) listOf(
        android.Manifest.permission.NEARBY_WIFI_DEVICES,
        android.Manifest.permission.ACCESS_FINE_LOCATION
    ) else listOf(
        android.Manifest.permission.ACCESS_FINE_LOCATION
    )
    val state = rememberMultiplePermissionsState(permissions = permissions)
    return state.permissions
}

@OptIn(ExperimentalPermissionsApi::class)
fun List<PermissionState>.allGranted(): Boolean = this.all { it.status.isGranted }



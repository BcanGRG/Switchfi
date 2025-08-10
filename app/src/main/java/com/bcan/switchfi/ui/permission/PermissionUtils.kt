package com.bcan.switchfi.ui.permission

import android.os.Build
import androidx.compose.runtime.Composable
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberWifiPermission(): PermissionState {
    val permission = if (Build.VERSION.SDK_INT >= 33) {
        android.Manifest.permission.NEARBY_WIFI_DEVICES
    } else {
        android.Manifest.permission.ACCESS_FINE_LOCATION
    }
    return rememberPermissionState(permission)
}

@OptIn(ExperimentalPermissionsApi::class)
fun PermissionState.isGrantedCompat(): Boolean = this.status.isGranted



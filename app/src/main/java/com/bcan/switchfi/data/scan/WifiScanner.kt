package com.bcan.switchfi.data.scan

import android.content.Context
import android.content.pm.PackageManager
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.content.ContextCompat
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiScanner @Inject constructor(
    private val context: Context,
    private val wifiManager: WifiManager
) {
    fun hasScanPermission(): Boolean {
        val fine = ContextCompat.checkSelfPermission(
            context,
            android.Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return if (Build.VERSION.SDK_INT >= 33) {
            val nearby = ContextCompat.checkSelfPermission(
                context,
                android.Manifest.permission.NEARBY_WIFI_DEVICES
            ) == PackageManager.PERMISSION_GRANTED
            fine && nearby
        } else {
            fine
        }
    }

    fun startScan(): Boolean {
        if (!hasScanPermission()) return false
        return runCatching { wifiManager.startScan() }.getOrDefault(false)
    }

    fun getScanResults(): List<ScanResult> {
        if (!hasScanPermission()) return emptyList()
        return runCatching { wifiManager.scanResults.orEmpty() }.getOrElse { emptyList() }
    }

    fun rssiToLevel(rssi: Int): Int {
        // Map -100..-30 dBm to 0..100
        val clamped = rssi.coerceIn(-100, -30)
        return ((clamped + 100) * (100f / 70f)).toInt().coerceIn(0, 100)
    }
}



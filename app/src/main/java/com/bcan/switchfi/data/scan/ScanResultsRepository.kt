package com.bcan.switchfi.data.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class ScanResultsRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val wifiManager: WifiManager
) {
    fun observeScanResults(): Flow<List<ScanResult>> = callbackFlow {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == WifiManager.SCAN_RESULTS_AVAILABLE_ACTION) {
                    trySend(wifiManager.scanResults.orEmpty())
                }
            }
        }
        val filter = IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)
        context.registerReceiver(receiver, filter)
        // emit current cache once
        trySend(wifiManager.scanResults.orEmpty())
        awaitClose { context.unregisterReceiver(receiver) }
    }
}



package com.bcan.switchfi.data.scan

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.wifi.WifiManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class WifiStateRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun isWifiEnabledFlow(): Flow<Boolean> = callbackFlow {
        val filter = IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION)
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                if (intent?.action == WifiManager.WIFI_STATE_CHANGED_ACTION) {
                    val state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
                    trySend(state == WifiManager.WIFI_STATE_ENABLED || state == WifiManager.WIFI_STATE_ENABLING)
                }
            }
        }
        // Emit last sticky state if available
        val sticky = context.registerReceiver(null, filter)
        val initialState = sticky?.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN)
        if (initialState != null) {
            trySend(initialState == WifiManager.WIFI_STATE_ENABLED || initialState == WifiManager.WIFI_STATE_ENABLING)
        }
        context.registerReceiver(receiver, filter)
        awaitClose { context.unregisterReceiver(receiver) }
    }
}



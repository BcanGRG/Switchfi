package com.bcan.switchfi.data.scan

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.wifi.WifiInfo
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

@Singleton
class ConnectedNetworkRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun observeConnectedWifi(): Flow<WifiInfo?> = callbackFlow {
        val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                val transportInfo = networkCapabilities.transportInfo
                if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) && transportInfo is WifiInfo) {
                    trySend(transportInfo)
                }
            }

            override fun onLost(network: Network) {
                trySend(null)
            }
        }
        cm.registerDefaultNetworkCallback(callback)
        awaitClose { cm.unregisterNetworkCallback(callback) }
    }
}



package com.bcan.switchfi.data.suggestions

import android.content.Context
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.WifiNetworkSuggestion.Builder
import android.net.wifi.WifiManager
import android.os.Build
import androidx.annotation.RequiresApi
import com.bcan.switchfi.domain.model.KnownNetwork
import com.bcan.switchfi.domain.model.SecurityType
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class WifiSuggestionRepository @Inject constructor(
    private val context: Context,
    private val wifiManager: WifiManager
) {
    fun addSuggestions(networks: List<KnownNetwork>): Int {
        val suggestions = networks.mapNotNull { toSuggestion(it) }
        return wifiManager.addNetworkSuggestions(suggestions)
    }

    fun removeSuggestions(networks: List<KnownNetwork>): Int {
        val suggestions = networks.mapNotNull { toSuggestion(it) }
        return wifiManager.removeNetworkSuggestions(suggestions)
    }

    private fun toSuggestion(model: KnownNetwork): WifiNetworkSuggestion? {
        val builder = WifiNetworkSuggestion.Builder()
            .setSsid(model.ssid)

        when (model.securityType) {
            SecurityType.OPEN -> { /* no passphrase setter for open networks */ }
            SecurityType.WPA2 -> model.passphrase?.let { builder.setWpa2Passphrase(it) } ?: return null
            SecurityType.WPA3 -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                model.passphrase?.let { builder.setWpa3Passphrase(it) } ?: return null
            } else return null
        }

        return builder.build()
    }
}



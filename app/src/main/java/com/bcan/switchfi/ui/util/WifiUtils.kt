package com.bcan.switchfi.ui.util

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.provider.Settings

fun isWifiEnabled(context: Context): Boolean {
    val cm = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    val networks = cm.allNetworks
    return networks.any { net ->
        val caps = cm.getNetworkCapabilities(net)
        caps?.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) == true
    }
}

fun openWifiSettings(context: Context) {
    val intent = if (Build.VERSION.SDK_INT >= 29) {
        Intent(Settings.Panel.ACTION_WIFI)
    } else {
        Intent(Settings.ACTION_WIFI_SETTINGS)
    }
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    runCatching { context.startActivity(intent) }
}



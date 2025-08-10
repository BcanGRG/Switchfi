package com.bcan.switchfi.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class SecurityType { OPEN, WPA2, WPA3 }

@Serializable
data class KnownNetwork(
    val ssid: String,
    val securityType: SecurityType,
    val passphrase: String? = null
)



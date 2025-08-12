package com.bcan.switchfi.domain.usecase

import com.bcan.switchfi.domain.model.KnownNetwork

data class EvaluateSwitchInput(
    val currentRssi: Int?,
    val nearby: List<NearbyNetwork>,
    val known: List<KnownNetwork>,
    val rssiThreshold: Int = -75, // dBm threshold to consider switching
    val hysteresis: Int = 8       // dB gap required to switch
)

data class NearbyNetwork(val ssid: String, val rssi: Int)

data class EvaluateSwitchResult(
    val shouldSuggest: Boolean,
    val targetSsid: String?
)

class EvaluateAutoSwitchUseCase {
    fun invoke(input: EvaluateSwitchInput): EvaluateSwitchResult {
        val knownSet = input.known.map { it.ssid }.toSet()
        val filtered = input.nearby.filter { it.ssid in knownSet }
        if (filtered.isEmpty()) return EvaluateSwitchResult(false, null)

        val best = filtered.maxByOrNull { it.rssi } ?: return EvaluateSwitchResult(false, null)
        val current = input.currentRssi

        // If current is strong enough, do nothing
        if (current != null && current >= input.rssiThreshold) return EvaluateSwitchResult(false, null)

        // Require best to beat current by hysteresis
        if (current != null && best.rssi < current + input.hysteresis) return EvaluateSwitchResult(false, null)

        return EvaluateSwitchResult(true, best.ssid)
    }
}



package com.bcan.switchfi.ui

import androidx.lifecycle.viewModelScope
import com.bcan.switchfi.core.mvi.MviViewModel
import com.bcan.switchfi.core.mvi.UiEffect
import com.bcan.switchfi.core.mvi.UiEvent
import com.bcan.switchfi.core.mvi.UiState
import com.bcan.switchfi.data.scan.WifiScanner
import com.bcan.switchfi.data.scan.ScanResultsRepository
import com.bcan.switchfi.data.scan.WifiStateRepository
import com.bcan.switchfi.data.known.KnownNetworksRepository
import com.bcan.switchfi.data.scan.ConnectedNetworkRepository
import com.bcan.switchfi.ui.settings.SettingsRepository
import com.bcan.switchfi.ui.theme.ThemeRepository
import com.bcan.switchfi.ui.i18n.LocaleRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

object NetworksContract {
    data class State(
        val isLoading: Boolean = false,
        val items: List<UiNetwork> = emptyList()
    ) : UiState

    sealed interface Event : UiEvent {
        data object Refresh : Event
        data class ToggleKnownNetwork(val ssid: String) : Event
    }

    sealed interface Effect : UiEffect
}

data class UiNetwork(val ssid: String, val level: Int)

@HiltViewModel
class NetworksViewModel @Inject constructor(
    private val scanner: WifiScanner,
    private val scans: ScanResultsRepository,
    private val wifiStateRepo: WifiStateRepository,
    private val knownRepo: KnownNetworksRepository,
    private val connectedRepo: ConnectedNetworkRepository,
    private val settingsRepo: SettingsRepository,
    private val themeRepo: ThemeRepository,
    private val localeRepo: LocaleRepository
) : MviViewModel<NetworksContract.State, NetworksContract.Event, NetworksContract.Effect>(
    initialState = NetworksContract.State()
) {
    val wifiEnabled = wifiStateRepo.isWifiEnabledFlow().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), false
    )

    val knownSsids = knownRepo.knownNetworks
        .map { list -> list.map { it.ssid }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    val isDark = themeRepo.isDarkMode.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), false
    )

    val currentLocale = localeRepo.appLocale.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )

    init {
        // Auto-discover existing configured networks on first launch
        viewModelScope.launch {
            settingsRepo.autoLearn.collect { autoLearnEnabled ->
                if (autoLearnEnabled) {
                    discoverConfiguredNetworks()
                }
            }
        }
        
        // Auto-learn: when connected SSID changes and autoLearn enabled, add to known if missing
        viewModelScope.launch {
            combine(
                connectedRepo.observeConnectedWifi(),
                settingsRepo.autoLearn,
                scans.observeScanResults()
            ) { wifiInfo, autoLearn, scanResults -> Triple(wifiInfo, autoLearn, scanResults) }
                .collect { (wifiInfo, autoLearn, scanResults) ->
                    if (!autoLearn) return@collect
                    val connectedSsid = wifiInfo?.ssid?.trim('"') ?: return@collect
                    if (connectedSsid.isNotBlank() && !knownSsids.value.contains(connectedSsid)) {
                        // Find security type from scan results
                        val matchingResult = scanResults.find { it.SSID == connectedSsid }
                        val securityType = matchingResult?.let { 
                            determineSecurityType(it.capabilities)
                        } ?: com.bcan.switchfi.domain.model.SecurityType.OPEN
                        
                        runCatching {
                            knownRepo.add(com.bcan.switchfi.domain.model.KnownNetwork(connectedSsid, securityType, null))
                        }
                    }
                }
        }
        
        // Initial refresh
        refresh()
    }

    private fun discoverConfiguredNetworks() {
        viewModelScope.launch {
            runCatching {
                // Get configured networks from WifiManager (requires ACCESS_FINE_LOCATION)
                val configuredNetworks = scanner.getConfiguredNetworks()
                configuredNetworks.forEach { config ->
                    val ssid = config.SSID?.trim('"')
                    if (!ssid.isNullOrBlank() && !knownSsids.value.contains(ssid)) {
                        // Determine security type from WifiConfiguration
                        val securityType = when {
                            config.allowedKeyManagement.get(android.net.wifi.WifiConfiguration.KeyMgmt.WPA2_PSK) -> com.bcan.switchfi.domain.model.SecurityType.WPA2
                            config.allowedKeyManagement.get(android.net.wifi.WifiConfiguration.KeyMgmt.NONE) -> com.bcan.switchfi.domain.model.SecurityType.OPEN
                            else -> com.bcan.switchfi.domain.model.SecurityType.WPA2 // Default to WPA2
                        }
                        knownRepo.add(com.bcan.switchfi.domain.model.KnownNetwork(ssid, securityType, null))
                    }
                }
            }
        }
    }

    private fun determineSecurityType(capabilities: String): com.bcan.switchfi.domain.model.SecurityType {
        return when {
            capabilities.contains("WPA3") -> com.bcan.switchfi.domain.model.SecurityType.WPA3
            capabilities.contains("WPA2") || capabilities.contains("WPA") || capabilities.contains("WEP") -> com.bcan.switchfi.domain.model.SecurityType.WPA2
            else -> com.bcan.switchfi.domain.model.SecurityType.OPEN
        }
    }

    override fun onEvent(event: NetworksContract.Event) {
        when (event) {
            NetworksContract.Event.Refresh -> refresh()
            is NetworksContract.Event.ToggleKnownNetwork -> toggleKnownNetwork(event.ssid)
        }
    }

    private fun toggleKnownNetwork(ssid: String) {
        viewModelScope.launch {
            val isCurrentlyKnown = knownSsids.value.contains(ssid)
            if (isCurrentlyKnown) {
                knownRepo.removeBySsid(ssid)
            } else {
                // Find security type from current scan results
                scans.observeScanResults().collect { currentResults ->
                    val matchingResult = currentResults.find { it.SSID == ssid }
                    val securityType = matchingResult?.let { 
                        determineSecurityType(it.capabilities)
                    } ?: com.bcan.switchfi.domain.model.SecurityType.OPEN
                    
                    knownRepo.add(com.bcan.switchfi.domain.model.KnownNetwork(ssid, securityType, null))
                    return@collect // Only take first emission
                }
            }
        }
    }

    fun toggleTheme() {
        viewModelScope.launch {
            themeRepo.setDarkMode(!isDark.value)
        }
    }

    fun toggleLanguage() {
        viewModelScope.launch {
            val nextLocale = if (currentLocale.value?.startsWith("tr", ignoreCase = true) == true) {
                java.util.Locale.ENGLISH
            } else {
                java.util.Locale("tr")
            }
            localeRepo.setAppLocaleTag(nextLocale.toLanguageTag())
        }
    }

    private fun refresh() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            // Start a scan (legacy trigger), and observe results via broadcast
            val scanStarted = scanner.startScan()
            if (!scanStarted) {
                // If scan couldn't start, get cached results immediately
                val cachedResults = scanner.getScanResults()
                val list = cachedResults
                    .filter { it.SSID.isNotBlank() }
                    .sortedByDescending { it.level }
                    .map { UiNetwork(ssid = it.SSID, level = scanner.rssiToLevel(it.level)) }
                setState { copy(isLoading = false, items = list) }
                return@launch
            }
            
            // Wait for fresh scan results
            var hasReceivedResult = false
            scans.observeScanResults().collect { results ->
                if (!hasReceivedResult) {
                    hasReceivedResult = true
                    val list = results
                        .filter { it.SSID.isNotBlank() }
                        .sortedByDescending { it.level }
                        .map { UiNetwork(ssid = it.SSID, level = scanner.rssiToLevel(it.level)) }
                    setState { copy(isLoading = false, items = list) }
                }
            }
        }
    }
}

package com.bcan.switchfi.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
 
import com.bcan.switchfi.R
import com.bcan.switchfi.core.mvi.MviViewModel
import com.bcan.switchfi.core.mvi.UiEffect
import com.bcan.switchfi.core.mvi.UiEvent
import com.bcan.switchfi.core.mvi.UiState
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcan.switchfi.ui.theme.ThemeViewModel
import com.bcan.switchfi.ui.i18n.LocaleViewModel
import com.bcan.switchfi.ui.i18n.applyAppLocale
import java.util.Locale
import com.bcan.switchfi.data.scan.WifiScanner
import com.bcan.switchfi.data.scan.ScanResultsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import androidx.compose.material3.ListItem
import androidx.lifecycle.viewModelScope
import com.bcan.switchfi.ui.components.StrengthBar
import com.bcan.switchfi.ui.permission.allGranted
import com.bcan.switchfi.ui.permission.rememberWifiPermissionsState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import com.bcan.switchfi.ui.util.isLocationEnabled
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bcan.switchfi.data.worker.AutoSwitchWorker
import com.bcan.switchfi.ui.navigation.NetworkDetailRoute
import com.bcan.switchfi.data.scan.WifiStateRepository
import com.bcan.switchfi.data.known.KnownNetworksRepository
import com.bcan.switchfi.data.scan.ConnectedNetworkRepository
import com.bcan.switchfi.ui.settings.SettingsRepository
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

object NetworksContract {
    data class State(
        val isLoading: Boolean = false,
        val items: List<UiNetwork> = emptyList()
    ) : UiState

    sealed interface Event : UiEvent {
        data object Refresh : Event
    }

    sealed interface Effect : UiEffect
}

data class UiNetwork(val ssid: String, val level: Int)

@HiltViewModel
class NetworksViewModel @javax.inject.Inject constructor(
    private val scanner: WifiScanner,
    private val scans: ScanResultsRepository,
    private val wifiStateRepo: WifiStateRepository,
    private val knownRepo: KnownNetworksRepository,
    private val connectedRepo: ConnectedNetworkRepository,
    private val settingsRepo: SettingsRepository
) : MviViewModel<NetworksContract.State, NetworksContract.Event, NetworksContract.Effect>(
    initialState = NetworksContract.State()
) {
    val wifiEnabled = wifiStateRepo.isWifiEnabledFlow()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    val knownSsids = knownRepo.knownNetworks
        .map { list -> list.map { it.ssid }.toSet() }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptySet())

    init {
        // Auto-learn: when connected SSID changes and autoLearn enabled, add to known if missing
        viewModelScope.launch {
            combine(
                connectedRepo.observeConnectedWifi(),
                settingsRepo.autoLearn
            ) { wifiInfo, autoLearn -> Pair(wifiInfo, autoLearn) }
                .collect { (wifiInfo, autoLearn) ->
                    if (!autoLearn) return@collect
                    val ssid = wifiInfo?.ssid?.trim('"') ?: return@collect
                    if (ssid.isNotBlank() && !knownSsids.value.contains(ssid)) {
                        runCatching {
                            knownRepo.add(com.bcan.switchfi.domain.model.KnownNetwork(ssid, com.bcan.switchfi.domain.model.SecurityType.OPEN, null))
                        }
                    }
                }
        }
    }
    override fun onEvent(event: NetworksContract.Event) {
        when (event) {
            NetworksContract.Event.Refresh -> refresh()
        }
    }

    private fun refresh() {
        setState { copy(isLoading = true) }
        viewModelScope.launch {
            // Start a scan (legacy trigger), and observe results via broadcast
            scanner.startScan()
            scans.observeScanResults().collect { results ->
                val list = results
                    .filter { it.SSID.isNotBlank() }
                    .sortedByDescending { it.level }
                    .map { UiNetwork(ssid = it.SSID, level = scanner.rssiToLevel(it.level)) }
                setState { copy(isLoading = false, items = list) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NetworksScreen(
    vm: NetworksViewModel = hiltViewModel(),
    themeVm: ThemeViewModel = hiltViewModel(),
    localeVm: LocaleViewModel = hiltViewModel(),
    navController: NavController = androidx.navigation.compose.rememberNavController(),
) {
    val state by vm.state.collectAsState()
    val isDark by themeVm.isDark.collectAsState()
    val permissionsState = rememberWifiPermissionsState()
    val hasAllPermissions = permissionsState.allGranted()
    val wifiEnabled by vm.wifiEnabled.collectAsState()
    val knownSet by vm.knownSsids.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(hasAllPermissions) {
        if (hasAllPermissions) vm.onEvent(NetworksContract.Event.Refresh)
    }
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = {
                    val count = state.items.size
                    Text(text = stringResource(id = R.string.title_networks) + if (count > 0) "  ($count)" else "")
                },
                actions = {
                    IconButton(onClick = { vm.onEvent(NetworksContract.Event.Refresh) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { themeVm.setDarkMode(!isDark) }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                    IconButton(onClick = {
                        val next = if (localeVm.localeTag.value?.startsWith("tr") == true) java.util.Locale.ENGLISH else java.util.Locale("tr")
                        localeVm.setLocale(next)
                        applyAppLocale(tag = next.toLanguageTag())
                    }) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = "Toggle language")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
            when {
                !hasAllPermissions -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                            Text(text = stringResource(id = R.string.btn_grant_permission))
                        }
                    }
                }
                !wifiEnabled -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        androidx.compose.material3.TextButton(onClick = { com.bcan.switchfi.ui.util.openWifiSettings(context) }) {
                            Text(text = stringResource(id = R.string.btn_turn_on_wifi))
                        }
                    }
                }

                state.isLoading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                state.items.isEmpty() -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        if (!isLocationEnabled(context)) {
                            androidx.compose.material3.TextButton(onClick = {
                                runCatching { context.startActivity(android.content.Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) }
                            }) { Text(text = stringResource(id = R.string.msg_enable_location)) }
                        } else {
                            Text(text = stringResource(id = R.string.networks_count, 0))
                        }
                    }
                }
                else -> {
                    // Schedule a one-off evaluation in background (lightweight)
                    val ctx = androidx.compose.ui.platform.LocalContext.current
                    androidx.compose.runtime.SideEffect {
                        WorkManager.getInstance(ctx).enqueueUniqueWork(
                            "auto-switch-eval",
                            ExistingWorkPolicy.REPLACE,
                            OneTimeWorkRequestBuilder<AutoSwitchWorker>().build()
                        )
                    }
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(state.items) { item ->
                            ListItem(
                                headlineContent = {
                                    val isKnown = knownSet.contains(item.ssid)
                                    Text(text = if (isKnown) "★ ${item.ssid}" else item.ssid)
                                },
                                supportingContent = {
                                    StrengthBar(level = item.level, modifier = Modifier.fillMaxWidth())
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { navController.navigate(NetworkDetailRoute(item.ssid)) }
                            )
                        }
                    }
                }
            }
        }
    }
}



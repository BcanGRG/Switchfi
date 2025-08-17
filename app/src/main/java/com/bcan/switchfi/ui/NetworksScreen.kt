package com.bcan.switchfi.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.bcan.switchfi.R
import com.bcan.switchfi.data.worker.AutoSwitchWorker
import com.bcan.switchfi.ui.components.StrengthBar
import com.bcan.switchfi.ui.navigation.NetworkDetailRoute
import com.bcan.switchfi.ui.permission.allGranted
import com.bcan.switchfi.ui.permission.rememberWifiPermissionsState
import com.bcan.switchfi.ui.util.isLocationEnabled
import com.bcan.switchfi.ui.util.openWifiSettings
import com.google.accompanist.permissions.ExperimentalPermissionsApi

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NetworksScreen(
    vm: NetworksViewModel = hiltViewModel(),
    navController: NavController
) {
    val state by vm.state.collectAsState()
    val permissionsState = rememberWifiPermissionsState()
    val hasAllPermissions = permissionsState.allGranted()
    val wifiEnabled by vm.wifiEnabled.collectAsState()
    val knownSet by vm.knownSsids.collectAsState()
    val isDark by vm.isDark.collectAsState()
    val currentLocale by vm.currentLocale.collectAsState()
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
                    IconButton(onClick = { vm.toggleTheme() }) {
                        Icon(
                            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                    IconButton(onClick = { 
                        vm.toggleLanguage()
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
                        TextButton(onClick = { permissionsState.launchMultiplePermissionRequest() }) {
                            Text(text = stringResource(id = R.string.btn_grant_permission))
                        }
                    }
                }
                !wifiEnabled -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = { openWifiSettings(context) }) {
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
                            TextButton(onClick = {
                                runCatching { 
                                    context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)) 
                                }
                            }) { 
                                Text(text = stringResource(id = R.string.msg_enable_location)) 
                            }
                        } else {
                            Text(text = stringResource(id = R.string.networks_count, 0))
                        }
                    }
                }
                else -> {
                    // Schedule a one-off evaluation in background (lightweight)
                    androidx.compose.runtime.SideEffect {
                        WorkManager.getInstance(context).enqueueUniqueWork(
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




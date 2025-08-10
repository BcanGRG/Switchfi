package com.bcan.switchfi.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.lifecycle.viewmodel.compose.viewModel
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
import com.bcan.switchfi.data.suggestions.WifiSuggestionRepository
import com.bcan.switchfi.domain.model.KnownNetwork
import com.bcan.switchfi.domain.model.SecurityType
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcan.switchfi.ui.permission.allGranted
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted

object NetworksContract {
    data class State(
        val isLoading: Boolean = true,
        val networksCount: Int = 0
    ) : UiState

    sealed interface Event : UiEvent {
        data object OnAppear : Event
    }

    sealed interface Effect : UiEffect
}

class NetworksViewModel : MviViewModel<NetworksContract.State, NetworksContract.Event, NetworksContract.Effect>(
    initialState = NetworksContract.State()
) {
    override fun onEvent(event: NetworksContract.Event) {
        when (event) {
            NetworksContract.Event.OnAppear -> setState { copy(isLoading = false, networksCount = 0) }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun NetworksScreen(
    vm: NetworksViewModel = viewModel(),
    themeVm: ThemeViewModel = hiltViewModel(),
    localeVm: LocaleViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    val permissions = com.bcan.switchfi.ui.permission.rememberWifiPermissions()
    val hasAllPermissions = permissions.allGranted()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.title_networks)) },
                actions = {
                    IconButton(onClick = { vm.onEvent(NetworksContract.Event.OnAppear) }) {
                        Icon(imageVector = Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                    IconButton(onClick = { themeVm.setDarkMode(!themeVm.isDark.value) }) {
                        Icon(
                            imageVector = if (themeVm.isDark.value) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = "Toggle theme"
                        )
                    }
                    IconButton(onClick = {
                        val next = if (localeVm.localeTag.value == "tr" ) java.util.Locale.ENGLISH else java.util.Locale("tr")
                        localeVm.setLocale(next)
                        applyAppLocale(tag = next.toLanguageTag())
                    }) {
                        Icon(imageVector = Icons.Default.Language, contentDescription = "Toggle language")
                    }
                }
            )
        }
    ) { innerPadding ->
        com.google.accompanist.swiperefresh.SwipeRefresh(
            state = com.google.accompanist.swiperefresh.rememberSwipeRefreshState(isRefreshing = state.isLoading),
            onRefresh = { vm.onEvent(NetworksContract.Event.OnAppear) },
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                when {
                    !hasAllPermissions -> {
                        IconButton(onClick = {
                            permissions.forEach { if (!it.status.isGranted) it.launchPermissionRequest() }
                        }) {
                            Text(text = stringResource(id = R.string.btn_grant_permission))
                        }
                    }
                    state.isLoading -> CircularProgressIndicator()
                    else -> Text(text = stringResource(id = R.string.networks_count, state.networksCount))
                }
            }
        }
    }
}



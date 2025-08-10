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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworksScreen(
    vm: NetworksViewModel = viewModel(),
    themeVm: ThemeViewModel = hiltViewModel(),
    localeVm: LocaleViewModel = hiltViewModel()
) {
    val state by vm.state.collectAsState()
    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text(stringResource(id = R.string.title_networks)) },
                actions = {
                    IconButton(onClick = { themeVm.setDarkMode(!themeVm.isDark.value) }) {
                        Icon(
                            painter = painterResource(id = if (themeVm.isDark.value) android.R.drawable.btn_star_big_on else android.R.drawable.btn_star_big_off),
                            contentDescription = "Toggle theme"
                        )
                    }
                    IconButton(onClick = {
                        val next = if (localeVm.localeTag.value == "tr" ) java.util.Locale.ENGLISH else java.util.Locale("tr")
                        localeVm.setLocale(next)
                        applyAppLocale(tag = next.toLanguageTag())
                    }) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_sort_by_size),
                            contentDescription = "Toggle language"
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            if (state.isLoading) {
                CircularProgressIndicator()
            } else {
                Text(text = stringResource(id = R.string.networks_count, state.networksCount))
            }
        }
    }
}



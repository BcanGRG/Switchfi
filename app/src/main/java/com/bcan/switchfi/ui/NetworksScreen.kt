package com.bcan.switchfi.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bcan.switchfi.R
import com.bcan.switchfi.core.mvi.MviViewModel
import com.bcan.switchfi.core.mvi.UiEffect
import com.bcan.switchfi.core.mvi.UiEvent
import com.bcan.switchfi.core.mvi.UiState
import androidx.compose.ui.res.stringResource

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

@Composable
fun NetworksScreen(vm: NetworksViewModel = viewModel()) {
    val state by vm.state.collectAsState()
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        if (state.isLoading) {
            CircularProgressIndicator()
        } else {
            Text(text = stringResource(id = R.string.networks_count, state.networksCount))
        }
    }
}



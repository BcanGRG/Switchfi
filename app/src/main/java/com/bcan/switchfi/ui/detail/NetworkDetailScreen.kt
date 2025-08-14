package com.bcan.switchfi.ui.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcan.switchfi.R
import com.bcan.switchfi.data.known.KnownNetworksRepository
import com.bcan.switchfi.domain.model.KnownNetwork
import com.bcan.switchfi.domain.model.SecurityType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDetailScreen(ssid: String?) {
    val knownRepo: KnownNetworksRepository = androidx.hilt.navigation.compose.hiltViewModel()
    val known by knownRepo.knownNetworks.collectAsState(initial = emptyList())
    val isKnown = ssid != null && known.any { it.ssid == ssid }

    Scaffold(topBar = {
        LargeTopAppBar(title = { Text(text = stringResource(id = R.string.title_network_detail)) })
    }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ListItem(headlineContent = { Text(text = ssid ?: stringResource(id = R.string.ssid_unknown)) }, supportingContent = { Text(text = if (isKnown) "★ Known" else "Unknown") })
            Button(onClick = {
                if (ssid != null) {
                    if (!isKnown) {
                        androidx.lifecycle.viewmodel.compose.viewModel<DetailHelperVm>().addKnown(ssid)
                    } else {
                        androidx.lifecycle.viewmodel.compose.viewModel<DetailHelperVm>().removeKnown(ssid)
                    }
                }
            }) { Text(text = if (isKnown) "Remove from Known" else "Add to Known") }
        }
    }
}

class DetailHelperVm @javax.inject.Inject constructor(
    private val repo: KnownNetworksRepository
) : androidx.lifecycle.ViewModel() {
    fun addKnown(ssid: String) = androidx.lifecycle.viewModelScope.launch {
        repo.add(KnownNetwork(ssid, SecurityType.OPEN, null))
    }
    fun removeKnown(ssid: String) = androidx.lifecycle.viewModelScope.launch {
        repo.removeBySsid(ssid)
    }
}



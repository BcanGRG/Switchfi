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
import com.bcan.switchfi.data.suggestions.WifiSuggestionRepository

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NetworkDetailScreen(ssid: String?) {
    val vm: NetworkDetailViewModel = hiltViewModel()
    val isKnown by vm.isKnown.collectAsState()
    val suggestions: WifiSuggestionRepository = hiltViewModel()

    Scaffold(topBar = {
        LargeTopAppBar(title = { Text(text = stringResource(id = R.string.title_network_detail)) })
    }) { innerPadding ->
        Column(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ListItem(headlineContent = { Text(text = vm.ssid.ifBlank { stringResource(id = R.string.ssid_unknown) }) }, supportingContent = { Text(text = if (isKnown) "★ Known" else "Unknown") })
            Button(onClick = {
                if (!isKnown) vm.addToKnown() else vm.removeFromKnown()
            }) { Text(text = if (isKnown) "Remove from Known" else "Add to Known") }

            Button(onClick = {
                // Suggest connection to OS for this SSID
                if (vm.ssid.isNotBlank()) {
                    runCatching {
                        suggestions.addSuggestions(listOf(KnownNetwork(vm.ssid, SecurityType.OPEN, null)))
                    }
                }
            }) { Text(stringResource(id = R.string.details_connect)) }
        }
    }
}



package com.bcan.switchfi.ui.known

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewModelScope
import com.bcan.switchfi.data.known.KnownNetworksRepository
import com.bcan.switchfi.domain.model.KnownNetwork
import com.bcan.switchfi.domain.model.SecurityType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class KnownUi(val ssid: String, val security: SecurityType)

@HiltViewModel
class KnownNetworksViewModel @Inject constructor(
    private val repo: KnownNetworksRepository
) : androidx.lifecycle.ViewModel() {
    val items: StateFlow<List<KnownUi>> = repo.knownNetworks
        .map { list -> list.map { KnownUi(it.ssid, it.securityType) } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun add(ssid: String, psk: String?, sec: SecurityType) =
        viewModelScope.launch { repo.add(KnownNetwork(ssid, sec, psk)) }

    fun remove(ssid: String) = viewModelScope.launch { repo.removeBySsid(ssid) }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KnownNetworksScreen(vm: KnownNetworksViewModel = hiltViewModel()) {
    val items by vm.items.collectAsState()
    var ssid by remember { mutableStateOf("") }
    var psk by remember { mutableStateOf("") }

    Scaffold(topBar = { LargeTopAppBar(title = { Text("Known Networks") }) }) { inner ->
        Column(Modifier.fillMaxSize().padding(inner).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = ssid, onValueChange = { ssid = it }, label = { Text("SSID") })
                OutlinedTextField(value = psk, onValueChange = { psk = it }, label = { Text("Passphrase (optional)") })
                Button(onClick = { if (ssid.isNotBlank()) vm.add(ssid.trim(), psk.ifBlank { null }, if (psk.isBlank()) SecurityType.OPEN else SecurityType.WPA2) }) {
                    Text("Add")
                }
            }
            LazyColumn {
                items(items) { item ->
                    ListItem(
                        headlineContent = { Text(text = "★ ${item.ssid}") },
                        supportingContent = { Text(text = item.security.name) },
                        trailingContent = {
                            IconButton(onClick = { vm.remove(item.ssid) }) { Text("Remove") }
                        }
                    )
                }
            }
        }
    }
}



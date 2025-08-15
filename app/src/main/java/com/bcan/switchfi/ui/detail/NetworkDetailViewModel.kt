package com.bcan.switchfi.ui.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bcan.switchfi.domain.model.KnownNetwork
import com.bcan.switchfi.domain.model.SecurityType
import com.bcan.switchfi.domain.usecase.AddKnownNetworkUseCase
import com.bcan.switchfi.domain.usecase.RemoveKnownNetworkUseCase
import com.bcan.switchfi.data.suggestions.WifiSuggestionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NetworkDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val addKnown: AddKnownNetworkUseCase,
    private val removeKnown: RemoveKnownNetworkUseCase,
    knownRepo: com.bcan.switchfi.data.known.KnownNetworksRepository,
    private val suggestions: WifiSuggestionRepository
) : ViewModel() {
    val ssid: String = savedStateHandle.get<String>("ssid") ?: ""

    val isKnown: StateFlow<Boolean> = knownRepo.knownNetworks
        .map { list -> list.any { it.ssid == ssid } }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), false)

    fun addToKnown() {
        viewModelScope.launch { addKnown(KnownNetwork(ssid, SecurityType.OPEN, null)) }
    }

    fun removeFromKnown() {
        viewModelScope.launch { removeKnown(ssid) }
    }

    fun connectViaSuggestion() {
        if (ssid.isBlank()) return
        viewModelScope.launch {
            runCatching {
                suggestions.addSuggestions(listOf(KnownNetwork(ssid, SecurityType.OPEN, null)))
            }
        }
    }
}



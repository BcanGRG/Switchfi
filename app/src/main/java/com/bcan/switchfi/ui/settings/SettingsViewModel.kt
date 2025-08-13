package com.bcan.switchfi.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: SettingsRepository
) : ViewModel() {
    val rssiThreshold: StateFlow<Int> = repo.rssiThreshold.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), -75)
    val hysteresis: StateFlow<Int> = repo.hysteresis.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), 8)

    fun setRssiThreshold(value: Int) { viewModelScope.launch { repo.setRssiThreshold(value) } }
    fun setHysteresis(value: Int) { viewModelScope.launch { repo.setHysteresis(value) } }
}



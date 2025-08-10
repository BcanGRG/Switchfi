package com.bcan.switchfi.ui.theme

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val themeRepository: ThemeRepository
) : ViewModel() {
    private val state: StateFlow<Boolean> = themeRepository.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )
    val isDark: StateFlow<Boolean> get() = state

    fun setDarkMode(enabled: Boolean) {
        // Optimistic update by writing immediately; collector will emit same value
        viewModelScope.launch { themeRepository.setDarkMode(enabled) }
    }
}



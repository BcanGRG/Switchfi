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
    val isDark: StateFlow<Boolean> = themeRepository.isDarkMode.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = false
    )

    fun setDarkMode(enabled: Boolean) {
        viewModelScope.launch { themeRepository.setDarkMode(enabled) }
    }
}



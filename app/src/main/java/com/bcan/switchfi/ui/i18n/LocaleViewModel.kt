package com.bcan.switchfi.ui.i18n

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class LocaleViewModel @Inject constructor(
    private val repo: LocaleRepository
) : ViewModel() {
    val localeTag: StateFlow<String?> = repo.appLocale.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), null
    )

    fun setLocale(locale: Locale?) {
        viewModelScope.launch {
            repo.setAppLocaleTag(locale?.toLanguageTag())
        }
    }
}



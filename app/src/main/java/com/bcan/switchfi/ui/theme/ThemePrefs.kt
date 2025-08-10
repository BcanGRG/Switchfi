package com.bcan.switchfi.ui.theme

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val THEME_PREFS = "theme_prefs"
val Context.themeDataStore by preferencesDataStore(name = THEME_PREFS)

object ThemeKeys {
    val DARK_MODE = booleanPreferencesKey("dark_mode")
}

class ThemeRepository(private val context: Context) {
    val isDarkMode: Flow<Boolean> = context.themeDataStore.data.map { prefs ->
        prefs[ThemeKeys.DARK_MODE] ?: false
    }

    suspend fun setDarkMode(enabled: Boolean) {
        context.themeDataStore.edit { prefs: MutablePreferences ->
            prefs[ThemeKeys.DARK_MODE] = enabled
        }
    }
}



package com.bcan.switchfi.ui.settings

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val SETTINGS_PREFS = "settings_prefs"
val Context.settingsDataStore by preferencesDataStore(name = SETTINGS_PREFS)

object SettingsKeys {
    val RSSI_THRESHOLD = intPreferencesKey("rssi_threshold")
    val HYSTERESIS = intPreferencesKey("hysteresis")
    val AUTO_LEARN = androidx.datastore.preferences.core.booleanPreferencesKey("auto_learn")
}

class SettingsRepository(private val context: Context) {
    val rssiThreshold: Flow<Int> = context.settingsDataStore.data.map { it[SettingsKeys.RSSI_THRESHOLD] ?: -75 }
    val hysteresis: Flow<Int> = context.settingsDataStore.data.map { it[SettingsKeys.HYSTERESIS] ?: 8 }
    val autoLearn: Flow<Boolean> = context.settingsDataStore.data.map { it[SettingsKeys.AUTO_LEARN] ?: true }

    suspend fun setRssiThreshold(value: Int) {
        context.settingsDataStore.edit { prefs: MutablePreferences ->
            prefs[SettingsKeys.RSSI_THRESHOLD] = value
        }
    }

    suspend fun setHysteresis(value: Int) {
        context.settingsDataStore.edit { prefs: MutablePreferences ->
            prefs[SettingsKeys.HYSTERESIS] = value
        }
    }

    suspend fun setAutoLearn(enabled: Boolean) {
        context.settingsDataStore.edit { prefs: MutablePreferences ->
            prefs[SettingsKeys.AUTO_LEARN] = enabled
        }
    }
}



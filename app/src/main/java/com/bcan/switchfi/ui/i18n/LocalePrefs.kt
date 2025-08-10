package com.bcan.switchfi.ui.i18n

import android.content.Context
import android.os.LocaleList
import androidx.core.os.ConfigurationCompat
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

private const val LOCALE_PREFS = "locale_prefs"
val Context.localeDataStore by preferencesDataStore(name = LOCALE_PREFS)

object LocaleKeys {
    val APP_LOCALE = stringPreferencesKey("app_locale")
}

class LocaleRepository(private val context: Context) {
    val appLocale: Flow<String?> = context.localeDataStore.data.map { prefs ->
        prefs[LocaleKeys.APP_LOCALE]
    }

    suspend fun setAppLocaleTag(localeTag: String?) {
        context.localeDataStore.edit { prefs: MutablePreferences ->
            if (localeTag == null) {
                prefs.remove(LocaleKeys.APP_LOCALE)
            } else {
                prefs[LocaleKeys.APP_LOCALE] = localeTag
            }
        }
    }

    fun resolveCurrentLocale(): Locale {
        val confLocale = ConfigurationCompat.getLocales(context.resources.configuration)
        return confLocale[0] ?: Locale.getDefault()
    }
}



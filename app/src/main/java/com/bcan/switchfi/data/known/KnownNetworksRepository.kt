package com.bcan.switchfi.data.known

import android.content.Context
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.bcan.switchfi.data.suggestions.WifiSuggestionRepository
import com.bcan.switchfi.domain.model.KnownNetwork
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.coroutines.flow.firstOrNull

private const val KNOWN_NETWORKS_PREFS = "known_networks_prefs"
val Context.knownNetworksDataStore by preferencesDataStore(name = KNOWN_NETWORKS_PREFS)

private object KnownNetworksKeys {
    val JSON = stringPreferencesKey("known_networks_json")
}

@Singleton
class KnownNetworksRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val suggestionRepository: WifiSuggestionRepository
) {
    private val json by lazy { Json { ignoreUnknownKeys = true; encodeDefaults = true } }

    val knownNetworks: Flow<List<KnownNetwork>> = context.knownNetworksDataStore.data.map { prefs ->
        val raw = prefs[KnownNetworksKeys.JSON]
        if (raw.isNullOrBlank()) emptyList() else runCatching { json.decodeFromString<List<KnownNetwork>>(raw) }.getOrElse { emptyList() }
    }

    suspend fun add(network: KnownNetwork) {
        val updated = knownNetworks.firstOrEmpty() + network
        save(updated)
        suggestionRepository.addSuggestions(listOf(network))
    }

    suspend fun removeBySsid(ssid: String) {
        val current = knownNetworks.firstOrEmpty()
        val target = current.find { it.ssid == ssid } ?: return
        suggestionRepository.removeSuggestions(listOf(target))
        save(current.filterNot { it.ssid == ssid })
    }

    suspend fun overwriteAll(networks: List<KnownNetwork>) {
        save(networks)
        // Refresh suggestions with current set
        suggestionRepository.addSuggestions(networks)
    }

    private suspend fun save(list: List<KnownNetwork>) {
        val serialized = json.encodeToString(list)
        context.knownNetworksDataStore.edit { prefs: MutablePreferences ->
            prefs[KnownNetworksKeys.JSON] = serialized
        }
    }
}

private suspend fun Flow<List<KnownNetwork>>.firstOrEmpty(): List<KnownNetwork> =
    this.firstOrNull() ?: emptyList()



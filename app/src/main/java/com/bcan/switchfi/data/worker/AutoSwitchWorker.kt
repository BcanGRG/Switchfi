package com.bcan.switchfi.data.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.bcan.switchfi.data.known.KnownNetworksRepository
import com.bcan.switchfi.data.scan.WifiScanner
import com.bcan.switchfi.data.suggestions.WifiSuggestionRepository
import com.bcan.switchfi.domain.usecase.EvaluateAutoSwitchUseCase
import com.bcan.switchfi.domain.usecase.EvaluateSwitchInput
import com.bcan.switchfi.domain.usecase.NearbyNetwork
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.firstOrNull
import com.bcan.switchfi.ui.settings.SettingsRepository

@HiltWorker
class AutoSwitchWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted params: WorkerParameters,
    private val knownRepo: KnownNetworksRepository,
    private val scanner: WifiScanner,
    private val suggestions: WifiSuggestionRepository,
    private val settings: SettingsRepository
) : CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        if (!scanner.hasScanPermission()) return Result.success()

        val known = knownRepo.knownNetworks.firstOrNull() ?: emptyList()
        val rssi = settings.rssiThreshold.firstOrNull() ?: -75
        val hyst = settings.hysteresis.firstOrNull() ?: 8

        scanner.startScan()
        val results = scanner.getScanResults()
        val nearby = results.map { NearbyNetwork(it.SSID, it.level) }

        val usecase = EvaluateAutoSwitchUseCase()
        val eval = usecase.invoke(
            EvaluateSwitchInput(currentRssi = null, nearby = nearby, known = known, rssiThreshold = rssi, hysteresis = hyst)
        )
        if (eval.shouldSuggest && eval.targetSsid != null) {
            // Re-suggest known networks; platform handles the actual handoff.
            suggestions.addSuggestions(known)
        }
        return Result.success()
    }
}



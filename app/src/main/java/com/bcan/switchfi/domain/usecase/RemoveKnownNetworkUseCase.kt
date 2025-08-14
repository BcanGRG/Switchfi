package com.bcan.switchfi.domain.usecase

import com.bcan.switchfi.data.known.KnownNetworksRepository
import javax.inject.Inject

class RemoveKnownNetworkUseCase @Inject constructor(
    private val repository: KnownNetworksRepository
) {
    suspend operator fun invoke(ssid: String) {
        repository.removeBySsid(ssid)
    }
}



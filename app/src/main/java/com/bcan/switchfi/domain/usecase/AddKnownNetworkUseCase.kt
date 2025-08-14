package com.bcan.switchfi.domain.usecase

import com.bcan.switchfi.data.known.KnownNetworksRepository
import com.bcan.switchfi.domain.model.KnownNetwork
import javax.inject.Inject

class AddKnownNetworkUseCase @Inject constructor(
    private val repository: KnownNetworksRepository
) {
    suspend operator fun invoke(network: KnownNetwork) {
        repository.add(network)
    }
}



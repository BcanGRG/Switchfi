package com.bcan.switchfi

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import androidx.work.Configuration
import androidx.hilt.work.HiltWorkerFactory
import javax.inject.Inject

@HiltAndroidApp
class SwitchfiApp : Application(), Configuration.Provider {
    @Inject lateinit var workerFactory: HiltWorkerFactory

    override val workManagerConfiguration: Configuration by lazy {
        Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
    }
}



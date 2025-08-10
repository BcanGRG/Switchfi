package com.bcan.switchfi.di

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiManager
import com.bcan.switchfi.ui.theme.ThemeRepository
import com.bcan.switchfi.ui.i18n.LocaleRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideWifiManager(app: Application): WifiManager =
        app.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @Provides
    @Singleton
    fun provideConnectivityManager(app: Application): ConnectivityManager =
        app.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    @Provides
    @Singleton
    fun provideThemeRepository(app: Application): ThemeRepository = ThemeRepository(app)

    @Provides
    @Singleton
    fun provideLocaleRepository(app: Application): LocaleRepository = LocaleRepository(app)
}



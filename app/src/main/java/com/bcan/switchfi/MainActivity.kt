package com.bcan.switchfi

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.collectAsState
import androidx.navigation.compose.rememberNavController
import androidx.hilt.navigation.compose.hiltViewModel
import com.bcan.switchfi.ui.NetworksScreen
import com.bcan.switchfi.ui.navigation.AppNavGraph
import com.bcan.switchfi.ui.theme.SwitchfiTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val themeVm = hiltViewModel<com.bcan.switchfi.ui.theme.ThemeViewModel>()
            val localeVm = hiltViewModel<com.bcan.switchfi.ui.i18n.LocaleViewModel>()
            val localeTag = localeVm.localeTag.collectAsState().value
            androidx.compose.runtime.SideEffect {
                AppCompatDelegate.setApplicationLocales(
                    androidx.core.os.LocaleListCompat.forLanguageTags(localeTag ?: "")
                )
            }
            SwitchfiTheme(darkTheme = themeVm.isDark.collectAsState().value) {
                val navController = rememberNavController()
                AppNavGraph(navController)
            }
        }
    }
}
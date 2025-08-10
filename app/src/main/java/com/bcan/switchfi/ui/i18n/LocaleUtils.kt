package com.bcan.switchfi.ui.i18n

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

fun applyAppLocale(tag: String?) {
    val locales = if (tag.isNullOrBlank()) {
        LocaleListCompat.getEmptyLocaleList()
    } else {
        LocaleListCompat.forLanguageTags(tag)
    }
    AppCompatDelegate.setApplicationLocales(locales)
}



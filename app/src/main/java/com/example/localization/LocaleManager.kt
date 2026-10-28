package com.example.localization

import android.app.LocaleManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.LocaleList
import java.util.Locale

/**
 * Manages dynamic app-wide locale switching and resource localization.
 * Updates Android framework LocaleManager (API 33+) and context configuration
 * so strings.xml and system locale immediately switch between English and Hindi.
 */
object AppLocaleManager {

    fun getLanguageCode(languageNameOrCode: String): String {
        return when (languageNameOrCode.trim().lowercase()) {
            "hi", "hindi", "हिंदी" -> "hi"
            "es", "spanish", "español" -> "es"
            "ja", "japanese", "日本語" -> "ja"
            else -> "en"
        }
    }

    fun applyLocale(context: Context, language: String) {
        val langCode = getLanguageCode(language)
        val locale = Locale.forLanguageTag(langCode)
        Locale.setDefault(locale)

        // 1. Android 13+ (Tiramisu) system LocaleManager API
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val systemLocaleManager = context.getSystemService(LocaleManager::class.java)
            systemLocaleManager?.applicationLocales = LocaleList.forLanguageTags(langCode)
        }

        // 2. Resources configuration update for instant Composable string reload
        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)
        @Suppress("DEPRECATION")
        context.resources.updateConfiguration(config, context.resources.displayMetrics)
    }
}

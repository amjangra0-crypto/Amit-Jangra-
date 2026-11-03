package com.example.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.localization.DefaultLanguageConfig
import com.example.localization.GlobalLanguageSystem
import com.example.localization.LanguageSystemState
import com.example.localization.TranslationLanguageConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

val Context.languageSystemDataStore: DataStore<Preferences> by preferencesDataStore(name = "global_language_system_prefs")

/**
 * DataStore persistence manager for independent Default App UI Language
 * and Translation Language preferences.
 *
 * Guarantees:
 * - App UI language and Translation settings have distinct keys.
 * - Changing App UI language never mutates translation keys.
 * - Changing Translation settings never mutates App UI keys.
 */
class LanguagePreferencesDataStore(private val context: Context) {

    companion object {
        val KEY_SELECTED_APP_LANGUAGE = stringPreferencesKey("selected_app_language")
        val KEY_SYSTEM_FALLBACK_LANGUAGE = stringPreferencesKey("system_fallback_language")
        val KEY_TRANSLATION_SOURCE_LANGUAGE = stringPreferencesKey("translation_source_language")
        val KEY_TRANSLATION_TARGET_LANGUAGE = stringPreferencesKey("translation_target_language")
        val KEY_TRANSLATION_AUTO_DETECT = booleanPreferencesKey("translation_auto_detect")
    }

    val languageSystemStateFlow: Flow<LanguageSystemState> = context.languageSystemDataStore.data.map { prefs ->
        val systemTag = prefs[KEY_SYSTEM_FALLBACK_LANGUAGE] ?: Locale.getDefault().toLanguageTag()
        val appTag = prefs[KEY_SELECTED_APP_LANGUAGE] ?: "en"
        val transSource = prefs[KEY_TRANSLATION_SOURCE_LANGUAGE] ?: TranslationLanguageConfig.AUTO_DETECT
        val transTarget = prefs[KEY_TRANSLATION_TARGET_LANGUAGE] ?: "hi"
        val autoDetect = prefs[KEY_TRANSLATION_AUTO_DETECT] ?: true

        val resolvedApp = GlobalLanguageSystem.resolveSupportedTag(appTag) ?: GlobalLanguageSystem.SYSTEM_FALLBACK
        val resolvedFallback = GlobalLanguageSystem.resolveSupportedTag(systemTag) ?: GlobalLanguageSystem.SYSTEM_FALLBACK

        LanguageSystemState(
            defaultLanguage = DefaultLanguageConfig(
                systemFallbackTag = resolvedFallback,
                selectedAppTag = resolvedApp
            ),
            translationLanguage = TranslationLanguageConfig(
                sourceTag = if (autoDetect) {
                    TranslationLanguageConfig.AUTO_DETECT
                } else {
                    GlobalLanguageSystem.resolveSupportedTag(transSource) ?: TranslationLanguageConfig.AUTO_DETECT
                },
                targetTag = GlobalLanguageSystem.resolveSupportedTag(transTarget) ?: "hi",
                autoDetectSource = autoDetect
            )
        )
    }

    /**
     * Persists the App UI Language ONLY. Never touches translation preferences.
     */
    suspend fun saveAppLanguage(appTagOrName: String) {
        val canonical = GlobalLanguageSystem.requireSupportedTag(appTagOrName)
        context.languageSystemDataStore.edit { prefs ->
            prefs[KEY_SELECTED_APP_LANGUAGE] = canonical
        }
    }

    /**
     * Persists Translation Source and Target ONLY. Never touches App UI language.
     */
    suspend fun saveTranslationSettings(sourceTag: String, targetTag: String, autoDetect: Boolean) {
        val validTarget = GlobalLanguageSystem.requireSupportedTag(targetTag)
        val validSource = if (autoDetect || sourceTag.equals(TranslationLanguageConfig.AUTO_DETECT, ignoreCase = true)) {
            TranslationLanguageConfig.AUTO_DETECT
        } else {
            GlobalLanguageSystem.requireSupportedTag(sourceTag)
        }
        context.languageSystemDataStore.edit { prefs ->
            prefs[KEY_TRANSLATION_SOURCE_LANGUAGE] = validSource
            prefs[KEY_TRANSLATION_TARGET_LANGUAGE] = validTarget
            prefs[KEY_TRANSLATION_AUTO_DETECT] = autoDetect
        }
    }

    /**
     * Persists system fallback locale.
     */
    suspend fun saveSystemFallback(tag: String) {
        val canonical = GlobalLanguageSystem.resolveSupportedTag(tag) ?: GlobalLanguageSystem.SYSTEM_FALLBACK
        context.languageSystemDataStore.edit { prefs ->
            prefs[KEY_SYSTEM_FALLBACK_LANGUAGE] = canonical
        }
    }
}

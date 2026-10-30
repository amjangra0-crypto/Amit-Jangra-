package com.example.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.dubbingDataStore: DataStore<Preferences> by preferencesDataStore(name = "dubbing_language_settings")

/**
 * Model representing a dubbing language option for anime audio generation.
 */
data class DubbingLanguageOption(
    val code: String,
    val name: String,
    val nativeName: String,
    val flag: String,
    val description: String,
    val sampleDialogue: String,
    val animeVibe: String,
    val ttsLocaleTag: String
)

/**
 * DataStore persistence manager for preferred anime dubbing languages and voice mood filters.
 * Persists user choices asynchronously using Jetpack DataStore Preferences.
 */
class DubbingLanguageDataStore(private val context: Context) {

    companion object {
        val KEY_PREFERRED_DUBBING_LANGUAGE = stringPreferencesKey("preferred_dubbing_language")
        val KEY_PREFERRED_VOICE_MOOD = stringPreferencesKey("preferred_voice_mood")

        val SUPPORTED_DUBBING_LANGUAGES = listOf(
            DubbingLanguageOption(
                code = "ja",
                name = "Japanese",
                nativeName = "日本語 (Japanese)",
                flag = "🇯🇵",
                description = "Authentic Tokyo anime voice acting with expressive emotional inflections & battle shouts",
                sampleDialogue = "俺たちの冒険はまだ始まったばかりだ！諦めるな！",
                animeVibe = "Original Seiyuu Experience",
                ttsLocaleTag = "ja"
            ),
            DubbingLanguageOption(
                code = "hi",
                name = "Hindi",
                nativeName = "हिन्दी (Hindi)",
                flag = "🇮🇳",
                description = "High-energy heroic dubbing with dynamic Hindi anime theatrical cadence & punchy dialogues",
                sampleDialogue = "हमारा रोमांच तो अभी शुरू हुआ है! कभी हार मत मानो!",
                animeVibe = "Desi Shonen Localization",
                ttsLocaleTag = "hi"
            ),
            DubbingLanguageOption(
                code = "en",
                name = "English",
                nativeName = "English (US/UK)",
                flag = "🇺🇸",
                description = "Crisp international anime dubbing with clear narrative articulation & cinematic delivery",
                sampleDialogue = "Our anime journey has just begun! Stand together and unleash your true power!",
                animeVibe = "Global Dub & Simulcast",
                ttsLocaleTag = "en"
            ),
            DubbingLanguageOption(
                code = "es",
                name = "Spanish",
                nativeName = "Español (Castellano/Latino)",
                flag = "🇪🇸",
                description = "Passionate and vibrant Spanish voice-overs for dramatic anime storytelling & emotion",
                sampleDialogue = "¡Nuestra aventura acaba de comenzar! ¡Nunca te rindas, amigo!",
                animeVibe = "Ibero-American Anime Cast",
                ttsLocaleTag = "es"
            ),
            DubbingLanguageOption(
                code = "de",
                name = "German",
                nativeName = "Deutsch",
                flag = "🇩🇪",
                description = "Precise, powerful German theatrical voice acting for epic fantasy and mecha anime",
                sampleDialogue = "Unsere epische Reise hat gerade erst begonnen! Gemeinsam sind wir unbesiegbar!",
                animeVibe = "European Mecha & Epic Dub",
                ttsLocaleTag = "de"
            ),
            DubbingLanguageOption(
                code = "fr",
                name = "French",
                nativeName = "Français",
                flag = "🇫🇷",
                description = "Expressive, poetic French voice dubbing with rich emotional depth and romantic flair",
                sampleDialogue = "Notre aventure ne fait que commencer ! Restons unis jusqu'au bout !",
                animeVibe = "Cinematic French Dubbing",
                ttsLocaleTag = "fr"
            ),
            DubbingLanguageOption(
                code = "ko",
                name = "Korean",
                nativeName = "한국어 (Korean)",
                flag = "🇰🇷",
                description = "Vibrant Korean webtoon style dubbing with dynamic cadence and heroic vocal styling",
                sampleDialogue = "우리의 모험은 이제 막 시작되었어! 절대 포기하지 마!",
                animeVibe = "K-Animation & Webtoon Dub",
                ttsLocaleTag = "ko"
            ),
            DubbingLanguageOption(
                code = "it",
                name = "Italian",
                nativeName = "Italiano",
                flag = "🇮🇹",
                description = "Melodic and dramatic Italian voice artistry for legendary anime adventures",
                sampleDialogue = "La nostra avventura è appena iniziata! Non arrenderti mai!",
                animeVibe = "Mediterranean Anime Vibe",
                ttsLocaleTag = "it"
            )
        )

        fun findByName(name: String): DubbingLanguageOption {
            return SUPPORTED_DUBBING_LANGUAGES.firstOrNull {
                it.name.equals(name, ignoreCase = true) || it.nativeName.contains(name, ignoreCase = true)
            } ?: SUPPORTED_DUBBING_LANGUAGES.first()
        }
    }

    /**
     * Flow of preferred dubbing language, defaulted to "Japanese" for authentic anime experience.
     */
    val preferredDubbingLanguage: Flow<String> = context.dubbingDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_PREFERRED_DUBBING_LANGUAGE] ?: "Japanese"
        }

    /**
     * Persists user's preferred dubbing language to DataStore.
     */
    suspend fun savePreferredDubbingLanguage(language: String) {
        context.dubbingDataStore.edit { preferences ->
            preferences[KEY_PREFERRED_DUBBING_LANGUAGE] = language
        }
    }

    /**
     * Flow of preferred voice mood filter, defaulted to "Natural".
     */
    val preferredVoiceMood: Flow<String> = context.dubbingDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            preferences[KEY_PREFERRED_VOICE_MOOD] ?: "Natural"
        }

    /**
     * Persists user's preferred voice mood filter to DataStore.
     */
    suspend fun savePreferredVoiceMood(mood: String) {
        context.dubbingDataStore.edit { preferences ->
            preferences[KEY_PREFERRED_VOICE_MOOD] = mood
        }
    }
}

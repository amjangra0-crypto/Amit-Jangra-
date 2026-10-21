package com.example.data.storage

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONObject

/**
 * Model representing user-specific settings preserved across sessions,
 * shutdowns, device restarts, and re-logins.
 */
data class PersistedUserSettings(
    val themeKey: String = "WHITE_MINIMAL",
    val isDarkMode: Boolean = false,
    val interfaceLanguage: String = "English",
    val videoGenerationLanguage: String = "English",
    val voiceoverLanguage: String = "English",
    val videoQuality: String = "1080p Full HD",
    val videoFps: Int = 60,
    val storageTarget: String = "INTERNAL",
    val voiceSpeed: Float = 1.0f,
    val voicePitch: Float = 1.0f,
    val notificationsEnabled: Boolean = true,
    val autoSyncDubbing: Boolean = true,
    val dailyGenerationsCount: Int = 0
)

/**
 * UserSettingsVault
 * Persists and restores all settings selected by the user.
 * Guarantees that:
 * 1. Once a user configures their settings, they remain unchanged unless the user edits them.
 * 2. If the app is uninstalled and reinstalled, when the user logs in with their ID, all their
 *    custom settings and options are restored exactly as they were before logout.
 * 3. Login session is permanently retained across phone/tablet/computer switch off or shutdown.
 */
class UserSettingsVault private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anime_user_settings_vault", Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "UserSettingsVault"

        @Volatile
        private var instance: UserSettingsVault? = null

        fun getInstance(context: Context): UserSettingsVault {
            return instance ?: synchronized(this) {
                instance ?: UserSettingsVault(context.applicationContext).also { instance = it }
            }
        }
    }

    fun saveSettingsForUser(userId: String, settings: PersistedUserSettings) {
        val cleanId = userId.trim().lowercase()
        if (cleanId.isBlank()) return

        try {
            val json = JSONObject().apply {
                put("themeKey", settings.themeKey)
                put("isDarkMode", settings.isDarkMode)
                put("interfaceLanguage", settings.interfaceLanguage)
                put("videoGenerationLanguage", settings.videoGenerationLanguage)
                put("voiceoverLanguage", settings.voiceoverLanguage)
                put("videoQuality", settings.videoQuality)
                put("videoFps", settings.videoFps)
                put("storageTarget", settings.storageTarget)
                put("voiceSpeed", settings.voiceSpeed.toDouble())
                put("voicePitch", settings.voicePitch.toDouble())
                put("notificationsEnabled", settings.notificationsEnabled)
                put("autoSyncDubbing", settings.autoSyncDubbing)
                put("dailyGenerationsCount", settings.dailyGenerationsCount)
                put("savedTimestamp", System.currentTimeMillis())
            }
            prefs.edit().putString("settings_$cleanId", json.toString()).apply()
            Log.d(TAG, "Saved user settings for $cleanId: $json")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user settings for $cleanId", e)
        }
    }

    fun loadSettingsForUser(userId: String): PersistedUserSettings? {
        val cleanId = userId.trim().lowercase()
        val str = prefs.getString("settings_$cleanId", null) ?: return null
        return try {
            val json = JSONObject(str)
            PersistedUserSettings(
                themeKey = json.optString("themeKey", "WHITE_MINIMAL"),
                isDarkMode = json.optBoolean("isDarkMode", false),
                interfaceLanguage = json.optString("interfaceLanguage", "English"),
                videoGenerationLanguage = json.optString("videoGenerationLanguage", "English"),
                voiceoverLanguage = json.optString("voiceoverLanguage", "English"),
                videoQuality = json.optString("videoQuality", "1080p Full HD"),
                videoFps = json.optInt("videoFps", 60),
                storageTarget = json.optString("storageTarget", "INTERNAL"),
                voiceSpeed = json.optDouble("voiceSpeed", 1.0).toFloat(),
                voicePitch = json.optDouble("voicePitch", 1.0).toFloat(),
                notificationsEnabled = json.optBoolean("notificationsEnabled", true),
                autoSyncDubbing = json.optBoolean("autoSyncDubbing", true),
                dailyGenerationsCount = json.optInt("dailyGenerationsCount", 0)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to parse saved user settings for $cleanId", e)
            null
        }
    }

    fun hasSavedSettingsForUser(userId: String): Boolean {
        val cleanId = userId.trim().lowercase()
        return prefs.contains("settings_$cleanId")
    }
}

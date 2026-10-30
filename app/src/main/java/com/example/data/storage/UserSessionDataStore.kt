package com.example.data.storage

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.data.model.AuthProvider
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import java.io.IOException

val Context.userSessionDataStore: DataStore<Preferences> by preferencesDataStore(name = "user_session_datastore")

/**
 * Robust User Session Persistence Manager using Jetpack DataStore Preferences.
 * Stores login state, user ID, tokens, profile details, and role securely
 * so that users remain logged in across app launches and device reboots.
 */
class UserSessionDataStore(private val context: Context) {

    companion object {
        val KEY_IS_LOGGED_IN = booleanPreferencesKey("session_is_logged_in")
        val KEY_IS_OWNER = booleanPreferencesKey("session_is_owner")
        val KEY_USER_ID = stringPreferencesKey("session_user_id")
        val KEY_DISPLAY_NAME = stringPreferencesKey("session_display_name")
        val KEY_EMAIL = stringPreferencesKey("session_email")
        val KEY_PHONE = stringPreferencesKey("session_phone")
        val KEY_AVATAR = stringPreferencesKey("session_avatar")
        val KEY_BIO = stringPreferencesKey("session_bio")
        val KEY_SPECIALTY = stringPreferencesKey("session_specialty")
        val KEY_AUTH_PROVIDER = stringPreferencesKey("session_auth_provider")
        val KEY_SUBSCRIPTION_PLAN = stringPreferencesKey("session_subscription_plan")
        val KEY_SUBSCRIPTION_EXPIRY = stringPreferencesKey("session_subscription_expiry")
        val KEY_JOINED_DATE = stringPreferencesKey("session_joined_date")
        val KEY_PROJECTS_COUNT = intPreferencesKey("session_projects_count")
        val KEY_VIDEOS_COUNT = intPreferencesKey("session_videos_count")
        val KEY_VOICE_MINUTES = intPreferencesKey("session_voice_minutes")
        val KEY_NOTIFICATIONS_ENABLED = booleanPreferencesKey("session_notifications_enabled")
        val KEY_AUTO_SYNC_DUBBING = booleanPreferencesKey("session_auto_sync_dubbing")
    }

    val userSessionFlow: Flow<UserProfile?> = context.userSessionDataStore.data
        .catch { exception ->
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { prefs ->
            val isLoggedIn = prefs[KEY_IS_LOGGED_IN] ?: false
            if (!isLoggedIn) {
                null
            } else {
                val isOwner = prefs[KEY_IS_OWNER] ?: false
                val providerStr = prefs[KEY_AUTH_PROVIDER] ?: "GUEST"
                val authProvider = try {
                    AuthProvider.valueOf(providerStr)
                } catch (_: Exception) {
                    if (prefs[KEY_EMAIL]?.isNotBlank() == true) AuthProvider.GMAIL else AuthProvider.MOBILE_OTP
                }

                UserProfile(
                    userId = prefs[KEY_USER_ID] ?: if (isOwner) "owner_amjangra0" else "user_session",
                    displayName = prefs[KEY_DISPLAY_NAME] ?: if (isOwner) "Aman Jangra" else "Anime Creator",
                    email = prefs[KEY_EMAIL],
                    phoneNumber = prefs[KEY_PHONE],
                    avatarDrawableName = prefs[KEY_AVATAR] ?: if (isOwner) "char_shonen_hero" else "char_anime_heroine",
                    bio = prefs[KEY_BIO] ?: "",
                    creatorSpecialty = prefs[KEY_SPECIALTY] ?: if (isOwner) "Anime Film Director" else "Anime Creator",
                    isOwner = isOwner,
                    authProvider = authProvider,
                    subscriptionPlan = prefs[KEY_SUBSCRIPTION_PLAN] ?: if (isOwner) "VIP OWNER LIFETIME" else "Free Explorer",
                    subscriptionExpiry = prefs[KEY_SUBSCRIPTION_EXPIRY] ?: if (isOwner) "Lifetime Unlimited" else "Active",
                    isLoggedIn = true,
                    joinedDate = prefs[KEY_JOINED_DATE] ?: "Recent",
                    projectsCreated = prefs[KEY_PROJECTS_COUNT] ?: 0,
                    videosRendered = prefs[KEY_VIDEOS_COUNT] ?: 0,
                    voiceMinutesUsed = prefs[KEY_VOICE_MINUTES] ?: 0,
                    notificationEnabled = prefs[KEY_NOTIFICATIONS_ENABLED] ?: true,
                    autoSyncDubbing = prefs[KEY_AUTO_SYNC_DUBBING] ?: true
                )
            }
        }

    val isLoggedInFlow: Flow<Boolean> = context.userSessionDataStore.data
        .catch { exception ->
            if (exception is IOException) emit(emptyPreferences()) else throw exception
        }
        .map { prefs ->
            prefs[KEY_IS_LOGGED_IN] ?: false
        }

    suspend fun saveUserSession(profile: UserProfile) {
        context.userSessionDataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = profile.isLoggedIn
            prefs[KEY_IS_OWNER] = profile.isOwner
            prefs[KEY_USER_ID] = profile.userId
            prefs[KEY_DISPLAY_NAME] = profile.displayName
            profile.email?.let { prefs[KEY_EMAIL] = it } ?: prefs.remove(KEY_EMAIL)
            profile.phoneNumber?.let { prefs[KEY_PHONE] = it } ?: prefs.remove(KEY_PHONE)
            prefs[KEY_AVATAR] = profile.avatarDrawableName
            prefs[KEY_BIO] = profile.bio
            prefs[KEY_SPECIALTY] = profile.creatorSpecialty
            prefs[KEY_AUTH_PROVIDER] = profile.authProvider.name
            prefs[KEY_SUBSCRIPTION_PLAN] = profile.subscriptionPlan
            prefs[KEY_SUBSCRIPTION_EXPIRY] = profile.subscriptionExpiry
            prefs[KEY_JOINED_DATE] = profile.joinedDate
            prefs[KEY_PROJECTS_COUNT] = profile.projectsCreated
            prefs[KEY_VIDEOS_COUNT] = profile.videosRendered
            prefs[KEY_VOICE_MINUTES] = profile.voiceMinutesUsed
            prefs[KEY_NOTIFICATIONS_ENABLED] = profile.notificationEnabled
            prefs[KEY_AUTO_SYNC_DUBBING] = profile.autoSyncDubbing
        }
    }

    suspend fun clearSession() {
        context.userSessionDataStore.edit { prefs ->
            prefs.clear()
            prefs[KEY_IS_LOGGED_IN] = false
        }
    }

    suspend fun getSavedSession(): UserProfile? {
        return try {
            userSessionFlow.first()
        } catch (_: Exception) {
            null
        }
    }
}

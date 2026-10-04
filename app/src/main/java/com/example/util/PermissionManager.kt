package com.example.util

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.core.content.ContextCompat
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

// Extension property for DataStore instance on Context
val Context.permissionDataStore: DataStore<Preferences> by preferencesDataStore(name = "app_permission_preferences")

/**
 * PermissionManager:
 * - Tracks the 'already requested' state in DataStore for all requested permissions (Microphone, Camera, Storage).
 * - Exposes helpers to check if a specific permission has been granted by system and/or enabled by user.
 * - Enforces that system permission dialogs are requested strictly ONCE after app installation.
 * - Provides manual toggle state (Allow / Deallow) so users have 100% control in Settings.
 */
class PermissionManager(private val context: Context) {

    companion object {
        // Keys for tracking whether the system prompt was already displayed once after install
        val KEY_INSTALL_PERMISSIONS_PROMPTED = booleanPreferencesKey("already_requested_on_install")
        val KEY_REQUESTED_MICROPHONE = booleanPreferencesKey("already_requested_microphone")
        val KEY_REQUESTED_CAMERA = booleanPreferencesKey("already_requested_camera")
        val KEY_REQUESTED_STORAGE = booleanPreferencesKey("already_requested_storage")

        // Keys for user manual toggle states inside app settings (Allow / Deallow)
        val KEY_USER_ALLOW_MICROPHONE = booleanPreferencesKey("user_allow_microphone")
        val KEY_USER_ALLOW_CAMERA = booleanPreferencesKey("user_allow_camera")
        val KEY_USER_ALLOW_STORAGE = booleanPreferencesKey("user_allow_storage")

        // Singleton instance helper
        @Volatile
        private var INSTANCE: PermissionManager? = null

        fun getInstance(context: Context): PermissionManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: PermissionManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    // =========================================================================
    // SYSTEM PERMISSION STATE HELPERS
    // =========================================================================

    /**
     * Checks if a specific Android system permission is currently granted.
     */
    fun isPermissionGranted(permission: String): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun isMicrophoneGranted(): Boolean = isPermissionGranted(Manifest.permission.RECORD_AUDIO)

    fun isCameraGranted(): Boolean = isPermissionGranted(Manifest.permission.CAMERA)

    fun isStorageGranted(): Boolean {
        // On Android 13+ (API 33+), scoped storage / Photo Picker is zero-permission
        return if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            true
        } else {
            isPermissionGranted(Manifest.permission.READ_EXTERNAL_STORAGE)
        }
    }

    // =========================================================================
    // DATASTORE: 'ALREADY REQUESTED' TRACKING (STRICTLY ONCE ON INSTALL)
    // =========================================================================

    /**
     * Flow emitting whether the install-time permission request has already occurred.
     */
    val alreadyRequestedOnInstallFlow: Flow<Boolean> = context.permissionDataStore.data.map { prefs ->
        prefs[KEY_INSTALL_PERMISSIONS_PROMPTED] ?: false
    }

    /**
     * Synchronous or suspend check whether permissions were already requested after installation.
     */
    suspend fun hasAlreadyRequestedOnInstall(): Boolean {
        return context.permissionDataStore.data.first()[KEY_INSTALL_PERMISSIONS_PROMPTED] ?: false
    }

    fun hasAlreadyRequestedOnInstallBlocking(): Boolean {
        return runBlocking { hasAlreadyRequestedOnInstall() }
    }

    /**
     * Records in DataStore that the initial install permission request was prompted.
     * Ensures the app will NEVER prompt system permission popups automatically again.
     */
    suspend fun markAlreadyRequestedOnInstall() {
        context.permissionDataStore.edit { prefs ->
            prefs[KEY_INSTALL_PERMISSIONS_PROMPTED] = true
            prefs[KEY_REQUESTED_MICROPHONE] = true
            prefs[KEY_REQUESTED_CAMERA] = true
            prefs[KEY_REQUESTED_STORAGE] = true
        }
        // Also update backup SharedPreferences for instant disk sync
        try {
            val sp = context.getSharedPreferences("anime_app_permissions_prefs", Context.MODE_PRIVATE)
            sp.edit().putBoolean("permissions_requested_on_install", true).commit()
        } catch (_: Throwable) {}
    }

    /**
     * Checks whether the app should trigger the one-time install permission request.
     * Returns true ONLY on fresh install if not yet prompted and not yet granted.
     */
    suspend fun shouldRequestPermissionOnInstall(): Boolean {
        val alreadyPrompted = hasAlreadyRequestedOnInstall()
        if (alreadyPrompted) return false

        // Check legacy marker fallback
        try {
            val marker = java.io.File(context.filesDir, ".mic_permission_installed_marker")
            if (marker.exists()) {
                markAlreadyRequestedOnInstall()
                return false
            }
        } catch (_: Throwable) {}

        // If microphone is already granted (e.g. system restored), mark as handled
        if (isMicrophoneGranted()) {
            markAlreadyRequestedOnInstall()
            return false
        }

        return true
    }

    // =========================================================================
    // DATASTORE: USER MANUAL TOGGLE CONTROLS (ALLOW / DEALLOW)
    // =========================================================================

    /**
     * Flow of user preference for Microphone permission (defaults to system granted state).
     */
    val userMicrophoneAllowedFlow: Flow<Boolean> = context.permissionDataStore.data.map { prefs ->
        prefs[KEY_USER_ALLOW_MICROPHONE] ?: isMicrophoneGranted()
    }

    /**
     * Flow of user preference for Camera permission.
     */
    val userCameraAllowedFlow: Flow<Boolean> = context.permissionDataStore.data.map { prefs ->
        prefs[KEY_USER_ALLOW_CAMERA] ?: isCameraGranted()
    }

    /**
     * Flow of user preference for Storage permission.
     */
    val userStorageAllowedFlow: Flow<Boolean> = context.permissionDataStore.data.map { prefs ->
        prefs[KEY_USER_ALLOW_STORAGE] ?: isStorageGranted()
    }

    /**
     * Allows or deallows Microphone at user preference level.
     */
    suspend fun setUserMicrophoneAllowed(allowed: Boolean) {
        context.permissionDataStore.edit { prefs ->
            prefs[KEY_USER_ALLOW_MICROPHONE] = allowed
        }
    }

    /**
     * Allows or deallows Camera at user preference level.
     */
    suspend fun setUserCameraAllowed(allowed: Boolean) {
        context.permissionDataStore.edit { prefs ->
            prefs[KEY_USER_ALLOW_CAMERA] = allowed
        }
    }

    /**
     * Allows or deallows Storage at user preference level.
     */
    suspend fun setUserStorageAllowed(allowed: Boolean) {
        context.permissionDataStore.edit { prefs ->
            prefs[KEY_USER_ALLOW_STORAGE] = allowed
        }
    }

    /**
     * Checks if a feature should proceed with Microphone:
     * both user toggle must be enabled AND system permission granted.
     */
    suspend fun canUseMicrophone(): Boolean {
        val userAllowed = context.permissionDataStore.data.first()[KEY_USER_ALLOW_MICROPHONE] ?: true
        return userAllowed && isMicrophoneGranted()
    }

    suspend fun canUseCamera(): Boolean {
        val userAllowed = context.permissionDataStore.data.first()[KEY_USER_ALLOW_CAMERA] ?: true
        return userAllowed && isCameraGranted()
    }

    suspend fun canUseStorage(): Boolean {
        val userAllowed = context.permissionDataStore.data.first()[KEY_USER_ALLOW_STORAGE] ?: true
        return userAllowed && isStorageGranted()
    }

    /**
     * Opens Android System Application Settings screen directly so the user can
     * toggle system-level permissions at any time.
     */
    fun openAppSettings() {
        try {
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        } catch (_: Throwable) {
            val fallbackIntent = Intent(Settings.ACTION_SETTINGS).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(fallbackIntent)
        }
    }
}

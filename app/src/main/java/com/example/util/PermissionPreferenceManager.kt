package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Dedicated manager for single-prompt install/onboarding permissions.
 * Ensures microphone/camera/storage permissions are ONLY requested once upon initial app installation/first run,
 * and NEVER on subsequent app opens or launches, by verifying multiple persistent storage markers.
 */
object PermissionPreferenceManager {
    private const val PREFS_NAME = "anime_app_permissions_prefs"
    private const val BACKUP_PREFS_NAME = "app_settings_prefs"
    private const val PERMANENT_PREFS_NAME = "app_permanent_flags"
    private const val KEY_RECORDED_ON_INSTALL = "permissions_requested_on_install"
    private const val MARKER_FILE_NAME = ".mic_permission_installed_marker"
    private const val CACHE_MARKER_FILE_NAME = ".perm_install_completed"

    // Process-level memory guard so within the same process it never prompts more than once
    @Volatile
    private var inMemoryPrompted: Boolean = false

    /**
     * Checks if permission request was already prompted or handled during installation or first run.
     * Evaluates multiple persistent sources: SharedPreferences, files directory marker, and cache marker.
     */
    fun hasRequestedOnInstall(context: Context): Boolean {
        if (inMemoryPrompted) return true

        try {
            val markerFile = java.io.File(context.filesDir, MARKER_FILE_NAME)
            if (markerFile.exists()) {
                inMemoryPrompted = true
                return true
            }
        } catch (_: Exception) {}

        try {
            val cacheMarker = java.io.File(context.cacheDir, CACHE_MARKER_FILE_NAME)
            if (cacheMarker.exists()) {
                inMemoryPrompted = true
                return true
            }
        } catch (_: Exception) {}

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val backupPrefs = context.getSharedPreferences(BACKUP_PREFS_NAME, Context.MODE_PRIVATE)
        val permPrefs = context.getSharedPreferences(PERMANENT_PREFS_NAME, Context.MODE_PRIVATE)

        val recorded = prefs.getBoolean(KEY_RECORDED_ON_INSTALL, false) ||
                backupPrefs.getBoolean(KEY_RECORDED_ON_INSTALL, false) ||
                permPrefs.getBoolean(KEY_RECORDED_ON_INSTALL, false)

        if (recorded) {
            inMemoryPrompted = true
        }
        return recorded
    }

    /**
     * Determines whether the app should trigger the initial installation permission request.
     * Strictly returns false if it has ever been launched or prompted before, or if permission
     * is already granted.
     */
    fun shouldTriggerInitialPermissionRequest(context: Context): Boolean {
        if (hasRequestedOnInstall(context)) {
            return false
        }
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (isGranted) {
            markPermissionRequestedOnInstall(context)
            return false
        }

        return true
    }

    /**
     * Permanently records that permission request was handled on initial install.
     * Uses synchronous commit and persistent disk markers across files and cache so it is never lost.
     */
    fun markPermissionRequestedOnInstall(context: Context) {
        inMemoryPrompted = true
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val backupPrefs = context.getSharedPreferences(BACKUP_PREFS_NAME, Context.MODE_PRIVATE)
            val permPrefs = context.getSharedPreferences(PERMANENT_PREFS_NAME, Context.MODE_PRIVATE)

            prefs.edit().putBoolean(KEY_RECORDED_ON_INSTALL, true).commit()
            backupPrefs.edit().putBoolean(KEY_RECORDED_ON_INSTALL, true).commit()
            permPrefs.edit().putBoolean(KEY_RECORDED_ON_INSTALL, true).commit()

            val markerFile = java.io.File(context.filesDir, MARKER_FILE_NAME)
            if (!markerFile.exists()) {
                markerFile.createNewFile()
                markerFile.writeText("permission_onboarding_completed_once")
            }

            val cacheMarker = java.io.File(context.cacheDir, CACHE_MARKER_FILE_NAME)
            if (!cacheMarker.exists()) {
                cacheMarker.createNewFile()
                cacheMarker.writeText("1")
            }

            // Sync with DataStore
            kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.IO).launch {
                try {
                    PermissionManager.getInstance(context).markAlreadyRequestedOnInstall()
                } catch (_: Throwable) {}
            }
        } catch (_: Exception) {}
    }
}

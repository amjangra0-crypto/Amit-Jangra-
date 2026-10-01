package com.example.util

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

/**
 * Dedicated manager for single-prompt install/onboarding permissions.
 * Ensures microphone permission is only triggered once upon initial installation,
 * and never on subsequent app launches, by checking SharedPreferences beforehand.
 */
object PermissionPreferenceManager {
    private const val PREFS_NAME = "anime_app_permissions_prefs"
    private const val BACKUP_PREFS_NAME = "app_settings_prefs"
    private const val KEY_RECORDED_ON_INSTALL = "permissions_requested_on_install"
    private const val MARKER_FILE_NAME = ".mic_permission_installed_marker"

    /**
     * Checks if permission request state was already prompted
     * or handled during initial installation/onboarding.
     * Evaluates multiple persistent sources: SharedPreferences and internal disk marker.
     */
    fun hasRequestedOnInstall(context: Context): Boolean {
        try {
            val markerFile = java.io.File(context.filesDir, MARKER_FILE_NAME)
            if (markerFile.exists()) return true
        } catch (_: Exception) {}

        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val backupPrefs = context.getSharedPreferences(BACKUP_PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_RECORDED_ON_INSTALL, false) ||
               backupPrefs.getBoolean(KEY_RECORDED_ON_INSTALL, false)
    }

    /**
     * Determines whether the app should trigger the initial permission request dialog.
     * Evaluates persistent state first: if already requested on install or if permission
     * is already granted, it strictly returns false to prevent re-prompting on every launch.
     */
    fun shouldTriggerInitialPermissionRequest(context: Context): Boolean {
        val alreadyRequested = hasRequestedOnInstall(context)
        if (alreadyRequested) {
            return false
        }
        val isGranted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        // If user already granted permission via system settings or prior setup, mark and don't prompt
        if (isGranted) {
            markPermissionRequestedOnInstall(context)
            return false
        }

        return true
    }

    /**
     * Permanently records that permission request was handled on initial install.
     * Uses synchronous commit and persistent disk marker so it is never lost.
     */
    fun markPermissionRequestedOnInstall(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val backupPrefs = context.getSharedPreferences(BACKUP_PREFS_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_RECORDED_ON_INSTALL, true).commit()
            backupPrefs.edit().putBoolean(KEY_RECORDED_ON_INSTALL, true).commit()

            val markerFile = java.io.File(context.filesDir, MARKER_FILE_NAME)
            if (!markerFile.exists()) {
                markerFile.createNewFile()
                markerFile.writeText("permission_onboarding_completed_once")
            }
        } catch (_: Exception) {}
    }
}

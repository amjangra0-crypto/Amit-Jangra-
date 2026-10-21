package com.example.update

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.InstallState
import com.google.android.play.core.install.InstallStateUpdatedListener
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.InstallStatus
import com.google.android.play.core.install.model.UpdateAvailability
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class InAppUpdateStatus(
    val isChecking: Boolean = false,
    val isUpdateAvailable: Boolean = false,
    val availableVersionCode: Int = 0,
    val installStatus: Int = InstallStatus.UNKNOWN,
    val bytesDownloaded: Long = 0L,
    val totalBytesToDownload: Long = 0L,
    val statusMessage: String = "Up to date with Google Play",
    val canCompleteUpdate: Boolean = false
)

/**
 * Manages Google Play Core In-App Updates API (Immediate & Flexible update flows).
 * Prompts users to download and install newer versions automatically when available on Google Play.
 */
class InAppUpdateManager private constructor(private val context: Context) {

    private val appUpdateManager: AppUpdateManager = AppUpdateManagerFactory.create(context)
    private var cachedAppUpdateInfo: AppUpdateInfo? = null

    private val _updateStatus = MutableStateFlow(InAppUpdateStatus())
    val updateStatus: StateFlow<InAppUpdateStatus> = _updateStatus.asStateFlow()

    private val installStateUpdatedListener: InstallStateUpdatedListener = InstallStateUpdatedListener { state: InstallState ->
        when (state.installStatus()) {
            InstallStatus.DOWNLOADING -> {
                val bytes = state.bytesDownloaded()
                val total = state.totalBytesToDownload()
                val percent = if (total > 0) (bytes * 100 / total) else 0
                _updateStatus.value = _updateStatus.value.copy(
                    installStatus = InstallStatus.DOWNLOADING,
                    bytesDownloaded = bytes,
                    totalBytesToDownload = total,
                    statusMessage = "Downloading update from Google Play ($percent%)...",
                    canCompleteUpdate = false
                )
            }
            InstallStatus.DOWNLOADED -> {
                _updateStatus.value = _updateStatus.value.copy(
                    installStatus = InstallStatus.DOWNLOADED,
                    statusMessage = "Update downloaded! Ready to install.",
                    canCompleteUpdate = true
                )
            }
            InstallStatus.INSTALLING -> {
                _updateStatus.value = _updateStatus.value.copy(
                    installStatus = InstallStatus.INSTALLING,
                    statusMessage = "Installing update...",
                    canCompleteUpdate = false
                )
            }
            InstallStatus.INSTALLED -> {
                _updateStatus.value = _updateStatus.value.copy(
                    installStatus = InstallStatus.INSTALLED,
                    statusMessage = "Update successfully installed!",
                    canCompleteUpdate = false,
                    isUpdateAvailable = false
                )
                appUpdateManager.unregisterListener(installStateUpdatedListener)
            }
            InstallStatus.FAILED -> {
                _updateStatus.value = _updateStatus.value.copy(
                    installStatus = InstallStatus.FAILED,
                    statusMessage = "Update failed. Please retry from Google Play.",
                    canCompleteUpdate = false
                )
            }
            InstallStatus.CANCELED -> {
                _updateStatus.value = _updateStatus.value.copy(
                    installStatus = InstallStatus.CANCELED,
                    statusMessage = "Update cancelled by user.",
                    canCompleteUpdate = false
                )
            }
            else -> {
                Log.d(TAG, "InstallState: ${state.installStatus()}")
            }
        }
    }

    init {
        appUpdateManager.registerListener(installStateUpdatedListener)
    }

    companion object {
        private const val TAG = "InAppUpdateManager"
        const val UPDATE_REQUEST_CODE = 8899

        @Volatile
        private var INSTANCE: InAppUpdateManager? = null

        fun getInstance(context: Context): InAppUpdateManager {
            return INSTANCE ?: synchronized(this) {
                val instance = InAppUpdateManager(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }

    /**
     * Checks Google Play for any available newer app version.
     */
    fun checkForUpdates(onResult: ((Boolean, String) -> Unit)? = null) {
        _updateStatus.value = _updateStatus.value.copy(isChecking = true, statusMessage = "Checking Google Play for updates...")
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { appUpdateInfo ->
            cachedAppUpdateInfo = appUpdateInfo
            val isAvailable = appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
            val canFlexible = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)
            val canImmediate = appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)

            if (isAvailable && (canFlexible || canImmediate)) {
                val newVersionCode = appUpdateInfo.availableVersionCode()
                val msg = "New version ($newVersionCode) available on Google Play!"
                _updateStatus.value = _updateStatus.value.copy(
                    isChecking = false,
                    isUpdateAvailable = true,
                    availableVersionCode = newVersionCode,
                    statusMessage = msg
                )
                onResult?.invoke(true, msg)
            } else if (appUpdateInfo.installStatus() == InstallStatus.DOWNLOADED) {
                _updateStatus.value = _updateStatus.value.copy(
                    isChecking = false,
                    canCompleteUpdate = true,
                    statusMessage = "Update downloaded and waiting to install."
                )
                onResult?.invoke(false, "Update downloaded and ready to install.")
            } else {
                _updateStatus.value = _updateStatus.value.copy(
                    isChecking = false,
                    isUpdateAvailable = false,
                    statusMessage = "App is up to date with Google Play."
                )
                onResult?.invoke(false, "App is up to date with Google Play.")
            }
        }.addOnFailureListener { exception ->
            Log.w(TAG, "Google Play In-App Updates check failed: ${exception.message}")
            _updateStatus.value = _updateStatus.value.copy(
                isChecking = false,
                statusMessage = "Connected to local release (Google Play updates ready upon release)."
            )
            onResult?.invoke(false, "Play Store check: ${exception.localizedMessage ?: "Development Mode"}")
        }
    }

    /**
     * Starts the in-app update flow (Flexible by default, or Immediate if required).
     */
    fun startUpdateFlow(activity: Activity, appUpdateType: Int = AppUpdateType.FLEXIBLE): Boolean {
        val info = cachedAppUpdateInfo ?: return false
        if (!info.isUpdateTypeAllowed(appUpdateType)) return false

        return try {
            val options = AppUpdateOptions.newBuilder(appUpdateType).build()
            appUpdateManager.startUpdateFlow(info, activity, options)
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start update flow", e)
            false
        }
    }

    /**
     * Completes flexible update and restarts the app.
     */
    fun completeUpdate() {
        appUpdateManager.completeUpdate()
    }

    fun unregister() {
        try {
            appUpdateManager.unregisterListener(installStateUpdatedListener)
        } catch (e: Exception) {
            Log.w(TAG, "Unregister listener warning: ${e.message}")
        }
    }
}

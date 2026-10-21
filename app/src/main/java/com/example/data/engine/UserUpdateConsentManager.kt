package com.example.data.engine

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

enum class UpdateConsentStatus {
    PENDING_USER_CONSENT,
    ACCEPTED,
    POSTPONED_OR_DECLINED
}

data class FeatureUpdateRollout(
    val updateId: String,
    val title: String,
    val description: String,
    val versionTag: String,
    val targetUserId: String? = null, // null or "ALL_USERS" for all random users, or specific userId/email
    val featureKey: String, // e.g. "COMMAND_LEARNING", "TREND_PULSE_HUB", "VIP_4K_EXPORT", "D3_LIP_SYNC", "SOCIAL_KNOWLEDGE"
    val enabledByOwnerTimestamp: Long = System.currentTimeMillis(),
    val status: UpdateConsentStatus = UpdateConsentStatus.PENDING_USER_CONSENT,
    val changelogItems: List<String> = emptyList(),
    val isOwnerForcedDirect: Boolean = false // Must always require user consent first as requested!
) {
    val isGlobal: Boolean
        get() = targetUserId == null || targetUserId.equals("ALL_USERS", ignoreCase = true)
}

/**
 * UserUpdateConsentManager
 * Ensures that whenever the Owner modifies options, enables features, or updates settings
 * (whether for random public users or targeted specific persons), it NEVER automatically
 * forces changes on the user's phone or computer without explicit consent.
 *
 * Instead, the update is sent as an explicit user option/notification dialog where the user
 * can review the changes and decide whether to update now or later.
 */
class UserUpdateConsentManager private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anime_update_consent_prefs", Context.MODE_PRIVATE)

    private val _rollouts = MutableStateFlow<List<FeatureUpdateRollout>>(loadRollouts())
    val rollouts: StateFlow<List<FeatureUpdateRollout>> = _rollouts.asStateFlow()

    private val _activePendingPrompt = MutableStateFlow<FeatureUpdateRollout?>(null)
    val activePendingPrompt: StateFlow<FeatureUpdateRollout?> = _activePendingPrompt.asStateFlow()

    companion object {
        private const val TAG = "UpdateConsentManager"
        private const val KEY_ROLLOUTS_JSON = "key_rollouts_json"

        @Volatile
        private var instance: UserUpdateConsentManager? = null

        fun getInstance(context: Context): UserUpdateConsentManager {
            return instance ?: synchronized(this) {
                instance ?: UserUpdateConsentManager(context.applicationContext).also { instance = it }
            }
        }
    }

    init {
        // Current build v2.4.0 features are pre-installed and accepted.
        // Update notification only triggers when Owner explicitly publishes a newer version/feature!
        val initial = if (_rollouts.value.isEmpty()) {
            listOf(
                FeatureUpdateRollout(
                    updateId = "upd_social_knowledge_v2",
                    title = "✨ Social Media Real-Time Learning & 3D Character Studio",
                    description = "Built-in v2.4.0 release with real-time Google & Instagram knowledge synthesis, 3D Character Studio Preview carousel, and D3 voice lip-sync visualization.",
                    versionTag = "v2.4.0",
                    targetUserId = "ALL_USERS",
                    featureKey = "SOCIAL_KNOWLEDGE",
                    status = UpdateConsentStatus.ACCEPTED,
                    changelogItems = listOf(
                        "3D-style carousel view in Character Studio Preview",
                        "Real-time D3 / Recharts voice intensity lip-sync visualizer",
                        "Real-time Google, Instagram & social media knowledge integration",
                        "Zero-bloat GZIP local compressed storage for video/movie generation",
                        "User Update Consent Gate: No silent background overrides"
                    )
                )
            )
        } else {
            _rollouts.value.map {
                if (it.updateId == "upd_social_knowledge_v2") it.copy(status = UpdateConsentStatus.ACCEPTED) else it
            }
        }
        _rollouts.value = initial
        saveRollouts(initial)
        _activePendingPrompt.value = null
    }

    /**
     * Called when the Owner toggles/enables an option for a specific person or all users.
     * Enqueues a pending update requiring explicit user approval.
     */
    fun enqueueOwnerRollout(
        featureKey: String,
        title: String,
        description: String,
        targetUserId: String? = null,
        changelog: List<String> = emptyList(),
        versionTag: String = "v${System.currentTimeMillis() % 1000}"
    ): FeatureUpdateRollout {
        val updateId = "upd_${featureKey.lowercase()}_${System.currentTimeMillis()}"
        val newRollout = FeatureUpdateRollout(
            updateId = updateId,
            title = title,
            description = description,
            versionTag = versionTag,
            targetUserId = targetUserId ?: "ALL_USERS",
            featureKey = featureKey,
            enabledByOwnerTimestamp = System.currentTimeMillis(),
            status = UpdateConsentStatus.PENDING_USER_CONSENT,
            changelogItems = changelog
        )

        val updatedList = _rollouts.value.filter { it.featureKey != featureKey || it.targetUserId != targetUserId } + newRollout
        _rollouts.value = updatedList
        saveRollouts(updatedList)

        Log.d(TAG, "Owner dispatched rollout $updateId for user: $targetUserId. Waiting for user acceptance.")
        return newRollout
    }

    /**
     * Checks if there is any pending update for the active user (specific or random/global)
     * and triggers the consent dialog if not yet accepted.
     */
    fun checkPendingUpdateForUser(userId: String?, userEmail: String?): FeatureUpdateRollout? {
        val pending = getPendingUpdateForUser(userId, userEmail)
        _activePendingPrompt.value = pending
        return pending
    }

    fun getPendingUpdateForUser(userId: String?, userEmail: String?): FeatureUpdateRollout? {
        return _rollouts.value.firstOrNull { rollout ->
            if (rollout.status != UpdateConsentStatus.PENDING_USER_CONSENT) return@firstOrNull false

            if (rollout.isGlobal) {
                true
            } else {
                val targets = listOfNotNull(rollout.targetUserId?.lowercase())
                val current = listOfNotNull(userId?.lowercase(), userEmail?.lowercase())
                targets.any { t -> current.any { c -> c == t || c.contains(t) } }
            }
        }
    }

    fun triggerUpdatePromptForUser(userId: String?, userEmail: String?): FeatureUpdateRollout? {
        val pending = getPendingUpdateForUser(userId, userEmail)
        _activePendingPrompt.value = pending
        return pending
    }

    /**
     * User tapped 'Update & Apply Now' in the dialog.
     */
    fun acceptUpdate(updateId: String) {
        val list = _rollouts.value.map {
            if (it.updateId == updateId) it.copy(status = UpdateConsentStatus.ACCEPTED) else it
        }
        _rollouts.value = list
        saveRollouts(list)
        if (_activePendingPrompt.value?.updateId == updateId) {
            _activePendingPrompt.value = null
        }
        Log.i(TAG, "User explicitly accepted update: $updateId. Feature is now activated on local device.")
    }

    /**
     * User tapped 'Later / Keep Current' in the dialog.
     */
    fun postponeUpdate(updateId: String) {
        val list = _rollouts.value.map {
            if (it.updateId == updateId) it.copy(status = UpdateConsentStatus.POSTPONED_OR_DECLINED) else it
        }
        _rollouts.value = list
        saveRollouts(list)
        if (_activePendingPrompt.value?.updateId == updateId) {
            _activePendingPrompt.value = null
        }
        Log.i(TAG, "User postponed update: $updateId. Device state kept intact.")
    }

    fun dismissActivePrompt() {
        _activePendingPrompt.value = null
    }

    fun isFeatureAccepted(featureKey: String, userId: String?, userEmail: String?): Boolean {
        // Find if this feature has an accepted rollout for this user
        val matching = _rollouts.value.filter { it.featureKey.equals(featureKey, ignoreCase = true) }
        if (matching.isEmpty()) return true // default active if no rollout required

        return matching.any { rollout ->
            val appliesToUser = rollout.isGlobal || listOfNotNull(userId, userEmail).any {
                it.equals(rollout.targetUserId, ignoreCase = true)
            }
            appliesToUser && rollout.status == UpdateConsentStatus.ACCEPTED
        }
    }

    private fun loadRollouts(): List<FeatureUpdateRollout> {
        val json = prefs.getString(KEY_ROLLOUTS_JSON, null) ?: return emptyList()
        return try {
            val arr = JSONArray(json)
            val list = mutableListOf<FeatureUpdateRollout>()
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val cl = mutableListOf<String>()
                val clArr = obj.optJSONArray("changelog")
                if (clArr != null) {
                    for (j in 0 until clArr.length()) cl.add(clArr.getString(j))
                }
                list.add(
                    FeatureUpdateRollout(
                        updateId = obj.getString("updateId"),
                        title = obj.getString("title"),
                        description = obj.getString("description"),
                        versionTag = obj.optString("versionTag", "v1.0.0"),
                        targetUserId = obj.optString("targetUserId", "ALL_USERS"),
                        featureKey = obj.optString("featureKey", "GENERAL"),
                        enabledByOwnerTimestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        status = try {
                            UpdateConsentStatus.valueOf(obj.optString("status", UpdateConsentStatus.PENDING_USER_CONSENT.name))
                        } catch (e: Exception) {
                            UpdateConsentStatus.PENDING_USER_CONSENT
                        },
                        changelogItems = cl
                    )
                )
            }
            list
        } catch (e: Exception) {
            Log.e(TAG, "Failed to load rollouts JSON", e)
            emptyList()
        }
    }

    private fun saveRollouts(list: List<FeatureUpdateRollout>) {
        try {
            val arr = JSONArray()
            for (r in list) {
                val obj = JSONObject()
                obj.put("updateId", r.updateId)
                obj.put("title", r.title)
                obj.put("description", r.description)
                obj.put("versionTag", r.versionTag)
                obj.put("targetUserId", r.targetUserId ?: "ALL_USERS")
                obj.put("featureKey", r.featureKey)
                obj.put("timestamp", r.enabledByOwnerTimestamp)
                obj.put("status", r.status.name)
                val clArr = JSONArray()
                r.changelogItems.forEach { clArr.put(it) }
                obj.put("changelog", clArr)
                arr.put(obj)
            }
            prefs.edit().putString(KEY_ROLLOUTS_JSON, arr.toString()).apply()
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save rollouts JSON", e)
        }
    }
}

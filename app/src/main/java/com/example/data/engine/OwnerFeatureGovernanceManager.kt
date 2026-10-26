package com.example.data.engine

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONObject

/**
 * Governed Features that the Owner can remotely control for all other users:
 * - Automation: Autonomous AI Director, Video Generator, Batch Scheduler
 * - TrendPlus: TrendPlus Live Forecasting, Viral Pattern Radar
 * - Learning Hub: Real-time Social Media (Google, FB, IG, Snapchat) Learning
 * - Social Research: Cross-Platform Research and Deep Topic Analysis
 */
enum class GovernedFeature(
    val key: String,
    val titleEn: String,
    val titleHi: String,
    val descriptionEn: String,
    val descriptionHi: String,
    val iconEmoji: String
) {
    AUTOMATION(
        key = "AUTOMATION",
        titleEn = "Autonomous AI Director & Scheduling",
        titleHi = "ऑटोनॉमस एआई डायरेक्टर व शेड्यूलिंग",
        descriptionEn = "1-Click autonomous anime generation from links, prompts and scheduled auto-director runs.",
        descriptionHi = "लिंक्स और प्रॉम्प्ट्स से 1-क्लिक ऑटोनॉमस एनिमे जनरेशन व शेड्यूलिंग।",
        iconEmoji = "⚡"
    ),
    TRENDPLUS(
        key = "TRENDPLUS",
        titleEn = "TrendPlus Viral Radar & Forecasting",
        titleHi = "ट्रेंडप्लस वायरल राडार व भविष्यवाणियाँ",
        descriptionEn = "Live social pulse tracking, viral prediction score, and cross-platform trend horizon.",
        descriptionHi = "लाइव सोशल ट्रेंड्स, वायरल प्रेडिक्शन स्कोर व ट्रेंड होराइजन।",
        iconEmoji = "🔮"
    ),
    LEARNING_HUB(
        key = "LEARNING_HUB",
        titleEn = "Social Knowledge & Learning Hub",
        titleHi = "सोशल नॉलेज व लर्निंग हब",
        descriptionEn = "Real-time self-learning from Google, Facebook, Instagram, Snapchat with compressed vault.",
        descriptionHi = "गूगल, फेसबुक, इंस्टाग्राम, स्नैपचैट से रियल-टाइम लर्निंग व कम्प्रेश्ड स्टोरेज।",
        iconEmoji = "🧠"
    ),
    SOCIAL_RESEARCH(
        key = "SOCIAL_RESEARCH",
        titleEn = "Multi-Platform Research & Analysis",
        titleHi = "मल्टी-प्लेटफॉर्म रिसर्च व एनालिसिस",
        descriptionEn = "Deep topic breakdown across global social networks with actionable cinematic suggestions.",
        descriptionHi = "ग्लोबल सोशल नेटवर्क्स पर डीप टॉपिक एनालिसिस व सिनेमैटिक सुझाव।",
        iconEmoji = "📊"
    )
}

data class FeatureGovernanceState(
    val feature: GovernedFeature,
    val isOwnerEnabled: Boolean = true,
    val isUserUpdatedAndAccepted: Boolean = true
) {
    /**
     * For regular users:
     * - If owner disabled: False immediately in real-time.
     * - If owner enabled: Requires user to have updated the app.
     */
    fun isAvailableForUser(isOwner: Boolean): Boolean {
        if (isOwner) return true // Owner always has unrestricted access
        if (!isOwnerEnabled) return false // Immediately disabled in real-time
        return isUserUpdatedAndAccepted // Enabled by owner, accessible only if updated
    }
}

/**
 * OwnerFeatureGovernanceManager
 * Enforces the strict rule:
 * 1. Owner can enable/disable Automation, TrendPlus, Learning Hub, Social Research.
 * 2. When Owner DISABLES: it immediately and in real-time shuts down for all regular users.
 * 3. When Owner ENABLES: regular users CANNOT use it until they update the app (enqueued as required update).
 */
class OwnerFeatureGovernanceManager private constructor(private val context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anime_feature_governance_prefs", Context.MODE_PRIVATE)

    private val _governanceStates = MutableStateFlow<Map<GovernedFeature, FeatureGovernanceState>>(loadStates())
    val governanceStates: StateFlow<Map<GovernedFeature, FeatureGovernanceState>> = _governanceStates.asStateFlow()

    companion object {
        private const val TAG = "FeatureGovernanceMgr"
        private const val PREF_KEY_GOVERNANCE_JSON = "key_governance_json"

        @Volatile
        private var instance: OwnerFeatureGovernanceManager? = null

        fun getInstance(context: Context): OwnerFeatureGovernanceManager {
            return instance ?: synchronized(this) {
                instance ?: OwnerFeatureGovernanceManager(context.applicationContext).also { instance = it }
            }
        }
    }

    private fun loadStates(): Map<GovernedFeature, FeatureGovernanceState> {
        val map = mutableMapOf<GovernedFeature, FeatureGovernanceState>()
        val jsonStr = prefs.getString(PREF_KEY_GOVERNANCE_JSON, null)
        val json = if (!jsonStr.isNullOrBlank()) {
            try { JSONObject(jsonStr) } catch (_: Exception) { JSONObject() }
        } else {
            JSONObject()
        }

        for (feat in GovernedFeature.entries) {
            val featObj = json.optJSONObject(feat.key)
            val ownerEnabled = featObj?.optBoolean("ownerEnabled", true) ?: true
            val userUpdated = featObj?.optBoolean("userUpdated", true) ?: true
            map[feat] = FeatureGovernanceState(
                feature = feat,
                isOwnerEnabled = ownerEnabled,
                isUserUpdatedAndAccepted = userUpdated
            )
        }
        return map
    }

    private fun persistStates(map: Map<GovernedFeature, FeatureGovernanceState>) {
        val json = JSONObject()
        for ((feat, state) in map) {
            val obj = JSONObject()
            obj.put("ownerEnabled", state.isOwnerEnabled)
            obj.put("userUpdated", state.isUserUpdatedAndAccepted)
            json.put(feat.key, obj)
        }
        prefs.edit().putString(PREF_KEY_GOVERNANCE_JSON, json.toString()).apply()
        _governanceStates.value = map
    }

    /**
     * Owner toggles feature on or off:
     * - If turning OFF: feature is disabled for users instantly in real-time.
     * - If turning ON: dispatches rollout update so users cannot use it until they update.
     */
    fun setFeatureEnabledByOwner(
        feature: GovernedFeature,
        enabled: Boolean,
        userConsentMgr: UserUpdateConsentManager? = null
    ) {
        val currentMap = _governanceStates.value.toMutableMap()
        val current = currentMap[feature] ?: FeatureGovernanceState(feature)

        if (!enabled) {
            // Disabled by Owner: Immediately disabled in real-time!
            currentMap[feature] = current.copy(isOwnerEnabled = false)
            persistStates(currentMap)
            Log.d(TAG, "Owner disabled ${feature.key}. Blocked in real-time for all users.")
        } else {
            // Enabled by Owner: Users must update app before using it!
            currentMap[feature] = current.copy(
                isOwnerEnabled = true,
                isUserUpdatedAndAccepted = false // Locked until update accepted
            )
            persistStates(currentMap)

            // Enqueue update rollout requiring user acceptance
            userConsentMgr?.enqueueOwnerRollout(
                featureKey = feature.key,
                title = "🚀 New Feature Unlocked: ${feature.titleEn}",
                description = "The app owner has unlocked ${feature.titleEn}. Please update the app to access this capability.",
                changelog = listOf(
                    "Unlocked ${feature.titleEn}",
                    feature.descriptionEn,
                    "Enhanced performance and stability"
                )
            )
            Log.d(TAG, "Owner enabled ${feature.key}. Dispatched update rollout requiring user update.")
        }
    }

    /**
     * Called when a regular user updates the app and accepts the update rollout.
     */
    fun markFeatureUpdatedByUser(featureKey: String) {
        val feat = GovernedFeature.entries.find { it.key.equals(featureKey, ignoreCase = true) } ?: return
        val currentMap = _governanceStates.value.toMutableMap()
        val current = currentMap[feat] ?: FeatureGovernanceState(feat)
        currentMap[feat] = current.copy(isUserUpdatedAndAccepted = true)
        persistStates(currentMap)
        Log.d(TAG, "User accepted update for ${feat.key}. Feature unlocked!")
    }

    /**
     * Check if feature is available right now for the specified user role.
     */
    fun isFeatureAccessible(feature: GovernedFeature, isOwner: Boolean): Boolean {
        val state = _governanceStates.value[feature] ?: FeatureGovernanceState(feature)
        return state.isAvailableForUser(isOwner)
    }
}

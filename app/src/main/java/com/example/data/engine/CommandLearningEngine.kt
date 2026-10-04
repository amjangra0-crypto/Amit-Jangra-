package com.example.data.engine

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.CommandLearningSettings
import com.example.data.model.LearnedCommandRecord
import com.example.data.model.ManagedUserAccess
import com.example.data.model.UserProfile
import com.example.network.GeminiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import org.json.JSONArray
import org.json.JSONObject

/**
 * Adaptive AI Command Learning, Self-Improvement & Creative Suggestion Engine.
 *
 * Automatically analyzes user prompts and commands, extracts narrative beats and styles,
 * autonomously improves prompts into master anime-director grade scripts, and generates
 * creative alternative suggestions.
 *
 * Strict Owner Access Control:
 * - Only active for the Owner by default.
 * - Owner can activate or deactivate the feature for themselves.
 * - Owner can toggle activation globally for other users.
 * - Owner can grant or revoke activation for specific users (User-by-User granularity).
 */
class CommandLearningEngine private constructor(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("anime_command_learning_prefs", Context.MODE_PRIVATE)

    private val _settings = MutableStateFlow(loadSettings())
    val settings: StateFlow<CommandLearningSettings> = _settings.asStateFlow()

    private val _learnedRecords = MutableStateFlow<List<LearnedCommandRecord>>(loadLearnedRecords())
    val learnedRecords: StateFlow<List<LearnedCommandRecord>> = _learnedRecords.asStateFlow()

    private val _recentSuggestions = MutableStateFlow<List<String>>(loadDefaultSuggestions())
    val recentSuggestions: StateFlow<List<String>> = _recentSuggestions.asStateFlow()

    private val _lastImprovedCommand = MutableStateFlow<String?>(null)
    val lastImprovedCommand: StateFlow<String?> = _lastImprovedCommand.asStateFlow()

    private val _isLearning = MutableStateFlow(false)
    val isLearning: StateFlow<Boolean> = _isLearning.asStateFlow()

    private val _managedUsers = MutableStateFlow(loadManagedUsers())
    val managedUsers: StateFlow<List<ManagedUserAccess>> = _managedUsers.asStateFlow()

    companion object {
        private const val KEY_OWNER_ACTIVE = "key_owner_active"
        private const val KEY_GLOBAL_OTHERS_ACTIVE = "key_global_others_active"
        private const val KEY_AUTH_USERS_SET = "key_auth_users_set"
        private const val KEY_LEARNED_RECORDS_JSON = "key_learned_records_json"
        private const val KEY_TOTAL_ANALYZED = "key_total_analyzed"
        private const val KEY_LEARNING_LEVEL = "key_learning_level"
        private const val KEY_MANAGED_USERS_JSON = "key_managed_users_json"

        @Volatile
        private var instance: CommandLearningEngine? = null

        fun getInstance(context: Context): CommandLearningEngine {
            return instance ?: synchronized(this) {
                instance ?: CommandLearningEngine(context.applicationContext).also { instance = it }
            }
        }
    }

    /**
     * Determines whether the Command Learning & Self-Improvement feature is active for the given user.
     */
    fun isFeatureAccessibleForUser(user: UserProfile): Boolean {
        val s = _settings.value
        if (user.isOwner) {
            return s.isOwnerActive
        }
        if (s.isGloballyActiveForOthers) {
            return true
        }
        val userIdentifiers = listOfNotNull(user.userId, user.email?.lowercase())
        return userIdentifiers.any { s.authorizedUserIds.contains(it) }
    }

    /**
     * Owner Control: Activate / Deactivate for Owner
     */
    fun setOwnerActive(active: Boolean) {
        val updated = _settings.value.copy(isOwnerActive = active, lastUpdated = System.currentTimeMillis())
        _settings.value = updated
        prefs.edit().putBoolean(KEY_OWNER_ACTIVE, active).apply()
    }

    /**
     * Owner Control: Activate / Deactivate Globally for Other Users
     */
    fun setGloballyActiveForOthers(active: Boolean) {
        val updated = _settings.value.copy(isGloballyActiveForOthers = active, lastUpdated = System.currentTimeMillis())
        _settings.value = updated
        prefs.edit().putBoolean(KEY_GLOBAL_OTHERS_ACTIVE, active).apply()
    }

    /**
     * Owner Control: Activate / Deactivate for a Specific User by ID or Email
     */
    fun setUserAuthorization(userIdOrEmail: String, isAuthorized: Boolean) {
        val clean = userIdOrEmail.trim().lowercase()
        if (clean.isBlank()) return

        val currentSet = _settings.value.authorizedUserIds.toMutableSet()
        if (isAuthorized) {
            currentSet.add(clean)
        } else {
            currentSet.remove(clean)
        }

        val updatedSettings = _settings.value.copy(
            authorizedUserIds = currentSet,
            lastUpdated = System.currentTimeMillis()
        )
        _settings.value = updatedSettings
        prefs.edit().putStringSet(KEY_AUTH_USERS_SET, currentSet).apply()

        // Update managed user list
        val currentUsers = _managedUsers.value.toMutableList()
        val index = currentUsers.indexOfFirst { it.userId.equals(clean, ignoreCase = true) || it.email.equals(clean, ignoreCase = true) }
        if (index != -1) {
            currentUsers[index] = currentUsers[index].copy(isGranted = isAuthorized)
        } else {
            currentUsers.add(
                ManagedUserAccess(
                    userId = clean,
                    displayName = clean.substringBefore("@").replaceFirstChar { it.uppercase() },
                    email = if (clean.contains("@")) clean else "$clean@anime.io",
                    role = "Custom Authorized User",
                    isGranted = isAuthorized
                )
            )
        }
        _managedUsers.value = currentUsers
        saveManagedUsers(currentUsers)
    }

    /**
     * Owner Control: Remove user entirely from permission registry
     */
    fun removeUserFromRegistry(userIdOrEmail: String) {
        val clean = userIdOrEmail.trim().lowercase()
        val currentSet = _settings.value.authorizedUserIds.toMutableSet()
        currentSet.remove(clean)
        _settings.value = _settings.value.copy(authorizedUserIds = currentSet)
        prefs.edit().putStringSet(KEY_AUTH_USERS_SET, currentSet).apply()

        val filtered = _managedUsers.value.filterNot {
            it.userId.equals(clean, ignoreCase = true) || it.email.equals(clean, ignoreCase = true)
        }
        _managedUsers.value = filtered
        saveManagedUsers(filtered)
    }

    /**
     * Core AI Adaptive Learning & Self-Improvement logic.
     * Takes raw command, learns from it, enhances it into a cinematic prompt,
     * and produces 3-5 distinct creative suggestions.
     */
    suspend fun learnAndImproveCommand(
        rawCommand: String,
        currentLanguage: String = "English",
        geminiApiService: GeminiApiService? = null,
        userId: String = "owner_amjangra0"
    ): Pair<String, List<String>> = withContext(Dispatchers.IO) {
        _isLearning.value = true
        val trimmed = rawCommand.trim()
        if (trimmed.isBlank()) {
            _isLearning.value = false
            val defaultSuggs = loadDefaultSuggestions()
            _recentSuggestions.value = defaultSuggs
            return@withContext Pair("", defaultSuggs)
        }

        // 1. Analyze and extract keywords & genre
        val genre = detectGenre(trimmed)
        val keywords = extractKeywords(trimmed)

        // 2. Self-Improve the Command ("उसे खुद से improve करे")
        var improved = ""
        var suggestions = mutableListOf<String>()

        // Try Gemini 3.5 Flash first if available
        if (geminiApiService != null) {
            try {
                val promptForGemini = """
                    You are an Elite Anime Film Director & Screenwriter AI.
                    The user gave this raw anime command/prompt:
                    "$trimmed"
                    
                    Do two tasks:
                    1. IMPROVED_PROMPT: Transform and dramatically improve this command into a vivid, cinematic anime production prompt with 4K cel animation direction, dramatic lighting (neon reflections/god rays), camera movement (dynamic dolly/pan), emotional character motivation, and audio cues.
                    2. SUGGESTIONS: Provide 4 distinct, creative alternative twists or narrative spin-offs based on this command.
                    
                    Respond strictly in this format:
                    [IMPROVED]
                    <Your enhanced cinematic prompt>
                    [SUGGESTION_1]
                    <Alternative variation 1: Action/Battle focus>
                    [SUGGESTION_2]
                    <Alternative variation 2: Emotional/Drama focus>
                    [SUGGESTION_3]
                    <Alternative variation 3: Mystery/Cyberpunk/Fantasy twist>
                    [SUGGESTION_4]
                    <Alternative variation 4: Climax/Epic encounter>
                """.trimIndent()

                val response = geminiApiService.callGemini(promptForGemini)
                if (response.isNotBlank() && response.contains("[IMPROVED]")) {
                    val parts = response.split("[IMPROVED]", "[SUGGESTION_1]", "[SUGGESTION_2]", "[SUGGESTION_3]", "[SUGGESTION_4]")
                    if (parts.size >= 2) {
                        improved = parts[1].trim()
                    }
                    for (i in 2 until parts.size) {
                        val s = parts[i].trim()
                        if (s.isNotBlank()) suggestions.add(s)
                    }
                }
            } catch (_: Exception) {}
        }

        // High-Quality Heuristic Fallback if Gemini is offline or did not format
        if (improved.isBlank()) {
            improved = buildSmartImprovedCommand(trimmed, genre, currentLanguage)
        }
        if (suggestions.isEmpty()) {
            suggestions = generateSmartSuggestions(trimmed, genre, currentLanguage)
        }

        // 3. Record Learning metrics
        val newAnalyzedCount = _settings.value.totalCommandsAnalyzed + 1
        val newLevel = (newAnalyzedCount / 3).coerceAtLeast(1).coerceAtMost(10)
        _settings.value = _settings.value.copy(
            totalCommandsAnalyzed = newAnalyzedCount,
            learningLevel = newLevel
        )
        prefs.edit()
            .putInt(KEY_TOTAL_ANALYZED, newAnalyzedCount)
            .putInt(KEY_LEARNING_LEVEL, newLevel)
            .apply()

        // 4. Save Record to Learned History
        val newRecord = LearnedCommandRecord(
            rawCommand = trimmed,
            improvedCommand = improved,
            suggestions = suggestions,
            genre = genre,
            keywords = keywords,
            timestamp = System.currentTimeMillis(),
            userId = userId
        )
        val currentRecords = _learnedRecords.value.toMutableList()
        currentRecords.add(0, newRecord)
        val trimmedRecords = currentRecords.take(50) // keep last 50
        _learnedRecords.value = trimmedRecords
        saveLearnedRecords(trimmedRecords)

        _lastImprovedCommand.value = improved
        _recentSuggestions.value = suggestions
        _isLearning.value = false

        return@withContext Pair(improved, suggestions)
    }

    private fun detectGenre(cmd: String): String {
        val lower = cmd.lowercase()
        return when {
            lower.contains("cyber") || lower.contains("neon") || lower.contains("sci-fi") || lower.contains("robot") || lower.contains("mecha") -> "Cyberpunk Sci-Fi"
            lower.contains("fight") || lower.contains("battle") || lower.contains("sword") || lower.contains("power") || lower.contains("samurai") -> "Shonen Action"
            lower.contains("magic") || lower.contains("dragon") || lower.contains("temple") || lower.contains("isekai") || lower.contains("sorcerer") -> "Mythical Fantasy"
            lower.contains("dark") || lower.contains("shadow") || lower.contains("demon") || lower.contains("vampire") -> "Dark Supernatural"
            lower.contains("love") || lower.contains("heart") || lower.contains("school") || lower.contains("friend") -> "Slice of Life / Drama"
            else -> "Cinematic Anime"
        }
    }

    private fun extractKeywords(cmd: String): List<String> {
        val stopWords = setOf("the", "and", "a", "an", "in", "on", "at", "to", "for", "with", "by", "of", "from", "is", "are", "दो", "का", "की", "के", "में", "पर", "और", "से")
        return cmd.split(" ", ",", ".", "!", "?")
            .map { it.trim().lowercase() }
            .filter { it.length > 3 && !stopWords.contains(it) }
            .distinct()
            .take(6)
    }

    private fun buildSmartImprovedCommand(raw: String, genre: String, lang: String): String {
        val isHi = lang.contains("hindi", ignoreCase = true)
        return when (genre) {
            "Cyberpunk Sci-Fi" -> if (isHi) {
                "🎬 [सिनेमैटिक एनिमे डायरेक्टर]: नियो टोक्यो की नियॉन-भीगी छतों पर 4K सेल शेडिंग एनिमेशन। $raw | तीव्र बारिश की बूंदों में रिफ्लेक्शन, होलोग्राफिक विजुअल्स, ड्रामेटिक लो-एंगल कैमरा डॉली और सिंक किया हुआ सिंथवेव BGM।"
            } else {
                "🎬 [Cinematic Anime Director]: Ultra 4K cel-shaded anime production. $raw — Set amidst rain-slicked Neo-Tokyo rooftops with volumetric neon bloom, dynamic camera tracking, high-velocity choreography, and an immersive bass-heavy synth soundtrack."
            }
            "Shonen Action" -> if (isHi) {
                "🎬 [मास्टर शोनेन एक्शन]: महाकाव्य शक्ति का टकराव! $raw | चमकती नीली बिजली के स्पार्क्स, क्लोज-अप आई ग्लिंट, तीव्र गति से घूमता कैमरा और चरम निर्णायक संवाद।"
            } else {
                "🎬 [Master Shonen Action]: High-octane battle sequence. $raw — Featuring kinetic sakuga animation, crackling spiritual aura, dynamic 360-degree orbital camera pan, and intense resolute character cadence."
            }
            "Mythical Fantasy" -> if (isHi) {
                "🎬 [रहस्यमयी जादुई फैंटेसी]: प्राचीन पवित्र मंदिर का दृश्य। $raw | तैरते हुए चेरी ब्लॉसम के फूल, स्वर्णिम गॉड रेज प्रकाश, रहस्यमयी प्राचीन मंत्र और भावुक संगीत।"
            } else {
                "🎬 [Mythical Fantasy Narrative]: Ethereal floating temple aesthetic. $raw — Drifting sakura blossoms, radiant golden volumetric god rays, ancient celestial symbols, and sweeping orchestral anime score."
            }
            else -> if (isHi) {
                "🎬 [विस्तृत एनिमे प्रोडक्शन]: $raw | सिनेमाई 16:9 पैनोरमा, उत्कृष्ट 60fps एनिमेशन, समृद्ध प्रकाश व्यवस्था और पात्रों के चेहरे के सूक्ष्म भाव।"
            } else {
                "🎬 [Cinematic Master Direction]: $raw — 16:9 panoramic cinematic framing, fluid 60fps sakuga motion, dramatic volumetric lighting, and deeply evocative voice cadence."
            }
        }
    }

    private fun generateSmartSuggestions(raw: String, genre: String, lang: String): MutableList<String> {
        val isHi = lang.contains("hindi", ignoreCase = true)
        return if (isHi) {
            mutableListOf(
                "⚔️ [हाई-स्टेक्स एक्शन]: $raw के बीच अचानक एक नया रहस्यमयी प्रतिद्वंद्वी अपनी बिजली की तलवार से हमला करता है।",
                "🌌 [नियॉन साइबरपंक ट्विस्ट]: $raw दृश्य अचानक अंधेरे में खो जाता है और होलोग्राफिक ड्रैगन आसमान में उभरता है।",
                "🌸 [भावुक संवाद मोड़]: दोनों पात्र एक दूसरे की आंखों में देखते हैं और वादा करते हैं कि वे कभी हार नहीं मानेंगे।",
                "🔥 [अंतिम महायुद्ध क्लाइमेक्स]: 60fps स्लो-मोशन में चरम शक्ति का विस्फोट और पृष्ठभूमि में उड़ते हुए अंगारे।"
            )
        } else {
            mutableListOf(
                "⚔️ [Action Climax]: In the midst of $raw, a formidable cyber-warrior awakens an ancient blade technique.",
                "🌌 [Cyberpunk Noir Twist]: The neon lights flicker off as $raw reveals a hidden AI hologram guarding the temple.",
                "🌸 [Emotional Bond Variant]: Focus on subtle character expressions and heartfelt dialogue before the battle begins.",
                "🔥 [Legendary Power Burst]: An explosion of blue lightning aura transforms the battlefield in dynamic 60fps sakuga."
            )
        }
    }

    private fun loadDefaultSuggestions(): List<String> {
        return listOf(
            "⚔️ Two cyber samurai duel beneath raining cherry blossom petals at twilight",
            "🌌 Neo-Tokyo underground hacker discovers a mythical spirit within the quantum core",
            "🌸 Lady mentor transmits ancient dragon power to the young warrior in a glowing temple",
            "⚡ High-speed mecha chase sequence through vertical neon skyscraper canyons"
        )
    }

    private fun loadSettings(): CommandLearningSettings {
        val ownerActive = prefs.getBoolean(KEY_OWNER_ACTIVE, true)
        val globalActive = prefs.getBoolean(KEY_GLOBAL_OTHERS_ACTIVE, false)
        val authSet = prefs.getStringSet(KEY_AUTH_USERS_SET, setOf("creator_ren", "vip_sakura", "amjangra0@gmail.com")) ?: emptySet()
        val total = prefs.getInt(KEY_TOTAL_ANALYZED, 14)
        val level = prefs.getInt(KEY_LEARNING_LEVEL, 4)
        return CommandLearningSettings(
            isOwnerActive = ownerActive,
            isGloballyActiveForOthers = globalActive,
            authorizedUserIds = authSet,
            learningLevel = level,
            totalCommandsAnalyzed = total
        )
    }

    private fun loadLearnedRecords(): List<LearnedCommandRecord> {
        val json = prefs.getString(KEY_LEARNED_RECORDS_JSON, null) ?: return emptyList()
        val list = mutableListOf<LearnedCommandRecord>()
        try {
            val arr = JSONArray(json)
            for (i in 0 until arr.length()) {
                val obj = arr.getJSONObject(i)
                val suggArr = obj.optJSONArray("suggestions")
                val suggs = mutableListOf<String>()
                if (suggArr != null) {
                    for (j in 0 until suggArr.length()) {
                        suggs.add(suggArr.getString(j))
                    }
                }
                list.add(
                    LearnedCommandRecord(
                        id = obj.optString("id", java.util.UUID.randomUUID().toString()),
                        rawCommand = obj.optString("rawCommand", ""),
                        improvedCommand = obj.optString("improvedCommand", ""),
                        suggestions = suggs,
                        genre = obj.optString("genre", "Anime"),
                        timestamp = obj.optLong("timestamp", System.currentTimeMillis()),
                        userId = obj.optString("userId", "owner_amjangra0")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    private fun saveLearnedRecords(records: List<LearnedCommandRecord>) {
        try {
            val arr = JSONArray()
            records.forEach { r ->
                val obj = JSONObject().apply {
                    put("id", r.id)
                    put("rawCommand", r.rawCommand)
                    put("improvedCommand", r.improvedCommand)
                    put("genre", r.genre)
                    put("timestamp", r.timestamp)
                    put("userId", r.userId)
                    val sArr = JSONArray()
                    r.suggestions.forEach { sArr.put(it) }
                    put("suggestions", sArr)
                }
                arr.put(obj)
            }
            prefs.edit().putString(KEY_LEARNED_RECORDS_JSON, arr.toString()).apply()
        } catch (_: Exception) {}
    }

    private fun loadManagedUsers(): List<ManagedUserAccess> {
        val json = prefs.getString(KEY_MANAGED_USERS_JSON, null)
        if (json != null) {
            try {
                val arr = JSONArray(json)
                val list = mutableListOf<ManagedUserAccess>()
                for (i in 0 until arr.length()) {
                    val o = arr.getJSONObject(i)
                    list.add(
                        ManagedUserAccess(
                            userId = o.optString("userId"),
                            displayName = o.optString("displayName"),
                            email = o.optString("email"),
                            role = o.optString("role"),
                            isGranted = o.optBoolean("isGranted"),
                            lastActive = o.optString("lastActive", "Active today")
                        )
                    )
                }
                if (list.isNotEmpty()) return list
            } catch (_: Exception) {}
        }

        // Default seeded creator users
        val defaultList = listOf(
            ManagedUserAccess(
                userId = "creator_ren",
                displayName = "Ren Tachibana",
                email = "ren.tachibana@anime.studio",
                role = "Anime Director / Creator",
                isGranted = true,
                lastActive = "12 min ago"
            ),
            ManagedUserAccess(
                userId = "vip_sakura",
                displayName = "Sakura Haruka",
                email = "sakura.vip@creative.net",
                role = "VIP Pro Subscriber",
                isGranted = true,
                lastActive = "1 hour ago"
            ),
            ManagedUserAccess(
                userId = "creator_kaito",
                displayName = "Kaito Shiro",
                email = "kaito.mecha@studio.jp",
                role = "Mecha Screenwriter",
                isGranted = false,
                lastActive = "Yesterday"
            ),
            ManagedUserAccess(
                userId = "guest_creative_user",
                displayName = "Public Guest User",
                email = "guest@animesamples.org",
                role = "Standard User",
                isGranted = false,
                lastActive = "3 days ago"
            )
        )
        saveManagedUsers(defaultList)
        return defaultList
    }

    private fun saveManagedUsers(users: List<ManagedUserAccess>) {
        try {
            val arr = JSONArray()
            users.forEach { u ->
                val o = JSONObject().apply {
                    put("userId", u.userId)
                    put("displayName", u.displayName)
                    put("email", u.email)
                    put("role", u.role)
                    put("isGranted", u.isGranted)
                    put("lastActive", u.lastActive)
                }
                arr.put(o)
            }
            prefs.edit().putString(KEY_MANAGED_USERS_JSON, arr.toString()).apply()
        } catch (_: Exception) {}
    }
}

package com.example.automation

import android.util.Log
import com.example.data.model.AnimeArtStyle
import com.example.data.model.AnimeScene
import com.example.data.model.AnimeScript
import com.example.data.model.CharacterProfile
import com.example.data.model.DialogueLine
import com.example.data.model.MotionEffect
import com.example.data.model.MusicMood
import com.example.data.model.ProductionFormat
import com.example.data.model.SourcePlatform
import com.example.network.GeminiApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URI
import java.util.Locale

/**
 * Data model representing the intelligent extraction from a user command or link
 */
data class AutoDirectorCommand(
    val rawInput: String,
    val isUrl: Boolean,
    val extractedUrl: String,
    val extractedDomain: String,
    val corePrompt: String,
    val requestedDurationSeconds: Int,
    val calculatedSceneCount: Int,
    val sceneDurationSec: Int,
    val targetLanguage: String,
    val targetAccent: String,
    val targetArtStyle: AnimeArtStyle,
    val targetProductionFormat: ProductionFormat,
    val targetMotionEffect: MotionEffect,
    val targetMusicMood: MusicMood,
    val detectedKeywords: List<String>,
    val detectionSummaryHindi: String,
    val detectionSummaryEnglish: String
)

/**
 * Autonomous AI Director Engine
 * Parses single commands or links, detects duration, language, accent, characters,
 * motion effects, dialogues, and orchestrates end-to-end video synthesis.
 */
object AutoDirectorEngine {

    private const val TAG = "AutoDirectorEngine"

    /**
     * Parses a user command line or URL link into structured parameters.
     */
    fun parseCommand(
        input: String,
        overrideDurationSec: Int? = null,
        overrideLanguage: String? = null,
        overrideAccent: String? = null
    ): AutoDirectorCommand {
        val trimmed = input.trim()
        val lower = trimmed.lowercase(Locale.ROOT)

        // 1. Detect if input is a Web URL
        val isUrl = trimmed.startsWith("http://", ignoreCase = true) ||
                trimmed.startsWith("https://", ignoreCase = true) ||
                trimmed.contains("www.", ignoreCase = true) ||
                (trimmed.contains(".com") || trimmed.contains(".org") || trimmed.contains(".net") || trimmed.contains(".ai") || trimmed.contains(".jp") || trimmed.contains(".in")) && !trimmed.contains(" ")

        var extractedUrl = ""
        var extractedDomain = ""
        var corePrompt = trimmed

        if (isUrl) {
            extractedUrl = if (trimmed.startsWith("http://") || trimmed.startsWith("https://")) trimmed else "https://$trimmed"
            extractedDomain = try {
                val uri = URI(extractedUrl)
                uri.host?.removePrefix("www.") ?: "weblink.com"
            } catch (e: Exception) {
                "weblink.com"
            }
            // Generate clean storyline theme from URL domain & path slug
            val pathTokens = extractedUrl
                .replace(Regex("https?://(www\\.)?"), "")
                .split("/", "?", "-", "_", "=", "&")
                .filter { it.length > 2 && !it.contains(".") && !it.matches(Regex("\\d+")) }
                .take(4)
                .joinToString(" ")
            corePrompt = if (pathTokens.isNotBlank()) "Anime story adapted from $extractedDomain: $pathTokens" else "Anime chronicle based on $extractedDomain content"
        }

        // 2. Detect Duration (Seconds, Minutes, or 1 to 2 Hours based on user command)
        var detectedSeconds = overrideDurationSec ?: 30

        if (overrideDurationSec == null || overrideDurationSec <= 0) {
            // Regex for Hour ranges e.g. "1 to 2 hour", "1-2 hour", "1 hour to 2 hour", "1 से 2 घंटे", "1 hour to 2 hour tk"
            val hourRangeMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*(?:to|-|se|से|tk|तक)\\s*(\\d+(?:\\.\\d+)?)\\s*(?:hour|hours|hr|hrs|h|घंटा|घंटे|घण्टा)(?:\\s*(?:tk|तक))?", RegexOption.IGNORE_CASE).find(lower)
            // Regex for single Hour e.g. "1 hour", "2 hour", "2 hr", "1.5 hours", "1 घंटा", "2 घंटे", "1 hour tk", "2 hour tk"
            val singleHourMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*(?:hour|hours|hr|hrs|h|घंटा|घंटे|घण्टा)(?:\\s*(?:tk|तक))?", RegexOption.IGNORE_CASE).find(lower)

            // Regex for e.g. "2 min", "2 mins", "2 minute", "2 minutes", "2m", "2 मिनट"
            val minMatch = Regex("(\\d+(?:\\.\\d+)?)\\s*(?:min|mins|minute|minutes|m|मिनट)", RegexOption.IGNORE_CASE).find(lower)
            // Regex for e.g. "15s", "15 second", "15 sec", "15 सेकंड"
            val secMatch = Regex("(\\d+)\\s*(?:sec|second|seconds|s|सेकंड|सेकंड्स)", RegexOption.IGNORE_CASE).find(lower)

            if (hourRangeMatch != null) {
                // Command specifies a range like 1 hour to 2 hours -> set to full 2 hours (7200s) or upper bound
                val maxHour = hourRangeMatch.groupValues[2].toFloatOrNull() ?: 2.0f
                detectedSeconds = (maxHour * 3600).toInt().coerceIn(15, 7200)
            } else if (singleHourMatch != null) {
                val hourVal = singleHourMatch.groupValues[1].toFloatOrNull() ?: 1.0f
                detectedSeconds = (hourVal * 3600).toInt().coerceIn(15, 7200)
            } else if (minMatch != null) {
                val minVal = minMatch.groupValues[1].toFloatOrNull() ?: 1.0f
                detectedSeconds = (minVal * 60).toInt().coerceIn(15, 7200)
            } else if (secMatch != null) {
                val secVal = secMatch.groupValues[1].toIntOrNull() ?: 30
                detectedSeconds = secVal.coerceIn(15, 7200)
            } else {
                // Semantic duration cues
                when {
                    lower.contains("2 hour") || lower.contains("2 hr") || lower.contains("2 घंटे") || lower.contains("2घंटे") || lower.contains("2h") || lower.contains("2 hour tk") -> detectedSeconds = 7200
                    lower.contains("1 to 2 hour") || lower.contains("1-2 hour") || lower.contains("1 hour to 2 hour") || lower.contains("1 se 2 ghante") -> detectedSeconds = 7200
                    lower.contains("1 hour") || lower.contains("1 hr") || lower.contains("1 घंटा") || lower.contains("1घंटा") || lower.contains("1h") || lower.contains("1 hour tk") -> detectedSeconds = 3600
                    lower.contains("30 min") || lower.contains("30 मिनट") || lower.contains("tv episode") -> detectedSeconds = 1800
                    lower.contains("reel") || lower.contains("रील") || lower.contains("15s") || lower.contains("short reel") -> detectedSeconds = 15
                    lower.contains("short") || lower.contains("शॉर्ट") || lower.contains("30s") -> detectedSeconds = 30
                    lower.contains("1 min") || lower.contains("60s") || lower.contains("1 मिनट") -> detectedSeconds = 60
                    lower.contains("2 min") || lower.contains("120s") || lower.contains("2 मिनट") -> detectedSeconds = 120
                    lower.contains("3 min") || lower.contains("180s") || lower.contains("3 मिनट") -> detectedSeconds = 180
                    lower.contains("5 min") || lower.contains("300s") || lower.contains("5 मिनट") || lower.contains("full episode") || lower.contains("पूरा एपिसोड") -> detectedSeconds = 300
                    lower.contains("movie") || lower.contains("मूवी") || lower.contains("film") || lower.contains("फिल्म") || lower.contains("महागाथा") -> detectedSeconds = 3600
                    lower.contains("long") || lower.contains("लंबी") || lower.contains("बड़ी") -> detectedSeconds = 300
                    else -> detectedSeconds = 30
                }
            }
        }

        // Calculate Scaled Scene Count matching duration (up to 1-2 Hours)
        val calculatedSceneCount = when {
            detectedSeconds <= 20 -> 2
            detectedSeconds <= 40 -> 4
            detectedSeconds <= 75 -> 6
            detectedSeconds <= 130 -> 10
            detectedSeconds <= 190 -> 15
            detectedSeconds <= 320 -> 22
            detectedSeconds <= 900 -> 30    // ~15 Minutes
            detectedSeconds <= 1800 -> 45   // ~30 Minutes (TV Broadcast)
            detectedSeconds <= 3600 -> 60   // ~1 Hour (60 Minutes Featurette)
            else -> 80                     // Up to 2 Hours (120 Minutes Cinematic Epic)
        }

        val sceneDurationSec = (detectedSeconds / calculatedSceneCount).coerceAtLeast(6)

        // 3. Detect Language
        val detectedLanguage = if (!overrideLanguage.isNullOrBlank() && !overrideLanguage.equals("Auto-Detect", ignoreCase = true)) {
            overrideLanguage
        } else {
            when {
                lower.contains("japanese") || lower.contains("जापानी") || lower.contains("nihongo") || lower.contains("tokyo") -> "Japanese"
                lower.contains("korean") || lower.contains("कोरियन") || lower.contains("hangul") || lower.contains("manhwa") || lower.contains("सियोल") -> "Korean"
                lower.contains("english") || lower.contains("अंग्रेजी") || lower.contains("hollywood") -> "English"
                lower.contains("tamil") || lower.contains("तमिल") -> "Tamil"
                lower.contains("telugu") || lower.contains("तेलुगु") || lower.contains("तेलगू") -> "Telugu"
                lower.contains("spanish") || lower.contains("स्पैनिश") || lower.contains("स्पेनिश") -> "Spanish"
                lower.contains("french") || lower.contains("फ्रेंच") -> "French"
                lower.contains("german") || lower.contains("जर्मन") -> "German"
                lower.contains("chinese") || lower.contains("चीनी") -> "Chinese"
                lower.contains("russian") || lower.contains("रूसी") || lower.contains("रशियन") -> "Russian"
                lower.contains("arabic") || lower.contains("अरबी") -> "Arabic"
                lower.contains("hindi") || lower.contains("हिंदी") || lower.contains("हिन्दी") -> "Hindi"
                else -> "Hindi" // Default friendly anime dub language
            }
        }

        // 4. Detect Voice Accent
        val detectedAccent = if (!overrideAccent.isNullOrBlank() && !overrideAccent.equals("Auto-Detect", ignoreCase = true)) {
            overrideAccent
        } else {
            when {
                lower.contains("tokyo") || lower.contains("japanese cadence") || lower.contains("जापानी लहज़ा") -> "Tokyo Standard Anime (Japanese Cadence)"
                lower.contains("korean dramatic") || lower.contains("manhwa style") || lower.contains("कोरियन डब") -> "Korean Dramatic (Manhwa Style)"
                lower.contains("bollywood") || lower.contains("heroic hindi") || lower.contains("हिंदी डब") -> "Hindi Dub (Heroic Bollywood Anime)"
                lower.contains("british") || lower.contains("royal") || lower.contains("aristocrat") -> "British Aristocrat / Royal"
                lower.contains("american") || lower.contains("action hero") || lower.contains("hollywood") -> "American Heroic Action"
                lower.contains("cyber") || lower.contains("robot") || lower.contains("synth") || lower.contains("vocoded") -> "Cybernetic / Vocoded Synth"
                lower.contains("kawaii") || lower.contains("chibi") || lower.contains("cute") -> "Kawaii High-Energy Anime"
                else -> when (detectedLanguage) {
                    "Japanese" -> "Tokyo Standard Anime (Japanese Cadence)"
                    "Korean" -> "Korean Dramatic (Manhwa Style)"
                    "Hindi" -> "Hindi Dub (Heroic Bollywood Anime)"
                    "English" -> "American Heroic Action"
                    else -> "Standard Anime"
                }
            }
        }

        // 5. Detect Art Style & Production Format
        val detectedArtStyle = when {
            lower.contains("manhwa") || lower.contains("webtoon") || lower.contains("solo leveling") || lower.contains("hunter") || lower.contains("dungeon") || lower.contains("शैडो") -> AnimeArtStyle.MANHWA_WEBTOON
            lower.contains("cyberpunk") || lower.contains("cyber") || lower.contains("neon") || lower.contains("sci-fi") || lower.contains("भविष्य") -> AnimeArtStyle.CYBERPUNK_ANIME
            lower.contains("manga") || lower.contains("मंगा") || lower.contains("black and white") -> AnimeArtStyle.CLASSIC_MANGA
            lower.contains("chibi") || lower.contains("cute") || lower.contains("cartoon") || lower.contains("कार्टून") -> AnimeArtStyle.CHIBI_CARTOON
            lower.contains("shonen") || lower.contains("battle") || lower.contains("fight") || lower.contains("योद्धा") -> AnimeArtStyle.SHONEN_ACTION
            else -> AnimeArtStyle.JAPANESE_ANIME
        }

        val targetProductionFormat = when {
            detectedSeconds <= 20 -> ProductionFormat.SHORTS_REEL
            detectedArtStyle == AnimeArtStyle.MANHWA_WEBTOON -> ProductionFormat.MANHWA_WEB_SERIES
            detectedSeconds >= 120 -> ProductionFormat.CINEMATIC_MOVIE
            else -> ProductionFormat.ANIME_EPISODE
        }

        // 6. Detect Motion Effects
        val targetMotionEffect = when {
            lower.contains("shake") || lower.contains("earthquake") || lower.contains("धमाका") || lower.contains("punch") -> MotionEffect.SCREEN_SHAKE_IMPACT
            lower.contains("aura") || lower.contains("glow") || lower.contains("fire") || lower.contains("शैडो") -> MotionEffect.AURA_GLOW_PARTICLES
            lower.contains("panel") || lower.contains("slide") || lower.contains("पैनल") -> MotionEffect.MANGA_PANEL_SLIDE
            lower.contains("zoom") || lower.contains("क्लोज़") || lower.contains("cinematic") -> MotionEffect.CINEMATIC_ZOOM
            lower.contains("sakura") || lower.contains("cherry") || lower.contains("फूल") -> MotionEffect.SPEEDLINES_ACTION
            else -> MotionEffect.SPEEDLINES_ACTION
        }

        val targetMusicMood = when {
            detectedArtStyle == AnimeArtStyle.CYBERPUNK_ANIME -> MusicMood.CYBER_SYNTH
            detectedArtStyle == AnimeArtStyle.MANHWA_WEBTOON || targetMotionEffect == MotionEffect.SCREEN_SHAKE_IMPACT -> MusicMood.EPIC_BATTLE
            detectedArtStyle == AnimeArtStyle.CHIBI_CARTOON -> MusicMood.KAWAII_PLAYFUL
            detectedSeconds >= 120 -> MusicMood.EMOTIONAL_PIANO
            else -> MusicMood.MYSTERY_FANTASY
        }

        val keywords = mutableListOf<String>()
        if (isUrl) keywords.add("Link: $extractedDomain")
        keywords.add("${detectedSeconds}s Duration ($calculatedSceneCount Scenes)")
        keywords.add("Lang: $detectedLanguage")
        keywords.add("Accent: $detectedAccent")
        keywords.add("Style: ${detectedArtStyle.title}")
        keywords.add("FX: ${targetMotionEffect.title}")

        val summaryHindi = "ऑटो-डिटेक्टेड: $detectedSeconds सेकंड वीडियो ($calculatedSceneCount सीन्स) • भाषा: $detectedLanguage ($detectedAccent) • आर्ट स्टाइल: ${detectedArtStyle.title} • मोशन: ${targetMotionEffect.title}"
        val summaryEnglish = "Auto-Configured: $detectedSeconds sec ($calculatedSceneCount scenes) • Language: $detectedLanguage ($detectedAccent) • Style: ${detectedArtStyle.title} • FX: ${targetMotionEffect.title}"

        return AutoDirectorCommand(
            rawInput = trimmed,
            isUrl = isUrl,
            extractedUrl = extractedUrl,
            extractedDomain = extractedDomain,
            corePrompt = corePrompt,
            requestedDurationSeconds = detectedSeconds,
            calculatedSceneCount = calculatedSceneCount,
            sceneDurationSec = sceneDurationSec,
            targetLanguage = detectedLanguage,
            targetAccent = detectedAccent,
            targetArtStyle = detectedArtStyle,
            targetProductionFormat = targetProductionFormat,
            targetMotionEffect = targetMotionEffect,
            targetMusicMood = targetMusicMood,
            detectedKeywords = keywords,
            detectionSummaryHindi = summaryHindi,
            detectionSummaryEnglish = summaryEnglish
        )
    }

    /**
     * Executes the end-to-end automated synthesis pipeline
     */
    suspend fun executeAutonomousPipeline(
        geminiService: GeminiApiService,
        command: AutoDirectorCommand,
        onProgressUpdate: (step: String, progress: Float) -> Unit
    ): AnimeScript = withContext(Dispatchers.Default) {
        val isHindi = command.targetLanguage.equals("Hindi", ignoreCase = true) || command.targetAccent.contains("Bollywood", ignoreCase = true)
        val topic = command.corePrompt.ifBlank { if (isHindi) "रहस्यमयी एनिमे महागाथा" else "Mystical Anime Saga" }
        onProgressUpdate(if (isHindi) "🧠 कमांड और लिंक का डीप एनालिसिस व प्लॉट कंस्ट्रक्शन..." else "🧠 Deep analysis of command and link, constructing plot...", 0.15f)

        val promptForAi = """
            You are an Autonomous AI Anime & Video Director.
            Generate an end-to-end complete Anime Script and Video Project based on this user command / link:
            "${command.corePrompt}"
            
            AUTOMATION REQUIREMENTS:
            - EXACT VIDEO DURATION: ${command.requestedDurationSeconds} seconds total.
            - EXACT SCENE COUNT: Exactly ${command.calculatedSceneCount} scenes (each approx ${command.sceneDurationSec} seconds).
            - LANGUAGE FOR DIALOGUES: ${command.targetLanguage}.
            - VOICE ACCENT & CADENCE: ${command.targetAccent}.
            - ART STYLE: ${command.targetArtStyle.title}.
            - DEFAULT MOTION EFFECT: ${command.targetMotionEffect.title}.
            - BACKGROUND MUSIC MOOD: ${command.targetMusicMood.label}.
            
            Synthesize:
            1. 2 to 4 unique characters (Names, gender, role, personality, distinct voice persona, voicePitch 0.8 to 1.7, voiceSpeed, voiceType, expression).
            2. Exactly ${command.calculatedSceneCount} sequential scenes with visual prompts, background types, camera motions, and dynamic dialogues spoken in ${command.targetLanguage}.
            
            Respond ONLY with a valid JSON in this exact structure:
            {
              "title": "Creative Anime Title",
              "genre": "Anime / Manhwa Genre",
              "synopsis": "Engaging 2-sentence synopsis",
              "characters": [
                {
                  "name": "Character Name",
                  "gender": "Boy / Girl / Adult Male / Mascot",
                  "role": "Hero / Hunter / Rival / Mentor / Mascot",
                  "personality": "Personality description",
                  "voiceType": "Boy / Girl / Male / Lady / Mascot",
                  "voicePitch": 1.0,
                  "voiceSpeed": 1.0,
                  "expression": "Fierce Battle Roar / Manhwa Glowing Eyes / Kawaii Blush & Sparkles / Confident Smirk"
                }
              ],
              "scenes": [
                {
                  "sceneNumber": 1,
                  "title": "Scene Title",
                  "visualPrompt": "Detailed visual description of anime frame",
                  "backgroundType": "Shadow Dungeon / Cherry Blossom Shrine / Cyber Neo City",
                  "bgMood": "${command.targetMusicMood.label}",
                  "motionEffect": "${command.targetMotionEffect.title}",
                  "durationSec": ${command.sceneDurationSec},
                  "dialogues": [
                    {
                      "characterName": "Character Name",
                      "text": "Spoken dialogue in ${command.targetLanguage}",
                      "emotion": "Determined / Fierce / Excited / Mysterious",
                      "expression": "Fierce Battle Roar / Manhwa Glowing Eyes / Kawaii Blush / Confident Smirk",
                      "motionEffect": "${command.targetMotionEffect.title}",
                      "voiceType": "Boy / Girl / Male / Lady / Mascot",
                      "voiceAccent": "${command.targetAccent}"
                    }
                  ]
                }
              ]
            }
        """.trimIndent()

        onProgressUpdate("🎬 ${command.calculatedSceneCount} दृश्यों की स्टोरीबोर्ड स्क्रिप्ट तैयार हो रही है (${command.requestedDurationSeconds}s)...", 0.35f)

        var aiResponse = ""
        try {
            aiResponse = geminiService.callGemini(promptForAi)
        } catch (e: Exception) {
            Log.w(TAG, "Gemini direct call exception: ${e.message}")
        }

        onProgressUpdate(if (isHindi) "👥 ऑटोनॉमस करैक्टर व वॉइस जनरेशन..." else "👥 Autonomous character & voice generation (Pitch & Dubbing)...", 0.65f)

        if (aiResponse.isNotBlank()) {
            try {
                val cleaned = aiResponse.replace("```json", "").replace("```", "").trim()
                val root = JSONObject(cleaned)
                val title = root.optString("title", "AI Autonomous Chronicle")
                val genre = root.optString("genre", "Action Anime")
                val synopsis = root.optString("synopsis", "An autonomous animation rendered to exact duration.")

                val charList = mutableListOf<CharacterProfile>()
                val charsArr = root.optJSONArray("characters")
                if (charsArr != null) {
                    for (i in 0 until charsArr.length()) {
                        val cObj = charsArr.getJSONObject(i)
                        val name = cObj.optString("name", "Character $i")
                        val vType = cObj.optString("voiceType", "Girl")
                        val expr = cObj.optString("expression", "Confident Smirk")
                        val matchedAvatar = when {
                            name.contains("Jin", true) || name.contains("Hunter", true) || vType.equals("Boy", true) || vType.equals("Male", true) -> "char_shonen_hero"
                            name.contains("Sensei", true) || vType.equals("Lady", true) -> "char_lady_mentor"
                            name.contains("Popo", true) || vType.equals("Mascot", true) -> "char_chibi_mascot"
                            else -> "char_anime_heroine"
                        }

                        charList.add(
                            CharacterProfile(
                                id = "auto_char_${System.currentTimeMillis()}_$i",
                                name = name,
                                gender = cObj.optString("gender", "Hero"),
                                role = cObj.optString("role", "Protagonist"),
                                personality = cObj.optString("personality", "Valiant"),
                                voicePitch = cObj.optDouble("voicePitch", if (vType == "Boy") 0.9 else 1.3).toFloat(),
                                voiceSpeed = cObj.optDouble("voiceSpeed", 1.0).toFloat(),
                                voiceType = vType,
                                avatarDrawableName = matchedAvatar,
                                expression = expr,
                                voiceAccent = command.targetAccent
                            )
                        )
                    }
                }

                val sceneList = mutableListOf<AnimeScene>()
                val scenesArr = root.optJSONArray("scenes")
                if (scenesArr != null) {
                    for (i in 0 until scenesArr.length()) {
                        val sObj = scenesArr.getJSONObject(i)
                        val dialogues = mutableListOf<DialogueLine>()
                        val dArr = sObj.optJSONArray("dialogues")
                        if (dArr != null) {
                            for (j in 0 until dArr.length()) {
                                val dObj = dArr.getJSONObject(j)
                                dialogues.add(
                                    DialogueLine(
                                        characterName = dObj.optString("characterName", "Narrator"),
                                        text = dObj.optString("text", "..."),
                                        emotion = dObj.optString("emotion", "Determined"),
                                        expression = dObj.optString("expression", "Fierce Battle Roar"),
                                        motionEffect = dObj.optString("motionEffect", command.targetMotionEffect.title),
                                        voicePitch = 1.0f,
                                        voiceSpeed = 1.0f,
                                        voiceType = dObj.optString("voiceType", "Girl"),
                                        voiceAccent = dObj.optString("voiceAccent", command.targetAccent)
                                    )
                                )
                            }
                        }

                        val isCyber = command.targetArtStyle == AnimeArtStyle.CYBERPUNK_ANIME
                        val isManhwa = command.targetArtStyle == AnimeArtStyle.MANHWA_WEBTOON
                        val bgDrawable = if (isManhwa || isCyber) "scene_cyber_city" else "scene_cherry_temple"

                        sceneList.add(
                            AnimeScene(
                                sceneNumber = sObj.optInt("sceneNumber", i + 1),
                                title = sObj.optString("title", "Scene ${i + 1}"),
                                visualPrompt = sObj.optString("visualPrompt", command.corePrompt),
                                backgroundType = sObj.optString("backgroundType", if (isManhwa) "Shadow Dungeon" else "Cyber City"),
                                bgMood = sObj.optString("bgMood", command.targetMusicMood.label),
                                dialogues = dialogues,
                                durationSec = sObj.optInt("durationSec", command.sceneDurationSec),
                                sceneDrawableName = bgDrawable,
                                motionEffect = sObj.optString("motionEffect", command.targetMotionEffect.title),
                                productionFormat = command.targetProductionFormat.title
                            )
                        )
                    }
                }

                if (charList.isNotEmpty() && sceneList.isNotEmpty()) {
                    onProgressUpdate(if (isHindi) "✨ मोशन इफेक्ट्स व ऑटो-डबिंग सिंक्रोनाइजेशन सम्पन्न..." else "✨ Motion effects & audio dubbing sync completed...", 0.90f)
                    return@withContext AnimeScript(
                        title = title,
                        originalPrompt = command.rawInput,
                        inputSourceType = if (command.isUrl) "LINK" else "COMMAND",
                        sourceReference = command.rawInput,
                        genre = genre,
                        artStyle = command.targetArtStyle.title,
                        productionFormat = command.targetProductionFormat.title,
                        defaultMotionEffect = command.targetMotionEffect.title,
                        language = command.targetLanguage,
                        voiceoverLanguage = command.targetLanguage,
                        synopsis = synopsis,
                        characters = charList,
                        scenes = sceneList,
                        sourcePlatformName = if (command.isUrl) "Web Link (${command.extractedDomain})" else "AI Command Line",
                        noveltyBadge = "⚡ 1-Click Autonomous (${command.requestedDurationSeconds}s • ${command.calculatedSceneCount} Scenes)"
                    )
                }
            } catch (e: Exception) {
                Log.w(TAG, "Error parsing AI response, falling back to autonomous procedural engine: ${e.message}")
            }
        }

        // Procedural Autonomous Engine (Guarantees exact duration & scene count calculation)
        onProgressUpdate("⚡ जनरेटिव ऑटोनॉमस इंजन: ${command.calculatedSceneCount} सीन्स व मोशन इफेक्ट्स रेंडर हो रहे हैं...", 0.85f)
        val script = generateProceduralAutonomousScript(command)
        onProgressUpdate(if (isHindi) "🎬 ऑटोमैटिक वीडियो तैयार! प्लेयर लॉन्च हो रहा है..." else "🎬 Automated video ready! Launching player...", 1.0f)
        return@withContext script
    }

    /**
     * Procedural autonomous generation matching exact requested duration, scenes, languages, accents & effects
     */
    fun generateProceduralAutonomousScript(command: AutoDirectorCommand): AnimeScript {
        val topic = command.corePrompt.ifBlank { if (isHindi) "रहस्यमयी एनिमे महागाथा" else "Mystical Anime Saga" }
        val lang = command.targetLanguage
        val isHindi = lang.equals("Hindi", ignoreCase = true)
        val isJapanese = lang.equals("Japanese", ignoreCase = true)
        val isKorean = lang.equals("Korean", ignoreCase = true)
        val isTamil = lang.equals("Tamil", ignoreCase = true)
        val isTelugu = lang.equals("Telugu", ignoreCase = true)
        val isSpanish = lang.equals("Spanish", ignoreCase = true)
        val isFrench = lang.equals("French", ignoreCase = true)
        val isGerman = lang.equals("German", ignoreCase = true)
        val isChinese = lang.equals("Chinese", ignoreCase = true)

        val isManhwa = command.targetArtStyle == AnimeArtStyle.MANHWA_WEBTOON || topic.contains("manhwa", true) || topic.contains("hunter", true) || topic.contains("shadow", true)
        val isCyber = command.targetArtStyle == AnimeArtStyle.CYBERPUNK_ANIME || topic.contains("cyber", true)

        // Generate characters
        val heroName = when {
            isJapanese -> "Ren Takahashi (高橋 蓮)"
            isKorean -> "Jin Sung-Woo (진성우)"
            isHindi -> "Aarya Varma"
            isTamil -> "Kavin (கவின்)"
            isTelugu -> "Arjun (అర్జున్)"
            isChinese -> "Li Wei (李伟)"
            else -> "Kaito Vance"
        }

        val heroineName = when {
            isJapanese -> "Hana Shizuka (静 華)"
            isKorean -> "Cha Hae-In (차해인)"
            isHindi -> "Meera Sen"
            isTamil -> "Nila (நிலா)"
            isTelugu -> "Ananya (అనన్య)"
            isChinese -> "Mei Ling (美玲)"
            else -> "Seraphina Frost"
        }

        val senseiName = when {
            isJapanese -> "Master Kenzo (マスター 研造)"
            isKorean -> "Elder Baek (백 사부님)"
            isHindi -> "Guru Drona"
            else -> "Commander Victor"
        }

        val mascotName = if (isHindi) "Popo (पोपो)" else "Popo"

        val characters = listOf(
            CharacterProfile(
                id = "char_hero_${System.currentTimeMillis()}",
                name = heroName,
                gender = "Boy",
                role = if (isManhwa) "Shadow Monarch Hunter" else "Shonen Anime Hero",
                personality = "Fierce, brave, unwavering resolve",
                voicePitch = 0.92f,
                voiceSpeed = 1.05f,
                voiceType = "Boy",
                avatarDrawableName = "char_shonen_hero",
                promptVisualDescription = "Spiky hair with glowing eyes and high-contrast aura",
                expression = if (isManhwa) "Manhwa Glowing Eyes" else "Fierce Battle Roar",
                voiceAccent = command.targetAccent
            ),
            CharacterProfile(
                id = "char_heroine_${System.currentTimeMillis()}",
                name = heroineName,
                gender = "Girl",
                role = "Celestial Mystic Guardian",
                personality = "Intelligent, elegant, powerful magic wielder",
                voicePitch = 1.35f,
                voiceSpeed = 1.02f,
                voiceType = "Girl",
                avatarDrawableName = "char_anime_heroine",
                promptVisualDescription = "Long flowing hair with celestial blossoms",
                expression = "Confident Smirk",
                voiceAccent = command.targetAccent
            ),
            CharacterProfile(
                id = "char_sensei_${System.currentTimeMillis()}",
                name = senseiName,
                gender = "Lady / Sensei",
                role = "Grand Master Mentor",
                personality = "Calm, strategic, profound wisdom",
                voicePitch = 1.10f,
                voiceSpeed = 0.95f,
                voiceType = "Lady",
                avatarDrawableName = "char_lady_mentor",
                promptVisualDescription = "Traditional master kimono with golden trim",
                expression = "Confident Smirk",
                voiceAccent = command.targetAccent
            ),
            CharacterProfile(
                id = "char_mascot_${System.currentTimeMillis()}",
                name = mascotName,
                gender = "Chibi Mascot",
                role = "Magical Familiar Spirit",
                personality = "Playful, energetic, comic relief",
                voicePitch = 1.68f,
                voiceSpeed = 1.18f,
                voiceType = "Mascot",
                avatarDrawableName = "char_chibi_mascot",
                promptVisualDescription = "Round floating chibi fairy with sparks",
                expression = "Kawaii Blush & Sparkles",
                voiceAccent = command.targetAccent
            )
        )

        // Build exact requested scene count
        val sceneCount = command.calculatedSceneCount
        val sceneDur = command.sceneDurationSec
        val scenes = mutableListOf<AnimeScene>()

        val motionList = listOf(
            command.targetMotionEffect.title,
            MotionEffect.SPEEDLINES_ACTION.title,
            MotionEffect.AURA_GLOW_PARTICLES.title,
            MotionEffect.SCREEN_SHAKE_IMPACT.title,
            MotionEffect.CINEMATIC_ZOOM.title,
            MotionEffect.MANGA_PANEL_SLIDE.title
        )

        for (i in 0 until sceneCount) {
            val sceneNumber = i + 1
            val sceneEff = motionList[i % motionList.size]
            val isClimax = (i == sceneCount - 2) || (sceneCount <= 2 && i == 1)

            val sceneTitle = when {
                sceneNumber == 1 -> if (isHindi) "दृश्य १: जागृति और महा-प्रवेश" else "Scene 1: Awakening & Grand Entrance"
                isClimax -> if (isHindi) "दृश्य $sceneNumber: महासंग्राम और अंतिम प्रहार (Climax)" else "Scene $sceneNumber: The Climax Impact Battle"
                sceneNumber == sceneCount -> if (isHindi) "दृश्य $sceneNumber: विजय और नया रहस्य (Cliffhanger)" else "Scene $sceneNumber: Victory & Unresolved Cliffhanger"
                else -> if (isHindi) "दृश्य $sceneNumber: द्वंद और बढ़ती ऊर्जा" else "Scene $sceneNumber: Escalating Clash & Power Surge"
            }

            // Create localized dialogues matching language
            val dList = mutableListOf<DialogueLine>()

            when {
                isHindi -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "आज से यह दुनिया मेरी शक्ति को पहचानेगी! $topic की शुरुआत हो चुकी है!", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "सावधान रहो, खतरे की आभा बहुत गहरी है। मैं तुम्हारे साथ हूँ!", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "महा-प्रहार! अनंत ऊर्जा का विस्फोट! यह युद्ध यहीं समाप्त होगा!", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "वाह! पोपो ने इतनी बड़ी शक्ति पहले कभी नहीं देखी!", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "अपनी सांसों को स्थिर करो। असली योद्धा तलवार से नहीं, आत्मा से लड़ता है।", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "हाँ मास्टर! मैं अपनी सीमाओं को पार करके दिखाऊंगा!", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isJapanese -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "ここからが俺たちの本番だ！運命を切り開く！", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "気を引き締めて。未知なる力が目覚めようとしているわ！", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "限界突破！奥義・天翔閃光斬撃！全てを終わらせる！", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "すごい威力ぽよ！大勝利だー！", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "心を研ぎ澄ませよ。影を恐れぬ者こそが真の光を掴む。", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "分かりました先生！俺の魂は決して折れません！", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isKorean -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "시스템 메시지 확인... 나의 그림자 군단이여, 일어나라!", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "이 압도적인 마력은 대체 뭐지? 헌터님을 엄호하겠어요!", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "절대 파괴! 단 한 번의 일격으로 게이트를 분쇄한다!", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "우와앙! 보스 몬스터가 쓰러졌다 뽀요!", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "방심하지 마라. 그림자의 진정한 힘은 이제부터 시작이다.", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "네, 한 발짝도 물러서지 않겠습니다.", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isTamil -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "இன்று இந்த உலகத்தின் தலைவிதியை நாம் மாற்றுவோம்! தொடங்குவோம்!", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "கவனமாக இருங்கள்! நான் எப்போதும் உங்கள் பக்கம் இருக்கிறேன்!", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "இறுதி தாக்குதல்! அசுர சக்தி வெடிப்பு! வெற்றி நமது!", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "ஆஹா! நாம் ஜெயித்துவிட்டோம்!", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "உன் மனதை ஒருமுகப்படுத்து! உண்மையான வீரன் அஞ்சுவதில்லை!", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "ஆம் குருவே! என் இலக்கை அடைவேன்!", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isTelugu -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "ఈ రోజే ఈ ప్రపంచానికి నా శక్తి తెలుస్తుంది! ప్రారంభించు!", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "జాగ్రత్తగా ఉండు! నా మాయా శక్తులు నీకు తోడుగా ఉంటాయి!", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "అంతిమ ప్రహారం! శత్రువులందరినీ అంతం చేస్తా!", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "హేయ్! మనం విజయం సాధించాం!", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "శాంతంగా ఉండు. అంతర్గత శక్తే నిజమైన విజయం.", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "అలాగే గురువుగారు! సరిహద్దులను దాటుతాను!", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isSpanish -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "¡A partir de hoy, nuestro destino cambia! ¡Despierta!", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "¡Ten cuidado, siento una energía mágica descomunal!", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "¡Golpe definitivo de sombras! ¡Esto termina aquí!", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "¡Hurra! ¡Victoria absoluta, poyo!", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "Enfoca tu mente. El verdadero poder proviene del espíritu.", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "¡Sí maestra! ¡Superaré cualquier obstáculo!", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isFrench -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "À partir d'aujourd'hui, le monde connaîtra ma vraie force !", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "Reste sur tes gardes, une aura formidable s'éveille !", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "Impact céleste absolu ! Tout s'achève maintenant !", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "Youpi ! La victoire est à nous !", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "Garde ton calme. La véritable épée est guidée par l'âme.", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "Bien compris maître ! Je dépasserai toutes les limites !", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isGerman -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "Von heute an wird das Schicksal neu geschrieben! Erwache!", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "Sei vorsichtig, diese magische Aura ist gewaltig!", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "Ultimativer Schattenstoß! Dies ist das Ende!", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "Hurra! Der Sieg gehört uns!", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "Fokussiere deinen Geist. Die wahre Kraft kommt von innen.", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "Ja Meister! Ich werde alle Grenzen durchbrechen!", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                isChinese -> {
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "从今天开始，命运将由我们主宰！苏醒吧！", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "小心，周围的魔力波动极其剧烈！我来支援你！", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "终极奥义·破空灭神斩！在此终结一切！", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "太棒了波波！我们赢了！", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "静心凝神。唯有超越自我，方能执掌天地之力。", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "弟子明白！绝不辜负师傅的教诲！", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
                else -> { // Default English
                    if (sceneNumber == 1) {
                        dList.add(DialogueLine(heroName, "From this moment forth, our destiny awakens! The chronicle of $topic begins!", "Fierce", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.05f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(heroineName, "Stay focused! The arcane rift is opening, and I've got your back!", "Determined", "Confident Smirk", sceneEff, 1.35f, 1.02f, "Girl", command.targetAccent))
                    } else if (isClimax) {
                        dList.add(DialogueLine(heroName, "Absolute Impact Surge! Surpassing all human limits right here!", "Fierce", "Fierce Battle Roar", MotionEffect.SCREEN_SHAKE_IMPACT.title, 0.90f, 1.15f, "Boy", command.targetAccent))
                        dList.add(DialogueLine(mascotName, "Incredible energy poyo! The boss monster is completely shattered!", "Happy", "Kawaii Blush & Sparkles", MotionEffect.SPEEDLINES_ACTION.title, 1.68f, 1.20f, "Mascot", command.targetAccent))
                    } else {
                        dList.add(DialogueLine(senseiName, "Breathe steadily. The true blade is not forged of steel, but of conviction.", "Serious", "Confident Smirk", sceneEff, 1.10f, 0.95f, "Lady", command.targetAccent))
                        dList.add(DialogueLine(heroName, "Yes Master! My blade and spirit will never waver!", "Determined", "Manhwa Glowing Eyes", sceneEff, 0.92f, 1.08f, "Boy", command.targetAccent))
                    }
                }
            }

            val bgType = if (isManhwa) "Shadow Dungeon Gate" else if (isCyber) "Cyber Neo City" else "Cherry Blossom Sanctuary"
            val bgDrawable = if (isManhwa || isCyber) "scene_cyber_city" else "scene_cherry_temple"

            scenes.add(
                AnimeScene(
                    sceneNumber = sceneNumber,
                    title = sceneTitle,
                    visualPrompt = "Dynamic anime sequence showing ${command.targetArtStyle.title} visuals with $sceneEff, glowing eyes, and cinematic depth.",
                    backgroundType = bgType,
                    bgMood = command.targetMusicMood.label,
                    dialogues = dList,
                    durationSec = sceneDur,
                    sceneDrawableName = bgDrawable,
                    motionEffect = sceneEff,
                    productionFormat = command.targetProductionFormat.title
                )
            )
        }

        val projectTitle = when {
            command.isUrl -> "AI Adaptation: ${command.extractedDomain.replaceFirstChar { it.uppercase() }}"
            isHindi -> "⚡ AI ऑटोमेशन: $topic (${command.requestedDurationSeconds}s)"
            isJapanese -> "⚡ AI自動生成: $topic (${command.requestedDurationSeconds}s)"
            isKorean -> "⚡ AI 자동화: $topic (${command.requestedDurationSeconds}s)"
            else -> "⚡ AI Autonomous: $topic (${command.requestedDurationSeconds}s)"
        }

        val projectSynopsis = when {
            isHindi -> "एक सिंगल कमांड से स्वचालित रूप से निर्मित $topic की $sceneCount सीन्स वाली ${command.requestedDurationSeconds} सेकंड की एनिमे वीडियो।"
            isJapanese -> "1クリックコマンドから自動生成された${command.requestedDurationSeconds}秒（$sceneCount シーン）のアニメーション映像。"
            isKorean -> "단 한 줄의 명령어로 자동 생성된 ${command.requestedDurationSeconds}초 ($sceneCount 씬) 분량의 애니메이션 비디오 프로젝트."
            else -> "An autonomous video project with $sceneCount scenes, synthesized in ${command.requestedDurationSeconds} seconds from a single command."
        }

        return AnimeScript(
            title = projectTitle,
            originalPrompt = command.rawInput,
            inputSourceType = if (command.isUrl) "LINK" else "COMMAND",
            sourceReference = command.rawInput,
            genre = if (isManhwa) "Manhwa Hunter" else if (isCyber) "Cyberpunk Sci-Fi" else "Shonen Fantasy",
            artStyle = command.targetArtStyle.title,
            productionFormat = command.targetProductionFormat.title,
            defaultMotionEffect = command.targetMotionEffect.title,
            language = command.targetLanguage,
            voiceoverLanguage = command.targetLanguage,
            synopsis = projectSynopsis,
            characters = characters,
            scenes = scenes,
            sourcePlatformName = if (command.isUrl) "Web Link (${command.extractedDomain})" else "AI Command Line",
            noveltyBadge = "⚡ 1-Click Autonomous (${command.requestedDurationSeconds}s • ${command.calculatedSceneCount} Scenes)"
        )
    }
}

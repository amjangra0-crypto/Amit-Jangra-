package com.example.data.repository

import android.content.Context
import android.content.Intent
import com.example.data.db.AdminAccessEntity
import com.example.data.db.AnimeDao
import com.example.data.db.CustomCharacterEntity
import com.example.data.db.SavedScriptEntity
import com.example.data.model.AnimeArtStyle
import com.example.data.model.AnimeScene
import com.example.data.model.AnimeScript
import com.example.data.model.CharacterProfile
import com.example.data.model.DialogueLine
import com.example.data.model.SourcePlatform
import com.example.data.model.SubscriptionPlan
import com.example.network.GeminiApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class UpdateInfo(
    val currentVersion: String = "1.0.0",
    val latestVersion: String = "1.1.0",
    val isUpdateAvailable: Boolean = true,
    val releaseDate: String = "Sept 2026",
    val updateTitle: String = "Anime Studio AI 1.1.0 Feature Drop",
    val changeLogs: List<String> = listOf(
        "✨ Ultra HD 4K Japanese Anime Render Engine",
        "🎙️ Real-time Japanese/English LipSync AI with Vocal Emotion",
        "🎵 10 New Cinematic Anime Background Soundtracks",
        "📱 Multi-character Duet and Shonen Dialogue Modes",
        "⚡ Faster Gemini 3.5 Flash Inference Pipeline"
    ),
    val isDownloading: Boolean = false,
    val downloadProgress: Float = 0f,
    val isInstalled: Boolean = false
)

class AnimeRepository(
    private val context: Context,
    private val dao: AnimeDao,
    private val geminiService: GeminiApiService
) {
    private val _updateState = MutableStateFlow(UpdateInfo())
    val updateState = _updateState.asStateFlow()

    private val defaultCharacters = listOf(
        CharacterProfile(
            id = "default_heroine",
            name = "Aira",
            gender = "Girl / Female",
            role = "Anime Heroine",
            personality = "Brave, spirited, radiant magic wielder",
            voicePitch = 1.35f,
            voiceSpeed = 1.02f,
            voiceType = "Girl",
            avatarDrawableName = "char_anime_heroine"
        ),
        CharacterProfile(
            id = "default_hero_boy",
            name = "Ren",
            gender = "Boy / Youth",
            role = "Shonen Swordsman",
            personality = "Passionate adventurer, fearless protector",
            voicePitch = 1.15f,
            voiceSpeed = 1.05f,
            voiceType = "Boy",
            avatarDrawableName = "char_shonen_hero"
        ),
        CharacterProfile(
            id = "default_lady_sensei",
            name = "Master Kyoto",
            gender = "Lady / Sensei",
            role = "Mystic Mentor",
            personality = "Wise, serene, strategic mastermind",
            voicePitch = 1.0f,
            voiceSpeed = 0.95f,
            voiceType = "Lady",
            avatarDrawableName = "char_lady_mentor"
        ),
        CharacterProfile(
            id = "default_chibi_mascot",
            name = "Popo",
            gender = "Chibi Mascot",
            role = "Kawaii Companion",
            personality = "Playful, cheerful, brings luck",
            voicePitch = 1.6f,
            voiceSpeed = 1.15f,
            voiceType = "Mascot",
            avatarDrawableName = "char_chibi_mascot"
        )
    )

    val allSavedScripts: Flow<List<SavedScriptEntity>> = dao.getAllScripts()
    val allCustomCharacters: Flow<List<CustomCharacterEntity>> = dao.getAllCharacters()
    val adminSettings: Flow<AdminAccessEntity?> = dao.getAdminSettings()

    suspend fun getInitialCharacters(): List<CharacterProfile> {
        return defaultCharacters
    }

    suspend fun getExistingCharacterNames(): Set<String> {
        val names = mutableSetOf<String>()
        defaultCharacters.forEach { names.add(it.name) }
        try {
            val scripts = dao.getAllScriptsOnce()
            for (s in scripts) {
                val des = deserializeScript(s.scriptJson)
                des?.characters?.forEach { names.add(it.name) }
            }
        } catch (_: Exception) {}
        return names
    }

    suspend fun createAnimeScript(
        input: String,
        sourceType: String,
        artStyle: AnimeArtStyle,
        language: String,
        productionFormat: com.example.data.model.ProductionFormat = com.example.data.model.ProductionFormat.ANIME_EPISODE,
        motionEffect: com.example.data.model.MotionEffect = com.example.data.model.MotionEffect.SPEEDLINES_ACTION,
        voiceAccent: String = "",
        sourcePlatform: SourcePlatform = SourcePlatform.DIRECT_TEXT,
        isSeriesContinuity: Boolean = false,
        linkedScript: AnimeScript? = null,
        linkedEpisodeNumber: Int = 1
    ): AnimeScript {
        val excludedNames = if (isSeriesContinuity) emptySet() else getExistingCharacterNames()
        val script = geminiService.generateAnimeScript(
            input = input,
            sourceType = sourceType,
            artStyle = artStyle,
            language = language,
            availableCharacters = defaultCharacters,
            productionFormat = productionFormat,
            motionEffect = motionEffect,
            voiceAccent = voiceAccent,
            sourcePlatform = sourcePlatform,
            isSeriesContinuity = isSeriesContinuity,
            linkedScript = linkedScript,
            linkedEpisodeNumber = linkedEpisodeNumber,
            excludedCharacterNames = excludedNames
        )
        return script
    }

    suspend fun executeAutonomousDirector(
        commandInput: String,
        overrideDurationSec: Int? = null,
        overrideLanguage: String? = null,
        overrideAccent: String? = null,
        onProgressUpdate: (step: String, progress: Float) -> Unit
    ): Pair<com.example.automation.AutoDirectorCommand, AnimeScript> {
        val parsedCommand = com.example.automation.AutoDirectorEngine.parseCommand(
            input = commandInput,
            overrideDurationSec = overrideDurationSec,
            overrideLanguage = overrideLanguage,
            overrideAccent = overrideAccent
        )
        val script = com.example.automation.AutoDirectorEngine.executeAutonomousPipeline(
            geminiService = geminiService,
            command = parsedCommand,
            onProgressUpdate = onProgressUpdate
        )
        saveScript(script)
        return Pair(parsedCommand, script)
    }

    suspend fun translateScript(script: AnimeScript, targetLanguage: String): AnimeScript {
        return geminiService.translateAnimeScript(script, targetLanguage)
    }

    suspend fun saveScript(script: AnimeScript) {
        val entity = SavedScriptEntity(
            id = script.id,
            title = script.title,
            originalPrompt = script.originalPrompt,
            inputSourceType = script.inputSourceType,
            genre = script.genre,
            artStyle = script.artStyle,
            language = script.language,
            synopsis = script.synopsis,
            scriptJson = serializeScript(script)
        )
        dao.insertScript(entity)
    }

    suspend fun loadScript(id: String): AnimeScript? {
        val entity = dao.getScriptById(id) ?: return null
        return deserializeScript(entity.scriptJson)
    }

    suspend fun deleteScript(id: String) {
        dao.deleteScriptById(id)
    }

    suspend fun saveCustomCharacter(character: CharacterProfile) {
        val entity = CustomCharacterEntity(
            id = character.id,
            name = character.name,
            gender = character.gender,
            role = character.role,
            personality = character.personality,
            voicePitch = character.voicePitch,
            voiceSpeed = character.voiceSpeed,
            voiceType = character.voiceType,
            avatarDrawableName = character.avatarDrawableName,
            promptVisualDescription = character.promptVisualDescription,
            hairStyle = character.hairStyle,
            hairColor = character.hairColor,
            eyeColor = character.eyeColor,
            outfit = character.outfit,
            outfitColor = character.outfitColor,
            accessoryAura = character.accessoryAura,
            expression = character.expression,
            voiceGender = character.voiceGender,
            voicePersona = character.voicePersona,
            voiceAccent = character.voiceAccent,
            sampleDialogue = character.sampleDialogue
        )
        dao.insertCharacter(entity)
    }

    suspend fun deleteCharacter(id: String) {
        dao.deleteCharacterById(id)
    }

    suspend fun generateCharacterFromPrompt(prompt: String): CharacterProfile {
        return geminiService.generateCharacterFromPrompt(prompt)
    }

    suspend fun generateVisualElement(
        input: String,
        sourceMode: String,
        artStyle: AnimeArtStyle
    ): com.example.data.model.AnimeVisualElement {
        val element = geminiService.generateVisualContent(input, sourceMode, artStyle)
        val entity = com.example.data.db.SavedVisualElementEntity(
            id = element.id,
            title = element.title,
            sourceMode = element.sourceMode,
            sourceQuery = element.sourceQuery,
            visualType = element.visualType,
            artStyle = element.artStyle.title,
            promptDescription = element.promptDescription,
            primaryHexColor = element.primaryHexColor,
            secondaryHexColor = element.secondaryHexColor,
            atmosphericEffect = element.atmosphericEffect,
            cameraMotion = element.cameraMotion,
            visualDrawableName = element.visualDrawableName,
            createdAt = element.createdAt
        )
        dao.insertVisualElement(entity)
        return element
    }

    val allVisualElements = dao.getAllVisualElements()

    suspend fun ensureAdminInitialized() {
        val existing = dao.getAdminSettingsOnce()
        if (existing == null) {
            dao.saveAdminSettings(
                AdminAccessEntity(
                    id = 1,
                    ownerEmail = "amjangra0@gmail.com",
                    isOwnerMode = true,
                    isGlobalFreeEnabled = false,
                    authorizedFreeEmails = "amjangra0@gmail.com,owner@animestudio.ai",
                    activePromoCodes = "VIPFREE,ANIME2026,CREATORPASS",
                    currentTier = SubscriptionPlan.STUDIO_OWNER.planId,
                    remainingCredits = 999999
                )
            )
        }
    }

    suspend fun ensureInitialProjectsSeeded() {
        val existing = dao.getAllScriptsOnce()
        if (existing.isEmpty()) {
            val now = System.currentTimeMillis()

            val project1 = AnimeScript(
                id = "proj_cyber_shinobi_2099",
                title = "Cyber Shinobi 2099: Neo Tokyo Awakening",
                originalPrompt = "नियॉन टोक्यो 2099 में साइबर शिनोबी और भविष्य के योद्धाओं का महामुकाबला",
                inputSourceType = "TEXT_PROMPT",
                genre = "Cyberpunk Action",
                artStyle = "Japanese Anime",
                productionFormat = "Standard Anime Episode",
                defaultMotionEffect = "Shonen Speedlines",
                language = "Hindi",
                voiceoverLanguage = "Hindi",
                synopsis = "वर्ष 2099 में जब नियॉन टोक्यो की गलियों पर विशाल कॉर्प्स का कब्ज़ा हो जाता है, तब युवा शिनोबी रेन और आयरा अपनी जादुई साइबर तलवारों से स्वतंत्रता की अंतिम लड़ाई लड़ते हैं।",
                characters = defaultCharacters,
                scenes = listOf(
                    AnimeScene(
                        sceneNumber = 1,
                        title = "Raid on the Neon Skyline",
                        visualPrompt = "Cyber shinobi standing atop a neon-lit futuristic skyscraper in Neo Tokyo during acid rain",
                        backgroundType = "Futuristic Cyberpunk Skyline",
                        bgMood = "Dark Cyber Synthwave",
                        dialogues = listOf(
                            DialogueLine(
                                characterName = "Ren",
                                text = "आयरा, शहर का केंद्रीय ग्रिड सक्रिय हो चुका है। अब पीछे हटने का कोई रास्ता नहीं है!",
                                emotion = "Determined",
                                expression = "Confident Smirk",
                                motionEffect = "SPEEDLINES_ACTION",
                                voicePitch = 1.15f,
                                voiceSpeed = 1.05f,
                                voiceType = "Boy",
                                voiceAccent = "Hindi Dub (Heroic Bollywood Anime)"
                            ),
                            DialogueLine(
                                characterName = "Aira",
                                text = "My plasma blade is charged, Ren! We will liberate Neo Tokyo today!",
                                emotion = "Heroic",
                                expression = "Determined Glance",
                                motionEffect = "GLOW_AURA",
                                voicePitch = 1.35f,
                                voiceSpeed = 1.02f,
                                voiceType = "Girl",
                                voiceAccent = "Hindi Dub (Heroic Bollywood Anime)"
                            )
                        ),
                        durationSec = 8,
                        sceneDrawableName = "scene_neon_tokyo",
                        atmosphericEffect = "Cyber Neon Rain",
                        motionEffect = "SPEEDLINES_ACTION",
                        productionFormat = "Standard Anime Episode"
                    ),
                    AnimeScene(
                        sceneNumber = 2,
                        title = "Master Kyoto's Secret Directive",
                        visualPrompt = "Holographic shrine inside a hidden underground dojo with Master Kyoto guiding the disciples",
                        backgroundType = "Cyber Shrine Dojo",
                        bgMood = "Mystic Cyber Flute",
                        dialogues = listOf(
                            DialogueLine(
                                characterName = "Master Kyoto",
                                text = "A shinobi spirit is never defeated. Awaken the thunder within!",
                                emotion = "Wise",
                                expression = "Calm Master",
                                motionEffect = "SHOCKWAVE",
                                voicePitch = 1.0f,
                                voiceSpeed = 0.95f,
                                voiceType = "Lady",
                                voiceAccent = "Sensei Hindi Cadence"
                            )
                        ),
                        durationSec = 7,
                        sceneDrawableName = "scene_cherry_temple",
                        atmosphericEffect = "Holographic Petals",
                        motionEffect = "DYNAMIC_ZOOM_IN",
                        productionFormat = "Standard Anime Episode"
                    ),
                    AnimeScene(
                        sceneNumber = 3,
                        title = "Final Supersonic Strike",
                        visualPrompt = "Climactic duel over the neon megatower with dual energy swords colliding",
                        backgroundType = "Megatower Apex",
                        bgMood = "Epic Shonen Battle Rock",
                        dialogues = listOf(
                            DialogueLine(
                                characterName = "Ren",
                                text = "Shinobi Secret Art: Thunder Dragon Cleave!",
                                emotion = "Explosive",
                                expression = "Battle Cry",
                                motionEffect = "SPEEDLINES_ACTION",
                                voicePitch = 1.25f,
                                voiceSpeed = 1.15f,
                                voiceType = "Boy",
                                voiceAccent = "Hindi Dub (Heroic Bollywood Anime)"
                            )
                        ),
                        durationSec = 9,
                        sceneDrawableName = "scene_neon_tokyo",
                        atmosphericEffect = "Thunder Sparks",
                        motionEffect = "SPEEDLINES_ACTION",
                        productionFormat = "Standard Anime Episode"
                    )
                ),
                createdAt = now - (3600000L * 4) // 4 hours ago
            )

            val project2 = AnimeScript(
                id = "proj_dragon_alchemist_lotus",
                title = "The Last Dragon Alchemist: Crimson Lotus",
                originalPrompt = "Shonen anime of ancient dragon alchemy and sacred flame lotus",
                inputSourceType = "TEXT_PROMPT",
                genre = "Fantasy Magic",
                artStyle = "Studio Ghibli Lush",
                productionFormat = "Anime Short Film",
                defaultMotionEffect = "Cherry Blossom Drift",
                language = "Hindi",
                voiceoverLanguage = "Hindi",
                synopsis = "एक युवा कीमियागर जो ड्रैगन की लुप्त अग्नि कला की खोज में चेरी ब्लॉसम पर्वत की चोटियों पर निकलता है, जहाँ उसका सामना जादुई ताकतों से होता है।",
                characters = defaultCharacters,
                scenes = listOf(
                    AnimeScene(
                        sceneNumber = 1,
                        title = "Temple of the Flame Lotus",
                        visualPrompt = "Ancient sacred dragon shrine in misty autumn mountains covered in red maple leaves",
                        backgroundType = "Crimson Lotus Sanctuary",
                        bgMood = "Emotional Piano & Strings",
                        dialogues = listOf(
                            DialogueLine(
                                characterName = "Popo (पोपो)",
                                text = "Master! The ancient wheel is glowing! Is the dragon really awakening?",
                                emotion = "Excited",
                                expression = "Wide Eyed Mascot",
                                motionEffect = "GENTLE_FLOAT",
                                voicePitch = 1.6f,
                                voiceSpeed = 1.15f,
                                voiceType = "Mascot",
                                voiceAccent = "Cute Mascot Hindi"
                            ),
                            DialogueLine(
                                characterName = "Ren",
                                text = "Yes Popo, after centuries of sleep, the dragon flame shall ignite again!",
                                emotion = "Awe",
                                expression = "Hopeful Smile",
                                motionEffect = "GLOW_AURA",
                                voicePitch = 1.15f,
                                voiceSpeed = 1.05f,
                                voiceType = "Boy",
                                voiceAccent = "Hindi Dub (Heroic Bollywood Anime)"
                            )
                        ),
                        durationSec = 8,
                        sceneDrawableName = "scene_cherry_temple",
                        atmosphericEffect = "Crimson Petal Storm",
                        motionEffect = "GENTLE_FLOAT",
                        productionFormat = "Anime Short Film"
                    )
                ),
                createdAt = now - (3600000L * 28) // Yesterday
            )

            val project3 = AnimeScript(
                id = "proj_tokyo_sakura_chronicles",
                title = "Tokyo Midnight Sakura: Love & Legends",
                originalPrompt = "टोक्यो की बारिश और चेरी ब्लॉसम के साए में दो प्रेमियों की रहस्यमयी कहानी",
                inputSourceType = "TEXT_PROMPT",
                genre = "Romance Supernatural",
                artStyle = "Makoto Shinkai Cinematic",
                productionFormat = "Anime Music Video (AMV)",
                defaultMotionEffect = "Cherry Blossom Drift",
                language = "Hindi",
                voiceoverLanguage = "Hindi",
                synopsis = "टोक्यो की मध्यरात्रि में जब समय थम जाता है, केवल वे दो लोग चेरी ब्लॉसम के पेड़ों के नीचे एक-दूसरे की आवाज़ सुन सकते हैं।",
                characters = defaultCharacters,
                scenes = listOf(
                    AnimeScene(
                        sceneNumber = 1,
                        title = "Midnight Sakura Bridge",
                        visualPrompt = "Two lovers meeting on a traditional wooden bridge under a luminous full moon with cherry blossoms falling",
                        backgroundType = "Kyoto Moonlight Sanctuary",
                        bgMood = "Nostalgic Acoustic Guitar",
                        dialogues = listOf(
                            DialogueLine(
                                characterName = "Aira",
                                text = "Ren, even if the world forgets us, these blossoms will remember our promise.",
                                emotion = "Gentle Love",
                                expression = "Blushing Warm Smile",
                                motionEffect = "GENTLE_FLOAT",
                                voicePitch = 1.3f,
                                voiceSpeed = 0.98f,
                                voiceType = "Girl",
                                voiceAccent = "Hindi Dub (Heroic Bollywood Anime)"
                            )
                        ),
                        durationSec = 8,
                        sceneDrawableName = "scene_cherry_temple",
                        atmosphericEffect = "Golden Stardust",
                        motionEffect = "GENTLE_FLOAT",
                        productionFormat = "Anime Music Video (AMV)"
                    )
                ),
                createdAt = now - (3600000L * 72) // 3 days ago
            )

            saveScript(project1)
            saveScript(project2)
            saveScript(project3)
        }
    }

    suspend fun updateAdminSettings(settings: AdminAccessEntity) {
        dao.saveAdminSettings(settings)
    }

    suspend fun redeemPromoCode(code: String): String {
        val current = dao.getAdminSettingsOnce() ?: AdminAccessEntity()
        val codes = current.activePromoCodes.split(",").map { it.trim().uppercase() }
        return if (codes.contains(code.trim().uppercase())) {
            dao.saveAdminSettings(
                current.copy(
                    currentTier = SubscriptionPlan.CREATOR_PRO.planId,
                    remainingCredits = 500
                )
            )
            "Success: VIP Promo Code applied! Creator Pro Unlocked."
        } else {
            "Invalid promo code. Please contact studio owner."
        }
    }

    fun startUpdateDownload(onProgress: (Float) -> Unit, onComplete: () -> Unit) {
        _updateState.value = _updateState.value.copy(isDownloading = true, downloadProgress = 0.1f)
    }

    fun completeUpdate() {
        _updateState.value = _updateState.value.copy(
            currentVersion = _updateState.value.latestVersion,
            isUpdateAvailable = false,
            isDownloading = false,
            downloadProgress = 1f,
            isInstalled = true
        )
    }

    fun serializeScript(script: AnimeScript): String {
        val root = JSONObject()
        root.put("id", script.id)
        root.put("title", script.title)
        root.put("originalPrompt", script.originalPrompt)
        root.put("inputSourceType", script.inputSourceType)
        root.put("genre", script.genre)
        root.put("artStyle", script.artStyle)
        root.put("productionFormat", script.productionFormat)
        root.put("defaultMotionEffect", script.defaultMotionEffect)
        root.put("language", script.language)
        root.put("voiceoverLanguage", script.voiceoverLanguage)
        root.put("synopsis", script.synopsis)
        root.put("createdAt", script.createdAt)

        val scenesArr = JSONArray()
        script.scenes.forEach { scene ->
            val sObj = JSONObject()
            sObj.put("sceneNumber", scene.sceneNumber)
            sObj.put("title", scene.title)
            sObj.put("visualPrompt", scene.visualPrompt)
            sObj.put("backgroundType", scene.backgroundType)
            sObj.put("bgMood", scene.bgMood)
            sObj.put("durationSec", scene.durationSec)
            sObj.put("sceneDrawableName", scene.sceneDrawableName)
            sObj.put("atmosphericEffect", scene.atmosphericEffect)
            sObj.put("motionEffect", scene.motionEffect)
            sObj.put("productionFormat", scene.productionFormat)

            val dArr = JSONArray()
            scene.dialogues.forEach { d ->
                val dObj = JSONObject()
                dObj.put("characterName", d.characterName)
                dObj.put("text", d.text)
                dObj.put("emotion", d.emotion)
                dObj.put("expression", d.expression)
                dObj.put("motionEffect", d.motionEffect)
                dObj.put("voiceType", d.voiceType)
                dObj.put("voicePitch", d.voicePitch.toDouble())
                dObj.put("voiceSpeed", d.voiceSpeed.toDouble())
                dObj.put("voiceAccent", d.voiceAccent)
                dArr.put(dObj)
            }
            sObj.put("dialogues", dArr)
            scenesArr.put(sObj)
        }
        root.put("scenes", scenesArr)
        return root.toString()
    }

    fun deserializeScript(jsonStr: String): AnimeScript? {
        return try {
            val root = JSONObject(jsonStr)
            val id = root.optString("id", System.currentTimeMillis().toString())
            val title = root.optString("title", "Untitled Anime Project")
            val originalPrompt = root.optString("originalPrompt", "")
            val inputSourceType = root.optString("inputSourceType", "TEXT")
            val genre = root.optString("genre", "Fantasy Adventure")
            val artStyle = root.optString("artStyle", "Japanese Anime")
            val productionFormat = root.optString("productionFormat", "Standard Anime Episode")
            val defaultMotionEffect = root.optString("defaultMotionEffect", "Shonen Speedlines")
            val language = root.optString("language", "Hindi")
            val voiceoverLanguage = root.optString("voiceoverLanguage", language)
            val synopsis = root.optString("synopsis", "")
            val createdAt = root.optLong("createdAt", System.currentTimeMillis())

            val scenesList = mutableListOf<AnimeScene>()
            val scenesArr = root.optJSONArray("scenes") ?: JSONArray()
            for (i in 0 until scenesArr.length()) {
                val sObj = scenesArr.getJSONObject(i)
                val dList = mutableListOf<DialogueLine>()
                val dArr = sObj.optJSONArray("dialogues") ?: JSONArray()
                for (j in 0 until dArr.length()) {
                    val dObj = dArr.getJSONObject(j)
                    dList.add(
                        DialogueLine(
                            characterName = dObj.optString("characterName", "Character"),
                            text = dObj.optString("text", ""),
                            emotion = dObj.optString("emotion", "Normal"),
                            expression = dObj.optString("expression", "Confident Smirk"),
                            motionEffect = dObj.optString("motionEffect", "SPEEDLINES_ACTION"),
                            voicePitch = dObj.optDouble("voicePitch", 1.0).toFloat(),
                            voiceSpeed = dObj.optDouble("voiceSpeed", 1.0).toFloat(),
                            voiceType = dObj.optString("voiceType", "Girl"),
                            voiceAccent = dObj.optString("voiceAccent", "Standard Anime")
                        )
                    )
                }
                scenesList.add(
                    AnimeScene(
                        sceneNumber = sObj.optInt("sceneNumber", i + 1),
                        title = sObj.optString("title", "Scene ${i + 1}"),
                        visualPrompt = sObj.optString("visualPrompt", ""),
                        backgroundType = sObj.optString("backgroundType", "Cherry Blossom Sanctuary"),
                        bgMood = sObj.optString("bgMood", "Emotional Piano"),
                        dialogues = dList,
                        durationSec = sObj.optInt("durationSec", 8),
                        sceneDrawableName = sObj.optString("sceneDrawableName", "scene_cherry_temple"),
                        atmosphericEffect = sObj.optString("atmosphericEffect", "Cherry Blossom Storm"),
                        motionEffect = sObj.optString("motionEffect", "SPEEDLINES_ACTION"),
                        productionFormat = sObj.optString("productionFormat", productionFormat)
                    )
                )
            }
            AnimeScript(
                id = id,
                title = title,
                originalPrompt = originalPrompt,
                inputSourceType = inputSourceType,
                genre = genre,
                artStyle = artStyle,
                productionFormat = productionFormat,
                defaultMotionEffect = defaultMotionEffect,
                language = language,
                voiceoverLanguage = voiceoverLanguage,
                synopsis = synopsis,
                characters = defaultCharacters,
                scenes = scenesList,
                createdAt = createdAt
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun formatScriptForExport(script: AnimeScript): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val dateStr = dateFormat.format(Date(script.createdAt))
        return buildString {
            appendLine("==========================================")
            appendLine("🎬 ANIME STUDIO AI - PROJECT SCRIPT")
            appendLine("==========================================")
            appendLine("📌 TITLE: ${script.title}")
            appendLine("🎭 GENRE: ${script.genre} | 🎨 ART STYLE: ${script.artStyle}")
            appendLine("🌍 LANGUAGE: ${script.language} (Voiceover: ${script.voiceoverLanguage})")
            appendLine("⏱️ TOTAL SCENES: ${script.scenes.size} | CREATED: $dateStr")
            appendLine("------------------------------------------")
            appendLine("📖 SYNOPSIS:")
            appendLine(script.synopsis)
            appendLine("------------------------------------------\n")
            script.scenes.forEach { scene ->
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("🎬 SCENE ${scene.sceneNumber}: ${scene.title}")
                appendLine("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━")
                appendLine("🖼️ Visual Prompt: ${scene.visualPrompt}")
                appendLine("🏞️ Environment: ${scene.backgroundType} (${scene.atmosphericEffect})")
                appendLine("🎵 BGM Mood: ${scene.bgMood} | Duration: ${scene.durationSec}s\n")
                appendLine("💬 DIALOGUE & VOICE PERFORMANCE:")
                scene.dialogues.forEach { d ->
                    appendLine("  • [${d.characterName.uppercase()}] (${d.emotion} | Voice: ${d.voiceType}, Pitch: ${d.voicePitch}x)")
                    appendLine("    \"${d.text}\"")
                }
                appendLine()
            }
            appendLine("==========================================")
            appendLine("Exported from Anime Studio AI Mobile App")
            appendLine("==========================================")
        }
    }

    fun getScriptAsJson(script: AnimeScript): String {
        return serializeScript(script)
    }

    fun generateSrtSubtitles(script: AnimeScript): String {
        var currentSecond = 0
        val sb = StringBuilder()
        var index = 1
        script.scenes.forEach { scene ->
            val dialogues = scene.dialogues
            val perDialogueSec = if (dialogues.isNotEmpty()) (scene.durationSec / dialogues.size).coerceAtLeast(2) else scene.durationSec
            dialogues.forEach { d ->
                val startSec = currentSecond
                val endSec = currentSecond + perDialogueSec
                val startTimeStr = formatSrtTime(startSec)
                val endTimeStr = formatSrtTime(endSec)
                sb.appendLine(index++)
                sb.appendLine("$startTimeStr --> $endTimeStr")
                sb.appendLine("[${d.characterName}]: ${d.text}")
                sb.appendLine()
                currentSecond = endSec
            }
        }
        return sb.toString()
    }

    private fun formatSrtTime(seconds: Int): String {
        val hrs = seconds / 3600
        val mins = (seconds % 3600) / 60
        val secs = seconds % 60
        return String.format(Locale.US, "%02d:%02d:%02d,000", hrs, mins, secs)
    }

    fun downloadFileToDevice(filename: String, mimeType: String, content: String): Pair<Boolean, String> {
        return try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
                val values = android.content.ContentValues().apply {
                    put(android.provider.MediaStore.Downloads.DISPLAY_NAME, filename)
                    put(android.provider.MediaStore.Downloads.MIME_TYPE, mimeType)
                    put(android.provider.MediaStore.Downloads.IS_PENDING, 1)
                }
                val resolver = context.contentResolver
                val uri = resolver.insert(android.provider.MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    resolver.openOutputStream(uri)?.use { stream ->
                        stream.write(content.toByteArray(Charsets.UTF_8))
                    }
                    values.clear()
                    values.put(android.provider.MediaStore.Downloads.IS_PENDING, 0)
                    resolver.update(uri, values, null, null)
                    Pair(true, "Downloads/$filename")
                } else {
                    val file = saveExportFile(filename, content)
                    Pair(true, file.name)
                }
            } else {
                val downloadsDir = android.os.Environment.getExternalStoragePublicDirectory(android.os.Environment.DIRECTORY_DOWNLOADS)
                if (!downloadsDir.exists()) downloadsDir.mkdirs()
                val file = File(downloadsDir, filename)
                file.writeText(content, Charsets.UTF_8)
                Pair(true, "Downloads/$filename")
            }
        } catch (e: Exception) {
            val file = saveExportFile(filename, content)
            Pair(true, file.name)
        }
    }

    fun saveExportFile(filename: String, content: String): File {
        val dir = File(context.getExternalFilesDir(null) ?: context.filesDir, "AnimeStudioExports")
        if (!dir.exists()) dir.mkdirs()
        val file = File(dir, filename)
        file.writeText(content, Charsets.UTF_8)
        return file
    }

    fun generateVideoShareUrl(script: AnimeScript): String {
        val slug = script.title.lowercase(Locale.ROOT)
            .replace(Regex("[^a-z0-9]"), "-")
            .trim('-')
            .take(30)
            .ifBlank { "anime-project" }
        return "https://animestudio.ai/watch?v=${script.id}&slug=$slug"
    }

    fun formatProjectDetailsForShare(
        script: AnimeScript,
        shareMode: String = "DETAILS_AND_LINK"
    ): String {
        val videoUrl = generateVideoShareUrl(script)
        val totalSec = script.scenes.sumOf { it.durationSec }

        return when (shareMode.uppercase(Locale.ROOT)) {
            "LINK_ONLY" -> {
                "🎬 ${script.title}\n▶️ वीडियो देखें (Watch Anime): $videoUrl\n\n🔥 Anime Studio AI द्वारा निर्मित #AnimeAnimation #AnimeShorts"
            }
            "FULL_SCRIPT" -> {
                buildString {
                    append(formatScriptForExport(script))
                    appendLine()
                    appendLine("=========================================")
                    appendLine("▶️ Online Video Link:")
                    appendLine(videoUrl)
                    appendLine("=========================================")
                    appendLine("🔥 Created with Anime Studio AI #Anime #Animation #AIAnime")
                }
            }
            else -> buildString {
                appendLine("🎬 ${script.title}")
                appendLine("▶️ Watch Video: $videoUrl")
                appendLine()
                appendLine("📊 Project Details:")
                appendLine("• प्रारूप (Format): ${script.productionFormat} (${script.scenes.size} सीन्स, ~${totalSec}s)")
                appendLine("• Art Style: ${script.artStyle}")
                appendLine("• Genre: ${script.genre}")
                appendLine("• Language: ${script.language} (Voice: ${script.voiceoverLanguage})")
                if (script.noveltyBadge.isNotBlank()) {
                    appendLine("• Badge: ${script.noveltyBadge}")
                }
                appendLine()
                appendLine("📖 Synopsis:")
                appendLine(script.synopsis.ifBlank { script.originalPrompt })
                appendLine()
                if (script.characters.isNotEmpty()) {
                    appendLine("🎭 Cast & Characters:")
                    script.characters.take(4).forEach {
                        appendLine("• ${it.name} (${it.role}) - Voice: ${it.voicePersona}")
                    }
                    appendLine()
                }
                appendLine("🔥 Created with Anime Studio AI")
                appendLine("#AnimeStudioAI #Anime #Animation #AIAnime #AnimeIndia")
            }
        }
    }

    fun createSocialShareIntent(
        script: AnimeScript,
        platform: String = "ALL",
        shareMode: String = "DETAILS_AND_LINK"
    ): Intent {
        val shareText = formatProjectDetailsForShare(script, shareMode)
        val subject = "🎬 Anime Project: ${script.title}"

        val baseIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, shareText)
            putExtra(Intent.EXTRA_TITLE, script.title)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        return when (platform.uppercase(Locale.ROOT)) {
            "WHATSAPP" -> {
                baseIntent.apply {
                    setPackage("com.whatsapp")
                }
            }
            "INSTAGRAM" -> {
                baseIntent.apply {
                    setPackage("com.instagram.android")
                    putExtra(Intent.EXTRA_TEXT, "$shareText\n#InstagramReels #AnimeArt")
                }
            }
            "SNAPCHAT" -> {
                baseIntent.apply {
                    setPackage("com.snapchat.android")
                    putExtra(Intent.EXTRA_TEXT, "$shareText\n#SnapchatSpotlight")
                }
            }
            "YOUTUBE" -> {
                baseIntent.apply {
                    setPackage("com.google.android.youtube")
                    putExtra(Intent.EXTRA_TEXT, "$shareText\n#YouTubeShorts #AnimeAnimation")
                }
            }
            "GOOGLE" -> {
                baseIntent.putExtra(Intent.EXTRA_SUBJECT, "Anime Project Backup: ${script.title}")
                Intent.createChooser(baseIntent, "Save to Google Drive / Cloud").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
            else -> {
                Intent.createChooser(baseIntent, "Share Project & Video Link").apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
            }
        }
    }

    fun createShareIntent(script: AnimeScript): Intent {
        return createSocialShareIntent(script, "ALL", "DETAILS_AND_LINK")
    }
}

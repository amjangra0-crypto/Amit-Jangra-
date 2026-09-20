package com.example.data.repository

import android.content.Context
import com.example.data.db.AdminAccessEntity
import com.example.data.db.AnimeDao
import com.example.data.db.CustomCharacterEntity
import com.example.data.db.SavedScriptEntity
import com.example.data.model.AnimeArtStyle
import com.example.data.model.AnimeScript
import com.example.data.model.CharacterProfile
import com.example.data.model.SubscriptionPlan
import com.example.network.GeminiApiService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import org.json.JSONObject

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
            name = "Aira (आयरा)",
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
            name = "Ren (रेन)",
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
            name = "Master Kyoto (क्योटो)",
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
            name = "Popo (पोपो)",
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

    suspend fun createAnimeScript(
        input: String,
        sourceType: String,
        artStyle: AnimeArtStyle,
        language: String
    ): AnimeScript {
        val script = geminiService.generateAnimeScript(
            input = input,
            sourceType = sourceType,
            artStyle = artStyle,
            language = language,
            availableCharacters = defaultCharacters
        )
        return script
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

    private fun serializeScript(script: AnimeScript): String {
        val root = JSONObject()
        root.put("id", script.id)
        root.put("title", script.title)
        root.put("genre", script.genre)
        root.put("artStyle", script.artStyle)
        root.put("language", script.language)
        root.put("synopsis", script.synopsis)

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

            val dArr = JSONArray()
            scene.dialogues.forEach { d ->
                val dObj = JSONObject()
                dObj.put("characterName", d.characterName)
                dObj.put("text", d.text)
                dObj.put("emotion", d.emotion)
                dObj.put("voiceType", d.voiceType)
                dObj.put("voicePitch", d.voicePitch.toDouble())
                dObj.put("voiceSpeed", d.voiceSpeed.toDouble())
                dArr.put(dObj)
            }
            sObj.put("dialogues", dArr)
            scenesArr.put(sObj)
        }
        root.put("scenes", scenesArr)
        return root.toString()
    }
}

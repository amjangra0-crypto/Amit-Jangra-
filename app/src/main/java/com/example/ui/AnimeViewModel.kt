package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AnimeMusicSynthesizer
import com.example.audio.VoiceSyncEngine
import com.example.data.db.AdminAccessEntity
import com.example.data.db.AnimeDatabase
import com.example.data.model.AnimeArtStyle
import com.example.data.model.AnimeScene
import com.example.data.model.AnimeScript
import com.example.data.model.AnimeVisualElement
import com.example.data.model.CharacterProfile
import com.example.data.model.DialogueLine
import com.example.data.model.SubtitleMode
import com.example.data.model.SupportedLanguage
import com.example.data.repository.AnimeRepository
import com.example.network.GeminiApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconName: String) {
    STUDIO("Studio", "movie_filter"),
    PLAYER("Video Player", "play_circle"),
    CHARACTERS("Characters", "face"),
    SUBSCRIPTION("VIP & Admin", "workspace_premium"),
    UPDATES("Updates", "system_update")
}

data class AnimeStudioUiState(
    val currentTab: AppTab = AppTab.STUDIO,
    val promptInput: String = "जादुई चेरी ब्लॉसम मंदिर और जापानी सामुराई की दास्तान",
    val linkInput: String = "https://animenews.org/legends/sakura-blade",
    val imageInputDescription: String = "Anime warrior under neon cherry blossoms",
    val selectedInputMode: Int = 0, // 0: Text Prompt, 1: Web Link, 2: Image Scanner
    val selectedArtStyle: AnimeArtStyle = AnimeArtStyle.JAPANESE_ANIME,
    val selectedLanguage: String = "Hindi",
    val voiceoverLanguage: String = "Hindi",
    val subtitleMode: SubtitleMode = SubtitleMode.TRANSLATED_ONLY,
    val isGenerating: Boolean = false,
    val generationStep: String = "",
    val currentScript: AnimeScript? = null,
    val activeSceneIndex: Int = 0,
    val isPlayingVideo: Boolean = false,
    val activeSpeakerName: String = "",
    val activeDialogueText: String = "",
    val activeDialogueEmotion: String = "",
    val currentDialogueIndex: Int = 0,
    val isMusicMuted: Boolean = false,
    val isOwnerAdmin: Boolean = true,
    val promoCodeInput: String = "",
    val promoCodeMessage: String = "",
    val statusMessage: String = "",
    val customCharacters: List<CharacterProfile> = emptyList(),
    // Character Studio & AI Designer
    val characterPromptInput: String = "Cyber samurai warrior with blue flame katana and silver hair",
    val isAiDesigningCharacter: Boolean = false,
    val characterDraft: CharacterProfile = CharacterProfile(
        id = "draft_initial",
        name = "Kaito (कायतो)",
        gender = "Boy / Youth",
        role = "Cyber Shinobi",
        personality = "Courageous, honorable, swift as lightning",
        voicePitch = 0.95f,
        voiceSpeed = 1.05f,
        voiceType = "Boy",
        avatarDrawableName = "char_shonen_hero",
        promptVisualDescription = "Cyber samurai warrior with blue flame katana and silver hair",
        hairStyle = "Spiky Shonen Action",
        hairColor = "Silver Starlight",
        eyeColor = "Sapphire Neon Blue",
        outfit = "Cyber Shinobi Exo-Suit",
        outfitColor = "Obsidian Black & Neon Cyan",
        accessoryAura = "Crackling Blue Lightning Sparks",
        expression = "Fierce Determined Stare",
        voiceGender = "Male",
        voicePersona = "Deep Shonen Hero",
        voiceAccent = "Standard Anime (Japanese Cadence)",
        sampleDialogue = "俺の魂が燃えている！限界を超えてみせる！"
    ),
    // AI Visual Content Generator
    val visualPromptInput: String = "Neo Tokyo floating temple with holographic sakura blossoms and neon rain",
    val visualSourceMode: String = "TEXT_PROMPT", // "TEXT_PROMPT", "UPLOADED_IMAGE", "WEB_LINK"
    val isGeneratingVisual: Boolean = false,
    val latestGeneratedVisual: AnimeVisualElement? = null,
    val savedVisualElements: List<AnimeVisualElement> = emptyList()
)

class AnimeViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AnimeDatabase.getInstance(application)
    private val geminiService = GeminiApiService()
    private val repository = AnimeRepository(application, db.animeDao(), geminiService)

    val voiceSyncEngine = VoiceSyncEngine(application)
    val musicSynthesizer = AnimeMusicSynthesizer()

    private val _uiState = MutableStateFlow(AnimeStudioUiState())
    val uiState: StateFlow<AnimeStudioUiState> = _uiState.asStateFlow()

    val adminSettingsState: StateFlow<AdminAccessEntity?> = repository.adminSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val updateState = repository.updateState

    private var playbackJob: Job? = null

    init {
        viewModelScope.launch {
            repository.ensureAdminInitialized()
            val initialChars = repository.getInitialCharacters()
            _uiState.value = _uiState.value.copy(customCharacters = initialChars)

            // Collect saved visual elements
            repository.allVisualElements.collect { list ->
                val models = list.map { e ->
                    val style = AnimeArtStyle.entries.find { it.title == e.artStyle } ?: AnimeArtStyle.JAPANESE_ANIME
                    AnimeVisualElement(
                        id = e.id,
                        title = e.title,
                        sourceMode = e.sourceMode,
                        sourceQuery = e.sourceQuery,
                        visualType = e.visualType,
                        artStyle = style,
                        promptDescription = e.promptDescription,
                        primaryHexColor = e.primaryHexColor,
                        secondaryHexColor = e.secondaryHexColor,
                        atmosphericEffect = e.atmosphericEffect,
                        cameraMotion = e.cameraMotion,
                        visualDrawableName = e.visualDrawableName,
                        createdAt = e.createdAt
                    )
                }
                _uiState.value = _uiState.value.copy(savedVisualElements = models)
            }
        }
        viewModelScope.launch {
            // Auto-create initial featured anime script for instant preview & testing!
            val initialScript = repository.createAnimeScript(
                input = _uiState.value.promptInput,
                sourceType = "TEXT",
                artStyle = AnimeArtStyle.JAPANESE_ANIME,
                language = "Hindi"
            )
            _uiState.value = _uiState.value.copy(currentScript = initialScript)
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
        if (tab != AppTab.PLAYER && _uiState.value.isPlayingVideo) {
            pauseVideo()
        }
    }

    fun setPromptInput(text: String) {
        _uiState.value = _uiState.value.copy(promptInput = text)
    }

    fun setLinkInput(url: String) {
        _uiState.value = _uiState.value.copy(linkInput = url)
    }

    fun setImageDescription(desc: String) {
        _uiState.value = _uiState.value.copy(imageInputDescription = desc)
    }

    fun setInputMode(mode: Int) {
        _uiState.value = _uiState.value.copy(selectedInputMode = mode)
    }

    fun setArtStyle(style: AnimeArtStyle) {
        _uiState.value = _uiState.value.copy(selectedArtStyle = style)
    }

    fun setLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(
            selectedLanguage = lang,
            voiceoverLanguage = lang
        )
        voiceSyncEngine.setLanguage(lang)
    }

    fun setVoiceoverLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(voiceoverLanguage = lang)
        voiceSyncEngine.setLanguage(lang)
    }

    fun setSubtitleMode(mode: SubtitleMode) {
        _uiState.value = _uiState.value.copy(subtitleMode = mode)
    }

    fun setVisualPromptInput(text: String) {
        _uiState.value = _uiState.value.copy(visualPromptInput = text)
    }

    fun setVisualSourceMode(mode: String) {
        _uiState.value = _uiState.value.copy(visualSourceMode = mode)
    }

    fun generateVisualContent() {
        val state = _uiState.value
        val query = state.visualPromptInput.ifBlank { "Neo Tokyo sakura temple with cinematic lighting" }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingVisual = true)
            val visual = repository.generateVisualElement(
                input = query,
                sourceMode = state.visualSourceMode,
                artStyle = state.selectedArtStyle
            )
            _uiState.value = _uiState.value.copy(
                isGeneratingVisual = false,
                latestGeneratedVisual = visual,
                statusMessage = "🎨 Visual element '${visual.title}' synthesized!"
            )
        }
    }

    fun applyVisualToActiveScene(visual: AnimeVisualElement) {
        val script = _uiState.value.currentScript ?: return
        val activeIdx = _uiState.value.activeSceneIndex
        if (activeIdx in script.scenes.indices) {
            val oldScene = script.scenes[activeIdx]
            val updatedScene = oldScene.copy(
                title = visual.title,
                visualPrompt = visual.promptDescription,
                backgroundType = visual.atmosphericEffect,
                sceneDrawableName = visual.visualDrawableName,
                atmosphericEffect = visual.atmosphericEffect
            )
            val updatedScenes = script.scenes.toMutableList().also { it[activeIdx] = updatedScene }
            _uiState.value = _uiState.value.copy(
                currentScript = script.copy(scenes = updatedScenes),
                statusMessage = "Applied '${visual.title}' to Scene ${activeIdx + 1}!"
            )
        }
    }

    fun setCharacterPromptInput(prompt: String) {
        _uiState.value = _uiState.value.copy(characterPromptInput = prompt)
    }

    fun generateCharacterWithAi() {
        val prompt = _uiState.value.characterPromptInput.ifBlank { "Cyber samurai warrior with blue flame katana" }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiDesigningCharacter = true)
            delay(400)
            val profile = repository.generateCharacterFromPrompt(prompt)
            _uiState.value = _uiState.value.copy(
                isAiDesigningCharacter = false,
                characterDraft = profile,
                statusMessage = "✨ AI designed character '${profile.name}' with matched voice & visual attributes!"
            )
            // Audition voice automatically
            auditionDraftVoice()
        }
    }

    fun updateCharacterDraft(updated: CharacterProfile) {
        _uiState.value = _uiState.value.copy(characterDraft = updated)
    }

    fun saveDraftCharacter() {
        val draft = _uiState.value.characterDraft
        val updated = _uiState.value.customCharacters.filter { it.id != draft.id } + draft
        _uiState.value = _uiState.value.copy(
            customCharacters = updated,
            statusMessage = "Saved character '${draft.name}' to Anime Roster!"
        )
        viewModelScope.launch {
            repository.saveCustomCharacter(draft)
        }
    }

    fun auditionDraftVoice(customText: String? = null) {
        val draft = _uiState.value.characterDraft
        val textToSpeak = customText ?: draft.sampleDialogue.ifBlank {
            "こんにちは！俺は${draft.name}だ！(Hello! I am ${draft.name}!)"
        }
        voiceSyncEngine.speakDialogue(
            characterName = draft.name,
            dialogueText = textToSpeak,
            emotion = "Excited",
            voiceType = draft.voiceType,
            pitch = draft.voicePitch,
            speed = draft.voiceSpeed,
            voiceGender = draft.voiceGender,
            voicePersona = draft.voicePersona,
            voiceAccent = draft.voiceAccent
        )
    }

    fun deleteCustomCharacter(id: String) {
        val updated = _uiState.value.customCharacters.filter { it.id != id }
        _uiState.value = _uiState.value.copy(
            customCharacters = updated,
            statusMessage = "Character removed."
        )
        viewModelScope.launch {
            repository.deleteCharacter(id)
        }
    }

    fun generateAnimeVideo() {
        val state = _uiState.value
        val input = when (state.selectedInputMode) {
            1 -> state.linkInput.ifBlank { "https://animestudio.ai/story" }
            2 -> state.imageInputDescription.ifBlank { "Scanned anime visual scene" }
            else -> state.promptInput.ifBlank { "Epic anime adventure" }
        }
        val sourceType = when (state.selectedInputMode) {
            1 -> "WEB_LINK"
            2 -> "SCANNED_IMAGE"
            else -> "TEXT_PROMPT"
        }

        viewModelScope.launch {
            pauseVideo()
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                generationStep = "AI analyzing $sourceType & building characters..."
            )
            delay(500)

            _uiState.value = _uiState.value.copy(
                generationStep = "Directing anime storyboard scenes with ${state.selectedArtStyle.title}..."
            )
            delay(400)

            _uiState.value = _uiState.value.copy(
                generationStep = "Harmonizing voice synchronization & Japanese audio scores..."
            )

            val script = repository.createAnimeScript(
                input = input,
                sourceType = sourceType,
                artStyle = state.selectedArtStyle,
                language = state.selectedLanguage
            )

            repository.saveScript(script)

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                generationStep = "",
                currentScript = script,
                activeSceneIndex = 0,
                currentDialogueIndex = 0,
                currentTab = AppTab.PLAYER,
                statusMessage = "✨ Anime Script & Video successfully generated!"
            )

            // Auto-start video in player
            playVideo()
        }
    }

    fun translateAndDub(targetLang: String) {
        val script = _uiState.value.currentScript ?: return
        viewModelScope.launch {
            pauseVideo()
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                generationStep = "Translating script & dubbing voices into $targetLang..."
            )

            val translated = repository.translateScript(script, targetLang)
            voiceSyncEngine.setLanguage(targetLang)

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                generationStep = "",
                currentScript = translated,
                selectedLanguage = targetLang,
                statusMessage = "🎌 Successfully dubbed into $targetLang!"
            )
            playVideo()
        }
    }

    fun playVideo() {
        val script = _uiState.value.currentScript ?: return
        if (script.scenes.isEmpty()) return

        _uiState.value = _uiState.value.copy(isPlayingVideo = true)

        val currentScene = script.scenes.getOrNull(_uiState.value.activeSceneIndex) ?: script.scenes.first()
        if (!_uiState.value.isMusicMuted) {
            musicSynthesizer.start(currentScene.bgMood)
        }

        startScenePlayback(_uiState.value.activeSceneIndex, _uiState.value.currentDialogueIndex)
    }

    fun pauseVideo() {
        _uiState.value = _uiState.value.copy(isPlayingVideo = false)
        playbackJob?.cancel()
        playbackJob = null
        voiceSyncEngine.stop()
        musicSynthesizer.stop()
    }

    fun selectScene(sceneIndex: Int) {
        pauseVideo()
        val script = _uiState.value.currentScript ?: return
        if (sceneIndex in script.scenes.indices) {
            _uiState.value = _uiState.value.copy(
                activeSceneIndex = sceneIndex,
                currentDialogueIndex = 0
            )
            playVideo()
        }
    }

    fun nextScene() {
        val script = _uiState.value.currentScript ?: return
        val nextIdx = (_uiState.value.activeSceneIndex + 1) % script.scenes.size
        selectScene(nextIdx)
    }

    fun previousScene() {
        val script = _uiState.value.currentScript ?: return
        val prevIdx = if (_uiState.value.activeSceneIndex - 1 < 0) script.scenes.size - 1 else _uiState.value.activeSceneIndex - 1
        selectScene(prevIdx)
    }

    fun toggleMusic() {
        val newMuted = !_uiState.value.isMusicMuted
        _uiState.value = _uiState.value.copy(isMusicMuted = newMuted)
        if (newMuted) {
            musicSynthesizer.stop()
        } else if (_uiState.value.isPlayingVideo) {
            val scene = _uiState.value.currentScript?.scenes?.getOrNull(_uiState.value.activeSceneIndex)
            musicSynthesizer.start(scene?.bgMood ?: "Emotional Piano")
        }
    }

    private fun startScenePlayback(sceneIdx: Int, startingDialogueIdx: Int) {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val script = _uiState.value.currentScript ?: return@launch
            if (sceneIdx >= script.scenes.size) {
                // Loop back to start
                _uiState.value = _uiState.value.copy(activeSceneIndex = 0, currentDialogueIndex = 0)
                startScenePlayback(0, 0)
                return@launch
            }

            val scene = script.scenes[sceneIdx]
            musicSynthesizer.currentMood = scene.bgMood

            for (dIdx in startingDialogueIdx until scene.dialogues.size) {
                if (!_uiState.value.isPlayingVideo) return@launch

                val dialogue = scene.dialogues[dIdx]
                _uiState.value = _uiState.value.copy(
                    activeSceneIndex = sceneIdx,
                    currentDialogueIndex = dIdx,
                    activeSpeakerName = dialogue.characterName,
                    activeDialogueText = dialogue.text,
                    activeDialogueEmotion = dialogue.emotion
                )

                musicSynthesizer.setDucking(true)

                var speechFinished = false
                val matchingChar = script.characters.find { it.name.equals(dialogue.characterName, ignoreCase = true) }
                    ?: _uiState.value.customCharacters.find { it.name.equals(dialogue.characterName, ignoreCase = true) }

                voiceSyncEngine.speakDialogue(
                    characterName = dialogue.characterName,
                    dialogueText = dialogue.text,
                    emotion = dialogue.emotion,
                    voiceType = dialogue.voiceType,
                    pitch = dialogue.voicePitch,
                    speed = dialogue.voiceSpeed,
                    voiceGender = matchingChar?.voiceGender ?: "",
                    voicePersona = matchingChar?.voicePersona ?: "",
                    voiceAccent = dialogue.voiceAccent.ifBlank { matchingChar?.voiceAccent ?: "" },
                    onStart = {
                        musicSynthesizer.setDucking(true)
                    },
                    onDone = {
                        musicSynthesizer.setDucking(false)
                        speechFinished = true
                    }
                )

                // Wait until dialogue is spoken (with safety timeout)
                val maxWaitMs = 12000L
                var waited = 0L
                while (!speechFinished && waited < maxWaitMs && _uiState.value.isPlayingVideo) {
                    delay(100)
                    waited += 100
                }
                musicSynthesizer.setDucking(false)
                delay(600) // Brief pause between character lines
            }

            // Scene complete! Move to next scene
            if (_uiState.value.isPlayingVideo) {
                val nextIdx = (sceneIdx + 1) % script.scenes.size
                _uiState.value = _uiState.value.copy(
                    activeSceneIndex = nextIdx,
                    currentDialogueIndex = 0
                )
                startScenePlayback(nextIdx, 0)
            }
        }
    }

    fun addCustomCharacter(name: String, gender: String, role: String, personality: String, voicePitch: Float, voiceSpeed: Float, voiceType: String) {
        val newChar = CharacterProfile(
            id = "char_${System.currentTimeMillis()}",
            name = name,
            gender = gender,
            role = role,
            personality = personality,
            voicePitch = voicePitch,
            voiceSpeed = voiceSpeed,
            voiceType = voiceType,
            avatarDrawableName = when {
                voiceType.contains("boy", ignoreCase = true) || voiceType.contains("male", ignoreCase = true) -> "char_shonen_hero"
                voiceType.contains("lady", ignoreCase = true) -> "char_lady_mentor"
                voiceType.contains("mascot", ignoreCase = true) -> "char_chibi_mascot"
                else -> "char_anime_heroine"
            }
        )
        val updated = _uiState.value.customCharacters + newChar
        _uiState.value = _uiState.value.copy(
            customCharacters = updated,
            statusMessage = "Character '$name' created with custom voice sync!"
        )
        viewModelScope.launch {
            repository.saveCustomCharacter(newChar)
        }
    }

    fun testAuditionVoice(character: CharacterProfile) {
        voiceSyncEngine.speakDialogue(
            characterName = character.name,
            dialogueText = "नमस्ते! मैं ${character.name} हूँ। यह मेरी एनिमे आवाज़ है!",
            emotion = "Happy",
            voiceType = character.voiceType,
            pitch = character.voicePitch,
            speed = character.voiceSpeed
        )
    }

    fun redeemPromoCode(code: String) {
        viewModelScope.launch {
            val result = repository.redeemPromoCode(code)
            _uiState.value = _uiState.value.copy(promoCodeMessage = result)
        }
    }

    fun toggleGlobalFree(enabled: Boolean) {
        viewModelScope.launch {
            val current = adminSettingsState.value ?: AdminAccessEntity()
            repository.updateAdminSettings(current.copy(isGlobalFreeEnabled = enabled))
        }
    }

    fun addAuthorizedEmail(email: String) {
        viewModelScope.launch {
            val current = adminSettingsState.value ?: AdminAccessEntity()
            val list = current.authorizedFreeEmails.split(",").map { it.trim() }.toMutableList()
            if (!list.contains(email.trim())) {
                list.add(email.trim())
                repository.updateAdminSettings(current.copy(authorizedFreeEmails = list.joinToString(",")))
                _uiState.value = _uiState.value.copy(statusMessage = "Granted Free VIP access to $email")
            }
        }
    }

    fun triggerInAppUpdate() {
        viewModelScope.launch {
            repository.startUpdateDownload(onProgress = {}, onComplete = {})
            delay(1500)
            repository.completeUpdate()
            _uiState.value = _uiState.value.copy(statusMessage = "App successfully updated to v1.1.0!")
        }
    }

    override fun onCleared() {
        super.onCleared()
        pauseVideo()
        voiceSyncEngine.release()
    }
}

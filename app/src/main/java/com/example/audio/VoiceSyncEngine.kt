package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Locale

data class SpeakingState(
    val isSpeaking: Boolean = false,
    val activeCharacter: String = "",
    val activeDialogue: String = "",
    val activeEmotion: String = "",
    val activeVoiceType: String = "",
    val activeAccent: String = "",
    val activeVoiceMood: String = "Natural",
    val progress: Float = 0f,
    val mouthOpenAmount: Float = 0f, // 0f to 1f for anime lip-sync
    val audioWaveAmplitude: Float = 0f // 0f to 1f for waveform visualizer
)

class VoiceSyncEngine(private val context: Context) {

    private var tts: TextToSpeech? = null
    private var isTtsReady = false
    private val scope = CoroutineScope(Dispatchers.Main)
    private var simulationJob: Job? = null
    private var lipSyncPulseJob: Job? = null

    private val _speakingState = MutableStateFlow(SpeakingState())
    val speakingState = _speakingState.asStateFlow()

    private var onSpeechDoneCallback: (() -> Unit)? = null
    private var onSpeechStartCallback: (() -> Unit)? = null

    init {
        initTts(Locale("hi", "IN"))
    }

    fun initTts(locale: Locale) {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                isTtsReady = true
                val result = tts?.setLanguage(locale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    tts?.language = Locale.ENGLISH
                }
                setupProgressListener()
            } else {
                isTtsReady = false
                Log.w("VoiceSyncEngine", "TTS initialization returned status: $status")
            }
        }
    }

    fun setLanguage(languageCode: String) {
        val locale = when (languageCode.lowercase()) {
            "hi", "hindi" -> Locale("hi", "IN")
            "ja", "japanese" -> Locale.JAPANESE
            "zh", "chinese" -> Locale.SIMPLIFIED_CHINESE
            "es", "spanish" -> Locale("es", "ES")
            "fr", "french" -> Locale.FRENCH
            "de", "german" -> Locale.GERMAN
            "ko", "korean" -> Locale.KOREAN
            "ta", "tamil" -> Locale("ta", "IN")
            "te", "telugu" -> Locale("te", "IN")
            "ru", "russian" -> Locale("ru", "RU")
            "ar", "arabic" -> Locale("ar", "SA")
            else -> Locale.ENGLISH
        }
        if (isTtsReady) {
            val res = tts?.setLanguage(locale)
            if (res == TextToSpeech.LANG_MISSING_DATA || res == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.language = Locale.ENGLISH
            }
        }
    }

    private fun setupProgressListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                scope.launch {
                    startLipSyncAnimation()
                    onSpeechStartCallback?.invoke()
                }
            }

            override fun onDone(utteranceId: String?) {
                scope.launch {
                    stopLipSyncAnimation()
                    _speakingState.value = _speakingState.value.copy(
                        isSpeaking = false,
                        progress = 1f,
                        mouthOpenAmount = 0f,
                        audioWaveAmplitude = 0f
                    )
                    onSpeechDoneCallback?.invoke()
                }
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                scope.launch {
                    stopLipSyncAnimation()
                    _speakingState.value = _speakingState.value.copy(
                        isSpeaking = false,
                        mouthOpenAmount = 0f,
                        audioWaveAmplitude = 0f
                    )
                    onSpeechDoneCallback?.invoke()
                }
            }
        })
    }

    val lipSyncController = LipSyncController()
    val lipSyncFrame: StateFlow<LipSyncVisualFrame> = lipSyncController.currentFrame

    private fun startLipSyncAnimation(dialogueText: String = "", estimatedDurationMs: Long = 2500L) {
        lipSyncPulseJob?.cancel()
        lipSyncController.parseDialogueToPhonemes(dialogueText, estimatedDurationMs)
        val startTime = System.currentTimeMillis()

        lipSyncPulseJob = scope.launch {
            while (_speakingState.value.isSpeaking) {
                val elapsed = System.currentTimeMillis() - startTime
                val frame = lipSyncController.getFrameAtTime(elapsed)
                _speakingState.value = _speakingState.value.copy(
                    mouthOpenAmount = frame.mouthOpenAmount,
                    audioWaveAmplitude = frame.audioAmplitude
                )
                delay(40) // 25fps fluid lip synchronization
            }
        }
    }

    private fun stopLipSyncAnimation() {
        lipSyncPulseJob?.cancel()
        lipSyncPulseJob = null
        lipSyncController.updateLiveFrame(isSpeaking = false, amplitude = 0f, mouthOpen = 0f)
    }

    fun speakDialogue(
        characterName: String,
        dialogueText: String,
        emotion: String,
        voiceType: String,
        pitch: Float,
        speed: Float,
        voiceGender: String = "",
        voicePersona: String = "",
        voiceAccent: String = "",
        voiceMood: String = "Natural",
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null
    ) {
        stop()
        onSpeechStartCallback = onStart
        onSpeechDoneCallback = onDone

        // Compute tailored voice pitch & speed based on Gender & Persona
        val personaPitchMultiplier = when {
            voicePersona.contains("Deep Shonen", ignoreCase = true) -> 0.88f
            voicePersona.contains("Sensei", ignoreCase = true) || voicePersona.contains("Mentor", ignoreCase = true) -> 0.78f
            voicePersona.contains("Antagonist", ignoreCase = true) -> 0.72f
            voicePersona.contains("Cyber Renegade", ignoreCase = true) -> 0.84f
            voicePersona.contains("Kawaii Heroine", ignoreCase = true) -> 1.38f
            voicePersona.contains("Tsundere", ignoreCase = true) -> 1.45f
            voicePersona.contains("Priestess", ignoreCase = true) -> 1.18f
            voicePersona.contains("Cyber Agent", ignoreCase = true) -> 1.24f
            voicePersona.contains("Playful Kid", ignoreCase = true) -> 1.65f
            voicePersona.contains("Apprentice", ignoreCase = true) -> 1.55f
            voicePersona.contains("Chibi", ignoreCase = true) || voicePersona.contains("Fairy", ignoreCase = true) -> 1.82f
            voiceGender.equals("Male", ignoreCase = true) || voiceType.equals("Male", ignoreCase = true) -> 0.85f
            voiceGender.equals("Child", ignoreCase = true) || voiceType.equals("Mascot", ignoreCase = true) -> 1.65f
            voiceGender.equals("Female", ignoreCase = true) || voiceType.equals("Girl", ignoreCase = true) -> 1.35f
            else -> 1.0f
        }

        val personaSpeedMultiplier = when {
            voicePersona.contains("Sensei", ignoreCase = true) || voicePersona.contains("Priestess", ignoreCase = true) -> 0.90f
            voicePersona.contains("Tsundere", ignoreCase = true) || voicePersona.contains("Chibi", ignoreCase = true) -> 1.15f
            voicePersona.contains("Playful Kid", ignoreCase = true) -> 1.12f
            else -> 1.0f
        }

        // Voice Mood Real-Time Filter Modifiers ('Deep Bass', 'High Pitch', 'Robot', 'Echo Ghost')
        val moodPitchMultiplier = when (voiceMood.lowercase()) {
            "deep bass" -> 0.65f
            "high pitch" -> 1.55f
            "robot" -> 0.96f
            "echo ghost" -> 0.82f
            else -> 1.0f
        }

        val moodSpeedMultiplier = when (voiceMood.lowercase()) {
            "deep bass" -> 0.92f
            "high pitch" -> 1.08f
            "robot" -> 0.85f
            "echo ghost" -> 0.88f
            else -> 1.0f
        }

        // Accent modulation
        if (voiceAccent.contains("British", ignoreCase = true)) {
            tts?.language = Locale.UK
        } else if (voiceAccent.contains("American", ignoreCase = true)) {
            tts?.language = Locale.US
        } else if (voiceAccent.contains("Japanese", ignoreCase = true) || voiceAccent.contains("Tokyo", ignoreCase = true)) {
            tts?.language = Locale.JAPANESE
        } else if (voiceAccent.contains("Korean", ignoreCase = true) || voiceAccent.contains("Seoul", ignoreCase = true) || voiceAccent.contains("Manhwa", ignoreCase = true)) {
            tts?.language = Locale.KOREAN
        } else if (voiceAccent.contains("Hindi", ignoreCase = true)) {
            tts?.language = Locale("hi", "IN")
        } else if (voiceAccent.contains("Spanish", ignoreCase = true) || voiceAccent.contains("Madrid", ignoreCase = true)) {
            tts?.language = Locale("es", "ES")
        } else if (voiceAccent.contains("French", ignoreCase = true) || voiceAccent.contains("Paris", ignoreCase = true)) {
            tts?.language = Locale.FRENCH
        } else if (voiceAccent.contains("German", ignoreCase = true)) {
            tts?.language = Locale.GERMAN
        } else if (voiceAccent.contains("Tamil", ignoreCase = true)) {
            tts?.language = Locale("ta", "IN")
        } else if (voiceAccent.contains("Telugu", ignoreCase = true)) {
            tts?.language = Locale("te", "IN")
        } else if (voiceAccent.contains("Russian", ignoreCase = true)) {
            tts?.language = Locale("ru", "RU")
        } else if (voiceAccent.contains("Arabic", ignoreCase = true)) {
            tts?.language = Locale("ar", "SA")
        }

        val finalPitch = (pitch * personaPitchMultiplier * moodPitchMultiplier).coerceIn(0.4f, 2.0f)
        val finalSpeed = (speed * personaSpeedMultiplier * moodSpeedMultiplier).coerceIn(0.5f, 2.0f)

        val processedDialogue = if (voiceMood.equals("robot", ignoreCase = true)) {
            dialogueText.replace(".", " ... [BEEP]. ").replace("!", " ... [SYS-ENGAGE]! ")
        } else {
            dialogueText
        }

        _speakingState.value = SpeakingState(
            isSpeaking = true,
            activeCharacter = characterName,
            activeDialogue = processedDialogue,
            activeEmotion = emotion,
            activeVoiceType = if (voicePersona.isNotBlank()) voicePersona else voiceType,
            activeAccent = voiceAccent,
            activeVoiceMood = voiceMood,
            progress = 0f
        )

        val estWordDuration = (300 / finalSpeed.coerceAtLeast(0.5f)).toLong()
        val estimatedTotalDuration = (dialogueText.split(" ").size.coerceAtLeast(2) * estWordDuration).coerceAtLeast(1800L)
        startLipSyncAnimation(dialogueText, estimatedTotalDuration)

        if (isTtsReady && tts != null) {
            tts?.setPitch(finalPitch)
            tts?.setSpeechRate(finalSpeed)
            val utteranceId = "dialogue_${System.currentTimeMillis()}"
            val res = tts?.speak(dialogueText, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
            if (res != TextToSpeech.SUCCESS) {
                fallbackSimulation(dialogueText, finalSpeed)
            }
        } else {
            fallbackSimulation(dialogueText, finalSpeed)
        }
    }

    /**
     * Speaks dialogue utilizing a dedicated CharacterVoicePreset and VoiceLanguage.
     * Fully synchronized with Character Age Groups and Tone Weights (Heavy/Light).
     */
    fun speakWithPreset(
        preset: com.example.data.model.CharacterVoicePreset,
        dialogueText: String,
        language: com.example.data.model.VoiceLanguage = preset.defaultLanguage,
        characterName: String = preset.nameEn,
        onStart: (() -> Unit)? = null,
        onDone: (() -> Unit)? = null
    ) {
        setLanguage(language.code)
        speakDialogue(
            characterName = characterName,
            dialogueText = dialogueText,
            emotion = "Expressive",
            voiceType = preset.ageGroup.displayNameEn,
            pitch = preset.pitch,
            speed = preset.speed,
            voiceGender = preset.gender,
            voicePersona = "${preset.nameEn} (${preset.toneWeight.displayNameEn})",
            voiceAccent = language.displayNameEn,
            onStart = onStart,
            onDone = onDone
        )
    }

    private fun fallbackSimulation(text: String, speed: Float) {
        simulationJob?.cancel()
        simulationJob = scope.launch {
            val estimatedWordDuration = (280 / speed).toLong()
            val totalDuration = (text.split(" ").size.coerceAtLeast(3) * estimatedWordDuration).coerceAtLeast(1800)
            val step = 100L
            var elapsed = 0L

            while (elapsed < totalDuration) {
                delay(step)
                elapsed += step
                _speakingState.value = _speakingState.value.copy(
                    progress = (elapsed.toFloat() / totalDuration).coerceIn(0f, 1f)
                )
            }

            stopLipSyncAnimation()
            _speakingState.value = _speakingState.value.copy(
                isSpeaking = false,
                progress = 1f,
                mouthOpenAmount = 0f,
                audioWaveAmplitude = 0f
            )
            onSpeechDoneCallback?.invoke()
        }
    }

    fun stop() {
        simulationJob?.cancel()
        simulationJob = null
        stopLipSyncAnimation()
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("VoiceSyncEngine", "Error stopping TTS", e)
        }
        _speakingState.value = _speakingState.value.copy(
            isSpeaking = false,
            mouthOpenAmount = 0f,
            audioWaveAmplitude = 0f
        )
    }

    fun release() {
        stop()
        tts?.shutdown()
        tts = null
    }
}

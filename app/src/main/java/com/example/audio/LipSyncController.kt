package com.example.audio

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.sin

/**
 * Phoneme Visemes for Anime Lip-Syncing
 * Maps speech phonetics to standard 2D/3D anime facial mouth shapes:
 * - REST: Lips closed, relaxed.
 * - A_AH: Wide open jaw (a, ah, ha, ka)
 * - E_EE: Wide horizontal stretch, teeth visible (ee, e, se)
 * - I_IH: Slightly open, neutral width (ih, i, ki)
 * - O_OH: Rounded oval, medium open (o, oh, ko)
 * - U_OO: Puckered rounded, small open (oo, u, ku)
 * - CONSONANT_CLOSED: Lips touching (m, b, p)
 * - FRICATIVE_FV: Top teeth touching bottom lip (f, v)
 * - DENTAL_TD: Tongue/teeth dental closure (t, d, s, z, n)
 */
enum class Viseme(
    val mouthOpenRatio: Float,
    val mouthWidthRatio: Float,
    val jawDropRatio: Float,
    val description: String
) {
    REST(0.02f, 0.50f, 0.05f, "Resting / Closed"),
    A_AH(0.95f, 0.80f, 0.90f, "Wide Open (A, Ah)"),
    E_EE(0.40f, 0.95f, 0.40f, "Horizontal Smile (E, Ee)"),
    I_IH(0.50f, 0.75f, 0.50f, "Half Open (I, Ih)"),
    O_OH(0.85f, 0.45f, 0.75f, "Rounded Open (O, Oh)"),
    U_OO(0.60f, 0.35f, 0.55f, "Puckered (U, Oo)"),
    CONSONANT_CLOSED(0.05f, 0.55f, 0.10f, "Lips Pressed (M, B, P)"),
    FRICATIVE_FV(0.20f, 0.65f, 0.25f, "Fricative (F, V)"),
    DENTAL_TD(0.30f, 0.70f, 0.30f, "Dental (T, D, S, N)")
}

data class PhonemeSegment(
    val startMs: Long,
    val endMs: Long,
    val viseme: Viseme,
    val phonemeSymbol: String,
    val intensity: Float = 1.0f
)

data class LipSyncVisualFrame(
    val viseme: Viseme = Viseme.REST,
    val mouthOpenAmount: Float = 0.02f,
    val mouthWidthScale: Float = 0.5f,
    val jawDropAmount: Float = 0.0f,
    val audioAmplitude: Float = 0.0f,
    val isBlinking: Boolean = false,
    val currentPhoneme: String = ""
)

/**
 * LipSyncController:
 * Maps phonetic segments and speech audio timestamps directly to anime character facial animation frames.
 * Ensures perfect lip-syncing during preview and playback across English, Hindi, and Japanese.
 */
class LipSyncController {

    private val _currentFrame = MutableStateFlow(LipSyncVisualFrame())
    val currentFrame: StateFlow<LipSyncVisualFrame> = _currentFrame.asStateFlow()

    private var activeSegments: List<PhonemeSegment> = emptyList()
    private var totalDurationMs: Long = 0L

    /**
     * Parses dialogue text and generates timed phonetic segments across the audio duration.
     * Maps Devanagari vowels, Japanese Kana syllables, and Latin characters to authentic anime visemes.
     */
    fun parseDialogueToPhonemes(dialogueText: String, durationMs: Long): List<PhonemeSegment> {
        totalDurationMs = durationMs
        val cleanText = dialogueText.trim()
        if (cleanText.isBlank() || durationMs <= 0) {
            activeSegments = emptyList()
            return emptyList()
        }

        val tokens = tokenizeText(cleanText)
        if (tokens.isEmpty()) {
            activeSegments = emptyList()
            return emptyList()
        }

        val stepMs = (durationMs.toFloat() / tokens.size.coerceAtLeast(1)).toLong()
        val segments = mutableListOf<PhonemeSegment>()

        for (i in tokens.indices) {
            val token = tokens[i]
            val viseme = mapCharOrSyllableToViseme(token)
            val start = i * stepMs
            val end = ((i + 1) * stepMs).coerceAtMost(durationMs)
            segments.add(
                PhonemeSegment(
                    startMs = start,
                    endMs = end,
                    viseme = viseme,
                    phonemeSymbol = token,
                    intensity = if (viseme == Viseme.REST) 0f else 0.85f + 0.15f * ((i % 3) / 2f)
                )
            )
        }

        activeSegments = segments
        return segments
    }

    /**
     * Retrieves the precise LipSyncVisualFrame at any playback timestamp in milliseconds.
     */
    fun getFrameAtTime(playbackMs: Long): LipSyncVisualFrame {
        if (activeSegments.isEmpty()) {
            return LipSyncVisualFrame(viseme = Viseme.REST, mouthOpenAmount = 0.02f)
        }

        val segment = activeSegments.find { playbackMs in it.startMs..it.endMs }
            ?: if (playbackMs > totalDurationMs) activeSegments.lastOrNull() else activeSegments.firstOrNull()

        if (segment == null) {
            return LipSyncVisualFrame(viseme = Viseme.REST, mouthOpenAmount = 0.02f)
        }

        // Interpolation within segment
        val segmentDuration = (segment.endMs - segment.startMs).coerceAtLeast(1)
        val progressInSegment = ((playbackMs - segment.startMs).toFloat() / segmentDuration).coerceIn(0f, 1f)
        val pulse = sin(progressInSegment * Math.PI.toFloat()).coerceIn(0f, 1f)

        val mouthOpen = (segment.viseme.mouthOpenRatio * pulse * segment.intensity).coerceIn(0.02f, 1f)
        val mouthWidth = (segment.viseme.mouthWidthRatio * (0.8f + 0.2f * pulse)).coerceIn(0.3f, 1f)
        val jawDrop = (segment.viseme.jawDropRatio * pulse).coerceIn(0f, 1f)
        val amplitude = (mouthOpen * 0.9f).coerceIn(0f, 1f)

        // Occasional anime blink (every ~3.5 seconds)
        val blinkCycle = (playbackMs % 3500L)
        val isBlink = blinkCycle in 1500L..1650L

        val frame = LipSyncVisualFrame(
            viseme = segment.viseme,
            mouthOpenAmount = mouthOpen,
            mouthWidthScale = mouthWidth,
            jawDropAmount = jawDrop,
            audioAmplitude = amplitude,
            isBlinking = isBlink,
            currentPhoneme = segment.phonemeSymbol
        )
        _currentFrame.value = frame
        return frame
    }

    /**
     * Updates frame in real-time when driven by live microphone or TTS engine callbacks.
     */
    fun updateLiveFrame(isSpeaking: Boolean, amplitude: Float, mouthOpen: Float) {
        val viseme = when {
            !isSpeaking || mouthOpen < 0.08f -> Viseme.REST
            mouthOpen > 0.75f -> Viseme.A_AH
            mouthOpen > 0.55f -> Viseme.O_OH
            mouthOpen > 0.35f -> Viseme.E_EE
            mouthOpen > 0.20f -> Viseme.I_IH
            else -> Viseme.CONSONANT_CLOSED
        }

        _currentFrame.value = LipSyncVisualFrame(
            viseme = viseme,
            mouthOpenAmount = if (isSpeaking) mouthOpen.coerceIn(0.02f, 1f) else 0.02f,
            mouthWidthScale = if (isSpeaking) 0.5f + (amplitude * 0.4f) else 0.5f,
            jawDropAmount = if (isSpeaking) (mouthOpen * 0.85f).coerceIn(0f, 1f) else 0.0f,
            audioAmplitude = if (isSpeaking) amplitude.coerceIn(0f, 1f) else 0.0f,
            isBlinking = false,
            currentPhoneme = viseme.name
        )
    }

    private fun tokenizeText(text: String): List<String> {
        val tokens = mutableListOf<String>()
        var i = 0
        while (i < text.length) {
            val char = text[i]
            if (char.isWhitespace() || char in listOf('.', ',', '!', '?', ';', ':', '-', '—')) {
                tokens.add(" ")
                i++
                continue
            }
            // Multilingual phonetic chunking
            val nextChar = if (i + 1 < text.length) text[i + 1] else null
            if (nextChar != null && isDevanagariMatra(nextChar)) {
                tokens.add("$char$nextChar")
                i += 2
            } else {
                tokens.add(char.toString())
                i++
            }
        }
        return tokens
    }

    private fun isDevanagariMatra(c: Char): Boolean {
        return c in '\u093E'..'\u094F' // Devanagari vowel signs (aa, i, ee, u, oo, e, ai, o, au, virama)
    }

    private fun mapCharOrSyllableToViseme(symbol: String): Viseme {
        val s = symbol.lowercase()
        return when {
            s.isBlank() -> Viseme.REST
            // Devanagari Vowels & Matras
            s.contains('आ') || s.contains('ा') || s.contains('अ') || s.contains("aa") || s.contains("ah") -> Viseme.A_AH
            s.contains('ए') || s.contains('े') || s.contains('ऐ') || s.contains('ै') || s.contains("ee") || s.contains("eh") -> Viseme.E_EE
            s.contains('इ') || s.contains('ि') || s.contains('ई') || s.contains('ी') || s.contains("ih") || s.contains("i") -> Viseme.I_IH
            s.contains('ओ') || s.contains('ो') || s.contains('औ') || s.contains('ौ') || s.contains("oh") || s.contains("o") -> Viseme.O_OH
            s.contains('उ') || s.contains('ु') || s.contains('ऊ') || s.contains('ू') || s.contains("oo") || s.contains("u") -> Viseme.U_OO
            // Japanese Kana
            s in listOf("あ", "か", "さ", "た", "な", "は", "ま", "や", "ら", "わ", "a") -> Viseme.A_AH
            s in listOf("え", "け", "せ", "て", "ね", "へ", "め", "れ", "e") -> Viseme.E_EE
            s in listOf("い", "き", "し", "ち", "に", "ひ", "み", "り", "i") -> Viseme.I_IH
            s in listOf("お", "こ", "そ", "と", "の", "ほ", "も", "よ", "ろ", "を", "o") -> Viseme.O_OH
            s in listOf("う", "く", "す", "つ", "ぬ", "ふ", "む", "ゆ", "る", "ん", "u") -> Viseme.U_OO
            // Consonants (Closed / Fricative / Dental)
            s in listOf("m", "b", "p", "म", "ब", "प") -> Viseme.CONSONANT_CLOSED
            s in listOf("f", "v", "फ", "व") -> Viseme.FRICATIVE_FV
            s in listOf("t", "d", "s", "z", "n", "त", "द", "स", "न") -> Viseme.DENTAL_TD
            else -> Viseme.A_AH
        }
    }
}

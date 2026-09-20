package com.example.audio

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Real-time procedural polyphonic music synthesizer for Anime Studio.
 * Generates dynamic atmospheric background scores tailored to scene moods:
 * - Epic Battle: Fast driving arpeggios with energetic pulse
 * - Emotional Piano: Gentle, warm pentatonic chords
 * - Mystery Fantasy: Ethereal bell tones and suspended fifths
 * - Kawaii Playful: Bouncy staccato major melodies
 * - Cyber Synth: Neon cyberpunk resonant sawtooth/sine waves
 */
class AnimeMusicSynthesizer {

    private val sampleRate = 22050
    private var audioTrack: AudioTrack? = null
    private var synthJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default)

    @Volatile
    var isPlaying = false
        private set

    @Volatile
    var currentMood: String = "Emotional Piano"

    @Volatile
    var masterVolume: Float = 0.45f

    @Volatile
    var isDucked: Boolean = false // When character is speaking, volume gently ducks to 25%

    fun start(mood: String = "Emotional Piano") {
        currentMood = mood
        if (isPlaying) return

        try {
            val minBufferSize = AudioTrack.getMinBufferSize(
                sampleRate,
                AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT
            )

            audioTrack = AudioTrack.Builder()
                .setAudioAttributes(
                    AudioAttributes.Builder()
                        .setUsage(AudioAttributes.USAGE_MEDIA)
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .build()
                )
                .setAudioFormat(
                    AudioFormat.Builder()
                        .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                        .setSampleRate(sampleRate)
                        .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                        .build()
                )
                .setBufferSizeInBytes(minBufferSize * 2)
                .setTransferMode(AudioTrack.MODE_STREAM)
                .build()

            audioTrack?.play()
            isPlaying = true

            synthJob = scope.launch {
                synthesizeLoop()
            }
        } catch (e: Exception) {
            Log.e("AnimeMusicSynth", "Failed to start audio track", e)
            isPlaying = false
        }
    }

    fun stop() {
        isPlaying = false
        synthJob?.cancel()
        synthJob = null
        try {
            audioTrack?.stop()
            audioTrack?.release()
        } catch (e: Exception) {
            Log.e("AnimeMusicSynth", "Error stopping audio track", e)
        }
        audioTrack = null
    }

    fun setDucking(duck: Boolean) {
        isDucked = duck
    }

    private suspend fun synthesizeLoop() {
        val bufferSize = 1024
        val buffer = ShortArray(bufferSize)
        var sampleIndex = 0L

        // Frequencies for musical notes (Hz)
        val noteC4 = 261.63
        val noteD4 = 293.66
        val noteE4 = 329.63
        val noteF4 = 349.23
        val noteG4 = 392.00
        val noteA4 = 440.00
        val noteB4 = 493.88
        val noteC5 = 523.25
        val noteE5 = 659.25
        val noteG5 = 783.99
        val noteA3 = 220.00
        val noteF3 = 174.61
        val noteG3 = 196.00

        val emotionalScale = doubleArrayOf(noteC4, noteE4, noteG4, noteB4, noteC5, noteG4, noteE4, noteD4)
        val epicScale = doubleArrayOf(noteA3, noteC4, noteE4, noteA4, noteG4, noteE4, noteD4, noteE4)
        val mysteryScale = doubleArrayOf(noteE4, noteG4, noteB4, noteE5, noteD4, noteA4, noteF4, noteB4)
        val kawaiiScale = doubleArrayOf(noteC4, noteG4, noteE4, noteC5, noteD4, noteA4, noteG4, noteC5)
        val cyberScale = doubleArrayOf(noteF3, noteC4, noteG4, noteA4, noteF4, noteG3, noteD4, noteA4)

        while (isPlaying && audioTrack != null && scope.isActive) {
            val mood = currentMood
            val scale = when {
                mood.contains("Epic", ignoreCase = true) -> epicScale
                mood.contains("Mystery", ignoreCase = true) -> mysteryScale
                mood.contains("Kawaii", ignoreCase = true) || mood.contains("Playful", ignoreCase = true) -> kawaiiScale
                mood.contains("Cyber", ignoreCase = true) -> cyberScale
                else -> emotionalScale
            }

            val tempoFactor = if (mood.contains("Epic", ignoreCase = true) || mood.contains("Cyber", ignoreCase = true)) 2.2 else 1.2
            val effectiveVol = (if (isDucked) masterVolume * 0.25f else masterVolume).coerceIn(0f, 1f)

            for (i in 0 until bufferSize) {
                val time = (sampleIndex + i).toDouble() / sampleRate
                val noteIndex = ((time * tempoFactor) % scale.size).toInt()
                val baseFreq = scale[noteIndex]

                // Polyphony: fundamental + harmonic fifth + subtle bass drone
                val w1 = 2.0 * Math.PI * baseFreq
                val w2 = 2.0 * Math.PI * (baseFreq * 1.5) // harmonic fifth
                val wBass = 2.0 * Math.PI * (baseFreq * 0.5) // bass octave

                val envelope = (0.5 + 0.5 * sin(2.0 * Math.PI * tempoFactor * time)).coerceIn(0.1, 1.0)
                val sampleValue = (
                        sin(w1 * time) * 0.5 +
                        sin(w2 * time) * 0.25 +
                        sin(wBass * time) * 0.25
                ) * envelope * effectiveVol

                buffer[i] = (sampleValue * Short.MAX_VALUE).toInt().coerceIn(Short.MIN_VALUE.toInt(), Short.MAX_VALUE.toInt()).toShort()
            }

            sampleIndex += bufferSize
            audioTrack?.write(buffer, 0, bufferSize)
            delay(10) // cooperative yielding
        }
    }
}

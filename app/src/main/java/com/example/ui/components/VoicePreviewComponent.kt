package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.VoicePersonaOption
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import kotlin.math.sin

/**
 * Voice Preview UI Component
 * Integrates native Canvas audio waveform visualization with 5-second sample playback
 * and customizable pitch/tempo before applying to characters.
 */
@Composable
fun VoicePreviewComponent(
    currentPersona: String = "Sweet Kawaii Heroine",
    currentPitch: Float = 1.0f,
    currentSpeed: Float = 1.0f,
    language: String = "English",
    onPlaySample: (text: String, pitch: Float, speed: Float, gender: String, persona: String) -> Unit = { _, _, _, _, _ -> },
    onApplyVoice: (persona: VoicePersonaOption, pitch: Float, speed: Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val isHindi = com.example.localization.AppLocaleStrings.isHindi(language)
    val voicePersonas = remember {
        listOf(
            VoicePersonaOption("kawaii", "Sweet Kawaii Heroine", "Female", 1.25f, 1.05f, "Sweet anime heroine cadence with sparkling inflection", "Japanese Kawaii"),
            VoicePersonaOption("shonen", "Fiery Shonen Hero", "Male", 0.95f, 1.15f, "High energy battle shouts & determination", "Standard Shonen"),
            VoicePersonaOption("sensei", "Deep Villain / Sensei", "Male", 0.75f, 0.90f, "Deep resonant master with calm tactical cadence", "Mysterious Master"),
            VoicePersonaOption("ninja", "Cyber Shinobi Android", "Female", 1.10f, 1.10f, "Crisp synthetic pitch with rapid ninja precision", "Cyberpunk Vocal"),
            VoicePersonaOption("narrator", "Epic Anime Narrator", "Male", 0.85f, 0.95f, "Authoritative theatrical anime OVA narrator", "Theatrical OVA"),
            VoicePersonaOption("chibi", "Playful Chibi Mascot", "Child", 1.45f, 1.20f, "Cute squeaky mascot comedic anime voice", "Playful Mascot"),
            VoicePersonaOption("tsundere", "Tsundere Rival Girl", "Female", 1.18f, 1.12f, "Sharp emotional transitions & sassy pouts", "Tsundere Rival")
        )
    }

    var selectedPersona by remember {
        mutableStateOf(voicePersonas.find { it.name == currentPersona } ?: voicePersonas.first())
    }
    var pitch by remember { mutableFloatStateOf(currentPitch) }
    var speed by remember { mutableFloatStateOf(currentSpeed) }

    var isPlayingSample by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }
    var playbackTimerSeconds by remember { mutableIntStateOf(5) }

    fun getSampleDialogue(personaId: String): String {
        return when (personaId) {
            "kawaii" -> if (isHindi) "मुझ पर विश्वास रखो! हम मिलकर भविष्य बदलेंगे!" else "Believe in me! Together we will change the future!"
            "shonen" -> if (isHindi) "मेरे दिल की आग कभी बुझ नहीं सकती! तैयार हो जाओ!" else "The fire in my heart will never fade! Prepare yourself!"
            "sensei" -> if (isHindi) "शांत रहो। असली युद्ध तो अब शुरू हुआ है।" else "Stay calm. The battle has only just begun."
            "ninja" -> if (isHindi) "बिना आहट के वार करो। मिशन शुरू।" else "Swift and silent in the shadows. Mission commences."
            "narrator" -> if (isHindi) "किस्मत का पहिया अब धीरे से घूमने लगा है..." else "And so, the gears of destiny quietly turn..."
            "chibi" -> if (isHindi) "हुर्रे! बहुत बड़ा कारनामा हो गया!" else "Yay! We succeeded with soaring colors!"
            else -> if (isHindi) "ऐसा मत समझो कि मैं तुम्हारी परवाह करती हूँ!" else "Don't get the wrong idea, it's not like I care about you!"
        }
    }

    // 5-second playback simulation loop
    LaunchedEffect(isPlayingSample) {
        if (isPlayingSample) {
            playbackTimerSeconds = 5
            val steps = 50
            for (i in 1..steps) {
                delay(100) // 5 seconds total (50 * 100ms)
                playbackProgress = i.toFloat() / steps
                playbackTimerSeconds = (5 - (i / 10)).coerceAtLeast(0)
            }
            isPlayingSample = false
            playbackProgress = 0f
            playbackTimerSeconds = 5
        } else {
            playbackProgress = 0f
        }
    }

    // Dynamic waveform samples computed from pitch and persona
    val waveformData = remember(selectedPersona, pitch, speed) {
        List(42) { idx ->
            val freq = (idx * pitch * 0.45f)
            val base = 0.35f + 0.45f * sin(freq).toFloat()
            val noise = ((idx * 13) % 7) / 25f
            (base + noise).coerceIn(0.15f, 0.95f)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_preview_card"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AnimePink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = AnimePink,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Voice Preview & Audio Waveform",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Listen to 5-second audio sample & check waveform",
                            color = AnimeCyanLight,
                            fontSize = 11.sp
                        )
                    }
                }

                // 5-Second Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeGold.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "5s SAMPLE",
                        color = AnimeGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Voice Persona Selector Horizontal List
            Text(
                text = "Select Voice Persona:",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(voicePersonas, key = { it.id }) { persona ->
                    val isSelected = selectedPersona.id == persona.id
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AnimeCyan else AnimeSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) AnimeCyan else AnimePurple.copy(alpha = 0.3f),
                                RoundedCornerShape(10.dp)
                            )
                            .clickable {
                                selectedPersona = persona
                                pitch = persona.defaultPitch
                                speed = persona.defaultSpeed
                                isPlayingSample = false
                            }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("persona_chip_${persona.id}")
                    ) {
                        Column {
                            Text(
                                text = persona.name,
                                color = if (isSelected) Color.Black else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Text(
                                text = "${persona.gender} • ${persona.defaultAccent}",
                                color = if (isSelected) Color.Black.copy(alpha = 0.7f) else TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Sample Dialogue Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "\"${getSampleDialogue(selectedPersona.id)}\"",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Native Canvas Audio Waveform Component
            AudioWaveformCanvas(
                waveformSamples = waveformData,
                playbackProgress = playbackProgress,
                isPlaying = isPlayingSample,
                barCount = 42,
                height = 56.dp,
                activeBarColor = AnimePink,
                playheadColor = AnimeGold,
                onSeek = { seekPos ->
                    playbackProgress = seekPos
                }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Playback Transport & Countdown Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = {
                            if (!isPlayingSample) {
                                isPlayingSample = true
                                val sampleText = getSampleDialogue(selectedPersona.id)
                                onPlaySample(sampleText, pitch, speed, selectedPersona.gender, selectedPersona.name)
                            } else {
                                isPlayingSample = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isPlayingSample) AnimePink else AnimeCyan
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("voice_preview_play_toggle_btn")
                    ) {
                        Icon(
                            imageVector = if (isPlayingSample) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isPlayingSample) {
                                if (isHindi) "रोकें (Playing)" else "Stop (Playing)"
                            } else {
                                if (isHindi) "▶ 5s सैंपल सुनें" else "▶ Play Sample"
                            },
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = if (isPlayingSample) "00:0${5 - playbackTimerSeconds} / 00:05 s" else "00:05 s Sample",
                        color = AnimeGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Apply Button
                Button(
                    onClick = { onApplyVoice(selectedPersona, pitch, speed) },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("apply_voice_to_char_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isHindi) "✓ करैक्टर पर लागू करें" else "✓ Assign to Character",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dynamic Pitch & Speed fine-tuning controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Pitch slider
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("पिच (Pitch): ${String.format("%.2f", pitch)}x", color = TextMuted, fontSize = 10.sp)
                    Slider(
                        value = pitch,
                        onValueChange = { pitch = it },
                        valueRange = 0.7f..1.6f,
                        colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink),
                        modifier = Modifier.fillMaxWidth().testTag("voice_preview_pitch_slider")
                    )
                }

                // Speed slider
                Column(modifier = Modifier.weight(1f).padding(start = 8.dp)) {
                    Text("गति (Speed): ${String.format("%.2f", speed)}x", color = TextMuted, fontSize = 10.sp)
                    Slider(
                        value = speed,
                        onValueChange = { speed = it },
                        valueRange = 0.8f..1.5f,
                        colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyan),
                        modifier = Modifier.fillMaxWidth().testTag("voice_preview_speed_slider")
                    )
                }
            }
        }
    }
}

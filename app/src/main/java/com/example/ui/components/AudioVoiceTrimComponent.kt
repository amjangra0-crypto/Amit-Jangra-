package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RangeSlider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.VoiceSyncEngine
import com.example.localization.AppLocaleStrings
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.sin

/**
 * Audio Voice Trim Component
 * Visually trims and adjusts start/end points of generated voice-overs using a dual seek bar/RangeSlider
 * and interactive canvas waveform.
 */
@Composable
fun AudioVoiceTrimComponent(
    totalDurationSeconds: Float = 10.0f,
    initialStartSeconds: Float = 0.0f,
    initialEndSeconds: Float = 10.0f,
    characterName: String = "Hero",
    dialogueText: String = "Believe in the tomorrow we choose to forge!",
    voiceSyncEngine: VoiceSyncEngine? = null,
    lang: String = "English",
    initialVoiceMood: String = "Natural",
    onTrimApplied: (startSec: Float, endSec: Float) -> Unit,
    onVoiceMoodChanged: ((String) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    var startSec by remember(initialStartSeconds) { mutableFloatStateOf(initialStartSeconds.coerceAtLeast(0f)) }
    var endSec by remember(initialEndSeconds, totalDurationSeconds) {
        mutableFloatStateOf(initialEndSeconds.coerceAtMost(totalDurationSeconds).coerceAtLeast(startSec + 0.5f))
    }

    var selectedVoiceMood by remember(initialVoiceMood) { mutableStateOf(initialVoiceMood) }
    var isPlayingTrimmed by remember { mutableStateOf(false) }
    var currentPlaybackSec by remember { mutableFloatStateOf(startSec) }
    var playbackJob by remember { mutableStateOf<Job?>(null) }

    val trimmedDuration = (endSec - startSec).coerceAtLeast(0.1f)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("audio_voice_trim_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.6f))
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
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(AnimePink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Voice-Over Trimmer", "वॉइस-ओवर ट्रिमर व कट"),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$characterName • Total: ${"%.1f".format(totalDurationSeconds)}s",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                }

                // Trimmed Duration Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeCyan.copy(alpha = 0.2f))
                        .border(1.dp, AnimeCyan.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "Trim: ${"%.1f".format(trimmedDuration)}s",
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dialogue Line Preview
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .padding(10.dp)
            ) {
                Text(
                    text = "\"$dialogueText\"",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Canvas Waveform Visualizer with Trim Windows
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF0F121C))
            ) {
                Canvas(modifier = Modifier.fillMaxWidth().height(55.dp)) {
                    val width = size.width
                    val height = size.height
                    val barCount = 48
                    val barWidth = width / barCount

                    val startRatio = (startSec / totalDurationSeconds).coerceIn(0f, 1f)
                    val endRatio = (endSec / totalDurationSeconds).coerceIn(0f, 1f)
                    val playRatio = (currentPlaybackSec / totalDurationSeconds).coerceIn(0f, 1f)

                    for (i in 0 until barCount) {
                        val fraction = i.toFloat() / barCount
                        val isInTrimZone = fraction in startRatio..endRatio
                        val barHeightFactor = (0.25f + 0.65f * kotlin.math.abs(sin(i * 0.45f + (startSec * 0.2f)))).toFloat()
                        val barH = height * barHeightFactor

                        val barColor = if (isInTrimZone) {
                            if (fraction <= playRatio && isPlayingTrimmed) AnimeGold else AnimeCyan
                        } else {
                            Color.White.copy(alpha = 0.15f)
                        }

                        val x = i * barWidth + (barWidth * 0.2f)
                        val y = (height - barH) / 2f

                        drawRect(
                            color = barColor,
                            topLeft = Offset(x, y),
                            size = androidx.compose.ui.geometry.Size(barWidth * 0.6f, barH)
                        )
                    }

                    // Trim Start Indicator Line
                    drawLine(
                        color = AnimePink,
                        start = Offset(width * startRatio, 0f),
                        end = Offset(width * startRatio, height),
                        strokeWidth = 3.dp.toPx()
                    )

                    // Trim End Indicator Line
                    drawLine(
                        color = AnimeGreen,
                        start = Offset(width * endRatio, 0f),
                        end = Offset(width * endRatio, height),
                        strokeWidth = 3.dp.toPx()
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // RangeSlider Seek Bar for Start/End Trimming
            RangeSlider(
                value = startSec..endSec,
                onValueChange = { range ->
                    val minDelta = 0.3f
                    if (range.endInclusive - range.start >= minDelta) {
                        startSec = range.start
                        endSec = range.endInclusive
                        currentPlaybackSec = startSec
                    }
                },
                valueRange = 0f..totalDurationSeconds,
                colors = SliderDefaults.colors(
                    thumbColor = AnimeCyan,
                    activeTrackColor = AnimeCyan,
                    inactiveTrackColor = AnimeSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("voice_trim_range_slider")
            )

            // Start & End Point Labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "Start Point", color = AnimePink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${"%.2f".format(startSec)}s", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "Active Segment", color = TextMuted, fontSize = 10.sp)
                    Text(text = "${"%.2f".format(trimmedDuration)}s", color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "End Point", color = AnimeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "${"%.2f".format(endSec)}s", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Play Trimmed, Reset & Apply
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Play / Pause Trimmed Selection
                Button(
                    onClick = {
                        if (isPlayingTrimmed) {
                            playbackJob?.cancel()
                            voiceSyncEngine?.stop()
                            isPlayingTrimmed = false
                        } else {
                            isPlayingTrimmed = true
                            currentPlaybackSec = startSec
                            voiceSyncEngine?.speakDialogue(
                                characterName = characterName,
                                dialogueText = dialogueText,
                                emotion = "Expressive",
                                voiceType = "Hero",
                                pitch = 1.0f,
                                speed = 1.0f,
                                voiceMood = selectedVoiceMood
                            )
                            playbackJob = coroutineScope.launch {
                                val steps = 40
                                val stepDelay = ((trimmedDuration * 1000f) / steps).toLong().coerceAtLeast(20L)
                                for (i in 0..steps) {
                                    currentPlaybackSec = startSec + (trimmedDuration * (i.toFloat() / steps))
                                    delay(stepDelay)
                                }
                                isPlayingTrimmed = false
                                currentPlaybackSec = startSec
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = if (isPlayingTrimmed) AnimeGold else AnimePurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).testTag("play_trimmed_voice_btn")
                ) {
                    Icon(
                        imageVector = if (isPlayingTrimmed) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play Trimmed",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (isPlayingTrimmed) "Playing (${"%.1f".format(currentPlaybackSec)}s)" else "Preview Trim",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Reset Button
                OutlinedButton(
                    onClick = {
                        startSec = 0f
                        endSec = totalDurationSeconds
                        currentPlaybackSec = 0f
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f)),
                    modifier = Modifier.testTag("reset_voice_trim_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = TextMuted, modifier = Modifier.size(16.dp))
                }

                // Apply Trim Button
                Button(
                    onClick = {
                        onTrimApplied(startSec, endSec)
                        Toast.makeText(context, "✂️ Voice-over trimmed: ${"%.1f".format(startSec)}s to ${"%.1f".format(endSec)}s", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.1f).testTag("apply_voice_trim_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Apply Trim",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Real-Time Voice Mood Filter Slider Component (Deep Bass, Natural, High Pitch, Robot)
            VoiceMoodFilterSlider(
                currentMood = selectedVoiceMood,
                onMoodSelected = { mood ->
                    selectedVoiceMood = mood
                    onVoiceMoodChanged?.invoke(mood)
                },
                onAuditionMood = { mood ->
                    selectedVoiceMood = mood
                    onVoiceMoodChanged?.invoke(mood)
                    voiceSyncEngine?.speakDialogue(
                        characterName = characterName,
                        dialogueText = dialogueText,
                        emotion = "Expressive",
                        voiceType = "Hero",
                        pitch = 1.0f,
                        speed = 1.0f,
                        voiceMood = mood
                    )
                },
                isCurrentlyAuditioning = isPlayingTrimmed,
                modifier = Modifier.fillMaxWidth().testTag("trim_editor_voice_mood_slider")
            )
        }
    }
}

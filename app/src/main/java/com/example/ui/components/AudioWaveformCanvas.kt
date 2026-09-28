package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.sin

/**
 * Custom Native Canvas Audio Waveform Component
 * Visualizes character dialogue voice lines, cadence pitch, and background audio
 * during the editing phase with real-time interactive scrubbing and playhead.
 */
@Composable
fun AudioWaveformCanvas(
    modifier: Modifier = Modifier,
    waveformSamples: List<Float> = emptyList(),
    playbackProgress: Float = 0f, // 0.0 to 1.0
    isPlaying: Boolean = false,
    barCount: Int = 48,
    height: Dp = 64.dp,
    activeBarColor: Color = AnimeCyan,
    inactiveBarColor: Color = AnimePurple.copy(alpha = 0.35f),
    playheadColor: Color = AnimeGold,
    showScrubber: Boolean = true,
    onSeek: ((Float) -> Unit)? = null
) {
    // Generate organic samples if none provided
    val samples = remember(waveformSamples, barCount) {
        if (waveformSamples.isNotEmpty()) {
            waveformSamples.take(barCount).let { list ->
                if (list.size < barCount) {
                    list + List(barCount - list.size) { 0.2f }
                } else list
            }
        } else {
            List(barCount) { idx ->
                val base = 0.25f + 0.65f * sin((idx.toDouble() / barCount) * Math.PI).toFloat()
                val variation = ((idx * 17) % 10) / 30f
                (base + variation).coerceIn(0.12f, 0.95f)
            }
        }
    }

    // Dynamic wave animation when playing
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_pulse")
    val animatedPulse by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .pointerInput(onSeek) {
                if (onSeek != null) {
                    detectTapGestures { offset ->
                        val newProgress = (offset.x / size.width).coerceIn(0f, 1f)
                        onSeek(newProgress)
                    }
                }
            }
            .pointerInput(onSeek) {
                if (onSeek != null) {
                    detectDragGestures { change, _ ->
                        change.consume()
                        val newProgress = (change.position.x / size.width).coerceIn(0f, 1f)
                        onSeek(newProgress)
                    }
                }
            }
            .testTag("audio_waveform_canvas")
    ) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        val totalBars = samples.size
        val barSpacing = 3.dp.toPx()
        val totalSpacing = barSpacing * (totalBars - 1)
        val barWidth = ((canvasWidth - totalSpacing) / totalBars).coerceAtLeast(2.dp.toPx())

        val centerY = canvasHeight / 2f
        val playheadX = canvasWidth * playbackProgress.coerceIn(0f, 1f)

        // Draw baseline center line
        drawLine(
            color = Color.White.copy(alpha = 0.08f),
            start = Offset(0f, centerY),
            end = Offset(canvasWidth, centerY),
            strokeWidth = 1.dp.toPx()
        )

        // Draw individual amplitude bars
        for (i in 0 until totalBars) {
            val barX = i * (barWidth + barSpacing)
            val isPlayed = barX <= playheadX

            var sampleAmp = samples[i]
            if (isPlaying && isPlayed) {
                sampleAmp = (sampleAmp * animatedPulse).coerceIn(0.1f, 1.0f)
            }

            val barHeight = (canvasHeight * 0.85f * sampleAmp).coerceAtLeast(4.dp.toPx())
            val topY = centerY - (barHeight / 2f)

            val barBrush = if (isPlayed) {
                Brush.verticalGradient(
                    colors = listOf(
                        AnimeCyanLight,
                        activeBarColor,
                        AnimePink
                    ),
                    startY = topY,
                    endY = topY + barHeight
                )
            } else {
                Brush.verticalGradient(
                    colors = listOf(
                        inactiveBarColor,
                        inactiveBarColor.copy(alpha = 0.15f)
                    ),
                    startY = topY,
                    endY = topY + barHeight
                )
            }

            drawRoundRect(
                brush = barBrush,
                topLeft = Offset(barX, topY),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(barWidth / 2f, barWidth / 2f)
            )
        }

        // Draw scrub head indicator line
        if (showScrubber) {
            // Glow behind playhead
            drawLine(
                color = playheadColor.copy(alpha = 0.35f),
                start = Offset(playheadX, 0f),
                end = Offset(playheadX, canvasHeight),
                strokeWidth = 5.dp.toPx()
            )
            // Crisp playhead line
            drawLine(
                color = playheadColor,
                start = Offset(playheadX, 0f),
                end = Offset(playheadX, canvasHeight),
                strokeWidth = 2.dp.toPx()
            )
            // Playhead thumb circle at top
            drawCircle(
                color = playheadColor,
                radius = 4.5.dp.toPx(),
                center = Offset(playheadX, 6.dp.toPx())
            )
            drawCircle(
                color = Color.White,
                radius = 2.dp.toPx(),
                center = Offset(playheadX, 6.dp.toPx())
            )
        }
    }
}

/**
 * Complete Voice Line Waveform Editor Section
 * Used during the dialogue & script editing phase
 */
@Composable
fun CharacterVoiceWaveformEditor(
    characterName: String,
    dialogueText: String,
    voicePersona: String,
    voicePitch: Float,
    voiceSpeed: Float,
    onPitchChange: (Float) -> Unit,
    onSpeedChange: (Float) -> Unit,
    onPlaySample: () -> Unit,
    isPlaying: Boolean,
    modifier: Modifier = Modifier
) {
    var scrubProgress by remember { mutableFloatStateOf(0f) }

    // Frequency & waveform samples based on pitch & speed
    val generatedSamples = remember(voicePitch, voiceSpeed) {
        List(48) { idx ->
            val wave1 = sin((idx.toDouble() * voicePitch * 0.4) * Math.PI).toFloat() * 0.4f
            val wave2 = sin((idx.toDouble() * 0.25) * Math.PI).toFloat() * 0.35f
            val baseline = 0.45f
            (baseline + wave1 + wave2).coerceIn(0.15f, 0.95f)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AnimeSurfaceVariant)
            .border(1.dp, AnimeCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
            .padding(12.dp)
    ) {
        Column {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(AnimeCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "वॉइस वेवफॉर्म एडिटर (Native Canvas Waveform)",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "$characterName • $voicePersona",
                            color = AnimeCyanLight,
                            fontSize = 10.sp
                        )
                    }
                }

                // Play / Pause sample
                IconButton(
                    onClick = onPlaySample,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) AnimePink else AnimeCyan)
                        .testTag("waveform_play_btn")
                ) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = "Play Dialogue Sample",
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dialogue Line Preview
            Text(
                text = "💬 \"$dialogueText\"",
                color = Color.White,
                fontSize = 11.sp,
                fontFamily = FontFamily.Serif,
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Native Canvas Audio Waveform Component
            AudioWaveformCanvas(
                waveformSamples = generatedSamples,
                playbackProgress = if (isPlaying) scrubProgress else 0.35f,
                isPlaying = isPlaying,
                barCount = 44,
                height = 52.dp,
                activeBarColor = AnimeCyan,
                playheadColor = AnimeGold,
                onSeek = { newPos -> scrubProgress = newPos }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Timestamps and Cadence stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "पिच: ${String.format("%.2f", voicePitch)}x | गति: ${String.format("%.2f", voiceSpeed)}x",
                    color = AnimeGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = if (isPlaying) "00:03 / 00:05 s (Playing)" else "00:05 s (Waveform Ready)",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

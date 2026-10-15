package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GenerationStage
import com.example.data.model.GenerationTaskProgress
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
import kotlinx.coroutines.flow.StateFlow

/**
 * High-precision Compose UI component tracking anime video generation tasks using Kotlin Flow.
 * Visualizes generation stages:
 * 1. Script Writing
 * 2. Visual Storyboard
 * 3. Voice Dubbing
 * 4. Lyria Music Composition
 * 5. MP4 Video Encoding
 */
@Composable
fun GenerationFlowProgressBar(
    progressFlow: StateFlow<GenerationTaskProgress>,
    modifier: Modifier = Modifier
) {
    val progress by progressFlow.collectAsState()

    if (!progress.isGenerating && progress.stage == GenerationStage.IDLE) {
        return
    }

    val animatedPercent by animateFloatAsState(
        targetValue = progress.progressPercent.coerceIn(0f, 100f),
        animationSpec = tween(durationMillis = 400, easing = FastOutSlowInEasing),
        label = "generation_progress_anim"
    )

    val infiniteTransition = rememberInfiniteTransition(label = "shimmer_pulse")
    val shimmerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.9f,
        animationSpec = infiniteRepeatable(
            animation = tween(800),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shimmer_alpha"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("generation_flow_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimePink)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Active Task & Percent
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
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = progress.stage.label,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = progress.stageDescription,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            maxLines = 1
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeCyan.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "${animatedPercent.toInt()}%",
                        color = AnimeCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Main Animated Gradient Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(AnimeSurfaceVariant)
                    .border(0.5.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
            ) {
                val fillFraction = (animatedPercent / 100f).coerceIn(0f, 1f)
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(fraction = fillFraction.coerceAtLeast(0.02f))
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(
                                    AnimeCyan.copy(alpha = shimmerAlpha),
                                    AnimePurple,
                                    AnimePink.copy(alpha = shimmerAlpha),
                                    AnimeGold
                                )
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Stage Step Pills (1 to 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StageStepIndicator(step = 1, icon = Icons.Default.Brush, label = "Script", currentStep = progress.stage.stepIndex)
                StageStepIndicator(step = 2, icon = Icons.Default.Movie, label = "Visuals", currentStep = progress.stage.stepIndex)
                StageStepIndicator(step = 3, icon = Icons.Default.GraphicEq, label = "Voice", currentStep = progress.stage.stepIndex)
                StageStepIndicator(step = 4, icon = Icons.Default.MusicNote, label = "Music", currentStep = progress.stage.stepIndex)
                StageStepIndicator(step = 5, icon = Icons.Default.CheckCircle, label = "Render", currentStep = progress.stage.stepIndex)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Footer ETA & Elapsed Time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Elapsed: ${progress.elapsedSeconds}s",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Text(
                    text = if (progress.etaSeconds > 0) "Est. remaining: ~${progress.etaSeconds}s" else "Finalizing frames...",
                    color = AnimeGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun StageStepIndicator(
    step: Int,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    currentStep: Int
) {
    val isCompleted = currentStep > step
    val isCurrent = currentStep == step
    val stepColor = when {
        isCompleted -> AnimeGreen
        isCurrent -> AnimeCyan
        else -> TextMuted.copy(alpha = 0.5f)
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(16.dp)
                .clip(CircleShape)
                .background(stepColor.copy(alpha = if (isCurrent) 0.3f else 0.15f))
                .border(1.dp, stepColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = stepColor,
                modifier = Modifier.size(9.dp)
            )
        }
        Spacer(modifier = Modifier.width(3.dp))
        Text(
            text = label,
            color = if (isCurrent) AnimeCyan else if (isCompleted) AnimeGreen else TextMuted,
            fontSize = 9.sp,
            fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal
        )
    }
}

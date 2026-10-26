package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.AnimeScript
import com.example.export.VideoExportProgress
import com.example.export.VideoExportStatus
import com.example.localization.AppLocaleStrings
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

/**
 * Real-Time Video Export Progress Bar with Step Indicator & Estimated Time Remaining:
 * Tracks steps:
 * 1. Encoding (Video Keyframes)
 * 2. Audio Mix (BGM & Speech Synch)
 * 3. Finalizing (MPEG-4 MP4 Container & Local DB Record)
 */
@Composable
fun VideoExportProgressBarComponent(
    progress: VideoExportProgress,
    lang: String,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isRunning = progress.status == VideoExportStatus.PREPARING ||
        progress.status == VideoExportStatus.RENDERING_SCENES ||
        progress.status == VideoExportStatus.ENCODING_VIDEO ||
        progress.status == VideoExportStatus.PROCESSING_AUDIO ||
        progress.status == VideoExportStatus.SAVING_FILE

    if (!isRunning && progress.status != VideoExportStatus.COMPLETED && progress.status != VideoExportStatus.FAILED) {
        return
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("video_export_realtime_progress_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.5.dp,
            if (progress.status == VideoExportStatus.COMPLETED) AnimeGreen
            else if (progress.status == VideoExportStatus.FAILED) AnimePink
            else AnimeCyan
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header: Title & Time Remaining
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                if (progress.status == VideoExportStatus.COMPLETED) AnimeGreen.copy(alpha = 0.2f)
                                else AnimeCyan.copy(alpha = 0.2f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (progress.status == VideoExportStatus.COMPLETED) Icons.Default.CheckCircle else Icons.Default.Videocam,
                            contentDescription = null,
                            tint = if (progress.status == VideoExportStatus.COMPLETED) AnimeGreen else AnimeCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (progress.status == VideoExportStatus.COMPLETED)
                                AppLocaleStrings.tr(lang, "Export Completed!", "एक्सपोर्ट पूर्ण हुआ!")
                            else if (progress.status == VideoExportStatus.FAILED)
                                AppLocaleStrings.tr(lang, "Export Failed", "एक्सपोर्ट असफल")
                            else
                                AppLocaleStrings.tr(lang, "Rendering Video...", "वीडियो रेंडर हो रहा है..."),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = progress.title.ifBlank { AppLocaleStrings.tr(lang, "Anime Episode", "एनिमे एपिसोड") },
                            fontSize = 11.sp,
                            color = TextMuted,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                if (isRunning) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeGold.copy(alpha = 0.15f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = if (progress.estimatedSecondsRemaining > 0)
                                AppLocaleStrings.tr(
                                    lang,
                                    "~${progress.estimatedSecondsRemaining}s remaining",
                                    "~${progress.estimatedSecondsRemaining} सेकंड शेष"
                                )
                            else
                                AppLocaleStrings.tr(lang, "Almost ready", "लगभग तैयार"),
                            color = AnimeGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Step Indicator Row: Encoding -> Audio Mix -> Finalizing
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                val steps = listOf(
                    Triple("Encoding", "1. " + AppLocaleStrings.tr(lang, "Encoding", "एन्कोडिंग"), progress.progressPercent >= 0.15f),
                    Triple("Audio Mix", "2. " + AppLocaleStrings.tr(lang, "Audio Mix", "ऑडियो मिक्स"), progress.progressPercent >= 0.80f),
                    Triple("Finalizing", "3. " + AppLocaleStrings.tr(lang, "Finalizing", "फ़ाइनलाइज़िंग"), progress.progressPercent >= 0.92f)
                )

                steps.forEach { (stepKey, label, isPassed) ->
                    val isCurrent = progress.currentStep.equals(stepKey, ignoreCase = true) && isRunning
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                when {
                                    isPassed -> AnimeGreen.copy(alpha = 0.2f)
                                    isCurrent -> AnimeCyan.copy(alpha = 0.2f)
                                    else -> MaterialTheme.colorScheme.surfaceVariant
                                }
                            )
                            .border(
                                1.dp,
                                when {
                                    isPassed -> AnimeGreen.copy(alpha = 0.6f)
                                    isCurrent -> AnimeCyan
                                    else -> Color.Transparent
                                },
                                RoundedCornerShape(8.dp)
                            )
                            .padding(vertical = 6.dp, horizontal = 4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            color = when {
                                isPassed -> AnimeGreen
                                isCurrent -> AnimeCyanLight
                                else -> TextMuted
                            },
                            fontSize = 9.sp,
                            fontWeight = if (isCurrent || isPassed) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Linear Progress Bar & Percentage
            LinearProgressIndicator(
                progress = { progress.progressPercent.coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .testTag("export_realtime_progress_indicator"),
                color = if (progress.status == VideoExportStatus.COMPLETED) AnimeGreen else AnimeCyan,
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Status message and cancel button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = progress.statusMessage.ifBlank {
                        "${(progress.progressPercent * 100).toInt()}% • ${progress.resolution}"
                    },
                    color = TextSecondary,
                    fontSize = 11.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (isRunning) {
                    OutlinedButton(
                        onClick = onCancel,
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("cancel_export_button"),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.7f))
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Cancel", "रद्द करें"),
                            color = AnimePink,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Aspect Ratio Sample Frame Preview Window:
 * Allows users to inspect and verify their visual composition across:
 * - 9:16 (Vertical Reels / TikTok / YouTube Shorts)
 * - 16:9 (Cinematic Widescreen Landscape)
 * - 1:1 (Square Post / Community Feed)
 */
@Composable
fun VideoExportAspectRatioPreview(
    aspectRatio: String,
    onAspectRatioSelected: (String) -> Unit,
    script: AnimeScript?,
    lang: String,
    modifier: Modifier = Modifier
) {
    val ratioFloat = when (aspectRatio) {
        "9:16" -> 9f / 16f
        "1:1" -> 1f
        else -> 16f / 9f
    }

    val previewDrawable = when {
        script?.artStyle?.contains("Cyber", ignoreCase = true) == true -> "scene_cyber_city"
        script?.artStyle?.contains("Manhwa", ignoreCase = true) == true -> "scene_cyber_city"
        else -> "scene_cherry_temple"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("export_aspect_ratio_preview_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.AspectRatio, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Export Composition Preview", "एक्सपोर्ट फ्रेम संरचना प्रीव्यू"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Text(
                    text = aspectRatio,
                    color = AnimeGold,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Aspect Ratio Selector Chips (9:16, 16:9, 1:1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val ratioOptions = listOf(
                    Triple("9:16", "9:16", AppLocaleStrings.tr(lang, "Reel / Shorts", "रील / शॉर्ट्स")),
                    Triple("16:9", "16:9", AppLocaleStrings.tr(lang, "Cinema / YT", "सिनेमा / यूट्यूब")),
                    Triple("1:1", "1:1", AppLocaleStrings.tr(lang, "Square", "चौकोर फीड"))
                )

                ratioOptions.forEach { (key, label, desc) ->
                    val isSelected = aspectRatio == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AnimeCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable { onAspectRatioSelected(key) }
                            .padding(vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(label, color = if (isSelected) AnimeCyan else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(desc, color = TextMuted, fontSize = 8.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Preview Viewport Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                // Actual frame window with selected aspect ratio
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp)
                        .aspectRatio(ratioFloat)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, AnimeCyanLight, RoundedCornerShape(8.dp))
                        .background(Color.DarkGray),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    val context = androidx.compose.ui.platform.LocalContext.current
                    val resId = ResourceHelpers.getDrawableId(context, previewDrawable)
                    if (resId != 0) {
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = "Sample Frame Preview",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    // Bottom Subtitle & Character Overlay Preview
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.verticalGradient(
                                    listOf(Color.Transparent, Color.Black.copy(alpha = 0.85f))
                                )
                            )
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        val sampleDialogue = script?.scenes?.firstOrNull()?.dialogues?.firstOrNull()?.text
                            ?: AppLocaleStrings.tr(lang, "Our spirits will break through every limit!", "हम अपनी शक्ति से सीमाओं को पार कर देंगे!")
                        Text(
                            text = sampleDialogue,
                            color = Color.White,
                            fontSize = if (aspectRatio == "9:16") 9.sp else 10.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }

                    // Watermark / Safe Zone Indicator
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${aspectRatio} SAFE",
                            color = AnimeGold,
                            fontSize = 7.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Circular progress indicator overlay on video thumbnail in project list to show real-time export progress.
 */
@Composable
fun ExportCircularProgressThumbnailOverlay(
    isExporting: Boolean,
    progressPercent: Float,
    modifier: Modifier = Modifier
) {
    if (!isExporting) return

    Box(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.70f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            CircularProgressIndicator(
                progress = { progressPercent.coerceIn(0.05f, 1f) },
                modifier = Modifier.size(28.dp),
                color = AnimeCyan,
                strokeWidth = 3.dp,
                trackColor = Color.White.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${(progressPercent * 100).toInt()}%",
                color = Color.White,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

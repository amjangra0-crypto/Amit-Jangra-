package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import java.util.Locale
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntOffset
import kotlin.math.roundToInt
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.MediaController
import android.widget.VideoView
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.VideoFile
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.model.SubtitleMode
import com.example.data.model.SupportedLanguage
import com.example.export.VideoExportStatus
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.components.DownloadProjectDialog
import java.io.File
import com.example.ui.components.ResourceHelpers
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

@Composable
fun AnimePlayerScreen(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()
    val exportProgress by viewModel.videoExportProgress.collectAsState()
    val exportedVideos by viewModel.exportedVideosState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showScriptDialog by remember { mutableStateOf(false) }
    var showDubbingDialog by remember { mutableStateOf(false) }
    var isNativeMp4Mode by remember { mutableStateOf(false) }
    var activePlayingMp4Path by remember { mutableStateOf<String?>(null) }
    var selectedExportDuration by remember { mutableStateOf(state.automationDurationSeconds) }

    val script = state.currentScript
    val scenes = script?.scenes ?: emptyList()
    val currentScene = scenes.getOrNull(state.activeSceneIndex) ?: scenes.firstOrNull()

    // Camera animation simulation (slow zoom & pan when video plays)
    val infiniteTransition = rememberInfiniteTransition(label = "anime_cam")
    val cameraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state.isPlayingVideo) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 6000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cam_scale"
    )

    // Speaking pulse for active character avatar
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (speakingState.isSpeaking) 1.15f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 350, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val currentMotionEffect = state.activeMotionEffect.ifBlank {
        currentScene?.motionEffect ?: script?.defaultMotionEffect ?: "SPEEDLINES_ACTION"
    }
    val currentExpression = state.activeCharacterExpression.ifBlank {
        currentScene?.dialogues?.getOrNull(state.currentDialogueIndex)?.expression ?: "Confident Smirk"
    }
    val productionFormat = script?.productionFormat ?: state.selectedProductionFormat.title

    // Shake offset for impact & battle action
    val isImpactScene = currentMotionEffect.contains("SHAKE", ignoreCase = true) ||
            currentExpression.contains("Roar", ignoreCase = true) ||
            currentExpression.contains("Shock", ignoreCase = true)

    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = if (isImpactScene && (state.isPlayingVideo || speakingState.isSpeaking)) -4f else 0f,
        targetValue = if (isImpactScene && (state.isPlayingVideo || speakingState.isSpeaking)) 4f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 70, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake_offset"
    )

    // Speedlines pulse
    val speedlinesAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speedlines_alpha"
    )

    // Aura pulse
    val auraGlowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1100, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_glow_alpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp)
    ) {
        // Title Bar & Quick Actions
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = script?.title ?: AppLocaleStrings.tr(state.selectedLanguage, "Anime Video Player", "एनिमे वीडियो प्लेयर"),
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnimePink.copy(alpha = 0.2f))
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = productionFormat.take(18),
                            color = AnimePink,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "• ${script?.artStyle ?: "Anime"} • ${state.selectedLanguage}",
                        color = AnimeCyanLight,
                        fontSize = 11.sp
                    )
                }
            }
            Row {
                IconButton(
                    onClick = { viewModel.toggleDownloadDialog(true) },
                    modifier = Modifier.testTag("player_download_top_btn")
                ) {
                    Icon(Icons.Default.FileDownload, contentDescription = "Download Video/Script", tint = AnimeGold)
                }
                IconButton(onClick = { viewModel.toggleExportShareDialog(true) }) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = AnimePink)
                }
                IconButton(onClick = { showDubbingDialog = true }) {
                    Icon(Icons.Default.Translate, contentDescription = "Dubbing", tint = AnimeCyan)
                }
                IconButton(onClick = { showScriptDialog = true }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "View Script", tint = AnimePurple)
                }
            }
        }

        // Format & Motion Status Chips Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AnimeSurfaceVariant)
                    .border(1.dp, AnimePurple.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "⚡ $currentMotionEffect",
                    color = AnimeCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(AnimeSurfaceVariant)
                    .border(1.dp, AnimeGold.copy(alpha = 0.4f), RoundedCornerShape(6.dp))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "🎙️ ${state.selectedVoiceAccent.take(22)}",
                    color = AnimeGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Player Mode Switcher: Live Animation Stage vs Local MP4 Video File
        val activeMp4 = activePlayingMp4Path ?: exportedVideos.firstOrNull { it.scriptId == script?.id }?.filePath ?: exportedVideos.firstOrNull()?.filePath
        if (activeMp4 != null && File(activeMp4).exists()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FilterChip(
                    selected = !isNativeMp4Mode,
                    onClick = { isNativeMp4Mode = false },
                    label = { Text(AppLocaleStrings.tr(state.selectedLanguage, "🎨 Live Animation (Compose)", "🎨 लाइव एनिमेशन (Compose)"), fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimePurple.copy(alpha = 0.35f),
                        selectedLabelColor = AnimeCyan
                    )
                )
                FilterChip(
                    selected = isNativeMp4Mode,
                    onClick = {
                        isNativeMp4Mode = true
                        activePlayingMp4Path = activeMp4
                    },
                    label = { Text(AppLocaleStrings.tr(state.selectedLanguage, "🎥 Local MP4 Player (.mp4 File)", "🎥 लोकल MP4 प्लेयर (.mp4 File)"), fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimeGold.copy(alpha = 0.35f),
                        selectedLabelColor = AnimeGold
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        if (isNativeMp4Mode && activeMp4 != null && File(activeMp4).exists()) {
            // Native Android VideoView Playback of exported .mp4 video file
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(1.dp, AnimeGold.copy(alpha = 0.6f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                AndroidView(
                    factory = { ctx ->
                        VideoView(ctx).apply {
                            val mediaController = MediaController(ctx)
                            mediaController.setAnchorView(this)
                            setMediaController(mediaController)
                            setVideoPath(activeMp4)
                            setOnPreparedListener { mp ->
                                mp.isLooping = true
                                start()
                            }
                        }
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            // 16:9 Video Canvas Frame
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .aspectRatio(16f / 9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black)
                    .border(1.dp, AnimePurple.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
            val sceneDrawableName = currentScene?.sceneDrawableName ?: "scene_cherry_temple"
            Image(
                painter = painterResource(id = ResourceHelpers.getDrawableId(context, sceneDrawableName)),
                contentDescription = "Anime Scene Frame",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxSize()
                    .scale(cameraScale)
                    .offset { IntOffset(shakeOffset.roundToInt(), (shakeOffset * 0.6f).roundToInt()) }
            )

            // Dynamic Motion Effect Layer 1: Solo Leveling / Manhwa Dark Shadow & Aura Glow
            if (currentMotionEffect.contains("AURA", ignoreCase = true) ||
                currentMotionEffect.contains("GLOW", ignoreCase = true) ||
                currentExpression.contains("Aura", ignoreCase = true)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    AnimePurple.copy(alpha = 0.35f * auraGlowAlpha),
                                    AnimeCyan.copy(alpha = 0.25f * auraGlowAlpha)
                                )
                            )
                        )
                )
            }

            // Dynamic Motion Effect Layer 2: Shonen Action Speedlines Overlay Canvas
            if (currentMotionEffect.contains("SPEEDLINES", ignoreCase = true) ||
                currentExpression.contains("Roar", ignoreCase = true)
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .offset { IntOffset((shakeOffset * 1.5f).roundToInt(), 0) }
                ) {
                    val w = size.width
                    val h = size.height
                    val cx = w * 0.5f
                    val cy = h * 0.45f
                    val lineCount = 20
                    val lineColor = Color.White.copy(alpha = speedlinesAlpha * 0.45f)
                    val cyanLine = AnimeCyan.copy(alpha = speedlinesAlpha * 0.35f)

                    for (i in 0 until lineCount) {
                        val angle = (i * (360f / lineCount)) * (Math.PI / 180f)
                        val outerX = cx + (w * 0.7f * kotlin.math.cos(angle)).toFloat()
                        val outerY = cy + (h * 0.7f * kotlin.math.sin(angle)).toFloat()
                        val innerX = cx + (w * 0.25f * kotlin.math.cos(angle)).toFloat()
                        val innerY = cy + (h * 0.25f * kotlin.math.sin(angle)).toFloat()
                        drawLine(
                            color = if (i % 2 == 0) lineColor else cyanLine,
                            start = Offset(outerX, outerY),
                            end = Offset(innerX, innerY),
                            strokeWidth = if (i % 3 == 0) 3.5f else 1.8f
                        )
                    }
                }
            }

            // Dynamic Motion Effect Layer 3: Manga / Manhwa Panel Border & Screentone Framing
            if (currentMotionEffect.contains("MANGA", ignoreCase = true) ||
                currentMotionEffect.contains("PANEL", ignoreCase = true) ||
                script?.artStyle?.contains("Manhwa", ignoreCase = true) == true
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(4.dp, Color.White.copy(alpha = 0.7f))
                        .padding(4.dp)
                        .border(1.dp, Color.Black.copy(alpha = 0.5f))
                )
            }

            // Cinematic Vignette Overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                Color.Black.copy(alpha = 0.4f),
                                Color.Transparent,
                                Color.Black.copy(alpha = 0.85f)
                            )
                        )
                    )
            )

            // Top Header Inside Video (Scene info & Music indicator)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = AppLocaleStrings.tr(state.selectedLanguage, "Scene ${(state.activeSceneIndex + 1)} / ${scenes.size.coerceAtLeast(1)}", "सीन ${(state.activeSceneIndex + 1)} / ${scenes.size.coerceAtLeast(1)}"),
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Dynamic Music Score Indicator
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { viewModel.toggleMusic() }
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = if (state.isMusicMuted) Icons.Default.MusicOff else Icons.Default.MusicNote,
                        contentDescription = "Music",
                        tint = if (state.isMusicMuted) Color.Gray else AnimeGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (state.isMusicMuted) "Music Muted" else (currentScene?.bgMood ?: "Piano"),
                        color = if (state.isMusicMuted) Color.Gray else AnimeGold,
                        fontSize = 11.sp
                    )
                }
            }

            // Bottom Karaoke Subtitle & Speaking Character Avatar Overlay
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Speaking Character Avatar with Voice Pulse & Lip-Sync Animation
                    val activeSpeaker = state.activeSpeakerName.ifBlank { "Narrator" }
                    val matchedProfile = script?.characters?.find { it.name.equals(activeSpeaker, ignoreCase = true) }
                        ?: state.customCharacters.find { it.name.equals(activeSpeaker, ignoreCase = true) }

                    val avatarDrawable = matchedProfile?.avatarDrawableName ?: when {
                        activeSpeaker.contains("Ren", ignoreCase = true) || activeSpeaker.contains("रेन", ignoreCase = true) -> "char_shonen_hero"
                        activeSpeaker.contains("Kyoto", ignoreCase = true) || activeSpeaker.contains("क्योटो", ignoreCase = true) -> "char_lady_mentor"
                        activeSpeaker.contains("Popo", ignoreCase = true) || activeSpeaker.contains("पोपो", ignoreCase = true) -> "char_chibi_mascot"
                        else -> "char_anime_heroine"
                    }

                    val isGlowingExpression = currentExpression.contains("Glow", ignoreCase = true) ||
                            currentExpression.contains("Manhwa", ignoreCase = true) ||
                            currentExpression.contains("Eyes", ignoreCase = true)

                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                if (isGlowingExpression) Brush.radialGradient(
                                    listOf(AnimeCyan, AnimePurple, Color.Transparent)
                                ) else Brush.radialGradient(listOf(AnimePurple, Color.Transparent))
                            )
                            .border(
                                width = if (isGlowingExpression) 2.5.dp else 2.dp,
                                brush = if (isGlowingExpression) Brush.sweepGradient(
                                    listOf(AnimeCyan, AnimePink, AnimeGold, AnimeCyan)
                                ) else Brush.linearGradient(
                                    listOf(if (speakingState.isSpeaking) AnimePink else AnimeCyan.copy(alpha = 0.5f), AnimePurple)
                                ),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = ResourceHelpers.getDrawableId(context, avatarDrawable)),
                            contentDescription = activeSpeaker,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )

                        // Real-time lip-flap mouth open animation indicator
                        if (speakingState.isSpeaking) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(bottom = 5.dp)
                                    .size(width = 14.dp, height = (8.dp * speakingState.mouthOpenAmount).coerceAtLeast(3.dp))
                                    .clip(RoundedCornerShape(3.dp))
                                    .background(Color.Red.copy(alpha = 0.9f))
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    // Character Name & Emotion Tag & Voice Persona & Dynamic Expression
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = activeSpeaker,
                                color = AnimeCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (state.activeDialogueEmotion.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimePink.copy(alpha = 0.4f))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = state.activeDialogueEmotion,
                                        color = Color.White,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                            // Expression Tag (e.g. Manhwa Glowing Eyes, Fierce Battle Roar)
                            if (currentExpression.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeCyan.copy(alpha = 0.25f))
                                        .border(0.5.dp, AnimeCyan, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = currentExpression.take(16),
                                        color = AnimeCyanLight,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            matchedProfile?.let { prof ->
                                if (prof.voicePersona.isNotBlank()) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AnimePurple.copy(alpha = 0.3f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = prof.voicePersona.take(12),
                                            color = AnimeGold,
                                            fontSize = 8.sp
                                        )
                                    }
                                }
                            }
                            if (speakingState.isSpeaking) {
                                Icon(
                                    Icons.Default.GraphicEq,
                                    contentDescription = "Voice Active",
                                    tint = AnimeGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        // Dialogue Subtitles (Supports Bilingual Dual Mode!)
                        val currentDialogueText = state.activeDialogueText.ifBlank { currentScene?.dialogues?.firstOrNull()?.text ?: "..." }
                        if (state.subtitleMode == SubtitleMode.BILINGUAL_DUAL && !state.selectedLanguage.equals("Japanese", ignoreCase = true)) {
                            // Display Romanized Japanese subtitle line + Localized line
                            val originalJp = currentScene?.dialogues?.getOrNull(state.currentDialogueIndex)?.text ?: currentDialogueText
                            Text(
                                text = "🎌 $originalJp",
                                color = AnimeGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Text(
                            text = currentDialogueText,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Karaoke progress bar
                LinearProgressIndicator(
                    progress = { speakingState.progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(3.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = AnimeCyan,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
            }
        }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Playback Transport Controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.previousScene() },
                        modifier = Modifier.testTag("prev_scene_button")
                    ) {
                        Icon(Icons.Default.FastRewind, contentDescription = "Previous Scene", tint = TextPrimary)
                    }

                    // Main Play/Pause Button
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(AnimePurple, AnimePink))
                            )
                            .clickable {
                                if (state.isPlayingVideo) viewModel.pauseVideo() else viewModel.playVideo()
                            }
                            .testTag("play_pause_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (state.isPlayingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = if (state.isPlayingVideo) "Pause" else "Play",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(
                        onClick = { viewModel.nextScene() },
                        modifier = Modifier.testTag("next_scene_button")
                    ) {
                        Icon(Icons.Default.FastForward, contentDescription = "Next Scene", tint = TextPrimary)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Scene Visual Description
                Text(
                    text = currentScene?.title ?: "Scene Title",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = currentScene?.visualPrompt ?: "",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    textAlign = TextAlign.Center,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Scene Timeline Strip
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = AppLocaleStrings.tr(state.selectedLanguage, "🎞️ Storyboard Scene Timeline:", "🎞️ सीन टाइमलाइन (Storyboard Scenes):"),
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                itemsIndexed(scenes) { index, scene ->
                    val isCurrent = state.activeSceneIndex == index
                    Card(
                        modifier = Modifier
                            .width(140.dp)
                            .clickable { viewModel.selectScene(index) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCurrent) AnimePurple.copy(alpha = 0.25f) else AnimeSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isCurrent) AnimeCyan else AnimeSurfaceVariant
                        )
                    ) {
                        Column {
                            Image(
                                painter = painterResource(id = ResourceHelpers.getDrawableId(context, scene.sceneDrawableName)),
                                contentDescription = scene.title,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(70.dp)
                            )
                            Column(modifier = Modifier.padding(8.dp)) {
                                Text(
                                    text = AppLocaleStrings.tr(state.selectedLanguage, "Scene ${scene.sceneNumber}", "सीन ${scene.sceneNumber}"),
                                    color = if (isCurrent) AnimeCyan else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = scene.title,
                                    color = TextSecondary,
                                    fontSize = 10.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Multi-Language Translation & Dubbing Engine Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.35f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(state.selectedLanguage, "🌐 AI Translation & Dubbing Engine", "🌐 एआई ट्रांसलेशन व डबिंग इंजन (Translation Engine)"),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(state.selectedLanguage, "Active Dubbing Language: ${state.selectedLanguage}", "वर्तमान डबिंग भाषा: ${state.selectedLanguage}"),
                                color = AnimeGold,
                                fontSize = 11.sp
                            )
                        }
                    }
                    Button(
                        onClick = { showDubbingDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(AppLocaleStrings.tr(state.selectedLanguage, "Translate Dialogues", "डायलॉग्स अनुवाद"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // One-tap quick language translation strip
                Text("त्वरित भाषा परिवर्तन (One-Tap Translate Video & Voices):", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    val quickLangs = listOf(
                        "Japanese" to "🎌 日本語 (Japanese)",
                        "English" to "🇬🇧 English",
                        "Chinese" to "🇨🇳 中文 (Chinese)",
                        "Hindi" to "🇮🇳 हिन्दी (Hindi)",
                        "Spanish" to "🇪🇸 Español",
                        "Korean" to "🇰🇷 한국어 (Korean)"
                    )
                    items(quickLangs.size) { idx ->
                        val (langKey, label) = quickLangs[idx]
                        val isSelected = state.selectedLanguage.equals(langKey, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AnimePurple else AnimeSurfaceVariant)
                                .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable {
                                    viewModel.translateAndDub(langKey)
                                }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) AnimeCyanLight else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Subtitle Display Mode Toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(AppLocaleStrings.tr(state.selectedLanguage, "Subtitle Mode:", "सबटाइटल मोड:"), color = TextSecondary, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SubtitleMode.values().forEach { mode ->
                            val isSel = state.subtitleMode == mode
                            val modeLabel = when (mode) {
                                SubtitleMode.TRANSLATED_ONLY -> AppLocaleStrings.tr(state.selectedLanguage, "Translated", "अनुवादित")
                                SubtitleMode.BILINGUAL_DUAL -> AppLocaleStrings.tr(state.selectedLanguage, "🎌+🌐 Dual", "🎌+🌐 द्विभाषी")
                                SubtitleMode.ORIGINAL_ONLY -> AppLocaleStrings.tr(state.selectedLanguage, "Original", "मूल")
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) AnimePurple.copy(alpha = 0.4f) else AnimeSurfaceVariant)
                                    .border(1.dp, if (isSel) AnimeGold else Color.Transparent, RoundedCornerShape(6.dp))
                                    .clickable { viewModel.setSubtitleMode(mode) }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(modeLabel, color = if (isSel) AnimeGold else TextMuted, fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Script Synopsis & Characters Present
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "📖 Synopsis:", "📖 कहानी का सार (Synopsis):"),
                    color = AnimeGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = script?.synopsis ?: AppLocaleStrings.tr(state.selectedLanguage, "Story is generating...", "दास्तान तैयार हो रही है..."),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Download & Export Studio Action Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
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
                                .background(AnimeGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(state.selectedLanguage, "📥 Download & Export Studio", "📥 डाउनलोड व एक्सपोर्ट स्टूडियो (Download)"),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(state.selectedLanguage, "Save video project, script and subtitles to phone storage", "फोन स्टोरेज में वीडियो प्रोजेक्ट, स्क्रिप्ट व सबटाइटल सेव करें"),
                                color = AnimeCyanLight,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = { viewModel.downloadProjectFile("JSON") },
                        modifier = Modifier.weight(1f).height(42.dp).testTag("quick_download_json_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppLocaleStrings.tr(state.selectedLanguage, "Download JSON", "डाउनलोड JSON"), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.downloadProjectFile("TXT") },
                        modifier = Modifier.weight(1f).height(42.dp).testTag("quick_download_txt_btn"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AnimePink, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppLocaleStrings.tr(state.selectedLanguage, "Download TXT", "डाउनलोड TXT"), color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { viewModel.downloadProjectFile("SRT") },
                        modifier = Modifier.weight(1f).height(42.dp).testTag("quick_download_srt_btn"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Subtitles, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppLocaleStrings.tr(state.selectedLanguage, "Download SRT", "डाउनलोड SRT"), color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { viewModel.toggleDownloadDialog(true) },
                        modifier = Modifier.weight(1f).height(42.dp).testTag("open_download_center_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppLocaleStrings.tr(state.selectedLanguage, "Download Hub", "डाउनलोड केंद्र"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Generated Video Link Banner
                val currentScript = state.currentScript
                if (currentScript != null) {
                    val videoUrl = viewModel.getVideoShareUrl(currentScript)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.35f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = AppLocaleStrings.tr(state.selectedLanguage, "▶️ Generated Video Link:", "▶️ वीडियो लिंक (Generated Video Link):"),
                                    fontSize = 10.sp,
                                    color = AnimeCyanLight,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = videoUrl,
                                    fontSize = 9.sp,
                                    color = TextSecondary,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Row {
                                IconButton(
                                    onClick = { viewModel.copyProjectLinkToClipboard(context, currentScript) },
                                    modifier = Modifier.size(28.dp).testTag("player_copy_video_link_btn")
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy Link",
                                        tint = AnimeGold,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                                IconButton(
                                    onClick = { viewModel.shareProject(context, "ALL", "LINK_ONLY", currentScript) },
                                    modifier = Modifier.size(28.dp).testTag("player_quick_share_link_btn")
                                ) {
                                    Icon(
                                        Icons.Default.Share,
                                        contentDescription = "Share Link",
                                        tint = AnimeCyan,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                }

                // Social Media Direct Share Strip
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "🌐 Direct Social Media Share:", "🌐 डायरेक्ट सोशल मीडिया शेयर (Direct Social Media Share):"),
                    color = AnimeCyanLight,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // WhatsApp
                    Button(
                        onClick = { viewModel.shareProject(context, "WHATSAPP", "DETAILS_AND_LINK", currentScript) },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("player_share_whatsapp"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("💬 WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    // YouTube
                    Button(
                        onClick = { viewModel.shareProject(context, "YOUTUBE", "DETAILS_AND_LINK", currentScript) },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("player_share_youtube"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("▶️ YouTube", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    // Instagram
                    Button(
                        onClick = { viewModel.shareProject(context, "INSTAGRAM", "DETAILS_AND_LINK", currentScript) },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("player_share_instagram"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("📸 Insta", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Snapchat
                    Button(
                        onClick = { viewModel.shareProject(context, "SNAPCHAT", "DETAILS_AND_LINK", currentScript) },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("player_share_snapchat"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFC00)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("👻 Snapchat", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    // Google
                    Button(
                        onClick = { viewModel.shareProject(context, "GOOGLE", "DETAILS_AND_LINK", currentScript) },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("player_share_google"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("📁 Google", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }

                    // All in One
                    OutlinedButton(
                        onClick = { viewModel.shareProject(context, "ALL", "DETAILS_AND_LINK", currentScript) },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("player_share_all"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AnimePink),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text(AppLocaleStrings.tr(state.selectedLanguage, "✨ All Apps", "✨ सभी ऐप्स"), color = AnimePink, fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Background MP4 Video Export Worker Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .testTag("mp4_export_worker_card"),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(AnimeCyan, AnimePurple, AnimeGold))
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AnimeCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "🎥 Background MP4 Video Exporter", "🎥 पृष्ठभूमि में MP4 वीडियो एक्सपोर्टर"),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "Script, character animation, audio tracks → .mp4 file", "स्क्रिप्ट, कैरेक्टर एनिमेशन, ऑडियो ट्रैक्स → .mp4 फाइल"),
                            color = AnimeCyanLight,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Duration Selection for MP4 Export (Supports up to 1-2 Hours!)
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "Select Export Duration (15s to 2 Hours):", "एक्सपोर्ट अवधि चुनें (15s से 2 घंटे तक):"),
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                val exportDurations = listOf(
                    30 to AppLocaleStrings.tr(state.selectedLanguage, "30s (Promo)", "30s (प्रोमो)"),
                    60 to AppLocaleStrings.tr(state.selectedLanguage, "1m (Scene)", "1m (सीन)"),
                    300 to AppLocaleStrings.tr(state.selectedLanguage, "5m (Mini)", "5m (मिनी)"),
                    1800 to AppLocaleStrings.tr(state.selectedLanguage, "30m (Episode)", "30m (एपिसोड)"),
                    3600 to AppLocaleStrings.tr(state.selectedLanguage, "1 Hour (Film)", "1 Hour (फिल्म)"),
                    7200 to AppLocaleStrings.tr(state.selectedLanguage, "2 Hours (Epic)", "2 Hours (महागाथा)")
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(exportDurations) { (sec, label) ->
                        val isSel = selectedExportDuration == sec
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedExportDuration = sec },
                            label = { Text(label, fontSize = 10.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnimeCyan.copy(alpha = 0.35f),
                                selectedLabelColor = AnimeCyan,
                                containerColor = AnimeSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            modifier = Modifier.height(28.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Launch MP4 Background Worker Button
                val isExportRunning = exportProgress.status in listOf(
                    VideoExportStatus.PREPARING,
                    VideoExportStatus.RENDERING_SCENES,
                    VideoExportStatus.ENCODING_VIDEO,
                    VideoExportStatus.PROCESSING_AUDIO,
                    VideoExportStatus.SAVING_FILE
                )

                Button(
                    onClick = {
                        viewModel.startMp4VideoExport(
                            durationSec = selectedExportDuration,
                            resolutionLabel = "1280x720 (720p HD)"
                        )
                    },
                    enabled = !isExportRunning,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("start_mp4_export_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VideoFile,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isExportRunning) "बैकग्राउंड एक्सपोर्ट जारी है..." else "⚡ बैकग्राउंड में .mp4 वीडियो बनाएं व सेव करें",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                // Live Background Progress UI
                if (exportProgress.status != VideoExportStatus.IDLE) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AnimeSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = exportProgress.statusMessage,
                                color = when (exportProgress.status) {
                                    VideoExportStatus.COMPLETED -> AnimeGreen
                                    VideoExportStatus.FAILED -> AnimePink
                                    else -> AnimeCyanLight
                                },
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.weight(1f)
                            )
                            if (isExportRunning) {
                                TextButton(
                                    onClick = { viewModel.cancelMp4VideoExport() },
                                    contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(AppLocaleStrings.tr(state.selectedLanguage, "Cancel", "रद्द करें"), color = AnimePink, fontSize = 11.sp)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LinearProgressIndicator(
                            progress = { exportProgress.progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp)),
                            color = when (exportProgress.status) {
                                VideoExportStatus.COMPLETED -> AnimeGreen
                                VideoExportStatus.FAILED -> AnimePink
                                else -> AnimeCyan
                            },
                            trackColor = Color.White.copy(alpha = 0.15f)
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = AppLocaleStrings.tr(state.selectedLanguage, "Scene: ${exportProgress.currentScene}/${exportProgress.totalScenes} • 720p HD", "सीन: ${exportProgress.currentScene}/${exportProgress.totalScenes} • 720p HD"),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "${(exportProgress.progressPercent * 100).toInt()}%",
                                color = AnimeGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Completed Quick Actions
                        if (exportProgress.status == VideoExportStatus.COMPLETED && exportProgress.outputFilePath.isNotBlank()) {
                            Spacer(modifier = Modifier.height(10.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = {
                                        activePlayingMp4Path = exportProgress.outputFilePath
                                        isNativeMp4Mode = true
                                    },
                                    modifier = Modifier.weight(1f).height(38.dp).testTag("play_exported_mp4_btn"),
                                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppLocaleStrings.tr(state.selectedLanguage, "▶ Play MP4", "▶ MP4 चलाएं"), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }

                                OutlinedButton(
                                    onClick = {
                                        val intent = viewModel.createShareVideoIntent(exportProgress.outputFilePath)
                                        if (intent != null) {
                                            context.startActivity(Intent.createChooser(intent, AppLocaleStrings.tr(state.selectedLanguage, "Share MP4 Video", "MP4 वीडियो शेयर करें")))
                                        }
                                    },
                                    modifier = Modifier.weight(1f).height(38.dp).testTag("share_exported_mp4_btn"),
                                    shape = RoundedCornerShape(8.dp),
                                    border = BorderStroke(1.dp, AnimeCyan)
                                ) {
                                    Icon(Icons.Default.Share, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppLocaleStrings.tr(state.selectedLanguage, "📤 Share", "📤 शेयर करें"), color = AnimeCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                // Recent Exported Videos in Local Storage
                if (exportedVideos.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = AppLocaleStrings.tr(state.selectedLanguage, "📁 Saved MP4 Files in Storage (${exportedVideos.size}):", "📁 लोकल स्टोरेज में सेव की गई MP4 फाइल्स (${exportedVideos.size}):"),
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    exportedVideos.take(3).forEach { video ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "${video.title}.mp4",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                    val sizeMb = String.format(Locale.getDefault(), "%.1f MB", video.fileSizeBytes.toDouble() / (1024 * 1024))
                                    Text(
                                        text = "${video.resolution} • ${video.durationSeconds}s • $sizeMb • ${video.language}",
                                        color = AnimeCyanLight,
                                        fontSize = 9.sp
                                    )
                                }
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    IconButton(
                                        onClick = {
                                            activePlayingMp4Path = video.filePath
                                            isNativeMp4Mode = true
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = AnimeGold, modifier = Modifier.size(18.dp))
                                    }
                                    IconButton(
                                        onClick = {
                                            val intent = viewModel.createShareVideoIntent(video.filePath)
                                            if (intent != null) {
                                                context.startActivity(Intent.createChooser(intent, "Share MP4 Video"))
                                            }
                                        },
                                        modifier = Modifier.size(30.dp)
                                    ) {
                                        Icon(Icons.Default.Share, contentDescription = "Share", tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Multilingual Dubbing Dialog
    if (showDubbingDialog) {
        AlertDialog(
            onDismissRequest = { showDubbingDialog = false },
            title = {
                Text(AppLocaleStrings.tr(state.selectedLanguage, "🌍 Dub & Translate Video", "🌍 भाषा चुनें (Dub & Translate Video)"), fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        AppLocaleStrings.tr(state.selectedLanguage, "Translate this video and character voices into your preferred language:", "इस पूरे वीडियो और करैक्टर आवाजों को अपनी पसंदीदा भाषा में बदलें:"),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    SupportedLanguage.values().forEach { lang ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .clickable {
                                    showDubbingDialog = false
                                    viewModel.translateAndDub(lang.displayName)
                                }
                                .padding(vertical = 8.dp, horizontal = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${lang.nativeName} (${lang.displayName})",
                                color = if (state.selectedLanguage == lang.displayName) AnimeCyan else TextPrimary,
                                fontWeight = FontWeight.Medium
                            )
                            if (state.selectedLanguage == lang.displayName) {
                                Text("Active", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { showDubbingDialog = false }) {
                    Text(AppLocaleStrings.tr(state.selectedLanguage, "Close", "बंद करें"), color = AnimePurple)
                }
            },
            containerColor = AnimeSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Script & Storyboard Viewer Dialog
    if (showScriptDialog && script != null) {
        AlertDialog(
            onDismissRequest = { showScriptDialog = false },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(AppLocaleStrings.tr(state.selectedLanguage, "📝 Full Anime Script", "📝 पूरी एनिमे स्क्रिप्ट"), fontWeight = FontWeight.Bold, color = TextPrimary)
                    IconButton(onClick = {
                        val fullText = buildString {
                            appendLine("TITLE: ${script.title}")
                            appendLine("GENRE: ${script.genre} | ART: ${script.artStyle}")
                            appendLine("SYNOPSIS: ${script.synopsis}\n")
                            script.scenes.forEach { s ->
                                appendLine("--- SCENE ${s.sceneNumber}: ${s.title} ---")
                                appendLine("Visual: ${s.visualPrompt}")
                                appendLine("BGM: ${s.bgMood}")
                                s.dialogues.forEach { d ->
                                    appendLine("${d.characterName} (${d.emotion}): ${d.text}")
                                }
                                appendLine()
                            }
                        }
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Anime Script", fullText))
                        Toast.makeText(context, AppLocaleStrings.tr(state.selectedLanguage, "Script copied to clipboard!", "स्क्रिप्ट क्लिपबोर्ड पर कॉपी हो गई!"), Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = AnimeCyan)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    script.scenes.forEach { s ->
                        Text(
                            text = "${AppLocaleStrings.tr(state.selectedLanguage, "Scene", "सीन")} ${s.sceneNumber}: ${s.title}",
                            color = AnimeCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${AppLocaleStrings.tr(state.selectedLanguage, "Background", "बैकग्राउंड")}: ${s.backgroundType} (${s.bgMood})",
                            color = AnimeGold,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        s.dialogues.forEach { d ->
                            Text(
                                text = "${d.characterName}: \"${d.text}\"",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = { showScriptDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple)
                ) {
                    Text(AppLocaleStrings.tr(state.selectedLanguage, "OK", "ठीक है"), color = Color.White)
                }
            },
            containerColor = AnimeSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }

    // Download Project Dialog
    if (state.showDownloadDialog) {
        DownloadProjectDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleDownloadDialog(false) }
        )
    }
}

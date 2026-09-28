package com.example.ui.components

import android.content.Context
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.audio.AnimeMusicSynthesizer
import com.example.data.db.SavedScriptEntity
import com.example.data.model.AnimeScene
import com.example.data.model.AnimeScript
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.TextMuted
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Thumbnail and Video Preview component for Project History screen.
 * Provides:
 * 1. 16:9 Anime visual thumbnail with scene backdrop, protagonist avatar, duration & format badges.
 * 2. Instant inline video preview directly within the history card with Ken Burns camera zoom,
 *    animated action speedlines, live dialogue subtitles, scene indicator, and audio BGM preview.
 * 3. Quick trigger to expand into a high-fidelity full video preview modal before opening.
 */
@Composable
fun ProjectThumbnailVideoPreview(
    entity: SavedScriptEntity,
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier,
    onOpenFullPreview: () -> Unit
) {
    val context = LocalContext.current
    val script = remember(entity.scriptJson) { viewModel.getScriptFromJsonString(entity.scriptJson) }

    val scenes = remember(script) { script?.scenes ?: emptyList() }
    val totalScenes = scenes.size.coerceAtLeast(1)
    val leadCharacter = remember(script) { script?.characters?.firstOrNull() }

    var isPreviewPlaying by remember { mutableStateOf(false) }
    var activePreviewSceneIndex by remember { mutableIntStateOf(0) }
    var activeDialogueIndex by remember { mutableIntStateOf(0) }
    var previewProgress by remember { mutableFloatStateOf(0f) }
    var isAudioMuted by remember { mutableStateOf(true) }

    val currentScene: AnimeScene? = scenes.getOrNull(activePreviewSceneIndex) ?: scenes.firstOrNull()
    val sceneDrawable = currentScene?.sceneDrawableName
        ?: if (entity.genre.contains("Cyber", true)) "scene_cyber_city" else "scene_cherry_temple"

    // Procedural Music Preview management
    DisposableEffect(isPreviewPlaying, isAudioMuted, activePreviewSceneIndex) {
        if (isPreviewPlaying && !isAudioMuted && currentScene != null) {
            viewModel.musicSynthesizer.start(currentScene.bgMood)
        } else {
            viewModel.musicSynthesizer.stop()
        }
        onDispose {
            viewModel.musicSynthesizer.stop()
        }
    }

    // Playback loop when inline preview is active
    LaunchedEffect(isPreviewPlaying, scenes.size) {
        if (isPreviewPlaying && scenes.isNotEmpty()) {
            while (true) {
                for (step in 1..40) {
                    delay(75)
                    previewProgress = step / 40f
                }
                // Advance dialogue or scene
                val currentDialogues = scenes.getOrNull(activePreviewSceneIndex)?.dialogues ?: emptyList()
                if (currentDialogues.isNotEmpty() && activeDialogueIndex < currentDialogues.size - 1) {
                    activeDialogueIndex += 1
                } else {
                    activeDialogueIndex = 0
                    activePreviewSceneIndex = (activePreviewSceneIndex + 1) % scenes.size
                }
                previewProgress = 0f
            }
        } else {
            previewProgress = 0f
            activeDialogueIndex = 0
        }
    }

    // Camera animation for video preview
    val infiniteTransition = rememberInfiniteTransition(label = "thumbnail_preview_anim")
    val camScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPreviewPlaying) 1.09f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cam_scale"
    )

    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = if (isPreviewPlaying) -2.5f else 0f,
        targetValue = if (isPreviewPlaying) 2.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 90, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake_offset"
    )

    val speedlinesAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 260, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speedlines_alpha"
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(16f / 9f)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black)
            .border(
                width = if (isPreviewPlaying) 1.5.dp else 1.dp,
                color = if (isPreviewPlaying) AnimeCyan else MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(12.dp)
            )
            .testTag("thumbnail_video_preview_${entity.id}")
    ) {
        // Visual Backdrop / Scene Frame
        Image(
            painter = painterResource(id = ResourceHelpers.getDrawableId(context, sceneDrawable)),
            contentDescription = "Project Scene Preview",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxSize()
                .scale(camScale)
                .offset {
                    if (isPreviewPlaying) IntOffset(shakeOffset.roundToInt(), (shakeOffset * 0.5f).roundToInt())
                    else IntOffset.Zero
                }
        )

        // Speedlines / Action Canvas Overlay (when preview playing)
        if (isPreviewPlaying) {
            ActionSpeedlinesOverlay(
                alpha = speedlinesAlpha,
                accentColor = if (entity.genre.contains("Cyber", true)) AnimeCyan else AnimeGold
            )
        }

        // Top Gradient Scrim for readable badges
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .align(Alignment.TopCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Black.copy(alpha = 0.85f), Color.Transparent)
                    )
                )
        )

        // Bottom Gradient Scrim for readable subtitles/title
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.92f))
                    )
                )
        )

        // Top Row Badges: Format & Duration
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 6.dp)
                .align(Alignment.TopCenter),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Quality & Format Badge
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.65f))
                        .border(0.5.dp, AnimePurple.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "1080p FHD",
                        color = AnimeCyan,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (leadCharacter != null) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color.Black.copy(alpha = 0.65f))
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "🎭 ${leadCharacter.name.take(10)}",
                            color = Color.White,
                            fontSize = 9.sp
                        )
                    }
                }
            }

            // Duration & Scene Count Capsule
            val totalSec = scenes.sumOf { it.durationSec }.coerceAtLeast(15)
            val min = totalSec / 60
            val sec = totalSec % 60
            val timeString = String.format("%02d:%02d", min, sec)

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(
                    text = "⏱️ $timeString • $totalScenes सीन्स",
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Center Action: Play/Pause Inline Video Preview or Center Overlay
        if (!isPreviewPlaying) {
            // Hover/Click Play Button overlay
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.6f))
                    .border(1.5.dp, AnimeCyan, CircleShape)
                    .clickable { isPreviewPlaying = true }
                    .padding(10.dp)
                    .testTag("btn_play_thumbnail_${entity.id}")
            ) {
                Icon(
                    Icons.Default.PlayArrow,
                    contentDescription = "Preview Video",
                    tint = AnimeCyan,
                    modifier = Modifier.size(24.dp)
                )
            }

            // Prompt / Atmosphere Pill in Center-Bottom
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .offset(y = 28.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.Black.copy(alpha = 0.7f))
                    .clickable { isPreviewPlaying = true }
                    .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "▶ वीडियो प्रीव्यू देखें",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        } else {
            // Active Inline Video Controls (Top Right overlay / Bottom Controls)
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 28.dp, end = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Audio Toggle
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { isAudioMuted = !isAudioMuted },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (isAudioMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                        contentDescription = "Toggle Audio",
                        tint = if (isAudioMuted) TextMuted else AnimeGold,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Pause Preview
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { isPreviewPlaying = false },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Pause,
                        contentDescription = "Pause Preview",
                        tint = AnimeCyan,
                        modifier = Modifier.size(14.dp)
                    )
                }

                // Expand to Full Video Preview Modal
                Box(
                    modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.Black.copy(alpha = 0.7f))
                        .clickable { onOpenFullPreview() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Fullscreen,
                        contentDescription = "Full Preview",
                        tint = Color.White,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }

        // Bottom Subtitle & Scene Info Banner
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 8.dp, vertical = 5.dp)
        ) {
            // Live Dialogue Subtitle (when playing)
            val currentDialogue = currentScene?.dialogues?.getOrNull(activeDialogueIndex)
            if (isPreviewPlaying && currentDialogue != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Black.copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${currentDialogue.characterName}: \"${currentDialogue.text}\"",
                        color = AnimeGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
            }

            // Bottom Status Row: Scene Switcher dots + Progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Scene Name or Scene pills
                Text(
                    text = "सीन ${activePreviewSceneIndex + 1}: ${currentScene?.title?.take(16) ?: "Intro"}",
                    color = Color.White,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Scene indicator dots
                if (scenes.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        scenes.indices.forEach { idx ->
                            Box(
                                modifier = Modifier
                                    .size(if (idx == activePreviewSceneIndex) 6.dp else 4.dp)
                                    .clip(CircleShape)
                                    .background(
                                        if (idx == activePreviewSceneIndex) AnimeCyan
                                        else Color.White.copy(alpha = 0.4f)
                                    )
                                    .clickable {
                                        activePreviewSceneIndex = idx
                                        activeDialogueIndex = 0
                                    }
                            )
                        }
                    }
                }
            }

            // Video Preview Progress Line
            if (isPreviewPlaying) {
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { previewProgress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(2.5.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = AnimeCyan,
                    trackColor = Color.White.copy(alpha = 0.25f)
                )
            }
        }
    }
}

/**
 * Dedicated Fullscreen / Modal Quick Video Preview Dialog.
 * Allows users to inspect and experience the complete anime video storyline,
 * visual styles, character dialogue, animated speedlines, and sound before opening.
 */
@Composable
fun ProjectQuickVideoPreviewModal(
    entity: SavedScriptEntity,
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit,
    onResumeInStudio: () -> Unit,
    onResumeInPlayer: () -> Unit
) {
    val context = LocalContext.current
    val script = remember(entity.scriptJson) { viewModel.getScriptFromJsonString(entity.scriptJson) }
    val scenes = remember(script) { script?.scenes ?: emptyList() }

    var selectedSceneIndex by remember { mutableIntStateOf(0) }
    var selectedDialogueIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var isMuted by remember { mutableStateOf(false) }
    var playbackProgress by remember { mutableFloatStateOf(0f) }

    val activeScene = scenes.getOrNull(selectedSceneIndex) ?: scenes.firstOrNull()
    val activeDialogue = activeScene?.dialogues?.getOrNull(selectedDialogueIndex)
    val sceneDrawable = activeScene?.sceneDrawableName ?: "scene_cherry_temple"

    // Sound control
    DisposableEffect(isPlaying, isMuted, selectedSceneIndex) {
        if (isPlaying && !isMuted && activeScene != null) {
            viewModel.musicSynthesizer.start(activeScene.bgMood)
        } else {
            viewModel.musicSynthesizer.stop()
        }
        onDispose {
            viewModel.musicSynthesizer.stop()
        }
    }

    // Playback loop
    LaunchedEffect(isPlaying, selectedSceneIndex, scenes.size) {
        if (isPlaying && scenes.isNotEmpty()) {
            while (true) {
                for (step in 1..50) {
                    delay(70)
                    playbackProgress = step / 50f
                }
                val dialogues = scenes.getOrNull(selectedSceneIndex)?.dialogues ?: emptyList()
                if (dialogues.isNotEmpty() && selectedDialogueIndex < dialogues.size - 1) {
                    selectedDialogueIndex += 1
                } else {
                    selectedDialogueIndex = 0
                    selectedSceneIndex = (selectedSceneIndex + 1) % scenes.size
                }
                playbackProgress = 0f
            }
        }
    }

    // Animated effects
    val infiniteTransition = rememberInfiniteTransition(label = "modal_video_anim")
    val camScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isPlaying) 1.08f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "cam_scale_modal"
    )

    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = if (isPlaying) -3f else 0f,
        targetValue = if (isPlaying) 3f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "shake_offset_modal"
    )

    val speedlinesAlpha by infiniteTransition.animateFloat(
        initialValue = 0.25f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 220, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "speedlines_alpha_modal"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_quick_preview_modal_${entity.id}"),
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Movie,
                        contentDescription = null,
                        tint = AnimeCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "त्वरित वीडियो प्रीव्यू (Quick Video Preview)",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = AnimeCyan
                        )
                        Text(
                            text = entity.title,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // 16:9 Video Canvas Frame
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(16f / 9f)
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black)
                        .border(1.5.dp, AnimePurple.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                ) {
                    Image(
                        painter = painterResource(id = ResourceHelpers.getDrawableId(context, sceneDrawable)),
                        contentDescription = "Scene Video Screen",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxSize()
                            .scale(camScale)
                            .offset {
                                if (isPlaying) IntOffset(shakeOffset.roundToInt(), (shakeOffset * 0.5f).roundToInt())
                                else IntOffset.Zero
                            }
                    )

                    // Speedlines action overlay
                    if (isPlaying) {
                        ActionSpeedlinesOverlay(
                            alpha = speedlinesAlpha,
                            accentColor = AnimeCyan
                        )
                    }

                    // Scrims
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Black.copy(alpha = 0.6f),
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.85f)
                                    )
                                )
                            )
                    )

                    // Top Scene & Mood Chips
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.TopStart),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "सीन ${selectedSceneIndex + 1}/${scenes.size}: ${activeScene?.title ?: ""}",
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color.Black.copy(alpha = 0.7f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "🎵 ${activeScene?.bgMood ?: "Theme"}",
                                color = AnimeGold,
                                fontSize = 10.sp
                            )
                        }
                    }

                    // Bottom Live Dialogue Subtitles Overlay
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                            .padding(8.dp)
                    ) {
                        if (activeDialogue != null) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.85f))
                                    .border(0.5.dp, AnimeGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                    .padding(horizontal = 8.dp, vertical = 5.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimePurple.copy(alpha = 0.4f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = activeDialogue.characterName,
                                        color = AnimeGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "\"${activeDialogue.text}\"",
                                    color = Color.White,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        // Progress scrubber line
                        LinearProgressIndicator(
                            progress = { playbackProgress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = AnimeCyan,
                            trackColor = Color.White.copy(alpha = 0.2f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Playback Control Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Play/Pause
                        Button(
                            onClick = { isPlaying = !isPlaying },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isPlaying) AnimePurple else AnimeCyan
                            ),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(
                                if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = null,
                                tint = if (isPlaying) Color.White else Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isPlaying) "रोकें" else "चलाएं",
                                color = if (isPlaying) Color.White else Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        // Audio Toggle
                        OutlinedButton(
                            onClick = { isMuted = !isMuted },
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                if (isMuted) Icons.Default.VolumeMute else Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = if (isMuted) TextMuted else AnimeGold,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isMuted) "आवाज बंद" else "BGM चालू",
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // Next Scene Button
                    IconButton(
                        onClick = {
                            selectedSceneIndex = (selectedSceneIndex + 1) % scenes.size.coerceAtLeast(1)
                            selectedDialogueIndex = 0
                            playbackProgress = 0f
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(Icons.Default.SkipNext, contentDescription = "Next Scene", tint = AnimeCyan)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scene Carousel Thumbnails
                Text(
                    text = "सीन्स का चयन (${scenes.size} सीन्स):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(scenes) { idx, scene ->
                        val isSelected = idx == selectedSceneIndex
                        Card(
                            modifier = Modifier
                                .width(110.dp)
                                .clickable {
                                    selectedSceneIndex = idx
                                    selectedDialogueIndex = 0
                                    playbackProgress = 0f
                                },
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AnimeCyan.copy(alpha = 0.15f)
                                else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) AnimeCyan else MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(6.dp)) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(55.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                ) {
                                    Image(
                                        painter = painterResource(
                                            id = ResourceHelpers.getDrawableId(context, scene.sceneDrawableName)
                                        ),
                                        contentDescription = null,
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                    Box(
                                        modifier = Modifier
                                            .align(Alignment.BottomEnd)
                                            .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(topStart = 4.dp))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = "${scene.durationSec}s",
                                            color = Color.White,
                                            fontSize = 8.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "सीन ${idx + 1}: ${scene.title}",
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) AnimeCyan else MaterialTheme.colorScheme.onSurface,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                Spacer(modifier = Modifier.height(10.dp))

                // Synopsis Preview
                Text(
                    text = "📖 सिनॉप्सिस: ${entity.synopsis.ifBlank { entity.originalPrompt }}",
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    lineHeight = 16.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onDismiss()
                    onResumeInStudio()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("preview_modal_btn_studio")
            ) {
                Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("स्टूडियो में खोलें", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        },
        dismissButton = {
            Button(
                onClick = {
                    onDismiss()
                    onResumeInPlayer()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("preview_modal_btn_player")
            ) {
                Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("फुल प्लेयर", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    )
}

/**
 * Animated dynamic radial speedlines overlay for combat, motion, and anime impact preview.
 */
@Composable
private fun ActionSpeedlinesOverlay(
    alpha: Float,
    accentColor: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("action_speedlines_canvas")
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val rayCount = 18
        val maxRadius = kotlin.math.max(size.width, size.height)

        for (i in 0 until rayCount) {
            val angle = (i * (360f / rayCount) + (alpha * 20f)) * (Math.PI / 180f).toFloat()
            val startRadius = maxRadius * 0.42f
            val endRadius = maxRadius * 0.95f

            val startX = centerX + cos(angle) * startRadius
            val startY = centerY + sin(angle) * startRadius
            val endX = centerX + cos(angle) * endRadius
            val endY = centerY + sin(angle) * endRadius

            drawLine(
                color = accentColor.copy(alpha = (alpha * 0.35f).coerceIn(0.1f, 0.7f)),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (i % 2 == 0) 2.5f else 1.2f
            )
        }
    }
}

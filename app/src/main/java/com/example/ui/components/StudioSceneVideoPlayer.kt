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
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.SkipNext
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.UnfoldLess
import androidx.compose.material.icons.filled.UnfoldMore
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.AnimeScene
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
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
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

/**
 * Real-Time Video Player Component in the Studio Dashboard.
 * Allows creators to preview generated anime scenes in real-time with:
 * - 16:9 cinematic live video screen with dynamic Ken Burns pan & zoom camera motion
 * - Action speedlines & atmospheric anime particle effects
 * - Real-time lip-synced character dialogue subtitle HUD with speaking avatar pulses
 * - Dynamic anime background score sync & mute controls
 * - Real-time scene scrubber and horizontal scene thumbnail ribbon for instant scene navigation
 * - One-tap multilingual dubbing audition and quick launch into full player
 */
@Composable
fun StudioSceneVideoPlayer(
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()
    val context = LocalContext.current

    val script = state.currentScript
    val scenes = script?.scenes ?: emptyList()
    val currentSceneIndex = state.activeSceneIndex.coerceIn(0, (scenes.size - 1).coerceAtLeast(0))
    val currentScene: AnimeScene? = scenes.getOrNull(currentSceneIndex) ?: scenes.firstOrNull()

    var isExpanded by remember { mutableStateOf(true) }
    var scenePlaybackProgress by remember { mutableFloatStateOf(0f) }

    // Estimate playback progress for active scene
    LaunchedEffect(state.isPlayingVideo, state.activeSceneIndex, state.currentDialogueIndex, scenes.size) {
        if (state.isPlayingVideo && currentScene != null) {
            val totalDialogues = currentScene.dialogues.size.coerceAtLeast(1)
            val baseProgress = state.currentDialogueIndex.toFloat() / totalDialogues.toFloat()
            scenePlaybackProgress = baseProgress
            while (state.isPlayingVideo) {
                delay(120)
                val target = ((state.currentDialogueIndex + 0.85f) / totalDialogues.toFloat()).coerceAtMost(0.98f)
                if (scenePlaybackProgress < target) {
                    scenePlaybackProgress = (scenePlaybackProgress + 0.03f).coerceAtMost(1f)
                }
            }
        } else {
            scenePlaybackProgress = 0f
        }
    }

    // Camera animation simulation (slow zoom & pan when video plays)
    val infiniteTransition = rememberInfiniteTransition(label = "studio_video_anim")
    val cameraScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (state.isPlayingVideo) 1.07f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 5500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "studio_cam_scale"
    )

    // Pulse for active character avatar in real-time
    val avatarPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (speakingState.isSpeaking) 1.14f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 320, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "studio_avatar_pulse"
    )

    // Shake offset for combat impact
    val motionEffect = state.activeMotionEffect.ifBlank { currentScene?.motionEffect ?: "SPEEDLINES_ACTION" }
    val isImpactScene = motionEffect.contains("SHAKE", ignoreCase = true) ||
            motionEffect.contains("ACTION", ignoreCase = true)

    val cameraShakeOffset by infiniteTransition.animateFloat(
        initialValue = if (isImpactScene && state.isPlayingVideo) -2.5f else 0f,
        targetValue = if (isImpactScene && state.isPlayingVideo) 2.5f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 80, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "studio_shake_offset"
    )

    // Speedlines pulse
    val speedlinesAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 240, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "studio_speedlines_alpha"
    )

    val activeSpeaker = state.activeSpeakerName.ifBlank {
        currentScene?.dialogues?.firstOrNull()?.characterName ?: script?.characters?.firstOrNull()?.name ?: "Anime Hero"
    }
    val activeDialogue = state.activeDialogueText.ifBlank {
        currentScene?.dialogues?.firstOrNull()?.text ?: script?.synopsis ?: "Generating real-time anime scene sequence..."
    }
    val matchingCharacter = script?.characters?.find { it.name.equals(activeSpeaker, ignoreCase = true) }
        ?: state.customCharacters.find { it.name.equals(activeSpeaker, ignoreCase = true) }
    val avatarDrawable = matchingCharacter?.avatarDrawableName ?: "char_shonen_hero"

    val sceneDrawable = currentScene?.sceneDrawableName
        ?: if (script?.genre?.contains("Cyber", true) == true) "scene_cyber_city" else "scene_cherry_temple"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("studio_scene_video_player_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(
            width = 1.5.dp,
            brush = Brush.horizontalGradient(
                listOf(
                    AnimeCyan.copy(alpha = 0.8f),
                    AnimePurple.copy(alpha = 0.8f),
                    AnimePink.copy(alpha = 0.8f)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title, Live indicator & Quick Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AnimeCyan.copy(alpha = 0.15f))
                            .border(1.dp, AnimeCyan.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Live Anime Scene Player",
                                    "लाइव एनिमे सीन प्लेयर"
                                ),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            // Pulsing Real-time indicator
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (state.isPlayingVideo) AnimePink.copy(alpha = 0.2f) else AnimeCyan.copy(alpha = 0.15f))
                                    .border(
                                        1.dp,
                                        if (state.isPlayingVideo) AnimePink else AnimeCyan.copy(alpha = 0.5f),
                                        RoundedCornerShape(6.dp)
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(6.dp)
                                            .clip(CircleShape)
                                            .background(if (state.isPlayingVideo) AnimePink else AnimeCyan)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (state.isPlayingVideo) "● REAL-TIME" else "LIVE STUDIO",
                                        color = if (state.isPlayingVideo) AnimePink else AnimeCyan,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (scenes.isNotEmpty()) {
                                "Scene ${currentSceneIndex + 1} of ${scenes.size}: ${currentScene?.title ?: "Intro"}"
                            } else {
                                AppLocaleStrings.tr(state.selectedLanguage, "Ready to preview generated scenes", "सीन प्रीव्यू के लिए तैयार")
                            },
                            color = AnimeCyanLight,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Header Control Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Audio BGM Toggle
                    IconButton(
                        onClick = { viewModel.toggleMusic() },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("studio_player_toggle_music_btn")
                    ) {
                        Icon(
                            imageVector = if (state.isMusicMuted) Icons.Default.MusicOff else Icons.Default.MusicNote,
                            contentDescription = "Toggle Music",
                            tint = if (state.isMusicMuted) TextMuted else AnimeGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Open Full Screen / Full Player
                    IconButton(
                        onClick = { viewModel.setTab(AppTab.PLAYER) },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("studio_player_open_fullscreen_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Fullscreen,
                            contentDescription = "Open Full Player",
                            tint = AnimeCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Expand / Collapse toggle
                    IconButton(
                        onClick = { isExpanded = !isExpanded },
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("studio_player_toggle_expand_btn")
                    ) {
                        Icon(
                            imageVector = if (isExpanded) Icons.Default.UnfoldLess else Icons.Default.UnfoldMore,
                            contentDescription = "Toggle Expand",
                            tint = TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = isExpanded) {
                Column {
                    Spacer(modifier = Modifier.height(10.dp))

                    // 16:9 Cinematic Real-time Video Viewport
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(16f / 9f)
                            .clip(RoundedCornerShape(14.dp))
                            .background(Color.Black)
                            .border(1.dp, AnimePurple.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                            .clickable {
                                if (state.isPlayingVideo) viewModel.pauseVideo() else viewModel.playVideo()
                            }
                            .testTag("studio_realtime_video_canvas")
                    ) {
                        // Scene Backdrop Image with Camera Motion Simulation
                        Image(
                            painter = painterResource(id = ResourceHelpers.getDrawableId(context, sceneDrawable)),
                            contentDescription = "Live Anime Scene Screen",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxSize()
                                .scale(cameraScale)
                                .offset {
                                    if (state.isPlayingVideo) IntOffset(cameraShakeOffset.roundToInt(), (cameraShakeOffset * 0.5f).roundToInt())
                                    else IntOffset.Zero
                                }
                        )

                        // Dynamic Action Speedlines Overlay (when playing or during action scenes)
                        if (state.isPlayingVideo && motionEffect.contains("SPEEDLINES", ignoreCase = true)) {
                            StudioActionSpeedlines(
                                alpha = speedlinesAlpha,
                                color = AnimeCyan
                            )
                        }

                        // Ambient Anime Particles (sakura petals or glowing stardust)
                        StudioAtmosphericParticles(
                            isPlaying = state.isPlayingVideo,
                            effectType = currentScene?.atmosphericEffect ?: "Cherry Blossom Storm"
                        )

                        // Top & Bottom Gradient Scrims for HUD readability
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        listOf(
                                            Color.Black.copy(alpha = 0.72f),
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.88f)
                                        )
                                    )
                                )
                        )

                        // Top HUD: Scene title badge, format, mood
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp)
                                .align(Alignment.TopCenter),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = Color.Black.copy(alpha = 0.65f),
                                border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "🎬 ${currentScene?.title ?: "Scene ${currentSceneIndex + 1}"}",
                                        color = AnimeGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                }
                            }

                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = Color.Black.copy(alpha = 0.65f),
                                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
                                ) {
                                    Text(
                                        text = "🎵 ${currentScene?.bgMood ?: "Anime BGM"}",
                                        color = AnimeCyanLight,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = AnimePurple.copy(alpha = 0.7f)
                                ) {
                                    Text(
                                        text = "60 FPS AI",
                                        color = Color.White,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }

                        // Center Play Overlay (when paused)
                        if (!state.isPlayingVideo && !state.isGenerating) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size(52.dp)
                                    .clip(CircleShape)
                                    .background(Color.Black.copy(alpha = 0.65f))
                                    .border(2.dp, AnimeCyan, CircleShape)
                                    .testTag("studio_player_center_play_button"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = "Play Video Preview",
                                    tint = AnimeCyan,
                                    modifier = Modifier.size(30.dp)
                                )
                            }
                        }

                        // Real-Time AI Generation Scanner Overlay
                        if (state.isGenerating) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(Color.Black.copy(alpha = 0.8f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(36.dp),
                                        color = AnimeCyan,
                                        strokeWidth = 3.5.dp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = AppLocaleStrings.tr(
                                            state.selectedLanguage,
                                            "Directing & Synthesizing Anime Scene...",
                                            "एनिमे सीन का निर्माण व डबिंग जारी है..."
                                        ),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = state.generationStep.ifBlank { "Real-time AI storyboard assembly..." },
                                        color = AnimeCyanLight,
                                        fontSize = 10.sp,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.padding(horizontal = 24.dp)
                                    )
                                }
                            }
                        }

                        // Bottom HUD: Character Lip-Sync Avatar & Subtitles
                        Column(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .fillMaxWidth()
                                .padding(horizontal = 10.dp, vertical = 8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Character Avatar with Lip-Sync Pulsing
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .scale(avatarPulseScale)
                                        .clip(CircleShape)
                                        .background(AnimeSurfaceVariant)
                                        .border(
                                            width = if (speakingState.isSpeaking) 2.dp else 1.dp,
                                            color = if (speakingState.isSpeaking) AnimePink else AnimeCyan,
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
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                // Speaker Name, Emotion & Mini Voice Amplitude Visualizer
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = activeSpeaker,
                                            color = AnimeGold,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "• ${currentScene?.dialogues?.getOrNull(state.currentDialogueIndex)?.emotion ?: "Speaking"}",
                                            color = AnimeCyanLight,
                                            fontSize = 9.sp
                                        )
                                        if (speakingState.isSpeaking) {
                                            Spacer(modifier = Modifier.width(8.dp))
                                            StudioMiniAudioMeter(amplitude = speakingState.audioWaveAmplitude)
                                        }
                                    }

                                    // Dialogue Subtitle Box
                                    Surface(
                                        shape = RoundedCornerShape(8.dp),
                                        color = Color.Black.copy(alpha = 0.65f),
                                        modifier = Modifier.padding(top = 2.dp)
                                    ) {
                                        Text(
                                            text = activeDialogue,
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            lineHeight = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            maxLines = 2,
                                            overflow = TextOverflow.Ellipsis,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Progress Bar for current scene & video playback
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = {
                            if (scenes.isNotEmpty()) {
                                ((currentSceneIndex + scenePlaybackProgress) / scenes.size.toFloat()).coerceIn(0f, 1f)
                            } else 0f
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(3.5.dp)
                            .clip(RoundedCornerShape(2.dp)),
                        color = AnimeCyan,
                        trackColor = AnimeSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Transport Playback Controls: Prev, Play/Pause, Next, Replay, Time indicator
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Scene duration and index label
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Scene ${currentSceneIndex + 1}/${scenes.size.coerceAtLeast(1)}",
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "• ~${(scenes.size * (currentScene?.durationSec ?: 8))}s total",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        // Center Playback Buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Previous Scene
                            IconButton(
                                onClick = { viewModel.previousScene() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("studio_player_prev_scene_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipPrevious,
                                    contentDescription = "Previous Scene",
                                    tint = if (scenes.isNotEmpty()) AnimeCyan else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Big Play / Pause Button
                            Button(
                                onClick = {
                                    if (state.isPlayingVideo) viewModel.pauseVideo() else viewModel.playVideo()
                                },
                                shape = CircleShape,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (state.isPlayingVideo) AnimePink else AnimeCyan
                                ),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                modifier = Modifier
                                    .size(42.dp)
                                    .testTag("studio_video_play_pause_button")
                            ) {
                                Icon(
                                    imageVector = if (state.isPlayingVideo) Icons.Default.Pause else Icons.Default.PlayArrow,
                                    contentDescription = if (state.isPlayingVideo) "Pause Video" else "Play Video",
                                    tint = Color.Black,
                                    modifier = Modifier.size(24.dp)
                                )
                            }

                            // Next Scene
                            IconButton(
                                onClick = { viewModel.nextScene() },
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("studio_player_next_scene_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.SkipNext,
                                    contentDescription = "Next Scene",
                                    tint = if (scenes.isNotEmpty()) AnimeCyan else TextMuted,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            // Replay current scene
                            IconButton(
                                onClick = { viewModel.selectScene(currentSceneIndex) },
                                modifier = Modifier
                                    .size(34.dp)
                                    .testTag("studio_player_replay_scene_btn")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Replay,
                                    contentDescription = "Replay Scene",
                                    tint = AnimeGold,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        // Launch full screen / Export quick trigger
                        OutlinedButton(
                            onClick = { viewModel.setTab(AppTab.PLAYER) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.6f)),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("studio_player_full_screen_btn")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Fullscreen,
                                contentDescription = null,
                                tint = AnimeCyan,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Full Player", color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Horizontal Scene Selector Strip (Thumbnail Cards for Real-Time Switching)
                    if (scenes.isNotEmpty()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "🎞️ Storyboard Scenes (Tap to Preview):",
                                    "🎞️ स्टोरीबोर्ड सीन्स (क्लिक करके देखें):"
                                ),
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${scenes.size} Scenes",
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            itemsIndexed(scenes) { index, scene ->
                                val isSelected = index == currentSceneIndex
                                Card(
                                    modifier = Modifier
                                        .width(130.dp)
                                        .clickable { viewModel.selectScene(index) }
                                        .testTag("studio_scene_thumbnail_card_$index"),
                                    shape = RoundedCornerShape(10.dp),
                                    colors = CardDefaults.cardColors(
                                        containerColor = if (isSelected) AnimePurple.copy(alpha = 0.25f) else AnimeSurfaceVariant
                                    ),
                                    border = BorderStroke(
                                        1.5.dp,
                                        if (isSelected) AnimeCyan else AnimeSurfaceVariant
                                    )
                                ) {
                                    Column {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(60.dp)
                                        ) {
                                            Image(
                                                painter = painterResource(
                                                    id = ResourceHelpers.getDrawableId(context, scene.sceneDrawableName)
                                                ),
                                                contentDescription = scene.title,
                                                contentScale = ContentScale.Crop,
                                                modifier = Modifier.fillMaxSize()
                                            )
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize()
                                                    .background(
                                                        Brush.verticalGradient(
                                                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.7f))
                                                        )
                                                    )
                                            )
                                            // Scene badge
                                            Surface(
                                                shape = RoundedCornerShape(bottomEnd = 6.dp),
                                                color = if (isSelected) AnimeCyan else Color.Black.copy(alpha = 0.7f),
                                                modifier = Modifier.align(Alignment.TopStart)
                                            ) {
                                                Text(
                                                    text = "Scene ${index + 1}",
                                                    color = if (isSelected) Color.Black else Color.White,
                                                    fontSize = 8.sp,
                                                    fontWeight = FontWeight.Bold,
                                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 2.dp)
                                                )
                                            }
                                            if (isSelected && state.isPlayingVideo) {
                                                Box(
                                                    modifier = Modifier
                                                        .align(Alignment.Center)
                                                        .size(20.dp)
                                                        .clip(CircleShape)
                                                        .background(AnimePink.copy(alpha = 0.85f)),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        imageVector = Icons.Default.PlayArrow,
                                                        contentDescription = null,
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }
                                        Column(modifier = Modifier.padding(6.dp)) {
                                            Text(
                                                text = scene.title,
                                                color = if (isSelected) AnimeCyanLight else TextPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Text(
                                                text = scene.bgMood,
                                                color = TextMuted,
                                                fontSize = 8.sp,
                                                maxLines = 1
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Real-Time Multilingual Dubbing & Voice Accent Strip
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Translate, contentDescription = null, tint = AnimePink, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Real-Time Voice Dubbing:",
                                    "रियल-टाइम वॉयस डबिंग:"
                                ),
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = state.selectedLanguage,
                            color = AnimeGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    val quickLanguages = listOf("Hindi", "Japanese", "English", "Spanish", "Korean", "German")
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        itemsIndexed(quickLanguages) { _, lang ->
                            val isSel = state.selectedLanguage.equals(lang, ignoreCase = true)
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.translateAndDub(lang) },
                                label = {
                                    val flag = when (lang) {
                                        "Hindi" -> "🇮🇳"
                                        "Japanese" -> "🇯🇵"
                                        "English" -> "🇺🇸"
                                        "Spanish" -> "🇪🇸"
                                        "Korean" -> "🇰🇷"
                                        "German" -> "🇩🇪"
                                        else -> "🌍"
                                    }
                                    Text("$flag $lang", fontSize = 9.sp, fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal)
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimePink.copy(alpha = 0.25f),
                                    selectedLabelColor = AnimePink
                                ),
                                modifier = Modifier.height(28.dp).testTag("studio_player_dub_${lang.lowercase()}")
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Animated dynamic action speedlines on the studio video viewport canvas.
 */
@Composable
private fun StudioActionSpeedlines(
    alpha: Float,
    color: Color
) {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .testTag("studio_speedlines_canvas")
    ) {
        val centerX = size.width / 2f
        val centerY = size.height / 2f
        val rayCount = 16
        val maxRadius = kotlin.math.max(size.width, size.height)

        for (i in 0 until rayCount) {
            val angle = (i * (360f / rayCount) + (alpha * 15f)) * (Math.PI / 180f).toFloat()
            val startRadius = maxRadius * 0.44f
            val endRadius = maxRadius * 0.95f

            val startX = centerX + cos(angle) * startRadius
            val startY = centerY + sin(angle) * startRadius
            val endX = centerX + cos(angle) * endRadius
            val endY = centerY + sin(angle) * endRadius

            drawLine(
                color = color.copy(alpha = (alpha * 0.35f).coerceIn(0.1f, 0.6f)),
                start = Offset(startX, startY),
                end = Offset(endX, endY),
                strokeWidth = if (i % 2 == 0) 2.2f else 1.0f
            )
        }
    }
}

/**
 * Atmospheric anime particles (floating sakura petals or stardust aura).
 */
@Composable
private fun StudioAtmosphericParticles(
    isPlaying: Boolean,
    effectType: String
) {
    val infiniteTransition = rememberInfiniteTransition(label = "studio_particles")
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "particle_phase"
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val isSakura = effectType.contains("Blossom", true) || effectType.contains("Sakura", true)
        val particleColor = if (isSakura) Color(0xFFFF80AB) else Color(0xFF00E5FF)
        val particleCount = 12

        for (i in 0 until particleCount) {
            val progress = (phase + (i.toFloat() / particleCount)) % 1f
            val x = (size.width * ((i * 0.17f) % 1f)) + (sin(progress * 6.28f + i) * 20f)
            val y = size.height * progress
            val radius = if (i % 3 == 0) 3.5f else 2.2f

            drawCircle(
                color = particleColor.copy(alpha = if (isPlaying) (sin(progress * 3.14f) * 0.7f).coerceIn(0.1f, 0.8f) else 0.25f),
                radius = radius,
                center = Offset(x, y)
            )
        }
    }
}

/**
 * Mini animated audio meter responding to voice amplitude.
 */
@Composable
private fun StudioMiniAudioMeter(amplitude: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        val baseH = listOf(0.4f, 0.9f, 0.6f, 1.0f, 0.7f)
        baseH.forEach { h ->
            val dynamicHeight = (8.dp * ((amplitude * h).coerceIn(0.25f, 1f)))
            Box(
                modifier = Modifier
                    .width(2.5.dp)
                    .height(dynamicHeight)
                    .clip(RoundedCornerShape(1.dp))
                    .background(AnimePink)
            )
        }
    }
}

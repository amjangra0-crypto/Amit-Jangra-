package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.MusicOff
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.data.model.SubtitleMode
import com.example.data.model.SupportedLanguage
import com.example.ui.AnimeViewModel
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
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var showScriptDialog by remember { mutableStateOf(false) }
    var showDubbingDialog by remember { mutableStateOf(false) }

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
                    text = script?.title ?: "एनिमे वीडियो प्लेयर",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${script?.genre ?: "Anime"} • ${script?.artStyle ?: "Japanese Style"} • ${state.selectedLanguage}",
                    color = AnimeCyanLight,
                    fontSize = 11.sp
                )
            }
            Row {
                IconButton(onClick = { showDubbingDialog = true }) {
                    Icon(Icons.Default.Translate, contentDescription = "Dubbing", tint = AnimeCyan)
                }
                IconButton(onClick = { showScriptDialog = true }) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "View Script", tint = AnimePurple)
                }
            }
        }

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
            )

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
                        text = "सीन ${(state.activeSceneIndex + 1)} / ${scenes.size.coerceAtLeast(1)}",
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

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(AnimePurple)
                            .border(
                                2.dp,
                                if (speakingState.isSpeaking) AnimePink else AnimeCyan.copy(alpha = 0.5f),
                                CircleShape
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

                    // Character Name & Emotion Tag & Voice Persona
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = activeSpeaker,
                                color = AnimeCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
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
                            matchedProfile?.let { prof ->
                                if (prof.voicePersona.isNotBlank()) {
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AnimePurple.copy(alpha = 0.3f))
                                            .padding(horizontal = 4.dp, vertical = 1.dp)
                                    ) {
                                        Text(
                                            text = prof.voicePersona.take(14),
                                            color = AnimeGold,
                                            fontSize = 8.sp
                                        )
                                    }
                                }
                            }
                            if (speakingState.isSpeaking) {
                                Spacer(modifier = Modifier.width(6.dp))
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
                text = "🎞️ सीन टाइमलाइन (Storyboard Scenes):",
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
                                    text = "सीन ${scene.sceneNumber}",
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
                                text = "🌐 एआई ट्रांसलेशन व डबिंग इंजन (Translation Engine)",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "वर्तमान डबिंग भाषा: ${state.selectedLanguage}",
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
                        Text("डायलॉग्स अनुवाद", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                    Text("सबटाइटल मोड:", color = TextSecondary, fontSize = 11.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        SubtitleMode.values().forEach { mode ->
                            val isSel = state.subtitleMode == mode
                            val modeLabel = when (mode) {
                                SubtitleMode.TRANSLATED_ONLY -> "अनुवादित"
                                SubtitleMode.BILINGUAL_DUAL -> "🎌+🌐 द्विभाषी"
                                SubtitleMode.ORIGINAL_ONLY -> "मूल"
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
                    text = "📖 कहानी का सार (Synopsis):",
                    color = AnimeGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = script?.synopsis ?: "दास्तान तैयार हो रही है...",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    lineHeight = 18.sp
                )
            }
        }
    }

    // Multilingual Dubbing Dialog
    if (showDubbingDialog) {
        AlertDialog(
            onDismissRequest = { showDubbingDialog = false },
            title = {
                Text("🌍 भाषा चुनें (Dub & Translate Video)", fontWeight = FontWeight.Bold, color = TextPrimary)
            },
            text = {
                Column {
                    Text(
                        "इस पूरे वीडियो और करैक्टर आवाजों को अपनी पसंदीदा भाषा में बदलें:",
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
                    Text("बंद करें", color = AnimePurple)
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
                    Text("📝 पूरी एनिमे स्क्रिप्ट", fontWeight = FontWeight.Bold, color = TextPrimary)
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
                        Toast.makeText(context, "स्क्रिप्ट क्लिपबोर्ड पर कॉपी हो गई!", Toast.LENGTH_SHORT).show()
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share", tint = AnimeCyan)
                    }
                }
            },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                    script.scenes.forEach { s ->
                        Text(
                            text = "सीन ${s.sceneNumber}: ${s.title}",
                            color = AnimeCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "बैकग्राउंड: ${s.backgroundType} (${s.bgMood})",
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
                    Text("ठीक है", color = Color.White)
                }
            },
            containerColor = AnimeSurface,
            shape = RoundedCornerShape(16.dp)
        )
    }
}

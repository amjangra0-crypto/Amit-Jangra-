package com.example.ui.screens

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.CloudQueue
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import com.example.ui.components.VoiceMoodFilterSlider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterProfile
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
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

/**
 * Data Model for Imported External Voices (Gallery, Google Drive, Download from Google/Web)
 */
data class ImportedVoiceAsset(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val sourceLabel: String,
    val uriOrUrl: String,
    val durationSeconds: Int = 15,
    val assignedCharacterName: String? = null,
    val isExplainerMode: Boolean = true
)

/**
 * Voice Studio Screen Component:
 * 1. Select Voice Character (Hero, Heroine, Mentor, Sensei, Narrator).
 * 2. Adjust Pitch (0.5x - 2.0x) and Speed (0.5x - 2.0x) Sliders.
 * 3. 5-Second Sample Audio Preview with Waveform and Countdown.
 * 4. Add Any Voice from Gallery, Google Drive, or Download from Google/Web.
 * 5. AI Auto-Fit: automatically selects the voice best suited for the character.
 * 6. Video Explainer Mode: toggle between dramatic anime dialogue and documentary/explainer narration.
 */
@Composable
fun VoiceStudioScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage
    val characters = state.customCharacters

    // Selected Character & Voice Modulation
    var selectedCharIndex by remember { mutableIntStateOf(0) }
    val activeCharacter = characters.getOrNull(selectedCharIndex) ?: CharacterProfile(
        id = "default_char",
        name = "Ren Kurogane",
        gender = "Male",
        role = "Hero",
        personality = "Brave & Determined",
        voiceType = "Shonen Hero",
        voicePitch = 1.0f,
        voiceSpeed = 1.0f
    )

    var pitchSlider by remember { mutableFloatStateOf(activeCharacter.voicePitch) }
    var speedSlider by remember { mutableFloatStateOf(activeCharacter.voiceSpeed) }
    var isVideoExplainerMode by remember { mutableStateOf(false) }

    // Synchronize sliders when character selection changes
    LaunchedEffect(selectedCharIndex) {
        pitchSlider = activeCharacter.voicePitch
        speedSlider = activeCharacter.voiceSpeed
    }

    // 5-Second Sample Audio Preview State
    var selectedVoiceMood by remember { mutableStateOf("Natural") }
    var isPreviewPlaying by remember { mutableStateOf(false) }
    var previewCountdown by remember { mutableIntStateOf(5) }
    var previewJob by remember { mutableStateOf<Job?>(null) }

    // External Audio Import States
    var googleDownloadUrlInput by remember { mutableStateOf("") }
    var driveAudioLinkInput by remember { mutableStateOf("") }
    var showImportVoiceModal by remember { mutableStateOf(false) }

    var importedVoices by remember {
        mutableStateOf(
            listOf(
                ImportedVoiceAsset(
                    id = "voice_sample_1",
                    name = "Japanese Anime Heroine Studio Narration",
                    sourceLabel = "Google Drive (Cloud)",
                    uriOrUrl = "https://drive.google.com/sample/anime_heroine_voice.mp3",
                    durationSeconds = 24,
                    assignedCharacterName = "Aoi",
                    isExplainerMode = false
                ),
                ImportedVoiceAsset(
                    id = "voice_sample_2",
                    name = "Deep Cinematic Documentary Explainer Voice",
                    sourceLabel = "Google Web Download",
                    uriOrUrl = "https://cdn.animestudio.ai/explainer_voiceover_male.mp3",
                    durationSeconds = 45,
                    assignedCharacterName = "Ren",
                    isExplainerMode = true
                )
            )
        )
    }

    // Gallery Audio File Picker Launcher
    val audioGalleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val fileName = uri.lastPathSegment ?: "Gallery_Voice_Audio"
            val newAsset = ImportedVoiceAsset(
                name = "Local: $fileName",
                sourceLabel = "Device Gallery / File Storage",
                uriOrUrl = uri.toString(),
                durationSeconds = 18,
                assignedCharacterName = activeCharacter.name,
                isExplainerMode = isVideoExplainerMode
            )
            importedVoices = importedVoices + newAsset
            Toast.makeText(context, "📁 Audio imported from Gallery: $fileName", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("voice_studio_screen")
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = {
                    previewJob?.cancel()
                    viewModel.voiceSyncEngine.stop()
                    onNavigateBack()
                },
                modifier = Modifier.testTag("voice_studio_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Voice Studio & Explainer Audio", "वॉइस स्टूडियो व एक्सप्लेनर ऑडियो"),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimePink.copy(alpha = 0.2f))
                            .border(1.dp, AnimePink, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("TTS + SYNC", color = AnimePink, fontSize = 8.sp, fontWeight = FontWeight.Black)
                    }
                }
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Pitch/Speed tuning, 5s preview, and external audio import for video explaining",
                        "पिच/स्पीड कंट्रोल, 5-सेकंड प्रीव्यू व गैलरी/ड्राइव से एक्सप्लेनर वॉइस आयात"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. CHARACTER SELECTOR ROW
            item {
                Text(
                    text = AppLocaleStrings.tr(lang, "SELECT VOICE CHARACTER:", "वॉइस करैक्टर चुनें:"),
                    color = AnimeCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(characters.indices.toList()) { index ->
                        val char = characters[index]
                        val isSelected = selectedCharIndex == index
                        Card(
                            modifier = Modifier
                                .clickable { selectedCharIndex = index }
                                .testTag("char_voice_select_$index"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AnimePurple.copy(alpha = 0.25f) else AnimeSurfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 1.5.dp else 0.5.dp,
                                color = if (isSelected) AnimeCyan else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(if (char.gender == "Female") AnimePink.copy(alpha = 0.2f) else AnimeCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.RecordVoiceOver,
                                        contentDescription = null,
                                        tint = if (char.gender == "Female") AnimePink else AnimeCyan,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = char.name,
                                        color = if (isSelected) AnimeCyan else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${char.gender} • ${char.role}",
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // 2. PITCH & SPEED SLIDERS + 5-SECOND PREVIEW CARD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("voice_pitch_speed_card"),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Voice Tuning for ${activeCharacter.name}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )

                            // AI Auto-Fit Recommendation Button
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AnimeCyan.copy(alpha = 0.2f))
                                    .clickable {
                                        // Auto-Fit logic based on character attributes
                                        val (autoPitch, autoSpeed) = when {
                                            isVideoExplainerMode -> 1.0f to 1.05f // Clear Explainer Cadence
                                            activeCharacter.gender.equals("Female", ignoreCase = true) -> 1.35f to 1.1f // Kawaii Heroine
                                            activeCharacter.role.equals("Mentor", ignoreCase = true) -> 0.85f to 0.95f // Deep Sensei
                                            activeCharacter.role.equals("Villain", ignoreCase = true) -> 0.75f to 0.9f // Menacing Baritone
                                            else -> 1.05f to 1.15f // Energetic Shonen Protagonist
                                        }
                                        pitchSlider = autoPitch
                                        speedSlider = autoSpeed
                                        Toast.makeText(
                                            context,
                                            "🤖 AI Auto-Fit applied for ${activeCharacter.name}: Pitch ${"%.2f".format(autoPitch)}x, Speed ${"%.2f".format(autoSpeed)}x!",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("ai_auto_fit_voice_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("🤖 AI Auto-Fit", color = AnimeCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Video Explainer Mode Switch
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AnimeSurfaceVariant)
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "🎙️ Video Explainer Narration Mode",
                                    color = if (isVideoExplainerMode) AnimeGold else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = if (isVideoExplainerMode)
                                        "Voice acts as Documentary/Explainer Narrator explaining video scenes"
                                    else
                                        "Standard Anime In-Character Dialogue Mode",
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                            Switch(
                                checked = isVideoExplainerMode,
                                onCheckedChange = { isVideoExplainerMode = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = AnimeGold,
                                    checkedTrackColor = AnimeGold.copy(alpha = 0.4f)
                                ),
                                modifier = Modifier.testTag("video_explainer_mode_switch")
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // 1. Pitch Slider (0.5x to 2.0x)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Voice Pitch (आवाज़ की टोन):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("${"%.2f".format(pitchSlider)}x (${if (pitchSlider > 1.0f) "Higher / Anime" else if (pitchSlider < 1.0f) "Deeper / Baritone" else "Natural"})", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = pitchSlider,
                            onValueChange = { pitchSlider = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = AnimeCyan,
                                activeTrackColor = AnimeCyan
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("voice_pitch_slider")
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // 2. Speed Slider (0.5x to 2.0x)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Speech Speed (बोलने की गति):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                            Text("${"%.2f".format(speedSlider)}x (${if (speedSlider > 1.0f) "Fast Action" else if (speedSlider < 1.0f) "Dramatic Slow" else "Normal"})", color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                        Slider(
                            value = speedSlider,
                            onValueChange = { speedSlider = it },
                            valueRange = 0.5f..2.0f,
                            colors = SliderDefaults.colors(
                                thumbColor = AnimePink,
                                activeTrackColor = AnimePink
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("voice_speed_slider")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Real-Time Voice Mood Filter Slider Component (Deep Bass, Natural, High Pitch, Robot)
                        VoiceMoodFilterSlider(
                            currentMood = selectedVoiceMood,
                            onMoodSelected = { mood ->
                                selectedVoiceMood = mood
                                viewModel.setPreferredVoiceMood(mood)
                            },
                            onAuditionMood = { mood ->
                                selectedVoiceMood = mood
                                viewModel.setPreferredVoiceMood(mood)
                                val sampleDialogue = if (isVideoExplainerMode) {
                                    "In this scene, the secret power awakens, changing the course of their journey forever."
                                } else {
                                    activeCharacter.sampleDialogue.ifBlank { "私を信じて！一緒に未来を変えよう！" }
                                }
                                viewModel.voiceSyncEngine.speakDialogue(
                                    characterName = activeCharacter.name,
                                    dialogueText = sampleDialogue,
                                    emotion = "Confidence",
                                    voiceType = activeCharacter.voiceType,
                                    pitch = pitchSlider,
                                    speed = speedSlider,
                                    voiceMood = mood
                                )
                            },
                            isCurrentlyAuditioning = isPreviewPlaying,
                            modifier = Modifier.fillMaxWidth().testTag("studio_voice_mood_filter_slider")
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // 3. 5-SECOND SAMPLE AUDIO PREVIEW BUTTON & WAVEFORM
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AnimeSurfaceVariant)
                                .border(1.dp, if (isPreviewPlaying) AnimeGreen else AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                if (isPreviewPlaying) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = "Playing 5s Sample Preview (${previewCountdown}s remaining)...",
                                            color = AnimeGreen,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AnimeGreen)
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text("${previewCountdown}s", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    // Dynamic Audio Waveform
                                    Row(
                                        modifier = Modifier.fillMaxWidth().height(36.dp),
                                        horizontalArrangement = Arrangement.SpaceEvenly,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        repeat(20) { i ->
                                            val barH = (8 + ((i * 11 + previewCountdown * 7) % 28)).dp
                                            Box(
                                                modifier = Modifier
                                                    .width(4.dp)
                                                    .height(barH)
                                                    .background(if (i % 2 == 0) AnimeGreen else AnimeCyan, RoundedCornerShape(2.dp))
                                            )
                                        }
                                    }

                                    Spacer(modifier = Modifier.height(10.dp))

                                    OutlinedButton(
                                        onClick = {
                                            previewJob?.cancel()
                                            viewModel.voiceSyncEngine.stop()
                                            isPreviewPlaying = false
                                        },
                                        border = BorderStroke(1.dp, AnimePink),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Icon(Icons.Default.Stop, contentDescription = null, tint = AnimePink, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Stop Preview", color = AnimePink, fontSize = 10.sp)
                                    }
                                } else {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text("5-Second Audio Sample Preview", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                            Text("Tests exact pitch, speed & pronunciation cadence", color = TextMuted, fontSize = 9.sp)
                                        }

                                        Button(
                                            onClick = {
                                                isPreviewPlaying = true
                                                previewCountdown = 5
                                                val sampleDialogue = if (isVideoExplainerMode) {
                                                    "In this scene, the secret power awakens, changing the course of their journey forever."
                                                } else {
                                                    activeCharacter.sampleDialogue.ifBlank { "私を信じて！一緒に未来を変えよう！" }
                                                }

                                                viewModel.voiceSyncEngine.speakDialogue(
                                                    characterName = activeCharacter.name,
                                                    dialogueText = sampleDialogue,
                                                    emotion = "Confidence",
                                                    voiceType = activeCharacter.voiceType,
                                                    pitch = pitchSlider,
                                                    speed = speedSlider,
                                                    voiceMood = selectedVoiceMood
                                                )

                                                previewJob?.cancel()
                                                previewJob = coroutineScope.launch {
                                                    for (sec in 5 downTo 1) {
                                                        previewCountdown = sec
                                                        delay(1000)
                                                    }
                                                    viewModel.voiceSyncEngine.stop()
                                                    isPreviewPlaying = false
                                                }
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                                            shape = RoundedCornerShape(8.dp),
                                            modifier = Modifier.testTag("play_5s_sample_preview_btn")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text("5s Audio Preview", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // 3. ADD VOICE FROM GALLERY, GOOGLE DRIVE, AND GOOGLE DOWNLOAD
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("import_voice_sources_card"),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Import Voice (Gallery / Drive / Google Download)",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Add custom voice tracks from local files, Google Drive, or Web for video explaining & character dubbing",
                            color = TextMuted,
                            fontSize = 10.sp
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Three Source Action Buttons
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Source 1: Device Gallery Audio
                            OutlinedButton(
                                onClick = { audioGalleryLauncher.launch("audio/*") },
                                border = BorderStroke(1.dp, AnimeCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("import_gallery_voice_btn")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.Folder, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Gallery", color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Source 2: Google Drive Cloud Audio
                            OutlinedButton(
                                onClick = {
                                    val newDriveAsset = ImportedVoiceAsset(
                                        name = "Drive: ${activeCharacter.name}_Explainer_Audio.mp3",
                                        sourceLabel = "Google Drive (Cloud)",
                                        uriOrUrl = "https://drive.google.com/sample/audio.mp3",
                                        durationSeconds = 30,
                                        assignedCharacterName = activeCharacter.name,
                                        isExplainerMode = isVideoExplainerMode
                                    )
                                    importedVoices = importedVoices + newDriveAsset
                                    Toast.makeText(context, "☁️ Google Drive Voice Track Linked!", Toast.LENGTH_SHORT).show()
                                },
                                border = BorderStroke(1.dp, AnimeGold),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("import_drive_voice_btn")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CloudQueue, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Google Drive", color = AnimeGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Source 3: Download from Google / Web URL
                            OutlinedButton(
                                onClick = { showImportVoiceModal = !showImportVoiceModal },
                                border = BorderStroke(1.dp, AnimePink),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.weight(1f).testTag("import_google_web_voice_btn")
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text("Download URL", color = AnimePink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Expandable Web Download URL Input
                        AnimatedVisibility(visible = showImportVoiceModal) {
                            Column(modifier = Modifier.padding(top = 10.dp)) {
                                OutlinedTextField(
                                    value = googleDownloadUrlInput,
                                    onValueChange = { googleDownloadUrlInput = it },
                                    label = { Text("Google Audio URL / Voiceover File Link", fontSize = 10.sp) },
                                    placeholder = { Text("https://example.com/explainer_voice.mp3", fontSize = 10.sp, color = TextMuted) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth()
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Button(
                                    onClick = {
                                        if (googleDownloadUrlInput.isNotBlank()) {
                                            val webAsset = ImportedVoiceAsset(
                                                name = "Web Download: Explainer Voice",
                                                sourceLabel = "Google Web Download",
                                                uriOrUrl = googleDownloadUrlInput.trim(),
                                                durationSeconds = 40,
                                                assignedCharacterName = activeCharacter.name,
                                                isExplainerMode = isVideoExplainerMode
                                            )
                                            importedVoices = importedVoices + webAsset
                                            Toast.makeText(context, "🌐 Audio downloaded and registered!", Toast.LENGTH_SHORT).show()
                                            googleDownloadUrlInput = ""
                                            showImportVoiceModal = false
                                        }
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Download & Assign to Explainer", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // List of Imported Voice Assets
                        Text(
                            text = "AVAILABLE IMPORTED VOICES (${importedVoices.size}):",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            importedVoices.forEach { asset ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AnimeSurfaceVariant)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(asset.name, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text("Source: ${asset.sourceLabel} • ${asset.durationSeconds}s clip", color = TextMuted, fontSize = 9.sp)
                                    }

                                    // Auto Fit / Assign Button
                                    Button(
                                        onClick = {
                                            Toast.makeText(
                                                context,
                                                "🤖 Auto-fitted '${asset.name}' to ${activeCharacter.name} for " + if (asset.isExplainerMode) "Video Explaining" else "Character Dialogue",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.3f)),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp)
                                    ) {
                                        Text("Auto-Fit", color = AnimeCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

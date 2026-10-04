package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.core.FastOutSlowInEasing
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
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterProfile
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.screens.AiVoiceProfile
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

data class AnimeEmotionOption(
    val id: String,
    val emoji: String,
    val nameEn: String,
    val nameHi: String,
    val defaultEyeExpression: String,
    val sampleDialogueJp: String,
    val sampleDialogueHi: String,
    val sampleDialogueEn: String,
    val recommendedPitchDelta: Float = 0.0f
)

/**
 * Dedicated Voice Selector Studio:
 * - Character Selector (switch across all local DB custom characters & presets)
 * - Emotion Selector (Joy, Battle Fury, Sorrow, Tsundere, Calm Sensei, Mystery, Romantic)
 * - Eye Expression synced with emotion (or overridden via dropdown)
 * - Dialogue System (multilingual anime presets & custom input)
 * - Voice tuning sliders (Pitch, Speed, Emotional Resonance)
 * - Live Audition playback & animated lipsync visualizer
 * - Instant persistence to Room Local Database
 */
@Composable
fun VoiceSelectorStudioComponent(
    viewModel: AnimeViewModel,
    draft: CharacterProfile,
    voiceProfiles: List<AiVoiceProfile>,
    isSpeaking: Boolean,
    mouthOpenAmount: Float,
    onSaveToDatabase: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val allCharacters = uiState.customCharacters
    val lang = uiState.selectedLanguage

    val emotions = remember {
        listOf(
            AnimeEmotionOption(
                id = "Joy",
                emoji = "🌟",
                nameEn = "Joy & Excited",
                nameHi = "आनंद व प्रफुल्लित",
                defaultEyeExpression = "Sparkling Wonder",
                sampleDialogueJp = "やったー！今日から新しい冒険の始まりだ！",
                sampleDialogueHi = "अरे वाह! आज से हमारे नए जादुई रोमांच की शुरुआत होगी!",
                sampleDialogueEn = "Yatta! Today marks the glorious start of our brand new adventure!",
                recommendedPitchDelta = 0.08f
            ),
            AnimeEmotionOption(
                id = "BattleFury",
                emoji = "⚔️",
                nameEn = "Battle Fury",
                nameHi = "क्रोध व युद्ध ललकार",
                defaultEyeExpression = "Fierce Determined Stare",
                sampleDialogueJp = "許さない…私の仲間を傷つける奴は絶対に許さない！",
                sampleDialogueHi = "मैं तुम्हें कभी माफ नहीं करूंगा! मेरे दोस्तों को छूने की हिम्मत मत करना!",
                sampleDialogueEn = "I will never forgive you... Anyone who harms my friends pays the ultimate price!",
                recommendedPitchDelta = -0.06f
            ),
            AnimeEmotionOption(
                id = "Sorrow",
                emoji = "😢",
                nameEn = "Sorrow & Tears",
                nameHi = "शोक व गहरी उदासी",
                defaultEyeExpression = "Teary Emotional",
                sampleDialogueJp = "もう誰も失いたくない…私の力じゃ足りないの…？",
                sampleDialogueHi = "मैं अब किसी और को खोना नहीं चाहता... क्या मेरी ताकत इतनी कम है...?",
                sampleDialogueEn = "I don't want to lose anyone ever again... Is my power really not enough...?",
                recommendedPitchDelta = -0.04f
            ),
            AnimeEmotionOption(
                id = "Tsundere",
                emoji = "💢",
                nameEn = "Tsundere Pout",
                nameHi = "त्सूनदेरे व तेवर",
                defaultEyeExpression = "Tsundere Glare",
                sampleDialogueJp = "べ、別にアンタのために助けたんじゃないんだからね！勘違いしないで！",
                sampleDialogueHi = "ब-बेवकूफ! मैंने तुम्हारी मदद इसलिए नहीं की कि तुम मुझे पसंद हो! गलत मत समझना!",
                sampleDialogueEn = "B-Baka! It's not like I saved you because I like you or anything! Don't get the wrong idea!",
                recommendedPitchDelta = 0.12f
            ),
            AnimeEmotionOption(
                id = "CalmSensei",
                emoji = "🧘",
                nameEn = "Calm Sensei",
                nameHi = "शांत व परिपक्व गुरु",
                defaultEyeExpression = "Soft Kawaii Smile",
                sampleDialogueJp = "風の流れを感じろ。心静かに、刀を抜け。",
                sampleDialogueHi = "हवा के शांत प्रवाह को महसूस करो। मन को स्थिर करके ही तलवार उठाओ।",
                sampleDialogueEn = "Feel the silent whisper of the wind. With a tranquil heart, draw your blade.",
                recommendedPitchDelta = -0.12f
            ),
            AnimeEmotionOption(
                id = "Mysterious",
                emoji = "🌙",
                nameEn = "Mysterious Smirk",
                nameHi = "रहस्यमयी फुसफुसाहट",
                defaultEyeExpression = "Mysterious Smirk",
                sampleDialogueJp = "闇の深淵で、真実が静かに微笑んでいるよ…",
                sampleDialogueHi = "अंधकार की गहराइयों में, सत्य चुपचाप हमारी प्रतीक्षा कर रहा है...",
                sampleDialogueEn = "In the deep abyss of shadows, the ultimate truth smiles upon us...",
                recommendedPitchDelta = -0.02f
            ),
            AnimeEmotionOption(
                id = "Romantic",
                emoji = "💖",
                nameEn = "Romantic / Sweet",
                nameHi = "रोमांटिक व कोमल",
                defaultEyeExpression = "Soft Kawaii Smile",
                sampleDialogueJp = "ずっとあなたのそばにいたい…約束してくれる？",
                sampleDialogueHi = "मैं हमेशा तुम्हारे साथ रहना चाहती हूँ... क्या तुम मुझसे यह वादा करोगे?",
                sampleDialogueEn = "I want to stay by your side forever... Will you promise me that?",
                recommendedPitchDelta = 0.05f
            )
        )
    }

    var selectedEmotion by remember {
        mutableStateOf(emotions.find { it.id.equals(draft.emotion, ignoreCase = true) } ?: emotions.first())
    }

    var isEyeDropdownOpen by remember { mutableStateOf(false) }
    var emotionalResonance by remember { mutableFloatStateOf(0.75f) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse_aura")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("voice_selector_studio_container")
    ) {
        // --- 1. STUDIO TITLE CARD ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(AnimePink, AnimeCyan, AnimeGold)))
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.radialGradient(listOf(AnimePink, AnimePurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.RecordVoiceOver, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "🎙️ Voice Selector Studio",
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Sync Character, Emotion, Eye Gaze & Dialogue with Live Audition",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text("ROOM DB ACTIVE", color = AnimeGreen, fontSize = 8.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. CHARACTER ROSTER SELECTOR (CHOOSE ACTIVE CHARACTER) ---
        Text(
            text = "1. Select Character to Voice:",
            color = AnimeGold,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(allCharacters) { char ->
                val isSelected = char.id == draft.id || char.name.equals(draft.name, ignoreCase = true)
                Card(
                    modifier = Modifier
                        .width(140.dp)
                        .clickable { viewModel.loadCharacterIntoDraft(char) }
                        .testTag("select_char_${char.id}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AnimePurple.copy(alpha = 0.45f) else AnimeSurface
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.5.dp, if (isSelected) AnimeCyan else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val resId = ResourceHelpers.getDrawableId(context, char.avatarDrawableName)
                        Image(
                            painter = painterResource(id = resId),
                            contentDescription = char.name,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .border(1.dp, AnimeCyan, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = char.name,
                                color = if (isSelected) AnimeCyanLight else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = char.role,
                                color = TextSecondary,
                                fontSize = 9.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 3. EMOTION SELECTOR CHIPS ---
        Text(
            text = "2. Select Character Emotion & Acting Tone:",
            color = AnimePink,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(6.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(emotions) { emo ->
                val isSelected = selectedEmotion.id == emo.id
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        selectedEmotion = emo
                        val syncedDialogue = if (AppLocaleStrings.isHindi(lang)) emo.sampleDialogueHi else emo.sampleDialogueJp
                        val adjustedPitch = (draft.voicePitch + emo.recommendedPitchDelta).coerceIn(0.5f, 1.8f)
                        viewModel.updateCharacterDraft(
                            draft.copy(
                                emotion = emo.id,
                                expression = emo.defaultEyeExpression,
                                sampleDialogue = syncedDialogue,
                                voicePitch = adjustedPitch
                            )
                        )
                    },
                    label = {
                        Text(
                            text = "${emo.emoji} ${emo.nameEn}",
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimePink.copy(alpha = 0.35f),
                        selectedLabelColor = AnimeCyanLight,
                        containerColor = AnimeSurfaceVariant
                    ),
                    border = BorderStroke(1.dp, if (isSelected) AnimePink else TextMuted.copy(alpha = 0.3f)),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("emotion_chip_${emo.id}")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 4. EYE EXPRESSION SYNCED WITH EMOTION (DROPDOWN) ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Visibility, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3. Synced Eye Expression & Gaze:",
                            color = AnimeGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnimeGold.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("SYNCED TO ${selectedEmotion.id.uppercase()}", color = AnimeGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isEyeDropdownOpen = !isEyeDropdownOpen }
                            .testTag("eye_sync_dropdown"),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.6f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "👁️ ${draft.expression}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Icon(
                                imageVector = if (isEyeDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = AnimeGold
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isEyeDropdownOpen,
                        onDismissRequest = { isEyeDropdownOpen = false },
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .background(AnimeSurface)
                            .border(1.dp, AnimeGold.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                    ) {
                        listOf(
                            "Sparkling Wonder" to "⭐ Sparkling Wonder (Joyful & Excited)",
                            "Fierce Determined Stare" to "🔥 Fierce Determined Stare (Battle Fury)",
                            "Teary Emotional" to "😢 Teary Emotional Gaze (Sorrow)",
                            "Tsundere Glare" to "💢 Tsundere Glare (Pouty & Defensive)",
                            "Soft Kawaii Smile" to "✨ Soft Kawaii Gentle Gaze (Calm Sensei)",
                            "Mysterious Smirk" to "🌙 Mysterious Smirk (Shadowy Whispers)",
                            "Heroic Combat Gaze" to "⚔️ Heroic Combat Gaze (Unstoppable)"
                        ).forEach { (exprKey, label) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = label,
                                        color = if (draft.expression == exprKey) AnimeGold else TextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (draft.expression == exprKey) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    viewModel.updateCharacterDraft(draft.copy(expression = exprKey))
                                    isEyeDropdownOpen = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 5. EMOTION DIALOGUE PRESETS & TEXT EDITOR ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = "4. Dialogue Script & Dubbing Line:",
                    color = AnimeCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                // Quick Dialogue Insertion Chips for Current Emotion
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.updateCharacterDraft(draft.copy(sampleDialogue = selectedEmotion.sampleDialogueJp)) },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("🇯🇵 Japanese Anime", color = AnimeCyanLight, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.updateCharacterDraft(draft.copy(sampleDialogue = selectedEmotion.sampleDialogueHi)) },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("🇮🇳 Hindi Dub", color = AnimePink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { viewModel.updateCharacterDraft(draft.copy(sampleDialogue = selectedEmotion.sampleDialogueEn)) },
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("🇬🇧 English Dub", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = draft.sampleDialogue,
                    onValueChange = { newDiag -> viewModel.updateCharacterDraft(draft.copy(sampleDialogue = newDiag)) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("studio_dialogue_field"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    minLines = 2,
                    maxLines = 4
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 6. VOICE TUNING SLIDERS & LIVE PERFORMANCE AUDITION ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "5. Voice Pitch & Speed Sliders:",
                            color = AnimePink,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "Persona: ${draft.voicePersona}",
                        color = AnimeCyanLight,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pitch Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Voice Pitch:", color = TextSecondary, fontSize = 11.sp)
                    Text("${"%.2f".format(draft.voicePitch)}x", color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = draft.voicePitch,
                    onValueChange = { p -> viewModel.updateCharacterDraft(draft.copy(voicePitch = p)) },
                    valueRange = 0.5f..1.8f,
                    colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink),
                    modifier = Modifier.testTag("studio_voice_pitch_slider")
                )

                // Speed Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Speech Tempo / Speed:", color = TextSecondary, fontSize = 11.sp)
                    Text("${"%.2f".format(draft.voiceSpeed)}x", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = draft.voiceSpeed,
                    onValueChange = { s -> viewModel.updateCharacterDraft(draft.copy(voiceSpeed = s)) },
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyan),
                    modifier = Modifier.testTag("studio_voice_speed_slider")
                )

                // Emotional Resonance Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Emotional Tremolo & Reverb:", color = TextSecondary, fontSize = 11.sp)
                    Text("${(emotionalResonance * 100).toInt()}%", color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = emotionalResonance,
                    onValueChange = { emotionalResonance = it },
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AnimeGold, activeTrackColor = AnimeGold),
                    modifier = Modifier.testTag("studio_resonance_slider")
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Live Audio Waveform & Interactive Audition Player
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Animated Character Mouth Lipsync Preview
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AnimePurple.copy(alpha = 0.3f)),
                            contentAlignment = Alignment.Center
                        ) {
                            val resId = ResourceHelpers.getDrawableId(context, draft.avatarDrawableName)
                            Image(
                                painter = painterResource(id = resId),
                                contentDescription = draft.name,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(50.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            )
                            if (isSpeaking) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .padding(bottom = 6.dp)
                                        .size(width = 12.dp, height = (8.dp * mouthOpenAmount).coerceAtLeast(3.dp))
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.Red)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Waveform Visualizer
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isSpeaking) "🎙️ Speaking Dialogue..." else "Ready for Audition",
                                    color = if (isSpeaking) AnimeCyan else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(24.dp)
                            ) {
                                val bars = 24
                                val barWidth = size.width / (bars * 1.5f)
                                for (i in 0 until bars) {
                                    val heightFactor = if (isSpeaking) {
                                        kotlin.math.sin((i.toDouble() + (mouthOpenAmount * 10)) * 0.8).toFloat().coerceAtLeast(0.1f)
                                    } else 0.15f
                                    val barH = size.height * heightFactor
                                    drawRoundRect(
                                        color = if (isSpeaking) AnimeCyan else AnimePurple.copy(alpha = 0.5f),
                                        topLeft = Offset(i * barWidth * 1.5f, (size.height - barH) / 2),
                                        size = androidx.compose.ui.geometry.Size(barWidth, barH),
                                        cornerRadius = androidx.compose.ui.geometry.CornerRadius(4f, 4f)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Audition Button
                        Button(
                            onClick = { viewModel.auditionDraftVoice() },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(42.dp)
                                .testTag("audition_play_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Audition", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // --- 7. SAVE TO LOCAL DATABASE BUTTON ---
        Button(
            onClick = {
                onSaveToDatabase()
                Toast.makeText(context, "✅ '${draft.name}' saved to Local Room Database!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_to_local_db_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "💾 Save Character & Voice to Local Database (Room)",
                color = Color.White,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Saved Characters Roster Panel:
 * Shows all persistent characters stored in Room local database,
 * with options to load, audition, or delete.
 */
@Composable
fun SavedCharactersRosterPanel(
    viewModel: AnimeViewModel,
    characters: List<CharacterProfile>,
    activeDraftId: String,
    onLoadCharacter: (CharacterProfile) -> Unit,
    onDeleteCharacter: (String) -> Unit
) {
    val context = LocalContext.current

    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Saved Characters in Local Database (${characters.size}):",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
        Spacer(modifier = Modifier.height(10.dp))

        if (characters.isEmpty()) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "No custom characters saved yet. Create one above and tap 'Save to Local Database'!",
                    color = TextMuted,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(16.dp)
                )
            }
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                characters.forEach { char ->
                    val isActive = char.id == activeDraftId
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("saved_character_card_${char.id}"),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isActive) AnimePurple.copy(alpha = 0.3f) else AnimeSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, if (isActive) AnimeCyan else TextMuted.copy(alpha = 0.2f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                val resId = ResourceHelpers.getDrawableId(context, char.avatarDrawableName)
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = char.name,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(44.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, AnimeCyan, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = char.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AnimeSurfaceVariant)
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(text = char.role, color = AnimeGold, fontSize = 9.sp)
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "${char.hairStyle} • ${char.eyeColor} • Emotion: ${char.emotion}",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = "\"${char.sampleDialogue}\"",
                                        color = AnimeCyanLight,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(
                                    onClick = { onLoadCharacter(char) },
                                    modifier = Modifier.testTag("load_char_${char.id}")
                                ) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit / Load", tint = AnimeCyan)
                                }

                                IconButton(
                                    onClick = { onDeleteCharacter(char.id) },
                                    modifier = Modifier.testTag("delete_char_${char.id}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AnimePink)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CharacterVoiceCatalog
import com.example.data.model.CharacterVoicePreset
import com.example.data.model.VoiceAgeGroup
import com.example.data.model.VoiceLanguage
import com.example.data.model.VoiceToneWeight
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

/**
 * MultiCharacterVoiceSyncDialog
 * Dialog for selecting, testing, and automatically synchronizing voices for characters:
 * - बच्चा: kid's voice (boy/girl)
 * - एडल्ट पर्सन: adult male voice
 * - एडल्ट लेडीज: adult lady voice
 * - लड़की: teen girl voice
 * - वृद्ध: elderly / senior citizen voice (बूढ़े-बूढ़ी)
 * - भारी आवाज (heavy/deep bass) vs हल्की आवाज (light/soft high-pitch)
 * - Multiple Voice Languages (Hindi, English, Japanese, Korean, Spanish, etc.)
 * - Automatic Character Sync based on character age & tone cues.
 */
@Composable
fun MultiCharacterVoiceSyncDialog(
    viewModel: AnimeViewModel,
    characterName: String = "Hero",
    characterGender: String = "",
    characterRole: String = "",
    selectedPresetId: String? = null,
    onVoiceSelected: (CharacterVoicePreset, VoiceLanguage) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()

    // Automatically detect and sync matching voice preset
    val autoMatchedPreset = remember(characterName, characterGender, characterRole) {
        CharacterVoiceCatalog.autoSyncVoiceForCharacter(characterName, characterGender, characterRole)
    }

    var currentPreset by remember {
        mutableStateOf(
            CharacterVoiceCatalog.allPresets.find { it.id == selectedPresetId } ?: autoMatchedPreset
        )
    }

    var selectedLanguage by remember { mutableStateOf(VoiceLanguage.HINDI) }
    var selectedAgeGroupFilter by remember { mutableStateOf<VoiceAgeGroup?>(null) }
    var selectedWeightFilter by remember { mutableStateOf<VoiceToneWeight?>(null) }

    val filteredPresets = remember(selectedAgeGroupFilter, selectedWeightFilter) {
        CharacterVoiceCatalog.allPresets.filter { preset ->
            val matchAge = selectedAgeGroupFilter == null || preset.ageGroup == selectedAgeGroupFilter
            val matchWeight = selectedWeightFilter == null || preset.toneWeight == selectedWeightFilter
            matchAge && matchWeight
        }
    }

    Dialog(
        onDismissRequest = {
            viewModel.voiceSyncEngine.stop()
            onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("multi_character_voice_sync_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimeGold)))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxHeight()
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(AnimePurple, AnimeCyan))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Mic, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "कैरेक्टर वॉयस व लैंग्वेज सिंक",
                                color = AnimeGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "बच्चा • एडल्ट • लेडीज • लड़की • वृद्ध • भारी व हल्की आवाज",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                    IconButton(
                        onClick = {
                            viewModel.voiceSyncEngine.stop()
                            onDismiss()
                        }
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // AUTO-SYNC NOTICE BANNER
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeGreen.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, AnimeGreen.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Icon(Icons.Default.Sync, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "⚡ ऑटोमैटिक वॉइस सिंक एक्टिव",
                                        color = AnimeGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "(${autoMatchedPreset.ageGroup.displayNameHi})",
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                                Text(
                                    text = "'$characterName' के लिए अनुशंसित: ${autoMatchedPreset.nameHi} (${autoMatchedPreset.toneWeight.displayNameHi})",
                                    color = TextPrimary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Button(
                            onClick = {
                                currentPreset = autoMatchedPreset
                                viewModel.voiceSyncEngine.speakWithPreset(
                                    preset = autoMatchedPreset,
                                    dialogueText = autoMatchedPreset.sampleDialogueHi,
                                    language = selectedLanguage
                                )
                                Toast.makeText(context, "⚡ ऑटो-सिंक वॉयस लागू!", Toast.LENGTH_SHORT).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text("ऑटो सिंक", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Multi-Language Selection Bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "वॉयस लैंग्वेज चुनें (Select Language):",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(VoiceLanguage.values()) { langItem ->
                        val isSel = selectedLanguage == langItem
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) AnimeCyan else AnimeSurfaceVariant)
                                .clickable { selectedLanguage = langItem }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = langItem.displayNameHi,
                                color = if (isSel) Color.Black else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Age & Tone Category Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val ageFilters = listOf(
                        Pair(null, "सभी उम्र"),
                        Pair(VoiceAgeGroup.CHILD, "बच्चा 👶"),
                        Pair(VoiceAgeGroup.TEEN_GIRL, "लड़की 🌸"),
                        Pair(VoiceAgeGroup.ADULT_MALE, "एडल्ट पुरुष 🛡️"),
                        Pair(VoiceAgeGroup.ADULT_LADY, "एडल्ट महिला 👑"),
                        Pair(VoiceAgeGroup.ELDERLY, "वृद्ध 👴")
                    )

                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        items(ageFilters) { (age, label) ->
                            val isSel = selectedAgeGroupFilter == age
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(if (isSel) AnimePurple else AnimeSurfaceVariant)
                                    .clickable { selectedAgeGroupFilter = age }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = label,
                                    color = if (isSel) Color.White else TextMuted,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Tone Weight Filter (भारी vs हल्की आवाज)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val weightFilters = listOf(
                        Pair(null, "सभी आवाजें"),
                        Pair(VoiceToneWeight.HEAVY, "भारी आवाज (Heavy/Bass) ⚡"),
                        Pair(VoiceToneWeight.LIGHT, "हल्की आवाज (Light/Soft) 🕊️")
                    )

                    weightFilters.forEach { (w, label) ->
                        val isSel = selectedWeightFilter == w
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AnimeGold.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                                .border(1.dp, if (isSel) AnimeGold else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { selectedWeightFilter = w }
                            .padding(vertical = 5.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) AnimeGold else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Presets List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredPresets) { preset ->
                        val isSelected = currentPreset.id == preset.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    currentPreset = preset
                                    viewModel.voiceSyncEngine.speakWithPreset(
                                        preset = preset,
                                        dialogueText = if (selectedLanguage == VoiceLanguage.ENGLISH) preset.sampleDialogueEn else preset.sampleDialogueHi,
                                        language = selectedLanguage
                                    )
                                },
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AnimePurple.copy(alpha = 0.25f) else AnimeSurfaceVariant
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AnimeCyan else Color.Transparent
                            )
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(22.dp)
                                                .clip(CircleShape)
                                                .background(if (isSelected) AnimeCyan else AnimeSurface)
                                                .border(1.dp, if (isSelected) AnimeCyan else TextMuted, CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            if (isSelected) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                            }
                                        }
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column {
                                            Text(
                                                text = preset.nameHi,
                                                color = if (isSelected) AnimeCyan else TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "${preset.ageGroup.displayNameHi} • ${preset.toneWeight.displayNameHi}",
                                                color = AnimeGold,
                                                fontSize = 10.sp
                                            )
                                        }
                                    }

                                    // Preview voice button
                                    Button(
                                        onClick = {
                                            currentPreset = preset
                                            viewModel.voiceSyncEngine.speakWithPreset(
                                                preset = preset,
                                                dialogueText = if (selectedLanguage == VoiceLanguage.ENGLISH) preset.sampleDialogueEn else preset.sampleDialogueHi,
                                                language = selectedLanguage
                                            )
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("सुनें", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = preset.descriptionHi,
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = "संवाद: \"${if (selectedLanguage == VoiceLanguage.ENGLISH) preset.sampleDialogueEn else preset.sampleDialogueHi}\"",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    maxLines = 2
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Live Audio Waveform / Lip-Sync indicator when speaking
                if (speakingState.isSpeaking) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeCyan.copy(alpha = 0.15f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "वॉइस चल रही है: ${speakingState.activeVoiceType}...",
                            color = AnimeCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { viewModel.voiceSyncEngine.stop() },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Stop, contentDescription = "Stop", tint = AnimePink, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                }

                // Apply & Save Selection Button
                Button(
                    onClick = {
                        viewModel.voiceSyncEngine.stop()
                        onVoiceSelected(currentPreset, selectedLanguage)
                        Toast.makeText(context, "✅ '${currentPreset.nameHi}' कैरेक्टर के साथ सिंक हो गई!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("apply_character_voice_btn")
                ) {
                    Text(
                        text = "कैरेक्टर के साथ वॉइस सिंक करें व लागू करें",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

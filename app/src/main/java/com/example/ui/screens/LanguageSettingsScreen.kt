package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.storage.DubbingLanguageDataStore
import com.example.data.storage.DubbingLanguageOption
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.components.VoiceMoodFilterSlider
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
 * Language Settings Screen Component
 * Allows users to select their preferred dubbing language (Japanese, Hindi, English, Spanish)
 * for generated audio using an intuitive dropdown menu, persisting the choice in Jetpack DataStore.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSettingsScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val currentDubbingLang by viewModel.preferredDubbingLanguage.collectAsState()
    val currentVoiceMood by viewModel.preferredVoiceMood.collectAsState()
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage

    val languages = DubbingLanguageDataStore.SUPPORTED_DUBBING_LANGUAGES
    val selectedOption = remember(currentDubbingLang) {
        DubbingLanguageDataStore.findByName(currentDubbingLang)
    }

    var isDropdownExpanded by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppLocaleStrings.tr(lang, "Language Settings", "डबिंग भाषा सेटिंग्स"),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier.testTag("language_settings_back_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = AnimeSurface
                )
            )
        },
        containerColor = Color(0xFF0F0F1A),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Header Hero Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(AnimePurple.copy(alpha = 0.35f), AnimeCyan.copy(alpha = 0.25f))
                        )
                    )
                    .border(1.dp, AnimeCyan.copy(alpha = 0.4f), RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(selectedOption.flag, fontSize = 28.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Anime Dubbing Audio", "एनिमे डबिंग ऑडियो"),
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Auto-Synced Dialogue & Character Vocals", "ऑटो-सिंक संवाद व करैक्टर वोकल्स"),
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        // DataStore Persistence Chip
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AnimeGreen.copy(alpha = 0.15f))
                                .border(1.dp, AnimeGreen, RoundedCornerShape(8.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Storage,
                                    contentDescription = null,
                                    tint = AnimeGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = AppLocaleStrings.tr(lang, "DataStore Persisted", "DataStore सुरक्षित"),
                                    color = AnimeGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Choose your preferred audio dubbing language. All character scripts, TTS voice-overs, and lip-sync timings will generate in this target language, saved automatically to on-device DataStore Preferences.",
                            "अपनी पसंदीदा ऑडियो डबिंग भाषा चुनें। सभी करैक्टर संवाद, TTS वॉइसओवर व लिप-सिंक इस भाषा में तैयार होंगे और DataStore Preferences में सुरक्षित रहेंगे।"
                        ),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // DROPDOWN MENU SECTION
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dubbing_language_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, AnimePurple.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Preferred Dubbing Language:", "पसंदीदा डबिंग भाषा:"),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Select from global anime dubbing languages using the dropdown menu below",
                            "नीचे दिए गए ड्रॉपडाउन मेनू से अपनी पसंदीदा डबिंग भाषा चुनें"
                        ),
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Dropdown Trigger Box
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(AnimeSurfaceVariant)
                                .border(1.5.dp, if (isDropdownExpanded) AnimeCyan else AnimePurple.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                                .clickable { isDropdownExpanded = !isDropdownExpanded }
                                .padding(horizontal = 14.dp, vertical = 12.dp)
                                .testTag("language_dropdown_selector"),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(selectedOption.flag, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = selectedOption.name,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = selectedOption.nativeName,
                                        color = AnimeCyan,
                                        fontSize = 11.sp
                                    )
                                }
                            }

                            Icon(
                                imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Toggle Dropdown",
                                tint = AnimeCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }

                        // Dropdown Menu Popover
                        DropdownMenu(
                            expanded = isDropdownExpanded,
                            onDismissRequest = { isDropdownExpanded = false },
                            modifier = Modifier
                                .fillMaxWidth(0.92f)
                                .background(Color(0xFF161626))
                                .border(1.dp, AnimeCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                        ) {
                            languages.forEach { option ->
                                val isSelected = option.name.equals(currentDubbingLang, ignoreCase = true)
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(option.flag, fontSize = 20.sp)
                                                Spacer(modifier = Modifier.width(10.dp))
                                                Column {
                                                    Text(
                                                        text = option.name,
                                                        color = if (isSelected) AnimeGold else TextPrimary,
                                                        fontSize = 13.sp,
                                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                                    )
                                                    Text(
                                                        text = option.nativeName,
                                                        color = TextMuted,
                                                        fontSize = 10.sp
                                                    )
                                                }
                                            }

                                            if (isSelected) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Selected",
                                                    tint = AnimeGold,
                                                    modifier = Modifier.size(18.dp)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        viewModel.setPreferredDubbingLanguage(option.name)
                                        isDropdownExpanded = false
                                        Toast.makeText(
                                            context,
                                            "✓ ${option.name} (DataStore)",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    },
                                    modifier = Modifier.testTag("dropdown_item_${option.name.lowercase()}")
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Switch Chips
                    Text(
                        text = AppLocaleStrings.tr(lang, "QUICK LANGUAGE SHORTCUTS:", "त्वरित भाषा शॉर्टकट:"),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        languages.forEach { option ->
                            val isSelected = option.name.equals(currentDubbingLang, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setPreferredDubbingLanguage(option.name)
                                },
                                label = {
                                    Text(
                                        text = "${option.flag} ${option.name}",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimePurple.copy(alpha = 0.35f),
                                    selectedLabelColor = AnimeGold,
                                    containerColor = AnimeSurfaceVariant
                                ),
                                border = BorderStroke(1.dp, if (isSelected) AnimeGold else Color.Transparent),
                                modifier = Modifier.testTag("quick_chip_${option.name.lowercase()}")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // ACTIVE DUBBING LANGUAGE DETAILS & AUDIO PREVIEW
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("language_details_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AnimeGreen.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Active Dubbing Profile", "सक्रिय डबिंग प्रोफाइल"),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = selectedOption.animeVibe,
                                color = AnimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${AppLocaleStrings.tr(lang, "TTS Code", "TTS कोड")}: ${selectedOption.code}",
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = selectedOption.description,
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Sample dialogue card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AnimeSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "SAMPLE DUBBING DIALOGUE:", "डबिंग संवाद का नमूना:"),
                                color = TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "\"${selectedOption.sampleDialogue}\"",
                                color = AnimeGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                lineHeight = 17.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Audition Dubbing Voice Sample Button
                    Button(
                        onClick = {
                            viewModel.auditionVoiceMood(currentVoiceMood, selectedOption.sampleDialogue)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("audition_dubbing_sample_btn")
                    ) {
                        Icon(
                            imageVector = if (speakingState.isSpeaking) Icons.Default.Hearing else Icons.Default.VolumeUp,
                            contentDescription = "Audition Dubbing",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (speakingState.isSpeaking) {
                                AppLocaleStrings.tr(lang, "Speaking in ${selectedOption.name}...", "${selectedOption.name} में बोल रहा है...")
                            } else {
                                AppLocaleStrings.tr(lang, "Audition ${selectedOption.name} Dubbing Sample", "${selectedOption.name} डबिंग नमूना सुनें")
                            },
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // REAL-TIME VOICE MOOD FILTER SLIDER COMPONENT
            Text(
                text = AppLocaleStrings.tr(lang, "VOICE MOOD FILTER (FOR GENERATED AUDIO):", "वॉइस मूड फ़िल्टर (ऑडियो निर्माण के लिए):"),
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))

            VoiceMoodFilterSlider(
                currentMood = currentVoiceMood,
                onMoodSelected = { newMood ->
                    viewModel.setPreferredVoiceMood(newMood)
                },
                onAuditionMood = { mood ->
                    viewModel.auditionVoiceMood(mood, selectedOption.sampleDialogue)
                },
                isCurrentlyAuditioning = speakingState.isSpeaking,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DataStore Information Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AnimeSurfaceVariant.copy(alpha = 0.5f))
                    .border(1.dp, AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Storage,
                        contentDescription = null,
                        tint = AnimeCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(lang, "DataStore Preference Synced", "DataStore प्राथमिकता समन्वयित"),
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${AppLocaleStrings.tr(lang, "Stored key", "सहेजी गई कुंजी")}: 'preferred_dubbing_language' = '${selectedOption.name}'.",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }
}

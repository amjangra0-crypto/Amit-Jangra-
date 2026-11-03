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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FormatTextdirectionLToR
import androidx.compose.material.icons.filled.FormatTextdirectionRToL
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.localization.AppLocaleStrings
import com.example.localization.GlobalLanguageSystem
import com.example.localization.LanguageInfo
import com.example.localization.TextDirection
import com.example.localization.TranslationLanguageConfig
import com.example.localization.TranslationResult
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
import kotlinx.coroutines.launch

/**
 * Production-ready Global Language Architecture Screen
 * Provides three independent language layers with strict Linguistic Firewall state isolation:
 * 1. Global Language System (BCP-47 registry, native names, scripts, text direction)
 * 2. Default Language System (App UI interface localization)
 * 3. Translation Language System (Source/Target/Auto-detect with real TranslationProvider SPI)
 * 4. Linguistic Firewall (Zero cross-contamination & metadata key preservation)
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
    val coroutineScope = rememberCoroutineScope()

    val languageSystemState by viewModel.languageSystemState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val currentDubbingLang by viewModel.preferredDubbingLanguage.collectAsState()
    val currentVoiceMood by viewModel.preferredVoiceMood.collectAsState()
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()

    val appTag = languageSystemState.defaultLanguage.selectedAppTag
    val activeAppLangInfo = remember(appTag) {
        GlobalLanguageSystem.findInfo(appTag)
            ?: GlobalLanguageSystem.findInfo("en")!!
    }

    val translationConfig = languageSystemState.translationLanguage
    val allLanguages = remember { GlobalLanguageSystem.allLanguages() }

    // Live Translation Workbench state
    var translationInputText by remember { mutableStateOf("Good morning") }
    var translationResult by remember { mutableStateOf<TranslationResult?>(null) }
    var translationErrorMessage by remember { mutableStateOf<String?>(null) }
    var isTranslating by remember { mutableStateOf(false) }

    // Audio dubbing collapsible section
    var showDubbingSection by remember { mutableStateOf(false) }

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
                            text = "Global Language Architecture",
                            fontSize = 16.sp,
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
        containerColor = Color(0xFF0C0C16),
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // HERO BANNER: 3-Layer Architecture & Linguistic Firewall
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(
                        Brush.horizontalGradient(
                            listOf(Color(0xFF1E1035), Color(0xFF0F223D))
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
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(AnimePurple.copy(alpha = 0.3f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = AnimeCyan,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Linguistic Firewall Active",
                                    color = TextPrimary,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "3 Independent Layers • State Isolation Guaranteed",
                                    color = AnimeCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }

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
                                    text = "DataStore Persisted",
                                    color = AnimeGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "Enforces strict isolation between App UI locale and Translation locale. Changing translation settings will NEVER change the app interface language, and changing app language will NEVER overwrite translation preferences.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================================
            // LAYER 1: DEFAULT LANGUAGE SYSTEM (APP INTERFACE ONLY)
            // =========================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("app_ui_language_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, AnimeCyan.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LAYER 1",
                                        color = AnimeCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Default Language System",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Controls App UI only (Menus, dashboard, buttons, notifications)",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        // Direction badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF1E1E30))
                                .border(1.dp, Color(0xFF33334D), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (activeAppLangInfo.direction == TextDirection.RTL) {
                                        Icons.Default.FormatTextdirectionRToL
                                    } else {
                                        Icons.Default.FormatTextdirectionLToR
                                    },
                                    contentDescription = null,
                                    tint = AnimeGold,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = activeAppLangInfo.direction.name,
                                    color = AnimeGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Active UI Language Summary Pill
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(AnimeSurfaceVariant)
                            .border(1.dp, AnimeCyan.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Active UI Interface Language:",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = activeAppLangInfo.nativeName,
                                        color = AnimeCyan,
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = "(${activeAppLangInfo.englishName})",
                                        color = TextSecondary,
                                        fontSize = 12.sp
                                    )
                                }
                            }

                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "BCP-47 Tag: ${activeAppLangInfo.tag}",
                                    color = AnimeGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Script: ${activeAppLangInfo.script ?: "Standard"}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "SELECT APP UI LANGUAGE (INDEPENDENT):",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Horizontal Chip Selector for all supported BCP-47 languages
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        allLanguages.forEach { langInfo ->
                            val isSelected = langInfo.tag.equals(appTag, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setGlobalAppLanguage(langInfo.tag)
                                    Toast.makeText(
                                        context,
                                        "App UI Language: ${langInfo.englishName} (${langInfo.tag})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                label = {
                                    Text(
                                        text = "${langInfo.nativeName} (${langInfo.tag})",
                                        fontSize = 11.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                    )
                                },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimeCyan.copy(alpha = 0.35f),
                                    selectedLabelColor = AnimeCyan,
                                    containerColor = AnimeSurfaceVariant
                                ),
                                border = BorderStroke(1.dp, if (isSelected) AnimeCyan else Color.Transparent),
                                modifier = Modifier.testTag("app_lang_chip_${langInfo.tag.lowercase()}")
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================================
            // LAYER 2: TRANSLATION LANGUAGE SYSTEM (INDEPENDENT)
            // =========================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("translation_language_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, AnimePurple.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimePurple.copy(alpha = 0.25f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "LAYER 2",
                                        color = AnimePurple,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Translation Language System",
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Text(
                                text = "Source / Target / Auto-detect for content translation",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF221633))
                                .border(1.dp, AnimePurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "Strictly Isolated",
                                color = AnimePurple,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Source & Target Config Summary
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Source Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AnimeSurfaceVariant)
                                .border(1.dp, AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "SOURCE LANGUAGE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = if (translationConfig.autoDetectSource) "Auto-Detect (auto)" else translationConfig.sourceTag,
                                    color = AnimeGold,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Arrow Icon
                        Box(modifier = Modifier.align(Alignment.CenterVertically)) {
                            Icon(
                                imageVector = Icons.Default.SyncAlt,
                                contentDescription = null,
                                tint = AnimeCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        // Target Card
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(AnimeSurfaceVariant)
                                .border(1.dp, AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(text = "TARGET LANGUAGE", color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                val targetInfo = GlobalLanguageSystem.findInfo(translationConfig.targetTag)
                                Text(
                                    text = "${targetInfo?.nativeName ?: translationConfig.targetTag} (${translationConfig.targetTag})",
                                    color = AnimeCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Auto-Detect Toggle Chip
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        FilterChip(
                            selected = translationConfig.autoDetectSource,
                            onClick = {
                                viewModel.setTranslationSource(
                                    sourceTag = TranslationLanguageConfig.AUTO_DETECT,
                                    autoDetect = !translationConfig.autoDetectSource
                                )
                            },
                            label = {
                                Text(
                                    text = "Auto-Detect Source Language (auto)",
                                    fontSize = 11.sp,
                                    fontWeight = if (translationConfig.autoDetectSource) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnimeGold.copy(alpha = 0.3f),
                                selectedLabelColor = AnimeGold,
                                containerColor = AnimeSurfaceVariant
                            ),
                            border = BorderStroke(1.dp, if (translationConfig.autoDetectSource) AnimeGold else Color.Transparent)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Target Language Selector
                    Text(
                        text = "SELECT TRANSLATION TARGET LANGUAGE:",
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
                        allLanguages.forEach { langInfo ->
                            val isSelected = langInfo.tag.equals(translationConfig.targetTag, ignoreCase = true)
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setTranslationTarget(langInfo.tag)
                                    Toast.makeText(
                                        context,
                                        "Target: ${langInfo.englishName} (${langInfo.tag})",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                label = {
                                    Text(
                                        text = "${langInfo.nativeName} (${langInfo.tag})",
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
                                modifier = Modifier.testTag("target_lang_chip_${langInfo.tag.lowercase()}")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = Color(0xFF26263B))
                    Spacer(modifier = Modifier.height(14.dp))

                    // =========================================================================
                    // LIVE TRANSLATION WORKBENCH (REAL TRANSLATION PROVIDER SPI)
                    // =========================================================================
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Interactive Translation Workbench",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Provider Badge
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF14241B))
                                .border(1.dp, AnimeGreen.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = viewModel.translationService.providerName,
                                color = AnimeGreen,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Test translation between supported pairs. Unknown phrases without API credentials will fail visibly with explicit status instead of fabricated mock text.",
                        color = TextMuted,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = translationInputText,
                        onValueChange = { translationInputText = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("translation_input_field"),
                        label = { Text("Input text to translate") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            unfocusedBorderColor = Color(0xFF33334D),
                            focusedLabelColor = AnimeCyan,
                            cursorColor = AnimeCyan
                        ),
                        singleLine = false,
                        maxLines = 3
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Sample Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        listOf("Good morning", "Anime Video", "Character Studio", "Voice Generate", "Action Animation").forEach { sample ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeSurfaceVariant)
                                    .border(1.dp, Color(0xFF33334D), RoundedCornerShape(6.dp))
                                    .clickable { translationInputText = sample }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = sample, color = TextSecondary, fontSize = 10.sp)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Execute Translation Button
                    Button(
                        onClick = {
                            if (translationInputText.isBlank()) {
                                Toast.makeText(context, "Please enter text to translate", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            isTranslating = true
                            translationResult = null
                            translationErrorMessage = null

                            coroutineScope.launch {
                                try {
                                    val res = viewModel.executeTranslation(translationInputText)
                                    translationResult = res
                                    translationErrorMessage = null
                                } catch (e: Exception) {
                                    translationErrorMessage = e.message ?: "Translation error occurred"
                                    translationResult = null
                                } finally {
                                    isTranslating = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(42.dp)
                            .testTag("execute_translation_btn")
                    ) {
                        if (isTranslating) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = Color.Black,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "Translating via Provider...", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        } else {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = "Translate with Provider SPI", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Translation Result Box
                    AnimatedVisibility(visible = translationResult != null) {
                        translationResult?.let { res ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF13221A))
                                    .border(1.dp, AnimeGreen, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "✓ TRANSLATION SUCCESSFUL",
                                            color = AnimeGreen,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Detected Source: ${res.detectedSourceTag ?: "Explicit"}",
                                            color = AnimeGold,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = res.translatedText,
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }
                    }

                    // Translation Error Box
                    AnimatedVisibility(visible = translationErrorMessage != null) {
                        translationErrorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Color(0xFF261214))
                                    .border(1.dp, AnimePink, RoundedCornerShape(10.dp))
                                    .padding(12.dp)
                            ) {
                                Row(verticalAlignment = Alignment.Top) {
                                    Icon(
                                        imageVector = Icons.Default.Info,
                                        contentDescription = null,
                                        tint = AnimePink,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = "Provider Status Notice:",
                                            color = AnimePink,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(2.dp))
                                        Text(
                                            text = err,
                                            color = TextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================================
            // LAYER 3: LINGUISTIC FIREWALL & METADATA PRESERVATION LAWS
            // =========================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("linguistic_firewall_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = null,
                                tint = AnimeGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Linguistic Firewall Laws",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimeGold.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = "UNBREAKABLE", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "1. Purity: 100% target language output. Zero English/Hindi leakage when other languages are active.\n" +
                               "2. Zero Substring Matching: Uses canonical BCP-47 tags without false substring collisions.\n" +
                               "3. State Isolation: App UI Language & Translation System mutate independently.\n" +
                               "4. Metadata Preservation: Internal tokens remain 100% untranslated:",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 15.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Protected metadata pills
                    listOf(
                        "[ACTIVATED_COMPONENTS]",
                        "[TRENDPLUS_FORECAST]",
                        "[CREATIVE_PRODUCTION_BLUEPRINT]",
                        "[FINANCIAL_PAYOUT_ROUTING]"
                    ).forEach { key ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFF141424))
                                .border(1.dp, Color(0xFF2A2A44), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "🔒 $key",
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // =========================================================================
            // COLLAPSIBLE SECTION: ANIME DUBBING AUDIO ENGINE (PRESERVED)
            // =========================================================================
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("dubbing_audio_collapsible_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Color(0xFF26263B))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { showDubbingSection = !showDubbingSection },
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.VolumeUp,
                                contentDescription = null,
                                tint = AnimeGreen,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = "Anime Dubbing & Voice Mood Filter",
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "TTS vocal synthesis & character lip-sync engine",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = if (showDubbingSection) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Toggle Dubbing",
                            tint = TextMuted
                        )
                    }

                    AnimatedVisibility(visible = showDubbingSection) {
                        Column {
                            Spacer(modifier = Modifier.height(12.dp))
                            val dubbingOption = remember(currentDubbingLang) {
                                DubbingLanguageDataStore.findByName(currentDubbingLang)
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "Active Dubbing: ${dubbingOption.flag} ${dubbingOption.name}",
                                    color = AnimeGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Code: ${dubbingOption.code}",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            VoiceMoodFilterSlider(
                                currentMood = currentVoiceMood,
                                onMoodSelected = { newMood ->
                                    viewModel.setPreferredVoiceMood(newMood)
                                },
                                onAuditionMood = { mood ->
                                    viewModel.auditionVoiceMood(mood, dubbingOption.sampleDialogue)
                                },
                                isCurrentlyAuditioning = speakingState.isSpeaking,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}

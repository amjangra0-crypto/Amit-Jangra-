package com.example.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Unified Studio Creation Hub (Master Generative Engine)
 * Combines all connected generation tools into ONE single cohesive frame:
 * 1. Story & Prompt Generator (Text Prompt / Web Link / Image Scanner)
 * 2. AI Visual Synthesizer (Visual Keyframe & Scene Generator)
 * 3. AI Autonomous Director & Scheduler System
 * 4. Creative Production Workflows (Video, Voice, Slides, Design)
 */
@Composable
fun UnifiedStudioCreationHub(
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var selectedHubTab by remember { mutableIntStateOf(0) }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.setUploadedImageUri(uri.toString(), uri.lastPathSegment)
        }
    }

    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importScriptFromUri(uri)
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("unified_studio_creation_hub_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(
            1.5.dp,
            Brush.horizontalGradient(
                listOf(
                    MaterialTheme.colorScheme.primary,
                    AnimeCyan,
                    AnimePurple
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Master Hub Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.2f))
                            .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(
                                state.selectedLanguage,
                                "AI Creation & Automation Hub",
                                "एआई क्रिएशन व ऑटोमेशन हब"
                            ),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = AppLocaleStrings.tr(
                                state.selectedLanguage,
                                "Prompts, Scanners, Visual Synthesizer & Automation",
                                "प्रॉम्प्ट्स, स्कैनर्स, विजुअल सिंथेसाइज़र व ऑटोमेशन"
                            ),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 11.sp
                        )
                    }
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Text(
                        text = "4-in-1 HUB",
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Navigation Tabs inside the Unified Frame
            val hubTabs = listOf(
                Pair(Icons.Default.TextFields, AppLocaleStrings.tr(state.selectedLanguage, "Story & Prompt", "कहानी व प्रॉम्प्ट")),
                Pair(Icons.Default.Palette, AppLocaleStrings.tr(state.selectedLanguage, "Visual Synthesizer", "विजुअल सिंथेसाइज़र")),
                Pair(Icons.Default.Speed, AppLocaleStrings.tr(state.selectedLanguage, "AI Automation", "एआई ऑटोमेशन")),
                Pair(Icons.Default.ViewCarousel, AppLocaleStrings.tr(state.selectedLanguage, "Workflows", "वर्कफ़्लो"))
            )

            ScrollableTabRow(
                selectedTabIndex = selectedHubTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                edgePadding = 6.dp,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedHubTab]),
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .testTag("unified_hub_tab_row")
            ) {
                hubTabs.forEachIndexed { index, (icon, label) ->
                    val isSelected = selectedHubTab == index
                    Tab(
                        selected = isSelected,
                        onClick = { selectedHubTab = index },
                        text = {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        icon = {
                            Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                        },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Content
            when (selectedHubTab) {
                0 -> {
                    // TAB 1: STORY & PROMPT GENERATOR (Prompt, Web Link, Image Scanner)
                    Column {
                        // Sub-Mode Switcher Pills
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            val inputModes = listOf(
                                Triple(0, Icons.Default.TextFields, AppLocaleStrings.get("input_mode_prompt", state.selectedLanguage)),
                                Triple(1, Icons.Default.Link, AppLocaleStrings.get("input_mode_web", state.selectedLanguage)),
                                Triple(2, Icons.Default.AddPhotoAlternate, AppLocaleStrings.get("input_mode_image", state.selectedLanguage))
                            )
                            inputModes.forEach { (mode, icon, label) ->
                                val isSelected = state.selectedInputMode == mode
                                FilterChip(
                                    selected = isSelected,
                                    onClick = { viewModel.setInputMode(mode) },
                                    label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                                    leadingIcon = {
                                        Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(14.dp))
                                    },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.25f),
                                        selectedLabelColor = MaterialTheme.colorScheme.primary
                                    ),
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        when (state.selectedInputMode) {
                            0 -> {
                                // Text Prompt Mode
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = AppLocaleStrings.get("story_prompt_label", state.selectedLanguage),
                                        color = TextSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        MicVoiceInputButton(
                                            language = state.selectedLanguage,
                                            onSpeechResult = { spoken ->
                                                val current = state.promptInput
                                                viewModel.setPromptInput(if (current.isBlank()) spoken else "$current $spoken")
                                            },
                                            testTag = "unified_story_prompt_mic_btn"
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        TextButton(
                                            onClick = { documentPickerLauncher.launch("*/*") },
                                            modifier = Modifier.testTag("unified_upload_story_prompt_btn")
                                        ) {
                                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                                            Spacer(modifier = Modifier.width(4.dp))
                                            Text(AppLocaleStrings.get("quick_upload_file", state.selectedLanguage), color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = state.promptInput,
                                    onValueChange = { viewModel.setPromptInput(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(115.dp)
                                        .testTag("prompt_input_field"),
                                    placeholder = {
                                        Text(
                                            text = AppLocaleStrings.tr(
                                                state.selectedLanguage,
                                                "Video Generate • Voice Generate • Visual Content • Translation\nDescribe what you want to create...",
                                                "वीडियो जनरेट • वॉयस जनरेट • विजुअल कंटेंट • ट्रांसलेशन\nआप जो बनाना चाहते हैं उसका विवरण दर्ज करें..."
                                            ),
                                            color = TextMuted,
                                            fontSize = 12.sp
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { viewModel.generateAnimeVideo() },
                                            modifier = Modifier.testTag("unified_inline_generate_prompt_icon_btn")
                                        ) {
                                            Icon(Icons.Default.AutoAwesome, contentDescription = "Generate", tint = MaterialTheme.colorScheme.primary)
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = MaterialTheme.colorScheme.primary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { viewModel.generateAnimeVideo() },
                                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("unified_inline_generate_prompt_btn")
                                    ) {
                                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = AppLocaleStrings.tr(state.selectedLanguage, "⚡ Generate Video / Execute", "⚡ वीडियो बनाएं / कमांड चलाएं"),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            1 -> {
                                // Web Link Mode
                                Text(
                                    text = AppLocaleStrings.tr(
                                        state.selectedLanguage,
                                        "Paste story, news or article link:",
                                        "कहानी या आर्टिकल का लिंक पेस्ट करें:"
                                    ),
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = state.linkInput,
                                    onValueChange = { viewModel.setLinkInput(it) },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("link_input_field"),
                                    placeholder = { Text("https://example.com/anime-story", color = TextMuted, fontSize = 12.sp) },
                                    singleLine = true,
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { viewModel.generateAnimeVideo() },
                                            modifier = Modifier.testTag("unified_inline_scan_link_btn")
                                        ) {
                                            Icon(Icons.Default.PlayArrow, contentDescription = "Scan & Generate", tint = AnimeCyan)
                                        }
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AnimeCyan,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 4.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    Button(
                                        onClick = { viewModel.generateAnimeVideo() },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.testTag("unified_inline_generate_link_btn")
                                    ) {
                                        Icon(Icons.Default.Search, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = AppLocaleStrings.tr(state.selectedLanguage, "🔍 Scan Link & Generate Video", "🔍 लिंक स्कैन करें व वीडियो बनाएं"),
                                            color = Color.Black,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = AppLocaleStrings.tr(
                                        state.selectedLanguage,
                                        "💡 AI will scan this link and generate a full anime sequence & video!",
                                        "💡 एआई इस लिंक को स्कैन करके ऑटोमैटिक एक पूरी एनिमे स्क्रिप्ट और वीडियो तैयार करेगा!"
                                    ),
                                    color = AnimeCyan,
                                    fontSize = 11.sp
                                )
                            }
                            2 -> {
                                // Image Scanner Mode
                                Text(
                                    text = AppLocaleStrings.tr(
                                        state.selectedLanguage,
                                        "Upload image or photo from gallery:",
                                        "गैलरी से इमेज या फोटो अपलोड करें:"
                                    ),
                                    color = TextSecondary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                if (state.uploadedImageUri != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(160.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .border(1.dp, AnimePink.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                                    ) {
                                        AsyncImage(
                                            model = state.uploadedImageUri,
                                            contentDescription = "Uploaded Reference",
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                        Row(
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp),
                                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                                        ) {
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.75f),
                                                modifier = Modifier.clickable {
                                                    photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                                }
                                            ) {
                                                Icon(Icons.Default.Edit, contentDescription = "Change", tint = Color.White, modifier = Modifier.padding(5.dp).size(14.dp))
                                            }
                                            Surface(
                                                shape = CircleShape,
                                                color = Color.Black.copy(alpha = 0.75f),
                                                modifier = Modifier.clickable { viewModel.clearUploadedImage() }
                                            ) {
                                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = AnimePink, modifier = Modifier.padding(5.dp).size(14.dp))
                                            }
                                        }
                                        Box(
                                            modifier = Modifier
                                                .align(Alignment.BottomStart)
                                                .fillMaxWidth()
                                                .background(Color.Black.copy(alpha = 0.75f))
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(13.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(
                                                    text = AppLocaleStrings.tr(state.selectedLanguage, "Photo uploaded! • AI Visual Scanner active", "इमेज अपलोड सफल! • एआई स्कैनर सक्रिय"),
                                                    color = Color.White,
                                                    fontSize = 10.sp,
                                                    fontWeight = FontWeight.Bold
                                                )
                                            }
                                        }
                                    }
                                } else {
                                    Card(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                                            .testTag("unified_upload_image_picker_card"),
                                        shape = RoundedCornerShape(12.dp),
                                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.4f))
                                    ) {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AnimePink, modifier = Modifier.size(24.dp))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = AppLocaleStrings.tr(state.selectedLanguage, "Select Photo / Character Image from Gallery", "गैलरी से करैक्टर या सीन की तस्वीर चुनें"),
                                                color = TextPrimary,
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Spacer(modifier = Modifier.height(8.dp))
                                            Button(
                                                onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                                colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                                                shape = RoundedCornerShape(8.dp),
                                                modifier = Modifier.height(32.dp).testTag("unified_select_photo_btn")
                                            ) {
                                                Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text(AppLocaleStrings.tr(state.selectedLanguage, "Choose Photo", "तस्वीर चुनें"), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(6.dp))
                                OutlinedTextField(
                                    value = state.imageInputDescription,
                                    onValueChange = { viewModel.setImageDescription(it) },
                                    modifier = Modifier.fillMaxWidth().testTag("unified_image_input_field"),
                                    placeholder = {
                                        Text(
                                            text = AppLocaleStrings.tr(state.selectedLanguage, "Describe scene: Anime warrior standing under neon cherry blossoms...", "विवरण: नियॉन रोशनी में खड़ी जापानी एनिमे योद्धा..."),
                                            color = TextMuted,
                                            fontSize = 11.sp
                                        )
                                    },
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedBorderColor = AnimePink,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                )
                            }
                        }

                        // Quick Inspiration Suggestions
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "⚡ Quick Capabilities:", "⚡ त्वरित निर्माण क्षमताएं (Capabilities):"),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val suggestions = if (AppLocaleStrings.isHindi(state.selectedLanguage)) {
                                listOf(
                                    "🎬 वीडियो जनरेट (Video Generate)",
                                    "🎙️ वॉयस जनरेट (Voice Generate)",
                                    "🖼️ विजुअल कंटेंट (Visual Content)",
                                    "🌐 ट्रांसलेशन (Translation)"
                                )
                            } else {
                                listOf(
                                    "🎬 Video Generate",
                                    "🎙️ Voice Generate",
                                    "🖼️ Visual Content",
                                    "🌐 Translation"
                                )
                            }
                            items(suggestions) { item ->
                                Surface(
                                    shape = RoundedCornerShape(16.dp),
                                    color = MaterialTheme.colorScheme.surfaceVariant,
                                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)),
                                    modifier = Modifier.clickable {
                                        val current = state.promptInput
                                        viewModel.setPromptInput(if (current.isBlank()) item else "$current • $item")
                                    }
                                ) {
                                    Text(text = item, color = TextPrimary, fontSize = 11.sp, modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp))
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // TAB 2: AI VISUAL CONTENT GENERATOR (Visual Synthesizer)
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "AI Visual Synthesizer (Keyframe Generator)",
                                    "AI विजुअल सिंथेसाइज़र (कीफ्रेम जनरेटर)"
                                ),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = AppLocaleStrings.tr(
                                state.selectedLanguage,
                                "Synthesize cinematic visual keyframes from text, images, or links",
                                "टेक्स्ट, इमेज या लिंक से कस्टम सीन विजुअल्स व कीफ्रेम बनाएं"
                            ),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Source Mode Chips
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val sourceModes = listOf("TEXT_PROMPT" to "✍️ Prompt", "UPLOADED_IMAGE" to "📷 Image", "WEB_LINK" to "🌐 Web Link")
                            sourceModes.forEach { (mode, label) ->
                                val isSel = state.visualSourceMode == mode
                                FilterChip(
                                    selected = isSel,
                                    onClick = { viewModel.setVisualSourceMode(mode) },
                                    label = { Text(label, fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                                        selectedLabelColor = AnimeCyanLight
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = state.visualPromptInput,
                            onValueChange = { viewModel.setVisualPromptInput(it) },
                            placeholder = {
                                Text(
                                    text = AppLocaleStrings.tr(
                                        state.selectedLanguage,
                                        "Visual Content: Enter scene background or visual style...",
                                        "विजुअल कंटेंट: सीन बैकग्राउंड या विजुअल स्टाइल दर्ज करें..."
                                    ),
                                    fontSize = 11.sp
                                )
                            },
                            modifier = Modifier.fillMaxWidth().testTag("unified_visual_prompt_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimeCyan,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            minLines = 2
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                            onClick = { viewModel.generateVisualContent() },
                            modifier = Modifier.fillMaxWidth().testTag("unified_generate_visual_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.85f)),
                            shape = RoundedCornerShape(10.dp),
                            enabled = !state.isGeneratingVisual
                        ) {
                            if (state.isGeneratingVisual) {
                                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(AppLocaleStrings.tr(state.selectedLanguage, "Synthesizing visual...", "विजुअल बन रहा है..."), fontSize = 11.sp)
                            } else {
                                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(AppLocaleStrings.tr(state.selectedLanguage, "✨ Synthesize Scene Keyframe", "✨ सीन कीफ्रेम बनाएं"), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }

                        // Display Latest Generated Visual Card if available
                        state.latestGeneratedVisual?.let { visual ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(10.dp),
                                border = BorderStroke(1.dp, AnimeCyan)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Column {
                                            Text(visual.title, color = AnimeCyanLight, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                            Text("${visual.visualType} • ${visual.artStyle.title}", color = AnimeGold, fontSize = 10.sp)
                                        }
                                        Surface(
                                            shape = RoundedCornerShape(6.dp),
                                            color = AnimePurple.copy(alpha = 0.3f)
                                        ) {
                                            Text(visual.atmosphericEffect, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Button(
                                        onClick = { viewModel.applyVisualToActiveScene(visual) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(13.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(AppLocaleStrings.tr(state.selectedLanguage, "Apply to Current Anime Scene", "सक्रिय सीन में लागू करें"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
                2 -> {
                    // TAB 3: AI AUTOMATION & SCHEDULER SYSTEM
                    AutomationDirectorSection(viewModel = viewModel)
                }
                3 -> {
                    // TAB 4: FLIKI-STYLE CREATIVE WORKFLOWS
                    Column {
                        WorkflowSelectorPills(
                            selectedCategory = state.currentWorkflowCategory,
                            onCategorySelected = { viewModel.setWorkflowCategory(it) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        WorkflowInputBox(viewModel = viewModel)
                        Spacer(modifier = Modifier.height(8.dp))
                        WorkflowCardsSection(viewModel = viewModel)
                    }
                }
            }
        }
    }
}

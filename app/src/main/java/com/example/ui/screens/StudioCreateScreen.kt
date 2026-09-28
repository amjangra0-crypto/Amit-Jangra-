package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import com.example.localization.AppLocaleStrings
import com.example.ui.components.MicVoiceInputButton
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import com.example.data.model.AnimeArtStyle
import com.example.data.model.AnimeVisualElement
import com.example.data.model.MotionEffect
import com.example.data.model.ProductionFormat
import com.example.data.model.SubtitleMode
import com.example.data.model.SupportedLanguage
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.TextButton
import coil.compose.AsyncImage
import com.example.ui.AnimeViewModel
import com.example.ui.components.AutomationDirectorSection
import com.example.ui.components.DownloadProjectDialog
import com.example.ui.components.ResourceHelpers
import com.example.ui.components.WorkflowCardsSection
import com.example.ui.components.WorkflowInputBox
import com.example.ui.components.WorkflowInteractiveDialog
import com.example.ui.components.WorkflowSelectorPills
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun StudioCreateScreen(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

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

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(bottom = 80.dp)
    ) {
        // Hero Studio Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            Image(
                painter = painterResource(id = ResourceHelpers.getDrawableId(context, "hero_anime_studio")),
                contentDescription = "Anime Studio Header Banner",
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                        )
                    )
            )
            Column(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(16.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AnimePink)
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "AI GENERATIVE ENGINE",
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AnimePurple.copy(alpha = 0.5f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "VOICE & MUSIC SYNC",
                            color = AnimeCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = AppLocaleStrings.get("studio_header_title", state.selectedLanguage),
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = AppLocaleStrings.get("studio_header_subtitle", state.selectedLanguage),
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick Vibrant Theme Switcher Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "Vibrant Themes:", "वाइब्रेंट थीम्स:"),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(com.example.ui.theme.VibrantThemePresets.all) { preset ->
                    val isSelected = state.vibrantTheme.equals(preset.key, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) preset.primaryColor else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, preset.primaryColor, RoundedCornerShape(8.dp))
                            .clickable { viewModel.setVibrantTheme(preset.key) }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("studio_theme_${preset.key.lowercase()}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = preset.emoji, fontSize = 11.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = preset.nameEn.split(" ").first(),
                                color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 1-Click AI Autonomous Director Console (Command / Link Pipeline)
        AutomationDirectorSection(viewModel = viewModel)

        Spacer(modifier = Modifier.height(10.dp))

        // Fliki AI Workflows Header Selector: Video, Voiceover, Design
        WorkflowSelectorPills(
            selectedCategory = state.currentWorkflowCategory,
            onCategorySelected = { viewModel.setWorkflowCategory(it) }
        )

        // Fliki AI Input Box with dynamic placeholders & quick action icons
        WorkflowInputBox(viewModel = viewModel)

        // Fliki AI Workflow Cards Section (Thumbnail, Social, Presentation, Music, Video, etc.)
        WorkflowCardsSection(viewModel = viewModel)

        Spacer(modifier = Modifier.height(12.dp))

        // Quick File Action Bar: Upload File & Download Options
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(
                onClick = { documentPickerLauncher.launch("*/*") },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("quick_upload_file_btn"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.7f))
            ) {
                Icon(Icons.Default.FileUpload, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppLocaleStrings.get("quick_upload_file", state.selectedLanguage), color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            OutlinedButton(
                onClick = { viewModel.toggleDownloadDialog(true) },
                modifier = Modifier
                    .weight(1f)
                    .height(40.dp)
                    .testTag("quick_download_project_btn"),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.7f))
            ) {
                Icon(Icons.Default.FileDownload, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(AppLocaleStrings.get("quick_download_project", state.selectedLanguage), color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Input Mode Tabs (Prompt / Web Link / Image Scanner)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            TabRow(
                selectedTabIndex = state.selectedInputMode,
                containerColor = AnimeSurface,
                contentColor = AnimePurple,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[state.selectedInputMode]),
                        color = AnimePurple
                    )
                }
            ) {
                Tab(
                    selected = state.selectedInputMode == 0,
                    onClick = { viewModel.setInputMode(0) },
                    text = { Text(AppLocaleStrings.get("input_mode_prompt", state.selectedLanguage), fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    selectedContentColor = AnimePurple,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = state.selectedInputMode == 1,
                    onClick = { viewModel.setInputMode(1) },
                    text = { Text(AppLocaleStrings.get("input_mode_web", state.selectedLanguage), fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    selectedContentColor = AnimeCyan,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = state.selectedInputMode == 2,
                    onClick = { viewModel.setInputMode(2) },
                    text = { Text(AppLocaleStrings.get("input_mode_image", state.selectedLanguage), fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    selectedContentColor = AnimePink,
                    unselectedContentColor = TextMuted
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                when (state.selectedInputMode) {
                    0 -> {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppLocaleStrings.get("story_prompt_label", state.selectedLanguage),
                                color = TextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                MicVoiceInputButton(
                                    language = state.selectedLanguage,
                                    onSpeechResult = { spoken ->
                                        val current = state.promptInput
                                        viewModel.setPromptInput(if (current.isBlank()) spoken else "$current $spoken")
                                    },
                                    testTag = "studio_story_prompt_mic_btn"
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                TextButton(
                                    onClick = { documentPickerLauncher.launch("*/*") },
                                    modifier = Modifier.testTag("upload_story_prompt_btn")
                                ) {
                                    Icon(Icons.Default.FileUpload, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(AppLocaleStrings.get("quick_upload_file", state.selectedLanguage), color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = state.promptInput,
                            onValueChange = { viewModel.setPromptInput(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("prompt_input_field"),
                            placeholder = {
                                Text(
                                    text = AppLocaleStrings.tr(
                                        state.selectedLanguage,
                                        "Eg: Two legendary cyber samurai encounter a mythical dragon at the floating shrine...",
                                        "उदा: दो समुराई योद्धा जो एक रहस्यमयी चेरी ब्लॉसम मंदिर में मिलते हैं..."
                                    ),
                                    color = TextMuted,
                                    fontSize = 13.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimePurple,
                                unfocusedBorderColor = AnimeSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                    }
                    1 -> {
                        Text(
                            text = AppLocaleStrings.tr(
                                state.selectedLanguage,
                                "Paste story, news or article link:",
                                "कहानी या आर्टिकल का लिंक पेस्ट करें:"
                            ),
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = state.linkInput,
                            onValueChange = { viewModel.setLinkInput(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("link_input_field"),
                            placeholder = { Text("https://example.com/article-or-anime-story", color = TextMuted, fontSize = 13.sp) },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimeCyan,
                                unfocusedBorderColor = AnimeSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
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
                        Text(
                            text = AppLocaleStrings.tr(
                                state.selectedLanguage,
                                "Upload image or photo from gallery:",
                                "गैलरी से इमेज या फोटो अपलोड करें:"
                            ),
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(8.dp))

                        if (state.uploadedImageUri != null) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .border(1.dp, AnimePink.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            ) {
                                AsyncImage(
                                    model = state.uploadedImageUri,
                                    contentDescription = "Uploaded Anime Reference",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                                // Action overlay on top
                                Row(
                                    modifier = Modifier
                                        .align(Alignment.TopEnd)
                                        .padding(8.dp),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.Black.copy(alpha = 0.75f),
                                        modifier = Modifier.clickable {
                                            photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
                                        }
                                    ) {
                                        Icon(Icons.Default.Edit, contentDescription = "Change", tint = Color.White, modifier = Modifier.padding(6.dp).size(16.dp))
                                    }
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.Black.copy(alpha = 0.75f),
                                        modifier = Modifier.clickable { viewModel.clearUploadedImage() }
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Remove", tint = AnimePink, modifier = Modifier.padding(6.dp).size(16.dp))
                                    }
                                }
                                // Bottom confirmation banner
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomStart)
                                        .fillMaxWidth()
                                        .background(Color.Black.copy(alpha = 0.75f))
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = AppLocaleStrings.tr(
                                                state.selectedLanguage,
                                                "Image uploaded successfully! • AI Analysis active",
                                                "इमेज सफलतापूर्वक अपलोड हो गई! • एआई एनालिसिस सक्रिय"
                                            ),
                                            color = Color.White,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        } else {
                            // Upload Picker Button / Card
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) }
                                    .testTag("upload_image_picker_card"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.5f)),
                                border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.4f))
                            ) {
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(AnimePink.copy(alpha = 0.15f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = AnimePink, modifier = Modifier.size(22.dp))
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = AppLocaleStrings.tr(state.selectedLanguage, "Upload Photo / Image", "फोटो / इमेज अपलोड करें"),
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Text(
                                        text = AppLocaleStrings.tr(state.selectedLanguage, "Select character or scene photo from gallery (PNG, JPG, WebP)", "गैलरी से करैक्टर या सीन की तस्वीर चुनें (PNG, JPG, WebP)"),
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Button(
                                        onClick = { photoPickerLauncher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                                        shape = RoundedCornerShape(8.dp),
                                        modifier = Modifier.height(34.dp).testTag("select_photo_btn")
                                    ) {
                                        Icon(Icons.Default.AddPhotoAlternate, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = AppLocaleStrings.tr(state.selectedLanguage, "Choose from Gallery", "गैलरी से इमेज चुनें"),
                                            color = Color.White,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "Image description or AI suggestion:", "इमेज का विवरण या एआई सुझाव:"),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = state.imageInputDescription,
                            onValueChange = { viewModel.setImageDescription(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("image_input_field"),
                            placeholder = {
                                Text(
                                    text = AppLocaleStrings.tr(state.selectedLanguage, "A Japanese anime warrior standing in neon rain", "नियॉन रोशनी में खड़ी एक जापानी एनिमे योद्धा की तस्वीर"),
                                    color = TextMuted,
                                    fontSize = 12.sp
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimePink,
                                unfocusedBorderColor = AnimeSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Inspiration Suggestions
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = AppLocaleStrings.tr(state.selectedLanguage, "⚡ Quick Inspiration (Tap to Try):", "⚡ त्वरित प्रेरणा (Quick Prompts):"),
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val suggestions = if (AppLocaleStrings.isHindi(state.selectedLanguage)) {
                    listOf(
                        "🌸 चेरी ब्लॉसम जादुई मंदिर की दास्तान",
                        "🏙️ नियो टोक्यो साइबरपंक समुराई",
                        "🐾 पोपो और प्यारे कार्टून स्कूल का रहस्य",
                        "🔥 ड्रैगन योद्धा का महासंग्राम"
                    )
                } else {
                    listOf(
                        "🌸 Legend of the Magical Cherry Blossom Temple",
                        "🏙️ Neo Tokyo Cyberpunk Shinobi Duel",
                        "🐾 Popo and the Mystical Cartoon School Adventure",
                        "🔥 Awakening of the Legendary Dragon Warrior"
                    )
                }
                items(suggestions) { item ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AnimeSurfaceVariant)
                            .border(1.dp, AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(20.dp))
                            .clickable { viewModel.setPromptInput(item) }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(text = item, color = TextPrimary, fontSize = 12.sp)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Production Format Selector (Short vs Long Animated Video, Movie, Web Series)
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Movie, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "🎬 Production Format:", "🎬 प्रॉडक्शन फॉर्मेट:"),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = AppLocaleStrings.tr(state.selectedLanguage, "Choose short reels, full episode, manhwa web series or movie:", "शॉर्ट रील्स, फुल एपिसोड, मन्हवा वेब सीरीज़ या मूवी चुनें:"),
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(ProductionFormat.values()) { format ->
                    val isSelected = state.selectedProductionFormat == format
                    Card(
                        modifier = Modifier
                            .width(185.dp)
                            .clickable { viewModel.setProductionFormat(format) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AnimePink.copy(alpha = 0.2f) else AnimeSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AnimePink else AnimeSurfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${format.icon} ${format.subtitle}",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AnimeGold
                                )
                                Spacer(modifier = Modifier.weight(1f))
                                Text(
                                    text = "${format.sceneCount} Scenes",
                                    fontSize = 10.sp,
                                    color = AnimeCyanLight
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = format.title,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(3.dp))
                            Text(
                                text = format.description,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Anime Art Style Selector
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Movie, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "Choose Anime / Cartoon / Manhwa Art Style:", "एनिमे / कार्टून / मन्हवा आर्ट स्टाइल चुनें:"),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(AnimeArtStyle.values()) { style ->
                    val isSelected = state.selectedArtStyle == style
                    Card(
                        modifier = Modifier
                            .width(170.dp)
                            .clickable { viewModel.setArtStyle(style) },
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) AnimePurple.copy(alpha = 0.25f) else AnimeSurface
                        ),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) AnimePurple else AnimeSurfaceVariant
                        )
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = style.title,
                                color = if (isSelected) AnimeCyan else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = style.description,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Motion Effects Selector
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "⚡ Anime Motion Effects:", "⚡ एनिमे मोशन इफेक्ट्स (Motion Effects):"),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = AppLocaleStrings.tr(state.selectedLanguage, "Select speed lines, screen shake, shadow aura or manga panel slides:", "स्पीड लाइंस, स्क्रीन शेक, शैडो ओरा या मंगा पैनल स्लाइड चुनें:"),
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(MotionEffect.values()) { effect ->
                    val isSelected = state.selectedMotionEffect == effect
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setMotionEffect(effect) },
                        label = {
                            Text("${effect.emoji} ${effect.title}", fontSize = 11.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                            selectedLabelColor = AnimeCyan,
                            containerColor = AnimeSurface,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Language & Multi-Language Subtitle Controls
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "🌐 Multi-Language Video Studio:", "🌐 बहुभाषी वीडियो निर्माण (Multi-Language Studio):"),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = AppLocaleStrings.tr(state.selectedLanguage, "Choose language for script & voiceover (English, Japanese, Hindi etc.):", "स्क्रिप्ट और वॉइसओवर के लिए भाषा चुनें (Japanese, English, Chinese, Hindi etc.):"),
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SupportedLanguage.values()) { lang ->
                    val isSelected = state.selectedLanguage.equals(lang.displayName, ignoreCase = true)
                    val langLabel = if (AppLocaleStrings.isHindi(state.selectedLanguage)) "${lang.nativeName} (${lang.displayName})" else lang.displayName
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setLanguage(lang.displayName) },
                        label = {
                            Text(langLabel, fontSize = 12.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimeCyan.copy(alpha = 0.2f),
                            selectedLabelColor = AnimeCyan,
                            containerColor = AnimeSurface,
                            labelColor = TextSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Subtitle Mode Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Subtitles, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(AppLocaleStrings.tr(state.selectedLanguage, "Subtitle Display Mode:", "सबटाइटल डिस्प्ले मोड (Subtitle Mode):"), color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SubtitleMode.values().forEach { mode ->
                    val isSel = state.subtitleMode == mode
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.setSubtitleMode(mode) },
                        label = {
                            val labelText = when (mode) {
                                SubtitleMode.TRANSLATED_ONLY -> AppLocaleStrings.tr(state.selectedLanguage, "🌐 Translated Only", "🌐 केवल अनुवाद (Translated Only)")
                                SubtitleMode.BILINGUAL_DUAL -> AppLocaleStrings.tr(state.selectedLanguage, "🎌+🌐 Bilingual Dual", "🎌+🌐 द्विभाषी (Bilingual Dual)")
                                SubtitleMode.ORIGINAL_ONLY -> AppLocaleStrings.tr(state.selectedLanguage, "🎌 Original Only", "🎌 मूल भाषा (Original)")
                            }
                            Text(labelText, fontSize = 11.sp)
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimePurple.copy(alpha = 0.35f),
                            selectedLabelColor = AnimeGold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Voice Accent & Dubbing Cadence Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(AppLocaleStrings.tr(state.selectedLanguage, "Voice Accent & Dubbing Cadence:", "डबिंग लहज़ा व एक्सेंट (Voice Accent & Dubbing):"), color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val accents = listOf(
                    "Hindi Dub (Heroic Bollywood Anime)",
                    "Korean Dramatic (Manhwa Style)",
                    "Tokyo Standard Anime (Japanese Cadence)",
                    "American Heroic Action",
                    "British Aristocrat / Royal",
                    "Spanish Passionate Dub",
                    "French Sophisticated Anime"
                )
                items(accents) { accent ->
                    val isSel = state.selectedVoiceAccent.equals(accent, ignoreCase = true)
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.setVoiceAccent(accent) },
                        label = { Text(accent, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimePink.copy(alpha = 0.25f),
                            selectedLabelColor = AnimePink,
                            containerColor = AnimeSurface,
                            labelColor = TextSecondary
                        )
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Dedicated AI Visual Content Generator Card
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimePink)))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Palette, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "🎨 AI Visual Content Generator (Visual Synthesizer)",
                                    "🎨 AI विजुअल कंटेंट जनरेटर (Visual Element Synthesizer)"
                                ),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Create custom scene visuals & keyframes from images, links, or text",
                                    "इमेज, वेब लिंक या टेक्स्ट से कस्टम सीन विजुअल्स व कीफ्रेम बनाएं"
                                ),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Source Mode Switcher
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        val sourceModes = if (AppLocaleStrings.isHindi(state.selectedLanguage)) {
                            listOf("TEXT_PROMPT" to "✍️ प्रॉम्प्ट", "UPLOADED_IMAGE" to "📷 इमेज / फोटो", "WEB_LINK" to "🌐 वेब लिंक")
                        } else {
                            listOf("TEXT_PROMPT" to "✍️ Prompt", "UPLOADED_IMAGE" to "📷 Image / Photo", "WEB_LINK" to "🌐 Web Link")
                        }
                        sourceModes.forEach { (mode, label) ->
                            val isSel = state.visualSourceMode == mode
                            FilterChip(
                                selected = isSel,
                                onClick = { viewModel.setVisualSourceMode(mode) },
                                label = { Text(label, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                                    selectedLabelColor = AnimeCyanLight
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = state.visualPromptInput,
                        onValueChange = { viewModel.setVisualPromptInput(it) },
                        placeholder = {
                            val hint = when (state.visualSourceMode) {
                                "UPLOADED_IMAGE" -> AppLocaleStrings.tr(state.selectedLanguage, "Enter image description or visual art style...", "इमेज का विवरण दर्ज करें या विजुअल स्टाइल बताएं...")
                                "WEB_LINK" -> AppLocaleStrings.tr(state.selectedLanguage, "Website URL or article link (eg: https://anime-news.jp/art)...", "वेबसाइट URL या आर्टिकल लिंक (उदा: https://anime-news.jp/art)...")
                                else -> AppLocaleStrings.tr(state.selectedLanguage, "Visual scene prompt (eg: Floating Sakura Shrine in Neo Tokyo neon rain)...", "विजुअल सीन प्रॉम्प्ट (उदा: Floating Sakura Shrine in Neo Tokyo neon rain)...")
                            }
                            Text(hint)
                        },
                        modifier = Modifier.fillMaxWidth().testTag("visual_prompt_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        minLines = 2
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Button(
                        onClick = { viewModel.generateVisualContent() },
                        modifier = Modifier.fillMaxWidth().testTag("generate_visual_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(10.dp),
                        enabled = !state.isGeneratingVisual
                    ) {
                        if (state.isGeneratingVisual) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(AppLocaleStrings.tr(state.selectedLanguage, "AI synthesizing visual keyframe...", "AI विजुअल कीफ्रेम बना रहा है..."), fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppLocaleStrings.tr(state.selectedLanguage, "✨ Generate AI Visual Content", "✨ AI विजुअल कंटेंट जनरेट करें"), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }

                    // Display Latest Generated Visual Card if available
                    state.latestGeneratedVisual?.let { visual ->
                        Spacer(modifier = Modifier.height(14.dp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AnimeCyan)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(visual.title, color = AnimeCyanLight, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        Text("${visual.visualType} • ${visual.artStyle.title}", color = AnimeGold, fontSize = 10.sp)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(AnimePurple.copy(alpha = 0.3f))
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Text(visual.atmosphericEffect, color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(visual.promptDescription, color = TextSecondary, fontSize = 11.sp, maxLines = 2)

                                Spacer(modifier = Modifier.height(10.dp))
                                Button(
                                    onClick = { viewModel.applyVisualToActiveScene(visual) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(AppLocaleStrings.tr(state.selectedLanguage, "Apply to Current Anime Scene", "सक्रिय एनिमे सीन में लागू करें (Apply to Current Scene)"), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Loading Generation Step Card
        AnimatedVisibility(visible = state.isGenerating) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AnimePurple)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(28.dp),
                        color = AnimeCyan,
                        strokeWidth = 3.dp
                    )
                    Spacer(modifier = Modifier.width(14.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "AI video generation in progress...", "एआई वीडियो निर्माण प्रगति पर है..."),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = state.generationStep,
                            color = AnimeCyanLight,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Generate Action Button
        Button(
            onClick = { viewModel.generateAnimeVideo() },
            enabled = !state.isGenerating,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(54.dp)
                .testTag("generate_anime_button"),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = AnimePurple,
                disabledContainerColor = AnimePurple.copy(alpha = 0.4f)
            )
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (state.isGenerating) AppLocaleStrings.get("generating_video", state.selectedLanguage) else AppLocaleStrings.get("generate_video_btn", state.selectedLanguage),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Status or Success Toast
        if (state.statusMessage.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(AnimePurple.copy(alpha = 0.15f))
                    .border(1.dp, AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Text(text = state.statusMessage, color = AnimeCyanLight, fontSize = 12.sp)
            }
        }
    }

    // Download Project Dialog
    if (state.showDownloadDialog) {
        DownloadProjectDialog(
            viewModel = viewModel,
            onDismiss = { viewModel.toggleDownloadDialog(false) }
        )
    }

    // Fliki Workflow Interactive Tool Dialog
    if (state.showWorkflowInteractiveModal) {
        WorkflowInteractiveDialog(viewModel = viewModel)
    }
}

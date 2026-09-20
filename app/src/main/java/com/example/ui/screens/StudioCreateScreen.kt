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
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import com.example.data.model.SubtitleMode
import com.example.data.model.SupportedLanguage
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Subtitles
import com.example.ui.AnimeViewModel
import com.example.ui.components.ResourceHelpers
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
                    text = "Anime & Video Studio",
                    color = TextPrimary,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "टेक्स्ट, लिंक या इमेज से एनिमे वीडियो और स्क्रिप्ट बनाएं",
                    color = TextSecondary,
                    fontSize = 13.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

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
                    text = { Text("टेक्स्ट प्रॉम्प्ट", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.TextFields, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    selectedContentColor = AnimePurple,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = state.selectedInputMode == 1,
                    onClick = { viewModel.setInputMode(1) },
                    text = { Text("वेब लिंक / URL", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Link, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    selectedContentColor = AnimeCyan,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = state.selectedInputMode == 2,
                    onClick = { viewModel.setInputMode(2) },
                    text = { Text("इमेज स्कैनर", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                    icon = { Icon(Icons.Default.Image, contentDescription = null, modifier = Modifier.size(18.dp)) },
                    selectedContentColor = AnimePink,
                    unselectedContentColor = TextMuted
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                when (state.selectedInputMode) {
                    0 -> {
                        Text(
                            text = "कहानी या सीन का विवरण लिखें:",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = state.promptInput,
                            onValueChange = { viewModel.setPromptInput(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .testTag("prompt_input_field"),
                            placeholder = {
                                Text(
                                    "उदा: दो समुराई योद्धा जो एक रहस्यमयी चेरी ब्लॉसम मंदिर में मिलते हैं...",
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
                            text = "कहानी या आर्टिकल का लिंक पेस्ट करें:",
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
                            text = "💡 एआई इस लिंक को स्कैन करके ऑटोमैटिक एक पूरी एनिमे स्क्रिप्ट और वीडियो तैयार करेगा!",
                            color = AnimeCyan,
                            fontSize = 11.sp
                        )
                    }
                    2 -> {
                        Text(
                            text = "इमेज स्कैन या दृश्य का विजुअल विवरण:",
                            color = TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = state.imageInputDescription,
                            onValueChange = { viewModel.setImageDescription(it) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("image_input_field"),
                            placeholder = { Text("नियॉन रोशनी में खड़ी एक जापानी एनिमे योद्धा की तस्वीर", color = TextMuted, fontSize = 13.sp) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimePink,
                                unfocusedBorderColor = AnimeSurfaceVariant,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "📷 इमेज के आधार पर एनिमे करैक्टर और विजुअल बैकग्राउंड ऑटोमैटिक सिंक्रोनाइज होंगे!",
                            color = AnimePink,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Inspiration Suggestions
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(
                text = "⚡ त्वरित प्रेरणा (Quick Prompts):",
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                val suggestions = listOf(
                    "🌸 चेरी ब्लॉसम जादुई मंदिर की दास्तान",
                    "🏙️ नियो टोक्यो साइबरपंक समुराई",
                    "🐾 पोपो और प्यारे कार्टून स्कूल का रहस्य",
                    "🔥 ड्रैगन योद्धा का महासंग्राम"
                )
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

        // Anime Art Style Selector
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Movie, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "एनिमे / कार्टून आर्ट स्टाइल चुनें:",
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

        // Language & Multi-Language Subtitle Controls
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🌐 बहुभाषी वीडियो निर्माण (Multi-Language Studio):",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "स्क्रिप्ट और वॉइसओवर के लिए भाषा चुनें (Japanese, English, Chinese, Hindi etc.):",
                color = TextSecondary,
                fontSize = 11.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(SupportedLanguage.values()) { lang ->
                    val isSelected = state.selectedLanguage.equals(lang.displayName, ignoreCase = true)
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setLanguage(lang.displayName) },
                        label = {
                            Text("${lang.nativeName} (${lang.displayName})", fontSize = 12.sp)
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
                Text("सबटाइटल डिस्प्ले मोड (Subtitle Mode):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
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
                                SubtitleMode.TRANSLATED_ONLY -> "🌐 केवल अनुवाद (Translated Only)"
                                SubtitleMode.BILINGUAL_DUAL -> "🎌+🌐 द्विभाषी (Bilingual Dual)"
                                SubtitleMode.ORIGINAL_ONLY -> "🎌 मूल भाषा (Original)"
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
                                text = "🎨 AI विजुअल कंटेंट जनरेटर (Visual Element Synthesizer)",
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "इमेज, वेब लिंक या टेक्स्ट से कस्टम सीन विजुअल्स व कीफ्रेम बनाएं",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Source Mode Switcher
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        listOf("TEXT_PROMPT" to "✍️ प्रॉम्प्ट", "UPLOADED_IMAGE" to "📷 इमेज / फोटो", "WEB_LINK" to "🌐 वेब लिंक").forEach { (mode, label) ->
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
                                "UPLOADED_IMAGE" -> "इमेज का विवरण दर्ज करें या विजुअल स्टाइल बताएं..."
                                "WEB_LINK" -> "वेबसाइट URL या आर्टिकल लिंक (उदा: https://anime-news.jp/art)..."
                                else -> "विजुअल सीन प्रॉम्प्ट (उदा: Floating Sakura Shrine in Neo Tokyo neon rain)..."
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
                            Text("AI विजुअल कीफ्रेम बना रहा है...", fontSize = 12.sp)
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("✨ AI विजुअल कंटेंट जनरेट करें", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
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
                                    Text("सक्रिय एनिमे सीन में लागू करें (Apply to Current Scene)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
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
                            text = "एआई वीडियो निर्माण प्रगति पर है...",
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
                    text = if (state.isGenerating) "वीडियो तैयार हो रहा है..." else "🎬 एनिमे वीडियो और स्क्रिप्ट बनाएं",
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
}

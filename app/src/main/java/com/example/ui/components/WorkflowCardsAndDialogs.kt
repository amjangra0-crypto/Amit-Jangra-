package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatQuote
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.VolumeUp
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.MusicMood
import com.example.data.model.StudioWorkflow
import com.example.data.model.WorkflowCategory
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Top pill selector matching Fliki AI:
 * [📹 Video] [♪ Voiceover] [🖼️ Design]
 */
@Composable
fun WorkflowSelectorPills(
    selectedCategory: WorkflowCategory,
    onCategorySelected: (WorkflowCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        shape = RoundedCornerShape(28.dp),
        color = AnimeSurface,
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            WorkflowCategory.entries.forEach { category ->
                val isSelected = category == selectedCategory
                val pillBackground by animateColorAsState(
                    targetValue = if (isSelected) {
                        when (category) {
                            WorkflowCategory.VIDEO -> Color(0xFFE53935)
                            WorkflowCategory.VOICEOVER -> Color(0xFF8E24AA)
                            WorkflowCategory.DESIGN -> Color(0xFF0288D1)
                        }
                    } else {
                        Color.Transparent
                    },
                    label = "pill_color"
                )

                Surface(
                    onClick = { onCategorySelected(category) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp)
                        .testTag("workflow_pill_${category.id}"),
                    shape = RoundedCornerShape(24.dp),
                    color = pillBackground
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = category.iconEmoji,
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = category.title,
                            color = if (isSelected) Color.White else TextSecondary,
                            fontSize = 13.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * Input box matching Fliki AI:
 * Text area with dynamic placeholder, quick action icons, and + Create -> button
 */
@Composable
fun WorkflowInputBox(
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()
    var showPromptIdeas by remember { mutableStateOf(false) }

    val placeholderText = when (state.currentWorkflowCategory) {
        WorkflowCategory.VIDEO -> "Enter your video idea, script, or blog link...\nEg: A cyberpunk ninja battle in Neo-Tokyo with glowing katana speedlines and Hindi dubbing"
        WorkflowCategory.VOICEOVER -> "Enter your voiceover idea, script, or blog link...\nEg: Heroic Hindi monologue with emotional background violin score, or full dialogue script"
        WorkflowCategory.DESIGN -> "Describe the design you want to create...\nEg: A bold YouTube thumbnail for an anime battle with dark cosmic background, glowing yellow headline 'THE DRAGON AWAKENS', and surprised facial expression"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.12f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            OutlinedTextField(
                value = state.promptInput,
                onValueChange = { viewModel.setPromptInput(it) },
                placeholder = {
                    Text(
                        text = placeholderText,
                        color = TextMuted,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(105.dp)
                    .testTag("fliki_workflow_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                maxLines = 4
            )

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                thickness = 1.dp,
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (state.currentWorkflowCategory == WorkflowCategory.VIDEO) {
                        IconButton(
                            onClick = { viewModel.setTab(com.example.ui.AppTab.CHARACTERS) },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = "Character Cast",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    MicVoiceInputButton(
                        language = state.selectedLanguage,
                        onSpeechResult = { spoken ->
                            val current = state.promptInput
                            viewModel.setPromptInput(if (current.isBlank()) spoken else "$current $spoken")
                        },
                        modifier = Modifier.size(36.dp),
                        testTag = "workflow_input_mic_btn"
                    )

                    IconButton(
                        onClick = { viewModel.setInputMode(2) },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.AttachFile,
                            contentDescription = "Attach File / Image",
                            tint = TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = { showPromptIdeas = !showPromptIdeas },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            Icons.Default.Lightbulb,
                            contentDescription = "Prompt Suggestions",
                            tint = AnimeGold,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // + Create -> Gradient Button
                Button(
                    onClick = { viewModel.generateAnimeVideo() },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("fliki_create_button"),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AnimePink
                    ),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 0.dp)
                ) {
                    Icon(
                        Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(15.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "+ Create →",
                        color = Color.White,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            AnimatedVisibility(visible = showPromptIdeas) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                        .background(AnimeSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(10.dp)
                ) {
                    Text(
                        text = "💡 ट्रेंडिंग आइडियाज (टैप करें और तुरंत भरें):",
                        color = AnimeGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    val suggestions = when (state.currentWorkflowCategory) {
                        WorkflowCategory.VIDEO -> listOf(
                            "साइबर समुराई और नियॉन ड्रैगन की लड़ाई टोक्यो टॉवर पर",
                            "मंगेशकर स्टाइल में जादूई स्कूल का पहला दिन और रहस्य",
                            "टाइम ट्रेवलर जो अतीत में जाकर अपनी प्रेमिका को बचाता है"
                        )
                        WorkflowCategory.VOICEOVER -> listOf(
                            "वीरतापूर्ण हिंदी डायलॉग: 'हमारी शक्ति हमारे संकल्प में है!'",
                            "शांत मास्टर सेंसई का उपदेश: 'बहते पानी की तरह बनो'",
                            "हाई-एनर्जी बैटल बीजीएम विथ 808 डिस्टॉर्शन बास"
                        )
                        WorkflowCategory.DESIGN -> listOf(
                            "YouTube थंबनेल: 'THE FINAL FORM UNLOCKED' विथ ग्लोइंग आइज",
                            "इंस्टाग्राम रील पोस्टर: नियॉन बारिश में भीगता अकेला समुराई",
                            "3-स्लाइड प्रेजेंटेशन: एनिमे वर्ल्डबिल्डिंग और कैरेक्टर आर्क्स"
                        )
                    }
                    suggestions.forEach { suggestion ->
                        Text(
                            text = "• $suggestion",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    viewModel.setPromptInput(suggestion)
                                    showPromptIdeas = false
                                }
                                .padding(vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

/**
 * Workflow cards grid matching Fliki AI layout
 */
@Composable
fun WorkflowCardsSection(
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.uiState.collectAsState()

    val title = when (state.currentWorkflowCategory) {
        WorkflowCategory.VIDEO -> "Video workflows"
        WorkflowCategory.VOICEOVER -> "Voiceover workflows"
        WorkflowCategory.DESIGN -> "Design workflows"
    }

    val workflows = StudioWorkflow.entries.filter { it.category == state.currentWorkflowCategory }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "${workflows.size} विकल्प उपलब्ध",
                color = AnimeCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Render as pairs in Rows for optimal nested scrolling inside Column
        val chunkedWorkflows = workflows.chunked(2)
        chunkedWorkflows.forEach { rowItems ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                rowItems.forEach { workflow ->
                    Box(modifier = Modifier.weight(1f)) {
                        WorkflowItemCard(
                            workflow = workflow,
                            onClick = {
                                viewModel.selectAndLaunchWorkflow(workflow)
                            }
                        )
                    }
                }
                if (rowItems.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
fun WorkflowItemCard(
    workflow: StudioWorkflow,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val primaryColor = Color(android.graphics.Color.parseColor(workflow.cardColorHex))
    val secondaryColor = Color(android.graphics.Color.parseColor(workflow.secondaryColorHex))

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .testTag("workflow_card_${workflow.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Subtle 3D background gradient banner on top half
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(95.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(
                                primaryColor.copy(alpha = 0.5f),
                                secondaryColor.copy(alpha = 0.2f),
                                Color.Transparent
                            )
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = workflow.title,
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.weight(1f)
                        )

                        if (workflow.badge.isNotEmpty()) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimePink)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = workflow.badge,
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // 3D Visual Preview Orb with emoji
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(primaryColor, secondaryColor)
                                    )
                                )
                                .border(1.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = workflow.iconEmoji,
                                fontSize = 20.sp
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Text(
                            text = workflow.subtitle,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Bottom Action: "Try it now ->" or "Start now ->"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = workflow.actionText,
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(AnimeCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "→",
                            color = AnimeCyan,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Interactive Studio Generator Dialog for the selected workflow
 */
@Composable
fun WorkflowInteractiveDialog(
    viewModel: AnimeViewModel
) {
    val state by viewModel.uiState.collectAsState()
    val workflow = state.activeStudioWorkflow ?: return

    Dialog(onDismissRequest = { viewModel.dismissWorkflowModal() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
                    .padding(18.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = workflow.iconEmoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = workflow.title,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = workflow.subtitle,
                                color = AnimeCyan,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = { viewModel.dismissWorkflowModal() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                HorizontalDivider(color = Color.White.copy(alpha = 0.1f), modifier = Modifier.padding(vertical = 10.dp))

                Text(
                    text = workflow.description,
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Interactive Controls based on workflow type
                when (workflow) {
                    StudioWorkflow.THUMBNAIL -> {
                        ThumbnailGeneratorSection(viewModel = viewModel)
                    }
                    StudioWorkflow.GENERATE_MUSIC -> {
                        MusicGeneratorSection(viewModel = viewModel)
                    }
                    StudioWorkflow.SOCIAL -> {
                        SocialGeneratorSection(viewModel = viewModel)
                    }
                    StudioWorkflow.PRESENTATION, StudioWorkflow.PPT_TO_VIDEO -> {
                        PresentationSlideSection(viewModel = viewModel)
                    }
                    StudioWorkflow.TRANSLATE_VIDEO -> {
                        TranslationStudioSection(viewModel = viewModel)
                    }
                    StudioWorkflow.SCRIPT_TO_AUDIO, StudioWorkflow.BLOG_TO_AUDIO -> {
                        AudioNarrationSection(viewModel = viewModel)
                    }
                    else -> {
                        // General Video or Empty Workflow
                        Column {
                            Text(
                                text = "वर्कफ़्लो प्रॉम्प्ट निर्देश:",
                                color = AnimeGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            OutlinedTextField(
                                value = state.promptInput,
                                onValueChange = { viewModel.setPromptInput(it) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(100.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 12.sp, color = TextPrimary),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCyan,
                                    unfocusedBorderColor = Color.White.copy(alpha = 0.2f)
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { viewModel.dismissWorkflowModal() },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("बंद करें", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            viewModel.applyWorkflowToDirectCreation(workflow)
                        },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePink)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("स्टूडियो में लागू करें", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ----------------------------------------------------------------------------
// SPECIFIC WORKFLOW SECTIONS
// ----------------------------------------------------------------------------

@Composable
fun ThumbnailGeneratorSection(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val thumbnail = state.thumbnailPreviewData

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("🎯 लाइव थंबनेल प्रीव्यू (YouTube & Shorts):", color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Visual Preview Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Black),
            border = BorderStroke(1.dp, AnimeGold)
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                // Background visual simulation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(Color(0xFF1A237E), Color(0xFF311B92), Color(0xFF880E4F))
                            )
                        )
                )

                // Badge top left
                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color.Red)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = thumbnail.badgeLabel, color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.ExtraBold)
                }

                // Text Overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(10.dp)
                ) {
                    Text(
                        text = thumbnail.headlineText,
                        color = Color(0xFFFFEB3B),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        maxLines = 1
                    )
                    Text(
                        text = thumbnail.subtitleText,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = thumbnail.headlineText,
            onValueChange = { viewModel.updateThumbnailConfig(it, thumbnail.subtitleText, thumbnail.badgeLabel, thumbnail.aspectRatio) },
            label = { Text("हेडलाइन टेक्स्ट (Headline)", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeGold, unfocusedBorderColor = Color.White.copy(alpha = 0.2f))
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(
                selected = thumbnail.aspectRatio == "16:9",
                onClick = { viewModel.updateThumbnailConfig(thumbnail.headlineText, thumbnail.subtitleText, thumbnail.badgeLabel, "16:9") },
                label = { Text("16:9 (YouTube Video)") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimeCyan)
            )
            FilterChip(
                selected = thumbnail.aspectRatio == "9:16",
                onClick = { viewModel.updateThumbnailConfig(thumbnail.headlineText, thumbnail.subtitleText, thumbnail.badgeLabel, "9:16") },
                label = { Text("9:16 (Shorts / Reels)") },
                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimePink)
            )
        }
    }
}

@Composable
fun MusicGeneratorSection(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val musicData = state.musicGeneratorData

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("🎵 एआई एनिमे म्यूजिक व साउंडट्रैक सिंथेसाइज़र:", color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        // Soundwave & Player Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(musicData.title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("${musicData.mood.label} • ${musicData.tempoBpm} BPM", color = AnimeCyan, fontSize = 11.sp)
                    }

                    Button(
                        onClick = { viewModel.toggleMusicSynthesizerPreview() },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (musicData.isPlayingPreview) Color.Red else AnimePurple
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(
                            if (musicData.isPlayingPreview) Icons.Default.Pause else Icons.Default.PlayArrow,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (musicData.isPlayingPreview) "रोकें" else "प्ले बीजीएम", fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Simulated active visualizer bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.Bottom
                ) {
                    val heights = if (musicData.isPlayingPreview) listOf(12, 22, 16, 24, 18, 10, 20, 14, 24, 18) else listOf(4, 4, 4, 4, 4, 4, 4, 4, 4, 4)
                    heights.forEach { h ->
                        Box(
                            modifier = Modifier
                                .width(6.dp)
                                .height(h.dp)
                                .clip(RoundedCornerShape(3.dp))
                                .background(if (musicData.isPlayingPreview) AnimeCyan else Color.Gray.copy(alpha = 0.3f))
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("म्यूजिक मूड चुनें:", color = TextSecondary, fontSize = 11.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MusicMood.entries.take(3).forEach { mood ->
                FilterChip(
                    selected = musicData.mood == mood,
                    onClick = { viewModel.updateMusicGeneratorMood(mood) },
                    label = { Text(mood.label, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimeGold)
                )
            }
        }
    }
}

@Composable
fun SocialGeneratorSection(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val social = state.socialPreviewData

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("📱 सोशल मीडिया पोस्टर व रील जनरेटर:", color = AnimeCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Instagram Reels", "YouTube Shorts", "Snapchat Spotlight").forEach { p ->
                FilterChip(
                    selected = social.platform == p,
                    onClick = { viewModel.updateSocialConfig(p, social.caption, social.stickerTag) },
                    label = { Text(p, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimePink)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
            value = social.caption,
            onValueChange = { viewModel.updateSocialConfig(social.platform, it, social.stickerTag) },
            label = { Text("कैप्शन व हैशटैग्स", fontSize = 11.sp) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 3,
            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimePink, unfocusedBorderColor = Color.White.copy(alpha = 0.2f))
        )
    }
}

@Composable
fun PresentationSlideSection(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val slides = state.presentationSlides

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("📊 एनिमे प्रेजेंटेशन व स्लाइड डेक (3 स्लाइड्स):", color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        slides.forEach { slide ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("स्लाइड ${slide.slideNumber}: ${slide.title}", color = AnimeCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(slide.visualTheme, color = TextMuted, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    slide.bulletPoints.forEach { point ->
                        Text("• $point", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun TranslationStudioSection(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val trans = state.translationStudioData

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("🌐 वीडियो व ऑडियो ट्रांसलेशन / डबिंग स्टूडियो:", color = AnimePurple, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("स्रोत भाषा: ${trans.sourceLanguage}", color = TextSecondary, fontSize = 11.sp)
            Text("→ लक्ष्य भाषा: ${trans.targetLanguage}", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            listOf("Japanese", "English", "French", "Spanish", "Korean").forEach { lang ->
                FilterChip(
                    selected = trans.targetLanguage == lang,
                    onClick = { viewModel.updateTranslationConfig(lang, trans.enableDualSubtitles) },
                    label = { Text(lang, fontSize = 10.sp) },
                    colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimePurple)
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("मूल डायलॉग: \"${trans.sampleOriginalLine}\"", color = TextSecondary, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text("अनुवादित डायलॉग: \"${trans.sampleTranslatedLine}\"", color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun AudioNarrationSection(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxWidth()) {
        Text("🎙️ वॉयस एक्टिंग व ऑडियो नरेशन स्टूडियो:", color = AnimePink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Text("डायलॉग स्क्रिप्ट विथ इमोशन कैडेंस:", color = TextMuted, fontSize = 10.sp)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = state.audioNarrationScript,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }
    }
}

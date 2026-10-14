package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import com.example.data.engine.ResearchedTrendItem
import com.example.data.engine.SocialPlatformSource
import com.example.data.engine.TrendHorizon
import com.example.data.engine.TrendPulseAiAgent
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
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
 * OwnerTrendPulseAgentSection
 * Owner-exclusive AI Agent panel that researches and learns from the internet,
 * Google, Instagram, Facebook, and YouTube to create viral anime videos and content.
 * Supports active/deactive toggle, future trend forecasting, and 1-click sync with Studio & Automation.
 */
@Composable
fun OwnerTrendPulseAgentSection(
    viewModel: AnimeViewModel,
    lang: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val agent = remember { TrendPulseAiAgent.getInstance(context) }

    val config by agent.config.collectAsState()
    val trends by agent.researchedTrends.collectAsState()
    val isScanning by agent.isScanning.collectAsState()
    val statusMessage by agent.agentStatusMessage.collectAsState()

    var selectedHorizonFilter by remember { mutableStateOf<TrendHorizon?>(null) } // null = All
    var selectedPlatformFilter by remember { mutableStateOf<SocialPlatformSource?>(null) } // null = All
    var showGuideDialog by remember { mutableStateOf(false) }
    var showStrategyDialog by remember { mutableStateOf(false) }
    var selectedTrendForBreakdown by remember { mutableStateOf<ResearchedTrendItem?>(null) }
    var commandPromptInput by remember { mutableStateOf("") }
    var isGeneratingCommand by remember { mutableStateOf(false) }

    val filteredTrends = remember(trends, selectedHorizonFilter, selectedPlatformFilter) {
        trends.filter { item ->
            val matchesHorizon = selectedHorizonFilter == null || item.horizon == selectedHorizonFilter
            val matchesPlatform = selectedPlatformFilter == null || item.platformSource == selectedPlatformFilter
            matchesHorizon && matchesPlatform
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("owner_trend_pulse_agent_section"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimeGold)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with Master Active / Deactive Switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AnimeCyan, AnimePurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "TrendPulse AI Agent",
                                color = AnimeGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("OWNER ONLY", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Internet & Social Media Research • Current & Future Viral Forecasting",
                                "इंटरनेट व सोशल मीडिया रिसर्च • वर्तमान व भविष्य के वायरल ट्रेंड्स"
                            ),
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Active / Deactive Toggle
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (config.isActive) "ACTIVE" else "DEACTIVE",
                        color = if (config.isActive) AnimeGreen else TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = config.isActive,
                        onCheckedChange = { agent.toggleAgentActive(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = AnimeGreen,
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = AnimeSurfaceVariant
                        ),
                        modifier = Modifier.testTag("trend_agent_active_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Agent Status & Scope Message
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (config.isActive) AnimePurple.copy(alpha = 0.15f) else AnimeSurfaceVariant)
                    .border(1.dp, if (config.isActive) AnimePurple.copy(alpha = 0.4f) else Color.Transparent, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (config.isActive) Icons.Default.CheckCircle else Icons.Default.Info,
                    contentDescription = null,
                    tint = if (config.isActive) AnimeCyan else TextMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusMessage,
                    color = if (config.isActive) AnimeCyan else TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Bar: Scan Internet & Social Media + "How to Work with Other Social Media" Button
            // Action Bar: Scan Internet & Social Media + Strategy ("When, Where & How")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            agent.runLiveInternetSocialScan()
                            Toast.makeText(context, "🌐 Completed Live Social & Future Trend Scan!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = config.isActive && !isScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f).height(42.dp).testTag("trend_agent_scan_btn")
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Researching...", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLocaleStrings.tr(lang, "Live Social Scan", "सोशल मीडिया लाइव स्कैन"),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Button(
                    onClick = { showStrategyDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.2f).height(42.dp).testTag("trend_agent_strategy_btn")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "When, Where & How", "कब, कहाँ व कैसे"),
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Option: How to work with other social media and internet
            OutlinedButton(
                onClick = { showGuideDialog = true },
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.8f)),
                modifier = Modifier.fillMaxWidth().height(40.dp).testTag("trend_agent_guide_btn")
            ) {
                Icon(Icons.Default.HelpOutline, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "How To Work With Social Media & Internet (Guide & APIs)",
                        "सोशल मीडिया व इंटरनेट के साथ काम कैसे करें? (गाइड व API)"
                    ),
                    color = AnimeCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // =========================================================================
            // GENERATE CONTENT & VIDEO ON COMMANDS (कमांड पर वीडियो व कंटेंट बनाएं)
            // =========================================================================
            Card(
                modifier = Modifier.fillMaxWidth().testTag("command_video_generation_card"),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(AnimeCyan.copy(alpha = 0.6f), AnimePurple.copy(alpha = 0.6f))))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .clip(CircleShape)
                                .background(AnimeCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "⚡ Generate Content & Video on Command", "⚡ आदेश व कमांड्स पर तुरंत वीडियो बनाएं"),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Researches live trends & forecasts future concepts to create videos",
                                    "वर्तमान व भविष्य के ट्रेंड्स पर शोध कर स्टूडियो व ऑटोमेशन हेतु वीडियो बनाता है"
                                ),
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedTextField(
                            value = commandPromptInput,
                            onValueChange = { commandPromptInput = it },
                            placeholder = {
                                Text(
                                    text = AppLocaleStrings.tr(lang, "e.g. Create future cyberpunk reel with katana duel...", "उदा. भविष्य के ट्रेंड पर साइबरपंक रील बनाएं..."),
                                    fontSize = 11.sp,
                                    color = TextMuted
                                )
                            },
                            modifier = Modifier.weight(1f).testTag("agent_command_input_field"),
                            shape = RoundedCornerShape(10.dp),
                            singleLine = true,
                            enabled = config.isActive && !isGeneratingCommand,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimeCyan,
                                unfocusedBorderColor = AnimeSurfaceVariant
                            )
                        )

                        Spacer(modifier = Modifier.width(8.dp))

                        Button(
                            onClick = {
                                if (commandPromptInput.isNotBlank()) {
                                    coroutineScope.launch {
                                        isGeneratingCommand = true
                                        val item = agent.generateContentOnCommand(commandPromptInput)
                                        isGeneratingCommand = false
                                        commandPromptInput = ""
                                        Toast.makeText(context, "🎬 Generated: ${item.suggestedVideoTitle.take(28)}...", Toast.LENGTH_SHORT).show()
                                        if (config.autoSyncToStudio) {
                                            viewModel.setPromptInput(item.completeAnimePrompt)
                                        }
                                    }
                                }
                            },
                            enabled = config.isActive && !isGeneratingCommand && commandPromptInput.isNotBlank(),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.height(50.dp).testTag("agent_execute_command_btn")
                        ) {
                            if (isGeneratingCommand) {
                                CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Send, contentDescription = "Execute Command", tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Command Chips
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        val quickCommands = listOf(
                            "🔥 Trending Now Reel",
                            "🔮 Future 30-Day Wave",
                            "⚡ Solo Leveling Shonen",
                            "🇮🇳 Vedic Mecha Sci-Fi",
                            "☕ Cozy Demon King Rom-Com"
                        )
                        items(quickCommands) { cmd ->
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimePurple.copy(alpha = 0.2f))
                                    .border(1.dp, AnimePurple.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .clickable {
                                        commandPromptInput = cmd
                                    }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(text = cmd, color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizon Filter Tabs (Trending Now vs Future Waves)
            Text(
                text = "⏱️ Select Trend Horizon (वर्तमान व भविष्य के ट्रेंड्स):",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val horizons = listOf(
                    Pair(null, "🌐 All Horizons"),
                    Pair(TrendHorizon.TRENDING_NOW, TrendHorizon.TRENDING_NOW.titleEn),
                    Pair(TrendHorizon.NEXT_7_DAYS, TrendHorizon.NEXT_7_DAYS.titleEn),
                    Pair(TrendHorizon.NEXT_30_DAYS, TrendHorizon.NEXT_30_DAYS.titleEn),
                    Pair(TrendHorizon.UPCOMING_SEASONS, TrendHorizon.UPCOMING_SEASONS.titleEn)
                )
                items(horizons) { (h, label) ->
                    val isSelected = selectedHorizonFilter == h
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AnimePurple else AnimeSurfaceVariant)
                            .border(1.dp, if (isSelected) AnimePurple else TextMuted.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                        .clickable { selectedHorizonFilter = h }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Platform Filter Pills (Google, YouTube, Instagram, Facebook, Reddit, Web)
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val platforms = listOf(
                    Pair(null, "All Platforms"),
                    Pair(SocialPlatformSource.GOOGLE_TRENDS, "Google Trends"),
                    Pair(SocialPlatformSource.YOUTUBE_VIRAL, "YouTube"),
                    Pair(SocialPlatformSource.INSTAGRAM_REELS, "Instagram Reels"),
                    Pair(SocialPlatformSource.FACEBOOK_COMMUNITIES, "Facebook"),
                    Pair(SocialPlatformSource.REDDIT_ANIME, "Reddit / X")
                )
                items(platforms) { (p, label) ->
                    val isSelected = selectedPlatformFilter == p
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSelected) AnimeCyan.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                            .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { selectedPlatformFilter = p }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) AnimeCyan else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Trends List
            if (filteredTrends.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No trends matching filter. Tap 'Live Social Scan' to research.", color = TextMuted, fontSize = 11.sp)
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    filteredTrends.forEach { trend ->
                        ResearchedTrendCard(
                            trend = trend,
                            onSendToStudio = {
                                viewModel.setPromptInput(trend.completeAnimePrompt)
                                viewModel.setTab(AppTab.STUDIO)
                                Toast.makeText(context, "🎬 Loaded trend into Studio workspace!", Toast.LENGTH_SHORT).show()
                            },
                            onSyncAutomation = {
                                val ok = agent.syncWithYouTubeAutomation(trend)
                                if (ok) {
                                    Toast.makeText(context, "⚡ Enqueued episode in YouTube Automation!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCopyPrompt = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Trend Anime Prompt", trend.completeAnimePrompt))
                                Toast.makeText(context, "Prompt copied to clipboard!", Toast.LENGTH_SHORT).show()
                            },
                            onViewDeepBreakdown = {
                                selectedTrendForBreakdown = trend
                            }
                        )
                    }
                }
            }
        }
    }

    // Modal: How to Work With Other Social Media & Internet Guide Dialog
    if (showGuideDialog) {
        TrendSocialMediaIntegrationGuideDialog(
            lang = lang,
            onDismiss = { showGuideDialog = false }
        )
    }

    // Modal: Deep Breakdown of selected trend
    selectedTrendForBreakdown?.let { trend ->
        TrendDeepBreakdownDialog(
            trend = trend,
            viewModel = viewModel,
            onDismiss = { selectedTrendForBreakdown = null },
            onSyncAutomation = {
                agent.syncWithYouTubeAutomation(trend)
            }
        )
    }

    // Modal: Owner Strategy Dialog (When, Where & How)
    if (showStrategyDialog) {
        OwnerTrendAgentStrategyDialog(
            agent = agent,
            lang = lang,
            onDismiss = { showStrategyDialog = false }
        )
    }
}

/**
 * Individual Researched Trend Card with direct Studio and Automation synchronization
 */
@Composable
private fun ResearchedTrendCard(
    trend: ResearchedTrendItem,
    onSendToStudio: () -> Unit,
    onSyncAutomation: () -> Unit,
    onCopyPrompt: () -> Unit,
    onViewDeepBreakdown: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(trend.horizon.badgeColorHex).copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Badges Row: Horizon & Virality Score
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(trend.horizon.badgeColorHex).copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = trend.horizon.titleEn,
                            color = Color(trend.horizon.badgeColorHex),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${trend.platformSource.displayName}",
                        color = Color(trend.platformSource.iconColorHex),
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(AnimeGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "🔥 ${trend.viralityScore}% VIRAL",
                        color = AnimeGreen,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Topic Title
            Text(
                text = trend.topicTitle,
                color = AnimeGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(2.dp))

            // Suggested High-CTR Video Title
            Text(
                text = "Suggested Hook: ${trend.suggestedVideoTitle}",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Velocity & Predicted Peak Date
            Text(
                text = "⚡ ${trend.searchVolumeGrowth} • ${trend.predictedPeakWindow}",
                color = AnimeCyan,
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Synopsis snippet
            Text(
                text = trend.scriptSynopsis,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 2,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row: Send to Studio, Sync Automation, Copy Prompt, Deep Breakdown
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = onSendToStudio,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp).weight(1.1f)
                ) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Push to Studio", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onSyncAutomation,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.height(32.dp).weight(1.1f)
                ) {
                    Icon(Icons.Default.Tv, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync Automation", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onCopyPrompt,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Prompt", tint = AnimeCyan, modifier = Modifier.size(16.dp))
                }

                IconButton(
                    onClick = onViewDeepBreakdown,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "View Deep Breakdown", tint = AnimeGold, modifier = Modifier.size(18.dp))
                }
            }
        }
    }
}

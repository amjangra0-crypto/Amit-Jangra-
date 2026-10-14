package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
 * OwnerAiAgentResearchHub
 * Dedicated UI for App Owner inside Owner Dashboard:
 * 1. Displays live feed of trending topics across YouTube, Instagram, and Facebook.
 * 2. Displays 'Future Prediction' score for each topic (forecast probability, peak window, virality).
 * 3. Configuration panel toggling specific research sources and future auto-generate preferences.
 * 4. User Access permissions panel allowing Owner to enable/disable globally and per-user.
 */
@Composable
fun OwnerAiAgentResearchHub(
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

    var selectedPlatformTab by remember { mutableIntStateOf(0) } // 0=All, 1=YouTube, 2=Instagram, 3=Facebook, 4=Future Forecasts
    var showConfigPanel by remember { mutableStateOf(false) }
    var showUserAccessPanel by remember { mutableStateOf(false) }
    var showGuideDialog by remember { mutableStateOf(false) }
    var selectedTrendForBreakdown by remember { mutableStateOf<ResearchedTrendItem?>(null) }

    // Filtered trends based on platform tab and active source toggles
    val displayTrends = remember(trends, selectedPlatformTab, config) {
        trends.filter { item ->
            // Filter by active sources configured by Owner
            val sourceAllowed = when (item.platformSource) {
                SocialPlatformSource.YOUTUBE_VIRAL -> config.enableYouTubeTrends
                SocialPlatformSource.INSTAGRAM_REELS -> config.enableInstagramTrends
                SocialPlatformSource.FACEBOOK_COMMUNITIES -> config.enableFacebookTrends
                SocialPlatformSource.GOOGLE_TRENDS -> config.enableGoogleTrends
                SocialPlatformSource.REDDIT_ANIME, SocialPlatformSource.GLOBAL_WEB -> config.enableRedditAndWebTrends
            }
            if (!sourceAllowed) return@filter false

            when (selectedPlatformTab) {
                1 -> item.platformSource == SocialPlatformSource.YOUTUBE_VIRAL
                2 -> item.platformSource == SocialPlatformSource.INSTAGRAM_REELS
                3 -> item.platformSource == SocialPlatformSource.FACEBOOK_COMMUNITIES
                4 -> item.isFutureForecast || item.horizon != TrendHorizon.TRENDING_NOW
                else -> true
            }
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("owner_ai_agent_research_hub"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(18.dp),
        border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimeGold)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AnimeCyan, AnimePurple))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(24.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "AI Agent Research Hub",
                                color = AnimeGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("LIVE INTEL", color = AnimeGold, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "YouTube, Instagram & Facebook Trending Feeds • Future Prediction Score",
                                "यूट्यूब, इंस्टाग्राम व फेसबुक लाइव ट्रेंड्स • भविष्य की भविष्यवाणी स्कोर"
                            ),
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Master Active / Deactive Switch
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
                        modifier = Modifier.testTag("research_hub_active_switch")
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Research Status Banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(AnimePurple.copy(alpha = 0.15f))
                    .border(1.dp, AnimePurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = statusMessage,
                    color = AnimeCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Toolbar: Scan Live + Configuration Panel + User Permissions + Guide
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            agent.runLiveInternetSocialScan()
                            Toast.makeText(context, "🌐 Completed Live Social & Future Prediction Scan!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = config.isActive && !isScanning,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.3f).height(40.dp).testTag("hub_scan_live_btn")
                ) {
                    if (isScanning) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(15.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Scanning...", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Live Scan", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = { showConfigPanel = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.3f).height(40.dp).testTag("hub_config_panel_btn")
                ) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Config & Sources", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }

                Button(
                    onClick = { showUserAccessPanel = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1.3f).height(40.dp).testTag("hub_user_permissions_btn")
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("User Access", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Platform Filter Tabs: All / YouTube / Instagram / Facebook / Future Waves
            TabRow(
                selectedTabIndex = selectedPlatformTab,
                containerColor = AnimeSurfaceVariant,
                contentColor = AnimeCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedPlatformTab]),
                        color = AnimeCyan
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedPlatformTab == 0,
                    onClick = { selectedPlatformTab = 0 },
                    text = { Text("🌐 All", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPlatformTab == 1,
                    onClick = { selectedPlatformTab = 1 },
                    text = { Text("🔴 YouTube", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPlatformTab == 2,
                    onClick = { selectedPlatformTab = 2 },
                    text = { Text("🟣 Reels", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPlatformTab == 3,
                    onClick = { selectedPlatformTab = 3 },
                    text = { Text("🔵 Meta", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
                Tab(
                    selected = selectedPlatformTab == 4,
                    onClick = { selectedPlatformTab = 4 },
                    text = { Text("🔮 Future", fontSize = 10.sp, fontWeight = FontWeight.Bold) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Active Source Indicators Summary
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Monitored Sources:", color = TextMuted, fontSize = 9.sp)
                SourceBadge("YouTube", config.enableYouTubeTrends, Color(0xFFFF0000))
                SourceBadge("Instagram", config.enableInstagramTrends, Color(0xFFE1306C))
                SourceBadge("Facebook", config.enableFacebookTrends, Color(0xFF1877F2))
                SourceBadge("Google", config.enableGoogleTrends, Color(0xFF4285F4))
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Feed of Researched Topics with Future Prediction Scores
            if (displayTrends.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No trending topics found for selected source. Check Config or tap 'Live Scan'.",
                        color = TextMuted,
                        fontSize = 11.sp
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    displayTrends.forEach { trend ->
                        ResearchHubTrendCard(
                            trend = trend,
                            onSendToStudio = {
                                viewModel.setPromptInput(trend.completeAnimePrompt)
                                viewModel.setTab(AppTab.STUDIO)
                                Toast.makeText(context, "🎬 Loaded trend into Studio workspace!", Toast.LENGTH_SHORT).show()
                            },
                            onSyncAutomation = {
                                val ok = agent.syncWithYouTubeAutomation(trend)
                                if (ok) {
                                    Toast.makeText(context, "⚡ Enqueued into YouTube Automation queue!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            onCopyHook = {
                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                cm.setPrimaryClip(ClipData.newPlainText("Trend Hook", trend.suggestedVideoTitle))
                                Toast.makeText(context, "Hook copied to clipboard!", Toast.LENGTH_SHORT).show()
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

    // Modal 1: Configuration Panel (Source Toggles & Future Auto-Generate Preferences)
    if (showConfigPanel) {
        OwnerAiAgentConfigDialog(
            agent = agent,
            lang = lang,
            onDismiss = { showConfigPanel = false }
        )
    }

    // Modal 2: User Access Permissions Panel (Global & Specific Person Control)
    if (showUserAccessPanel) {
        OwnerAiAgentUserAccessDialog(
            agent = agent,
            lang = lang,
            onDismiss = { showUserAccessPanel = false }
        )
    }

    // Modal 3: Deep Breakdown of selected trend
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
}

/**
 * Small Source Status Badge
 */
@Composable
private fun SourceBadge(title: String, enabled: Boolean, tint: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(if (enabled) tint.copy(alpha = 0.2f) else AnimeSurfaceVariant)
            .border(1.dp, if (enabled) tint.copy(alpha = 0.6f) else TextMuted.copy(alpha = 0.3f), RoundedCornerShape(4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
    ) {
        Text(
            text = "$title: ${if (enabled) "ON" else "OFF"}",
            color = if (enabled) tint else TextMuted,
            fontSize = 8.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

/**
 * ResearchHubTrendCard with Prominent 'Future Prediction' Score
 */
@Composable
private fun ResearchHubTrendCard(
    trend: ResearchedTrendItem,
    onSendToStudio: () -> Unit,
    onSyncAutomation: () -> Unit,
    onCopyHook: () -> Unit,
    onViewDeepBreakdown: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, Color(trend.horizon.badgeColorHex).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Header Row: Platform Badge + FUTURE PREDICTION SCORE
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(Color(trend.platformSource.iconColorHex).copy(alpha = 0.25f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = trend.platformSource.displayName,
                            color = Color(trend.platformSource.iconColorHex),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.width(6.dp))
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
                }

                // PROMINENT FUTURE PREDICTION SCORE BADGE
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AnimePurple, AnimeCyan)
                            )
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(11.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "🔮 Future Prediction: ${trend.futurePredictionScore}%",
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
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

            // High-CTR Suggested Hook
            Text(
                text = "Hook: ${trend.suggestedVideoTitle}",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Growth Velocity & Peak Window
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "⚡ ${trend.searchVolumeGrowth}",
                    color = AnimeCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "⏳ ${trend.predictedPeakWindow}",
                    color = AnimeGreen,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Synopsis
            Text(
                text = trend.scriptSynopsis,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 2,
                lineHeight = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Action Buttons Row
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
                    modifier = Modifier.height(32.dp).weight(1.2f)
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
                    modifier = Modifier.height(32.dp).weight(1.2f)
                ) {
                    Icon(Icons.Default.Tv, contentDescription = null, tint = Color.White, modifier = Modifier.size(13.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Sync Automation", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }

                IconButton(
                    onClick = onCopyHook,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Hook", tint = AnimeCyan, modifier = Modifier.size(15.dp))
                }

                IconButton(
                    onClick = onViewDeepBreakdown,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Breakdown", tint = AnimeGold, modifier = Modifier.size(17.dp))
                }
            }
        }
    }
}

/**
 * OwnerAiAgentConfigDialog
 * Configuration Panel:
 * 1. Toggle specific research sources (YouTube, Instagram, Facebook, Google, Reddit)
 * 2. Set auto-generate preferences for future content (7-day, 30-day drafts, score threshold, language, voice sync)
 */
@Composable
fun OwnerAiAgentConfigDialog(
    agent: TrendPulseAiAgent,
    lang: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val config by agent.config.collectAsState()

    var ytEnabled by remember(config.enableYouTubeTrends) { mutableStateOf(config.enableYouTubeTrends) }
    var instaEnabled by remember(config.enableInstagramTrends) { mutableStateOf(config.enableInstagramTrends) }
    var fbEnabled by remember(config.enableFacebookTrends) { mutableStateOf(config.enableFacebookTrends) }
    var googleEnabled by remember(config.enableGoogleTrends) { mutableStateOf(config.enableGoogleTrends) }
    var redditEnabled by remember(config.enableRedditAndWebTrends) { mutableStateOf(config.enableRedditAndWebTrends) }

    var auto7Day by remember(config.autoGenerate7DayDrafts) { mutableStateOf(config.autoGenerate7DayDrafts) }
    var auto30Day by remember(config.autoGenerate30DayDrafts) { mutableStateOf(config.autoGenerate30DayDrafts) }
    var thresholdScore by remember(config.futurePredictionThreshold) { mutableStateOf(config.futurePredictionThreshold.toFloat()) }
    var selectedLanguage by remember(config.preferredFutureLanguage) { mutableStateOf(config.preferredFutureLanguage) }
    var selectedVoiceSync by remember(config.preferredCharacterVoiceSync) { mutableStateOf(config.preferredCharacterVoiceSync) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("ai_agent_config_panel_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeGold, AnimeCyan)))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AnimeGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Agent Configuration Panel",
                                color = AnimeGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Toggle Research Sources & Future Content Preferences",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // =========================================================================
                // 1. RESEARCH SOURCES TOGGLES (रिसर्च स्रोत नियंत्रण)
                // =========================================================================
                Text(
                    text = "📡 1. Research Sources Toggles (रिसर्च स्रोत नियंत्रण)",
                    color = AnimeCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enable or disable specific social platforms monitored by the AI Agent:",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                SourceToggleCard(
                    title = "YouTube Trends & Viral Shorts",
                    subtitle = "Monitor high CTR hooks, retention and viral sound trends on YouTube",
                    enabled = ytEnabled,
                    color = Color(0xFFFF0000),
                    onToggle = { ytEnabled = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SourceToggleCard(
                    title = "Instagram Reels & Audio Wave",
                    subtitle = "Track viral audio beat drops, aesthetic edits & 60FPS katana trends",
                    enabled = instaEnabled,
                    color = Color(0xFFE1306C),
                    onToggle = { instaEnabled = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SourceToggleCard(
                    title = "Facebook Video & Meta Communities",
                    subtitle = "Research emotional storytelling, lore explainers & high-share clips",
                    enabled = fbEnabled,
                    color = Color(0xFF1877F2),
                    onToggle = { fbEnabled = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SourceToggleCard(
                    title = "Google Trends Search Breakouts",
                    subtitle = "Detect real-time search volume velocity surges (+300% to +800%)",
                    enabled = googleEnabled,
                    color = Color(0xFF4285F4),
                    onToggle = { googleEnabled = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SourceToggleCard(
                    title = "Reddit & Global Anime Web Feeds",
                    subtitle = "Early fandom sentiment, manga chapters & upcoming season leaks",
                    enabled = redditEnabled,
                    color = Color(0xFFFF4500),
                    onToggle = { redditEnabled = it }
                )

                Spacer(modifier = Modifier.height(18.dp))

                // =========================================================================
                // 2. FUTURE CONTENT AUTO-GENERATE PREFERENCES (भविष्य के कंटेंट प्राथमिकताएं)
                // =========================================================================
                Text(
                    text = "🔮 2. Future Content Auto-Generate Preferences",
                    color = AnimePurple,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Control how future predicted trends automatically generate drafts & videos:",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Auto-generate 7-day
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Generate 7-Day Forecast Drafts", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Automatically generate scripts for topics peaking in next 5-7 days", color = TextMuted, fontSize = 10.sp)
                    }
                    Switch(
                        checked = auto7Day,
                        onCheckedChange = { auto7Day = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AnimePurple)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Auto-generate 30-day
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Auto-Generate 30-Day Future Wave Drafts", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text("Prepare early production pipelines for month-ahead mega trends", color = TextMuted, fontSize = 10.sp)
                    }
                    Switch(
                        checked = auto30Day,
                        onCheckedChange = { auto30Day = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AnimeCyan)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Minimum Future Prediction Score Threshold Slider
                Text(
                    text = "Minimum Future Prediction Score Cutoff: ${thresholdScore.toInt()}%",
                    color = AnimeGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Only auto-generate drafts when AI predicts virality >= ${thresholdScore.toInt()}%",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Slider(
                    value = thresholdScore,
                    onValueChange = { thresholdScore = it },
                    valueRange = 75f..98f,
                    steps = 22,
                    colors = SliderDefaults.colors(
                        thumbColor = AnimeGold,
                        activeTrackColor = AnimeGold,
                        inactiveTrackColor = AnimeSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Preferred Future Language
                Text(
                    text = "Output Language Preference:",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                val languages = listOf("Hindi (हिंदी)", "English", "Japanese (日本語)", "Korean", "Spanish")
                LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    items(languages) { langItem ->
                        val isSel = selectedLanguage == langItem
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) AnimeCyan else AnimeSurfaceVariant)
                                .clickable { selectedLanguage = langItem }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = langItem,
                                color = if (isSel) Color.Black else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Preferred Character Voice Sync Mode
                Text(
                    text = "Character Voice Auto-Sync Mode:",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                val voiceModes = listOf(
                    "AUTO_SYNC" to "⚡ Auto-Sync by Character Age & Weight",
                    "KID_VOICE" to "👶 Kid / Child Voice",
                    "ADULT_MALE_HEAVY" to "🛡️ Adult Male (Heavy Bass)",
                    "ADULT_LADY" to "👑 Adult Lady (Sensei)",
                    "TEEN_GIRL_LIGHT" to "🌸 Teen Girl (Light Kawaii)",
                    "ELDERLY" to "👴 Elderly (बूढ़े-बूढ़ी)"
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    voiceModes.forEach { (key, label) ->
                        val isSel = selectedVoiceSync == key
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) AnimePurple.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                                .border(1.dp, if (isSel) AnimePurple else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { selectedVoiceSync = key }
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) AnimeCyan else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Save Configuration Button
                Button(
                    onClick = {
                        agent.toggleResearchSource(SocialPlatformSource.YOUTUBE_VIRAL, ytEnabled)
                        agent.toggleResearchSource(SocialPlatformSource.INSTAGRAM_REELS, instaEnabled)
                        agent.toggleResearchSource(SocialPlatformSource.FACEBOOK_COMMUNITIES, fbEnabled)
                        agent.toggleResearchSource(SocialPlatformSource.GOOGLE_TRENDS, googleEnabled)
                        agent.toggleResearchSource(SocialPlatformSource.REDDIT_ANIME, redditEnabled)

                        agent.updateAutoGeneratePreferences(
                            auto7Day = auto7Day,
                            auto30Day = auto30Day,
                            threshold = thresholdScore.toInt(),
                            language = selectedLanguage,
                            voiceSync = selectedVoiceSync
                        )

                        Toast.makeText(context, "✅ Research Sources & Preferences Saved!", Toast.LENGTH_SHORT).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(46.dp)
                ) {
                    Text("Apply & Save Preferences", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun SourceToggleCard(
    title: String,
    subtitle: String,
    enabled: Boolean,
    color: Color,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AnimeSurfaceVariant)
            .border(1.dp, if (enabled) color.copy(alpha = 0.5f) else Color.Transparent, RoundedCornerShape(10.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = if (enabled) color else TextMuted, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextMuted, fontSize = 10.sp)
        }
        Switch(
            checked = enabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = color)
        )
    }
}

/**
 * OwnerAiAgentUserAccessDialog
 * Permissions Panel:
 * 1. Global toggle: Enable / disable AI Agent for all non-owner users
 * 2. Specific person access: Whitelist or blacklist users by ID/email
 */
@Composable
fun OwnerAiAgentUserAccessDialog(
    agent: TrendPulseAiAgent,
    lang: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val config by agent.config.collectAsState()

    var globalOthersEnabled by remember(config.isGloballyEnabledForUsers) { mutableStateOf(config.isGloballyEnabledForUsers) }
    var newUserIdInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 12.dp)
                .testTag("ai_agent_user_access_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimePurple, AnimeGold)))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
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
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(AnimePurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "AI Agent User Permissions",
                                color = AnimeCyan,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "Global Access & Specific Person Permission Control",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Global All Users Toggle Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (globalOthersEnabled) AnimeGreen.copy(alpha = 0.12f) else AnimePink.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, if (globalOthersEnabled) AnimeGreen else AnimePink)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (globalOthersEnabled) "🟢 Globally ENABLED for All Users" else "🔴 Globally DISABLED for All Users",
                                color = if (globalOthersEnabled) AnimeGreen else AnimePink,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (globalOthersEnabled) {
                                    "All users in the app can access AI Agent trend research & commands"
                                } else {
                                    "Only the App Owner and specifically authorized users can access AI Agent"
                                },
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                        Switch(
                            checked = globalOthersEnabled,
                            onCheckedChange = {
                                globalOthersEnabled = it
                                agent.setGloballyEnabledForUsers(it)
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AnimeGreen,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = AnimeSurfaceVariant
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Specific Person Add Input
                Text(
                    text = "Grant or Revoke Specific Person Access:",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newUserIdInput,
                        onValueChange = { newUserIdInput = it },
                        placeholder = { Text("Enter User ID or Email...", fontSize = 11.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            unfocusedBorderColor = AnimeSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newUserIdInput.isNotBlank()) {
                                agent.setUserAccess(newUserIdInput.trim(), true)
                                Toast.makeText(context, "Granted access to ${newUserIdInput.trim()}", Toast.LENGTH_SHORT).show()
                                newUserIdInput = ""
                            }
                        },
                        enabled = newUserIdInput.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.height(50.dp)
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Add User", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Authorized Users List
                Text(
                    text = "Specifically Authorized Users (${config.authorizedUserIds.size}):",
                    color = AnimeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                if (config.authorizedUserIds.isEmpty()) {
                    Text("No specific users authorized yet.", color = TextMuted, fontSize = 11.sp)
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        config.authorizedUserIds.forEach { uid ->
                            UserAccessRow(
                                userId = uid,
                                isGranted = true,
                                onToggle = { isGranted ->
                                    agent.setUserAccess(uid, isGranted)
                                },
                                onRemove = {
                                    agent.removeUserFromAccessList(uid)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Specifically Blocked Users List
                if (config.blockedUserIds.isNotEmpty()) {
                    Text(
                        text = "Specifically Blocked Users (${config.blockedUserIds.size}):",
                        color = AnimePink,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        config.blockedUserIds.forEach { uid ->
                            UserAccessRow(
                                userId = uid,
                                isGranted = false,
                                onToggle = { isGranted ->
                                    agent.setUserAccess(uid, isGranted)
                                },
                                onRemove = {
                                    agent.removeUserFromAccessList(uid)
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun UserAccessRow(
    userId: String,
    isGranted: Boolean,
    onToggle: (Boolean) -> Unit,
    onRemove: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(AnimeSurfaceVariant)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(userId, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            Text(
                text = if (isGranted) "Status: Authorized ✓" else "Status: Blocked ✗",
                color = if (isGranted) AnimeGreen else AnimePink,
                fontSize = 10.sp
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Switch(
                checked = isGranted,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AnimeGreen)
            )
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AnimePink, modifier = Modifier.size(16.dp))
            }
        }
    }
}

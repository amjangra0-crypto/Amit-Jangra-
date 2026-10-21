package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.WorkspacePremium
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
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AutomationChannelManager
import com.example.data.engine.TrendPulseAiAgent
import com.example.data.model.TrendHorizonAccessTier
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
 * Consolidated Automation Trend Horizon Hub.
 * Consolidates all trending horizon features in one place:
 * 1. AI Trend Research & Virality Spikes
 * 2. Multi-channel Social Media Account Sync (YouTube, Instagram, Facebook)
 * 3. 1-Click Viral Video Generation Pipeline
 * 4. Owner Access & Monetization Controller (Owner decides who uses free vs paid/VIP)
 */
@Composable
fun AutomationTrendHorizonHub(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val channelManager = remember { AutomationChannelManager.getInstance(context) }
    val trendAgent = remember { TrendPulseAiAgent.getInstance(context) }

    val state by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val channels by channelManager.connectedChannels.collectAsState()
    val trendHorizonConfig by channelManager.trendHorizonConfig.collectAsState()
    val activeTrends by trendAgent.researchedTrends.collectAsState()
    val lang = state.selectedLanguage

    val isOwner = currentUser.isOwner
    val isUserVip = currentUser.subscriptionPlan.contains("VIP", ignoreCase = true) || currentUser.subscriptionPlan.contains("PRO", ignoreCase = true) || isOwner

    val isAccessAllowed = when (trendHorizonConfig.accessTier) {
        TrendHorizonAccessTier.FREE_FOR_ALL -> true
        TrendHorizonAccessTier.VIP_PAID_ONLY -> isUserVip
        TrendHorizonAccessTier.CREDITS_PER_USE -> isUserVip || isOwner // fallback to wallet credits
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("automation_trend_horizon_screen")
    ) {
        var selectedCategoryTab by remember { mutableStateOf("ALL") }

        // TOP APP BAR WITH BACK, DONE AND CLOSE (×)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("trend_horizon_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Trend Horizon", "ट्रेंड होराइजन"),
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeGold.copy(alpha = 0.2f))
                                .border(1.dp, AnimeGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("ALL-IN-ONE", color = AnimeGold, fontSize = 8.sp, fontWeight = FontWeight.Black)
                        }
                    }
                    Text(
                        text = "Google Trends • Instagram • AI Command Learning • TrendPulse",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("trend_horizon_done_btn")
                ) {
                    Text("Done", color = AnimeCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("trend_horizon_close_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // HORIZONTAL CATEGORY TABS (Consolidates all options into one organized list)
        val hubCategories = listOf(
            "ALL" to "🌟 All Options",
            "GOOGLE" to "📈 Google Trends",
            "INSTAGRAM" to "📸 Instagram",
            "AI_AGENT" to "🤖 TrendPulse AI",
            "COMMAND_AI" to "🧠 AI Commands",
            "SYNC" to "🔄 Social Sync",
            "OWNER" to "👑 Owner Policy"
        )

        ScrollableTabRow(
            selectedTabIndex = hubCategories.indexOfFirst { it.first == selectedCategoryTab }.coerceAtLeast(0),
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = AnimeCyan,
            edgePadding = 8.dp,
            indicator = { tabPositions ->
                val idx = hubCategories.indexOfFirst { it.first == selectedCategoryTab }.coerceAtLeast(0)
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[idx]),
                    color = AnimeCyan
                )
            },
            modifier = Modifier.clip(RoundedCornerShape(10.dp))
        ) {
            hubCategories.forEach { (catKey, catTitle) ->
                Tab(
                    selected = selectedCategoryTab == catKey,
                    onClick = { selectedCategoryTab = catKey },
                    text = {
                        Text(
                            text = catTitle,
                            fontWeight = if (selectedCategoryTab == catKey) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    },
                    selectedContentColor = AnimeCyan,
                    unselectedContentColor = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // GOOGLE TRENDS RADAR (Option in Trend Horizon)
            if (selectedCategoryTab == "ALL" || selectedCategoryTab == "GOOGLE") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("google_trends_radar_card"),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(AnimeCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Search, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "📈 Google Trends Global Radar",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Live search queries & viral breakout keywords",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val googleQueries = listOf(
                                "Romantic Anime Confession at Sunset" to "+340% Breakout",
                                "Shonen Power Awakening Battle" to "+280% Surge",
                                "Emotional Heartbreak Rain Scene" to "+210% Rising"
                            )

                            googleQueries.forEach { (q, stat) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AnimeSurfaceVariant)
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(q, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text(stat, color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.setPromptInput(q)
                                            viewModel.setTab(AppTab.STUDIO)
                                            Toast.makeText(context, "Loaded into Studio: $q", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Use", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // INSTAGRAM TRENDS & REELS VIRAL RADAR (Option in Trend Horizon)
            if (selectedCategoryTab == "ALL" || selectedCategoryTab == "INSTAGRAM") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("instagram_trends_radar_card"),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(AnimePink.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CameraAlt, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "📸 Instagram Reels Viral Audio & Formats",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Trending audio hooks, pacing, and 9:16 vertical styling",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val instaHooks = listOf(
                                "Lo-Fi Romance Piano #AnimeVibes" to "2.4M Reels using this audio",
                                "Fast Dramatic Transition Beat" to "1.8M Reels this week"
                            )

                            instaHooks.forEach { (hook, sub) ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 4.dp)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AnimeSurfaceVariant)
                                        .padding(horizontal = 10.dp, vertical = 7.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(hook, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                                        Text(sub, color = AnimePink, fontSize = 9.sp)
                                    }
                                    Button(
                                        onClick = {
                                            viewModel.setPromptInput("Anime video synced to $hook")
                                            viewModel.setTab(AppTab.STUDIO)
                                            Toast.makeText(context, "Loaded: $hook", Toast.LENGTH_SHORT).show()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text("Apply", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // AI COMMAND LEARNING HUB (Option in Trend Horizon)
            if (selectedCategoryTab == "ALL" || selectedCategoryTab == "COMMAND_AI") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("ai_command_learning_hub_card"),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(AnimePurple.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Psychology, contentDescription = null, tint = AnimePurple, modifier = Modifier.size(16.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "🧠 AI Command Learning Engine",
                                        color = TextPrimary,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Continuously learns creator style and auto-suggests optimal commands",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Learned Creator Preferences: High-Emotion Dialogue, Dynamic Sakura Petal Wind, Cinematic 4K Shading",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                lineHeight = 14.sp
                            )
                        }
                    }
                }
            }

            // SECTION: OWNER ACCESS & MONETIZATION CONTROLLER (if selected or ALL)
            if (selectedCategoryTab == "ALL" || selectedCategoryTab == "OWNER") {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("owner_trend_access_controller_card"),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.dp, if (isOwner) AnimeGold else AnimePurple.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(34.dp)
                                            .clip(CircleShape)
                                            .background(AnimeGold.copy(alpha = 0.2f)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = if (isOwner) "👑 Owner Trend Horizon Access Controller" else "Access Policy & Tier",
                                            color = if (isOwner) AnimeGold else TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = if (isOwner)
                                                "Owner decides: Who uses for Free vs Who Pays for Trend Horizon"
                                            else
                                                "Policy set by App Owner",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (isAccessAllowed) AnimeGreen.copy(alpha = 0.2f) else AnimePink.copy(alpha = 0.2f))
                                        .padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = if (isAccessAllowed) "ACCESS ACTIVE" else "UPGRADE REQUIRED",
                                        color = if (isAccessAllowed) AnimeGreen else AnimePink,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            if (isOwner) {
                                Text(
                                    text = "Choose Who Can Use Automation Trending Horizon:",
                                    color = TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Spacer(modifier = Modifier.height(6.dp))

                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    TrendHorizonAccessTier.entries.forEach { tier ->
                                        val isSel = trendHorizonConfig.accessTier == tier
                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(if (isSel) AnimeGold.copy(alpha = 0.18f) else AnimeSurfaceVariant)
                                                .border(1.dp, if (isSel) AnimeGold else Color.Transparent, RoundedCornerShape(10.dp))
                                                .clickable {
                                                    channelManager.setTrendHorizonAccessTier(tier)
                                                    Toast.makeText(context, "👑 Policy updated to: ${tier.title}", Toast.LENGTH_SHORT).show()
                                                }
                                                .padding(10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(tier.title, color = if (isSel) AnimeGold else TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                                Text(tier.description, color = TextMuted, fontSize = 9.sp)
                                            }
                                            if (isSel) {
                                                Icon(Icons.Default.Check, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                                            }
                                        }
                                    }
                            }
                        } else {
                            // User View of the Policy
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AnimeSurfaceVariant)
                                    .padding(10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Current Mode: ${trendHorizonConfig.accessTier.title}",
                                        color = TextPrimary,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = trendHorizonConfig.accessTier.description,
                                        color = TextMuted,
                                        fontSize = 9.sp
                                    )
                                }
                            }

                            if (!isAccessAllowed) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.setTab(AppTab.SUBSCRIPTION) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Upgrade to VIP to Unlock Trend Horizon", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 2: MULTI-CHANNEL SOCIAL MEDIA ACCOUNT SYNC
            item {
                Card(
                    modifier = Modifier.fillMaxWidth().testTag("trend_horizon_social_sync_card"),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(34.dp)
                                        .clip(CircleShape)
                                        .background(AnimeCyan.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.CloudSync, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "Synced Social Media Channels (${channels.size})",
                                        color = TextPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Accounts connected to Trend Horizon auto-pipeline",
                                        color = TextMuted,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            OutlinedButton(
                                onClick = { viewModel.setTab(AppTab.SETTINGS) },
                                shape = RoundedCornerShape(8.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                modifier = Modifier.height(28.dp)
                            ) {
                                Text("Manage", color = AnimeCyan, fontSize = 10.sp)
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            channels.take(3).forEach { ch ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(AnimeSurfaceVariant)
                                        .padding(8.dp)
                                ) {
                                    Column {
                                        Text(text = ch.platform.iconEmoji + " " + ch.platform.displayName, color = AnimeCyan, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(text = ch.channelName, color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                                        Text(text = ch.subscriberCount, color = TextMuted, fontSize = 8.sp, maxLines = 1)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // SECTION 3: VIRAL TREND INTELLIGENCE & 1-CLICK PIPELINE
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE HIGH-POTENTIAL VIRAL TRENDS:",
                        color = AnimeGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Text(
                        text = "${activeTrends.size} Discovered",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            items(activeTrends, key = { it.id }) { trend ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("trend_item_card_${trend.id}"),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(0.8.dp, AnimePurple.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = trend.topicTitle,
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🔥 ${trend.viralityScore}% VIRAL",
                                    color = AnimeGold,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = trend.completeAnimePrompt,
                            color = TextSecondary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // 1-Click Pipeline Button
                        Button(
                            onClick = {
                                if (isAccessAllowed) {
                                    viewModel.setPromptInput(trend.completeAnimePrompt)
                                    viewModel.generateAnimeVideo()
                                    viewModel.setTab(AppTab.STUDIO)
                                    Toast.makeText(context, "🎬 1-Click Video Pipeline Started for '${trend.topicTitle}'!", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(context, "🔒 VIP Subscription required to run Trend Horizon pipeline", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().testTag("run_trend_pipeline_btn_${trend.id}")
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "1-Click Trend-to-Video Pipeline",
                                color = Color.Black,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
}

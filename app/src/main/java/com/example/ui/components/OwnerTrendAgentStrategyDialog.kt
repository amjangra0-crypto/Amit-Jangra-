package com.example.ui.components

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.engine.TrendHorizon
import com.example.data.engine.TrendPulseAiAgent
import com.example.localization.AppLocaleStrings
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
 * OwnerTrendAgentStrategyDialog
 * Gives the Owner full strategic control deciding:
 * 1. WHEN to use (Research Frequency & Timing, e.g. Real-time continuous, Daily digest, Future surge alert)
 * 2. WHERE to use (Routing target: Studio workspace, YouTube Automation, Reels/Shorts export queue, Script DB)
 * 3. HOW to use (Operational Mode: Full video pipeline, on-demand commands, future forecasting)
 * 4. Master Active / Deactive toggle
 * 5. Monitored niche keywords
 */
@Composable
fun OwnerTrendAgentStrategyDialog(
    agent: TrendPulseAiAgent,
    lang: String,
    onDismiss: () -> Unit
) {
    val config by agent.config.collectAsState()

    var activeState by remember(config.isActive) { mutableStateOf(config.isActive) }
    var selectedFrequency by remember(config.researchFrequency) { mutableStateOf(config.researchFrequency) }
    var selectedDestination by remember(config.destinationTarget) { mutableStateOf(config.destinationTarget) }
    var selectedMode by remember(config.executionMode) { mutableStateOf(config.executionMode) }
    var selectedHorizon by remember(config.selectedHorizon) { mutableStateOf(config.selectedHorizon) }
    var keywordsInput by remember(config.customMonitoredKeywords) { mutableStateOf(config.customMonitoredKeywords) }
    var autoStudio by remember(config.autoSyncToStudio) { mutableStateOf(config.autoSyncToStudio) }
    var autoAutomation by remember(config.autoSyncToYouTubeAutomation) { mutableStateOf(config.autoSyncToYouTubeAutomation) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("owner_trend_strategy_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeGold, AnimePurple, AnimeCyan)))
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
                                .size(42.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(AnimeGold, AnimePurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = Color.Black, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppLocaleStrings.tr(lang, "AI Agent Usage Strategy", "एआई एजेंट रणनीति व नियंत्रण"),
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
                                    Text("OWNER ONLY", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Decide WHEN, WHERE & HOW the AI Agent works",
                                    "तय करें एआई एजेंट कब, कहाँ और कैसे काम करेगा"
                                ),
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

                // Master Active / Deactive Switch Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (activeState) AnimeGreen.copy(alpha = 0.12f) else AnimePink.copy(alpha = 0.12f)
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, if (activeState) AnimeGreen else AnimePink)
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
                                text = if (activeState) "🟢 AI Agent Status: ACTIVE" else "🔴 AI Agent Status: DEACTIVATED",
                                color = if (activeState) AnimeGreen else AnimePink,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (activeState) {
                                    AppLocaleStrings.tr(
                                        lang,
                                        "Agent researches internet trends & creates viral video content",
                                        "एजेंट इंटरनेट रिसर्च कर ट्रेंडिंग व भविष्य के वीडियो बना रहा है"
                                    )
                                } else {
                                    AppLocaleStrings.tr(
                                        lang,
                                        "Agent is paused. No internet research or automated generation",
                                        "एजेंट रुका हुआ है। कोई इंटरनेट रिसर्च या ऑटोमेशन नहीं होगा"
                                    )
                                },
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = activeState,
                            onCheckedChange = { activeState = it },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = AnimeGreen,
                                uncheckedThumbColor = TextMuted,
                                uncheckedTrackColor = AnimeSurfaceVariant
                            ),
                            modifier = Modifier.testTag("strategy_active_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // =========================================================================
                // 1. WHEN TO USE (कहाँ और कब: Research Schedule & Timing)
                // =========================================================================
                SectionTitle(
                    icon = Icons.Default.AccessTime,
                    title = AppLocaleStrings.tr(lang, "1. WHEN To Use (कब इस्तेमाल करें - Timing & Frequency)", "1. कब इस्तेमाल करें (Timing व Frequency)"),
                    tint = AnimeCyan
                )

                Spacer(modifier = Modifier.height(8.dp))

                val frequencies = listOf(
                    Triple("REALTIME_CONTINUOUS", "Real-Time Continuous (24/7 Monitoring)", "Google Trends, YouTube & Reels के वायरल सिग्नल पर तुरंत रिसर्च"),
                    Triple("DAILY_DIGEST", "Daily Morning Forecast (06:00 AM)", "रोज सुबह आज के और अगले 7 दिनों के टॉप 6 ट्रेंड्स तैयार करना"),
                    Triple("SURGE_ALERTS", "Surge Velocity Triggers (+300% Growth)", "जब किसी टॉपिक का सर्च वॉल्यूम 300% से ऊपर उछले तभी वीडियो बनाएं"),
                    Triple("ON_DEMAND", "On-Demand Only (When Owner Commands)", "केवल तभी जब ओनर खुद बटन दबाए या कमांड दे")
                )

                frequencies.forEach { (key, title, subtitle) ->
                    SelectionOptionCard(
                        selected = selectedFrequency == key,
                        title = title,
                        subtitle = subtitle,
                        onClick = { selectedFrequency = key }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Preferred Trend Horizon
                Text(
                    text = AppLocaleStrings.tr(lang, "Target Trend Horizon Focus:", "ट्रेंड समय सीमा (वर्तमान या भविष्य का फोकस):"),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                val horizons = listOf(
                    TrendHorizon.TRENDING_NOW,
                    TrendHorizon.NEXT_7_DAYS,
                    TrendHorizon.NEXT_30_DAYS,
                    TrendHorizon.UPCOMING_SEASONS
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    horizons.forEach { h ->
                        val isSel = selectedHorizon == h
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSel) Color(h.badgeColorHex) else AnimeSurfaceVariant)
                                .clickable { selectedHorizon = h }
                                .padding(vertical = 8.dp, horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = h.titleEn.take(14),
                                color = if (isSel) Color.White else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                maxLines = 1
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // =========================================================================
                // 2. WHERE TO USE (कहाँ इस्तेमाल करें - Routing Target Destinations)
                // =========================================================================
                SectionTitle(
                    icon = Icons.Default.Movie,
                    title = AppLocaleStrings.tr(lang, "2. WHERE To Use (कहाँ भेजें - Destinations)", "2. कहाँ भेजें (Destinations)"),
                    tint = AnimeGold
                )

                Spacer(modifier = Modifier.height(8.dp))

                val destinations = listOf(
                    Triple("STUDIO_AND_AUTOMATION", "Dual Sync: Studio Workspace + YouTube Automation", "स्टूडियो सीन एडिटर और ऑटोमेशन कतार दोनों में स्वतः भेजें"),
                    Triple("STUDIO_ONLY", "Studio Creator Workspace Only", "केवल स्टूडियो में प्रॉम्प्ट व स्क्रिप्ट लोड करें (Manual Review)"),
                    Triple("AUTOMATION_ONLY", "YouTube Multi-Channel Automation Only", "सीधे यूट्यूब ऑटोमेशन चैनल कतार में शेड्यूल करें"),
                    Triple("SCRIPT_LIBRARY", "Saved Project Scripts Library Only", "रूम डेटाबेस में केवल ट्रेंड स्क्रिप्ट सेव करें")
                )

                destinations.forEach { (key, title, subtitle) ->
                    SelectionOptionCard(
                        selected = selectedDestination == key,
                        title = title,
                        subtitle = subtitle,
                        onClick = { selectedDestination = key }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Auto-Sync Toggles
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-Sync to Studio Workspace:", color = TextSecondary, fontSize = 12.sp)
                    Switch(
                        checked = autoStudio,
                        onCheckedChange = { autoStudio = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = AnimePurple)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Auto-Sync to YouTube Automation Queue:", color = TextSecondary, fontSize = 12.sp)
                    Switch(
                        checked = autoAutomation,
                        onCheckedChange = { autoAutomation = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = Color.White, checkedTrackColor = Color(0xFFFF0000))
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                // =========================================================================
                // 3. HOW TO USE (कैसे काम करे - Execution Mode & Operational Behavior)
                // =========================================================================
                SectionTitle(
                    icon = Icons.Default.Psychology,
                    title = AppLocaleStrings.tr(lang, "3. HOW To Use (कैसे काम करे - Execution Mode)", "3. कैसे काम करे (Execution Mode)"),
                    tint = AnimePurple
                )

                Spacer(modifier = Modifier.height(8.dp))

                val modes = listOf(
                    Triple("FULL_VIDEO_PIPELINE", "Full Autonomous Video Pipeline", "रिसर्च करें, स्क्रिप्ट लिखें, डायलॉग व सीन तय करें और वीडियो बनाएं"),
                    Triple("ON_COMMAND", "Generate on Owner Voice / Text Commands", "ओनर के आदेशों व कमांड्स पर तुरंत वायरल वीडियो और कंटेंट बनाएं"),
                    Triple("FUTURE_FORECASTING", "Future Predictive Forecast Engine (7-90 Days)", "भविष्य के आने वाले ट्रेंड्स का पहले से पता लगाकर कंटेंट तैयार करें"),
                    Triple("HYBRID", "Hybrid Dynamic Multi-Model Mode", "वर्तमान ट्रेंड्स + भविष्य की भविष्यवाणी + कमांड दोनों साथ-साथ")
                )

                modes.forEach { (key, title, subtitle) ->
                    SelectionOptionCard(
                        selected = selectedMode == key,
                        title = title,
                        subtitle = subtitle,
                        onClick = { selectedMode = key }
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Custom Monitored Keywords
                Text(
                    text = AppLocaleStrings.tr(lang, "Monitored Internet & Social Media Keywords:", "निगरानी के कीवर्ड्स (Google, YouTube, Reels):"),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = keywordsInput,
                    onValueChange = { keywordsInput = it },
                    modifier = Modifier.fillMaxWidth().testTag("strategy_keywords_input"),
                    shape = RoundedCornerShape(10.dp),
                    placeholder = { Text("e.g. shonen anime, cyberpunk samurai, hindi anime dub", color = TextMuted) },
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save Changes Button
                Button(
                    onClick = {
                        agent.toggleAgentActive(activeState)
                        agent.updateConfig(
                            selectedHorizon = selectedHorizon,
                            autoSyncToStudio = autoStudio,
                            autoSyncToYouTubeAutomation = autoAutomation,
                            researchFrequency = selectedFrequency,
                            destinationTarget = selectedDestination,
                            executionMode = selectedMode,
                            customKeywords = keywordsInput.trim()
                        )
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_agent_strategy_btn")
                ) {
                    Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Save AI Agent Strategy & Apply", "एआई एजेंट रणनीति सहेजें व लागू करें"),
                        color = Color.Black,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionTitle(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    tint: Color
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = tint, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = tint,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SelectionOptionCard(
    selected: Boolean,
    title: String,
    subtitle: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (selected) AnimePurple.copy(alpha = 0.25f) else AnimeSurfaceVariant
        ),
        border = BorderStroke(
            1.dp,
            if (selected) AnimeCyan else Color.Transparent
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .background(if (selected) AnimeCyan else AnimeSurface)
                    .border(1.dp, if (selected) AnimeCyan else TextMuted, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                if (selected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    color = if (selected) AnimeCyan else TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.SemiBold
                )
                Text(
                    text = subtitle,
                    color = TextSecondary,
                    fontSize = 10.sp,
                    lineHeight = 13.sp
                )
            }
        }
    }
}

package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.engine.ResearchedTrendItem
import com.example.data.engine.TrendHorizon
import com.example.data.engine.TrendPulseAiAgent
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.launch

/**
 * StudioTrendAgentModal
 * Direct bridge inside Studio allowing Owner to:
 * 1. Pull researched current and future trends straight into Studio prompt editor
 * 2. Issue commands to generate customized viral videos on demand
 * 3. Sync seamlessly with AI Automation
 */
@Composable
fun StudioTrendAgentModal(
    viewModel: AnimeViewModel,
    lang: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val agent = remember { TrendPulseAiAgent.getInstance(context) }

    val config by agent.config.collectAsState()
    val trends by agent.researchedTrends.collectAsState()
    val isScanning by agent.isScanning.collectAsState()

    var selectedHorizonFilter by remember { mutableStateOf<TrendHorizon?>(null) }
    var commandPromptInput by remember { mutableStateOf("") }
    var isGeneratingCommand by remember { mutableStateOf(false) }
    var showStrategyDialog by remember { mutableStateOf(false) }

    val filteredTrends = remember(trends, selectedHorizonFilter) {
        if (selectedHorizonFilter == null) trends else trends.filter { it.horizon == selectedHorizonFilter }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .padding(vertical = 12.dp)
                .testTag("studio_trend_agent_modal"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimeGold)))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .fillMaxHeight()
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
                                .background(Brush.linearGradient(listOf(AnimeCyan, AnimePurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Psychology, contentDescription = null, tint = Color.White, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppLocaleStrings.tr(lang, "TrendPulse AI Studio Sync", "ट्रेंडपल्स AI स्टूडियो सिंक"),
                                    color = AnimeGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(if (config.isActive) AnimeGreen.copy(alpha = 0.2f) else AnimeSurfaceVariant)
                                        .padding(horizontal = 5.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = if (config.isActive) "ACTIVE" else "DEACTIVATED",
                                        color = if (config.isActive) AnimeGreen else TextMuted,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = "Internet & Social Media Research • Live & Future Viral Topics",
                                color = TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row {
                        IconButton(onClick = { showStrategyDialog = true }) {
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = AnimeGold)
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Command Generation Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Bolt, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "Generate Video on Command:", "कमांड देकर वीडियो बनाएं:"),
                                color = TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            OutlinedTextField(
                                value = commandPromptInput,
                                onValueChange = { commandPromptInput = it },
                                placeholder = {
                                    Text("e.g. Create futuristic shonen anime reel...", fontSize = 11.sp, color = TextMuted)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(8.dp),
                                singleLine = true,
                                enabled = config.isActive && !isGeneratingCommand,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeCyan,
                                    unfocusedBorderColor = AnimeSurfaceVariant
                                )
                            )

                            Spacer(modifier = Modifier.width(6.dp))

                            Button(
                                onClick = {
                                    if (commandPromptInput.isNotBlank()) {
                                        coroutineScope.launch {
                                            isGeneratingCommand = true
                                            val item = agent.generateContentOnCommand(commandPromptInput)
                                            isGeneratingCommand = false
                                            commandPromptInput = ""
                                            viewModel.setPromptInput(item.completeAnimePrompt)
                                            Toast.makeText(context, "🎬 Generated & Loaded into Studio!", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        }
                                    }
                                },
                                enabled = config.isActive && !isGeneratingCommand && commandPromptInput.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(48.dp)
                            ) {
                                if (isGeneratingCommand) {
                                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                                } else {
                                    Icon(Icons.Default.Send, contentDescription = "Run", tint = Color.Black, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Action Bar: Scan + Filter Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "⏱️ Select Horizon:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                agent.runLiveInternetSocialScan()
                                Toast.makeText(context, "🌐 Scanned Internet Trends!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        enabled = config.isActive && !isScanning,
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        if (isScanning) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(12.dp), strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Scan Live", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val horizons = listOf(
                        Pair(null, "🌐 All"),
                        Pair(TrendHorizon.TRENDING_NOW, "🔥 Now"),
                        Pair(TrendHorizon.NEXT_7_DAYS, "⚡ 7 Days"),
                        Pair(TrendHorizon.NEXT_30_DAYS, "🔮 30 Days (Future)"),
                        Pair(TrendHorizon.UPCOMING_SEASONS, "🚀 90+ Days")
                    )
                    items(horizons) { (h, label) ->
                        val isSel = selectedHorizonFilter == h
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AnimePurple else AnimeSurfaceVariant)
                                .clickable { selectedHorizonFilter = h }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSel) Color.White else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Trends List
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredTrends) { trend ->
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, Color(trend.horizon.badgeColorHex).copy(alpha = 0.5f))
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
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
                                                .padding(horizontal = 5.dp, vertical = 2.dp)
                                        ) {
                                            Text(trend.horizon.titleEn, color = Color(trend.horizon.badgeColorHex), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(trend.platformSource.displayName, color = Color(trend.platformSource.iconColorHex), fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                    }

                                    Text("${trend.viralityScore}% VIRAL", color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = trend.topicTitle,
                                    color = AnimeGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )

                                Text(
                                    text = "Hook: ${trend.suggestedVideoTitle}",
                                    color = TextPrimary,
                                    fontSize = 10.sp
                                )

                                Text(
                                    text = trend.scriptSynopsis,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 2
                                )

                                Spacer(modifier = Modifier.height(6.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.setPromptInput(trend.completeAnimePrompt)
                                            Toast.makeText(context, "🎬 Inserted into Studio!", Toast.LENGTH_SHORT).show()
                                            onDismiss()
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1.3f).height(30.dp)
                                    ) {
                                        Icon(Icons.Default.Movie, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Load in Studio", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            val ok = agent.syncWithYouTubeAutomation(trend)
                                            if (ok) {
                                                Toast.makeText(context, "⚡ Enqueued in YouTube Automation!", Toast.LENGTH_SHORT).show()
                                            }
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.weight(1.3f).height(30.dp)
                                    ) {
                                        Icon(Icons.Default.Tv, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Queue Automation", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showStrategyDialog) {
        OwnerTrendAgentStrategyDialog(
            agent = agent,
            lang = lang,
            onDismiss = { showStrategyDialog = false }
        )
    }
}

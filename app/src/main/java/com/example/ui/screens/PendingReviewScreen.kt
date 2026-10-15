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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CompareArrows
import androidx.compose.material.icons.filled.HourglassEmpty
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.data.engine.AutomationChannelManager
import com.example.data.model.AutomatedEpisode
import com.example.data.model.EpisodeReviewStatus
import com.example.data.model.UploadPrivacyStatus
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
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
 * Screen displaying generated videos held in 'Pending Review' state.
 * Implements:
 * - Side-by-side prompt comparisons (Original User Prompt vs AI Director Enriched Prompt)
 * - Built-in video player & animated preview
 * - Granular Approve (Green Signal) and Reject actions
 * - Target Platform and Draft vs Public mode confirmation
 */
@Composable
fun PendingReviewScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val channelManager = remember { AutomationChannelManager.getInstance(context) }
    val episodes by channelManager.episodesQueue.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage

    val pendingEpisodes = episodes.filter {
        it.status == EpisodeReviewStatus.PENDING_REVIEW || it.status == EpisodeReviewStatus.SCHEDULED
    }

    var selectedPreviewEpisodeId by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("pending_review_screen")
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("pending_review_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Pending Review Gate", "सत्यापन व रिव्यू कतार"),
                        color = TextPrimary,
                        fontSize = 18.sp,
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
                        Text(
                            text = "${pendingEpisodes.size} PENDING",
                            color = AnimeGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Verify prompts, preview clips, and give Green Signal before social upload",
                        "सोशल अपलोड से पहले ओरिजिनल प्रॉम्प्ट व वीडियो की पुष्टि करें"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // INFO NOTICE CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(24.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Manual Verification is ON: Generated clips are held here. Tap 'Approve' to send to YouTube, Instagram, or Facebook as configured (Draft or Public).",
                        "मैनुअल वेरिफिकेशन सक्रिय है: वीडियो यहाँ रोके गए हैं। आपके 'Approve' करने पर ही ये प्लेटफॉर्म पर भेजे जाएंगे।"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 15.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        if (pendingEpisodes.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = AnimeGreen,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "All Caught Up!", "सभी वीडियो स्वीकृत हैं!"),
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "No videos are currently waiting in the verification queue.",
                            "वर्तमान में कोई भी वीडियो रिव्यू के लिए बाकी नहीं है।"
                        ),
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                channelManager.triggerDailyEpisodeGeneration(1)
                                Toast.makeText(context, "🎬 Generated fresh episode for review", Toast.LENGTH_SHORT).show()
                            }
                        },
                        border = BorderStroke(1.dp, AnimeCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(AppLocaleStrings.tr(lang, "Generate Sample Episode", "सैंपल एपिसोड बनाएं"), color = AnimeCyan, fontSize = 12.sp)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(pendingEpisodes, key = { it.id }) { episode ->
                    PendingEpisodeCard(
                        episode = episode,
                        lang = lang,
                        isPlayingPreview = selectedPreviewEpisodeId == episode.id,
                        onTogglePreview = {
                            selectedPreviewEpisodeId = if (selectedPreviewEpisodeId == episode.id) null else episode.id
                        },
                        onApprove = {
                            coroutineScope.launch {
                                channelManager.giveGreenSignalAndUpload(episode.id)
                                val modeStr = if (episode.uploadPrivacy == UploadPrivacyStatus.PUBLIC) "Public Post" else "Private Draft"
                                Toast.makeText(
                                    context,
                                    "🟢 Approved! Uploading to ${episode.uploadedPlatform.displayName} as $modeStr!",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        },
                        onReject = {
                            channelManager.rejectEpisode(episode.id)
                            Toast.makeText(context, "❌ Episode rejected and removed from queue.", Toast.LENGTH_SHORT).show()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingEpisodeCard(
    episode: AutomatedEpisode,
    lang: String,
    isPlayingPreview: Boolean,
    onTogglePreview: () -> Unit,
    onApprove: () -> Unit,
    onReject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("pending_episode_card_${episode.id}"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header: Title & Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = episode.uploadedPlatform.iconEmoji, fontSize = 16.sp)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = episode.title,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (episode.uploadPrivacy == UploadPrivacyStatus.PUBLIC) AnimeGreen.copy(alpha = 0.2f)
                                else AnimeCyan.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (episode.uploadPrivacy == UploadPrivacyStatus.PUBLIC) "PUBLIC" else "DRAFT",
                            color = if (episode.uploadPrivacy == UploadPrivacyStatus.PUBLIC) AnimeGreen else AnimeCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnimeGold.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${episode.viralityScore}% VIRAL",
                            color = AnimeGold,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Clip Specs Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "⏱️ ${episode.durationSeconds}s",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "📺 ${episode.resolution}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
                Text(
                    text = "⚡ ${episode.fps} FPS",
                    color = AnimeCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "🎯 ${episode.uploadedPlatform.displayName}",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Video Preview Player Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(if (isPlayingPreview) 120.dp else 50.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .clickable { onTogglePreview() }
                    .border(1.dp, AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(10.dp)),
                contentAlignment = Alignment.Center
            ) {
                if (isPlayingPreview) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("▶ Simulated Video Preview Playing (${episode.fps} FPS)", color = AnimeGreen, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                            repeat(24) { i ->
                                val barHeight = (10 + ((i * 7) % 32)).dp
                                Box(
                                    modifier = Modifier
                                        .width(3.dp)
                                        .height(barHeight)
                                        .background(if (i % 2 == 0) AnimeCyan else AnimePink, RoundedCornerShape(2.dp))
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Tap to minimize preview", color = TextMuted, fontSize = 9.sp)
                    }
                } else {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Watch Generated Video Preview (${episode.durationSeconds}s Clip)", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // SIDE-BY-SIDE PROMPT COMPARISON SECTION
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.CompareArrows, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SIDE-BY-SIDE PROMPT COMPARISON",
                    color = AnimeCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Left Column: Original User Prompt
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.8.dp, AnimePurple.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AnimePurple))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Original Creator Prompt:",
                                color = AnimePurple,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = episode.originalPrompt.ifBlank { episode.title },
                            color = TextPrimary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                // Right Column: AI Director Enriched Prompt
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(0.8.dp, AnimeCyan.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(AnimeCyan))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI Director Enriched Prompt:",
                                color = AnimeCyan,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = episode.aiDirectorEnrichedPrompt.ifBlank { episode.synopsis },
                            color = TextPrimary,
                            fontSize = 10.sp,
                            lineHeight = 14.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Approve (Green Signal) vs Reject
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onReject,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = AnimePink),
                    border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(38.dp)
                        .testTag("reject_pending_video_btn_${episode.id}")
                ) {
                    Icon(Icons.Default.Close, contentDescription = null, tint = AnimePink, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Reject", color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = onApprove,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.6f)
                        .height(38.dp)
                        .testTag("approve_pending_video_btn_${episode.id}")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (episode.uploadPrivacy == UploadPrivacyStatus.PUBLIC) "Approve & Publish Live" else "Approve & Save Draft",
                        color = Color.Black,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

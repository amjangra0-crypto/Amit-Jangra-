package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AutomationChannelManager
import com.example.data.model.ChannelPlatform
import com.example.data.model.ConnectedChannel
import com.example.data.model.UploadPrivacyStatus
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
 * Dedicated 'Social Sync' Panel in settings where users can:
 * 1. Toggle individual account auto-uploading (ON/OFF).
 * 2. Set each connected platform/account to 'Draft' or 'Public' mode.
 * 3. Add and manage multiple YouTube, Instagram, and Facebook accounts.
 */
@Composable
fun SocialSyncSettingsPanel(
    lang: String = "English",
    onNavigateToReviewQueue: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val channelManager = remember { AutomationChannelManager.getInstance(context) }
    val channels by channelManager.connectedChannels.collectAsState()
    val schedule by channelManager.webSeriesSchedule.collectAsState()

    var showAddAccountSection by remember { mutableStateOf(false) }
    var newPlatform by remember { mutableStateOf(ChannelPlatform.YOUTUBE) }
    var newAccountHandle by remember { mutableStateOf("") }
    var newAccountName by remember { mutableStateOf("") }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("settings_social_sync_card"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AnimeCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.CloudSync,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Social Sync Hub", "सोशल सिंक पैनल"),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("${channels.size} CONNECTED", color = AnimeGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Toggle auto-upload & configure Draft vs Public mode per account",
                                "प्रति अकाउंट ऑटो-अपलोड व ड्राफ्ट/पब्लिक मोड सेट करें"
                            ),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                onNavigateToReviewQueue?.let { navQueue ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeGold.copy(alpha = 0.2f))
                            .clickable { navQueue() }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Review Queue", "रिव्यू कतार"),
                            color = AnimeGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Master Verification Gate Status Notice
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (schedule.requireOwnerVerificationBeforeUpload)
                            "🔒 " + AppLocaleStrings.tr(lang, "Manual Verification Required", "मैनुअल वेरिफिकेशन अनिवार्य है")
                        else
                            "⚡ " + AppLocaleStrings.tr(lang, "Direct Auto-Publish Enabled", "डायरेक्ट ऑटो-पब्लिश सक्रिय है"),
                        color = if (schedule.requireOwnerVerificationBeforeUpload) AnimeGold else AnimeGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (schedule.requireOwnerVerificationBeforeUpload)
                            AppLocaleStrings.tr(lang, "Videos held in 'Pending Review' until you tap Approve", "वीडियो तब तक रोके रहेंगे जब तक आप अप्रूव नहीं करेंगे")
                        else
                            AppLocaleStrings.tr(lang, "Videos upload immediately based on schedule", "वीडियो सीधे शेड्यूल पर अपलोड हो जाएंगे"),
                        color = TextMuted,
                        fontSize = 9.sp
                    )
                }

                Switch(
                    checked = schedule.requireOwnerVerificationBeforeUpload,
                    onCheckedChange = { channelManager.setRequireOwnerVerification(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AnimeGold,
                        checkedTrackColor = AnimeGold.copy(alpha = 0.4f),
                        uncheckedThumbColor = AnimeGreen,
                        uncheckedTrackColor = AnimeGreen.copy(alpha = 0.4f)
                    ),
                    modifier = Modifier.testTag("social_sync_master_verify_switch")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // LIST OF CONNECTED ACCOUNTS WITH INDIVIDUAL TOGGLES
            Text(
                text = AppLocaleStrings.tr(lang, "CONNECTED SOCIAL ACCOUNTS:", "कनेक्टेड सोशल अकाउंट्स:"),
                color = AnimeCyan,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                channels.forEach { channel ->
                    ConnectedAccountSyncRow(
                        channel = channel,
                        lang = lang,
                        onToggleAutoUpload = { enabled ->
                            channelManager.toggleChannelAutoUpload(channel.id, enabled)
                            Toast.makeText(
                                context,
                                "🔄 ${channel.channelName}: Auto-upload " + if (enabled) "ON" else "OFF",
                                Toast.LENGTH_SHORT
                            ).show()
                        },
                        onTogglePrivacyMode = { privacy ->
                            channelManager.setChannelPrivacy(channel.id, privacy)
                            val label = if (privacy == UploadPrivacyStatus.PUBLIC) "Public Post" else "Private Draft"
                            Toast.makeText(
                                context,
                                "📝 ${channel.channelName}: Mode set to $label",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ADD NEW ACCOUNT EXPANDABLE SECTION
            if (!showAddAccountSection) {
                OutlinedButton(
                    onClick = { showAddAccountSection = true },
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.6f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("add_social_account_btn")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Connect Another Account (YouTube / Insta / FB)", "नया अकाउंट कनेक्ट करें"),
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimePurple)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Connect New Channel / Page:",
                            color = AnimeGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        // Platform Selector
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            listOf(ChannelPlatform.YOUTUBE, ChannelPlatform.INSTAGRAM, ChannelPlatform.FACEBOOK).forEach { p ->
                                FilterChip(
                                    selected = newPlatform == p,
                                    onClick = { newPlatform = p },
                                    label = { Text("${p.iconEmoji} ${p.displayName}", fontSize = 10.sp) },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = AnimeCyan.copy(alpha = 0.3f),
                                        selectedLabelColor = AnimeCyan
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        OutlinedTextField(
                            value = newAccountName,
                            onValueChange = { newAccountName = it },
                            label = { Text("Channel / Account Name", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = newAccountHandle,
                            onValueChange = { newAccountHandle = it },
                            label = { Text("Handle or URL (e.g. @MyAnimePage)", fontSize = 10.sp) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = { showAddAccountSection = false },
                                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Cancel", color = TextMuted, fontSize = 11.sp)
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Button(
                                onClick = {
                                    if (newAccountHandle.isNotBlank()) {
                                        channelManager.addChannel(
                                            channelName = newAccountName.ifBlank { "My ${newPlatform.displayName}" },
                                            channelHandleOrUrl = newAccountHandle,
                                            platform = newPlatform,
                                            defaultPrivacy = UploadPrivacyStatus.PUBLIC
                                        )
                                        Toast.makeText(context, "✅ ${newPlatform.displayName} Connected!", Toast.LENGTH_SHORT).show()
                                        showAddAccountSection = false
                                        newAccountName = ""
                                        newAccountHandle = ""
                                    } else {
                                        Toast.makeText(context, "Please enter a handle or URL", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Connect Account", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ConnectedAccountSyncRow(
    channel: ConnectedChannel,
    lang: String,
    onToggleAutoUpload: (Boolean) -> Unit,
    onTogglePrivacyMode: (UploadPrivacyStatus) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("account_sync_row_${channel.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        border = BorderStroke(0.6.dp, Color.White.copy(alpha = 0.1f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Row 1: Platform & Channel info + Auto-upload switch
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = channel.platform.iconEmoji, fontSize = 18.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = channel.channelName,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                        Text(
                            text = "${channel.channelHandle} • ${channel.subscriberCount}",
                            color = TextMuted,
                            fontSize = 9.sp,
                            maxLines = 1
                        )
                    }
                }

                // Individual Account Auto-Upload Toggle Switch
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (channel.autoUploadAfterReview) "Auto-Upload ON" else "Auto OFF",
                        color = if (channel.autoUploadAfterReview) AnimeGreen else TextMuted,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Switch(
                        checked = channel.autoUploadAfterReview,
                        onCheckedChange = onToggleAutoUpload,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AnimeGreen,
                            checkedTrackColor = AnimeGreen.copy(alpha = 0.4f),
                            uncheckedThumbColor = TextMuted,
                            uncheckedTrackColor = AnimeSurface
                        ),
                        modifier = Modifier.testTag("account_auto_upload_switch_${channel.id}")
                    )
                }
            }

            HorizontalDivider(
                color = Color.White.copy(alpha = 0.08f),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            // Row 2: Per-Platform Privacy Mode (Public vs Draft)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Upload Destination Mode:",
                    color = TextSecondary,
                    fontSize = 10.sp
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // Draft Chip
                    val isDraft = channel.defaultPrivacy == UploadPrivacyStatus.DRAFT_PRIVATE
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isDraft) AnimeCyan.copy(alpha = 0.3f) else AnimeSurface)
                            .border(1.dp, if (isDraft) AnimeCyan else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { onTogglePrivacyMode(UploadPrivacyStatus.DRAFT_PRIVATE) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("account_privacy_mode_draft_${channel.id}")
                    ) {
                        Text(
                            text = "📝 Save as Draft",
                            color = if (isDraft) AnimeCyan else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = if (isDraft) FontWeight.Bold else FontWeight.Normal
                        )
                    }

                    // Public Chip
                    val isPublic = channel.defaultPrivacy == UploadPrivacyStatus.PUBLIC
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isPublic) AnimeGreen.copy(alpha = 0.3f) else AnimeSurface)
                            .border(1.dp, if (isPublic) AnimeGreen else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable { onTogglePrivacyMode(UploadPrivacyStatus.PUBLIC) }
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("account_privacy_mode_public_${channel.id}")
                    ) {
                        Text(
                            text = "🌍 Direct Publish",
                            color = if (isPublic) AnimeGreen else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = if (isPublic) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
    }
}

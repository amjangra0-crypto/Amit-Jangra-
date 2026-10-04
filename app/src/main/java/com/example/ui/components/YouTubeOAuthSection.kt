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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.screens.YouTubeSpecificUserAccessDialog
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.ConnectionTestResult
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
import com.example.worker.AutomationWorkScheduler

/**
 * YouTube Channel ID, OAuth 2.0 Credentials & WorkManager Daily Scheduler Component
 * Securely persists YouTube credentials and sets automated daily generation & upload time.
 */
@Composable
fun YouTubeOAuthCredentialsSection(
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val ytCreds by viewModel.youtubeCredentials.collectAsState()
    val isOwnerUnlocked by viewModel.isOwnerAccessUnlocked.collectAsState()
    val isGloballyEnabled by viewModel.isYouTubeAutomationGloballyEnabled.collectAsState()
    val ytManagedUsers by viewModel.youtubeManagedUsers.collectAsState()
    val isUserSpecificallyAuthorized = viewModel.isYouTubeAutomationAccessibleForCurrentUser()
    val isOwner = currentUser.isOwner || isOwnerUnlocked
    val lang = state.selectedLanguage

    var showOwnerPinDialog by remember { mutableStateOf(false) }
    var showYtUserAccessModal by remember { mutableStateOf(false) }
    var ownerPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }

    // Local form state initialized from persistent YouTubeOAuthCredentials
    var channelId by remember(ytCreds.channelId) { mutableStateOf(ytCreds.channelId) }
    var channelHandle by remember(ytCreds.channelHandle) { mutableStateOf(ytCreds.channelHandle) }
    var channelName by remember(ytCreds.channelName) { mutableStateOf(ytCreds.channelName) }
    var channelUrl by remember(ytCreds.channelUrl) { mutableStateOf(ytCreds.channelUrl) }
    var clientId by remember(ytCreds.clientId) { mutableStateOf(ytCreds.clientId) }
    var clientSecret by remember(ytCreds.clientSecret) { mutableStateOf(ytCreds.clientSecret) }
    var accessToken by remember(ytCreds.accessToken) { mutableStateOf(ytCreds.accessToken) }
    var refreshToken by remember(ytCreds.refreshToken) { mutableStateOf(ytCreds.refreshToken) }
    var defaultPrivacy by remember(ytCreds.defaultPrivacy) { mutableStateOf(ytCreds.defaultPrivacy) }

    var isSecretVisible by remember { mutableStateOf(false) }
    var isTokenVisible by remember { mutableStateOf(false) }

    // WorkManager Daily Scheduler settings
    var isSchedulerActive by remember(ytCreds.isDailySchedulerEnabled) { mutableStateOf(ytCreds.isDailySchedulerEnabled) }
    var selectedHour by remember(ytCreds.dailyScheduledHour) { mutableStateOf(ytCreds.dailyScheduledHour) }
    var selectedMinute by remember(ytCreds.dailyScheduledMinute) { mutableStateOf(ytCreds.dailyScheduledMinute) }
    var episodesPerDay by remember(ytCreds.episodesPerDay) { mutableStateOf(ytCreds.episodesPerDay) }

    var testResult by remember { mutableStateOf<ConnectionTestResult?>(null) }
    var saveStatusMsg by remember { mutableStateOf("") }

    val remainingDelay = remember(selectedHour, selectedMinute, isSchedulerActive) {
        if (isSchedulerActive) {
            AutomationWorkScheduler.getFormattedDelayRemaining(selectedHour, selectedMinute)
        } else {
            "अक्रिय (Paused)"
        }
    }

    if (showOwnerPinDialog) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = {
                showOwnerPinDialog = false
                pinError = ""
                ownerPinInput = ""
            },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("👑", fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Owner PIN Verification", "ओनर पिन सत्यापन"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
            },
            text = {
                Column {
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Enter Owner Master PIN (Default: 1234):",
                            "ओनर मास्टर पिन दर्ज करें (डिफ़ॉल्ट: 1234):"
                        ),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    OutlinedTextField(
                        value = ownerPinInput,
                        onValueChange = {
                            ownerPinInput = it
                            pinError = ""
                        },
                        placeholder = { Text("1234", color = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    if (pinError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = pinError, color = Color(0xFFEF4444), fontSize = 11.sp)
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (viewModel.unlockOwnerWithPin(ownerPinInput)) {
                            showOwnerPinDialog = false
                            pinError = ""
                            ownerPinInput = ""
                            Toast.makeText(context, "👑 Owner verified!", Toast.LENGTH_SHORT).show()
                        } else {
                            pinError = if (AppLocaleStrings.isHindi(lang)) "गलत पिन! (डिफ़ॉल्ट: 1234)" else "Invalid PIN! (Default: 1234)"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold)
                ) {
                    Text("Unlock", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                androidx.compose.material3.TextButton(onClick = {
                    showOwnerPinDialog = false
                    pinError = ""
                    ownerPinInput = ""
                }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("youtube_oauth_section_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(
            1.5.dp,
            Brush.linearGradient(listOf(Color(0xFFFF0000), AnimeGold, AnimeCyan))
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF0000)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("▶️", fontSize = 18.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppLocaleStrings.get("youtube_creds_title", lang),
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(if (isOwner) AnimeGold else AnimePurple.copy(alpha = 0.5f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isOwner) "👑 OWNER ONLY" else "🔒 SECURE",
                                color = if (isOwner) Color.Black else Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    Text(
                        text = AppLocaleStrings.get("youtube_creds_desc", lang),
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. OWNER MASTER CONTROL CARD
            if (isOwner) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeGold.copy(alpha = 0.12f)),
                    border = BorderStroke(1.dp, AnimeGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "👑 " + AppLocaleStrings.tr(
                                    lang,
                                    "Master Owner Control: YouTube Automation",
                                    "ओनर मास्टर नियंत्रण: YouTube ऑटोमेशन"
                                ),
                                color = AnimeGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (isGloballyEnabled)
                                    AppLocaleStrings.tr(
                                        lang,
                                        "Enabled for other users (You can toggle OFF to lock)",
                                        "अन्य उपयोगकर्ताओं के लिए चालू है (आप बंद कर सकते हैं)"
                                    )
                                else
                                    AppLocaleStrings.tr(
                                        lang,
                                        "Disabled for other users • Only Owner has access",
                                        "अन्य उपयोगकर्ताओं के लिए बंद है • केवल ओनर को अनुमति"
                                    ),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = isGloballyEnabled,
                            onCheckedChange = { enable ->
                                viewModel.setYouTubeAutomationGloballyEnabled(enable)
                                Toast.makeText(
                                    context,
                                    if (enable) "👑 YouTube Automation enabled for users" else "👑 YouTube Automation disabled for users",
                                    Toast.LENGTH_SHORT
                                ).show()
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AnimeGold,
                                checkedTrackColor = AnimeGold.copy(alpha = 0.4f)
                            ),
                            modifier = Modifier.testTag("owner_toggle_youtube_automation")
                        )
                    }
                }

                // 2. OWNER SPECIFIC PERSON ACCESS CONTROL CARD
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "👤 " + AppLocaleStrings.tr(
                                    lang,
                                    "Specific Person Access Control",
                                    "विशिष्ट व्यक्ति हेतु एक्सेस कंट्रोल"
                                ),
                                color = AnimeCyanLight,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Activate or deactivate access for specific persons (${ytManagedUsers.count { it.isGranted }} authorized)",
                                    "विशिष्ट व्यक्तियों के लिए YouTube ऑटोमेशन चालू/बंद करें (${ytManagedUsers.count { it.isGranted }} अधिकृत)"
                                ),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Button(
                            onClick = { showYtUserAccessModal = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(32.dp).testTag("owner_manage_yt_access_btn")
                        ) {
                            Text("Manage", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else if (!isGloballyEnabled && !isUserSpecificallyAuthorized) {
                // Locked for non-owners because Owner disabled it
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    border = BorderStroke(1.5.dp, Color(0xFFFF0000)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = AppLocaleStrings.tr(lang, "YouTube Automation Disabled by Owner", "YouTube ऑटोमेशन ओनर द्वारा बंद है"),
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "The app owner has disabled YouTube automation for other users. Only the Owner or authorized persons can use it.",
                                "YouTube ऑटोमेशन एक्सेस केवल ओनर व अधिकृत व्यक्तियों के लिए है। बाकी सभी के लिए यह ओनर द्वारा बंद है।"
                            ),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Button(
                            onClick = { showOwnerPinDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("👑 " + AppLocaleStrings.tr(lang, "Enter Owner PIN to Unlock", "ओनर पिन से अनलॉक करें"), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            } else if (!isGloballyEnabled && isUserSpecificallyAuthorized) {
                // Granted specific access by Owner banner
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp),
                    colors = CardDefaults.cardColors(containerColor = AnimeGreen.copy(alpha = 0.15f)),
                    border = BorderStroke(1.dp, AnimeGreen),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "✓ You have been granted YouTube Automation access by the Owner",
                                "✓ आपको ऐप ओनर द्वारा YouTube ऑटोमेशन की विशेष अनुमति दी गई है"
                            ),
                            color = AnimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Owner Security Warning / Notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(if (isOwner) AnimeGold.copy(alpha = 0.12f) else AnimePurple.copy(alpha = 0.15f))
                    .border(
                        1.dp,
                        if (isOwner) AnimeGold.copy(alpha = 0.5f) else AnimePurple.copy(alpha = 0.4f),
                        RoundedCornerShape(10.dp)
                    )
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (isOwner) Icons.Default.Security else Icons.Default.Lock,
                        contentDescription = null,
                        tint = if (isOwner) AnimeGold else AnimeCyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isOwner) {
                            "👑 प्रमाणित ओनर (${currentUser.displayName}): YouTube चैनल ID व OAuth 2.0 क्रेडेंशियल्स पूर्णतः एन्क्रिप्टेड हैं। केवल आप ही इन्हें देख या बदल सकते हैं।"
                        } else {
                            AppLocaleStrings.get("owner_only_warning", lang)
                        },
                        color = if (isOwner) AnimeGold else TextMuted,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Input: Target YouTube Channel ID
            Text(
                text = AppLocaleStrings.get("channel_id_label", lang),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = channelId,
                onValueChange = { if (isOwner) channelId = it },
                enabled = isOwner,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_yt_channel_id"),
                placeholder = { Text("UC_AmanJangraAnimeStudio2099", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF0000),
                    unfocusedBorderColor = AnimePurple.copy(alpha = 0.4f),
                    focusedContainerColor = AnimeSurfaceVariant,
                    unfocusedContainerColor = AnimeSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input: Target YouTube Channel Handle & URL
            Text(
                text = AppLocaleStrings.get("channel_handle_label", lang),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = channelHandle,
                    onValueChange = {
                        if (isOwner) {
                            channelHandle = it
                            channelUrl = "https://youtube.com/${if (it.startsWith("@")) it else "@$it"}"
                        }
                    },
                    enabled = isOwner,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_yt_channel_handle"),
                    placeholder = { Text("@AmanJangraAnime", color = TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF0000),
                        unfocusedBorderColor = AnimePurple.copy(alpha = 0.4f),
                        focusedContainerColor = AnimeSurfaceVariant,
                        unfocusedContainerColor = AnimeSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                OutlinedTextField(
                    value = channelName,
                    onValueChange = { if (isOwner) channelName = it },
                    enabled = isOwner,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_yt_channel_name"),
                    placeholder = { Text("Aman Anime Studio", color = TextMuted, fontSize = 12.sp) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFFFF0000),
                        unfocusedBorderColor = AnimePurple.copy(alpha = 0.4f),
                        focusedContainerColor = AnimeSurfaceVariant,
                        unfocusedContainerColor = AnimeSurfaceVariant
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Input: OAuth 2.0 Client ID
            Text(
                text = AppLocaleStrings.get("client_id_label", lang),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = clientId,
                onValueChange = { if (isOwner) clientId = it },
                enabled = isOwner,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_yt_client_id"),
                placeholder = { Text("928374829102-animeclient.apps.googleusercontent.com", color = TextMuted, fontSize = 12.sp) },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCyan,
                    unfocusedBorderColor = AnimePurple.copy(alpha = 0.4f),
                    focusedContainerColor = AnimeSurfaceVariant,
                    unfocusedContainerColor = AnimeSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input: OAuth 2.0 Client Secret (Masked)
            Text(
                text = AppLocaleStrings.get("client_secret_label", lang),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = clientSecret,
                onValueChange = { if (isOwner) clientSecret = it },
                enabled = isOwner,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_yt_client_secret"),
                visualTransformation = if (isSecretVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isSecretVisible = !isSecretVisible }) {
                        Icon(
                            imageVector = if (isSecretVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Secret Visibility",
                            tint = AnimeGold
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeGold,
                    unfocusedBorderColor = AnimePurple.copy(alpha = 0.4f),
                    focusedContainerColor = AnimeSurfaceVariant,
                    unfocusedContainerColor = AnimeSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Input: OAuth 2.0 Access / Bearer Token
            Text(
                text = AppLocaleStrings.get("access_token_label", lang),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = accessToken,
                onValueChange = { if (isOwner) accessToken = it },
                enabled = isOwner,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_yt_access_token"),
                visualTransformation = if (isTokenVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isTokenVisible = !isTokenVisible }) {
                        Icon(
                            imageVector = if (isTokenVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = "Toggle Token Visibility",
                            tint = AnimeCyan
                        )
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCyan,
                    unfocusedBorderColor = AnimePurple.copy(alpha = 0.4f),
                    focusedContainerColor = AnimeSurfaceVariant,
                    unfocusedContainerColor = AnimeSurfaceVariant
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Default Upload Privacy Selector
            Text(
                text = "डिफ़ॉल्ट अपलोड गोपनीयता (Default Privacy):",
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                UploadPrivacyStatus.entries.forEach { status ->
                    val isSelected = defaultPrivacy == status
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFFFF0000).copy(alpha = 0.2f) else AnimeSurfaceVariant)
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFFF0000) else AnimePurple.copy(alpha = 0.3f),
                                RoundedCornerShape(8.dp)
                            )
                            .clickable(enabled = isOwner) { defaultPrivacy = status }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = status.title.substringBefore(" ("),
                            color = if (isSelected) Color(0xFFFF0000) else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // WorkManager Daily Scheduler Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AnimeCyan.copy(alpha = 0.08f))
                    .border(1.dp, AnimeCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(14.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Alarm, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = AppLocaleStrings.get("daily_scheduler_title", lang),
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = AppLocaleStrings.get("daily_scheduler_desc", lang),
                                    color = TextSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Switch(
                            checked = isSchedulerActive,
                            onCheckedChange = {
                                if (isOwner) {
                                    isSchedulerActive = it
                                    viewModel.updateYouTubeDailySchedule(
                                        hour = selectedHour,
                                        minute = selectedMinute,
                                        enabled = it,
                                        episodesPerDay = episodesPerDay
                                    )
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AnimeCyan,
                                checkedTrackColor = AnimeCyan.copy(alpha = 0.3f)
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Preset Times Selection
                    Text(
                        text = "दैनिक शेड्यूल समय (Daily Run Time):",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    val timePresets = listOf(
                        Triple(9, 0, "09:00 AM (सुबह)"),
                        Triple(14, 0, "02:00 PM (दोपहर)"),
                        Triple(18, 0, "06:00 PM (शाम 6 बजे)"),
                        Triple(21, 0, "09:00 PM (रात 9 बजे)")
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        timePresets.forEach { (h, m, label) ->
                            val isChosen = selectedHour == h && selectedMinute == m
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isChosen) AnimeCyan else AnimeSurfaceVariant)
                                    .clickable(enabled = isOwner) {
                                        selectedHour = h
                                        selectedMinute = m
                                        if (isSchedulerActive) {
                                            viewModel.updateYouTubeDailySchedule(
                                                hour = h,
                                                minute = m,
                                                enabled = true,
                                                episodesPerDay = episodesPerDay
                                            )
                                        }
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "%02d:%02d".format(h, m),
                                    color = if (isChosen) Color.Black else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Episodes per day selection
                    Text(
                        text = "प्रतिदिन एपिसोड संख्या (Episodes Per Day):",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(1, 2, 3).forEach { count ->
                            val isSelected = episodesPerDay == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AnimeGold else AnimeSurfaceVariant)
                                    .clickable(enabled = isOwner) {
                                        episodesPerDay = count
                                        if (isSchedulerActive) {
                                            viewModel.updateYouTubeDailySchedule(
                                                hour = selectedHour,
                                                minute = selectedMinute,
                                                enabled = true,
                                                episodesPerDay = count
                                            )
                                        }
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "$count एपिसोड/दिन",
                                    color = if (isSelected) Color.Black else TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Live Status of WorkManager
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isSchedulerActive) {
                                "अगला ऑटोमेशन रन: %02d:%02d ($remainingDelay)".format(selectedHour, selectedMinute)
                            } else {
                                "शेड्यूलर अक्रिय है (स्विच ऑन करें)"
                            },
                            color = if (isSchedulerActive) AnimeGreen else TextMuted,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Test Connection & Trigger Now
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        testResult = viewModel.testYouTubeConnection(channelId, channelHandle, accessToken)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("test_yt_connection_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, Color(0xFFFF0000))
                ) {
                    Text(
                        text = AppLocaleStrings.get("test_connection_btn", lang),
                        color = Color(0xFFFF0000),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                OutlinedButton(
                    onClick = {
                        viewModel.triggerImmediateDailyAutomation()
                        Toast.makeText(context, "⚡ WorkManager तुरंत बैकग्राउंड रन शुरू हुआ!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(42.dp)
                        .testTag("trigger_immediate_work_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimeCyan)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = AppLocaleStrings.get("trigger_now_btn", lang),
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Connection Test Output Banner
            testResult?.let { result ->
                Spacer(modifier = Modifier.height(10.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (result.isSuccess) AnimeGreen.copy(alpha = 0.15f) else Color(0xFFFF0000).copy(alpha = 0.15f))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (result.isSuccess) Icons.Default.CheckCircle else Icons.Default.Error,
                            contentDescription = null,
                            tint = if (result.isSuccess) AnimeGreen else Color(0xFFFF0000),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = result.message,
                            color = if (result.isSuccess) AnimeGreen else Color(0xFFFF0000),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Save YouTube Credentials Button
            Button(
                onClick = {
                    if (isOwner) {
                        viewModel.saveYouTubeCredentials(
                            channelId = channelId,
                            channelHandle = channelHandle,
                            channelName = channelName,
                            channelUrl = channelUrl,
                            clientId = clientId,
                            clientSecret = clientSecret,
                            accessToken = accessToken,
                            refreshToken = refreshToken,
                            defaultPrivacy = defaultPrivacy
                        )
                        viewModel.updateYouTubeDailySchedule(
                            hour = selectedHour,
                            minute = selectedMinute,
                            enabled = isSchedulerActive,
                            episodesPerDay = episodesPerDay
                        )
                        saveStatusMsg = "✓ YouTube चैनल ID व क्रेडेंशियल्स सुरक्षित सेव हो गए!"
                        Toast.makeText(context, saveStatusMsg, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "केवल Owner ही क्रेडेंशियल्स सेव कर सकता है।", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = isOwner,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("save_youtube_oauth_btn")
            ) {
                Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppLocaleStrings.get("save_creds_btn", lang),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }

    if (showYtUserAccessModal && isOwner) {
        YouTubeSpecificUserAccessDialog(
            managedUsers = ytManagedUsers,
            lang = lang,
            onGrantUser = { idOrEmail -> viewModel.setYouTubeAutomationUserAccess(idOrEmail, true) },
            onToggleUser = { idOrEmail, granted -> viewModel.setYouTubeAutomationUserAccess(idOrEmail, granted) },
            onRemoveUser = { idOrEmail -> viewModel.removeYouTubeAutomationUser(idOrEmail) },
            onDismiss = { showYtUserAccessModal = false }
        )
    }
}

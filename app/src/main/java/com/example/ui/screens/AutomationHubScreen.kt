package com.example.ui.screens
import com.example.localization.AppLocaleStrings

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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.material3.AlertDialog
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.AutomationChannelManager
import com.example.data.engine.LyriaMusicEngine
import com.example.data.model.AutomatedEpisode
import com.example.data.model.ChannelPlatform
import com.example.data.model.EpisodeReviewStatus
import com.example.data.model.MusicMood
import com.example.ui.AnimeViewModel
import com.example.ui.components.AudioWaveformCanvas
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
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
 * Automation & YouTube / Instagram Publishing Hub Screen
 * Features:
 * 1. Personal YouTube channel connection via copied link / URL
 * 2. Daily Web Series episode generation command (1, 2, or 3 episodes/day)
 * 3. Notification & Review Gate: Preview video & give Green Signal (OK) before upload
 * 4. Scheduled Fallback Auto-Upload: Automatically uploads at scheduled time if not reviewed
 * 5. Lyria AI Music & Soundtrack Generator
 * 6. Multi-App API Integration (YouTube API v3, Instagram Reels, Webhooks)
 */
@Composable
fun AutomationHubScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val automationManager = remember { AutomationChannelManager.getInstance(context) }
    val lyriaMusicEngine = remember { LyriaMusicEngine.getInstance(context) }

    val connectedChannel by automationManager.connectedChannel.collectAsState()
    val webSeriesSchedule by automationManager.webSeriesSchedule.collectAsState()
    val episodesQueue by automationManager.episodesQueue.collectAsState()
    val statusMessage by automationManager.automationStatusMessage.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isOwnerUnlocked by viewModel.isOwnerAccessUnlocked.collectAsState()
    val isGloballyEnabled by viewModel.isYouTubeAutomationGloballyEnabled.collectAsState()
    val ytManagedUsers by viewModel.youtubeManagedUsers.collectAsState()
    val isUserSpecificallyAuthorized = viewModel.isYouTubeAutomationAccessibleForCurrentUser()
    val isOwner = currentUser.isOwner || isOwnerUnlocked
    val hasAutomationAccess = isOwner || isGloballyEnabled || isUserSpecificallyAuthorized
    val lang = state.selectedLanguage

    var showOwnerPinDialog by remember { mutableStateOf(false) }
    var showYtUserAccessModal by remember { mutableStateOf(false) }
    var ownerPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }

    var channelUrlInput by remember { mutableStateOf(connectedChannel.channelUrl) }
    var channelNameInput by remember { mutableStateOf(connectedChannel.channelName) }
    var seriesTitleInput by remember { mutableStateOf(webSeriesSchedule.seriesTitle) }
    var episodesPerDaySelected by remember { mutableIntStateOf(webSeriesSchedule.episodesPerDay) }
    var isGeneratingDailyBatch by remember { mutableStateOf(false) }

    // Lyria Music Generator States
    var musicPromptInput by remember { mutableStateOf("Epic Shonen battle theme with traditional Japanese shamisen and cyber synth") }
    var selectedMusicMood by remember { mutableStateOf(MusicMood.EPIC_BATTLE) }
    var isGeneratingMusic by remember { mutableStateOf(false) }
    var lastGeneratedMusicTitle by remember { mutableStateOf("") }
    var musicWaveformData by remember { mutableStateOf<List<Float>>(emptyList()) }

    var activeTab by remember { mutableIntStateOf(0) } // 0: Channel & Web Series, 1: Review & Green Signal, 2: Lyria AI Music, 3: Multi-App APIs

    if (showOwnerPinDialog) {
        AlertDialog(
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

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 88.dp)
    ) {
        // Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("automation_back_btn")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AnimePurple)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Automation & YouTube Hub",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(0xFFFF0000))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("YOUTUBE AUTO", color = Color.White, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    if (isOwner) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeGold.copy(alpha = 0.2f))
                                .border(1.dp, AnimeGold, RoundedCornerShape(6.dp))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("👑 OWNER", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                Text(
                    text = "Automated video creation, review & upload on green signal",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. OWNER MASTER CONTROL CARDS
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
                                    "Enabled for other users (Toggle OFF to lock)",
                                    "अन्य उपयोगकर्ताओं के लिए चालू है (आप बंद कर सकते हैं)"
                                )
                            else
                                AppLocaleStrings.tr(
                                    lang,
                                    "Disabled for other users • Only Owner & specific authorized persons",
                                    "अन्य उपयोगकर्ताओं के लिए बंद है • केवल ओनर व अधिकृत विशिष्ट व्यक्ति"
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
                        modifier = Modifier.testTag("owner_automation_hub_toggle")
                    )
                }
            }

            // SPECIFIC PERSON ACCESS CONTROL CARD (OWNER CAN GRANT / REVOKE FOR ANY PERSON)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
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
                                "विशिष्ट व्यक्ति हेतु एक्सेस नियंत्रण"
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
                        modifier = Modifier.height(32.dp).testTag("automation_manage_persons_btn")
                    ) {
                        Text("Manage (${ytManagedUsers.count { it.isGranted }})", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else if (!isGloballyEnabled && !isUserSpecificallyAuthorized) {
            // Locked for non-owners because Owner disabled it and not specifically authorized
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                border = BorderStroke(1.5.dp, Color(0xFFFF0000)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(40.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "YouTube Automation Disabled by Owner", "YouTube ऑटोमेशन ओनर द्वारा बंद है"),
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "YouTube automation access is restricted exclusively to the Owner and authorized persons. Contact owner (amjangra0@gmail.com) for access.",
                            "YouTube ऑटोमेशन का एक्सेस केवल ओनर व अधिकृत विशिष्ट व्यक्तियों के लिए है। अनुमति हेतु ओनर से संपर्क करें।"
                        ),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Button(
                        onClick = { showOwnerPinDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("👑 " + AppLocaleStrings.tr(lang, "Enter Owner PIN to Unlock", "ओनर पिन दर्ज करें"), color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        } else if (!isGloballyEnabled && isUserSpecificallyAuthorized) {
            // Authorized specific person badge
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
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

        // If user does not have permission, gate remaining automation controls
        if (!hasAutomationAccess) {
            Spacer(modifier = Modifier.height(30.dp))
            return
        }

        // System Live Status Message Banner
        if (statusMessage.isNotBlank()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .border(1.dp, AnimeCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.NotificationsActive, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = statusMessage, color = AnimeCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                }
            }
            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section Tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AnimeSurfaceVariant)
                .padding(4.dp)
        ) {
            val tabs = listOf(
                Pair("📺 Channel & Series", 0),
                Pair("🟢 Review & Green Signal", 1),
                Pair("🎵 AI Music", 2),
                Pair("🔌 Multi APIs", 3)
            )
            tabs.forEach { (label, idx) ->
                val isSelected = activeTab == idx
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AnimePurple else Color.Transparent)
                        .clickable { activeTab = idx }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = if (isSelected) Color.White else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        when (activeTab) {
            0 -> {
                // TAB 0: YouTube Channel Link & Web Series Command Hub
                ChannelConnectionCard(
                    connectedChannel = connectedChannel,
                    channelUrlInput = channelUrlInput,
                    channelNameInput = channelNameInput,
                    onUrlChange = { channelUrlInput = it },
                    onNameChange = { channelNameInput = it },
                    onSaveChannel = {
                        automationManager.connectChannelByLink(channelUrlInput, channelNameInput)
                        Toast.makeText(context, "✅ Channel link connected successfully!", Toast.LENGTH_SHORT).show()
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                WebSeriesAutomationCard(
                    schedule = webSeriesSchedule,
                    seriesTitle = seriesTitleInput,
                    episodesPerDay = episodesPerDaySelected,
                    onTitleChange = { seriesTitleInput = it },
                    onEpisodesPerDayChange = {
                        episodesPerDaySelected = it
                        automationManager.setSeriesEpisodesPerDay(it, seriesTitleInput)
                    },
                    onToggleAutoPublishFallback = { enabled ->
                        automationManager.setAutoPublishFallback(enabled, webSeriesSchedule.fallbackTimerHours)
                    },
                    isGenerating = isGeneratingDailyBatch,
                    onTriggerDailyGeneration = {
                        coroutineScope.launch {
                            isGeneratingDailyBatch = true
                            automationManager.triggerDailyEpisodeGeneration(episodesPerDaySelected)
                            isGeneratingDailyBatch = false
                            activeTab = 1 // Switch to Review & Green Signal tab
                        }
                    }
                )
            }

            1 -> {
                // TAB 1: Review & Green Signal (OK) Upload Gate
                ReviewAndGreenSignalCard(
                    episodes = episodesQueue,
                    onGreenSignalAndUpload = { episodeId ->
                        coroutineScope.launch {
                            automationManager.giveGreenSignalAndUpload(episodeId)
                            Toast.makeText(context, "🟢 Green signal received! Video uploaded to YouTube!", Toast.LENGTH_LONG).show()
                        }
                    },
                    onRejectEpisode = { episodeId ->
                        automationManager.rejectEpisode(episodeId)
                        Toast.makeText(context, "❌ Episode rejected", Toast.LENGTH_SHORT).show()
                    }
                )
            }

            2 -> {
                // TAB 2: Lyria AI Generative Music & Soundtrack Engine
                LyriaMusicGeneratorCard(
                    musicPrompt = musicPromptInput,
                    selectedMood = selectedMusicMood,
                    isGenerating = isGeneratingMusic,
                    lastGeneratedTitle = lastGeneratedMusicTitle,
                    waveformData = musicWaveformData,
                    onPromptChange = { musicPromptInput = it },
                    onMoodChange = { selectedMusicMood = it },
                    onGenerateMusic = {
                        coroutineScope.launch {
                            isGeneratingMusic = true
                            val result = lyriaMusicEngine.generateSoundtrack(
                                prompt = musicPromptInput,
                                mood = selectedMusicMood,
                                durationSeconds = 30
                            )
                            lastGeneratedMusicTitle = result.title
                            musicWaveformData = result.waveformData
                            isGeneratingMusic = false
                            Toast.makeText(context, "🎵 New AI soundtrack ready: ${result.title}", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            }

            3 -> {
                // TAB 3: Multi-App API Integration Console
                MultiAppApiConnectCard(
                    connectedChannel = connectedChannel
                )
            }
        }
    }

    if (showYtUserAccessModal) {
        YouTubeSpecificUserAccessDialog(
            managedUsers = ytManagedUsers,
            lang = lang,
            onGrantUser = { idOrEmail ->
                viewModel.setYouTubeAutomationUserAccess(idOrEmail, true)
            },
            onToggleUser = { idOrEmail, granted ->
                viewModel.setYouTubeAutomationUserAccess(idOrEmail, granted)
            },
            onRemoveUser = { idOrEmail ->
                viewModel.removeYouTubeAutomationUser(idOrEmail)
            },
            onDismiss = { showYtUserAccessModal = false }
        )
    }
}

/**
 * YouTube Channel Connection Card via Link Copy-Paste
 */
@Composable
private fun ChannelConnectionCard(
    connectedChannel: com.example.data.model.ConnectedChannel,
    channelUrlInput: String,
    channelNameInput: String,
    onUrlChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onSaveChannel: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Color(0xFFFF0000).copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                            .background(Color(0xFFFF0000).copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tv, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text("Connect YouTube Channel Link", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text("Add your personal channel link and enable auto-upload", color = TextSecondary, fontSize = 11.sp)
                    }
                }

                if (connectedChannel.isConnected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("CONNECTED", color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Channel Link Input Field
            Text("Paste personal channel link here:", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = channelUrlInput,
                onValueChange = onUrlChange,
                placeholder = { Text("उदा: https://youtube.com/@MyAnimeChannel", color = TextMuted, fontSize = 12.sp) },
                leadingIcon = {
                    Icon(Icons.Default.Link, contentDescription = null, tint = Color(0xFFFF0000), modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            if (channelUrlInput.isNotBlank()) {
                                onSaveChannel()
                            }
                        }) {
                            Icon(Icons.Default.Search, contentDescription = "Search & Connect", tint = Color(0xFFFF0000), modifier = Modifier.size(18.dp))
                        }
                        IconButton(onClick = {
                            onUrlChange("https://youtube.com/@AmanAnimeStudioOfficial")
                        }) {
                            Icon(Icons.Default.ContentPaste, contentDescription = "Paste", tint = AnimeCyan, modifier = Modifier.size(18.dp))
                        }
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("youtube_channel_link_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFFF0000),
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { onSaveChannel() },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Icon(Icons.Default.Search, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Search & Link Channel", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Channel Name Input
            Text("Channel Display Name:", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = channelNameInput,
                onValueChange = onNameChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("youtube_channel_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCyan,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Connected Channel Status Summary
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(connectedChannel.channelName, color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("${connectedChannel.channelHandle} • ${connectedChannel.subscriberCount}", color = TextSecondary, fontSize = 10.sp)
                }

                Button(
                    onClick = onSaveChannel,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("save_channel_link_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Save Channel", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

/**
 * Web Series Daily Generation & Command Card
 */
@Composable
private fun WebSeriesAutomationCard(
    schedule: com.example.data.model.WebSeriesSchedule,
    seriesTitle: String,
    episodesPerDay: Int,
    onTitleChange: (String) -> Unit,
    onEpisodesPerDayChange: (Int) -> Unit,
    onToggleAutoPublishFallback: (Boolean) -> Unit,
    isGenerating: Boolean,
    onTriggerDailyGeneration: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = AnimePink, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Web Series Daily Command (Automation)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimePink.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("EPISODIC", color = AnimePink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Create 1, 2, or 3 episodes daily per your command. The app will notify you when ready and upload to YouTube upon your green signal review.",
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Web Series Title Input
            Text("Web Series Title:", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = seriesTitle,
                onValueChange = onTitleChange,
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = onTriggerDailyGeneration) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = "Generate Series", tint = AnimePurple)
                    }
                },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimePurple,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Episodes per day Command Selector (1, 2, 3)
            Text("How many episodes to make per day? (Daily Command):", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf(1, 2, 3).forEach { count ->
                    val isSelected = episodesPerDay == count
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) AnimePurple else AnimeSurfaceVariant)
                            .border(1.dp, if (isSelected) AnimePink else Color.Transparent, RoundedCornerShape(10.dp))
                            .clickable { onEpisodesPerDayChange(count) }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "$count ep/day",
                                color = if (isSelected) Color.White else TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (count == 1) "Daily Release" else if (count == 2) "Noon & Evening" else "Tri-Daily Saga",
                                color = if (isSelected) AnimeCyanLight else TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scheduled Fallback Toggle
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .padding(12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                    Text("Scheduled Time Auto-Upload Backup", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Text("If you are busy and unable to review, it will auto-upload to YouTube after 2 hours.", color = TextSecondary, fontSize = 10.sp)
                }
                Switch(
                    checked = schedule.autoPublishIfNoReview,
                    onCheckedChange = onToggleAutoPublishFallback,
                    colors = SwitchDefaults.colors(checkedThumbColor = AnimeGold, checkedTrackColor = AnimeGold.copy(alpha = 0.3f))
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Trigger Daily Generation Button
            Button(
                onClick = onTriggerDailyGeneration,
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("trigger_daily_episodes_btn")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Generating episodes...", color = Color.White, fontSize = 13.sp)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("⚡ Generate today's $episodesPerDay episode(s) & send for review", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Review & Green Signal (OK) Approval Gate Card
 */
@Composable
private fun ReviewAndGreenSignalCard(
    episodes: List<AutomatedEpisode>,
    onGreenSignalAndUpload: (String) -> Unit,
    onRejectEpisode: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimeGreen.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Review & Green Signal Gate (Approval Gate)", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeGreen.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("${episodes.count { it.status == EpisodeReviewStatus.PENDING_REVIEW }} PENDING", color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "जब तक आप 'ग्रीन सिग्नल / OK' नहीं बोलेंगे, वीडियो अपलोड नहीं होगा (या निर्धारित शेड्यूल टाइम खत्म होने पर स्वतः अपलोड होगा)।",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            if (episodes.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No pending episodes. Create new episodes from the 'Channel & Series' tab!", color = TextMuted, fontSize = 12.sp)
                }
            } else {
                episodes.forEach { episode ->
                    EpisodeReviewItemCard(
                        episode = episode,
                        onGreenSignal = { onGreenSignalAndUpload(episode.id) },
                        onReject = { onRejectEpisode(episode.id) }
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
private fun EpisodeReviewItemCard(
    episode: AutomatedEpisode,
    onGreenSignal: () -> Unit,
    onReject: () -> Unit
) {
    val isPending = episode.status == EpisodeReviewStatus.PENDING_REVIEW
    val isUploaded = episode.status == EpisodeReviewStatus.UPLOADED

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(
            1.dp,
            if (isPending) AnimeGold else if (isUploaded) AnimeGreen else AnimePurple.copy(alpha = 0.3f)
        )
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Episode Title & Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "🎬 ${episode.title}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(if (isUploaded) AnimeGreen else if (isPending) AnimeGold else AnimeCyan)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = episode.status.label,
                        color = Color.Black,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(text = episode.synopsis, color = TextSecondary, fontSize = 11.sp, lineHeight = 15.sp)

            Spacer(modifier = Modifier.height(10.dp))

            // Countdown / Schedule Info
            if (isPending) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeGold.copy(alpha = 0.12f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Schedule, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Schedule fallback: 01:54:30 left", color = AnimeGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Text("Auto-upload if unreviewed", color = TextMuted, fontSize = 9.sp)
                }
                Spacer(modifier = Modifier.height(10.dp))
            } else if (isUploaded && episode.uploadedUrl.isNotBlank()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeGreen.copy(alpha = 0.15f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("YouTube पर लाइव: ${episode.uploadedUrl}", color = AnimeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Green Signal & Action Buttons
            if (isPending) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onGreenSignal,
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(42.dp).testTag("green_signal_ok_btn")
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("🟢 Give Green Signal (OK) & Upload", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                    }

                    OutlinedButton(
                        onClick = onReject,
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color(0xFFFF5252)),
                        modifier = Modifier.height(42.dp)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = null, tint = Color(0xFFFF5252), modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reject", color = Color(0xFFFF5252), fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

/**
 * Lyria AI Generative Music & Soundtrack Card
 */
@Composable
private fun LyriaMusicGeneratorCard(
    musicPrompt: String,
    selectedMood: MusicMood,
    isGenerating: Boolean,
    lastGeneratedTitle: String,
    waveformData: List<Float>,
    onPromptChange: (String) -> Unit,
    onMoodChange: (MusicMood) -> Unit,
    onGenerateMusic: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.MusicNote, contentDescription = null, tint = AnimePink, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lyria AI Music & Soundtrack Generator", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimePink.copy(alpha = 0.2f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("LYRIA V3", color = AnimePink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "प्रॉम्प्ट्स या सीन इमेज से कस्टम बैकग्राउंड म्यूजिक, बैटल थीम, जिंगल्स और जापानी ऑर्केस्ट्रा तैयार करें।",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mood Selector
            Text("Select Music Mood / Genre:", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                MusicMood.values().forEach { mood ->
                    val isSelected = selectedMood == mood
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AnimePink else AnimeSurfaceVariant)
                            .border(1.dp, if (isSelected) AnimeGold else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { onMoodChange(mood) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = mood.label,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Music Prompt Input
            Text("Describe Soundtrack (Music Prompt):", color = TextMuted, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(6.dp))
            OutlinedTextField(
                value = musicPrompt,
                onValueChange = onPromptChange,
                minLines = 2,
                modifier = Modifier.fillMaxWidth().testTag("lyria_music_prompt_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimePink,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Generate Music Button
            Button(
                onClick = onGenerateMusic,
                enabled = !isGenerating,
                colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth().height(46.dp).testTag("generate_lyria_music_btn")
            ) {
                if (isGenerating) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lyria AI is generating soundtrack...", color = Color.White, fontSize = 12.sp)
                } else {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("🎵 Create Custom Soundtrack with Lyria AI", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Waveform Preview if generated
            if (lastGeneratedTitle.isNotBlank()) {
                Spacer(modifier = Modifier.height(14.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimePink)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = "🎧 $lastGeneratedTitle", color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Text(text = "${selectedMood.label} • 30s", color = AnimeCyanLight, fontSize = 10.sp)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Native Canvas Audio Waveform Component
                        AudioWaveformCanvas(
                            waveformSamples = waveformData,
                            playbackProgress = 0.45f,
                            isPlaying = true,
                            barCount = 40,
                            height = 46.dp,
                            activeBarColor = AnimePink,
                            playheadColor = AnimeGold
                        )
                    }
                }
            }
        }
    }
}

/**
 * Multi-App API Integration Card
 */
@Composable
private fun MultiAppApiConnectCard(
    connectedChannel: com.example.data.model.ConnectedChannel
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("Multi-App API Hub Automation", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
            Text("Connect automations with YouTube, Instagram, Webhooks & Zapier", color = TextSecondary, fontSize = 11.sp)

            Spacer(modifier = Modifier.height(14.dp))

            ApiConnectorRow(
                appName = "YouTube Data API v3",
                status = "Connected (Active)",
                icon = Icons.Default.Tv,
                color = Color(0xFFFF0000),
                desc = "Automated video upload, thumbnail & tags publishing"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ApiConnectorRow(
                appName = "Instagram Graph API",
                status = "Reels Ready (Active)",
                icon = Icons.Default.Movie,
                color = Color(0xFFE1306C),
                desc = "Reels scheduling & 9:16 vertical auto-upload"
            )

            Spacer(modifier = Modifier.height(10.dp))

            ApiConnectorRow(
                appName = "Webhooks & Zapier Connector",
                status = "REST Endpoint (Active)",
                icon = Icons.Default.Link,
                color = AnimeCyan,
                desc = "https://api.animestudio.ai/v1/webhook/episodes"
            )
        }
    }
}

@Composable
private fun ApiConnectorRow(
    appName: String,
    status: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color,
    desc: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AnimeSurfaceVariant)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(appName, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Text(desc, color = TextMuted, fontSize = 10.sp)
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(AnimeGreen.copy(alpha = 0.2f))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(status, color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
    }
}

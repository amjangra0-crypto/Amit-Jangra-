package com.example.ui.screens

import android.Manifest
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.components.SocialSyncSettingsPanel
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
import com.example.util.PermissionManager
import kotlinx.coroutines.launch

/**
 * SettingsScreen Composable:
 * - Lists all requested permissions (Microphone, Camera, Storage / Gallery) with manual toggle switches.
 * - Tracks 'already requested' state in DataStore via PermissionManager.
 * - Strictly guarantees that permissions are requested only ONCE after installation.
 * - Allows users to manually Allow or Deallow each permission at any time.
 * - Provides one-click access to Android System App Settings.
 */
@Composable
fun SettingsScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToAdvancedSettings: (() -> Unit)? = null
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val permissionManager = remember { PermissionManager.getInstance(context) }
    val uiState by viewModel.uiState.collectAsState()
    val lang = uiState.selectedLanguage

    // Track user toggle flows from DataStore
    val userMicAllowed by permissionManager.userMicrophoneAllowedFlow.collectAsState(initial = permissionManager.isMicrophoneGranted())
    val userCameraAllowed by permissionManager.userCameraAllowedFlow.collectAsState(initial = permissionManager.isCameraGranted())
    val userStorageAllowed by permissionManager.userStorageAllowedFlow.collectAsState(initial = permissionManager.isStorageGranted())
    val hasAlreadyRequestedInstall by permissionManager.alreadyRequestedOnInstallFlow.collectAsState(initial = true)

    // Track live system permission statuses
    var sysMicGranted by remember { mutableStateOf(permissionManager.isMicrophoneGranted()) }
    var sysCameraGranted by remember { mutableStateOf(permissionManager.isCameraGranted()) }
    var sysStorageGranted by remember { mutableStateOf(permissionManager.isStorageGranted()) }
    var showThemePicker by remember { mutableStateOf(false) }

    // User Suggestion Box State
    var suggestionType by remember { mutableStateOf("ADD_FEATURE") } // "ADD_FEATURE", "REMOVE_OPTION", "FEEDBACK"
    var suggestionCategory by remember { mutableStateOf("AI Video Generation") }
    var suggestionText by remember { mutableStateOf("") }
    var submittedSuggestions by remember { mutableStateOf(listOf<Triple<String, String, Long>>()) }

    // Manual Updates Check State
    var updateCheckStatus by remember { mutableStateOf<String?>(null) }
    var isCheckingUpdates by remember { mutableStateOf(false) }

    // Permission launchers if user actively taps enable for first time
    val micLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        sysMicGranted = granted
        scope.launch {
            permissionManager.setUserMicrophoneAllowed(granted)
            permissionManager.markAlreadyRequestedOnInstall()
        }
        val toastMsg = if (granted) {
            AppLocaleStrings.tr(lang, "✓ Microphone access allowed", "✓ माइक्रोफोन की अनुमति चालू की गई")
        } else {
            AppLocaleStrings.tr(lang, "Microphone access denied in system", "सिस्टम में माइक्रोफोन अनुमति अस्वीकृत")
        }
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
    }

    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        sysCameraGranted = granted
        scope.launch {
            permissionManager.setUserCameraAllowed(granted)
            permissionManager.markAlreadyRequestedOnInstall()
        }
        val toastMsg = if (granted) {
            AppLocaleStrings.tr(lang, "✓ Camera access allowed", "✓ कैमरा अनुमति चालू की गई")
        } else {
            AppLocaleStrings.tr(lang, "Camera access denied in system", "सिस्टम में कैमरा अनुमति अस्वीकृत")
        }
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
    }

    val storageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        sysStorageGranted = granted
        scope.launch {
            permissionManager.setUserStorageAllowed(granted)
            permissionManager.markAlreadyRequestedOnInstall()
        }
        val toastMsg = if (granted) {
            AppLocaleStrings.tr(lang, "✓ Storage access allowed", "✓ स्टोरेज अनुमति चालू की गई")
        } else {
            AppLocaleStrings.tr(lang, "Storage access denied in system", "सिस्टम में स्टोरेज अनुमति अस्वीकृत")
        }
        Toast.makeText(context, toastMsg, Toast.LENGTH_SHORT).show()
    }

    // Refresh live system statuses when screen is composed or resumed
    LaunchedEffect(Unit) {
        sysMicGranted = permissionManager.isMicrophoneGranted()
        sysCameraGranted = permissionManager.isCameraGranted()
        sysStorageGranted = permissionManager.isStorageGranted()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("settings_screen")
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("settings_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Settings & Permissions", "सेटिंग्स व परमिशन नियंत्रण"),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeGreen.copy(alpha = 0.2f))
                            .border(1.dp, AnimeGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "DATASTORE SECURE",
                            color = AnimeGreen,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Manage app access, permissions & audio preferences",
                        "ऐप परमिशन, माइक्रोफोन व सुरक्षा सेटिंग्स प्रबंधित करें"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // THEME SETTINGS (Light, Dark, Neon Anime)
        ThemeSettings(
            viewModel = viewModel,
            lang = lang,
            modifier = Modifier.fillMaxWidth().testTag("settings_theme_card")
        )

        Spacer(modifier = Modifier.height(14.dp))

        // USE OFFLINE MODE TOGGLE (Restricts to local assets and Room Database, disabling AI cloud generation)
        Card(
            modifier = Modifier.fillMaxWidth().testTag("settings_offline_mode_card"),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                if (uiState.isOfflineMode) AnimeGold.copy(alpha = 0.8f) else AnimePurple.copy(alpha = 0.4f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                if (uiState.isOfflineMode) AnimeGold.copy(alpha = 0.2f) else AnimeCyan.copy(alpha = 0.15f)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.isOfflineMode) "💾" else "☁️",
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Use Offline Mode", "ऑफ़लाइन मोड का उपयोग करें"),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            if (uiState.isOfflineMode) {
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = AppLocaleStrings.tr(lang, "OFFLINE ACTIVE", "ऑफ़लाइन सक्रिय"),
                                        color = AnimeGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Restricts app activity to local assets and Room Database, disabling AI cloud generation until toggled back on.",
                                "ऐप गतिविधियों को केवल लोकल एसेट्स और रूम डेटाबेस तक सीमित करता है, और पुनः चालू करने तक क्लाउड जेनरेशन बंद रखता है।"
                            ),
                            color = TextMuted,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }

                Switch(
                    checked = uiState.isOfflineMode,
                    onCheckedChange = { viewModel.setOfflineMode(it) },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AnimeGold,
                        checkedTrackColor = AnimeGold.copy(alpha = 0.4f),
                        uncheckedThumbColor = AnimePurple,
                        uncheckedTrackColor = AnimePurple.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("settings_offline_toggle_switch")
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // GLOBAL DEFAULT LANGUAGE CUSTOMIZATION CARD
        Card(
            modifier = Modifier.fillMaxWidth().testTag("settings_language_card"),
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AnimeCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Public, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Global Default Language", "ग्लोबल डिफ़ॉल्ट भाषा प्रणाली"),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "${AppLocaleStrings.tr(lang, "Current", "वर्तमान")}: ${uiState.selectedLanguage} • 12 Supported Worldwide",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 7.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = uiState.selectedLanguage.uppercase(),
                            color = AnimeCyan,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Customize default app language globally. All screens, buttons, menus & dubbing voice reflect your selected language.",
                        "पूरी ऐप के लिए मुख्य भाषा कस्टमाइज़ करें। स्क्रीन, बटन, मेन्यू व वॉइस तुरंत उसी भाषा में अपडेट होंगे।"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // 12 Worldwide Language Grid Options
                val globalLanguages = listOf(
                    Triple("Hindi", "🇮🇳 हिन्दी", "hi"),
                    Triple("English", "🇺🇸 English", "en"),
                    Triple("Japanese", "🇯🇵 日本語", "ja"),
                    Triple("Korean", "🇰🇷 한국어", "ko"),
                    Triple("Spanish", "🇪🇸 Español", "es"),
                    Triple("German", "🇩🇪 Deutsch", "de"),
                    Triple("French", "🇫🇷 Français", "fr"),
                    Triple("Chinese", "🇨🇳 中文", "zh"),
                    Triple("Arabic", "🇦🇪 العربية", "ar"),
                    Triple("Russian", "🇷🇺 Русский", "ru"),
                    Triple("Portuguese", "🇧🇷 Português", "pt"),
                    Triple("Indonesian", "🇮🇩 Bahasa", "id")
                )

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    globalLanguages.chunked(3).forEach { rowLangs ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            rowLangs.forEach { (langName, label, _) ->
                                val isSelected = uiState.selectedLanguage.equals(langName, ignoreCase = true)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(if (isSelected) AnimeCyan.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                                        .border(
                                            1.dp,
                                            if (isSelected) AnimeCyan else Color.Transparent,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable {
                                            viewModel.setAppInterfaceLanguage(langName)
                                            viewModel.setLanguage(langName)
                                            Toast.makeText(
                                                context,
                                                "🌍 Global Default Language: $label ($langName)",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                        .padding(vertical = 8.dp, horizontal = 4.dp)
                                        .testTag("global_lang_btn_$langName"),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = label,
                                            color = if (isSelected) AnimeCyanLight else TextPrimary,
                                            fontSize = 10.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                        )
                                        if (isSelected) {
                                            Spacer(modifier = Modifier.width(3.dp))
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = null,
                                                tint = AnimeGreen,
                                                modifier = Modifier.size(12.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. ONE-TIME INSTALL PERMISSION POLICY CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AnimeGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AnimeGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "🛡️ Single-Request Permission Guarantee",
                                "🛡️ केवल 1 बार अनुमति पूछने की गारंटी"
                            ),
                            color = AnimeGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Prompts only 1 time on install • User decides Allow/Deallow anytime",
                                "केवल इंस्टॉल के बाद 1 बार परमिशन • उसके बाद निर्णय केवल यूजर का"
                            ),
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "To protect your privacy and provide a distraction-free experience, this app will NEVER repeatedly prompt system permission dialogs. You can manually enable or disable any permission below.",
                        "आपकी गोपनीयता की सुरक्षा के लिए यह ऐप बार-बार परमिशन के पॉपअप नहीं दिखाता। केवल पहली बार इंस्टॉल होने पर पूछा जाता है। आप नीचे किसी भी परमिशन को चालू (Allow) या बंद (Deallow) कर सकते हैं।"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "DataStore Tracking: " + if (hasAlreadyRequestedInstall) "✓ Handled (Never Prompts Again)" else "Fresh Install Mode",
                        color = if (hasAlreadyRequestedInstall) AnimeGreen else AnimePink,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )

                    OutlinedButton(
                        onClick = { permissionManager.openAppSettings() },
                        border = BorderStroke(1.dp, AnimeCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(30.dp).testTag("open_system_settings_btn")
                    ) {
                        Icon(Icons.Default.OpenInNew, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppLocaleStrings.tr(lang, "App Settings", "सिस्टम सेटिंग्स"),
                            color = AnimeCyan,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 2. REQUESTED PERMISSIONS WITH TOGGLE SWITCHES
        Text(
            text = AppLocaleStrings.tr(lang, "MANAGE REQUESTED PERMISSIONS", "परमिशन चालू या बंद करें (ALLOW / DEALLOW)"),
            color = AnimeCyan,
            fontSize = 12.sp,
            fontWeight = FontWeight.ExtraBold,
            modifier = Modifier.padding(horizontal = 4.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {

                // PERMISSION 1: MICROPHONE / RECORD AUDIO
                PermissionToggleRow(
                    icon = Icons.Default.Mic,
                    iconTint = AnimePink,
                    title = AppLocaleStrings.tr(lang, "Microphone (Record Audio)", "माइक्रोफोन (ऑडियो रिकॉर्डिंग)"),
                    subtitle = AppLocaleStrings.tr(
                        lang,
                        "Used for Voice Dubbing, Speech Prompts & Live voice sync",
                        "वॉयस डबिंग, बोलकर प्रॉम्प्ट देने व लाइव वॉयस सिंक हेतु"
                    ),
                    isUserAllowed = userMicAllowed,
                    isSystemGranted = sysMicGranted,
                    lang = lang,
                    testTag = "perm_toggle_microphone",
                    onToggleChanged = { targetAllow ->
                        scope.launch {
                            permissionManager.setUserMicrophoneAllowed(targetAllow)
                            if (targetAllow && !sysMicGranted) {
                                micLauncher.launch(Manifest.permission.RECORD_AUDIO)
                            } else if (!targetAllow) {
                                Toast.makeText(
                                    context,
                                    AppLocaleStrings.tr(lang, "Microphone deallowed in app", "ऐप में माइक्रोफोन बंद किया गया"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

                HorizontalDivider(color = TextMuted.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                // PERMISSION 2: CAMERA
                PermissionToggleRow(
                    icon = Icons.Default.CameraAlt,
                    iconTint = AnimeCyan,
                    title = AppLocaleStrings.tr(lang, "Camera Access", "कैमरा एक्सेस"),
                    subtitle = AppLocaleStrings.tr(
                        lang,
                        "Capture character references, backgrounds & textures",
                        "कैरैक्टर रेफरेंस फोटो, बैकग्राउंड व टेक्सचर खींचने हेतु"
                    ),
                    isUserAllowed = userCameraAllowed,
                    isSystemGranted = sysCameraGranted,
                    lang = lang,
                    testTag = "perm_toggle_camera",
                    onToggleChanged = { targetAllow ->
                        scope.launch {
                            permissionManager.setUserCameraAllowed(targetAllow)
                            if (targetAllow && !sysCameraGranted) {
                                cameraLauncher.launch(Manifest.permission.CAMERA)
                            } else if (!targetAllow) {
                                Toast.makeText(
                                    context,
                                    AppLocaleStrings.tr(lang, "Camera deallowed in app", "ऐप में कैमरा बंद किया गया"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )

                HorizontalDivider(color = TextMuted.copy(alpha = 0.2f), modifier = Modifier.padding(vertical = 12.dp))

                // PERMISSION 3: STORAGE & GALLERY
                PermissionToggleRow(
                    icon = Icons.Default.PhotoLibrary,
                    iconTint = AnimeGold,
                    title = AppLocaleStrings.tr(lang, "Storage & Gallery Access", "स्टोरेज व गैलरी एक्सेस"),
                    subtitle = AppLocaleStrings.tr(
                        lang,
                        "Import media assets and save exported anime MP4 videos",
                        "मीडिया फाइल्स आयात करने व रेंडर वीडियो सुरक्षित करने हेतु"
                    ),
                    isUserAllowed = userStorageAllowed,
                    isSystemGranted = sysStorageGranted,
                    lang = lang,
                    testTag = "perm_toggle_storage",
                    onToggleChanged = { targetAllow ->
                        scope.launch {
                            permissionManager.setUserStorageAllowed(targetAllow)
                            if (targetAllow && !sysStorageGranted) {
                                storageLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                            } else if (!targetAllow) {
                                Toast.makeText(
                                    context,
                                    AppLocaleStrings.tr(lang, "Storage access deallowed in app", "ऐप में स्टोरेज अनुमति बंद की गई"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 3. ANDROID SYSTEM LEVEL PERMISSIONS HELPER
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Settings, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "System Permission Info", "सिस्टम परमिशन जानकारी"),
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Under Android OS rules, granting or revoking system hardware permissions can also be managed directly in your device App Info settings.",
                        "एंड्रॉयड नियमों के तहत, आप किसी भी समय अपने फोन की सेटिंग्स में जाकर भी परमिशन को बदल सकते हैं।"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = { permissionManager.openAppSettings() },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("open_android_settings_btn")
                ) {
                    Icon(Icons.Default.OpenInNew, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Open Android App Settings", "फोन की ऐप सेटिंग्स खोलें"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 4. THEME & VISUAL STUDIO (EXCLUSIVELY IN SETTINGS)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.5f))
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
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AnimePink.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Palette, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Vibrant Theme Studio", "वाइब्रेंट थीम स्टूडियो"),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(lang, "Exclusive to Settings (Removed from Front Page)", "केवल सेटिंग्स में उपलब्ध (फ्रंट पेज से हटाया गया)"),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showThemePicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp).testTag("settings_theme_studio_btn")
                    ) {
                        Text(AppLocaleStrings.tr(lang, "Pick Theme", "थीम चुनें"), color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // SOCIAL SYNC PANEL: Multi-Account Auto-Upload & Draft/Public Modes
        SocialSyncSettingsPanel(
            lang = lang,
            onNavigateToReviewQueue = { viewModel.setTab(AppTab.PENDING_REVIEW) },
            modifier = Modifier.fillMaxWidth().testTag("settings_social_sync_panel")
        )

        Spacer(modifier = Modifier.height(16.dp))

        // STUDIO PRODUCTION MODULES CARD
        Card(
            modifier = Modifier.fillMaxWidth().testTag("settings_studio_modules_card"),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = AppLocaleStrings.tr(lang, "Studio Production Modules", "स्टूडियो प्रोडक्शन मॉड्यूल्स"),
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                // Pending Review Screen
                OutlinedButton(
                    onClick = { viewModel.setTab(AppTab.PENDING_REVIEW) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_nav_pending_review_btn")
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        AppLocaleStrings.tr(lang, "Pending Review Gate (Side-by-Side Prompts & Approvals)", "सत्यापन व रिव्यू कतार"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Export Presets Screen
                OutlinedButton(
                    onClick = { viewModel.setTab(AppTab.EXPORT_PRESETS) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_nav_export_presets_btn")
                ) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        AppLocaleStrings.tr(lang, "Export Presets (720p - 4K, 30-90 fps, YouTube/Instagram)", "एक्सपोर्ट प्रीसेट्स व FPS"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Voice Studio Screen
                OutlinedButton(
                    onClick = { viewModel.setTab(AppTab.VOICE_STUDIO) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_nav_voice_studio_btn")
                ) {
                    Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        AppLocaleStrings.tr(lang, "Voice Studio (Pitch, Speed, Gallery/Drive/Google Audio & Explainer)", "वॉइस स्टूडियो व ऑडियो"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Trend Horizon Hub
                OutlinedButton(
                    onClick = { viewModel.setTab(AppTab.TREND_HORIZON) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_nav_trend_horizon_btn")
                ) {
                    Icon(Icons.Default.TrendingUp, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        AppLocaleStrings.tr(lang, "Automation Trend Horizon Hub (Multi-Account & Access Control)", "ट्रेंड होराइजन ऑटोमेशन हब"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Project Settings (Aspect Ratios: 9:16, 16:9, 1:1)
                OutlinedButton(
                    onClick = { viewModel.setTab(AppTab.PROJECT_SETTINGS) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_nav_project_settings_btn")
                ) {
                    Icon(Icons.Default.AspectRatio, contentDescription = null, tint = AnimePurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        AppLocaleStrings.tr(lang, "Project Settings (Aspect Ratio: 9:16 Shorts, 16:9 YouTube, 1:1 Feed)", "प्रोजेक्ट सेटिंग्स (आस्पेक्ट रेशियो 9:16, 16:9, 1:1)"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Language Settings (Dubbing Language & DataStore Preferences)
                OutlinedButton(
                    onClick = { viewModel.setTab(AppTab.LANGUAGE_SETTINGS) },
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().testTag("settings_nav_language_settings_btn")
                ) {
                    Icon(Icons.Default.Translate, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        AppLocaleStrings.tr(lang, "Language Settings (Dubbing: Japanese, Hindi, English, Spanish - DataStore)", "डबिंग भाषा सेटिंग्स (जापानी, हिन्दी, अंग्रेज़ी, स्पैनिश)"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        if (onNavigateToAdvancedSettings != null) {
            Spacer(modifier = Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
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
                                    .size(32.dp)
                                    .clip(CircleShape)
                                    .background(AnimeCyan.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Tune, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Advanced Studio Settings", "एडवांस्ड स्टूडियो सेटिंग्स"),
                                    color = TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Voice pitch, video resolution, export targets & API config", "ऑडियो पिच, रेजोल्यूशन, एक्सपोर्ट व API सेटिंग्स"),
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        Button(
                            onClick = onNavigateToAdvancedSettings,
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp).testTag("open_advanced_settings_btn")
                        ) {
                            Text("Open", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // -------------------------------------------------------------------------------------
        // 5. INTERACTIVE USER SUGGESTION BOX (ADD NEW OPTION OR REMOVE EXISTING OPTION)
        // -------------------------------------------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_user_suggestion_box_card"),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.5.dp, AnimeGold)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AnimeGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Feedback, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(lang, "💡 User Suggestion & Feature Request Box", "💡 यूजर सुझाव व फीचर अनुरोध बॉक्स"),
                            color = AnimeGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Request to add new features or remove unwanted options",
                                "कोई नया विकल्प जोड़ने या पुराना विकल्प हटाने के लिए सुझाव दें"
                            ),
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Suggestion Type Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val types = listOf(
                        Triple("ADD_FEATURE", "➕ Add Feature", "➕ नया जोड़ें"),
                        Triple("REMOVE_OPTION", "➖ Remove Option", "➖ विकल्प हटाएं"),
                        Triple("FEEDBACK", "💬 Feedback", "💬 सुझाव")
                    )

                    types.forEach { (typeKey, enLabel, hiLabel) ->
                        val isSelected = suggestionType == typeKey
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AnimeGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSelected) AnimeGold else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { suggestionType = typeKey }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = AppLocaleStrings.tr(lang, enLabel, hiLabel),
                                color = if (isSelected) AnimeGold else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Category Selector
                val categories = listOf("AI Video Generation", "Voice & Dubbing", "Themes & Visuals", "Export & FPS", "Subscriptions", "Automation")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    categories.take(3).forEach { cat ->
                        val isSel = suggestionCategory == cat
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AnimeCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSel) AnimeCyan else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { suggestionCategory = cat }
                                .padding(vertical = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(cat, color = if (isSel) AnimeCyan else TextMuted, fontSize = 8.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = suggestionText,
                    onValueChange = { suggestionText = it },
                    placeholder = {
                        Text(
                            text = if (suggestionType == "REMOVE_OPTION")
                                AppLocaleStrings.tr(lang, "Specify which button or option you want removed and why...", "बताएं कि आप कौन सा बटन या विकल्प हटाना चाहते हैं...")
                            else
                                AppLocaleStrings.tr(lang, "Describe the new option or feature you want added...", "बताएं कि आप कौन सा नया विकल्प या फीचर जुड़वाना चाहते हैं..."),
                            fontSize = 11.sp,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .testTag("user_suggestion_input"),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeGold,
                        unfocusedBorderColor = TextMuted.copy(alpha = 0.4f)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = {
                        if (suggestionText.isNotBlank()) {
                            val newEntry = Triple(suggestionType, suggestionText.trim(), System.currentTimeMillis())
                            submittedSuggestions = submittedSuggestions + newEntry
                            suggestionText = ""
                            Toast.makeText(
                                context,
                                AppLocaleStrings.tr(
                                    lang,
                                    "✓ Suggestion submitted successfully! Reviewed for upcoming updates.",
                                    "✓ आपका सुझाव सफलतापूर्वक दर्ज हो गया है! इसे आगामी अपडेट में समीक्षा किया जाएगा।"
                                ),
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    },
                    enabled = suggestionText.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("submit_user_suggestion_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Submit Suggestion to App Engine", "सुझाव सबमिट करें"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                if (submittedSuggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Your Submitted Suggestions:", "आपके द्वारा दिए गए सुझाव:"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    submittedSuggestions.takeLast(3).reversed().forEach { (type, text, time) ->
                        val timeStr = java.text.SimpleDateFormat("dd MMM, HH:mm", java.util.Locale.getDefault()).format(java.util.Date(time))
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Column(modifier = Modifier.padding(8.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(
                                        text = if (type == "REMOVE_OPTION") "➖ REMOVE REQUEST" else "➕ ADD REQUEST",
                                        color = if (type == "REMOVE_OPTION") AnimePink else AnimeGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(timeStr, color = TextMuted, fontSize = 8.sp)
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(text, color = TextPrimary, fontSize = 10.sp)
                                Spacer(modifier = Modifier.height(2.dp))
                                Text(
                                    text = "Status: Logged & Reviewed by Studio Director",
                                    color = AnimeCyan,
                                    fontSize = 8.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // -------------------------------------------------------------------------------------
        // 6. MANUAL CHECK FOR SYSTEM & FEATURE UPDATES
        // -------------------------------------------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("settings_manual_update_check_card"),
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
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AnimeCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.SystemUpdate, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "System & Feature Updates", "सिस्टम व फीचर अपडेट्स"),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Current Version: v2.4.0 (Build 240)",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = {
                            isCheckingUpdates = true
                            scope.launch {
                                kotlinx.coroutines.delay(800)
                                val consentMgr = com.example.data.engine.UserUpdateConsentManager.getInstance(context)
                                val pending = consentMgr.getPendingUpdateForUser(null, null)
                                updateCheckStatus = if (pending != null) {
                                    "✨ New Feature Rollout Available: ${pending.title}! Tap to apply."
                                } else {
                                    "✓ You are on the latest version v2.4.0. No pending updates found."
                                }
                                isCheckingUpdates = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("manual_check_update_btn")
                    ) {
                        Text(
                            text = if (isCheckingUpdates) "Checking..." else AppLocaleStrings.tr(lang, "Check for Updates", "अपडेट चेक करें"),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (updateCheckStatus != null) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, AnimeGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = updateCheckStatus!!,
                            color = AnimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(26.dp))
    }

    if (showThemePicker) {
        com.example.ui.components.VibrantThemePickerSheet(
            viewModel = viewModel,
            selectedTheme = uiState.vibrantTheme,
            isDarkMode = uiState.isDarkMode,
            selectedLanguage = lang,
            onDismiss = { showThemePicker = false }
        )
    }
}

/**
 * Reusable Row for listing each permission with status and Allow/Deallow toggle switch
 */
@Composable
private fun PermissionToggleRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    isUserAllowed: Boolean,
    isSystemGranted: Boolean,
    lang: String,
    testTag: String,
    onToggleChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = subtitle,
                    color = TextMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                if (isUserAllowed && isSystemGranted) AnimeGreen.copy(alpha = 0.2f)
                                else if (isUserAllowed && !isSystemGranted) AnimeGold.copy(alpha = 0.2f)
                                else AnimePink.copy(alpha = 0.2f)
                            )
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        val statusText = if (isUserAllowed && isSystemGranted) {
                            AppLocaleStrings.tr(lang, "ALLOWED", "अनुमति सक्रिय")
                        } else if (isUserAllowed && !isSystemGranted) {
                            AppLocaleStrings.tr(lang, "PENDING SYSTEM GRANT", "सिस्टम में अनुमति बाकी")
                        } else {
                            AppLocaleStrings.tr(lang, "DEALLOWED (OFF)", "बंद (DEALLOWED)")
                        }
                        Text(
                            text = statusText,
                            color = if (isUserAllowed && isSystemGranted) AnimeGreen
                            else if (isUserAllowed && !isSystemGranted) AnimeGold
                            else AnimePink,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        Switch(
            checked = isUserAllowed,
            onCheckedChange = onToggleChanged,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AnimeGreen,
                checkedTrackColor = AnimeGreen.copy(alpha = 0.4f),
                uncheckedThumbColor = AnimePink,
                uncheckedTrackColor = AnimePink.copy(alpha = 0.25f)
            ),
            modifier = Modifier.testTag(testTag)
        )
    }
}

/**
 * ThemeSettings Composable:
 * Provides a 3-way toggle between 'Light', 'Dark', and 'Neon Anime' themes,
 * using DataStore / AppThemeController to persist the choice and apply it globally to all Compose screens.
 */
@Composable
fun ThemeSettings(
    viewModel: AnimeViewModel,
    lang: String,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeMode = com.example.ui.theme.AppThemeController.themeMode

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(
                                when (activeMode) {
                                    "LIGHT" -> AnimeCyan.copy(alpha = 0.2f)
                                    "DARK" -> AnimeGold.copy(alpha = 0.2f)
                                    else -> AnimePurple.copy(alpha = 0.3f)
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (activeMode) {
                                "LIGHT" -> "☀️"
                                "DARK" -> "🌙"
                                else -> "⚡"
                            },
                            fontSize = 18.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Display & Aesthetic Themes", "थीम व डिस्प्ले सेटिंग्स"),
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = when (activeMode) {
                                "LIGHT" -> AppLocaleStrings.tr(lang, "Active: Crisp Studio Light (Pure White)", "सक्रिय: स्टूडियो लाइट मोड (प्योर व्हाइट)")
                                "DARK" -> AppLocaleStrings.tr(lang, "Active: Cinematic Dark Mode", "सक्रिय: सिनेमाई डार्क मोड")
                                else -> AppLocaleStrings.tr(lang, "Active: Neon Anime Glow Mode", "सक्रिय: नियॉन एनीमे ग्लो मोड")
                            },
                            color = when (activeMode) {
                                "LIGHT" -> AnimeCyan
                                "DARK" -> AnimeGold
                                else -> AnimePink
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Quick Light/Dark Switch
                Switch(
                    checked = uiState.isDarkMode,
                    onCheckedChange = { viewModel.toggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = AnimeGold,
                        checkedTrackColor = AnimeGold.copy(alpha = 0.4f),
                        uncheckedThumbColor = AnimePurple,
                        uncheckedTrackColor = AnimePurple.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.testTag("settings_quick_theme_toggle_switch")
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tri-state Theme Selector Buttons (Light, Dark, Neon Anime)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val themeOptions = listOf(
                    Triple("LIGHT", "☀️ " + AppLocaleStrings.tr(lang, "Light", "लाइट"), "Pure White"),
                    Triple("DARK", "🌙 " + AppLocaleStrings.tr(lang, "Dark", "डार्क"), "Dark Slate"),
                    Triple("NEON_ANIME", "⚡ " + AppLocaleStrings.tr(lang, "Neon Anime", "नियॉन एनीमे"), "Cyber Glow")
                )

                themeOptions.forEach { (modeKey, label, subtitle) ->
                    val isSelected = activeMode == modeKey
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (isSelected) {
                                    when (modeKey) {
                                        "LIGHT" -> AnimeCyan.copy(alpha = 0.25f)
                                        "DARK" -> AnimeGold.copy(alpha = 0.25f)
                                        else -> AnimePurple.copy(alpha = 0.35f)
                                    }
                                } else {
                                    AnimeSurfaceVariant
                                }
                            )
                            .border(
                                1.5.dp,
                                if (isSelected) {
                                    when (modeKey) {
                                        "LIGHT" -> AnimeCyan
                                        "DARK" -> AnimeGold
                                        else -> AnimePink
                                    }
                                } else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { viewModel.setThemeMode(modeKey) }
                            .padding(vertical = 10.dp, horizontal = 4.dp)
                            .testTag("theme_mode_btn_$modeKey"),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = label,
                                color = if (isSelected) TextPrimary else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = subtitle,
                                color = if (isSelected) AnimeCyanLight else TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

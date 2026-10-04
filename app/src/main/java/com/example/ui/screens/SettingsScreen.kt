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
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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

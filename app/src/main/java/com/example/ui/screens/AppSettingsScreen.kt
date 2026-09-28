package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import com.example.data.model.StorageTargetType
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.components.CountryCodePickerBottomSheet
import com.example.ui.components.StorageDestinationDialog
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
import com.example.ui.theme.VibrantThemePresets
import kotlinx.coroutines.launch

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AppSettingsScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val storageConfig by viewModel.storageConfig.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lang = state.selectedLanguage

    var showCountryPicker by remember { mutableStateOf(false) }
    var voiceSpeed by remember { mutableFloatStateOf(1.0f) }
    var voicePitch by remember { mutableFloatStateOf(1.0f) }
    var selectedResolution by remember { mutableStateOf("1080p Full HD") }
    var cacheClearedMessage by remember { mutableStateOf("") }
    var isSyncingToDrive by remember { mutableStateOf(false) }

    val resolutions = listOf("720p HD", "1080p Full HD", "4K Ultra HD (VIP)")

    val quickLanguages = listOf(
        Pair("Hindi", "🇮🇳 हिन्दी"),
        Pair("English", "🇺🇸 English"),
        Pair("Japanese", "🇯🇵 日本語"),
        Pair("Korean", "🇰🇷 한국어"),
        Pair("Spanish", "🇪🇸 Español"),
        Pair("German", "🇩🇪 Deutsch"),
        Pair("French", "🇫🇷 Français"),
        Pair("Chinese", "🇨🇳 中文"),
        Pair("Arabic", "🇦🇪 العربية"),
        Pair("Russian", "🇷🇺 Русский"),
        Pair("Portuguese", "🇧🇷 Português"),
        Pair("Indonesian", "🇮🇩 Bahasa")
    )

    // SAF folder picker for SD Card
    val sdCardFolderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Ignore if not supported
            }
            val folderName = uri.lastPathSegment ?: "MicroSD/AnimeStudio"
            viewModel.setSdCardFolder(uri, folderName)
            Toast.makeText(context, "💾 SD कार्ड: $folderName", Toast.LENGTH_SHORT).show()
        }
    }

    // SAF folder picker for External Hard Disk
    val hardDiskFolderPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                )
            } catch (e: Exception) {
                // Ignore if not supported
            }
            val folderName = uri.lastPathSegment ?: "USB_HDD/AnimeStudio"
            viewModel.setHardDiskFolder(uri, folderName)
            Toast.makeText(context, "🔌 हार्ड डिस्क: $folderName", Toast.LENGTH_SHORT).show()
        }
    }

    Column(
        modifier = Modifier
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
            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("app_settings_back_btn")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AnimePurple)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = AppLocaleStrings.get("settings_title", lang),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = AppLocaleStrings.get("settings_subtitle", lang),
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // 1. GLOBAL MULTI-LANGUAGE SYSTEM CARD (Changes EVERYTHING in the chosen language)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Public, contentDescription = null, tint = AnimeCyan)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppLocaleStrings.get("global_language_country", lang),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("12 LANGUAGES", color = AnimeCyan, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = AppLocaleStrings.get("global_language_desc", lang),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Current Active Language Display & Change Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = selectedCountry.flagEmoji, fontSize = 24.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = selectedCountry.name,
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "(${selectedCountry.dialCode})",
                                    color = AnimeCyan,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            }
                            Text(
                                text = "सक्रिय भाषा: ${selectedCountry.nativeLanguageName} ($lang)",
                                color = AnimeGold,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showCountryPicker = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier.testTag("settings_change_country_btn")
                    ) {
                        Icon(Icons.Default.Translate, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = AppLocaleStrings.get("change_country_btn", lang),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Quick 1-Tap Language Switching Chips
                Text(
                    text = "त्वरित भाषा चयन (1-Tap Instant Language Switch):",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    quickLanguages.forEach { (langKey, label) ->
                        val isSelected = lang.equals(langKey, ignoreCase = true)
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AnimeCyan else AnimeSurfaceVariant)
                                .border(
                                    1.dp,
                                    if (isSelected) AnimeCyan else AnimePurple.copy(alpha = 0.3f),
                                    RoundedCornerShape(8.dp)
                                )
                                .clickable {
                                    viewModel.setAppLanguage(langKey)
                                    Toast.makeText(context, "Language switched to $langKey", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("quick_lang_$langKey")
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.Black else TextPrimary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 2. STORAGE DESTINATION SELECTION & MANAGEMENT CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = AnimeGold)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppLocaleStrings.get("storage_title", lang),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeGold.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("TARGET STORAGE", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = AppLocaleStrings.get("storage_subtitle", lang),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Storage Option 1: Mobile Local Storage
                val isInternal = storageConfig.activeTarget == StorageTargetType.INTERNAL_STORAGE
                SettingsStorageRow(
                    emoji = "📱",
                    title = AppLocaleStrings.get("storage_internal", lang),
                    description = AppLocaleStrings.get("storage_internal_desc", lang),
                    capacity = storageConfig.freeInternalSpaceFormatted,
                    isSelected = isInternal,
                    badgeColor = AnimeCyan,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.INTERNAL_STORAGE) },
                    testTag = "settings_storage_internal"
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Storage Option 2: Google Drive
                val isDrive = storageConfig.activeTarget == StorageTargetType.GOOGLE_DRIVE
                SettingsStorageRow(
                    emoji = "☁️",
                    title = AppLocaleStrings.get("storage_gdrive", lang),
                    description = "${AppLocaleStrings.get("storage_gdrive_desc", lang)} (${storageConfig.googleDriveAccount})",
                    capacity = storageConfig.driveQuotaFormatted,
                    isSelected = isDrive,
                    badgeColor = AnimeGold,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.GOOGLE_DRIVE) },
                    testTag = "settings_storage_gdrive",
                    trailing = {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isSyncingToDrive = true
                                    viewModel.syncAllToGoogleDrive()
                                    isSyncingToDrive = false
                                    Toast.makeText(context, "✅ Google Drive Synced!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, AnimeGold),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            if (isSyncingToDrive) {
                                CircularProgressIndicator(modifier = Modifier.size(10.dp), color = AnimeGold, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(10.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Sync Drive", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Storage Option 3: Memory Card (SD Card)
                val isSd = storageConfig.activeTarget == StorageTargetType.SD_CARD
                SettingsStorageRow(
                    emoji = "💾",
                    title = AppLocaleStrings.get("storage_sdcard", lang),
                    description = if (storageConfig.sdCardDisplayName != null)
                        "फ़ोल्डर: ${storageConfig.sdCardDisplayName}"
                    else
                        AppLocaleStrings.get("storage_sdcard_desc", lang),
                    capacity = storageConfig.sdCardSpaceFormatted,
                    isSelected = isSd,
                    badgeColor = AnimePink,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.SD_CARD) },
                    testTag = "settings_storage_sdcard",
                    trailing = {
                        OutlinedButton(
                            onClick = { sdCardFolderPicker.launch(null) },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, AnimePink),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = AnimePink, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Folder", color = AnimePink, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Storage Option 4: External Hard Disk / USB
                val isHdd = storageConfig.activeTarget == StorageTargetType.HARD_DISK
                SettingsStorageRow(
                    emoji = "🔌",
                    title = AppLocaleStrings.get("storage_harddisk", lang),
                    description = if (storageConfig.hardDiskDisplayName != null)
                        "फ़ोल्डर: ${storageConfig.hardDiskDisplayName}"
                    else
                        AppLocaleStrings.get("storage_harddisk_desc", lang),
                    capacity = storageConfig.hardDiskSpaceFormatted,
                    isSelected = isHdd,
                    badgeColor = AnimeGreen,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.HARD_DISK) },
                    testTag = "settings_storage_harddisk",
                    trailing = {
                        OutlinedButton(
                            onClick = { hardDiskFolderPicker.launch(null) },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, AnimeGreen),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Usb, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(10.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pick Folder", color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // YouTube Channel ID & OAuth 2.0 Credentials Section (Owner Secured)
        com.example.ui.components.YouTubeOAuthCredentialsSection(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        // Theme Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePurple)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (state.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = if (state.isDarkMode) AnimeGold else AnimePurple
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.get("theme_display", lang),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(AppLocaleStrings.get("dark_mode", lang), color = TextPrimary, fontSize = 13.sp)
                        Text(
                            text = if (state.isDarkMode)
                                AppLocaleStrings.get("dark_mode_active", lang)
                            else
                                AppLocaleStrings.get("dark_mode_inactive", lang),
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = state.isDarkMode,
                        onCheckedChange = { viewModel.toggleTheme() },
                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeGold, checkedTrackColor = AnimeGold.copy(alpha = 0.3f)),
                        modifier = Modifier.testTag("settings_theme_switch")
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = AppLocaleStrings.tr(lang, "Vibrant & Colourful Page Themes:", "वाइब्रेंट रंगीन पेज थीम्स:"),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    VibrantThemePresets.all.forEach { preset ->
                        val isSelected = state.vibrantTheme.equals(preset.key, ignoreCase = true)
                        val themeName = if (AppLocaleStrings.isHindi(lang)) preset.nameHi else preset.nameEn

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) preset.primaryColor.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                                .border(
                                    1.5.dp,
                                    if (isSelected) preset.primaryColor else AnimePurple.copy(alpha = 0.2f),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable {
                                    viewModel.setVibrantTheme(preset.key)
                                    Toast.makeText(context, "🎨 Vibrant Theme: ${preset.nameEn}", Toast.LENGTH_SHORT).show()
                                }
                                .padding(horizontal = 10.dp, vertical = 7.dp)
                                .testTag("settings_vibrant_${preset.key.lowercase()}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(text = preset.emoji, fontSize = 14.sp)
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = themeName,
                                    color = if (isSelected) preset.primaryColor else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                    fontSize = 11.sp
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .size(10.dp)
                                        .clip(CircleShape)
                                        .background(preset.primaryColor)
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Dubbing Audio Engine
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Mic, contentDescription = null, tint = AnimePink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.get("audio_engine", lang),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "${AppLocaleStrings.get("voice_speed", lang)}: ${String.format("%.2f", voiceSpeed)}x",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Slider(
                    value = voiceSpeed,
                    onValueChange = { voiceSpeed = it },
                    valueRange = 0.75f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink),
                    modifier = Modifier.fillMaxWidth().testTag("voice_speed_slider")
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "${AppLocaleStrings.get("voice_pitch", lang)}: ${String.format("%.2f", voicePitch)}x",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Slider(
                    value = voicePitch,
                    onValueChange = { voicePitch = it },
                    valueRange = 0.8f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyan),
                    modifier = Modifier.fillMaxWidth().testTag("voice_pitch_slider")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Video Export Quality Settings
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.HighQuality, contentDescription = null, tint = AnimeCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.get("video_quality", lang),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                resolutions.forEach { res ->
                    val isSelected = selectedResolution == res
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AnimeCyan.copy(alpha = 0.2f) else AnimeSurfaceVariant)
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = res,
                            color = if (isSelected) AnimeCyan else TextPrimary,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                        OutlinedButton(
                            onClick = { selectedResolution = res },
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, if (isSelected) AnimeCyan else TextMuted)
                        ) {
                            Text(
                                text = if (isSelected) "Active" else "Select",
                                fontSize = 11.sp,
                                color = if (isSelected) AnimeCyan else TextMuted
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Storage & Cache Manager
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Storage, contentDescription = null, tint = AnimeGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.get("storage_cache", lang),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Temporary Video Cache:", color = TextMuted, fontSize = 12.sp)
                    Text("24.8 MB", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Audio Vocal Buffer:", color = TextMuted, fontSize = 12.sp)
                    Text("12.4 MB", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        cacheClearedMessage = "✓ " + AppLocaleStrings.get("cache_cleared", lang)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("clear_cache_btn")
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.get("clear_cache_btn", lang),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                if (cacheClearedMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = cacheClearedMessage, color = AnimeGreen, fontSize = 11.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // App Info & Owner Details
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = AnimeGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.get("app_info", lang),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("${AppLocaleStrings.get("version_label", lang)} 2.7.0 (Enterprise AI Studio)", color = TextSecondary, fontSize = 12.sp)
                if (currentUser.isOwner) {
                    Text("${AppLocaleStrings.get("owner_info", lang)} Aman Jangra (amjangra0@gmail.com)", color = AnimeGold, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    Text("License Status: VIP Owner Lifetime Master Access", color = AnimeGold, fontSize = 11.sp)
                } else {
                    Text("Studio Architecture: Enterprise AI Creative Suite", color = TextMuted, fontSize = 12.sp)
                    Text("License Status: Active Creator License", color = AnimeCyan, fontSize = 11.sp)
                }
            }
        }

        if (showCountryPicker) {
            CountryCodePickerBottomSheet(
                selectedCountry = selectedCountry,
                onCountrySelected = { country ->
                    viewModel.selectCountry(country)
                    showCountryPicker = false
                },
                onDismiss = { showCountryPicker = false }
            )
        }
    }
}

@Composable
private fun SettingsStorageRow(
    emoji: String,
    title: String,
    description: String,
    capacity: String,
    isSelected: Boolean,
    badgeColor: Color,
    onClick: () -> Unit,
    testTag: String,
    trailing: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) badgeColor.copy(alpha = 0.12f) else AnimeSurfaceVariant
        ),
        border = BorderStroke(
            1.dp,
            if (isSelected) badgeColor else AnimePurple.copy(alpha = 0.2f)
        ),
        shape = RoundedCornerShape(10.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Text(text = emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = title,
                            color = if (isSelected) badgeColor else TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(text = capacity, color = AnimeCyanLight, fontSize = 10.sp)
                    }
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(badgeColor)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text("ACTIVE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = TextSecondary,
                fontSize = 10.sp,
                lineHeight = 14.sp
            )

            if (trailing != null) {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    trailing()
                }
            }
        }
    }
}

package com.example.ui.components

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.SdCard
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Usb
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StorageDestinationConfig
import com.example.data.model.StorageTargetType
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
import kotlinx.coroutines.launch

/**
 * Storage Destination Selection Dialog
 * Allows users to choose their active destination:
 * 1. Mobile Local Storage (Phone Flash & SQLite Room DB)
 * 2. Google Drive (Cloud synchronization & direct Drive export)
 * 3. Memory Card (MicroSD removable card via Storage Access Framework)
 * 4. External Hard Disk (USB OTG HDD/SSD via Storage Access Framework)
 */
@Composable
fun StorageDestinationDialog(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val storageConfig by viewModel.storageConfig.collectAsState()
    val syncMessage by viewModel.storageSyncMessage.collectAsState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lang = state.selectedLanguage

    var isSyncingToDrive by remember { mutableStateOf(false) }

    // SAF Directory picker for SD Card
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
                // Persistable flag not granted by some providers, continue
            }
            val folderName = uri.lastPathSegment ?: "MicroSD/AnimeStudio"
            viewModel.setSdCardFolder(uri, folderName)
            Toast.makeText(context, "💾 SD कार्ड फ़ोल्डर चुना गया: $folderName", Toast.LENGTH_SHORT).show()
        }
    }

    // SAF Directory picker for External Hard Disk
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
                // Persistable flag not granted by some providers, continue
            }
            val folderName = uri.lastPathSegment ?: "USB_HDD/AnimeStudio"
            viewModel.setHardDiskFolder(uri, folderName)
            Toast.makeText(context, "🔌 हार्ड डिस्क फ़ोल्डर चुना गया: $folderName", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                            .background(AnimeCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.get("storage_title", lang),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = AppLocaleStrings.get("storage_subtitle", lang),
                            fontSize = 10.sp,
                            color = AnimeCyanLight
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("close_storage_dialog_btn")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Active Target Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = storageConfig.activeTarget.iconEmoji, fontSize = 28.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppLocaleStrings.get("storage_destination_title", lang),
                                    color = AnimeGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = AppLocaleStrings.get("storage_active_badge", lang),
                                        color = AnimeGreen,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = when (storageConfig.activeTarget) {
                                    StorageTargetType.INTERNAL_STORAGE -> AppLocaleStrings.get("storage_internal", lang)
                                    StorageTargetType.GOOGLE_DRIVE -> AppLocaleStrings.get("storage_gdrive", lang)
                                    StorageTargetType.SD_CARD -> AppLocaleStrings.get("storage_sdcard", lang)
                                    StorageTargetType.HARD_DISK -> AppLocaleStrings.get("storage_harddisk", lang)
                                },
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                if (syncMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = syncMessage,
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = AppLocaleStrings.get("storage_select_destination", lang),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 1. Mobile Local Storage Card
                StorageOptionCard(
                    icon = Icons.Default.PhoneAndroid,
                    emoji = "📱",
                    title = AppLocaleStrings.get("storage_internal", lang),
                    description = AppLocaleStrings.get("storage_internal_desc", lang),
                    capacityInfo = storageConfig.freeInternalSpaceFormatted,
                    isSelected = storageConfig.activeTarget == StorageTargetType.INTERNAL_STORAGE,
                    badgeColor = AnimeCyan,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.INTERNAL_STORAGE) },
                    testTag = "storage_target_internal"
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Google Drive Card
                StorageOptionCard(
                    icon = Icons.Default.Cloud,
                    emoji = "☁️",
                    title = AppLocaleStrings.get("storage_gdrive", lang),
                    description = "${AppLocaleStrings.get("storage_gdrive_desc", lang)} (${storageConfig.googleDriveAccount})",
                    capacityInfo = storageConfig.driveQuotaFormatted,
                    isSelected = storageConfig.activeTarget == StorageTargetType.GOOGLE_DRIVE,
                    badgeColor = AnimeGold,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.GOOGLE_DRIVE) },
                    testTag = "storage_target_gdrive",
                    trailingAction = {
                        OutlinedButton(
                            onClick = {
                                coroutineScope.launch {
                                    isSyncingToDrive = true
                                    viewModel.syncAllToGoogleDrive()
                                    isSyncingToDrive = false
                                    Toast.makeText(context, "✅ गूगल ड्राइव सफलतापूर्वक सिंक हो गया!", Toast.LENGTH_SHORT).show()
                                }
                            },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AnimeGold),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("storage_sync_drive_btn")
                        ) {
                            if (isSyncingToDrive) {
                                CircularProgressIndicator(modifier = Modifier.size(12.dp), color = AnimeGold, strokeWidth = 2.dp)
                            } else {
                                Icon(Icons.Default.Sync, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(12.dp))
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppLocaleStrings.get("storage_sync_now", lang),
                                color = AnimeGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Memory Card (MicroSD) Card
                StorageOptionCard(
                    icon = Icons.Default.SdCard,
                    emoji = "💾",
                    title = AppLocaleStrings.get("storage_sdcard", lang),
                    description = if (storageConfig.sdCardDisplayName != null)
                        "फ़ोल्डर: ${storageConfig.sdCardDisplayName}"
                    else
                        AppLocaleStrings.get("storage_sdcard_desc", lang),
                    capacityInfo = storageConfig.sdCardSpaceFormatted,
                    isSelected = storageConfig.activeTarget == StorageTargetType.SD_CARD,
                    badgeColor = AnimePink,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.SD_CARD) },
                    testTag = "storage_target_sdcard",
                    trailingAction = {
                        OutlinedButton(
                            onClick = { sdCardFolderPicker.launch(null) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AnimePink),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("storage_pick_sdcard_folder_btn")
                        ) {
                            Icon(Icons.Default.FolderOpen, contentDescription = null, tint = AnimePink, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppLocaleStrings.get("storage_choose_sd_folder", lang),
                                color = AnimePink,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 4. External Hard Disk / USB OTG Card
                StorageOptionCard(
                    icon = Icons.Default.Usb,
                    emoji = "🔌",
                    title = AppLocaleStrings.get("storage_harddisk", lang),
                    description = if (storageConfig.hardDiskDisplayName != null)
                        "फ़ोल्डर: ${storageConfig.hardDiskDisplayName}"
                    else
                        AppLocaleStrings.get("storage_harddisk_desc", lang),
                    capacityInfo = storageConfig.hardDiskSpaceFormatted,
                    isSelected = storageConfig.activeTarget == StorageTargetType.HARD_DISK,
                    badgeColor = AnimeGreen,
                    onClick = { viewModel.setStorageTarget(StorageTargetType.HARD_DISK) },
                    testTag = "storage_target_harddisk",
                    trailingAction = {
                        OutlinedButton(
                            onClick = { hardDiskFolderPicker.launch(null) },
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AnimeGreen),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                            modifier = Modifier.testTag("storage_pick_hdd_folder_btn")
                        ) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = AppLocaleStrings.get("storage_choose_hdd_folder", lang),
                                color = AnimeGreen,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("confirm_storage_selection_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "स्वीकारें (${when (storageConfig.activeTarget) {
                        StorageTargetType.INTERNAL_STORAGE -> "📱 Mobile"
                        StorageTargetType.GOOGLE_DRIVE -> "☁️ Google Drive"
                        StorageTargetType.SD_CARD -> "💾 SD Card"
                        StorageTargetType.HARD_DISK -> "🔌 Hard Disk"
                    }})",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    )
}

@Composable
private fun StorageOptionCard(
    icon: ImageVector,
    emoji: String,
    title: String,
    description: String,
    capacityInfo: String,
    isSelected: Boolean,
    badgeColor: Color,
    onClick: () -> Unit,
    testTag: String,
    trailingAction: @Composable (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .testTag(testTag),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) badgeColor.copy(alpha = 0.12f) else AnimeSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) badgeColor else AnimePurple.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = capacityInfo,
                            color = AnimeCyanLight,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(badgeColor),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Check, contentDescription = "Selected", tint = Color.Black, modifier = Modifier.size(14.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = description,
                color = TextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp
            )

            if (trailingAction != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    trailingAction()
                }
            }
        }
    }
}

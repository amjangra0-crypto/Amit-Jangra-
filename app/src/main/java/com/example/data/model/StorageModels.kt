package com.example.data.model

/**
 * Storage Target Destinations supported by the Anime Studio
 * - Mobile Local Storage (Device Internal / Room SQLite / Movies)
 * - Google Drive (Cloud synchronization & projects backup)
 * - Memory Card (MicroSD removable storage)
 * - Hard Disk (External USB HDD, SSD or USB Flash Drive)
 */
enum class StorageTargetType(
    val id: String,
    val defaultLabel: String,
    val iconEmoji: String,
    val iconName: String,
    val description: String
) {
    INTERNAL_STORAGE(
        id = "internal",
        defaultLabel = "Mobile Local Storage",
        iconEmoji = "📱",
        iconName = "phone_android",
        description = "High-speed internal flash storage & offline SQLite Room database"
    ),
    GOOGLE_DRIVE(
        id = "google_drive",
        defaultLabel = "Google Drive",
        iconEmoji = "☁️",
        iconName = "cloud",
        description = "Automatic cloud backup, project sync & shareable drive links"
    ),
    SD_CARD(
        id = "sd_card",
        defaultLabel = "Memory Card (SD Card)",
        iconEmoji = "💾",
        iconName = "sd_card",
        description = "Removable MicroSD card storage for large 4K video exports & backups"
    ),
    HARD_DISK(
        id = "hard_disk",
        defaultLabel = "External Hard Disk / USB",
        iconEmoji = "🔌",
        iconName = "usb",
        description = "USB OTG external hard drive, SSD or flash drive archive"
    )
}

data class StorageDestinationConfig(
    val activeTarget: StorageTargetType = StorageTargetType.INTERNAL_STORAGE,
    val sdCardFolderUri: String? = null,
    val sdCardDisplayName: String? = null,
    val hardDiskFolderUri: String? = null,
    val hardDiskDisplayName: String? = null,
    val googleDriveAccount: String = "amjangra0@gmail.com",
    val googleDriveFolder: String = "AnimeStudioAI_Projects",
    val isGoogleDriveLinked: Boolean = true,
    val autoSyncExports: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis(),
    val totalInternalSpaceFormatted: String = "128 GB",
    val freeInternalSpaceFormatted: String = "48.2 GB Available",
    val driveQuotaFormatted: String = "11.4 GB / 15 GB Free",
    val sdCardSpaceFormatted: String = "58.6 GB Available",
    val hardDiskSpaceFormatted: String = "840 GB Available"
)

data class StorageVolumeInfo(
    val type: StorageTargetType,
    val title: String,
    val pathOrAccount: String,
    val freeSpaceFormatted: String,
    val totalSpaceFormatted: String,
    val isConnected: Boolean = true,
    val isConfigured: Boolean = true,
    val fileCount: Int = 0
)

data class StorageExportResult(
    val success: Boolean,
    val targetType: StorageTargetType,
    val targetPathOrUri: String,
    val fileName: String,
    val fileSizeBytes: Long,
    val displayMessage: String
)

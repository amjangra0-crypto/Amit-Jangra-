package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_scripts")
data class SavedScriptEntity(
    @PrimaryKey val id: String,
    val title: String,
    val originalPrompt: String,
    val inputSourceType: String,
    val genre: String,
    val artStyle: String,
    val language: String,
    val synopsis: String,
    val scriptJson: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "custom_characters")
data class CustomCharacterEntity(
    @PrimaryKey val id: String,
    val name: String,
    val gender: String,
    val role: String,
    val personality: String,
    val voicePitch: Float,
    val voiceSpeed: Float,
    val voiceType: String,
    val avatarDrawableName: String,
    val promptVisualDescription: String = "",
    val hairStyle: String = "Spiky Shonen",
    val hairColor: String = "Silver White",
    val eyeColor: String = "Sapphire Blue",
    val outfit: String = "Cyber Shinobi Battle Suit",
    val outfitColor: String = "Obsidian & Neon Purple",
    val accessoryAura: String = "Sakura Sparks",
    val expression: String = "Confident Smirk",
    val voiceGender: String = "Female",
    val voicePersona: String = "Sweet Kawaii Heroine",
    val voiceAccent: String = "Standard Anime (Japanese Cadence)",
    val sampleDialogue: String = "私を信じて！一緒に未来を変えよう！",
    val emotion: String = "Joy",
    val hairLength: Float = 1.0f,
    val hairVolume: Float = 1.0f,
    val hairShine: Float = 0.5f,
    val eyeSize: Float = 1.0f,
    val eyeTilt: Float = 0.0f,
    val eyePupilGlow: Float = 0.7f,
    val outfitFit: Float = 1.0f,
    val outfitGlow: Float = 0.5f,
    val capeLength: Float = 1.0f
)

@Entity(tableName = "saved_visual_elements")
data class SavedVisualElementEntity(
    @PrimaryKey val id: String,
    val title: String,
    val sourceMode: String,
    val sourceQuery: String,
    val visualType: String,
    val artStyle: String,
    val promptDescription: String,
    val primaryHexColor: String,
    val secondaryHexColor: String,
    val atmosphericEffect: String,
    val cameraMotion: String,
    val visualDrawableName: String,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "admin_access_settings")
data class AdminAccessEntity(
    @PrimaryKey val id: Int = 1,
    val ownerEmail: String = "amjangra0@gmail.com",
    val isOwnerMode: Boolean = true,
    val isGlobalFreeEnabled: Boolean = false,
    val authorizedFreeEmails: String = "amjangra0@gmail.com,friend@creator.com",
    val activePromoCodes: String = "ANIME2026,VIPFREE,STUDIOFREE",
    val currentTier: String = "plan_vip",
    val remainingCredits: Int = 999999,
    val isFaqAgentEnabledForUsers: Boolean = true
)

@Entity(tableName = "exported_videos")
data class ExportedVideoEntity(
    @PrimaryKey val id: String,
    val scriptId: String,
    val title: String,
    val durationSeconds: Int,
    val filePath: String,
    val fileSizeBytes: Long,
    val resolution: String = "1280x720 (720p HD)",
    val fps: Int = 30,
    val language: String,
    val artStyle: String,
    val sceneCount: Int = 4,
    val status: String = "COMPLETED", // COMPLETED, PROCESSING, FAILED
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Room Entity storing project metadata:
 * - Script history and dialogue drafts
 * - Character configurations (avatars, hair, outfit, voice)
 * - Links to generated audio, video, and subtitle files
 */
@Entity(tableName = "project_metadata")
data class ProjectMetadataEntity(
    @PrimaryKey val id: String,
    val title: String,
    val synopsis: String = "",
    val genre: String = "Action / Shonen",
    val artStyle: String = "Modern Shonen Anime",
    val aspectRatio: String = "16:9",
    val dubbingLanguage: String = "Japanese",
    val voiceMood: String = "Natural",
    val scriptHistoryJson: String,
    val characterConfigsJson: String,
    val audioFilePath: String = "",
    val videoFilePath: String = "",
    val srtSubtitlePath: String = "",
    val thumbnailUri: String = "",
    val durationSeconds: Int = 30,
    val sceneCount: Int = 4,
    val isExported: Boolean = false,
    val exportStoragePath: String = "",
    val updatedAt: Long = System.currentTimeMillis(),
    val createdAt: Long = System.currentTimeMillis()
)

/**
 * Room Entity storing AI FAQ incident notifications and bug reports for the Owner.
 */
@Entity(tableName = "faq_support_incidents")
data class FaqSupportIncidentEntity(
    @PrimaryKey val id: String,
    val userEmailOrId: String = "user@animeapp.io",
    val category: String, // "SUBSCRIPTION", "BILLING", "PLAYBACK", "BUG_REPORT", "GENERAL"
    val userQuery: String,
    val aiResolution: String,
    val suggestedAutoFix: String = "",
    val isBugReport: Boolean = false,
    val isOwnerNotified: Boolean = true,
    val timestamp: Long = System.currentTimeMillis()
)

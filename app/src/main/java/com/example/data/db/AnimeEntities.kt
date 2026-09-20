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
    val sampleDialogue: String = "私を信じて！一緒に未来を変えよう！"
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
    val remainingCredits: Int = 999999
)

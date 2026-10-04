package com.example.data.model

/**
 * Data models for AI Adaptive Command Learning, Self-Improvement, and Suggestion Engine.
 */
data class LearnedCommandRecord(
    val id: String = java.util.UUID.randomUUID().toString(),
    val rawCommand: String,
    val improvedCommand: String,
    val suggestions: List<String> = emptyList(),
    val genre: String = "Shonen Action",
    val keywords: List<String> = emptyList(),
    val timestamp: Long = System.currentTimeMillis(),
    val userId: String = "owner_amjangra0",
    val useCount: Int = 1
)

data class CommandLearningSettings(
    val isOwnerActive: Boolean = true,
    val isGloballyActiveForOthers: Boolean = false,
    val authorizedUserIds: Set<String> = setOf("creator_ren", "vip_sakura"),
    val autoSuggestEnabled: Boolean = true,
    val learningLevel: Int = 1,
    val totalCommandsAnalyzed: Int = 0,
    val lastUpdated: Long = System.currentTimeMillis()
)

data class ManagedUserAccess(
    val userId: String,
    val displayName: String,
    val email: String,
    val role: String,
    val isGranted: Boolean,
    val lastActive: String = "Active today"
)

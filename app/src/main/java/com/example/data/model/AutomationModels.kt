package com.example.data.model

/**
 * YouTube & Social Platforms Connection and Channel Data
 */
enum class ChannelPlatform(val id: String, val displayName: String, val iconEmoji: String, val colorHex: String) {
    YOUTUBE("youtube", "YouTube", "▶️", "#FF0000"),
    INSTAGRAM("instagram", "Instagram Reels", "📸", "#E1306C"),
    FACEBOOK("facebook", "Facebook Video & Reels", "📘", "#1877F2"),
    TIKTOK("tiktok", "TikTok", "🎵", "#00F2FE")
}

enum class UploadPrivacyStatus(val id: String, val title: String, val description: String) {
    PUBLIC("public", "सार्वजनिक (Public)", "Upload directly to public audience"),
    DRAFT_PRIVATE("draft", "ड्राफ्ट / प्राइवेट (Draft / Private)", "Save as Draft / Private first for safety before publishing"),
    UNLISTED("unlisted", "अनलिस्टेड (Unlisted)", "Visible only via link for testing"),
    SCHEDULED("scheduled", "शेड्यूल (Scheduled)", "Publish automatically at set time")
}

data class ConnectedChannel(
    val id: String = System.currentTimeMillis().toString(),
    val platform: ChannelPlatform = ChannelPlatform.YOUTUBE,
    val channelUrl: String = "https://youtube.com/@AnimeStudioCreator",
    val channelHandle: String = "@AnimeStudioCreator",
    val channelName: String = "My Personal Anime Channel",
    val channelAvatarUrl: String = "",
    val subscriberCount: String = "12.4K Subscribers",
    val isConnected: Boolean = true,
    val defaultPrivacy: UploadPrivacyStatus = UploadPrivacyStatus.PUBLIC,
    val defaultCategory: String = "Animation / Film",
    val defaultTags: String = "anime, animation, ai anime, shonen, webtoon, manga",
    val autoUploadAfterReview: Boolean = true,
    val requireOwnerVerificationBeforeUpload: Boolean = true, // When true: won't upload until Owner verifies / gives permission
    val autoPublishTimerMinutes: Int = 120, // fallback if verification toggle is disabled
    val connectedAt: Long = System.currentTimeMillis()
)

enum class EpisodeReviewStatus(val id: String, val label: String, val emoji: String) {
    GENERATING("generating", "AI वीडियो बन रहा है...", "⚡"),
    PENDING_REVIEW("pending_review", "रिव्यू की प्रतीक्षा (Pending Review)", "⏳"),
    APPROVED("approved", "ग्रीन सिग्नल मिला (Approved)", "🟢"),
    SCHEDULED("scheduled", "शेड्यूल टाइम पर अपलोड होगा", "⏰"),
    UPLOADED("uploaded", "प्लेटफॉर्म पर अपलोड हो गया", "✅"),
    DRAFT_SAVED("draft_saved", "ड्राफ्ट में सेव हुआ (Draft Saved)", "📝"),
    REJECTED("rejected", "अस्वीकृत (Rejected)", "❌")
}

data class WebSeriesSchedule(
    val id: String = System.currentTimeMillis().toString(),
    val seriesTitle: String = "The Cyber Shinobi 2099",
    val genre: String = "Sci-Fi Cyberpunk Anime",
    val artStyle: String = "Japanese Anime (Makoto Shinkai)",
    val episodesPerDay: Int = 1, // User command: 1, 2, or 3 episodes per day
    val scheduledPublishTime: String = "18:00 (6:00 PM)",
    val scheduledHour: Int = 18,
    val scheduledMinute: Int = 0,
    val totalEpisodesPlanned: Int = 12,
    val currentEpisodeIndex: Int = 1,
    val isDailyAutomationActive: Boolean = true,
    val requireOwnerVerificationBeforeUpload: Boolean = true, // When active: stops auto upload until owner verifies
    val defaultUploadPrivacy: UploadPrivacyStatus = UploadPrivacyStatus.PUBLIC, // Public vs Draft / Private
    val autoPublishIfNoReview: Boolean = false,
    val fallbackTimerHours: Int = 2,
    val targetChannelUrl: String = "https://youtube.com/@AnimeStudioCreator",
    val promptLoreBible: String = "Neo Kyoto year 2099. Cyber samurai warriors battle corrupt neon mega-corporations using enchanted plasma katanas.",
    val createdAt: Long = System.currentTimeMillis()
)

data class AutomatedEpisode(
    val id: String = System.currentTimeMillis().toString(),
    val seriesId: String = "default_series",
    val episodeNumber: Int = 1,
    val title: String = "Episode 1: Awakening of the Plasma Blade",
    val synopsis: String = "Ren awakens his plasma blade in the neon rain of Neo Kyoto.",
    val scriptId: String = "",
    val durationSeconds: Int = 60,
    val videoFilePath: String = "",
    val videoShareUrl: String = "",
    val status: EpisodeReviewStatus = EpisodeReviewStatus.PENDING_REVIEW,
    val generatedAtMillis: Long = System.currentTimeMillis(),
    val reviewDeadlineMillis: Long = System.currentTimeMillis() + (2 * 60 * 60 * 1000L), // 2 hours
    val uploadedPlatform: ChannelPlatform = ChannelPlatform.YOUTUBE,
    val uploadedUrl: String = "",
    val uploadPrivacy: UploadPrivacyStatus = UploadPrivacyStatus.PUBLIC,
    val viewCountEstimate: String = "Just Published",
    val userReviewNote: String = "",
    val isGreenSignalGiven: Boolean = false,
    val originalPrompt: String = "Cyberpunk samurai duel under crimson cherry blossoms in futuristic Neo-Tokyo rain",
    val aiDirectorEnrichedPrompt: String = "Cinematic 4K Makoto Shinkai style: Dynamic camera panning, electric katana sparks reflecting in puddle reflections, binaural atmospheric thunder audio, 60fps high motion choreography",
    val viralityScore: Int = 94,
    val fps: Int = 60,
    val resolution: String = "1080p Full HD"
)

/**
 * Owner-controlled Monetization & Access Configuration for Automation Trend Horizon
 */
enum class TrendHorizonAccessTier(val title: String, val description: String) {
    FREE_FOR_ALL("Free for All Creators (मुफ़्त)", "Everyone can freely research viral trends and run automated video generation pipelines"),
    VIP_PAID_ONLY("VIP & Paid Subscribers Only (पेड / वीआईपी)", "Strictly requires PRO Creator or VIP Lifetime subscription to access trend horizon features"),
    CREDITS_PER_USE("Pay-Per-Use with Credits (क्रेडिट्स)", "Charges 10 wallet credits per trend horizon video generation pipeline")
}

data class TrendHorizonConfig(
    val accessTier: TrendHorizonAccessTier = TrendHorizonAccessTier.FREE_FOR_ALL,
    val creditCostPerRun: Int = 10,
    val isOwnerOverrideActive: Boolean = true,
    val totalTrendPipelinesRun: Int = 142
)

/**
 * Kotlin Flow Tracking for Video Generation Tasks
 */
enum class GenerationStage(val label: String, val stepIndex: Int, val totalSteps: Int = 5) {
    IDLE("Ready for Creation", 0),
    SCRIPT_WRITING("Writing Cinematic Anime Script & Dialogues", 1),
    STORYBOARD_GENERATION("Synthesizing Visual Storyboard & Character Poses", 2),
    VOICE_DUBBING("Generating Character & Explainer Audio Dubbing", 3),
    MUSIC_COMPOSITION("Composing Dynamic Lyria AI Background Score", 4),
    RENDERING_ENCODING("Encoding Final MP4 Video at Target FPS", 5),
    COMPLETED("Video Generation Completed", 5),
    FAILED("Video Generation Error", 0)
}

data class GenerationTaskProgress(
    val stage: GenerationStage = GenerationStage.IDLE,
    val progressPercent: Float = 0f,
    val stageDescription: String = "Standing by",
    val activeTaskName: String = "",
    val elapsedSeconds: Int = 0,
    val etaSeconds: Int = 0,
    val isGenerating: Boolean = false
)

/**
 * Lyria AI Music Generation Model
 */
data class LyriaSoundtrack(
    val id: String = System.currentTimeMillis().toString(),
    val title: String = "Sakura Cyber Blade Theme",
    val prompt: String = "Epic orchestral anime battle music with traditional shamisen and heavy synth bass",
    val mood: String = "Epic Battle",
    val durationSec: Int = 30,
    val tempoBpm: Int = 140,
    val keySignature: String = "D Minor",
    val waveformData: List<Float> = emptyList(),
    val audioUrlOrPath: String = "",
    val createdAt: Long = System.currentTimeMillis()
)


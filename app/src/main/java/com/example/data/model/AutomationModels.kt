package com.example.data.model

/**
 * YouTube & Social Platforms Connection and Channel Data
 */
enum class ChannelPlatform(val id: String, val displayName: String, val iconEmoji: String, val colorHex: String) {
    YOUTUBE("youtube", "YouTube", "▶️", "#FF0000"),
    INSTAGRAM("instagram", "Instagram Reels", "📸", "#E1306C"),
    TIKTOK("tiktok", "TikTok", "🎵", "#00F2FE")
}

enum class UploadPrivacyStatus(val id: String, val title: String, val description: String) {
    PUBLIC("public", "सार्वजनिक (Public)", "Upload directly to public audience"),
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
    val autoPublishTimerMinutes: Int = 120, // 2 hours fallback if no review given
    val connectedAt: Long = System.currentTimeMillis()
)

enum class EpisodeReviewStatus(val id: String, val label: String, val emoji: String) {
    GENERATING("generating", "AI वीडियो बन रहा है...", "⚡"),
    PENDING_REVIEW("pending_review", "रिव्यू की प्रतीक्षा (Pending Review)", "⏳"),
    APPROVED("approved", "ग्रीन सिग्नल मिला (Approved)", "🟢"),
    SCHEDULED("scheduled", "शेड्यूल टाइम पर अपलोड होगा", "⏰"),
    UPLOADED("uploaded", "YouTube पर अपलोड हो गया", "✅"),
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
    val autoPublishIfNoReview: Boolean = true,
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
    val viewCountEstimate: String = "Just Published",
    val userReviewNote: String = "",
    val isGreenSignalGiven: Boolean = false
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

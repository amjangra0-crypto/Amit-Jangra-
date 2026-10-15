package com.example.data.model

/**
 * Configuration data models for custom and preset video exports.
 * Supports:
 * - Resolutions: 720p HD, 1080p Full HD, 4K Ultra HD
 * - Aspect Ratios: 9:16 (Shorts/Reels), 16:9 (YouTube), 1:1 (Square)
 * - Frame Rates: 30 fps, 45 fps, 60 fps, 75 fps, 90 fps
 * - Platform assignments: YouTube, Instagram, Facebook, TikTok
 */
data class ExportPreset(
    val id: String = java.util.UUID.randomUUID().toString(),
    val name: String,
    val resolutionLabel: String = "1080p Full HD",
    val width: Int = 1920,
    val height: Int = 1080,
    val aspectRatio: String = "16:9",
    val fps: Int = 60, // 30, 45, 60, 75, 90
    val targetPlatform: String = "YouTube",
    val isDefault: Boolean = false,
    val isCustom: Boolean = false,
    val bitrateMbps: Int = 16,
    val description: String = "Balanced cinematic anime playback"
)

object DefaultExportPresets {
    val presets = listOf(
        ExportPreset(
            id = "preset_yt_4k_60",
            name = "YouTube 4K Cinema Master",
            resolutionLabel = "4K Ultra HD",
            width = 3840,
            height = 2160,
            aspectRatio = "16:9",
            fps = 60,
            targetPlatform = "YouTube",
            isDefault = true,
            bitrateMbps = 45,
            description = "Maximum cinematic fidelity for high-resolution displays"
        ),
        ExportPreset(
            id = "preset_ig_reels_90",
            name = "Instagram Reels Ultra-Smooth (90 fps)",
            resolutionLabel = "1080p Full HD",
            width = 1080,
            height = 1920,
            aspectRatio = "9:16",
            fps = 90,
            targetPlatform = "Instagram",
            isDefault = false,
            bitrateMbps = 24,
            description = "High-frame-rate vertical video optimized for 90Hz/120Hz screens"
        ),
        ExportPreset(
            id = "preset_yt_shorts_75",
            name = "YouTube Shorts High-Motion (75 fps)",
            resolutionLabel = "1080p Full HD",
            width = 1080,
            height = 1920,
            aspectRatio = "9:16",
            fps = 75,
            targetPlatform = "YouTube",
            isDefault = false,
            bitrateMbps = 20,
            description = "Crisp action-packed vertical anime shorts"
        ),
        ExportPreset(
            id = "preset_fb_reels_45",
            name = "Facebook Video Fluid (45 fps)",
            resolutionLabel = "1080p Full HD",
            width = 1920,
            height = 1080,
            aspectRatio = "16:9",
            fps = 45,
            targetPlatform = "Facebook",
            isDefault = false,
            bitrateMbps = 15,
            description = "Optimized bitrate and frame pacing for social web feeds"
        ),
        ExportPreset(
            id = "preset_budget_720_30",
            name = "Fast Share HD (30 fps)",
            resolutionLabel = "720p HD",
            width = 1280,
            height = 720,
            aspectRatio = "16:9",
            fps = 30,
            targetPlatform = "All Platforms",
            isDefault = false,
            bitrateMbps = 8,
            description = "Compact lightweight file size for instant mobile sharing"
        )
    )

    val supportedResolutions = listOf(
        Triple("720p HD", 1280 to 720, 720 to 1280),
        Triple("1080p Full HD", 1920 to 1080, 1080 to 1920),
        Triple("4K Ultra HD", 3840 to 2160, 2160 to 3840)
    )

    val supportedAspectRatios = listOf(
        "16:9" to "Landscape (YouTube / TV)",
        "9:16" to "Vertical (Reels / Shorts)",
        "1:1" to "Square (Feed / Post)"
    )

    val supportedFrameRates = listOf(30, 45, 60, 75, 90)

    val supportedPlatforms = listOf(
        "YouTube", "Instagram", "Facebook", "TikTok", "All Platforms"
    )
}

package com.example.data.model

enum class WorkflowCategory(
    val id: String,
    val title: String,
    val subtitleHindi: String,
    val iconEmoji: String
) {
    VIDEO("video", "Video", "वीडियो वर्कफ़्लो", "📹"),
    VOICEOVER("voiceover", "Voiceover", "वॉयसओवर व ऑडियो", "♪"),
    DESIGN("design", "Design", "डिजाइन व थंबनेल", "🖼️")
}

enum class StudioWorkflow(
    val id: String,
    val title: String,
    val category: WorkflowCategory,
    val subtitle: String,
    val description: String,
    val iconEmoji: String,
    val defaultPrompt: String = "",
    val badge: String = "",
    val cardColorHex: String = "#FF5722",
    val secondaryColorHex: String = "#FF9800",
    val actionText: String = "Try it now →"
) {
    // ----------------------------------------------------
    // DESIGN WORKFLOWS
    // ----------------------------------------------------
    THUMBNAIL(
        id = "thumbnail",
        title = "Thumbnail",
        category = WorkflowCategory.DESIGN,
        subtitle = "YouTube & Video Cover",
        description = "Create bold, high-CTR anime thumbnails with glowing headline text, shocked expressions & dynamic lighting.",
        iconEmoji = "🎯",
        defaultPrompt = "",
        cardColorHex = "#FF6F00",
        secondaryColorHex = "#FF8F00"
    ),
    SOCIAL(
        id = "social",
        title = "Social",
        category = WorkflowCategory.DESIGN,
        subtitle = "Reels, Posts & Stories",
        description = "Design aesthetic anime graphics, quote posters, and vertical 9:16 or square posts for Instagram, Snapchat and TikTok.",
        iconEmoji = "📱",
        defaultPrompt = "",
        cardColorHex = "#0288D1",
        secondaryColorHex = "#29B6F6"
    ),
    PRESENTATION(
        id = "presentation",
        title = "Presentation",
        category = WorkflowCategory.DESIGN,
        subtitle = "Slide Decks & Storyboards",
        description = "Generate cinematic anime pitch decks, visual lore presentations, and character overview cards with infographics.",
        iconEmoji = "📊",
        defaultPrompt = "",
        cardColorHex = "#2E7D32",
        secondaryColorHex = "#66BB6A"
    ),
    EMPTY_DESIGN(
        id = "empty_design",
        title = "Empty",
        category = WorkflowCategory.DESIGN,
        subtitle = "Blank Design Canvas",
        description = "Start from scratch with a clean visual canvas to customize your own anime dimensions, layers, and text overlays.",
        iconEmoji = "➕",
        defaultPrompt = "",
        cardColorHex = "#616161",
        secondaryColorHex = "#9E9E9E",
        actionText = "Start now →"
    ),

    // ----------------------------------------------------
    // VOICEOVER WORKFLOWS
    // ----------------------------------------------------
    SCRIPT_TO_AUDIO(
        id = "script_to_audio",
        title = "Script to audio",
        category = WorkflowCategory.VOICEOVER,
        subtitle = "Voice Acting & Narration",
        description = "Transform scripted dialogues into rich character voice acting with custom emotional cadences, speed, and accents.",
        iconEmoji = "🎙️",
        defaultPrompt = "",
        cardColorHex = "#C2185B",
        secondaryColorHex = "#EC407A"
    ),
    BLOG_TO_AUDIO(
        id = "blog_to_audio",
        title = "Blog to audio",
        category = WorkflowCategory.VOICEOVER,
        subtitle = "Articles to Audio & Podcasts",
        description = "Turn web articles, manga recaps, or blog posts into narrated anime podcasts and audiobooks with background ambiance.",
        iconEmoji = "🎧",
        defaultPrompt = "",
        cardColorHex = "#558B2F",
        secondaryColorHex = "#8BC34A"
    ),
    GENERATE_MUSIC(
        id = "generate_music",
        title = "Music",
        category = WorkflowCategory.VOICEOVER,
        subtitle = "Dynamic Soundtrack & BGM",
        description = "Synthesize custom anime OST, epic orchestral battle themes, lo-fi study beats, and emotional piano soundscapes.",
        iconEmoji = "🎵",
        defaultPrompt = "",
        cardColorHex = "#E65100",
        secondaryColorHex = "#FFA726"
    ),
    EMPTY_AUDIO(
        id = "empty_audio",
        title = "Empty",
        category = WorkflowCategory.VOICEOVER,
        subtitle = "Blank Audio Suite",
        description = "Start a blank audio project with real-time multi-track TTS speech synthesis, sound effects, and music mixer.",
        iconEmoji = "🎛️",
        defaultPrompt = "",
        cardColorHex = "#455A64",
        secondaryColorHex = "#78909C",
        actionText = "Start now →"
    ),

    // ----------------------------------------------------
    // VIDEO WORKFLOWS
    // ----------------------------------------------------
    SCRIPT_TO_VIDEO(
        id = "script_to_video",
        title = "Script to video",
        category = WorkflowCategory.VIDEO,
        subtitle = "Script to Full Animated Video",
        description = "Turn detailed text scripts into full anime scenes with synchronized lip-sync, animated speedlines, and voiceovers.",
        iconEmoji = "🎬",
        defaultPrompt = "",
        cardColorHex = "#B71C1C",
        secondaryColorHex = "#E53935"
    ),
    BLOG_TO_VIDEO(
        id = "blog_to_video",
        title = "Blog to video",
        category = WorkflowCategory.VIDEO,
        subtitle = "URL / Article to Anime Video",
        description = "Convert blog posts, web links, or news summaries into 4-scene anime explainer videos with narration and subtitles.",
        iconEmoji = "📰",
        defaultPrompt = "",
        cardColorHex = "#33691E",
        secondaryColorHex = "#689F38"
    ),
    PPT_TO_VIDEO(
        id = "ppt_to_video",
        title = "PPT to video",
        category = WorkflowCategory.VIDEO,
        subtitle = "Presentation to Video",
        description = "Transform presentation slides and bullet points into an engaging animated video with AI character presenter.",
        iconEmoji = "📑",
        defaultPrompt = "",
        cardColorHex = "#004D40",
        secondaryColorHex = "#00897B"
    ),
    EXPLAINER_VIDEO(
        id = "explainer_video",
        title = "Explainer videos",
        category = WorkflowCategory.VIDEO,
        subtitle = "Host & Concept Explainer",
        description = "Create engaging tutorial and explainer videos hosted by animated 2D/3D anime characters with whiteboard annotations.",
        iconEmoji = "👩‍🏫",
        defaultPrompt = "",
        cardColorHex = "#EF6C00",
        secondaryColorHex = "#FB8C00"
    ),
    MOTION_GRAPHICS_EXPLAINER(
        id = "motion_graphics_explainer",
        title = "Motion graphics explainer",
        category = WorkflowCategory.VIDEO,
        subtitle = "Kinetic Typography & FX",
        description = "Produce high-impact kinetic motion graphics with speedlines, impact shockwaves, screen shake, and infographic transitions.",
        iconEmoji = "⚡",
        defaultPrompt = "",
        cardColorHex = "#1565C0",
        secondaryColorHex = "#1E88E5"
    ),
    MUSIC_VIDEO(
        id = "music_video",
        title = "Music video",
        category = WorkflowCategory.VIDEO,
        subtitle = "Anime Music Video (AMV)",
        description = "Craft rhythm-synced anime music videos (AMV) with beat drops, dramatic camera cuts, and stylized lyric subtitles.",
        iconEmoji = "🎤",
        defaultPrompt = "",
        badge = "NEW",
        cardColorHex = "#880E4F",
        secondaryColorHex = "#D81B60"
    ),
    EDIT_VIDEO(
        id = "edit_video",
        title = "Edit video",
        category = WorkflowCategory.VIDEO,
        subtitle = "Timeline & Scene Editor",
        description = "Cut, trim, re-order scenes, restyle subtitles, adjust audio volumes, and tweak camera motion effects on existing videos.",
        iconEmoji = "✂️",
        defaultPrompt = "",
        cardColorHex = "#006064",
        secondaryColorHex = "#00ACC1"
    ),
    SCREEN_RECORDING(
        id = "screen_recording",
        title = "Screen recording",
        category = WorkflowCategory.VIDEO,
        subtitle = "Demo & Avatar Narration",
        description = "Create app walkthroughs and gaming tutorials with picture-in-picture anime avatar reactions and voice commentary.",
        iconEmoji = "📹",
        defaultPrompt = "",
        cardColorHex = "#7B1FA2",
        secondaryColorHex = "#BA68C8"
    ),
    TRANSLATE_VIDEO(
        id = "translate_video",
        title = "Translate video",
        category = WorkflowCategory.VIDEO,
        subtitle = "Multilingual Dubbing & Subtitles",
        description = "Dub and translate any anime video into Hindi, English, Japanese, French, Spanish, German, or Chinese with synchronized subs.",
        iconEmoji = "🌐",
        defaultPrompt = "",
        cardColorHex = "#311B92",
        secondaryColorHex = "#5E35B1"
    ),
    EMPTY_VIDEO(
        id = "empty_video",
        title = "Empty",
        category = WorkflowCategory.VIDEO,
        subtitle = "Blank Video Project",
        description = "Start an empty multi-scene storyboard with custom aspect ratio, blank scene slots, and custom director controls.",
        iconEmoji = "🎬",
        defaultPrompt = "",
        cardColorHex = "#37474F",
        secondaryColorHex = "#607D8B",
        actionText = "Start now →"
    )
}

// Data models for the interactive tool generators
data class ThumbnailPreviewData(
    val headlineText: String = "THE DRAGON AWAKENS",
    val subtitleText: String = "EPISODE 01 • SHONEN CLIMAX",
    val badgeLabel: String = "🔥 4K UHD",
    val aspectRatio: String = "16:9", // "16:9" or "9:16"
    val backgroundVisualName: String = "scene_cyber_battle",
    val fontColorHex: String = "#FFEB3B",
    val accentGlowHex: String = "#FF1744"
)

data class SocialPreviewData(
    val platform: String = "Instagram Reels", // "Instagram Reels", "YouTube Shorts", "Snapchat Spotlight"
    val caption: String = "जब अंधेरा छाएगा, तब ड्रैगन जागेगा! 🐉🔥 #AnimeReels #HindiAnime",
    val aspectRatio: String = "9:16",
    val stickerTag: String = "TRENDING SOUND",
    val backgroundVisualName: String = "scene_neon_rain"
)

data class PresentationSlideData(
    val slideNumber: Int,
    val title: String,
    val bulletPoints: List<String>,
    val narratorNote: String,
    val visualTheme: String
)

data class MusicGeneratorData(
    val title: String = "Cyber Samurai Beat",
    val mood: MusicMood = MusicMood.EPIC_BATTLE,
    val tempoBpm: Int = 138,
    val isPlayingPreview: Boolean = false,
    val instruments: List<String> = listOf("Distorted 808 Bass", "Cyber Synthwave Lead", "Japanese Taiko Drums", "Electric Shamisen")
)

data class TranslationStudioData(
    val sourceLanguage: String = "Hindi",
    val targetLanguage: String = "Japanese",
    val enableDualSubtitles: Boolean = true,
    val translatedTitle: String = "龍の目覚め (Ryū no Mezame)",
    val sampleOriginalLine: String = "हम कभी पीछे नहीं हटेंगे!",
    val sampleTranslatedLine: String = "決して後退はしない！ (Kesshite kōtai wa shinai!)"
)

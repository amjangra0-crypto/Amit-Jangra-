package com.example.data.model

data class CharacterProfile(
    val id: String,
    val name: String,
    val gender: String, // "Girl / Female", "Boy / Youth", "Adult Male", "Lady / Sensei", "Chibi Mascot"
    val role: String,
    val personality: String,
    val voicePitch: Float = 1.0f,
    val voiceSpeed: Float = 1.0f,
    val voiceType: String = "Girl", // "Girl", "Boy", "Male", "Lady", "Mascot", "Narrator"
    val avatarDrawableName: String = "char_anime_heroine",
    // Visual Attributes defined from text prompts or customization
    val promptVisualDescription: String = "",
    val hairStyle: String = "Spiky Shonen",
    val hairColor: String = "Silver White",
    val eyeColor: String = "Sapphire Blue",
    val outfit: String = "Cyber Shinobi Battle Suit",
    val outfitColor: String = "Obsidian & Neon Purple",
    val accessoryAura: String = "Sakura Sparks",
    val expression: String = "Confident Smirk",
    // Diverse Voice Attributes
    val voiceGender: String = "Female", // "Male", "Female", "Child"
    val voicePersona: String = "Sweet Kawaii Heroine",
    val voiceAccent: String = "Standard Anime (Japanese Cadence)",
    val sampleDialogue: String = "私を信じて！一緒に未来を変えよう！",
    // Emotion, Eye Expression & Avatar Customization Sliders
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

data class VoicePersonaOption(
    val id: String,
    val name: String,
    val gender: String, // "Male", "Female", "Child"
    val defaultPitch: Float,
    val defaultSpeed: Float,
    val description: String,
    val defaultAccent: String
)

data class VoiceAccentOption(
    val id: String,
    val name: String,
    val localeTag: String,
    val description: String
)

data class AnimeVisualElement(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val sourceMode: String, // "TEXT_PROMPT", "UPLOADED_IMAGE", "WEB_LINK"
    val sourceQuery: String,
    val visualType: String, // "SCENE_BACKGROUND", "CHARACTER_KEY_FRAME", "BATTLE_EFFECT", "STORYBOARD_FRAME"
    val artStyle: AnimeArtStyle = AnimeArtStyle.JAPANESE_ANIME,
    val promptDescription: String,
    val primaryHexColor: String = "#FF4081",
    val secondaryHexColor: String = "#00E5FF",
    val atmosphericEffect: String = "Cherry Blossom Storm", // "Sakura Blizzard", "Cyber Rain", "Golden Sparks", "Speedlines", "Ethereal Glow"
    val cameraMotion: String = "Ken Burns Zoom In", // "Ken Burns Zoom In", "Dramatic Pan Right", "Cinematic Wide Float"
    val visualDrawableName: String = "scene_cherry_temple",
    val createdAt: Long = System.currentTimeMillis()
)

enum class SubtitleMode(val label: String, val description: String) {
    TRANSLATED_ONLY("अनुवादित (Translated)", "Show dialogue in target language"),
    BILINGUAL_DUAL("द्विभाषी (Bilingual Subtitles)", "Show original + translated text side-by-side"),
    ORIGINAL_ONLY("मूल (Original)", "Show original script dialogue")
}

enum class ProductionFormat(
    val id: String,
    val title: String,
    val subtitle: String,
    val sceneCount: Int,
    val icon: String,
    val targetAspect: String,
    val description: String
) {
    SHORTS_REEL(
        id = "shorts_reel",
        title = "Shorts / Reel (15-60s)",
        subtitle = "⚡ शार्ट / रील",
        sceneCount = 2,
        icon = "⚡",
        targetAspect = "9:16 Vertical",
        description = "Rapid punchy pacing, viral cliffhanger, vertical framing & fast dub tempo"
    ),
    ANIME_EPISODE(
        id = "anime_episode",
        title = "Anime Episode (Long)",
        subtitle = "🎬 एनिमे एपिसोड",
        sceneCount = 4,
        icon = "🎬",
        targetAspect = "16:9 Widescreen",
        description = "Standard 4-scene anime episode with prologue, battle encounter & climactic payoff"
    ),
    MANHWA_WEB_SERIES(
        id = "manhwa_web_series",
        title = "Manhwa Web Series",
        subtitle = "📜 मन्हवा वेब सीरीज",
        sceneCount = 5,
        icon = "📜",
        targetAspect = "Webtoon Vertical",
        description = "Korean manhwa episodic narrative with shadow auras, dungeon awakening & chapter cliffhangers"
    ),
    CINEMATIC_MOVIE(
        id = "cinematic_movie",
        title = "Cinematic Movie (OVA)",
        subtitle = "🎥 एनिमे मूवी / OVA",
        sceneCount = 6,
        icon = "🎥",
        targetAspect = "21:9 Ultra-Wide",
        description = "Grand theatrical cinematic with orchestral scores, widescreen letterbox & deep emotional arcs"
    )
}

enum class MotionEffect(
    val id: String,
    val title: String,
    val emoji: String,
    val description: String
) {
    SPEEDLINES_ACTION("speedlines", "Shonen Speedlines", "⚡", "Dynamic radial action speedlines for combat and sudden impact"),
    SCREEN_SHAKE_IMPACT("screen_shake", "Screen Shake Impact", "💥", "Dynamic camera shake on explosions, punches, shouts & magic blasts"),
    AURA_GLOW_PARTICLES("aura_glow", "Manhwa Shadow Aura", "🔥", "Electric blue & purple glowing aura with rising embers"),
    MANGA_PANEL_SLIDE("panel_slide", "Manga / Webtoon Panel Shift", "📜", "Webtoon vertical panel shift & ink screentone borders"),
    CINEMATIC_ZOOM("cinematic_zoom", "Dramatic Ken Burns Zoom", "🎥", "Smooth camera zoom-in focusing into character expressions"),
    SLOW_MO_FREEZE("slow_mo", "Slow-Mo Freeze Frame", "⏱️", "Bullet-time slow motion with sudden motion burst")
}

enum class CharacterExpressionType(
    val id: String,
    val title: String,
    val emoji: String,
    val stickerIcon: String,
    val description: String
) {
    FIERCE_BATTLE("fierce_battle", "Fierce Battle Roar", "🔥", "roaring", "Fiery warrior spirit, clenched combat teeth & intense stare"),
    MANHWA_GLOWING_EYES("glowing_eyes", "Manhwa Glowing Eyes", "⚡", "glowing", "Overpowered cold gaze with neon glowing eyes & dark aura"),
    KAWAII_BLUSH("kawaii_blush", "Kawaii Blush & Sparkles", "🌸", "blush", "Rosy pink cheeks, sparkling eyes & warm anime smile"),
    SHOCK_SWEATDROP("shock_sweatdrop", "Comedic Sweatdrop", "💧", "sweatdrop", "Oversized anime sweatdrop, wide panic eyes & comedic shudder"),
    TSUNDERE_POUT("tsundere_pout", "Tsundere Pout", "💢", "pout", "Anime anger vein mark, puffed cheeks & tsundere glare"),
    MELANCHOLIC_TEARS("melancholic_tears", "Melancholic Tears", "✨", "tears", "Glistening cinematic tears, emotional heartbreak & soft blink"),
    VILLAIN_SMIRK("villain_smirk", "Villainous Smirk", "😈", "smirk", "Shadow-shaded smirk, confident tilted head & sharp eyes")
}

enum class SourcePlatform(
    val id: String,
    val title: String,
    val iconEmoji: String,
    val brandColorHex: String,
    val description: String
) {
    YOUTUBE("youtube", "YouTube", "▶️", "#FF0000", "वीडियो पेसिंग, हुक और बैकग्राउंड म्यूजिक समझकर निर्माण"),
    GOOGLE("google", "Google Web", "🔍", "#4285F4", "गहरा रिसर्च, वर्ल्डबिल्डिंग और मिथोलॉजी डाटा विश्लेषण"),
    INSTAGRAM("instagram", "Instagram Reels", "📸", "#E1306C", "एस्थेटिक विजुअल्स, ट्रेंडिंग वाइब और त्वरित हुक"),
    SNAPCHAT("snapchat", "Snapchat Spotlight", "👻", "#FFFC00", "फास्ट-पेस्ड स्नैप्स, एआर मूड और स्पॉन्डटेनियस डायलॉग्स"),
    VOICE_COMMAND("voice", "Voice Directive", "🎙️", "#00E5FF", "ध्वनि व बोलकर दिए गए निर्देश को समझकर निर्माण"),
    DIRECT_TEXT("text", "Text Prompt", "✍️", "#9C27B0", "विस्तृत टेक्स्ट विवरण व रचनात्मक कहानी")
}

data class SourceIntelligence(
    val platform: SourcePlatform,
    val rawQueryOrUrl: String,
    val analyzedTheme: String,
    val narrativeHook: String,
    val visualVibe: String,
    val audioMoodTag: String,
    val keyExtractedTags: List<String> = emptyList(),
    val isContinuityIntentDetected: Boolean = false,
    val detectedEpisodeHint: Int = 1
)

data class ContinuityConfig(
    val isSeriesContinuityEnabled: Boolean = false,
    val linkedParentProjectId: String? = null,
    val linkedParentProjectTitle: String? = null,
    val episodeNumber: Int = 1,
    val partLabel: String = "Part 1 / Standalone"
)

data class DialogueLine(
    val characterName: String,
    val text: String,
    val emotion: String = "Normal", // "Happy", "Serious", "Excited", "Mysterious", "Dramatic"
    val expression: String = "Confident Smirk", // Dynamic facial expression
    val motionEffect: String = "SPEEDLINES_ACTION", // Motion effect during line
    val voicePitch: Float = 1.0f,
    val voiceSpeed: Float = 1.0f,
    val voiceType: String = "Girl",
    val voiceAccent: String = "Standard Anime (Japanese Cadence)"
)

data class AnimeScene(
    val sceneNumber: Int,
    val title: String,
    val visualPrompt: String,
    val backgroundType: String = "Cherry Blossom Sanctuary",
    val bgMood: String = "Emotional Piano", // "Epic Battle", "Emotional Piano", "Mystery Fantasy", "Kawaii Playful", "Cyber Synth"
    val dialogues: List<DialogueLine>,
    val durationSec: Int = 8,
    val sceneDrawableName: String = "scene_cherry_temple",
    val onScreenTitleTranslated: String = "",
    val atmosphericEffect: String = "Cherry Blossom Storm",
    val motionEffect: String = "SPEEDLINES_ACTION",
    val productionFormat: String = "Anime Episode",
    val isMuted: Boolean = false,
    val transitionEffect: String = "Fade",
    val transitionDurationSec: Float = 1.0f
)

data class AnimeScript(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val originalPrompt: String,
    val inputSourceType: String = "TEXT", // "TEXT", "LINK", "IMAGE", "VOICE"
    val sourceReference: String = "",
    val genre: String = "Shonen Fantasy",
    val artStyle: String = "Japanese Anime (Makoto Shinkai)",
    val productionFormat: String = "Anime Episode (Long)",
    val defaultMotionEffect: String = "SPEEDLINES_ACTION",
    val language: String = "Hindi",
    val voiceoverLanguage: String = "Hindi",
    val subtitleMode: SubtitleMode = SubtitleMode.TRANSLATED_ONLY,
    val synopsis: String,
    val characters: List<CharacterProfile>,
    val scenes: List<AnimeScene>,
    val createdAt: Long = System.currentTimeMillis(),
    val sourcePlatformName: String = "Direct Text",
    val isLinkedSequel: Boolean = false,
    val linkedEpisodeNumber: Int = 1,
    val linkedParentTitle: String = "",
    val noveltyBadge: String = "✨ 100% Brand New Characters & Lore"
)

enum class MusicMood(val label: String, val description: String) {
    EPIC_BATTLE("Epic Battle", "High-energy driving anime action theme"),
    EMOTIONAL_PIANO("Emotional Piano", "Melancholic sweet pentatonic chords"),
    MYSTERY_FANTASY("Mystery Fantasy", "Ethereal enchanted bells and pads"),
    KAWAII_PLAYFUL("Kawaii Playful", "Cute cartoon upbeat rhythmic beats"),
    CYBER_SYNTH("Cyber Synth", "Futuristic neon synthwave anime bass")
}

enum class AnimeArtStyle(val id: String, val title: String, val description: String) {
    JAPANESE_ANIME("japanese_anime", "Japanese Anime", "Vibrant colors, cinematic lighting, Makoto Shinkai / CoMix Wave aesthetic"),
    MANHWA_WEBTOON("manhwa_webtoon", "Manhwa Webtoon", "Solo-Leveling Korean webtoon style, vibrant digital glow, dark shadow aura & glowing neon eyes"),
    CLASSIC_MANGA("classic_manga", "Classic Manga", "Monochrome ink screentones, dramatic manga panels, hatching & speedlines"),
    SHONEN_ACTION("shonen_action", "Shonen Action", "Dynamic line art, high contrast, ufotable anime style"),
    CHIBI_CARTOON("chibi_cartoon", "Chibi Cartoon", "Super cute, round kawaii proportions, playful cartoon vibes"),
    CYBERPUNK_ANIME("cyberpunk_anime", "Cyberpunk Neo", "Neon glow, rain reflections, futuristic anime cityscapes"),
    SKETCH_LINEART("sketch_lineart", "Sketch & Line Art Anime", "Pencil sketch, clean dynamic character lines, animation storyboard aesthetics"),
    HYPER_REALISTIC("hyper_realistic", "Hyper-Realistic Anime (Veo/CGI)", "Ultra-detailed textures, photorealistic cinematic lighting, raytraced reflections, hyper-realistic anime CGI"),
    RETRO_90S_CEL("retro_90s_cel", "90s Retro Cel Anime", "Classic 90s vintage anime cel shading, grain, hand-drawn retro aesthetic")
}

enum class SupportedLanguage(val code: String, val displayName: String, val nativeName: String) {
    HINDI("hi", "Hindi", "हिन्दी"),
    ENGLISH("en", "English", "English"),
    JAPANESE("ja", "Japanese", "日本語"),
    CHINESE("zh", "Chinese", "中文"),
    SPANISH("es", "Spanish", "Español"),
    FRENCH("fr", "French", "Français"),
    GERMAN("de", "German", "Deutsch"),
    KOREAN("ko", "Korean", "한국어"),
    TAMIL("ta", "Tamil", "தமிழ்"),
    TELUGU("te", "Telugu", "తెలుగు"),
    RUSSIAN("ru", "Russian", "Русский"),
    ARABIC("ar", "Arabic", "العربية")
}

enum class SubscriptionPlan(
    val planId: String,
    val title: String,
    val price: String,
    val yearlyPrice: String = "Free",
    val badge: String = "",
    val dailyCredits: Int = 10,
    val features: List<String> = emptyList()
) {
    FREE(
        planId = "plan_free",
        title = "Free Explorer",
        price = "Free",
        yearlyPrice = "Free",
        badge = "स्टार्टर (Starter)",
        dailyCredits = 3,
        features = listOf("3 Anime scripts / day", "Standard TTS Voice", "720p Storyboard Video", "Community Support")
    ),
    CREATOR_PRO(
        planId = "plan_pro",
        title = "Creator Pro",
        price = "₹499 / mo",
        yearlyPrice = "₹399 / mo (₹4,788/yr)",
        badge = "🔥 सबसे लोकप्रिय (Most Popular)",
        dailyCredits = 50,
        features = listOf("50 Anime scripts / day", "All Voice Styles & Audio FX", "1080p Full HD Anime Visuals", "All Languages Translation", "Priority AI Render", "Manga Sketch Camera Scan")
    ),
    STUDIO_ULTRA(
        planId = "plan_ultra",
        title = "Studio Ultra Pass",
        price = "₹999 / mo",
        yearlyPrice = "₹799 / mo (₹9,588/yr)",
        badge = "⚡ अनलिमिटेड 4K (Ultra Power)",
        dailyCredits = 999,
        features = listOf("Unlimited Anime Scripts & Videos", "4K Ultra HD Export & Master Audio", "Custom Voice Cloning & Mic Input", "Full Commercial & YouTube Rights", "VIP Priority Pipeline")
    ),
    STUDIO_OWNER(
        planId = "plan_vip",
        title = "Studio Master VIP",
        price = "Free for Owner",
        yearlyPrice = "Free for Owner",
        badge = "👑 ऐप ओनर (Lifetime VIP)",
        dailyCredits = 999999,
        features = listOf("Unlimited Video & Anime Generation", "Admin Dashboard & User Access Control", "Decide Who Gets Free vs Paid", "Direct OTA Update Manager", "Lifetime Commercial Rights")
    )
}

/**
 * Mutable Subscription Scheme Model allowing App Owner to edit & modify details
 */
data class SubscriptionSchemeDetails(
    val planId: String,
    val title: String,
    val badge: String,
    val monthlyPriceInr: Double,
    val yearlyPriceInr: Double,
    val monthlyPriceUsd: Double,
    val yearlyPriceUsd: Double,
    val monthlyPriceEur: Double = 9.99,
    val monthlyPriceGbp: Double = 8.99,
    val monthlyPriceJpy: Double = 1480.0,
    val dailyCredits: Int,
    val features: List<String>,
    val isActive: Boolean = true,
    val discountPercent: Int = 0,
    val isFreeForOwner: Boolean = false
)


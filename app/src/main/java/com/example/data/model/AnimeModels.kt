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
    val sampleDialogue: String = "私を信じて！一緒に未来を変えよう！"
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

data class DialogueLine(
    val characterName: String,
    val text: String,
    val emotion: String = "Normal", // "Happy", "Serious", "Excited", "Mysterious", "Dramatic"
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
    val atmosphericEffect: String = "Cherry Blossom Storm"
)

data class AnimeScript(
    val id: String = System.currentTimeMillis().toString(),
    val title: String,
    val originalPrompt: String,
    val inputSourceType: String = "TEXT", // "TEXT", "LINK", "IMAGE"
    val sourceReference: String = "",
    val genre: String = "Shonen Fantasy",
    val artStyle: String = "Japanese Anime (Makoto Shinkai)",
    val language: String = "Hindi",
    val voiceoverLanguage: String = "Hindi",
    val subtitleMode: SubtitleMode = SubtitleMode.TRANSLATED_ONLY,
    val synopsis: String,
    val characters: List<CharacterProfile>,
    val scenes: List<AnimeScene>,
    val createdAt: Long = System.currentTimeMillis()
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
    SHONEN_ACTION("shonen_action", "Shonen Action", "Dynamic line art, high contrast, ufotable anime style"),
    CHIBI_CARTOON("chibi_cartoon", "Chibi Cartoon", "Super cute, round kawaii proportions, playful cartoon vibes"),
    CLASSIC_MANGA("classic_manga", "Classic Manga", "Monochrome ink screentones, dramatic manga panels"),
    CYBERPUNK_ANIME("cyberpunk_anime", "Cyberpunk Neo", "Neon glow, rain reflections, futuristic anime cityscapes")
}

enum class SupportedLanguage(val code: String, val displayName: String, val nativeName: String) {
    HINDI("hi", "Hindi", "हिन्दी"),
    ENGLISH("en", "English", "English"),
    JAPANESE("ja", "Japanese", "日本語"),
    CHINESE("zh", "Chinese", "中文"),
    SPANISH("es", "Spanish", "Español"),
    FRENCH("fr", "French", "Français"),
    GERMAN("de", "German", "Deutsch"),
    KOREAN("ko", "Korean", "한국어")
}

enum class SubscriptionPlan(
    val planId: String,
    val title: String,
    val price: String,
    val dailyCredits: Int,
    val features: List<String>
) {
    FREE("plan_free", "Free Explorer", "Free", 3, listOf("3 Anime scripts / day", "Standard TTS Voice", "720p Storyboard Video", "Community Support")),
    CREATOR_PRO("plan_pro", "Creator Pro", "₹499 / mo", 50, listOf("50 Anime scripts / day", "All Voice Personas & Pitch Sync", "1080p HD Anime Visuals", "All Languages Translation", "Priority AI Render")),
    STUDIO_OWNER("plan_vip", "Studio Master VIP", "Free for Owner", 999999, listOf("Unlimited Video & Anime Generation", "Admin Dashboard & User Access Control", "Decide Who Gets Free vs Paid", "Direct OTA Update Manager", "Lifetime Commercial Rights"))
}

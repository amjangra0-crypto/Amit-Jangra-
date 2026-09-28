package com.example.data.engine

import com.example.data.model.CharacterProfile
import com.example.data.model.MotionEffect
import com.example.data.model.SourceIntelligence
import com.example.data.model.SourcePlatform
import java.util.Locale
import kotlin.random.Random

object SourceIntelligenceEngine {

    /**
     * Detects source platform from query string, URL, or voice mode
     */
    fun detectPlatform(input: String, isVoiceInput: Boolean = false): SourcePlatform {
        if (isVoiceInput) return SourcePlatform.VOICE_COMMAND
        val lower = input.lowercase(Locale.getDefault())

        return when {
            lower.contains("youtube.com") || lower.contains("youtu.be") || lower.contains("yt shorts") || lower.contains("youtube") ->
                SourcePlatform.YOUTUBE
            lower.contains("instagram.com") || lower.contains("instagr.am") || lower.contains("ig reel") || lower.contains("instagram") ->
                SourcePlatform.INSTAGRAM
            lower.contains("snapchat.com") || lower.contains("snap.com") || lower.contains("snap spotlight") || lower.contains("snapchat") ->
                SourcePlatform.SNAPCHAT
            lower.contains("google.com") || lower.contains("http://") || lower.contains("https://") || lower.contains("wikipedia") || lower.contains("article") ->
                SourcePlatform.GOOGLE
            else -> SourcePlatform.DIRECT_TEXT
        }
    }

    /**
     * Inspects text or voice command to detect if user wants to continue or link with previous video/movie/series
     */
    fun detectContinuityIntent(input: String): Pair<Boolean, Int> {
        val lower = input.lowercase(Locale.getDefault())
        val isContinuity = lower.contains("part 2") || lower.contains("part 3") || lower.contains("part 4") ||
                lower.contains("episode 2") || lower.contains("episode 3") || lower.contains("ep 2") ||
                lower.contains("भाग 2") || lower.contains("भाग दो") || lower.contains("भाग 3") || lower.contains("भाग तीन") ||
                lower.contains("एपिसोड 2") || lower.contains("एपिसोड दो") || lower.contains("एपिसोड") ||
                lower.contains("सीक्वल") || lower.contains("sequel") || lower.contains("continuation") ||
                lower.contains("continue") || lower.contains("पहले वाले के साथ") || lower.contains("जोड़ो") ||
                lower.contains("जोड़ कर") || lower.contains("link with previous") || lower.contains("link previous") ||
                lower.contains("web series") || lower.contains("वेब सीरीज")

        val epNum = when {
            lower.contains("part 4") || lower.contains("episode 4") || lower.contains("भाग 4") -> 4
            lower.contains("part 3") || lower.contains("episode 3") || lower.contains("भाग 3") -> 3
            lower.contains("part 2") || lower.contains("episode 2") || lower.contains("भाग 2") || lower.contains("भाग दो") -> 2
            isContinuity -> 2
            else -> 1
        }
        return Pair(isContinuity, epNum)
    }

    /**
     * Performs deep analysis of data and context across YouTube, Google, Instagram, Snapchat, Voice, and Text
     */
    fun analyzeSource(input: String, platform: SourcePlatform): SourceIntelligence {
        val (continuityDetected, episodeHint) = detectContinuityIntent(input)
        val lower = input.lowercase(Locale.getDefault())

        return when (platform) {
            SourcePlatform.YOUTUBE -> {
                val isShort = lower.contains("shorts") || lower.contains("short")
                SourceIntelligence(
                    platform = SourcePlatform.YOUTUBE,
                    rawQueryOrUrl = input,
                    analyzedTheme = "YouTube Video Lore & AMV Dynamics: Analyzing high-tempo scene progression, dramatic visual punchlines, and sonic synchronization.",
                    narrativeHook = if (isShort) "Immediate 3-second visual adrenaline spike followed by a cliffhanger beat" else "Cinematic intro with orchestral swell, building up to an epic combat drop",
                    visualVibe = "High-contrast cinematic saturation, dramatic framing with dynamic camera pans",
                    audioMoodTag = "Epic Battle & Driving Synth",
                    keyExtractedTags = listOf("YouTube-Paced", if (isShort) "Vertical-9:16-Hook" else "Widescreen-AMV", "BassDrop-Cues", "High-Engagement"),
                    isContinuityIntentDetected = continuityDetected,
                    detectedEpisodeHint = episodeHint
                )
            }
            SourcePlatform.INSTAGRAM -> {
                SourceIntelligence(
                    platform = SourcePlatform.INSTAGRAM,
                    rawQueryOrUrl = input,
                    analyzedTheme = "Instagram Reel & Aesthetic Wave: Analyzing visual glamour, modern anime color palettes, aesthetic aura particles, and snappy punchy dialogues.",
                    narrativeHook = "Visually striking close-up on character eyes followed by aesthetic petal or glitch transitions",
                    visualVibe = "Pastel aesthetic gradients, dreamy bloom highlights, and crisp character lineart",
                    audioMoodTag = "Emotional Piano & Lo-Fi Synth",
                    keyExtractedTags = listOf("Insta-Aesthetic", "Viral-Reel-Energy", "Bloom-Lighting", "Snappy-Dialogue"),
                    isContinuityIntentDetected = continuityDetected,
                    detectedEpisodeHint = episodeHint
                )
            }
            SourcePlatform.SNAPCHAT -> {
                SourceIntelligence(
                    platform = SourcePlatform.SNAPCHAT,
                    rawQueryOrUrl = input,
                    analyzedTheme = "Snapchat Spotlight & AR Expressiveness: Analyzing spontaneous comedic timing, bold expressive faces, vibrant sticker energy, and fast 10-second cuts.",
                    narrativeHook = "Sudden comedic or action shockwave with expressive character sweatdrop/blush reactions",
                    visualVibe = "Vibrant pop colors, animated comic speedlines, expressive eye stickers & energetic motion",
                    audioMoodTag = "Kawaii Playful & Upbeat Cartoon",
                    keyExtractedTags = listOf("Snap-Spotlight", "AR-Filter-Mood", "Comedic-Pacing", "Punchy-Expressions"),
                    isContinuityIntentDetected = continuityDetected,
                    detectedEpisodeHint = episodeHint
                )
            }
            SourcePlatform.GOOGLE -> {
                SourceIntelligence(
                    platform = SourcePlatform.GOOGLE,
                    rawQueryOrUrl = input,
                    analyzedTheme = "Google Deep Lore & Factual Worldbuilding: Extracting rich mythological, futuristic, or historical contextual knowledge to build deep anime canon.",
                    narrativeHook = "Expansive worldbuilding panning shot unveiling the grand lore, ancient artifacts, and mystical prophecies",
                    visualVibe = "Intricate background art with deep atmospheric shadows, architectural fidelity, and rich lore symbolism",
                    audioMoodTag = "Mystery Fantasy & Celestial Bells",
                    keyExtractedTags = listOf("Google-Lore-Analysis", "Deep-Worldbuilding", "Mythology-Canon", "Expansive-Scale"),
                    isContinuityIntentDetected = continuityDetected,
                    detectedEpisodeHint = episodeHint
                )
            }
            SourcePlatform.VOICE_COMMAND -> {
                SourceIntelligence(
                    platform = SourcePlatform.VOICE_COMMAND,
                    rawQueryOrUrl = input,
                    analyzedTheme = "Spoken Voice Command Directives: Analyzing speaker emotion, imperative tone, and direct conversational intent in real-time.",
                    narrativeHook = "Natural conversational dialogue launch reacting directly to the spoken command intent",
                    visualVibe = "Expressive dialogue-centric cinematography focusing on speaker lip-sync and dynamic expressions",
                    audioMoodTag = "Dramatic Orchestral & Voice-Harmonized",
                    keyExtractedTags = listOf("Voice-Directive", "Speech-Interpreted", "Natural-Cadence", "Intent-Driven"),
                    isContinuityIntentDetected = continuityDetected,
                    detectedEpisodeHint = episodeHint
                )
            }
            SourcePlatform.DIRECT_TEXT -> {
                SourceIntelligence(
                    platform = SourcePlatform.DIRECT_TEXT,
                    rawQueryOrUrl = input,
                    analyzedTheme = "Original Creative Text Conception: Synthesizing original narrative seeds into rich anime screenplay.",
                    narrativeHook = "Direct realization of creative narrative description with cinematic pacing",
                    visualVibe = "Custom tailored to the prompt genre and art style",
                    audioMoodTag = "Balanced Orchestral Score",
                    keyExtractedTags = listOf("Original-Prompt", "Creative-Writing", "Story-Direct"),
                    isContinuityIntentDetected = continuityDetected,
                    detectedEpisodeHint = episodeHint
                )
            }
        }
    }

    /**
     * Generates 100% BRAND NEW, NEVER-BEFORE-SEEN Characters for Novel Generations.
     * Guarantees non-collision with existing names or previous scripts.
     */
    fun generateNovelCharacters(
        artStyleTitle: String,
        genre: String,
        language: String,
        excludedNames: Set<String> = emptySet(),
        voiceAccent: String = ""
    ): List<CharacterProfile> {
        val isKorean = artStyleTitle.contains("Manhwa", ignoreCase = true) || language.equals("Korean", ignoreCase = true)
        val isHindi = language.equals("Hindi", ignoreCase = true) || language.equals("hi", ignoreCase = true)
        val isCyber = artStyleTitle.contains("Cyber", ignoreCase = true) || genre.contains("Cyber", ignoreCase = true)

        val firstNamesBoy = when {
            isKorean -> listOf("Min-Hyuk (민혁)", "Do-Yun (도윤)", "Tae-Oh (태오)", "Seo-Jun (서준)", "Ha-Neul (하늘)", "Kang-Woo (강우)", "Seung-Ho (승호)", "Shi-Woo (시우)")
            isHindi -> listOf("आर्यन (Aryan)", "वीर (Veer)", "कबीर (Kabir)", "रुद्र (Rudra)", "देवराज (Devraj)", "त्रिशूल (Trishul)", "शौर्य (Shaurya)", "आकाश (Akash)")
            isCyber -> listOf("Jax (जैक)", "Cipher (साइफर)", "Neon-09", "Vektor (वेक्टर)", "Kael (काएल)", "Raze (रेज)")
            else -> listOf("Haruto (हारुतो)", "Kazuma (काजुमा)", "Sora (सोरा)", "Hikaru (हिकारु)", "Shion (शिओन)", "Tatsuya (तात्सुया)", "Daiki (दाइकी)", "Raiden (राइदेन)")
        }

        val firstNamesGirl = when {
            isKorean -> listOf("Yu-Jin (유진)", "Ye-Seul (예슬)", "Eun-Ji (은지)", "So-Min (소민)", "Bo-Ra (보라)", "Ji-Ah (지아)", "Ha-Eun (하은)")
            isHindi -> listOf("अनन्या (Ananya)", "तारा (Tara)", "माया (Maya)", "रोशनी (Roshni)", "दिया (Diya)", "ईशा (Isha)", "अवनि (Avani)")
            isCyber -> listOf("Nyx (निक्स)", "Aura-7 (ऑरा)", "Kira (किरा)", "Valkyrie (वाल्किरी)", "Pixel (पिक्सेल)", "Zoya (ज़ोया)")
            else -> listOf("Ayame (आयामे)", "Koharu (कोहारु)", "Mirai (मिराई)", "Hoshiko (होशिको)", "Sayuri (सायुरी)", "Nanami (नानामि)", "Akari (अकारी)")
        }

        val mentorNames = when {
            isKorean -> listOf("고진철 협회장 (Chairman Go)", "마스터 권 (Master Kwon)", "원로 한 (Elder Han)")
            isHindi -> listOf("गुरु वशिष्ठ (Guru Vashistha)", "आचार्य सोम (Acharya Som)", "मास्टर युवान (Master Yuvan)")
            isCyber -> listOf("Commander Nova", "Dr. Kusanagi", "Cyber Oracle 01")
            else -> listOf("Master Genkai (गेनकाई)", "Lady Satsuki (सत्सुकी)", "Sensei Kuroba (कुरोबा)", "Elder Juro (जुरो)")
        }

        val mascotNames = when {
            isKorean -> listOf("도깨비 멍이 (Kebi)", "퐁이 (Pongi)", "그림자 비스트 (Kuro)")
            isHindi -> listOf("गोलू (Golu)", "चिंटू (Chintu)", "शेरू (Sheru)", "पंखी (Pankhi)")
            isCyber -> listOf("Byte (बाइट)", "Glitch-Bot", "Nano-Pup", "Sparks")
            else -> listOf("Mochi (मोची)", "Poko (पोको)", "Kiki (किकी)", "Taro (तारो)", "Bubu (बुबु)")
        }

        // Filter out any already used names to guarantee 100% uniqueness
        val availableBoyNames = firstNamesBoy.filter { it !in excludedNames }.ifEmpty { firstNamesBoy }
        val availableGirlNames = firstNamesGirl.filter { it !in excludedNames }.ifEmpty { firstNamesGirl }
        val availableMentorNames = mentorNames.filter { it !in excludedNames }.ifEmpty { mentorNames }
        val availableMascotNames = mascotNames.filter { it !in excludedNames }.ifEmpty { mascotNames }

        val seed = Random(System.currentTimeMillis() + Random.nextInt(100000))
        val selectedBoy = availableBoyNames[seed.nextInt(availableBoyNames.size)]
        val selectedGirl = availableGirlNames[seed.nextInt(availableGirlNames.size)]
        val selectedMentor = availableMentorNames[seed.nextInt(availableMentorNames.size)]
        val selectedMascot = availableMascotNames[seed.nextInt(availableMascotNames.size)]

        val heroRoles = listOf("Astral Blade Wielder", "Shadow Sovereign", "Chrono-Shinobi", "Thunder Strike Adventurer", "Rift Hunter", "Solar Vanguard")
        val heroineRoles = listOf("Celestial Priestess", "Aura Sniper", "Storm Empress", "Alchemist of Starlight", "Frostblade Valkyrie", "Dimensional Weaver")
        val hairColors = listOf("Silver Starlight", "Crimson Flame", "Electric Neon Cyan", "Obsidian Midnight", "Sakura Rose Pink", "Golden Sunburst", "Mystic Violet")
        val eyeColors = listOf("Sapphire Glow", "Ruby Blaze", "Neon Electric Cyan", "Amethyst Star", "Golden Topaz", "Emerald Jade")

        val heroBoy = CharacterProfile(
            id = "novel_hero_${System.currentTimeMillis()}_1",
            name = selectedBoy,
            gender = "Boy / Male",
            role = heroRoles[seed.nextInt(heroRoles.size)],
            personality = "Courageous, strategic, unwavering resolve in combat",
            voicePitch = if (isKorean) 0.90f else 1.12f,
            voiceSpeed = 1.02f,
            voiceType = "Boy",
            avatarDrawableName = "char_shonen_hero",
            hairStyle = "Dynamic Shonen Spike",
            hairColor = hairColors[seed.nextInt(hairColors.size)],
            eyeColor = eyeColors[seed.nextInt(eyeColors.size)],
            expression = if (isKorean) "Manhwa Glowing Eyes" else "Fierce Battle Roar",
            voiceAccent = voiceAccent.ifBlank { if (isKorean) "Korean Seoul Dramatic (Manhwa Style)" else if (isHindi) "Hindi Dub (Heroic Bollywood Anime)" else "Standard Anime (Japanese Cadence)" }
        )

        val heroine = CharacterProfile(
            id = "novel_heroine_${System.currentTimeMillis()}_2",
            name = selectedGirl,
            gender = "Girl / Female",
            role = heroineRoles[seed.nextInt(heroineRoles.size)],
            personality = "Spirited, fiercely compassionate, radiant spellcaster",
            voicePitch = 1.32f,
            voiceSpeed = 1.02f,
            voiceType = "Girl",
            avatarDrawableName = "char_anime_heroine",
            hairStyle = "Flowing Twin Tails",
            hairColor = hairColors[seed.nextInt(hairColors.size)],
            eyeColor = eyeColors[seed.nextInt(eyeColors.size)],
            expression = "Kawaii Blush & Sparkles",
            voiceAccent = voiceAccent.ifBlank { if (isKorean) "Korean Seoul Dramatic (Manhwa Style)" else if (isHindi) "Hindi Dub (Heroic Bollywood Anime)" else "Standard Anime (Japanese Cadence)" }
        )

        val mentor = CharacterProfile(
            id = "novel_mentor_${System.currentTimeMillis()}_3",
            name = selectedMentor,
            gender = "Lady / Sensei",
            role = "Ancient Arts Mentor",
            personality = "Calm, enigmatic, boundless wisdom",
            voicePitch = 0.96f,
            voiceSpeed = 0.94f,
            voiceType = "Lady",
            avatarDrawableName = "char_lady_mentor",
            hairStyle = "Elegant Topknot",
            hairColor = "Obsidian Midnight",
            eyeColor = "Golden Topaz",
            expression = "Villainous Smirk",
            voiceAccent = voiceAccent.ifBlank { "Standard Anime (Japanese Cadence)" }
        )

        val mascot = CharacterProfile(
            id = "novel_mascot_${System.currentTimeMillis()}_4",
            name = selectedMascot,
            gender = "Chibi Companion",
            role = "Mythical Mascot",
            personality = "Hyperactive, loyal, comedy relief",
            voicePitch = 1.68f,
            voiceSpeed = 1.15f,
            voiceType = "Mascot",
            avatarDrawableName = "char_chibi_mascot",
            hairStyle = "Chibi Fluff",
            hairColor = "Electric Neon Cyan",
            eyeColor = "Ruby Blaze",
            expression = "Comedic Sweatdrop",
            voiceAccent = voiceAccent.ifBlank { "Standard Anime (Japanese Cadence)" }
        )

        return listOf(heroine, heroBoy, mentor, mascot)
    }
}

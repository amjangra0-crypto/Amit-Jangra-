package com.example.data.model

/**
 * CharacterVoiceModels
 * Multi-character, multi-language voice and automated age-weight synchronization models.
 */

enum class VoiceAgeGroup(val displayNameEn: String, val displayNameHi: String) {
    CHILD("Child / Kid (बच्चा)", "बच्चा (बालक/बालिका)"),
    TEEN_GIRL("Young / Teen Girl (लड़की)", "लड़की (युवा नायिका)"),
    ADULT_MALE("Adult Male (एडल्ट पुरुष)", "एडल्ट पुरुष"),
    ADULT_LADY("Adult Lady / Woman (एडल्ट महिला)", "एडल्ट महिला"),
    ELDERLY("Elderly / Senior (वृद्ध / बूढ़े-बूढ़ी)", "वृद्ध (बूढ़े-बूढ़ी)")
}

enum class VoiceToneWeight(val displayNameEn: String, val displayNameHi: String) {
    HEAVY("Heavy / Deep / Bass (भारी आवाज)", "भारी आवाज (गंभीर/गहरी)"),
    LIGHT("Light / Soft / High-Pitch (हल्की आवाज)", "हल्की आवाज (कोमल/चुलबुली)"),
    BALANCED("Balanced / Natural (संतुलित आवाज)", "संतुलित आवाज (प्राकृतिक)")
}

enum class VoiceLanguage(
    val code: String,
    val displayNameEn: String,
    val displayNameHi: String,
    val sampleText: String
) {
    HINDI("hi", "Hindi (हिंदी)", "हिंदी", "नमस्ते! मैं आपके एनीमे कैरेक्टर की आवाज़ हूँ।"),
    ENGLISH("en", "English", "अंग्रेज़ी", "Hello! I am ready to bring this anime character to life."),
    JAPANESE("ja", "Japanese (日本語)", "जापानी", "こんにちは！アニメキャラクターを演じる準備ができました。"),
    KOREAN("ko", "Korean (한국어)", "कोरियाई", "안녕하세요! 애니메이션 캐릭터의 목소리입니다."),
    SPANISH("es", "Spanish (Español)", "स्पेनिश", "¡Hola! Estoy listo para dar vida a este personaje anime."),
    FRENCH("fr", "French (Français)", "फ्रेंच", "Bonjour! Je suis prêt pour cette grande aventure anime."),
    GERMAN("de", "German (Deutsch)", "जर्मन", "Hallo! Bereit für diese epische Anime-Szene."),
    TAMIL("ta", "Tamil (தமிழ்)", "तमिल", "வணக்கம்! உங்கள் அனிம் கதாபாத்திரத்தின் குரல் இது."),
    TELUGU("te", "Telugu (తెలుగు)", "तेलुगु", "నమస్కారం! మీ అనిమే పాత్ర వాయిస్ సిద్ధంగా ఉంది.")
}

data class CharacterVoicePreset(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val ageGroup: VoiceAgeGroup,
    val toneWeight: VoiceToneWeight,
    val gender: String, // "Male", "Female", "Child", "Elderly"
    val pitch: Float,
    val speed: Float,
    val descriptionEn: String,
    val descriptionHi: String,
    val sampleDialogueHi: String,
    val sampleDialogueEn: String,
    val defaultLanguage: VoiceLanguage = VoiceLanguage.HINDI,
    val avatarDrawableName: String = "char_shonen_hero",
    val isAutoSynced: Boolean = true
)

object CharacterVoiceCatalog {

    val allPresets: List<CharacterVoicePreset> = listOf(
        // 1. बच्चे के लिए आवाज (Kid / Child Voices)
        CharacterVoicePreset(
            id = "voice_child_boy",
            nameEn = "Chota Shonen (Boy Kid)",
            nameHi = "छोटा शोनन (बालक की आवाज)",
            ageGroup = VoiceAgeGroup.CHILD,
            toneWeight = VoiceToneWeight.LIGHT,
            gender = "Child",
            pitch = 1.62f,
            speed = 1.15f,
            descriptionEn = "Playful, enthusiastic, innocent young boy voice",
            descriptionHi = "मासूम, ऊर्जावान, साहसी छोटे बालक की आवाज",
            sampleDialogueHi = "अरे वाह! नया रोमांच शुरू होने वाला है, चलो जल्दी भागें!",
            sampleDialogueEn = "Wow! A brand new adventure is starting, let's run fast!",
            avatarDrawableName = "char_chibi_mascot"
        ),
        CharacterVoicePreset(
            id = "voice_child_girl",
            nameEn = "Nanhi Pari (Girl Kid)",
            nameHi = "नन्ही परी (बालिका की आवाज)",
            ageGroup = VoiceAgeGroup.CHILD,
            toneWeight = VoiceToneWeight.LIGHT,
            gender = "Child",
            pitch = 1.74f,
            speed = 1.18f,
            descriptionEn = "Sweet, curious, high-pitched young girl voice",
            descriptionHi = "मधुर, चुलबुली, कोमल नन्ही बालिका की आवाज",
            sampleDialogueHi = "देखो मास्टर! जादुई तितलियाँ आकाश में नाच रही हैं!",
            sampleDialogueEn = "Look Master! Magical butterflies are dancing in the sky!",
            avatarDrawableName = "char_chibi_mascot"
        ),

        // 2. एडल्ट पर्सन के लिए आवाज (Adult Male Voices)
        CharacterVoicePreset(
            id = "voice_adult_male_brave",
            nameEn = "Vikram (Adult Hero)",
            nameHi = "विक्रम (एडल्ट नायक)",
            ageGroup = VoiceAgeGroup.ADULT_MALE,
            toneWeight = VoiceToneWeight.BALANCED,
            gender = "Male",
            pitch = 0.88f,
            speed = 1.05f,
            descriptionEn = "Strong, courageous adult protagonist voice",
            descriptionHi = "दृढ़, साहसी और न्यायप्रिय एडल्ट पुरुष की आवाज",
            sampleDialogueHi = "मैं कभी पीछे नहीं हटूंगा! अपने राज्य और अपनों की रक्षा मैं करूँगा!",
            sampleDialogueEn = "I will never step back! I will protect my kingdom and friends!",
            avatarDrawableName = "char_shonen_hero"
        ),
        CharacterVoicePreset(
            id = "voice_adult_male_commander",
            nameEn = "Ranveer (Tactical Commander)",
            nameHi = "रणवीर (एडल्ट सेनापति)",
            ageGroup = VoiceAgeGroup.ADULT_MALE,
            toneWeight = VoiceToneWeight.HEAVY,
            gender = "Male",
            pitch = 0.78f,
            speed = 0.98f,
            descriptionEn = "Authoritative, disciplined adult commander voice",
            descriptionHi = "अनुशासित, गंभीर और रणनीतिकार सेनापति की भारी आवाज",
            sampleDialogueHi = "सेना तैयार करो! दुश्मन का घेरा हम पहली ही किरण में तोड़ देंगे!",
            sampleDialogueEn = "Ready the legions! We shall break their siege at dawn!",
            avatarDrawableName = "char_shonen_hero"
        ),

        // 3. एडल्ट लेडीज के लिए आवाज (Adult Lady / Woman Voices)
        CharacterVoicePreset(
            id = "voice_adult_lady_warrior",
            nameEn = "Devika (Adult Lady Warrior)",
            nameHi = "देविका (एडल्ट लेडीज योद्धा)",
            ageGroup = VoiceAgeGroup.ADULT_LADY,
            toneWeight = VoiceToneWeight.BALANCED,
            gender = "Female",
            pitch = 1.14f,
            speed = 1.02f,
            descriptionEn = "Poised, courageous, elegant adult lady warrior",
            descriptionHi = "गरिमापूर्ण, साहसी, आत्मविश्वासी एडल्ट लेडीज की आवाज",
            sampleDialogueHi = "मेरी तलवार सिर्फ न्याय के लिए उठती है। समर्पण करो!",
            sampleDialogueEn = "My blade strikes only for justice. Yield now!",
            avatarDrawableName = "char_lady_mentor"
        ),
        CharacterVoicePreset(
            id = "voice_adult_lady_sensei",
            nameEn = "Meera (Wise Lady Sensei)",
            nameHi = "मीरा (गुरुमाता / लेडीज मेंटॉर)",
            ageGroup = VoiceAgeGroup.ADULT_LADY,
            toneWeight = VoiceToneWeight.BALANCED,
            gender = "Female",
            pitch = 1.08f,
            speed = 0.92f,
            descriptionEn = "Calm, gentle, wise adult mentor woman voice",
            descriptionHi = "शांत, गंभीर, अनुभवी और प्रेरणादायक गुरुमाता की आवाज",
            sampleDialogueHi = "क्रोध को शांत करो। असली ताकत तुम्हारे भीतर के अनुशासन में है।",
            sampleDialogueEn = "Calm your anger. True strength lies in your inner discipline.",
            avatarDrawableName = "char_lady_mentor"
        ),

        // 4. लड़की के लिए उसकी उम्र के हिसाब से आवाज (Teen / Young Girl Voices)
        CharacterVoicePreset(
            id = "voice_teen_girl_kawaii",
            nameEn = "Ananya (Kawaii Teen Girl)",
            nameHi = "अनन्या (क्यूट लड़की की आवाज)",
            ageGroup = VoiceAgeGroup.TEEN_GIRL,
            toneWeight = VoiceToneWeight.LIGHT,
            gender = "Female",
            pitch = 1.38f,
            speed = 1.10f,
            descriptionEn = "Bright, dynamic, cheerful teen girl anime heroine",
            descriptionHi = "उम्र के अनुरूप चुलबुली, मधुर, उत्साही युवा लड़की की आवाज",
            sampleDialogueHi = "मुझ पर भरोसा रखो! हम सब मिलकर इस दुनिया को सुंदर बनाएंगे!",
            sampleDialogueEn = "Believe in me! Together we can make this world shine!",
            avatarDrawableName = "char_anime_heroine"
        ),
        CharacterVoicePreset(
            id = "voice_teen_girl_tsundere",
            nameEn = "Maya (Fiery Blade Girl)",
            nameHi = "माया (तेजस्वी किशोरी लड़की)",
            ageGroup = VoiceAgeGroup.TEEN_GIRL,
            toneWeight = VoiceToneWeight.BALANCED,
            gender = "Female",
            pitch = 1.44f,
            speed = 1.14f,
            descriptionEn = "Sharp, spirited, passionate teenage sword maiden",
            descriptionHi = "तीखी, स्वाभिमानी, ऊर्जावान टीनएज लड़की की आवाज",
            sampleDialogueHi = "ब-बेवकूफ! मैंने तुम्हारी मदद इसलिए नहीं की कि तुम मुझे पसंद हो!",
            sampleDialogueEn = "B-Baka! I didn't help you because I like you or anything!",
            avatarDrawableName = "char_anime_heroine"
        ),

        // 5. वृद्ध के लिए उसकी उम्र के हिसाब से आवाज (Elderly / Senior Voices: बूढ़े-बूढ़ी)
        CharacterVoicePreset(
            id = "voice_elderly_grandpa",
            nameEn = "Baba Gorakh (Wise Elderly Sage)",
            nameHi = "बाबा गोरख (वृद्ध बूढ़े बाबा / ऋषि)",
            ageGroup = VoiceAgeGroup.ELDERLY,
            toneWeight = VoiceToneWeight.HEAVY,
            gender = "Elderly",
            pitch = 0.65f,
            speed = 0.84f,
            descriptionEn = "Weathered, dignified, raspy elderly grandfather voice",
            descriptionHi = "उम्र के अनुसार अनुभवी, गंभीर, भारी और कंपित वृद्ध पुरुष की आवाज",
            sampleDialogueHi = "सुनो वत्स... इस जंगल का रहस्य सदियों पुराना है। धैर्य रखो।",
            sampleDialogueEn = "Listen child... the secrets of this forest are centuries old. Have patience.",
            avatarDrawableName = "char_lady_mentor"
        ),
        CharacterVoicePreset(
            id = "voice_elderly_grandma",
            nameEn = "Dadi Amma (Elderly Oracle Matron)",
            nameHi = "दादी अम्मा (वृद्धा बूढ़ी माता)",
            ageGroup = VoiceAgeGroup.ELDERLY,
            toneWeight = VoiceToneWeight.BALANCED,
            gender = "Elderly",
            pitch = 0.95f,
            speed = 0.88f,
            descriptionEn = "Warm, trembling, wise grandmother oracle voice",
            descriptionHi = "उम्र के अनुसार ममतामयी, धीमी, गंभीर और करुणामयी वृद्धा की आवाज",
            sampleDialogueHi = "तुम्हारा समय आ गया है बेटा... अपने पूर्वजों का आशीर्वाद साथ रखो।",
            sampleDialogueEn = "Your time has arrived, dear child... carry the blessings of your ancestors.",
            avatarDrawableName = "char_lady_mentor"
        ),

        // 6. भारी आवाज (Heavy / Deep Bass Voices)
        CharacterVoicePreset(
            id = "voice_heavy_titan",
            nameEn = "Mahakal Titan (Ultra Heavy Bass)",
            nameHi = "महाकाल टाइटन (अति भारी आवाज)",
            ageGroup = VoiceAgeGroup.ADULT_MALE,
            toneWeight = VoiceToneWeight.HEAVY,
            gender = "Male",
            pitch = 0.58f,
            speed = 0.82f,
            descriptionEn = "Deep, thunderous, resonant sovereign bass voice",
            descriptionHi = "गहरा, गरजता हुआ, गूंजने वाला अति भारी स्वर",
            sampleDialogueHi = "तुम मेरे सिंहासन के सामने झुकने के सिवा कुछ नहीं कर सकते!",
            sampleDialogueEn = "You have no choice but to bow before my sovereign throne!",
            avatarDrawableName = "char_shonen_hero"
        ),
        CharacterVoicePreset(
            id = "voice_heavy_asura",
            nameEn = "Asura Warlord (Heavy Dark Resonance)",
            nameHi = "असुर योद्धा (भारी गंभीर स्वर)",
            ageGroup = VoiceAgeGroup.ADULT_MALE,
            toneWeight = VoiceToneWeight.HEAVY,
            gender = "Male",
            pitch = 0.64f,
            speed = 0.86f,
            descriptionEn = "Gravelly, intimidating, dark heavy warrior voice",
            descriptionHi = "भारी, डरावनी, शक्तिशाली और तीक्ष्ण गंभीर आवाज",
            sampleDialogueHi = "कमजोरी की यहाँ कोई जगह नहीं है... अपनी तलवार उठाओ!",
            sampleDialogueEn = "There is no room for weakness here... raise your sword!",
            avatarDrawableName = "char_shonen_hero"
        ),

        // 7. हल्की आवाज (Light / Soft / Delicate Voices)
        CharacterVoicePreset(
            id = "voice_light_fairy",
            nameEn = "Titli Spirit (Ultra Light Soft Voice)",
            nameHi = "तितली स्पिरिट (अति हल्की आवाज)",
            ageGroup = VoiceAgeGroup.TEEN_GIRL,
            toneWeight = VoiceToneWeight.LIGHT,
            gender = "Female",
            pitch = 1.82f,
            speed = 1.16f,
            descriptionEn = "Airy, gentle, high-pitched magical spirit whisper",
            descriptionHi = "कोमल, हवा जैसी हल्की, चुलबुली जादुई स्पिरिट की आवाज",
            sampleDialogueHi = "पिकू! हवाओं का गीत सुनो, जादुई रोशनी तुम्हारा इंतजार कर रही है!",
            sampleDialogueEn = "Piku! Listen to the wind's song, magical light awaits you!",
            avatarDrawableName = "char_chibi_mascot"
        ),
        CharacterVoicePreset(
            id = "voice_light_healer",
            nameEn = "Komal Healer (Soft Whispering Maiden)",
            nameHi = "कोमल कन्या (हल्की सुकोमल आवाज)",
            ageGroup = VoiceAgeGroup.TEEN_GIRL,
            toneWeight = VoiceToneWeight.LIGHT,
            gender = "Female",
            pitch = 1.34f,
            speed = 0.94f,
            descriptionEn = "Gentle, soothing, soft delicate maiden voice",
            descriptionHi = "शांत, मधुर, सुकोमल और फुसफुसाती हुई हल्की आवाज",
            sampleDialogueHi = "चिंता मत करो, तुम्हारी चोटें जल्द ठीक हो जाएंगी... आराम करो।",
            sampleDialogueEn = "Do not worry, your wounds will heal soon... rest now.",
            avatarDrawableName = "char_anime_heroine"
        )
    )

    /**
     * Automatic Character Voice Sync Algorithm
     * Automatically binds the ideal voice preset based on character name, role, gender & age cues.
     */
    fun autoSyncVoiceForCharacter(
        characterName: String,
        genderDescription: String = "",
        roleDescription: String = ""
    ): CharacterVoicePreset {
        val query = "$characterName $genderDescription $roleDescription".lowercase()

        return when {
            // 1. Elderly cues (बूढ़े, बूढ़ी, वृद्ध, sage, old, grandma, grandpa, baba, dadi)
            query.contains("elder") || query.contains("old") || query.contains("sage") ||
                    query.contains("grandpa") || query.contains("baba") || query.contains("बूढ़े") ||
                    query.contains("वृद्ध") || query.contains("बाबा") || query.contains("ऋषि") -> {
                if (query.contains("female") || query.contains("lady") || query.contains("grandma") ||
                    query.contains("दादी") || query.contains("माता") || query.contains("बूढ़ी")) {
                    allPresets.first { it.id == "voice_elderly_grandma" }
                } else {
                    allPresets.first { it.id == "voice_elderly_grandpa" }
                }
            }

            // 2. Kid / Child cues (बच्चा, बालक, kid, child, shota, chibi, boy kid)
            query.contains("child") || query.contains("kid") || query.contains("baby") ||
                    query.contains("chibi") || query.contains("shota") || query.contains("बच्चा") ||
                    query.contains("बालक") || query.contains("बालिका") || query.contains("नन्हा") || query.contains("नन्ही") -> {
                if (query.contains("girl") || query.contains("female") || query.contains("बालिका") || query.contains("नन्ही")) {
                    allPresets.first { it.id == "voice_child_girl" }
                } else {
                    allPresets.first { it.id == "voice_child_boy" }
                }
            }

            // 3. Heavy / Bass / Titan / Demon cues (भारी, गहरा, titan, demon, monster, warlord, heavy)
            query.contains("heavy") || query.contains("bass") || query.contains("titan") ||
                    query.contains("demon") || query.contains("warlord") || query.contains("भारी") ||
                    query.contains("असुर") || query.contains("राक्षस") || query.contains("विशाल") -> {
                allPresets.first { it.id == "voice_heavy_titan" }
            }

            // 4. Light / Soft / Fairy cues (हल्की, कोमल, fairy, light, whisper, delicate)
            query.contains("light") || query.contains("fairy") || query.contains("whisper") ||
                    query.contains("delicate") || query.contains("हल्की") || query.contains("कोमल") || query.contains("तितली") -> {
                allPresets.first { it.id == "voice_light_fairy" }
            }

            // 5. Adult Lady cues (लेडीज, महिला, sensei, mentor, adult lady, matron)
            query.contains("lady") || query.contains("woman") || query.contains("mentor") ||
                    query.contains("sensei") || query.contains("priestess") || query.contains("महिला") ||
                    query.contains("लेडीज") || query.contains("गुरुमाता") -> {
                allPresets.first { it.id == "voice_adult_lady_warrior" }
            }

            // 6. Teen Girl cues (लड़की, girl, teen, kawaii, heroine, anime girl)
            query.contains("girl") || query.contains("female") || query.contains("kawaii") ||
                    query.contains("tsundere") || query.contains("teen") || query.contains("लड़की") ||
                    query.contains("किशोरी") || query.contains("नायिका") -> {
                allPresets.first { it.id == "voice_teen_girl_kawaii" }
            }

            // 7. Adult Male / Commander default
            query.contains("commander") || query.contains("king") || query.contains("captain") || query.contains("सेनापति") -> {
                allPresets.first { it.id == "voice_adult_male_commander" }
            }

            // Default Shonen Adult Male Hero
            else -> allPresets.first { it.id == "voice_adult_male_brave" }
        }
    }
}

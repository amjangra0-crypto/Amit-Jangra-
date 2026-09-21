package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterProfile
import com.example.ui.AnimeViewModel
import com.example.ui.components.ResourceHelpers
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

// Hair Style Definition
data class HairStyleOption(
    val id: String,
    val name: String,
    val description: String,
    val category: String
)

// Color Swatch Definition
data class ColorSwatch(
    val name: String,
    val color: Color,
    val hexCode: String
)

// Outfit Definition
data class OutfitOption(
    val id: String,
    val name: String,
    val description: String,
    val archetype: String,
    val recommendedColor: String
)

// AI-Generated Voice Profile Definition (Male, Female, Child)
data class AiVoiceProfile(
    val id: String,
    val name: String,
    val gender: String, // "Male", "Female", "Child"
    val archetype: String,
    val defaultPitch: Float,
    val defaultSpeed: Float,
    val defaultDialogue: String,
    val description: String,
    val avatarDrawableName: String = "char_shonen_hero"
)

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun CharacterBuilderScreen(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()
    val context = LocalContext.current
    val draft = state.characterDraft

    // Category Tabs: 0 = बाल (Hair), 1 = आँखें (Eyes), 2 = पोशाक (Outfits), 3 = ऑरा व एक्सेसरी (Aura & FX), 4 = आवाज व पहचान (Voice & Identity)
    var selectedCategoryTab by remember { mutableIntStateOf(0) }

    // Hair Options Catalog
    val hairStyles = remember {
        listOf(
            HairStyleOption("spiky_shonen", "Spiky Shonen", "ऊर्जावान नुकीले बाल (Iconic hero spikes)", "Male/Boy"),
            HairStyleOption("twin_tails", "Kawaii Twin Tails", "क्यूट ट्विन टेल्स रिबन के साथ (Classic Anime Twintails)", "Female"),
            HairStyleOption("flowing_celestial", "Flowing Celestial", "लंबे रेशमी लहराते बाल (Elegant flowing locks)", "Universal"),
            HairStyleOption("samurai_ponytail", "Samurai Ponytail", "योद्धा पोनीटेल व टॉपनॉट (Traditional warrior tail)", "Universal"),
            HairStyleOption("anime_bob", "Anime Bob", "शार्प कट बॉब हेयरस्टाइल (Clean modern angled bob)", "Female/Sensei"),
            HairStyleOption("wolf_cut", "Wolf Cut", "रेबेलियस लेयर्ड वुल्फ कट (Modern rebellious shaggy hair)", "Universal"),
            HairStyleOption("braided_shinobi", "Braided Shinobi", "टैक्टिकल साइड चोटी व बैंन्ग्स (Ninja battle braids)", "Universal"),
            HairStyleOption("pixie_cyber", "Pixie Cyber", "फ्यूचरिस्टिक नियॉन पिक्सी कट (Hi-tech cropped style)", "Universal")
        )
    }

    // Hair Color Palette
    val hairColors = remember {
        listOf(
            ColorSwatch("Silver Starlight", Color(0xFFE2E8F0), "#E2E8F0"),
            ColorSwatch("Sakura Blossom", Color(0xFFFF7597), "#FF7597"),
            ColorSwatch("Electric Cyan", Color(0xFF00E5FF), "#00E5FF"),
            ColorSwatch("Crimson Flame", Color(0xFFFF1744), "#FF1744"),
            ColorSwatch("Midnight Obsidian", Color(0xFF1E1E28), "#1E1E28"),
            ColorSwatch("Golden Sun", Color(0xFFFFD700), "#FFD700"),
            ColorSwatch("Mystic Amethyst", Color(0xFFB388FF), "#B388FF"),
            ColorSwatch("Emerald Shinobi", Color(0xFF00E676), "#00E676"),
            ColorSwatch("Sunset Coral", Color(0xFFFF7043), "#FF7043"),
            ColorSwatch("Neon Lavender", Color(0xFFEA80FC), "#EA80FC")
        )
    }

    // Eye Colors Catalog
    val eyeColors = remember {
        listOf(
            ColorSwatch("Sapphire Neon Blue", Color(0xFF2979FF), "#2979FF"),
            ColorSwatch("Crimson Ruby", Color(0xFFFF1744), "#FF1744"),
            ColorSwatch("Mystic Amethyst", Color(0xFFAB47BC), "#AB47BC"),
            ColorSwatch("Emerald Jade", Color(0xFF00E676), "#00E676"),
            ColorSwatch("Golden Topaz", Color(0xFFFFC107), "#FFC107"),
            ColorSwatch("Cyber Aqua", Color(0xFF00E5FF), "#00E5FF"),
            ColorSwatch("Rose Quartz", Color(0xFFF06292), "#F06292"),
            ColorSwatch("Void Obsidian", Color(0xFF37474F), "#37474F")
        )
    }

    // Eye Expression & Gaze Catalog
    val eyeExpressions = remember {
        listOf(
            "Fierce Determined Stare" to "🔥 दृढ़ निश्चयी (Determined Hero)",
            "Soft Kawaii Smile" to "✨ सौम्य कवाई (Soft Kawaii)",
            "Tsundere Glare" to "💢 त्सुन्दरे तेवर (Tsundere Glare)",
            "Mysterious Smirk" to "🌙 रहस्यमयी मुस्कान (Mysterious Smirk)",
            "Heroic Combat Gaze" to "⚔️ युद्ध तत्पर (Combat Focused)",
            "Sparkling Wonder" to "⭐ आशावान चमक (Sparkling Wonder)"
        )
    }

    // Outfits Catalog
    val outfits = remember {
        listOf(
            OutfitOption(
                "cyber_shinobi",
                "Cyber Shinobi Exo-Suit",
                "कार्बन-फाइबर नैनो आर्मर व नियॉन सर्किट्स (Futuristic stealth ninja suit)",
                "Sci-Fi / Action",
                "Obsidian Black & Neon Cyan"
            ),
            OutfitOption(
                "royal_kimono",
                "Royal Astral Kimono",
                "रेशमी पारम्परिक किमोनो स्वर्णिम बॉर्डर संग (Traditional celestial ceremonial robe)",
                "Fantasy / Traditional",
                "Crimson Scarlet & Imperial Gold"
            ),
            OutfitOption(
                "academy_blazer",
                "High Academy Blazer",
                "क्लासिक जापानी हाई स्कूल यूनिफॉर्म (Iconic anime blazer with gold crest)",
                "Slice of Life / Supernatural",
                "Midnight Navy & Crimson Tie"
            ),
            OutfitOption(
                "battle_robes",
                "Shonen Battle Robes",
                "मार्शल आर्ट्स दोबॉक व वॉरियर सैश (Torn martial arts battle gi)",
                "Shonen Adventure",
                "Orange Flame & Indigo"
            ),
            OutfitOption(
                "astral_mage",
                "Astral Mage Robe",
                "रहस्यमयी हूडिड मैजिक केप (Cosmic hooded cloak with glowing runes)",
                "Isekai / Magic",
                "Shadow Violet & Silver"
            ),
            OutfitOption(
                "mecha_pilot",
                "Mecha Pilot Plugsuit",
                "एयरोडायनामिक प्रेशराइज्ड कॉकपिट सूट (High-velocity mecha cockpit pilot gear)",
                "Mecha / Sci-Fi",
                "Glacier White & Electric Blue"
            ),
            OutfitOption(
                "neo_samurai",
                "Streetwear Neo-Samurai",
                "मॉडर्न ओवरसाइज़्ड हुडी व हाओरी जैकेट (Urban streetwear combined with samurai aesthetic)",
                "Cyberpunk",
                "Obsidian & Emerald Jade"
            )
        )
    }

    // Outfit Color Combos
    val outfitColorCombos = remember {
        listOf(
            "Obsidian Black & Neon Cyan",
            "Crimson Scarlet & Imperial Gold",
            "Pastel Sakura Pink & White",
            "Shadow Violet & Silver",
            "Emerald Jade & Antique Bronze",
            "Glacier White & Electric Blue",
            "Midnight Navy & Crimson Tie",
            "Orange Flame & Indigo"
        )
    }

    // Accessories Catalog
    val accessories = remember {
        listOf(
            "Kitsune Fox Mask" to "🦊 कित्सुने स्पिरिट मास्क",
            "Cyber HUD Visor" to "🕶️ नियॉन साइबर विज़र",
            "Samurai Forehead Protector" to "⚔️ सामुराई हेडबैंड",
            "Kawaii Cat Ears" to "🐾 कवाई कैट इयर्स",
            "Celestial Angel Halo" to "😇 एंजेलिक सेलेस्टियल हेलो",
            "Dragon Horns" to "🐲 ड्रैगन हॉर्न्स",
            "Clean Look (None)" to "✨ बिना एक्सेसरी (Clean Look)"
        )
    }

    // Aura & Particle Effects Catalog
    val auras = remember {
        listOf(
            "Crackling Blue Lightning Sparks" to "⚡ बिजली के कड़कते स्पार्क्स",
            "Swirling Sakura Petal Blizzard" to "🌸 उड़ती चेरी ब्लॉसम पंखुड़ियाँ",
            "Dragon Flame Blaze" to "🔥 प्रज्वलित ड्रैगन ज्वाला",
            "Celestial Stardust Glow" to "✨ दिव्य आकाशीय चमक",
            "Cyber Matrix Grid" to "🌐 डिजिटल नियॉन मैट्रिक्स ग्रिड",
            "Void Shadow Mist" to "🌌 रहस्यमयी गहरी धुंध"
        )
    }

    // Voice Personas Catalog
    val voicePersonas = remember {
        listOf(
            Triple("Deep Shonen Hero", "Male", "दृढ़, साहसी व ऊर्जावान मुख्य नायक (Protagonist)"),
            Triple("Sweet Kawaii Heroine", "Female", "मीठी, मासूम और सहानुभूतिपूर्ण आवाज (Kawaii Heroine)"),
            Triple("Energetic Shonen Youth", "Male", "जोशीला युवा समुराई (Fiery Shonen Boy)"),
            Triple("Stoic Anti-Hero", "Male", "शांत, गंभीर व रहस्यमयी प्रतिद्वंद्वी (Rival Anti-Hero)"),
            Triple("Wise Sensei Mentor", "Female", "शांत, परिपक्व व अनुभवी मेंटर (Mentor / Sensei)"),
            Triple("Playful Chibi Mascot", "Mascot", "चुलबुला, नटखट और फन स्पिरिट (Cute Spirit Guide)"),
            Triple("Ethereal Princess", "Female", "शाही, सौम्य व जादुई राजकुमारी (Astral Princess)")
        )
    }

    // Comprehensive Curated AI-Generated Voice Profiles (Male, Female, Child)
    val aiVoiceProfiles = remember {
        listOf(
            // --- Male Voice Profiles ---
            AiVoiceProfile(
                id = "male_shonen_hero",
                name = "Deep Shonen Hero",
                gender = "Male",
                archetype = "Courageous Protagonist",
                defaultPitch = 0.88f,
                defaultSpeed = 1.10f,
                defaultDialogue = "मैं कभी पीछे नहीं हटूंगा! अपने दोस्तों के लिए मैं कुछ भी कर सकता हूँ!",
                description = "जोशीला, दृढ़ और अदम्य साहस से भरा मुख्य नायक",
                avatarDrawableName = "char_shonen_hero"
            ),
            AiVoiceProfile(
                id = "male_sensei",
                name = "Calm Master Sensei",
                gender = "Male",
                archetype = "Disciplined Mentor",
                defaultPitch = 0.76f,
                defaultSpeed = 0.90f,
                defaultDialogue = "धैर्य और शांति ही एक सच्चे योद्धा की सबसे बड़ी शक्ति है। अपनी सांसों को नियंत्रित करो।",
                description = "गंभीर, शांत और अनुभवी गुरु की गहरी आवाज",
                avatarDrawableName = "char_lady_mentor"
            ),
            AiVoiceProfile(
                id = "male_dark_rival",
                name = "Stoic Dark Anti-Hero",
                gender = "Male",
                archetype = "Cold Rival / Avenger",
                defaultPitch = 0.70f,
                defaultSpeed = 0.88f,
                defaultDialogue = "कमजोरी के लिए इस दुनिया में कोई जगह नहीं है... अपनी तलवार उठाओ!",
                description = "रहस्यमयी, ठंडा और तीक्ष्ण खलनायक / प्रतिद्वंद्वी",
                avatarDrawableName = "char_shonen_hero"
            ),
            AiVoiceProfile(
                id = "male_cyber_rebel",
                name = "Cyberpunk Renegade",
                gender = "Male",
                archetype = "Hi-Tech Rebel",
                defaultPitch = 0.84f,
                defaultSpeed = 1.05f,
                defaultDialogue = "सिस्टम का फायरवॉल टूट चुका है, टीम! चलो शहर को मुक्त कराएं!",
                description = "आधुनिक, फुर्तीला और विद्रोही स्ट्रीट योद्धा",
                avatarDrawableName = "char_shonen_hero"
            ),

            // --- Female Voice Profiles ---
            AiVoiceProfile(
                id = "female_kawaii",
                name = "Sweet Kawaii Heroine",
                gender = "Female",
                archetype = "Bubbly Magical Heroine",
                defaultPitch = 1.38f,
                defaultSpeed = 1.10f,
                defaultDialogue = "मुझ पर विश्वास रखो! हम सब मिलकर इस दुनिया को सुंदर बनाएंगे!",
                description = "क्यूट, मधुर, कोमल और जादुई नायिका",
                avatarDrawableName = "char_anime_heroine"
            ),
            AiVoiceProfile(
                id = "female_tsundere",
                name = "Tsundere Blade Maiden",
                gender = "Female",
                archetype = "Fiery Swordswoman",
                defaultPitch = 1.46f,
                defaultSpeed = 1.15f,
                defaultDialogue = "ब-बेवकूफ! मैंने तुम्हारी मदद इसलिए नहीं की कि तुम मुझे पसंद हो!",
                description = "तीखी, स्वाभिमानी, ऊर्जावान और फुर्तीली योद्धा",
                avatarDrawableName = "char_anime_heroine"
            ),
            AiVoiceProfile(
                id = "female_priestess",
                name = "Regal Celestial Priestess",
                gender = "Female",
                archetype = "Ethereal Oracle",
                defaultPitch = 1.16f,
                defaultSpeed = 0.94f,
                defaultDialogue = "पवित्र चेरी ब्लॉसम का प्रकाश तुम्हारे मार्ग को सदा आलोकित करे, प्रिय आत्मा।",
                description = "गरिमापूर्ण, शांत, आध्यात्मिक और दिव्य वाणी",
                avatarDrawableName = "char_lady_mentor"
            ),
            AiVoiceProfile(
                id = "female_kunoichi",
                name = "Stealth Cyber Kunoichi",
                gender = "Female",
                archetype = "Shadow Ninja Assassin",
                defaultPitch = 1.25f,
                defaultSpeed = 1.04f,
                defaultDialogue = "साये में चलो, लक्ष्य पर वार करो, कोई आवाज मत निकालो।",
                description = "फुसफुसाती, सटीक, शांत और खतरनाक निंजा",
                avatarDrawableName = "char_anime_heroine"
            ),

            // --- Child Voice Profiles ---
            AiVoiceProfile(
                id = "child_cheerful",
                name = "Cheerful Shota Adventurer",
                gender = "Child",
                archetype = "Brave Youngster",
                defaultPitch = 1.65f,
                defaultSpeed = 1.18f,
                defaultDialogue = "अरे वाह! नया रोमांच शुरू होने वाला है, चलो जल्दी भागें!",
                description = "ऊर्जावान, मासूम, निडर और चंचल बालक योद्धा",
                avatarDrawableName = "char_chibi_mascot"
            ),
            AiVoiceProfile(
                id = "child_chibi_fairy",
                name = "Kawaii Chibi Mascot Spirit",
                gender = "Child",
                archetype = "Playful Spirit Familiar",
                defaultPitch = 1.82f,
                defaultSpeed = 1.22f,
                defaultDialogue = "पिकू! मास्टर, बिल्कुल चिंता मत करो, मैं हमेशा तुम्हारे साथ हूँ!",
                description = "अल्ट्रा-क्यूट, चुलबुली जादुई स्पिरिट और फेयरी मस्कट",
                avatarDrawableName = "char_chibi_mascot"
            ),
            AiVoiceProfile(
                id = "child_prodigy",
                name = "Mystic Apprentice Prodigy",
                gender = "Child",
                archetype = "Curious Pupil",
                defaultPitch = 1.52f,
                defaultSpeed = 0.98f,
                defaultDialogue = "क्या आप मुझे प्राचीन मंत्रों और गुप्त ऊर्जा का रहस्य सिखा सकते हैं?",
                description = "जिज्ञासु, शांत, प्रतिभाशाली और सौम्य नन्हा शिष्य",
                avatarDrawableName = "char_chibi_mascot"
            )
        )
    }

    // Popular Archetype Presets
    val presets = remember {
        listOf(
            "⚡ Cyber Shinobi" to {
                viewModel.updateCharacterDraft(
                    draft.copy(
                        name = "Raiden (रायदेन)",
                        role = "Cyber Shinobi",
                        gender = "Boy / Youth",
                        hairStyle = "Spiky Shonen",
                        hairColor = "Silver Starlight",
                        eyeColor = "Sapphire Neon Blue",
                        expression = "Fierce Determined Stare",
                        outfit = "Cyber Shinobi Exo-Suit",
                        outfitColor = "Obsidian Black & Neon Cyan",
                        accessoryAura = "Crackling Blue Lightning Sparks",
                        voicePersona = "Deep Shonen Hero",
                        avatarDrawableName = "char_shonen_hero",
                        sampleDialogue = "मेरी बिजली अंधकार को चीर कर नया युग लाएगी!"
                    )
                )
            },
            "🌸 Sakura Shrine Maiden" to {
                viewModel.updateCharacterDraft(
                    draft.copy(
                        name = "Sakura (साकुरा)",
                        role = "Shrine Maiden",
                        gender = "Girl / Female",
                        hairStyle = "Kawaii Twin Tails",
                        hairColor = "Sakura Blossom",
                        eyeColor = "Rose Quartz",
                        expression = "Soft Kawaii Smile",
                        outfit = "Royal Astral Kimono",
                        outfitColor = "Pastel Sakura Pink & White",
                        accessoryAura = "Swirling Sakura Petal Blizzard",
                        voicePersona = "Sweet Kawaii Heroine",
                        avatarDrawableName = "char_anime_heroine",
                        sampleDialogue = "चेरी ब्लॉसम की हर पंखुड़ी में एक प्राचीन आशीर्वाद छुपा है।"
                    )
                )
            },
            "🔥 Dragon Shonen" to {
                viewModel.updateCharacterDraft(
                    draft.copy(
                        name = "Ren (रेन)",
                        role = "Flame Warrior",
                        gender = "Boy / Youth",
                        hairStyle = "Wolf Cut",
                        hairColor = "Crimson Flame",
                        eyeColor = "Golden Topaz",
                        expression = "Heroic Combat Gaze",
                        outfit = "Shonen Battle Robes",
                        outfitColor = "Orange Flame & Indigo",
                        accessoryAura = "Dragon Flame Blaze",
                        voicePersona = "Energetic Shonen Youth",
                        avatarDrawableName = "char_shonen_hero",
                        sampleDialogue = "मेरे दिल की आग कभी बुझ नहीं सकती! चलो आगे बढ़ें!"
                    )
                )
            },
            "✨ Astral Sorceress" to {
                viewModel.updateCharacterDraft(
                    draft.copy(
                        name = "Kyoto (क्योटो)",
                        role = "Astral Sensei",
                        gender = "Lady / Sensei",
                        hairStyle = "Flowing Celestial",
                        hairColor = "Mystic Amethyst",
                        eyeColor = "Mystic Amethyst",
                        expression = "Mysterious Smirk",
                        outfit = "Astral Mage Robe",
                        outfitColor = "Shadow Violet & Silver",
                        accessoryAura = "Celestial Stardust Glow",
                        voicePersona = "Wise Sensei Mentor",
                        avatarDrawableName = "char_lady_mentor",
                        sampleDialogue = "तारों का रहस्य केवल धैर्यवान आत्माओं को ही समझ आता है।"
                    )
                )
            },
            "🐾 Chibi Spirit" to {
                viewModel.updateCharacterDraft(
                    draft.copy(
                        name = "Popo (पोपो)",
                        role = "Spirit Mascot",
                        gender = "Chibi Mascot",
                        hairStyle = "Anime Bob",
                        hairColor = "Golden Sun",
                        eyeColor = "Emerald Jade",
                        expression = "Sparkling Wonder",
                        outfit = "High Academy Blazer",
                        outfitColor = "Pastel Sakura Pink & White",
                        accessoryAura = "Cyber Matrix Grid",
                        voicePersona = "Playful Chibi Mascot",
                        avatarDrawableName = "char_chibi_mascot",
                        sampleDialogue = "पोपो हमेशा तुम्हारी रक्षा करेगा! पोपो पावर!"
                    )
                )
            }
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp)
            .padding(bottom = 80.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(8.dp))

            // Screen Title & Subtitle
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Palette,
                            contentDescription = null,
                            tint = AnimePink,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "🎨 करैक्टर बिल्डर (Character Builder)",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                    Text(
                        text = "हेयर स्टाइल, आँखों का रंग, पोशाक व ऑरा कस्टमाइज करें (Live State Preview)",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Archetype Presets & Quick Randomize Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "क्विक प्रीसेट:",
                    color = AnimeGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(end = 6.dp)
                )
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(presets) { (title, action) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AnimeSurfaceVariant)
                                .border(1.dp, AnimePurple.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                .clickable { action() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(text = title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Interactive Live Anime Character Canvas & Visualizer Card
            CharacterVisualizerCard(
                draft = draft,
                isSpeaking = speakingState.isSpeaking,
                mouthOpenAmount = speakingState.mouthOpenAmount,
                onRandomize = { viewModel.randomizeCharacterDraft() },
                onAuditionVoice = { viewModel.auditionDraftVoice() },
                onSave = { viewModel.saveDraftCharacter() },
                onCastInScene = { viewModel.applyDraftToActiveScene() }
            )

            // Status Message Alert
            if (state.statusMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = AnimePurple.copy(alpha = 0.25f)),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = state.statusMessage, color = AnimeCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // AI Voice Profile Selection (Dropdown or Radio Button Group - Male, Female, Child)
            AiVoiceProfileSelector(
                draft = draft,
                voiceProfiles = aiVoiceProfiles,
                isSpeaking = speakingState.isSpeaking,
                onProfileSelected = { profile ->
                    viewModel.updateCharacterDraft(
                        draft.copy(
                            voicePersona = profile.name,
                            voiceGender = profile.gender,
                            voicePitch = profile.defaultPitch,
                            voiceSpeed = profile.defaultSpeed,
                            sampleDialogue = profile.defaultDialogue,
                            avatarDrawableName = profile.avatarDrawableName,
                            voiceType = profile.gender
                        )
                    )
                },
                onAudition = {
                    viewModel.auditionDraftVoice()
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Builder Customization Category TabRow
            val tabs = listOf(
                "💇 बाल (Hair)",
                "👁️ आँखें (Eyes)",
                "👘 पोशाक (Outfit)",
                "✨ ऑरा व स्टाइल",
                "🎙️ आवाज व नाम"
            )
            TabRow(
                selectedTabIndex = selectedCategoryTab,
                containerColor = AnimeSurface,
                contentColor = AnimeCyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedCategoryTab]),
                        color = AnimePink
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedCategoryTab == index,
                        onClick = { selectedCategoryTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 11.sp,
                                fontWeight = if (selectedCategoryTab == index) FontWeight.Bold else FontWeight.Normal,
                                color = if (selectedCategoryTab == index) AnimePink else TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        },
                        modifier = Modifier.testTag("tab_builder_$index")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Tab Content Panels based on State
        when (selectedCategoryTab) {
            0 -> {
                // HAIR STYLING & HAIR COLOR SECTION
                item {
                    HairCustomizationPanel(
                        currentStyle = draft.hairStyle,
                        currentColor = draft.hairColor,
                        styles = hairStyles,
                        colors = hairColors,
                        onStyleSelected = { newStyle ->
                            viewModel.updateCharacterDraft(draft.copy(hairStyle = newStyle))
                        },
                        onColorSelected = { newColor ->
                            viewModel.updateCharacterDraft(draft.copy(hairColor = newColor))
                        }
                    )
                }
            }
            1 -> {
                // EYES & FACIAL EXPRESSION SECTION
                item {
                    EyesCustomizationPanel(
                        currentColor = draft.eyeColor,
                        currentExpression = draft.expression,
                        colors = eyeColors,
                        expressions = eyeExpressions,
                        onColorSelected = { newColor ->
                            viewModel.updateCharacterDraft(draft.copy(eyeColor = newColor))
                        },
                        onExpressionSelected = { newExpr ->
                            viewModel.updateCharacterDraft(draft.copy(expression = newExpr))
                        }
                    )
                }
            }
            2 -> {
                // OUTFITS & COLOR ACCENTS SECTION
                item {
                    OutfitsCustomizationPanel(
                        currentOutfit = draft.outfit,
                        currentColor = draft.outfitColor,
                        outfits = outfits,
                        colorCombos = outfitColorCombos,
                        onOutfitSelected = { newOutfit ->
                            viewModel.updateCharacterDraft(draft.copy(outfit = newOutfit.name, role = newOutfit.archetype))
                        },
                        onColorSelected = { newColor ->
                            viewModel.updateCharacterDraft(draft.copy(outfitColor = newColor))
                        }
                    )
                }
            }
            3 -> {
                // AURA, ACCESSORIES & ELEMENTAL EFFECTS SECTION
                item {
                    AuraAccessoriesPanel(
                        currentAccessory = draft.accessoryAura,
                        accessories = accessories,
                        auras = auras,
                        onAccessorySelected = { acc ->
                            viewModel.updateCharacterDraft(draft.copy(accessoryAura = acc))
                        }
                    )
                }
            }
            4 -> {
                // VOICE, IDENTITY & DIALOGUE PERSONA SECTION
                item {
                    IdentityVoicePanel(
                        draft = draft,
                        aiVoiceProfiles = aiVoiceProfiles,
                        isSpeaking = speakingState.isSpeaking,
                        onNameChanged = { newName ->
                            viewModel.updateCharacterDraft(draft.copy(name = newName))
                        },
                        onGenderChanged = { newGender ->
                            val avatar = when {
                                newGender.contains("Female", ignoreCase = true) || newGender.contains("Girl", ignoreCase = true) -> "char_anime_heroine"
                                newGender.contains("Lady", ignoreCase = true) || newGender.contains("Mentor", ignoreCase = true) -> "char_lady_mentor"
                                newGender.contains("Mascot", ignoreCase = true) -> "char_chibi_mascot"
                                else -> "char_shonen_hero"
                            }
                            viewModel.updateCharacterDraft(draft.copy(gender = newGender, avatarDrawableName = avatar))
                        },
                        onProfileSelected = { profile ->
                            viewModel.updateCharacterDraft(
                                draft.copy(
                                    voicePersona = profile.name,
                                    voiceGender = profile.gender,
                                    voicePitch = profile.defaultPitch,
                                    voiceSpeed = profile.defaultSpeed,
                                    sampleDialogue = profile.defaultDialogue,
                                    avatarDrawableName = profile.avatarDrawableName,
                                    voiceType = profile.gender
                                )
                            )
                        },
                        onPitchChanged = { pitch ->
                            viewModel.updateCharacterDraft(draft.copy(voicePitch = pitch))
                        },
                        onSpeedChanged = { speed ->
                            viewModel.updateCharacterDraft(draft.copy(voiceSpeed = speed))
                        },
                        onDialogueChanged = { diag ->
                            viewModel.updateCharacterDraft(draft.copy(sampleDialogue = diag))
                        },
                        onAudition = {
                            viewModel.auditionDraftVoice()
                        }
                    )
                }
            }
        }

        // Bottom Action Bar: Save & Cast to Video Player
        item {
            Spacer(modifier = Modifier.height(20.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🚀 करैक्टर उपयोग (Apply & Export):",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Button(
                            onClick = { viewModel.saveDraftCharacter() },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("save_character_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("रोस्टर में सेव करें", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        Button(
                            onClick = { viewModel.applyDraftToActiveScene() },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.weight(1f).testTag("cast_character_btn")
                        ) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("सक्रिय सीन में कास्ट करें", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

// ==========================================
// 1. INTERACTIVE LIVE AVATAR VISUALIZER CARD
// ==========================================
@Composable
fun CharacterVisualizerCard(
    draft: CharacterProfile,
    isSpeaking: Boolean,
    mouthOpenAmount: Float,
    onRandomize: () -> Unit,
    onAuditionVoice: () -> Unit,
    onSave: () -> Unit,
    onCastInScene: () -> Unit
) {
    val context = LocalContext.current
    val infiniteTransition = rememberInfiniteTransition(label = "aura_pulse")
    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "aura_alpha"
    )

    // Parse Hair and Eye Colors to actual Compose Colors
    val parsedHairColor = when {
        draft.hairColor.contains("Silver", ignoreCase = true) -> Color(0xFFE2E8F0)
        draft.hairColor.contains("Sakura", ignoreCase = true) || draft.hairColor.contains("Pink", ignoreCase = true) -> Color(0xFFFF7597)
        draft.hairColor.contains("Cyan", ignoreCase = true) || draft.hairColor.contains("Blue", ignoreCase = true) -> Color(0xFF00E5FF)
        draft.hairColor.contains("Crimson", ignoreCase = true) || draft.hairColor.contains("Red", ignoreCase = true) -> Color(0xFFFF1744)
        draft.hairColor.contains("Golden", ignoreCase = true) || draft.hairColor.contains("Sun", ignoreCase = true) -> Color(0xFFFFD700)
        draft.hairColor.contains("Amethyst", ignoreCase = true) || draft.hairColor.contains("Purple", ignoreCase = true) -> Color(0xFFB388FF)
        draft.hairColor.contains("Emerald", ignoreCase = true) || draft.hairColor.contains("Green", ignoreCase = true) -> Color(0xFF00E676)
        else -> Color(0xFF1E1E28)
    }

    val parsedEyeColor = when {
        draft.eyeColor.contains("Sapphire", ignoreCase = true) || draft.eyeColor.contains("Blue", ignoreCase = true) -> Color(0xFF2979FF)
        draft.eyeColor.contains("Ruby", ignoreCase = true) || draft.eyeColor.contains("Red", ignoreCase = true) -> Color(0xFFFF1744)
        draft.eyeColor.contains("Amethyst", ignoreCase = true) || draft.eyeColor.contains("Purple", ignoreCase = true) -> Color(0xFFAB47BC)
        draft.eyeColor.contains("Jade", ignoreCase = true) || draft.eyeColor.contains("Green", ignoreCase = true) -> Color(0xFF00E676)
        draft.eyeColor.contains("Topaz", ignoreCase = true) || draft.eyeColor.contains("Gold", ignoreCase = true) -> Color(0xFFFFC107)
        draft.eyeColor.contains("Aqua", ignoreCase = true) || draft.eyeColor.contains("Cyan", ignoreCase = true) -> Color(0xFF00E5FF)
        draft.eyeColor.contains("Rose", ignoreCase = true) || draft.eyeColor.contains("Pink", ignoreCase = true) -> Color(0xFFF06292)
        else -> Color(0xFF37474F)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(parsedHairColor, AnimePurple, AnimeCyan)))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Character Avatar Stage with dynamic layered aura, hair tint halo & lipsync
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(AnimeSurfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    // Pulsing Elemental Aura Canvas
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawCircle(
                            brush = Brush.radialGradient(
                                colors = listOf(parsedHairColor.copy(alpha = auraAlpha), Color.Transparent),
                                center = Offset(size.width / 2, size.height / 2),
                                radius = size.width * 0.8f
                            )
                        )
                    }

                    // Avatar Portrait Base
                    val resId = ResourceHelpers.getDrawableId(context, draft.avatarDrawableName)
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = draft.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .border(2.dp, parsedHairColor, RoundedCornerShape(12.dp))
                    )

                    // Lip-Sync Mouth Opening Visualizer
                    if (isSpeaking) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.BottomCenter)
                                .padding(bottom = 12.dp)
                                .size(width = 16.dp, height = (10.dp * mouthOpenAmount).coerceAtLeast(3.dp))
                                .clip(RoundedCornerShape(3.dp))
                                .background(Color.Red.copy(alpha = 0.9f))
                        )
                    }

                    // Eye Color Gem Glow Indicator (top-right badge)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(4.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(parsedEyeColor)
                            .border(1.5.dp, Color.White, CircleShape)
                    )

                    // Hair Style Indicator Badge (top-left badge)
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(4.dp)
                            .size(14.dp)
                            .clip(CircleShape)
                            .background(parsedHairColor)
                            .border(1.5.dp, Color.White, CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                // Character Identity Details & Visual Specs
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = draft.name,
                            color = AnimeCyanLight,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimePurple.copy(alpha = 0.4f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(text = draft.gender, color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(3.dp))

                    // Hair & Eyes Badge
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("💇 ${draft.hairStyle} (${draft.hairColor})", color = TextSecondary, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👁️ ${draft.eyeColor} • ${draft.expression.take(16)}", color = TextSecondary, fontSize = 10.sp)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("👘 ${draft.outfit.take(24)}", color = AnimePink, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨ ${draft.accessoryAura.take(26)}", color = AnimeGold, fontSize = 10.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Quick Action Buttons (Audition Voice & Randomize)
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        Button(
                            onClick = onAuditionVoice,
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.85f)),
                            shape = RoundedCornerShape(6.dp),
                            modifier = Modifier.height(30.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (isSpeaking) "बोल रहा है..." else "आवाज टेस्ट", color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onRandomize,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, AnimePurple),
                            modifier = Modifier.height(30.dp),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Casino, contentDescription = null, tint = AnimePink, modifier = Modifier.size(13.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("🎲 रैंडम", color = AnimePink, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. HAIR STYLING & HAIR COLOR CUSTOMIZATION
// ==========================================
@Composable
fun HairCustomizationPanel(
    currentStyle: String,
    currentColor: String,
    styles: List<HairStyleOption>,
    colors: List<ColorSwatch>,
    onStyleSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title: Hair Color Palette
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Palette, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "बालों का रंग चुनें (Hair Color Palette):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Color Swatches Row
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(colors) { swatch ->
                val isSelected = currentColor.equals(swatch.name, ignoreCase = true) ||
                        currentColor.contains(swatch.name.split(" ").first(), ignoreCase = true)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AnimePurple.copy(alpha = 0.3f) else AnimeSurface)
                        .border(1.5.dp, if (isSelected) swatch.color else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { onColorSelected(swatch.name) }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(swatch.color)
                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = swatch.name.split(" ").first(),
                        color = if (isSelected) AnimeCyanLight else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Hair Style Catalog
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Face, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "हेयर स्टाइल चुनें (Anime Hair Style):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            styles.forEach { style ->
                val isSelected = currentStyle.equals(style.name, ignoreCase = true) || currentStyle.contains(style.name, ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onStyleSelected(style.name) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AnimePurple.copy(alpha = 0.3f) else AnimeSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) AnimeCyan else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = style.name,
                                    color = if (isSelected) AnimeCyanLight else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeSurfaceVariant)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(text = style.category, color = AnimeGold, fontSize = 9.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = style.description, color = TextSecondary, fontSize = 11.sp)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 3. EYES & EXPRESSIONS CUSTOMIZATION
// ==========================================
@Composable
fun EyesCustomizationPanel(
    currentColor: String,
    currentExpression: String,
    colors: List<ColorSwatch>,
    expressions: List<Pair<String, String>>,
    onColorSelected: (String) -> Unit,
    onExpressionSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title: Eye Colors
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Visibility, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "आँखों का रंग (Anime Eye Color):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        // Eye Color Swatches
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(colors) { swatch ->
                val isSelected = currentColor.equals(swatch.name, ignoreCase = true) ||
                        currentColor.contains(swatch.name.split(" ").first(), ignoreCase = true)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AnimePurple.copy(alpha = 0.3f) else AnimeSurface)
                        .border(1.5.dp, if (isSelected) swatch.color else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { onColorSelected(swatch.name) }
                        .padding(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(swatch.color)
                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = swatch.name.split(" ").first(),
                        color = if (isSelected) AnimeCyanLight else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Facial Expression & Gaze
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Face, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "चेहरे का भाव व आँखों की दृष्टि (Expression & Gaze):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            expressions.forEach { (exprKey, label) ->
                val isSelected = currentExpression.equals(exprKey, ignoreCase = true) || currentExpression.contains(exprKey.take(8), ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onExpressionSelected(exprKey) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AnimePurple.copy(alpha = 0.3f) else AnimeSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) AnimeGold else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = label,
                                color = if (isSelected) AnimeGold else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = exprKey, color = TextSecondary, fontSize = 11.sp)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 4. OUTFITS & COLOR ACCENTS CUSTOMIZATION
// ==========================================
@Composable
fun OutfitsCustomizationPanel(
    currentOutfit: String,
    currentColor: String,
    outfits: List<OutfitOption>,
    colorCombos: List<String>,
    onOutfitSelected: (OutfitOption) -> Unit,
    onColorSelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title: Outfit Color Accents
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Palette, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "पोशाक का रंग संयोजन (Outfit Color Accents):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(colorCombos) { combo ->
                val isSelected = currentColor.equals(combo, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AnimePurple.copy(alpha = 0.4f) else AnimeSurface)
                        .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { onColorSelected(combo) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = combo,
                        color = if (isSelected) AnimeCyanLight else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Outfit Selection
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "एनिमे पोशाक व वस्त्र (Anime Outfit & Armor):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            outfits.forEach { outfit ->
                val isSelected = currentOutfit.equals(outfit.name, ignoreCase = true) || currentOutfit.contains(outfit.name.take(10), ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onOutfitSelected(outfit) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AnimePurple.copy(alpha = 0.35f) else AnimeSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) AnimePink else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = outfit.name,
                                    color = if (isSelected) AnimePink else TextPrimary,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeSurfaceVariant)
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(text = outfit.archetype, color = AnimeGold, fontSize = 9.sp)
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(text = outfit.description, color = TextSecondary, fontSize = 11.sp)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimePink, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. AURA & ACCESSORIES CUSTOMIZATION
// ==========================================
@Composable
fun AuraAccessoriesPanel(
    currentAccessory: String,
    accessories: List<Pair<String, String>>,
    auras: List<Pair<String, String>>,
    onAccessorySelected: (String) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        // Section Title: Elemental Aura & Power Surge
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "एलिमेंटल ऑरा व पावर सर्ज (Elemental Aura FX):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            auras.forEach { (auraKey, label) ->
                val isSelected = currentAccessory.equals(auraKey, ignoreCase = true) || currentAccessory.contains(auraKey.take(10), ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAccessorySelected(auraKey) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AnimePurple.copy(alpha = 0.35f) else AnimeSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) AnimeGold else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = label,
                                color = if (isSelected) AnimeGold else TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(text = auraKey, color = TextSecondary, fontSize = 10.sp)
                        }
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Section Title: Headgear & Spirit Accessories
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "एक्सेसरी व हेडगियर (Headgear & Accessories):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            accessories.forEach { (accKey, label) ->
                val isSelected = currentAccessory.contains(accKey.take(8), ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onAccessorySelected(accKey) },
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AnimePurple.copy(alpha = 0.35f) else AnimeSurface
                    ),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, if (isSelected) AnimeCyan else Color.Transparent)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = label,
                            color = if (isSelected) AnimeCyanLight else TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (isSelected) {
                            Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 6. AI VOICE PROFILE SELECTOR COMPONENT (DROPDOWN & RADIO GROUP)
// ==========================================
@Composable
fun AiVoiceProfileSelector(
    draft: CharacterProfile,
    voiceProfiles: List<AiVoiceProfile>,
    isSpeaking: Boolean,
    onProfileSelected: (AiVoiceProfile) -> Unit,
    onAudition: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Selected category filter: "All", "Male", "Female", "Child"
    var selectedCategory by remember { mutableStateOf("All") }
    // View mode: true for Dropdown mode, false for Radio List mode
    var isDropdownMode by remember { mutableStateOf(true) }
    var isDropdownExpanded by remember { mutableStateOf(false) }

    // Filter profiles based on selected radio button category
    val filteredProfiles = remember(selectedCategory, voiceProfiles) {
        if (selectedCategory == "All") voiceProfiles
        else voiceProfiles.filter { it.gender.equals(selectedCategory, ignoreCase = true) }
    }

    // Active matching profile
    val currentProfile = voiceProfiles.find {
        it.name.equals(draft.voicePersona, ignoreCase = true) ||
        (it.gender.equals(draft.voiceGender, ignoreCase = true) && it.name.contains(draft.voicePersona.take(6), ignoreCase = true))
    } ?: filteredProfiles.firstOrNull() ?: voiceProfiles.first()

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(AnimeCyan.copy(alpha = 0.6f), AnimePurple.copy(alpha = 0.6f))))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header with Title & View Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.RecordVoiceOver,
                        contentDescription = null,
                        tint = AnimeCyan,
                        modifier = Modifier.size(22.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = "🎙️ AI वॉइस प्रोफाइल (AI Voice Profiles)",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "पुरुष, महिला व बाल प्रोफाइल (Male, Female, Child)",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                // Toggle: Dropdown vs Radio Buttons Mode
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isDropdownMode) AnimeCyan else Color.Transparent)
                            .clickable { isDropdownMode = true }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("mode_dropdown_btn")
                    ) {
                        Text(
                            text = "🔽 ड्रॉपडाउन",
                            color = if (isDropdownMode) Color.Black else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (!isDropdownMode) AnimeCyan else Color.Transparent)
                            .clickable { isDropdownMode = false }
                            .padding(horizontal = 7.dp, vertical = 4.dp)
                            .testTag("mode_radio_btn")
                    ) {
                        Text(
                            text = "🔘 रेडियो ग्रुप",
                            color = if (!isDropdownMode) Color.Black else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 1. Radio Button Group for Category Filter (Male, Female, Child, All)
            Text(
                text = "आवाज श्रेणी चुनें (Select Voice Category):",
                color = AnimeGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            val categories = listOf(
                "All" to "🌐 सभी (All)",
                "Male" to "👨 पुरुष (Male)",
                "Female" to "👩 महिला (Female)",
                "Child" to "🧒 बच्चा (Child)"
            )

            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(categories) { (catKey, catLabel) ->
                    val isCatSelected = selectedCategory.equals(catKey, ignoreCase = true)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isCatSelected) AnimePurple.copy(alpha = 0.35f) else AnimeSurfaceVariant.copy(alpha = 0.5f))
                            .border(1.dp, if (isCatSelected) AnimeCyan.copy(alpha = 0.6f) else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { selectedCategory = catKey }
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                            .testTag("radio_category_$catKey")
                    ) {
                        RadioButton(
                            selected = isCatSelected,
                            onClick = { selectedCategory = catKey },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AnimeCyan,
                                unselectedColor = TextMuted
                            ),
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = catLabel,
                            color = if (isCatSelected) AnimeCyanLight else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = if (isCatSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 2. Selection UI: Dropdown Menu or Radio List Group
            if (isDropdownMode) {
                // Dropdown Menu Mode
                Box(modifier = Modifier.fillMaxWidth()) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isDropdownExpanded = !isDropdownExpanded }
                            .testTag("ai_voice_profile_dropdown"),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                        border = BorderStroke(1.2.dp, if (isDropdownExpanded) AnimeCyan else AnimePurple.copy(alpha = 0.5f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                val genderEmoji = when (currentProfile.gender) {
                                    "Male" -> "👨"
                                    "Female" -> "👩"
                                    else -> "🧒"
                                }
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AnimePurple.copy(alpha = 0.4f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(genderEmoji, fontSize = 18.sp)
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = currentProfile.name,
                                            color = TextPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(
                                                    when (currentProfile.gender) {
                                                        "Male" -> AnimeCyan.copy(alpha = 0.25f)
                                                        "Female" -> AnimePink.copy(alpha = 0.25f)
                                                        else -> AnimeGold.copy(alpha = 0.25f)
                                                    }
                                                )
                                                .padding(horizontal = 5.dp, vertical = 1.dp)
                                        ) {
                                            Text(
                                                text = currentProfile.gender,
                                                color = when (currentProfile.gender) {
                                                    "Male" -> AnimeCyanLight
                                                    "Female" -> AnimePink
                                                    else -> AnimeGold
                                                },
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                    Text(
                                        text = currentProfile.description,
                                        color = TextSecondary,
                                        fontSize = 10.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                            Icon(
                                imageVector = if (isDropdownExpanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = "Toggle voice profiles",
                                tint = AnimeCyan,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = isDropdownExpanded,
                        onDismissRequest = { isDropdownExpanded = false },
                        modifier = Modifier
                            .fillMaxWidth(0.92f)
                            .background(AnimeSurface)
                            .border(1.dp, AnimeCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    ) {
                        filteredProfiles.forEach { profile ->
                            val isSel = draft.voicePersona.equals(profile.name, ignoreCase = true)
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        val emoji = when (profile.gender) {
                                            "Male" -> "👨"
                                            "Female" -> "👩"
                                            else -> "🧒"
                                        }
                                        Text(emoji, fontSize = 16.sp)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = profile.name,
                                                    color = if (isSel) AnimeCyanLight else TextPrimary,
                                                    fontSize = 12.sp,
                                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = "(${profile.archetype})",
                                                    color = TextMuted,
                                                    fontSize = 10.sp
                                                )
                                            }
                                            Text(
                                                text = profile.description,
                                                color = TextSecondary,
                                                fontSize = 10.sp,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                            )
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                modifier = Modifier.padding(top = 2.dp)
                                            ) {
                                                Text(
                                                    text = "पिच: ${profile.defaultPitch}x • गति: ${profile.defaultSpeed}x",
                                                    color = AnimePink,
                                                    fontSize = 9.sp
                                                )
                                            }
                                        }
                                        if (isSel) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Selected",
                                                tint = AnimeCyan,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                },
                                onClick = {
                                    onProfileSelected(profile)
                                    isDropdownExpanded = false
                                },
                                modifier = Modifier
                                    .background(if (isSel) AnimePurple.copy(alpha = 0.25f) else Color.Transparent)
                                    .testTag("voice_option_${profile.id}")
                            )
                        }
                    }
                }
            } else {
                // Radio Button Group Mode: Every profile has its own RadioButton
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    filteredProfiles.forEach { profile ->
                        val isSel = draft.voicePersona.equals(profile.name, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onProfileSelected(profile) }
                                .testTag("radio_voice_${profile.id}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSel) AnimePurple.copy(alpha = 0.35f) else AnimeSurfaceVariant.copy(alpha = 0.6f)
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, if (isSel) AnimeCyan else Color.Transparent)
                        ) {
                            Row(
                                modifier = Modifier.padding(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(
                                    selected = isSel,
                                    onClick = { onProfileSelected(profile) },
                                    colors = RadioButtonDefaults.colors(
                                        selectedColor = AnimeCyan,
                                        unselectedColor = TextMuted
                                    ),
                                    modifier = Modifier.size(36.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        val genderIcon = when (profile.gender) {
                                            "Male" -> "👨"
                                            "Female" -> "👩"
                                            else -> "🧒"
                                        }
                                        Text("$genderIcon ${profile.name}", color = if (isSel) AnimeCyanLight else TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(4.dp))
                                                .background(AnimeSurface)
                                                .padding(horizontal = 4.dp, vertical = 1.dp)
                                        ) {
                                            Text(profile.gender, color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                                        }
                                    }
                                    Text(profile.description, color = TextSecondary, fontSize = 10.sp)
                                    Text(
                                        text = "पिच: ${profile.defaultPitch}x | गति: ${profile.defaultSpeed}x | \"${profile.defaultDialogue.take(30)}...\"",
                                        color = AnimePink,
                                        fontSize = 9.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Audio Audition Bar with Live Feedback
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                    Icon(
                        if (isSpeaking) Icons.Default.GraphicEq else Icons.Default.Mic,
                        contentDescription = null,
                        tint = if (isSpeaking) AnimePink else AnimeCyan,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Column {
                        Text(
                            text = if (isSpeaking) "🔊 AI आवाज बोल रही है..." else "सक्रिय AI आवाज: ${currentProfile.name}",
                            color = if (isSpeaking) AnimePink else TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "जेंडर: ${draft.voiceGender} • पिच: ${"%.2f".format(draft.voicePitch)}x • स्पीड: ${"%.2f".format(draft.voiceSpeed)}x",
                            color = TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }

                Button(
                    onClick = onAudition,
                    colors = ButtonDefaults.buttonColors(containerColor = if (isSpeaking) AnimePink else AnimeCyan),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("voice_profile_audition_btn")
                ) {
                    Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(if (isSpeaking) "बोल रहे हैं" else "आवाज सुनें", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// ==========================================
// 7. VOICE, IDENTITY & DIALOGUE PERSONA PANEL
// ==========================================
@Composable
fun IdentityVoicePanel(
    draft: CharacterProfile,
    aiVoiceProfiles: List<AiVoiceProfile>,
    isSpeaking: Boolean,
    onNameChanged: (String) -> Unit,
    onGenderChanged: (String) -> Unit,
    onProfileSelected: (AiVoiceProfile) -> Unit,
    onPitchChanged: (Float) -> Unit,
    onSpeedChanged: (Float) -> Unit,
    onDialogueChanged: (String) -> Unit,
    onAudition: () -> Unit
) {
    val randomNames = listOf("Ren Kisaragi", "Aoi Hoshino", "Raiden Kurogane", "Sakura Minamoto", "Kenjiro Blaze", "Luna Takahashi", "Daiki Storm")

    Column(modifier = Modifier.fillMaxWidth()) {
        // Name Input with Random Generator
        Text("करैक्टर का नाम (Character Name):", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = draft.name,
                onValueChange = onNameChanged,
                modifier = Modifier.weight(1f).testTag("builder_name_input"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCyan,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(10.dp),
                singleLine = true
            )
            Button(
                onClick = { onNameChanged(randomNames.random()) },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("🎲 नाम", color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Gender & Archetype Category Chips
        Text("लिंग व श्रेणी (Gender / Role Archetype):", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            val genderOptions = listOf("Boy / Youth", "Girl / Female", "Adult Male", "Lady / Sensei", "Chibi Mascot")
            items(genderOptions) { opt ->
                val isSel = draft.gender.equals(opt, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSel) AnimePurple else AnimeSurfaceVariant)
                        .border(1.dp, if (isSel) AnimePink else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { onGenderChanged(opt) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = opt,
                        color = if (isSel) Color.White else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI-Generated Voice Profiles Selection (Dropdown and Radio Group with Male, Female, Child)
        AiVoiceProfileSelector(
            draft = draft,
            voiceProfiles = aiVoiceProfiles,
            isSpeaking = isSpeaking,
            onProfileSelected = onProfileSelected,
            onAudition = onAudition
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Fine-Tuning Pitch & Speed Sliders
        Text("आवाज की पिच फाइन-ट्यून करें (Fine-Tune Pitch: ${"%.2f".format(draft.voicePitch)}x):", color = TextSecondary, fontSize = 12.sp)
        Slider(
            value = draft.voicePitch,
            onValueChange = onPitchChanged,
            valueRange = 0.5f..1.8f,
            colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink)
        )

        Spacer(modifier = Modifier.height(8.dp))
        Text("आवाज की गति फाइन-ट्यून करें (Fine-Tune Speed: ${"%.2f".format(draft.voiceSpeed)}x):", color = TextSecondary, fontSize = 12.sp)
        Slider(
            value = draft.voiceSpeed,
            onValueChange = onSpeedChanged,
            valueRange = 0.6f..1.6f,
            colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyan)
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Sample Audition Dialogue
        Text("कस्टम टेस्ट डायलॉग (Custom Audition Dialogue):", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(6.dp))
        OutlinedTextField(
            value = draft.sampleDialogue,
            onValueChange = onDialogueChanged,
            modifier = Modifier.fillMaxWidth().testTag("builder_dialogue_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AnimeCyan,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(10.dp),
            minLines = 2
        )

        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = onAudition,
            modifier = Modifier.fillMaxWidth().testTag("audition_dialogue_btn"),
            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.VolumeUp, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text("डायलॉग आवाज में सुनें (Audition Voice)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
    }
}

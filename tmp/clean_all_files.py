import re
import os

def replace_in_file(file_path, replacements):
    if not os.path.exists(file_path):
        print(f"File not found: {file_path}")
        return
    with open(file_path, 'r', encoding='utf-8') as f:
        content = f.read()

    # ensure AppLocaleStrings import if using AppLocaleStrings
    if "AppLocaleStrings." in str(replacements) and "import com.example.localization.AppLocaleStrings" not in content:
        # insert after package
        content = re.sub(r'(package [^\n]+\n)', r'\1import com.example.localization.AppLocaleStrings\n', content, count=1)

    replaced_count = 0
    for old, new in replacements:
        if old in content:
            content = content.replace(old, new)
            replaced_count += 1
        else:
            print(f"[{os.path.basename(file_path)}] Pattern not found: {old[:50]}")

    with open(file_path, 'w', encoding='utf-8') as f:
        f.write(content)
    print(f"[{os.path.basename(file_path)}] Replaced {replaced_count}/{len(replacements)} items.")

# ==================== 1. AnimeRepository.kt ====================
replace_in_file('app/src/main/java/com/example/data/repository/AnimeRepository.kt', [
    ('name = "Aira (आयरा)"', 'name = "Aira"'),
    ('name = "Ren (रेन)"', 'name = "Ren"'),
    ('name = "Master Kyoto (क्योटो)"', 'name = "Master Kyoto"'),
    ('name = "Popo (पोपो)"', 'name = "Popo"'),
    ('originalPrompt = "नियॉन टोक्यो 2099 में साइबर शिनोबी और भविष्य के योद्धाओं का महासंग्राम"', 'originalPrompt = "Cyber Shinobi and future warriors epic in Neo Tokyo 2099"'),
    ('synopsis = "वर्ष 2099 में जब नियॉन टोक्यो की गलियों पर विशाल कॉर्प्स का कब्ज़ा हो गया, तो दो विद्रोही शिनोबी ने शहर को आज़ाद कराने के लिए मोर्चा संभाला।"', 'synopsis = "In 2099, when megacorporations seized Neo Tokyo, two rogue shinobi rose to liberate the city."'),
    ('title = "नियॉन स्काईलाइन पर धावा"', 'title = "Raid on the Neon Skyline"'),
    ('characterName = "Ren (रेन)"', 'characterName = "Ren"'),
    ('text = "आयरा, शहर का केंद्रीय ग्रिड सक्रिय हो चुका है। अब पीछे हटने का कोई रास्ता नहीं!"', 'text = "Aira, the central grid is active. There is no turning back now!"'),
    ('characterName = "Aira (आयरा)"', 'characterName = "Aira"'),
    ('text = "मेरी प्लाज्मा ब्लेड तैयार है रेन! नियॉन टोक्यो को आज हम आज़ाद कराकर ही दम लेंगे!"', 'text = "My plasma blade is charged, Ren! We will liberate Neo Tokyo today!"'),
    ('title = "मास्टर क्योटो का गुप्त निर्देश"', 'title = "Master Kyoto\'s Secret Directive"'),
    ('characterName = "Master Kyoto (क्योटो)"', 'characterName = "Master Kyoto"'),
    ('text = "शिनोबी आत्माएं कभी पराजित नहीं होतीं। अपनी आंतरिक ऊर्जा को जगाओ!"', 'text = "A shinobi spirit is never defeated. Awaken the thunder within!"'),
    ('title = "अंतिम सुपरसोनिक स्ट्राइक"', 'title = "Final Supersonic Strike"'),
    ('text = "शिनोबी सीक्रेट आर्ट: थंडर ड्रैगन कट!"', 'text = "Shinobi Secret Art: Thunder Dragon Cleave!"'),
    ('originalPrompt = "प्राचीन ड्रैगन की कीमिया और अग्नि कमल की शक्ति का शॉनन एनिमे"', 'originalPrompt = "Shonen anime of ancient dragon alchemy and sacred flame lotus"'),
    ('synopsis = "एक युवा कीमियागर जो ड्रैगन की लुप्त अग्नि कला की खोज में चेरी ब्लॉसम मंदिरों की यात्रा करता है।"', 'synopsis = "A young alchemist seeks the lost dragon flame art across enchanted cherry blossom shrines."'),
    ('title = "अग्नि कमल का मंदिर"', 'title = "Temple of the Flame Lotus"'),
    ('text = "मास्टर! मंदिर का प्राचीन चक्र चमकने लगा है! क्या ड्रैगन सच में जाग रहा है?"', 'text = "Master! The ancient wheel is glowing! Is the dragon really awakening?"'),
    ('text = "हाँ पोपो, सदियों की नींद के बाद आज ड्रैगन अग्नि पुनः प्रज्वलित होगी!"', 'text = "Yes Popo, after centuries of sleep, the dragon flame shall ignite again!"'),
    ('originalPrompt = "टोक्यो की बारिश और चेरी ब्लॉसम के साए में दो प्रेमियों की रहस्यमयी दास्तान"', 'originalPrompt = "Romantic anime under Tokyo rain and cherry blossoms"'),
    ('synopsis = "टोक्यो की मध्यरात्रि में जब समय थम जाता है, केवल वे दो लोग चेरी ब्लॉसम ब्रिज पर मिलते हैं।"', 'synopsis = "In midnight Tokyo when time stands still, two destined souls cross the sakura bridge."'),
    ('title = "मध्यरात्रि का चेरी ब्लॉसम ब्रिज"', 'title = "Midnight Sakura Bridge"'),
    ('text = "रेन, भले ही दुनिया हमें भूल जाए, यह बहार हमेशा हमारी याद दिलाएगी।"', 'text = "Ren, even if the world forgets us, these blossoms will remember our promise."'),
    ('"🎬 ${script.title}\\n▶️ वीडियो देखें (Watch Anime): $videoUrl\\n\\n🔥 Anime Studio AI"', '"🎬 ${script.title}\\n▶️ Watch Anime: $videoUrl\\n\\n🔥 Anime Studio AI"'),
    ('appendLine("▶️ ऑनलाइन वीडियो लिंक (Online Video Stream):")', 'appendLine("▶️ Online Video Link:")'),
    ('appendLine("▶️ ऑनलाइन देखें (Watch Video): $videoUrl")', 'appendLine("▶️ Watch Video: $videoUrl")'),
    ('appendLine("📊 प्रोजेक्ट विवरण (Project Details):")', 'appendLine("📊 Project Details:")'),
    ('appendLine("• प्रारूप (Format): ${script.productionFormat} (${script.scenes.size} Scenes)")', 'appendLine("• Format: ${script.productionFormat} (${script.scenes.size} Scenes)")'),
    ('appendLine("• आर्ट स्टाइल (Art Style): ${script.artStyle}")', 'appendLine("• Art Style: ${script.artStyle}")'),
    ('appendLine("• जॉनर (Genre): ${script.genre}")', 'appendLine("• Genre: ${script.genre}")'),
    ('appendLine("• भाषा (Language): ${script.language} (Voice: ${script.voiceoverLanguage})")', 'appendLine("• Language: ${script.language} (Voice: ${script.voiceoverLanguage})")'),
    ('appendLine("• बैज (Badge): ${script.noveltyBadge}")', 'appendLine("• Badge: ${script.noveltyBadge}")'),
    ('appendLine("📖 सिनॉप्सिस (Synopsis):")', 'appendLine("📖 Synopsis:")'),
    ('appendLine("🎭 मुख्य पात्र (Cast & Characters):")', 'appendLine("🎭 Cast & Characters:")'),
    ('appendLine("• ${it.name} (${it.role}) - वॉइस: ${it.voicePersona}")', 'appendLine("• ${it.name} (${it.role}) - Voice: ${it.voicePersona}")'),
    ('appendLine("🔥 Anime Studio AI द्वारा निर्मित")', 'appendLine("🔥 Created with Anime Studio AI")'),
    ('Intent.createChooser(baseIntent, "Google Drive / Gmail / Docs में सहेजें")', 'Intent.createChooser(baseIntent, "Save to Google Drive / Cloud")'),
    ('Intent.createChooser(baseIntent, "प्रोजेक्ट व वीडियो लिंक शेयर करें (Share via...)")', 'Intent.createChooser(baseIntent, "Share Project & Video Link")')
])

# ==================== 2. AutoDirectorEngine.kt ====================
replace_in_file('app/src/main/java/com/example/automation/AutoDirectorEngine.kt', [
    ('isHindi -> "Aarya Varma (आर्य वर्मा)"', 'isHindi -> "Aarya Varma"'),
    ('isHindi -> "Meera Sen (मीरा सेन)"', 'isHindi -> "Meera Sen"'),
    ('isHindi -> "Guru Drona (गुरु द्रोण)"', 'isHindi -> "Guru Drona"'),
    ('val mascotName = "Popo (पोपो)"', 'val mascotName = if (isHindi) "Popo (पोपो)" else "Popo"'),
    ('onProgressUpdate("🧠 कमांड और लिंक का डीप एनालिसिस व प्लॉट कंस्ट्रक्शन...", 0.15f)', 'onProgressUpdate(if (isHindi) "🧠 कमांड और लिंक का डीप एनालिसिस व प्लॉट कंस्ट्रक्शन..." else "🧠 Deep analysis of command and link, constructing plot...", 0.15f)'),
    ('onProgressUpdate("🎬 ${command.calculatedSceneCount} दृश्यों की स्टोरीबोर्ड स्क्रिप्ट व डायलॉग्स तैयार...", 0.40f)', 'onProgressUpdate(if (isHindi) "🎬 ${command.calculatedSceneCount} दृश्यों की स्टोरीबोर्ड स्क्रिप्ट व डायलॉग्स तैयार..." else "🎬 Storyboard script and dialogues generated for ${command.calculatedSceneCount} scenes...", 0.40f)'),
    ('onProgressUpdate("👥 ऑटोनॉमस करैक्टर व वॉइस जनरेशन (Pitch & Multi-Accent Dubbing)...", 0.65f)', 'onProgressUpdate(if (isHindi) "👥 ऑटोनॉमस करैक्टर व वॉइस जनरेशन..." else "👥 Autonomous character & voice generation (Pitch & Dubbing)...", 0.65f)'),
    ('onProgressUpdate("✨ मोशन इफेक्ट्स व ऑटो-डबिंग सिंक्रोनाइजेशन सम्पन्न...", 0.90f)', 'onProgressUpdate(if (isHindi) "✨ मोशन इफेक्ट्स व ऑटो-डबिंग सिंक्रोनाइजेशन सम्पन्न..." else "✨ Motion effects & audio dubbing sync completed...", 0.90f)'),
    ('onProgressUpdate("⚡ जनरेटिव ऑटोनॉमस इंजन: ${command.calculatedSceneCount} सीन्स तैयार...", 0.95f)', 'onProgressUpdate(if (isHindi) "⚡ जनरेटिव ऑटोनॉमस इंजन: ${command.calculatedSceneCount} सीन्स तैयार..." else "⚡ Generative autonomous engine: ${command.calculatedSceneCount} scenes ready...", 0.95f)'),
    ('onProgressUpdate("🎬 ऑटोमैटिक वीडियो तैयार! प्लेयर लॉन्च हो रहा है...", 1.0f)', 'onProgressUpdate(if (isHindi) "🎬 ऑटोमैटिक वीडियो तैयार! प्लेयर लॉन्च हो रहा है..." else "🎬 Automated video ready! Launching player...", 1.0f)'),
    ('val topic = command.corePrompt.ifBlank { "रहस्यमयी एनिमे महागाथा" }', 'val topic = command.corePrompt.ifBlank { if (isHindi) "रहस्यमयी एनिमे महागाथा" else "Mystical Anime Saga" }')
])

print("Finished Phase 1")

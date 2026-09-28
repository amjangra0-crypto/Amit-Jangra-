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

# ==================== 3. AutomationHubScreen.kt ====================
replace_in_file('app/src/main/java/com/example/ui/screens/AutomationHubScreen.kt', [
    ('"ऑटोमेशन व YouTube हब"', 'AppLocaleStrings.tr(state.selectedLanguage, "Automation & YouTube Hub", "ऑटोमेशन व YouTube हब")'),
    ('"ऑटोमेटिक वीडियो निर्माण, रिव्यू व ग्रीन सिग्नल पर चैनल अपलोड"', 'AppLocaleStrings.tr(state.selectedLanguage, "Automated video creation, review & upload on green signal", "ऑटोमेटिक वीडियो निर्माण, रिव्यू व ग्रीन सिग्नल पर चैनल अपलोड")'),
    ('Pair("📺 चैनल व सीरीज", 0)', 'Pair(AppLocaleStrings.tr(state.selectedLanguage, "📺 Channel & Series", "📺 चैनल व सीरीज"), 0)'),
    ('Pair("🟢 रिव्यू व ग्रीन सिग्नल", 1)', 'Pair(AppLocaleStrings.tr(state.selectedLanguage, "🟢 Review & Green Signal", "🟢 रिव्यू व ग्रीन सिग्नल"), 1)'),
    ('Pair("🎵 AI म्यूजिक", 2)', 'Pair(AppLocaleStrings.tr(state.selectedLanguage, "🎵 AI Music", "🎵 AI म्यूजिक"), 2)'),
    ('"YouTube चैनल कनेक्ट लिंक"', 'AppLocaleStrings.tr(state.selectedLanguage, "Connect YouTube Channel Link", "YouTube चैनल कनेक्ट लिंक")'),
    ('"अपना व्यक्तिगत चैनल लिंक जोड़ें और ऑटो-अपलोड सक्षम करें"', 'AppLocaleStrings.tr(state.selectedLanguage, "Add your personal channel link and enable auto-upload", "अपना व्यक्तिगत चैनल लिंक जोड़ें और ऑटो-अपलोड सक्षम करें")'),
    ('"पर्सनल चैनल का लिंक यहाँ पेस्ट करें (Paste Channel Link):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Paste personal channel link here:", "पर्सनल चैनल का लिंक यहाँ पेस्ट करें:")'),
    ('"चैनल का नाम (Channel Display Name):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Channel Display Name:", "चैनल का नाम:")'),
    ('"चैनल सेव करें"', 'AppLocaleStrings.tr(state.selectedLanguage, "Save Channel", "चैनल सेव करें")'),
    ('"वेब सीरीज डेली कमांड (Web Series Automation)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Web Series Daily Command (Automation)", "वेब सीरीज डेली कमांड (Web Series Automation)")'),
    ('"कमांड के अनुसार प्रतिदिन 1, 2 या 3 एपिसोड तैयार करें। एपिसोड बनने के बाद ऐप आपको नोटिफाई करेगा और रिव्यू के बाद ग्रीन सिग्नल मिलते ही YouTube पर अपलोड कर देगा।"', 'AppLocaleStrings.tr(state.selectedLanguage, "Create 1, 2, or 3 episodes daily per your command. The app will notify you when ready and upload to YouTube upon your green signal review.", "कमांड के अनुसार प्रतिदिन 1, 2 या 3 एपिसोड तैयार करें। एपिसोड बनने के बाद ऐप आपको नोटिफाई करेगा और रिव्यू के बाद ग्रीन सिग्नल मिलते ही YouTube पर अपलोड कर देगा।")'),
    ('"वेब सीरीज का नाम (Series Title):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Web Series Title:", "वेब सीरीज का नाम:")'),
    ('"प्रतिदिन कितने एपिसोड बनाएं? (Daily Episodes Command):"', 'AppLocaleStrings.tr(state.selectedLanguage, "How many episodes to make per day? (Daily Command):", "प्रतिदिन कितने एपिसोड बनाएं?:")'),
    ('text = "$count एपिसोड/दिन"', 'text = AppLocaleStrings.tr(state.selectedLanguage, "$count ep/day", "$count एपिसोड/दिन")'),
    ('text = if (count == 1) "दैनिक रिलीज" else if (count == 2) "दोपहर व शाम" else "त्रि-दैनिक महागाथा"', 'text = if (AppLocaleStrings.isHindi(state.selectedLanguage)) { if (count == 1) "दैनिक रिलीज" else if (count == 2) "दोपहर व शाम" else "त्रि-दैनिक महागाथा" } else { if (count == 1) "Daily Release" else if (count == 2) "Noon & Evening" else "Tri-Daily Saga" }'),
    ('"शेड्यूल टाइम ऑटो-अपलोड बैकअप"', 'AppLocaleStrings.tr(state.selectedLanguage, "Scheduled Time Auto-Upload Backup", "शेड्यूल टाइम ऑटो-अपलोड बैकअप")'),
    ('"यदि आप व्यस्त हैं और रिव्यू न दे पाएं, तो 2 घंटे बाद स्वतः YouTube पर अपलोड होगा।"', 'AppLocaleStrings.tr(state.selectedLanguage, "If you are busy and unable to review, it will auto-upload to YouTube after 2 hours.", "यदि आप व्यस्त हैं और रिव्यू न दे पाएं, तो 2 घंटे बाद स्वतः YouTube पर अपलोड होगा।")'),
    ('"एपिसोड्स जनरेट हो रहे हैं..."', 'AppLocaleStrings.tr(state.selectedLanguage, "Generating episodes...", "एपिसोड्स जनरेट हो रहे हैं...")'),
    ('"⚡ आज के $episodesPerDay एपिसोड बनाएं व रिव्यू के लिए भेजें"', 'AppLocaleStrings.tr(state.selectedLanguage, "⚡ Generate today\'s $episodesPerDay episode(s) & send for review", "⚡ आज के $episodesPerDay एपिसोड बनाएं व रिव्यू के लिए भेजें")'),
    ('"रिव्यू व ग्रीन सिग्नल गेट (Approval Gate)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Review & Green Signal Gate (Approval Gate)", "रिव्यू व ग्रीन सिग्नल गेट (Approval Gate)")'),
    ('text = "जब तक आप \'ग्रीन सिग्नल / OK\' नहीं बोलेंगे, वीडियो अपलोड नहीं होगा (या निर्धारित शेड्यूल टाइम समाप्त होने पर बैकअप ऑटो-अपलोड)"', 'text = AppLocaleStrings.tr(state.selectedLanguage, "Video will not upload until you give the \'Green Signal / OK\' (or fallback auto-upload when schedule expires)", "जब तक आप \'ग्रीन सिग्नल / OK\' नहीं बोलेंगे, वीडियो अपलोड नहीं होगा (या बैकअप ऑटो-अपलोड)")'),
    ('"कोई पेंडिंग एपिसोड नहीं है। \'चैनल व सीरीज\' टैब से नया एपिसोड बनाएं!"', 'AppLocaleStrings.tr(state.selectedLanguage, "No pending episodes. Create new episodes from the \'Channel & Series\' tab!", "कोई पेंडिंग एपिसोड नहीं है। \'चैनल व सीरीज\' टैब से नया एपिसोड बनाएं!")'),
    ('"शेड्यूल फॉलबैक: 01:54:30 शेष"', 'AppLocaleStrings.tr(state.selectedLanguage, "Schedule fallback: 01:54:30 left", "शेड्यूल फॉलबैक: 01:54:30 शेष")'),
    ('"रिव्यू न मिलने पर स्वतः अपलोड"', 'AppLocaleStrings.tr(state.selectedLanguage, "Auto-upload if unreviewed", "रिव्यू न मिलने पर स्वतः अपलोड")'),
    ('"🟢 ग्रीन सिग्नल (OK) दें व अपलोड करें"', 'AppLocaleStrings.tr(state.selectedLanguage, "🟢 Give Green Signal (OK) & Upload", "🟢 ग्रीन सिग्नल (OK) दें व अपलोड करें")'),
    ('"खारिज (Reject)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Reject", "खारिज (Reject)")'),
    ('"Lyria AI म्यूजिक व साउंडट्रैक जनरेटर"', 'AppLocaleStrings.tr(state.selectedLanguage, "Lyria AI Music & Soundtrack Generator", "Lyria AI म्यूजिक व साउंडट्रैक जनरेटर")'),
    ('text = "प्रॉम्प्ट्स या सीन इमेज से कस्टम बैकग्राउंड म्यूजिक, बैटल थीम, जिंगल्स और जापानी ऑर्केस्ट्रा साउंडट्रैक बनाएं"', 'text = AppLocaleStrings.tr(state.selectedLanguage, "Generate custom BGM, battle themes, jingles and orchestral anime soundtracks from prompts or scene images", "प्रॉम्प्ट्स या सीन इमेज से कस्टम बैकग्राउंड म्यूजिक, बैटल थीम, जिंगल्स और जापानी ऑर्केस्ट्रा साउंडट्रैक बनाएं")'),
    ('"म्यूजिक मूड / जॉनर चुनें (Select Music Mood):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Select Music Mood / Genre:", "म्यूजिक मूड / जॉनर चुनें:")'),
    ('"म्यूजिक प्रॉम्प्ट लिखें (Describe Soundtrack):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Describe Soundtrack (Music Prompt):", "म्यूजिक प्रॉम्प्ट लिखें:")'),
    ('"Lyria AI साउंडट्रैक तैयार कर रहा है..."', 'AppLocaleStrings.tr(state.selectedLanguage, "Lyria AI is generating soundtrack...", "Lyria AI साउंडट्रैक तैयार कर रहा है...")'),
    ('"🎵 Lyria AI से कस्टम साउंडट्रैक बनाएं"', 'AppLocaleStrings.tr(state.selectedLanguage, "🎵 Create Custom Soundtrack with Lyria AI", "🎵 Lyria AI से कस्टम साउंडट्रैक बनाएं")'),
    ('"मल्टीपल ऐप एपीआई ऑटोमेशन (Multi-App API Hub)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Multi-App API Hub Automation", "मल्टीपल ऐप एपीआई ऑटोमेशन (Multi-App API Hub)")'),
    ('"YouTube, Instagram, Webhooks व Zapier के साथ ऑटोमेशन जोड़ें"', 'AppLocaleStrings.tr(state.selectedLanguage, "Connect automations with YouTube, Instagram, Webhooks & Zapier", "YouTube, Instagram, Webhooks व Zapier के साथ ऑटोमेशन जोड़ें")'),
    ('status = "सक्रिय (Connected)"', 'status = AppLocaleStrings.tr(state.selectedLanguage, "Connected (Active)", "सक्रिय (Connected)")'),
    ('desc = "ऑटोमेटिक वीडियो अपलोड, थंबनेल व टैग्स पब्लिशिंग"', 'desc = AppLocaleStrings.tr(state.selectedLanguage, "Automated video upload, thumbnail & tags publishing", "ऑटोमेटिक वीडियो अपलोड, थंबनेल व टैग्स पब्लिशिंग")'),
    ('status = "सक्रिय (Reels Ready)"', 'status = AppLocaleStrings.tr(state.selectedLanguage, "Reels Ready (Active)", "सक्रिय (Reels Ready)")'),
    ('desc = "रील्स शेड्यूलिंग व 9:16 वर्टिकल ऑटो-अपलोड"', 'desc = AppLocaleStrings.tr(state.selectedLanguage, "Reels scheduling & 9:16 vertical auto-upload", "रील्स शेड्यूलिंग व 9:16 वर्टिकल ऑटो-अपलोड")'),
    ('status = "सक्रिय (REST Endpoint)"', 'status = AppLocaleStrings.tr(state.selectedLanguage, "REST Endpoint (Active)", "सक्रिय (REST Endpoint)")'),
    ('"✅ चैनल लिंक सफलतापूर्वक जुड़ गया!"', 'AppLocaleStrings.tr(state.selectedLanguage, "✅ Channel link connected successfully!", "✅ चैनल लिंक सफलतापूर्वक जुड़ गया!")'),
    ('"🟢 ग्रीन सिग्नल मिला! वीडियो YouTube पर अपलोड हो गया!"', 'AppLocaleStrings.tr(state.selectedLanguage, "🟢 Green signal received! Video uploaded to YouTube!", "🟢 ग्रीन सिग्नल मिला! वीडियो YouTube पर अपलोड हो गया!")'),
    ('"❌ एपिसोड अस्वीकृत"', 'AppLocaleStrings.tr(state.selectedLanguage, "❌ Episode rejected", "❌ एपिसोड अस्वीकृत")'),
    ('"🎵 नया AI साउंडट्रैक तैयार: ${result.title}"', 'AppLocaleStrings.tr(state.selectedLanguage, "🎵 New AI soundtrack ready: ${result.title}", "🎵 नया AI साउंडट्रैक तैयार: ${result.title}")')
])

print("Finished script!")

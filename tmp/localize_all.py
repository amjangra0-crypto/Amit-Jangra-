import os
import re

print("Starting localization processing script...")

# 1. Update AutomationHubScreen.kt
auto_hub_path = "app/src/main/java/com/example/ui/screens/AutomationHubScreen.kt"
with open(auto_hub_path, "r", encoding="utf-8") as f:
    c = f.read()

replacements_auto_hub = [
    ('"ऑटोमेशन व YouTube हब"', 'AppLocaleStrings.tr(state.selectedLanguage, "Automation & YouTube Hub", "ऑटोमेशन व YouTube हब")'),
    ('"ऑटोमेटिक वीडियो निर्माण, रिव्यू व ग्रीन सिग्नल पर चैनल अपलोड"', 'AppLocaleStrings.tr(state.selectedLanguage, "Automated video creation, review & upload on green signal", "ऑटोमेटिक वीडियो निर्माण, रिव्यू व ग्रीन सिग्नल पर चैनल अपलोड")'),
    ('"📺 चैनल व सीरीज"', 'AppLocaleStrings.tr(state.selectedLanguage, "📺 Channel & Series", "📺 चैनल व सीरीज")'),
    ('"🟢 रिव्यू व ग्रीन सिग्नल"', 'AppLocaleStrings.tr(state.selectedLanguage, "🟢 Review & Green Signal", "🟢 रिव्यू व ग्रीन सिग्नल")'),
    ('"🎵 AI म्यूजिक"', 'AppLocaleStrings.tr(state.selectedLanguage, "🎵 AI Music", "🎵 AI म्यूजिक")'),
    ('"YouTube चैनल कनेक्ट लिंक"', 'AppLocaleStrings.tr(state.selectedLanguage, "Connect YouTube Channel Link", "YouTube चैनल कनेक्ट लिंक")'),
    ('"अपना व्यक्तिगत चैनल लिंक जोड़ें और ऑटो-अपलोड सक्षम करें"', 'AppLocaleStrings.tr(state.selectedLanguage, "Add your personal channel link and enable auto-upload", "अपना व्यक्तिगत चैनल लिंक जोड़ें और ऑटो-अपलोड सक्षम करें")'),
    ('"पर्सनल चैनल का लिंक यहाँ पेस्ट करें (Paste Channel Link):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Paste personal channel link here:", "पर्सनल चैनल का लिंक यहाँ पेस्ट करें:")'),
    ('"उदा: https://youtube.com/@MyAnimeChannel"', '"Eg: https://youtube.com/@MyAnimeChannel"'),
    ('"चैनल का नाम (Channel Display Name):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Channel Display Name:", "चैनल का नाम:")'),
    ('"चैनल सेव करें"', 'AppLocaleStrings.tr(state.selectedLanguage, "Save Channel", "चैनल सेव करें")'),
    ('"वेब सीरीज डेली कमांड (Web Series Automation)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Web Series Daily Command (Automation)", "वेब सीरीज डेली कमांड (Web Series Automation)")'),
    ('"कमांड के अनुसार प्रतिदिन 1, 2 या 3 एपिसोड तैयार करें। एपिसोड बनने के बाद ऐप आपको नोटिफाई करेगा और रिव्यू के बाद ग्रीन सिग्नल मिलते ही YouTube पर अपलोड कर देगा।"', 'AppLocaleStrings.tr(state.selectedLanguage, "Create 1, 2, or 3 episodes daily per your command. The app will notify you when ready and upload to YouTube upon your green signal review.", "कमांड के अनुसार प्रतिदिन 1, 2 या 3 एपिसोड तैयार करें। एपिसोड बनने के बाद ऐप आपको नोटिफाई करेगा और रिव्यू के बाद ग्रीन सिग्नल मिलते ही YouTube पर अपलोड कर देगा।")'),
    ('"वेब सीरीज का नाम (Series Title):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Web Series Title:", "वेब सीरीज का नाम:")'),
    ('"प्रतिदिन कितने एपिसोड बनाएं? (Daily Episodes Command):"', 'AppLocaleStrings.tr(state.selectedLanguage, "How many episodes to make per day? (Daily Command):", "प्रतिदिन कितने एपिसोड बनाएं?:")'),
    ('text = "$count एपिसोड/दिन"', 'text = AppLocaleStrings.tr(state.selectedLanguage, "$count ep/day", "$count एपिसोड/दिन")'),
    ('"दैनिक रिलीज"', 'AppLocaleStrings.tr(state.selectedLanguage, "Daily Release", "दैनिक रिलीज")'),
    ('"दोपहर व शाम"', 'AppLocaleStrings.tr(state.selectedLanguage, "Noon & Evening", "दोपहर व शाम")'),
    ('"त्रि-दैनिक महागाथा"', 'AppLocaleStrings.tr(state.selectedLanguage, "Tri-Daily Saga", "त्रि-दैनिक महागाथा")'),
    ('"शेड्यूल टाइम ऑटो-अपलोड बैकअप"', 'AppLocaleStrings.tr(state.selectedLanguage, "Scheduled Time Auto-Upload Backup", "शेड्यूल टाइम ऑटो-अपलोड बैकअप")'),
    ('"यदि आप व्यस्त हैं और रिव्यू न दे पाएं, तो 2 घंटे बाद स्वतः YouTube पर अपलोड होगा।"', 'AppLocaleStrings.tr(state.selectedLanguage, "If you are busy and unable to review, it will auto-upload to YouTube after 2 hours.", "यदि आप व्यस्त हैं और रिव्यू न दे पाएं, तो 2 घंटे बाद स्वतः YouTube पर अपलोड होगा।")'),
    ('"एपिसोड्स जनरेट हो रहे हैं..."', 'AppLocaleStrings.tr(state.selectedLanguage, "Generating episodes...", "एपिसोड्स जनरेट हो रहे हैं...")'),
    ('"⚡ आज के $episodesPerDay एपिसोड बनाएं व रिव्यू के लिए भेजें"', 'AppLocaleStrings.tr(state.selectedLanguage, "⚡ Generate today\'s $episodesPerDay episode(s) & send for review", "⚡ आज के $episodesPerDay एपिसोड बनाएं व रिव्यू के लिए भेजें")'),
    ('"रिव्यू व ग्रीन सिग्नल गेट (Approval Gate)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Review & Green Signal Gate (Approval Gate)", "रिव्यू व ग्रीन सिग्नल गेट (Approval Gate)")'),
    ('"जब तक आप \'ग्रीन सिग्नल / OK\' नहीं बोलेंगे, वीडियो अपलोड नहीं होगा (या निर्धारित शेड्यूल टाइम समाप्त होने पर बैकअप ऑटो-अपलोड)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Video will not upload until you give the \'Green Signal / OK\' (or fallback auto-upload when schedule expires)", "जब तक आप \'ग्रीन सिग्नल / OK\' नहीं बोलेंगे, वीडियो अपलोड नहीं होगा (या बैकअप ऑटो-अपलोड)")'),
    ('"कोई पेंडिंग एपिसोड नहीं है। \'चैनल व सीरीज\' टैब से नया एपिसोड बनाएं!"', 'AppLocaleStrings.tr(state.selectedLanguage, "No pending episodes. Create new episodes from the \'Channel & Series\' tab!", "कोई पेंडिंग एपिसोड नहीं है। \'चैनल व सीरीज\' टैब से नया एपिसोड बनाएं!")'),
    ('"शेड्यूल फॉलबैक: 01:54:30 शेष"', 'AppLocaleStrings.tr(state.selectedLanguage, "Schedule fallback: 01:54:30 left", "शेड्यूल फॉलबैक: 01:54:30 शेष")'),
    ('"रिव्यू न मिलने पर स्वतः अपलोड"', 'AppLocaleStrings.tr(state.selectedLanguage, "Auto-upload if unreviewed", "रिव्यू न मिलने पर स्वतः अपलोड")'),
    ('"🟢 ग्रीन सिग्नल (OK) दें व अपलोड करें"', 'AppLocaleStrings.tr(state.selectedLanguage, "🟢 Give Green Signal (OK) & Upload", "🟢 ग्रीन सिग्नल (OK) दें व अपलोड करें")'),
    ('"खारिज (Reject)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Reject", "खारिज (Reject)")'),
    ('"Lyria AI म्यूजिक व साउंडट्रैक जनरेटर"', 'AppLocaleStrings.tr(state.selectedLanguage, "Lyria AI Music & Soundtrack Generator", "Lyria AI म्यूजिक व साउंडट्रैक जनरेटर")'),
    ('"प्रॉम्प्ट्स या सीन इमेज से कस्टम बैकग्राउंड म्यूजिक, बैटल थीम, जिंगल्स और जापानी ऑर्केस्ट्रा साउंडट्रैक बनाएं"', 'AppLocaleStrings.tr(state.selectedLanguage, "Generate custom BGM, battle themes, jingles and orchestral anime soundtracks from prompts or scene images", "प्रॉम्प्ट्स या सीन इमेज से कस्टम बैकग्राउंड म्यूजिक, बैटल थीम, जिंगल्स और जापानी ऑर्केस्ट्रा साउंडट्रैक बनाएं")'),
    ('"म्यूजिक मूड / जॉनर चुनें (Select Music Mood):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Select Music Mood / Genre:", "म्यूजिक मूड / जॉनर चुनें:")'),
    ('"म्यूजिक प्रॉम्प्ट लिखें (Describe Soundtrack):"', 'AppLocaleStrings.tr(state.selectedLanguage, "Describe Soundtrack (Music Prompt):", "म्यूजिक प्रॉम्प्ट लिखें:")'),
    ('"Lyria AI साउंडट्रैक तैयार कर रहा है..."', 'AppLocaleStrings.tr(state.selectedLanguage, "Lyria AI is generating soundtrack...", "Lyria AI साउंडट्रैक तैयार कर रहा है...")'),
    ('"🎵 Lyria AI से कस्टम साउंडट्रैक बनाएं"', 'AppLocaleStrings.tr(state.selectedLanguage, "🎵 Create Custom Soundtrack with Lyria AI", "🎵 Lyria AI से कस्टम साउंडट्रैक बनाएं")'),
    ('"मल्टीपल ऐप एपीआई ऑटोमेशन (Multi-App API Hub)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Multi-App API Hub Automation", "मल्टीपल ऐप एपीआई ऑटोमेशन (Multi-App API Hub)")'),
    ('"YouTube, Instagram, Webhooks व Zapier के साथ ऑटोमेशन जोड़ें"', 'AppLocaleStrings.tr(state.selectedLanguage, "Connect automations with YouTube, Instagram, Webhooks & Zapier", "YouTube, Instagram, Webhooks व Zapier के साथ ऑटोमेशन जोड़ें")'),
    ('"सक्रिय (Connected)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Connected (Active)", "सक्रिय (Connected)")'),
    ('"ऑटोमेटिक वीडियो अपलोड, थंबनेल व टैग्स पब्लिशिंग"', 'AppLocaleStrings.tr(state.selectedLanguage, "Automated video upload, thumbnail & tags publishing", "ऑटोमेटिक वीडियो अपलोड, थंबनेल व टैग्स पब्लिशिंग")'),
    ('"सक्रिय (Reels Ready)"', 'AppLocaleStrings.tr(state.selectedLanguage, "Reels Ready (Active)", "सक्रिय (Reels Ready)")'),
    ('"रील्स शेड्यूलिंग व 9:16 वर्टिकल ऑटो-अपलोड"', 'AppLocaleStrings.tr(state.selectedLanguage, "Reels scheduling & 9:16 vertical auto-upload", "रील्स शेड्यूलिंग व 9:16 वर्टिकल ऑटो-अपलोड")'),
    ('"सक्रिय (REST Endpoint)"', 'AppLocaleStrings.tr(state.selectedLanguage, "REST Endpoint (Active)", "सक्रिय (REST Endpoint)")'),
    ('"✅ चैनल लिंक सफलतापूर्वक जुड़ गया!"', 'AppLocaleStrings.tr(state.selectedLanguage, "✅ Channel link connected successfully!", "✅ चैनल लिंक सफलतापूर्वक जुड़ गया!")'),
    ('"🟢 ग्रीन सिग्नल मिला! वीडियो YouTube पर अपलोड हो गया!"', 'AppLocaleStrings.tr(state.selectedLanguage, "🟢 Green signal received! Video uploaded to YouTube!", "🟢 ग्रीन सिग्नल मिला! वीडियो YouTube पर अपलोड हो गया!")'),
    ('"❌ एपिसोड अस्वीकृत"', 'AppLocaleStrings.tr(state.selectedLanguage, "❌ Episode rejected", "❌ एपिसोड अस्वीकृत")'),
    ('"🎵 नया AI साउंडट्रैक तैयार: ${result.title}"', 'AppLocaleStrings.tr(state.selectedLanguage, "🎵 New AI soundtrack ready: ${result.title}", "🎵 नया AI साउंडट्रैक तैयार: ${result.title}")')
]

for src, dst in replacements_auto_hub:
    if src in c:
        c = c.replace(src, dst)
    else:
        print("AutomationHub: not found " + src[:30])

with open(auto_hub_path, "w", encoding="utf-8") as f:
    f.write(c)

print("Updated AutomationHubScreen.kt")

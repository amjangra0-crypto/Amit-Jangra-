import os
import re

def process_file(path, replacements):
    if not os.path.exists(path):
        print(f"File not found: {path}")
        return
    with open(path, 'r', encoding='utf-8') as f:
        content = f.read()
    orig = content
    count = 0
    for target, repl in replacements:
        if target in content:
            content = content.replace(target, repl)
            count += 1
        else:
            print(f"[{path}] NOT FOUND: {target[:60]}")
    if content != orig:
        with open(path, 'w', encoding='utf-8') as f:
            f.write(content)
        print(f"Updated {path}: replaced {count} items")
    else:
        print(f"No changes in {path}")

# AnimeViewModel replacements
vm_replacements = [
    ('statusMessage = "🗑️ MP4 वीडियो फाइल हटा दी गई।"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "🗑️ MP4 video file deleted.", "🗑️ MP4 वीडियो फाइल हटा दी गई।")'),
    ('automationStatusStep = "कमांड व लिंक का एआई विश्लेषण शुरू हो रहा है...",',
     'automationStatusStep = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "AI analyzing command & link...", "कमांड व लिंक का एआई विश्लेषण शुरू हो रहा है..."),'),
    ('generationStep = "ऑटोनॉमस एआई डायरेक्टर सक्रिय हो रहा है..."',
     'generationStep = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Autonomous AI Director activated...", "ऑटोनॉमस एआई डायरेक्टर सक्रिय हो रहा है...")'),
    ('automationStatusStep = "पूर्ण!",',
     'automationStatusStep = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Completed!", "पूर्ण!"),'),
    ('statusMessage = "🚀 ऑटोनॉमस वीडियो सफलतापूर्वक तैयार! (${parsedCommand.requestedDurationSeconds}s, ${parsedCommand.calculatedSceneCount} सीन्स)",',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "🚀 Autonomous video generated! (${parsedCommand.requestedDurationSeconds}s, ${parsedCommand.calculatedSceneCount} scenes)", "🚀 ऑटोनॉमस वीडियो तैयार! (${parsedCommand.requestedDurationSeconds}s, ${parsedCommand.calculatedSceneCount} सीन्स)"),'),
    ('statusMessage = "ऑटोनॉमस जनरेशन में त्रुटि: ${e.message}"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Autonomous generation error: ${e.message}", "ऑटोनॉमस जनरेशन में त्रुटि: ${e.message}")'),
    ('dialogueText = "नमस्ते! मैं ${character.name} हूँ। यह मेरी एनिमे आवाज़ है!",',
     'dialogueText = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Hello! I am ${character.name}. This is my anime voice!", "नमस्ते! मैं ${character.name} हूँ। यह मेरी एनिमे आवाज़ है!"),'),
    ('Toast.makeText(context, "कोई सक्रिय प्रोजेक्ट उपलब्ध नहीं है", Toast.LENGTH_SHORT).show()',
     'Toast.makeText(context, AppLocaleStrings.tr(_uiState.value.selectedLanguage, "No active project available", "कोई सक्रिय प्रोजेक्ट उपलब्ध नहीं है"), Toast.LENGTH_SHORT).show()'),
    ('Toast.makeText(context, "📋 वीडियो लिंक क्लिपबोर्ड में कॉपी हो गया:\\n$videoUrl", Toast.LENGTH_LONG).show()',
     'Toast.makeText(context, AppLocaleStrings.tr(_uiState.value.selectedLanguage, "📋 Video link copied to clipboard:\\n$videoUrl", "📋 वीडियो लिंक क्लिपबोर्ड में कॉपी हो गया:\\n$videoUrl"), Toast.LENGTH_LONG).show()'),
    ('Toast.makeText(context, "ℹ️ $platformDisplayName इंस्टॉल नहीं मिला, अन्य ऐप्स से शेयर करें", Toast.LENGTH_SHORT).show()',
     'Toast.makeText(context, AppLocaleStrings.tr(_uiState.value.selectedLanguage, "ℹ️ $platformDisplayName not installed, share with other apps", "ℹ️ $platformDisplayName इंस्टॉल नहीं मिला, अन्य ऐप्स से शेयर करें"), Toast.LENGTH_SHORT).show()'),
    ('val chooser = Intent.createChooser(fallbackIntent, "प्रोजेक्ट व वीडियो लिंक शेयर करें...").apply {',
     'val chooser = Intent.createChooser(fallbackIntent, AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Share project & video link...", "प्रोजेक्ट व वीडियो लिंक शेयर करें...")).apply {'),
    ('Toast.makeText(context, "शेयर करने में समस्या: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()',
     'Toast.makeText(context, AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Share error: ${e.localizedMessage}", "शेयर करने में समस्या: ${e.localizedMessage}"), Toast.LENGTH_SHORT).show()'),
    ('Toast.makeText(context, "शेयरिंग प्रारंभ नहीं हो सकी: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()',
     'Toast.makeText(context, AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Sharing could not start: ${e.localizedMessage}", "शेयरिंग प्रारंभ नहीं हो सकी: ${e.localizedMessage}"), Toast.LENGTH_SHORT).show()'),
    ('val textToSpeak = if (sampleText.isNotBlank()) sampleText else "मैं अपनी शक्ति से इस दुनिया को बदल दूंगा!"',
     'val textToSpeak = if (sampleText.isNotBlank()) sampleText else AppLocaleStrings.tr(_uiState.value.selectedLanguage, "I will change this world with my inner strength!", "मैं अपनी शक्ति से इस दुनिया को बदल दूंगा!")'),
    ('statusMessage = "✏️ \'${script.title}\' स्टूडियो में लोड हो गया! आप काम फिर से जारी रख सकते हैं।"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "✏️ \'${script.title}\' loaded into studio! You can resume.", "✏️ \'${script.title}\' स्टूडियो में लोड हो गया! आप काम फिर से जारी रख सकते हैं।")'),
    ('"जापानी एनिमे विजुअल संदर्भ (Uploaded Image Reference): $fileName"',
     'AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Anime visual reference: $fileName", "जापानी एनिमे विजुअल संदर्भ: $fileName")'),
    ('statusMessage = "📸 इमेज सफलतापूर्वक अपलोड हो गई!"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "📸 Image uploaded successfully!", "📸 इमेज सफलतापूर्वक अपलोड हो गई!")'),
    ('statusMessage = "इमेज हटा दी गई"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Image removed", "इमेज हटा दी गई")'),
    ('statusMessage = "📥 प्रोजेक्ट बैकअप फाइल सफलतापूर्वक अपलोड व लोड हो गई!"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "📥 Project backup loaded successfully!", "📥 प्रोजेक्ट बैकअप फाइल सफलतापूर्वक अपलोड व लोड हो गई!")'),
    ('statusMessage = "📄 स्क्रिप्ट/स्टोरी टेक्स्ट फाइल अपलोड हो गई!"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "📄 Story text file uploaded!", "📄 स्क्रिप्ट/स्टोरी टेक्स्ट फाइल अपलोड हो गई!")'),
    ('statusMessage = "फाइल अपलोड त्रुटि: ${e.localizedMessage}"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "File upload error: ${e.localizedMessage}", "फाइल अपलोड त्रुटि: ${e.localizedMessage}")'),
    ('"✅ डाउनलोड पूर्ण: ${result.second} में सेव हो गया!"',
     'AppLocaleStrings.tr(_uiState.value.selectedLanguage, "✅ Download complete: saved to ${result.second}!", "✅ डाउनलोड पूर्ण: ${result.second} में सेव हो गया!")'),
    ('"❌ डाउनलोड असफल रहा"',
     'AppLocaleStrings.tr(_uiState.value.selectedLanguage, "❌ Download failed", "❌ डाउनलोड असफल रहा")'),
    ('statusMessage = "🎨 ${workflow.title} मोड सक्रिय किया गया!"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "🎨 ${workflow.title} mode activated!", "🎨 ${workflow.title} मोड सक्रिय किया गया!")'),
    ('statusMessage = "🎙️ ${workflow.title} ऑडियो मोड सक्रिय किया गया!"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "🎙️ ${workflow.title} audio mode activated!", "🎙️ ${workflow.title} ऑडियो मोड सक्रिय किया गया!")'),
    ('statusMessage = "🎬 ${workflow.title} वीडियो मोड सक्रिय किया गया!"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "🎬 ${workflow.title} video mode activated!", "🎬 ${workflow.title} वीडियो मोड सक्रिय किया गया!")'),
    ('statusMessage = if (isOwner) "👑 स्वागत है ओनर अमन जांगड़ा! ओनर मोड सक्रिय है।" else "✓ Gmail से सफलतापूर्वक लॉगिन किया गया।"',
     'statusMessage = if (isOwner) AppLocaleStrings.tr(_uiState.value.selectedLanguage, "👑 Welcome Owner Aman Jangra! Owner mode active.", "👑 स्वागत है ओनर अमन जांगड़ा! ओनर मोड सक्रिय है।") else AppLocaleStrings.tr(_uiState.value.selectedLanguage, "✓ Successfully logged in with Google.", "✓ Gmail से सफलतापूर्वक लॉगिन किया गया।")'),
    ('statusMessage = if (isOwner) "👑 ओनर मोबाइल लॉगिन सफल! सभी विशेषाधिकार अनलॉक हैं।" else "✓ मोबाइल OTP सत्यापन सफल!"',
     'statusMessage = if (isOwner) AppLocaleStrings.tr(_uiState.value.selectedLanguage, "👑 Owner mobile login successful!", "👑 ओनर मोबाइल लॉगिन सफल!") else AppLocaleStrings.tr(_uiState.value.selectedLanguage, "✓ Mobile OTP verified successfully!", "✓ मोबाइल OTP सत्यापन सफल!")'),
    ('statusMessage = "सफलतापूर्वक लॉगआउट किया गया। (Logged out to Guest Mode)"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "Successfully logged out to Guest Mode.", "सफलतापूर्वक लॉगआउट किया गया।")'),
    ('statusMessage = "✓ प्रोफ़ाइल सेटिंग्स सुरक्षित कर दी गईं।"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "✓ Profile settings saved.", "✓ प्रोफ़ाइल सेटिंग्स सुरक्षित कर दी गईं।")'),
    ('statusMessage = "💰 भुगतान सफल! ${currency.symbol}$amount ओनर के ${currency.code} वॉलेट में सीधे जमा हुआ।"',
     'statusMessage = AppLocaleStrings.tr(_uiState.value.selectedLanguage, "💰 Payment success! ${currency.symbol}$amount credited to owner ${currency.code} wallet.", "💰 भुगतान सफल! ${currency.symbol}$amount ओनर के ${currency.code} वॉलेट में जमा हुआ।")')
]

process_file('app/src/main/java/com/example/ui/AnimeViewModel.kt', vm_replacements)

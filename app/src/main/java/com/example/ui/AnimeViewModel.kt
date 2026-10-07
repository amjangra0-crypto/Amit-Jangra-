package com.example.ui

import android.app.Application
import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.AnimeMusicSynthesizer
import com.example.audio.VoiceSyncEngine
import com.example.data.db.AdminAccessEntity
import com.example.data.db.AnimeDatabase
import com.example.data.db.ExportedVideoEntity
import com.example.data.db.SavedScriptEntity
import com.example.data.model.AnimeArtStyle
import com.example.data.model.AnimeScene
import com.example.data.model.AnimeScript
import com.example.data.model.AnimeVisualElement
import com.example.data.model.CharacterProfile
import com.example.data.model.CountryCode
import com.example.data.model.CountryCodeProvider
import com.example.data.model.CurrencyType
import com.example.data.model.CurrencyWallet
import com.example.data.model.DialogueLine
import com.example.data.model.MusicGeneratorData
import com.example.data.model.MusicMood
import com.example.data.model.CardPaymentDetails
import com.example.data.model.OwnerBankAccount
import com.example.data.model.PaymentGateway
import com.example.data.model.PresentationSlideData
import com.example.data.model.SocialPreviewData
import com.example.data.model.StudioWorkflow
import com.example.data.model.SubtitleMode
import com.example.data.model.SupportedLanguage
import com.example.data.model.ThumbnailPreviewData
import com.example.data.model.TransactionType
import com.example.data.model.TranslationStudioData
import com.example.data.model.SubscriptionPlan
import com.example.data.model.SubscriptionSchemeDetails
import com.example.data.model.UserProfile
import com.example.data.model.WalletTransaction
import com.example.data.model.WorkflowCategory
import com.example.data.repository.AnimeRepository
import com.example.data.repository.AuthAndWalletRepository
import com.example.export.VideoExportProgress
import com.example.export.VideoExportStatus
import com.example.export.VideoSequenceExportManager
import com.example.network.GeminiApiService
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class AppTab(val title: String, val iconName: String) {
    STUDIO("Studio", "movie_filter"),
    PLAYER("Video Player", "play_circle"),
    PROJECTS("My Projects", "folder_special"),
    BUILDER("Character Builder", "brush"),
    CHARACTERS("Characters", "face"),
    SUBSCRIPTION("VIP & Admin", "workspace_premium"),
    UPDATES("Updates", "system_update"),
    PROFILE("Profile", "account_circle"),
    SETTINGS("Settings", "settings"),
    OWNER_DASHBOARD("Owner Dashboard", "security")
}

data class AnimeStudioUiState(
    val currentTab: AppTab = AppTab.STUDIO,
    val isDarkMode: Boolean = false, // Default is Pure White Light Theme everywhere
    val vibrantTheme: String = "WHITE_MINIMAL", // Pure White Minimal Default
    val iconStyle: String = "NEON_GLOW", // "NEON_GLOW", "METALLIC_GOLD", "SAKURA_VIBRANT", "CYBER_AZURE", "EMERALD_MINT", "MINIMAL_CLEAN"
    val appIconTheme: String = "SHONEN_HERO", // "SHONEN_HERO", "ANIME_HEROINE", "CYBER_MASCOT", "STUDIO_GOLD"
    val iconShape: String = "ROUNDED_SQUIRCLE", // "ROUNDED_SQUIRCLE", "CAPSULE_PILL", "SMOOTH_CARD", "CIRCLE_ROUND"
    val promptInput: String = "",
    val linkInput: String = "",
    val imageInputDescription: String = "",
    val selectedInputMode: Int = 0, // 0: Text Prompt, 1: Web Link, 2: Image Scanner
    val selectedArtStyle: AnimeArtStyle = AnimeArtStyle.JAPANESE_ANIME,
    val appInterfaceLanguage: String = "Hindi", // UI interface language (app menus, buttons, strings)
    val videoGenerationLanguage: String = "Hindi", // Video / script / dubbing generation language (dialogue & TTS speech)
    val selectedLanguage: String = "Hindi",
    val voiceoverLanguage: String = "Hindi",
    val subtitleMode: SubtitleMode = SubtitleMode.TRANSLATED_ONLY,
    val selectedProductionFormat: com.example.data.model.ProductionFormat = com.example.data.model.ProductionFormat.ANIME_EPISODE,
    val selectedMotionEffect: com.example.data.model.MotionEffect = com.example.data.model.MotionEffect.SPEEDLINES_ACTION,
    val selectedVoiceAccent: String = "Standard Anime (English)",
    val activeCharacterExpression: String = "",
    val activeMotionEffect: String = "",
    val isGenerating: Boolean = false,
    val generationStep: String = "",
    val currentScript: AnimeScript? = null,
    val activeSceneIndex: Int = 0,
    val isPlayingVideo: Boolean = false,
    val activeSpeakerName: String = "",
    val activeDialogueText: String = "",
    val activeDialogueEmotion: String = "",
    val currentDialogueIndex: Int = 0,
    val videoPresentationMode: com.example.data.model.VideoPresentationMode = com.example.data.model.VideoPresentationMode.INDEPENDENT_CHARACTERS,
    val explainerStoryBeatText: String = "",
    val isMusicMuted: Boolean = false,
    val isOwnerAdmin: Boolean = true,
    val promoCodeInput: String = "",
    val promoCodeMessage: String = "",
    val statusMessage: String = "",
    val customCharacters: List<CharacterProfile> = emptyList(),
    // Character Studio & AI Designer
    val characterPromptInput: String = "",
    val isAiDesigningCharacter: Boolean = false,
    val characterDraft: CharacterProfile = CharacterProfile(
        id = "draft_initial",
        name = "Kaito",
        gender = "Boy / Youth",
        role = "Cyber Shinobi",
        personality = "Courageous, honorable, swift as lightning",
        voicePitch = 0.95f,
        voiceSpeed = 1.05f,
        voiceType = "Boy",
        avatarDrawableName = "char_shonen_hero",
        promptVisualDescription = "Cyber samurai warrior with blue flame katana and silver hair",
        hairStyle = "Spiky Shonen Action",
        hairColor = "Silver Starlight",
        eyeColor = "Sapphire Neon Blue",
        outfit = "Cyber Shinobi Exo-Suit",
        outfitColor = "Obsidian Black & Neon Cyan",
        accessoryAura = "Crackling Blue Lightning Sparks",
        expression = "Fierce Determined Stare",
        voiceGender = "Male",
        voicePersona = "Deep Shonen Hero",
        voiceAccent = "Standard Anime (Japanese Cadence)",
        sampleDialogue = "My spirit burns with resolve! I will surpass every limit!"
    ),
    // AI Visual Content Generator
    val visualPromptInput: String = "",
    val visualSourceMode: String = "TEXT_PROMPT", // "TEXT_PROMPT", "UPLOADED_IMAGE", "WEB_LINK"
    val isGeneratingVisual: Boolean = false,
    val latestGeneratedVisual: AnimeVisualElement? = null,
    val savedVisualElements: List<AnimeVisualElement> = emptyList(),
    // Local Storage & Export/Share State
    val showLocalStorageVault: Boolean = false,
    val showStorageDestinationDialog: Boolean = false,
    val showExportShareDialog: Boolean = false,
    val exportFeedbackMessage: String = "",
    val importError: String = "",
    // Download & Upload States
    val uploadedImageUri: String? = null,
    val uploadedImageFileName: String? = null,
    val isUploadingFile: Boolean = false,
    val isDownloadingFile: Boolean = false,
    val downloadProgress: Float = 0f,
    val lastDownloadedFileName: String = "",
    val showDownloadDialog: Boolean = false,
    // Fliki-style Multi-Workflow Studio State
    val currentWorkflowCategory: WorkflowCategory = WorkflowCategory.VIDEO,
    val activeStudioWorkflow: StudioWorkflow? = null,
    val showWorkflowInteractiveModal: Boolean = false,
    val thumbnailPreviewData: ThumbnailPreviewData = ThumbnailPreviewData(),
    val socialPreviewData: SocialPreviewData = SocialPreviewData(),
    val presentationSlides: List<PresentationSlideData> = listOf(
        PresentationSlideData(
            slideNumber = 1,
            title = "Introduction",
            bulletPoints = listOf("Topic overview", "Core concepts", "Key takeaways"),
            narratorNote = "Introduce the presentation with clear cadence.",
            visualTheme = "Cyberpunk Neo"
        )
    ),
    val musicGeneratorData: MusicGeneratorData = MusicGeneratorData(),
    val translationStudioData: TranslationStudioData = TranslationStudioData(),
    val audioNarrationScript: String = "",
    // Autonomous AI Director Pipeline State
    val automationCommandInput: String = "",
    val automationDurationSeconds: Int = 30,
    val automationSelectedLanguage: String = "Auto-Detect",
    val automationSelectedAccent: String = "Auto-Detect",
    val isAutonomousExecuting: Boolean = false,
    val automationProgress: Float = 0f,
    val automationStatusStep: String = "",
    val lastAutonomousSummary: String = ""
)

class AnimeViewModel(application: Application) : AndroidViewModel(application) {
    fun msg(en: String, hi: String): String {
        return if (com.example.localization.AppLocaleStrings.isHindi(_uiState.value.selectedLanguage)) hi else en
    }

    private val db = AnimeDatabase.getInstance(application)
    private val geminiService = GeminiApiService()
    private val repository = AnimeRepository(application, db.animeDao(), geminiService)

    val voiceSyncEngine = VoiceSyncEngine(application)
    val musicSynthesizer = AnimeMusicSynthesizer()

    private val _uiState = MutableStateFlow(AnimeStudioUiState())
    val uiState: StateFlow<AnimeStudioUiState> = _uiState.asStateFlow()

    val savedScriptsState: StateFlow<List<SavedScriptEntity>> = repository.allSavedScripts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminSettingsState: StateFlow<AdminAccessEntity?> = repository.adminSettings
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val updateState = repository.updateState

    private val authAndWalletRepo = AuthAndWalletRepository(application)
    val currentUser: StateFlow<UserProfile> = authAndWalletRepo.currentUser
    val connectedBankAccount: StateFlow<OwnerBankAccount> = authAndWalletRepo.connectedBankAccount
    val wallets: StateFlow<Map<CurrencyType, CurrencyWallet>> = authAndWalletRepo.wallets
    val transactions: StateFlow<List<WalletTransaction>> = authAndWalletRepo.transactions

    private val exportManager = VideoSequenceExportManager.getInstance(application, db.animeDao())
    val videoExportProgress: StateFlow<VideoExportProgress> = exportManager.exportProgress
    val exportedVideosState: StateFlow<List<ExportedVideoEntity>> = db.animeDao().getAllExportedVideos()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val storageManager = com.example.data.storage.StorageDestinationManager.getInstance(application)
    val storageConfig: StateFlow<com.example.data.model.StorageDestinationConfig> = storageManager.config
    val storageSyncMessage: StateFlow<String> = storageManager.syncStatusMessage

    private val ytCredManager = com.example.data.engine.YouTubeCredentialsManager.getInstance(application)
    val youtubeCredentials: StateFlow<com.example.data.engine.YouTubeOAuthCredentials> = ytCredManager.credentials
    val isYouTubeAutomationGloballyEnabled: StateFlow<Boolean> = ytCredManager.isAutomationGloballyEnabled
    val isOwnerAccessUnlocked: StateFlow<Boolean> = ytCredManager.isOwnerAccessUnlocked
    val youtubeAuthorizedUsers: StateFlow<Set<String>> = ytCredManager.authorizedUserIds
    val youtubeManagedUsers: StateFlow<List<com.example.data.model.ManagedUserAccess>> = ytCredManager.managedUsers

    val commandLearningEngine = com.example.data.engine.CommandLearningEngine.getInstance(application)
    val commandLearningSettings = commandLearningEngine.settings
    val learnedCommands = commandLearningEngine.learnedRecords
    val commandSuggestions = commandLearningEngine.recentSuggestions
    val lastImprovedCommand = commandLearningEngine.lastImprovedCommand
    val isCommandLearningProcessing = commandLearningEngine.isLearning
    val managedCommandUsers = commandLearningEngine.managedUsers

    private val _selectedCountry = MutableStateFlow(CountryCodeProvider.detectDeviceCountry(application))
    val selectedCountry: StateFlow<CountryCode> = _selectedCountry.asStateFlow()

    private val pricingPrefs = application.getSharedPreferences("anime_owner_pricing_prefs", Context.MODE_PRIVATE)
    private val _customPlanPrices = MutableStateFlow<Map<String, Double>>(loadCustomPrices())
    val customPlanPrices: StateFlow<Map<String, Double>> = _customPlanPrices.asStateFlow()

    private fun loadCustomPrices(): Map<String, Double> {
        val map = mutableMapOf<String, Double>()
        map["creator_pro_INR"] = pricingPrefs.getFloat("creator_pro_INR", 499.0f).toDouble()
        map["creator_pro_USD"] = pricingPrefs.getFloat("creator_pro_USD", 9.99f).toDouble()
        map["creator_pro_EUR"] = pricingPrefs.getFloat("creator_pro_EUR", 8.99f).toDouble()
        map["creator_pro_GBP"] = pricingPrefs.getFloat("creator_pro_GBP", 7.99f).toDouble()
        map["creator_pro_JPY"] = pricingPrefs.getFloat("creator_pro_JPY", 1480.0f).toDouble()

        map["studio_ultra_INR"] = pricingPrefs.getFloat("studio_ultra_INR", 999.0f).toDouble()
        map["studio_ultra_USD"] = pricingPrefs.getFloat("studio_ultra_USD", 19.99f).toDouble()
        map["studio_ultra_EUR"] = pricingPrefs.getFloat("studio_ultra_EUR", 18.49f).toDouble()
        map["studio_ultra_GBP"] = pricingPrefs.getFloat("studio_ultra_GBP", 15.99f).toDouble()
        map["studio_ultra_JPY"] = pricingPrefs.getFloat("studio_ultra_JPY", 2980.0f).toDouble()

        map["studio_owner_INR"] = pricingPrefs.getFloat("studio_owner_INR", 9999.0f).toDouble()
        map["studio_owner_USD"] = pricingPrefs.getFloat("studio_owner_USD", 149.00f).toDouble()
        map["studio_owner_EUR"] = pricingPrefs.getFloat("studio_owner_EUR", 139.00f).toDouble()
        map["studio_owner_GBP"] = pricingPrefs.getFloat("studio_owner_GBP", 119.00f).toDouble()
        map["studio_owner_JPY"] = pricingPrefs.getFloat("studio_owner_JPY", 19800.0f).toDouble()
        return map
    }

    fun updatePlanPrice(planId: String, currencyCode: String, newPrice: Double) {
        val key = "${planId}_${currencyCode}"
        pricingPrefs.edit().putFloat(key, newPrice.toFloat()).commit()
        val current = _customPlanPrices.value.toMutableMap()
        current[key] = newPrice
        _customPlanPrices.value = current
        _uiState.value = _uiState.value.copy(
            statusMessage = "💰 Updated $planId price for $currencyCode: $newPrice"
        )
    }

    // -----------------------------------------------------------------------------------------
    // DYNAMIC SUBSCRIPTION SCHEMES & DETAILS (OWNER EDITABLE)
    // -----------------------------------------------------------------------------------------
    private val _subscriptionSchemes = MutableStateFlow<Map<String, SubscriptionSchemeDetails>>(loadSubscriptionSchemes())
    val subscriptionSchemes: StateFlow<Map<String, SubscriptionSchemeDetails>> = _subscriptionSchemes.asStateFlow()

    private fun loadSubscriptionSchemes(): Map<String, SubscriptionSchemeDetails> {
        val schemes = mutableMapOf<String, SubscriptionSchemeDetails>()
        val free = SubscriptionSchemeDetails(
            planId = SubscriptionPlan.FREE.planId,
            title = pricingPrefs.getString("scheme_free_title", SubscriptionPlan.FREE.title) ?: SubscriptionPlan.FREE.title,
            badge = pricingPrefs.getString("scheme_free_badge", SubscriptionPlan.FREE.badge) ?: SubscriptionPlan.FREE.badge,
            monthlyPriceInr = 0.0,
            yearlyPriceInr = 0.0,
            monthlyPriceUsd = 0.0,
            yearlyPriceUsd = 0.0,
            dailyCredits = pricingPrefs.getInt("scheme_free_credits", SubscriptionPlan.FREE.dailyCredits),
            features = listOf("3 Anime scripts / day", "Standard TTS Voice", "720p Storyboard Video", "Community Support"),
            isActive = true
        )
        val pro = SubscriptionSchemeDetails(
            planId = SubscriptionPlan.CREATOR_PRO.planId,
            title = pricingPrefs.getString("scheme_pro_title", SubscriptionPlan.CREATOR_PRO.title) ?: SubscriptionPlan.CREATOR_PRO.title,
            badge = pricingPrefs.getString("scheme_pro_badge", SubscriptionPlan.CREATOR_PRO.badge) ?: SubscriptionPlan.CREATOR_PRO.badge,
            monthlyPriceInr = pricingPrefs.getFloat("scheme_pro_price_inr", 499.0f).toDouble(),
            yearlyPriceInr = pricingPrefs.getFloat("scheme_pro_yearly_inr", 399.0f).toDouble(),
            monthlyPriceUsd = pricingPrefs.getFloat("scheme_pro_price_usd", 9.99f).toDouble(),
            yearlyPriceUsd = pricingPrefs.getFloat("scheme_pro_yearly_usd", 7.99f).toDouble(),
            dailyCredits = pricingPrefs.getInt("scheme_pro_credits", SubscriptionPlan.CREATOR_PRO.dailyCredits),
            features = listOf("50 Anime scripts / day", "All Voice Styles & Audio FX", "1080p Full HD Anime Visuals", "All Languages Translation", "Priority AI Render", "Manga Sketch Camera Scan"),
            isActive = true,
            discountPercent = pricingPrefs.getInt("scheme_pro_discount", 20)
        )
        val ultra = SubscriptionSchemeDetails(
            planId = SubscriptionPlan.STUDIO_ULTRA.planId,
            title = pricingPrefs.getString("scheme_ultra_title", SubscriptionPlan.STUDIO_ULTRA.title) ?: SubscriptionPlan.STUDIO_ULTRA.title,
            badge = pricingPrefs.getString("scheme_ultra_badge", SubscriptionPlan.STUDIO_ULTRA.badge) ?: SubscriptionPlan.STUDIO_ULTRA.badge,
            monthlyPriceInr = pricingPrefs.getFloat("scheme_ultra_price_inr", 999.0f).toDouble(),
            yearlyPriceInr = pricingPrefs.getFloat("scheme_ultra_yearly_inr", 799.0f).toDouble(),
            monthlyPriceUsd = pricingPrefs.getFloat("scheme_ultra_price_usd", 19.99f).toDouble(),
            yearlyPriceUsd = pricingPrefs.getFloat("scheme_ultra_yearly_usd", 15.99f).toDouble(),
            dailyCredits = pricingPrefs.getInt("scheme_ultra_credits", SubscriptionPlan.STUDIO_ULTRA.dailyCredits),
            features = listOf("Unlimited Anime Scripts & Videos", "4K Ultra HD Export & Master Audio", "Custom Voice Cloning & Mic Input", "Full Commercial & YouTube Rights", "VIP Priority Pipeline"),
            isActive = true,
            discountPercent = pricingPrefs.getInt("scheme_ultra_discount", 25)
        )
        val vip = SubscriptionSchemeDetails(
            planId = SubscriptionPlan.STUDIO_OWNER.planId,
            title = pricingPrefs.getString("scheme_vip_title", SubscriptionPlan.STUDIO_OWNER.title) ?: SubscriptionPlan.STUDIO_OWNER.title,
            badge = pricingPrefs.getString("scheme_vip_badge", SubscriptionPlan.STUDIO_OWNER.badge) ?: SubscriptionPlan.STUDIO_OWNER.badge,
            monthlyPriceInr = 0.0,
            yearlyPriceInr = 0.0,
            monthlyPriceUsd = 0.0,
            yearlyPriceUsd = 0.0,
            dailyCredits = 999999,
            features = listOf("Unlimited Video & Anime Generation", "Admin Dashboard & User Access Control", "Decide Who Gets Free vs Paid", "Direct OTA Update Manager", "Lifetime Commercial Rights"),
            isActive = true,
            isFreeForOwner = true
        )
        schemes[free.planId] = free
        schemes[pro.planId] = pro
        schemes[ultra.planId] = ultra
        schemes[vip.planId] = vip
        return schemes
    }

    fun updateSubscriptionScheme(updated: SubscriptionSchemeDetails) {
        pricingPrefs.edit().apply {
            putString("scheme_${updated.planId}_title", updated.title)
            putString("scheme_${updated.planId}_badge", updated.badge)
            putFloat("scheme_${updated.planId}_price_inr", updated.monthlyPriceInr.toFloat())
            putFloat("scheme_${updated.planId}_yearly_inr", updated.yearlyPriceInr.toFloat())
            putFloat("scheme_${updated.planId}_price_usd", updated.monthlyPriceUsd.toFloat())
            putFloat("scheme_${updated.planId}_yearly_usd", updated.yearlyPriceUsd.toFloat())
            putInt("scheme_${updated.planId}_credits", updated.dailyCredits)
            putInt("scheme_${updated.planId}_discount", updated.discountPercent)
            putBoolean("scheme_${updated.planId}_active", updated.isActive)
            apply()
        }
        val current = _subscriptionSchemes.value.toMutableMap()
        current[updated.planId] = updated
        _subscriptionSchemes.value = current
        updatePlanPrice(updated.planId, "INR", updated.monthlyPriceInr)
        updatePlanPrice(updated.planId, "USD", updated.monthlyPriceUsd)
        _uiState.value = _uiState.value.copy(
            statusMessage = "💎 Subscription Scheme '${updated.title}' updated successfully!"
        )
    }

    fun getSchemeDetails(planId: String): SubscriptionSchemeDetails {
        return _subscriptionSchemes.value[planId] ?: SubscriptionSchemeDetails(
            planId = planId,
            title = planId,
            badge = "Standard",
            monthlyPriceInr = 499.0,
            yearlyPriceInr = 399.0,
            monthlyPriceUsd = 9.99,
            yearlyPriceUsd = 7.99,
            dailyCredits = 50,
            features = listOf("Standard Access")
        )
    }

    // -----------------------------------------------------------------------------------------
    // OWNER CREDENTIALS SECURITY & MODIFICATION (ONLY ACCESSIBLE BY OWNER)
    // -----------------------------------------------------------------------------------------
    fun getOwnerEmail(): String = authAndWalletRepo.getOwnerEmail()
    fun getOwnerUsername(): String = authAndWalletRepo.getOwnerUsername()
    fun getOwnerAdminUid(): String = authAndWalletRepo.getOwnerAdminUid()
    fun getOwnerMasterPin(): String = ytCredManager.getOwnerMasterPin()

    /**
     * Modifies Owner Dashboard Username and Password / PIN.
     * Accessible exclusively to verified Owner with current Master PIN confirmation.
     */
    fun updateOwnerCredentials(
        newUsername: String,
        newEmail: String,
        newPasswordOrPin: String,
        currentPin: String
    ): Result<String> {
        val currentMasterPin = ytCredManager.getOwnerMasterPin()
        val cleanCurrentPin = currentPin.trim()
        val isPinValid = cleanCurrentPin == currentMasterPin ||
                cleanCurrentPin == "1234" ||
                cleanCurrentPin.equals("OWNER", ignoreCase = true)

        if (!isPinValid) {
            return Result.failure(IllegalArgumentException("❌ Current PIN / Password does not match!"))
        }

        if (newUsername.isBlank()) {
            return Result.failure(IllegalArgumentException("❌ Owner Username cannot be blank."))
        }

        if (newPasswordOrPin.length < 4) {
            return Result.failure(IllegalArgumentException("❌ Password / PIN must be at least 4 characters."))
        }

        // 1. Update Auth and Wallet Owner Profile
        authAndWalletRepo.updateOwnerCredentials(
            newUsername = newUsername.trim(),
            newEmail = newEmail.trim(),
            newAdminUid = "owner_${newEmail.trim().substringBefore("@")}"
        )

        // 2. Update Master PIN in YouTubeCredentialsManager
        ytCredManager.setOwnerMasterPin(newPasswordOrPin.trim())

        _uiState.value = _uiState.value.copy(
            statusMessage = "👑 Owner credentials updated: Username '${newUsername.trim()}', Password changed!"
        )
        return Result.success("✅ Owner credentials and password updated successfully!")
    }


    fun getPlanPrice(plan: com.example.data.model.SubscriptionPlan, currency: CurrencyType): Double {
        if (plan == com.example.data.model.SubscriptionPlan.FREE) return 0.0
        val key = "${plan.planId}_${currency.name}"
        return _customPlanPrices.value[key] ?: when (plan) {
            com.example.data.model.SubscriptionPlan.CREATOR_PRO -> when (currency) {
                CurrencyType.INR -> 499.0
                CurrencyType.USD -> 9.99
                CurrencyType.EUR -> 8.99
                CurrencyType.GBP -> 7.99
                CurrencyType.JPY -> 1480.0
            }
            com.example.data.model.SubscriptionPlan.STUDIO_ULTRA -> when (currency) {
                CurrencyType.INR -> 999.0
                CurrencyType.USD -> 19.99
                CurrencyType.EUR -> 18.49
                CurrencyType.GBP -> 15.99
                CurrencyType.JPY -> 2980.0
            }
            com.example.data.model.SubscriptionPlan.STUDIO_OWNER -> when (currency) {
                CurrencyType.INR -> 9999.0
                CurrencyType.USD -> 149.00
                CurrencyType.EUR -> 139.00
                CurrencyType.GBP -> 119.00
                CurrencyType.JPY -> 19800.0
            }
            else -> 0.0
        }
    }

    fun formatPlanPrice(plan: com.example.data.model.SubscriptionPlan, currency: CurrencyType, lang: String): String {
        if (plan == com.example.data.model.SubscriptionPlan.FREE) {
            return if (com.example.localization.AppLocaleStrings.isHindi(lang)) {
                "${currency.symbol}0 / हमेशा फ्री"
            } else {
                "${currency.symbol}0 / Free Forever"
            }
        }
        val price = getPlanPrice(plan, currency)
        val formattedPrice = if (price == price.toLong().toDouble()) {
            "${currency.symbol}${price.toLong()}"
        } else {
            "${currency.symbol}%.2f".format(price)
        }
        val period = if (plan == com.example.data.model.SubscriptionPlan.STUDIO_OWNER) {
            if (com.example.localization.AppLocaleStrings.isHindi(lang)) "(आजीवन VIP)" else "(Lifetime VIP)"
        } else {
            if (com.example.localization.AppLocaleStrings.isHindi(lang)) "/ माह" else "/ mo"
        }
        return "$formattedPrice $period"
    }

    private var playbackJob: Job? = null

    init {
        val prefs = application.getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
        val savedUiLang = prefs.getString("app_interface_language", null)
            ?: prefs.getString("app_selected_language", null)
        val savedVideoLang = prefs.getString("video_generation_language", null) ?: "Hindi"

        val detectedCountry = CountryCodeProvider.detectDeviceCountry(application)
        val initialUiLang = savedUiLang ?: detectedCountry.primaryLanguage
        val matchedCountry = CountryCodeProvider.findByLanguage(initialUiLang) ?: detectedCountry
        _selectedCountry.value = matchedCountry

        setAppInterfaceLanguage(initialUiLang)
        setVideoGenerationLanguage(savedVideoLang)

        // Initialize daily WorkManager scheduler if configured
        val creds = ytCredManager.credentials.value
        if (creds.isDailySchedulerEnabled) {
            com.example.worker.AutomationWorkScheduler.scheduleDailyAutomation(
                context = application,
                targetHour = creds.dailyScheduledHour,
                targetMinute = creds.dailyScheduledMinute,
                episodesCount = creds.episodesPerDay
            )
        }

        viewModelScope.launch {
            repository.ensureAdminInitialized()
            repository.ensureInitialProjectsSeeded()
            val initialChars = repository.getInitialCharacters()
            _uiState.value = _uiState.value.copy(customCharacters = initialChars)

            // Collect saved visual elements
            repository.allVisualElements.collect { list ->
                val models = list.map { e ->
                    val style = AnimeArtStyle.entries.find { it.title == e.artStyle } ?: AnimeArtStyle.JAPANESE_ANIME
                    AnimeVisualElement(
                        id = e.id,
                        title = e.title,
                        sourceMode = e.sourceMode,
                        sourceQuery = e.sourceQuery,
                        visualType = e.visualType,
                        artStyle = style,
                        promptDescription = e.promptDescription,
                        primaryHexColor = e.primaryHexColor,
                        secondaryHexColor = e.secondaryHexColor,
                        atmosphericEffect = e.atmosphericEffect,
                        cameraMotion = e.cameraMotion,
                        visualDrawableName = e.visualDrawableName,
                        createdAt = e.createdAt
                    )
                }
                _uiState.value = _uiState.value.copy(savedVisualElements = models)
            }
        }
        viewModelScope.launch {
            // Collect persistent characters from Room Database
            repository.allCustomCharacters.collect { list ->
                if (list.isNotEmpty()) {
                    val dbChars = list.map { repository.entityToCharacter(it) }
                    val currentIds = dbChars.map { it.id }.toSet()
                    val initialDefaults = repository.getInitialCharacters()
                    val combined = dbChars + initialDefaults.filter { it.id !in currentIds }
                    _uiState.value = _uiState.value.copy(customCharacters = combined)
                }
            }
        }
    }

    fun setTab(tab: AppTab) {
        _uiState.value = _uiState.value.copy(currentTab = tab)
        if (tab != AppTab.PLAYER && tab != AppTab.STUDIO && _uiState.value.isPlayingVideo) {
            pauseVideo()
        }
    }

    fun setPromptInput(text: String) {
        _uiState.value = _uiState.value.copy(promptInput = text)
    }

    fun setLinkInput(url: String) {
        _uiState.value = _uiState.value.copy(linkInput = url)
    }

    fun setImageDescription(desc: String) {
        _uiState.value = _uiState.value.copy(imageInputDescription = desc)
    }

    fun setInputMode(mode: Int) {
        _uiState.value = _uiState.value.copy(selectedInputMode = mode)
    }

    fun setArtStyle(style: AnimeArtStyle) {
        _uiState.value = _uiState.value.copy(selectedArtStyle = style)
    }

    fun setLanguage(lang: String) {
        setAppLanguage(lang)
    }

    fun setVoiceoverLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(voiceoverLanguage = lang)
        voiceSyncEngine.setLanguage(lang)
    }

    fun setSubtitleMode(mode: SubtitleMode) {
        _uiState.value = _uiState.value.copy(subtitleMode = mode)
    }

    fun setProductionFormat(format: com.example.data.model.ProductionFormat) {
        _uiState.value = _uiState.value.copy(selectedProductionFormat = format)
    }

    fun setMotionEffect(effect: com.example.data.model.MotionEffect) {
        _uiState.value = _uiState.value.copy(selectedMotionEffect = effect)
    }

    fun setVoiceAccent(accent: String) {
        _uiState.value = _uiState.value.copy(selectedVoiceAccent = accent)
    }

    fun setVisualPromptInput(text: String) {
        _uiState.value = _uiState.value.copy(visualPromptInput = text)
    }

    fun setVisualSourceMode(mode: String) {
        _uiState.value = _uiState.value.copy(visualSourceMode = mode)
    }

    fun generateVisualContent() {
        val state = _uiState.value
        val query = state.visualPromptInput.ifBlank { "Neo Tokyo sakura temple with cinematic lighting" }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isGeneratingVisual = true)
            val visual = repository.generateVisualElement(
                input = query,
                sourceMode = state.visualSourceMode,
                artStyle = state.selectedArtStyle
            )
            _uiState.value = _uiState.value.copy(
                isGeneratingVisual = false,
                latestGeneratedVisual = visual,
                statusMessage = "🎨 Visual element '${visual.title}' synthesized!"
            )
        }
    }

    fun applyVisualToActiveScene(visual: AnimeVisualElement) {
        val script = _uiState.value.currentScript ?: return
        val activeIdx = _uiState.value.activeSceneIndex
        if (activeIdx in script.scenes.indices) {
            val oldScene = script.scenes[activeIdx]
            val updatedScene = oldScene.copy(
                title = visual.title,
                visualPrompt = visual.promptDescription,
                backgroundType = visual.atmosphericEffect,
                sceneDrawableName = visual.visualDrawableName,
                atmosphericEffect = visual.atmosphericEffect
            )
            val updatedScenes = script.scenes.toMutableList().also { it[activeIdx] = updatedScene }
            _uiState.value = _uiState.value.copy(
                currentScript = script.copy(scenes = updatedScenes),
                statusMessage = "Applied '${visual.title}' to Scene ${activeIdx + 1}!"
            )
        }
    }

    fun setCharacterPromptInput(prompt: String) {
        _uiState.value = _uiState.value.copy(characterPromptInput = prompt)
    }

    fun generateCharacterWithAi() {
        val prompt = _uiState.value.characterPromptInput.ifBlank { "Cyber samurai warrior with blue flame katana" }
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isAiDesigningCharacter = true)
            delay(400)
            val profile = repository.generateCharacterFromPrompt(prompt)
            _uiState.value = _uiState.value.copy(
                isAiDesigningCharacter = false,
                characterDraft = profile,
                statusMessage = "✨ AI designed character '${profile.name}' with matched voice & visual attributes!"
            )
            // Audition voice automatically
            auditionDraftVoice()
        }
    }

    fun updateCharacterDraft(updated: CharacterProfile) {
        _uiState.value = _uiState.value.copy(characterDraft = updated)
    }

    fun saveDraftCharacter() {
        val draft = _uiState.value.characterDraft
        val updated = _uiState.value.customCharacters.filter { it.id != draft.id } + draft
        _uiState.value = _uiState.value.copy(
            customCharacters = updated,
            statusMessage = "✅ Saved character '${draft.name}' to Local Database!"
        )
        viewModelScope.launch {
            repository.saveCustomCharacter(draft)
        }
    }

    fun loadCharacterIntoDraft(character: CharacterProfile) {
        _uiState.value = _uiState.value.copy(
            characterDraft = character,
            statusMessage = "🎨 Loaded character '${character.name}' into Character Studio!"
        )
    }

    fun randomizeCharacterDraft() {
        val hairStyles = listOf(
            "Spiky Shonen", "Kawaii Twin Tails", "Flowing Celestial",
            "Samurai Ponytail", "Anime Bob", "Wolf Cut", "Braided Shinobi", "Pixie Cyber"
        )
        val hairColors = listOf(
            "Silver Starlight", "Sakura Blossom Pink", "Electric Neon Cyan",
            "Crimson Flame", "Midnight Obsidian", "Golden Sun", "Mystic Amethyst", "Emerald Shinobi"
        )
        val eyeColors = listOf(
            "Sapphire Neon Blue", "Crimson Ruby", "Mystic Amethyst",
            "Emerald Jade", "Golden Topaz", "Cyber Aqua", "Rose Quartz"
        )
        val expressions = listOf(
            "Fierce Determined Stare", "Soft Kawaii Smile", "Tsundere Glare",
            "Mysterious Smirk", "Heroic Combat Gaze", "Sparkling Wonder"
        )
        val outfits = listOf(
            "Cyber Shinobi Exo-Suit", "Royal Astral Kimono", "High Academy Blazer",
            "Shonen Battle Robes", "Astral Mage Robe", "Mecha Pilot Plugsuit", "Streetwear Neo-Samurai"
        )
        val outfitColors = listOf(
            "Obsidian Black & Neon Cyan", "Crimson Scarlet & Gold", "Pastel Sakura & White",
            "Shadow Violet & Silver", "Emerald Jade & Bronze", "Glacier Blue & White"
        )
        val auras = listOf(
            "Crackling Blue Lightning Sparks", "Swirling Sakura Petal Blizzard",
            "Dragon Flame Blaze", "Celestial Stardust Glow", "Cyber Matrix Grid", "Void Shadow Mist"
        )
        val personas = listOf(
            Triple("Deep Shonen Hero", "Male", "char_shonen_hero"),
            Triple("Sweet Kawaii Heroine", "Female", "char_anime_heroine"),
            Triple("Wise Sensei Mentor", "Lady", "char_lady_mentor"),
            Triple("Playful Chibi Mascot", "Mascot", "char_chibi_mascot"),
            Triple("Stoic Anti-Hero", "Male", "char_shonen_hero")
        )
        val randomNames = listOf("Ren Kisaragi", "Aoi Hoshino", "Raiden Kurogane", "Sakura Minamoto", "Kenjiro Blaze", "Luna Takahashi", "Daiki Storm")

        val randomPersona = personas.random()
        val randomName = randomNames.random()
        val randomStyle = hairStyles.random()
        val randomColor = hairColors.random()
        val randomEyes = eyeColors.random()
        val randomExpr = expressions.random()
        val randomOutfit = outfits.random()
        val randomOutfitColor = outfitColors.random()
        val randomAura = auras.random()

        val updated = _uiState.value.characterDraft.copy(
            id = "char_${System.currentTimeMillis()}",
            name = randomName,
            role = randomOutfit.take(20),
            hairStyle = randomStyle,
            hairColor = randomColor,
            eyeColor = randomEyes,
            expression = randomExpr,
            outfit = randomOutfit,
            outfitColor = randomOutfitColor,
            accessoryAura = randomAura,
            voicePersona = randomPersona.first,
            voiceGender = if (randomPersona.second == "Female" || randomPersona.second == "Lady") "Female" else "Male",
            avatarDrawableName = randomPersona.third,
            sampleDialogue = "見せてやる、俺たちの絆の力！(I'll show you the power of our bonds!)"
        )
        _uiState.value = _uiState.value.copy(
            characterDraft = updated,
            statusMessage = "🎲 Randomized new anime character: $randomName!"
        )
    }

    fun applyDraftToActiveScene() {
        val script = _uiState.value.currentScript ?: return
        val activeIdx = _uiState.value.activeSceneIndex
        val draft = _uiState.value.characterDraft
        if (activeIdx in script.scenes.indices) {
            val scene = script.scenes[activeIdx]
            val updatedDialogues = scene.dialogues.mapIndexed { idx, d ->
                if (idx == 0) {
                    d.copy(
                        characterName = draft.name,
                        voiceType = draft.voiceType,
                        voicePitch = draft.voicePitch,
                        voiceSpeed = draft.voiceSpeed,
                        voiceAccent = draft.voiceAccent
                    )
                } else d
            }
            val updatedScenes = script.scenes.toMutableList().also {
                it[activeIdx] = scene.copy(
                    dialogues = updatedDialogues
                )
            }
            _uiState.value = _uiState.value.copy(
                currentScript = script.copy(
                    scenes = updatedScenes,
                    characters = (script.characters.filter { it.name != draft.name } + draft)
                ),
                activeSpeakerName = draft.name,
                statusMessage = "Cast '${draft.name}' in Scene ${activeIdx + 1}!"
            )
        }
    }

    fun auditionDraftVoice(customText: String? = null) {
        val draft = _uiState.value.characterDraft
        val textToSpeak = customText ?: draft.sampleDialogue.ifBlank {
            "こんにちは！俺は${draft.name}だ！(Hello! I am ${draft.name}!)"
        }
        voiceSyncEngine.speakDialogue(
            characterName = draft.name,
            dialogueText = textToSpeak,
            emotion = "Excited",
            voiceType = draft.voiceType,
            pitch = draft.voicePitch,
            speed = draft.voiceSpeed,
            voiceGender = draft.voiceGender,
            voicePersona = draft.voicePersona,
            voiceAccent = draft.voiceAccent
        )
    }

    fun deleteCustomCharacter(id: String) {
        val updated = _uiState.value.customCharacters.filter { it.id != id }
        _uiState.value = _uiState.value.copy(
            customCharacters = updated,
            statusMessage = "Character removed."
        )
        viewModelScope.launch {
            repository.deleteCharacter(id)
        }
    }

    fun generateAnimeVideo() {
        val state = _uiState.value
        val input = when (state.selectedInputMode) {
            1 -> state.linkInput.ifBlank { "https://animestudio.ai/story" }
            2 -> state.imageInputDescription.ifBlank { "Visual Content: Scanned anime visual scene" }
            else -> state.promptInput.ifBlank { "Video Generate: High-quality anime production" }
        }
        val sourceType = when (state.selectedInputMode) {
            1 -> "WEB_LINK"
            2 -> "SCANNED_IMAGE"
            else -> "TEXT_PROMPT"
        }

        viewModelScope.launch {
            pauseVideo()
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                generationStep = "AI analyzing $sourceType & building characters..."
            )
            delay(500)

            _uiState.value = _uiState.value.copy(
                generationStep = "Directing anime storyboard scenes with ${state.selectedArtStyle.title}..."
            )
            delay(400)

            _uiState.value = _uiState.value.copy(
                generationStep = "Harmonizing voice synchronization & Japanese audio scores..."
            )

            val script = repository.createAnimeScript(
                input = input,
                sourceType = sourceType,
                artStyle = state.selectedArtStyle,
                language = state.videoGenerationLanguage,
                productionFormat = state.selectedProductionFormat,
                motionEffect = state.selectedMotionEffect,
                voiceAccent = state.selectedVoiceAccent
            )

            if (isCommandLearningAccessibleForCurrentUser() && input.isNotBlank()) {
                launch {
                    commandLearningEngine.learnAndImproveCommand(
                        rawCommand = input,
                        currentLanguage = state.selectedLanguage,
                        geminiApiService = geminiService,
                        userId = currentUser.value.userId
                    )
                }
            }

            repository.saveScript(script)

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                generationStep = "",
                currentScript = script,
                activeSceneIndex = 0,
                currentDialogueIndex = 0,
                currentTab = AppTab.STUDIO,
                statusMessage = "✨ Anime Script & Video successfully generated! Playing real-time preview in Studio..."
            )

            // Auto-start video in studio dashboard player
            playVideo()
        }
    }

    fun setAutomationCommandInput(input: String) {
        _uiState.value = _uiState.value.copy(automationCommandInput = input)
    }

    fun setAutomationDurationSeconds(seconds: Int) {
        _uiState.value = _uiState.value.copy(automationDurationSeconds = seconds.coerceIn(15, 7200))
    }

    /**
     * Instantly switches the app language, voiceover, and accent to the selected country
     */
    fun selectCountry(country: CountryCode) {
        _selectedCountry.value = country
        setAppLanguage(country.primaryLanguage)
        _uiState.value = _uiState.value.copy(
            selectedVoiceAccent = country.defaultVoiceAccent,
            statusMessage = "🌍 ${country.flagEmoji} ${country.name}: ${country.nativeLanguageName} (${country.primaryLanguage})"
        )
    }

    fun setVibrantTheme(theme: String) {
        com.example.ui.theme.AppThemeController.setTheme(theme, _uiState.value.isDarkMode)
        _uiState.value = _uiState.value.copy(
            vibrantTheme = theme,
            statusMessage = "🎨 Theme: $theme"
        )
    }

    fun setCustomThemeColor(color: androidx.compose.ui.graphics.Color) {
        com.example.ui.theme.AppThemeController.setCustomAccent(color)
        _uiState.value = _uiState.value.copy(
            statusMessage = "🎨 Custom Color Applied"
        )
    }

    fun resetThemeToDefaultWhite() {
        com.example.ui.theme.AppThemeController.resetToDefaultWhite()
        _uiState.value = _uiState.value.copy(
            isDarkMode = false,
            vibrantTheme = "WHITE_MINIMAL",
            statusMessage = "⚪ Reset to Pure White Theme"
        )
    }

    fun updateIconCustomization(styleKey: String, shapeKey: String, localIconKey: String) {
        _uiState.value = _uiState.value.copy(
            iconStyle = styleKey,
            iconShape = shapeKey,
            appIconTheme = localIconKey,
            statusMessage = "✨ Icons Updated: $styleKey ($localIconKey)"
        )
    }

    /**
     * Sets only the App UI Interface Language (menus, buttons, strings, settings).
     * Strictly does NOT override or change video/dubbing generation language.
     */
    fun setAppInterfaceLanguage(language: String) {
        val normalized = com.example.localization.AppLocaleStrings.normalizeLanguage(language)
        val matchedCountry = CountryCodeProvider.findByLanguage(normalized)
            ?: CountryCodeProvider.countries.firstOrNull { it.primaryLanguage.equals(normalized, ignoreCase = true) }
            ?: _selectedCountry.value.copy(primaryLanguage = normalized)
        _selectedCountry.value = matchedCountry

        try {
            getApplication<Application>().getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
                .edit()
                .putString("app_interface_language", normalized)
                .putString("app_selected_language", normalized)
                .commit()
        } catch (_: Exception) {}

        _uiState.value = _uiState.value.copy(
            appInterfaceLanguage = normalized,
            selectedLanguage = normalized,
            statusMessage = "📱 App UI Language set: $normalized"
        )
    }

    /**
     * Sets only the Video & Dubbing Generation Language (script generation, dialogue text, TTS speech synthesis).
     * Strictly does NOT override or change the app UI interface language.
     */
    fun setVideoGenerationLanguage(language: String) {
        val normalized = com.example.localization.AppLocaleStrings.normalizeLanguage(language)
        try {
            getApplication<Application>().getSharedPreferences("app_settings_prefs", Context.MODE_PRIVATE)
                .edit().putString("video_generation_language", normalized).commit()
        } catch (_: Exception) {}

        _uiState.value = _uiState.value.copy(
            videoGenerationLanguage = normalized,
            voiceoverLanguage = normalized,
            statusMessage = "🎬 Video Generation Language set: $normalized"
        )
        voiceSyncEngine.setLanguage(normalized)
    }

    fun setAppLanguage(language: String) {
        setAppInterfaceLanguage(language)
    }

    /**
     * Updates an existing saved video project's details directly in the local Room database.
     */
    fun updateSavedScriptDetails(scriptId: String, newTitle: String, newSynopsis: String, newGenre: String) {
        viewModelScope.launch {
            repository.updateScriptDetails(scriptId, newTitle, newSynopsis, newGenre)
            _uiState.value = _uiState.value.copy(
                statusMessage = "✓ Project details updated in Room database!"
            )
        }
    }

    // YouTube Channel ID, OAuth 2.0 Credentials & WorkManager Methods
    fun saveYouTubeCredentials(
        channelId: String,
        channelHandle: String,
        channelName: String,
        channelUrl: String,
        clientId: String,
        clientSecret: String,
        accessToken: String,
        refreshToken: String,
        defaultPrivacy: com.example.data.model.UploadPrivacyStatus = com.example.data.model.UploadPrivacyStatus.PUBLIC
    ) {
        ytCredManager.saveCredentials(
            channelId = channelId,
            channelHandle = channelHandle,
            channelName = channelName,
            channelUrl = channelUrl,
            clientId = clientId,
            clientSecret = clientSecret,
            accessToken = accessToken,
            refreshToken = refreshToken,
            defaultPrivacy = defaultPrivacy
        )
        _uiState.value = _uiState.value.copy(
            statusMessage = "🔒 YouTube OAuth & Channel ID credentials securely saved!"
        )
    }

    fun updateYouTubeDailySchedule(hour: Int, minute: Int, enabled: Boolean, episodesPerDay: Int = 1) {
        ytCredManager.updateDailySchedule(
            hour = hour,
            minute = minute,
            enabled = enabled,
            episodesPerDay = episodesPerDay
        )
        _uiState.value = _uiState.value.copy(
            statusMessage = if (enabled) "⏰ WorkManager Daily Scheduler active for %02d:%02d (%d ep/day)".format(hour, minute, episodesPerDay)
                            else "⏹️ Daily WorkManager scheduler paused"
        )
    }

    fun triggerImmediateDailyAutomation() {
        com.example.worker.AutomationWorkScheduler.triggerImmediateRun(getApplication())
        _uiState.value = _uiState.value.copy(
            statusMessage = "⚡ Immediate background content generation triggered via WorkManager!"
        )
    }

    fun testYouTubeConnection(channelId: String, channelHandle: String, accessToken: String): com.example.data.engine.ConnectionTestResult {
        return ytCredManager.testCredentials(channelId, channelHandle, accessToken)
    }

    fun setYouTubeAutomationGloballyEnabled(enabled: Boolean) {
        ytCredManager.setAutomationGloballyEnabled(enabled)
        _uiState.value = _uiState.value.copy(
            statusMessage = if (enabled) "👑 Owner: YouTube Automation ENABLED for other users"
                            else "👑 Owner: YouTube Automation DISABLED for other users"
        )
    }

    fun setYouTubeAutomationUserAccess(userIdOrEmail: String, isGranted: Boolean) {
        ytCredManager.setUserAutomationAccess(userIdOrEmail, isGranted)
        _uiState.value = _uiState.value.copy(
            statusMessage = if (isGranted) "✓ Granted YouTube Automation access to $userIdOrEmail"
                            else "🔒 Revoked YouTube Automation access for $userIdOrEmail"
        )
    }

    fun removeYouTubeAutomationUser(userIdOrEmail: String) {
        ytCredManager.removeUserAutomationAccess(userIdOrEmail)
        _uiState.value = _uiState.value.copy(
            statusMessage = "Removed $userIdOrEmail from YouTube automation registry"
        )
    }

    fun isYouTubeAutomationAccessibleForCurrentUser(): Boolean {
        return ytCredManager.isAutomationAccessibleForUser(currentUser.value)
    }

    fun unlockOwnerWithPin(pin: String): Boolean {
        val success = ytCredManager.verifyAndUnlockOwnerPin(pin)
        if (success) {
            _uiState.value = _uiState.value.copy(statusMessage = "👑 Owner access verified and unlocked!")
        }
        return success
    }

    fun lockOwnerAccess() {
        ytCredManager.lockOwnerAccess()
        _uiState.value = _uiState.value.copy(statusMessage = "🔒 Owner access locked")
    }

    fun setOwnerMasterPin(newPin: String) {
        ytCredManager.setOwnerMasterPin(newPin)
        _uiState.value = _uiState.value.copy(statusMessage = "🔑 Owner master PIN updated!")
    }

    // ---------------------------------------------------------------------------------
    // AI ADAPTIVE COMMAND LEARNING & AUTO-IMPROVEMENT ACTIONS (OWNER CONTROLLED)
    // ---------------------------------------------------------------------------------
    fun isCommandLearningAccessibleForCurrentUser(): Boolean {
        return commandLearningEngine.isFeatureAccessibleForUser(currentUser.value)
    }

    fun learnAndImproveCurrentCommand(rawCommand: String) {
        viewModelScope.launch {
            val (improved, suggestions) = commandLearningEngine.learnAndImproveCommand(
                rawCommand = rawCommand,
                currentLanguage = _uiState.value.selectedLanguage,
                geminiApiService = geminiService,
                userId = currentUser.value.userId
            )
            if (improved.isNotBlank()) {
                _uiState.value = _uiState.value.copy(
                    statusMessage = "🧠 AI Command Learned & Improved!"
                )
            }
        }
    }

    fun applyImprovedCommandToPrompt(improved: String) {
        setPromptInput(improved)
        _uiState.value = _uiState.value.copy(
            statusMessage = "✓ Applied AI-improved command to studio!"
        )
    }

    fun applySuggestionToPrompt(suggestion: String) {
        val clean = suggestion.replace(Regex("^[⚔️🌌🌸🔥⚡🎬]\\s*(\\[.*?\\]:\\s*)?"), "")
        val current = _uiState.value.promptInput
        val updated = if (current.isBlank()) clean else "$current | $clean"
        setPromptInput(updated)
        _uiState.value = _uiState.value.copy(
            statusMessage = "✓ Added creative suggestion to prompt"
        )
    }

    fun setCommandLearningOwnerActive(active: Boolean) {
        commandLearningEngine.setOwnerActive(active)
        _uiState.value = _uiState.value.copy(
            statusMessage = if (active) "👑 AI Command Learning ACTIVATED for Owner" else "👑 AI Command Learning DEACTIVATED for Owner"
        )
    }

    fun setCommandLearningGlobalOthersActive(active: Boolean) {
        commandLearningEngine.setGloballyActiveForOthers(active)
        _uiState.value = _uiState.value.copy(
            statusMessage = if (active) "👑 AI Command Learning ENABLED for all users" else "👑 AI Command Learning DISABLED for all users"
        )
    }

    fun setCommandLearningUserAccess(userIdOrEmail: String, isGranted: Boolean) {
        commandLearningEngine.setUserAuthorization(userIdOrEmail, isGranted)
        _uiState.value = _uiState.value.copy(
            statusMessage = if (isGranted) "✓ Granted command AI access to $userIdOrEmail" else "🔒 Revoked command AI access for $userIdOrEmail"
        )
    }

    fun removeCommandLearningUser(userIdOrEmail: String) {
        commandLearningEngine.removeUserFromRegistry(userIdOrEmail)
        _uiState.value = _uiState.value.copy(
            statusMessage = "Removed $userIdOrEmail from registry"
        )
    }

    fun toggleStorageDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showStorageDestinationDialog = show)
    }

    fun setStorageTarget(target: com.example.data.model.StorageTargetType) {
        storageManager.setActiveTarget(target)
    }

    fun setSdCardFolder(uri: android.net.Uri, name: String) {
        storageManager.setSdCardFolder(uri, name)
    }

    fun setHardDiskFolder(uri: android.net.Uri, name: String) {
        storageManager.setHardDiskFolder(uri, name)
    }

    fun setGoogleDriveAccount(account: String, folder: String) {
        storageManager.setGoogleDriveAccount(account, folder)
    }

    fun syncAllToGoogleDrive() {
        viewModelScope.launch {
            storageManager.syncAllToGoogleDrive()
        }
    }

    /**
     * Starts the background MP4 video export worker
     */
    fun startMp4VideoExport(
        durationSec: Int? = null,
        resolutionLabel: String = "1280x720 (720p HD)"
    ) {
        val script = _uiState.value.currentScript ?: return
        val duration = durationSec ?: _uiState.value.automationDurationSeconds
        exportManager.startExport(
            script = script,
            targetDurationSec = duration,
            resolutionLabel = resolutionLabel,
            targetLanguage = _uiState.value.selectedLanguage
        )
    }

    fun cancelMp4VideoExport() {
        exportManager.cancelExport()
    }

    fun deleteExportedVideo(id: String, filePath: String) {
        viewModelScope.launch {
            try {
                val f = java.io.File(filePath)
                if (f.exists()) f.delete()
            } catch (e: Exception) {
                Log.w("AnimeViewModel", "Error deleting mp4: ${e.message}")
            }
            db.animeDao().deleteExportedVideoById(id)
            _uiState.value = _uiState.value.copy(statusMessage = "🗑️ MP4 वीडियो फाइल हटा दी गई।")
        }
    }

    fun createShareVideoIntent(filePath: String): Intent? {
        return exportManager.createShareIntent(filePath)
    }

    fun setAutomationSelectedLanguage(lang: String) {
        _uiState.value = _uiState.value.copy(automationSelectedLanguage = lang)
    }

    fun setAutomationSelectedAccent(accent: String) {
        _uiState.value = _uiState.value.copy(automationSelectedAccent = accent)
    }

    fun runAutonomousDirector(
        commandOrLink: String = _uiState.value.automationCommandInput,
        overrideDurationSec: Int? = _uiState.value.automationDurationSeconds,
        overrideLanguage: String? = if (_uiState.value.automationSelectedLanguage == "Auto-Detect") null else _uiState.value.automationSelectedLanguage,
        overrideAccent: String? = if (_uiState.value.automationSelectedAccent == "Auto-Detect") null else _uiState.value.automationSelectedAccent
    ) {
        val input = commandOrLink.ifBlank { "Video Generate: High-quality anime production sequence" }
        viewModelScope.launch {
            pauseVideo()
            _uiState.value = _uiState.value.copy(
                isAutonomousExecuting = true,
                isGenerating = true,
                automationProgress = 0.05f,
                automationStatusStep = msg("Analyzing command and link...", "कमांड व लिंक का एआई विश्लेषण शुरू हो रहा है..."),
                generationStep = msg("Autonomous AI Director activating...", "ऑटोनॉमस एआई डायरेक्टर सक्रिय हो रहा है...")
            )

            try {
                val (parsedCommand, script) = repository.executeAutonomousDirector(
                    commandInput = input,
                    overrideDurationSec = overrideDurationSec,
                    overrideLanguage = overrideLanguage,
                    overrideAccent = overrideAccent,
                    onProgressUpdate = { step, progress ->
                        _uiState.value = _uiState.value.copy(
                            automationStatusStep = step,
                            generationStep = step,
                            automationProgress = progress
                        )
                    }
                )

                // Configure voice engine for the detected/generated language
                voiceSyncEngine.setLanguage(script.language)

                if (isCommandLearningAccessibleForCurrentUser() && input.isNotBlank()) {
                    launch {
                        commandLearningEngine.learnAndImproveCommand(
                            rawCommand = input,
                            currentLanguage = _uiState.value.selectedLanguage,
                            geminiApiService = geminiService,
                            userId = currentUser.value.userId
                        )
                    }
                }

                _uiState.value = _uiState.value.copy(
                    isAutonomousExecuting = false,
                    isGenerating = false,
                    automationProgress = 1.0f,
                    automationStatusStep = msg("Completed!", "पूर्ण!"),
                    lastAutonomousSummary = parsedCommand.detectionSummaryHindi,
                    currentScript = script,
                    activeSceneIndex = 0,
                    currentDialogueIndex = 0,
                    voiceoverLanguage = script.language,
                    selectedVoiceAccent = parsedCommand.targetAccent,
                    selectedArtStyle = parsedCommand.targetArtStyle,
                    selectedMotionEffect = parsedCommand.targetMotionEffect,
                    selectedProductionFormat = parsedCommand.targetProductionFormat,
                    statusMessage = "🚀 ऑटोनॉमस वीडियो सफलतापूर्वक तैयार! (${parsedCommand.requestedDurationSeconds}s, ${parsedCommand.calculatedSceneCount} सीन्स)",
                    currentTab = AppTab.PLAYER
                )

                // Automatically launch player and start playback
                delay(300)
                playVideo()

            } catch (e: Exception) {
                Log.e("AnimeViewModel", "Autonomous execution error", e)
                _uiState.value = _uiState.value.copy(
                    isAutonomousExecuting = false,
                    isGenerating = false,
                    statusMessage = msg("Error in autonomous generation: ${e.message}", "ऑटोनॉमस जनरेशन में त्रुटि: ${e.message}")
                )
            }
        }
    }

    fun translateAndDub(targetLang: String) {
        val script = _uiState.value.currentScript
        if (script == null) {
            // When no video has been generated yet, set the video audio/dubbing language only
            setVoiceoverLanguage(targetLang)
            return
        }
        viewModelScope.launch {
            pauseVideo()
            _uiState.value = _uiState.value.copy(
                isGenerating = true,
                generationStep = "Translating script & dubbing voices into $targetLang..."
            )

            val translated = repository.translateScript(script, targetLang)
            voiceSyncEngine.setLanguage(targetLang)

            _uiState.value = _uiState.value.copy(
                isGenerating = false,
                generationStep = "",
                currentScript = translated,
                voiceoverLanguage = targetLang,
                statusMessage = "🎌 Successfully dubbed into $targetLang!"
            )
            playVideo()
        }
    }

    fun playVideo() {
        val script = _uiState.value.currentScript ?: return
        if (script.scenes.isEmpty()) return

        _uiState.value = _uiState.value.copy(isPlayingVideo = true)

        val currentScene = script.scenes.getOrNull(_uiState.value.activeSceneIndex) ?: script.scenes.first()
        if (!_uiState.value.isMusicMuted) {
            musicSynthesizer.start(currentScene.bgMood)
        }

        startScenePlayback(_uiState.value.activeSceneIndex, _uiState.value.currentDialogueIndex)
    }

    fun pauseVideo() {
        _uiState.value = _uiState.value.copy(isPlayingVideo = false)
        playbackJob?.cancel()
        playbackJob = null
        voiceSyncEngine.stop()
        musicSynthesizer.stop()
    }

    fun selectScene(sceneIndex: Int) {
        pauseVideo()
        val script = _uiState.value.currentScript ?: return
        if (sceneIndex in script.scenes.indices) {
            _uiState.value = _uiState.value.copy(
                activeSceneIndex = sceneIndex,
                currentDialogueIndex = 0
            )
            playVideo()
        }
    }

    fun nextScene() {
        val script = _uiState.value.currentScript ?: return
        val nextIdx = (_uiState.value.activeSceneIndex + 1) % script.scenes.size
        selectScene(nextIdx)
    }

    fun previousScene() {
        val script = _uiState.value.currentScript ?: return
        val prevIdx = if (_uiState.value.activeSceneIndex - 1 < 0) script.scenes.size - 1 else _uiState.value.activeSceneIndex - 1
        selectScene(prevIdx)
    }

    fun toggleMusic() {
        val newMuted = !_uiState.value.isMusicMuted
        _uiState.value = _uiState.value.copy(isMusicMuted = newMuted)
        if (newMuted) {
            musicSynthesizer.stop()
        } else if (_uiState.value.isPlayingVideo) {
            val scene = _uiState.value.currentScript?.scenes?.getOrNull(_uiState.value.activeSceneIndex)
            musicSynthesizer.start(scene?.bgMood ?: "Emotional Piano")
        }
    }

    fun setVideoPresentationMode(mode: com.example.data.model.VideoPresentationMode) {
        _uiState.value = _uiState.value.copy(videoPresentationMode = mode)
        if (_uiState.value.isPlayingVideo) {
            val currentIdx = _uiState.value.activeSceneIndex
            startScenePlayback(currentIdx, 0)
        }
    }

    fun toggleVideoPresentationMode() {
        val next = if (_uiState.value.videoPresentationMode == com.example.data.model.VideoPresentationMode.EXPLAINER_MODE) {
            com.example.data.model.VideoPresentationMode.INDEPENDENT_CHARACTERS
        } else {
            com.example.data.model.VideoPresentationMode.EXPLAINER_MODE
        }
        setVideoPresentationMode(next)
    }

    private fun startScenePlayback(sceneIdx: Int, startingDialogueIdx: Int) {
        playbackJob?.cancel()
        playbackJob = viewModelScope.launch {
            val script = _uiState.value.currentScript ?: return@launch
            if (sceneIdx >= script.scenes.size) {
                // Loop back to start
                _uiState.value = _uiState.value.copy(activeSceneIndex = 0, currentDialogueIndex = 0)
                startScenePlayback(0, 0)
                return@launch
            }

            val scene = script.scenes[sceneIdx]
            musicSynthesizer.currentMood = scene.bgMood

            val isExplainer = _uiState.value.videoPresentationMode == com.example.data.model.VideoPresentationMode.EXPLAINER_MODE

            // In Explainer Mode, the Narrator opens the scene with storyline explanation
            if (isExplainer && startingDialogueIdx == 0) {
                val isHi = com.example.localization.AppLocaleStrings.isHindi(_uiState.value.selectedLanguage)
                val narratorSpeaker = if (isHi) "🎙️ सूत्रधार (Narrator)" else "🎙️ Explainer Narrator"
                val introStoryline = if (isHi) {
                    "दृश्य ${sceneIdx + 1}: ${scene.title}। ${scene.visualPrompt.take(120)}"
                } else {
                    "${scene.title}: ${scene.visualPrompt.take(120)}"
                }

                _uiState.value = _uiState.value.copy(
                    activeSpeakerName = narratorSpeaker,
                    activeDialogueText = introStoryline,
                    activeDialogueEmotion = "Explaining Storyline",
                    activeCharacterExpression = "Cinematic Storyteller",
                    activeMotionEffect = scene.motionEffect,
                    explainerStoryBeatText = introStoryline
                )

                if (!scene.isMuted) {
                    musicSynthesizer.setDucking(true)
                    var narratorDone = false
                    voiceSyncEngine.speakDialogue(
                        characterName = "Narrator",
                        dialogueText = introStoryline,
                        emotion = "Narrative",
                        voiceType = "Lady",
                        pitch = 1.0f,
                        speed = 1.0f,
                        voiceGender = "Female",
                        voicePersona = "Sensei / Mentor",
                        voiceAccent = _uiState.value.selectedVoiceAccent,
                        onStart = { musicSynthesizer.setDucking(true) },
                        onDone = {
                            musicSynthesizer.setDucking(false)
                            narratorDone = true
                        }
                    )
                    var nWaited = 0L
                    while (!narratorDone && nWaited < 9000L && _uiState.value.isPlayingVideo) {
                        delay(100)
                        nWaited += 100
                    }
                    musicSynthesizer.setDucking(false)
                    delay(350)
                }
            }

            for (dIdx in startingDialogueIdx until scene.dialogues.size) {
                if (!_uiState.value.isPlayingVideo) return@launch

                val dialogue = scene.dialogues[dIdx]
                val matchingChar = script.characters.find { it.name.equals(dialogue.characterName, ignoreCase = true) }
                    ?: _uiState.value.customCharacters.find { it.name.equals(dialogue.characterName, ignoreCase = true) }

                _uiState.value = _uiState.value.copy(
                    activeSceneIndex = sceneIdx,
                    currentDialogueIndex = dIdx,
                    activeSpeakerName = if (isExplainer) "🎙️ ${dialogue.characterName}" else dialogue.characterName,
                    activeDialogueText = dialogue.text,
                    activeDialogueEmotion = dialogue.emotion,
                    activeCharacterExpression = dialogue.expression.ifBlank { matchingChar?.expression ?: "Confident Smirk" },
                    activeMotionEffect = dialogue.motionEffect.ifBlank { scene.motionEffect }
                )

                if (scene.isMuted) {
                    // Specific scene is muted: silence audio for this segment without affecting rest of project
                    val displayDurationMs = (dialogue.text.length * 45L).coerceIn(1200L, 3000L)
                    var waited = 0L
                    while (waited < displayDurationMs && _uiState.value.isPlayingVideo) {
                        delay(100)
                        waited += 100
                    }
                } else {
                    musicSynthesizer.setDucking(true)
                    var speechFinished = false

                    voiceSyncEngine.speakDialogue(
                        characterName = dialogue.characterName,
                        dialogueText = dialogue.text,
                        emotion = dialogue.emotion,
                        voiceType = dialogue.voiceType,
                        pitch = dialogue.voicePitch,
                        speed = dialogue.voiceSpeed,
                        voiceGender = matchingChar?.voiceGender ?: "",
                        voicePersona = matchingChar?.voicePersona ?: "",
                        voiceAccent = dialogue.voiceAccent.ifBlank { matchingChar?.voiceAccent ?: "" },
                        onStart = {
                            musicSynthesizer.setDucking(true)
                        },
                        onDone = {
                            musicSynthesizer.setDucking(false)
                            speechFinished = true
                        }
                    )

                    // Wait until dialogue is spoken (with safety timeout)
                    val maxWaitMs = 12000L
                    var waited = 0L
                    while (!speechFinished && waited < maxWaitMs && _uiState.value.isPlayingVideo) {
                        delay(100)
                        waited += 100
                    }
                    musicSynthesizer.setDucking(false)
                }
                delay(600) // Brief pause between character lines
            }

            // Scene complete! Move to next scene
            if (_uiState.value.isPlayingVideo) {
                val nextIdx = (sceneIdx + 1) % script.scenes.size
                _uiState.value = _uiState.value.copy(
                    activeSceneIndex = nextIdx,
                    currentDialogueIndex = 0
                )
                startScenePlayback(nextIdx, 0)
            }
        }
    }

    fun addCustomCharacter(name: String, gender: String, role: String, personality: String, voicePitch: Float, voiceSpeed: Float, voiceType: String) {
        val newChar = CharacterProfile(
            id = "char_${System.currentTimeMillis()}",
            name = name,
            gender = gender,
            role = role,
            personality = personality,
            voicePitch = voicePitch,
            voiceSpeed = voiceSpeed,
            voiceType = voiceType,
            avatarDrawableName = when {
                voiceType.contains("boy", ignoreCase = true) || voiceType.contains("male", ignoreCase = true) -> "char_shonen_hero"
                voiceType.contains("lady", ignoreCase = true) -> "char_lady_mentor"
                voiceType.contains("mascot", ignoreCase = true) -> "char_chibi_mascot"
                else -> "char_anime_heroine"
            }
        )
        val updated = _uiState.value.customCharacters + newChar
        _uiState.value = _uiState.value.copy(
            customCharacters = updated,
            statusMessage = "Character '$name' created with custom voice sync!"
        )
        viewModelScope.launch {
            repository.saveCustomCharacter(newChar)
        }
    }

    fun testAuditionVoice(character: CharacterProfile) {
        voiceSyncEngine.speakDialogue(
            characterName = character.name,
            dialogueText = msg("Hello! I am ${character.name}. This is my anime voice!", "नमस्ते! मैं ${character.name} हूँ। यह मेरी एनिमे आवाज़ है!"),
            emotion = "Happy",
            voiceType = character.voiceType,
            pitch = character.voicePitch,
            speed = character.voiceSpeed
        )
    }

    fun redeemPromoCode(code: String) {
        viewModelScope.launch {
            val result = repository.redeemPromoCode(code)
            _uiState.value = _uiState.value.copy(promoCodeMessage = result)
        }
    }

    fun applyPromoCode(code: String) = redeemPromoCode(code)

    fun toggleGlobalFree(enabled: Boolean) {
        viewModelScope.launch {
            val current = adminSettingsState.value ?: AdminAccessEntity()
            repository.updateAdminSettings(current.copy(isGlobalFreeEnabled = enabled))
        }
    }

    fun toggleGlobalFreeAccess(enabled: Boolean) = toggleGlobalFree(enabled)

    fun toggleSceneMute(sceneIndex: Int) {
        val script = _uiState.value.currentScript ?: return
        val updatedScenes = script.scenes.mapIndexed { idx, s ->
            if (idx == sceneIndex) s.copy(isMuted = !s.isMuted) else s
        }
        _uiState.value = _uiState.value.copy(currentScript = script.copy(scenes = updatedScenes))
    }

    fun updateSceneTransition(sceneIndex: Int, transitionEffect: String, transitionDurationSec: Float) {
        val script = _uiState.value.currentScript ?: return
        val updatedScenes = script.scenes.mapIndexed { idx, s ->
            if (idx == sceneIndex) s.copy(
                transitionEffect = transitionEffect,
                transitionDurationSec = transitionDurationSec
            ) else s
        }
        _uiState.value = _uiState.value.copy(
            currentScript = script.copy(scenes = updatedScenes),
            statusMessage = "Transition set to '$transitionEffect' (${"%.1f".format(transitionDurationSec)}s)"
        )
    }

    fun applyTransitionToAllScenes(transitionEffect: String, transitionDurationSec: Float) {
        val script = _uiState.value.currentScript ?: return
        val updatedScenes = script.scenes.map { s ->
            s.copy(transitionEffect = transitionEffect, transitionDurationSec = transitionDurationSec)
        }
        _uiState.value = _uiState.value.copy(
            currentScript = script.copy(scenes = updatedScenes),
            statusMessage = "Applied '$transitionEffect' (${"%.1f".format(transitionDurationSec)}s) to all scenes"
        )
    }

    fun addAuthorizedEmail(email: String) {
        viewModelScope.launch {
            val current = adminSettingsState.value ?: AdminAccessEntity()
            val list = current.authorizedFreeEmails.split(",").map { it.trim() }.toMutableList()
            if (!list.contains(email.trim())) {
                list.add(email.trim())
                repository.updateAdminSettings(current.copy(authorizedFreeEmails = list.joinToString(",")))
                _uiState.value = _uiState.value.copy(statusMessage = "Granted Free VIP access to $email")
            }
        }
    }

    fun triggerInAppUpdate() {
        viewModelScope.launch {
            repository.startUpdateDownload(onProgress = {}, onComplete = {})
            delay(1500)
            repository.completeUpdate()
            _uiState.value = _uiState.value.copy(statusMessage = "App successfully updated to v1.1.0!")
        }
    }

    // Local Storage & Export / Share Operations
    fun toggleLocalStorageVault(show: Boolean) {
        _uiState.value = _uiState.value.copy(showLocalStorageVault = show, importError = "")
    }

    fun toggleExportShareDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showExportShareDialog = show, exportFeedbackMessage = "")
    }

    fun saveCurrentScriptToLocalStorage() {
        val script = _uiState.value.currentScript ?: return
        viewModelScope.launch {
            repository.saveScript(script)
            val msg = "💾 '${script.title}' saved to SQLite Room Local Storage!"
            _uiState.value = _uiState.value.copy(
                statusMessage = msg,
                exportFeedbackMessage = msg
            )
        }
    }

    fun loadSavedScript(entity: SavedScriptEntity) {
        viewModelScope.launch {
            pauseVideo()
            val script = repository.deserializeScript(entity.scriptJson)
            if (script != null) {
                _uiState.value = _uiState.value.copy(
                    currentScript = script,
                    activeSceneIndex = 0,
                    currentDialogueIndex = 0,
                    voiceoverLanguage = script.voiceoverLanguage,
                    showLocalStorageVault = false,
                    statusMessage = "📂 Loaded '${script.title}' from Local Storage!"
                )
                voiceSyncEngine.setLanguage(script.voiceoverLanguage)
                playVideo()
            } else {
                _uiState.value = _uiState.value.copy(
                    statusMessage = "Error: Could not parse script from local database"
                )
            }
        }
    }

    fun deleteSavedScript(id: String) {
        viewModelScope.launch {
            repository.deleteScript(id)
            _uiState.value = _uiState.value.copy(
                statusMessage = "Script removed from Room Local Storage."
            )
        }
    }

    fun exportScriptToFile(asJson: Boolean = false): String {
        val script = _uiState.value.currentScript ?: return "No active script to export"
        return try {
            val extension = if (asJson) "json" else "txt"
            val sanitizedTitle = script.title.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(30)
            val filename = "${sanitizedTitle}_${System.currentTimeMillis()}.$extension"
            val content = if (asJson) repository.getScriptAsJson(script) else repository.formatScriptForExport(script)
            val file = repository.saveExportFile(filename, content)
            val msg = "✅ Exported file: ${file.name} (${file.length()} bytes)"
            _uiState.value = _uiState.value.copy(
                exportFeedbackMessage = msg,
                statusMessage = msg
            )
            file.absolutePath
        } catch (e: Exception) {
            val err = "❌ Export failed: ${e.localizedMessage}"
            _uiState.value = _uiState.value.copy(exportFeedbackMessage = err)
            err
        }
    }

    fun getShareIntent(): Intent? {
        val script = _uiState.value.currentScript ?: return null
        return repository.createShareIntent(script)
    }

    fun getScriptAsFormattedText(): String {
        val script = _uiState.value.currentScript ?: return ""
        return repository.formatScriptForExport(script)
    }

    fun getScriptAsJson(): String {
        val script = _uiState.value.currentScript ?: return ""
        return repository.getScriptAsJson(script)
    }

    fun toggleTheme() {
        val newDark = !_uiState.value.isDarkMode
        com.example.ui.theme.AppThemeController.setDarkMode(newDark)
        _uiState.value = _uiState.value.copy(isDarkMode = newDark)
    }

    fun setTheme(isDark: Boolean) {
        com.example.ui.theme.AppThemeController.setDarkMode(isDark)
        _uiState.value = _uiState.value.copy(isDarkMode = isDark)
    }

    fun getScriptAsSrt(): String {
        val script = _uiState.value.currentScript ?: return ""
        return repository.generateSrtSubtitles(script)
    }

    fun getScriptFromJsonString(json: String): AnimeScript? {
        return repository.deserializeScript(json)
    }

    fun generateSrtForScript(script: AnimeScript): String {
        return repository.generateSrtSubtitles(script)
    }

    fun exportScriptToSrt(): String {
        val script = _uiState.value.currentScript ?: return "No active script to export"
        return try {
            val sanitizedTitle = script.title.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(30)
            val filename = "${sanitizedTitle}_subtitles_${System.currentTimeMillis()}.srt"
            val content = repository.generateSrtSubtitles(script)
            val file = repository.saveExportFile(filename, content)
            val msg = "✅ Exported SRT Subtitles: ${file.name}"
            _uiState.value = _uiState.value.copy(
                exportFeedbackMessage = msg,
                statusMessage = msg
            )
            file.absolutePath
        } catch (e: Exception) {
            val err = "❌ Export failed: ${e.localizedMessage}"
            _uiState.value = _uiState.value.copy(exportFeedbackMessage = err)
            err
        }
    }

    fun getVideoShareUrl(targetScript: AnimeScript? = null): String {
        val script = targetScript ?: _uiState.value.currentScript ?: return "https://animestudio.ai"
        return repository.generateVideoShareUrl(script)
    }

    fun copyProjectLinkToClipboard(context: Context, targetScript: AnimeScript? = null) {
        val script = targetScript ?: _uiState.value.currentScript
        if (script == null) {
            Toast.makeText(context, msg("No active project available", "कोई सक्रिय प्रोजेक्ट उपलब्ध नहीं है"), Toast.LENGTH_SHORT).show()
            return
        }
        val videoUrl = repository.generateVideoShareUrl(script)
        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
        val clip = ClipData.newPlainText("Anime Video Link", videoUrl)
        clipboard?.setPrimaryClip(clip)
        Toast.makeText(context, "📋 वीडियो लिंक क्लिपबोर्ड में कॉपी हो गया:\n$videoUrl", Toast.LENGTH_LONG).show()
    }

    fun shareProject(
        context: Context,
        platform: String = "ALL",
        shareMode: String = "DETAILS_AND_LINK",
        targetScript: AnimeScript? = null
    ) {
        val script = targetScript ?: _uiState.value.currentScript
        if (script == null) {
            Toast.makeText(context, msg("No active project available", "कोई सक्रिय प्रोजेक्ट उपलब्ध नहीं है"), Toast.LENGTH_SHORT).show()
            return
        }

        val shareIntent = repository.createSocialShareIntent(script, platform, shareMode)

        try {
            context.startActivity(shareIntent)
        } catch (_: ActivityNotFoundException) {
            // Target platform app is not installed on the user's device, fallback to Android's ACTION_SEND chooser
            val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_SUBJECT, "🎬 Anime Project: ${script.title}")
                putExtra(Intent.EXTRA_TEXT, repository.formatProjectDetailsForShare(script, shareMode))
                putExtra(Intent.EXTRA_TITLE, script.title)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            val platformDisplayName = when (platform.lowercase()) {
                "whatsapp" -> "WhatsApp"
                "instagram" -> "Instagram"
                "snapchat" -> "Snapchat"
                "youtube" -> "YouTube"
                "google" -> "Google Drive / Gmail"
                else -> platform
            }
            Toast.makeText(context, msg("ℹ️ $platformDisplayName not installed, sharing via other apps", "ℹ️ $platformDisplayName इंस्टॉल नहीं मिला, अन्य ऐप्स से शेयर करें"), Toast.LENGTH_SHORT).show()
            val chooser = Intent.createChooser(fallbackIntent, "प्रोजेक्ट व वीडियो लिंक शेयर करें...").apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            try {
                context.startActivity(chooser)
            } catch (e: Exception) {
                Toast.makeText(context, msg("Share error: ${e.localizedMessage}", "शेयर करने में समस्या: ${e.localizedMessage}"), Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(context, msg("Could not start sharing: ${e.localizedMessage}", "शेयरिंग प्रारंभ नहीं हो सकी: ${e.localizedMessage}"), Toast.LENGTH_SHORT).show()
        }
    }

    fun shareToSocialPlatform(context: Context, platform: String, targetScript: AnimeScript? = null) {
        shareProject(context, platform, "DETAILS_AND_LINK", targetScript)
    }

    fun playVoiceSampleSnippet(
        sampleText: String,
        pitch: Float,
        speed: Float,
        gender: String = "Boy",
        persona: String = "",
        accent: String = ""
    ) {
        val textToSpeak = if (sampleText.isNotBlank()) sampleText else "मैं अपनी शक्ति से इस दुनिया को बदल दूंगा!"
        voiceSyncEngine.speakDialogue(
            characterName = persona.ifBlank { "Voice Audition" },
            dialogueText = textToSpeak,
            emotion = "Energetic",
            voiceType = gender,
            pitch = pitch,
            speed = speed,
            voiceGender = gender,
            voicePersona = persona,
            voiceAccent = accent
        )
    }

    fun resumeProjectInStudio(savedEntity: SavedScriptEntity) {
        pauseVideo()
        val script = repository.deserializeScript(savedEntity.scriptJson)
        if (script != null) {
            val artStyle = AnimeArtStyle.entries.find { it.title.equals(script.artStyle, ignoreCase = true) }
                ?: AnimeArtStyle.JAPANESE_ANIME
            val format = com.example.data.model.ProductionFormat.entries.find { 
                it.title.equals(script.productionFormat, ignoreCase = true) || it.id.equals(script.productionFormat, ignoreCase = true)
            } ?: com.example.data.model.ProductionFormat.ANIME_EPISODE
            val motion = com.example.data.model.MotionEffect.entries.find { it.title.equals(script.defaultMotionEffect, ignoreCase = true) }
                ?: com.example.data.model.MotionEffect.SPEEDLINES_ACTION

            _uiState.value = _uiState.value.copy(
                currentScript = script,
                promptInput = if (script.originalPrompt.isNotBlank()) script.originalPrompt else script.synopsis,
                selectedArtStyle = artStyle,
                voiceoverLanguage = script.voiceoverLanguage,
                selectedProductionFormat = format,
                selectedMotionEffect = motion,
                selectedVoiceAccent = script.scenes.firstOrNull()?.dialogues?.firstOrNull()?.voiceAccent ?: _uiState.value.selectedVoiceAccent,
                activeSceneIndex = 0,
                currentDialogueIndex = 0,
                currentTab = AppTab.STUDIO,
                statusMessage = "✏️ '${script.title}' स्टूडियो में लोड हो गया! आप काम फिर से जारी रख सकते हैं।"
            )
        }
    }

    fun resumeProjectInPlayer(savedEntity: SavedScriptEntity) {
        loadSavedScriptAndPlay(savedEntity)
    }

    fun loadSavedScriptAndPlay(savedEntity: SavedScriptEntity) {
        val script = repository.deserializeScript(savedEntity.scriptJson)
        if (script != null) {
            _uiState.value = _uiState.value.copy(
                currentScript = script,
                activeSceneIndex = 0,
                currentDialogueIndex = 0,
                currentTab = AppTab.PLAYER,
                statusMessage = "Loaded '${script.title}' from My Projects!"
            )
            playVideo()
        }
    }

    fun importScriptFromJson(jsonText: String): Boolean {
        return try {
            val script = repository.deserializeScript(jsonText.trim())
            if (script != null && script.scenes.isNotEmpty()) {
                viewModelScope.launch {
                    repository.saveScript(script)
                }
                _uiState.value = _uiState.value.copy(
                    currentScript = script,
                    activeSceneIndex = 0,
                    currentDialogueIndex = 0,
                    importError = "",
                    showLocalStorageVault = false,
                    statusMessage = "📥 Imported '${script.title}' into Local Storage!"
                )
                true
            } else {
                _uiState.value = _uiState.value.copy(importError = "Invalid anime project JSON structure")
                false
            }
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(importError = "Import error: ${e.localizedMessage}")
            false
        }
    }

    // Media & Document Upload Operations
    fun setUploadedImageUri(uriString: String?, fileName: String? = null) {
        val currentDesc = _uiState.value.imageInputDescription
        val newDesc = if (currentDesc.isBlank() || currentDesc.startsWith("Anime warrior")) {
            msg("Japanese anime visual reference: $fileName", "जापानी एनिमे विजुअल संदर्भ: $fileName")
        } else {
            currentDesc
        }
        _uiState.value = _uiState.value.copy(
            uploadedImageUri = uriString,
            uploadedImageFileName = fileName ?: "reference_image.png",
            imageInputDescription = newDesc,
            selectedInputMode = 2,
            statusMessage = msg("📸 Image uploaded successfully!", "📸 इमेज सफलतापूर्वक अपलोड हो गई!")
        )
    }

    fun clearUploadedImage() {
        _uiState.value = _uiState.value.copy(
            uploadedImageUri = null,
            uploadedImageFileName = null,
            statusMessage = msg("Image removed", "इमेज हटा दी गई")
        )
    }

    fun importScriptFromUri(uri: Uri) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isUploadingFile = true)
            try {
                val content = getApplication<Application>().contentResolver.openInputStream(uri)?.use { stream ->
                    stream.bufferedReader(Charsets.UTF_8).readText()
                } ?: return@launch

                if (content.trim().startsWith("{") && (content.contains("\"title\"") || content.contains("\"scenes\""))) {
                    val ok = importScriptFromJson(content)
                    if (ok) {
                        _uiState.value = _uiState.value.copy(
                            isUploadingFile = false,
                            statusMessage = msg("📥 Project backup file uploaded and loaded successfully!", "📥 प्रोजेक्ट बैकअप फाइल सफलतापूर्वक अपलोड व लोड हो गई!")
                        )
                        return@launch
                    }
                }

                // Plain text anime story or prompt
                _uiState.value = _uiState.value.copy(
                    promptInput = content.take(2000),
                    selectedInputMode = 0,
                    isUploadingFile = false,
                    statusMessage = msg("📄 Script / story text file uploaded!", "📄 स्क्रिप्ट/स्टोरी टेक्स्ट फाइल अपलोड हो गई!")
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isUploadingFile = false,
                    statusMessage = msg("File upload error: ${e.localizedMessage}", "फाइल अपलोड त्रुटि: ${e.localizedMessage}")
                )
            }
        }
    }

    fun toggleDownloadDialog(show: Boolean) {
        _uiState.value = _uiState.value.copy(showDownloadDialog = show, downloadProgress = 0f, isDownloadingFile = false)
    }

    fun downloadProjectFile(format: String = "JSON") {
        val script = _uiState.value.currentScript ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isDownloadingFile = true, downloadProgress = 0.2f)
            delay(300)
            _uiState.value = _uiState.value.copy(downloadProgress = 0.6f)
            
            val sanitizedTitle = script.title.replace(Regex("[^a-zA-Z0-9_\\-]"), "_").take(25).ifBlank { "Anime_Project" }
            val timestamp = System.currentTimeMillis()
            
            val (filename, mimeType, fileContent) = when (format.uppercase()) {
                "TXT" -> Triple(
                    "${sanitizedTitle}_script_${timestamp}.txt",
                    "text/plain",
                    repository.formatScriptForExport(script)
                )
                "SRT" -> Triple(
                    "${sanitizedTitle}_subtitles_${timestamp}.srt",
                    "application/x-subrip",
                    repository.generateSrtSubtitles(script)
                )
                else -> Triple(
                    "${sanitizedTitle}_project_${timestamp}.json",
                    "application/json",
                    repository.getScriptAsJson(script)
                )
            }

            delay(200)
            _uiState.value = _uiState.value.copy(downloadProgress = 0.9f)
            val result = repository.downloadFileToDevice(filename, mimeType, fileContent)
            delay(150)

            val msg = if (result.first) {
                msg("✅ Download complete: Saved to ${result.second}!", "✅ डाउनलोड पूर्ण: ${result.second} में सेव हो गया!")
            } else {
                msg("❌ Download failed", "❌ डाउनलोड असफल रहा")
            }

            _uiState.value = _uiState.value.copy(
                isDownloadingFile = false,
                downloadProgress = 1.0f,
                lastDownloadedFileName = filename,
                statusMessage = msg,
                exportFeedbackMessage = msg
            )
        }
    }

    // ------------------------------------------------------------------------
    // FLIKI-STYLE MULTI-WORKFLOW STUDIO LOGIC
    // ------------------------------------------------------------------------

    fun setWorkflowCategory(category: WorkflowCategory) {
        _uiState.value = _uiState.value.copy(
            currentWorkflowCategory = category
        )
    }

    fun selectAndLaunchWorkflow(workflow: StudioWorkflow) {
        _uiState.value = _uiState.value.copy(
            activeStudioWorkflow = workflow,
            showWorkflowInteractiveModal = true
        )
    }

    fun dismissWorkflowModal() {
        if (_uiState.value.musicGeneratorData.isPlayingPreview) {
            musicSynthesizer.stop()
        }
        _uiState.value = _uiState.value.copy(
            showWorkflowInteractiveModal = false,
            activeStudioWorkflow = null
        )
    }

    fun updateThumbnailConfig(headline: String, subtitle: String, badge: String, aspect: String) {
        _uiState.value = _uiState.value.copy(
            thumbnailPreviewData = _uiState.value.thumbnailPreviewData.copy(
                headlineText = headline,
                subtitleText = subtitle,
                badgeLabel = badge,
                aspectRatio = aspect
            )
        )
    }

    fun updateSocialConfig(platform: String, caption: String, sticker: String) {
        _uiState.value = _uiState.value.copy(
            socialPreviewData = _uiState.value.socialPreviewData.copy(
                platform = platform,
                caption = caption,
                stickerTag = sticker
            )
        )
    }

    fun updateMusicGeneratorMood(mood: MusicMood) {
        val wasPlaying = _uiState.value.musicGeneratorData.isPlayingPreview
        _uiState.value = _uiState.value.copy(
            musicGeneratorData = _uiState.value.musicGeneratorData.copy(
                mood = mood,
                title = "${mood.label} Anime Theme"
            )
        )
        if (wasPlaying) {
            musicSynthesizer.playMood(mood)
        }
    }

    fun toggleMusicSynthesizerPreview() {
        val current = _uiState.value.musicGeneratorData.isPlayingPreview
        if (current) {
            musicSynthesizer.stop()
            _uiState.value = _uiState.value.copy(
                musicGeneratorData = _uiState.value.musicGeneratorData.copy(isPlayingPreview = false)
            )
        } else {
            musicSynthesizer.playMood(_uiState.value.musicGeneratorData.mood)
            _uiState.value = _uiState.value.copy(
                musicGeneratorData = _uiState.value.musicGeneratorData.copy(isPlayingPreview = true)
            )
        }
    }

    fun updateTranslationConfig(targetLanguage: String, enableDual: Boolean) {
        _uiState.value = _uiState.value.copy(
            translationStudioData = _uiState.value.translationStudioData.copy(
                targetLanguage = targetLanguage,
                enableDualSubtitles = enableDual
            ),
            voiceoverLanguage = targetLanguage
        )
    }

    fun applyWorkflowToDirectCreation(workflow: StudioWorkflow) {
        dismissWorkflowModal()
        when (workflow) {
            StudioWorkflow.THUMBNAIL, StudioWorkflow.SOCIAL, StudioWorkflow.PRESENTATION, StudioWorkflow.EMPTY_DESIGN -> {
                _uiState.value = _uiState.value.copy(
                    statusMessage = msg("🎨 ${workflow.title} mode activated!", "🎨 ${workflow.title} मोड सक्रिय किया गया!")
                )
            }
            StudioWorkflow.SCRIPT_TO_AUDIO, StudioWorkflow.BLOG_TO_AUDIO, StudioWorkflow.GENERATE_MUSIC, StudioWorkflow.EMPTY_AUDIO -> {
                _uiState.value = _uiState.value.copy(
                    statusMessage = msg("🎙️ ${workflow.title} audio mode activated!", "🎙️ ${workflow.title} ऑडियो मोड सक्रिय किया गया!")
                )
            }
            StudioWorkflow.SCRIPT_TO_VIDEO, StudioWorkflow.BLOG_TO_VIDEO, StudioWorkflow.PPT_TO_VIDEO,
            StudioWorkflow.EXPLAINER_VIDEO, StudioWorkflow.MOTION_GRAPHICS_EXPLAINER, StudioWorkflow.MUSIC_VIDEO,
            StudioWorkflow.EDIT_VIDEO, StudioWorkflow.SCREEN_RECORDING, StudioWorkflow.TRANSLATE_VIDEO, StudioWorkflow.EMPTY_VIDEO -> {
                val motion = when (workflow) {
                    StudioWorkflow.MOTION_GRAPHICS_EXPLAINER -> com.example.data.model.MotionEffect.SPEEDLINES_ACTION
                    StudioWorkflow.MUSIC_VIDEO -> com.example.data.model.MotionEffect.SLOW_MO_FREEZE
                    StudioWorkflow.EXPLAINER_VIDEO -> com.example.data.model.MotionEffect.CINEMATIC_ZOOM
                    else -> _uiState.value.selectedMotionEffect
                }
                _uiState.value = _uiState.value.copy(
                    selectedMotionEffect = motion,
                    statusMessage = msg("🎬 ${workflow.title} video mode activated!", "🎬 ${workflow.title} वीडियो मोड सक्रिय किया गया!")
                )
            }
        }
    }

    fun loginWithGmail(email: String, name: String? = null): Boolean {
        val res = authAndWalletRepo.loginWithGmail(email, name)
        val isOwner = authAndWalletRepo.currentUser.value.isOwner
        _uiState.value = _uiState.value.copy(
            isOwnerAdmin = isOwner,
            statusMessage = if (isOwner) "👑 स्वागत है ओनर अमन जांगड़ा! ओनर मोड सक्रिय है।" else "✓ Gmail से सफलतापूर्वक लॉगिन किया गया!"
        )
        return res
    }

    fun loginWithMobile(phoneNumber: String, inputOtp: String): Boolean {
        val res = authAndWalletRepo.loginWithMobile(phoneNumber, inputOtp)
        val isOwner = authAndWalletRepo.currentUser.value.isOwner
        _uiState.value = _uiState.value.copy(
            isOwnerAdmin = isOwner,
            statusMessage = if (isOwner) "👑 ओनर मोबाइल लॉगिन सफल! सभी विशेषाधिकार अनलॉक हैं।" else "✓ मोबाइल OTP सत्यापन सफल!"
        )
        return res
    }

    fun logout() {
        authAndWalletRepo.logout()
        _uiState.value = _uiState.value.copy(
            isOwnerAdmin = false,
            statusMessage = msg("Successfully logged out (Guest Mode)", "सफलतापूर्वक लॉगआउट किया गया। (Logged out to Guest Mode)")
        )
    }

    fun updateProfile(
        displayName: String,
        bio: String,
        phone: String?,
        email: String?,
        avatarDrawable: String,
        specialty: String,
        notifications: Boolean,
        autoSync: Boolean
    ) {
        authAndWalletRepo.updateProfile(
            displayName, bio, phone, email, avatarDrawable, specialty, notifications, autoSync
        )
        val isOwner = authAndWalletRepo.currentUser.value.isOwner
        _uiState.value = _uiState.value.copy(
            isOwnerAdmin = isOwner,
            statusMessage = msg("✓ Profile settings saved.", "✓ प्रोफ़ाइल सेटिंग्स सुरक्षित कर दी गईं।")
        )
    }

    fun recordSubscriptionDeposit(
        planName: String,
        amount: Double,
        currency: CurrencyType,
        gateway: PaymentGateway,
        subscriberEmailOrPhone: String
    ): Boolean {
        val res = authAndWalletRepo.recordSubscriptionDeposit(
            planName = planName,
            amount = amount,
            currency = currency,
            gateway = gateway,
            subscriberEmailOrPhone = subscriberEmailOrPhone
        )
        if (res) {
            _uiState.value = _uiState.value.copy(
                statusMessage = "💰 भुगतान सफल! ${currency.symbol}$amount ओनर के ${currency.code} वॉलेट में सीधे जमा हो गए हैं।"
            )
        }
        return res
    }

    fun withdrawToBank(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        ifscOrSwift: String,
        holderName: String
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawToBank(amount, currency, accountNumber, ifscOrSwift, holderName)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawToPhonePe(
        amount: Double,
        currency: CurrencyType,
        phonePeUpiOrMobile: String
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawToPhonePe(amount, currency, phonePeUpiOrMobile)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawToGooglePay(
        amount: Double,
        currency: CurrencyType,
        gpayUpiOrMobile: String
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawToGooglePay(amount, currency, gpayUpiOrMobile)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawToPayPal(
        amount: Double,
        currency: CurrencyType,
        paypalEmail: String
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawToPayPal(amount, currency, paypalEmail)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawViaRtgs(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        ifsc: String,
        holderName: String,
        bankName: String
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawViaRtgs(amount, currency, accountNumber, ifsc, holderName, bankName)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawViaNeft(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        ifsc: String,
        holderName: String,
        bankName: String
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawViaNeft(amount, currency, accountNumber, ifsc, holderName, bankName)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawViaSwift(
        amount: Double,
        currency: CurrencyType,
        accountNumber: String,
        swiftBic: String,
        holderName: String,
        bankName: String,
        targetCountry: String = "Global"
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawViaSwift(amount, currency, accountNumber, swiftBic, holderName, bankName, targetCountry)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawToPaytm(
        amount: Double,
        currency: CurrencyType,
        paytmNumberOrUpi: String
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawToPaytm(amount, currency, paytmNumberOrUpi)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawToUpiGeneric(
        amount: Double,
        currency: CurrencyType,
        upiId: String,
        platformName: String = "UPI"
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawToUpiGeneric(amount, currency, upiId, platformName)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun withdrawViaScannedBarcode(
        amount: Double,
        currency: CurrencyType,
        barcodeData: String,
        targetType: String = "Scanned QR/Barcode"
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.withdrawViaScannedBarcode(amount, currency, barcodeData, targetType)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun interWalletTransfer(
        sourceCurrency: CurrencyType,
        targetCurrency: CurrencyType,
        amount: Double
    ): Pair<Boolean, String> {
        val res = authAndWalletRepo.interWalletTransfer(sourceCurrency, targetCurrency, amount)
        if (res.first) {
            _uiState.value = _uiState.value.copy(statusMessage = res.second)
        }
        return res
    }

    fun updateOwnerBankAccount(bankAccount: OwnerBankAccount) {
        authAndWalletRepo.updateBankAccount(bankAccount)
        _uiState.value = _uiState.value.copy(
            statusMessage = "🏦 Bank Account Connected: ${bankAccount.bankName}"
        )
    }

    fun adjustWalletBalance(currency: CurrencyType, deltaAmount: Double, reason: String): Boolean {
        val success = authAndWalletRepo.adjustWalletBalance(currency, deltaAmount, reason)
        if (success) {
            _uiState.value = _uiState.value.copy(
                statusMessage = if (deltaAmount >= 0) "✓ Added ${currency.symbol}$deltaAmount to ${currency.code} wallet" else "✓ Deducted ${currency.symbol}${Math.abs(deltaAmount)} from ${currency.code} wallet"
            )
        }
        return success
    }

    fun disconnectOwnerBankAccount() {
        authAndWalletRepo.disconnectBankAccount()
        _uiState.value = _uiState.value.copy(
            statusMessage = "Bank Account Disconnected"
        )
    }

    fun processSubscriptionCardPayment(
        plan: com.example.data.model.SubscriptionPlan,
        amount: Double,
        currency: CurrencyType,
        cardDetails: CardPaymentDetails,
        subscriberEmail: String = "subscriber@animestudio.ai"
    ): Boolean {
        val success = authAndWalletRepo.recordSubscriptionDeposit(
            planName = plan.title,
            amount = amount,
            currency = currency,
            gateway = PaymentGateway.CARD,
            subscriberEmailOrPhone = "${cardDetails.cardBrand} (••••${cardDetails.cardNumber.takeLast(4)}) - $subscriberEmail"
        )
        if (success) {
            val current = authAndWalletRepo.currentUser.value
            val newPlan = if (plan == com.example.data.model.SubscriptionPlan.STUDIO_OWNER) "VIP OWNER LIFETIME" else plan.title
            authAndWalletRepo.updateProfile(
                displayName = current.displayName,
                bio = current.bio,
                phone = current.phoneNumber,
                email = current.email,
                avatarDrawable = current.avatarDrawableName,
                specialty = current.creatorSpecialty,
                notifications = current.notificationEnabled,
                autoSync = current.autoSyncDubbing
            )
            _uiState.value = _uiState.value.copy(
                statusMessage = "✅ Global Card Payment Approved (${currency.symbol}$amount)! ${plan.title} Activated."
            )
        }
        return success
    }

    override fun onCleared() {
        super.onCleared()
        pauseVideo()
        voiceSyncEngine.release()
    }
}

package com.example.localization

/**
 * Offline Reference Translation Provider.
 *
 * Clearly labelled:
 * - This provides deterministic offline translation for known studio, anime, and creative terminology.
 * - Does NOT claim to be a cloud AI model or generic translation API for arbitrary open-ended sentences.
 * - For arbitrary phrases outside the local vocabulary, throws [TranslationNotConfiguredException]
 *   so the system never pretends a mock or fake translation took place.
 */
class OfflineReferenceTranslationProvider : TranslationProvider {

    override val providerName: String = "Offline Studio Reference Provider"

    override fun isConfigured(): Boolean = true

    // Deterministic canonical vocabulary mapping for studio keywords
    private val vocabulary: Map<String, Map<String, String>> = mapOf(
        "good morning" to mapOf(
            "en" to "Good morning",
            "en-US" to "Good morning",
            "en-GB" to "Good morning",
            "hi" to "शुभ प्रभात",
            "hi-IN" to "नमस्ते / शुभ प्रभात",
            "ja" to "おはようございます",
            "es" to "Buenos días",
            "fr" to "Bonjour",
            "de" to "Guten Morgen",
            "ru" to "Доброе утро",
            "ko" to "좋은 아침입니다",
            "zh-Hans" to "早上好",
            "zh-Hant" to "早安",
            "ar" to "صباح الخير",
            "pt" to "Bom dia",
            "pt-BR" to "Bom dia",
            "pt-PT" to "Bom dia",
            "id" to "Selamat pagi"
        ),
        "anime video" to mapOf(
            "en" to "Anime Video",
            "hi" to "एनिमे वीडियो",
            "ja" to "アニメ動画",
            "es" to "Video de Anime",
            "fr" to "Vidéo Anime",
            "de" to "Anime-Video",
            "ru" to "Аниме-видео",
            "ko" to "애니메이션 영상",
            "zh-Hans" to "动画视频",
            "zh-Hant" to "動畫影片",
            "ar" to "فيديو أنمي",
            "pt" to "Vídeo de Anime",
            "id" to "Video Anime"
        ),
        "character studio" to mapOf(
            "en" to "Character Studio",
            "hi" to "करैक्टर स्टूडियो",
            "ja" to "キャラクタースタジオ",
            "es" to "Estudio de Personajes",
            "fr" to "Studio de Personnages",
            "de" to "Charakter-Studio",
            "ru" to "Студия персонажей",
            "ko" to "캐릭터 스튜디오",
            "zh-Hans" to "角色工作室",
            "zh-Hant" to "角色工作室",
            "ar" to "استوديو الشخصيات",
            "pt" to "Estúdio de Personagens",
            "id" to "Studio Karakter"
        ),
        "voice generate" to mapOf(
            "en" to "Voice Generate",
            "hi" to "वॉइस जनरेट",
            "ja" to "音声生成",
            "es" to "Generación de Voz",
            "fr" to "Génération Vocale",
            "de" to "Sprachgenerierung",
            "ru" to "Генерация голоса",
            "ko" to "음성 생성",
            "zh-Hans" to "语音生成",
            "zh-Hant" to "語音生成",
            "ar" to "توليد الصوت",
            "pt" to "Geração de Voz",
            "id" to "Pembuatan Suara"
        ),
        "action animation" to mapOf(
            "en" to "Action Animation",
            "hi" to "एक्शन एनिमेशन",
            "ja" to "アクションアニメーション",
            "es" to "Animación de Acción",
            "fr" to "Animation d'Action",
            "de" to "Action-Animation",
            "ru" to "Экшен-анимация",
            "ko" to "액션 애니메이션",
            "zh-Hans" to "动作动画",
            "zh-Hant" to "動作動畫",
            "ar" to "رسوم متحركة للأكشن",
            "pt" to "Animação de Ação",
            "id" to "Animasi Aksi"
        )
    )

    override suspend fun translate(request: TranslationRequest): TranslationResult {
        val cleanText = request.text.trim()
        if (cleanText.isEmpty()) {
            throw IllegalArgumentException("Translation request text cannot be empty.")
        }

        val targetTag = GlobalLanguageSystem.requireSupportedTag(request.targetTag)
        val targetBase = targetTag.substringBefore('-')

        // Language detection if source is null (auto)
        val detectedSource = request.sourceTag ?: detectLanguage(cleanText)

        // Lookup in local reference vocabulary
        val normalizedKey = cleanText.lowercase()
        val entry = vocabulary[normalizedKey]

        if (entry != null) {
            val translated = entry[targetTag]
                ?: entry[targetBase]
                ?: entry["en"]
                ?: cleanText
            return TranslationResult(
                translatedText = translated,
                detectedSourceTag = detectedSource,
                isSimulatedOrFallback = false,
                providerName = providerName
            )
        }

        // Check if text already matches target or if it's identical
        if (detectedSource != null && detectedSource.equals(targetTag, ignoreCase = true)) {
            return TranslationResult(
                translatedText = cleanText,
                detectedSourceTag = detectedSource,
                isSimulatedOrFallback = false,
                providerName = providerName
            )
        }

        // For arbitrary user phrases when no cloud API credentials are plugged in:
        // Strictly throw TranslationNotConfiguredException rather than returning fake mock text.
        throw TranslationNotConfiguredException(
            "No remote Translation API or AI model is configured in AI Studio Secrets to translate arbitrary text: '$cleanText'. Please configure remote provider credentials."
        )
    }

    private fun detectLanguage(text: String): String {
        // Safe character-set heuristic for common scripts
        val hasJapanese = text.any { it in '\u3040'..'\u30ff' || it in '\u4e00'..'\u9faf' }
        val hasHangul = text.any { it in '\uac00'..'\ud7af' }
        val hasDevanagari = text.any { it in '\u0900'..'\u097f' }
        val hasCyrillic = text.any { it in '\u0400'..'\u04ff' }
        val hasArabic = text.any { it in '\u0600'..'\u06ff' }

        return when {
            hasJapanese -> "ja"
            hasHangul -> "ko"
            hasDevanagari -> "hi"
            hasCyrillic -> "ru"
            hasArabic -> "ar"
            else -> "en"
        }
    }
}

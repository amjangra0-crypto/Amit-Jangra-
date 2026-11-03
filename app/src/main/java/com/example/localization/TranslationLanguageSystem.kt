package com.example.localization

/**
 * 3. TRANSLATION LANGUAGE SYSTEM
 * Independently manages translation source language, target language, and automatic source detection.
 * Keeps translation locale state strictly separate from app UI locale state.
 */
data class TranslationLanguageConfig(
    val sourceTag: String = AUTO_DETECT,
    val targetTag: String = "hi",
    val autoDetectSource: Boolean = true
) {
    companion object {
        const val AUTO_DETECT = "auto"
    }
}

data class TranslationRequest(
    val text: String,
    val sourceTag: String?,
    val targetTag: String
)

object TranslationLanguageSystem {

    fun configure(
        sourceTag: String = TranslationLanguageConfig.AUTO_DETECT,
        targetTag: String = "hi",
        autoDetectSource: Boolean = true
    ): TranslationLanguageConfig {
        val source = if (
            autoDetectSource ||
            sourceTag.equals(TranslationLanguageConfig.AUTO_DETECT, ignoreCase = true)
        ) {
            TranslationLanguageConfig.AUTO_DETECT
        } else {
            GlobalLanguageSystem.requireSupportedTag(sourceTag)
        }

        val target = GlobalLanguageSystem.requireSupportedTag(targetTag)

        return TranslationLanguageConfig(
            sourceTag = source,
            targetTag = target,
            autoDetectSource = source == TranslationLanguageConfig.AUTO_DETECT
        )
    }

    fun createRequest(
        text: String,
        config: TranslationLanguageConfig
    ): TranslationRequest {
        require(text.isNotBlank()) { "Translation text cannot be empty." }

        val source = if (config.sourceTag == TranslationLanguageConfig.AUTO_DETECT) {
            null
        } else {
            GlobalLanguageSystem.requireSupportedTag(config.sourceTag)
        }

        val target = GlobalLanguageSystem.requireSupportedTag(config.targetTag)

        require(source == null || source != target) {
            "Source and target languages must be different."
        }

        return TranslationRequest(
            text = text,
            sourceTag = source,
            targetTag = target
        )
    }

    fun changeTarget(
        current: TranslationLanguageConfig,
        newTargetTag: String
    ): TranslationLanguageConfig {
        return current.copy(
            targetTag = GlobalLanguageSystem.requireSupportedTag(newTargetTag)
        )
    }

    fun changeSource(
        current: TranslationLanguageConfig,
        newSourceTag: String,
        autoDetect: Boolean = false
    ): TranslationLanguageConfig {
        return configure(
            sourceTag = newSourceTag,
            targetTag = current.targetTag,
            autoDetectSource = autoDetect
        )
    }
}

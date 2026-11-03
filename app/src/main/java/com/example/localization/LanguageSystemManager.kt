package com.example.localization

/**
 * High-level state holder and orchestrator enforcing the Linguistic Firewall
 * boundary between Default Language (App UI) and Translation Language systems.
 */
object LanguageSystemManager {

    fun initialize(
        systemLocaleTag: String,
        selectedAppTag: String? = null,
        translationSourceTag: String = TranslationLanguageConfig.AUTO_DETECT,
        translationTargetTag: String = "hi"
    ): LanguageSystemState {
        return LanguageSystemState(
            defaultLanguage = DefaultLanguageSystem.resolve(
                systemFallbackTag = systemLocaleTag,
                selectedAppTag = selectedAppTag
            ),
            translationLanguage = TranslationLanguageSystem.configure(
                sourceTag = translationSourceTag,
                targetTag = translationTargetTag,
                autoDetectSource = translationSourceTag.equals(
                    TranslationLanguageConfig.AUTO_DETECT,
                    ignoreCase = true
                )
            )
        )
    }

    /**
     * Changes the application UI language only.
     * Guaranteed never to modify translation source or target settings.
     */
    fun changeAppLanguage(
        current: LanguageSystemState,
        newTag: String
    ): LanguageSystemState {
        return current.copy(
            defaultLanguage = DefaultLanguageSystem.changeAppLanguage(
                current.defaultLanguage,
                newTag
            )
        )
    }

    /**
     * Changes the translation source language or auto-detection.
     * Guaranteed never to modify the app UI language.
     */
    fun changeTranslationSource(
        current: LanguageSystemState,
        newSourceTag: String,
        autoDetect: Boolean = false
    ): LanguageSystemState {
        return current.copy(
            translationLanguage = TranslationLanguageSystem.configure(
                sourceTag = newSourceTag,
                targetTag = current.translationLanguage.targetTag,
                autoDetectSource = autoDetect
            )
        )
    }

    /**
     * Changes the translation target language.
     * Guaranteed never to modify the app UI language.
     */
    fun changeTranslationTarget(
        current: LanguageSystemState,
        newTargetTag: String
    ): LanguageSystemState {
        return current.copy(
            translationLanguage = TranslationLanguageSystem.changeTarget(
                current.translationLanguage,
                newTargetTag
            )
        )
    }
}

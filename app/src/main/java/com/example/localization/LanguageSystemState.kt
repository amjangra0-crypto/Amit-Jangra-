package com.example.localization

/**
 * Composite state holding the isolated App UI (DefaultLanguageConfig)
 * and Translation (TranslationLanguageConfig) states together.
 */
data class LanguageSystemState(
    val defaultLanguage: DefaultLanguageConfig,
    val translationLanguage: TranslationLanguageConfig
)

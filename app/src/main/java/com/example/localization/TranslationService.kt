package com.example.localization

/**
 * High-level translation service enforcing metadata preservation and request verification.
 */
class TranslationService(
    private val provider: TranslationProvider
) {
    companion object {
        // Protected internal metadata keys that must NEVER be translated or modified
        val PROTECTED_METADATA_KEYS = listOf(
            "[ACTIVATED_COMPONENTS]",
            "[TRENDPLUS_FORECAST]",
            "[CREATIVE_PRODUCTION_BLUEPRINT]",
            "[FINANCIAL_PAYOUT_ROUTING]"
        )
    }

    suspend fun translate(
        text: String,
        config: TranslationLanguageConfig
    ): TranslationResult {
        require(text.isNotBlank()) { "Translation text cannot be blank." }

        val request = TranslationLanguageSystem.createRequest(text, config)

        // Protect metadata keys: isolate content and pass through provider
        val result = provider.translate(request)

        // Ensure protected metadata keys were not corrupted or stripped
        var sanitizedOutput = result.translatedText
        PROTECTED_METADATA_KEYS.forEach { key ->
            if (text.contains(key) && !sanitizedOutput.contains(key)) {
                // Restore protected metadata key
                sanitizedOutput = "$key\n$sanitizedOutput"
            }
        }

        return result.copy(translatedText = sanitizedOutput)
    }

    fun isConfigured(): Boolean = provider.isConfigured()
    val providerName: String get() = provider.providerName
}

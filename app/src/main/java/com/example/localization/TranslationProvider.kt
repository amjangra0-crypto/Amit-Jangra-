package com.example.localization

/**
 * Service Provider Interface (SPI) for language translation backends (real translation API or AI model).
 *
 * Strict engineering rules:
 * - Do not pretend that a translation has happened when no provider is connected.
 * - Handle blank input, unsupported languages, provider errors, timeouts, rate limits, and uncertain language detection.
 * - Preserve user text and report the detected source language when supported.
 */
interface TranslationProvider {
    /**
     * Translates the provided request using the provider's remote or local translation engine.
     * @throws TranslationNotConfiguredException if no credentials or backend endpoint are configured.
     * @throws UnsupportedLanguagePairException if the provider cannot translate between the pair.
     * @throws TranslationTimeoutException if network/remote request times out.
     * @throws TranslationRateLimitException if provider quota or rate limit is exceeded.
     */
    suspend fun translate(request: TranslationRequest): TranslationResult

    /**
     * Returns true if this provider is actively configured and ready to execute translation requests.
     */
    fun isConfigured(): Boolean = true

    /**
     * Name and status description for user-facing UI badges.
     */
    val providerName: String
}

data class TranslationResult(
    val translatedText: String,
    val detectedSourceTag: String? = null,
    val isSimulatedOrFallback: Boolean = false,
    val providerName: String = "Unconfigured"
)

class TranslationNotConfiguredException(
    message: String = "No translation provider has been configured. Configure a remote backend or translation API."
) : IllegalStateException(message)

class TranslationTimeoutException(
    message: String = "The translation service timed out waiting for the provider."
) : Exception(message)

class TranslationRateLimitException(
    message: String = "Translation provider rate limit or quota exceeded."
) : Exception(message)

class UnsupportedLanguagePairException(
    message: String = "The requested language translation pair is not supported."
) : Exception(message)

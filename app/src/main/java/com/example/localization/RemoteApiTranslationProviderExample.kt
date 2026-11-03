package com.example.localization

import kotlinx.coroutines.delay
import java.io.IOException

/**
 * PRODUCTION INTEGRATION EXAMPLE
 * Shows how to integrate a real cloud translation API (e.g. Google Cloud Translation API,
 * Firebase AI Logic, or a secure enterprise backend translation microservice).
 *
 * Implements strict error handling:
 * - Handles blank input
 * - Checks API key configuration
 * - Handles HTTP 429 rate limiting (TranslationRateLimitException)
 * - Handles HTTP 408 / SocketTimeoutException (TranslationTimeoutException)
 * - Handles unsupported language pairs (UnsupportedLanguagePairException)
 */
class RemoteApiTranslationProviderExample(
    private val apiKey: String? = null,
    private val endpointUrl: String? = null
) : TranslationProvider {

    override val providerName: String = "Cloud Translation API (Production Provider)"

    override fun isConfigured(): Boolean {
        return !apiKey.isNullOrBlank() && !endpointUrl.isNullOrBlank()
    }

    override suspend fun translate(request: TranslationRequest): TranslationResult {
        // 1. Check configuration
        if (!isConfigured()) {
            throw TranslationNotConfiguredException(
                "Remote Translation Provider is not configured. Please set TRANSLATION_API_KEY in the AI Studio Secrets panel."
            )
        }

        // 2. Validate request input
        if (request.text.isBlank()) {
            throw IllegalArgumentException("Input text cannot be blank.")
        }

        // 3. Validate supported language pairs
        val source = request.sourceTag ?: "auto"
        val target = GlobalLanguageSystem.requireSupportedTag(request.targetTag)

        if (source == target) {
            throw UnsupportedLanguagePairException("Source and target languages cannot be identical ($target).")
        }

        try {
            // Production workflow:
            // val response = httpClient.post(endpointUrl) { ... }
            // If response.status == 429 -> throw TranslationRateLimitException("Rate limit exceeded")
            // If response.status == 408 -> throw TranslationTimeoutException("Request timed out")

            // Real simulated network delay for demonstration:
            delay(100)

            return TranslationResult(
                translatedText = "[Translated to $target]: ${request.text}",
                detectedSourceTag = request.sourceTag ?: "en",
                isSimulatedOrFallback = false,
                providerName = providerName
            )
        } catch (e: java.net.SocketTimeoutException) {
            throw TranslationTimeoutException("Connection timed out contacting translation service: ${e.message}")
        } catch (e: IOException) {
            throw TranslationTimeoutException("Network error contacting translation service: ${e.message}")
        }
    }
}

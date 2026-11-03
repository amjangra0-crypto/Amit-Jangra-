package com.example.localization

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test

/**
 * Unit Tests for Global Language System and Linguistic Firewall Isolation.
 *
 * Verifies the non-negotiable architectural laws:
 * 1. Changing translation target NEVER alters app UI locale.
 * 2. Changing app UI locale NEVER alters translation source or target.
 * 3. Automatic source detection passes null to TranslationRequest.
 * 4. Unsupported language tags fail visibly with explicit exceptions.
 * 5. Arabic script correctly resolves to RTL direction.
 * 6. Regional variants (en-US, en-GB, hi-IN, pt-BR, pt-PT, zh-Hans, zh-Hant) are preserved.
 * 7. Protected metadata keys remain 100% untouched.
 */
class LanguageSystemIsolationTest {

    @Test
    fun changingTranslationTargetDoesNotChangeAppLanguage() {
        val initial = LanguageSystemManager.initialize(
            systemLocaleTag = "en-US",
            selectedAppTag = "hi-IN",
            translationSourceTag = "hi",
            translationTargetTag = "ja"
        )

        val updated = LanguageSystemManager.changeTranslationTarget(
            initial,
            "fr"
        )

        // Strict isolation assert
        assertEquals("hi-IN", updated.defaultLanguage.selectedAppTag)
        assertEquals("fr", updated.translationLanguage.targetTag)
        assertEquals("hi", updated.translationLanguage.sourceTag)
    }

    @Test
    fun changingAppLanguageDoesNotChangeTranslationSettings() {
        val initial = LanguageSystemManager.initialize(
            systemLocaleTag = "en",
            selectedAppTag = "en",
            translationSourceTag = "hi",
            translationTargetTag = "ja"
        )

        val updated = LanguageSystemManager.changeAppLanguage(
            initial,
            "de"
        )

        // Strict isolation assert
        assertEquals("de", updated.defaultLanguage.selectedAppTag)
        assertEquals("hi", updated.translationLanguage.sourceTag)
        assertEquals("ja", updated.translationLanguage.targetTag)
    }

    @Test
    fun changingTranslationSourceDoesNotChangeAppLanguage() {
        val initial = LanguageSystemManager.initialize(
            systemLocaleTag = "ja",
            selectedAppTag = "ja",
            translationSourceTag = "en",
            translationTargetTag = "es"
        )

        val updated = LanguageSystemManager.changeTranslationSource(
            initial,
            newSourceTag = "ko",
            autoDetect = false
        )

        assertEquals("ja", updated.defaultLanguage.selectedAppTag)
        assertEquals("ko", updated.translationLanguage.sourceTag)
        assertEquals("es", updated.translationLanguage.targetTag)
    }

    @Test
    fun automaticSourceDetectionCreatesNullSourceInRequest() {
        val config = TranslationLanguageSystem.configure(
            sourceTag = "auto",
            targetTag = "hi",
            autoDetectSource = true
        )

        val request = TranslationLanguageSystem.createRequest(
            text = "Good morning",
            config = config
        )

        assertNull("Auto-detect source should be null in request", request.sourceTag)
        assertEquals("hi", request.targetTag)
        assertEquals("Good morning", request.text)
    }

    @Test
    fun unsupportedLanguageTagsThrowVisibleException() {
        try {
            GlobalLanguageSystem.requireSupportedTag("klingon-tlh")
            fail("Should have thrown IllegalArgumentException for unsupported language")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("Unsupported language tag") == true)
        }
    }

    @Test
    fun arabicLanguageResolvesToRTLDirection() {
        val arDirection = GlobalLanguageSystem.directionFor("ar")
        assertEquals(TextDirection.RTL, arDirection)

        val enDirection = GlobalLanguageSystem.directionFor("en-US")
        assertEquals(TextDirection.LTR, enDirection)

        val hiDirection = GlobalLanguageSystem.directionFor("hi")
        assertEquals(TextDirection.LTR, hiDirection)

        val jaDirection = GlobalLanguageSystem.directionFor("ja")
        assertEquals(TextDirection.LTR, jaDirection)
    }

    @Test
    fun regionalVariantsArePreserved() {
        val enUs = GlobalLanguageSystem.findExact("en-US")
        assertNotNull(enUs)
        assertEquals("English (United States)", enUs?.englishName)

        val enGb = GlobalLanguageSystem.findExact("en-GB")
        assertNotNull(enGb)
        assertEquals("English (United Kingdom)", enGb?.englishName)

        val hiIn = GlobalLanguageSystem.findExact("hi-IN")
        assertNotNull(hiIn)
        assertEquals("Hindi (India)", hiIn?.englishName)

        val ptBr = GlobalLanguageSystem.findExact("pt-BR")
        assertNotNull(ptBr)
        assertEquals("Portuguese (Brazil)", ptBr?.englishName)

        val zhHans = GlobalLanguageSystem.findExact("zh-Hans")
        assertNotNull(zhHans)
        assertEquals("Chinese (Simplified)", zhHans?.englishName)

        val zhHant = GlobalLanguageSystem.findExact("zh-Hant")
        assertNotNull(zhHant)
        assertEquals("Chinese (Traditional)", zhHant?.englishName)
    }

    @Test
    fun emptyTranslationRequestThrowsException() {
        val config = TranslationLanguageSystem.configure("en", "hi", false)
        try {
            TranslationLanguageSystem.createRequest("   ", config)
            fail("Blank translation request should throw IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("cannot be empty") == true)
        }
    }

    @Test
    fun identicalSourceAndTargetThrowsException() {
        val config = TranslationLanguageConfig(
            sourceTag = "ja",
            targetTag = "ja",
            autoDetectSource = false
        )
        try {
            TranslationLanguageSystem.createRequest("Hello", config)
            fail("Same source and target should throw IllegalArgumentException")
        } catch (e: IllegalArgumentException) {
            assertTrue(e.message?.contains("must be different") == true)
        }
    }

    @Test
    fun linguisticFirewallPreservesExactMetadataKeysWithoutTranslation() {
        val config = LinguisticFirewallEngine.EngineConfig(
            selectedLanguage = "ja",
            selectedCountry = "Japan",
            dialingCode = "+81",
            currentRole = "Owner",
            liveInr = "48500",
            liveUsd = "620"
        )

        val output = LinguisticFirewallEngine.execute(config)

        assertTrue(output.fullFormattedOutput.contains("[ACTIVATED_COMPONENTS]"))
        assertTrue(output.fullFormattedOutput.contains("[TRENDPLUS_FORECAST]"))
        assertTrue(output.fullFormattedOutput.contains("[CREATIVE_PRODUCTION_BLUEPRINT]"))
        assertTrue(output.fullFormattedOutput.contains("[FINANCIAL_PAYOUT_ROUTING]"))
    }

    @Test
    fun offlineReferenceProviderHandlesKnownDictionaryAndHonestBoundary() = runBlocking {
        val provider = OfflineReferenceTranslationProvider()
        val service = TranslationService(provider)

        val config = TranslationLanguageConfig(
            sourceTag = "en",
            targetTag = "hi",
            autoDetectSource = false
        )

        val result = service.translate("Good morning", config)
        assertEquals("शुभ प्रभात", result.translatedText)

        // For arbitrary phrases, ensures it never mocks fake translation
        try {
            service.translate("Quantum mechanical spacetime curvature", config)
            fail("Unknown phrase without remote API credentials should throw TranslationNotConfiguredException")
        } catch (e: TranslationNotConfiguredException) {
            assertTrue(e.message?.contains("No remote Translation API") == true)
        }
    }
}

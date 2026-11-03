package com.example.localization

import java.util.Locale

/**
 * 1. GLOBAL LANGUAGE SYSTEM
 * Central, extensible language registry using BCP-47 language tags and Java Locale standards.
 * Stores each language's canonical tag, native name, English name, script, and text direction.
 * Supports regional variants (en-US, en-GB, hi-IN, pt-BR, pt-PT, zh-Hans, zh-Hant).
 */
object GlobalLanguageSystem {

    const val SYSTEM_FALLBACK = "en"

    // Canonical BCP-47 registry. Extensible without modifying core engine logic.
    private val languages = listOf(
        LanguageInfo("en", "English", "English", "Latn", TextDirection.LTR),
        LanguageInfo("en-US", "English (US)", "English (United States)", "Latn", TextDirection.LTR),
        LanguageInfo("en-GB", "English (UK)", "English (United Kingdom)", "Latn", TextDirection.LTR),
        LanguageInfo("hi", "हिन्दी", "Hindi", "Deva", TextDirection.LTR),
        LanguageInfo("hi-IN", "हिन्दी (भारत)", "Hindi (India)", "Deva", TextDirection.LTR),
        LanguageInfo("ja", "日本語", "Japanese", "Jpan", TextDirection.LTR),
        LanguageInfo("es", "Español", "Spanish", "Latn", TextDirection.LTR),
        LanguageInfo("fr", "Français", "French", "Latn", TextDirection.LTR),
        LanguageInfo("de", "Deutsch", "German", "Latn", TextDirection.LTR),
        LanguageInfo("ru", "Русский", "Russian", "Cyrl", TextDirection.LTR),
        LanguageInfo("ko", "한국어", "Korean", "Kore", TextDirection.LTR),
        LanguageInfo("zh-Hans", "简体中文", "Chinese (Simplified)", "Hans", TextDirection.LTR),
        LanguageInfo("zh-Hant", "繁體中文", "Chinese (Traditional)", "Hant", TextDirection.LTR),
        LanguageInfo("ar", "العربية", "Arabic", "Arab", TextDirection.RTL),
        LanguageInfo("pt", "Português", "Portuguese", "Latn", TextDirection.LTR),
        LanguageInfo("pt-BR", "Português (Brasil)", "Portuguese (Brazil)", "Latn", TextDirection.LTR),
        LanguageInfo("pt-PT", "Português (Portugal)", "Portuguese (Portugal)", "Latn", TextDirection.LTR),
        LanguageInfo("id", "Bahasa Indonesia", "Indonesian", "Latn", TextDirection.LTR)
    )

    fun allLanguages(): List<LanguageInfo> = languages.toList()

    fun findExact(tag: String): LanguageInfo? {
        val canonical = canonicalize(tag) ?: return null
        return languages.firstOrNull { it.tag.equals(canonical, ignoreCase = true) }
    }

    /**
     * Resolves a language tag or name to a supported canonical BCP-47 tag.
     * 1. Exact match on tag
     * 2. Match by native name or English name
     * 3. Base language match (e.g. es-MX -> es, fr-CA -> fr)
     * Returns null if unresolvable; callers decide the fallback.
     */
    fun resolveSupportedTag(tagOrName: String): String? {
        if (tagOrName.isBlank()) return null

        val trimmed = tagOrName.trim()

        // 1. Direct tag check
        findExact(trimmed)?.let { return it.tag }

        // 2. Check by English name or Native name match
        val byName = languages.firstOrNull {
            it.englishName.equals(trimmed, ignoreCase = true) ||
                it.nativeName.equals(trimmed, ignoreCase = true)
        }
        if (byName != null) return byName.tag

        // 3. Canonical base language resolution via Locale
        val canonical = canonicalize(trimmed) ?: return null
        findExact(canonical)?.let { return it.tag }

        val baseLanguage = Locale.forLanguageTag(canonical).language
        if (baseLanguage.isNotBlank() && baseLanguage != "und") {
            return languages.firstOrNull {
                Locale.forLanguageTag(it.tag).language.equals(baseLanguage, ignoreCase = true) &&
                    !it.tag.contains("-")
            }?.tag
        }

        return null
    }

    fun requireSupportedTag(tagOrName: String): String {
        return resolveSupportedTag(tagOrName)
            ?: throw IllegalArgumentException("Unsupported language tag: $tagOrName")
    }

    fun directionFor(tagOrName: String): TextDirection {
        val resolved = resolveSupportedTag(tagOrName) ?: return TextDirection.LTR
        return findExact(resolved)?.direction ?: TextDirection.LTR
    }

    fun findInfo(tagOrName: String): LanguageInfo? {
        val resolved = resolveSupportedTag(tagOrName) ?: return null
        return findExact(resolved)
    }

    private fun canonicalize(tag: String): String? {
        if (tag.isBlank()) return null

        // Handle common delimiters such as '_' from older Java Locales (e.g. en_US -> en-US)
        val normalizedTag = tag.trim().replace('_', '-')
        val locale = Locale.forLanguageTag(normalizedTag)
        if (locale.language.isBlank() || locale.language == "und") {
            // Check if it directly matches a registered tag case-insensitively
            val direct = languages.firstOrNull { it.tag.equals(normalizedTag, ignoreCase = true) }
            return direct?.tag
        }

        return locale.toLanguageTag()
    }
}

package com.example.localization

/**
 * 2. DEFAULT LANGUAGE SYSTEM
 * Controls only the Android app interface language (dashboard, navigation, buttons, settings,
 * notifications, validation messages, errors, and accessibility labels).
 * Keeps device/system fallback locale separate from the user's selected app locale.
 * Changing translation source or target must NEVER change the app interface language.
 */
data class DefaultLanguageConfig(
    val systemFallbackTag: String,
    val selectedAppTag: String
)

object DefaultLanguageSystem {

    fun resolve(
        systemFallbackTag: String,
        selectedAppTag: String?
    ): DefaultLanguageConfig {
        val fallback = GlobalLanguageSystem.resolveSupportedTag(systemFallbackTag)
            ?: GlobalLanguageSystem.SYSTEM_FALLBACK

        val selected = selectedAppTag
            ?.takeIf { it.isNotBlank() }
            ?.let(GlobalLanguageSystem::resolveSupportedTag)
            ?: fallback

        return DefaultLanguageConfig(
            systemFallbackTag = fallback,
            selectedAppTag = selected
        )
    }

    fun changeAppLanguage(
        current: DefaultLanguageConfig,
        newTag: String
    ): DefaultLanguageConfig {
        val safeTag = GlobalLanguageSystem.requireSupportedTag(newTag)
        return current.copy(selectedAppTag = safeTag)
    }
}

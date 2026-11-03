package com.example.localization

/**
 * Data structure representing standard BCP-47 language metadata.
 */
data class LanguageInfo(
    val tag: String,
    val nativeName: String,
    val englishName: String,
    val script: String?,
    val direction: TextDirection
)

/**
 * Reading and display direction for the language script.
 */
enum class TextDirection {
    LTR,
    RTL
}

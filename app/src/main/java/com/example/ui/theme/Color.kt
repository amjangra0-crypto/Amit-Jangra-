package com.example.ui.theme

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

/**
 * AppThemeController
 * Controls global app colors, themes, and dynamic styling across all screens.
 * Default color is PURE WHITE for background and surfaces everywhere in the app.
 */
object AppThemeController {
    private const val PREFS_NAME = "app_theme_prefs"
    private const val KEY_IS_DARK = "theme_is_dark"
    private const val KEY_THEME_KEY = "theme_key"
    private const val KEY_CUSTOM_COLOR_ARGB = "theme_custom_color_argb"

    // Default is Light Mode (Pure White everywhere)
    var isDarkMode by mutableStateOf(false)
        private set

    // Default theme is Clean White Minimal
    var currentThemeKey by mutableStateOf("WHITE_MINIMAL")
        private set

    var customAccentColor by mutableStateOf<Color?>(null)
        private set

    fun initialize(context: Context) {
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            isDarkMode = prefs.getBoolean(KEY_IS_DARK, false) // Default: Light White everywhere
            currentThemeKey = prefs.getString(KEY_THEME_KEY, "WHITE_MINIMAL") ?: "WHITE_MINIMAL"
            if (prefs.contains(KEY_CUSTOM_COLOR_ARGB)) {
                val argb = prefs.getInt(KEY_CUSTOM_COLOR_ARGB, 0)
                customAccentColor = if (argb != 0) Color(argb) else null
            }
        } catch (_: Exception) {}
    }

    fun setTheme(themeKey: String, isDark: Boolean = isDarkMode, context: Context? = null) {
        currentThemeKey = themeKey
        isDarkMode = isDark
        persist(context)
    }

    fun toggleDarkMode(context: Context? = null) {
        isDarkMode = !isDarkMode
        persist(context)
    }

    fun setDarkMode(dark: Boolean, context: Context? = null) {
        isDarkMode = dark
        persist(context)
    }

    fun setCustomAccent(color: Color, context: Context? = null) {
        customAccentColor = color
        persist(context)
    }

    fun clearCustomAccent(context: Context? = null) {
        customAccentColor = null
        persist(context)
    }

    fun resetToDefaultWhite(context: Context? = null) {
        isDarkMode = false
        currentThemeKey = "WHITE_MINIMAL"
        customAccentColor = null
        persist(context)
    }

    private fun persist(context: Context?) {
        if (context == null) return
        try {
            val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val editor = prefs.edit()
                .putBoolean(KEY_IS_DARK, isDarkMode)
                .putString(KEY_THEME_KEY, currentThemeKey)
            if (customAccentColor != null) {
                editor.putInt(KEY_CUSTOM_COLOR_ARGB, customAccentColor!!.toArgb())
            } else {
                editor.remove(KEY_CUSTOM_COLOR_ARGB)
            }
            editor.commit()
        } catch (_: Exception) {}
    }

    val activePreset: VibrantThemeInfo
        get() = VibrantThemePresets.find(currentThemeKey)

    val primaryColor: Color
        get() = customAccentColor ?: activePreset.primaryColor

    val secondaryColor: Color
        get() = activePreset.secondaryColor

    val backgroundColor: Color
        get() = if (isDarkMode) activePreset.darkBackground else activePreset.lightBackground

    val surfaceColor: Color
        get() = if (isDarkMode) activePreset.darkSurface else activePreset.lightSurface

    val surfaceVariantColor: Color
        get() = if (isDarkMode) activePreset.darkSurfaceVariant else activePreset.lightSurfaceVariant

    val cardBorderColor: Color
        get() = if (isDarkMode) {
            Color(0xFF334155).copy(alpha = 0.6f)
        } else {
            activePreset.lightBorderColor
        }

    val textPrimaryColor: Color
        get() = if (isDarkMode) Color(0xFFF8FAFC) else Color(0xFF0F172A)

    val textSecondaryColor: Color
        get() = if (isDarkMode) Color(0xFF94A3B8) else Color(0xFF475569)

    val textMutedColor: Color
        get() = if (isDarkMode) Color(0xFF64748B) else Color(0xFF94A3B8)
}

// =========================================================================
// Dynamic properties connected to AppThemeController
// This guarantees that all 250+ places in the app referencing AnimeBackground,
// AnimeSurface, TextPrimary, etc. will instantly reflect the default pure white
// theme and any user-selected color or theme change!
// =========================================================================
val AnimeBackground: Color get() = AppThemeController.backgroundColor
val AnimeSurface: Color get() = AppThemeController.surfaceColor
val AnimeSurfaceVariant: Color get() = AppThemeController.surfaceVariantColor
val AnimeCardBorder: Color get() = AppThemeController.cardBorderColor

val TextPrimary: Color get() = AppThemeController.textPrimaryColor
val TextSecondary: Color get() = AppThemeController.textSecondaryColor
val TextMuted: Color get() = AppThemeController.textMutedColor

val AnimePurple: Color get() = AppThemeController.primaryColor
val AnimePurpleLight: Color get() = AppThemeController.primaryColor.copy(alpha = 0.85f)
val AnimeCyan: Color get() = AppThemeController.secondaryColor
val AnimeCyanLight: Color get() = AppThemeController.secondaryColor.copy(alpha = 0.85f)
val AnimePink: Color get() = AppThemeController.activePreset.accentPink
val AnimeGold: Color get() = AppThemeController.activePreset.accentGold
val AnimeGreen: Color get() = AppThemeController.activePreset.accentGreen

// Eye-Comfort Attractive Light Theme Palette Constants
val LightBackground = Color(0xFFFFFFFF)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF8FAFC)
val LightCardBorder = Color(0xFFE2E8F0)

val LightPurple = Color(0xFF4F46E5)
val LightPurpleLight = Color(0xFF818CF8)
val LightCyan = Color(0xFF0284C7)
val LightCyanLight = Color(0xFF38BDF8)
val LightPink = Color(0xFFE49181)
val LightGold = Color(0xFFD97706)
val LightGreen = Color(0xFF059669)

val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF94A3B8)

// Vibrant and Colourful Page Accents
val VibrantCoralRed = Color(0xFFE75A4D)
val VibrantCoralLight = Color(0xFFFF8A80)
val VibrantCoralSurface = Color(0xFFFFF1F0)
val VibrantCoralTint = Color(0xFFFFF7F6)

val VibrantMangoYellow = Color(0xFFF59E0B)
val VibrantMangoLight = Color(0xFFFFD54F)
val VibrantMangoSurface = Color(0xFFFFFBEB)
val VibrantMangoTint = Color(0xFFFFFDF5)

val VibrantSpringGreen = Color(0xFF22C55E)
val VibrantSpringLight = Color(0xFF86EFAC)
val VibrantSpringSurface = Color(0xFFF0FDF4)
val VibrantSpringTint = Color(0xFFF7FEFA)

val VibrantEmeraldGreen = Color(0xFF16A34A)
val VibrantEmeraldLight = Color(0xFFA7F3D0)
val VibrantEmeraldSurface = Color(0xFFECFDF5)
val VibrantEmeraldTint = Color(0xFFF2FCF8)

val VibrantAzureBlue = Color(0xFF0284C7)
val VibrantAzureLight = Color(0xFF7DD3FC)
val VibrantAzureSurface = Color(0xFFF0F9FF)
val VibrantAzureTint = Color(0xFFF6FAFF)

val VibrantSakuraPink = Color(0xFFEC4899)
val VibrantSakuraLight = Color(0xFFF472B6)
val VibrantSakuraSurface = Color(0xFFFDF2F8)
val VibrantSakuraTint = Color(0xFFFFF5F9)

val VibrantNeonPurple = Color(0xFF8B5CF6)
val VibrantNeonPurpleLight = Color(0xFFA78BFA)
val VibrantNeonPurpleSurface = Color(0xFFF5F3FF)
val VibrantNeonPurpleTint = Color(0xFFFAF8FF)

data class VibrantThemeInfo(
    val key: String,
    val nameEn: String,
    val nameHi: String,
    val emoji: String,
    val category: String, // "Default White", "Image Inspiration", "Google Messages Style", "Classic Aesthetic"
    val primaryColor: Color,
    val secondaryColor: Color,
    val lightBackground: Color = Color(0xFFFFFFFF), // PURE WHITE DEFAULT
    val lightSurface: Color = Color(0xFFFFFFFF),    // PURE WHITE DEFAULT
    val lightSurfaceVariant: Color = Color(0xFFF8FAFC),
    val lightBorderColor: Color = Color(0xFFE2E8F0),
    val darkBackground: Color = Color(0xFF0A0E1A),
    val darkSurface: Color = Color(0xFF121829),
    val darkSurfaceVariant: Color = Color(0xFF1B233A),
    val accentPink: Color = Color(0xFFEC4899),
    val accentGold: Color = Color(0xFFF59E0B),
    val accentGreen: Color = Color(0xFF10B981),
    // Color tokens matching Image 1
    val secondaryForeground: Color = Color(0xFF44334D),
    val mutedColor: Color = Color(0xFFF2EDF3),
    val mutedForeground: Color = Color(0xFF866C93),
    val accentColor: Color = Color(0xFFE49181),
    val destructiveColor: Color = Color(0xFFEF4444)
)

object VibrantThemePresets {
    val all: List<VibrantThemeInfo> = listOf(
        // ==========================================
        // 1. DEFAULT: PURE WHITE THEME
        // ==========================================
        VibrantThemeInfo(
            key = "WHITE_MINIMAL",
            nameEn = "Pure White (Default)",
            nameHi = "प्योर व्हाइट (डिफ़ॉल्ट)",
            emoji = "⚪",
            category = "Default White",
            primaryColor = Color(0xFF4F46E5), // Indigo Accent
            secondaryColor = Color(0xFF0284C7),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF8FAFC),
            lightBorderColor = Color(0xFFE2E8F0),
            accentPink = Color(0xFFE49181),
            accentGold = Color(0xFFF59E0B),
            secondaryForeground = Color(0xFF1E293B),
            mutedColor = Color(0xFFF1F5F9),
            mutedForeground = Color(0xFF64748B),
            accentColor = Color(0xFF4F46E5)
        ),

        // ==========================================
        // 2. IMAGE 1: TRAVEL PLANNER (LAVENDER & CORAL)
        // ==========================================
        VibrantThemeInfo(
            key = "TRAVEL_PLANNER",
            nameEn = "Travel Lavender & Coral",
            nameHi = "ट्रैवल लैवेंडर व कोरल",
            emoji = "✈️",
            category = "Image Inspiration",
            primaryColor = Color(0xFFE49181), // Accent Coral from Screenshot
            secondaryColor = Color(0xFF866C93), // Muted Foreground Lavender
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF2EDF3), // Muted from Screenshot
            lightBorderColor = Color(0xFFE4DAE7), // Border from Screenshot
            secondaryForeground = Color(0xFF44334D),
            mutedColor = Color(0xFFF2EDF3),
            mutedForeground = Color(0xFF866C93),
            accentColor = Color(0xFFE49181),
            destructiveColor = Color(0xFFEF4444)
        ),

        // ==========================================
        // 3. IMAGE 4: 10 DESIGNER PALETTES
        // ==========================================
        VibrantThemeInfo(
            key = "SOFT",
            nameEn = "Soft Pastel",
            nameHi = "सॉफ्ट पेस्टल",
            emoji = "🌾",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFF8A9DA4), // Dusty blue
            secondaryColor = Color(0xFFEEC089), // Soft Peach
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF7F5F0),
            lightBorderColor = Color(0xFFE5DFD3),
            accentColor = Color(0xFFEEC089)
        ),
        VibrantThemeInfo(
            key = "POWERFUL",
            nameEn = "Powerful Bold",
            nameHi = "पावरफुल बोल्ड",
            emoji = "⚡",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFFCD1818), // Crimson Red
            secondaryColor = Color(0xFF0F2C67), // Navy
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFFF7ED),
            lightBorderColor = Color(0xFFFED7AA),
            accentColor = Color(0xFFFFCA03)
        ),
        VibrantThemeInfo(
            key = "MODERN",
            nameEn = "Modern Warm",
            nameHi = "मॉडर्न वॉर्म",
            emoji = "☕",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFF9E7777), // Dusty Rose
            secondaryColor = Color(0xFF7D5A50), // Mocha
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFAF6F0),
            lightBorderColor = Color(0xFFEBDDCB),
            accentColor = Color(0xFFE5B299)
        ),
        VibrantThemeInfo(
            key = "FUTURISTIC",
            nameEn = "Futuristic Cyber",
            nameHi = "फ्यूचरिस्टिक साइबर",
            emoji = "🚀",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFFF43B86), // Neon Pink
            secondaryColor = Color(0xFF3D087B), // Cyber Violet
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFDF4F9),
            lightBorderColor = Color(0xFFFBCFE8),
            accentColor = Color(0xFFFFE459)
        ),
        VibrantThemeInfo(
            key = "NATURAL",
            nameEn = "Natural Forest",
            nameHi = "नेचुरल फॉरेस्ट",
            emoji = "🍃",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFF0B8457), // Forest Green
            secondaryColor = Color(0xFF096C47),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF0FDF4),
            lightBorderColor = Color(0xFFBBF7D0),
            accentColor = Color(0xFFFF4E5C)
        ),
        VibrantThemeInfo(
            key = "EXCLUSIVE",
            nameEn = "Exclusive Terracotta",
            nameHi = "एक्सक्लूसिव टेराकोटा",
            emoji = "🏺",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFFB85C38), // Terracotta
            secondaryColor = Color(0xFF5C3D2E), // Espresso
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFBF6F0),
            lightBorderColor = Color(0xFFEAD8C7),
            accentColor = Color(0xFFE0C097)
        ),
        VibrantThemeInfo(
            key = "ROMANTIC",
            nameEn = "Romantic Sakura",
            nameHi = "रोमांटिक साकुरा",
            emoji = "🌸",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFFE36387), // Rose
            secondaryColor = Color(0xFFA6DCEF), // Sky
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFFF1F5),
            lightBorderColor = Color(0xFFFFD6E0),
            accentColor = Color(0xFFF2AAAA)
        ),
        VibrantThemeInfo(
            key = "VINTAGE",
            nameEn = "Vintage Seafoam",
            nameHi = "विंटेज सीफोम",
            emoji = "🎞️",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFF87A7B3), // Vintage Blue
            secondaryColor = Color(0xFF766161), // Dusty Walnut
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF4F7F6),
            lightBorderColor = Color(0xFFDAE4E1),
            accentColor = Color(0xFFCAE4DB)
        ),
        VibrantThemeInfo(
            key = "TRADITIONAL",
            nameEn = "Traditional Heritage",
            nameHi = "ट्रेडिशनल हेरिटेज",
            emoji = "🏮",
            category = "Classic Aesthetic",
            primaryColor = Color(0xFFE48257), // Spice
            secondaryColor = Color(0xFF3A6351), // Heritage Sage
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFBF8EE),
            lightBorderColor = Color(0xFFE6DEC3),
            accentColor = Color(0xFFF2EDD7)
        ),

        // ==========================================
        // 4. IMAGE 2 & 5: GOOGLE MESSAGES MATERIAL PRESETS
        // ==========================================
        VibrantThemeInfo(
            key = "BLUE_SKY",
            nameEn = "Sky Blue (Material)",
            nameHi = "आसमानी नीला",
            emoji = "🔹",
            category = "Google Messages Style",
            primaryColor = Color(0xFF3B82F6),
            secondaryColor = Color(0xFF1D4ED8),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFEFF6FF),
            lightBorderColor = Color(0xFFBFDBFE)
        ),
        VibrantThemeInfo(
            key = "INDIGO_SLATE",
            nameEn = "Indigo Slate",
            nameHi = "इंडिगो स्लेट",
            emoji = "🌌",
            category = "Google Messages Style",
            primaryColor = Color(0xFF4F46E5),
            secondaryColor = Color(0xFF4338CA),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFEEF2FF),
            lightBorderColor = Color(0xFFC7D2FE)
        ),
        VibrantThemeInfo(
            key = "TEAL_MINT",
            nameEn = "Teal Cyan",
            nameHi = "टील सियान",
            emoji = "🌊",
            category = "Google Messages Style",
            primaryColor = Color(0xFF0D9488),
            secondaryColor = Color(0xFF0F766E),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF0FDFA),
            lightBorderColor = Color(0xFF99F6E4)
        ),
        VibrantThemeInfo(
            key = "VIOLET_PURPLE",
            nameEn = "Violet Purple",
            nameHi = "वायलेट बैंगनी",
            emoji = "🟣",
            category = "Google Messages Style",
            primaryColor = Color(0xFF7C3AED),
            secondaryColor = Color(0xFF6D28D9),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF5F3FF),
            lightBorderColor = Color(0xFFDDD6FE)
        ),
        VibrantThemeInfo(
            key = "SAGE_OLIVE",
            nameEn = "Sage Olive",
            nameHi = "सेज ऑलिव",
            emoji = "🫒",
            category = "Google Messages Style",
            primaryColor = Color(0xFF4D7C0F),
            secondaryColor = Color(0xFF3F6212),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF7FEE7),
            lightBorderColor = Color(0xFFD9F99D)
        ),
        VibrantThemeInfo(
            key = "TERRACOTTA_ORANGE",
            nameEn = "Amber Terracotta",
            nameHi = "एम्बर टेराकोटा",
            emoji = "🟠",
            category = "Google Messages Style",
            primaryColor = Color(0xFFC2410C),
            secondaryColor = Color(0xFF9A3412),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFFF7ED),
            lightBorderColor = Color(0xFFFED7AA)
        ),
        VibrantThemeInfo(
            key = "ROSE_BERRY",
            nameEn = "Berry Rose",
            nameHi = "बेरी रोज़",
            emoji = "🍷",
            category = "Google Messages Style",
            primaryColor = Color(0xFFBE185D),
            secondaryColor = Color(0xFF9D174D),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFDF2F8),
            lightBorderColor = Color(0xFFFBCFE8)
        ),
        VibrantThemeInfo(
            key = "MAGENTA_PINK",
            nameEn = "Magenta Glow",
            nameHi = "मैजेंटा ग्लो",
            emoji = "💖",
            category = "Google Messages Style",
            primaryColor = Color(0xFFDB2777),
            secondaryColor = Color(0xFFBE185D),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFFDF2F8),
            lightBorderColor = Color(0xFFF9A8D4)
        ),
        VibrantThemeInfo(
            key = "CHARCOAL_MONO",
            nameEn = "Minimal Slate",
            nameHi = "मिनिमल स्लेट",
            emoji = "🖤",
            category = "Google Messages Style",
            primaryColor = Color(0xFF374151),
            secondaryColor = Color(0xFF1F2937),
            lightBackground = Color(0xFFFFFFFF),
            lightSurface = Color(0xFFFFFFFF),
            lightSurfaceVariant = Color(0xFFF9FAFB),
            lightBorderColor = Color(0xFFE5E7EB)
        )
    )

    fun find(key: String): VibrantThemeInfo {
        return all.firstOrNull { it.key.equals(key, ignoreCase = true) }
            ?: all.first() // Default is WHITE_MINIMAL
    }
}

/**
 * 10 Columns x 8 Rows Color Swatch Matrix (Image 6)
 * Enables comprehensive, flexible single-tap color switching.
 */
object ColorSwatchGridData {
    val swatchesMatrix: List<List<Color>> = listOf(
        // Row 1: Grayscale & Neutrals (Black to Pure White)
        listOf(
            Color(0xFF000000), Color(0xFF262626), Color(0xFF404040), Color(0xFF525252),
            Color(0xFF737373), Color(0xFFA3A3A3), Color(0xFFD4D4D4), Color(0xFFE5E5E5),
            Color(0xFFF5F5F5), Color(0xFFFFFFFF)
        ),
        // Row 2: Vivid Spectrum (Primary & Accents)
        listOf(
            Color(0xFF7F1D1D), Color(0xFFDC2626), Color(0xFFEA580C), Color(0xFFCA8A04),
            Color(0xFF65A30D), Color(0xFF059669), Color(0xFF0D9488), Color(0xFF0284C7),
            Color(0xFF2563EB), Color(0xFF7C3AED)
        ),
        // Row 3: Soft Pastels
        listOf(
            Color(0xFFE0C097), Color(0xFFF2AAAA), Color(0xFFF5E8C7), Color(0xFFFEF08A),
            Color(0xFFDCFCE7), Color(0xFFCCFBF1), Color(0xFFBAE6FD), Color(0xFFC7D2FE),
            Color(0xFFDDD6FE), Color(0xFFFCE7F3)
        ),
        // Row 4: Earth & Desert Tones
        listOf(
            Color(0xFFB85C38), Color(0xFFE48257), Color(0xFFE5B299), Color(0xFFD7D0B6),
            Color(0xFF8A9DA4), Color(0xFF79B4B7), Color(0xFF87A7B3), Color(0xFF9E7777),
            Color(0xFF7D5A50), Color(0xFF5C3D2E)
        ),
        // Row 5: Jewel Tones
        listOf(
            Color(0xFF991B1B), Color(0xFFC2410C), Color(0xFFB45309), Color(0xFF15803D),
            Color(0xFF0F766E), Color(0xFF0369A1), Color(0xFF1D4ED8), Color(0xFF5B21B6),
            Color(0xFF7E22CE), Color(0xFF9D174D)
        ),
        // Row 6: Deep Tones
        listOf(
            Color(0xFF450A0A), Color(0xFF7C2D12), Color(0xFF713F12), Color(0xFF14532D),
            Color(0xFF134E4A), Color(0xFF0C4A6E), Color(0xFF1E3A8A), Color(0xFF2E1065),
            Color(0xFF3B0764), Color(0xFF500724)
        ),
        // Row 7: Neon & Vibrant Pops
        listOf(
            Color(0xFFEF4444), Color(0xFFF97316), Color(0xFFFACC15), Color(0xFF22C55E),
            Color(0xFF14B8A6), Color(0xFF06B6D4), Color(0xFF3B82F6), Color(0xFF8B5CF6),
            Color(0xFFD946EF), Color(0xFFF43F5E)
        ),
        // Row 8: Modern Slate & Muted Navy
        listOf(
            Color(0xFF1E293B), Color(0xFF334155), Color(0xFF475569), Color(0xFF64748B),
            Color(0xFF4B4D63), Color(0xFF3A6351), Color(0xFF323232), Color(0xFF251F44),
            Color(0xFF766161), Color(0xFF1F2937)
        )
    )

    val quickThemeChips: List<Color> = listOf(
        Color(0xFF000000), Color(0xFFFFFFFF), Color(0xFF3B82F6), Color(0xFFEF4444),
        Color(0xFFF59E0B), Color(0xFF10B981), Color(0xFFF97316), Color(0xFF06B6D4)
    )
}

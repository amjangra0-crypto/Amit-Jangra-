package com.example.ui.theme

import androidx.compose.ui.graphics.Color

// Anime Studio Vibrant Dark Theme Palette
val AnimeBackground = Color(0xFF0A0E1A)
val AnimeSurface = Color(0xFF121829)
val AnimeSurfaceVariant = Color(0xFF1B233A)
val AnimeCardBorder = Color(0xFF2B3654)

val AnimePurple = Color(0xFF8B5CF6)
val AnimePurpleLight = Color(0xFFA78BFA)
val AnimeCyan = Color(0xFF06B6D4)
val AnimeCyanLight = Color(0xFF22D3EE)
val AnimePink = Color(0xFFEC4899)
val AnimeGold = Color(0xFFF59E0B)
val AnimeGreen = Color(0xFF10B981)

val TextPrimary = Color(0xFFF8FAFC)
val TextSecondary = Color(0xFF94A3B8)
val TextMuted = Color(0xFF64748B)

// Eye-Comfort Attractive Light Theme Palette
val LightBackground = Color(0xFFF8FAFC)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F5F9)
val LightCardBorder = Color(0xFFE2E8F0)

val LightPurple = Color(0xFF6366F1)
val LightPurpleLight = Color(0xFF818CF8)
val LightCyan = Color(0xFF0284C7)
val LightCyanLight = Color(0xFF38BDF8)
val LightPink = Color(0xFFDB2777)
val LightGold = Color(0xFFD97706)
val LightGreen = Color(0xFF059669)

val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF334155)
val LightTextMuted = Color(0xFF64748B)

// Vibrant & Colourful Pages Palette (Rich, saturated, high energy)
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
    val primaryColor: Color,
    val secondaryColor: Color,
    val lightBackground: Color,
    val lightSurface: Color,
    val lightSurfaceVariant: Color,
    val darkBackground: Color,
    val darkSurface: Color,
    val darkSurfaceVariant: Color
)

object VibrantThemePresets {
    val all = listOf(
        VibrantThemeInfo(
            key = "CORAL",
            nameEn = "Coral Red",
            nameHi = "कोरल लाल",
            emoji = "🔴",
            primaryColor = Color(0xFFE53935),
            secondaryColor = Color(0xFFF59E0B),
            lightBackground = Color(0xFFFFECEB),
            lightSurface = Color(0xFFFFD9D6),
            lightSurfaceVariant = Color(0xFFFFC5C0),
            darkBackground = Color(0xFF200709),
            darkSurface = Color(0xFF330C10),
            darkSurfaceVariant = Color(0xFF481419)
        ),
        VibrantThemeInfo(
            key = "EMERALD",
            nameEn = "Emerald Green",
            nameHi = "एमराल्ड हरा",
            emoji = "🟢",
            primaryColor = Color(0xFF10B981),
            secondaryColor = Color(0xFF06B6D4),
            lightBackground = Color(0xFFE8FDF0),
            lightSurface = Color(0xFFD1FADF),
            lightSurfaceVariant = Color(0xFFA6F4C5),
            darkBackground = Color(0xFF041E11),
            darkSurface = Color(0xFF092C1A),
            darkSurfaceVariant = Color(0xFF103F27)
        ),
        VibrantThemeInfo(
            key = "AZURE",
            nameEn = "Azure Blue",
            nameHi = "एज़्यूर नीला",
            emoji = "🔵",
            primaryColor = Color(0xFF0284C7),
            secondaryColor = Color(0xFF8B5CF6),
            lightBackground = Color(0xFFE0F2FE),
            lightSurface = Color(0xFFBAE6FD),
            lightSurfaceVariant = Color(0xFF7DD3FC),
            darkBackground = Color(0xFF051726),
            darkSurface = Color(0xFF09233A),
            darkSurfaceVariant = Color(0xFF0F3252)
        ),
        VibrantThemeInfo(
            key = "MANGO",
            nameEn = "Mango Gold",
            nameHi = "मैंगो गोल्ड",
            emoji = "🥭",
            primaryColor = Color(0xFFD97706),
            secondaryColor = Color(0xFFE53935),
            lightBackground = Color(0xFFFEF3C7),
            lightSurface = Color(0xFFFDE68A),
            lightSurfaceVariant = Color(0xFFFCD34D),
            darkBackground = Color(0xFF261902),
            darkSurface = Color(0xFF382504),
            darkSurfaceVariant = Color(0xFF4D3307)
        ),
        VibrantThemeInfo(
            key = "SPRING",
            nameEn = "Spring Lime",
            nameHi = "स्प्रिंग लाइम",
            emoji = "🌿",
            primaryColor = Color(0xFF22C55E),
            secondaryColor = Color(0xFF0284C7),
            lightBackground = Color(0xFFF0FDF4),
            lightSurface = Color(0xFFDCFCE7),
            lightSurfaceVariant = Color(0xFFBBF7D0),
            darkBackground = Color(0xFF05210E),
            darkSurface = Color(0xFF0A3116),
            darkSurfaceVariant = Color(0xFF134521)
        ),
        VibrantThemeInfo(
            key = "SAKURA",
            nameEn = "Sakura Pink",
            nameHi = "साकुरा गुलाबी",
            emoji = "🌸",
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFFF59E0B),
            lightBackground = Color(0xFFFCE7F3),
            lightSurface = Color(0xFFFBCFE8),
            lightSurfaceVariant = Color(0xFFF472B6),
            darkBackground = Color(0xFF260619),
            darkSurface = Color(0xFF380B26),
            darkSurfaceVariant = Color(0xFF4D1235)
        ),
        VibrantThemeInfo(
            key = "NEON",
            nameEn = "Neon Purple",
            nameHi = "नियॉन पर्पल",
            emoji = "🔮",
            primaryColor = Color(0xFF8B5CF6),
            secondaryColor = Color(0xFF06B6D4),
            lightBackground = Color(0xFFF3E8FF),
            lightSurface = Color(0xFFE9D5FF),
            lightSurfaceVariant = Color(0xFFD8B4FE),
            darkBackground = Color(0xFF150826),
            darkSurface = Color(0xFF220D3D),
            darkSurfaceVariant = Color(0xFF321556)
        )
    )

    fun find(key: String): VibrantThemeInfo {
        return all.firstOrNull { it.key.equals(key, ignoreCase = true) }
            ?: if (key.equals("RED", ignoreCase = true)) all.first()
            else if (key.equals("GREEN", ignoreCase = true)) all[1]
            else if (key.equals("BLUE", ignoreCase = true)) all[2]
            else all.first()
    }
}

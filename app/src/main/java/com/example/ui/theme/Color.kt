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
            nameEn = "Crimson Red",
            nameHi = "क्रिमसन लाल",
            emoji = "🔴",
            primaryColor = Color(0xFFEF4444),
            secondaryColor = Color(0xFFF59E0B),
            lightBackground = Color(0xFF4A000A),
            lightSurface = Color(0xFF6E0715),
            lightSurfaceVariant = Color(0xFF910F1F),
            darkBackground = Color(0xFF3B0007),
            darkSurface = Color(0xFF5C0510),
            darkSurfaceVariant = Color(0xFF7E0B19)
        ),
        VibrantThemeInfo(
            key = "EMERALD",
            nameEn = "Emerald Green",
            nameHi = "एमराल्ड हरा",
            emoji = "🟢",
            primaryColor = Color(0xFF10B981),
            secondaryColor = Color(0xFF06B6D4),
            lightBackground = Color(0xFF032B19),
            lightSurface = Color(0xFF064427),
            lightSurfaceVariant = Color(0xFF0A633A),
            darkBackground = Color(0xFF022314),
            darkSurface = Color(0xFF043820),
            darkSurfaceVariant = Color(0xFF075430)
        ),
        VibrantThemeInfo(
            key = "AZURE",
            nameEn = "Azure Blue",
            nameHi = "एज़्यूर नीला",
            emoji = "🔵",
            primaryColor = Color(0xFF0EA5E9),
            secondaryColor = Color(0xFF8B5CF6),
            lightBackground = Color(0xFF051C3B),
            lightSurface = Color(0xFF0A3063),
            lightSurfaceVariant = Color(0xFF10468E),
            darkBackground = Color(0xFF04162E),
            darkSurface = Color(0xFF082752),
            darkSurfaceVariant = Color(0xFF0D3C7A)
        ),
        VibrantThemeInfo(
            key = "MANGO",
            nameEn = "Mango Gold",
            nameHi = "मैंगो गोल्ड",
            emoji = "🥭",
            primaryColor = Color(0xFFF59E0B),
            secondaryColor = Color(0xFFEF4444),
            lightBackground = Color(0xFF381F02),
            lightSurface = Color(0xFF542E03),
            lightSurfaceVariant = Color(0xFF733F05),
            darkBackground = Color(0xFF2E1901),
            darkSurface = Color(0xFF472702),
            darkSurfaceVariant = Color(0xFF663803)
        ),
        VibrantThemeInfo(
            key = "SPRING",
            nameEn = "Spring Lime",
            nameHi = "स्प्रिंग लाइम",
            emoji = "🌿",
            primaryColor = Color(0xFF84CC16),
            secondaryColor = Color(0xFF0EA5E9),
            lightBackground = Color(0xFF173005),
            lightSurface = Color(0xFF244A08),
            lightSurfaceVariant = Color(0xFF34690C),
            darkBackground = Color(0xFF122604),
            darkSurface = Color(0xFF1E3D07),
            darkSurfaceVariant = Color(0xFF2C590B)
        ),
        VibrantThemeInfo(
            key = "SAKURA",
            nameEn = "Sakura Pink",
            nameHi = "साकुरा गुलाबी",
            emoji = "🌸",
            primaryColor = Color(0xFFEC4899),
            secondaryColor = Color(0xFFF59E0B),
            lightBackground = Color(0xFF3D0623),
            lightSurface = Color(0xFF5E0A37),
            lightSurfaceVariant = Color(0xFF7F104B),
            darkBackground = Color(0xFF33041C),
            darkSurface = Color(0xFF4F072D),
            darkSurfaceVariant = Color(0xFF6E0D40)
        ),
        VibrantThemeInfo(
            key = "NEON",
            nameEn = "Neon Purple",
            nameHi = "नियॉन पर्पल",
            emoji = "🔮",
            primaryColor = Color(0xFFA855F7),
            secondaryColor = Color(0xFF06B6D4),
            lightBackground = Color(0xFF22073F),
            lightSurface = Color(0xFF350C62),
            lightSurfaceVariant = Color(0xFF4C128C),
            darkBackground = Color(0xFF1B0533),
            darkSurface = Color(0xFF2D0954),
            darkSurfaceVariant = Color(0xFF430E7A)
        )
    )

    fun find(key: String): VibrantThemeInfo {
        return all.firstOrNull { it.key.equals(key, ignoreCase = true) }
            ?: if (key.equals("RED", ignoreCase = true) || key.equals("CORAL", ignoreCase = true)) all.first()
            else if (key.equals("GREEN", ignoreCase = true) || key.equals("EMERALD", ignoreCase = true)) all[1]
            else if (key.equals("BLUE", ignoreCase = true) || key.equals("AZURE", ignoreCase = true)) all[2]
            else if (key.equals("GOLD", ignoreCase = true) || key.equals("MANGO", ignoreCase = true) || key.equals("YELLOW", ignoreCase = true)) all[3]
            else if (key.equals("LIME", ignoreCase = true) || key.equals("SPRING", ignoreCase = true)) all[4]
            else if (key.equals("PINK", ignoreCase = true) || key.equals("SAKURA", ignoreCase = true)) all[5]
            else if (key.equals("PURPLE", ignoreCase = true) || key.equals("NEON", ignoreCase = true)) all[6]
            else all.first()
    }
}

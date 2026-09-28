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
    val lightSurface: Color
)

object VibrantThemePresets {
    val all = listOf(
        VibrantThemeInfo(
            key = "CORAL",
            nameEn = "Coral Sunset",
            nameHi = "कोरल सनसेट",
            emoji = "🌺",
            primaryColor = VibrantCoralRed,
            secondaryColor = VibrantMangoYellow,
            lightSurface = VibrantCoralSurface
        ),
        VibrantThemeInfo(
            key = "MANGO",
            nameEn = "Mango Gold",
            nameHi = "मैंगो गोल्ड",
            emoji = "🥭",
            primaryColor = VibrantMangoYellow,
            secondaryColor = VibrantCoralRed,
            lightSurface = VibrantMangoSurface
        ),
        VibrantThemeInfo(
            key = "SPRING",
            nameEn = "Spring Lime",
            nameHi = "स्प्रिंग लाइम",
            emoji = "🌿",
            primaryColor = VibrantSpringGreen,
            secondaryColor = VibrantAzureBlue,
            lightSurface = VibrantSpringSurface
        ),
        VibrantThemeInfo(
            key = "EMERALD",
            nameEn = "Emerald Jade",
            nameHi = "एमराल्ड जेड",
            emoji = "💎",
            primaryColor = VibrantEmeraldGreen,
            secondaryColor = VibrantSpringGreen,
            lightSurface = VibrantEmeraldSurface
        ),
        VibrantThemeInfo(
            key = "AZURE",
            nameEn = "Azure Ocean",
            nameHi = "एज़्यूर ओशन",
            emoji = "🌊",
            primaryColor = VibrantAzureBlue,
            secondaryColor = VibrantNeonPurple,
            lightSurface = VibrantAzureSurface
        ),
        VibrantThemeInfo(
            key = "SAKURA",
            nameEn = "Sakura Blossom",
            nameHi = "साकुरा ब्लॉसम",
            emoji = "🌸",
            primaryColor = VibrantSakuraPink,
            secondaryColor = VibrantCoralRed,
            lightSurface = VibrantSakuraSurface
        ),
        VibrantThemeInfo(
            key = "NEON",
            nameEn = "Neon Cyber",
            nameHi = "नियॉन साइबर",
            emoji = "🔮",
            primaryColor = VibrantNeonPurple,
            secondaryColor = VibrantAzureBlue,
            lightSurface = VibrantNeonPurpleSurface
        )
    )

    fun find(key: String): VibrantThemeInfo {
        return all.firstOrNull { it.key.equals(key, ignoreCase = true) } ?: all.first()
    }
}

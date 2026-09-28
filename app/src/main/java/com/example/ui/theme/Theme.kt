package com.example.ui.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = AnimePurple,
    onPrimary = TextPrimary,
    primaryContainer = AnimeSurfaceVariant,
    onPrimaryContainer = AnimePurpleLight,
    secondary = AnimeCyan,
    onSecondary = TextPrimary,
    secondaryContainer = AnimeSurfaceVariant,
    onSecondaryContainer = AnimeCyanLight,
    tertiary = AnimePink,
    background = AnimeBackground,
    onBackground = TextPrimary,
    surface = AnimeSurface,
    onSurface = TextPrimary,
    surfaceVariant = AnimeSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = AnimeCardBorder
)

private val LightColorScheme = lightColorScheme(
    primary = LightPurple,
    onPrimary = Color.White,
    primaryContainer = LightSurfaceVariant,
    onPrimaryContainer = LightPurple,
    secondary = LightCyan,
    onSecondary = Color.White,
    secondaryContainer = LightSurfaceVariant,
    onSecondaryContainer = LightCyan,
    tertiary = LightPink,
    background = LightBackground,
    onBackground = LightTextPrimary,
    surface = LightSurface,
    onSurface = LightTextPrimary,
    surfaceVariant = LightSurfaceVariant,
    onSurfaceVariant = LightTextSecondary,
    outline = LightCardBorder
)

fun getVibrantColorScheme(theme: String, isDark: Boolean): androidx.compose.material3.ColorScheme {
    val preset = VibrantThemePresets.find(theme)
    val primaryColor = preset.primaryColor
    val secondaryColor = preset.secondaryColor

    return if (isDark) {
        DarkColorScheme.copy(
            primary = primaryColor,
            primaryContainer = AnimeSurfaceVariant,
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            secondaryContainer = AnimeSurfaceVariant,
            onSecondaryContainer = secondaryColor,
            tertiary = when (theme.uppercase()) {
                "SAKURA", "CORAL" -> VibrantMangoYellow
                "MANGO" -> VibrantCoralRed
                "SPRING", "EMERALD" -> VibrantAzureBlue
                "AZURE" -> VibrantSakuraPink
                else -> AnimePink
            },
            background = AnimeBackground,
            surface = AnimeSurface,
            surfaceVariant = AnimeSurfaceVariant
        )
    } else {
        val lightBg = when (theme.uppercase()) {
            "CORAL" -> VibrantCoralTint
            "MANGO" -> VibrantMangoTint
            "SPRING" -> VibrantSpringTint
            "EMERALD" -> VibrantEmeraldTint
            "AZURE" -> VibrantAzureTint
            "SAKURA" -> VibrantSakuraTint
            "NEON" -> VibrantNeonPurpleTint
            else -> VibrantCoralTint
        }

        LightColorScheme.copy(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = preset.lightSurface,
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = LightSurfaceVariant,
            onSecondaryContainer = secondaryColor,
            tertiary = when (theme.uppercase()) {
                "SAKURA", "CORAL" -> VibrantMangoYellow
                "MANGO" -> VibrantCoralRed
                "SPRING", "EMERALD" -> VibrantAzureBlue
                "AZURE" -> VibrantSakuraPink
                else -> AnimePink
            },
            background = lightBg,
            surface = Color.White,
            surfaceVariant = preset.lightSurface,
            onSurface = LightTextPrimary,
            onSurfaceVariant = LightTextSecondary,
            outline = primaryColor.copy(alpha = 0.25f)
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false,
    vibrantTheme: String = "CORAL",
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = getVibrantColorScheme(vibrantTheme, darkTheme)
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = colorScheme.primary.toArgb()
                window.navigationBarColor = colorScheme.background.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = false
                insetsController.isAppearanceLightNavigationBars = !darkTheme
            }
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

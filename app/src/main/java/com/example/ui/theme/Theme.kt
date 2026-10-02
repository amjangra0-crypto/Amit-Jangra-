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

fun getVibrantColorScheme(theme: String, isDark: Boolean): androidx.compose.material3.ColorScheme {
    val preset = VibrantThemePresets.find(theme)
    val primaryColor = AppThemeController.customAccentColor ?: preset.primaryColor
    val secondaryColor = preset.secondaryColor

    return if (isDark) {
        darkColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = preset.darkSurfaceVariant,
            onPrimaryContainer = Color.White,
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = preset.darkSurfaceVariant,
            onSecondaryContainer = secondaryColor,
            tertiary = preset.accentPink,
            background = preset.darkBackground,
            onBackground = Color(0xFFF8FAFC),
            surface = preset.darkSurface,
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = preset.darkSurfaceVariant,
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF334155)
        )
    } else {
        // Pure White Default Color everywhere
        lightColorScheme(
            primary = primaryColor,
            onPrimary = Color.White,
            primaryContainer = preset.lightSurfaceVariant,
            onPrimaryContainer = primaryColor,
            secondary = secondaryColor,
            onSecondary = Color.White,
            secondaryContainer = preset.lightSurfaceVariant,
            onSecondaryContainer = secondaryColor,
            tertiary = preset.accentPink,
            background = preset.lightBackground, // Pure White (0xFFFFFFFF)
            onBackground = Color(0xFF0F172A),     // Crisp dark slate
            surface = preset.lightSurface,       // Pure White (0xFFFFFFFF)
            onSurface = Color(0xFF0F172A),       // Crisp dark slate
            surfaceVariant = preset.lightSurfaceVariant, // Soft clean off-white (0xFFF8FAFC)
            onSurfaceVariant = Color(0xFF475569),
            outline = preset.lightBorderColor    // Delicate subtle border (0xFFE2E8F0)
        )
    }
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = if (AppThemeController.isFollowSystemTheme) androidx.compose.foundation.isSystemInDarkTheme() else AppThemeController.isDarkMode,
    vibrantTheme: String = AppThemeController.currentThemeKey,
    dynamicColor: Boolean = AppThemeController.isFollowSystemTheme,
    content: @Composable () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val colorScheme = when {
        dynamicColor && android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S -> {
            if (darkTheme) androidx.compose.material3.dynamicDarkColorScheme(context)
            else androidx.compose.material3.dynamicLightColorScheme(context)
        }
        else -> getVibrantColorScheme(vibrantTheme, darkTheme)
    }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                // If light mode: clean white status bar with dark icons
                // If dark mode: dark status bar with light icons
                window.statusBarColor = if (darkTheme) colorScheme.surface.toArgb() else Color.White.toArgb()
                window.navigationBarColor = if (darkTheme) colorScheme.background.toArgb() else Color.White.toArgb()
                val insetsController = WindowCompat.getInsetsController(window, view)
                insetsController.isAppearanceLightStatusBars = !darkTheme
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

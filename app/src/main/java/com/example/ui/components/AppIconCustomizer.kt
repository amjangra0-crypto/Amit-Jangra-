package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLocaleStrings
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Presets for icon customization across the entire application.
 */
data class IconStylePreset(
    val key: String,
    val nameEn: String,
    val nameHi: String,
    val primaryTint: Color,
    val containerColor: Color,
    val borderColor: Color,
    val descriptionEn: String,
    val descriptionHi: String
)

data class LocalAppIconPreset(
    val key: String,
    val nameEn: String,
    val nameHi: String,
    val emoji: String,
    val accentColor: Color,
    val subtitle: String
)

data class IconShapePreset(
    val key: String,
    val nameEn: String,
    val nameHi: String,
    val shape: Shape
)

object AppIconPresets {
    val styles = listOf(
        IconStylePreset(
            key = "NEON_GLOW",
            nameEn = "Neon Glow",
            nameHi = "नियॉन ग्लो",
            primaryTint = AnimeCyan,
            containerColor = AnimeCyan.copy(alpha = 0.16f),
            borderColor = AnimeCyan.copy(alpha = 0.45f),
            descriptionEn = "Vibrant cyberpunk neon glow with high contrast",
            descriptionHi = "उज्ज्वल साइबरपंक नियॉन चमक"
        ),
        IconStylePreset(
            key = "METALLIC_GOLD",
            nameEn = "Metallic Gold",
            nameHi = "गोल्डन लक्स",
            primaryTint = AnimeGold,
            containerColor = AnimeGold.copy(alpha = 0.18f),
            borderColor = AnimeGold.copy(alpha = 0.5f),
            descriptionEn = "Luxurious royal studio golden radiance",
            descriptionHi = "शाही एनीमे स्टूडियो गोल्डन चमक"
        ),
        IconStylePreset(
            key = "SAKURA_VIBRANT",
            nameEn = "Sakura Pink",
            nameHi = "साकुरा पिंक",
            primaryTint = AnimePink,
            containerColor = AnimePink.copy(alpha = 0.16f),
            borderColor = AnimePink.copy(alpha = 0.45f),
            descriptionEn = "Lively cherry blossom pink and violet tone",
            descriptionHi = "चमकदार चेरी ब्लॉसम गुलाबी टोन"
        ),
        IconStylePreset(
            key = "CYBER_AZURE",
            nameEn = "Azure Crystal",
            nameHi = "एज़्योर क्रिस्टल",
            primaryTint = Color(0xFF00E5FF),
            containerColor = Color(0xFF00E5FF).copy(alpha = 0.15f),
            borderColor = Color(0xFF00E5FF).copy(alpha = 0.5f),
            descriptionEn = "Deep ocean crystal blue and high-tech glow",
            descriptionHi = "हाई-टेक क्रिस्टल नीला ग्लो"
        ),
        IconStylePreset(
            key = "EMERALD_MINT",
            nameEn = "Emerald Lime",
            nameHi = "एमराल्ड लाइम",
            primaryTint = AnimeGreen,
            containerColor = AnimeGreen.copy(alpha = 0.16f),
            borderColor = AnimeGreen.copy(alpha = 0.45f),
            descriptionEn = "Fresh luminous lime green anime matrix",
            descriptionHi = "ताज़ा लाइम ग्रीन एनीमे स्टाइल"
        ),
        IconStylePreset(
            key = "MINIMAL_CLEAN",
            nameEn = "Minimal Clean",
            nameHi = "मिनिमल क्लीन",
            primaryTint = TextPrimary,
            containerColor = Color.White.copy(alpha = 0.08f),
            borderColor = Color.White.copy(alpha = 0.25f),
            descriptionEn = "Sleek and crisp minimalist monochrome outline",
            descriptionHi = "सटीक और आकर्षक क्लीन आउटलाइन"
        )
    )

    val shapes = listOf(
        IconShapePreset("ROUNDED_SQUIRCLE", "Squircle", "राउंडेड स्क्वियरकल", RoundedCornerShape(12.dp)),
        IconShapePreset("CAPSULE_PILL", "Capsule", "कैप्सूल पिल", RoundedCornerShape(20.dp)),
        IconShapePreset("SMOOTH_CARD", "Smooth 8dp", "स्मूथ 8dp", RoundedCornerShape(8.dp)),
        IconShapePreset("CIRCLE_ROUND", "Circular", "सर्कुलर", CircleShape)
    )

    val localAppIcons = listOf(
        LocalAppIconPreset("SHONEN_HERO", "Shonen Hero", "शोनेन हीरो", "🔥", Color(0xFFFF5722), "Fiery action anime protagonist"),
        LocalAppIconPreset("ANIME_HEROINE", "Anime Heroine", "एनीमे हीरोइन", "🌸", AnimePink, "Cherry blossom star heroine"),
        LocalAppIconPreset("CYBER_MASCOT", "Cyber Chibi", "साइबर मस्कट", "⚡", AnimeCyan, "Cybernetic anime mascot droid"),
        LocalAppIconPreset("STUDIO_GOLD", "Studio Master", "स्टूडियो मास्टर", "👑", AnimeGold, "Director VIP golden crest")
    )
}

/**
 * Customized icon badge for all screens, navigation bars, and top bars.
 * Provides custom container shapes, vibrant alpha tints, border accents, and ripple feedback.
 */
@Composable
fun AppThemedIconBadge(
    imageVector: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    iconSize: Dp = 20.dp,
    containerSize: Dp = 38.dp,
    shape: Shape = RoundedCornerShape(10.dp),
    tint: Color = MaterialTheme.colorScheme.primary,
    containerColor: Color = tint.copy(alpha = 0.14f),
    borderColor: Color = tint.copy(alpha = 0.3f),
    onClick: (() -> Unit)? = null
) {
    val clickableModifier = if (onClick != null) {
        modifier
            .size(containerSize)
            .clip(shape)
            .background(containerColor)
            .border(1.dp, borderColor, shape)
            .clickable(onClick = onClick)
    } else {
        modifier
            .size(containerSize)
            .clip(shape)
            .background(containerColor)
            .border(1.dp, borderColor, shape)
    }

    Box(
        modifier = clickableModifier,
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            tint = tint,
            modifier = Modifier.size(iconSize)
        )
    }
}

/**
 * Full interactive "Customize All Icons" dialog
 * Allows customizing:
 * 1. Icon Accent Style (Neon Glow, Metallic Gold, Sakura, Azure Crystal, Emerald, Minimal Clean)
 * 2. Icon Container Shape (Squircle, Capsule, Smooth 8dp, Circular)
 * 3. Local App Launcher Icon (Shonen Hero, Anime Heroine, Cyber Mascot, Studio Master)
 * 4. Live showcase previewing ALL app icons in real-time
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun AllIconsCustomizerDialog(
    currentStyleKey: String,
    currentShapeKey: String,
    currentLocalIconKey: String,
    selectedLanguage: String,
    onSavePreferences: (styleKey: String, shapeKey: String, localIconKey: String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var selectedStyleKey by remember { mutableStateOf(currentStyleKey) }
    var selectedShapeKey by remember { mutableStateOf(currentShapeKey) }
    var selectedLocalIconKey by remember { mutableStateOf(currentLocalIconKey) }

    val activePreset = AppIconPresets.styles.find { it.key.equals(selectedStyleKey, ignoreCase = true) }
        ?: AppIconPresets.styles.first()

    val activeShapePreset = AppIconPresets.shapes.find { it.key.equals(selectedShapeKey, ignoreCase = true) }
        ?: AppIconPresets.shapes.first()

    val activeLocalIcon = AppIconPresets.localAppIcons.find { it.key.equals(selectedLocalIconKey, ignoreCase = true) }
        ?: AppIconPresets.localAppIcons.first()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(activePreset.primaryTint.copy(alpha = 0.2f))
                            .border(1.5.dp, activePreset.primaryTint, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = activePreset.primaryTint,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(selectedLanguage, "Customize All Icons", "सभी आइकन्स कस्टमाइज़ करें"),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = AppLocaleStrings.tr(selectedLanguage, "Stylize all app, local & UI icons globally", "ग्लोबल ऐप, लोकल व यूआई आइकन स्टाइल कस्टमाइज़ करें"),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.testTag("icon_customizer_close_btn")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 1. Live Showcase Preview Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.5.dp, activePreset.primaryTint)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(selectedLanguage, "Live Icons Showcase Preview:", "लाइव आइकन्स प्रीव्यू:"),
                            color = activePreset.primaryTint,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(activeLocalIcon.emoji, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${activeLocalIcon.nameEn} • ${activePreset.nameEn}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Showcase grid of app icons using the active custom style
                    val previewIcons = listOf(
                        Pair(Icons.Default.MovieFilter, "Studio"),
                        Pair(Icons.Default.PlayCircle, "Player"),
                        Pair(Icons.Default.History, "History"),
                        Pair(Icons.Default.Brush, "Builder"),
                        Pair(Icons.Default.WorkspacePremium, "VIP Pass"),
                        Pair(Icons.Default.DarkMode, "Dark Mode"),
                        Pair(Icons.Default.Palette, "Theme"),
                        Pair(Icons.Default.PhoneAndroid, "Local Icon"),
                        Pair(Icons.Default.AccountBalance, "Bank"),
                        Pair(Icons.Default.CreditCard, "Card"),
                        Pair(Icons.Default.MusicNote, "Lyria Audio"),
                        Pair(Icons.Default.Mic, "Dubbing"),
                        Pair(Icons.Default.Cloud, "Storage"),
                        Pair(Icons.Default.Download, "Export"),
                        Pair(Icons.Default.SmartToy, "Auto AI"),
                        Pair(Icons.Default.Settings, "Settings")
                    )

                    FlowRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        previewIcons.forEach { (iconVector, label) ->
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.width(68.dp)
                            ) {
                                AppThemedIconBadge(
                                    imageVector = iconVector,
                                    contentDescription = label,
                                    tint = activePreset.primaryTint,
                                    containerColor = activePreset.containerColor,
                                    borderColor = activePreset.borderColor,
                                    shape = activeShapePreset.shape,
                                    containerSize = 42.dp,
                                    iconSize = 22.dp
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = label,
                                    color = TextSecondary,
                                    fontSize = 9.sp,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 2. Icon Accent Styles
            Text(
                text = AppLocaleStrings.tr(selectedLanguage, "1. Choose Icon Accent Style:", "1. आइकन एक्सेंट स्टाइल चुनें:"),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppIconPresets.styles.forEach { style ->
                    val isSelected = selectedStyleKey.equals(style.key, ignoreCase = true)
                    val label = if (AppLocaleStrings.isHindi(selectedLanguage)) style.nameHi else style.nameEn

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) style.primaryTint.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                            .border(
                                width = if (isSelected) 1.8.dp else 1.dp,
                                color = if (isSelected) style.primaryTint else AnimePurple.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedStyleKey = style.key }
                            .padding(horizontal = 12.dp, vertical = 8.dp)
                            .testTag("icon_style_${style.key.lowercase()}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(14.dp)
                                    .clip(CircleShape)
                                    .background(style.primaryTint)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = label,
                                color = if (isSelected) style.primaryTint else TextPrimary,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Medium,
                                fontSize = 12.sp
                            )
                            if (isSelected) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = null,
                                    tint = style.primaryTint,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Icon Container Shapes
            Text(
                text = AppLocaleStrings.tr(selectedLanguage, "2. Choose Icon Container Shape:", "2. आइकन कंटेनर आकार चुनें:"),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppIconPresets.shapes.forEach { shapePreset ->
                    val isSelected = selectedShapeKey.equals(shapePreset.key, ignoreCase = true)
                    val label = if (AppLocaleStrings.isHindi(selectedLanguage)) shapePreset.nameHi else shapePreset.nameEn

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) activePreset.primaryTint.copy(alpha = 0.22f) else AnimeSurfaceVariant)
                            .border(
                                width = if (isSelected) 1.8.dp else 1.dp,
                                color = if (isSelected) activePreset.primaryTint else Color.White.copy(alpha = 0.15f),
                                shape = RoundedCornerShape(10.dp)
                            )
                            .clickable { selectedShapeKey = shapePreset.key }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(shapePreset.shape)
                                    .background(if (isSelected) activePreset.primaryTint else TextMuted.copy(alpha = 0.4f))
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label,
                                color = if (isSelected) activePreset.primaryTint else TextPrimary,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 4. Local App Launcher Icon
            Text(
                text = AppLocaleStrings.tr(selectedLanguage, "3. Local App Launcher Icon Theme:", "3. लोकल ऐप लॉन्चर आइकन थीम:"),
                color = MaterialTheme.colorScheme.onSurface,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AppIconPresets.localAppIcons.forEach { localIcon ->
                    val isSelected = selectedLocalIconKey.equals(localIcon.key, ignoreCase = true)
                    val label = if (AppLocaleStrings.isHindi(selectedLanguage)) localIcon.nameHi else localIcon.nameEn

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) localIcon.accentColor.copy(alpha = 0.22f) else AnimeSurfaceVariant)
                            .border(
                                width = if (isSelected) 1.8.dp else 1.dp,
                                color = if (isSelected) localIcon.accentColor else Color.White.copy(alpha = 0.12f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            .clickable { selectedLocalIconKey = localIcon.key }
                            .padding(horizontal = 12.dp, vertical = 9.dp)
                            .testTag("local_icon_${localIcon.key.lowercase()}")
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = localIcon.emoji, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column {
                                Text(
                                    text = label,
                                    color = if (isSelected) localIcon.accentColor else TextPrimary,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = localIcon.subtitle,
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(22.dp))

            // Save & Apply Button
            Button(
                onClick = {
                    onSavePreferences(selectedStyleKey, selectedShapeKey, selectedLocalIconKey)
                    Toast.makeText(
                        context,
                        "✨ Icons Customized: ${activePreset.nameEn} (${activeLocalIcon.nameEn}) applied across all screens!",
                        Toast.LENGTH_SHORT
                    ).show()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_all_icons_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = activePreset.primaryTint),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppLocaleStrings.tr(selectedLanguage, "Apply All Icons & Local Theme", "सभी आइकन्स व लोकल थीम लागू करें"),
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

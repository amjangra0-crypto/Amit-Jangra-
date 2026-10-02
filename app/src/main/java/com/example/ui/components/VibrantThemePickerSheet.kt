package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tune
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeBackground
import com.example.ui.theme.AnimeCardBorder
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.AppThemeController
import com.example.ui.theme.ColorSwatchGridData
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VibrantThemeInfo
import com.example.ui.theme.VibrantThemePresets

/**
 * VibrantThemePickerSheet
 * Complete Theme & Color Customizer inspired directly by the user's reference images:
 * 1. Image 1: Theme & Custom Tabs, Token List (Muted, Accent, Destructive, Border, Input)
 * 2. Image 2: Palette Cards with colored gradients & capsule preview buttons
 * 3. Image 4: 10 Aesthetic Designer Palettes (Soft, Powerful, Modern, Futuristic, Natural, Exclusive, Popular, Romantic, Vintage, Traditional)
 * 4. Image 5: 9 Google Messages Material Preview Cards with pill buttons & checkmarks
 * 5. Image 6: 10x8 Color Swatch Matrix, Reset button, THEME circular chips, CUSTOM color creator
 * 6. Default Color: Pure White everywhere
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VibrantThemePickerSheet(
    viewModel: AnimeViewModel,
    selectedTheme: String,
    isDarkMode: Boolean,
    selectedLanguage: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedTab by remember { mutableIntStateOf(0) } // 0: Themes, 1: Color Swatches, 2: Design Tokens

    // Custom Color State
    var customHexInput by remember { mutableStateOf("") }
    var redSlider by remember { mutableFloatStateOf(79f / 255f) }
    var greenSlider by remember { mutableFloatStateOf(70f / 255f) }
    var blueSlider by remember { mutableFloatStateOf(229f / 255f) }
    val sliderColor = remember(redSlider, greenSlider, blueSlider) {
        Color(red = redSlider, green = greenSlider, blue = blueSlider)
    }

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
            // Header Bar matching Image 1
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (AppLocaleStrings.isHindi(selectedLanguage)) "थीम व रंग कस्टमाइज़र" else "Theme & Color Studio",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = if (AppLocaleStrings.isHindi(selectedLanguage)) "प्योर व्हाइट डिफ़ॉल्ट • हर जगह रंग बदलें" else "Pure White Default • Customize Everywhere",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Quick Reset to Pure White Button (matching Image 6)
                    OutlinedButton(
                        onClick = {
                            viewModel.resetThemeToDefaultWhite()
                            AppThemeController.resetToDefaultWhite(context)
                        },
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.testTag("theme_reset_white_btn")
                    ) {
                        Icon(Icons.Default.Refresh, contentDescription = "Reset", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Reset", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(onClick = onDismiss, modifier = Modifier.testTag("theme_sheet_close_btn")) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary, modifier = Modifier.size(20.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Tab Row matching Image 1 ("Theme", "Custom", "Tokens")
            val tabs = listOf(
                "🎨 " + if (AppLocaleStrings.isHindi(selectedLanguage)) "थीम्स" else "Themes",
                "🌈 " + if (AppLocaleStrings.isHindi(selectedLanguage)) "रंग ग्रिड" else "Swatches",
                "📑 " + if (AppLocaleStrings.isHindi(selectedLanguage)) "टोकन" else "Tokens"
            )
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = MaterialTheme.colorScheme.primary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .border(1.dp, AnimeCardBorder, RoundedCornerShape(12.dp))
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                fontSize = 12.sp,
                                fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedTab) {
                // ==========================================
                // TAB 0: PRESET THEMES (IMAGE 2, 4, 5)
                // ==========================================
                0 -> {
                    ThemesTabContent(
                        selectedTheme = selectedTheme,
                        selectedLanguage = selectedLanguage,
                        onThemeSelected = { key ->
                            viewModel.setVibrantTheme(key)
                            AppThemeController.setTheme(key, AppThemeController.isDarkMode, context)
                        }
                    )
                }

                // ==========================================
                // TAB 1: 10x8 COLOR SWATCHES & CUSTOM (IMAGE 6 & 7)
                // ==========================================
                1 -> {
                    ColorSwatchesTabContent(
                        selectedLanguage = selectedLanguage,
                        customHexInput = customHexInput,
                        onCustomHexChange = { customHexInput = it },
                        redSlider = redSlider,
                        onRedChange = { redSlider = it },
                        greenSlider = greenSlider,
                        onGreenChange = { greenSlider = it },
                        blueSlider = blueSlider,
                        onBlueChange = { blueSlider = it },
                        sliderColor = sliderColor,
                        onColorSelected = { color ->
                            viewModel.setCustomThemeColor(color)
                            AppThemeController.setCustomAccent(color, context)
                        },
                        onResetWhite = {
                            viewModel.resetThemeToDefaultWhite()
                            AppThemeController.resetToDefaultWhite(context)
                        }
                    )
                }

                // ==========================================
                // TAB 2: DESIGN TOKENS (IMAGE 1)
                // ==========================================
                2 -> {
                    DesignTokensTabContent(
                        activePreset = AppThemeController.activePreset,
                        selectedLanguage = selectedLanguage
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Default Mobile Theme Switch (Matches mobile phone system theme)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                        Text("📱", fontSize = 18.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (AppLocaleStrings.isHindi(selectedLanguage)) "फ़ोन की डिफ़ॉल्ट थीम (System Mobile Theme)" else "Default Mobile Theme (Follow System)",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (AppLocaleStrings.isHindi(selectedLanguage))
                                    "ऑन करने पर मोबाइल में जो थीम है, वही लागू हो जाएगी"
                                else
                                    "Automatically matches your phone system theme & colors",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = AppThemeController.isFollowSystemTheme,
                        onCheckedChange = { follow ->
                            AppThemeController.setFollowSystemTheme(follow, context)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AnimeCyan,
                            checkedTrackColor = AnimeCyan.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("theme_sheet_follow_system_toggle")
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Dark Mode Switch (Defaults to False / Light Mode Pure White everywhere)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = if (AppThemeController.isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = if (AppThemeController.isDarkMode) "Dark Mode (Vibrant Dark)" else "Light Mode (Pure White Everywhere)",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (AppThemeController.isDarkMode) "Dark slate surfaces with glowing accents" else "Crisp white background, clean cards & high contrast text",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Switch(
                        checked = AppThemeController.isDarkMode,
                        onCheckedChange = { isDark ->
                            viewModel.setTheme(isDark)
                            AppThemeController.setDarkMode(isDark, context)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = MaterialTheme.colorScheme.primary,
                            checkedTrackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.35f)
                        ),
                        modifier = Modifier.testTag("theme_sheet_dark_toggle")
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onDismiss,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("theme_sheet_apply_btn"),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (AppLocaleStrings.isHindi(selectedLanguage)) "थीम व रंग लागू करें (Confirm)" else "Confirm & Apply",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}

/**
 * Tab 0: Themes & Palettes (Image 2, 4, 5)
 */
@Composable
private fun ThemesTabContent(
    selectedTheme: String,
    selectedLanguage: String,
    onThemeSelected: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Section: Dual & Multi-colour Themes (White & Gold, White & Black, etc.)
        Text(
            text = "✨ " + if (AppLocaleStrings.isHindi(selectedLanguage)) "मल्टी-कलर व डुअल थीम्स (Dual Colours)" else "Multi-colour & Dual Themes (White & Gold, White & Black)",
            color = AnimeGold,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        val dualThemes = VibrantThemePresets.all.filter { it.category == "Dual Colour Themes" }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            dualThemes.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    pair.forEach { preset ->
                        val isSelected = selectedTheme.equals(preset.key, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .clickable { onThemeSelected(preset.key) }
                                .testTag("theme_card_dual_${preset.key.lowercase()}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) preset.primaryColor.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) preset.primaryColor else AnimeCardBorder
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(preset.emoji, fontSize = 18.sp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = if (AppLocaleStrings.isHindi(selectedLanguage)) preset.nameHi else preset.nameEn,
                                        color = MaterialTheme.colorScheme.onSurface,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        maxLines = 1
                                    )
                                    Spacer(modifier = Modifier.height(3.dp))
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(preset.lightBackground)
                                                .border(1.dp, Color.Gray, CircleShape)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Box(
                                            modifier = Modifier
                                                .size(10.dp)
                                                .clip(CircleShape)
                                                .background(preset.primaryColor)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = "Dual",
                                            color = TextSecondary,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                                if (isSelected) {
                                    Icon(Icons.Default.Check, contentDescription = "Active", tint = preset.primaryColor, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                    if (pair.size == 1) {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Section A: Google Messages Material Style 9 Cards (Image 5 & 2)
        Text(
            text = "📱 " + if (AppLocaleStrings.isHindi(selectedLanguage)) "गूगल मेसेज स्टाइल 9 थीम्स (Material You)" else "Material Card Themes (Image 5 & 2)",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        val materialThemes = VibrantThemePresets.all.filter { it.category == "Google Messages Style" || it.key == "WHITE_MINIMAL" }
        // Display in a 3x3 Grid matching Image 5
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            materialThemes.chunked(3).forEach { rowPresets ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    rowPresets.forEach { preset ->
                        val isSelected = selectedTheme.equals(preset.key, ignoreCase = true)
                        Card(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(0.78f)
                                .clip(RoundedCornerShape(14.dp))
                                .clickable { onThemeSelected(preset.key) }
                                .testTag("theme_card_${preset.key.lowercase()}"),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) preset.primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                            ),
                            border = BorderStroke(
                                width = if (isSelected) 2.dp else 1.dp,
                                color = if (isSelected) preset.primaryColor else AnimeCardBorder
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                // Top Bubble Preview
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.85f)
                                        .height(14.dp)
                                        .clip(RoundedCornerShape(7.dp))
                                        .background(preset.primaryColor.copy(alpha = 0.2f))
                                )

                                // Center Pill Action Button matching Image 5
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth(0.9f)
                                        .height(28.dp)
                                        .clip(RoundedCornerShape(14.dp))
                                        .background(preset.primaryColor),
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(Icons.Default.Check, contentDescription = "Active", tint = Color.White, modifier = Modifier.size(16.dp))
                                    }
                                }

                                // Bottom Theme Label
                                Text(
                                    text = preset.nameEn.split(" ").first(),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Section B: 10 Aesthetic Designer Palettes (Image 4)
        Text(
            text = "🎨 " + if (AppLocaleStrings.isHindi(selectedLanguage)) "क्लासिक 10 डिज़ाइनर पैलेट (Image 4)" else "10 Aesthetic Designer Palettes (Image 4)",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        val aestheticThemes = VibrantThemePresets.all.filter { it.category == "Classic Aesthetic" || it.key == "TRAVEL_PLANNER" }
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            aestheticThemes.forEach { preset ->
                val isSelected = selectedTheme.equals(preset.key, ignoreCase = true)
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onThemeSelected(preset.key) }
                        .testTag("theme_preset_${preset.key.lowercase()}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) preset.primaryColor.copy(alpha = 0.12f) else MaterialTheme.colorScheme.surfaceVariant
                    ),
                    border = BorderStroke(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) preset.primaryColor else AnimeCardBorder
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Text(preset.emoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = preset.nameEn,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                // Palette Stripe preview matching Image 4
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    listOf(preset.primaryColor, preset.secondaryColor, preset.accentColor, preset.lightBorderColor).forEach { col ->
                                        Box(
                                            modifier = Modifier
                                                .size(16.dp)
                                                .clip(CircleShape)
                                                .background(col)
                                                .border(0.5.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                        )
                                    }
                                }
                            }
                        }

                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(24.dp)
                                    .clip(CircleShape)
                                    .background(preset.primaryColor),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Tab 1: 10x8 Color Swatches & Custom Picker (Image 6 & 7)
 */
@Composable
private fun ColorSwatchesTabContent(
    selectedLanguage: String,
    customHexInput: String,
    onCustomHexChange: (String) -> Unit,
    redSlider: Float,
    onRedChange: (Float) -> Unit,
    greenSlider: Float,
    onGreenChange: (Float) -> Unit,
    blueSlider: Float,
    onBlueChange: (Float) -> Unit,
    sliderColor: Color,
    onColorSelected: (Color) -> Unit,
    onResetWhite: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Section Header matching Image 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "🎨 " + if (AppLocaleStrings.isHindi(selectedLanguage)) "रंग ग्रिड (10x8 Color Swatches)" else "Color Swatch Grid (Image 6)",
                color = MaterialTheme.colorScheme.primary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap any color to apply",
                color = TextSecondary,
                fontSize = 11.sp
            )
        }

        // 10 Columns x 8 Rows Swatch Matrix (Image 6)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AnimeCardBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorSwatchGridData.swatchesMatrix.forEachIndexed { rowIndex, rowColors ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        rowColors.forEachIndexed { colIndex, color ->
                            val isCurrentPrimary = AppThemeController.primaryColor.toArgb() == color.toArgb()
                            Box(
                                modifier = Modifier
                                    .size(26.dp)
                                    .clip(CircleShape)
                                    .background(color)
                                    .border(
                                        width = if (isCurrentPrimary) 2.dp else 1.dp,
                                        color = if (isCurrentPrimary) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.15f),
                                        shape = CircleShape
                                    )
                                    .clickable { onColorSelected(color) }
                                    .testTag("swatch_${rowIndex}_$colIndex"),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isCurrentPrimary) {
                                    Icon(
                                        Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = if (color.toArgb() == Color.White.toArgb()) Color.Black else Color.White,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Quick THEME Circle Chips matching Image 6
        Text(
            text = "THEME PRESET CHIPS:",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            ColorSwatchGridData.quickThemeChips.forEach { chipColor ->
                val isSelected = AppThemeController.primaryColor.toArgb() == chipColor.toArgb()
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(chipColor)
                        .border(
                            width = if (isSelected) 2.5.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Black.copy(alpha = 0.2f),
                            shape = CircleShape
                        )
                        .clickable { onColorSelected(chipColor) },
                    contentAlignment = Alignment.Center
                ) {
                    if (isSelected) {
                        Icon(
                            Icons.Default.Check,
                            contentDescription = null,
                            tint = if (chipColor.toArgb() == Color.White.toArgb()) Color.Black else Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        // CUSTOM COLOR BUILDER matching Image 6 (+)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimeCardBorder)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("CUSTOM COLOR PICKER", color = MaterialTheme.colorScheme.onSurface, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Live Color Preview Chip
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(sliderColor)
                            .border(1.dp, AnimeCardBorder, RoundedCornerShape(8.dp))
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // RGB Sliders
                Text("Red: ${(redSlider * 255).toInt()}", fontSize = 10.sp, color = TextSecondary)
                Slider(
                    value = redSlider,
                    onValueChange = onRedChange,
                    colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
                )

                Text("Green: ${(greenSlider * 255).toInt()}", fontSize = 10.sp, color = TextSecondary)
                Slider(
                    value = greenSlider,
                    onValueChange = onGreenChange,
                    colors = SliderDefaults.colors(thumbColor = Color.Green, activeTrackColor = Color.Green)
                )

                Text("Blue: ${(blueSlider * 255).toInt()}", fontSize = 10.sp, color = TextSecondary)
                Slider(
                    value = blueSlider,
                    onValueChange = onBlueChange,
                    colors = SliderDefaults.colors(thumbColor = Color.Blue, activeTrackColor = Color.Blue)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onColorSelected(sliderColor) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("apply_custom_color_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = sliderColor),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Apply Custom RGB Color", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

/**
 * Tab 2: Design Tokens (Image 1)
 */
@Composable
private fun DesignTokensTabContent(
    activePreset: VibrantThemeInfo,
    selectedLanguage: String
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "📑 " + if (AppLocaleStrings.isHindi(selectedLanguage)) "डिज़ाइन सिस्टम टोकन (Image 1)" else "Theme Tokens (Image 1)",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        val tokens = listOf(
            Triple("Secondary-foreground", activePreset.secondaryForeground, "#44334D"),
            Triple("Muted", activePreset.mutedColor, "#F2EDF3"),
            Triple("Muted-foreground", activePreset.mutedForeground, "#866C93"),
            Triple("Accent", activePreset.accentColor, "#E49181"),
            Triple("Accent-foreground", Color.White, "#FFFFFF"),
            Triple("Destructive", activePreset.destructiveColor, "#EF4444"),
            Triple("Destructive-foreground", Color(0xFFFAFAFA), "#FAFAFA"),
            Triple("Border", activePreset.lightBorderColor, "#E4DAE7"),
            Triple("Input", activePreset.lightBorderColor, "#E4DAE7"),
            Triple("Default Background", Color(0xFFFFFFFF), "#FFFFFF"),
            Triple("Default Surface", Color(0xFFFFFFFF), "#FFFFFF")
        )

        tokens.forEach { (tokenName, tokenColor, defaultHex) ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, AnimeCardBorder)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = tokenName,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = defaultHex,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(tokenColor)
                                .border(1.dp, Color.Black.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        )
                    }
                }
            }
        }
    }
}

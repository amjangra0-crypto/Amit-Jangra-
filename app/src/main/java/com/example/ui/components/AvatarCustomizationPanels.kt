package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CharacterProfile
import com.example.ui.screens.ColorSwatch
import com.example.ui.screens.HairStyleOption
import com.example.ui.screens.OutfitOption
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
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
 * Hair Customization with Style Dropdown, Color Dropdown + Swatches,
 * and Sliders for Length, Volume, and Shine.
 */
@Composable
fun HairCustomizationSlidersAndDropdownPanel(
    currentStyle: String,
    currentColor: String,
    hairLength: Float,
    hairVolume: Float,
    hairShine: Float,
    styles: List<HairStyleOption>,
    colors: List<ColorSwatch>,
    onStyleSelected: (String) -> Unit,
    onColorSelected: (String) -> Unit,
    onLengthChanged: (Float) -> Unit,
    onVolumeChanged: (Float) -> Unit,
    onShineChanged: (Float) -> Unit
) {
    var isStyleDropdownOpen by remember { mutableStateOf(false) }
    var isColorDropdownOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- 1. HAIR STYLE DROPDOWN ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Face, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Anime Hair Style (Dropdown Selector):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isStyleDropdownOpen = !isStyleDropdownOpen }
                    .testTag("hair_style_dropdown_trigger"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isStyleDropdownOpen) AnimeCyan else AnimePurple.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = currentStyle, color = AnimeCyanLight, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        val desc = styles.find { it.name.equals(currentStyle, ignoreCase = true) }?.description ?: "Customized Hair Style"
                        Text(text = desc, color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(
                        imageVector = if (isStyleDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = AnimeCyan
                    )
                }
            }

            DropdownMenu(
                expanded = isStyleDropdownOpen,
                onDismissRequest = { isStyleDropdownOpen = false },
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(AnimeSurface)
                    .border(1.dp, AnimeCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            ) {
                styles.forEach { style ->
                    val isSel = currentStyle.equals(style.name, ignoreCase = true)
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = style.name,
                                        color = if (isSel) AnimeCyanLight else TextPrimary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                    Text(text = "${style.category} • ${style.description}", color = TextSecondary, fontSize = 10.sp)
                                }
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        onClick = {
                            onStyleSelected(style.name)
                            isStyleDropdownOpen = false
                        },
                        modifier = Modifier.testTag("hair_style_item_${style.id}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. HAIR COLOR DROPDOWN & SWATCHES ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Hair Color Palette:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(
                onClick = { isColorDropdownOpen = !isColorDropdownOpen },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Dropdown Menu ▼", color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Swatches LazyRow
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(colors) { swatch ->
                val isSelected = currentColor.equals(swatch.name, ignoreCase = true) ||
                        currentColor.contains(swatch.name.split(" ").first(), ignoreCase = true)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AnimePurple.copy(alpha = 0.35f) else AnimeSurface)
                        .border(1.5.dp, if (isSelected) swatch.color else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { onColorSelected(swatch.name) }
                        .padding(6.dp)
                        .testTag("hair_color_swatch_${swatch.name.lowercase().replace(" ", "_")}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(swatch.color)
                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = swatch.name.split(" ").first(),
                        color = if (isSelected) AnimeCyanLight else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        DropdownMenu(
            expanded = isColorDropdownOpen,
            onDismissRequest = { isColorDropdownOpen = false },
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(AnimeSurface)
                .border(1.dp, AnimePink.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        ) {
            colors.forEach { swatch ->
                val isSel = currentColor.equals(swatch.name, ignoreCase = true)
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(swatch.color)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = swatch.name, color = if (isSel) AnimePink else TextPrimary, fontSize = 12.sp)
                        }
                    },
                    onClick = {
                        onColorSelected(swatch.name)
                        isColorDropdownOpen = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- 3. HAIR SLIDERS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hair Property Sliders:",
                        color = AnimeGold,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Slider 1: Hair Length
                val lengthLabel = when {
                    hairLength < 0.8f -> "Short Crop (${"%.1f".format(hairLength)}x)"
                    hairLength < 1.3f -> "Medium Shoulder (${"%.1f".format(hairLength)}x)"
                    hairLength < 1.7f -> "Long Flowing (${"%.1f".format(hairLength)}x)"
                    else -> "Celestial Floor Length (${"%.1f".format(hairLength)}x)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hair Length:", color = TextSecondary, fontSize = 11.sp)
                    Text(lengthLabel, color = AnimeCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = hairLength,
                    onValueChange = onLengthChanged,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyan),
                    modifier = Modifier.testTag("hair_length_slider")
                )

                // Slider 2: Hair Volume / Density
                val volumeLabel = when {
                    hairVolume < 0.8f -> "Sleek & Flat (${"%.1f".format(hairVolume)}x)"
                    hairVolume < 1.3f -> "Natural Volume (${"%.1f".format(hairVolume)}x)"
                    hairVolume < 1.7f -> "Voluminous Fluff (${"%.1f".format(hairVolume)}x)"
                    else -> "Ultra Spiky Anime Spikes (${"%.1f".format(hairVolume)}x)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hair Volume & Density:", color = TextSecondary, fontSize = 11.sp)
                    Text(volumeLabel, color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = hairVolume,
                    onValueChange = onVolumeChanged,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink),
                    modifier = Modifier.testTag("hair_volume_slider")
                )

                // Slider 3: Hair Anime Highlights & Shine
                val shinePercent = (hairShine * 100).toInt()
                val shineLabel = when {
                    hairShine < 0.25f -> "Matte Finish ($shinePercent%)"
                    hairShine < 0.65f -> "Silky Shimmer ($shinePercent%)"
                    else -> "Ultra Gloss Anime Star ($shinePercent%)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Hair Shine & Highlights:", color = TextSecondary, fontSize = 11.sp)
                    Text(shineLabel, color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = hairShine,
                    onValueChange = onShineChanged,
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AnimeGold, activeTrackColor = AnimeGold),
                    modifier = Modifier.testTag("hair_shine_slider")
                )
            }
        }
    }
}

/**
 * Eyes Customization with Eye Shape Dropdown, Eye Color Dropdown + Swatches,
 * Expression Dropdown, and Sliders for Size, Slant/Tilt, and Pupil Glow.
 */
@Composable
fun EyesCustomizationSlidersAndDropdownPanel(
    currentColor: String,
    currentExpression: String,
    eyeSize: Float,
    eyeTilt: Float,
    eyePupilGlow: Float,
    colors: List<ColorSwatch>,
    expressions: List<Pair<String, String>>,
    onColorSelected: (String) -> Unit,
    onExpressionSelected: (String) -> Unit,
    onSizeChanged: (Float) -> Unit,
    onTiltChanged: (Float) -> Unit,
    onGlowChanged: (Float) -> Unit
) {
    var isColorDropdownOpen by remember { mutableStateOf(false) }
    var isExpressionDropdownOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- 1. EYE EXPRESSION & GAZE DROPDOWN ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Face, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Expression & Gaze (Dropdown Selector):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpressionDropdownOpen = !isExpressionDropdownOpen }
                    .testTag("eye_expression_dropdown_trigger"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isExpressionDropdownOpen) AnimeGold else AnimePurple.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        val activeLabel = expressions.find { it.first.equals(currentExpression, ignoreCase = true) }?.second ?: currentExpression
                        Text(text = activeLabel, color = AnimeGold, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        Text(text = currentExpression, color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(
                        imageVector = if (isExpressionDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = AnimeGold
                    )
                }
            }

            DropdownMenu(
                expanded = isExpressionDropdownOpen,
                onDismissRequest = { isExpressionDropdownOpen = false },
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(AnimeSurface)
                    .border(1.dp, AnimeGold.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            ) {
                expressions.forEach { (exprKey, label) ->
                    val isSel = currentExpression.equals(exprKey, ignoreCase = true)
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = label,
                                        color = if (isSel) AnimeGold else TextPrimary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                        fontSize = 13.sp
                                    )
                                    Text(text = exprKey, color = TextSecondary, fontSize = 10.sp)
                                }
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        onClick = {
                            onExpressionSelected(exprKey)
                            isExpressionDropdownOpen = false
                        },
                        modifier = Modifier.testTag("eye_expr_item_${exprKey.lowercase().replace(" ", "_")}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. EYE COLOR DROPDOWN & SWATCHES ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Visibility, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Anime Eye Color:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(
                onClick = { isColorDropdownOpen = !isColorDropdownOpen },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Dropdown Menu ▼", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        // Swatches LazyRow
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            items(colors) { swatch ->
                val isSelected = currentColor.equals(swatch.name, ignoreCase = true) ||
                        currentColor.contains(swatch.name.split(" ").first(), ignoreCase = true)
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (isSelected) AnimePurple.copy(alpha = 0.35f) else AnimeSurface)
                        .border(1.5.dp, if (isSelected) swatch.color else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { onColorSelected(swatch.name) }
                        .padding(6.dp)
                        .testTag("eye_color_swatch_${swatch.name.lowercase().replace(" ", "_")}")
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(swatch.color)
                            .border(1.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = swatch.name.split(" ").first(),
                        color = if (isSelected) AnimeCyanLight else TextSecondary,
                        fontSize = 10.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        DropdownMenu(
            expanded = isColorDropdownOpen,
            onDismissRequest = { isColorDropdownOpen = false },
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(AnimeSurface)
                .border(1.dp, AnimeCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        ) {
            colors.forEach { swatch ->
                val isSel = currentColor.equals(swatch.name, ignoreCase = true)
                DropdownMenuItem(
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(18.dp)
                                    .clip(CircleShape)
                                    .background(swatch.color)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(text = swatch.name, color = if (isSel) AnimeCyanLight else TextPrimary, fontSize = 12.sp)
                        }
                    },
                    onClick = {
                        onColorSelected(swatch.name)
                        isColorDropdownOpen = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- 3. EYE SLIDERS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Eye Proportion & Glow Sliders:",
                        color = AnimeCyan,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Slider 1: Eye Size
                val sizeLabel = when {
                    eyeSize < 0.85f -> "Compact Shonen Eyes (${"%.1f".format(eyeSize)}x)"
                    eyeSize < 1.2f -> "Standard Anime Eyes (${"%.1f".format(eyeSize)}x)"
                    else -> "Large Kawaii Eyes (${"%.1f".format(eyeSize)}x)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Eye Size & Aperture:", color = TextSecondary, fontSize = 11.sp)
                    Text(sizeLabel, color = AnimeCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = eyeSize,
                    onValueChange = onSizeChanged,
                    valueRange = 0.7f..1.5f,
                    colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyan),
                    modifier = Modifier.testTag("eye_size_slider")
                )

                // Slider 2: Eye Tilt / Slant
                val tiltLabel = when {
                    eyeTilt < -5f -> "Droopy / Soft Kawaii (${eyeTilt.toInt()}°)"
                    eyeTilt > 5f -> "Sharp / Anti-Hero Fierce (+${eyeTilt.toInt()}°)"
                    else -> "Balanced Neutral (${eyeTilt.toInt()}°)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Eye Slant / Tilt:", color = TextSecondary, fontSize = 11.sp)
                    Text(tiltLabel, color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = eyeTilt,
                    onValueChange = onTiltChanged,
                    valueRange = -15f..15f,
                    colors = SliderDefaults.colors(thumbColor = AnimeGold, activeTrackColor = AnimeGold),
                    modifier = Modifier.testTag("eye_tilt_slider")
                )

                // Slider 3: Eye Pupil Glow / Reflection
                val glowPercent = (eyePupilGlow * 100).toInt()
                val glowLabel = when {
                    eyePupilGlow < 0.3f -> "Deep Pupil ($glowPercent%)"
                    eyePupilGlow < 0.7f -> "Luminous Highlight ($glowPercent%)"
                    else -> "Blazing Star Glow ($glowPercent%)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Pupil Glow & Star Reflection:", color = TextSecondary, fontSize = 11.sp)
                    Text(glowLabel, color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = eyePupilGlow,
                    onValueChange = onGlowChanged,
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink),
                    modifier = Modifier.testTag("eye_glow_slider")
                )
            }
        }
    }
}

/**
 * Outfit Customization with Archetype Dropdown, Color Combo Dropdown,
 * and Sliders for Fit/Tightness, Neon Glow, and Cape/Sash Length.
 */
@Composable
fun OutfitsCustomizationSlidersAndDropdownPanel(
    currentOutfit: String,
    currentColor: String,
    outfitFit: Float,
    outfitGlow: Float,
    capeLength: Float,
    outfits: List<OutfitOption>,
    colorCombos: List<String>,
    onOutfitSelected: (OutfitOption) -> Unit,
    onColorSelected: (String) -> Unit,
    onFitChanged: (Float) -> Unit,
    onGlowChanged: (Float) -> Unit,
    onCapeChanged: (Float) -> Unit
) {
    var isOutfitDropdownOpen by remember { mutableStateOf(false) }
    var isColorComboDropdownOpen by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        // --- 1. OUTFIT ARCHETYPE DROPDOWN ---
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Shield, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = "Outfit Archetype (Dropdown Selector):",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(6.dp))

        Box(modifier = Modifier.fillMaxWidth()) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isOutfitDropdownOpen = !isOutfitDropdownOpen }
                    .testTag("outfit_dropdown_trigger"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, if (isOutfitDropdownOpen) AnimePink else AnimePurple.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = currentOutfit, color = AnimePink, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                        val out = outfits.find { it.name.equals(currentOutfit, ignoreCase = true) }
                        Text(text = "${out?.archetype ?: "Anime Style"} • ${out?.description ?: ""}", color = TextSecondary, fontSize = 11.sp)
                    }
                    Icon(
                        imageVector = if (isOutfitDropdownOpen) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = null,
                        tint = AnimePink
                    )
                }
            }

            DropdownMenu(
                expanded = isOutfitDropdownOpen,
                onDismissRequest = { isOutfitDropdownOpen = false },
                modifier = Modifier
                    .fillMaxWidth(0.92f)
                    .background(AnimeSurface)
                    .border(1.dp, AnimePink.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            ) {
                outfits.forEach { outfit ->
                    val isSel = currentOutfit.equals(outfit.name, ignoreCase = true)
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = outfit.name,
                                        color = if (isSel) AnimePink else TextPrimary,
                                        fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 13.sp
                                    )
                                    Text(text = "${outfit.archetype} • ${outfit.description}", color = TextSecondary, fontSize = 10.sp)
                                }
                                if (isSel) {
                                    Icon(Icons.Default.Check, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                                }
                            }
                        },
                        onClick = {
                            onOutfitSelected(outfit)
                            isOutfitDropdownOpen = false
                        },
                        modifier = Modifier.testTag("outfit_item_${outfit.id}")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // --- 2. OUTFIT COLOR COMBOS DROPDOWN & CHIPS ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Palette, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Outfit Color Scheme:",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            TextButton(
                onClick = { isColorComboDropdownOpen = !isColorComboDropdownOpen },
                contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text("Dropdown Menu ▼", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(4.dp))

        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(colorCombos) { combo ->
                val isSelected = currentColor.equals(combo, ignoreCase = true)
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (isSelected) AnimePurple.copy(alpha = 0.4f) else AnimeSurface)
                        .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(8.dp))
                        .clickable { onColorSelected(combo) }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                        .testTag("outfit_color_chip_${combo.lowercase().take(6)}")
                ) {
                    Text(
                        text = combo,
                        color = if (isSelected) AnimeCyanLight else TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        DropdownMenu(
            expanded = isColorComboDropdownOpen,
            onDismissRequest = { isColorComboDropdownOpen = false },
            modifier = Modifier
                .fillMaxWidth(0.92f)
                .background(AnimeSurface)
                .border(1.dp, AnimeCyan.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
        ) {
            colorCombos.forEach { combo ->
                val isSel = currentColor.equals(combo, ignoreCase = true)
                DropdownMenuItem(
                    text = {
                        Text(text = combo, color = if (isSel) AnimeCyanLight else TextPrimary, fontSize = 12.sp)
                    },
                    onClick = {
                        onColorSelected(combo)
                        isColorComboDropdownOpen = false
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // --- 3. OUTFIT SLIDERS ---
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Tune, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Outfit Fit & Trim Sliders:",
                        color = AnimePink,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Slider 1: Outfit Fit
                val fitLabel = when {
                    outfitFit < 0.85f -> "Skin-Tight Exo-Suit (${"%.1f".format(outfitFit)}x)"
                    outfitFit < 1.25f -> "Standard Tailored (${"%.1f".format(outfitFit)}x)"
                    else -> "Loose & Baggy Robes (${"%.1f".format(outfitFit)}x)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Outfit Fit / Tightness:", color = TextSecondary, fontSize = 11.sp)
                    Text(fitLabel, color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = outfitFit,
                    onValueChange = onFitChanged,
                    valueRange = 0.6f..1.6f,
                    colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink),
                    modifier = Modifier.testTag("outfit_fit_slider")
                )

                // Slider 2: Neon Accent / Glow
                val glowPercent = (outfitGlow * 100).toInt()
                val glowLabel = when {
                    outfitGlow < 0.25f -> "Off / Matte ($glowPercent%)"
                    outfitGlow < 0.7f -> "Subtle Cyber Neon ($glowPercent%)"
                    else -> "Ultra Blazing Radiance ($glowPercent%)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Neon Trim & Accent Glow:", color = TextSecondary, fontSize = 11.sp)
                    Text(glowLabel, color = AnimeCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = outfitGlow,
                    onValueChange = onGlowChanged,
                    valueRange = 0.0f..1.0f,
                    colors = SliderDefaults.colors(thumbColor = AnimeCyan, activeTrackColor = AnimeCyan),
                    modifier = Modifier.testTag("outfit_glow_slider")
                )

                // Slider 3: Cape / Robe Length
                val capeLabel = when {
                    capeLength < 0.8f -> "Cropped Sash (${"%.1f".format(capeLength)}x)"
                    capeLength < 1.3f -> "Waist Robe (${"%.1f".format(capeLength)}x)"
                    else -> "Flowing Celestial Trail (${"%.1f".format(capeLength)}x)"
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Cape & Robe Length:", color = TextSecondary, fontSize = 11.sp)
                    Text(capeLabel, color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = capeLength,
                    onValueChange = onCapeChanged,
                    valueRange = 0.5f..2.0f,
                    colors = SliderDefaults.colors(thumbColor = AnimeGold, activeTrackColor = AnimeGold),
                    modifier = Modifier.testTag("cape_length_slider")
                )
            }
        }
    }
}

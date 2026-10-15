package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DefaultExportPresets
import com.example.data.model.ExportPreset
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
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
 * Configuration screen for Export Presets:
 * Saves combinations of Resolution (720p/1080p/4K), Aspect Ratio (9:16, 16:9, 1:1),
 * and Frame Rates (30, 45, 60, 75, 90 fps) assigned to platforms (YouTube, Instagram, Facebook).
 */
@Composable
fun ExportPresetsScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage

    val customPresets by viewModel.customExportPresets.collectAsState()
    val selectedPreset by viewModel.activeExportPreset.collectAsState()

    var showCreateDialog by remember { mutableStateOf(false) }

    // Creation Form States
    var newPresetName by remember { mutableStateOf("") }
    var selectedRes by remember { mutableStateOf("1080p Full HD") }
    var selectedRatio by remember { mutableStateOf("16:9") }
    var selectedFps by remember { mutableIntStateOf(60) }
    var selectedPlatform by remember { mutableStateOf("YouTube") }

    val allPresets = remember(customPresets) {
        DefaultExportPresets.presets + customPresets
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("export_presets_screen")
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("export_presets_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Video Export Presets", "वीडियो एक्सपोर्ट प्रीसेट्स"),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeCyan.copy(alpha = 0.2f))
                            .border(1.dp, AnimeCyan, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${allPresets.size} PRESETS",
                            color = AnimeCyan,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Resolution (720p/1080p/4K), Aspect (9:16/16:9), FPS (30/45/60/75/90) & Platforms",
                        "रिज़ॉल्यूशन, आस्पेक्ट रेशियो, फ्रेम रेट्स (30 से 90 FPS) व सोशल प्लेटफॉर्म प्रीसेट्स"
                    ),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ACTIVE PRESET BANNER
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.5.dp, AnimeGold)
        ) {
            Row(
                modifier = Modifier.padding(14.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AnimeGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Tune, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Active Target: ${selectedPreset.name}",
                                color = AnimeGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "${selectedPreset.resolutionLabel} • ${selectedPreset.aspectRatio} • ${selectedPreset.fps} FPS • ${selectedPreset.targetPlatform}",
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeGold)
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text("ACTIVE", color = Color.Black, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ADD NEW CUSTOM PRESET BUTTON
        if (!showCreateDialog) {
            Button(
                onClick = { showCreateDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().testTag("create_custom_preset_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(lang, "Create Custom Preset (30 to 90 FPS)", "नया कस्टम प्रीसेट बनाएं (30-90 FPS)"),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        } else {
            // EXPANDED PRESET BUILDER CARD
            Card(
                modifier = Modifier.fillMaxWidth().testTag("custom_preset_builder_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AnimeCyan)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "New Custom Export Preset",
                            color = AnimeCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showCreateDialog = false }) {
                            Icon(Icons.Default.Delete, contentDescription = "Close", tint = TextMuted)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = newPresetName,
                        onValueChange = { newPresetName = it },
                        label = { Text("Preset Name (e.g. Anime Shonen 90 FPS Action)", fontSize = 11.sp) },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("preset_name_input")
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 1. Resolution Selection
                    Text("1. Resolution (रिज़ॉल्यूशन):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("720p HD", "1080p Full HD", "4K Ultra HD").forEach { res ->
                            FilterChip(
                                selected = selectedRes == res,
                                onClick = { selectedRes = res },
                                label = { Text(res, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimeCyan.copy(alpha = 0.3f),
                                    selectedLabelColor = AnimeCyan
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 2. Aspect Ratio Selection
                    Text("2. Aspect Ratio (आस्पेक्ट रेशियो):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("16:9" to "Landscape (YouTube)", "9:16" to "Vertical (Reels/Shorts)", "1:1" to "Square").forEach { (ratio, label) ->
                            FilterChip(
                                selected = selectedRatio == ratio,
                                onClick = { selectedRatio = ratio },
                                label = { Text(label, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimeGold.copy(alpha = 0.3f),
                                    selectedLabelColor = AnimeGold
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 3. FRAME RATE SELECTION (30, 45, 60, 75, 90 FPS)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "3. Frame Rate: 30 / 45 / 60 / 75 / 90 FPS:",
                            color = AnimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf(30, 45, 60, 75, 90).forEach { fps ->
                            FilterChip(
                                selected = selectedFps == fps,
                                onClick = { selectedFps = fps },
                                label = { Text("${fps} fps", fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimeGreen.copy(alpha = 0.35f),
                                    selectedLabelColor = AnimeGreen
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // 4. Target Social Platform Selection
                    Text("4. Assign to Social Platform:", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("YouTube", "Instagram", "Facebook", "TikTok", "All Platforms").forEach { plat ->
                            FilterChip(
                                selected = selectedPlatform == plat,
                                onClick = { selectedPlatform = plat },
                                label = { Text(plat, fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = AnimePink.copy(alpha = 0.3f),
                                    selectedLabelColor = AnimePink
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Save Preset Action
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        OutlinedButton(
                            onClick = { showCreateDialog = false },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Cancel", color = TextMuted, fontSize = 11.sp)
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Button(
                            onClick = {
                                if (newPresetName.isNotBlank()) {
                                    val (w, h) = when {
                                        selectedRes.startsWith("4K") && selectedRatio == "9:16" -> 2160 to 3840
                                        selectedRes.startsWith("4K") -> 3840 to 2160
                                        selectedRes.startsWith("720p") && selectedRatio == "9:16" -> 720 to 1280
                                        selectedRes.startsWith("720p") -> 1280 to 720
                                        selectedRatio == "9:16" -> 1080 to 1920
                                        selectedRatio == "1:1" -> 1080 to 1080
                                        else -> 1920 to 1080
                                    }
                                    val newPreset = ExportPreset(
                                        name = newPresetName.trim(),
                                        resolutionLabel = selectedRes,
                                        width = w,
                                        height = h,
                                        aspectRatio = selectedRatio,
                                        fps = selectedFps,
                                        targetPlatform = selectedPlatform,
                                        isCustom = true,
                                        description = "Custom preset: $selectedRes @ ${selectedFps}fps for $selectedPlatform"
                                    )
                                    viewModel.saveCustomExportPreset(newPreset)
                                    Toast.makeText(context, "✅ Preset '${newPreset.name}' saved!", Toast.LENGTH_SHORT).show()
                                    showCreateDialog = false
                                    newPresetName = ""
                                } else {
                                    Toast.makeText(context, "Please enter a preset name", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.testTag("save_preset_submit_btn")
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Save Preset", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // LIST OF PRESETS
        Text(
            text = "AVAILABLE PRESETS & PLATFORM CONFIGS:",
            color = AnimeCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn(
            modifier = Modifier.fillMaxWidth().weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(allPresets, key = { it.id }) { preset ->
                val isSelected = selectedPreset.id == preset.id
                PresetItemCard(
                    preset = preset,
                    isSelected = isSelected,
                    onSelect = {
                        viewModel.selectExportPreset(preset)
                        Toast.makeText(context, "🎯 Preset applied: ${preset.name}", Toast.LENGTH_SHORT).show()
                    },
                    onDelete = if (preset.isCustom) {
                        { viewModel.deleteCustomExportPreset(preset.id) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun PresetItemCard(
    preset: ExportPreset,
    isSelected: Boolean,
    onSelect: () -> Unit,
    onDelete: (() -> Unit)?
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("preset_card_${preset.id}")
            .clickable { onSelect() },
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AnimeCyan.copy(alpha = 0.12f) else AnimeSurface
        ),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 0.8.dp,
            color = if (isSelected) AnimeCyan else Color.White.copy(alpha = 0.1f)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = preset.name,
                        color = if (isSelected) AnimeCyan else TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    if (preset.isCustom) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimePurple.copy(alpha = 0.2f))
                                .padding(horizontal = 5.dp, vertical = 1.dp)
                        ) {
                            Text("CUSTOM", color = AnimePurple, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "📺 ${preset.resolutionLabel}", color = TextSecondary, fontSize = 10.sp)
                    Text(text = "📐 ${preset.aspectRatio}", color = TextSecondary, fontSize = 10.sp)
                    Text(text = "⚡ ${preset.fps} FPS", color = AnimeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(text = "🎯 ${preset.targetPlatform}", color = AnimeGold, fontSize = 10.sp)
                }

                Text(
                    text = preset.description,
                    color = TextMuted,
                    fontSize = 9.sp,
                    maxLines = 1
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Selected",
                        tint = AnimeCyan,
                        modifier = Modifier.size(22.dp)
                    )
                } else {
                    OutlinedButton(
                        onClick = onSelect,
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp)
                    ) {
                        Text("Apply", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                if (onDelete != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(26.dp)
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete Preset", tint = AnimePink, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

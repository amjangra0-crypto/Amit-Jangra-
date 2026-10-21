package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

data class AspectRatioOption(
    val ratio: String, // "9:16", "16:9", "1:1"
    val title: String,
    val subtitle: String,
    val platforms: String,
    val width: Int,
    val height: Int,
    val previewAspect: Float
)

/**
 * Project Settings Screen Component
 * Allows users to toggle between common aspect ratios:
 * - 9:16 (TikTok / YouTube Shorts / Instagram Reels)
 * - 16:9 (YouTube / Cinematic Landscape)
 * - 1:1 (Instagram Feed / Square)
 * using an intuitive Radio Button group, visual frame preview, and Room persistence.
 */
@Composable
fun ProjectSettingsScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val currentAspect by viewModel.activeAspectRatio.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage

    val options = remember {
        listOf(
            AspectRatioOption(
                ratio = "9:16",
                title = "Vertical / Mobile (9:16)",
                subtitle = "Optimized for vertical video feeds",
                platforms = "TikTok • YouTube Shorts • IG Reels",
                width = 1080,
                height = 1920,
                previewAspect = 9f / 16f
            ),
            AspectRatioOption(
                ratio = "16:9",
                title = "Widescreen / Landscape (16:9)",
                subtitle = "Standard cinematic anime television format",
                platforms = "YouTube • TV • Web • Monitors",
                width = 1920,
                height = 1080,
                previewAspect = 16f / 9f
            ),
            AspectRatioOption(
                ratio = "1:1",
                title = "Square Feed (1:1)",
                subtitle = "Balanced framing for photo & grid posts",
                platforms = "Instagram Feed • Facebook • WhatsApp",
                width = 1080,
                height = 1080,
                previewAspect = 1f
            )
        )
    }

    var selectedRatio by remember(currentAspect) { mutableStateOf(currentAspect) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("project_settings_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.testTag("project_settings_back_btn")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = MaterialTheme.colorScheme.onBackground
                )
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = AppLocaleStrings.tr(lang, "Project Settings", "प्रोजेक्ट सेटिंग्स व पहलू अनुपात"),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = AppLocaleStrings.tr(lang, "Configure aspect ratio, canvas framing & export dimensions", "पहलू अनुपात (Aspect Ratio) व कैनवास सेटिंग्स"),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Visual Canvas Framing Preview
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "CANVAS FRAMING PREVIEW",
                    color = AnimeCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(10.dp))

                val activeOpt = options.find { it.ratio == selectedRatio } ?: options.first()

                // Framing Box
                Box(
                    modifier = Modifier
                        .height(140.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(130.dp)
                            .aspectRatio(activeOpt.previewAspect, matchHeightConstraintsFirst = true)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF0B0E17))
                            .border(2.dp, AnimeCyan, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Movie,
                                contentDescription = null,
                                tint = AnimeGold,
                                modifier = Modifier.size(24.dp)
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = activeOpt.ratio,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${activeOpt.width} x ${activeOpt.height}",
                                color = TextMuted,
                                fontSize = 9.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Radio Button Group for Aspect Ratio Selection
        Text(
            text = "SELECT ASPECT RATIO:",
            color = AnimeCyan,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(8.dp))

        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            options.forEach { option ->
                val isSelected = selectedRatio == option.ratio
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedRatio = option.ratio }
                        .testTag("aspect_ratio_card_${option.ratio.replace(':', '_')}"),
                    colors = CardDefaults.cardColors(
                        containerColor = if (isSelected) AnimeSurfaceVariant else AnimeSurface
                    ),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(
                        if (isSelected) 1.5.dp else 1.dp,
                        if (isSelected) AnimeCyan else AnimePurple.copy(alpha = 0.4f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = isSelected,
                            onClick = { selectedRatio = option.ratio },
                            colors = RadioButtonDefaults.colors(
                                selectedColor = AnimeCyan,
                                unselectedColor = TextMuted
                            )
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = option.title,
                                    color = if (isSelected) AnimeCyan else TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                    Text(
                                        text = "${option.width}x${option.height}",
                                        color = AnimeGold,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = option.subtitle,
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "🎯 ${option.platforms}",
                                color = TextMuted,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Save & Apply Button
        Button(
            onClick = {
                viewModel.setActiveAspectRatio(selectedRatio)
                Toast.makeText(context, "✅ Aspect Ratio set to $selectedRatio for this project!", Toast.LENGTH_SHORT).show()
                onNavigateBack()
            },
            colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("save_project_settings_btn")
        ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black)
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Apply Aspect Ratio ($selectedRatio)",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

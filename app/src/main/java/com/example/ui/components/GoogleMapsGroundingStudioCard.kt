package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeStudioUiState
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
 * Google Maps Grounding Component for Anime Studio.
 * Uses gemini-3.8-flash with the googleMaps tool to scout real-world landmarks,
 * architectural blueprints, and lighting cues for anime scenes.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun GoogleMapsGroundingStudioCard(
    uiState: AnimeStudioUiState,
    onScoutLocation: (String) -> Unit,
    onApplyToPrompt: (String) -> Unit,
    onRefreshGps: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.appInterfaceLanguage
    val isHindi = AppLocaleStrings.isHindi(lang)

    var locationInput by remember {
        mutableStateOf(if (uiState.activeGpsCountry.contains("Detecting") || uiState.activeGpsCountry.contains("Global")) "Akihabara & Tokyo Tower" else "${uiState.activeGpsCountry} Iconic Landmarks")
    }

    val presetLocations = remember(uiState.activeGpsCountry) {
        listOf(
            "Current GPS (${uiState.activeGpsCountry})",
            "Shibuya Crossing & Sky, Tokyo",
            "Fushimi Inari Taisha, Kyoto",
            "Red Fort & Old Delhi Heritage",
            "Marine Drive & Gateway, Mumbai",
            "Gothic Quarter, Barcelona"
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("google_maps_grounding_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePink, AnimeGold)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AnimeCyan, AnimePink))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Map,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isHindi) "गूगल मैप्स डेटा व ग्राउंडिंग (Gemini 3.8 Flash)" else "Google Maps Data Grounding (Gemini 3.8 Flash)",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (isHindi) "वास्तविक स्थानों के आधार पर सिनेमैटिक एनिमे सीन्स तैयार करें" else "Ground real places into anime scene blueprints via googleMaps tool",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Active GPS Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimePurple.copy(alpha = 0.2f))
                        .border(1.dp, AnimePurple.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .clickable { onRefreshGps() }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Place, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = uiState.activeGpsCountry.take(14),
                            color = AnimeGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Preset Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                presetLocations.forEach { preset ->
                    val cleanPreset = if (preset.startsWith("Current GPS")) uiState.activeGpsCountry else preset
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (locationInput == cleanPreset) AnimeCyan.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                            .border(
                                1.dp,
                                if (locationInput == cleanPreset) AnimeCyan else Color.White.copy(alpha = 0.1f),
                                RoundedCornerShape(6.dp)
                            )
                            .clickable {
                                locationInput = cleanPreset
                                onScoutLocation(cleanPreset)
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = preset,
                            color = if (locationInput == cleanPreset) AnimeCyan else TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Search Bar & Scout Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("maps_location_search_input"),
                    placeholder = {
                        Text(
                            text = if (isHindi) "शहर, लैंडमार्क या स्थान का नाम..." else "Enter city, landmark, or GPS spot...",
                            fontSize = 12.sp,
                            color = TextMuted
                        )
                    },
                    leadingIcon = {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        unfocusedBorderColor = Color.White.copy(alpha = 0.2f),
                        focusedContainerColor = AnimeSurfaceVariant,
                        unfocusedContainerColor = AnimeSurfaceVariant
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = {
                        if (locationInput.isNotBlank()) {
                            onScoutLocation(locationInput)
                        }
                    },
                    enabled = !uiState.isScoutingMapsLocations,
                    modifier = Modifier
                        .height(50.dp)
                        .testTag("scout_maps_location_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    if (uiState.isScoutingMapsLocations) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    } else {
                        Icon(Icons.Default.MyLocation, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    }
                }
            }

            // Results Display
            AnimatedVisibility(visible = uiState.mapsScoutedLocations.isNotBlank()) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(AnimeSurfaceVariant)
                            .border(1.dp, AnimeGreen.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isHindi) "गूगल मैप्स ग्राउंडेड ब्लूप्रिंट परिणाम:" else "Maps Grounded Scene Blueprint:",
                                        color = AnimeGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }

                                Button(
                                    onClick = {
                                        onApplyToPrompt(uiState.mapsScoutedLocations)
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = if (isHindi) "प्रॉम्प्ट में जोड़ें" else "Apply to Prompt",
                                        color = Color.Black,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = uiState.mapsScoutedLocations,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

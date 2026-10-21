package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
import kotlin.math.roundToInt

/**
 * Voice Mood definition for audio filtering.
 */
data class VoiceMoodOption(
    val id: String,
    val name: String,
    val title: String,
    val subtitle: String,
    val pitchMultiplier: Float,
    val speedMultiplier: Float,
    val accentColor: Color,
    val icon: ImageVector,
    val frequencyDescription: String,
    val animeVibe: String
)

/**
 * UI Slider component for the audio editor that allows users to apply real-time 'voice mood'
 * filters such as 'Deep Bass,' 'High Pitch,' or 'Robot' to their generated character voice-overs.
 */
@Composable
fun VoiceMoodFilterSlider(
    currentMood: String = "Natural",
    onMoodSelected: (String) -> Unit,
    onAuditionMood: (mood: String) -> Unit = {},
    isCurrentlyAuditioning: Boolean = false,
    modifier: Modifier = Modifier
) {
    val moods = remember {
        listOf(
            VoiceMoodOption(
                id = "deep_bass",
                name = "Deep Bass",
                title = "Deep Bass (लो-एंड बास)",
                subtitle = "Sub-harmonic boost with heavy baritone resonance",
                pitchMultiplier = 0.65f,
                speedMultiplier = 0.92f,
                accentColor = AnimePurple,
                icon = Icons.Default.GraphicEq,
                frequencyDescription = "80Hz - 250Hz Sub-Bass Warmth",
                animeVibe = "Sensei / Legendary Villain"
            ),
            VoiceMoodOption(
                id = "natural",
                name = "Natural",
                title = "Natural (स्वाभाविक)",
                subtitle = "Balanced studio reference tone without synthetic shift",
                pitchMultiplier = 1.00f,
                speedMultiplier = 1.00f,
                accentColor = AnimeGreen,
                icon = Icons.Default.Mic,
                frequencyDescription = "Flat Studio Response",
                animeVibe = "Authentic Voice Actor"
            ),
            VoiceMoodOption(
                id = "high_pitch",
                name = "High Pitch",
                title = "High Pitch (हाई पिच)",
                subtitle = "Bright treble harmonic excitation & sparkle",
                pitchMultiplier = 1.55f,
                speedMultiplier = 1.08f,
                accentColor = AnimePink,
                icon = Icons.Default.AutoAwesome,
                frequencyDescription = "3kHz - 8kHz Treble Shimmer",
                animeVibe = "Kawaii Heroine / Chibi Mascot"
            ),
            VoiceMoodOption(
                id = "robot",
                name = "Robot",
                title = "Robot (रोबोटिक वोकल)",
                subtitle = "Staccato monotone cybernetic vocoder emulation",
                pitchMultiplier = 0.96f,
                speedMultiplier = 0.85f,
                accentColor = AnimeCyan,
                icon = Icons.Default.SmartToy,
                frequencyDescription = "Monotone Ring Modulator",
                animeVibe = "Mecha Android / Cyber Drone"
            )
        )
    }

    // Match current mood to index
    val initialIndex = moods.indexOfFirst { it.name.equals(currentMood, ignoreCase = true) }
        .coerceAtLeast(0)

    var sliderValue by remember(currentMood) {
        mutableFloatStateOf(initialIndex.toFloat())
    }

    var filterIntensity by remember { mutableFloatStateOf(100f) }

    val activeIndex = sliderValue.roundToInt().coerceIn(0, moods.size - 1)
    val activeMood = moods[activeIndex]

    val activeColor by animateColorAsState(targetValue = activeMood.accentColor, label = "color")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("voice_mood_filter_container"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.5.dp, activeColor.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(activeColor.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = activeMood.icon,
                            contentDescription = activeMood.name,
                            tint = activeColor,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Real-Time Voice Mood Filter",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Applied instantly to generated anime voice-overs",
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                // Active Mood Tag
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(activeColor.copy(alpha = 0.15f))
                        .border(1.dp, activeColor, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = activeMood.name.uppercase(),
                        color = activeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // MAIN SLIDER: 0 (Deep Bass) -> 1 (Natural) -> 2 (High Pitch) -> 3 (Robot)
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voice Mood Continuum Slider:",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${activeMood.name} (${"%.1f".format(activeMood.pitchMultiplier)}x Pitch)",
                        color = activeColor,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Slider(
                    value = sliderValue,
                    onValueChange = { newValue ->
                        sliderValue = newValue
                        val newIndex = newValue.roundToInt().coerceIn(0, moods.size - 1)
                        val selected = moods[newIndex]
                        onMoodSelected(selected.name)
                    },
                    valueRange = 0f..((moods.size - 1).toFloat()),
                    steps = moods.size - 2, // 4 items -> 2 intermediate steps
                    colors = SliderDefaults.colors(
                        thumbColor = activeColor,
                        activeTrackColor = activeColor,
                        inactiveTrackColor = AnimeSurfaceVariant
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("voice_mood_slider")
                )

                // Slider Labels (4 Steps)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    moods.forEachIndexed { index, mood ->
                        val isSelected = index == activeIndex
                        Text(
                            text = mood.name,
                            color = if (isSelected) activeColor else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier
                                .clickable {
                                    sliderValue = index.toFloat()
                                    onMoodSelected(mood.name)
                                }
                                .padding(vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Quick Selection Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                moods.forEachIndexed { index, mood ->
                    val isSelected = index == activeIndex
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            sliderValue = index.toFloat()
                            onMoodSelected(mood.name)
                        },
                        label = {
                            Text(
                                text = mood.name,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        leadingIcon = {
                            Icon(
                                imageVector = mood.icon,
                                contentDescription = null,
                                modifier = Modifier.size(12.dp)
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = mood.accentColor.copy(alpha = 0.25f),
                            selectedLabelColor = mood.accentColor,
                            selectedLeadingIconColor = mood.accentColor,
                            containerColor = AnimeSurfaceVariant
                        ),
                        border = BorderStroke(
                            1.dp,
                            if (isSelected) mood.accentColor else Color.Transparent
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("voice_mood_chip_${mood.name.lowercase().replace(" ", "_")}")
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Detail Card for the currently active filter
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(AnimeSurfaceVariant)
                    .border(1.dp, activeColor.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                    .padding(12.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = activeMood.title,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = activeMood.frequencyDescription,
                            color = activeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = activeMood.subtitle,
                        color = TextMuted,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🎭 Vibe: ${activeMood.animeVibe}",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Speed: ${"%.2f".format(activeMood.speedMultiplier)}x",
                            color = AnimeGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Real-Time Audition Preview Button
            Button(
                onClick = { onAuditionMood(activeMood.name) },
                colors = ButtonDefaults.buttonColors(containerColor = activeColor),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(42.dp)
                    .testTag("audition_voice_mood_btn")
            ) {
                Icon(
                    imageVector = if (isCurrentlyAuditioning) Icons.Default.Hearing else Icons.Default.PlayArrow,
                    contentDescription = "Audition Voice Mood",
                    tint = if (activeColor == AnimeGreen || activeColor == AnimeGold) Color.Black else Color.White,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isCurrentlyAuditioning) "Auditioning '${activeMood.name}' Filter..." else "Audition '${activeMood.name}' Filter in Real-Time",
                    color = if (activeColor == AnimeGreen || activeColor == AnimeGold) Color.Black else Color.White,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

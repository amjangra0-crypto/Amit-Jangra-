package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import com.example.ui.theme.AnimeGreen
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import com.example.localization.AppLocaleStrings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.automation.AutoDirectorEngine
import com.example.data.model.SupportedLanguage
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AutomationDirectorSection(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()

    // Pulse animation for glowing border
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_glow")
    val glowAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 0.95f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_alpha"
    )

    // Calculate dynamic live prediction of command parameters
    val parsedPreview = remember(
        state.automationCommandInput,
        state.automationDurationSeconds,
        state.automationSelectedLanguage,
        state.automationSelectedAccent
    ) {
        AutoDirectorEngine.parseCommand(
            input = state.automationCommandInput,
            overrideDurationSec = state.automationDurationSeconds,
            overrideLanguage = if (state.automationSelectedLanguage == "Auto-Detect") null else state.automationSelectedLanguage,
            overrideAccent = if (state.automationSelectedAccent == "Auto-Detect") null else state.automationSelectedAccent
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .testTag("automation_director_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(
            2.dp,
            Brush.linearGradient(
                listOf(
                    AnimeCyan.copy(alpha = glowAlpha),
                    AnimePurple.copy(alpha = glowAlpha),
                    AnimePink.copy(alpha = glowAlpha)
                )
            )
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header with glowing badges
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(AnimeCyan, AnimePurple, AnimePink))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppLocaleStrings.get("automation_title", state.selectedLanguage),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimePink)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "1-CLICK PIPELINE",
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Text(
                        text = AppLocaleStrings.get("automation_desc", state.selectedLanguage),
                        color = AnimeCyanLight,
                        fontSize = 11.sp,
                        lineHeight = 14.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Command / Link Input Field
            Text(
                text = AppLocaleStrings.get("automation_command_label", state.selectedLanguage),
                color = TextSecondary,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.automationCommandInput,
                onValueChange = { viewModel.setAutomationCommandInput(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("automation_command_input"),
                placeholder = {
                    Text(
                        text = AppLocaleStrings.tr(
                            state.selectedLanguage,
                            "Video Generate • Voice Generate • Visual Content • Translation\nEnter command, duration, or paste link...",
                            "वीडियो जनरेट • वॉयस जनरेट • विजुअल कंटेंट • ट्रांसलेशन\nकमांड, अवधि दर्ज करें या लिंक पेस्ट करें..."
                        ),
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                },
                trailingIcon = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = { viewModel.runAutonomousDirector() },
                            modifier = Modifier.testTag("inline_automation_execute_btn")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = "Execute", tint = AnimeGreen)
                        }
                        MicVoiceInputButton(
                            language = state.selectedLanguage,
                            onSpeechResult = { spoken ->
                                viewModel.setAutomationCommandInput(spoken)
                            },
                            testTag = "automation_command_mic_btn"
                        )
                        if (state.automationCommandInput.isNotBlank()) {
                            IconButton(onClick = { viewModel.setAutomationCommandInput("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                            }
                        }
                    }
                },
                minLines = 2,
                maxLines = 4,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCyan,
                    unfocusedBorderColor = AnimeSurfaceVariant,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = { viewModel.runAutonomousDirector() },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("automation_inline_run_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(state.selectedLanguage, "⚡ Run Command / Generate", "⚡ कमांड चलाएं / ऑटोमेशन शुरू करें"),
                        color = Color.Black,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            CommandLearningSuggestionBar(
                viewModel = viewModel,
                currentRawCommand = state.automationCommandInput
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Quick Capabilities Chips
            Text(
                text = AppLocaleStrings.tr(
                    state.selectedLanguage,
                    "⚡ Quick Capabilities:",
                    "⚡ त्वरित निर्माण क्षमताएं (Capabilities):"
                ),
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Spacer(modifier = Modifier.height(6.dp))

            val presets = if (AppLocaleStrings.isHindi(state.selectedLanguage)) {
                listOf(
                    "🎬 वीडियो जनरेट (Video Generate)" to "Video Generate: High-quality anime production",
                    "🎙️ वॉयस जनरेट (Voice Generate)" to "Voice Generate: Character voice acting and dialogue",
                    "🖼️ विजुअल कंटेंट (Visual Content)" to "Visual Content: Dynamic anime background and scenes",
                    "🌐 ट्रांसलेशन (Translation)" to "Translation: Multilingual dubbing and subtitles"
                )
            } else {
                listOf(
                    "🎬 Video Generate" to "Video Generate: High-quality anime production",
                    "🎙️ Voice Generate" to "Voice Generate: Character voice acting and dialogue",
                    "🖼️ Visual Content" to "Visual Content: Dynamic anime background and scenes",
                    "🌐 Translation" to "Translation: Multilingual dubbing and subtitles"
                )
            }

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(presets) { (label, commandText) ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(AnimeSurfaceVariant)
                            .border(1.dp, AnimeCyan.copy(alpha = 0.4f), RoundedCornerShape(20.dp))
                            .clickable { viewModel.setAutomationCommandInput(commandText) }
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(text = label, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Duration Customizer Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Timer, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            state.selectedLanguage,
                            "Clip Duration Slider (Shorts & Reels):",
                            "क्लिप अवधि स्लाइडर (शॉर्ट्स व रील्स):"
                        ),
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                val formattedDuration = when {
                    state.automationDurationSeconds >= 3600 -> {
                        val hrs = state.automationDurationSeconds / 3600
                        val mins = (state.automationDurationSeconds % 3600) / 60
                        if (mins > 0) "${hrs}h ${mins}m" else "${hrs} Hour(s)"
                    }
                    state.automationDurationSeconds >= 60 -> "${state.automationDurationSeconds / 60}m ${state.automationDurationSeconds % 60}s"
                    else -> "${state.automationDurationSeconds}s"
                }

                val scenesWord = AppLocaleStrings.tr(state.selectedLanguage, "scenes", "सीन्स")
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeGold.copy(alpha = 0.25f))
                        .border(1.dp, AnimeGold, RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "$formattedDuration (${state.customSceneCount} $scenesWord)",
                        color = AnimeGold,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Interactive Duration Slider (15s to 60s for Shorts/Reels, up to 180s)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AnimeSurfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = AppLocaleStrings.tr(
                            state.selectedLanguage,
                            "15s (Shorts/Reels)",
                            "15 सेकंड (शॉर्ट्स)"
                        ),
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${state.automationDurationSeconds}s (" + AppLocaleStrings.tr(
                            state.selectedLanguage,
                            "Current Length",
                            "वर्तमान लंबाई"
                        ) + ")",
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = AppLocaleStrings.tr(
                            state.selectedLanguage,
                            "60s (Max Reel)",
                            "60 सेकंड (रील्स)"
                        ),
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Slider(
                    value = state.automationDurationSeconds.coerceIn(15, 60).toFloat(),
                    onValueChange = { newValue ->
                        viewModel.setAutomationDurationSeconds(newValue.toInt())
                    },
                    valueRange = 15f..60f,
                    steps = 8,
                    colors = SliderDefaults.colors(
                        thumbColor = AnimeGold,
                        activeTrackColor = AnimeGold,
                        inactiveTrackColor = AnimePurple.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("video_duration_slider")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Duration Quick Presets including 1 Hour & 2 Hours
            val durationPresets = listOf(
                15 to "15s Reel",
                30 to "30s Short",
                45 to "45s Story",
                60 to "60s Reel",
                300 to "5m",
                1800 to "30m",
                3600 to "1 Hour",
                7200 to "2 Hours"
            )
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                items(durationPresets) { (dur, label) ->
                    val isSelected = state.automationDurationSeconds == dur
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setAutomationDurationSeconds(dur) },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimeGold.copy(alpha = 0.35f),
                            selectedLabelColor = AnimeGold,
                            containerColor = AnimeSurfaceVariant,
                            labelColor = TextSecondary
                        ),
                        modifier = Modifier.height(28.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // SCENE COUNT & PER-SCENE TIMING CUSTOMIZER
            Card(
                modifier = Modifier.fillMaxWidth().testTag("scenes_timing_customizer_card"),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Movie, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Scene Count & Scene Timing Control:",
                                    "सीन संख्या व हर सीन की टाइमिंग सेटिंग:"
                                ),
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "${state.customSceneCount} " + AppLocaleStrings.tr(state.selectedLanguage, "Scenes", "सीन्स"),
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            state.selectedLanguage,
                            "User decides कितने scenes कितनी देर की video में चाहिए (e.g. 2 scenes for 15s to 60s shorts/reels).",
                            "आप तय कर सकते हैं कितने scenes कितनी देर की video में चाहिए (जैसे शॉर्ट्स व रील्स में 2 सीन्स 15 से 60 सेकंड)।"
                        ),
                        color = TextMuted,
                        fontSize = 10.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Number of Scenes Chips
                    Text(
                        text = AppLocaleStrings.tr(state.selectedLanguage, "Select Number of Scenes:", "सीन्स की संख्या चुनें:"),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        val sceneCountOptions = listOf(
                            2 to "2 Scenes (Shorts/Reels)",
                            3 to "3 Scenes",
                            4 to "4 Scenes",
                            5 to "5 Scenes"
                        )
                        sceneCountOptions.forEach { (count, label) ->
                            val isSelected = state.customSceneCount == count
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSelected) AnimeCyan.copy(alpha = 0.25f) else AnimeSurface)
                                    .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                    .clickable {
                                        val perScene = (state.automationDurationSeconds / count).coerceAtLeast(5)
                                        val timings = MutableList(count) { perScene }
                                        val rem = state.automationDurationSeconds - (perScene * count)
                                        if (rem > 0) timings[0] += rem
                                        viewModel.setCustomSceneCountAndTimings(count, timings)
                                    }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (count == 2) "2 Scenes (Reels)" else "$count Scenes",
                                    color = if (isSelected) AnimeCyanLight else TextSecondary,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Quick Timing Presets for Shorts & Reels
                    Text(
                        text = AppLocaleStrings.tr(state.selectedLanguage, "Quick Scene Timing Presets:", "त्वरित सीन टाइमिंग प्रीसेट:"),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = state.customSceneCount == 2 && state.customSceneTimings == listOf(15, 15),
                                onClick = { viewModel.setCustomSceneCountAndTimings(2, listOf(15, 15)) },
                                label = { Text("2 Scenes (15s + 15s = 30s Short)", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimeGold.copy(alpha = 0.3f), selectedLabelColor = AnimeGold)
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.customSceneCount == 2 && state.customSceneTimings == listOf(30, 30),
                                onClick = { viewModel.setCustomSceneCountAndTimings(2, listOf(30, 30)) },
                                label = { Text("2 Scenes (30s + 30s = 60s Reel)", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimeGold.copy(alpha = 0.3f), selectedLabelColor = AnimeGold)
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.customSceneCount == 2 && state.customSceneTimings == listOf(10, 20),
                                onClick = { viewModel.setCustomSceneCountAndTimings(2, listOf(10, 20)) },
                                label = { Text("2 Scenes (10s hook + 20s action)", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimeGold.copy(alpha = 0.3f), selectedLabelColor = AnimeGold)
                            )
                        }
                        item {
                            FilterChip(
                                selected = state.customSceneCount == 3 && state.customSceneTimings == listOf(10, 10, 10),
                                onClick = { viewModel.setCustomSceneCountAndTimings(3, listOf(10, 10, 10)) },
                                label = { Text("3 Scenes (10s + 10s + 10s = 30s)", fontSize = 10.sp) },
                                colors = FilterChipDefaults.filterChipColors(selectedContainerColor = AnimeGold.copy(alpha = 0.3f), selectedLabelColor = AnimeGold)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Individual Scene Sliders
                    Text(
                        text = AppLocaleStrings.tr(state.selectedLanguage, "Adjust Individual Scene Timings:", "प्रत्येक सीन की टाइमिंग व्यक्तिगत बदलें:"),
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    state.customSceneTimings.forEachIndexed { sceneIdx, timingSec ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Scene ${sceneIdx + 1}:",
                                color = AnimeCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.width(60.dp)
                            )

                            Slider(
                                value = timingSec.toFloat(),
                                onValueChange = { newVal ->
                                    viewModel.updateSingleSceneTiming(sceneIdx, newVal.toInt())
                                },
                                valueRange = 5f..60f,
                                steps = 10,
                                colors = SliderDefaults.colors(
                                    thumbColor = AnimeCyan,
                                    activeTrackColor = AnimeCyan,
                                    inactiveTrackColor = AnimePurple.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.weight(1f).testTag("scene_slider_$sceneIdx")
                            )

                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeSurface)
                                    .border(0.5.dp, AnimeCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "${timingSec}s",
                                    color = AnimeCyanLight,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Multi-Language & Voice Accent Selectors
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Translate, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        state.selectedLanguage,
                        "Language for Dubbing & Subtitles:",
                        "भाषा चयन (Language for Dubbing & Subtitles):"
                    ),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                item {
                    val isAuto = state.automationSelectedLanguage == "Auto-Detect"
                    FilterChip(
                        selected = isAuto,
                        onClick = { viewModel.setAutomationSelectedLanguage("Auto-Detect") },
                        label = { Text("⚡ Auto-Detect", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                            selectedLabelColor = AnimeCyan
                        )
                    )
                }
                items(SupportedLanguage.values()) { lang ->
                    val isSel = state.automationSelectedLanguage == lang.displayName
                    val labelText = if (AppLocaleStrings.isHindi(state.selectedLanguage)) "${lang.nativeName} (${lang.displayName})" else lang.displayName
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.setAutomationSelectedLanguage(lang.displayName) },
                        label = { Text(labelText, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                            selectedLabelColor = AnimeCyan
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Voice Accent Selector
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.GraphicEq, contentDescription = null, tint = AnimePink, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        state.selectedLanguage,
                        "Voice Accent & Cadence:",
                        "लहज़ा व एक्सेंट (Voice Accent & Cadence):"
                    ),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))

            val accentPresets = listOf(
                "Auto-Detect",
                "Tokyo Standard Anime (Japanese Cadence)",
                "Hindi Dub (Heroic Bollywood Anime)",
                "Korean Dramatic (Manhwa Style)",
                "American Heroic Action",
                "British Aristocrat / Royal",
                "Cybernetic / Vocoded Synth",
                "Kawaii High-Energy Anime"
            )

            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                items(accentPresets) { acc ->
                    val isSel = state.automationSelectedAccent == acc
                    FilterChip(
                        selected = isSel,
                        onClick = { viewModel.setAutomationSelectedAccent(acc) },
                        label = { Text(acc, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AnimePink.copy(alpha = 0.25f),
                            selectedLabelColor = AnimePink
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Dynamic AI Command Parser HUD Preview
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.7f)),
                border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Speed, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(15.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLocaleStrings.tr(
                                state.selectedLanguage,
                                "AI Analysis & Auto-Config Prediction (HUD Live):",
                                "एआई विश्लेषण व ऑटो-कंफिगरेशन प्रेडिक्शन (HUD Live):"
                            ),
                            color = AnimeCyanLight,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    val hudScenesWord = AppLocaleStrings.tr(state.selectedLanguage, "scenes", "सीन्स")
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(alpha = 0.6f)) {
                            Text(
                                text = "⏱️ " + AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Duration: ${parsedPreview.requestedDurationSeconds}s (${parsedPreview.calculatedSceneCount} $hudScenesWord)",
                                    "अवधि: ${parsedPreview.requestedDurationSeconds}s (${parsedPreview.calculatedSceneCount} $hudScenesWord)"
                                ),
                                color = AnimeGold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(alpha = 0.6f)) {
                            Text(
                                text = "🌐 " + AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Language: ${parsedPreview.targetLanguage}",
                                    "भाषा: ${parsedPreview.targetLanguage}"
                                ),
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(alpha = 0.6f)) {
                            Text(
                                text = "🎙️ " + AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Accent: ${parsedPreview.targetAccent.take(22)}...",
                                    "लहज़ा: ${parsedPreview.targetAccent.take(22)}..."
                                ),
                                color = AnimePink,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(alpha = 0.6f)) {
                            Text(
                                text = "⚡ " + AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Motion: ${parsedPreview.targetMotionEffect.title}",
                                    "मोशन: ${parsedPreview.targetMotionEffect.title}"
                                ),
                                color = Color.White,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(alpha = 0.6f)) {
                            Text(
                                text = "🎨 " + AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Style: ${parsedPreview.targetArtStyle.title}",
                                    "स्टाइल: ${parsedPreview.targetArtStyle.title}"
                                ),
                                color = AnimeCyanLight,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Surface(shape = RoundedCornerShape(6.dp), color = Color.Black.copy(alpha = 0.6f)) {
                            Text(
                                text = "👥 " + AppLocaleStrings.tr(
                                    state.selectedLanguage,
                                    "Cast: 4 Characters Auto-Synthesized",
                                    "करैक्टर: 4 पात्र ऑटो-सिंथेसिस"
                                ),
                                color = AnimeGold,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            // Progress bar & Step indicator when running
            AnimatedVisibility(visible = state.isAutonomousExecuting) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = state.automationStatusStep.ifBlank { "ऑटोनॉमस पाइपलाइन सक्रिय..." },
                            color = AnimeCyanLight,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        CircularProgressIndicator(
                            color = AnimeCyan,
                            modifier = Modifier.size(16.dp),
                            strokeWidth = 2.dp
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { state.automationProgress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = AnimeCyan,
                        trackColor = AnimeSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Main Trigger Button
            Button(
                onClick = { viewModel.runAutonomousDirector() },
                enabled = !state.isAutonomousExecuting,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("run_autonomous_pipeline_btn"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnimeCyan,
                    disabledContainerColor = AnimeCyan.copy(alpha = 0.4f)
                )
            ) {
                if (state.isAutonomousExecuting) {
                    CircularProgressIndicator(
                        color = Color.Black,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.5.dp
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = AppLocaleStrings.get("generating_video", state.selectedLanguage),
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚀 " + AppLocaleStrings.get("generate_video_btn", state.selectedLanguage),
                        color = Color.Black,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                }
            }
        }
    }
}

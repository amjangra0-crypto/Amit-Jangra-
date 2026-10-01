package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.AnimeScene
import com.example.localization.AppLocaleStrings
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
import kotlin.math.roundToInt

data class TransitionEffectItem(
    val id: String,
    val nameEn: String,
    val nameHi: String,
    val description: String,
    val iconEmoji: String
)

val AvailableTransitions = listOf(
    TransitionEffectItem("Fade", "Fade to Black", "फ़ेड टू ब्लैक", "Smooth cinematic crossfade dip through dark shadows", "🎬"),
    TransitionEffectItem("Cross-Dissolve", "Cross-Dissolve", "क्रॉस-डिज़ॉल्व", "Soft optical blend dissolving Scene A into Scene B", "✨"),
    TransitionEffectItem("Slide Left", "Slide Left", "स्लाइड लेफ्ट", "Dynamic action pan pushing incoming scene from the right", "◀️"),
    TransitionEffectItem("Slide Right", "Slide Right", "स्लाइड राइट", "Swift lateral camera pan sliding scene to the right", "▶️"),
    TransitionEffectItem("Zoom Blur", "Zoom & Blur", "ज़ूम व ब्लर", "Hyper-speed kinetic zoom burst into next sequence", "🔍"),
    TransitionEffectItem("Wipe", "Wipe Reveal", "वाइप रीवील", "Linear graphic boundary wipe sweeping across canvas", "🧹"),
    TransitionEffectItem("Cut", "Hard Cut", "हार्ड कट", "Direct instant switch without transition blending", "✂️")
)

/**
 * Transition Library Modal
 * Allows previewing and selecting different animation transition effects (e.g. fade, slide, cross-dissolve)
 * between scenes with an interactive duration slider (0.2s - 3.0s) and a live mini-preview window.
 */
@Composable
fun TransitionLibraryModal(
    sceneIndex: Int,
    scenes: List<AnimeScene>,
    language: String,
    onApplyTransition: (transitionEffect: String, durationSec: Float) -> Unit,
    onApplyToAll: (transitionEffect: String, durationSec: Float) -> Unit,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val currentScene = scenes.getOrNull(sceneIndex) ?: scenes.firstOrNull()
    val nextScene = scenes.getOrNull(sceneIndex + 1) ?: scenes.firstOrNull()

    var selectedTransition by remember {
        mutableStateOf(currentScene?.transitionEffect ?: "Fade")
    }
    var transitionDuration by remember {
        mutableFloatStateOf(currentScene?.transitionDurationSec ?: 1.0f)
    }

    // Animation transition period in milliseconds for live mini-preview
    val animDurationMs = (transitionDuration * 1000f).coerceIn(400f, 3000f).roundToInt()
    val infiniteTransition = rememberInfiniteTransition(label = "transition_preview_anim")
    val previewProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = animDurationMs + 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "trans_progress"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("transition_library_modal"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, AnimeCyan.copy(alpha = 0.6f))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Header Row
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
                                .background(AnimeCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AnimeCyan,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(language, "Transition Library", "ट्रांजिशन लाइब्रेरी"),
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Scene ${sceneIndex + 1} ➔ Scene ${((sceneIndex + 1) % (scenes.size.coerceAtLeast(1))) + 1}",
                                color = AnimeCyanLight,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_transition_modal_btn")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Live Mini-Preview Window
                Text(
                    text = "🎥 ${AppLocaleStrings.tr(language, "Live Mini-Preview Window", "लाइव मिनी-प्रीव्यू विंडो")}:",
                    color = AnimeGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(170.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(Color.Black)
                        .border(1.5.dp, AnimePurple.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val sceneADrawable = ResourceHelpers.getDrawableId(context, currentScene?.sceneDrawableName ?: "scene_cherry_temple")
                    val sceneBDrawable = ResourceHelpers.getDrawableId(context, nextScene?.sceneDrawableName ?: "scene_neo_tokyo")

                    // Scene A (Base)
                    Image(
                        painter = painterResource(id = sceneADrawable),
                        contentDescription = "Scene A",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Scene B with chosen transition effect applied
                    when (selectedTransition) {
                        "Fade" -> {
                            // Fade through black overlay
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha((previewProgress * 2f).coerceIn(0f, 1f))
                                    .background(Color.Black)
                            )
                            Image(
                                painter = painterResource(id = sceneBDrawable),
                                contentDescription = "Scene B",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(((previewProgress - 0.5f) * 2f).coerceIn(0f, 1f))
                            )
                        }
                        "Cross-Dissolve" -> {
                            Image(
                                painter = painterResource(id = sceneBDrawable),
                                contentDescription = "Scene B",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .alpha(previewProgress)
                            )
                        }
                        "Slide Left" -> {
                            val xOffset = ((1f - previewProgress) * 450).dp
                            Image(
                                painter = painterResource(id = sceneBDrawable),
                                contentDescription = "Scene B",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(x = xOffset)
                            )
                        }
                        "Slide Right" -> {
                            val xOffset = (-(1f - previewProgress) * 450).dp
                            Image(
                                painter = painterResource(id = sceneBDrawable),
                                contentDescription = "Scene B",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .offset(x = xOffset)
                            )
                        }
                        "Zoom Blur" -> {
                            val scale = 0.6f + (previewProgress * 0.4f)
                            Image(
                                painter = painterResource(id = sceneBDrawable),
                                contentDescription = "Scene B",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .scale(scale)
                                    .alpha(previewProgress)
                            )
                        }
                        "Wipe" -> {
                            Image(
                                painter = painterResource(id = sceneBDrawable),
                                contentDescription = "Scene B",
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxSize()
                                    .graphicsLayer {
                                        clip = true
                                        alpha = previewProgress
                                    }
                            )
                        }
                        else -> { // Cut
                            if (previewProgress > 0.5f) {
                                Image(
                                    painter = painterResource(id = sceneBDrawable),
                                    contentDescription = "Scene B",
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                    }

                    // Live transition preview overlay badge
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color.Black.copy(alpha = 0.75f),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.8f)),
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Effect: $selectedTransition • ${"%.1f".format(transitionDuration)}s",
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DURATION SLIDER SECTION (Explicit User Requirement)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Schedule,
                                    contentDescription = null,
                                    tint = AnimeGold,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppLocaleStrings.tr(language, "Transition Duration Slider", "ट्रांजिशन अवधि स्लाइडर"),
                                    color = TextPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = AnimeGold.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, AnimeGold)
                            ) {
                                Text(
                                    text = "${"%.1f".format(transitionDuration)}s",
                                    color = AnimeGold,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    modifier = Modifier
                                        .padding(horizontal = 8.dp, vertical = 2.dp)
                                        .testTag("transition_duration_value_label")
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Slider(
                            value = transitionDuration,
                            onValueChange = { transitionDuration = ((it * 10).roundToInt() / 10f) },
                            valueRange = 0.2f..3.0f,
                            steps = 27,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("transition_duration_slider"),
                            colors = SliderDefaults.colors(
                                thumbColor = AnimeGold,
                                activeTrackColor = AnimeGold,
                                inactiveTrackColor = TextMuted.copy(alpha = 0.3f)
                            )
                        )

                        // Duration Quick Presets
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf(0.5f to "0.5s (Quick)", 1.0f to "1.0s (Normal)", 1.5f to "1.5s (Smooth)", 2.5f to "2.5s (Cinematic)").forEach { (sec, label) ->
                                val isSelectedPreset = kotlin.math.abs(transitionDuration - sec) < 0.05f
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (isSelectedPreset) AnimeCyan.copy(alpha = 0.25f) else Color.Transparent,
                                    border = BorderStroke(0.5.dp, if (isSelectedPreset) AnimeCyan else TextMuted.copy(alpha = 0.4f)),
                                    modifier = Modifier.clickable { transitionDuration = sec }
                                ) {
                                    Text(
                                        text = label,
                                        color = if (isSelectedPreset) AnimeCyan else TextSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = if (isSelectedPreset) FontWeight.Bold else FontWeight.Normal,
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transition Effects Grid / List
                Text(
                    text = "🎨 ${AppLocaleStrings.tr(language, "Choose Animation Effect", "एनिमेशन इफ़ेक्ट चुनें")}:",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AvailableTransitions.forEach { effect ->
                        val isSelected = selectedTransition == effect.id
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedTransition = effect.id }
                                .testTag("transition_effect_card_${effect.id.lowercase().replace(" ", "_")}"),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (isSelected) AnimePurple.copy(alpha = 0.35f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = BorderStroke(
                                1.dp,
                                if (isSelected) AnimeCyan else Color.Transparent
                            )
                        ) {
                            Row(
                                modifier = Modifier
                                    .padding(10.dp)
                                    .fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                    Text(effect.iconEmoji, fontSize = 20.sp)
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = if (AppLocaleStrings.isHindi(language)) effect.nameHi else effect.nameEn,
                                                color = if (isSelected) AnimeCyanLight else TextPrimary,
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            if (isSelected) {
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Box(
                                                    modifier = Modifier
                                                        .clip(RoundedCornerShape(4.dp))
                                                        .background(AnimeCyan)
                                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                                ) {
                                                    Text("ACTIVE", color = Color.Black, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Text(
                                            text = effect.description,
                                            color = TextSecondary,
                                            fontSize = 10.sp,
                                            maxLines = 1
                                        )
                                    }
                                }

                                if (isSelected) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Selected",
                                        tint = AnimeCyan,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Bottom Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            onApplyToAll(selectedTransition, transitionDuration)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("apply_all_transitions_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AnimePurple)
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(language, "Apply to All", "सभी में लागू"),
                            color = AnimePurple,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Button(
                        onClick = {
                            onApplyTransition(selectedTransition, transitionDuration)
                            onDismiss()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("apply_transition_btn"),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan)
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(language, "Apply to Scene", "सीन में लागू"),
                            color = Color.Black,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

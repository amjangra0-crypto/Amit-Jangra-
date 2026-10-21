package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import com.example.ui.components.ResourceHelpers
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.util.lerp
import com.example.data.model.CharacterProfile
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
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
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * CharacterStudioPreviewComponent
 * 3D-Style Carousel View for anime characters:
 * - Allows cycling through generated anime characters with genuine 3D perspective rotation and depth scaling.
 * - Shows hair, eyes, outfit, persona voice styles, and audition capability.
 * - Features one-tap 'Apply to Script' button to cast the selected 3D character into screenplay scenes.
 */
@Composable
fun CharacterStudioPreviewComponent(
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier,
    onCharacterAppliedToScript: ((CharacterProfile) -> Unit)? = null
) {
    val state by viewModel.uiState.collectAsState()
    val speakingState by viewModel.voiceSyncEngine.speakingState.collectAsState()
    val characters = remember(state.customCharacters, state.characterDraft) {
        val list = state.customCharacters.toMutableList()
        if (list.none { it.name == state.characterDraft.name }) {
            list.add(0, state.characterDraft)
        }
        if (list.isEmpty()) {
            list.add(state.characterDraft)
        }
        list
    }

    val pagerState = rememberPagerState(
        initialPage = 0,
        pageCount = { characters.size }
    )
    val coroutineScope = rememberCoroutineScope()
    var appliedFeedbackName by remember { mutableStateOf<String?>(null) }

    val activeCharacter = characters.getOrNull(pagerState.currentPage) ?: state.characterDraft
    val isSpeakingActive = speakingState.isSpeaking && speakingState.activeCharacter == activeCharacter.name

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("character_studio_3d_preview_card"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(20.dp),
        border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(AnimePurple, AnimeCyan.copy(alpha = 0.5f)))),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Bar with 3D Carousel Tag
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                Brush.linearGradient(listOf(AnimePink, AnimePurple)),
                                shape = CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.ViewCarousel,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "3D Character Studio Preview", "3D करैक्टर स्टूडियो प्रिव्यू (3D Carousel)"),
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "Cycle characters in 3D perspective before applying to script", "स्क्रिप्ट में जोड़ने से पहले 3D व्यू में जांचें"),
                            color = AnimeCyanLight,
                            fontSize = 11.sp
                        )
                    }
                }

                // Page indicator badge
                Surface(
                    color = AnimePurple.copy(alpha = 0.35f),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "${pagerState.currentPage + 1} / ${characters.size}",
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 3D Carousel Horizontal Pager
            val density = LocalDensity.current.density
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(290.dp),
                contentAlignment = Alignment.Center
            ) {
                HorizontalPager(
                    state = pagerState,
                    contentPadding = PaddingValues(horizontal = 48.dp),
                    pageSpacing = 16.dp,
                    modifier = Modifier.fillMaxSize()
                ) { page ->
                    val char = characters.getOrNull(page) ?: return@HorizontalPager

                    // 3D Rotation and Scale calculations based on offset from center
                    val pageOffset = (
                        (pagerState.currentPage - page) + pagerState.currentPageOffsetFraction
                    )

                    val rotationY = (pageOffset * 32f).coerceIn(-48f, 48f)
                    val scale = lerp(
                        start = 0.82f,
                        stop = 1f,
                        fraction = 1f - abs(pageOffset).coerceIn(0f, 1f)
                    )
                    val alpha = lerp(
                        start = 0.55f,
                        stop = 1f,
                        fraction = 1f - abs(pageOffset).coerceIn(0f, 1f)
                    )

                    Character3DCard(
                        character = char,
                        isCenter = page == pagerState.currentPage,
                        isSpeaking = speakingState.isSpeaking && speakingState.activeCharacter == char.name,
                        modifier = Modifier
                            .graphicsLayer {
                                this.rotationY = rotationY
                                this.scaleX = scale
                                this.scaleY = scale
                                this.alpha = alpha
                                this.cameraDistance = 14f * density
                            }
                            .fillMaxSize()
                    )
                }

                // Carousel Left / Right Arrows
                if (pagerState.currentPage > 0) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                            .border(1.dp, AnimeCyan.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous Character", tint = AnimeCyan, modifier = Modifier.size(18.dp))
                    }
                }

                if (pagerState.currentPage < characters.size - 1) {
                    IconButton(
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .size(36.dp)
                            .background(Color.Black.copy(alpha = 0.65f), CircleShape)
                            .border(1.dp, AnimeCyan.copy(alpha = 0.6f), CircleShape)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next Character", tint = AnimeCyan, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Character Details Summary Pill
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = activeCharacter.name,
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "${activeCharacter.role} • ${activeCharacter.personality}",
                                color = AnimeGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Audition voice button
                        Button(
                            onClick = {
                                if (isSpeakingActive) {
                                    viewModel.voiceSyncEngine.stop()
                                } else {
                                    viewModel.auditionDraftVoice(activeCharacter.sampleDialogue)
                                }
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSpeakingActive) AnimePink else AnimePurple
                            ),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Icon(
                                if (isSpeakingActive) Icons.Default.Stop else Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isSpeakingActive) "Stop Voice" else "Voice Sample",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Trait Chips Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        TraitPill(label = "Hair: ${activeCharacter.hairStyle}", color = AnimeCyan)
                        TraitPill(label = "Eyes: ${activeCharacter.eyeColor}", color = AnimePink)
                        TraitPill(label = "Voice: ${activeCharacter.voicePersona}", color = AnimeGold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Apply to Script Action Button
            Button(
                onClick = {
                    // 1. Cast character in active script scene
                    viewModel.updateCharacterDraft(activeCharacter)
                    viewModel.applyDraftToActiveScene()
                    appliedFeedbackName = activeCharacter.name
                    onCharacterAppliedToScript?.invoke(activeCharacter)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("apply_character_to_script_button"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AnimeCyan
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        state.selectedLanguage,
                        "Apply '${activeCharacter.name}' to Script Scene",
                        "'${activeCharacter.name}' को स्क्रिप्ट में जोड़ें (Apply to Script)"
                    ),
                    color = Color.Black,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            // Success feedback notification
            AnimatedVisibility(visible = appliedFeedbackName != null) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Successfully cast '${appliedFeedbackName}' into Scene ${state.activeSceneIndex + 1}!",
                        color = AnimeGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun Character3DCard(
    character: CharacterProfile,
    isCenter: Boolean,
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val borderBrush = if (isSpeaking) {
        Brush.sweepGradient(listOf(AnimePink, AnimeCyan, AnimeGold, AnimePink))
    } else if (isCenter) {
        Brush.linearGradient(listOf(AnimeCyan, AnimePurple))
    } else {
        Brush.linearGradient(listOf(Color.White.copy(alpha = 0.1f), Color.White.copy(alpha = 0.05f)))
    }

    Card(
        modifier = modifier
            .shadow(if (isCenter) 16.dp else 4.dp, RoundedCornerShape(18.dp))
            .border(if (isCenter) 2.dp else 1.dp, borderBrush, RoundedCornerShape(18.dp)),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            // Background stylized anime visual
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(AnimePurple.copy(alpha = 0.6f), Color.Black)
                            )
                        )
                        .border(
                            if (isSpeaking) 3.dp else 1.5.dp,
                            if (isSpeaking) AnimePink else AnimeCyan.copy(alpha = 0.7f),
                            CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    val context = LocalContext.current
                    val avatarRes = ResourceHelpers.getDrawableId(context, character.avatarDrawableName)
                    Image(
                        painter = painterResource(id = avatarRes),
                        contentDescription = character.name,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Speaking Lip-sync aura
                    if (isSpeaking) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(4.dp, AnimePink, CircleShape)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = character.name,
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = character.role,
                    color = AnimeCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "\"${character.sampleDialogue}\"",
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 2,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            // Top Status Badge
            if (isSpeaking) {
                Surface(
                    color = AnimePink,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                ) {
                    Text(
                        text = "🎙️ SPEAKING",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun TraitPill(label: String, color: Color) {
    Surface(
        color = color.copy(alpha = 0.12f),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(0.8.dp, color.copy(alpha = 0.5f))
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 9.5.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
        )
    }
}

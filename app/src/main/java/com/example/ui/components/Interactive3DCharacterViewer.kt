package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

/**
 * Interactive 3D Character Preview Component:
 * Allows users to inspect and rotate custom avatars in 3D space with touch drag gestures
 * before assigning them to screenplay projects.
 */
@Composable
fun Interactive3DCharacterViewer(
    character: CharacterProfile,
    lang: String,
    onAssignToProject: (CharacterProfile) -> Unit,
    modifier: Modifier = Modifier
) {
    var rotationYaw by remember { mutableFloatStateOf(0f) }
    var rotationPitch by remember { mutableFloatStateOf(0f) }
    var zoomScale by remember { mutableFloatStateOf(1.0f) }
    var isAuraActive by remember { mutableStateOf(true) }
    val density = LocalDensity.current.density
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("interactive_3d_character_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.5.dp, AnimePurple.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .clip(CircleShape)
                            .background(AnimeCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(lang, "3D Avatar Inspector", "3D अवतार निरीक्षण"),
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = AppLocaleStrings.tr(lang, "Drag to rotate 360° & tilt perspective", "360° घुमाने व झुकाने के लिए ड्रैग करें"),
                            fontSize = 10.sp,
                            color = TextMuted
                        )
                    }
                }

                IconButton(
                    onClick = {
                        rotationYaw = 0f
                        rotationPitch = 0f
                        zoomScale = 1.0f
                    },
                    modifier = Modifier.size(28.dp).testTag("reset_3d_rotation_btn")
                ) {
                    Icon(Icons.Default.Refresh, contentDescription = "Reset Angle", tint = AnimeGold, modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Interactive 3D Viewport Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                Color(0xFF1E1B4B),
                                Color(0xFF0F0E26),
                                Color(0xFF070614)
                            )
                        )
                    )
                    .border(1.dp, AnimeCyan.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            rotationYaw = (rotationYaw + dragAmount.x * 0.5f) % 360f
                            rotationPitch = (rotationPitch - dragAmount.y * 0.3f).coerceIn(-35f, 35f)
                        }
                    }
                    .testTag("interactive_3d_character_canvas"),
                contentAlignment = Alignment.Center
            ) {
                // Background aura particle simulation
                if (isAuraActive) {
                    Box(
                        modifier = Modifier
                            .size(180.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        AnimePink.copy(alpha = 0.45f),
                                        AnimePurple.copy(alpha = 0.25f),
                                        Color.Transparent
                                    )
                                )
                            )
                    )
                }

                // 3D Rotated Character Avatar Model
                val avatarResId = ResourceHelpers.getDrawableId(context, character.avatarDrawableName)
                Box(
                    modifier = Modifier
                        .size(190.dp)
                        .graphicsLayer {
                            this.rotationY = rotationYaw
                            this.rotationX = rotationPitch
                            this.scaleX = zoomScale
                            this.scaleY = zoomScale
                            this.cameraDistance = 16f * density
                        },
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarResId != 0) {
                        Image(
                            painter = painterResource(id = avatarResId),
                            contentDescription = character.name,
                            modifier = Modifier
                                .fillMaxSize()
                                .shadow(8.dp, CircleShape),
                            contentScale = ContentScale.Fit
                        )
                    }
                }

                // Yaw & Pitch HUD Indicators
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .background(Color.Black.copy(alpha = 0.65f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "YAW: ${rotationYaw.toInt()}° • PITCH: ${rotationPitch.toInt()}°",
                        color = AnimeCyan,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Aura Energy Toggle Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(if (isAuraActive) AnimePink.copy(alpha = 0.3f) else Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, if (isAuraActive) AnimePink else TextMuted, RoundedCornerShape(6.dp))
                        .clickable { isAuraActive = !isAuraActive }
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = if (isAuraActive) "✨ " + AppLocaleStrings.tr(lang, "AURA ON", "आभा चालू") else "⚪ " + AppLocaleStrings.tr(lang, "AURA OFF", "आभा बंद"),
                        color = if (isAuraActive) AnimePink else TextMuted,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Character Details & Assign Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = character.name,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${character.role} • ${character.voiceType} (${character.voicePersona})",
                        color = AnimeCyanLight,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }

                Button(
                    onClick = { onAssignToProject(character) },
                    modifier = Modifier
                        .height(38.dp)
                        .testTag("assign_3d_character_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Assign to Project", "प्रोजेक्ट में कास्ट करें"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

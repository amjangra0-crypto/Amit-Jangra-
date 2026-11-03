package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
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
 * Global Architecture Matrix & Localization Status Card.
 * Displays the 4 Unbreakable Laws, active dynamic runtime tokens:
 * [BASE_DEFAULT_LANGUAGE: English]
 * [ACTIVE_GPS_COUNTRY: {{Country}}]
 * [MANUAL_LANGUAGE_SELECTION: {{Manual_Lang}}]
 * [CURRENT_ROLE: {{User OR Owner}}]
 *
 * And Dynamic UI Matrix Blocks:
 * - [ACTIVATED_COMPONENTS]
 * - [GLOBAL_UI_MATRIX]
 * - [CREATIVE_PRODUCTION_BLUEPRINT]
 * - [FINANCIAL_PAYOUT_ROUTING]
 */
@Composable
fun GlobalArchitectureMatrixCard(
    uiState: AnimeStudioUiState,
    isOwner: Boolean,
    onResetToGps: () -> Unit,
    onChangeLanguage: () -> Unit,
    onOpenPermissions: () -> Unit,
    modifier: Modifier = Modifier
) {
    val lang = uiState.appInterfaceLanguage
    val isHindi = AppLocaleStrings.isHindi(lang)
    var isExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("global_architecture_matrix_card"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.2.dp, Brush.horizontalGradient(listOf(AnimePurple, AnimeCyan, AnimeGold)))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isExpanded = !isExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(AnimePurple, AnimeCyan))),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isHindi) "ग्लोबल आर्किटेक्चर लोकलाइज़ेशन इंजन" else "Global Architecture Localization Engine",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (uiState.isManualLanguageOverrideActive) {
                                    if (isHindi) "मैन्युअल भाषा सक्रिय (Manual Override)" else "Manual Override Active (${uiState.selectedLanguage})"
                                } else {
                                    if (isHindi) "जीपीएस ऑटो-डिटेक्शन सक्रिय (${uiState.activeGpsCountry})" else "GPS Auto-Detected (${uiState.activeGpsCountry})"
                                },
                                color = if (uiState.isManualLanguageOverrideActive) AnimePink else AnimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Icon(
                    imageVector = if (isExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                    contentDescription = null,
                    tint = TextMuted,
                    modifier = Modifier.size(22.dp)
                )
            }

            // Compact Dynamic Plugins Row (Always visible)
            Spacer(modifier = Modifier.height(10.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // Plugin 1: Baseline
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(text = "BASELINE", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text(text = "English", color = TextPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Plugin 2: GPS Country
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(text = "GPS COUNTRY", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text(text = uiState.activeGpsCountry.take(10), color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Plugin 3: Manual Lang
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(text = "OVERRIDE", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text(text = uiState.manualLanguageSelection ?: "None (GPS)", color = if (uiState.isManualLanguageOverrideActive) AnimePink else AnimeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Plugin 4: Currency
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Column {
                        Text(text = "CURRENCY", color = TextMuted, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text(text = "${uiState.regionalCurrencySymbol} (${uiState.regionalCurrencyCode})", color = AnimeGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Expanded UI Matrix Injection Blocks
            AnimatedVisibility(visible = isExpanded) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    // Raw Injection Token Inspector
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.Black.copy(alpha = 0.4f))
                            .border(1.dp, Color.White.copy(alpha = 0.08f), RoundedCornerShape(10.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Text(
                                text = "CURRENT DYNAMIC APP DATA PLUGINS:",
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "[BASE_DEFAULT_LANGUAGE: English]\n" +
                                        "[ACTIVE_GPS_COUNTRY: ${uiState.activeGpsCountry}]\n" +
                                        "[MANUAL_LANGUAGE_SELECTION: ${uiState.manualLanguageSelection ?: "None (Following GPS)"}]\n" +
                                        "[CURRENT_ROLE: ${if (isOwner) "Owner" else "User"}]",
                                color = TextSecondary,
                                fontSize = 10.sp,
                                fontFamily = FontFamily.Monospace,
                                lineHeight = 14.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isHindi) "डायनामिक यूआई मैट्रिक्स इंजेक्शन ब्लॉक्स (Dynamic UI Matrix):" else "Dynamic UI Matrix Injection Blocks:",
                        color = TextPrimary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    // Block 1: ACTIVATED_COMPONENTS
                    MatrixItemRow(
                        icon = Icons.Default.Route,
                        title = "[ACTIVATED_COMPONENTS]",
                        desc = if (isHindi) "ऑटोमेटेड फीचर रूटिंग व मॉड्यूल्स शुद्ध देशी लिपि में लोड हैं।" else "Automated feature routing loaded natively in pure script.",
                        color = AnimeCyan
                    )

                    // Block 2: GLOBAL_UI_MATRIX
                    MatrixItemRow(
                        icon = Icons.Default.Public,
                        title = "[GLOBAL_UI_MATRIX]",
                        desc = if (isHindi) "समस्त स्क्रीन्स, हेडर, मेनू व बटन्स का शून्य-मिलावट अनुवाद।" else "Zero-bleed script purity across all headers, cards, and sub-menus.",
                        color = AnimePink
                    )

                    // Block 3: CREATIVE_PRODUCTION_BLUEPRINT
                    MatrixItemRow(
                        icon = Icons.Default.Movie,
                        title = "[CREATIVE_PRODUCTION_BLUEPRINT]",
                        desc = if (isHindi) "सीन स्क्रिप्ट्स, संवाद पत्रक, उच्चारण संकेत व मोशन ग्राफिक्स।" else "Scene scripts, dialogue sheets, accent cues, and motion notes.",
                        color = AnimeGold
                    )

                    // Block 4: FINANCIAL_PAYOUT_ROUTING
                    MatrixItemRow(
                        icon = Icons.Default.AccountBalanceWallet,
                        title = "[FINANCIAL_PAYOUT_ROUTING]",
                        desc = if (isHindi) "सुरक्षित एस्क्रो वॉलेट व रीजनल मुद्रा सिंबल (${uiState.regionalCurrencySymbol}) प्रारूप।" else "Secured escrow wallet formatted with native currency (${uiState.regionalCurrencySymbol}).",
                        color = AnimeGreen
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Quick Control Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.isManualLanguageOverrideActive) {
                            Button(
                                onClick = onResetToGps,
                                modifier = Modifier.weight(1f).height(38.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isHindi) "GPS पर लौटें" else "Reset to GPS",
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        OutlinedButton(
                            onClick = onChangeLanguage,
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AnimeCyan)
                        ) {
                            Icon(Icons.Default.Language, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "भाषा बदलें" else "Override Lang",
                                color = AnimeCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        OutlinedButton(
                            onClick = onOpenPermissions,
                            modifier = Modifier.weight(1f).height(38.dp),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AnimePurple)
                        ) {
                            Icon(Icons.Default.Tune, contentDescription = null, tint = AnimePurple, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isHindi) "अनुमतियाँ" else "Permissions",
                                color = AnimePurple,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MatrixItemRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    desc: String,
    color: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(14.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
            Text(text = desc, color = TextSecondary, fontSize = 9.5.sp, lineHeight = 12.sp)
        }
    }
}

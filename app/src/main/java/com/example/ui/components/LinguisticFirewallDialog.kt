package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CountryCodeProvider
import com.example.data.repository.AuthAndWalletRepository
import com.example.localization.AppLocaleStrings
import com.example.localization.LinguisticFirewallEngine
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * LinguisticFirewallDialog
 * Interactive UI demonstrating and enforcing the Absolute Core Translation and Creative Engine.
 * Displays the 4-part Output Architecture strictly adhering to the Linguistic Firewall Laws.
 */
@Composable
fun LinguisticFirewallDialog(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsState()
    val selectedCountry by viewModel.selectedCountry.collectAsState()
    val authRepo = remember { AuthAndWalletRepository.getInstance(context) }
    val currentUser by authRepo.currentUser.collectAsState()
    val wallets by authRepo.wallets.collectAsState()

    val inrBal = wallets[com.example.data.model.CurrencyType.INR]?.balance ?: 48500.0
    val usdBal = wallets[com.example.data.model.CurrencyType.USD]?.balance ?: 620.0

    var testRoleOverride by remember { mutableStateOf<String?>(null) }
    val effectiveRole = testRoleOverride ?: if (currentUser.isOwner) "Owner" else "User"

    val engineConfig = remember(state.selectedLanguage, selectedCountry, effectiveRole, inrBal, usdBal) {
        LinguisticFirewallEngine.resolveConfig(
            language = state.selectedLanguage,
            countryCode = selectedCountry,
            isOwner = effectiveRole.equals("Owner", ignoreCase = true),
            inrBalance = inrBal,
            usdBalance = usdBal
        )
    }

    val output = remember(engineConfig) {
        LinguisticFirewallEngine.execute(engineConfig)
    }

    var selectedTab by remember { mutableStateOf(0) } // 0: Components, 1: TrendPlus, 2: Blueprint, 3: Financial

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.92f)
                .testTag("linguistic_firewall_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimeGold)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                // Header
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
                                .background(Brush.linearGradient(listOf(AnimeCyan, AnimePurple))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(state.selectedLanguage, "Linguistic Firewall Engine", "भाषाई फ़ायरवॉल व क्रिएटिव इंजन"),
                                color = AnimeGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "100% Pure Target Translation • Zero Cross-Contamination",
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Active Configuration HUD Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "ACTIVE CONFIGURATION",
                                color = AnimeCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            // Role Switcher Button
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = if (effectiveRole == "Owner") AnimeGold.copy(alpha = 0.2f) else AnimeCyan.copy(alpha = 0.2f),
                                border = BorderStroke(1.dp, if (effectiveRole == "Owner") AnimeGold else AnimeCyan),
                                modifier = Modifier.clickable {
                                    testRoleOverride = if (effectiveRole == "Owner") "User" else "Owner"
                                }
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                ) {
                                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "Role: $effectiveRole (Tap to Toggle)",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "[SELECTED_DEFAULT_LANGUAGE: ${engineConfig.selectedLanguage}] • [SELECTED_COUNTRY: ${engineConfig.selectedCountry} (${engineConfig.dialingCode})]",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Text(
                            text = "[CURRENT_ROLE: $effectiveRole] • [LIVE_VAULT_LEDGER: {\"INR\": \"${engineConfig.liveInr}\", \"USD\": \"${engineConfig.liveUsd}\"}]",
                            color = TextSecondary,
                            fontSize = 10.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Output Architecture Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val tabs = listOf(
                        "COMPONENTS" to "[ACTIVATED_COMPONENTS]",
                        "TRENDPLUS" to "[TRENDPLUS_FORECAST]",
                        "BLUEPRINT" to "[CREATIVE_PRODUCTION_BLUEPRINT]",
                        "PAYOUT" to "[FINANCIAL_PAYOUT_ROUTING]"
                    )
                    tabs.forEachIndexed { index, (label, _) ->
                        val isSelected = selectedTab == index
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) AnimePurple else AnimeSurfaceVariant,
                            border = BorderStroke(1.dp, if (isSelected) AnimeCyan else Color.Transparent),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = index }
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 8.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Scrollable Output Body
                LazyColumn(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(Color.Black.copy(alpha = 0.4f))
                        .border(1.dp, AnimePurple.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    item {
                        val (blockKey, content) = when (selectedTab) {
                            0 -> "[ACTIVATED_COMPONENTS]" to output.activatedComponents
                            1 -> "[TRENDPLUS_FORECAST]" to output.trendplusForecast
                            2 -> "[CREATIVE_PRODUCTION_BLUEPRINT]" to output.creativeBlueprint
                            else -> "[FINANCIAL_PAYOUT_ROUTING]" to output.financialPayoutRouting
                        }

                        Text(
                            text = blockKey,
                            color = AnimeGold,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.ExtraBold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = content,
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Action Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Copy Full Output
                    Button(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            val clip = ClipData.newPlainText("Linguistic Output", output.fullFormattedOutput)
                            clipboard.setPrimaryClip(clip)
                            Toast.makeText(context, "Full Architecture Copied to Clipboard!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f)
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Copy Output", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }

                    // Apply Blueprint to Studio
                    Button(
                        onClick = {
                            viewModel.setPromptInput("Awakening of the Void Samurai in Neo Tokyo")
                            Toast.makeText(context, "Blueprint Applied to Studio!", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.3f)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Apply to Studio", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

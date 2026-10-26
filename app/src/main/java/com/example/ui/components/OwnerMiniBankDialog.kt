package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CurrencyType
import com.example.data.model.PaymentGateway
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
 * AI Agent Capability configuration for Owner Account Management
 */
data class OwnerAiAgentPermissions(
    val allowAutoDebitCardSettlement: Boolean = true,
    val allowCurrencyForexOptimization: Boolean = true,
    val allowFraudAuditAndChargebackBlock: Boolean = true,
    val allowDailyFirestoreLedgerBackup: Boolean = true,
    val allowAutoRenewalProcessing: Boolean = true,
    val allowOwnerAlertsAboveThreshold: Boolean = true,
    val alertThresholdUsd: Double = 500.0,
    val customOwnerInstruction: String = "Manage all incoming multi-currency deposits. Auto-convert Euro to USD when rate > 1.08. Never execute external bank withdrawals without explicit Master PIN confirmation."
)

/**
 * OwnerMiniBankDialog
 * Dedicated Mini Bank exclusively for the Owner:
 * 1. Multi-Currency Vault (USD $, INR ₹, EUR €, GBP £, JPY ¥)
 * 2. Real-time balances, conversions, and direct deposit ledger
 * 3. Debit Card payment processing acceptance (Visa, Mastercard, RuPay, Maestro)
 * 4. Protected by Firebase Authentication & Firewall Security Shield
 * 5. Owner AI Agent Controller:
 *    - Owner specifies exact commands on what the AI agent can and cannot handle.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OwnerMiniBankDialog(
    viewModel: AnimeViewModel,
    lang: String = "English",
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val wallets by viewModel.wallets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val bankAccount by viewModel.connectedBankAccount.collectAsState()
    val scrollState = rememberScrollState()

    var selectedTab by remember { mutableIntStateOf(0) } // 0: Vaults & Balances, 1: AI Agent Manager, 2: Debit Card Terminal, 3: Transactions

    // AI Agent Control State
    var aiPermissions by remember { mutableStateOf(OwnerAiAgentPermissions()) }
    var ownerCommandInput by remember { mutableStateOf(aiPermissions.customOwnerInstruction) }
    var isAiAgentEditing by remember { mutableStateOf(false) }

    // Debit Card Acceptance Simulator / Terminal State
    var debitCardNumber by remember { mutableStateOf("") }
    var debitCardHolder by remember { mutableStateOf("") }
    var debitCardExpiry by remember { mutableStateOf("") }
    var debitCardCvv by remember { mutableStateOf("") }
    var debitCardAmount by remember { mutableStateOf("10.00") }
    var debitCardCurrency by remember { mutableStateOf(CurrencyType.USD) }
    var isProcessingCard by remember { mutableStateOf(false) }

    // Calculate total net worth in INR and USD
    val totalInr = wallets.values.map { it.balance * it.currency.exchangeToInr }.sum()
    val totalUsd = if (CurrencyType.USD.exchangeToInr > 0) totalInr / CurrencyType.USD.exchangeToInr else 0.0

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .padding(vertical = 14.dp)
                .testTag("owner_mini_bank_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(2.dp, Brush.horizontalGradient(listOf(AnimeGold, AnimeCyan, AnimePurple)))
        ) {
            Column(
                modifier = Modifier
                    .padding(18.dp)
                    .verticalScroll(scrollState)
            ) {
                // TOP BAR: HEADER WITH FIREBASE/FIREWALL BADGE & CLOSE BUTTON
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(CircleShape)
                                .background(AnimeGold.copy(alpha = 0.2f))
                                .border(1.5.dp, AnimeGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AccountBalance,
                                contentDescription = null,
                                tint = AnimeGold,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppLocaleStrings.tr(lang, "👑 Owner Mini Bank", "👑 ओनर मिनी बैंक"),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "FIREBASE PROTECTED",
                                        color = AnimeGreen,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Multi-Currency Vault & AI Agent Account Manager",
                                    "मल्टी-करेंसी वॉल्ट व एआई एजेंट अकाउंट मैनेजर"
                                ),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .testTag("owner_mini_bank_close_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // FIREWALL & OWNER SECURITY BANNER
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(AnimePurple.copy(alpha = 0.15f), AnimeCyan.copy(alpha = 0.15f))
                            )
                        )
                        .border(1.dp, AnimeCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                        .padding(10.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Shield,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "🛡️ Bank Firewall Active • 256-Bit Hardware Security",
                                    "🛡️ बैंक फायरवॉल सक्रिय • 256-बिट हार्डवेयर सुरक्षा"
                                ),
                                color = AnimeCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Owner privacy is strictly enforced. No user or third-party can access this bank.",
                                    "ओनर की गोपनीयता सर्वोच्च प्राथमिकता है। केवल प्रमाणित ओनर ही इसे एक्सेस कर सकता है।"
                                ),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // AGGREGATE TOTAL NET WORTH CARD
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Total Mini Bank Portfolio", "कुल मिनी बैंक पोर्टफोलियो"),
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "5 Currencies Managed",
                                color = AnimeGold,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                text = "₹${String.format("%,.2f", totalInr)}",
                                color = AnimeGreen,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "≈ $${String.format("%,.2f", totalUsd)} USD",
                                color = AnimeCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // MINI BANK TABS
                val tabs = listOf(
                    AppLocaleStrings.tr(lang, "💰 Vaults", "💰 वॉल्ट्स"),
                    AppLocaleStrings.tr(lang, "🤖 AI Agent", "🤖 एआई एजेंट"),
                    AppLocaleStrings.tr(lang, "💳 Debit Card", "💳 डेबिट कार्ड"),
                    AppLocaleStrings.tr(lang, "📜 Ledger", "📜 बहीखाता")
                )

                TabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = Color.Transparent,
                    contentColor = MaterialTheme.colorScheme.primary,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                            color = AnimeGold
                        )
                    }
                ) {
                    tabs.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTab == index,
                            onClick = { selectedTab = index },
                            text = {
                                Text(
                                    text = title,
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal,
                                    color = if (selectedTab == index) AnimeGold else TextMuted
                                )
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // TAB 0: CURRENCY VAULTS
                if (selectedTab == 0) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Multi-Currency Reserve Accounts:", "मल्टी-करेंसी रिज़र्व खाते:"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    CurrencyType.entries.forEach { curr ->
                        val wallet = wallets[curr]
                        val bal = wallet?.balance ?: 0.0
                        val formattedBal = "${curr.symbol}${String.format("%,.2f", bal)}"

                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                when (curr) {
                                                    CurrencyType.INR -> Color(0xFF059669).copy(alpha = 0.2f)
                                                    CurrencyType.USD -> Color(0xFF0284C7).copy(alpha = 0.2f)
                                                    CurrencyType.EUR -> Color(0xFFDB2777).copy(alpha = 0.2f)
                                                    CurrencyType.GBP -> Color(0xFFD97706).copy(alpha = 0.2f)
                                                    CurrencyType.JPY -> Color(0xFF7C3AED).copy(alpha = 0.2f)
                                                }
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = curr.symbol, color = AnimeGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Column {
                                        Text(
                                            text = "${curr.code} Vault (${curr.title})",
                                            color = MaterialTheme.colorScheme.onSurface,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Forex: 1 ${curr.code} = ₹${curr.exchangeToInr}",
                                            color = TextMuted,
                                            fontSize = 10.sp
                                        )
                                    }
                                }

                                Text(
                                    text = formattedBal,
                                    color = AnimeGreen,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.ExtraBold
                                )
                            }
                        }
                    }
                }

                // TAB 1: OWNER AI AGENT ACCOUNT CONTROLLER
                if (selectedTab == 1) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, AnimeGold)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .clip(CircleShape)
                                        .background(AnimeGold.copy(alpha = 0.2f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.SmartToy, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = AppLocaleStrings.tr(
                                            lang,
                                            "AI Agent Account Manager",
                                            "एआई एजेंट अकाउंट मैनेजर"
                                        ),
                                        color = AnimeGold,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = AppLocaleStrings.tr(
                                            lang,
                                            "Exclusively for Owner • Governed strictly by Owner commands",
                                            "केवल ओनर के लिए • पूर्णतः ओनर के आदेशों से संचालित"
                                        ),
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Define what the AI Agent CAN and CANNOT handle in your account:",
                                    "निर्धारित करें कि एआई एजेंट आपके खाते में क्या कार्य कर सकता है और क्या नहीं:"
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            // Perm 1: Auto-Debit Card Settlement
                            AiPermissionRow(
                                title = AppLocaleStrings.tr(lang, "Auto-Settle Debit Card & UPI Payments", "डेबिट कार्ड व यूपीआई भुगतान का स्वतः निपटारा"),
                                subtitle = AppLocaleStrings.tr(lang, "Instantly verify and credit subscriber fees into Mini Bank", "सब्सक्राइबर फीस का स्वतः सत्यापन व क्रेडिट"),
                                isEnabled = aiPermissions.allowAutoDebitCardSettlement,
                                onToggle = { aiPermissions = aiPermissions.copy(allowAutoDebitCardSettlement = it) }
                            )

                            // Perm 2: Forex Optimization
                            AiPermissionRow(
                                title = AppLocaleStrings.tr(lang, "Currency & Forex Optimization", "मुद्रा विनिमय व फॉरेक्स अनुकूलन"),
                                subtitle = AppLocaleStrings.tr(lang, "Recommend best times to convert between USD, EUR, INR", "USD, EUR, INR के बीच अनुकूल समय पर विनिमय"),
                                isEnabled = aiPermissions.allowCurrencyForexOptimization,
                                onToggle = { aiPermissions = aiPermissions.copy(allowCurrencyForexOptimization = it) }
                            )

                            // Perm 3: Fraud Chargeback Shield
                            AiPermissionRow(
                                title = AppLocaleStrings.tr(lang, "Fraud Audit & Chargeback Shield", "धोखाधड़ी ऑडिट व चार्ज-बैक सुरक्षा"),
                                subtitle = AppLocaleStrings.tr(lang, "Block suspicious attempts to bypass subscription verification", "संदेहास्पद सब्सक्रिप्शन अनुरोधों को स्वतः ब्लॉक करें"),
                                isEnabled = aiPermissions.allowFraudAuditAndChargebackBlock,
                                onToggle = { aiPermissions = aiPermissions.copy(allowFraudAuditAndChargebackBlock = it) }
                            )

                            // Perm 4: Daily Firestore Ledger Backup
                            AiPermissionRow(
                                title = AppLocaleStrings.tr(lang, "Automated Firestore Ledger Sync", "फायरस्टोर बहीखाता स्वतः सिंक"),
                                subtitle = AppLocaleStrings.tr(lang, "Backup all mini bank balances to Firebase Firestore nightly", "मिनी बैंक बैलेंस का सुरक्षित बैकअप बनाएं"),
                                isEnabled = aiPermissions.allowDailyFirestoreLedgerBackup,
                                onToggle = { aiPermissions = aiPermissions.copy(allowDailyFirestoreLedgerBackup = it) }
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            // Specific Natural Language Command to AI Agent
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Specific Owner Command to AI Agent:",
                                    "एआई एजेंट के लिए ओनर का विशिष्ट निर्देश:"
                                ),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))

                            OutlinedTextField(
                                value = ownerCommandInput,
                                onValueChange = { ownerCommandInput = it },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(90.dp)
                                    .testTag("owner_ai_agent_command_input"),
                                shape = RoundedCornerShape(10.dp),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 11.sp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = AnimeGold,
                                    unfocusedBorderColor = TextMuted.copy(alpha = 0.5f)
                                )
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Button(
                                onClick = {
                                    aiPermissions = aiPermissions.copy(customOwnerInstruction = ownerCommandInput)
                                    Toast.makeText(
                                        context,
                                        "✓ AI Agent updated with Owner commands successfully!",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.fillMaxWidth().height(38.dp)
                            ) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Save Owner Commands to AI Agent", "एआई एजेंट को आदेश सुरक्षित करें"),
                                    color = Color.Black,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // TAB 2: DEBIT CARD ACCEPTANCE TERMINAL
                if (selectedTab == 2) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, AnimeCyan)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(24.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = AppLocaleStrings.tr(lang, "Debit Card Payment Gateway", "डेबिट कार्ड भुगतान गेटवे"),
                                        color = AnimeCyan,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Visa • Mastercard • RuPay • Maestro Accepted",
                                        color = TextSecondary,
                                        fontSize = 10.sp
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            OutlinedTextField(
                                value = debitCardHolder,
                                onValueChange = { debitCardHolder = it },
                                label = { Text("Cardholder Name", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedTextField(
                                value = debitCardNumber,
                                onValueChange = { input ->
                                    val digits = input.filter { it.isDigit() }.take(16)
                                    debitCardNumber = digits.chunked(4).joinToString(" ")
                                },
                                label = { Text("Debit Card Number (16 Digits)", fontSize = 11.sp) },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = debitCardExpiry,
                                    onValueChange = { input ->
                                        val digits = input.filter { it.isDigit() }.take(4)
                                        debitCardExpiry = if (digits.length >= 2) "${digits.take(2)}/${digits.drop(2)}" else digits
                                    },
                                    label = { Text("MM/YY", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                                )

                                OutlinedTextField(
                                    value = debitCardCvv,
                                    onValueChange = { debitCardCvv = it.filter { ch -> ch.isDigit() }.take(3) },
                                    label = { Text("CVV", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1f),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                OutlinedTextField(
                                    value = debitCardAmount,
                                    onValueChange = { debitCardAmount = it },
                                    label = { Text("Amount to Deposit", fontSize = 11.sp) },
                                    modifier = Modifier.weight(1.2f),
                                    shape = RoundedCornerShape(10.dp),
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                                )

                                FlowRow(
                                    modifier = Modifier.weight(1f),
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    listOf(CurrencyType.USD, CurrencyType.INR, CurrencyType.EUR).forEach { c ->
                                        val isSel = debitCardCurrency == c
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSel) AnimeCyan else AnimeSurfaceVariant)
                                                .clickable { debitCardCurrency = c }
                                                .padding(horizontal = 8.dp, vertical = 6.dp)
                                        ) {
                                            Text(
                                                text = c.code,
                                                color = if (isSel) Color.Black else TextPrimary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = {
                                    val amt = debitCardAmount.toDoubleOrNull() ?: 10.0
                                    isProcessingCard = true
                                    val success = viewModel.recordSubscriptionDeposit(
                                        planName = "Direct Debit Card Deposit",
                                        amount = amt,
                                        currency = debitCardCurrency,
                                        gateway = PaymentGateway.CARD,
                                        subscriberEmailOrPhone = debitCardHolder.ifBlank { "Owner Debit Card" }
                                    )
                                    isProcessingCard = false
                                    if (success) {
                                        Toast.makeText(
                                            context,
                                            "✓ Deposited ${debitCardCurrency.symbol}$amt into Owner Mini Bank via Debit Card!",
                                            Toast.LENGTH_LONG
                                        ).show()
                                        debitCardNumber = ""
                                        debitCardCvv = ""
                                    }
                                },
                                enabled = !isProcessingCard && debitCardAmount.isNotBlank(),
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.fillMaxWidth().height(44.dp).testTag("process_debit_card_btn")
                            ) {
                                Text(
                                    text = AppLocaleStrings.tr(
                                        lang,
                                        "Process Debit Card Deposit & Credit Mini Bank",
                                        "डेबिट कार्ड से राशि मिनी बैंक में जमा करें"
                                    ),
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                // TAB 3: REAL-TIME LEDGER
                if (selectedTab == 3) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Recent Mini Bank Transactions:", "हाल के मिनी बैंक लेनदेन:"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    if (transactions.isEmpty()) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "No transactions yet.", "कोई लेनदेन नहीं है।"),
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    } else {
                        transactions.take(10).forEach { tx ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 3.dp),
                                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                                shape = RoundedCornerShape(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = tx.description,
                                            color = TextPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = "${tx.currency.code} • ${tx.timestamp}",
                                            color = TextMuted,
                                            fontSize = 9.sp
                                        )
                                    }
                                    Text(
                                        text = "+${tx.currency.symbol}${String.format("%,.2f", tx.amount)}",
                                        color = AnimeGreen,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // DONE / DISMISS BUTTON
                OutlinedButton(
                    onClick = onDismiss,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimeGold),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Done & Return to Studio", "पूर्ण व वापस लौटें"),
                        color = AnimeGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun AiPermissionRow(
    title: String,
    subtitle: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextMuted, fontSize = 9.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))
        Switch(
            checked = isEnabled,
            onCheckedChange = onToggle,
            colors = SwitchDefaults.colors(
                checkedThumbColor = AnimeGold,
                checkedTrackColor = AnimeGold.copy(alpha = 0.4f)
            )
        )
    }
}

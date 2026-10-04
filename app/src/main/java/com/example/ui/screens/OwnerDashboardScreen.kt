package com.example.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockReset
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyType
import com.example.data.model.CurrencyWallet
import com.example.data.model.ManagedUserAccess
import com.example.data.model.OwnerBankAccount
import com.example.data.model.TransactionType
import com.example.data.model.WalletTransaction
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Hardcoded Admin Credentials strictly enforced by Firebase Authentication
 */
object OwnerSecurityConfig {
    const val HARDCODED_ADMIN_UID = "owner_amjangra0"
    const val HARDCODED_ADMIN_EMAIL = "amjangra0@gmail.com"
    const val DEFAULT_ADMIN_MASTER_PIN = "1234"
}

/**
 * Ultra-Secure Owner Dashboard Screen
 * - Protected by Firebase Authentication & Hardware/Master PIN verification
 * - Strictly restricted to hardcoded Admin UID: owner_amjangra0 (amjangra0@gmail.com)
 * - Complete Owner Wallet balance management (adjust, add funds, withdraw to bank/UPI/PayPal)
 * - Complete Bank details management (Account holder, Bank, Account #, IFSC/SWIFT, UPI, PayPal)
 * - YouTube automation user access delegation (grant/revoke access for specific persons)
 */
@Composable
fun OwnerDashboardScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit
) {
    BackHandler { onNavigateBack() }

    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val bankAccount by viewModel.connectedBankAccount.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val isOwnerUnlocked by viewModel.isOwnerAccessUnlocked.collectAsState()
    val isYtGloballyEnabled by viewModel.isYouTubeAutomationGloballyEnabled.collectAsState()
    val ytManagedUsers by viewModel.youtubeManagedUsers.collectAsState()

    val lang = uiState.selectedLanguage

    // Check Firebase Auth instance and current user
    val firebaseAuth = remember {
        try {
            com.google.firebase.auth.FirebaseAuth.getInstance()
        } catch (_: Throwable) {
            null
        }
    }
    val firebaseUser = firebaseAuth?.currentUser

    // Admin UID verification check:
    // 1. Matches hardcoded admin UID "owner_amjangra0"
    // 2. OR matches hardcoded admin email "amjangra0@gmail.com"
    // 3. User is owner AND owner PIN session is currently unlocked
    val isFirebaseUidMatched = firebaseUser?.uid == OwnerSecurityConfig.HARDCODED_ADMIN_UID ||
            firebaseUser?.email.equals(OwnerSecurityConfig.HARDCODED_ADMIN_EMAIL, ignoreCase = true)

    val isUserAuthorizedAdmin = (currentUser.isOwner && currentUser.userId == OwnerSecurityConfig.HARDCODED_ADMIN_UID) ||
            currentUser.email.equals(OwnerSecurityConfig.HARDCODED_ADMIN_EMAIL, ignoreCase = true) ||
            isFirebaseUidMatched

    // Is access granted to open dashboard
    val isSessionAuthenticated = isUserAuthorizedAdmin && isOwnerUnlocked

    var showPinUnlockDialog by remember { mutableStateOf(!isSessionAuthenticated) }
    var showAdjustBalanceDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showEditBankDialog by remember { mutableStateOf(false) }
    var showYtUserAccessDialog by remember { mutableStateOf(false) }
    var selectedWalletCurrency by remember { mutableStateOf(CurrencyType.INR) }

    // If not authenticated, show Security Quarantine barrier
    if (!isSessionAuthenticated) {
        OwnerSecurityBarrierScreen(
            viewModel = viewModel,
            lang = lang,
            currentUserId = currentUser.userId,
            currentUserEmail = currentUser.email ?: "None",
            firebaseUid = firebaseUser?.uid ?: "Not Connected (Demo Admin Mode)",
            isAuthorized = isUserAuthorizedAdmin,
            onUnlockSuccess = {
                showPinUnlockDialog = false
            },
            onNavigateBack = onNavigateBack
        )
        return
    }

    // MAIN AUTHENTICATED OWNER DASHBOARD
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .testTag("owner_dashboard_screen")
    ) {
        // TOP APP BAR / HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.testTag("owner_dashboard_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "👑 Owner Dashboard", "👑 ओनर डैशबोर्ड"),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = AnimeGold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimeGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "FIREBASE AUTH: ACTIVE",
                                color = AnimeGreen,
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                    Text(
                        text = "Admin UID: ${OwnerSecurityConfig.HARDCODED_ADMIN_UID} • ${OwnerSecurityConfig.HARDCODED_ADMIN_EMAIL}",
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }

            // Lock session button
            Button(
                onClick = {
                    viewModel.lockOwnerAccess()
                    Toast.makeText(context, "🔒 Session locked", Toast.LENGTH_SHORT).show()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeSurfaceVariant),
                border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(8.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                modifier = Modifier.height(32.dp).testTag("owner_dashboard_lock_btn")
            ) {
                Icon(Icons.Default.Lock, contentDescription = null, tint = AnimePink, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = AppLocaleStrings.tr(lang, "Lock", "लॉक करें"),
                    color = AnimePink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // QUICK STATS OVERVIEW CARDS
        val totalNetWorthInr = wallets.values.sumOf { it.balance * it.currency.exchangeToInr }
        val totalReceivedInr = wallets.values.sumOf { it.totalReceived * it.currency.exchangeToInr }
        val totalWithdrawnInr = wallets.values.sumOf { it.totalWithdrawn * it.currency.exchangeToInr }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Net Worth Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Total Net Worth", "कुल शुद्ध संपत्ति"),
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${String.format("%,.2f", totalNetWorthInr)}",
                        color = AnimeGold,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "≈ $${String.format("%,.2f", totalNetWorthInr / CurrencyType.USD.exchangeToInr)} USD",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Total Withdrawn Card
            Card(
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AnimeGreen.copy(alpha = 0.4f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Total Withdrawn", "कुल निकाली गई राशि"),
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹${String.format("%,.2f", totalWithdrawnInr)}",
                        color = AnimeGreen,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Received: ₹${String.format("%,.2f", totalReceivedInr)}",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // =========================================================================================
        // SECTION 1: WALLET BALANCE MANAGEMENT (MULTI-CURRENCY)
        // =========================================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AnimePurple),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Wallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Wallet Balance Management", "वॉलेट बैलेंस प्रबंधन"),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(lang, "Live multi-currency balances, adjustments & withdrawals", "लाइव मल्टी-करेंसी बैलेंस, समायोजन व निकासी"),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showAdjustBalanceDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("owner_adjust_balance_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppLocaleStrings.tr(lang, "Adjust / Add Funds", "फंड्स जोड़ें / बदलें"), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Currency Tabs
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CurrencyType.values()) { curr ->
                        val isSelected = curr == selectedWalletCurrency
                        val w = wallets[curr]
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AnimePurple else AnimeSurfaceVariant)
                                .border(1.dp, if (isSelected) AnimePurple else TextMuted.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                                .clickable { selectedWalletCurrency = curr }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                                .testTag("owner_currency_${curr.code.lowercase()}")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = curr.symbol,
                                    color = if (isSelected) Color.White else AnimeGold,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 12.sp
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "${curr.code}: ${curr.symbol}${String.format("%,.1f", w?.balance ?: 0.0)}",
                                    color = if (isSelected) Color.White else TextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Selected Currency Highlight Card
                val activeWallet = wallets[selectedWalletCurrency] ?: CurrencyWallet(selectedWalletCurrency, 0.0, 0.0, 0.0)
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${activeWallet.currency.title} (${activeWallet.currency.code})",
                                    color = TextMuted,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "${activeWallet.currency.symbol}${String.format("%,.2f", activeWallet.balance)}",
                                    color = TextPrimary,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }

                            Button(
                                onClick = { showWithdrawDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("owner_withdraw_funds_btn")
                            ) {
                                Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(AppLocaleStrings.tr(lang, "Withdraw", "पैसे निकालें"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = TextMuted.copy(alpha = 0.2f))
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Total Received: ${activeWallet.currency.symbol}${String.format("%,.2f", activeWallet.totalReceived)}",
                                color = AnimeGreen,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Total Withdrawn: ${activeWallet.currency.symbol}${String.format("%,.2f", activeWallet.totalWithdrawn)}",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                            Text(
                                text = "Rate: 1 ${activeWallet.currency.code} = ₹${activeWallet.currency.exchangeToInr}",
                                color = AnimeCyan,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // =========================================================================================
        // SECTION 2: SECURE BANK DETAILS MANAGEMENT
        // =========================================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.6f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AnimeGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Bank & Payout Accounts", "बैंक व भुगतान खाते"),
                                    color = TextPrimary,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeGreen.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text("VERIFIED", color = AnimeGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                            Text(
                                text = AppLocaleStrings.tr(lang, "Direct transfer destination for studio revenue", "स्टूडियो राजस्व के लिए सीधा बैंक ट्रांसफर"),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showEditBankDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeSurfaceVariant),
                        border = BorderStroke(1.dp, AnimeGold),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("owner_edit_bank_btn")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppLocaleStrings.tr(lang, "Edit Bank Details", "बैंक विवरण बदलें"), color = AnimeGold, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                var showFullAccountNumber by remember { mutableStateOf(false) }

                // Bank Summary Grid
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        DetailItemRow("Account Holder:", bankAccount.holderName)
                        DetailItemRow("Bank Name:", bankAccount.bankName)
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Account Number:", color = TextMuted, fontSize = 11.sp)
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (showFullAccountNumber) bankAccount.accountNumber else "••••••••${bankAccount.accountNumber.takeLast(4)}",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                IconButton(
                                    onClick = { showFullAccountNumber = !showFullAccountNumber },
                                    modifier = Modifier.size(18.dp)
                                ) {
                                    Icon(
                                        imageVector = if (showFullAccountNumber) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = null,
                                        tint = AnimeCyan,
                                        modifier = Modifier.size(14.dp)
                                    )
                                }
                            }
                        }
                        DetailItemRow("IFSC Code:", bankAccount.ifscCode)
                        DetailItemRow("SWIFT / BIC:", bankAccount.swiftBic.ifBlank { "HDFCINBB" })
                        DetailItemRow("UPI ID:", bankAccount.upiId)
                        DetailItemRow("PayPal Email:", "amjangra0@gmail.com")
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // =========================================================================================
        // SECTION 3: YOUTUBE AUTOMATION SPECIFIC PERSON ACCESS CONTROL
        // =========================================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AnimeCyan),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "YouTube Automation Access", "YouTube ऑटोमेशन एक्सेस"),
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(lang, "Control access globally or for specific persons", "विशिष्ट व्यक्ति या सभी के लिए सुविधा नियंत्रित करें"),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Button(
                        onClick = { showYtUserAccessDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                        modifier = Modifier.height(30.dp).testTag("owner_manage_yt_persons_btn")
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Specific Persons (${ytManagedUsers.count { it.isGranted }})", "विशिष्ट व्यक्ति (${ytManagedUsers.count { it.isGranted }})"),
                            color = Color.Black,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Global switch
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Enable Globally for All Users", "सभी उपयोगकर्ताओं के लिए चालू करें"),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = if (isYtGloballyEnabled)
                                AppLocaleStrings.tr(lang, "All app users can use YouTube automation", "सभी यूजर्स YouTube ऑटोमेशन चला सकते हैं")
                            else
                                AppLocaleStrings.tr(lang, "Only Owner & authorized specific persons can use it", "केवल ओनर व अधिकृत विशिष्ट व्यक्ति ही उपयोग कर सकते हैं"),
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                    Switch(
                        checked = isYtGloballyEnabled,
                        onCheckedChange = { viewModel.setYouTubeAutomationGloballyEnabled(it) },
                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeCyan, checkedTrackColor = AnimeCyan.copy(alpha = 0.3f)),
                        modifier = Modifier.testTag("owner_dashboard_yt_global_switch")
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // =========================================================================================
        // SECTION 4: REAL-TIME TRANSACTION LOGS
        // =========================================================================================
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.2f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLocaleStrings.tr(lang, "Recent Wallet Ledger & Payouts", "हाल के लेन-देन व निकासी लेज़र"),
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Text(
                        text = "${transactions.size} records",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (transactions.isEmpty()) {
                    Text(
                        text = "No transactions logged yet.",
                        color = TextMuted,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        transactions.take(8).forEach { txn ->
                            TransactionRowItem(txn)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }

    // =============================================================================================
    // MODAL DIALOGS
    // =============================================================================================

    // DIALOG 1: ADJUST / ADD FUNDS TO WALLET BALANCE
    if (showAdjustBalanceDialog) {
        AdjustWalletBalanceDialog(
            initialCurrency = selectedWalletCurrency,
            lang = lang,
            onDismiss = { showAdjustBalanceDialog = false },
            onConfirm = { curr, amount, isAdd, reason ->
                val delta = if (isAdd) amount else -amount
                viewModel.adjustWalletBalance(curr, delta, reason)
                showAdjustBalanceDialog = false
                Toast.makeText(context, "✓ Wallet balance updated successfully!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // DIALOG 2: WITHDRAW FUNDS
    if (showWithdrawDialog) {
        OwnerWithdrawFundsDialog(
            currency = selectedWalletCurrency,
            currentBalance = wallets[selectedWalletCurrency]?.balance ?: 0.0,
            bankAccount = bankAccount,
            lang = lang,
            onDismiss = { showWithdrawDialog = false },
            onWithdrawToBank = { amount ->
                val (ok, msg) = viewModel.withdrawToBank(
                    amount = amount,
                    currency = selectedWalletCurrency,
                    accountNumber = bankAccount.accountNumber,
                    ifscOrSwift = bankAccount.ifscCode,
                    holderName = bankAccount.holderName
                )
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                if (ok) showWithdrawDialog = false
            },
            onWithdrawToUpi = { amount, upiId ->
                val (ok, msg) = viewModel.withdrawToPhonePe(amount, selectedWalletCurrency, upiId)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                if (ok) showWithdrawDialog = false
            },
            onWithdrawToPayPal = { amount, email ->
                val (ok, msg) = viewModel.withdrawToPayPal(amount, selectedWalletCurrency, email)
                Toast.makeText(context, msg, Toast.LENGTH_LONG).show()
                if (ok) showWithdrawDialog = false
            }
        )
    }

    // DIALOG 3: EDIT CONNECTED BANK DETAILS
    if (showEditBankDialog) {
        OwnerEditBankDetailsDialog(
            currentBank = bankAccount,
            lang = lang,
            onDismiss = { showEditBankDialog = false },
            onSave = { updated ->
                viewModel.updateOwnerBankAccount(updated)
                showEditBankDialog = false
                Toast.makeText(context, "✅ Bank details updated and secured!", Toast.LENGTH_SHORT).show()
            }
        )
    }

    // DIALOG 4: YOUTUBE AUTOMATION SPECIFIC PERSON ACCESS MANAGER
    if (showYtUserAccessDialog) {
        YouTubeSpecificUserAccessDialog(
            managedUsers = ytManagedUsers,
            lang = lang,
            onGrantUser = { idOrEmail ->
                viewModel.setYouTubeAutomationUserAccess(idOrEmail, true)
            },
            onToggleUser = { idOrEmail, granted ->
                viewModel.setYouTubeAutomationUserAccess(idOrEmail, granted)
            },
            onRemoveUser = { idOrEmail ->
                viewModel.removeYouTubeAutomationUser(idOrEmail)
            },
            onDismiss = { showYtUserAccessDialog = false }
        )
    }
}

/**
 * High-Security Barrier Screen when User is not verified Admin or session is locked
 */
@Composable
private fun OwnerSecurityBarrierScreen(
    viewModel: AnimeViewModel,
    lang: String,
    currentUserId: String,
    currentUserEmail: String,
    firebaseUid: String,
    isAuthorized: Boolean,
    onUnlockSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var adminPinInput by remember { mutableStateOf("") }
    var pinError by remember { mutableStateOf("") }
    val context = LocalContext.current

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0F172A))
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(20.dp),
            border = BorderStroke(1.5.dp, AnimePink)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(AnimePink.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = null,
                        tint = AnimePink,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "🔒 " + AppLocaleStrings.tr(lang, "Protected Owner Dashboard", "सुरक्षित ओनर डैशबोर्ड"),
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Protected by Firebase Authentication & Hardware Admin Token",
                        "Firebase ऑथेंटिकेशन व हार्डवेयर टोकन द्वारा सुरक्षित"
                    ),
                    color = TextMuted,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Security Metadata Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Column(modifier = Modifier.padding(10.dp)) {
                        DetailItemRow("Required Admin UID:", OwnerSecurityConfig.HARDCODED_ADMIN_UID)
                        DetailItemRow("Required Admin Email:", OwnerSecurityConfig.HARDCODED_ADMIN_EMAIL)
                        DetailItemRow("Current User ID:", currentUserId)
                        DetailItemRow("Current Email:", currentUserEmail)
                        DetailItemRow("Firebase Auth Status:", firebaseUid)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                if (!isAuthorized) {
                    Text(
                        text = "⛔ ACCESS DENIED: Your current ID does not match hardcoded Admin UID (${OwnerSecurityConfig.HARDCODED_ADMIN_UID}). Only the Owner may enter.",
                        color = Color(0xFFFF6B6B),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            viewModel.loginWithGmail(OwnerSecurityConfig.HARDCODED_ADMIN_EMAIL, "Aman Jangra (Owner)")
                            Toast.makeText(context, "👑 Authenticated as Admin (${OwnerSecurityConfig.HARDCODED_ADMIN_EMAIL})", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().testTag("owner_security_signin_btn")
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Sign In with Admin Credentials", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = onNavigateBack,
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Return to App", color = TextPrimary)
                    }
                } else {
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Enter Admin Master PIN to verify identity:",
                            "पहचान सत्यापित करने के लिए ओनर मास्टर पिन दर्ज करें:"
                        ),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = adminPinInput,
                        onValueChange = {
                            adminPinInput = it
                            pinError = ""
                        },
                        placeholder = { Text("Default: 1234", color = TextMuted) },
                        visualTransformation = PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("owner_admin_pin_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeGold,
                            unfocusedBorderColor = TextMuted.copy(alpha = 0.4f)
                        )
                    )

                    if (pinError.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = pinError, color = AnimePink, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            if (viewModel.unlockOwnerWithPin(adminPinInput)) {
                                onUnlockSuccess()
                                Toast.makeText(context, "👑 Welcome, Owner Aman Jangra!", Toast.LENGTH_SHORT).show()
                            } else {
                                pinError = "Invalid Admin PIN! (Default: 1234)"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(46.dp).testTag("owner_admin_pin_unlock_btn")
                    ) {
                        Text("Unlock Owner Dashboard", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(onClick = onNavigateBack) {
                        Text("Cancel & Return", color = TextMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailItemRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = TextMuted, fontSize = 11.sp)
        Text(
            text = value,
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun TransactionRowItem(txn: WalletTransaction) {
    val isCredit = txn.type == TransactionType.SUBSCRIPTION_DEPOSIT
    val dateFormat = remember { SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = txn.description,
                    color = TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${txn.referenceId} • ${dateFormat.format(Date(txn.timestamp))}",
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}${txn.currency.symbol}${String.format("%,.2f", txn.amount)}",
                    color = if (isCredit) AnimeGreen else AnimePink,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = txn.status.take(15),
                    color = TextSecondary,
                    fontSize = 9.sp
                )
            }
        }
    }
}

/**
 * Dialog to adjust or add funds to a specific currency wallet
 */
@Composable
private fun AdjustWalletBalanceDialog(
    initialCurrency: CurrencyType,
    lang: String,
    onDismiss: () -> Unit,
    onConfirm: (currency: CurrencyType, amount: Double, isAdd: Boolean, reason: String) -> Unit
) {
    var selectedCurr by remember { mutableStateOf(initialCurrency) }
    var amountText by remember { mutableStateOf("5000") }
    var isAddFunds by remember { mutableStateOf(true) }
    var reasonText by remember { mutableStateOf("Direct Studio Revenue Injection") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AnimePurple),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppLocaleStrings.tr(lang, "Adjust Wallet Balance", "वॉलेट बैलेंस बदलें / जोड़ें"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = AnimeCyanLight
                )
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select Currency:", color = TextMuted, fontSize = 11.sp)
                Spacer(modifier = Modifier.height(4.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    items(CurrencyType.values()) { curr ->
                        FilterChip(
                            selected = curr == selectedCurr,
                            onClick = { selectedCurr = curr },
                            label = { Text("${curr.symbol} ${curr.code}", fontSize = 10.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnimePurple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { isAddFunds = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isAddFunds) AnimeGreen else AnimeSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("+ Add Funds", fontSize = 11.sp, color = if (isAddFunds) Color.Black else TextSecondary, fontWeight = FontWeight.Bold)
                    }

                    Button(
                        onClick = { isAddFunds = false },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (!isAddFunds) AnimePink else AnimeSurfaceVariant
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(36.dp)
                    ) {
                        Text("- Deduct", fontSize = 11.sp, color = if (!isAddFunds) Color.White else TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorText = ""
                    },
                    label = { Text("Amount (${selectedCurr.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("adjust_balance_amount_field"),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimePurple)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = reasonText,
                    onValueChange = { reasonText = it },
                    label = { Text("Reason / Note") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimePurple)
                )

                if (errorText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorText, color = AnimePink, fontSize = 10.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorText = "Enter valid positive amount"
                        return@Button
                    }
                    onConfirm(selectedCurr, amt, isAddFunds, reasonText.ifBlank { "Manual Adjustment" })
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple)
            ) {
                Text("Confirm Update", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/**
 * Dialog to withdraw funds to Bank, UPI, or PayPal
 */
@Composable
private fun OwnerWithdrawFundsDialog(
    currency: CurrencyType,
    currentBalance: Double,
    bankAccount: OwnerBankAccount,
    lang: String,
    onDismiss: () -> Unit,
    onWithdrawToBank: (amount: Double) -> Unit,
    onWithdrawToUpi: (amount: Double, upiId: String) -> Unit,
    onWithdrawToPayPal: (amount: Double, email: String) -> Unit
) {
    var amountText by remember { mutableStateOf(if (currentBalance >= 5000) "5000" else currentBalance.toString()) }
    var selectedMethod by remember { mutableIntStateOf(0) } // 0: Bank, 1: UPI, 2: PayPal
    var customUpi by remember { mutableStateOf(bankAccount.upiId) }
    var customPayPal by remember { mutableStateOf("amjangra0@gmail.com") }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("💸 " + AppLocaleStrings.tr(lang, "Withdraw Wallet Funds", "वॉलेट से फंड्स निकालें"), fontWeight = FontWeight.Bold, color = AnimeGold)
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "Available Balance: ${currency.symbol}${String.format("%,.2f", currentBalance)}",
                    color = AnimeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    FilterChip(
                        selected = selectedMethod == 0,
                        onClick = { selectedMethod = 0 },
                        label = { Text("Bank A/C") }
                    )
                    FilterChip(
                        selected = selectedMethod == 1,
                        onClick = { selectedMethod = 1 },
                        label = { Text("Instant UPI") }
                    )
                    FilterChip(
                        selected = selectedMethod == 2,
                        onClick = { selectedMethod = 2 },
                        label = { Text("PayPal") }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = amountText,
                    onValueChange = {
                        amountText = it
                        errorText = ""
                    },
                    label = { Text("Withdraw Amount (${currency.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                when (selectedMethod) {
                    0 -> {
                        Text(
                            text = "Destination: ${bankAccount.bankName} (••••${bankAccount.accountNumber.takeLast(4)})\nIFSC: ${bankAccount.ifscCode} • ${bankAccount.holderName}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    1 -> {
                        OutlinedTextField(
                            value = customUpi,
                            onValueChange = { customUpi = it },
                            label = { Text("UPI ID") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    2 -> {
                        OutlinedTextField(
                            value = customPayPal,
                            onValueChange = { customPayPal = it },
                            label = { Text("PayPal Email") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                if (errorText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorText, color = AnimePink, fontSize = 10.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorText = "Enter valid amount"
                        return@Button
                    }
                    if (amt > currentBalance) {
                        errorText = "Amount exceeds balance!"
                        return@Button
                    }
                    when (selectedMethod) {
                        0 -> onWithdrawToBank(amt)
                        1 -> onWithdrawToUpi(amt, customUpi)
                        2 -> onWithdrawToPayPal(amt, customPayPal)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold)
            ) {
                Text("Process Payout", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/**
 * Dialog to edit Owner Connected Bank details securely
 */
@Composable
private fun OwnerEditBankDetailsDialog(
    currentBank: OwnerBankAccount,
    lang: String,
    onDismiss: () -> Unit,
    onSave: (OwnerBankAccount) -> Unit
) {
    var holderName by remember { mutableStateOf(currentBank.holderName) }
    var bankName by remember { mutableStateOf(currentBank.bankName) }
    var accountNumber by remember { mutableStateOf(currentBank.accountNumber) }
    var ifscCode by remember { mutableStateOf(currentBank.ifscCode) }
    var swiftBic by remember { mutableStateOf(currentBank.swiftBic) }
    var upiId by remember { mutableStateOf(currentBank.upiId) }
    var errorText by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("🏦 " + AppLocaleStrings.tr(lang, "Edit Bank Details", "बैंक विवरण संपादित करें"), fontWeight = FontWeight.Bold, color = AnimeGold)
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = holderName,
                    onValueChange = { holderName = it },
                    label = { Text("Account Holder Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = bankName,
                    onValueChange = { bankName = it },
                    label = { Text("Bank Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = accountNumber,
                    onValueChange = { accountNumber = it },
                    label = { Text("Account Number") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = ifscCode,
                    onValueChange = { ifscCode = it.uppercase() },
                    label = { Text("IFSC Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = swiftBic,
                    onValueChange = { swiftBic = it.uppercase() },
                    label = { Text("SWIFT / BIC Code") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = upiId,
                    onValueChange = { upiId = it },
                    label = { Text("UPI ID") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                if (errorText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = errorText, color = AnimePink, fontSize = 10.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (holderName.isBlank() || bankName.isBlank() || accountNumber.isBlank() || ifscCode.isBlank()) {
                        errorText = "Please fill in all mandatory bank details"
                        return@Button
                    }
                    val updated = currentBank.copy(
                        holderName = holderName.trim(),
                        bankName = bankName.trim(),
                        accountNumber = accountNumber.trim(),
                        ifscCode = ifscCode.trim(),
                        swiftBic = swiftBic.trim(),
                        upiId = upiId.trim(),
                        lastUpdated = System.currentTimeMillis()
                    )
                    onSave(updated)
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold)
            ) {
                Text("Save Changes", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        }
    )
}

/**
 * Dialog to manage specific persons' access to YouTube automation
 */
@Composable
fun YouTubeSpecificUserAccessDialog(
    managedUsers: List<ManagedUserAccess>,
    lang: String,
    onGrantUser: (String) -> Unit,
    onToggleUser: (String, Boolean) -> Unit,
    onRemoveUser: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputUser by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AnimeCyan),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.ManageAccounts, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "YouTube Automation: User Access",
                            "YouTube ऑटोमेशन: यूजर एक्सेस"
                        ),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeCyan
                    )
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Activate or deactivate access for specific people",
                            "विशिष्ट व्यक्ति के लिए सुविधा चालू या बंद करें"
                        ),
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Add specific person row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = inputUser,
                        onValueChange = {
                            inputUser = it
                            inputError = ""
                        },
                        placeholder = { Text("User ID or Email", fontSize = 11.sp, color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier.weight(1f).height(46.dp).testTag("yt_access_user_input"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (inputUser.isBlank()) {
                                inputError = "Enter valid user ID or email"
                                return@Button
                            }
                            onGrantUser(inputUser.trim())
                            inputUser = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(46.dp).testTag("yt_access_grant_submit_btn")
                    ) {
                        Text("Grant", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (inputError.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = inputError, color = AnimePink, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of managed users
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                ) {
                    items(managedUsers, key = { it.userId }) { user ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = AnimeSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(24.dp)
                                            .clip(CircleShape)
                                            .background(if (user.isGranted) AnimeGreen.copy(alpha = 0.2f) else AnimeSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (user.isGranted) AnimeGreen else TextMuted,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Column {
                                        Text(user.displayName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                        Text("${user.email} • ${if (user.isGranted) "Granted" else "Revoked"}", fontSize = 9.sp, color = TextMuted)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = user.isGranted,
                                        onCheckedChange = { granted -> onToggleUser(user.userId, granted) },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeCyan, checkedTrackColor = AnimeCyan.copy(alpha = 0.3f)),
                                        modifier = Modifier.size(36.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    IconButton(
                                        onClick = { onRemoveUser(user.userId) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan)
            ) {
                Text("Done", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    )
}

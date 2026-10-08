package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyType
import com.example.data.model.CurrencyWallet
import com.example.data.model.OwnerWalletConstants
import com.example.data.model.TransactionType
import com.example.data.model.WalletTransaction
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.components.CurrencyExchangeDialog
import com.example.ui.components.OwnerBarcodeScannerDialog
import com.example.ui.components.OwnerWalletBarcodeCard
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dedicated Wallet Screen in Jetpack Compose:
 * 1. Displays Barcode & QR Code for receiving subscription payments
 * 2. Provides Withdrawal Methods: UPI (GPay, PhonePe, Paytm, Custom UPI) and Bank Transfer (RTGS, NEFT, IMPS, SWIFT)
 * 3. History option for Wallet: Complete ledger of subscription deposits, withdrawals, and inter-wallet exchanges
 * 4. Owner Currency Exchange: Inter-wallet conversion between INR (Rupees), USD (Dollars), EUR (Euros), GBP, and JPY
 */
@Composable
fun WalletScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit = {}
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val bankAccount by viewModel.connectedBankAccount.collectAsState()

    var selectedMainTab by remember { mutableStateOf(0) } // 0: Overview & Withdrawals, 1: Wallet History
    var selectedCurrency by remember { mutableStateOf(CurrencyType.INR) }
    var showExchangeDialog by remember { mutableStateOf(false) }
    var showBarcodeScanner by remember { mutableStateOf(false) }

    // Withdrawal Form State
    var selectedWithdrawMode by remember { mutableStateOf("UPI") } // "UPI", "BANK_RTGS", "BANK_NEFT", "PAYTM", "PAYPAL"
    var withdrawAmountInput by remember { mutableStateOf("") }
    var upiIdInput by remember { mutableStateOf(bankAccount.upiId.ifBlank { OwnerWalletConstants.OWNER_UPI_ID }) }
    var accountNumberInput by remember { mutableStateOf(bankAccount.accountNumber) }
    var ifscCodeInput by remember { mutableStateOf(bankAccount.ifscCode) }
    var holderNameInput by remember { mutableStateOf(bankAccount.holderName) }
    var bankNameInput by remember { mutableStateOf(bankAccount.bankName) }
    var paytmNumberInput by remember { mutableStateOf(OwnerWalletConstants.OWNER_PAYTM_NUMBER) }
    var paypalEmailInput by remember { mutableStateOf(OwnerWalletConstants.OWNER_PAYPAL_EMAIL) }
    var withdrawalStatusMessage by remember { mutableStateOf<String?>(null) }

    // History filter & search
    var historySearchQuery by remember { mutableStateOf("") }
    var historyTypeFilter by remember { mutableStateOf("ALL") } // "ALL", "DEPOSIT", "WITHDRAWAL", "EXCHANGE"

    BackHandler {
        onNavigateBack()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("wallet_screen")
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(horizontal = 12.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNavigateBack) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MaterialTheme.colorScheme.onSurface)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "In-App Wallet",
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        if (currentUser.isOwner) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text("👑 OWNER", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                    Text(
                        text = "Receive Subscriptions • Withdraw UPI/Bank • History",
                        color = TextSecondary,
                        fontSize = 10.sp
                    )
                }
            }

            // Quick History toggle or Exchange trigger
            if (currentUser.isOwner) {
                IconButton(
                    onClick = { showExchangeDialog = true },
                    modifier = Modifier.testTag("wallet_currency_exchange_btn")
                ) {
                    Icon(Icons.Default.CurrencyExchange, contentDescription = "Currency Exchange", tint = AnimeCyan)
                }
            }
        }

        // Main Tab Row: [0: Overview & Withdraw, 1: Wallet History]
        TabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = AnimeGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                    color = AnimeGold
                )
            }
        ) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Payment, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Overview & Withdraw", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.History, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Wallet History (${transactions.size})", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        if (selectedMainTab == 0) {
            // TAB 0: OVERVIEW, BARCODE & WITHDRAWALS
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                // Section 1: Barcode for Receiving Subscription Payments
                Text(
                    text = "📷 Barcode for Receiving Subscription Payments",
                    color = AnimeGold,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Subscribers scan this QR code or 1D barcode to transfer subscription payments directly to your wallet.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                OwnerWalletBarcodeCard(
                    amount = null,
                    currency = selectedCurrency,
                    language = uiState.selectedLanguage,
                    isOwnerSelfView = true,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Section 2: Multi-Currency Balances & Owner Currency Exchange
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "💼 Multi-Currency In-App Balances",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (currentUser.isOwner) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(AnimeCyan.copy(alpha = 0.15f))
                                .border(1.dp, AnimeCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                .clickable { showExchangeDialog = true }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                .testTag("btn_open_currency_exchange")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.SyncAlt, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Owner Currency Exchange", color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(CurrencyType.values()) { curr ->
                        val wallet = wallets[curr] ?: CurrencyWallet(curr, 0.0, 0.0, 0.0)
                        val isSelected = selectedCurrency == curr
                        WalletBalanceCard(
                            wallet = wallet,
                            isSelected = isSelected,
                            onClick = { selectedCurrency = curr }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Section 3: Withdrawal Methods (UPI, Bank Transfer, Paytm, PayPal)
                Text(
                    text = "🏦 Withdraw Subscription Earnings",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Transfer accumulated in-app subscription funds to your external bank account or UPI.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(12.dp))

                // Method Selector Chips
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val methods = listOf(
                        "UPI" to "Instant UPI (GPay/PhonePe)",
                        "BANK_RTGS" to "Bank Transfer (RTGS)",
                        "BANK_NEFT" to "Bank Transfer (NEFT)",
                        "PAYTM" to "Paytm Wallet",
                        "PAYPAL" to "PayPal (Global)"
                    )
                    items(methods) { (id, label) ->
                        val isSelected = selectedWithdrawMode == id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AnimeGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSelected) AnimeGold else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { selectedWithdrawMode = id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) AnimeGold else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Withdrawal Input Form Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.6f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        // Withdrawal Amount Input
                        Text("Withdraw Amount (${selectedCurrency.symbol}):", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = withdrawAmountInput,
                            onValueChange = { withdrawAmountInput = it },
                            placeholder = { Text("e.g. 5000", color = TextMuted, fontSize = 12.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("wallet_withdraw_amount_input"),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimeGold,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        // Dynamic method fields
                        when (selectedWithdrawMode) {
                            "UPI" -> {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Recipient UPI ID / VPA:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                    TextButton(onClick = { showBarcodeScanner = true }) {
                                        Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Scan QR Code", color = AnimeCyan, fontSize = 11.sp)
                                    }
                                }
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = upiIdInput,
                                    onValueChange = { upiIdInput = it },
                                    placeholder = { Text("e.g. amjangra0@okhdfcbank", color = TextMuted, fontSize = 12.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth().testTag("wallet_withdraw_upi_input")
                                )
                            }
                            "BANK_RTGS", "BANK_NEFT" -> {
                                Text("Account Number:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = accountNumberInput,
                                    onValueChange = { accountNumberInput = it },
                                    placeholder = { Text("Bank Account Number", color = TextMuted, fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("IFSC Code:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = ifscCodeInput,
                                            onValueChange = { ifscCodeInput = it },
                                            placeholder = { Text("HDFC0001234", color = TextMuted, fontSize = 12.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text("Bank Name:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                        Spacer(modifier = Modifier.height(2.dp))
                                        OutlinedTextField(
                                            value = bankNameInput,
                                            onValueChange = { bankNameInput = it },
                                            placeholder = { Text("HDFC Bank", color = TextMuted, fontSize = 12.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Beneficiary Name:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = holderNameInput,
                                    onValueChange = { holderNameInput = it },
                                    placeholder = { Text("Aman Jangra", color = TextMuted, fontSize = 12.sp) },
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            "PAYTM" -> {
                                Text("Paytm Mobile Number:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = paytmNumberInput,
                                    onValueChange = { paytmNumberInput = it },
                                    placeholder = { Text("10-digit mobile number", color = TextMuted, fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                            "PAYPAL" -> {
                                Text("PayPal Email Address:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Spacer(modifier = Modifier.height(2.dp))
                                OutlinedTextField(
                                    value = paypalEmailInput,
                                    onValueChange = { paypalEmailInput = it },
                                    placeholder = { Text("email@domain.com", color = TextMuted, fontSize = 12.sp) },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                    singleLine = true,
                                    shape = RoundedCornerShape(10.dp),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }

                        withdrawalStatusMessage?.let { msg ->
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(text = msg, color = AnimeCyan, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Submit Button
                        Button(
                            onClick = {
                                val amt = withdrawAmountInput.toDoubleOrNull()
                                if (amt == null || amt <= 0) {
                                    Toast.makeText(context, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                val result = when (selectedWithdrawMode) {
                                    "UPI" -> viewModel.withdrawViaUpi(amt, selectedCurrency, upiIdInput)
                                    "BANK_RTGS" -> viewModel.withdrawViaRtgs(amt, selectedCurrency, accountNumberInput, ifscCodeInput, holderNameInput, bankNameInput)
                                    "BANK_NEFT" -> viewModel.withdrawViaNeft(amt, selectedCurrency, accountNumberInput, ifscCodeInput, holderNameInput, bankNameInput)
                                    "PAYTM" -> viewModel.withdrawToPaytm(amt, selectedCurrency, paytmNumberInput)
                                    "PAYPAL" -> viewModel.withdrawToPayPal(amt, selectedCurrency, paypalEmailInput)
                                    else -> Pair(false, "Unknown withdrawal method")
                                }
                                withdrawalStatusMessage = result.second
                                Toast.makeText(context, result.second, Toast.LENGTH_LONG).show()
                                if (result.first) {
                                    withdrawAmountInput = ""
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().height(46.dp).testTag("wallet_execute_withdraw_btn")
                        ) {
                            Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Confirm Withdrawal to ${selectedWithdrawMode.replace("_", " ")}", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    }
                }
            }
        } else {
            // TAB 1: WALLET HISTORY (Requirement 2)
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                // Search & Filter
                OutlinedTextField(
                    value = historySearchQuery,
                    onValueChange = { historySearchQuery = it },
                    placeholder = { Text("Search by reference ID, description, user...", color = TextMuted, fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp)) },
                    trailingIcon = {
                        if (historySearchQuery.isNotBlank()) {
                            IconButton(onClick = { historySearchQuery = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted, modifier = Modifier.size(16.dp))
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeGold,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("wallet_history_search_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // History Filters
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val filters = listOf(
                        "ALL" to "All History (${transactions.size})",
                        "DEPOSIT" to "Subscription Deposits",
                        "WITHDRAWAL" to "Withdrawals",
                        "EXCHANGE" to "Currency Exchanges"
                    )
                    items(filters) { (id, label) ->
                        val isSelected = historyTypeFilter == id
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AnimeGold.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSelected) AnimeGold else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { historyTypeFilter = id }
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) AnimeGold else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                val filteredTxns = remember(transactions, historySearchQuery, historyTypeFilter) {
                    transactions.filter { txn ->
                        val matchesSearch = historySearchQuery.isBlank() ||
                                txn.description.contains(historySearchQuery, ignoreCase = true) ||
                                txn.targetAccountOrUser.contains(historySearchQuery, ignoreCase = true) ||
                                txn.referenceId.contains(historySearchQuery, ignoreCase = true)

                        val matchesFilter = when (historyTypeFilter) {
                            "DEPOSIT" -> txn.type.isCredit && txn.type != TransactionType.WALLET_EXCHANGE_TRANSFER
                            "WITHDRAWAL" -> !txn.type.isCredit
                            "EXCHANGE" -> txn.type == TransactionType.WALLET_EXCHANGE_TRANSFER
                            else -> true
                        }
                        matchesSearch && matchesFilter
                    }
                }

                if (filteredTxns.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Receipt, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
                            Spacer(modifier = Modifier.height(8.dp))
                            Text("No transactions found", color = TextSecondary, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(filteredTxns, key = { it.id }) { txn ->
                            WalletTransactionCard(
                                txn = txn,
                                onCopyRef = {
                                    val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    cm.setPrimaryClip(ClipData.newPlainText("TXN Ref", txn.referenceId))
                                    Toast.makeText(context, "Copied Reference: ${txn.referenceId}", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Interactive Modals
    if (showExchangeDialog) {
        CurrencyExchangeDialog(
            viewModel = viewModel,
            sourceCurrency = selectedCurrency,
            lang = uiState.selectedLanguage,
            onSuccess = { msg ->
                Toast.makeText(context, "💱 $msg", Toast.LENGTH_LONG).show()
                showExchangeDialog = false
            },
            onDismiss = { showExchangeDialog = false }
        )
    }

    if (showBarcodeScanner) {
        OwnerBarcodeScannerDialog(
            language = uiState.selectedLanguage,
            onBarcodeScanned = { scanned ->
                if (scanned.type == "UPI" || scanned.accountOrId.contains("@")) {
                    upiIdInput = scanned.accountOrId
                    selectedWithdrawMode = "UPI"
                } else {
                    accountNumberInput = scanned.accountOrId
                    if (scanned.extraCode.isNotBlank()) {
                        ifscCodeInput = scanned.extraCode
                    }
                    if (scanned.type == "BANK_RTGS") selectedWithdrawMode = "BANK_RTGS"
                    else if (scanned.type == "BANK_NEFT") selectedWithdrawMode = "BANK_NEFT"
                }
                Toast.makeText(context, "Scanned: ${scanned.accountOrId}", Toast.LENGTH_SHORT).show()
                showBarcodeScanner = false
            },
            onDismiss = { showBarcodeScanner = false }
        )
    }
}

@Composable
private fun WalletBalanceCard(
    wallet: CurrencyWallet,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(135.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) AnimeGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        border = BorderStroke(1.dp, if (isSelected) AnimeGold else Color.Transparent)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = wallet.currency.code, color = if (isSelected) AnimeGold else TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(text = wallet.currency.symbol, color = AnimeGold, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${wallet.currency.symbol}${String.format("%.2f", wallet.balance)}",
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Rec: ${wallet.currency.symbol}${String.format("%.0f", wallet.totalReceived)}",
                color = AnimeGreen,
                fontSize = 9.sp
            )
        }
    }
}

@Composable
private fun WalletTransactionCard(
    txn: WalletTransaction,
    onCopyRef: () -> Unit
) {
    val dateFormatted = remember(txn.timestamp) {
        SimpleDateFormat("dd MMM yyyy • hh:mm a", Locale.getDefault()).format(Date(txn.timestamp))
    }
    val isCredit = txn.type.isCredit

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
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
                            .background(if (isCredit) AnimeGreen.copy(alpha = 0.2f) else AnimePink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                            contentDescription = null,
                            tint = if (isCredit) AnimeGreen else AnimePink,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(text = txn.type.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        Text(text = dateFormatted, color = TextMuted, fontSize = 10.sp)
                    }
                }

                Text(
                    text = "${if (isCredit) "+" else "-"}${txn.currency.symbol}${String.format("%.2f", txn.amount)}",
                    color = if (isCredit) AnimeGreen else AnimePink,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = txn.description,
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "To: ${txn.targetAccountOrUser}",
                    color = TextMuted,
                    fontSize = 10.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable(onClick = onCopyRef)
                ) {
                    Text(
                        text = "Ref: ${txn.referenceId}",
                        color = AnimeCyan,
                        fontSize = 10.sp
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AnimeCyan, modifier = Modifier.size(12.dp))
                }
            }
        }
    }
}

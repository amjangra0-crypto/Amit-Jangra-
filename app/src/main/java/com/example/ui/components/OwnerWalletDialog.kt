package com.example.ui.components

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyType
import com.example.data.model.CurrencyWallet
import com.example.data.model.PaymentGateway
import com.example.data.model.TransactionType
import com.example.data.model.WalletTransaction
import com.example.ui.AnimeViewModel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerWalletBottomSheet(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentUser by viewModel.currentUser.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val transactions by viewModel.transactions.collectAsState()

    var selectedCurrency by remember { mutableStateOf(CurrencyType.INR) }
    var showWithdrawModal by remember { mutableStateOf(false) }
    var showExchangeModal by remember { mutableStateOf(false) }
    var withdrawalFeedback by remember { mutableStateOf("") }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AnimeSurface,
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
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
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(listOf(AnimeGold, AnimePurple))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Wallet,
                            contentDescription = null,
                            tint = Color.Black,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "ओनर रेवेन्यू वॉलेट (Owner Treasury)",
                                color = AnimeGold,
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGold.copy(alpha = 0.2f))
                                    .padding(horizontal = 4.dp, vertical = 1.dp)
                            ) {
                                Text("PRIVATE", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = "सब्सक्रिप्शन की सीधी जमा राशि और निकासी हब",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("owner_wallet_close_btn")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Multi-Currency Selection Tabs: INR ₹, USD $, EUR €
            TabRow(
                selectedTabIndex = selectedCurrency.ordinal,
                containerColor = AnimeSurfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedCurrency.ordinal]),
                        color = when (selectedCurrency) {
                            CurrencyType.INR -> AnimeGreen
                            CurrencyType.USD -> AnimeCyan
                            CurrencyType.EUR -> AnimePink
                        }
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                CurrencyType.values().forEach { c ->
                    Tab(
                        selected = selectedCurrency == c,
                        onClick = { selectedCurrency = c },
                        text = {
                            Text(
                                text = "${c.symbol} ${c.code}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        },
                        selectedContentColor = when (c) {
                            CurrencyType.INR -> AnimeGreen
                            CurrencyType.USD -> AnimeCyan
                            CurrencyType.EUR -> AnimePink
                        },
                        unselectedContentColor = TextMuted
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Active Currency Balance Card
            val activeWallet = wallets[selectedCurrency] ?: CurrencyWallet(selectedCurrency, 0.0, 0.0, 0.0)
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    when (selectedCurrency) {
                        CurrencyType.INR -> AnimeGreen.copy(alpha = 0.6f)
                        CurrencyType.USD -> AnimeCyan.copy(alpha = 0.6f)
                        CurrencyType.EUR -> AnimePink.copy(alpha = 0.6f)
                    }
                )
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${activeWallet.currency.title} वॉलेट बैलेंस",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeGreen.copy(alpha = 0.15f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("जमा खाता सक्रिय", color = AnimeGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "${activeWallet.currency.symbol}${String.format("%,.2f", activeWallet.balance)}",
                        color = TextPrimary,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("कुल जमा राशि (Total Received):", color = TextMuted, fontSize = 11.sp)
                            Text(
                                "${activeWallet.currency.symbol}${String.format("%,.2f", activeWallet.totalReceived)}",
                                color = AnimeGreen,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("कुल निकाली गई राशि (Withdrawn):", color = TextMuted, fontSize = 11.sp)
                            Text(
                                "${activeWallet.currency.symbol}${String.format("%,.2f", activeWallet.totalWithdrawn)}",
                                color = AnimeCyan,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Action Buttons: Withdraw & Inter-Wallet Exchange
            Row(modifier = Modifier.fillMaxWidth()) {
                Button(
                    onClick = { showWithdrawModal = true },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("wallet_withdraw_btn")
                ) {
                    Icon(Icons.Default.Send, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("रुपये / राशि निकालें (Withdraw)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.width(10.dp))

                OutlinedButton(
                    onClick = { showExchangeModal = true },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimeCyan),
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("wallet_exchange_btn")
                ) {
                    Icon(Icons.Default.SwapHoriz, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("मुद्रा एक्सचेंज (Currency Transfer)", color = AnimeCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            if (withdrawalFeedback.isNotBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = withdrawalFeedback,
                    color = AnimeGreen,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Supported Withdrawal Gateways info card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.5f)),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "🚀 उपलब्ध ट्रांसफर व विथड्रॉल विकल्प (Withdrawal Gateways):",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TransferPill("🏦 बैंक ट्रांसफर (IMPS/NEFT)")
                        TransferPill("📱 PhonePe")
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TransferPill("🌐 Google Pay (UPI)")
                        TransferPill("💳 PayPal")
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Recent Transactions / Deposit Logs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📋 हाल की जमा व निकासी लेन-देन (Transactions):",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${transactions.size} Records",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            transactions.take(8).forEach { txn ->
                TransactionRow(txn = txn)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }

    // Modal for Withdrawal: Bank, PhonePe, GPay, PayPal
    if (showWithdrawModal) {
        WithdrawalActionDialog(
            viewModel = viewModel,
            currentCurrency = selectedCurrency,
            onSuccess = { msg ->
                withdrawalFeedback = msg
                showWithdrawModal = false
            },
            onDismiss = { showWithdrawModal = false }
        )
    }

    // Modal for Inter-Wallet Currency Exchange
    if (showExchangeModal) {
        CurrencyExchangeDialog(
            viewModel = viewModel,
            sourceCurrency = selectedCurrency,
            onSuccess = { msg ->
                withdrawalFeedback = msg
                showExchangeModal = false
            },
            onDismiss = { showExchangeModal = false }
        )
    }
}

@Composable
private fun TransferPill(title: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AnimeSurface)
            .border(1.dp, AnimeCyan.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(text = title, color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun TransactionRow(txn: WalletTransaction) {
    val isCredit = txn.type.isCredit
    val dateFormat = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    val dateStr = dateFormat.format(Date(txn.timestamp))

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCredit) AnimeGreen.copy(alpha = 0.2f) else AnimePink.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isCredit) Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                    contentDescription = null,
                    tint = if (isCredit) AnimeGreen else AnimePink,
                    modifier = Modifier.size(18.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = txn.description,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "${txn.targetAccountOrUser} • $dateStr",
                    color = TextSecondary,
                    fontSize = 10.sp
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${if (isCredit) "+" else "-"}${txn.currency.symbol}${String.format("%,.2f", txn.amount)}",
                    color = if (isCredit) AnimeGreen else AnimePink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = txn.status,
                    color = AnimeCyan,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
fun WithdrawalActionDialog(
    viewModel: AnimeViewModel,
    currentCurrency: CurrencyType,
    onSuccess: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedMethod by remember { mutableStateOf("BANK") } // "BANK", "PHONEPE", "GPAY", "PAYPAL"
    var amountInput by remember { mutableStateOf("") }

    // Bank Details
    var bankAccountNo by remember { mutableStateOf("50100482194812") }
    var ifscCode by remember { mutableStateOf("HDFC0001234") }
    var accountHolderName by remember { mutableStateOf("Aman Jangra") }

    // UPI / PayPal
    var phonePeUpi by remember { mutableStateOf("9876543210@ybl") }
    var gpayUpi by remember { mutableStateOf("amjangra0@okhdfcbank") }
    var payPalEmail by remember { mutableStateOf("amjangra0@gmail.com") }

    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AnimeGold)
                Spacer(modifier = Modifier.width(8.dp))
                Text("वॉलेट से ट्रांसफर व निकासी", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "निकासी का माध्यम चुनें (${currentCurrency.code} ${currentCurrency.symbol}):",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Methods Tabs
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    WithdrawMethodChip("🏦 बैंक", selectedMethod == "BANK") { selectedMethod = "BANK" }
                    WithdrawMethodChip("📱 PhonePe", selectedMethod == "PHONEPE") { selectedMethod = "PHONEPE" }
                    WithdrawMethodChip("🌐 GPay", selectedMethod == "GPAY") { selectedMethod = "GPAY" }
                    WithdrawMethodChip("💳 PayPal", selectedMethod == "PAYPAL") { selectedMethod = "PAYPAL" }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("निकासी राशि (${currentCurrency.symbol})") },
                    placeholder = { Text("उदा: 5000", color = TextMuted) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeGold,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("withdraw_amount_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                when (selectedMethod) {
                    "BANK" -> {
                        OutlinedTextField(
                            value = bankAccountNo,
                            onValueChange = { bankAccountNo = it },
                            label = { Text("खाता संख्या (Bank Account No)") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_bank_account_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = ifscCode,
                            onValueChange = { ifscCode = it },
                            label = { Text("IFSC कोड / SWIFT") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_ifsc_input")
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = accountHolderName,
                            onValueChange = { accountHolderName = it },
                            label = { Text("खाता धारक का नाम (Account Holder Name)") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_holder_input")
                        )
                    }
                    "PHONEPE" -> {
                        OutlinedTextField(
                            value = phonePeUpi,
                            onValueChange = { phonePeUpi = it },
                            label = { Text("PhonePe UPI ID या रजिस्टर्ड नंबर") },
                            placeholder = { Text("9876543210@ybl") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_phonepe_input")
                        )
                    }
                    "GPAY" -> {
                        OutlinedTextField(
                            value = gpayUpi,
                            onValueChange = { gpayUpi = it },
                            label = { Text("Google Pay UPI ID या रजिस्टर्ड नंबर") },
                            placeholder = { Text("user@okhdfcbank") },
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_gpay_input")
                        )
                    }
                    "PAYPAL" -> {
                        OutlinedTextField(
                            value = payPalEmail,
                            onValueChange = { payPalEmail = it },
                            label = { Text("PayPal रजिस्टर्ड ईमेल एड्रेस") },
                            placeholder = { Text("user@example.com") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth().testTag("withdraw_paypal_input")
                        )
                    }
                }

                if (errorMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = errorMsg, color = AnimePink, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountInput.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMsg = "कृपया मान्य राशि दर्ज करें"
                        return@Button
                    }
                    when (selectedMethod) {
                        "BANK" -> {
                            val res = viewModel.withdrawToBank(amt, currentCurrency, bankAccountNo, ifscCode, accountHolderName)
                            if (res.first) onSuccess(res.second) else errorMsg = res.second
                        }
                        "PHONEPE" -> {
                            val res = viewModel.withdrawToPhonePe(amt, currentCurrency, phonePeUpi)
                            if (res.first) onSuccess(res.second) else errorMsg = res.second
                        }
                        "GPAY" -> {
                            val res = viewModel.withdrawToGooglePay(amt, currentCurrency, gpayUpi)
                            if (res.first) onSuccess(res.second) else errorMsg = res.second
                        }
                        "PAYPAL" -> {
                            val res = viewModel.withdrawToPayPal(amt, currentCurrency, payPalEmail)
                            if (res.first) onSuccess(res.second) else errorMsg = res.second
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                modifier = Modifier.testTag("confirm_withdraw_submit_btn")
            ) {
                Text("ट्रांसफर पुष्टि करें", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें", color = TextMuted)
            }
        },
        containerColor = AnimeSurface
    )
}

@Composable
fun CurrencyExchangeDialog(
    viewModel: AnimeViewModel,
    sourceCurrency: CurrencyType,
    onSuccess: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var targetCurrency by remember {
        mutableStateOf(if (sourceCurrency == CurrencyType.INR) CurrencyType.USD else CurrencyType.INR)
    }
    var amountInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CurrencyExchange, contentDescription = null, tint = AnimeCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("अंतर-वॉलेट मुद्रा एक्सचेंज", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "एक वॉलेट से दूसरे वॉलेट में तुरंत राशि ट्रांसफर करें:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                Text(
                    text = "स्रोत वॉलेट: ${sourceCurrency.code} (${sourceCurrency.symbol})",
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "लक्षित वॉलेट चुनें:",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CurrencyType.values().filter { it != sourceCurrency }.forEach { c ->
                        WithdrawMethodChip("${c.symbol} ${c.code}", targetCurrency == c) {
                            targetCurrency = c
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = amountInput,
                    onValueChange = { amountInput = it },
                    label = { Text("एक्सचेंज राशि (${sourceCurrency.symbol})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("exchange_amount_input")
                )

                val amt = amountInput.toDoubleOrNull() ?: 0.0
                if (amt > 0) {
                    val inrValue = amt * sourceCurrency.exchangeToInr
                    val converted = inrValue / targetCurrency.exchangeToInr
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "प्राप्त राशि लगभग: ${targetCurrency.symbol}${String.format("%.2f", converted)}",
                        color = AnimeGreen,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (errorMsg.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMsg, color = AnimePink, fontSize = 11.sp)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val amt = amountInput.toDoubleOrNull()
                    if (amt == null || amt <= 0) {
                        errorMsg = "कृपया सही राशि दर्ज करें"
                        return@Button
                    }
                    val res = viewModel.interWalletTransfer(sourceCurrency, targetCurrency, amt)
                    if (res.first) onSuccess(res.second) else errorMsg = res.second
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                modifier = Modifier.testTag("confirm_exchange_btn")
            ) {
                Text("एक्सचेंज ट्रांसफर करें", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("रद्द करें", color = TextMuted)
            }
        },
        containerColor = AnimeSurface
    )
}

@Composable
private fun WithdrawMethodChip(
    title: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (isSelected) AnimeCyan.copy(alpha = 0.25f) else AnimeSurfaceVariant)
            .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Text(
            text = title,
            color = if (isSelected) AnimeCyan else TextSecondary,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

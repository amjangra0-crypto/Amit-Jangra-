package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CurrencyType
import com.example.data.model.PaymentGateway
import com.example.data.model.SubscriptionPlan
import com.example.ui.AnimeViewModel
import com.example.ui.components.OwnerWalletBottomSheet
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

@Composable
fun SubscriptionAdminScreen(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val adminSettings by viewModel.adminSettingsState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val scrollState = rememberScrollState()

    var promoInput by remember { mutableStateOf("") }
    var newFreeEmailInput by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(CurrencyType.INR) }
    var selectedGateway by remember { mutableStateOf(PaymentGateway.UPI_GPAY) }

    var showOwnerWalletSheet by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var checkoutPlan by remember { mutableStateOf<SubscriptionPlan?>(null) }
    var checkoutSuccessMessage by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 88.dp)
    ) {
        // App Owner Master Card (Always Free for Owner)
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimeGold)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(AnimePurple.copy(alpha = 0.25f), AnimeGold.copy(alpha = 0.15f))
                        )
                    )
                    .padding(18.dp)
            ) {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(AnimeGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "👑 ऐप ओनर वीआईपी एक्सेस (App Owner Status)",
                                color = AnimeGold,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "ओनर ईमेल: ${adminSettings?.ownerEmail ?: "amjangra0@gmail.com"}",
                                color = TextPrimary,
                                fontSize = 12.sp
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeGreen.copy(alpha = 0.2f))
                            .border(1.dp, AnimeGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "✓ आपके लिए यह ऐप आजीवन 100% फ्री है (Lifetime Free Owner License)",
                            color = AnimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------------------------------------------------------------
        // STRICT OWNER WALLET SYSTEM (ONLY VISIBLE IN OWNER'S ID!)
        // -------------------------------------------------------------------------------------
        if (currentUser.isOwner) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AnimeGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
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
                                Icon(Icons.Default.Wallet, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "💰 ओनर मल्टी-करंसी डिपॉजिट वॉलेट",
                                    color = AnimeGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "सब्सक्रिप्शन की जमा राशि केवल आपकी ID में",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimeGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text("OWNER ONLY", color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Balances preview
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val inr = wallets[CurrencyType.INR]?.balance ?: 0.0
                        val usd = wallets[CurrencyType.USD]?.balance ?: 0.0
                        val eur = wallets[CurrencyType.EUR]?.balance ?: 0.0

                        CurrencyTag("₹ INR वॉलेट", "₹${String.format("%,.0f", inr)}", AnimeGreen, Modifier.weight(1f))
                        CurrencyTag("$ USD वॉलेट", "$${String.format("%,.0f", usd)}", AnimeCyan, Modifier.weight(1f))
                        CurrencyTag("€ EUR वॉलेट", "€${String.format("%,.0f", eur)}", AnimePink, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = { showOwnerWalletSheet = true },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("subscription_open_owner_wallet_btn")
                    ) {
                        Icon(Icons.Default.AccountBalance, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "वॉलेट खोलें व बैंक/UPI/PayPal में ट्रांसफर करें",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Multi-Currency Selection for Subscription Purchase
        Text(
            text = "मुद्रा चुनें (Select Currency for Plans):",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(6.dp))

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
                        CurrencyType.GBP -> AnimeGold
                        CurrencyType.JPY -> AnimePurple
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
                            text = "${c.symbol} ${c.title}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    selectedContentColor = when (c) {
                        CurrencyType.INR -> AnimeGreen
                        CurrencyType.USD -> AnimeCyan
                        CurrencyType.EUR -> AnimePink
                        CurrencyType.GBP -> AnimeGold
                        CurrencyType.JPY -> AnimePurple
                    },
                    unselectedContentColor = TextMuted
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Subscription Plans List
        Text(
            text = "💎 सब्सक्रिप्शन योजनाएं (${selectedCurrency.code} ${selectedCurrency.symbol}):",
            color = TextPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "सब्सक्रिप्शन लेने पर राशि सीधे ओनर के सुरक्षित वॉलेट में जमा होती है",
            color = TextMuted,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        SubscriptionPlan.values().forEach { plan ->
            val priceStr = getLocalizedPrice(plan, selectedCurrency)
            PlanCardWithAction(
                plan = plan,
                priceString = priceStr,
                currency = selectedCurrency,
                onSubscribeClick = {
                    checkoutPlan = plan
                    showCheckoutDialog = true
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
        }

        if (checkoutSuccessMessage.isNotBlank()) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = checkoutSuccessMessage,
                color = AnimeGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Redeem VIP Access Code
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.5f))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.LockOpen, contentDescription = null, tint = AnimePink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "वीआईपी प्रोमो कोड दर्ज करें (Redeem Code):",
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = promoInput,
                        onValueChange = { promoInput = it },
                        placeholder = { Text("उदा: VIPFREE या ANIME2026", color = TextMuted, fontSize = 12.sp) },
                        modifier = Modifier.weight(1f).testTag("promo_code_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimePink,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (promoInput.isNotBlank()) {
                                viewModel.redeemPromoCode(promoInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("apply_promo_button")
                    ) {
                        Text("लागू करें", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (state.promoCodeMessage.isNotBlank()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = state.promoCodeMessage, color = AnimeCyan, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Admin Master Controls Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "मास्टर एडमिन नियंत्रण (Master Controls)",
                        color = AnimeGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "ग्लोबल फ्री मोड (Global Free Pass)",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = if (adminSettings?.isGlobalFreeEnabled == true) "वर्तमान: सभी यूजर्स के लिए फ्री" else "वर्तमान: केवल ऑथराइज्ड व पेड यूजर्स",
                            color = if (adminSettings?.isGlobalFreeEnabled == true) AnimeGreen else TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    Switch(
                        checked = adminSettings?.isGlobalFreeEnabled == true,
                        onCheckedChange = { viewModel.toggleGlobalFree(it) },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = AnimeGreen,
                            checkedTrackColor = AnimeGreen.copy(alpha = 0.3f)
                        ),
                        modifier = Modifier.testTag("global_free_switch")
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "किसी खास व्यक्ति को फ्री एक्सेस दें (Add Free User):",
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = newFreeEmailInput,
                        onValueChange = { newFreeEmailInput = it },
                        placeholder = { Text("friend@example.com", fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f).testTag("free_email_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (newFreeEmailInput.isNotBlank()) {
                                viewModel.addAuthorizedEmail(newFreeEmailInput)
                                newFreeEmailInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("add_free_user_button")
                    ) {
                        Icon(Icons.Default.PersonAdd, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("जोड़ें", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal: Owner Wallet Sheet
    if (showOwnerWalletSheet) {
        OwnerWalletBottomSheet(
            viewModel = viewModel,
            onDismiss = { showOwnerWalletSheet = false }
        )
    }

    // Dialog: Subscription Checkout (UPI, Card, PayPal)
    if (showCheckoutDialog && checkoutPlan != null) {
        SubscriptionCheckoutDialog(
            plan = checkoutPlan!!,
            currency = selectedCurrency,
            onPaymentComplete = { gateway, amt ->
                val emailOrPhone = currentUser.email ?: currentUser.phoneNumber ?: "user_subscriber"
                viewModel.recordSubscriptionDeposit(
                    planName = checkoutPlan!!.title,
                    amount = amt,
                    currency = selectedCurrency,
                    gateway = gateway,
                    subscriberEmailOrPhone = emailOrPhone
                )
                checkoutSuccessMessage = "✓ भुगतान सफल! ${selectedCurrency.symbol}$amt ओनर के ${selectedCurrency.code} वॉलेट में सफलतापूर्वक जमा हो गए हैं।"
                showCheckoutDialog = false
            },
            onDismiss = { showCheckoutDialog = false }
        )
    }
}

@Composable
private fun CurrencyTag(label: String, balance: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(AnimeSurfaceVariant)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp)
    ) {
        Column {
            Text(label, color = TextMuted, fontSize = 9.sp)
            Text(balance, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PlanCardWithAction(
    plan: SubscriptionPlan,
    priceString: String,
    currency: CurrencyType,
    onSubscribeClick: () -> Unit
) {
    val isOwner = plan == SubscriptionPlan.STUDIO_OWNER
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwner) AnimePurple.copy(alpha = 0.2f) else AnimeSurface
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (isOwner) AnimeGold else AnimeSurfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = plan.title,
                        color = if (isOwner) AnimeGold else TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isOwner) "ओनर के लिए आजीवन फ्री" else "UPI / कार्ड / PayPal समर्थित",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
                Text(
                    text = priceString,
                    color = AnimeCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            plan.features.forEach { feat ->
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = feat, color = TextSecondary, fontSize = 11.sp)
                }
            }

            if (plan != SubscriptionPlan.FREE) {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = onSubscribeClick,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isOwner) AnimeGold else AnimeCyan
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(40.dp)
                ) {
                    Icon(Icons.Default.Payment, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "सब्सक्राइब करें ($priceString)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

private fun getLocalizedPrice(plan: SubscriptionPlan, currency: CurrencyType): String {
    return when (plan) {
        SubscriptionPlan.FREE -> "${currency.symbol}0 / हमेशा फ्री"
        SubscriptionPlan.CREATOR_PRO -> when (currency) {
            CurrencyType.INR -> "₹499 / माह"
            CurrencyType.USD -> "$9.99 / mo"
            CurrencyType.EUR -> "€8.99 / mo"
        }
        SubscriptionPlan.STUDIO_ULTRA -> when (currency) {
            CurrencyType.INR -> "₹999 / माह"
            CurrencyType.USD -> "$19.99 / mo"
            CurrencyType.EUR -> "€18.49 / mo"
        }
        SubscriptionPlan.STUDIO_OWNER -> when (currency) {
            CurrencyType.INR -> "₹9,999 (Life VIP)"
            CurrencyType.USD -> "$149.00 (Life VIP)"
            CurrencyType.EUR -> "€139.00 (Life VIP)"
        }
    }
}

private fun getRawPlanAmount(plan: SubscriptionPlan, currency: CurrencyType): Double {
    return when (plan) {
        SubscriptionPlan.FREE -> 0.0
        SubscriptionPlan.CREATOR_PRO -> when (currency) {
            CurrencyType.INR -> 499.0
            CurrencyType.USD -> 9.99
            CurrencyType.EUR -> 8.99
        }
        SubscriptionPlan.STUDIO_ULTRA -> when (currency) {
            CurrencyType.INR -> 999.0
            CurrencyType.USD -> 19.99
            CurrencyType.EUR -> 18.49
        }
        SubscriptionPlan.STUDIO_OWNER -> when (currency) {
            CurrencyType.INR -> 9999.0
            CurrencyType.USD -> 149.00
            CurrencyType.EUR -> 139.00
        }
    }
}

@Composable
fun SubscriptionCheckoutDialog(
    plan: SubscriptionPlan,
    currency: CurrencyType,
    onPaymentComplete: (PaymentGateway, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedGateway by remember { mutableStateOf(PaymentGateway.UPI_GPAY) }
    var isProcessing by remember { mutableStateOf(false) }
    val amount = getRawPlanAmount(plan, currency)

    val supportedGateways = when (currency) {
        CurrencyType.INR -> listOf(PaymentGateway.UPI_GPAY, PaymentGateway.UPI_PHONEPE, PaymentGateway.UPI_GENERIC, PaymentGateway.CARD, PaymentGateway.BANK_TRANSFER)
        CurrencyType.USD -> listOf(PaymentGateway.PAYPAL, PaymentGateway.CARD, PaymentGateway.BANK_TRANSFER)
        CurrencyType.EUR -> listOf(PaymentGateway.PAYPAL, PaymentGateway.CARD, PaymentGateway.BANK_TRANSFER)
    }

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = AnimeCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text("सब्सक्रिप्शन पेमेंट गेटवे", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Column {
                Text(
                    text = "प्लान: ${plan.title}",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "कुल राशि: ${currency.symbol}$amount (${currency.code})",
                    color = AnimeGreen,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("भुगतान का माध्यम चुनें (Payment Method):", color = TextSecondary, fontSize = 12.sp)

                Spacer(modifier = Modifier.height(8.dp))

                supportedGateways.forEach { gateway ->
                    val isSelected = selectedGateway == gateway
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) AnimeCyan.copy(alpha = 0.2f) else AnimeSurfaceVariant)
                            .clickable { selectedGateway = gateway }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isSelected) Icons.Default.CheckCircle else Icons.Default.CreditCard,
                            contentDescription = null,
                            tint = if (isSelected) AnimeCyan else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(gateway.title, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Text(gateway.description, color = TextMuted, fontSize = 10.sp)
                        }
                    }
                }

                if (isProcessing) {
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(color = AnimeCyan, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("भुगतान संसाधित हो रहा है... ओनर वॉलेट में जमा हो रहा है", color = AnimeCyan, fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isProcessing = true
                    onPaymentComplete(selectedGateway, amount)
                },
                enabled = !isProcessing,
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan)
            ) {
                Text("भुगतान करें (${currency.symbol}$amount)", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isProcessing
            ) {
                Text("रद्द करें", color = TextMuted)
            }
        },
        containerColor = AnimeSurface
    )
}

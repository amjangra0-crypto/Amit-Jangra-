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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PriceChange
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
import androidx.compose.runtime.mutableIntStateOf
import com.example.data.model.SubscriptionSchemeDetails
import com.example.ui.AppTab
import com.example.ui.components.AddNewSubscriptionCategoryDialog
import com.example.ui.components.EditSubscriptionSchemeDialog
import com.example.ui.components.UserSubscriptionCheckoutDialog
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
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.components.OwnerPricingEditorDialog
import com.example.ui.components.OwnerWalletBarcodeCard
import com.example.ui.components.OwnerWalletBottomSheet
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun SubscriptionAdminScreen(viewModel: AnimeViewModel) {
    val state by viewModel.uiState.collectAsState()
    val adminSettings by viewModel.adminSettingsState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val wallets by viewModel.wallets.collectAsState()
    val customPrices by viewModel.customPlanPrices.collectAsState()
    val scrollState = rememberScrollState()
    val lang = state.selectedLanguage

    var promoInput by remember { mutableStateOf("") }
    var newFreeEmailInput by remember { mutableStateOf("") }
    var selectedCurrency by remember { mutableStateOf(CurrencyType.INR) }
    var selectedGateway by remember { mutableStateOf(PaymentGateway.UPI_GPAY) }

    var showOwnerWalletSheet by remember { mutableStateOf(false) }
    var showPricingEditor by remember { mutableStateOf(false) }
    var showCheckoutDialog by remember { mutableStateOf(false) }
    var checkoutPlan by remember { mutableStateOf<SubscriptionPlan?>(null) }
    var checkoutSuccessMessage by remember { mutableStateOf("") }

    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var editingScheme by remember { mutableStateOf<SubscriptionSchemeDetails?>(null) }
    var userCheckoutScheme by remember { mutableStateOf<SubscriptionSchemeDetails?>(null) }
    val allSchemes by viewModel.subscriptionSchemes.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 88.dp)
    ) {
        // TOP APP BAR WITH BACK & DONE BUTTONS
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.setTab(AppTab.STUDIO) },
                    modifier = Modifier.testTag("subscription_screen_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Column {
                    Text(
                        text = if (currentUser.isOwner) AppLocaleStrings.tr(lang, "👑 Subscription & VIP Admin", "👑 सब्सक्रिप्शन व VIP एडमिन")
                               else AppLocaleStrings.tr(lang, "💎 Studio Subscription Plans", "💎 स्टूडियो सब्सक्रिप्शन प्लान"),
                        color = MaterialTheme.colorScheme.onBackground,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (currentUser.isOwner) "Full Owner Privileges & Direct Multi-Currency Wallet"
                               else "Unlimited AI Generation • Real-Time Voice Dubbing",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }

            TextButton(
                onClick = { viewModel.setTab(AppTab.STUDIO) },
                modifier = Modifier.testTag("subscription_screen_done_btn")
            ) {
                Text(
                    text = AppLocaleStrings.tr(lang, "Done", "पूर्ण"),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // App Owner Master Card (STRICTLY FOR OWNER ONLY - COMPLETELY HIDDEN FOR REGULAR USERS)
        if (currentUser.isOwner) {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("owner_vip_access_master_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AnimeGold)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.horizontalGradient(
                                listOf(MaterialTheme.colorScheme.primary.copy(alpha = 0.25f), AnimeGold.copy(alpha = 0.15f))
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
                                    text = AppLocaleStrings.tr(lang, "👑 App Owner VIP Access", "👑 ऐप ओनर वीआईपी एक्सेस"),
                                    color = AnimeGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${AppLocaleStrings.tr(lang, "Owner Email", "ओनर ईमेल")}: ${adminSettings?.ownerEmail ?: "amjangra0@gmail.com"}",
                                    color = MaterialTheme.colorScheme.onSurface,
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
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "✓ Lifetime 100% Free Owner License for You",
                                    "✓ आपके लिए यह ऐप आजीवन 100% फ्री है (Lifetime Free Owner License)"
                                ),
                                color = AnimeGreen,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // -------------------------------------------------------------------------------------
        // STRICT OWNER WALLET SYSTEM (ONLY VISIBLE IN OWNER'S ID!)
        // -------------------------------------------------------------------------------------
        if (currentUser.isOwner) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
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
                                    text = AppLocaleStrings.tr(lang, "💰 Owner Multi-Currency Deposit Wallet", "💰 ओनर मल्टी-करंसी डिपॉजिट वॉलेट"),
                                    color = AnimeGold,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Subscription revenue directly deposits into your ID", "सब्सक्रिप्शन की जमा राशि केवल आपकी ID में"),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
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

                        CurrencyTag(AppLocaleStrings.tr(lang, "₹ INR Wallet", "₹ INR वॉलेट"), "₹${String.format("%,.0f", inr)}", AnimeGreen, Modifier.weight(1f))
                        CurrencyTag(AppLocaleStrings.tr(lang, "$ USD Wallet", "$ USD वॉलेट"), "$${String.format("%,.0f", usd)}", AnimeCyan, Modifier.weight(1f))
                        CurrencyTag(AppLocaleStrings.tr(lang, "€ EUR Wallet", "€ EUR वॉलेट"), "€${String.format("%,.0f", eur)}", AnimePink, Modifier.weight(1f))
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.setTab(com.example.ui.AppTab.OWNER_DASHBOARD) },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("subscription_open_owner_dashboard_btn")
                        ) {
                            Icon(Icons.Default.Security, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "Owner Dashboard", "ओनर डैशबोर्ड"),
                                color = Color.Black,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        OutlinedButton(
                            onClick = { showPricingEditor = true },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AnimeGold),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("subscription_edit_pricing_btn")
                        ) {
                            Icon(Icons.Default.PriceChange, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "Edit Plan Prices", "मूल्य बदलें"),
                                color = AnimeGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = { viewModel.toggleSubscriptionHistoryDialog(true) },
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.dp, AnimeCyan),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("subscription_view_history_btn")
                        ) {
                            Icon(Icons.Default.Group, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Subscriber History",
                                color = AnimeCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }

                        Button(
                            onClick = { viewModel.setTab(com.example.ui.AppTab.WALLET) },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(44.dp)
                                .testTag("subscription_open_wallet_btn")
                        ) {
                            Icon(Icons.Default.Wallet, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Open Wallet",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Multi-Currency Selection for Subscription Purchase (STRICTLY VISIBLE ONLY TO OWNER!)
        // Non-owner users select their payment currency inside the dedicated Checkout dialog!
        if (currentUser.isOwner) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppLocaleStrings.tr(lang, "👑 Owner Currency View for Schemes:", "👑 योजनाओं के लिए मुद्रा (Owner Currency View):"),
                    color = AnimeGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(
                    onClick = { showPricingEditor = true },
                    modifier = Modifier.testTag("owner_quick_pricing_edit_link")
                ) {
                    Icon(Icons.Default.PriceChange, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Edit Regional Pricing", "क्षेत्रीय मूल्य बदलें"),
                        color = AnimeGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            TabRow(
                selectedTabIndex = selectedCurrency.ordinal,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
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

            // Owner Action: Add New Subscription / VIP Category Button
            Button(
                onClick = { showAddCategoryDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("owner_add_new_category_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "+ Add New Subscription / VIP Category",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(14.dp))
        }

        // Section Title
        Text(
            text = if (currentUser.isOwner)
                "👑 Active Subscription Schemes (${selectedCurrency.code} ${selectedCurrency.symbol}):"
            else
                "💎 Choose Your Subscription Plan:",
            color = MaterialTheme.colorScheme.onSurface,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = if (currentUser.isOwner)
                "Owner can change daily generation limits, edit prices across currencies & customize features"
            else
                "Select a plan and choose your local currency at checkout (INR, USD, EUR, GBP, JPY with real-time barcode)",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Render Dynamic Schemes
        val visibleSchemes = if (currentUser.isOwner) {
            allSchemes.values.toList()
        } else {
            allSchemes.values.filter { it.planId != "owner_vip" && it.planId != "studio_owner" }
        }

        visibleSchemes.forEach { scheme ->
            val isCurrentPlan = currentUser.subscriptionPlan.equals(scheme.title, ignoreCase = true) ||
                (scheme.planId == "free" && currentUser.subscriptionPlan.contains("Free", ignoreCase = true))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("subscription_scheme_card_${scheme.planId}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (scheme.isVip) MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)
                                     else MaterialTheme.colorScheme.surface
                ),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(
                    1.dp,
                    if (scheme.isVip) AnimeGold else MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = scheme.title,
                                    color = if (scheme.isVip) AnimeGold else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (scheme.isVip) AnimeGold.copy(alpha = 0.2f) else AnimeCyan.copy(alpha = 0.2f))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = scheme.badge,
                                        color = if (scheme.isVip) AnimeGold else AnimeCyan,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))
                            // Daily Generation Limit Badge (Customizable by Owner!)
                            Text(
                                text = "🎬 Daily Generation: ${scheme.dailyCredits} videos/day",
                                color = AnimeGreen,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Pricing Display
                        if (currentUser.isOwner) {
                            val priceVal = when (selectedCurrency) {
                                CurrencyType.INR -> scheme.monthlyPriceInr
                                CurrencyType.USD -> scheme.monthlyPriceUsd
                                CurrencyType.EUR -> scheme.monthlyPriceEur
                                CurrencyType.GBP -> scheme.monthlyPriceGbp
                                CurrencyType.JPY -> scheme.monthlyPriceJpy
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = if (scheme.planId == "free") "FREE" else "${selectedCurrency.symbol}${String.format("%,.0f", priceVal)}",
                                    color = AnimeCyan,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.Black
                                )
                                Text(
                                    text = "/month",
                                    color = TextMuted,
                                    fontSize = 10.sp
                                )
                            }
                        } else {
                            if (scheme.planId == "free") {
                                Text(
                                    text = "FREE",
                                    color = AnimeGreen,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Black
                                )
                            } else {
                                Text(
                                    text = "$${String.format("%,.2f", scheme.monthlyPriceUsd)} / ₹${String.format("%,.0f", scheme.monthlyPriceInr)}",
                                    color = AnimeCyan,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Features list
                    scheme.features.forEach { feat ->
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 2.dp)) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(text = feat, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Action Buttons
                    if (currentUser.isOwner) {
                        // Owner action buttons: Edit Scheme and optional Delete Category
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { editingScheme = scheme },
                                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(38.dp)
                                    .testTag("owner_edit_scheme_${scheme.planId}")
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Edit Scheme & Limits", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            if (scheme.planId != "free" && scheme.planId != "pro" && scheme.planId != "vip") {
                                OutlinedButton(
                                    onClick = { viewModel.removeSubscriptionCategory(scheme.planId) },
                                    border = BorderStroke(1.dp, AnimePink),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .height(38.dp)
                                        .testTag("owner_delete_scheme_${scheme.planId}")
                                ) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AnimePink, modifier = Modifier.size(14.dp))
                                }
                            }
                        }
                    } else {
                        // Regular user action: Subscribe or Current Active
                        if (scheme.planId == "free") {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AnimeGreen.copy(alpha = 0.15f))
                                    .padding(vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = if (isCurrentPlan) "✓ Active Free Plan" else "Included Free",
                                    color = AnimeGreen,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        } else {
                            Button(
                                onClick = { userCheckoutScheme = scheme },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (scheme.isVip) AnimeGold else AnimeCyan
                                ),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(42.dp)
                                    .testTag("user_subscribe_btn_${scheme.planId}")
                            ) {
                                Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Choose Currency & Subscribe",
                                    color = Color.Black,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                            }
                        }
                    }
                }
            }
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

        // Promo Code Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = AppLocaleStrings.tr(lang, "Have a Promo / VIP Access Code?", "प्रोमो कोड या वीआईपी कोड है?"),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = promoInput,
                        onValueChange = { promoInput = it },
                        placeholder = { Text(AppLocaleStrings.tr(lang, "Enter code (e.g. VIPCREATOR)", "कोड दर्ज करें"), fontSize = 12.sp, color = TextMuted) },
                        modifier = Modifier.weight(1f).testTag("promo_code_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            viewModel.applyPromoCode(promoInput)
                            promoInput = ""
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("apply_promo_button")
                    ) {
                        Text(AppLocaleStrings.tr(lang, "Apply", "लागू करें"), color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Owner Master Access Switch & Free Emails Control
        if (currentUser.isOwner) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "👑 Owner Master Controls", "👑 ओनर मास्टर कंट्रोल्स"),
                        color = AnimeGold,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Global Free Access Toggle", "ग्लोबल फ्री एक्सेस टॉगल"),
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (adminSettings?.isGlobalFreeEnabled == true) {
                                    AppLocaleStrings.tr(lang, "Current: Free for all users", "वर्तमान: सभी यूजर्स के लिए फ्री")
                                } else {
                                    AppLocaleStrings.tr(lang, "Current: Paid & authorized users only", "वर्तमान: केवल ऑथराइज्ड व पेड यूजर्स")
                                },
                                color = if (adminSettings?.isGlobalFreeEnabled == true) AnimeGreen else TextMuted,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = adminSettings?.isGlobalFreeEnabled == true,
                            onCheckedChange = { viewModel.toggleGlobalFreeAccess(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = AnimeGold, checkedTrackColor = AnimeGold.copy(alpha = 0.5f))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = AppLocaleStrings.tr(lang, "Grant Free Lifetime Access to an Email:", "किसी यूजर को आजीवन फ्री एक्सेस दें:"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 12.sp,
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
                            Text(AppLocaleStrings.tr(lang, "Add", "जोड़ें"), color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Modal: Owner Wallet Sheet (STRICT OWNER ONLY)
    if (showOwnerWalletSheet) {
        if (currentUser.isOwner) {
            OwnerWalletBottomSheet(
                viewModel = viewModel,
                onDismiss = { showOwnerWalletSheet = false }
            )
        } else {
            showOwnerWalletSheet = false
        }
    }

    // Modal: Owner Pricing Editor Dialog
    if (showPricingEditor) {
        OwnerPricingEditorDialog(
            viewModel = viewModel,
            initialCurrency = selectedCurrency,
            language = lang,
            onDismiss = { showPricingEditor = false }
        )
    }

    // Dialog: Subscription Checkout (UPI, Card, PayPal)
    if (showCheckoutDialog && checkoutPlan != null) {
        SubscriptionCheckoutDialog(
            viewModel = viewModel,
            plan = checkoutPlan!!,
            currency = selectedCurrency,
            language = lang,
            onPaymentComplete = { gateway, amt ->
                val emailOrPhone = currentUser.email ?: currentUser.phoneNumber ?: "user_subscriber"
                viewModel.recordSubscriptionDeposit(
                    planName = checkoutPlan!!.title,
                    amount = amt,
                    currency = selectedCurrency,
                    gateway = gateway,
                    subscriberEmailOrPhone = emailOrPhone
                )
                checkoutSuccessMessage = AppLocaleStrings.tr(
                    lang,
                    "✓ Payment Successful! ${selectedCurrency.symbol}$amt successfully deposited into Owner's ${selectedCurrency.code} wallet.",
                    "✓ भुगतान सफल! ${selectedCurrency.symbol}$amt ओनर के ${selectedCurrency.code} वॉलेट में सफलतापूर्वक जमा हो गए हैं।"
                )
                showCheckoutDialog = false
            },
            onDismiss = { showCheckoutDialog = false }
        )
    }

    // Dialog: Owner Add New Subscription / VIP Category
    if (showAddCategoryDialog && currentUser.isOwner) {
        AddNewSubscriptionCategoryDialog(
            viewModel = viewModel,
            onDismiss = { showAddCategoryDialog = false },
            onCreated = { showAddCategoryDialog = false }
        )
    }

    // Dialog: Owner Edit Subscription Scheme Details & Daily Limits
    if (editingScheme != null && currentUser.isOwner) {
        EditSubscriptionSchemeDialog(
            initialScheme = editingScheme!!,
            viewModel = viewModel,
            onDismiss = { editingScheme = null },
            onSaved = { editingScheme = null }
        )
    }

    // Dialog: User Multi-Currency Subscription Checkout with Dedicated Vector Barcode
    if (userCheckoutScheme != null) {
        UserSubscriptionCheckoutDialog(
            scheme = userCheckoutScheme!!,
            viewModel = viewModel,
            lang = lang,
            onDismiss = { userCheckoutScheme = null },
            onSuccess = { userCheckoutScheme = null }
        )
    }
}

@Composable
private fun CurrencyTag(label: String, balance: String, color: Color, modifier: Modifier) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
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
    language: String,
    onSubscribeClick: () -> Unit
) {
    val isOwner = plan == SubscriptionPlan.STUDIO_OWNER
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isOwner) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(
            1.dp,
            if (isOwner) AnimeGold else MaterialTheme.colorScheme.surfaceVariant
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
                        color = if (isOwner) AnimeGold else MaterialTheme.colorScheme.onSurface,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (isOwner) {
                            AppLocaleStrings.tr(language, "Lifetime free for Owner", "ओनर के लिए आजीवन फ्री")
                        } else {
                            AppLocaleStrings.tr(language, "UPI / Card / PayPal Supported", "UPI / कार्ड / PayPal समर्थित")
                        },
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
                    Text(text = feat, color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp)
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
                        text = AppLocaleStrings.tr(
                            language,
                            "Subscribe ($priceString)",
                            "सब्सक्राइब करें ($priceString)"
                        ),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Composable
fun SubscriptionCheckoutDialog(
    viewModel: AnimeViewModel,
    plan: SubscriptionPlan,
    currency: CurrencyType,
    language: String,
    onPaymentComplete: (PaymentGateway, Double) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedGateway by remember { mutableStateOf(PaymentGateway.OWNER_WALLET_BARCODE) }
    var isProcessing by remember { mutableStateOf(false) }
    var checkoutTab by remember { mutableIntStateOf(0) } // 0: Barcode & Wallet ID, 1: All Payment Methods
    val amount = viewModel.getPlanPrice(plan, currency)

    val supportedGateways = when (currency) {
        CurrencyType.INR -> listOf(
            PaymentGateway.OWNER_WALLET_BARCODE,
            PaymentGateway.OWNER_WALLET_ID,
            PaymentGateway.UPI_GPAY,
            PaymentGateway.UPI_PHONEPE,
            PaymentGateway.PAYTM,
            PaymentGateway.UPI_GENERIC,
            PaymentGateway.CARD,
            PaymentGateway.BANK_TRANSFER
        )
        CurrencyType.USD, CurrencyType.EUR, CurrencyType.GBP, CurrencyType.JPY -> listOf(
            PaymentGateway.OWNER_WALLET_BARCODE,
            PaymentGateway.OWNER_WALLET_ID,
            PaymentGateway.PAYPAL,
            PaymentGateway.CARD,
            PaymentGateway.BANK_TRANSFER
        )
    }

    AlertDialog(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Payment, contentDescription = null, tint = AnimeCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = AppLocaleStrings.tr(language, "Subscription Checkout", "सब्सक्रिप्शन भुगतान (Owner Wallet)"),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Amount deposits directly into Owner's in-app Wallet",
                        color = AnimeGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                // Plan Summary Badge
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
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
                                text = plan.title,
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Access: Full Pro AI Generation",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                        Text(
                            text = "${currency.symbol}$amount (${currency.code})",
                            color = AnimeGreen,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Selector Tabs: 0: Owner Wallet Barcode & ID, 1: Payment Gateways
                TabRow(
                    selectedTabIndex = checkoutTab,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = AnimeCyan,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .height(38.dp)
                ) {
                    Tab(
                        selected = checkoutTab == 0,
                        onClick = {
                            checkoutTab = 0
                            selectedGateway = PaymentGateway.OWNER_WALLET_BARCODE
                        },
                        text = {
                            Text(
                                AppLocaleStrings.tr(language, "📱 Barcode & ID", "📱 बारकोड व ID"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                    Tab(
                        selected = checkoutTab == 1,
                        onClick = { checkoutTab = 1 },
                        text = {
                            Text(
                                AppLocaleStrings.tr(language, "⚡ Payment Apps", "⚡ भुगतान माध्यम"),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                if (checkoutTab == 0) {
                    // Visual Owner Wallet Barcode, 1D Barcode, and Owner Wallet ID Card
                    OwnerWalletBarcodeCard(
                        amount = amount,
                        currency = currency,
                        language = language,
                        isOwnerSelfView = false
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = AppLocaleStrings.tr(
                            language,
                            "User Instruction: You can scan this Barcode or use the Owner Wallet ID above. Once you confirm, the subscription amount will be deposited directly into this app's Owner Wallet.",
                            "यूजर निर्देश: आप ऊपर दिए गए बारकोड को स्कैन कर सकते हैं या ओनर वॉलेट ID कॉपी करके भुगतान कर सकते हैं। पुष्टि होते ही राशि सीधे इसी ऐप के ओनर वॉलेट में जमा हो जाएगी।"
                        ),
                        color = TextSecondary,
                        fontSize = 10.sp,
                        lineHeight = 14.sp
                    )
                } else {
                    // Gateway List
                    Text(
                        text = AppLocaleStrings.tr(language, "Choose Payment Channel:", "भुगतान का माध्यम चुनें:"),
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    supportedGateways.forEach { gateway ->
                        val isSelected = selectedGateway == gateway
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) AnimeCyan.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                                .border(1.dp, if (isSelected) AnimeCyan else Color.Transparent, RoundedCornerShape(8.dp))
                                .clickable { selectedGateway = gateway }
                                .padding(horizontal = 10.dp, vertical = 7.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(gateway.iconEmoji, fontSize = 16.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = gateway.displayName,
                                    color = if (isSelected) AnimeCyan else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 12.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                )
                                Text(
                                    text = gateway.description,
                                    color = TextMuted,
                                    fontSize = 9.sp
                                )
                            }
                            if (isSelected) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeGold.copy(alpha = 0.15f))
                        .padding(8.dp)
                ) {
                    Text(
                        text = AppLocaleStrings.tr(
                            language,
                            "🛡️ Guaranteed Deposit: This payment will be deposited into the Owner's in-app Wallet. The Owner can withdraw anytime via RTGS, NEFT, SWIFT, UPI, Paytm, or PayPal.",
                            "🛡️ गारंटीड डिपॉजिट: यह भुगतान इसी ऐप के ओनर वॉलेट में जमा होगा, जिसे ओनर कभी भी RTGS, NEFT, SWIFT, UPI, Paytm या PayPal से निकाल सकते हैं।"
                        ),
                        color = AnimeGold,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
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
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("confirm_subscription_payment_btn")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                } else {
                    Text(
                        text = AppLocaleStrings.tr(
                            language,
                            "Confirm & Deposit ${currency.symbol}$amount",
                            "भुगतान कर वॉलेट में जमा करें (${currency.symbol}$amount)"
                        ),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isProcessing) {
                Text(AppLocaleStrings.tr(language, "Cancel", "रद्द करें"), color = TextMuted)
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    )
}

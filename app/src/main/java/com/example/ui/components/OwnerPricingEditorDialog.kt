package com.example.ui.components

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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PriceChange
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.CurrencyType
import com.example.data.model.SubscriptionPlan
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

/**
 * Dialog allowing App Owner to customize subscription plan prices
 * for each country/currency region (INR, USD, EUR, GBP, JPY).
 */
@Composable
fun OwnerPricingEditorDialog(
    viewModel: AnimeViewModel,
    initialCurrency: CurrencyType = CurrencyType.INR,
    language: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val customPrices by viewModel.customPlanPrices.collectAsState()
    var selectedCurrency by remember { mutableStateOf(initialCurrency) }

    val currencies = CurrencyType.values()
    val selectedTabIndex = currencies.indexOf(selectedCurrency).coerceAtLeast(0)

    val proKey = "${SubscriptionPlan.CREATOR_PRO.planId}_${selectedCurrency.name}"
    val ultraKey = "${SubscriptionPlan.STUDIO_ULTRA.planId}_${selectedCurrency.name}"
    val ownerKey = "${SubscriptionPlan.STUDIO_OWNER.planId}_${selectedCurrency.name}"

    var proPriceInput by remember(selectedCurrency, customPrices) {
        mutableStateOf((customPrices[proKey] ?: viewModel.getPlanPrice(SubscriptionPlan.CREATOR_PRO, selectedCurrency)).toString())
    }
    var ultraPriceInput by remember(selectedCurrency, customPrices) {
        mutableStateOf((customPrices[ultraKey] ?: viewModel.getPlanPrice(SubscriptionPlan.STUDIO_ULTRA, selectedCurrency)).toString())
    }
    var ownerPriceInput by remember(selectedCurrency, customPrices) {
        mutableStateOf((customPrices[ownerKey] ?: viewModel.getPlanPrice(SubscriptionPlan.STUDIO_OWNER, selectedCurrency)).toString())
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("owner_pricing_editor_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(
                1.5.dp,
                Brush.horizontalGradient(listOf(AnimeGold, AnimeCyan, AnimePurple))
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
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
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AnimeGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.PriceChange, contentDescription = null, tint = Color.Black, modifier = Modifier.size(22.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(language, "Regional Pricing Manager", "क्षेत्रीय मूल्य प्रबंधक"),
                                color = AnimeGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = AppLocaleStrings.tr(language, "Owner Control: Set plan prices by country & currency", "ओनर कंट्रोल: देश व मुद्रा अनुसार प्लान मूल्य निर्धारित करें"),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Currency Tabs
                Text(
                    text = AppLocaleStrings.tr(language, "Select Region / Currency:", "क्षेत्र / मुद्रा चुनें:"),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(6.dp))

                ScrollableTabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
                    contentColor = AnimeGold,
                    edgePadding = 4.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = AnimeGold
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(12.dp))
                ) {
                    currencies.forEachIndexed { index, curr ->
                        val isSelected = curr == selectedCurrency
                        Tab(
                            selected = isSelected,
                            onClick = { selectedCurrency = curr },
                            text = {
                                Text(
                                    text = "${curr.symbol} ${curr.name}",
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            },
                            selectedContentColor = AnimeGold,
                            unselectedContentColor = TextMuted
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Plan 1: Free Explorer (Always Free)
                PricingPlanRow(
                    title = "Free Explorer",
                    symbol = selectedCurrency.symbol,
                    amountText = "0",
                    isEditable = false,
                    onAmountChange = {},
                    subtitle = AppLocaleStrings.tr(language, "Always free for all creators", "सभी रचनाकारों के लिए हमेशा निःशुल्क"),
                    accentColor = AnimeGreen
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Plan 2: Creator Pro
                PricingPlanRow(
                    title = "Creator Pro",
                    symbol = selectedCurrency.symbol,
                    amountText = proPriceInput,
                    isEditable = true,
                    onAmountChange = { proPriceInput = it },
                    subtitle = AppLocaleStrings.tr(language, "Monthly plan for active creators", "मासिक प्लान सक्रिय रचनाकारों के लिए"),
                    accentColor = AnimeCyan
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Plan 3: Studio Ultra
                PricingPlanRow(
                    title = "Studio Ultra",
                    symbol = selectedCurrency.symbol,
                    amountText = ultraPriceInput,
                    isEditable = true,
                    onAmountChange = { ultraPriceInput = it },
                    subtitle = AppLocaleStrings.tr(language, "Full anime episodes & 4K UHD render", "पूर्ण एनिमे एपिसोड व 4K UHD रेंडर"),
                    accentColor = AnimePurple
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Plan 4: Studio Owner / VIP Lifetime
                PricingPlanRow(
                    title = "Studio VIP Lifetime",
                    symbol = selectedCurrency.symbol,
                    amountText = ownerPriceInput,
                    isEditable = true,
                    onAmountChange = { ownerPriceInput = it },
                    subtitle = AppLocaleStrings.tr(language, "One-time permanent master license", "एकमुश्त स्थायी मास्टर लाइसेंस"),
                    accentColor = AnimeGold
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Save Action Button
                Button(
                    onClick = {
                        val proVal = proPriceInput.toDoubleOrNull()
                        val ultraVal = ultraPriceInput.toDoubleOrNull()
                        val ownerVal = ownerPriceInput.toDoubleOrNull()

                        if (proVal != null && proVal >= 0) {
                            viewModel.updatePlanPrice(SubscriptionPlan.CREATOR_PRO.planId, selectedCurrency.name, proVal)
                        }
                        if (ultraVal != null && ultraVal >= 0) {
                            viewModel.updatePlanPrice(SubscriptionPlan.STUDIO_ULTRA.planId, selectedCurrency.name, ultraVal)
                        }
                        if (ownerVal != null && ownerVal >= 0) {
                            viewModel.updatePlanPrice(SubscriptionPlan.STUDIO_OWNER.planId, selectedCurrency.name, ownerVal)
                        }

                        Toast.makeText(
                            context,
                            AppLocaleStrings.tr(
                                language,
                                "✓ Prices updated for ${selectedCurrency.name} (${selectedCurrency.symbol})",
                                "✓ ${selectedCurrency.name} के लिए मूल्य अपडेट कर दिए गए हैं!"
                            ),
                            Toast.LENGTH_SHORT
                        ).show()
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_regional_pricing_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            language,
                            "Save Prices for ${selectedCurrency.name}",
                            "${selectedCurrency.name} के मूल्य सेव करें"
                        ),
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun PricingPlanRow(
    title: String,
    symbol: String,
    amountText: String,
    isEditable: Boolean,
    onAmountChange: (String) -> Unit,
    subtitle: String,
    accentColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = title, color = accentColor, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Text(text = subtitle, color = TextSecondary, fontSize = 10.sp, lineHeight = 13.sp)
            }

            Spacer(modifier = Modifier.width(12.dp))

            if (isEditable) {
                OutlinedTextField(
                    value = amountText,
                    onValueChange = { input ->
                        // Allow only valid numbers and dot
                        if (input.isEmpty() || input.matches(Regex("""^\d*\.?\d*$"""))) {
                            onAmountChange(input)
                        }
                    },
                    leadingIcon = {
                        Text(text = symbol, color = accentColor, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.width(115.dp).height(50.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = accentColor,
                        unfocusedBorderColor = accentColor.copy(alpha = 0.4f),
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.2f))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = "$symbol 0 (FREE)",
                        color = accentColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

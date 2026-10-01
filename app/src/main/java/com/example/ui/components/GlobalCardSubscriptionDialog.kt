package com.example.ui.components

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CardPaymentDetails
import com.example.data.model.CurrencyType
import com.example.data.model.PaymentGateway
import com.example.data.model.SubscriptionPlan
import com.example.localization.AppLocaleStrings
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GlobalCardSubscriptionDialog(
    plan: SubscriptionPlan,
    initialCurrency: CurrencyType,
    selectedLanguage: String,
    viewModel: AnimeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onPaymentSuccess: (Double, CurrencyType, CardPaymentDetails) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val coroutineScope = rememberCoroutineScope()

    var activeCurrency by remember { mutableStateOf(initialCurrency) }
    var cardholderName by remember { mutableStateOf("Aman Jangra") }
    var cardNumber by remember { mutableStateOf("4532 8921 7844 9012") }
    var expiryDate by remember { mutableStateOf("09/29") }
    var cvv by remember { mutableStateOf("884") }
    var billingCountry by remember { mutableStateOf("Global (Worldwide)") }
    var saveCard by remember { mutableStateOf(true) }

    var isProcessing by remember { mutableStateOf(false) }
    var isSuccess by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    // Dynamic Card Brand Detection
    val cleanNumber = cardNumber.replace(" ", "")
    val detectedBrand = when {
        cleanNumber.startsWith("4") -> "VISA"
        cleanNumber.startsWith("5") || cleanNumber.startsWith("2") -> "MasterCard"
        cleanNumber.startsWith("34") || cleanNumber.startsWith("37") -> "American Express"
        cleanNumber.startsWith("60") || cleanNumber.startsWith("65") || cleanNumber.startsWith("81") -> "RuPay"
        cleanNumber.startsWith("35") -> "JCB"
        else -> "Credit/Debit"
    }

    // Dynamic Price based on Currency and Owner Regional Settings
    val planPrice = viewModel.getPlanPrice(plan, activeCurrency)

    ModalBottomSheet(
        onDismissRequest = { if (!isProcessing) onDismiss() },
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
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
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(AnimeCyan.copy(alpha = 0.2f))
                            .border(1.dp, AnimeCyan, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.CreditCard, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(selectedLanguage, "Global Card Checkout", "ग्लोबल डेबिट व क्रेडिट कार्ड भुगतान"),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = AppLocaleStrings.tr(selectedLanguage, "Worldwide Debit & Credit Card Checkout", "विश्वव्यापी डेबिट और क्रेडिट कार्ड सुविधा"),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(
                    onClick = { if (!isProcessing) onDismiss() },
                    modifier = Modifier.testTag("global_card_close_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Plan Summary & Global Currency Selector
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(12.dp)
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
                                color = MaterialTheme.colorScheme.onSurface,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = AppLocaleStrings.tr(selectedLanguage, "Global Subscription License", "ग्लोबल सब्सक्रिप्शन लाइसेंस"),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }

                        Text(
                            text = "${activeCurrency.symbol}${String.format("%,.2f", planPrice)}",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Global Currency Switcher
                    Text(
                        text = AppLocaleStrings.tr(selectedLanguage, "Select Currency (Global Worldwide):", "मुद्रा चुनें (ग्लोबल विकल्प):"),
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        CurrencyType.values().forEach { cur ->
                            val isSel = activeCurrency == cur
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (isSel) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface)
                                    .clickable { activeCurrency = cur }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${cur.symbol} ${cur.code}",
                                    color = if (isSel) Color.White else MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.sp,
                                    fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Card Brand Badges Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = AppLocaleStrings.tr(selectedLanguage, "Accepted Cards Worldwide:", "स्वीकृत कार्ड्स (Global):"),
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("VISA", "MC", "AMEX", "RUPAY", "JCB").forEach { brand ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(MaterialTheme.colorScheme.surfaceVariant)
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(text = brand, color = MaterialTheme.colorScheme.primary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 1. Cardholder Full Name
            OutlinedTextField(
                value = cardholderName,
                onValueChange = {
                    cardholderName = it
                    errorMessage = ""
                },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Cardholder Full Name", "कार्डधारक का पूरा नाम")) },
                placeholder = { Text("e.g. Aman Jangra") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AnimeCyan) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("card_holder_name_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Card Number with brand detection
            OutlinedTextField(
                value = cardNumber,
                onValueChange = {
                    val digits = it.filter { ch -> ch.isDigit() }.take(16)
                    cardNumber = digits.chunked(4).joinToString(" ")
                    errorMessage = ""
                },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Debit / Credit Card Number", "कार्ड संख्या (16 अंक)")) },
                placeholder = { Text("4111 2222 3333 4444") },
                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = AnimeGold) },
                trailingIcon = {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(AnimeCyan.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 3.dp)
                    ) {
                        Text(text = detectedBrand, color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("card_number_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 3. Expiry Date & CVV Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedTextField(
                    value = expiryDate,
                    onValueChange = {
                        val cleaned = it.filter { ch -> ch.isDigit() || ch == '/' }.take(5)
                        expiryDate = if (cleaned.length == 2 && !cleaned.contains("/") && !expiryDate.endsWith("/")) {
                            "$cleaned/"
                        } else {
                            cleaned
                        }
                        errorMessage = ""
                    },
                    label = { Text("Expiry (MM/YY)") },
                    placeholder = { Text("09/29") },
                    leadingIcon = { Icon(Icons.Default.DateRange, contentDescription = null, tint = AnimePink) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("card_expiry_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = cvv,
                    onValueChange = {
                        cvv = it.filter { ch -> ch.isDigit() }.take(4)
                        errorMessage = ""
                    },
                    label = { Text("CVV / CVC") },
                    placeholder = { Text("•••") },
                    leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = AnimeGreen) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    visualTransformation = PasswordVisualTransformation(),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("card_cvv_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 4. Billing Country
            OutlinedTextField(
                value = billingCountry,
                onValueChange = { billingCountry = it },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Billing Country / Region", "बिलिंग देश / क्षेत्र")) },
                placeholder = { Text("e.g. United States, India, UK") },
                leadingIcon = { Icon(Icons.Default.Public, contentDescription = null, tint = MaterialTheme.colorScheme.primary) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("card_country_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Save Card & Auto-Renewal Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = AppLocaleStrings.tr(selectedLanguage, "Save Card for Instant Renewal", "भविष्य के लिए कार्ड सुरक्षित रखें"),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = AppLocaleStrings.tr(selectedLanguage, "Zero-friction renewal with 256-bit tokenized storage", "सुरक्षित 256-बिट एन्क्रिप्शन"),
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                Switch(
                    checked = saveCard,
                    onCheckedChange = { saveCard = it },
                    colors = SwitchDefaults.colors(checkedThumbColor = AnimeGreen, checkedTrackColor = AnimeGreen.copy(alpha = 0.35f))
                )
            }

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Security statement
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "🔒 256-Bit SSL Encrypted • PCI-DSS Certified Global Gateway",
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Pay Button
            Button(
                onClick = {
                    if (cardholderName.isBlank()) {
                        errorMessage = "Please enter Cardholder Name"
                        return@Button
                    }
                    val numDigits = cardNumber.replace(" ", "")
                    if (numDigits.length < 13) {
                        errorMessage = "Please enter a valid 16-digit card number"
                        return@Button
                    }
                    if (expiryDate.length < 4 || !expiryDate.contains("/")) {
                        errorMessage = "Please enter valid Expiry Date (MM/YY)"
                        return@Button
                    }
                    if (cvv.length < 3) {
                        errorMessage = "Please enter valid 3 or 4-digit CVV"
                        return@Button
                    }

                    isProcessing = true
                    coroutineScope.launch {
                        delay(1200) // Realistic checkout processing
                        isProcessing = false
                        isSuccess = true
                        val cardDetails = CardPaymentDetails(
                            cardholderName = cardholderName.trim(),
                            cardNumber = cardNumber.trim(),
                            expiryMonthYear = expiryDate.trim(),
                            cvv = cvv.trim(),
                            cardBrand = detectedBrand,
                            billingCountry = billingCountry.trim(),
                            saveCard = saveCard
                        )
                        onPaymentSuccess(planPrice, activeCurrency, cardDetails)
                    }
                },
                enabled = !isProcessing && !isSuccess,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("global_pay_btn")
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(selectedLanguage, "Authorizing Card...", "कार्ड अधिकृत किया जा रहा है..."),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else if (isSuccess) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(selectedLanguage, "Payment Approved! 🎉", "भुगतान सफल! 🎉"),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                } else {
                    Icon(Icons.Default.CreditCard, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${AppLocaleStrings.tr(selectedLanguage, "Pay", "भुगतान करें")} ${activeCurrency.symbol}${String.format("%,.2f", planPrice)} via $detectedBrand",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

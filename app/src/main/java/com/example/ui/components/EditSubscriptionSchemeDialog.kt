package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SubscriptionSchemeDetails
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

/**
 * Dialog allowing App Owner to edit & modify subscription scheme details:
 * Title, Badge, Pricing (INR & USD), Daily Credits, Features List & Active status.
 */
@Composable
fun EditSubscriptionSchemeDialog(
    initialScheme: SubscriptionSchemeDetails,
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit,
    onSaved: (SubscriptionSchemeDetails) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf(initialScheme.title) }
    var badge by remember { mutableStateOf(initialScheme.badge) }
    var monthlyPriceInr by remember { mutableStateOf(initialScheme.monthlyPriceInr.toString()) }
    var yearlyPriceInr by remember { mutableStateOf(initialScheme.yearlyPriceInr.toString()) }
    var monthlyPriceUsd by remember { mutableStateOf(initialScheme.monthlyPriceUsd.toString()) }
    var yearlyPriceUsd by remember { mutableStateOf(initialScheme.yearlyPriceUsd.toString()) }
    var dailyCredits by remember { mutableStateOf(initialScheme.dailyCredits.toString()) }
    var isActive by remember { mutableStateOf(initialScheme.isActive) }
    var discountPercent by remember { mutableIntStateOf(initialScheme.discountPercent) }

    val featuresList = remember { mutableStateListOf<String>().apply { addAll(initialScheme.features) } }
    var newFeatureInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 20.dp)
                .testTag("edit_subscription_scheme_dialog"),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePurple, AnimeGold)))
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
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(24.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "💎 Edit Subscription Scheme",
                                color = AnimeGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Plan ID: ${initialScheme.planId}",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Active Plan Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AnimeSurfaceVariant, RoundedCornerShape(10.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Active & Available for Users:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                        Text(if (isActive) "Visible in Pricing & Checkout" else "Hidden / Paused", color = if (isActive) AnimeGreen else TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeGreen, checkedTrackColor = AnimeGreen.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Plan Title
                Text("Scheme Name / Plan Title:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    placeholder = { Text("e.g. Creator Pro Ultra") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("scheme_title_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Badge / Subtitle
                Text("Scheme Badge / Tagline:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = badge,
                    onValueChange = { badge = it },
                    placeholder = { Text("e.g. 🔥 Most Popular / Unlimited 4K") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("scheme_badge_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeGold,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Pricing Row (INR)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Monthly (INR ₹):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = monthlyPriceInr,
                            onValueChange = { monthlyPriceInr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("price_monthly_inr"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Yearly (INR ₹):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = yearlyPriceInr,
                            onValueChange = { yearlyPriceInr = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("price_yearly_inr"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Pricing Row (USD)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Monthly (USD $):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = monthlyPriceUsd,
                            onValueChange = { monthlyPriceUsd = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("price_monthly_usd"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Text("Yearly (USD $):", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        OutlinedTextField(
                            value = yearlyPriceUsd,
                            onValueChange = { yearlyPriceUsd = it },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("price_yearly_usd"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Daily Credits Allowance
                Text("Daily AI Credits Allowance:", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(4.dp))
                OutlinedTextField(
                    value = dailyCredits,
                    onValueChange = { dailyCredits = it },
                    placeholder = { Text("e.g. 50 or 999999 for unlimited") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().testTag("daily_credits_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Promotional Discount Slider
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Promotional Discount:", color = TextSecondary, fontSize = 12.sp)
                    Text("$discountPercent% OFF", color = AnimePink, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Slider(
                    value = discountPercent.toFloat(),
                    onValueChange = { discountPercent = it.toInt() },
                    valueRange = 0f..80f,
                    colors = SliderDefaults.colors(thumbColor = AnimePink, activeTrackColor = AnimePink)
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Features / Benefits List
                Text("Scheme Features & Benefits:", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    featuresList.forEachIndexed { index, feat ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(AnimeSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(horizontal = 10.dp, vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(feat, color = TextPrimary, fontSize = 12.sp)
                            }
                            IconButton(
                                onClick = { featuresList.removeAt(index) },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Remove", tint = AnimePink, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Add Feature Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newFeatureInput,
                        onValueChange = { newFeatureInput = it },
                        placeholder = { Text("Add new feature...", fontSize = 12.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        shape = RoundedCornerShape(8.dp)
                    )
                    Button(
                        onClick = {
                            if (newFeatureInput.isNotBlank()) {
                                featuresList.add(newFeatureInput.trim())
                                newFeatureInput = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    TextButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Cancel", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val inrM = monthlyPriceInr.toDoubleOrNull() ?: initialScheme.monthlyPriceInr
                            val inrY = yearlyPriceInr.toDoubleOrNull() ?: initialScheme.yearlyPriceInr
                            val usdM = monthlyPriceUsd.toDoubleOrNull() ?: initialScheme.monthlyPriceUsd
                            val usdY = yearlyPriceUsd.toDoubleOrNull() ?: initialScheme.yearlyPriceUsd
                            val credits = dailyCredits.toIntOrNull() ?: initialScheme.dailyCredits

                            val updated = initialScheme.copy(
                                title = title.ifBlank { initialScheme.title },
                                badge = badge.ifBlank { initialScheme.badge },
                                monthlyPriceInr = inrM,
                                yearlyPriceInr = inrY,
                                monthlyPriceUsd = usdM,
                                yearlyPriceUsd = usdY,
                                dailyCredits = credits,
                                features = featuresList.toList(),
                                isActive = isActive,
                                discountPercent = discountPercent
                            )

                            viewModel.updateSubscriptionScheme(updated)
                            Toast.makeText(context, "💎 Scheme '${updated.title}' updated successfully!", Toast.LENGTH_SHORT).show()
                            onSaved(updated)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1.5f).testTag("save_scheme_details_btn")
                    ) {
                        Icon(Icons.Default.Check, contentDescription = null, tint = MaterialTheme.colorScheme.surface, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Save Scheme Details", color = MaterialTheme.colorScheme.surface, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                }
            }
        }
    }
}

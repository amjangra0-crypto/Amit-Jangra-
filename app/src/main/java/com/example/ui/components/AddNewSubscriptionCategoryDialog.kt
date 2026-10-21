package com.example.ui.components

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
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
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.util.UUID

/**
 * AddNewSubscriptionCategoryDialog
 * Strictly for App Owner to create new subscription or VIP categories,
 * set daily video limits, configure multi-currency prices, and customize features.
 */
@Composable
fun AddNewSubscriptionCategoryDialog(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit,
    onCreated: (SubscriptionSchemeDetails) -> Unit
) {
    val context = LocalContext.current
    var title by remember { mutableStateOf("") }
    var badge by remember { mutableStateOf("New Tier") }
    var isVip by remember { mutableStateOf(false) }
    var dailyCredits by remember { mutableStateOf("25") }
    var priceInr by remember { mutableStateOf("299") }
    var priceUsd by remember { mutableStateOf("4.99") }
    var priceEur by remember { mutableStateOf("4.99") }
    var priceGbp by remember { mutableStateOf("3.99") }
    var priceJpy by remember { mutableStateOf("680") }

    val features = remember {
        mutableStateListOf(
            "AI Anime Generation Access",
            "Full HD 1080p Video Rendering",
            "Multi-language Audio Dubbing"
        )
    }
    var newFeatureInput by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp)
                .testTag("add_new_subscription_category_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeGold, AnimePurple, AnimeCyan)))
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Header with Close (×)
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
                                .background(AnimeGold.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "👑 Add New Plan / VIP Category",
                                color = AnimeGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "App Owner Plan & Category Creator",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = MaterialTheme.colorScheme.onSurface, modifier = Modifier.size(16.dp))
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Title & Badge
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Category / Plan Title (e.g. Student Creator, Enterprise VIP)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeGold)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = badge,
                        onValueChange = { badge = it },
                        label = { Text("Badge Label") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = dailyCredits,
                        onValueChange = { dailyCredits = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Daily Videos Limit") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // VIP Category Toggle
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AnimeSurfaceVariant)
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Category Type: ${if (isVip) "💎 VIP Category" else "🌟 Standard Subscription"}", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = if (isVip) AnimePurple else AnimeCyan)
                        Text(if (isVip) "Visible in VIP Lounge and VIP Plan tiers" else "Visible in regular user subscription list", fontSize = 10.sp, color = TextMuted)
                    }
                    Switch(
                        checked = isVip,
                        onCheckedChange = { isVip = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeGold, checkedTrackColor = AnimeGold.copy(alpha = 0.4f))
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-Currency Pricing
                Text("Regional Pricing Setup:", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceInr,
                        onValueChange = { priceInr = it },
                        label = { Text("₹ INR") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = priceUsd,
                        onValueChange = { priceUsd = it },
                        label = { Text("$ USD") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = priceEur,
                        onValueChange = { priceEur = it },
                        label = { Text("€ EUR") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = priceGbp,
                        onValueChange = { priceGbp = it },
                        label = { Text("£ GBP") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = priceJpy,
                        onValueChange = { priceJpy = it },
                        label = { Text("¥ JPY") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Features list
                Text("Plan Features (${features.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(modifier = Modifier.height(6.dp))

                features.forEachIndexed { index, feat ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("• $feat", modifier = Modifier.weight(1f), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        IconButton(
                            onClick = { features.removeAt(index) },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AnimePink, modifier = Modifier.size(16.dp))
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = newFeatureInput,
                        onValueChange = { newFeatureInput = it },
                        placeholder = { Text("Add feature bullet point...", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    IconButton(
                        onClick = {
                            if (newFeatureInput.isNotBlank()) {
                                features.add(newFeatureInput.trim())
                                newFeatureInput = ""
                            }
                        },
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimeGreen)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Feature", tint = Color.Black)
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Buttons: Create and Cancel
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Button(
                        onClick = onDismiss,
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onSurface)
                    }

                    Button(
                        onClick = {
                            if (title.isBlank()) {
                                Toast.makeText(context, "Please enter a plan title", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            val cleanPlanId = "plan_custom_${UUID.randomUUID().toString().take(8)}"
                            val newScheme = SubscriptionSchemeDetails(
                                planId = cleanPlanId,
                                title = title.trim(),
                                badge = badge.trim().ifBlank { "Creator Tier" },
                                monthlyPriceInr = priceInr.toDoubleOrNull() ?: 299.0,
                                yearlyPriceInr = (priceInr.toDoubleOrNull() ?: 299.0) * 10,
                                monthlyPriceUsd = priceUsd.toDoubleOrNull() ?: 4.99,
                                yearlyPriceUsd = (priceUsd.toDoubleOrNull() ?: 4.99) * 10,
                                monthlyPriceEur = priceEur.toDoubleOrNull() ?: 4.99,
                                monthlyPriceGbp = priceGbp.toDoubleOrNull() ?: 3.99,
                                monthlyPriceJpy = priceJpy.toDoubleOrNull() ?: 680.0,
                                dailyCredits = dailyCredits.toIntOrNull() ?: 25,
                                features = features.toList().ifEmpty { listOf("AI Anime Generation Access") },
                                isActive = true,
                                isVip = isVip
                            )
                            onCreated(newScheme)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1.5f)
                            .height(46.dp)
                            .testTag("save_new_subscription_category_btn")
                    ) {
                        Text("Save & Add Category", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

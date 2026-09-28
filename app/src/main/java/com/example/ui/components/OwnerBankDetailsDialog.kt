package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OwnerBankAccount
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun OwnerBankDetailsDialog(
    currentAccount: OwnerBankAccount,
    selectedLanguage: String,
    onSave: (OwnerBankAccount) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val context = LocalContext.current

    var holderName by remember { mutableStateOf(currentAccount.holderName) }
    var bankName by remember { mutableStateOf(currentAccount.bankName) }
    var accountNumber by remember { mutableStateOf(currentAccount.accountNumber) }
    var confirmAccountNumber by remember { mutableStateOf(currentAccount.accountNumber) }
    var ifscCode by remember { mutableStateOf(currentAccount.ifscCode) }
    var swiftBic by remember { mutableStateOf(currentAccount.swiftBic) }
    var branchName by remember { mutableStateOf(currentAccount.branchName) }
    var accountType by remember { mutableStateOf(currentAccount.accountType) }
    var upiId by remember { mutableStateOf(currentAccount.upiId) }

    var errorMessage by remember { mutableStateOf("") }

    val popularBanks = listOf(
        "HDFC Bank",
        "State Bank of India",
        "ICICI Bank",
        "Axis Bank",
        "Punjab National Bank",
        "Bank of Baroda",
        "JPMorgan Chase",
        "Bank of America",
        "Barclays"
    )

    val accountTypes = listOf("Savings", "Current", "Business", "Checking")

    ModalBottomSheet(
        onDismissRequest = onDismiss,
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
                            .background(AnimeGold.copy(alpha = 0.2f))
                            .border(1.dp, AnimeGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = AnimeGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(selectedLanguage, "Owner Bank Account Details", "ओनर बैंक खाता विवरण"),
                            color = MaterialTheme.colorScheme.onSurface,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = AppLocaleStrings.tr(selectedLanguage, "Connected account for subscription revenue & payouts", "सब्सक्रिप्शन भुगतान व निकासी के लिए बैंक विवरण"),
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                IconButton(onClick = onDismiss, modifier = Modifier.testTag("bank_dialog_close_btn")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Security assurance badge
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(10.dp)
            ) {
                Row(
                    modifier = Modifier.padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            selectedLanguage,
                            "Bank details are securely encrypted and stored on-device for direct settlement.",
                            "बैंक विवरण सुरक्षित रूप से एन्क्रिप्टेड हैं और सीधे भुगतान के लिए उपयोग होंगे।"
                        ),
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 1. Account Holder Name
            OutlinedTextField(
                value = holderName,
                onValueChange = {
                    holderName = it
                    errorMessage = ""
                },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Account Holder Full Name *", "खाताधारक का पूरा नाम *")) },
                placeholder = { Text("e.g. Aman Jangra") },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AnimeGold) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("bank_holder_name_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 2. Bank Name with Popular Bank quick chips
            OutlinedTextField(
                value = bankName,
                onValueChange = {
                    bankName = it
                    errorMessage = ""
                },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Bank Name *", "बैंक का नाम *")) },
                placeholder = { Text("e.g. HDFC Bank, State Bank of India") },
                leadingIcon = { Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AnimeCyan) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("bank_name_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = AppLocaleStrings.tr(selectedLanguage, "Quick Bank Presets:", "त्वरित बैंक चयन:"),
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(4.dp))

            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                popularBanks.forEach { b ->
                    val isSel = bankName.equals(b, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(if (isSel) AnimeCyan.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSel) AnimeCyan else Color.Transparent, RoundedCornerShape(6.dp))
                            .clickable {
                                bankName = b
                                errorMessage = ""
                            }
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = b,
                            color = if (isSel) AnimeCyan else MaterialTheme.colorScheme.onSurface,
                            fontSize = 10.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // 3. Bank Account Number & Confirm Account Number
            OutlinedTextField(
                value = accountNumber,
                onValueChange = {
                    accountNumber = it.filter { ch -> ch.isDigit() }
                    errorMessage = ""
                },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Account Number *", "खाता संख्या *")) },
                placeholder = { Text("Enter account number") },
                leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null, tint = AnimeGold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("bank_account_number_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = confirmAccountNumber,
                onValueChange = {
                    confirmAccountNumber = it.filter { ch -> ch.isDigit() }
                    errorMessage = ""
                },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Confirm Account Number *", "खाता संख्या पुनः दर्ज करें *")) },
                placeholder = { Text("Re-enter account number") },
                leadingIcon = { Icon(Icons.Default.Check, contentDescription = null, tint = AnimeGreen) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = confirmAccountNumber.isNotBlank() && accountNumber != confirmAccountNumber,
                supportingText = {
                    if (confirmAccountNumber.isNotBlank() && accountNumber != confirmAccountNumber) {
                        Text("Account numbers do not match!", color = MaterialTheme.colorScheme.error, fontSize = 10.sp)
                    }
                },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("bank_confirm_account_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 4. IFSC Code & SWIFT Code Row
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = ifscCode,
                    onValueChange = {
                        ifscCode = it.uppercase()
                        errorMessage = ""
                    },
                    label = { Text(AppLocaleStrings.tr(selectedLanguage, "IFSC Code *", "IFSC कोड *")) },
                    placeholder = { Text("e.g. HDFC0001234") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("bank_ifsc_input"),
                    shape = RoundedCornerShape(10.dp)
                )

                OutlinedTextField(
                    value = swiftBic,
                    onValueChange = {
                        swiftBic = it.uppercase()
                        errorMessage = ""
                    },
                    label = { Text("SWIFT / BIC") },
                    placeholder = { Text("e.g. HDFCINBB") },
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    singleLine = true,
                    modifier = Modifier.weight(1f).testTag("bank_swift_input"),
                    shape = RoundedCornerShape(10.dp)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 5. Branch Name & Account Type
            OutlinedTextField(
                value = branchName,
                onValueChange = { branchName = it },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Branch Name / City", "शाखा का नाम व शहर")) },
                placeholder = { Text("e.g. Connaught Place, New Delhi") },
                leadingIcon = { Icon(Icons.Default.LocationOn, contentDescription = null, tint = AnimePink) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("bank_branch_input"),
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Account Type Selector
            Text(
                text = AppLocaleStrings.tr(selectedLanguage, "Account Type:", "खाता प्रकार:"),
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                accountTypes.forEach { type ->
                    val isSel = accountType.equals(type, ignoreCase = true)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) AnimeGold.copy(alpha = 0.2f) else MaterialTheme.colorScheme.surfaceVariant)
                            .border(1.dp, if (isSel) AnimeGold else Color.Transparent, RoundedCornerShape(8.dp))
                            .clickable { accountType = type }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = type,
                            color = if (isSel) AnimeGold else MaterialTheme.colorScheme.onSurface,
                            fontSize = 11.sp,
                            fontWeight = if (isSel) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // 6. Linked UPI ID (Optional)
            OutlinedTextField(
                value = upiId,
                onValueChange = { upiId = it },
                label = { Text(AppLocaleStrings.tr(selectedLanguage, "Linked UPI ID (Optional)", "लिंक्ड UPI आईडी (वैकल्पिक)")) },
                placeholder = { Text("e.g. amjangra0@okhdfcbank") },
                leadingIcon = { Icon(Icons.Default.QrCode, contentDescription = null, tint = AnimeCyan) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("bank_upi_input"),
                shape = RoundedCornerShape(10.dp)
            )

            if (errorMessage.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = errorMessage, color = MaterialTheme.colorScheme.error, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Save / Update Button
            Button(
                onClick = {
                    if (holderName.isBlank()) {
                        errorMessage = "Please enter Account Holder Name"
                        return@Button
                    }
                    if (bankName.isBlank()) {
                        errorMessage = "Please enter Bank Name"
                        return@Button
                    }
                    if (accountNumber.isBlank() || accountNumber.length < 6) {
                        errorMessage = "Please enter a valid Account Number (min 6 digits)"
                        return@Button
                    }
                    if (accountNumber != confirmAccountNumber) {
                        errorMessage = "Account numbers do not match!"
                        return@Button
                    }
                    if (ifscCode.isBlank()) {
                        errorMessage = "Please enter IFSC or SWIFT Code"
                        return@Button
                    }

                    val updated = OwnerBankAccount(
                        holderName = holderName.trim(),
                        bankName = bankName.trim(),
                        accountNumber = accountNumber.trim(),
                        ifscCode = ifscCode.trim(),
                        swiftBic = swiftBic.trim(),
                        branchName = branchName.trim(),
                        accountType = accountType,
                        upiId = upiId.trim(),
                        isConnected = true,
                        lastUpdated = System.currentTimeMillis()
                    )
                    onSave(updated)
                    Toast.makeText(context, "✅ Bank Account Connected: ${updated.bankName}", Toast.LENGTH_SHORT).show()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_bank_details_btn")
            ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppLocaleStrings.tr(selectedLanguage, "Save & Connect Bank Account", "बैंक खाता विवरण सेव व कनेक्ट करें"),
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}

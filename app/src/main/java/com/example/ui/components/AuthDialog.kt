package com.example.ui.components

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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.AlertDialog
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
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountryCode
import com.example.data.model.CountryCodeProvider
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalBottomSheet(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val currentUser by viewModel.currentUser.collectAsState()
    var selectedAuthTab by remember { mutableIntStateOf(0) } // 0: Gmail, 1: Mobile No.

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
                            .background(
                                Brush.linearGradient(listOf(AnimePurple, AnimePink))
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "खाता लॉगिन / साइन इन (Account Auth)",
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Gmail या मोबाइल नंबर से सुरक्षित लॉगिन करें",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
                IconButton(onClick = onDismiss, modifier = Modifier.testTag("auth_dialog_close_btn")) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Auth Tab Selection: Gmail vs Mobile Number
            TabRow(
                selectedTabIndex = selectedAuthTab,
                containerColor = AnimeSurfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedAuthTab]),
                        color = if (selectedAuthTab == 0) AnimePink else AnimeCyan
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedAuthTab == 0,
                    onClick = { selectedAuthTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Gmail लॉगिन", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    selectedContentColor = AnimePink,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = selectedAuthTab == 1,
                    onClick = { selectedAuthTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("मोबाइल नंबर (OTP)", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        }
                    },
                    selectedContentColor = AnimeCyan,
                    unselectedContentColor = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (selectedAuthTab == 0) {
                GmailAuthContent(
                    viewModel = viewModel,
                    onSuccess = onDismiss
                )
            } else {
                MobileAuthContent(
                    viewModel = viewModel,
                    onSuccess = onDismiss
                )
            }
        }
    }
}

@Composable
fun GmailAuthContent(
    viewModel: AnimeViewModel,
    onSuccess: () -> Unit
) {
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }
    var showQuickOwnerButton by remember { mutableStateOf(true) }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "Google Account / Gmail",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = "अपने Gmail एड्रेस से सीधे लॉगिन करें या ओनर अकाउंट से प्रवेश करें:",
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Quick One-Tap App Owner Gmail Button
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.loginWithGmail("amjangra0@gmail.com", "Aman Jangra (Owner)")
                    onSuccess()
                }
                .testTag("one_tap_owner_gmail_btn"),
            colors = CardDefaults.cardColors(containerColor = AnimePurple.copy(alpha = 0.2f)),
            shape = RoundedCornerShape(14.dp),
            border = BorderStroke(1.dp, AnimeGold)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AnimeGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = Color.Black, modifier = Modifier.size(20.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Aman Jangra",
                            color = AnimeGold,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(AnimeGold.copy(alpha = 0.2f))
                                .padding(horizontal = 4.dp, vertical = 2.dp)
                        ) {
                            Text("👑 APP OWNER", color = AnimeGold, fontSize = 9.sp, fontWeight = FontWeight.ExtraBold)
                        }
                    }
                    Text(
                        text = "amjangra0@gmail.com",
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                }
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Divider
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.weight(1f).height(1.dp).background(AnimeSurfaceVariant))
            Text(
                text = " अथवा अन्य Gmail दर्ज करें ",
                color = TextMuted,
                fontSize = 11.sp,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
            Box(modifier = Modifier.weight(1f).height(1.dp).background(AnimeSurfaceVariant))
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text("आपका नाम (Display Name)") },
            placeholder = { Text("उदा: Priya Sharma", color = TextMuted) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("auth_gmail_name_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AnimePink,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text("Gmail Address") },
            placeholder = { Text("creator@gmail.com", color = TextMuted) },
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth().testTag("auth_gmail_email_input"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AnimePink,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (emailInput.isNotBlank() && emailInput.contains("@")) {
                    viewModel.loginWithGmail(emailInput, nameInput.ifBlank { null })
                    onSuccess()
                }
            },
            enabled = emailInput.isNotBlank() && emailInput.contains("@"),
            colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth().height(48.dp).testTag("auth_gmail_login_submit_btn")
        ) {
            Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Gmail से लॉगिन करें (Sign In with Gmail)",
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun MobileAuthContent(
    viewModel: AnimeViewModel,
    onSuccess: () -> Unit
) {
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(CountryCodeProvider.defaultCountry) }
    var showCountryPicker by remember { mutableStateOf(false) }
    var isOtpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var resendTimer by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var otpError by remember { mutableStateOf("") }

    LaunchedEffect(isTimerRunning) {
        if (isTimerRunning) {
            while (resendTimer > 0) {
                delay(1000L)
                resendTimer--
            }
            isTimerRunning = false
        }
    }

    Column(modifier = Modifier.fillMaxWidth()) {
        AnimatedVisibility(visible = !isOtpSent) {
            Column {
                Text(
                    text = "मोबाइल नंबर दर्ज करें (Global Phone Access)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "किसी भी देश का डायलिंग कोड चुनें और मोबाइल नंबर दर्ज करें:",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Mobile Input Row with Country Code Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Country Code Interactive Chip
                    CountryCodeSelectorChip(
                        selectedCountry = selectedCountry,
                        onClick = { showCountryPicker = true },
                        testTag = "auth_country_code_chip"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }
                            if (digitsOnly.length <= selectedCountry.maxDigits) {
                                phoneNumber = digitsOnly
                            }
                        },
                        placeholder = { Text(selectedCountry.exampleNumber, color = TextMuted) },
                        label = { Text("${selectedCountry.name} Phone") },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("auth_mobile_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Owner Mobile Fill Button
                OutlinedButton(
                    onClick = {
                        selectedCountry = CountryCodeProvider.defaultCountry
                        phoneNumber = "9876543210"
                    },
                    modifier = Modifier.fillMaxWidth().testTag("fill_owner_phone_btn"),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.5f))
                ) {
                    Icon(Icons.Default.Stars, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("👑 ओनर नंबर भरें (🇮🇳 +91 98765 43210)", color = AnimeGold, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (phoneNumber.length >= selectedCountry.minDigits) {
                            isOtpSent = true
                            resendTimer = 30
                            isTimerRunning = true
                            otpInput = "123456" // Auto-suggest default demo OTP
                        }
                    },
                    enabled = phoneNumber.length >= selectedCountry.minDigits,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("send_otp_btn")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("OTP भेजें (${selectedCountry.dialCode} $phoneNumber)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        AnimatedVisibility(visible = isOtpSent) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { isOtpSent = false }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AnimeCyan)
                    }
                    Column {
                        Text(
                            text = "OTP कोड सत्यापित करें",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${selectedCountry.flagEmoji} ${selectedCountry.dialCode} $phoneNumber पर भेजा गया",
                            color = AnimeCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = otpInput,
                    onValueChange = {
                        if (it.length <= 6) {
                            otpInput = it
                            otpError = ""
                        }
                    },
                    label = { Text("6-Digit OTP") },
                    placeholder = { Text("123456", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("otp_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeGreen,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (otpError.isNotBlank()) {
                    Text(text = otpError, color = AnimePink, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTimerRunning) "पुनः OTP भेजें ($resendTimer s)" else "OTP नहीं मिला?",
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                    TextButton(
                        onClick = {
                            resendTimer = 30
                            isTimerRunning = true
                            otpInput = "123456"
                        },
                        enabled = !isTimerRunning
                    ) {
                        Text("Resend OTP", color = if (!isTimerRunning) AnimeCyan else TextMuted, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (otpInput.length == 6) {
                            val fullPhone = "${selectedCountry.dialCode} $phoneNumber"
                            val success = viewModel.loginWithMobile(fullPhone, otpInput)
                            if (success) {
                                onSuccess()
                            } else {
                                otpError = "अमान्य OTP कोड! कृपया पुनः प्रयास करें।"
                            }
                        } else {
                            otpError = "कृपया पूरा 6-अंकों का OTP दर्ज करें।"
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_otp_submit_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("सत्यापित करें व लॉगिन करें (Verify & Login)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }

    // Modal: International Country Code Picker Bottom Sheet
    if (showCountryPicker) {
        CountryCodePickerBottomSheet(
            selectedCountry = selectedCountry,
            onCountrySelected = {
                selectedCountry = it
                showCountryPicker = false
            },
            onDismiss = { showCountryPicker = false }
        )
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = AnimePink)
                Spacer(modifier = Modifier.width(8.dp))
                Text("लॉगआउट की पुष्टि (Confirm Logout)", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        },
        text = {
            Text(
                text = "क्या आप वास्तव में अपने खाते से लॉगआउट करना चाहते हैं? लॉगआउट करने पर सुरक्षित ओनर वॉलेट व प्राइवेट सेटिंग्स हाइड हो जाएंगी।",
                color = TextSecondary,
                fontSize = 13.sp
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirmLogout,
                colors = ButtonDefaults.buttonColors(containerColor = AnimePink),
                modifier = Modifier.testTag("dialog_confirm_logout_btn")
            ) {
                Text("हाँ, लॉगआउट करें", fontWeight = FontWeight.Bold)
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

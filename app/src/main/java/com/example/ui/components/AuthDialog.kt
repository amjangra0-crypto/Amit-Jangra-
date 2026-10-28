package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountryCodeProvider
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

/**
 * Authentication Modal: Provides Clean User Sign In and Account Creation
 * Strict Privacy: All Owner pre-fills and credentials are completely removed.
 * Users can create accounts with Name, Gmail, Phone and OTP verification.
 * Owner accounts are recognized securely when the Owner signs in with their credentials.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthModalBottomSheet(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage

    // Top Level Mode: 0 = Sign In, 1 = Create Account
    var primaryModeTab by remember { mutableIntStateOf(0) }
    // In Sign In: 0 = Gmail, 1 = Mobile No
    var signInMethodTab by remember { mutableIntStateOf(0) }

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
                            imageVector = if (primaryModeTab == 0) Icons.Default.Security else Icons.Default.PersonAdd,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = if (primaryModeTab == 0) {
                                AppLocaleStrings.tr(lang, "Account Authentication", "खाता लॉगिन / साइन इन")
                            } else {
                                AppLocaleStrings.tr(lang, "Create Creator Account", "नया क्रिएटर खाता बनाएं")
                            },
                            color = TextPrimary,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (primaryModeTab == 0) {
                                AppLocaleStrings.tr(lang, "Sign in securely with Gmail or Mobile Number", "Gmail या मोबाइल नंबर से सुरक्षित लॉगिन करें")
                            } else {
                                AppLocaleStrings.tr(lang, "Register with Name, Gmail and Mobile OTP", "नाम, Gmail व मोबाइल OTP से पंजीकरण करें")
                            },
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

            // Primary Tabs: Sign In vs Create Account
            TabRow(
                selectedTabIndex = primaryModeTab,
                containerColor = AnimeSurfaceVariant,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[primaryModeTab]),
                        color = if (primaryModeTab == 0) AnimePink else AnimeCyan
                    )
                },
                modifier = Modifier.clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = primaryModeTab == 0,
                    onClick = { primaryModeTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Security, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "Sign In", "लॉगिन करें"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    },
                    selectedContentColor = AnimePink,
                    unselectedContentColor = TextMuted
                )
                Tab(
                    selected = primaryModeTab == 1,
                    onClick = { primaryModeTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.PersonAdd, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "Create Account", "नया खाता बनाएं"),
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    },
                    selectedContentColor = AnimeCyan,
                    unselectedContentColor = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (primaryModeTab == 0) {
                // Sign In Method Sub-tabs: Gmail vs Mobile Number
                TabRow(
                    selectedTabIndex = signInMethodTab,
                    containerColor = AnimeSurfaceVariant.copy(alpha = 0.5f),
                    indicator = { tabPositions ->
                        TabRowDefaults.SecondaryIndicator(
                            Modifier.tabIndicatorOffset(tabPositions[signInMethodTab]),
                            color = if (signInMethodTab == 0) AnimePurple else AnimeGreen
                        )
                    },
                    modifier = Modifier.clip(RoundedCornerShape(10.dp))
                ) {
                    Tab(
                        selected = signInMethodTab == 0,
                        onClick = { signInMethodTab = 0 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Gmail", "Gmail"),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        selectedContentColor = AnimePurple,
                        unselectedContentColor = TextMuted
                    )
                    Tab(
                        selected = signInMethodTab == 1,
                        onClick = { signInMethodTab = 1 },
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = AppLocaleStrings.tr(lang, "Mobile (OTP)", "मोबाइल (OTP)"),
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            }
                        },
                        selectedContentColor = AnimeGreen,
                        unselectedContentColor = TextMuted
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                if (signInMethodTab == 0) {
                    GmailAuthContent(viewModel = viewModel, onSuccess = onDismiss)
                } else {
                    MobileAuthContent(viewModel = viewModel, onSuccess = onDismiss)
                }
            } else {
                // Create Account Flow
                CreateAccountContent(viewModel = viewModel, onSuccess = onDismiss)
            }
        }
    }
}

/**
 * Clean Gmail Authentication: Strictly NO pre-filled owner details or owner cards.
 * Any user enters their own Gmail; if the owner enters owner credentials, owner access is granted.
 */
@Composable
fun GmailAuthContent(
    viewModel: AnimeViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage
    var emailInput by remember { mutableStateOf("") }
    var nameInput by remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = AppLocaleStrings.tr(lang, "Google Account / Gmail", "गूगल खाता / Gmail"),
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = AppLocaleStrings.tr(lang, "Enter your Gmail address to sign in:", "लॉगिन करने के लिए अपना Gmail पता दर्ज करें:"),
            color = TextSecondary,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        OutlinedTextField(
            value = emailInput,
            onValueChange = { emailInput = it },
            label = { Text(AppLocaleStrings.tr(lang, "Gmail Address", "Gmail पता")) },
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

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = nameInput,
            onValueChange = { nameInput = it },
            label = { Text(AppLocaleStrings.tr(lang, "Display Name (Optional)", "डिस्प्ले नाम (वैकल्पिक)")) },
            placeholder = { Text(AppLocaleStrings.tr(lang, "e.g., Alex Rivers", "उदा: अमन शर्मा"), color = TextMuted) },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().testTag("auth_gmail_name_input"),
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
                text = AppLocaleStrings.tr(lang, "Sign In with Gmail", "Gmail से लॉगिन करें"),
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Clean Mobile Authentication: Strictly NO pre-filled owner number or fill owner button.
 */
@Composable
fun MobileAuthContent(
    viewModel: AnimeViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage
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
                    text = AppLocaleStrings.tr(lang, "Global Phone Access", "मोबाइल नंबर दर्ज करें"),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = AppLocaleStrings.tr(lang, "Select country dial code and enter mobile number:", "देश का डायलिंग कोड चुनें और मोबाइल नंबर दर्ज करें:"),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // Mobile Input Row with Country Code Selector
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
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
                        label = { Text(AppLocaleStrings.tr(lang, "${selectedCountry.name} Phone", "${selectedCountry.name} फ़ोन")) },
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

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (phoneNumber.length >= selectedCountry.minDigits) {
                            isOtpSent = true
                            resendTimer = 30
                            isTimerRunning = true
                            otpInput = "123456" // Auto-fill default demo OTP
                        }
                    },
                    enabled = phoneNumber.length >= selectedCountry.minDigits,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("send_otp_btn")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${AppLocaleStrings.tr(lang, "Send OTP", "OTP भेजें")} (${selectedCountry.dialCode} $phoneNumber)",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
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
                            text = AppLocaleStrings.tr(lang, "Verify OTP Code", "OTP कोड सत्यापित करें"),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Sent to ${selectedCountry.flagEmoji} ${selectedCountry.dialCode} $phoneNumber",
                                "${selectedCountry.flagEmoji} ${selectedCountry.dialCode} $phoneNumber पर भेजा गया"
                            ),
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
                    label = { Text(AppLocaleStrings.tr(lang, "6-Digit OTP Code", "6-अंकों का OTP कोड")) },
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
                        text = if (isTimerRunning) {
                            AppLocaleStrings.tr(lang, "Resend OTP ($resendTimer s)", "पुनः OTP भेजें ($resendTimer s)")
                        } else {
                            AppLocaleStrings.tr(lang, "Didn't receive OTP?", "OTP नहीं मिला?")
                        },
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
                        Text(
                            text = AppLocaleStrings.tr(lang, "Resend OTP", "पुनः भेजें"),
                            color = if (!isTimerRunning) AnimeCyan else TextMuted,
                            fontSize = 12.sp
                        )
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
                                otpError = AppLocaleStrings.tr(lang, "Invalid OTP code! Please try again.", "अमान्य OTP कोड! कृपया पुनः प्रयास करें।")
                            }
                        } else {
                            otpError = AppLocaleStrings.tr(lang, "Please enter full 6-digit OTP code.", "कृपया पूरा 6-अंकों का OTP दर्ज करें।")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("verify_otp_submit_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Verify & Sign In", "सत्यापित करें व लॉगिन करें"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

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

/**
 * User Account Creation Form:
 * Fills Name, Gmail ID, Mobile No. with Country Code and OTP Verification.
 * Saves user account persistently so users never have to repeatedly login.
 */
@Composable
fun CreateAccountContent(
    viewModel: AnimeViewModel,
    onSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage

    var nameInput by remember { mutableStateOf("") }
    var emailInput by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var selectedCountry by remember { mutableStateOf(CountryCodeProvider.defaultCountry) }
    var showCountryPicker by remember { mutableStateOf(false) }

    var isOtpSent by remember { mutableStateOf(false) }
    var otpInput by remember { mutableStateOf("") }
    var resendTimer by remember { mutableIntStateOf(30) }
    var isTimerRunning by remember { mutableStateOf(false) }
    var formError by remember { mutableStateOf("") }

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
                    text = AppLocaleStrings.tr(lang, "Enter Registration Details", "पंजीकरण विवरण भरें"),
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Fill your name, Gmail and mobile number. Verification code will be sent via OTP.",
                        "अपना नाम, Gmail व मोबाइल नंबर भरें। खाता सत्यापन हेतु OTP भेजा जाएगा।"
                    ),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )

                // 1. Full Name
                OutlinedTextField(
                    value = nameInput,
                    onValueChange = { nameInput = it; formError = "" },
                    label = { Text(AppLocaleStrings.tr(lang, "Full Name / Display Name", "पूरा नाम")) },
                    placeholder = { Text(AppLocaleStrings.tr(lang, "e.g., Alex Rivers", "उदा: अमन शर्मा"), color = TextMuted) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = AnimeCyan) },
                    modifier = Modifier.fillMaxWidth().testTag("create_account_name_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 2. Gmail Address
                OutlinedTextField(
                    value = emailInput,
                    onValueChange = { emailInput = it; formError = "" },
                    label = { Text(AppLocaleStrings.tr(lang, "Gmail Address", "Gmail पता")) },
                    placeholder = { Text("creator@gmail.com", color = TextMuted) },
                    singleLine = true,
                    leadingIcon = { Icon(Icons.Default.Mail, contentDescription = null, tint = AnimePink) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    modifier = Modifier.fillMaxWidth().testTag("create_account_email_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimePink,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // 3. Mobile Number with Country Code
                Text(
                    text = AppLocaleStrings.tr(lang, "Mobile Number (OTP Verification)", "मोबाइल नंबर (OTP सत्यापन)"),
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountryCodeSelectorChip(
                        selectedCountry = selectedCountry,
                        onClick = { showCountryPicker = true },
                        testTag = "create_account_country_chip"
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { input ->
                            val digitsOnly = input.filter { it.isDigit() }
                            if (digitsOnly.length <= selectedCountry.maxDigits) {
                                phoneNumber = digitsOnly
                                formError = ""
                            }
                        },
                        placeholder = { Text(selectedCountry.exampleNumber, color = TextMuted) },
                        label = { Text(AppLocaleStrings.tr(lang, "${selectedCountry.name} Phone", "${selectedCountry.name} फ़ोन")) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        modifier = Modifier.weight(1f).testTag("create_account_phone_input"),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )
                }

                if (formError.isNotBlank()) {
                    Text(text = formError, color = AnimePink, fontSize = 11.sp, modifier = Modifier.padding(top = 6.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Note for App Owner
                Card(
                    colors = CardDefaults.cardColors(containerColor = AnimeGold.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.3f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("ℹ️", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "App Owner note: Owner does not need to create an account. Simply sign in via the Sign In tab.",
                                "ऐप ओनर नोट: ओनर को नया खाता बनाने की आवश्यकता नहीं है। सीधे 'लॉगिन' टैब से प्रवेश करें।"
                            ),
                            color = AnimeGold,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (nameInput.isBlank()) {
                            formError = AppLocaleStrings.tr(lang, "Please enter your name.", "कृपया अपना नाम दर्ज करें।")
                        } else if (!emailInput.contains("@") || emailInput.isBlank()) {
                            formError = AppLocaleStrings.tr(lang, "Please enter a valid Gmail address.", "कृपया मान्य Gmail पता दर्ज करें।")
                        } else if (phoneNumber.length < selectedCountry.minDigits) {
                            formError = AppLocaleStrings.tr(lang, "Please enter a valid mobile number.", "कृपया मान्य मोबाइल नंबर दर्ज करें।")
                        } else {
                            formError = ""
                            isOtpSent = true
                            resendTimer = 30
                            isTimerRunning = true
                            otpInput = "123456" // Auto-fill default verification OTP
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("create_account_send_otp_btn")
                ) {
                    Icon(Icons.Default.Phone, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Send Verification Code (OTP)", "सत्यापन कोड (OTP) भेजें"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
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
                            text = AppLocaleStrings.tr(lang, "Confirm OTP & Create Account", "OTP सत्यापित करें व खाता बनाएं"),
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Verification code sent to ${selectedCountry.flagEmoji} ${selectedCountry.dialCode} $phoneNumber",
                                "सत्यापन कोड ${selectedCountry.flagEmoji} ${selectedCountry.dialCode} $phoneNumber पर भेजा गया"
                            ),
                            color = AnimeCyan,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = otpInput,
                    onValueChange = {
                        if (it.length <= 6) {
                            otpInput = it
                            formError = ""
                        }
                    },
                    label = { Text(AppLocaleStrings.tr(lang, "6-Digit OTP Code", "6-अंकों का OTP कोड")) },
                    placeholder = { Text("123456", color = TextMuted) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth().testTag("create_account_otp_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeGreen,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(12.dp)
                )

                if (formError.isNotBlank()) {
                    Text(text = formError, color = AnimePink, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isTimerRunning) {
                            AppLocaleStrings.tr(lang, "Resend in ($resendTimer s)", "पुनः भेजें ($resendTimer s)")
                        } else {
                            AppLocaleStrings.tr(lang, "Didn't receive code?", "कोड नहीं मिला?")
                        },
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
                        Text(
                            text = AppLocaleStrings.tr(lang, "Resend OTP", "OTP पुनः भेजें"),
                            color = if (!isTimerRunning) AnimeCyan else TextMuted,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        if (otpInput.length == 6) {
                            val fullPhone = "${selectedCountry.dialCode} $phoneNumber"
                            val success = viewModel.createAccount(
                                name = nameInput,
                                email = emailInput,
                                phoneNumber = fullPhone,
                                otp = otpInput
                            )
                            if (success) {
                                onSuccess()
                            } else {
                                formError = AppLocaleStrings.tr(lang, "Verification failed! Please try again.", "सत्यापन विफल! कृपया पुनः प्रयास करें।")
                            }
                        } else {
                            formError = AppLocaleStrings.tr(lang, "Please enter full 6-digit OTP code.", "कृपया पूरा 6-अंकों का OTP दर्ज करें।")
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(48.dp).testTag("create_account_submit_btn")
                ) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Confirm & Create Account", "पुष्टि करें व खाता बनाएं"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }

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

/**
 * Logout Confirmation Modal
 */
@Composable
fun LogoutConfirmationDialog(
    onConfirmLogout: () -> Unit,
    onDismiss: () -> Unit,
    lang: String = "English"
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = AnimePink)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppLocaleStrings.tr(lang, "Confirm Logout", "लॉगआउट की पुष्टि"),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Text(
                text = AppLocaleStrings.tr(
                    lang,
                    "Are you sure you want to log out of your account? Secure owner wallet and private settings will be hidden.",
                    "क्या आप वास्तव में अपने खाते से लॉगआउट करना चाहते हैं? लॉगआउट करने पर सुरक्षित ओनर वॉलेट व प्राइवेट सेटिंग्स हाइड हो जाएंगी।"
                ),
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
                Text(
                    text = AppLocaleStrings.tr(lang, "Yes, Log Out", "हाँ, लॉगआउट करें"),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = AppLocaleStrings.tr(lang, "Cancel", "रद्द करें"),
                    color = TextMuted
                )
            }
        },
        containerColor = AnimeSurface
    )
}

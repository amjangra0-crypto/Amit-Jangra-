package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Stars
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CountryCode
import com.example.data.model.CountryCodeProvider
import com.example.ui.AnimeViewModel
import com.example.ui.components.CountryCodePickerBottomSheet
import com.example.ui.components.CountryCodeSelectorChip
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

@Composable
fun ProfileSettingsScreen(
    viewModel: AnimeViewModel,
    onNavigateBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current

    var displayName by remember { mutableStateOf(currentUser.displayName) }
    var bio by remember { mutableStateOf(currentUser.bio) }
    val initialCountry = remember(currentUser.phoneNumber) {
        val phone = currentUser.phoneNumber ?: ""
        CountryCodeProvider.countries
            .sortedByDescending { it.dialCode.length }
            .firstOrNull { phone.startsWith(it.dialCode) }
            ?: CountryCodeProvider.defaultCountry
    }
    var selectedCountry by remember { mutableStateOf(initialCountry) }
    var showCountryPicker by remember { mutableStateOf(false) }

    val rawNumber = remember(currentUser.phoneNumber, initialCountry) {
        val phone = currentUser.phoneNumber ?: ""
        if (phone.startsWith(initialCountry.dialCode)) {
            phone.removePrefix(initialCountry.dialCode).trim()
        } else {
            phone
        }
    }
    var phoneNumber by remember { mutableStateOf(rawNumber) }
    var email by remember { mutableStateOf(currentUser.email ?: "") }
    var selectedAvatar by remember { mutableStateOf(currentUser.avatarDrawableName) }
    var specialty by remember { mutableStateOf(currentUser.creatorSpecialty) }
    var notificationsEnabled by remember { mutableStateOf(currentUser.notificationEnabled) }
    var autoSyncDubbing by remember { mutableStateOf(currentUser.autoSyncDubbing) }
    var feedbackMessage by remember { mutableStateOf("") }

    val availableAvatars = listOf(
        Pair("char_shonen_hero", "Shonen Hero (रेन)"),
        Pair("char_anime_heroine", "Anime Heroine (आयरा)"),
        Pair("char_lady_mentor", "Sensei Mentor (क्योटो)"),
        Pair("char_chibi_mascot", "Chibi Mascot (पोपो)")
    )

    val specialties = listOf(
        "Anime Film Director",
        "Manga Concept Artist",
        "AI Voice Dubbing Artist",
        "Cinematic Sound Designer"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .padding(bottom = 88.dp)
    ) {
        // Top Navigation Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack, modifier = Modifier.testTag("profile_settings_back_btn")) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = AnimeCyan)
            }
            Spacer(modifier = Modifier.width(6.dp))
            Column {
                Text(
                    text = "प्रोफ़ाइल सेटिंग्स (Profile Settings)",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Text(
                    text = "अपनी प्रोफ़ाइल, संपर्क व अवतार को अनुकूलित करें",
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Avatar Picker Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimePurple)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "अवतार छवि चुनें (Select Persona Avatar):",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    availableAvatars.forEach { (drawableName, label) ->
                        val isSelected = selectedAvatar == drawableName
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier
                                .clickable { selectedAvatar = drawableName }
                                .padding(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .border(
                                        2.dp,
                                        if (isSelected) AnimeGold else Color.Transparent,
                                        CircleShape
                                    )
                                    .background(AnimeSurfaceVariant),
                                contentAlignment = Alignment.Center
                            ) {
                                val resId = context.resources.getIdentifier(drawableName, "drawable", context.packageName)
                                if (resId != 0) {
                                    Image(
                                        painter = painterResource(id = resId),
                                        contentDescription = label,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                } else {
                                    Icon(Icons.Default.Face, contentDescription = null, tint = AnimePink)
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = label.split(" ").first(),
                                color = if (isSelected) AnimeGold else TextMuted,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Personal Info Form Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "व्यक्तिगत जानकारी (Personal Information)",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = displayName,
                    onValueChange = { displayName = it },
                    label = { Text("डिस्प्ले नाम (Display Name)") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    label = { Text("बायो / परिचय (About Bio)") },
                    placeholder = { Text("उदा: Creator of Next-Gen Cyber Anime") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimeCyan,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    maxLines = 3,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_bio_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Mobile No with Multi Country Code Selector
                Text(
                    text = "मोबाइल नंबर (Global Access Phone Number)",
                    color = TextPrimary,
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
                        testTag = "profile_country_code_chip"
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
                        label = { Text("${selectedCountry.name} Phone") },
                        placeholder = { Text(selectedCountry.exampleNumber, color = TextMuted) },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = AnimeCyan,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.weight(1f).testTag("profile_edit_phone_input")
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Gmail
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Gmail Address") },
                    placeholder = { Text("creator@gmail.com") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AnimePink,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().testTag("profile_edit_email_input")
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Creator Specialty
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "क्रिएटर विशेषज्ञता (Creator Specialty):",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(10.dp))

                specialties.forEach { spec ->
                    val isChosen = specialty == spec
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isChosen) AnimePurple.copy(alpha = 0.2f) else Color.Transparent)
                            .clickable { specialty = spec }
                            .padding(horizontal = 10.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (isChosen) Icons.Default.Check else Icons.Default.Stars,
                            contentDescription = null,
                            tint = if (isChosen) AnimeCyan else TextMuted,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = spec,
                            color = if (isChosen) TextPrimary else TextSecondary,
                            fontWeight = if (isChosen) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Preferences Toggles
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "नोटिफिकेशन व प्राथमिकताएं (Preferences)",
                    color = TextPrimary,
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
                        Text("सब्सक्रिप्शन व जमा अलर्ट", color = TextPrimary, fontSize = 13.sp)
                        Text("नया सब्सक्रिप्शन आने पर तत्काल सूचना", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeGreen, checkedTrackColor = AnimeGreen.copy(alpha = 0.3f))
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ऑटो लिप-सिंक वॉइस डबिंग", color = TextPrimary, fontSize = 13.sp)
                        Text("संवाद के अनुसार स्वतः कैडेंस सिंक", color = TextMuted, fontSize = 11.sp)
                    }
                    Switch(
                        checked = autoSyncDubbing,
                        onCheckedChange = { autoSyncDubbing = it },
                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeCyan, checkedTrackColor = AnimeCyan.copy(alpha = 0.3f))
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // YouTube Channel ID & OAuth 2.0 Credentials Section (Owner Secured)
        com.example.ui.components.YouTubeOAuthCredentialsSection(viewModel = viewModel)

        Spacer(modifier = Modifier.height(16.dp))

        if (feedbackMessage.isNotBlank()) {
            Text(
                text = feedbackMessage,
                color = AnimeGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 10.dp)
            )
        }

        // Save Profile Button
        Button(
            onClick = {
                val fullPhone = if (phoneNumber.isNotBlank()) "${selectedCountry.dialCode} ${phoneNumber.trim()}" else null
                viewModel.updateProfile(
                    displayName = displayName,
                    bio = bio,
                    phone = fullPhone,
                    email = email,
                    avatarDrawable = selectedAvatar,
                    specialty = specialty,
                    notifications = notificationsEnabled,
                    autoSync = autoSyncDubbing
                )
                feedbackMessage = "✓ प्रोफ़ाइल सेटिंग्स सफलतापूर्वक सहेजी गईं! (Profile Updated)"
            },
            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("save_profile_settings_btn")
        ) {
            Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "सेटिंग्स सहेजें (Save Changes)",
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
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

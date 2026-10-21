package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import android.widget.Toast
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.Publish
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SystemUpdate
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.BuildConfig
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
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
fun UpdateSettingsScreen(viewModel: AnimeViewModel) {
    val updateState by viewModel.updateState.collectAsState()
    val uiState by viewModel.uiState.collectAsState()
    val lang = uiState.selectedLanguage
    val currentUser by viewModel.currentUser.collectAsState()
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    var ownerNewVersion by remember { mutableStateOf("2.5.0") }
    var ownerNewTitle by remember { mutableStateOf("Anime Studio AI 2.5.0 Feature Drop") }
    var ownerNewChange by remember { mutableStateOf("New Romance & Dramatic Anime Expression Models") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(scrollState)
            .padding(16.dp)
            .padding(bottom = 80.dp)
    ) {
        // TOP APP BAR
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = { viewModel.setTab(AppTab.SETTINGS) },
                    modifier = Modifier.testTag("update_screen_back_btn")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = AppLocaleStrings.tr(lang, "Update Center", "अपडेट सेंटर"),
                    color = MaterialTheme.colorScheme.onBackground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            TextButton(
                onClick = { viewModel.setTab(AppTab.SETTINGS) },
                modifier = Modifier.testTag("update_screen_done_btn")
            ) {
                Text(
                    text = AppLocaleStrings.tr(lang, "Done", "पूर्ण"),
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }

        // Current Version & Play Store Status Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(1.dp, AnimeCyan)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "App Update & System Status",
                                "ऐप अपडेट एवं सिस्टम स्थिति"
                            ),
                            color = TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "${AppLocaleStrings.tr(lang, "Current Version:", "वर्तमान संस्करण:")} v${updateState.currentVersion}",
                            color = AnimeCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Box(
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(AnimeGreen.copy(alpha = 0.2f))
                            .border(1.dp, AnimeGreen, CircleShape)
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Play Store Ready", "प्ले स्टोर रेडी"),
                            color = AnimeGreen,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        val avail = viewModel.checkForNewUpdates()
                        Toast.makeText(
                            context,
                            if (avail) "🎉 New version v${updateState.latestVersion} found!"
                            else "✓ You're running the latest version (v${updateState.currentVersion})",
                            Toast.LENGTH_SHORT
                        ).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(42.dp)
                        .testTag("update_check_button")
                ) {
                    Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Check for Updates Now", "अभी नए अपडेट चेक करें"),
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // In-App OTA Update Engine
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = BorderStroke(
                1.dp,
                if (updateState.isUpdateAvailable) AnimePurple else AnimeSurfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = if (updateState.isUpdateAvailable) Icons.Default.NewReleases else Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (updateState.isUpdateAvailable) AnimeGold else AnimeGreen
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (updateState.isUpdateAvailable)
                            AppLocaleStrings.tr(
                                lang,
                                "New feature update available (v${updateState.latestVersion})",
                                "नया फीचर अपडेट उपलब्ध है (v${updateState.latestVersion})"
                            )
                        else
                            AppLocaleStrings.tr(
                                lang,
                                "Your app is up to date!",
                                "आपका ऐप पूरी तरह से अप-टू-डेट है!"
                            ),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (updateState.isUpdateAvailable) {
                    Text(
                        text = "${AppLocaleStrings.tr(lang, "Release Notes & Features", "रिलीज नोट्स एवं नए फीचर्स")} (${updateState.releaseDate}):",
                        color = AnimeCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    updateState.changeLogs.forEach { log ->
                        Text(
                            text = "• $log",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            modifier = Modifier.padding(vertical = 2.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (updateState.isDownloading) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = AnimePurple,
                                strokeWidth = 2.dp
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Downloading and installing update...",
                                    "अपडेट डाउनलोड व इंस्टॉल हो रहा है..."
                                ),
                                color = AnimeCyanLight,
                                fontSize = 12.sp
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.triggerInAppUpdate() },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("install_update_button")
                        ) {
                            Icon(Icons.Default.CloudDownload, contentDescription = null, tint = Color.White)
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Update Now (Install Feature Drop)",
                                    "अब अपडेट करें (Install Feature Drop)"
                                ),
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Upcoming anime models, voice packs, and video creation tools will be available here with one-click installation.",
                            "भविष्य में आने वाले नए एनिमे मॉडल, वॉइस पैक और वीडियो टूल्स यहां एक क्लिक में उपलब्ध होंगे।"
                        ),
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // -------------------------------------------------------------------------------------
        // OWNER FEATURE DROP & RELEASE PUBLISHER (VISIBLE ONLY TO OWNER)
        // -------------------------------------------------------------------------------------
        if (currentUser.isOwner) {
            Card(
                modifier = Modifier.fillMaxWidth().testTag("owner_release_publisher_card"),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, AnimeGold)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(AnimeGold),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Publish, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "👑 Owner Feature Drop Publisher",
                                color = AnimeGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Publish new updates with latest features for all users",
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = ownerNewVersion,
                        onValueChange = { ownerNewVersion = it },
                        label = { Text("Version Tag (e.g. 2.5.0)", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("owner_update_version_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeGold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ownerNewTitle,
                        onValueChange = { ownerNewTitle = it },
                        label = { Text("Release Title", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("owner_update_title_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeGold)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = ownerNewChange,
                        onValueChange = { ownerNewChange = it },
                        label = { Text("Feature / Change Summary", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth().testTag("owner_update_notes_input"),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeGold)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            viewModel.publishOwnerAppUpdate(
                                version = ownerNewVersion,
                                title = ownerNewTitle,
                                changelog = listOf(
                                    ownerNewChange,
                                    "Romance & Love Stories Fully Supported (Kissing, Hugs, Confessions)",
                                    "Google Play Zero Adult Content Guardrail Active"
                                )
                            )
                            Toast.makeText(context, "👑 Successfully published v$ownerNewVersion to all users!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp).testTag("owner_publish_release_btn")
                    ) {
                        Icon(Icons.Default.Publish, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Publish Release to All Users",
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }

        // Google Play Store Compliance Checklist
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            shape = RoundedCornerShape(16.dp),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGreen)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Google Play Store Compliance Checklist",
                            "गूगल प्ले स्टोर अनुपालन (Play Store Checklist)"
                        ),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))

                val complianceItems = if (AppLocaleStrings.isHindi(lang)) {
                    listOf(
                        "सुरक्षित अनुमतियां: किसी भी अनावश्यक स्टोरेज अनुमति के बिना स्वच्छ आर्किटेक्चर",
                        "गूगल प्ले सुरक्षा दिशानिर्देश: शून्य गैरकानूनी डायनामिक कोड लोडिंग (No DCL)",
                        "डेटा सुरक्षा: संपूर्ण डेटा स्थानीय रूम डेटाबेस (Room SQLite) में एन्क्रिप्टेड",
                        "अद्यतन अनुकूल: नए फीचर्स जोड़ने हेतु मॉड्यूलर जेटपैक कंपोज़ UI आर्किटेक्चर",
                        "स्मार्ट फॉलबैक: नेटवर्क अनुपलब्ध होने पर भी निर्बाध एनिमे निर्माण"
                    )
                } else {
                    listOf(
                        "Permission Safety: Clean architecture with zero unnecessary storage permissions",
                        "Google Play Guidelines: Zero Dynamic Code Loading (No DCL) for policy compliance",
                        "Data Protection: Complete data securely encrypted in local Room SQLite database",
                        "Update Scalability: Modular Jetpack Compose UI architecture for feature drops",
                        "Smart Fallback: Seamless offline & local animation generation even without network"
                    )
                }

                complianceItems.forEach { item ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            Icons.Default.VerifiedUser,
                            contentDescription = null,
                            tint = AnimeGreen,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = item,
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // AI Engine & Gemini Configuration Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Key, contentDescription = null, tint = AnimeGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "AI Engine & Model Status",
                            "एआई इंजन स्थिति (AI Engine Status)"
                        ),
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))

                val hasKey = try {
                    BuildConfig.GEMINI_API_KEY.isNotBlank() && BuildConfig.GEMINI_API_KEY != "MY_GEMINI_API_KEY"
                } catch (e: Throwable) {
                    false
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .clip(CircleShape)
                            .background(if (hasKey) AnimeGreen else AnimeCyan)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (hasKey)
                            AppLocaleStrings.tr(
                                lang,
                                "Gemini 1.5 Flash API is active",
                                "Gemini 1.5 Flash API सक्रिय है"
                            )
                        else
                            AppLocaleStrings.tr(
                                lang,
                                "Local Smart Anime Generator + Gemini Fallback is active",
                                "लोकल स्मार्ट एनिमे जनरेटर + Gemini Fallback सक्रिय है"
                            ),
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Note: If you want to use your personal Gemini API Key, you can configure GEMINI_API_KEY in the AI Studio Secrets panel.",
                        "नोट: यदि आप अपनी व्यक्तिगत Gemini API Key लगाना चाहते हैं तो AI Studio के Secrets पैनल में GEMINI_API_KEY दर्ज कर सकते हैं।"
                    ),
                    color = TextMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

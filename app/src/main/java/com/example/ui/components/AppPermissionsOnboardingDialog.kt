package com.example.ui.components

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.ContextCompat
import com.example.localization.AppLocaleStrings
import com.example.localization.GpsLocationManager
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
import com.example.util.PermissionPreferenceManager

/**
 * One-Time Installation Permissions Onboarding Dialog.
 * Prompts strictly ONCE upon initial app installation to acquire Google Maps GPS location
 * for regional auto-detection and anime studio grounding, plus microphone for voice dubbing.
 * Persistently recorded so it never prompts again automatically.
 */
@Composable
fun AppPermissionsOnboardingDialog(
    language: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val gpsManager = remember { GpsLocationManager.getInstance(context) }
    val activeCountry by gpsManager.activeGpsCountry.collectAsState()

    var locationGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED
        )
    }

    var micGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
        )
    }

    fun markHandled() {
        PermissionPreferenceManager.markPermissionRequestedOnInstall(context)
    }

    val multiplePermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val fineLoc = result[Manifest.permission.ACCESS_FINE_LOCATION] ?: false
        val coarseLoc = result[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false
        val mic = result[Manifest.permission.RECORD_AUDIO] ?: false

        locationGranted = fineLoc || coarseLoc
        micGranted = mic

        markHandled()

        if (locationGranted) {
            gpsManager.refreshGpsLocation()
        }

        val successMsg = if (AppLocaleStrings.isHindi(language)) {
            "✅ गूगल मैप्स व स्टूडियो अनुमतियाँ सफलतापूर्वक सक्रिय की गईं!"
        } else {
            "✅ Google Maps & Studio permissions active!"
        }
        Toast.makeText(context, successMsg, Toast.LENGTH_SHORT).show()
        onDismiss()
    }

    Dialog(
        onDismissRequest = {
            markHandled()
            onDismiss()
        },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
                .testTag("permissions_onboarding_dialog"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            border = BorderStroke(
                1.5.dp,
                Brush.linearGradient(listOf(AnimeCyan, AnimePink, AnimeGold))
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(listOf(AnimeCyan, AnimePink, AnimeGold))
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // One-Time Install Badge
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimeCyan.copy(alpha = 0.18f))
                        .border(1.dp, AnimeCyan.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (AppLocaleStrings.isHindi(language)) {
                            "⚡ इंस्टालेशन पर केवल एक बार अनुमति (One-Time Install Setup)"
                        } else {
                            "⚡ One-Time Installation Permission"
                        },
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = if (AppLocaleStrings.isHindi(language)) {
                        "गूगल मैप्स व ग्लोबल आर्किटेक्चर सेटअप"
                    } else {
                        "Google Maps & Global Studio Architecture"
                    },
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = if (AppLocaleStrings.isHindi(language)) {
                        "जीपीएस ऑटो-डिटेक्शन, रीजनल एनिमे लोकेशन ग्राउंडिंग व विशुद्ध देशीय भाषा अनुवाद सक्रिय करें।"
                    } else {
                        "Enable hardware GPS regional auto-detection, Google Maps scene grounding, and zero-bleed native localization."
                    },
                    color = TextSecondary,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Feature 1: Google Maps & GPS Regional Auto-Detection
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AnimeSurfaceVariant)
                        .border(
                            1.dp,
                            if (locationGranted) AnimeGreen.copy(alpha = 0.6f) else AnimeCyan.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (locationGranted) AnimeGreen.copy(alpha = 0.2f) else AnimeCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = if (locationGranted) AnimeGreen else AnimeCyan,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (AppLocaleStrings.isHindi(language)) "गूगल मैप्स व जीपीएस ऑटो-डिटेक्शन" else "Google Maps & GPS Detection",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (locationGranted) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (AppLocaleStrings.isHindi(language)) {
                                "क्षेत्रीय भाषा का शून्य-मिलावट अनुवाद व गूगल मैप्स सीन लोकेशन स्काउटिंग।"
                            } else {
                                "Maps-grounded anime scene landmarks & pure script regional localization."
                            },
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Feature 2: Microphone for Audio Dubbing
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(AnimeSurfaceVariant)
                        .border(
                            1.dp,
                            if (micGranted) AnimeGreen.copy(alpha = 0.6f) else AnimePink.copy(alpha = 0.4f),
                            RoundedCornerShape(14.dp)
                        )
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (micGranted) AnimeGreen.copy(alpha = 0.2f) else AnimePink.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = if (micGranted) AnimeGreen else AnimePink,
                            modifier = Modifier.size(22.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppLocaleStrings.get("perm_mic", language),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            if (micGranted) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = AppLocaleStrings.get("perm_mic_desc", language),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // GPS Regional Tag Preview Box
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AnimePurple.copy(alpha = 0.18f))
                        .border(1.dp, AnimePurple.copy(alpha = 0.35f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Navigation, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "[ACTIVE_GPS_COUNTRY: $activeCountry]",
                                color = AnimeGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = if (AppLocaleStrings.isHindi(language)) {
                                "स्वचालित क्षेत्रीय भाषा: ${GpsLocationManager.getInstance(context).mapCountryToLanguage(activeCountry)} (मैन्युअल बदलाव कभी भी संभव)"
                            } else {
                                "Auto-Mapped Language: ${GpsLocationManager.getInstance(context).mapCountryToLanguage(activeCountry)} (Manual override always available)"
                            },
                            color = TextMuted,
                            fontSize = 10.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                // Primary Button: Grant All
                Button(
                    onClick = {
                        val toRequest = mutableListOf<String>()
                        if (!locationGranted) {
                            toRequest.add(Manifest.permission.ACCESS_FINE_LOCATION)
                            toRequest.add(Manifest.permission.ACCESS_COARSE_LOCATION)
                        }
                        if (!micGranted) {
                            toRequest.add(Manifest.permission.RECORD_AUDIO)
                        }

                        if (toRequest.isEmpty()) {
                            markHandled()
                            onDismiss()
                        } else {
                            multiplePermissionLauncher.launch(toRequest.toTypedArray())
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("grant_all_permissions_btn"),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = if (locationGranted && micGranted) Icons.Default.CheckCircle else Icons.Default.Place,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (locationGranted && micGranted) {
                            if (AppLocaleStrings.isHindi(language)) "स्टूडियो में प्रवेश करें" else "Enter Studio"
                        } else {
                            if (AppLocaleStrings.isHindi(language)) "गूगल मैप्स अनुमति दें व शुरू करें" else "Grant Maps Permission & Continue"
                        },
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Skip / Continue
                    TextButton(
                        onClick = {
                            markHandled()
                            onDismiss()
                        },
                        modifier = Modifier.testTag("skip_permissions_btn")
                    ) {
                        Text(
                            text = if (AppLocaleStrings.isHindi(language)) "अंग्रेजी डिफॉल्ट से जारी रखें" else "Continue with English Default",
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }

                    // Open App Settings
                    TextButton(
                        onClick = {
                            try {
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", context.packageName, null)
                                }
                                context.startActivity(intent)
                            } catch (_: Exception) {}
                        }
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Settings, contentDescription = null, tint = TextMuted, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (AppLocaleStrings.isHindi(language)) "सेटिंग्स" else "Settings",
                                color = TextMuted,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

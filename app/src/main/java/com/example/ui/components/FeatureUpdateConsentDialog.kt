package com.example.ui.components

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.NewReleases
import androidx.compose.material.icons.filled.SystemUpdateAlt
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.engine.FeatureUpdateRollout
import com.example.localization.AppLocaleStrings
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
 * FeatureUpdateConsentDialog
 * Mandatory User Consent Gate:
 * Whenever the Owner modifies options, turns on features, or releases updates
 * (either globally for random users or specifically for a designated person),
 * this dialog presents the update choice to the user.
 *
 * It prevents silent direct execution on the user's phone or computer,
 * ensuring the user always explicitly chooses whether to update now or later.
 */
@Composable
fun FeatureUpdateConsentDialog(
    rollout: FeatureUpdateRollout,
    lang: String = "hi",
    onAcceptUpdate: (String) -> Unit,
    onPostponeUpdate: (String) -> Unit
) {
    Dialog(
        onDismissRequest = { onPostponeUpdate(rollout.updateId) },
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("feature_update_consent_dialog"),
            shape = RoundedCornerShape(22.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurface),
            border = BorderStroke(1.5.dp, Brush.horizontalGradient(listOf(AnimeCyan, AnimePink)))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(
                            Brush.linearGradient(listOf(AnimeCyan, AnimePurple)),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.SystemUpdateAlt,
                        contentDescription = null,
                        tint = Color.Black,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title & Version Badge
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "✨ New Update Available", "✨ नया अपडेट उपलब्ध है"),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        color = AnimePink.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = rollout.versionTag,
                            color = AnimePink,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Target Indication
                val targetInfo = if (rollout.isGlobal) {
                    AppLocaleStrings.tr(lang, "Applies to: All Users", "सभी यूज़र्स के लिए अपडेट")
                } else {
                    AppLocaleStrings.tr(lang, "Custom Enabled for your account: ${rollout.targetUserId}", "आपके अकाउंट के लिए विशेष विकल्प: ${rollout.targetUserId}")
                }
                Text(
                    text = targetInfo,
                    color = AnimeGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Feature Title Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.7f)),
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = rollout.title,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = rollout.description,
                            color = TextSecondary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )

                        if (rollout.changelogItems.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "What's Included:", "इस अपडेट में क्या शामिल है:"),
                                color = AnimeCyan,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            rollout.changelogItems.forEach { item ->
                                Row(modifier = Modifier.padding(vertical = 2.dp), verticalAlignment = Alignment.Top) {
                                    Text("• ", color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    Text(item, color = TextMuted, fontSize = 11.sp)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Consent Notice
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Notice: Options enabled by the owner do NOT directly take effect without your consent. You have full choice over this device.",
                        "सूचना: ओनर द्वारा सक्रिय किया गया कोई भी विकल्प आपकी अनुमति के बिना सीधे आपके फोन में लागू नहीं होगा। आपके पास पूर्ण विकल्प है।"
                    ),
                    color = TextMuted,
                    fontSize = 10.sp,
                    lineHeight = 14.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { onPostponeUpdate(rollout.updateId) },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Later (Not Now)", "बाद में (Later)"),
                            color = TextMuted,
                            fontSize = 12.sp
                        )
                    }

                    Button(
                        onClick = { onAcceptUpdate(rollout.updateId) },
                        modifier = Modifier
                            .weight(1.3f)
                            .height(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLocaleStrings.tr(lang, "Update & Apply Now", "अपडेट करें और सक्रिय करें"),
                            color = Color.Black,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Hub
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
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
 * TrendSocialMediaIntegrationGuideDialog
 * Comprehensive manual and technical workflow explaining how the AI Agent learns from
 * and works with Google, YouTube, Instagram, Facebook, and open internet platforms.
 */
@Composable
fun TrendSocialMediaIntegrationGuideDialog(
    lang: String,
    onDismiss: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(dismissOnBackPress = true, dismissOnClickOutside = true, usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.96f)
                .fillMaxHeight(0.90f)
                .padding(vertical = 12.dp)
                .testTag("trend_social_media_guide_dialog"),
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
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(AnimeCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Hub, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(24.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Internet & Social Media AI Integration Guide", "इंटरनेट व सोशल मीडिया एआई एकीकरण गाइड"),
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                            Text(
                                text = "How TrendPulse AI researches Google, YouTube, Instagram & Meta",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Core Philosophy Banner
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimePurple.copy(alpha = 0.15f)),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
                ) {
                    Row(modifier = Modifier.padding(14.dp), verticalAlignment = Alignment.Top) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(22.dp))
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "Dual-Horizon Learning Engine (वर्तमान व भविष्य की समझ)",
                                color = AnimeGold,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "यह एआई एजेंट केवल वर्तमान में चल रहे ट्रेंड्स को ही नहीं देखता, बल्कि इंटरनेट डेटा, सर्च वॉल्यूम और एल्गोरिद्मिक पैटर्न का विश्लेषण करके यह भी भविष्यवाणी करता है कि अगले 7, 30 या 90 दिनों में कौन से एनिमे कॉन्सेप्ट्स और थीम्स वायरल होने वाली हैं।",
                                color = AnimeCyan,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step-by-Step Platform Protocols
                Text(
                    text = "1. How The AI Researches Across Platforms:",
                    color = AnimeGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                PlatformStepCard(
                    iconColor = Color(0xFF4285F4),
                    title = "Google Trends & Web Search Feeds",
                    summary = "एआई एजेंट Google Daily Search Trends, Hot Queries, और एनिमे कीवर्ड सर्च वॉल्यूम को स्कैन करता है। यह पता लगाता है कि भारत, जापान और अमेरिका में दर्शक सबसे ज्यादा किस तरह की कहानियों और पात्रों को सर्च कर रहे हैं।"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PlatformStepCard(
                    iconColor = Color(0xFFFF0000),
                    title = "YouTube Viral Shorts & Data API",
                    summary = "YouTube Shorts एल्गोरिदम में 60 सेकंड के वायरल क्लिप्स, हाई-सीटीआर थंबनेल आइडियाज, और ट्रेंडिंग एनिमे ऑडियो ट्रैक्स को स्कैन करता है। यह हुक (पहले 3 सेकंड) और क्लाइमेक्स पैटर्न की पहचान करता है।"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PlatformStepCard(
                    iconColor = Color(0xFFE1306C),
                    title = "Instagram Reels & TikTok Trending Sounds",
                    summary = "Reels पर ट्रेंड कर रहे ऑडियो बीट ड्रॉप्स, ट्रांसफॉर्मेशन एडिट्स, और वायरल हैशटैग्स (#AnimeReels, #KatanaParry) का विश्लेषण करता है। यह एनिमे वीडियो को उसी बीट और एस्थेटिक के अनुसार तैयार करने के निर्देश देता है।"
                )

                Spacer(modifier = Modifier.height(8.dp))

                PlatformStepCard(
                    iconColor = Color(0xFF1877F2),
                    title = "Facebook Communities & Reddit Fandoms",
                    summary = "Facebook एनिमे ग्रुप्स और r/anime / r/manga पर चल रही सबसे गरमागरम चर्चाओं, फैन थ्योरीज, और आगामी 2026/2027 सीजन के लीक हुए प्लॉट्स को समझता है।"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 2. Future Forecasting Mechanism
                Text(
                    text = "2. Future Trend Forecasting (भविष्य के ट्रेंड्स पर काम कैसे करता है?):",
                    color = AnimeCyan,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.3f))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        FutureWaveRow("⚡ Wave 1: Next 7 Days (अल्पावधि)", "वर्तमान में शुरू हो रहे माइक्रो-ट्रेंड्स जो अगले हफ्ते पीक पर पहुंचेंगे (जैसे नए एपिसोड की रिलीज के बाद आने वाले रिएक्शन्स)।")
                        Spacer(modifier = Modifier.height(8.dp))
                        FutureWaveRow("🔮 Wave 2: Next 30 Days (मध्यावधि)", "नए जॉनर शिफ्ट्स—जैसे डार्क फैंटेसी से साइबरपंक की ओर झुकाव, या वैदिक माइथोलॉजी व एनिमे क्रॉसओवर की बढ़ती मांग।")
                        Spacer(modifier = Modifier.height(8.dp))
                        FutureWaveRow("🚀 Wave 3: Next 90+ Days (दीर्घावधि)", "आगामी त्योहार, सीजनल इवेंट्स, और नए ग्लोबल एनिमे एडेप्टेशन्स से पहले ही एपिसोड्स और सीरीज का प्री-प्रोडक्शन तैयार करना।")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 3. How Owner Decides When, Where & How to Use
                Text(
                    text = "3. Owner Decides When, Where & How to Use (ओनर के अधिकार):",
                    color = AnimePink,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("• Active / Deactive Switch: आप किसी भी समय एआई एजेंट को ऑन या ऑफ कर सकते हैं।", color = TextPrimary, fontSize = 11.sp)
                        Text("• When to Scan: लगातार लाइव स्कैन (Continuous), दैनिक ब्रीफिंग (Daily Digest), या आवश्यकतानुसार (On-Demand)।", color = TextPrimary, fontSize = 11.sp)
                        Text("• Where to Deploy: 1-क्लिक में स्टूडियो वर्कस्पेस (Studio) में भेजें या सीधे यूट्यूब मल्टी-चैनल ऑटोमेशन (YouTube Automation) में शेड्यूल करें।", color = TextPrimary, fontSize = 11.sp)
                        Text("• How to Guide: अपने कस्टम कीवर्ड्स और पसंदीदा प्लेटफॉर्म्स को चुनकर एआई के रिसर्च फोकस को मनचाहा आकार दें।", color = TextPrimary, fontSize = 11.sp)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Got It! Close Guide (समझ गया)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun PlatformStepCard(iconColor: Color, title: String, summary: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(0.5.dp, iconColor.copy(alpha = 0.5f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.Top) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(iconColor)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(title, color = iconColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(3.dp))
                Text(summary, color = TextSecondary, fontSize = 11.sp, lineHeight = 16.sp)
            }
        }
    }
}

@Composable
private fun FutureWaveRow(title: String, desc: String) {
    Column {
        Text(title, color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(2.dp))
        Text(desc, color = TextPrimary, fontSize = 11.sp, lineHeight = 15.sp)
    }
}

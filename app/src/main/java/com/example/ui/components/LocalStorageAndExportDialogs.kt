package com.example.ui.components
import com.example.localization.AppLocaleStrings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Upload
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SavedScriptEntity
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeCyanLight
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.AnimeSurface
import com.example.ui.theme.AnimeSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExportShareSheetDialog(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val script = state.currentScript
    var shareMode by remember { mutableStateOf("DETAILS_AND_LINK") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                            .background(AnimePurple.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(language, "Export & Share", "एक्सपोर्ट व शेयर (Export & Share)"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = script?.title ?: AppLocaleStrings.tr(language, "Anime Project", "एनिमे प्रोजेक्ट"),
                            fontSize = 11.sp,
                            color = AnimeCyanLight,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (script == null) {
                    Text(AppLocaleStrings.tr(language, "No active anime project available.", "कोई सक्रिय एनिमे प्रोजेक्ट उपलब्ध नहीं है।"), color = TextSecondary)
                } else {
                    // Project Info Badge
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "${script.genre} • ${script.artStyle}",
                                    color = AnimeGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${script.scenes.size} सीन्स • भाषा: ${script.language} (Voice: ${script.voiceoverLanguage})",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(AnimeCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text("Ready to Share", color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Generated Video Link Card
                    val videoUrl = viewModel.getVideoShareUrl(script)
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = AppLocaleStrings.tr(language, "▶️ Generated Video Link:", "▶️ जनरेटेड वीडियो लिंक (Watch Link):"),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AnimeCyan
                                )
                                IconButton(
                                    onClick = { viewModel.copyProjectLinkToClipboard(context, script) },
                                    modifier = Modifier.size(28.dp).testTag("dialog_copy_video_link_btn")
                                ) {
                                    Icon(
                                        Icons.Default.ContentCopy,
                                        contentDescription = "Copy Link",
                                        tint = AnimeGold,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Text(
                                text = videoUrl,
                                fontSize = 10.sp,
                                color = TextSecondary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Share Mode Selector
                    Text(
                        text = AppLocaleStrings.tr(language, "📋 Choose Share Format:", "📋 शेयरिंग फॉर्मेट चुनें (Share Format Mode):"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeGold
                    )
                    Spacer(modifier = Modifier.height(4.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        FilterChip(
                            selected = shareMode == "DETAILS_AND_LINK",
                            onClick = { shareMode = "DETAILS_AND_LINK" },
                            label = { Text("🎬 विवरण + लिंक", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f).testTag("share_mode_details_chip")
                        )
                        FilterChip(
                            selected = shareMode == "LINK_ONLY",
                            onClick = { shareMode = "LINK_ONLY" },
                            label = { Text("🔗 केवल लिंक", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f).testTag("share_mode_link_chip")
                        )
                        FilterChip(
                            selected = shareMode == "FULL_SCRIPT",
                            onClick = { shareMode = "FULL_SCRIPT" },
                            label = { Text("📜 पूरी स्क्रिप्ट", fontSize = 10.sp) },
                            modifier = Modifier.weight(1f).testTag("share_mode_script_chip")
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Primary Download Button
                    Button(
                        onClick = {
                            onDismiss()
                            viewModel.toggleDownloadDialog(true)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("action_open_download_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGold),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("⬇️ डिवाइस में डाउनलोड करें (Download Video/Script)", color = Color.Black, fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Option 1: Native Share via ACTION_SEND (All Installed Apps: WhatsApp, Telegram, X, Discord, etc.)
                    Button(
                        onClick = {
                            viewModel.shareProject(context, "ALL", shareMode, script)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_share_apps_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppLocaleStrings.tr(language, "📲 Share to All Installed Apps", "📲 सभी इंस्टॉल ऐप्स पर शेयर करें (System Share Sheet)"), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Social Media Direct Integration Options
                    Text(
                        text = AppLocaleStrings.tr(language, "🌐 Direct Social Media Share:", "🌐 डायरेक्ट सोशल मीडिया शेयर (Direct Social Media Share):"),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeCyanLight
                    )
                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 1: WhatsApp & YouTube
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.shareProject(context, "WHATSAPP", shareMode, script) },
                            modifier = Modifier.weight(1f).height(42.dp).testTag("dialog_share_whatsapp"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF25D366)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("💬 WhatsApp", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.shareProject(context, "YOUTUBE", shareMode, script) },
                            modifier = Modifier.weight(1f).height(42.dp).testTag("dialog_share_youtube"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF0000)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("▶️ YouTube", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 2: Instagram & Snapchat
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = { viewModel.shareProject(context, "INSTAGRAM", shareMode, script) },
                            modifier = Modifier.weight(1f).height(42.dp).testTag("dialog_share_instagram"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE1306C)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("📸 Instagram", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }

                        Button(
                            onClick = { viewModel.shareProject(context, "SNAPCHAT", shareMode, script) },
                            modifier = Modifier.weight(1f).height(42.dp).testTag("dialog_share_snapchat"),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFFC00)),
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(2.dp)
                        ) {
                            Text("👻 Snapchat", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Row 3: Google (Drive/Gmail/Docs)
                    Button(
                        onClick = { viewModel.shareProject(context, "GOOGLE", shareMode, script) },
                        modifier = Modifier.fillMaxWidth().height(40.dp).testTag("dialog_share_google"),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4)),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(2.dp)
                    ) {
                        Text("📁 Google (Drive / Gmail / Docs)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // SRT Subtitle Export
                    OutlinedButton(
                        onClick = {
                            val path = viewModel.exportScriptToSrt()
                            Toast.makeText(context, "SRT सबटाइटल्स फाइल सेव हो गई!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_export_srt_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Subtitles, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppLocaleStrings.tr(language, "📑 Export SRT Subtitles (.srt File)", "📑 SRT सबटाइटल्स एक्सपोर्ट करें (.srt File)"), color = AnimeCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 2: Save to Local Storage Database (Room)
                    Button(
                        onClick = {
                            viewModel.saveCurrentScriptToLocalStorage()
                            Toast.makeText(context, "लोकल डेटाबेस में सुरक्षित सेव हो गया!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_save_local_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.85f)),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("💾 लोकल स्टोरेज में सेव करें (Save to SQLite)", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 3: Export Formatted Script (.txt file)
                    OutlinedButton(
                        onClick = {
                            val path = viewModel.exportScriptToFile(asJson = false)
                            Toast.makeText(context, "स्क्रिप्ट फाइल एक्सपोर्ट हो गई!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_export_txt_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppLocaleStrings.tr(language, "📄 Export Text Script (.txt File)", "📄 टेक्स्ट स्क्रिप्ट एक्सपोर्ट करें (.txt File)"), color = AnimePink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 4: Export Full Project Backup (.json file)
                    OutlinedButton(
                        onClick = {
                            val path = viewModel.exportScriptToFile(asJson = true)
                            Toast.makeText(context, "प्रोजेक्ट JSON बैकअप एक्सपोर्ट हो गया!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_export_json_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppLocaleStrings.tr(language, "📦 Export Project Backup (.json File)", "📦 प्रोजेक्ट बैकअप एक्सपोर्ट करें (.json File)"), color = AnimeGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Option 5: Copy Formatted Script to Clipboard
                    OutlinedButton(
                        onClick = {
                            val text = viewModel.getScriptAsFormattedText()
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("Anime Script", text))
                            Toast.makeText(context, "📋 पूरी स्क्रिप्ट क्लिपबोर्ड में कॉपी हो गई!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(46.dp)
                            .testTag("action_copy_clipboard_btn"),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(AppLocaleStrings.tr(language, "📋 Copy Full Script to Clipboard", "📋 पूरी स्क्रिप्ट क्लिपबोर्ड में कॉपी करें"), color = AnimeCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }

                    // Status / Feedback Card
                    if (state.exportFeedbackMessage.isNotBlank()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AnimePurple.copy(alpha = 0.2f))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(state.exportFeedbackMessage, color = AnimeCyanLight, fontSize = 11.sp)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(AppLocaleStrings.tr(language, "Done", "पूर्ण (Done)"), color = AnimeCyan, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = AnimeSurface,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
fun LocalStorageVaultDialog(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val savedScripts by viewModel.savedScriptsState.collectAsState()
    val context = LocalContext.current

    var isImportBoxExpanded by remember { mutableStateOf(false) }
    var importJsonText by remember { mutableStateOf("") }
    val dateFormat = remember { SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault()) }

    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri ->
        if (uri != null) {
            viewModel.importScriptFromUri(uri)
            Toast.makeText(context, "फाइल अपलोड की जा रही है...", Toast.LENGTH_SHORT).show()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                            .background(AnimeCyan.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Storage, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(state.selectedLanguage, "Local Storage Vault", "लोकल स्टोरेज वॉल्ट"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Offline SQLite Room Database",
                            fontSize = 10.sp,
                            color = AnimeCyanLight
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Storage Stats Bar
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${savedScripts.size}", color = AnimeCyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(AppLocaleStrings.tr(state.selectedLanguage, "Projects", "प्रोजेक्ट्स"), color = TextSecondary, fontSize = 10.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(AnimePurple.copy(alpha = 0.4f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${state.customCharacters.size}", color = AnimePink, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(AppLocaleStrings.tr(state.selectedLanguage, "Characters", "करैक्टर"), color = TextSecondary, fontSize = 10.sp)
                        }
                        Box(modifier = Modifier.width(1.dp).height(24.dp).background(AnimePurple.copy(alpha = 0.4f)))
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("${state.savedVisualElements.size}", color = AnimeGold, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                            Text(AppLocaleStrings.tr(state.selectedLanguage, "Visuals", "विजुअल्स"), color = TextSecondary, fontSize = 10.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Actions: Quick Save Current & Import
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(
                        onClick = {
                            viewModel.saveCurrentScriptToLocalStorage()
                            Toast.makeText(context, "सक्रिय प्रोजेक्ट लोकल स्टोरेज में सेव हो गया!", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("vault_save_current_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(Icons.Default.Save, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(AppLocaleStrings.tr(state.selectedLanguage, "Save Project", "प्रोजेक्ट सेव करें"), fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = { isImportBoxExpanded = !isImportBoxExpanded },
                        modifier = Modifier.weight(1f).height(38.dp).testTag("vault_import_btn"),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.6f))
                    ) {
                        Icon(Icons.Default.Upload, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(if (isImportBoxExpanded) "बंद करें" else "इम्पोर्ट JSON", fontSize = 11.sp, color = AnimeCyan, fontWeight = FontWeight.Bold)
                    }
                }

                // Expandable Import Box
                AnimatedVisibility(visible = isImportBoxExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(AnimeSurfaceVariant)
                            .padding(10.dp)
                    ) {
                        Text("बैकअप JSON पेस्ट करें:", color = AnimeCyanLight, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(6.dp))
                        OutlinedTextField(
                            value = importJsonText,
                            onValueChange = { importJsonText = it },
                            placeholder = { Text("{\"id\": ... \"title\": ...}", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("import_json_input"),
                            minLines = 2,
                            maxLines = 4,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = AnimeCyan,
                                focusedTextColor = TextPrimary,
                                unfocusedTextColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        if (state.importError.isNotBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(state.importError, color = AnimePink, fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Button(
                            onClick = {
                                if (importJsonText.isNotBlank()) {
                                    val ok = viewModel.importScriptFromJson(importJsonText)
                                    if (ok) {
                                        importJsonText = ""
                                        isImportBoxExpanded = false
                                        Toast.makeText(context, "सफलतापूर्वक इम्पोर्ट हो गया!", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("submit_import_btn"),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.85f)),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(AppLocaleStrings.tr(state.selectedLanguage, "Import to Database", "डेटाबेस में इम्पोर्ट करें"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Upload from Device Storage
                        OutlinedButton(
                            onClick = { filePickerLauncher.launch("*/*") },
                            modifier = Modifier.fillMaxWidth().height(36.dp).testTag("vault_upload_file_btn"),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.7f))
                        ) {
                            Icon(Icons.Default.FileUpload, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("📂 डिवाइस से फाइल अपलोड करें (.json/.txt)", color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = "सेव्ड प्रोजेक्ट्स सूची (${savedScripts.size}):",
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Saved Scripts List
                if (savedScripts.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Folder, contentDescription = null, tint = TextMuted, modifier = Modifier.size(40.dp))
                            Spacer(modifier = Modifier.height(6.dp))
                            Text("लोकल स्टोरेज में कोई प्रोजेक्ट सुरक्षित नहीं है।", color = TextMuted, fontSize = 11.sp)
                            Text("ऊपर 'प्रोजेक्ट सेव करें' बटन दबाएं।", color = AnimeCyanLight, fontSize = 10.sp)
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .heightIn(max = 280.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(savedScripts, key = { it.id }) { item ->
                            SavedScriptItemCard(
                                item = item,
                                dateFormat = dateFormat,
                                onLoad = {
                                    viewModel.loadSavedScript(item)
                                    viewModel.setTab(AppTab.PLAYER)
                                    onDismiss()
                                },
                                onDelete = {
                                    viewModel.deleteSavedScript(item.id)
                                    Toast.makeText(context, "प्रोजेक्ट हटा दिया गया", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(AppLocaleStrings.tr(state.selectedLanguage, "Close", "बंद करें"), color = AnimeCyan)
            }
        },
        containerColor = AnimeSurface,
        shape = RoundedCornerShape(18.dp)
    )
}

@Composable
private fun SavedScriptItemCard(
    item: SavedScriptEntity,
    dateFormat: SimpleDateFormat,
    onLoad: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.25f))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = dateFormat.format(Date(item.createdAt)),
                    color = TextMuted,
                    fontSize = 9.sp
                )
            }
            Spacer(modifier = Modifier.height(2.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalAlignment = Alignment.CenterVertically) {
                Text(item.genre, color = AnimeGold, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Text("•", color = TextMuted, fontSize = 10.sp)
                Text(item.artStyle, color = AnimeCyanLight, fontSize = 10.sp)
                Text("•", color = TextMuted, fontSize = 10.sp)
                Text(item.language, color = AnimePink, fontSize = 10.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = item.synopsis,
                color = TextSecondary,
                fontSize = 10.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(30.dp)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = AnimePink, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(6.dp))
                Button(
                    onClick = onLoad,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.85f)),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(AppLocaleStrings.tr(state.selectedLanguage, "Play in Player", "प्लेयर में चलाएं"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun DownloadProjectDialog(
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val script = state.currentScript

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
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
                            .background(AnimeGold.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.FileDownload, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = AppLocaleStrings.tr(language, "📥 Download & Save", "📥 डाउनलोड व सेव (Download)"),
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = AppLocaleStrings.tr(language, "Save project to phone Downloads folder", "प्रोजेक्ट को फोन के Downloads फोल्डर में सेव करें"),
                            fontSize = 10.sp,
                            color = AnimeCyanLight
                        )
                    }
                }
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState())
            ) {
                if (script == null) {
                    Text("डाउनलोड करने के लिए कोई सक्रिय स्क्रिप्ट नहीं है।", color = TextSecondary)
                } else {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = script.title,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Text(
                                text = "${script.genre} • ${script.scenes.size} सीन • भाषा: ${state.selectedLanguage}",
                                color = AnimeCyanLight,
                                fontSize = 11.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    if (state.isDownloadingFile) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(AnimePurple.copy(alpha = 0.2f))
                                .padding(12.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("डाउनलोड हो रहा है...", color = AnimeCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            Spacer(modifier = Modifier.height(8.dp))
                            LinearProgressIndicator(
                                progress = { state.downloadProgress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp)
                                    .clip(RoundedCornerShape(3.dp)),
                                color = AnimeCyan,
                                trackColor = Color.White.copy(alpha = 0.2f)
                            )
                        }
                        Spacer(modifier = Modifier.height(12.dp))
                    }

                    // Download Option 1: Full Project JSON
                    Button(
                        onClick = {
                            viewModel.downloadProjectFile("JSON")
                            Toast.makeText(context, "प्रोजेक्ट डाउनलोड शुरू हो गया...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("download_json_btn"),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Icon(Icons.Default.Archive, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("वीडियो प्रोजेक्ट डाउनलोड करें (.JSON)", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("सीन टाइमिंग, करैक्टर आवाज़ और विजुअल प्रॉम्प्ट्स", color = Color.White.copy(alpha = 0.7f), fontSize = 9.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Download Option 2: Formatted Screenplay TXT
                    OutlinedButton(
                        onClick = {
                            viewModel.downloadProjectFile("TXT")
                            Toast.makeText(context, "स्क्रीनप्ले टेक्स्ट डाउनलोड शुरू...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("download_txt_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AnimePink.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AnimePink, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("स्क्रीनप्ले स्क्रिप्ट डाउनलोड करें (.TXT)", color = AnimePink, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("रीडिंग व डबिंग के लिए पूर्ण डायलॉग शीट", color = TextSecondary, fontSize = 9.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Download Option 3: SRT Subtitles
                    OutlinedButton(
                        onClick = {
                            viewModel.downloadProjectFile("SRT")
                            Toast.makeText(context, "सबटाइटल डाउनलोड शुरू...", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("download_srt_btn"),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.7f))
                    ) {
                        Icon(Icons.Default.Subtitles, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("सबटाइटल डाउनलोड करें (.SRT)", color = AnimeCyan, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            Text("वीडियो एडिटर्स (Premiere, CapCut) के लिए टाइमकोड", color = TextSecondary, fontSize = 9.sp)
                        }
                    }

                    if (state.lastDownloadedFileName.isNotBlank()) {
                        Spacer(modifier = Modifier.height(14.dp))
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(AnimeCyan.copy(alpha = 0.15f))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Check, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "सेव किया गया: ${state.lastDownloadedFileName}",
                                    color = AnimeCyanLight,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("बंद करें", color = AnimeCyan, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = AnimeSurface,
        shape = RoundedCornerShape(18.dp)
    )
}

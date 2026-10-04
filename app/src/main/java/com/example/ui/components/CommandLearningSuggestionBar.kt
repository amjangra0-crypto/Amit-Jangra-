package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.LearnedCommandRecord
import com.example.data.model.ManagedUserAccess
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Adaptive AI Command Learning, Auto-Improvement & Smart Suggestion Bar.
 *
 * Appears below prompt/command input fields in Studio, Director, and Creation hubs.
 * - Learns from entered commands.
 * - Autonomously improves raw commands into cinematic anime prompts.
 * - Generates creative variations/suggestions that can be tapped to apply.
 * - Strictly guarded: Only Owner by default.
 * - Owner can activate/deactivate for themselves, globally for all other users,
 *   or specifically for chosen individual users.
 */
@Composable
fun CommandLearningSuggestionBar(
    viewModel: AnimeViewModel,
    currentRawCommand: String,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val settings by viewModel.commandLearningSettings.collectAsState()
    val suggestions by viewModel.commandSuggestions.collectAsState()
    val lastImproved by viewModel.lastImprovedCommand.collectAsState()
    val isLearning by viewModel.isCommandLearningProcessing.collectAsState()
    val learnedRecords by viewModel.learnedCommands.collectAsState()
    val managedUsers by viewModel.managedCommandUsers.collectAsState()

    val lang = uiState.selectedLanguage
    val isAccessible = viewModel.isCommandLearningAccessibleForCurrentUser()

    var showOwnerControlDialog by remember { mutableStateOf(false) }
    var showHistoryDialog by remember { mutableStateOf(false) }
    var isExpandedImprovedPreview by remember { mutableStateOf(true) }

    // If neither Owner nor authorized by Owner, show locked notice or hide
    if (!isAccessible) {
        if (currentUser.isOwner) {
            // Owner has feature deactivated for themselves; offer quick re-activate button
            Card(
                modifier = modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, TextMuted.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Psychology, contentDescription = null, tint = TextMuted, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "AI Command Learning is paused for Owner",
                                "ओनर के लिए AI कमांड लर्निंग निष्क्रिय है"
                            ),
                            color = TextMuted,
                            fontSize = 11.sp
                        )
                    }
                    TextButton(onClick = { viewModel.setCommandLearningOwnerActive(true) }) {
                        Text(
                            text = AppLocaleStrings.tr(lang, "Activate", "चालू करें"),
                            color = AnimeGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
        return
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("command_learning_suggestion_bar")
    ) {
        // Main Toolbar Bar
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
            border = BorderStroke(
                1.dp,
                Brush.horizontalGradient(listOf(AnimePurple.copy(alpha = 0.6f), AnimeCyan.copy(alpha = 0.6f)))
            )
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                // Header with Learning Badge & Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(listOf(AnimePurple, AnimeCyan))
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = AppLocaleStrings.tr(
                                        lang,
                                        "Adaptive AI Command Intelligence",
                                        "एडैप्टिव AI कमांड इंटेलिजेंस"
                                    ),
                                    color = AnimeCyanLight,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(AnimeGold.copy(alpha = 0.2f))
                                        .padding(horizontal = 5.dp, vertical = 1.dp)
                                ) {
                                    Text(
                                        text = "LVL ${settings.learningLevel} • ${settings.totalCommandsAnalyzed} LEARNED",
                                        color = AnimeGold,
                                        fontSize = 8.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "Learns from your input & self-improves commands automatically",
                                    "आपकी कमांड से सीखकर उसे खुद बेहतर बनाता है"
                                ),
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // History & Insights button
                        IconButton(
                            onClick = { showHistoryDialog = true },
                            modifier = Modifier
                                .size(28.dp)
                                .testTag("command_learning_history_btn")
                        ) {
                            Icon(Icons.Default.History, contentDescription = "History", tint = AnimeCyan, modifier = Modifier.size(16.dp))
                        }

                        // Owner Controls Button (Only visible if Owner)
                        if (currentUser.isOwner) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(AnimeGold.copy(alpha = 0.15f))
                                    .border(1.dp, AnimeGold, RoundedCornerShape(8.dp))
                                    .clickable { showOwnerControlDialog = true }
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                                    .testTag("owner_command_learning_mgr_btn")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Security, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(12.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = AppLocaleStrings.tr(lang, "Owner Control", "ओनर नियंत्रण"),
                                        color = AnimeGold,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Row: ⚡ Self-Improve Button & Live Status
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            viewModel.learnAndImproveCurrentCommand(currentRawCommand)
                        },
                        enabled = !isLearning,
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = AnimePurple
                        ),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier
                            .height(34.dp)
                            .testTag("self_improve_command_btn")
                    ) {
                        if (isLearning) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(14.dp), strokeWidth = 2.dp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "Learning & Improving...", "सीख रहा है व सुधार रहा है..."),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = AppLocaleStrings.tr(lang, "⚡ Self-Improve Command", "⚡ कमांड खुद बेहतर बनाएं"),
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Tap to enhance with 4K lighting & direction",
                            "4K लाइटिंग व कैमरा मोशन के साथ सुधारें"
                        ),
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }

                // Display Last Improved Command if available
                if (!lastImproved.isNullOrBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
                        shape = RoundedCornerShape(10.dp),
                        border = BorderStroke(1.dp, AnimeGreen.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = AppLocaleStrings.tr(lang, "✨ AI Self-Improved Command:", "✨ AI द्वारा सुधारी गई कमांड:"),
                                        color = AnimeGreen,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Button(
                                        onClick = { viewModel.applyImprovedCommandToPrompt(lastImproved!!) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                                        shape = RoundedCornerShape(6.dp),
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                        modifier = Modifier.height(26.dp).testTag("apply_improved_command_btn")
                                    ) {
                                        Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(12.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = AppLocaleStrings.tr(lang, "Apply to Studio", "स्टूडियो में लगाएं"),
                                            color = Color.Black,
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = lastImproved!!,
                                color = TextPrimary,
                                fontSize = 11.sp,
                                lineHeight = 16.sp
                            )
                        }
                    }
                }

                // Creative Suggestions Strip ("उन commands के बारे में अलग suggestion दे")
                if (suggestions.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lightbulb, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "💡 Creative Suggestions for this Command (Tap to add):",
                                "💡 इस कमांड के विभिन्न सुझाव (जोड़ने के लिए टैप करें):"
                            ),
                            color = AnimeGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(suggestions) { sugg ->
                            SuggestionPillCard(
                                suggestionText = sugg,
                                onClick = { viewModel.applySuggestionToPrompt(sugg) }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal 1: Owner Command Learning Control Center (Activation / Deactivation & Per-User Access)
    if (showOwnerControlDialog && currentUser.isOwner) {
        OwnerCommandLearningControlDialog(
            viewModel = viewModel,
            settings = settings,
            managedUsers = managedUsers,
            lang = lang,
            onDismiss = { showOwnerControlDialog = false }
        )
    }

    // Modal 2: History & Insights Dialog
    if (showHistoryDialog) {
        CommandLearningHistoryDialog(
            learnedRecords = learnedRecords,
            lang = lang,
            onSelectCommand = { cmd ->
                viewModel.setPromptInput(cmd)
                showHistoryDialog = false
            },
            onDismiss = { showHistoryDialog = false }
        )
    }
}

@Composable
private fun SuggestionPillCard(
    suggestionText: String,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .clickable(onClick = onClick)
            .testTag("command_suggestion_chip"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = suggestionText,
                color = TextSecondary,
                fontSize = 11.sp,
                maxLines = 2,
                modifier = Modifier.widthIn(max = 240.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Icon(Icons.Default.Add, contentDescription = "Add", tint = AnimeCyan, modifier = Modifier.size(14.dp))
        }
    }
}

/**
 * Owner Control Dialog:
 * - Master Switch for Owner: Activate / Deactivate
 * - Global Switch for All Other Users: Activate / Deactivate
 * - User-Specific Granular Activation Manager (Add user by ID/Email, toggle individually)
 */
@Composable
fun OwnerCommandLearningControlDialog(
    viewModel: AnimeViewModel,
    settings: com.example.data.model.CommandLearningSettings,
    managedUsers: List<ManagedUserAccess>,
    lang: String,
    onDismiss: () -> Unit
) {
    var newUserIdInput by remember { mutableStateOf("") }
    var inputError by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(AnimeGold),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Security, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "👑 Owner Command AI Access Hub",
                            "👑 ओनर कमांड AI एक्सेस हब"
                        ),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AnimeGold
                    )
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "Manage learning activation for Owner & other users",
                            "ओनर व अन्य उपयोगकर्ताओं के लिए सुविधा सक्रिय/निष्क्रिय करें"
                        ),
                        fontSize = 10.sp,
                        color = TextMuted
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            ) {
                // 1. OWNER MASTER SWITCH
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimeGold.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "👑 Activate for Owner (Self)",
                                    "👑 स्वयं ओनर के लिए एक्टिवेट करें"
                                ),
                                color = AnimeGold,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (settings.isOwnerActive)
                                    AppLocaleStrings.tr(lang, "Active: Command learning & self-improvement ON", "सक्रिय: कमांड लर्निंग व सुधार चालू है")
                                else
                                    AppLocaleStrings.tr(lang, "Deactivated for Owner", "ओनर के लिए बंद है"),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = settings.isOwnerActive,
                            onCheckedChange = { viewModel.setCommandLearningOwnerActive(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = AnimeGold, checkedTrackColor = AnimeGold.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("owner_command_learning_self_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // 2. GLOBAL SWITCH FOR ALL OTHER USERS
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = AppLocaleStrings.tr(
                                    lang,
                                    "🌐 Enable Globally for All Users",
                                    "🌐 सभी अन्य उपयोगकर्ताओं के लिए चालू करें"
                                ),
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (settings.isGloballyActiveForOthers)
                                    AppLocaleStrings.tr(lang, "All public users can use command learning", "सभी उपयोगकर्ता इस सुविधा का उपयोग कर सकते हैं")
                                else
                                    AppLocaleStrings.tr(lang, "Restricted to Owner & specifically authorized users", "केवल ओनर व अधिकृत उपयोगकर्ताओं तक सीमित"),
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Switch(
                            checked = settings.isGloballyActiveForOthers,
                            onCheckedChange = { viewModel.setCommandLearningGlobalOthersActive(it) },
                            colors = SwitchDefaults.colors(checkedThumbColor = AnimeCyan, checkedTrackColor = AnimeCyan.copy(alpha = 0.3f)),
                            modifier = Modifier.testTag("owner_command_learning_global_switch")
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // 3. GRANULAR USER-BY-USER PERMISSION MANAGER
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "👤 Specific User Permissions (Grant / Revoke individually):",
                        "👤 विशिष्ट उपयोगकर्ता नियंत्रण (व्यक्तिगत रूप से चालू/बंद करें):"
                    ),
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Add User by ID/Email Input Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = newUserIdInput,
                        onValueChange = {
                            newUserIdInput = it
                            inputError = ""
                        },
                        placeholder = { Text(AppLocaleStrings.tr(lang, "Enter User ID or Email", "User ID या ईमेल दर्ज करें"), fontSize = 11.sp, color = TextMuted) },
                        singleLine = true,
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("grant_user_input_field"),
                        shape = RoundedCornerShape(8.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = AnimeCyan)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            if (newUserIdInput.isBlank()) {
                                inputError = "Enter valid ID"
                                return@Button
                            }
                            viewModel.setCommandLearningUserAccess(newUserIdInput.trim(), true)
                            newUserIdInput = ""
                        },
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        modifier = Modifier.height(48.dp).testTag("grant_user_submit_btn")
                    ) {
                        Text(AppLocaleStrings.tr(lang, "Grant", "अनुमति दें"), color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                if (inputError.isNotBlank()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = inputError, color = AnimePink, fontSize = 10.sp)
                }

                Spacer(modifier = Modifier.height(10.dp))

                // List of Managed Users
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    items(managedUsers, key = { it.userId }) { user ->
                        val isUserAuth = settings.authorizedUserIds.contains(user.userId.lowercase()) ||
                                settings.authorizedUserIds.contains(user.email.lowercase())
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp),
                            colors = CardDefaults.cardColors(containerColor = AnimeSurface)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(28.dp)
                                            .clip(CircleShape)
                                            .background(if (isUserAuth) AnimeGreen.copy(alpha = 0.2f) else AnimeSurfaceVariant),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            Icons.Default.Person,
                                            contentDescription = null,
                                            tint = if (isUserAuth) AnimeGreen else TextMuted,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Column {
                                        Text(
                                            text = user.displayName,
                                            color = TextPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "${user.email} • ${user.role}",
                                            color = TextMuted,
                                            fontSize = 10.sp,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Switch(
                                        checked = isUserAuth,
                                        onCheckedChange = { isChecked ->
                                            viewModel.setCommandLearningUserAccess(user.userId, isChecked)
                                        },
                                        colors = SwitchDefaults.colors(checkedThumbColor = AnimeGreen, checkedTrackColor = AnimeGreen.copy(alpha = 0.3f)),
                                        modifier = Modifier.testTag("toggle_user_${user.userId}")
                                    )
                                    IconButton(
                                        onClick = { viewModel.removeCommandLearningUser(user.userId) },
                                        modifier = Modifier.size(24.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGold)
            ) {
                Text(AppLocaleStrings.tr(lang, "Done", "पूर्ण"), color = Color.Black, fontWeight = FontWeight.Bold)
            }
        },
        containerColor = AnimeSurface
    )
}

/**
 * Dialog displaying history of learned commands, extracted genres, and quick re-use
 */
@Composable
fun CommandLearningHistoryDialog(
    learnedRecords: List<LearnedCommandRecord>,
    lang: String,
    onSelectCommand: (String) -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Psychology, contentDescription = null, tint = AnimePurple)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = AppLocaleStrings.tr(
                        lang,
                        "Learned Commands & AI Insights",
                        "सीखी गई कमांड्स व AI इनसाइट्स"
                    ),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            if (learnedRecords.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = AppLocaleStrings.tr(
                            lang,
                            "No commands learned yet. Start typing prompts in Studio to train your adaptive model!",
                            "अभी तक कोई कमांड नहीं सीखी गई। एआई को प्रशिक्षित करने के लिए स्टूडियो में प्रॉम्प्ट लिखें!"
                        ),
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(learnedRecords, key = { it.id }) { record ->
                        val dateStr = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault()).format(Date(record.timestamp))
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Column(modifier = Modifier.padding(10.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(AnimePurple.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(text = record.genre, color = AnimePurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(text = dateStr, color = TextMuted, fontSize = 9.sp)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Command: ${record.rawCommand}",
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                if (record.improvedCommand.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Enhanced: ${record.improvedCommand.take(120)}...",
                                        color = AnimeGreen,
                                        fontSize = 10.sp
                                    )
                                }
                                Spacer(modifier = Modifier.height(6.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(
                                        onClick = { onSelectCommand(record.improvedCommand.ifBlank { record.rawCommand }) },
                                        contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 8.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = AppLocaleStrings.tr(lang, "Use this prompt", "यह प्रॉम्प्ट लगाएं"),
                                            color = AnimeCyan,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = AnimePurple)
            ) {
                Text(AppLocaleStrings.tr(lang, "Close", "बंद करें"), color = Color.White)
            }
        },
        containerColor = AnimeSurface
    )
}

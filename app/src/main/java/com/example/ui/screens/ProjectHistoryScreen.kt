package com.example.ui.screens
import com.example.localization.AppLocaleStrings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.db.SavedScriptEntity
import com.example.data.model.AnimeScript
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.components.ProjectQuickVideoPreviewModal
import com.example.ui.components.ProjectThumbnailVideoPreview
import com.example.ui.theme.AnimeCyan
import com.example.ui.theme.AnimeGold
import com.example.ui.theme.AnimeGreen
import com.example.ui.theme.AnimePink
import com.example.ui.theme.AnimePurple
import com.example.ui.theme.TextMuted
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProjectHistoryScreen(viewModel: AnimeViewModel) {
    val savedScripts by viewModel.savedScriptsState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedGenreFilter by remember { mutableStateOf("All") }
    var sortOrder by remember { mutableStateOf("Newest") } // "Newest", "Oldest", "Alphabetical"

    var selectedEntityForResumeDetail by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var selectedEntityForQuickVideoPreview by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var selectedEntityForSrt by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var selectedEntityForJson by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var selectedEntityForShare by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<SavedScriptEntity?>(null) }

    val filteredProjects = remember(savedScripts, searchQuery, selectedGenreFilter, sortOrder) {
        var list = savedScripts

        // Search Filter
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            list = list.filter {
                it.title.lowercase(Locale.ROOT).contains(q) ||
                it.genre.lowercase(Locale.ROOT).contains(q) ||
                it.artStyle.lowercase(Locale.ROOT).contains(q) ||
                it.language.lowercase(Locale.ROOT).contains(q) ||
                it.synopsis.lowercase(Locale.ROOT).contains(q)
            }
        }

        // Genre Filter
        if (selectedGenreFilter != "All") {
            val g = selectedGenreFilter.lowercase(Locale.ROOT)
            list = list.filter { it.genre.lowercase(Locale.ROOT).contains(g) }
        }

        // Sort Order
        when (sortOrder) {
            "Oldest" -> list.sortedBy { it.createdAt }
            "Alphabetical" -> list.sortedBy { it.title.lowercase(Locale.ROOT) }
            else -> list.sortedByDescending { it.createdAt } // "Newest"
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("project_history_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(
                            Brush.linearGradient(
                                listOf(AnimePurple.copy(alpha = 0.3f), AnimeCyan.copy(alpha = 0.2f))
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.History,
                        contentDescription = "Project History",
                        tint = AnimeCyan,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "Project History",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeGreen.copy(alpha = 0.2f))
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "Room DB",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = AnimeGreen
                            )
                        }
                    }
                    Text(
                        text = "${savedScripts.size} ${AppLocaleStrings.tr(state.selectedLanguage, "projects saved in SQLite • Tap to resume", "प्रोजेक्ट्स SQLite Room में सुरक्षित • टैप करके रिज्यूम करें")}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Create New in Studio
            Button(
                onClick = { viewModel.setTab(AppTab.STUDIO) },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                shape = RoundedCornerShape(10.dp),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                modifier = Modifier.testTag("btn_new_project_history")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Create New", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("project_history_search_input"),
            placeholder = {
                Text(
                    "Search by title, genre, language or synopsis...",
                    color = TextMuted,
                    fontSize = 13.sp
                )
            },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = AnimeCyan)
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Close, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AnimeCyan,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            shape = RoundedCornerShape(12.dp),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Filter & Sort Row
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            val genres = listOf("All" to if (AppLocaleStrings.isHindi(state.selectedLanguage)) "सभी" else "All", "Action" to "Action", "Cyberpunk" to "Cyberpunk", "Fantasy" to "Fantasy", "Romance" to "Romance")
            genres.forEach { (key, label) ->
                FilterChip(
                    selected = selectedGenreFilter == key,
                    onClick = { selectedGenreFilter = key },
                    label = { Text(label, fontSize = 11.sp, fontWeight = FontWeight.Medium) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimePurple.copy(alpha = 0.25f),
                        selectedLabelColor = AnimePurple
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = selectedGenreFilter == key,
                        borderColor = MaterialTheme.colorScheme.outline,
                        selectedBorderColor = AnimePurple
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(30.dp)
                )
            }

            // Sort Toggle Button
            OutlinedButton(
                onClick = {
                    sortOrder = when (sortOrder) {
                        "Newest" -> "Oldest"
                        "Oldest" -> "Alphabetical"
                        else -> "Newest"
                    }
                },
                shape = RoundedCornerShape(8.dp),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.height(30.dp)
            ) {
                Icon(Icons.Default.Sort, contentDescription = "Sort", modifier = Modifier.size(14.dp), tint = AnimeGold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = when (sortOrder) {
                        "Oldest" -> if (AppLocaleStrings.isHindi(state.selectedLanguage)) "पुरातन" else "Oldest"
                        "Alphabetical" -> "A-Z"
                        else -> if (AppLocaleStrings.isHindi(state.selectedLanguage)) "नवीनतम" else "Newest"
                    },
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Project Grid / Empty State
        if (filteredProjects.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(0.92f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Movie,
                            contentDescription = null,
                            tint = AnimeGold,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedGenreFilter != "All") "कोई प्रोजेक्ट नहीं मिला" else "हिस्ट्री में कोई प्रोजेक्ट नहीं है",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "स्टूडियो में जाकर अपनी पसंद का एनिमे प्रॉम्प्ट लिखें। जनरेट होने पर वह स्वतः Room डेटाबेस में सुरक्षित हो जाएगा।",
                            fontSize = 12.sp,
                            color = TextMuted,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { viewModel.setTab(AppTab.STUDIO) },
                            colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("🎬 Create New Anime", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 280.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .testTag("project_history_grid")
            ) {
                items(filteredProjects, key = { it.id }) { entity ->
                    ProjectHistoryCard(
                        entity = entity,
                        viewModel = viewModel,
                        onCardClick = { selectedEntityForResumeDetail = entity },
                        onQuickVideoPreview = { selectedEntityForQuickVideoPreview = entity },
                        onResumeInStudio = { viewModel.resumeProjectInStudio(entity) },
                        onResumeInPlayer = { viewModel.resumeProjectInPlayer(entity) },
                        onViewSrt = { selectedEntityForSrt = entity },
                        onViewJson = { selectedEntityForJson = entity },
                        onShare = { selectedEntityForShare = entity },
                        onDelete = { itemToDelete = entity }
                    )
                }
            }
        }
    }

    // Modal: Comprehensive Resume & Storyboard Detail Sheet
    selectedEntityForResumeDetail?.let { entity ->
        val script = viewModel.getScriptFromJsonString(entity.scriptJson)
        val dateFormat = remember { SimpleDateFormat("dd MMMM, yyyy • hh:mm a", Locale.getDefault()) }
        val dateString = remember(entity.createdAt) {
            try { dateFormat.format(Date(entity.createdAt)) } catch (_: Exception) { "हाल ही में" }
        }

        AlertDialog(
            onDismissRequest = { selectedEntityForResumeDetail = null },
            modifier = Modifier.testTag("resume_project_dialog_${entity.id}"),
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(AppLocaleStrings.tr(state.selectedLanguage, "Resume Project", "रिज्यूम प्रोजेक्ट (Resume Work)"), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, color = AnimeCyan)
                        }
                        Text(
                            text = entity.title,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                    IconButton(onClick = { selectedEntityForResumeDetail = null }) {
                        Icon(Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Badge details
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimePurple.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(entity.genre, color = AnimePurple, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeGold.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(entity.artStyle, color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(AnimeCyan.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text("${entity.language} Dub", color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("📅 रिकॉर्डेड: $dateString", fontSize = 11.sp, color = TextMuted)

                    Spacer(modifier = Modifier.height(12.dp))

                    // Synopsis
                    Text(AppLocaleStrings.tr(state.selectedLanguage, "📖 Story Synopsis:", "📖 कहानी व सिनॉप्सिस (Synopsis):"), fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = entity.synopsis.ifBlank { "No synopsis available" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        lineHeight = 18.sp
                    )

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(14.dp))

                    // Scenes List Preview
                    if (script != null && script.scenes.isNotEmpty()) {
                        Text("${AppLocaleStrings.tr(state.selectedLanguage, "🎬 Storyboard Scenes", "🎬 सीन्स की सूची")} (${script.scenes.size}):", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Spacer(modifier = Modifier.height(6.dp))
                        script.scenes.forEach { scene ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = "${AppLocaleStrings.tr(state.selectedLanguage, "Scene", "सीन")} ${scene.sceneNumber}: ${scene.title}",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AnimeCyan
                                        )
                                        Text(
                                            text = "${scene.durationSec}s • ${scene.atmosphericEffect}",
                                            fontSize = 10.sp,
                                            color = TextMuted
                                        )
                                    }
                                    if (scene.dialogues.isNotEmpty()) {
                                        Spacer(modifier = Modifier.height(4.dp))
                                        val firstD = scene.dialogues.first()
                                        Text(
                                            text = "${firstD.characterName}: \"${firstD.text}\"",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Quick Actions inside Detail
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                selectedEntityForResumeDetail = null
                                selectedEntityForQuickVideoPreview = entity
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("🎬 Preview", fontSize = 11.sp, color = AnimeCyan)
                        }
                        OutlinedButton(
                            onClick = {
                                selectedEntityForResumeDetail = null
                                selectedEntityForSrt = entity
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("📑 Subtitles", fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = {
                                selectedEntityForResumeDetail = null
                                selectedEntityForShare = entity
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("📤 Share", fontSize = 11.sp)
                        }
                    }
                }
            },
            confirmButton = {
                // Resume in Studio Button
                Button(
                    onClick = {
                        selectedEntityForResumeDetail = null
                        viewModel.resumeProjectInStudio(entity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("modal_resume_studio_btn")
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Resume in Studio", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                // Resume in Player Button
                Button(
                    onClick = {
                        selectedEntityForResumeDetail = null
                        viewModel.resumeProjectInPlayer(entity)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimePurple),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("modal_resume_player_btn")
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Play in Player", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        )
    }

    // Modal: Fullscreen / Quick Video Preview Modal
    selectedEntityForQuickVideoPreview?.let { entity ->
        ProjectQuickVideoPreviewModal(
            entity = entity,
            viewModel = viewModel,
            onDismiss = { selectedEntityForQuickVideoPreview = null },
            onResumeInStudio = {
                selectedEntityForQuickVideoPreview = null
                viewModel.resumeProjectInStudio(entity)
            },
            onResumeInPlayer = {
                selectedEntityForQuickVideoPreview = null
                viewModel.resumeProjectInPlayer(entity)
            }
        )
    }

    // Modal: SRT Subtitles
    selectedEntityForSrt?.let { entity ->
        val script = viewModel.getScriptFromJsonString(entity.scriptJson)
        val srtContent = if (script != null) viewModel.generateSrtForScript(script) else "SRT Data unavailable"

        AlertDialog(
            onDismissRequest = { selectedEntityForSrt = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Subtitles, contentDescription = null, tint = AnimeCyan)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${AppLocaleStrings.tr(state.selectedLanguage, "SRT Subtitles", "SRT सबटाइटल्स")}: ${entity.title}", fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = srtContent,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("SRT Subtitles", srtContent))
                        Toast.makeText(context, AppLocaleStrings.tr(state.selectedLanguage, "SRT subtitles copied to clipboard!", "SRT सबटाइटल्स क्लिपबोर्ड में कॉपी हो गए!"), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEntityForSrt = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal: JSON Data
    selectedEntityForJson?.let { entity ->
        AlertDialog(
            onDismissRequest = { selectedEntityForJson = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Description, contentDescription = null, tint = AnimeGold)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("${AppLocaleStrings.tr(state.selectedLanguage, "JSON Project Backup", "JSON प्रोजेक्ट बैकअप")}: ${entity.title}", fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(280.dp)
                        .verticalScroll(rememberScrollState())
                        .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = entity.scriptJson,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("JSON Data", entity.scriptJson))
                        Toast.makeText(context, AppLocaleStrings.tr(state.selectedLanguage, "JSON copied to clipboard!", "JSON डेटा क्लिपबोर्ड में कॉपी हो गया!"), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeGold)
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Copy JSON", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedEntityForJson = null }) {
                    Text("Close")
                }
            }
        )
    }

    // Modal: Social Share Dialog
    selectedEntityForShare?.let { entity ->
        val script = viewModel.getScriptFromJsonString(entity.scriptJson)
        var historyShareMode by remember { mutableStateOf("DETAILS_AND_LINK") }

        AlertDialog(
            onDismissRequest = { selectedEntityForShare = null },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Share, contentDescription = null, tint = AnimePink)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Share to Social Media", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    Text(
                        text = AppLocaleStrings.tr(state.selectedLanguage, "Share '${entity.title}' directly to social media:", "'${entity.title}' को सीधे प्लेटफॉर्म पर पोस्ट व शेयर करें:"),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (script != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        val videoUrl = viewModel.getVideoShareUrl(script)
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                            shape = RoundedCornerShape(8.dp),
                            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.4f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 10.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(AppLocaleStrings.tr(state.selectedLanguage, "▶️ Video Link:", "▶️ वीडियो लिंक (Video Link):"), fontSize = 10.sp, color = AnimeCyan, fontWeight = FontWeight.Bold)
                                    Text(videoUrl, fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                }
                                IconButton(
                                    onClick = { viewModel.copyProjectLinkToClipboard(context, script) },
                                    modifier = Modifier.size(28.dp).testTag("history_copy_video_link_btn")
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Link", tint = AnimeGold, modifier = Modifier.size(15.dp))
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = historyShareMode == "DETAILS_AND_LINK",
                                onClick = { historyShareMode = "DETAILS_AND_LINK" },
                                label = { Text("🎬 Details+Link", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f).testTag("history_share_mode_details")
                            )
                            FilterChip(
                                selected = historyShareMode == "LINK_ONLY",
                                onClick = { historyShareMode = "LINK_ONLY" },
                                label = { Text("🔗 Link Only", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f).testTag("history_share_mode_link")
                            )
                            FilterChip(
                                selected = historyShareMode == "FULL_SCRIPT",
                                onClick = { historyShareMode = "FULL_SCRIPT" },
                                label = { Text("📜 Script", fontSize = 10.sp) },
                                modifier = Modifier.weight(1f).testTag("history_share_mode_script")
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    val platforms = listOf(
                        Triple("💬 WhatsApp Chat & Status", "whatsapp", Color(0xFF25D366)),
                        Triple("📸 Instagram Reels & Stories", "instagram", Color(0xFFE1306C)),
                        Triple("▶️ YouTube Shorts & Video", "youtube", Color(0xFFFF0000)),
                        Triple("👻 Snapchat Spotlight", "snapchat", Color(0xFFFFFC00)),
                        Triple("📁 Google Drive / Docs / Gmail", "google", Color(0xFF4285F4)),
                        Triple("📲 Share to All Apps", "ALL", AnimePurple)
                    )

                    platforms.forEach { (name, platformKey, brandColor) ->
                        Button(
                            onClick = {
                                viewModel.shareProject(context, platformKey, historyShareMode, script)
                                selectedEntityForShare = null
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = brandColor),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 3.dp)
                                .testTag("history_share_${platformKey.lowercase()}")
                        ) {
                            Text(
                                text = name,
                                color = if (platformKey == "snapchat") Color.Black else Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { selectedEntityForShare = null }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Modal: Delete Confirmation
    itemToDelete?.let { entity ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = { Text("Delete Project?", fontWeight = FontWeight.Bold) },
            text = { Text(AppLocaleStrings.tr(state.selectedLanguage, "'${entity.title}' will be permanently removed from SQLite database.", "'${entity.title}' को Room SQLite डेटाबेस से हमेशा के लिए हटा दिया जाएगा।")) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSavedScript(entity.id)
                        itemToDelete = null
                        Toast.makeText(context, "प्रोजेक्ट हटा दिया गया", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC2626))
                ) {
                    Text("Delete", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun ProjectHistoryCard(
    entity: SavedScriptEntity,
    viewModel: AnimeViewModel,
    onCardClick: () -> Unit,
    onQuickVideoPreview: () -> Unit,
    onResumeInStudio: () -> Unit,
    onResumeInPlayer: () -> Unit,
    onViewSrt: () -> Unit,
    onViewJson: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = remember { SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()) }
    val sceneCount = remember(entity.scriptJson) {
        val count = Regex("\"sceneNumber\"").findAll(entity.scriptJson).count()
        if (count > 0) count else 3
    }
    val formattedDate = remember(entity.createdAt) {
        try {
            dateFormat.format(Date(entity.createdAt))
        } catch (_: Exception) {
            "हाल ही में"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCardClick() }
            .testTag("project_history_item_${entity.id}"),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Genre & Art Style Badges + Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimePurple.copy(alpha = 0.2f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = entity.genre.take(15),
                            color = AnimePurple,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeGold.copy(alpha = 0.18f))
                            .padding(horizontal = 7.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = entity.artStyle.take(16),
                            color = AnimeGold,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 16:9 Anime Thumbnail and Interactive Video Preview Component
            ProjectThumbnailVideoPreview(
                entity = entity,
                viewModel = viewModel,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 2.dp),
                onOpenFullPreview = onQuickVideoPreview
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Project Title
            Text(
                text = entity.title,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Synopsis
            Text(
                text = entity.synopsis.ifBlank { entity.originalPrompt },
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Metadata Row: Scenes & Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "🎬 ${sceneCount} scenes • ${entity.language}",
                    fontSize = 11.sp,
                    color = AnimeCyan,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = formattedDate,
                    fontSize = 10.sp,
                    color = TextMuted
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quick Tools Row: SRT, JSON, Video Preview, Share, Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onViewSrt() }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📑 SRT", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AnimeCyan)
                }

                Box(
                    modifier = Modifier
                        .weight(0.9f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .clickable { onViewJson() }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("{ } JSON", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AnimeGold)
                }

                // Dedicated Quick Video Preview Button
                Box(
                    modifier = Modifier
                        .weight(1.2f)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeCyan.copy(alpha = 0.16f))
                        .border(1.dp, AnimeCyan.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                        .clickable { onQuickVideoPreview() }
                        .padding(vertical = 5.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Preview", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AnimeCyan)
                    }
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimePink.copy(alpha = 0.2f))
                        .clickable { onShare() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Share, contentDescription = "Share", tint = AnimePink, modifier = Modifier.size(14.dp))
                }

                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(AnimeCyan.copy(alpha = 0.2f))
                        .clickable { onCardClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Visibility, contentDescription = "Details", tint = AnimeCyan, modifier = Modifier.size(14.dp))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Dual Action Buttons: Resume in Studio & Play in Player
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Resume Work in Studio
                Button(
                    onClick = onResumeInStudio,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(1.2f)
                        .height(38.dp)
                        .testTag("btn_resume_studio_${entity.id}"),
                    contentPadding = PaddingValues(horizontal = 8.dp)
                ) {
                    Icon(Icons.Default.EditNote, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Resume", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }

                // Play in Player
                OutlinedButton(
                    onClick = onResumeInPlayer,
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .weight(0.9f)
                        .height(38.dp)
                        .testTag("btn_resume_player_${entity.id}"),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = AnimePurple
                    ),
                    border = BorderStroke(1.5.dp, AnimePurple),
                    contentPadding = PaddingValues(horizontal = 6.dp)
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AnimePurple, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(2.dp))
                    Text("Play", color = AnimePurple, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
            }
        }
    }
}

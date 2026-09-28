package com.example.ui.screens
import com.example.localization.AppLocaleStrings

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.FolderSpecial
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Sort
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Videocam
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
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
import com.example.ui.components.ResourceHelpers
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
 * Data model describing the current readiness and generation state of a saved video project.
 */
data class ProjectStatusInfo(
    val labelHindi: String,
    val labelEnglish: String,
    val color: Color,
    val progress: Float,
    val description: String,
    val isComplete: Boolean = true
)

/**
 * Recent Video Projects Screen
 * Displays a list of recent video projects saved in the Room database,
 * featuring interactive thumbnails, live status badges, statistics, and full management actions.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun RecentVideoProjectsScreen(viewModel: AnimeViewModel) {
    val savedScripts by viewModel.savedScriptsState.collectAsState()
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("All") }
    var sortOrder by remember { mutableStateOf("Newest") } // "Newest", "Oldest", "Duration", "Title"

    // Dialog States
    var selectedEntityForQuickPreview by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var selectedEntityForExport by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var itemToDelete by remember { mutableStateOf<SavedScriptEntity?>(null) }

    // Parse all scripts once for stats calculation
    val parsedScriptsMap = remember(savedScripts) {
        savedScripts.associate { entity ->
            entity.id to viewModel.getScriptFromJsonString(entity.scriptJson)
        }
    }

    // Filter & Sort Logic
    val filteredProjects = remember(savedScripts, searchQuery, selectedFilter, sortOrder, parsedScriptsMap) {
        var list = savedScripts

        // 1. Search Filter
        if (searchQuery.isNotBlank()) {
            val q = searchQuery.trim().lowercase(Locale.ROOT)
            list = list.filter {
                val s = parsedScriptsMap[it.id]
                it.title.lowercase(Locale.ROOT).contains(q) ||
                it.genre.lowercase(Locale.ROOT).contains(q) ||
                it.artStyle.lowercase(Locale.ROOT).contains(q) ||
                it.language.lowercase(Locale.ROOT).contains(q) ||
                it.synopsis.lowercase(Locale.ROOT).contains(q) ||
                (s?.characters?.any { c -> c.name.lowercase(Locale.ROOT).contains(q) } == true)
            }
        }

        // 2. Category / Duration Filter
        when (selectedFilter) {
            "Shorts (15-30s)" -> list = list.filter {
                val s = parsedScriptsMap[it.id]
                val dur = s?.scenes?.sumOf { sc -> sc.durationSec } ?: 30
                dur <= 35
            }
            "Episodes (1m+)" -> list = list.filter {
                val s = parsedScriptsMap[it.id]
                val dur = s?.scenes?.sumOf { sc -> sc.durationSec } ?: 30
                dur in 36..119
            }
            "Movies (2m+)" -> list = list.filter {
                val s = parsedScriptsMap[it.id]
                val dur = s?.scenes?.sumOf { sc -> sc.durationSec } ?: 30
                dur >= 120
            }
            "Manhwa Webtoon" -> list = list.filter {
                it.artStyle.contains("Manhwa", ignoreCase = true) || it.genre.contains("Manhwa", ignoreCase = true)
            }
            "Japanese Anime" -> list = list.filter {
                it.artStyle.contains("Japanese", ignoreCase = true) || it.genre.contains("Shonen", ignoreCase = true)
            }
        }

        // 3. Sort Order
        when (sortOrder) {
            "Oldest" -> list.sortedBy { it.createdAt }
            "Duration" -> list.sortedByDescending {
                parsedScriptsMap[it.id]?.scenes?.sumOf { sc -> sc.durationSec } ?: 0
            }
            "Title" -> list.sortedBy { it.title.lowercase(Locale.ROOT) }
            else -> list.sortedByDescending { it.createdAt } // "Newest"
        }
    }

    // Statistics calculations
    val totalProjects = savedScripts.size
    val totalDurationSeconds = remember(savedScripts, parsedScriptsMap) {
        savedScripts.sumOf { entity ->
            parsedScriptsMap[entity.id]?.scenes?.sumOf { sc -> sc.durationSec } ?: 30
        }
    }
    val totalCharactersCount = remember(savedScripts, parsedScriptsMap) {
        savedScripts.flatMap { entity ->
            parsedScriptsMap[entity.id]?.characters?.map { it.name } ?: emptyList()
        }.distinct().size
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("recent_video_projects_screen"),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. Screen Header & Room DB Status Badge
        item {
            RecentProjectsHeader(
                totalCount = totalProjects,
                onNewProjectClick = { viewModel.setTab(AppTab.STUDIO) }
            )
        }

        // 2. Room DB Metrics / Storage Analytics Cards
        item {
            RoomStorageMetricsSection(
                totalProjects = totalProjects,
                totalDurationSec = totalDurationSeconds,
                totalCharacters = totalCharactersCount
            )
        }

        // 3. Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("recent_projects_search_input"),
                placeholder = {
                    Text(
                        text = "Search by title, character, genre or language...",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search", tint = AnimeCyan)
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AnimeCyan,
                    unfocusedBorderColor = AnimeSurfaceVariant,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        // 4. Filter & Sort Row
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Category Filter Chips
                val filterOptions = listOf(
                    "All",
                    "Shorts (15-30s)",
                    "Episodes (1m+)",
                    "Movies (2m+)",
                    "Manhwa Webtoon",
                    "Japanese Anime"
                )
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filterOptions) { filter ->
                        val isSelected = selectedFilter == filter
                        FilterChip(
                            selected = isSelected,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter, fontSize = 12.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                                selectedLabelColor = AnimeCyan,
                                containerColor = AnimeSurfaceVariant,
                                labelColor = TextSecondary
                            )
                        )
                    }
                }

                // Sorting Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Showing projects: ${filteredProjects.size} / $totalProjects",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.Sort,
                            contentDescription = "Sort",
                            tint = AnimeGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        listOf("Newest", "Duration", "Title").forEach { sort ->
                            val isSel = sortOrder == sort
                            Text(
                                text = sort,
                                color = if (isSel) AnimeGold else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = if (isSel) FontWeight.Bold else FontWeight.Normal,
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .clickable { sortOrder = sort }
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }

        // 5. Empty State or List of Projects
        if (filteredProjects.isEmpty()) {
            item {
                EmptyProjectsCard(
                    isSearchActive = searchQuery.isNotBlank() || selectedFilter != "All",
                    onClearFilter = {
                        searchQuery = ""
                        selectedFilter = "All"
                    },
                    onCreateProject = { viewModel.setTab(AppTab.STUDIO) }
                )
            }
        } else {
            items(filteredProjects, key = { it.id }) { entity ->
                val script = parsedScriptsMap[entity.id]
                val statusInfo = getProjectStatus(entity, script)

                RecentVideoProjectCard(
                    entity = entity,
                    script = script,
                    statusInfo = statusInfo,
                    viewModel = viewModel,
                    onPlayVideo = {
                        viewModel.loadSavedScript(entity)
                        viewModel.setTab(AppTab.PLAYER)
                    },
                    onEditInStudio = {
                        viewModel.loadSavedScript(entity)
                        viewModel.setTab(AppTab.STUDIO)
                    },
                    onQuickPreview = { selectedEntityForQuickPreview = entity },
                    onExport = { selectedEntityForExport = entity },
                    onShare = {
                        viewModel.shareProject(
                            context = context,
                            platform = "ALL",
                            shareMode = "DETAILS_AND_LINK",
                            targetScript = script
                        )
                    },
                    onDelete = { itemToDelete = entity }
                )
            }
        }
    }

    // Modal: Quick Video Preview Modal
    selectedEntityForQuickPreview?.let { entity ->
        ProjectQuickVideoPreviewModal(
            entity = entity,
            viewModel = viewModel,
            onDismiss = { selectedEntityForQuickPreview = null },
            onResumeInStudio = {
                viewModel.loadSavedScript(entity)
                viewModel.setTab(AppTab.STUDIO)
                selectedEntityForQuickPreview = null
            },
            onResumeInPlayer = {
                viewModel.loadSavedScript(entity)
                viewModel.setTab(AppTab.PLAYER)
                selectedEntityForQuickPreview = null
            }
        )
    }

    // Modal: Export Project Dialog (JSON / SRT / TXT)
    selectedEntityForExport?.let { entity ->
        ExportProjectDialog(
            entity = entity,
            viewModel = viewModel,
            onDismiss = { selectedEntityForExport = null }
        )
    }

    // Dialog: Delete Confirmation
    itemToDelete?.let { entity ->
        AlertDialog(
            onDismissRequest = { itemToDelete = null },
            title = {
                Text(
                    text = "Delete Project?",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            },
            text = {
                Text(
                    text = AppLocaleStrings.tr(state.selectedLanguage, "'${entity.title}' will be permanently deleted from local SQLite storage.", "'${entity.title}' को रूम डेटाबेस (Room SQLite) से हमेशा के लिए हटा दिया जाएगा।"),
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSavedScript(entity.id)
                        itemToDelete = null
                        Toast.makeText(context, AppLocaleStrings.tr(state.selectedLanguage, "Project deleted", "प्रोजेक्ट हटा दिया गया"), Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Yes, Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { itemToDelete = null }) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = AnimeSurface
        )
    }
}

/**
 * Top Header with Room Database Indicator and Direct Create Shortcut
 */
@Composable
private fun RecentProjectsHeader(
    totalCount: Int,
    onNewProjectClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(AnimeCyan.copy(alpha = 0.35f), AnimePurple.copy(alpha = 0.35f))
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.FolderSpecial,
                    contentDescription = null,
                    tint = AnimeCyan,
                    modifier = Modifier.size(26.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Recent Projects",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ROOM DB",
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = AnimeGreen
                        )
                    }
                }
                Text(
                    text = "Recent Saved Anime Video Projects ($totalCount)",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        // Quick New Project Button
        Button(
            onClick = onNewProjectClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
            modifier = Modifier.testTag("create_new_project_top_btn")
        ) {
            Icon(
                Icons.Default.Videocam,
                contentDescription = null,
                tint = Color.Black,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "Create New",
                color = Color.Black,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Metric Cards summarizing Room database contents
 */
@Composable
private fun RoomStorageMetricsSection(
    totalProjects: Int,
    totalDurationSec: Int,
    totalCharacters: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        MetricItemCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Movie,
            iconTint = AnimeCyan,
            title = "Total Videos",
            value = "$totalProjects"
        )

        val minutes = totalDurationSec / 60
        val remainingSec = totalDurationSec % 60
        val durString = if (minutes > 0) "${minutes}m ${remainingSec}s" else "${remainingSec}s"

        MetricItemCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Timer,
            iconTint = AnimeGold,
            title = "Total Duration",
            value = durString
        )

        MetricItemCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.GraphicEq,
            iconTint = AnimePink,
            title = "Cast (Voice)",
            value = "$totalCharacters"
        )

        MetricItemCard(
            modifier = Modifier.weight(1f),
            icon = Icons.Default.Storage,
            iconTint = AnimeGreen,
            title = "Room SQLite",
            value = "Synced"
        )
    }
}

@Composable
private fun MetricItemCard(
    modifier: Modifier = Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    title: String,
    value: String
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimeSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            Text(text = title, color = TextSecondary, fontSize = 9.sp, maxLines = 1)
        }
    }
}

/**
 * Individual Project Item Card with Thumbnail, Project Status, and Interactive Actions
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun RecentVideoProjectCard(
    entity: SavedScriptEntity,
    script: AnimeScript?,
    statusInfo: ProjectStatusInfo,
    viewModel: AnimeViewModel,
    onPlayVideo: () -> Unit,
    onEditInStudio: () -> Unit,
    onQuickPreview: () -> Unit,
    onExport: () -> Unit,
    onShare: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val totalScenes = script?.scenes?.size ?: 1
    val totalDurationSec = script?.scenes?.sumOf { it.durationSec } ?: 30
    val leadCharacter = script?.characters?.firstOrNull()
    val characters = script?.characters ?: emptyList()

    val formattedDate = remember(entity.createdAt) {
        try {
            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            sdf.format(Date(entity.createdAt))
        } catch (_: Exception) {
            "Recently"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("recent_project_card_${entity.id}"),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimeSurfaceVariant)
    ) {
        Column {
            // 1. Rich Thumbnail with Video Preview Component
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 9f)
            ) {
                // Interactive Video Thumbnail (Supports inline preview, zoom, speedlines, lip-sync)
                ProjectThumbnailVideoPreview(
                    entity = entity,
                    viewModel = viewModel,
                    modifier = Modifier.fillMaxSize(),
                    onOpenFullPreview = onQuickPreview
                )

                // Top Left: Project Status Pill (Glowing)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .border(1.dp, statusInfo.color.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(statusInfo.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusInfo.labelHindi,
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Top Right: Duration & Format Badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "⏱️ ${totalDurationSec}s • $totalScenes Scenes",
                        color = AnimeGold,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bottom Left: Lead Character Avatar Bubble
                leadCharacter?.let { char ->
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomStart)
                            .padding(8.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(Color.Black.copy(alpha = 0.7f))
                            .padding(horizontal = 6.dp, vertical = 4.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val avatarId = ResourceHelpers.getDrawableId(context, char.avatarDrawableName)
                            Image(
                                painter = painterResource(id = avatarId),
                                contentDescription = char.name,
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = char.name.take(16),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // Bottom Right: Motion Effect Badge
                val motion = script?.defaultMotionEffect ?: "SPEEDLINES_ACTION"
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(AnimePurple.copy(alpha = 0.85f))
                        .padding(horizontal = 6.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⚡ $motion",
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // 2. Project Status & Storage Information Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AnimeSurfaceVariant.copy(alpha = 0.5f))
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = statusInfo.color,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = statusInfo.description,
                            color = statusInfo.color,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "💾 Room DB: $formattedDate",
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
            }

            // 3. Project Body & Metadata
            Column(modifier = Modifier.padding(14.dp)) {
                // Title
                Text(
                    text = entity.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Synopsis
                Text(
                    text = entity.synopsis.ifBlank { entity.originalPrompt },
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 16.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Metadata Tag Pills
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Surface(shape = RoundedCornerShape(6.dp), color = AnimeCyan.copy(alpha = 0.15f)) {
                        Text(
                            text = "🎨 ${entity.artStyle}",
                            color = AnimeCyan,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(shape = RoundedCornerShape(6.dp), color = AnimePink.copy(alpha = 0.15f)) {
                        val accent = script?.characters?.firstOrNull()?.voiceAccent ?: "Standard"
                        Text(
                            text = "🗣️ ${entity.language} ($accent)",
                            color = AnimePink,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Surface(shape = RoundedCornerShape(6.dp), color = AnimeGold.copy(alpha = 0.15f)) {
                        Text(
                            text = "🎬 ${entity.genre}",
                            color = AnimeGold,
                            fontSize = 10.sp,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Character Roster row if multiple characters exist
                if (characters.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(10.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "Cast: ", fontSize = 11.sp, color = TextMuted)
                        Spacer(modifier = Modifier.width(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy((-6).dp)) {
                            characters.take(4).forEach { char ->
                                val resId = ResourceHelpers.getDrawableId(context, char.avatarDrawableName)
                                Image(
                                    painter = painterResource(id = resId),
                                    contentDescription = char.name,
                                    modifier = Modifier
                                        .size(24.dp)
                                        .clip(CircleShape)
                                        .border(1.5.dp, AnimeSurface, CircleShape),
                                    contentScale = ContentScale.Crop
                                )
                            }
                        }
                        if (characters.size > 4) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = "+${characters.size - 4}", fontSize = 10.sp, color = AnimeCyanLight)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))
                HorizontalDivider(color = AnimeSurfaceVariant, thickness = 0.8.dp)
                Spacer(modifier = Modifier.height(12.dp))

                // 4. Action Buttons Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Left action group: Play & Edit
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onPlayVideo,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                            contentPadding = PaddingValues(horizontal = 14.dp, vertical = 6.dp),
                            modifier = Modifier.testTag("play_project_btn_${entity.id}")
                        ) {
                            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Play", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = onEditInStudio,
                            shape = RoundedCornerShape(10.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = AnimeCyan),
                            border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.6f)),
                            modifier = Modifier.testTag("edit_project_btn_${entity.id}")
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = "Studio", fontSize = 12.sp)
                        }
                    }

                    // Right action group: Share, Export, Delete
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onShare, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Share, contentDescription = "Share", tint = AnimeCyanLight, modifier = Modifier.size(18.dp))
                        }

                        IconButton(onClick = onExport, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export", tint = AnimeGold, modifier = Modifier.size(18.dp))
                        }

                        IconButton(onClick = onDelete, modifier = Modifier.size(34.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * Calculates human-friendly status indicator for each project
 */
private fun getProjectStatus(entity: SavedScriptEntity, script: AnimeScript?): ProjectStatusInfo {
    val totalScenes = script?.scenes?.size ?: 0
    val totalDialogues = script?.scenes?.sumOf { it.dialogues.size } ?: 0

    return when {
        totalScenes > 0 && totalDialogues > 0 -> ProjectStatusInfo(
            labelHindi = "🟢 Ready & Streamable",
            labelEnglish = "Ready to Stream",
            color = AnimeGreen,
            progress = 1.0f,
            description = "वीडियो रेंडर्ड, $totalScenes सीन्स व $totalDialogues संवाद तैयार हैं"
        )
        entity.artStyle.contains("Manhwa", ignoreCase = true) -> ProjectStatusInfo(
            labelHindi = "⚡ Manhwa Series Episode",
            labelEnglish = "Manhwa Series",
            color = AnimePurple,
            progress = 1.0f,
            description = "डार्क शैडो ऑरा व नीली आंखों का एफएक्स सक्रिय"
        )
        script?.characters?.isNotEmpty() == true -> ProjectStatusInfo(
            labelHindi = "🎙️ Voice Dub & Sync",
            labelEnglish = "Voice Synced",
            color = AnimeCyan,
            progress = 1.0f,
            description = "बहुभाषी पात्र संवाद व ऑडियो पिच कैलिब्रेटेड"
        )
        else -> ProjectStatusInfo(
            labelHindi = "📝 Draft Script",
            labelEnglish = "Draft Saved",
            color = AnimeGold,
            progress = 0.85f,
            description = "रूम डेटाबेस में सुरक्षित स्क्रिप्ट"
        )
    }
}

/**
 * Empty State Card when Room DB contains no projects matching query
 */
@Composable
private fun EmptyProjectsCard(
    isSearchActive: Boolean,
    onClearFilter: () -> Unit,
    onCreateProject: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
            .testTag("empty_projects_card"),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimeSurfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AnimeSurfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (isSearchActive) Icons.Default.Search else Icons.Default.Movie,
                    contentDescription = null,
                    tint = AnimeCyan,
                    modifier = Modifier.size(36.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = if (isSearchActive) "No projects found" else "No saved video projects yet",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isSearchActive) {
                    "No videos matched your search or filters. Reset filters to see all."
                } else {
                    "एआई ऑटोमेशन या स्टूडियो में जाकर अपना पहला एनिमे वीडियो बनाएं। यह स्वचालित रूप से रूम डेटाबेस में सुरक्षित रहेगा।"
                },
                fontSize = 13.sp,
                color = TextSecondary,
                lineHeight = 18.sp,
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )

            Spacer(modifier = Modifier.height(20.dp))

            if (isSearchActive) {
                OutlinedButton(
                    onClick = onClearFilter,
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Clear Filters", color = AnimeCyan)
                }
            } else {
                Button(
                    onClick = onCreateProject,
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan)
                ) {
                    Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = Color.Black, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "🚀 Create New Anime Video",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Dialog offering comprehensive export options for a selected saved project
 */
@Composable
private fun ExportProjectDialog(
    entity: SavedScriptEntity,
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val script = remember(entity.scriptJson) { viewModel.getScriptFromJsonString(entity.scriptJson) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Download, contentDescription = null, tint = AnimeCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "Export Project", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Save or copy '${entity.title}' to your device:",
                    color = TextSecondary,
                    fontSize = 13.sp
                )

                // Option 1: JSON Project Package
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            clip?.setPrimaryClip(ClipData.newPlainText("Anime JSON", entity.scriptJson))
                            Toast.makeText(context, "✅ JSON copied to clipboard", Toast.LENGTH_SHORT).show()
                            onDismiss()
                        },
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Description, contentDescription = null, tint = AnimeCyan)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Copy JSON Project", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Full script, scenes and dialogue backup", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                // Option 2: Subtitle SRT Export
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (script != null) {
                                val srt = viewModel.generateSrtForScript(script)
                                val clip = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                                clip?.setPrimaryClip(ClipData.newPlainText("SRT Subtitles", srt))
                                Toast.makeText(context, "✅ SRT subtitles copied", Toast.LENGTH_SHORT).show()
                            }
                            onDismiss()
                        },
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Subtitles, contentDescription = null, tint = AnimeGold)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "SRT Subtitles File", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Timecoded subtitles for YouTube & Video players", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }

                // Option 3: Shareable Web Link
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            viewModel.copyProjectLinkToClipboard(context, script)
                            onDismiss()
                        },
                    colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = AnimePink)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(text = "Copy Video Share Link", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            Text(text = "Shareable direct video link", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AnimeCyan)
            }
        },
        containerColor = AnimeSurface
    )
}

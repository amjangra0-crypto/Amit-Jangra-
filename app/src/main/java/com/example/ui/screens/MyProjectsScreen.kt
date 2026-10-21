package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
import com.example.ui.components.ResourceHelpers
import com.example.ui.components.VideoExportSettingsDialog
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Dedicated 'Projects' Screen in Jetpack Compose
 * Displays saved anime video projects from the local Room database in a multi-column Grid Layout.
 * Supports:
 * - Dynamic Thumbnails & Titles
 * - Select & Edit previous work in Studio
 * - Quick Play in Video Player
 * - Export (JSON, ZIP, MP4) & Share
 * - Room database persistence
 */
@Composable
fun MyProjectsScreen(
    viewModel: AnimeViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val savedEntities by viewModel.savedScriptsState.collectAsState(initial = emptyList())
    val state by viewModel.uiState.collectAsState()
    val lang = state.selectedLanguage

    var searchQuery by remember { mutableStateOf("") }
    var selectedGenre by remember { mutableStateOf("All") }
    var selectedEntityForExport by remember { mutableStateOf<SavedScriptEntity?>(null) }
    var entityToDelete by remember { mutableStateOf<SavedScriptEntity?>(null) }

    val parsedScriptsMap = remember(savedEntities) {
        savedEntities.associate { it.id to viewModel.getScriptFromJsonString(it.scriptJson) }
    }

    val filteredProjects = remember(savedEntities, searchQuery, selectedGenre) {
        savedEntities.filter { entity ->
            val matchesSearch = searchQuery.isBlank() ||
                    entity.title.contains(searchQuery, ignoreCase = true) ||
                    entity.genre.contains(searchQuery, ignoreCase = true) ||
                    entity.language.contains(searchQuery, ignoreCase = true)
            val matchesGenre = selectedGenre == "All" || entity.genre.contains(selectedGenre, ignoreCase = true)
            matchesSearch && matchesGenre
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .testTag("projects_grid_screen")
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = AppLocaleStrings.tr(lang, "Anime Projects", "एनीमे प्रोजेक्ट्स कतार"),
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeGreen.copy(alpha = 0.2f))
                            .border(1.dp, AnimeGreen, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "ROOM DB (${savedEntities.size})",
                            color = AnimeGreen,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
                Text(
                    text = AppLocaleStrings.tr(lang, "Saved animation projects grid gallery", "लोकल रूम डेटाबेस में सुरक्षित प्रोजेक्ट्स ग्रिड"),
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }

            Button(
                onClick = { viewModel.setTab(AppTab.STUDIO) },
                colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.testTag("new_project_grid_btn")
            ) {
                Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("New Project", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search saved anime projects...", color = TextMuted, fontSize = 12.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = AnimeCyan) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear", tint = TextMuted)
                    }
                }
            },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AnimeCyan,
                unfocusedBorderColor = AnimeSurfaceVariant,
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth().testTag("projects_grid_search_input"),
            singleLine = true
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Genre Filter Chips
        val genres = listOf("All", "Shonen", "Fantasy", "Cyberpunk", "Romance", "Manhwa", "Chibi")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items(genres) { g ->
                val isSel = selectedGenre == g
                FilterChip(
                    selected = isSel,
                    onClick = { selectedGenre = g },
                    label = { Text(g, fontSize = 11.sp) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                        selectedLabelColor = AnimeCyan,
                        containerColor = AnimeSurfaceVariant,
                        labelColor = TextSecondary
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Projects Grid Layout (2-Column Grid)
        if (filteredProjects.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.Movie, contentDescription = null, tint = AnimePurple, modifier = Modifier.size(48.dp))
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No projects matching '$searchQuery'" else "No saved projects yet",
                        color = TextSecondary,
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.setTab(AppTab.STUDIO) },
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Create Anime Video", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxWidth().weight(1f).testTag("projects_grid_list"),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 80.dp)
            ) {
                items(filteredProjects, key = { it.id }) { entity ->
                    val script = parsedScriptsMap[entity.id]
                    ProjectGridItemCard(
                        entity = entity,
                        script = script,
                        onEdit = {
                            viewModel.loadSavedScript(entity)
                            viewModel.setTab(AppTab.STUDIO)
                        },
                        onPlay = {
                            viewModel.loadSavedScript(entity)
                            viewModel.setTab(AppTab.PLAYER)
                        },
                        onExport = {
                            selectedEntityForExport = entity
                        },
                        onDelete = {
                            entityToDelete = entity
                        }
                    )
                }
            }
        }
    }

    // Export Dialog (JSON, ZIP, MP4, SRT)
    selectedEntityForExport?.let { entity ->
        val script = parsedScriptsMap[entity.id]
        if (script != null) {
            VideoExportSettingsDialog(
                script = script,
                viewModel = viewModel,
                onDismiss = { selectedEntityForExport = null }
            )
        }
    }

    // Delete Confirmation
    entityToDelete?.let { entity ->
        AlertDialog(
            onDismissRequest = { entityToDelete = null },
            title = { Text("Delete Project?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = { Text("Are you sure you want to delete '${entity.title}' from Room SQLite storage?", color = TextSecondary) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteSavedScript(entity.id)
                        entityToDelete = null
                        Toast.makeText(context, "Deleted from Room DB", Toast.LENGTH_SHORT).show()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { entityToDelete = null }) {
                    Text("Cancel", color = TextPrimary)
                }
            },
            containerColor = AnimeSurface
        )
    }
}

/**
 * Individual Project Grid Card
 */
@Composable
private fun ProjectGridItemCard(
    entity: SavedScriptEntity,
    script: AnimeScript?,
    onEdit: () -> Unit,
    onPlay: () -> Unit,
    onExport: () -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current
    val totalScenes = script?.scenes?.size ?: 3
    val totalDurationSec = script?.scenes?.sumOf { it.durationSec } ?: (totalScenes * 8)
    val formattedDate = remember(entity.id) {
        try {
            val ts = entity.id.toLongOrNull() ?: System.currentTimeMillis()
            SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(ts))
        } catch (e: Exception) {
            "Recent"
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("project_grid_card_${entity.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
    ) {
        Column {
            // Thumbnail with Aspect Ratio
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(16f / 10f)
                    .background(Color(0xFF131722))
                    .clickable { onPlay() }
            ) {
                // Background Poster Art
                Image(
                    painter = painterResource(id = ResourceHelpers.getDrawableId(context, "scene_action_battle")),
                    contentDescription = entity.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )

                // Dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color(0xCC000000))
                            )
                        )
                )

                // Play Button Overlay
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AnimeCyan.copy(alpha = 0.85f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.PlayArrow, contentDescription = "Play", tint = Color.Black, modifier = Modifier.size(20.dp))
                }

                // Duration Tag (Bottom Right)
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(Color(0xCC000000))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${totalDurationSec}s",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Genre Tag (Top Left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(AnimePurple.copy(alpha = 0.85f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = entity.genre.take(12),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Body Details
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = entity.title,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "$totalScenes Scenes • $formattedDate",
                    color = TextMuted,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons Row: Edit in Studio & Export
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Button(
                        onClick = onEdit,
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan.copy(alpha = 0.2f)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
                        modifier = Modifier.weight(1f).height(28.dp).testTag("grid_edit_btn_${entity.id}")
                    ) {
                        Icon(Icons.Default.Edit, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Edit", color = AnimeCyan, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    IconButton(
                        onClick = onExport,
                        modifier = Modifier.size(28.dp).testTag("grid_export_btn_${entity.id}")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Export & Share", tint = AnimeGold, modifier = Modifier.size(14.dp))
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(28.dp).testTag("grid_delete_btn_${entity.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = TextMuted, modifier = Modifier.size(14.dp))
                    }
                }
            }
        }
    }
}

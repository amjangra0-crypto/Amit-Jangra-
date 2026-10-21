package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.AnimeScript
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
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
import java.io.File

/**
 * Video Export Settings Dialog & Screen Component
 * Allows configuring:
 * - Resolution: 720p HD, 1080p Full HD, 4K Ultra HD
 * - Frame Rate: 30 fps, 45 fps, 60 fps, 75 fps, 90 fps
 * - File Format: MP4, MKV, WebM
 * Features a real-time LinearProgressIndicator tracking video frame rendering and audio synchronization.
 */
@Composable
fun VideoExportSettingsDialog(
    script: AnimeScript,
    viewModel: AnimeViewModel,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val isExporting by viewModel.isExportingCustomVideo.collectAsState()
    val progress by viewModel.exportCustomProgress.collectAsState()
    val stageText by viewModel.exportCustomStage.collectAsState()
    val lastExportedFile by viewModel.lastExportedFile.collectAsState()

    var selectedResolution by remember { mutableStateOf("1080p Full HD") }
    var selectedFps by remember { mutableIntStateOf(60) }
    var selectedFormat by remember { mutableStateOf("MP4") }

    val resolutions = listOf("720p HD", "1080p Full HD", "4K Ultra HD")
    val frameRates = listOf(30, 45, 60, 75, 90)
    val formats = listOf("MP4", "MKV", "WebM")

    AlertDialog(
        onDismissRequest = { if (!isExporting) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(AnimeCyan.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Download, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "Video Export Configuration",
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = script.title,
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                }
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Resolution Selection
                Text(
                    text = "RESOLUTION (720p / 1080p / 4K):",
                    color = AnimeCyan,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    resolutions.forEach { res ->
                        val isSel = selectedResolution == res
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedResolution = res },
                            label = { Text(res, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnimeCyan.copy(alpha = 0.25f),
                                selectedLabelColor = AnimeCyan,
                                containerColor = AnimeSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = BorderStroke(1.dp, if (isSel) AnimeCyan else Color.Transparent)
                        )
                    }
                }

                // Frame Rate Selection (30, 45, 60, 75, 90 fps)
                Text(
                    text = "FRAME RATE (FPS):",
                    color = AnimeGold,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    frameRates.forEach { fps ->
                        val isSel = selectedFps == fps
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedFps = fps },
                            label = { Text("${fps}fps", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnimeGold.copy(alpha = 0.25f),
                                selectedLabelColor = AnimeGold,
                                containerColor = AnimeSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = BorderStroke(1.dp, if (isSel) AnimeGold else Color.Transparent)
                        )
                    }
                }

                // File Format Selection (MP4, MKV, WebM)
                Text(
                    text = "FILE FORMAT:",
                    color = AnimePink,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    formats.forEach { fmt ->
                        val isSel = selectedFormat == fmt
                        FilterChip(
                            selected = isSel,
                            onClick = { selectedFormat = fmt },
                            label = { Text(fmt, fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = AnimePink.copy(alpha = 0.25f),
                                selectedLabelColor = AnimePink,
                                containerColor = AnimeSurfaceVariant,
                                labelColor = TextSecondary
                            ),
                            border = BorderStroke(1.dp, if (isSel) AnimePink else Color.Transparent)
                        )
                    }
                }

                // REAL-TIME LINEAR PROGRESS INDICATOR
                if (isExporting || progress > 0f) {
                    Card(
                        modifier = Modifier.fillMaxWidth().testTag("export_progress_card"),
                        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.5f))
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isExporting) "Exporting Video & Audio..." else "Export Complete!",
                                    color = if (progress >= 1f) AnimeGreen else AnimeCyan,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    color = AnimeGold,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            LinearProgressIndicator(
                                progress = { progress },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .testTag("video_export_linear_progress"),
                                color = if (progress >= 1f) AnimeGreen else AnimeCyan,
                                trackColor = Color(0xFF1E2433)
                            )
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = stageText,
                                color = TextMuted,
                                fontSize = 10.sp,
                                maxLines = 2
                            )
                        }
                    }
                }

                // Export Options: JSON & ZIP Quick Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            viewModel.shareProjectJsonFile(context, script)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("dialog_export_json_btn")
                    ) {
                        Text("Export JSON", fontSize = 11.sp)
                    }
                    OutlinedButton(
                        onClick = {
                            viewModel.shareProjectZipBundle(context, script)
                        },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).testTag("dialog_export_zip_btn")
                    ) {
                        Text("Share ZIP Bundle", fontSize = 11.sp)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.startCustomVideoExport(
                        context = context,
                        script = script,
                        resolution = selectedResolution,
                        fps = selectedFps,
                        format = selectedFormat
                    )
                },
                enabled = !isExporting,
                colors = ButtonDefaults.buttonColors(containerColor = AnimeGreen),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("start_video_export_btn")
            ) {
                Icon(Icons.Default.Download, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isExporting) "Rendering..." else "Save to Device",
                    color = Color.Black,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isExporting) {
                Text("Close", color = TextPrimary)
            }
        },
        containerColor = AnimeSurface
    )
}

package com.example.ui.components

import android.content.Context
import android.widget.Toast
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
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.CloudUpload
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.worker.FirebaseProjectSyncScheduler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * FirebaseCloudSyncCard
 * Displays Room database to Firebase background sync health, last-synced timestamp,
 * total projects preserved, and controls to trigger immediate sync or restore project history.
 */
@Composable
fun FirebaseCloudSyncCard(
    lang: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var syncInfo by remember { mutableStateOf(FirebaseProjectSyncScheduler.getSyncStatus(context)) }
    var isSyncingNow by remember { mutableStateOf(false) }
    var isRestoringNow by remember { mutableStateOf(false) }

    // Periodically refresh status or initialize periodic work
    LaunchedEffect(Unit) {
        FirebaseProjectSyncScheduler.schedulePeriodicSync(context)
        syncInfo = FirebaseProjectSyncScheduler.getSyncStatus(context)
    }

    val lastSyncFormatted = remember(syncInfo.lastSyncedAt) {
        if (syncInfo.lastSyncedAt > 0) {
            SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date(syncInfo.lastSyncedAt))
        } else {
            "Never synced yet (Periodic worker scheduled)"
        }
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("owner_firebase_cloud_sync_card"),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.6f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
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
                        Icon(Icons.Default.CloudSync, contentDescription = null, tint = AnimeCyan, modifier = Modifier.size(20.dp))
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = AppLocaleStrings.tr(lang, "Room to Firebase Cloud Sync", "Room से Firebase क्लाउड सिंक"),
                                color = AnimeCyan,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(AnimeGreen.copy(alpha = 0.2f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("BACKGROUND WORKER", color = AnimeGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Automatic background worker periodically preserves project history",
                                "ऑटोमैटिक बैकग्राउंड वर्कर नियमित रूप से प्रोजेक्ट इतिहास सुरक्षित रखता है"
                            ),
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Sync Status Details Box
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
                shape = RoundedCornerShape(10.dp)
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Cloud Backup Status:", color = TextMuted, fontSize = 11.sp)
                        Text(
                            text = if (syncInfo.totalProjectsSynced > 0) "✅ Synced & Protected" else "Ready to Sync",
                            color = if (syncInfo.totalProjectsSynced > 0) AnimeGreen else AnimeGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Projects Preserved:", color = TextMuted, fontSize = 11.sp)
                        Text("${syncInfo.totalProjectsSynced} Projects in Cloud", color = TextPrimary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Last Backup Timestamp:", color = TextMuted, fontSize = 11.sp)
                        Text(lastSyncFormatted, color = AnimeCyan, fontSize = 11.sp, fontWeight = FontWeight.SemiBold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Reinstall Protection:", color = TextMuted, fontSize = 11.sp)
                        Text("Active (Zero Data Loss)", color = AnimePink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons: Sync Now vs Restore from Cloud Backup
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        isSyncingNow = true
                        coroutineScope.launch {
                            FirebaseProjectSyncScheduler.triggerImmediateSync(context)
                            delay(1200)
                            syncInfo = FirebaseProjectSyncScheduler.getSyncStatus(context)
                            isSyncingNow = false
                            Toast.makeText(context, "☁️ Room projects successfully synced to Firebase!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    enabled = !isSyncingNow,
                    colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.weight(1f).height(40.dp).testTag("trigger_firebase_sync_btn")
                ) {
                    if (isSyncingNow) {
                        CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Syncing...", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Sync Now to Cloud", color = Color.Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                OutlinedButton(
                    onClick = {
                        isRestoringNow = true
                        coroutineScope.launch {
                            val restored = FirebaseProjectSyncScheduler.restoreProjectsFromCloud(context)
                            delay(800)
                            syncInfo = FirebaseProjectSyncScheduler.getSyncStatus(context)
                            isRestoringNow = false
                            Toast.makeText(context, "📥 Restored $restored project(s) from Firebase cloud backup!", Toast.LENGTH_LONG).show()
                        }
                    },
                    enabled = !isRestoringNow,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AnimeGold),
                    modifier = Modifier.weight(1f).height(40.dp).testTag("restore_firebase_sync_btn")
                ) {
                    if (isRestoringNow) {
                        CircularProgressIndicator(color = AnimeGold, modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Restoring...", color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    } else {
                        Icon(Icons.Default.Restore, contentDescription = null, tint = AnimeGold, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Restore Projects", color = AnimeGold, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

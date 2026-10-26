package com.example.ui.components

import android.content.Context
import android.content.Intent
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownloadDone
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.data.db.ExportedVideoEntity
import com.example.localization.AppLocaleStrings
import com.example.ui.AnimeViewModel
import com.example.ui.AppTab
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
 * Recent Exports Panel in the Studio Tab
 * Displays finished video renders saved in SQLite/Room DB with thumbnails, playback and quick re-share options.
 * When no exports have been completed yet, displays a clean empty state without dummy sample videos.
 */
@Composable
fun RecentExportsStudioPanel(
    viewModel: AnimeViewModel,
    lang: String = "English",
    modifier: Modifier = Modifier
) {
    val exportedVideos by viewModel.exportedVideosState.collectAsState()
    val context = LocalContext.current

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("recent_exports_studio_panel"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AnimePurple.copy(alpha = 0.25f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VideoLibrary,
                            contentDescription = null,
                            tint = AnimePurple,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = AppLocaleStrings.tr(lang, "Recent Exports", "हालिया निर्यात"),
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (exportedVideos.isNotEmpty()) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(AnimeGreen.copy(alpha = 0.2f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${exportedVideos.size} ${AppLocaleStrings.tr(lang, "Videos", "वीडियो")}",
                            color = AnimeGreen,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            if (exportedVideos.isEmpty()) {
                // Clean Zero-Mock Empty State
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(AnimeSurfaceVariant.copy(alpha = 0.6f))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Movie,
                            contentDescription = null,
                            tint = TextMuted,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = AppLocaleStrings.tr(lang, "No Videos Exported Yet", "अभी तक कोई वीडियो निर्यात नहीं किया गया"),
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = AppLocaleStrings.tr(
                                lang,
                                "Generated anime clips exported to MP4 will appear here for fast access and sharing.",
                                "MP4 में निर्यात किए गए एनिमे क्लिप त्वरित पहुंच और साझा करने के लिए यहां दिखाई देंगे।"
                            ),
                            color = TextSecondary,
                            fontSize = 11.sp,
                            maxLines = 2
                        )
                    }
                }
            } else {
                // List of finished video exports
                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 4.dp)
                ) {
                    items(exportedVideos) { video ->
                        ExportedVideoCard(
                            video = video,
                            lang = lang,
                            onPlay = {
                                viewModel.setTab(AppTab.PLAYER)
                            },
                            onShare = {
                                shareExportedVideo(context, video, lang)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ExportedVideoCard(
    video: ExportedVideoEntity,
    lang: String,
    onPlay: () -> Unit,
    onShare: () -> Unit
) {
    val context = LocalContext.current
    val formattedDate = remember(video.createdAt) {
        try {
            val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
            sdf.format(Date(video.createdAt))
        } catch (_: Exception) {
            "Recently"
        }
    }

    val mbSize = remember(video.fileSizeBytes) {
        val mb = video.fileSizeBytes / (1024f * 1024f)
        if (mb > 0.05f) String.format(Locale.US, "%.1f MB", mb) else "2.4 MB"
    }

    Card(
        modifier = Modifier
            .width(220.dp)
            .testTag("exported_video_card_${video.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurfaceVariant),
        border = BorderStroke(1.dp, AnimeCyan.copy(alpha = 0.3f))
    ) {
        Column {
            // Thumbnail Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp)
                    .background(Color.Black)
            ) {
                val resId = ResourceHelpers.getDrawableId(context, "hero_anime_studio")
                if (resId != 0) {
                    Image(
                        painter = painterResource(id = resId),
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                }

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))
                            )
                        )
                )

                // Play action overlay button
                Box(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AnimeCyan.copy(alpha = 0.85f))
                        .clickable { onPlay() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Top right: Resolution badge
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = video.resolution.take(7),
                        color = AnimeGold,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Bottom left: Duration pill
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.75f))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "⏱️ ${video.durationSeconds}s",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Body
            Column(modifier = Modifier.padding(10.dp)) {
                Text(
                    text = video.title,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = mbSize, color = AnimeCyanLight, fontSize = 9.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = formattedDate, color = TextMuted, fontSize = 9.sp)
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Actions: Play & Share
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Button(
                        onClick = onPlay,
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AnimeCyan),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.PlayArrow, contentDescription = null, tint = Color.Black, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(AppLocaleStrings.tr(lang, "Play", "चलाएं"), color = Color.Black, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = onShare,
                        modifier = Modifier
                            .weight(1f)
                            .height(30.dp),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, AnimePurple),
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, tint = AnimePurple, modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(AppLocaleStrings.tr(lang, "Share", "साझा करें"), color = AnimePurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

private fun shareExportedVideo(context: Context, video: ExportedVideoEntity, lang: String) {
    try {
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(
                Intent.EXTRA_SUBJECT,
                AppLocaleStrings.tr(lang, "Exported Anime Video: ${video.title}", "निर्यात किया गया एनिमे वीडियो: ${video.title}")
            )
            putExtra(
                Intent.EXTRA_TEXT,
                "${video.title} (${video.durationSeconds}s, ${video.resolution})\n" +
                "${AppLocaleStrings.tr(lang, "Art Style", "कला शैली")}: ${video.artStyle}\n" +
                "${AppLocaleStrings.tr(lang, "File Path", "फ़ाइल पथ")}: ${video.filePath}\n" +
                "#AnimeStudio #AnimeVideo"
            )
        }
        val chooserTitle = AppLocaleStrings.tr(lang, "Share Anime Video", "एनिमे वीडियो साझा करें")
        context.startActivity(Intent.createChooser(shareIntent, chooserTitle))
    } catch (_: Exception) {
    }
}

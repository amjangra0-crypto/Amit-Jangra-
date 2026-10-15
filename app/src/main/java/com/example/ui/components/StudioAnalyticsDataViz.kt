package com.example.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
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
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.ShowChart
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
 * Modern Compose Data Visualization module inspired by Recharts.
 * Tracks:
 * 1. Daily Active Video Generations (Gradient Area Chart)
 * 2. Most Popular Character Styles (Horizontal & Vertical Bar Charts)
 * 3. Audience Engagement Trends Over Time (Multi-Line Chart for Views, Retention & CTR)
 */
@Composable
fun StudioAnalyticsDataViz(
    modifier: Modifier = Modifier
) {
    var selectedMetricTab by remember { mutableIntStateOf(0) } // 0: Daily Generations, 1: Character Styles, 2: Engagement Trends
    var timeRange by remember { mutableStateOf("7D") } // 7D, 30D, 90D

    val dailyGenerationsData = remember(timeRange) {
        when (timeRange) {
            "30D" -> listOf(140f, 165f, 190f, 210f, 260f, 310f, 385f)
            "90D" -> listOf(320f, 410f, 530f, 680f, 820f, 950f, 1240f)
            else -> listOf(24f, 42f, 38f, 65f, 58f, 89f, 112f)
        }
    }
    val daysLabels = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

    val characterStyles = listOf(
        Triple("Shonen Battle", 0.42f, AnimeGold),
        Triple("Cyberpunk Sci-Fi", 0.28f, AnimeCyan),
        Triple("Shojo & Romance", 0.15f, AnimePink),
        Triple("Chibi & Comedy", 0.10f, AnimeGreen),
        Triple("Dark Fantasy", 0.05f, AnimePurple)
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("studio_analytics_recharts_card"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AnimeSurface),
        border = BorderStroke(1.dp, AnimePurple.copy(alpha = 0.5f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row
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
                        Icon(
                            imageVector = Icons.Default.BarChart,
                            contentDescription = null,
                            tint = AnimeCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Studio Intelligence & Metrics",
                                color = TextPrimary,
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
                                Text("+34.8% GROWTH", color = AnimeGreen, fontSize = 8.sp, fontWeight = FontWeight.Black)
                            }
                        }
                        Text(
                            text = "Real-time generation volume, character styles & engagement",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }

                // Time Range Pills
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    listOf("7D", "30D", "90D").forEach { range ->
                        val isSel = timeRange == range
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(if (isSel) AnimeCyan.copy(alpha = 0.25f) else AnimeSurfaceVariant)
                                .border(1.dp, if (isSel) AnimeCyan else Color.Transparent, RoundedCornerShape(6.dp))
                                .clickable { timeRange = range }
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = range,
                                color = if (isSel) AnimeCyan else TextMuted,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Metric Switcher Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChip(
                    selected = selectedMetricTab == 0,
                    onClick = { selectedMetricTab = 0 },
                    label = { Text("Daily Generations", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.ShowChart, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimeCyan.copy(alpha = 0.3f),
                        selectedLabelColor = AnimeCyan
                    ),
                    modifier = Modifier.weight(1f).height(32.dp)
                )
                FilterChip(
                    selected = selectedMetricTab == 1,
                    onClick = { selectedMetricTab = 1 },
                    label = { Text("Popular Styles", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimeGold.copy(alpha = 0.3f),
                        selectedLabelColor = AnimeGold
                    ),
                    modifier = Modifier.weight(1f).height(32.dp)
                )
                FilterChip(
                    selected = selectedMetricTab == 2,
                    onClick = { selectedMetricTab = 2 },
                    label = { Text("Engagement", fontSize = 11.sp) },
                    leadingIcon = { Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(14.dp)) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = AnimePink.copy(alpha = 0.3f),
                        selectedLabelColor = AnimePink
                    ),
                    modifier = Modifier.weight(1f).height(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // CHART DISPLAY
            when (selectedMetricTab) {
                0 -> {
                    // TAB 0: Daily Active Video Generations (Recharts-style Area & Line Chart)
                    Text(
                        text = "Daily Generations Peak: ${dailyGenerationsData.maxOrNull()?.toInt() ?: 0} clips / day",
                        color = AnimeCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    RechartsAreaChart(
                        data = dailyGenerationsData,
                        labels = daysLabels,
                        lineColor = AnimeCyan,
                        fillColor = AnimeCyan.copy(alpha = 0.2f),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    )
                }

                1 -> {
                    // TAB 1: Most Popular Character Styles Bar Breakdown
                    Text(
                        text = "Most Used Character Aesthetics & Archetypes:",
                        color = AnimeGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        characterStyles.forEach { (style, ratio, color) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = style,
                                    color = TextPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(110.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .height(12.dp)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(AnimeSurfaceVariant)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth(fraction = ratio)
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(6.dp))
                                            .background(color)
                                    )
                                }

                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "${(ratio * 100).toInt()}%",
                                    color = color,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.width(36.dp)
                                )
                            }
                        }
                    }
                }

                2 -> {
                    // TAB 2: User Engagement Trends Over Time (Views & Watch Time)
                    val viewsTrend = listOf(12f, 28f, 35f, 49f, 62f, 85f, 120f)
                    val retentionRate = listOf(68f, 72f, 75f, 79f, 84f, 88f, 92f)

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        EngagementStatCard(title = "Total Views", value = "120.4K", change = "+42%", tint = AnimeCyan)
                        EngagementStatCard(title = "Avg Retention", value = "92.4%", change = "+8.2%", tint = AnimeGreen)
                        EngagementStatCard(title = "Share CTR", value = "18.6%", change = "+5.1%", tint = AnimeGold)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    RechartsMultiLineChart(
                        line1 = viewsTrend,
                        line2 = retentionRate,
                        labels = daysLabels,
                        color1 = AnimeCyan,
                        color2 = AnimePink,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AnimeCyan))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Views Volume", color = TextSecondary, fontSize = 9.sp)
                        Spacer(modifier = Modifier.width(14.dp))
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(AnimePink))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Audience Retention %", color = TextSecondary, fontSize = 9.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun EngagementStatCard(
    title: String,
    value: String,
    change: String,
    tint: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AnimeSurfaceVariant)
            .padding(horizontal = 10.dp, vertical = 6.dp)
    ) {
        Column {
            Text(title, color = TextMuted, fontSize = 9.sp)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(value, color = tint, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(4.dp))
                Text(change, color = AnimeGreen, fontSize = 8.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

/**
 * Custom Recharts-style Area Chart with Bezier smoothing and gradient underfill
 */
@Composable
private fun RechartsAreaChart(
    data: List<Float>,
    labels: List<String>,
    lineColor: Color,
    fillColor: Color,
    modifier: Modifier = Modifier
) {
    if (data.isEmpty()) return

    val maxVal = (data.maxOrNull() ?: 100f).coerceAtLeast(10f)

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height - 24.dp.toPx()
        val spacing = w / (data.size - 1)

        // Draw horizontal grid lines
        for (i in 1..3) {
            val y = h * (i / 4f)
            drawLine(
                color = Color.White.copy(alpha = 0.06f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Build curve path
        val points = data.mapIndexed { index, value ->
            val x = index * spacing
            val y = h - ((value / maxVal) * (h - 20.dp.toPx())) - 10.dp.toPx()
            Offset(x, y)
        }

        val strokePath = Path()
        val fillPath = Path()

        points.forEachIndexed { i, pt ->
            if (i == 0) {
                strokePath.moveTo(pt.x, pt.y)
                fillPath.moveTo(pt.x, h)
                fillPath.lineTo(pt.x, pt.y)
            } else {
                val prev = points[i - 1]
                val midX = (prev.x + pt.x) / 2
                strokePath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
                fillPath.cubicTo(midX, prev.y, midX, pt.y, pt.x, pt.y)
            }
        }

        fillPath.lineTo(points.last().x, h)
        fillPath.close()

        // Draw Gradient Fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                listOf(fillColor, fillColor.copy(alpha = 0.0f)),
                startY = 0f,
                endY = h
            )
        )

        // Draw Stroke Line
        drawPath(
            path = strokePath,
            color = lineColor,
            style = Stroke(width = 2.5.dp.toPx(), cap = StrokeCap.Round)
        )

        // Draw Data Point Nodes
        points.forEach { pt ->
            drawCircle(
                color = lineColor,
                radius = 3.5.dp.toPx(),
                center = pt
            )
            drawCircle(
                color = Color.Black,
                radius = 1.5.dp.toPx(),
                center = pt
            )
        }
    }

    // X-Axis Labels
    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEach { label ->
            Text(text = label, color = TextMuted, fontSize = 9.sp)
        }
    }
}

/**
 * Custom Recharts-style Multi-Line Chart comparing two data flows
 */
@Composable
private fun RechartsMultiLineChart(
    line1: List<Float>,
    line2: List<Float>,
    labels: List<String>,
    color1: Color,
    color2: Color,
    modifier: Modifier = Modifier
) {
    val max1 = line1.maxOrNull() ?: 100f
    val max2 = line2.maxOrNull() ?: 100f

    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height - 20.dp.toPx()
        val spacing = w / (line1.size - 1)

        val points1 = line1.mapIndexed { index, value ->
            Offset(index * spacing, h - ((value / max1) * (h - 20.dp.toPx())) - 10.dp.toPx())
        }
        val points2 = line2.mapIndexed { index, value ->
            Offset(index * spacing, h - ((value / max2) * (h - 20.dp.toPx())) - 10.dp.toPx())
        }

        // Draw Line 1
        val path1 = Path()
        points1.forEachIndexed { i, pt ->
            if (i == 0) path1.moveTo(pt.x, pt.y)
            else {
                val prev = points1[i - 1]
                path1.cubicTo((prev.x + pt.x) / 2, prev.y, (prev.x + pt.x) / 2, pt.y, pt.x, pt.y)
            }
        }
        drawPath(path1, color = color1, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

        // Draw Line 2
        val path2 = Path()
        points2.forEachIndexed { i, pt ->
            if (i == 0) path2.moveTo(pt.x, pt.y)
            else {
                val prev = points2[i - 1]
                path2.cubicTo((prev.x + pt.x) / 2, prev.y, (prev.x + pt.x) / 2, pt.y, pt.x, pt.y)
            }
        }
        drawPath(path2, color = color2, style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round))

        points1.forEach { drawCircle(color1, radius = 3.dp.toPx(), center = it) }
        points2.forEach { drawCircle(color2, radius = 3.dp.toPx(), center = it) }
    }

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        labels.forEach { label ->
            Text(text = label, color = TextMuted, fontSize = 9.sp)
        }
    }
}

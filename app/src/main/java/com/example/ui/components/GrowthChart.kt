package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GrowthMeasurement
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GoldenHarvest
import com.example.ui.theme.LeafGreenSecondary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun GrowthChart(
    measurements: List<GrowthMeasurement>,
    modifier: Modifier = Modifier,
    title: String = "Grafik Tren Pertumbuhan Bibit"
) {
    var selectedMetric by remember { mutableStateOf("height") } // "height" or "leaf"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = if (selectedMetric == "height") "Perkembangan tinggi bibit (cm)" else "Pertambahan helai daun",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(
                        selected = selectedMetric == "height",
                        onClick = { selectedMetric = "height" },
                        label = { Text("Tinggi (cm)", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ForestGreenPrimary,
                            selectedLabelColor = Color.White
                        )
                    )
                    FilterChip(
                        selected = selectedMetric == "leaf",
                        onClick = { selectedMetric = "leaf" },
                        label = { Text("Daun", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = LeafGreenSecondary,
                            selectedLabelColor = Color.White
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (measurements.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Belum ada rekaman pengukuran untuk grafik.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                val sorted = remember(measurements) { measurements.sortedBy { it.measurementDate } }
                val sdf = remember {
                    SimpleDateFormat("dd/MM", Locale("id", "ID")).apply {
                        timeZone = TimeZone.getTimeZone("Asia/Makassar")
                    }
                }

                val primaryLineColor = if (selectedMetric == "height") ForestGreenPrimary else LeafGreenSecondary
                val values = sorted.map {
                    if (selectedMetric == "height") it.heightCm else it.leafCount.toFloat()
                }
                val maxValue = (values.maxOrNull() ?: 10f).coerceAtLeast(5f) * 1.25f

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    val w = size.width
                    val h = size.height - 40f
                    val topPadding = 20f

                    // Draw horizontal guide lines
                    val lineCount = 4
                    for (i in 0..lineCount) {
                        val y = topPadding + (h / lineCount) * i
                        drawLine(
                            color = Color(0xFFE0E5E0),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx()
                        )
                    }

                    if (sorted.size == 1) {
                        // Single point
                        val ptX = w / 2f
                        val ptY = topPadding + h - ((values[0] / maxValue) * h)
                        drawCircle(
                            color = primaryLineColor,
                            radius = 6.dp.toPx(),
                            center = Offset(ptX, ptY)
                        )
                    } else {
                        val stepX = w / (sorted.size - 1)
                        val points = mutableListOf<Offset>()

                        for (i in sorted.indices) {
                            val x = i * stepX
                            val valY = values[i]
                            val y = topPadding + h - ((valY / maxValue) * h)
                            points.add(Offset(x, y))
                        }

                        // Path for smooth line
                        val path = Path()
                        path.moveTo(points.first().x, points.first().y)
                        for (i in 1 until points.size) {
                            val prev = points[i - 1]
                            val curr = points[i]
                            val cX1 = (prev.x + curr.x) / 2f
                            val cY1 = prev.y
                            val cX2 = (prev.x + curr.x) / 2f
                            val cY2 = curr.y
                            path.cubicTo(cX1, cY1, cX2, cY2, curr.x, curr.y)
                        }

                        drawPath(
                            path = path,
                            color = primaryLineColor,
                            style = Stroke(width = 3.5.dp.toPx(), cap = StrokeCap.Round)
                        )

                        // Points and labels
                        for (i in points.indices) {
                            val p = points[i]
                            drawCircle(
                                color = Color.White,
                                radius = 5.dp.toPx(),
                                center = p
                            )
                            drawCircle(
                                color = primaryLineColor,
                                radius = 3.5.dp.toPx(),
                                center = p
                            )
                        }
                    }
                }

                // X Axis Dates
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    sorted.forEach { m ->
                        Text(
                            text = sdf.format(Date(m.measurementDate)),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats indicator pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    val latest = sorted.last()
                    val earliest = sorted.first()
                    val diff = if (selectedMetric == "height") {
                        "${String.format(Locale.US, "%.1f", (latest.heightCm - earliest.heightCm).coerceAtLeast(0f))} cm"
                    } else {
                        "+${(latest.leafCount - earliest.leafCount).coerceAtLeast(0)} helai"
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(ForestGreenPrimary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Terakhir: ${if (selectedMetric == "height") "${latest.heightCm} cm" else "${latest.leafCount} helai"}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(GoldenHarvest, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Total Kenaikan: $diff",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

package com.example.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.HealthAlert
import com.example.ui.PlantViewModel
import com.example.ui.components.AddAlertDialog
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GoldenHarvest
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusHealthy
import com.example.ui.theme.StatusMonitor
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun HealthAlertsScreen(
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val alerts by viewModel.healthAlerts.collectAsStateWithLifecycle()
    val batches by viewModel.batches.collectAsStateWithLifecycle()

    var showAddDialog by remember { mutableStateOf(false) }

    val sdf = remember {
        SimpleDateFormat("dd/MM/yyyy HH:mm", Locale("id", "ID")).apply {
            timeZone = TimeZone.getTimeZone("Asia/Makassar")
        }
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddDialog = true },
                containerColor = ForestGreenPrimary,
                contentColor = Color.White,
                shape = CircleShape,
                modifier = Modifier.padding(bottom = 72.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Tambah Alert")
            }
        }
    ) { paddingVals ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(paddingVals)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Header
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreenPrimary)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Kesehatan Tanaman & Peringatan Dini",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Deteksi otomatis klorosis, etiolasi, penyakit rebah & penurunan survival rate.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFC8E6C9)
                )
            }

            // Health Criteria Info Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Klasifikasi Kondisi Bibit Nursery",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = ForestGreenPrimary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        ConditionPill("Sehat", StatusHealthy)
                        ConditionPill("Perlu Dipantau", StatusMonitor)
                        ConditionPill("Penanganan", StatusDanger)
                        ConditionPill("Siap Pindah", Color(0xFF0288D1))
                    }
                }
            }

            if (alerts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tidak ada peringatan masalah kesehatan. Semua bibit terpantau baik!",
                        style = MaterialTheme.typography.bodyMedium,
                        color = StatusHealthy
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 120.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(alerts) { alert ->
                        val targetBatch = batches.find { it.id == alert.batchId }
                        val alertColor = when (alert.severity) {
                            "DANGER" -> StatusDanger
                            "WARNING" -> StatusMonitor
                            else -> Color(0xFF0288D1)
                        }

                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = if (alert.isResolved) Color(0xFFF5F5F5) else MaterialTheme.colorScheme.surface
                            ),
                            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Box(
                                            modifier = Modifier
                                                .size(34.dp)
                                                .background(alertColor.copy(alpha = 0.15f), CircleShape),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = when (alert.severity) {
                                                    "DANGER" -> Icons.Default.Error
                                                    "WARNING" -> Icons.Default.Warning
                                                    else -> Icons.Default.Info
                                                },
                                                contentDescription = null,
                                                tint = alertColor,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = alert.title,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = if (alert.isResolved) Color.Gray else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${targetBatch?.plantName ?: "Batch"} (${targetBatch?.batchCode ?: ""})",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }

                                    IconButton(
                                        onClick = { viewModel.deleteAlert(alert.id) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(16.dp))
                                    }
                                }

                                if (alert.symptom.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = "Gejala: ${alert.symptom}",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                }

                                if (alert.recommendation.isNotBlank()) {
                                    Spacer(modifier = Modifier.height(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFF1F8E9), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Column {
                                            Text(
                                                text = "Rekomendasi Agronomi:",
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = ForestGreenPrimary
                                            )
                                            Text(
                                                text = alert.recommendation,
                                                fontSize = 12.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = sdf.format(Date(alert.timestamp)),
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )

                                    Button(
                                        onClick = { viewModel.resolveAlert(alert.id, !alert.isResolved) },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (alert.isResolved) Color.Gray else ForestGreenPrimary
                                        ),
                                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        if (alert.isResolved) {
                                            Text("Selesai Ditangani ✓", fontSize = 11.sp)
                                        } else {
                                            Text("Tandai Selesai", fontSize = 11.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddAlertDialog(
            batches = batches,
            onDismiss = { showAddDialog = false },
            onConfirm = { bId, title, sev, sym, rec ->
                viewModel.addHealthAlert(bId, title, sev, sym, rec)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun ConditionPill(name: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f))
            .padding(horizontal = 6.dp, vertical = 3.dp)
    ) {
        Text(text = name, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = color)
    }
}

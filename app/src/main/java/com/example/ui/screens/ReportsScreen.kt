package com.example.ui.screens

import android.content.Intent
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.PlantViewModel
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GoldenHarvest
import com.example.ui.theme.LeafGreenSecondary
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Composable
fun ReportsScreen(
    viewModel: PlantViewModel,
    modifier: Modifier = Modifier
) {
    val batches by viewModel.batches.collectAsStateWithLifecycle()
    val measurements by viewModel.measurements.collectAsStateWithLifecycle()
    val careTasks by viewModel.careTasks.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val totalSeeds = batches.sumOf { it.seedsPlanted }
    val totalInitial = batches.sumOf { it.initialSeedlings }
    val totalActiveBatches = batches.count { it.status == "Aktif" }
    val readyTransplant = batches.count { it.status == "Siap Pindah Tanam" }

    val sdf = SimpleDateFormat("dd MMMM yyyy, HH:mm 'WITA'", Locale("id", "ID")).apply {
        timeZone = TimeZone.getTimeZone("Asia/Makassar")
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(bottom = 120.dp)
    ) {
        // Header
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ForestGreenPrimary)
                    .padding(horizontal = 16.dp, vertical = 14.dp)
            ) {
                Text(
                    text = "Laporan & Analitik Pertumbuhan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Rekapitulasi performa pembibitan, survival rate, dan ekspor CSV.",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFC8E6C9)
                )
            }
        }

        // Summary Card
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Ringkasan Eksekutif Nursery",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = ForestGreenPrimary
                        )
                        Icon(
                            imageVector = Icons.Default.Assessment,
                            contentDescription = null,
                            tint = GoldenHarvest
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF7F9F6), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Total Benih", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalSeeds butir", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Bibit Awal", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$totalInitial btg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LeafGreenSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Siap Pindah", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("$readyTransplant batch", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldenHarvest)
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Button(
                        onClick = {
                            coroutineScope.launch {
                                val sb = StringBuilder()
                                sb.append("=== LAPORAN LENGKAP TANAM BOSQUU ===\n")
                                sb.append("Waktu: ${sdf.format(Date())}\n")
                                sb.append("Total Batch Semai: ${batches.size}\n")
                                sb.append("Total Benih Disemai: $totalSeeds butir\n")
                                sb.append("Total Bibit Hidup: $totalInitial batang\n\n")

                                for (b in batches) {
                                    sb.append("-----------------------------\n")
                                    sb.append("Batch: ${b.batchCode} - ${b.plantName} (${b.variety})\n")
                                    sb.append("Kategori: ${b.category} | Status: ${b.status}\n")
                                    sb.append("Wadah: ${b.trayId} | Media: ${b.growingMedium}\n")
                                    sb.append("PIC: ${b.picName} | Lokasi: ${b.nurseryLocation}\n")
                                    val bMeas = measurements.filter { it.batchId == b.id }
                                    sb.append("Jumlah Pengukuran: ${bMeas.size} kali\n")
                                    bMeas.lastOrNull()?.let { last ->
                                        sb.append("Tinggi Terakhir: ${last.heightCm} cm | Daun: ${last.leafCount} helai\n")
                                        sb.append("Bibit Hidup: ${last.survivingCount} batang\n")
                                    }
                                }

                                val sendIntent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, sb.toString())
                                    type = "text/plain"
                                }
                                context.startActivity(Intent.createChooser(sendIntent, "Ekspor Ringkasan Laporan"))
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_export_all_report")
                    ) {
                        Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Bagikan / Salin Ringkasan Laporan (Teks)")
                    }
                }
            }
        }

        item {
            Text(
                text = "Laporan Per Batch Semai",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
            )
        }

        items(batches) { b ->
            val bMeas = measurements.filter { it.batchId == b.id }
            val latestM = bMeas.maxByOrNull { it.measurementDate }
            val survival = if (b.initialSeedlings > 0 && latestM != null) {
                (latestM.survivingCount.toFloat() / b.initialSeedlings.toFloat()) * 100f
            } else 100f

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(b.batchCode, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldenHarvest)
                            Text(b.plantName, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            Text("${b.variety} • ${b.category}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }

                        Button(
                            onClick = {
                                coroutineScope.launch {
                                    val csv = viewModel.getBatchCsv(b)
                                    val sendIntent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, csv)
                                        type = "text/plain"
                                    }
                                    context.startActivity(Intent.createChooser(sendIntent, "Ekspor CSV Batch ${b.batchCode}"))
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = LeafGreenSecondary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CSV", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF4F6F3), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tinggi Akhir", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${latestM?.heightCm ?: 0f} cm", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Daun", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${latestM?.leafCount ?: 0} helai", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Survival Rate", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text("${String.format(Locale.US, "%.1f", survival)}%", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Status", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(b.status, fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = ForestGreenPrimary)
                        }
                    }
                }
            }
        }
    }
}

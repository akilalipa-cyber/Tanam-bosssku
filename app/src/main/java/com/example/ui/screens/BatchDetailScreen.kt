package com.example.ui.screens

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.CareTask
import com.example.data.model.GrowthMeasurement
import com.example.data.model.PlantBatch
import com.example.ui.PlantViewModel
import com.example.ui.components.AddCareTaskDialog
import com.example.ui.components.AddMeasurementDialog
import com.example.ui.components.GrowthChart
import com.example.ui.theme.ForestGreenDark
import com.example.ui.theme.ForestGreenPrimary
import com.example.ui.theme.GoldenHarvest
import com.example.ui.theme.LeafGreenSecondary
import com.example.ui.theme.StatusDanger
import com.example.ui.theme.StatusReady
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BatchDetailScreen(
    batchId: String,
    viewModel: PlantViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val batches by viewModel.batches.collectAsStateWithLifecycle()
    val measurements by viewModel.measurements.collectAsStateWithLifecycle()
    val careTasks by viewModel.careTasks.collectAsStateWithLifecycle()

    val batch = batches.find { it.id == batchId }
    val batchMeasurements = measurements.filter { it.batchId == batchId }.sortedBy { it.measurementDate }
    val batchTasks = careTasks.filter { it.batchId == batchId }.sortedByDescending { it.scheduledDate }

    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Ringkasan", "Grafik Pertumbuhan", "Riwayat Ukur", "Jadwal Perawatan", "Foto Bibit")

    var showAddMeasurementDialog by remember { mutableStateOf(false) }
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var showDeleteConfirmDialog by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    if (batch == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Batch tidak ditemukan atau telah dihapus.")
        }
        return
    }

    val ageDays = ((System.currentTimeMillis() - batch.plantingDate) / 86400000L).coerceAtLeast(0)

    // Automated Calculations
    val latestM = batchMeasurements.lastOrNull()
    val earliestM = batchMeasurements.firstOrNull()

    val survivalRate = if (batch.initialSeedlings > 0 && latestM != null) {
        (latestM.survivingCount.toFloat() / batch.initialSeedlings.toFloat()) * 100f
    } else 100f

    val germinationRate = if (batch.seedsPlanted > 0 && latestM != null) {
        (latestM.germinatedCount.toFloat() / batch.seedsPlanted.toFloat()) * 100f
    } else 95f

    val avgDailyGrowth = if (batchMeasurements.size >= 2 && latestM != null && earliestM != null) {
        val daysBetween = ((latestM.measurementDate - earliestM.measurementDate) / 86400000L).coerceAtLeast(1)
        (latestM.heightCm - earliestM.heightCm) / daysBetween
    } else 0f

    val sdf = remember {
        SimpleDateFormat("dd MMMM yyyy", Locale("id", "ID")).apply {
            timeZone = TimeZone.getTimeZone("Asia/Makassar")
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ForestGreenPrimary)
                .padding(horizontal = 8.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Kembali",
                        tint = Color.White
                    )
                }
                Column {
                    Text(
                        text = batch.plantName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "${batch.batchCode} • ${batch.category}",
                        color = Color(0xFFC8E6C9),
                        fontSize = 11.sp
                    )
                }
            }

            Row {
                IconButton(
                    onClick = {
                        coroutineScope.launch {
                            val csvData = viewModel.getBatchCsv(batch)
                            val sendIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_TEXT, csvData)
                                type = "text/plain"
                            }
                            context.startActivity(Intent.createChooser(sendIntent, "Ekspor Laporan Batch"))
                        }
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "Bagikan",
                        tint = Color.White
                    )
                }

                IconButton(onClick = { showDeleteConfirmDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Hapus",
                        tint = Color(0xFFFFCDD2)
                    )
                }
            }
        }

        // Header Metrics Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ForestGreenDark)
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Umur Semai", fontSize = 10.sp, color = Color(0xFFA5D6A7))
                Text("$ageDays Hari", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Survival Rate", fontSize = 10.sp, color = Color(0xFFA5D6A7))
                Text("${String.format(Locale.US, "%.1f", survivalRate)}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color(0xFFFFD54F))
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Daya Kecambah", fontSize = 10.sp, color = Color(0xFFA5D6A7))
                Text("${String.format(Locale.US, "%.1f", germinationRate)}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Laju/Hari", fontSize = 10.sp, color = Color(0xFFA5D6A7))
                Text("${String.format(Locale.US, "%.2f", avgDailyGrowth)} cm", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }
        }

        // Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = ForestGreenPrimary,
            edgePadding = 16.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = ForestGreenPrimary
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                )
            }
        }

        // Action Toolbar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = { showAddMeasurementDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_catat_ukur"),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Catat Ukur", fontSize = 12.sp)
            }

            OutlinedButton(
                onClick = { showAddTaskDialog = true },
                modifier = Modifier
                    .weight(1f)
                    .testTag("btn_tambah_tugas"),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                Icon(Icons.Default.WaterDrop, contentDescription = null, modifier = Modifier.size(16.dp), tint = ForestGreenPrimary)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Jadwal Rawat", fontSize = 12.sp, color = ForestGreenPrimary)
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                0 -> SummaryTab(batch = batch, sdf = sdf)
                1 -> ChartTab(measurements = batchMeasurements)
                2 -> MeasurementsTab(batch = batch, measurements = batchMeasurements, sdf = sdf, onDelete = { viewModel.deleteMeasurement(it) })
                3 -> TasksTab(tasks = batchTasks, sdf = sdf, onToggle = { t -> viewModel.toggleTaskComplete(t.id, t.isCompleted) })
                4 -> PhotosTab(measurements = batchMeasurements, sdf = sdf)
            }
        }
    }

    if (showAddMeasurementDialog) {
        AddMeasurementDialog(
            batch = batch,
            onDismiss = { showAddMeasurementDialog = false },
            onConfirm = { h, l, s, surv, germ, leafC, stemC, rootC, moisture, health, obs ->
                viewModel.addMeasurement(
                    batch = batch,
                    heightCm = h,
                    leafCount = l,
                    stemDiameterMm = s,
                    survivingCount = surv,
                    germinatedCount = germ,
                    leafCondition = leafC,
                    stemCondition = stemC,
                    rootCondition = rootC,
                    mediumMoisture = moisture,
                    healthStatus = health,
                    observations = obs
                )
                showAddMeasurementDialog = false
            }
        )
    }

    if (showAddTaskDialog) {
        AddCareTaskDialog(
            batches = batches,
            selectedBatchId = batch.id,
            onDismiss = { showAddTaskDialog = false },
            onConfirm = { bId, tType, schedDate, dosage, method, item, pic, notes ->
                viewModel.addCareTask(bId, tType, schedDate, dosage, method, item, pic, notes)
                showAddTaskDialog = false
            }
        )
    }

    if (showDeleteConfirmDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmDialog = false },
            title = { Text("Konfirmasi Hapus Batch") },
            text = { Text("Apakah Anda yakin ingin menghapus batch '${batch.plantName}' (${batch.batchCode}) beserta seluruh riwayat pengukurannya?") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.deleteBatch(batch.id)
                        showDeleteConfirmDialog = false
                        onBack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = StatusDanger)
                ) {
                    Text("Hapus")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteConfirmDialog = false }) {
                    Text("Batal")
                }
            }
        )
    }
}

@Composable
private fun SummaryTab(batch: PlantBatch, sdf: SimpleDateFormat) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Identitas & Lokasi Semai", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow("Varietas / Kultivar", batch.variety.ifBlank { "-" })
                    DetailRow("Nama Ilmiah (Latin)", batch.scientificName.ifBlank { "-" })
                    DetailRow("Kategori Tanaman", batch.category)
                    DetailRow("Asal / Sumber Benih", batch.seedSource.ifBlank { "-" })
                    DetailRow("Lokasi Persemaian", batch.nurseryLocation.ifBlank { "-" })
                    DetailRow("Wadah / Tray ID", batch.trayId.ifBlank { "-" })
                    DetailRow("Media Tanam", batch.growingMedium.ifBlank { "-" })
                    DetailRow("Penanggung Jawab (PIC)", batch.picName.ifBlank { "-" })
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Parameter & Target Siklus", fontWeight = FontWeight.Bold, color = ForestGreenPrimary, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(10.dp))
                    DetailRow("Tanggal Semai", sdf.format(Date(batch.plantingDate)))
                    DetailRow("Benih Disemai", "${batch.seedsPlanted} biji")
                    DetailRow("Bibit Awal Hidup", "${batch.initialSeedlings} batang")
                    DetailRow("Estimasi Berkecambah", "${batch.expectedGerminationDays} hari")
                    if (batch.expectedTransplantDate > 0) {
                        DetailRow("Estimasi Pindah Tanam", sdf.format(Date(batch.expectedTransplantDate)))
                    }
                    DetailRow("Status Saat Ini", batch.status)
                    if (batch.notes.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Text("Catatan:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(batch.notes, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartTab(measurements: List<GrowthMeasurement>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp)
    ) {
        item {
            GrowthChart(
                measurements = measurements,
                title = "Grafik Pertumbuhan Batch Ini"
            )
        }
    }
}

@Composable
private fun MeasurementsTab(
    batch: PlantBatch,
    measurements: List<GrowthMeasurement>,
    sdf: SimpleDateFormat,
    onDelete: (GrowthMeasurement) -> Unit
) {
    if (measurements.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada rekaman pengukuran untuk batch ini.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(measurements.reversed()) { m ->
            val ageDays = ((m.measurementDate - batch.plantingDate) / 86400000L).coerceAtLeast(0)
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = sdf.format(Date(m.measurementDate)),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = ForestGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text("Hari ke-$ageDays", fontSize = 11.sp, color = ForestGreenPrimary, fontWeight = FontWeight.SemiBold)
                            }
                        }

                        IconButton(onClick = { onDelete(m) }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.Delete, contentDescription = "Hapus", tint = Color.Gray, modifier = Modifier.size(16.dp))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF9FAF8), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tinggi", fontSize = 10.sp, color = Color.Gray)
                            Text("${m.heightCm} cm", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ForestGreenPrimary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Daun", fontSize = 10.sp, color = Color.Gray)
                            Text("${m.leafCount} helai", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = LeafGreenSecondary)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Batang", fontSize = 10.sp, color = Color.Gray)
                            Text("${m.stemDiameterMm} mm", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Hidup", fontSize = 10.sp, color = Color.Gray)
                            Text("${m.survivingCount} btg", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = GoldenHarvest)
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Daun: ${m.leafCondition} • Batang: ${m.stemCondition} • Kelembapan: ${m.mediumMoisture}",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (m.observations.isNotBlank()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Catatan: ${m.observations}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TasksTab(
    tasks: List<CareTask>,
    sdf: SimpleDateFormat,
    onToggle: (CareTask) -> Unit
) {
    if (tasks.isEmpty()) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Belum ada jadwal perawatan untuk batch ini.", color = Color.Gray)
        }
        return
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(tasks) { task ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = task.taskType,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = if (task.isCompleted) Color.Gray else ForestGreenPrimary
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = sdf.format(Date(task.scheduledDate)),
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Text(
                            text = "${task.fertilizerOrItem} • ${task.dosageOrVolume} (${task.method})",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Button(
                        onClick = { onToggle(task) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (task.isCompleted) Color(0xFF81C784) else ForestGreenPrimary
                        ),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(if (task.isCompleted) "Selesai ✓" else "Selesaikan", fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun PhotosTab(measurements: List<GrowthMeasurement>, sdf: SimpleDateFormat) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Dokumentasi Foto Perkembangan Bibit",
                        fontWeight = FontWeight.Bold,
                        color = ForestGreenPrimary,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Rekam jejak visual dari hari penyemaian hingga siap pindah tanam.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        items(measurements.reversed()) { m ->
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = sdf.format(Date(m.measurementDate)),
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = ForestGreenPrimary
                        )
                        Text(
                            text = "${m.heightCm} cm • ${m.leafCount} Daun",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            color = LeafGreenSecondary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Image(
                        painter = painterResource(id = R.drawable.hero_seedling_banner),
                        contentDescription = "Foto Bibit",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(140.dp)
                            .clip(RoundedCornerShape(10.dp)),
                        contentScale = ContentScale.Crop
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = m.observations.ifBlank { "Kondisi: ${m.leafCondition}, Batang: ${m.stemCondition}, Status: ${m.healthStatus}" },
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.onSurface)
    }
}

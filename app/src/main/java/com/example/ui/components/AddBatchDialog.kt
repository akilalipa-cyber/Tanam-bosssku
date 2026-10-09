package com.example.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ForestGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBatchDialog(
    onDismiss: () -> Unit,
    onConfirm: (
        plantName: String,
        scientificName: String,
        category: String,
        variety: String,
        seedsPlanted: Int,
        initialSeedlings: Int,
        plantingDate: Long,
        seedSource: String,
        nurseryLocation: String,
        growingMedium: String,
        trayId: String,
        picName: String,
        expectedGerminationDays: Int,
        expectedTransplantDays: Int,
        notes: String
    ) -> Unit
) {
    var plantName by remember { mutableStateOf("") }
    var scientificName by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Sayuran") }
    var variety by remember { mutableStateOf("") }
    var seedsPlantedStr by remember { mutableStateOf("100") }
    var initialSeedlingsStr by remember { mutableStateOf("95") }
    var seedSource by remember { mutableStateOf("Benih Unggul Lokal") }
    var nurseryLocation by remember { mutableStateOf("Greenhouse Blok A") }
    var growingMedium by remember { mutableStateOf("Cocopeat + Sekam Bakar") }
    var trayId by remember { mutableStateOf("Tray Semai #01") }
    var picName by remember { mutableStateOf("Akil Alipa") }
    var germinationDaysStr by remember { mutableStateOf("5") }
    var transplantDaysStr by remember { mutableStateOf("25") }
    var notes by remember { mutableStateOf("") }

    val categories = listOf(
        "Sayuran",
        "Buah-buahan",
        "Tanaman perkebunan",
        "Tanaman hias",
        "Tanaman kehutanan",
        "Tanaman obat",
        "Lainnya"
    )
    var categoryExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(20.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = "Tambah Batch Semai Baru",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
                Text(
                    text = "Daftarkan bibit tanaman baru untuk dimonitoring.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = plantName,
                    onValueChange = { plantName = it },
                    label = { Text("Nama Tanaman *") },
                    placeholder = { Text("Contoh: Cabai Rawit Merah") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_plant_name"),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = scientificName,
                    onValueChange = { scientificName = it },
                    label = { Text("Nama Ilmiah (Latin)") },
                    placeholder = { Text("Contoh: Capsicum frutescens") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = !categoryExpanded }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kategori Tanaman") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = variety,
                    onValueChange = { variety = it },
                    label = { Text("Varietas / Kultivar") },
                    placeholder = { Text("Contoh: Ori 212 / Servo F1") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = seedsPlantedStr,
                        onValueChange = { seedsPlantedStr = it },
                        label = { Text("Benih Disemai") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = initialSeedlingsStr,
                        onValueChange = { initialSeedlingsStr = it },
                        label = { Text("Bibit Hidup Awal") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = seedSource,
                    onValueChange = { seedSource = it },
                    label = { Text("Asal / Sumber Benih") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = nurseryLocation,
                    onValueChange = { nurseryLocation = it },
                    label = { Text("Lokasi Persemaian / Rak") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = growingMedium,
                    onValueChange = { growingMedium = it },
                    label = { Text("Media Tanam") },
                    placeholder = { Text("Contoh: Cocopeat, Rockwool, Sekam") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = trayId,
                        onValueChange = { trayId = it },
                        label = { Text("ID Wadah / Tray") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = picName,
                        onValueChange = { picName = it },
                        label = { Text("PIC Penanggung Jawab") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = germinationDaysStr,
                        onValueChange = { germinationDaysStr = it },
                        label = { Text("Hari Berkecambah") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = transplantDaysStr,
                        onValueChange = { transplantDaysStr = it },
                        label = { Text("Hari Pindah Tanam") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Catatan Tambahan") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(onClick = onDismiss) {
                        Text("Batal")
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Button(
                        onClick = {
                            if (plantName.isNotBlank()) {
                                onConfirm(
                                    plantName.trim(),
                                    scientificName.trim(),
                                    category,
                                    variety.trim(),
                                    seedsPlantedStr.toIntOrNull() ?: 100,
                                    initialSeedlingsStr.toIntOrNull() ?: 90,
                                    System.currentTimeMillis(),
                                    seedSource.trim(),
                                    nurseryLocation.trim(),
                                    growingMedium.trim(),
                                    trayId.trim(),
                                    picName.trim(),
                                    germinationDaysStr.toIntOrNull() ?: 5,
                                    transplantDaysStr.toIntOrNull() ?: 25,
                                    notes.trim()
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        modifier = Modifier.testTag("submit_add_batch")
                    ) {
                        Text("Simpan Batch")
                    }
                }
            }
        }
    }
}

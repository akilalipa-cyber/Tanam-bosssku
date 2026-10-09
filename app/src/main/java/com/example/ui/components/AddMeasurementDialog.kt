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
import com.example.data.model.PlantBatch
import com.example.ui.theme.ForestGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddMeasurementDialog(
    batch: PlantBatch,
    onDismiss: () -> Unit,
    onConfirm: (
        heightCm: Float,
        leafCount: Int,
        stemDiameterMm: Float,
        survivingCount: Int,
        germinatedCount: Int,
        leafCondition: String,
        stemCondition: String,
        rootCondition: String,
        mediumMoisture: String,
        healthStatus: String,
        observations: String
    ) -> Unit
) {
    var heightCmStr by remember { mutableStateOf("5.0") }
    var leafCountStr by remember { mutableStateOf("4") }
    var stemDiameterMmStr by remember { mutableStateOf("1.5") }
    var survivingCountStr by remember { mutableStateOf(batch.initialSeedlings.toString()) }
    var germinatedCountStr by remember { mutableStateOf(batch.initialSeedlings.toString()) }

    var leafCondition by remember { mutableStateOf("Hijau Segar") }
    var stemCondition by remember { mutableStateOf("Kokoh") }
    var rootCondition by remember { mutableStateOf("Putih Sehat") }
    var mediumMoisture by remember { mutableStateOf("Optimal (60-70%)") }
    var healthStatus by remember { mutableStateOf("Sehat") }
    var observations by remember { mutableStateOf("") }

    val leafOptions = listOf("Hijau Segar", "Hijau Muda", "Kekuningan (Klorosis)", "Bercak Cokelat", "Layu")
    val stemOptions = listOf("Kokoh", "Normal", "Kurus/Etiolasi", "Lemah/Rebah")
    val rootOptions = listOf("Putih Sehat", "Padat & Berbulu", "Cokelat/Busuk", "Terbatas")
    val moistureOptions = listOf("Optimal (60-70%)", "Kering (<40%)", "Terlalu Basah (>80%)")
    val healthOptions = listOf("Sehat", "Perlu Dipantau", "Perlu Penanganan", "Siap Pindah Tanam")

    var leafExp by remember { mutableStateOf(false) }
    var stemExp by remember { mutableStateOf(false) }
    var moistureExp by remember { mutableStateOf(false) }
    var healthExp by remember { mutableStateOf(false) }

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
                    text = "Catat Pertumbuhan Bibit",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
                Text(
                    text = "${batch.plantName} (${batch.batchCode})",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = heightCmStr,
                        onValueChange = { heightCmStr = it },
                        label = { Text("Tinggi (cm) *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_height"),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = leafCountStr,
                        onValueChange = { leafCountStr = it },
                        label = { Text("Helai Daun *") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_leaf_count"),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = stemDiameterMmStr,
                        onValueChange = { stemDiameterMmStr = it },
                        label = { Text("Diameter Batang (mm)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = survivingCountStr,
                        onValueChange = { survivingCountStr = it },
                        label = { Text("Bibit Hidup") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = germinatedCountStr,
                    onValueChange = { germinatedCountStr = it },
                    label = { Text("Jumlah Benih Berkecambah") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Leaf condition dropdown
                ExposedDropdownMenuBox(
                    expanded = leafExp,
                    onExpandedChange = { leafExp = !leafExp }
                ) {
                    OutlinedTextField(
                        value = leafCondition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kondisi & Warna Daun") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = leafExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = leafExp,
                        onDismissRequest = { leafExp = false }
                    ) {
                        leafOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    leafCondition = opt
                                    leafExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Stem condition dropdown
                ExposedDropdownMenuBox(
                    expanded = stemExp,
                    onExpandedChange = { stemExp = !stemExp }
                ) {
                    OutlinedTextField(
                        value = stemCondition,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kondisi Batang") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = stemExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = stemExp,
                        onDismissRequest = { stemExp = false }
                    ) {
                        stemOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    stemCondition = opt
                                    stemExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Moisture dropdown
                ExposedDropdownMenuBox(
                    expanded = moistureExp,
                    onExpandedChange = { moistureExp = !moistureExp }
                ) {
                    OutlinedTextField(
                        value = mediumMoisture,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Kelembapan Media Tanam") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = moistureExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = moistureExp,
                        onDismissRequest = { moistureExp = false }
                    ) {
                        moistureOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    mediumMoisture = opt
                                    moistureExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Health status dropdown
                ExposedDropdownMenuBox(
                    expanded = healthExp,
                    onExpandedChange = { healthExp = !healthExp }
                ) {
                    OutlinedTextField(
                        value = healthStatus,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Status Kesehatan Tanaman") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = healthExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = healthExp,
                        onDismissRequest = { healthExp = false }
                    ) {
                        healthOptions.forEach { opt ->
                            DropdownMenuItem(
                                text = { Text(opt) },
                                onClick = {
                                    healthStatus = opt
                                    healthExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = observations,
                    onValueChange = { observations = it },
                    label = { Text("Catatan Observasi Pertumbuhan") },
                    placeholder = { Text("Contoh: Kotiledon membuka sempurna, pucuk daun sehat") },
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
                            val h = heightCmStr.toFloatOrNull() ?: 1.0f
                            val l = leafCountStr.toIntOrNull() ?: 2
                            val s = stemDiameterMmStr.toFloatOrNull() ?: 1.0f
                            val surv = survivingCountStr.toIntOrNull() ?: batch.initialSeedlings
                            val germ = germinatedCountStr.toIntOrNull() ?: batch.initialSeedlings
                            onConfirm(
                                h, l, s, surv, germ,
                                leafCondition, stemCondition, rootCondition,
                                mediumMoisture, healthStatus, observations
                            )
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        modifier = Modifier.testTag("submit_add_measurement")
                    ) {
                        Text("Simpan Data")
                    }
                }
            }
        }
    }
}

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PlantBatch
import com.example.ui.theme.ForestGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddAlertDialog(
    batches: List<PlantBatch>,
    selectedBatchId: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        batchId: String,
        title: String,
        severity: String,
        symptom: String,
        recommendation: String
    ) -> Unit
) {
    var chosenBatchId by remember {
        mutableStateOf(selectedBatchId ?: batches.firstOrNull()?.id ?: "")
    }
    var title by remember { mutableStateOf("") }
    var severity by remember { mutableStateOf("WARNING") }
    var symptom by remember { mutableStateOf("") }
    var recommendation by remember { mutableStateOf("") }

    val severities = listOf("INFO", "WARNING", "DANGER")
    var sevExp by remember { mutableStateOf(false) }
    var batchExp by remember { mutableStateOf(false) }

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
                    text = "Catat Masalah / Kesehatan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
                Text(
                    text = "Laporkan anomali, serangan hama, atau daun layu/menguning.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                ExposedDropdownMenuBox(
                    expanded = batchExp,
                    onExpandedChange = { batchExp = !batchExp }
                ) {
                    val currentBatch = batches.find { it.id == chosenBatchId }
                    OutlinedTextField(
                        value = currentBatch?.let { "${it.plantName} (${it.batchCode})" } ?: "Pilih Batch",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Target Batch Semai") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = batchExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = batchExp,
                        onDismissRequest = { batchExp = false }
                    ) {
                        batches.forEach { b ->
                            DropdownMenuItem(
                                text = { Text("${b.plantName} - ${b.batchCode}") },
                                onClick = {
                                    chosenBatchId = b.id
                                    batchExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Judul Masalah / Indikasi *") },
                    placeholder = { Text("Contoh: Daun Klorosis Menguning") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                ExposedDropdownMenuBox(
                    expanded = sevExp,
                    onExpandedChange = { sevExp = !sevExp }
                ) {
                    OutlinedTextField(
                        value = when (severity) {
                            "DANGER" -> "Bahaya / Tindakan Mendesak"
                            "WARNING" -> "Perlu Perhatian / Pemantauan"
                            else -> "Informasi / Catatan"
                        },
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Tingkat Urgensi") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = sevExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = sevExp,
                        onDismissRequest = { sevExp = false }
                    ) {
                        severities.forEach { s ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        when (s) {
                                            "DANGER" -> "Bahaya / Tindakan Mendesak"
                                            "WARNING" -> "Perlu Perhatian / Pemantauan"
                                            else -> "Informasi / Catatan"
                                        }
                                    )
                                },
                                onClick = {
                                    severity = s
                                    sevExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = symptom,
                    onValueChange = { symptom = it },
                    label = { Text("Gejala yang Terlihat") },
                    placeholder = { Text("Contoh: Ujung daun kering kecokelatan, batang agak rebah") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = recommendation,
                    onValueChange = { recommendation = it },
                    label = { Text("Tindakan / Rekomendasi Solusi") },
                    placeholder = { Text("Contoh: Pindahkan ke tempat teduh dan semprot fungisida organik") },
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
                            if (title.isNotBlank() && chosenBatchId.isNotBlank()) {
                                onConfirm(chosenBatchId, title.trim(), severity, symptom.trim(), recommendation.trim())
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary)
                    ) {
                        Text("Simpan Alert")
                    }
                }
            }
        }
    }
}

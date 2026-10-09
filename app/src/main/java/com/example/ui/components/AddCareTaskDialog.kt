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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.data.model.PlantBatch
import com.example.ui.theme.ForestGreenPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddCareTaskDialog(
    batches: List<PlantBatch>,
    selectedBatchId: String? = null,
    onDismiss: () -> Unit,
    onConfirm: (
        batchId: String,
        taskType: String,
        scheduledDate: Long,
        dosageOrVolume: String,
        method: String,
        fertilizerOrItem: String,
        picName: String,
        notes: String
    ) -> Unit
) {
    var chosenBatchId by remember {
        mutableStateOf(selectedBatchId ?: batches.firstOrNull()?.id ?: "")
    }
    var taskType by remember { mutableStateOf("Penyiraman") }
    var dosageOrVolume by remember { mutableStateOf("1.5 Liter") }
    var method by remember { mutableStateOf("Sprayer kabut") }
    var fertilizerOrItem by remember { mutableStateOf("Air Baku pH 6.5") }
    var picName by remember { mutableStateOf("Akil Alipa") }
    var notes by remember { mutableStateOf("") }

    val taskTypes = listOf("Penyiraman", "Pemupukan", "Inspeksi Hama", "Penyiangan", "Pindah Tanam", "Pruning")
    var taskTypeExp by remember { mutableStateOf(false) }
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
                    text = "Jadwalkan Perawatan",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = ForestGreenPrimary
                )
                Text(
                    text = "Buat tugas penyiraman, pemupukan, atau inspeksi nursery.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Select Batch
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

                // Task Type
                ExposedDropdownMenuBox(
                    expanded = taskTypeExp,
                    onExpandedChange = { taskTypeExp = !taskTypeExp }
                ) {
                    OutlinedTextField(
                        value = taskType,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Jenis Tugas Perawatan") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = taskTypeExp) },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = taskTypeExp,
                        onDismissRequest = { taskTypeExp = false }
                    ) {
                        taskTypes.forEach { t ->
                            DropdownMenuItem(
                                text = { Text(t) },
                                onClick = {
                                    taskType = t
                                    when (t) {
                                        "Penyiraman" -> {
                                            dosageOrVolume = "1.5 Liter"
                                            method = "Sprayer kabut"
                                            fertilizerOrItem = "Air Baku pH 6.5"
                                        }
                                        "Pemupukan" -> {
                                            dosageOrVolume = "2 ml / Liter"
                                            method = "Kocor akar / Foliar"
                                            fertilizerOrItem = "POC Daun Kelor / NPK 16-16-16"
                                        }
                                        "Inspeksi Hama" -> {
                                            dosageOrVolume = "Pemeriksaan daun bawah"
                                            method = "Observasi visual mikroskopis"
                                            fertilizerOrItem = "Cek kutu kebul & thrips"
                                        }
                                        else -> {}
                                    }
                                    taskTypeExp = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = fertilizerOrItem,
                    onValueChange = { fertilizerOrItem = it },
                    label = { Text("Bahan / Jenis Pupuk / Air") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = dosageOrVolume,
                        onValueChange = { dosageOrVolume = it },
                        label = { Text("Dosis / Volume") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = method,
                        onValueChange = { method = it },
                        label = { Text("Metode") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = picName,
                    onValueChange = { picName = it },
                    label = { Text("Penanggung Jawab (PIC)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Instruksi / Catatan") },
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
                            if (chosenBatchId.isNotBlank()) {
                                onConfirm(
                                    chosenBatchId,
                                    taskType,
                                    System.currentTimeMillis(),
                                    dosageOrVolume,
                                    method,
                                    fertilizerOrItem,
                                    picName,
                                    notes
                                )
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ForestGreenPrimary),
                        modifier = Modifier.testTag("submit_add_task")
                    ) {
                        Text("Simpan Jadwal")
                    }
                }
            }
        }
    }
}

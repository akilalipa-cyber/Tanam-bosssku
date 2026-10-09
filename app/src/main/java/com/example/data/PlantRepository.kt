package com.example.data

import com.example.data.dao.PlantDao
import com.example.data.model.CareTask
import com.example.data.model.GrowthMeasurement
import com.example.data.model.HealthAlert
import com.example.data.model.PlantBatch
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.UUID

class PlantRepository(private val dao: PlantDao) {

    val allBatches: Flow<List<PlantBatch>> = dao.getAllBatches()
    val allMeasurements: Flow<List<GrowthMeasurement>> = dao.getAllMeasurements()
    val allCareTasks: Flow<List<CareTask>> = dao.getAllCareTasks()
    val allHealthAlerts: Flow<List<HealthAlert>> = dao.getAllHealthAlerts()
    val userProfile: Flow<UserProfile?> = dao.getUserProfileFlow()

    fun getBatchById(id: String): Flow<PlantBatch?> = dao.getBatchFlowById(id)
    fun getMeasurementsForBatch(batchId: String): Flow<List<GrowthMeasurement>> =
        dao.getMeasurementsForBatch(batchId)
    fun getCareTasksForBatch(batchId: String): Flow<List<CareTask>> =
        dao.getCareTasksForBatch(batchId)
    fun getHealthAlertsForBatch(batchId: String): Flow<List<HealthAlert>> =
        dao.getHealthAlertsForBatch(batchId)

    suspend fun insertBatch(batch: PlantBatch) {
        dao.insertBatch(batch)
    }

    suspend fun updateBatch(batch: PlantBatch) {
        dao.updateBatch(batch)
    }

    suspend fun deleteBatch(batchId: String) {
        dao.deleteBatchById(batchId)
        dao.deleteMeasurementsByBatchId(batchId)
    }

    suspend fun insertMeasurement(measurement: GrowthMeasurement, batch: PlantBatch) {
        dao.insertMeasurement(measurement)

        // Check for automatic health alert triggers:
        // 1. Survival rate drop check
        if (batch.initialSeedlings > 0) {
            val survivalRate = (measurement.survivingCount.toFloat() / batch.initialSeedlings.toFloat()) * 100f
            if (survivalRate < 80f) {
                dao.insertHealthAlert(
                    HealthAlert(
                        id = UUID.randomUUID().toString(),
                        batchId = batch.id,
                        title = "Survival Rate Rendah (${String.format(Locale.US, "%.1f", survivalRate)}%)",
                        severity = "DANGER",
                        symptom = "Tingkat kelangsungan hidup bibit turun di bawah ambang batas aman 80%.",
                        recommendation = "Periksa drainase media tanam, intensitas sinar matahari, dan indikasi penyakit rebah semai (damping-off).",
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
        }

        // 2. Health status trigger
        if (measurement.healthStatus == "Perlu Penanganan") {
            dao.insertHealthAlert(
                HealthAlert(
                    id = UUID.randomUUID().toString(),
                    batchId = batch.id,
                    title = "Bibit Memerlukan Penanganan Cepat",
                    severity = "WARNING",
                    symptom = "Observasi: ${measurement.leafCondition}, Batang: ${measurement.stemCondition}",
                    recommendation = "Karantina tray semai dari tray lainnya dan berikan perlakuan sesuai gejala.",
                    timestamp = System.currentTimeMillis()
                )
            )
        } else if (measurement.healthStatus == "Siap Pindah Tanam") {
            dao.updateBatch(batch.copy(status = "Siap Pindah Tanam"))
        }
    }

    suspend fun deleteMeasurement(measurement: GrowthMeasurement) {
        dao.deleteMeasurement(measurement)
    }

    suspend fun insertCareTask(task: CareTask) {
        dao.insertCareTask(task)
    }

    suspend fun markTaskCompleted(taskId: String, isCompleted: Boolean) {
        dao.markCareTaskCompleted(
            id = taskId,
            completed = isCompleted,
            completedDate = if (isCompleted) System.currentTimeMillis() else null
        )
    }

    suspend fun deleteCareTask(task: CareTask) {
        dao.deleteCareTask(task)
    }

    suspend fun insertHealthAlert(alert: HealthAlert) {
        dao.insertHealthAlert(alert)
    }

    suspend fun resolveAlert(alertId: String, isResolved: Boolean) {
        dao.resolveAlert(alertId, isResolved)
    }

    suspend fun deleteAlert(alertId: String) {
        dao.deleteAlert(alertId)
    }

    suspend fun updateUserProfile(profile: UserProfile) {
        dao.updateUserProfile(profile)
    }

    suspend fun resetToInitialData() {
        AppDatabase.populateInitialData(dao)
    }

    // Export generator to formatted CSV text
    suspend fun exportBatchDataToCsv(batch: PlantBatch): String {
        val measurements = dao.getMeasurementsListForBatch(batch.id)
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale("id", "ID")).apply {
            timeZone = TimeZone.getTimeZone("Asia/Makassar")
        }

        val sb = StringBuilder()
        sb.append("LAPORAN MONITORING BIBIT TANAMAN - TANAM BOSQUU\n")
        sb.append("Tagline: Pantau Pertumbuhan Rawat Kehidupan\n")
        sb.append("Waktu Ekspor: ${sdf.format(Date())} WITA\n\n")

        sb.append("INFORMASI BATCH\n")
        sb.append("Kode Batch,Nama Tanaman,Nama Ilmiah,Kategori,Varietas,Benih Disemai,Bibit Awal,Tanggal Semai,Status,PIC,Lokasi\n")
        sb.append("\"${batch.batchCode}\",\"${batch.plantName}\",\"${batch.scientificName}\",\"${batch.category}\",\"${batch.variety}\",${batch.seedsPlanted},${batch.initialSeedlings},\"${sdf.format(Date(batch.plantingDate))}\",\"${batch.status}\",\"${batch.picName}\",\"${batch.nurseryLocation}\"\n\n")

        sb.append("RIWAYAT PENGUKURAN PERTUMBUHAN\n")
        sb.append("Tanggal,Umur (hari),Tinggi (cm),Daun,Diameter (mm),Bibit Hidup,Kecambah,Kondisi Daun,Kondisi Batang,Kelembapan,Status Kesehatan,Catatan\n")

        var prevHeight = 0f
        for (m in measurements) {
            val ageDays = ((m.measurementDate - batch.plantingDate) / 86400000L).coerceAtLeast(0)
            val diffHeight = if (prevHeight > 0f) m.heightCm - prevHeight else 0f
            prevHeight = m.heightCm
            sb.append("\"${sdf.format(Date(m.measurementDate))}\",")
            sb.append("$ageDays,")
            sb.append("${String.format(Locale.US, "%.1f", m.heightCm)},")
            sb.append("${m.leafCount},")
            sb.append("${String.format(Locale.US, "%.1f", m.stemDiameterMm)},")
            sb.append("${m.survivingCount},")
            sb.append("${m.germinatedCount},")
            sb.append("\"${m.leafCondition}\",")
            sb.append("\"${m.stemCondition}\",")
            sb.append("\"${m.mediumMoisture}\",")
            sb.append("\"${m.healthStatus}\",")
            sb.append("\"${m.observations.replace("\"", "\"\"")}\"\n")
        }

        return sb.toString()
    }
}

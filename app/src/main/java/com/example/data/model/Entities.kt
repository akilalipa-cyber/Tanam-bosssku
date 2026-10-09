package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "plant_batches")
data class PlantBatch(
    @PrimaryKey val id: String,
    val batchCode: String,
    val plantName: String,
    val scientificName: String = "",
    val category: String, // Sayuran, Buah-buahan, Tanaman perkebunan, Tanaman hias, Tanaman kehutanan, Tanaman obat, Lainnya
    val variety: String = "",
    val seedsPlanted: Int,
    val initialSeedlings: Int,
    val plantingDate: Long, // timestamp millis
    val seedSource: String = "",
    val nurseryLocation: String = "",
    val growingMedium: String = "",
    val trayId: String = "",
    val picName: String = "",
    val expectedGerminationDays: Int = 5,
    val expectedTransplantDate: Long = 0L,
    val status: String = "Aktif", // Aktif, Siap Pindah Tanam, Selesai Dipindah, Gagal
    val notes: String = "",
    val photoUri: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Entity(tableName = "growth_measurements")
data class GrowthMeasurement(
    @PrimaryKey val id: String,
    val batchId: String,
    val measurementDate: Long,
    val heightCm: Float,
    val leafCount: Int,
    val stemDiameterMm: Float = 0f,
    val survivingCount: Int,
    val germinatedCount: Int,
    val leafCondition: String = "Hijau Segar", // Hijau Segar, Hijau Muda, Kekuningan, Bercak Cokelat, Layu
    val stemCondition: String = "Kokoh", // Kokoh, Normal, Kurus/Etiolasi, Lemah
    val rootCondition: String = "Putih Sehat", // Putih Sehat, Cokelat, Terbatas, Padat
    val mediumMoisture: String = "Optimal (60-70%)", // Optimal, Kering, Terlalu Basah
    val healthStatus: String = "Sehat", // Sehat, Perlu Dipantau, Perlu Penanganan, Siap Pindah Tanam
    val observations: String = "",
    val photoUri: String? = null
)

@Entity(tableName = "care_tasks")
data class CareTask(
    @PrimaryKey val id: String,
    val batchId: String,
    val taskType: String, // Penyiraman, Pemupukan, Inspeksi Hama, Penyiangan, Pindah Tanam, Pruning
    val scheduledDate: Long,
    val completedDate: Long? = null,
    val isCompleted: Boolean = false,
    val dosageOrVolume: String = "", // e.g. "0.5 L air", "2 ml/L POC"
    val method: String = "", // Sprayer kabut, Kocor akar, Rendam bawah, Semprot daun
    val fertilizerOrItem: String = "", // e.g. "Air Baku pH 6.5", "NPK 16-16-16", "AB Mix"
    val picName: String = "",
    val notes: String = ""
)

@Entity(tableName = "health_alerts")
data class HealthAlert(
    @PrimaryKey val id: String,
    val batchId: String,
    val title: String,
    val severity: String, // INFO, WARNING, DANGER
    val symptom: String,
    val recommendation: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isResolved: Boolean = false
)

@Entity(tableName = "user_profiles")
data class UserProfile(
    @PrimaryKey val id: String,
    val fullName: String,
    val email: String,
    val role: String = "Administrator", // Administrator, Manajer Kebun, Petugas Lapangan, Viewer
    val workspaceName: String = "Kebun Percobaan Nursery Utama",
    val isLoggedIn: Boolean = true,
    val lastSyncTimestamp: Long = System.currentTimeMillis()
)

package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.dao.PlantDao
import com.example.data.model.CareTask
import com.example.data.model.GrowthMeasurement
import com.example.data.model.HealthAlert
import com.example.data.model.PlantBatch
import com.example.data.model.UserProfile
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.UUID

@Database(
    entities = [
        PlantBatch::class,
        GrowthMeasurement::class,
        CareTask::class,
        HealthAlert::class,
        UserProfile::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plantDao(): PlantDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tanam_bosquu_database"
                )
                    .addCallback(DatabaseCallback())
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                INSTANCE?.let { database ->
                    CoroutineScope(Dispatchers.IO).launch {
                        populateInitialData(database.plantDao())
                    }
                }
            }
        }

        suspend fun populateInitialData(dao: PlantDao) {
            val now = System.currentTimeMillis()
            val dayMillis = 86400000L

            // Default User Profile
            val profile = UserProfile(
                id = "usr_001",
                fullName = "Akil Alipa",
                email = "AKIL.alipa@gmail.com",
                role = "Administrator",
                workspaceName = "Kebun Persemaian Agrotek Bosquu (Makassar)",
                isLoggedIn = true,
                lastSyncTimestamp = now
            )
            dao.insertUserProfile(profile)

            // Batch 1: Cabai Rawit Merah 'Ori 212'
            val b1Id = "batch_001"
            val b1PlantDate = now - (18 * dayMillis)
            val b1 = PlantBatch(
                id = b1Id,
                batchCode = "TB-CBI-001",
                plantName = "Cabai Rawit Merah",
                scientificName = "Capsicum frutescens",
                category = "Sayuran",
                variety = "Ori 212",
                seedsPlanted = 100,
                initialSeedlings = 96,
                plantingDate = b1PlantDate,
                seedSource = "Benih Inti Agro Nusantara",
                nurseryLocation = "Greenhouse Blok A1",
                growingMedium = "Cocopeat + Sekam Bakar (1:1)",
                trayId = "Tray Semai 104 Lubang #01",
                picName = "Akil Alipa",
                expectedGerminationDays = 6,
                expectedTransplantDate = b1PlantDate + (25 * dayMillis),
                status = "Aktif",
                notes = "Perlakuan perendaman air hangat kuku 50°C selama 3 jam sebelum semai.",
                photoUri = null,
                createdAt = b1PlantDate
            )
            dao.insertBatch(b1)

            // Measurements for Batch 1 (Growth Curve over 18 days)
            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b1Id,
                    measurementDate = b1PlantDate + (6 * dayMillis),
                    heightCm = 1.8f,
                    leafCount = 2,
                    stemDiameterMm = 0.8f,
                    survivingCount = 94,
                    germinatedCount = 94,
                    leafCondition = "Hijau Muda",
                    stemCondition = "Normal",
                    rootCondition = "Putih Sehat",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Kotiledon (daun lembaga) terbuka sempurna, warna seragam."
                )
            )
            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b1Id,
                    measurementDate = b1PlantDate + (12 * dayMillis),
                    heightCm = 4.5f,
                    leafCount = 4,
                    stemDiameterMm = 1.4f,
                    survivingCount = 94,
                    germinatedCount = 96,
                    leafCondition = "Hijau Segar",
                    stemCondition = "Kokoh",
                    rootCondition = "Putih Sehat",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Muncul sepasang daun sejati pertama. Batang mulai menguat."
                )
            )
            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b1Id,
                    measurementDate = b1PlantDate + (17 * dayMillis),
                    heightCm = 7.6f,
                    leafCount = 6,
                    stemDiameterMm = 2.1f,
                    survivingCount = 93,
                    germinatedCount = 96,
                    leafCondition = "Hijau Segar",
                    stemCondition = "Kokoh",
                    rootCondition = "Padat & Sehat",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Tinggi rata-rata mencapai 7.6 cm dengan 6 helai daun. Siap hardening off."
                )
            )

            // Batch 2: Tomat Servo F1
            val b2Id = "batch_002"
            val b2PlantDate = now - (23 * dayMillis)
            val b2 = PlantBatch(
                id = b2Id,
                batchCode = "TB-TMT-002",
                plantName = "Tomat Hibrida Servo",
                scientificName = "Solanum lycopersicum",
                category = "Sayuran",
                variety = "Servo F1",
                seedsPlanted = 120,
                initialSeedlings = 118,
                plantingDate = b2PlantDate,
                seedSource = "Cap Panah Merah",
                nurseryLocation = "Greenhouse Blok A2",
                growingMedium = "Rockwool Cubes + Kompos Halus",
                trayId = "Tray Semai 128 Lubang #03",
                picName = "Budi Hartono",
                expectedGerminationDays = 4,
                expectedTransplantDate = b2PlantDate + (24 * dayMillis),
                status = "Siap Pindah Tanam",
                notes = "Pertumbuhan sangat serempak, sistem perakaran sangat lebat.",
                photoUri = null,
                createdAt = b2PlantDate
            )
            dao.insertBatch(b2)

            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b2Id,
                    measurementDate = b2PlantDate + (5 * dayMillis),
                    heightCm = 2.4f,
                    leafCount = 2,
                    stemDiameterMm = 1.1f,
                    survivingCount = 117,
                    germinatedCount = 118,
                    leafCondition = "Hijau Muda",
                    stemCondition = "Kokoh",
                    rootCondition = "Putih Sehat",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Kecambah vigor kuat, daun kotiledon hijau cerah."
                )
            )
            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b2Id,
                    measurementDate = b2PlantDate + (14 * dayMillis),
                    heightCm = 8.2f,
                    leafCount = 5,
                    stemDiameterMm = 2.6f,
                    survivingCount = 116,
                    germinatedCount = 118,
                    leafCondition = "Hijau Segar",
                    stemCondition = "Kokoh",
                    rootCondition = "Putih Sehat",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Batang menebal, bau khas daun tomat mulai terasa."
                )
            )
            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b2Id,
                    measurementDate = b2PlantDate + (22 * dayMillis),
                    heightCm = 14.5f,
                    leafCount = 8,
                    stemDiameterMm = 3.8f,
                    survivingCount = 116,
                    germinatedCount = 118,
                    leafCondition = "Hijau Segar",
                    stemCondition = "Kokoh",
                    rootCondition = "Padat & Berbulu Halus",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Siap Pindah Tanam",
                    observations = "Bibit siap pindah ke lahan utama atau polybag 35cm."
                )
            )

            // Batch 3: Selada Keriting 'Grand Rapids'
            val b3Id = "batch_003"
            val b3PlantDate = now - (9 * dayMillis)
            val b3 = PlantBatch(
                id = b3Id,
                batchCode = "TB-SLD-003",
                plantName = "Selada Keriting Hijau",
                scientificName = "Lactuca sativa",
                category = "Sayuran",
                variety = "Grand Rapids",
                seedsPlanted = 200,
                initialSeedlings = 190,
                plantingDate = b3PlantDate,
                seedSource = "Known-You Seed",
                nurseryLocation = "Meja Semai Hidroponik Rak 2",
                growingMedium = "Rockwool Sponge 2.5x2.5cm",
                trayId = "Flat Tray Hidroponik #05",
                picName = "Dewi Lestari",
                expectedGerminationDays = 3,
                expectedTransplantDate = b3PlantDate + (14 * dayMillis),
                status = "Aktif",
                notes = "Penyinaran lampu LED grow light 12 jam/hari untuk mencegah kutilang.",
                photoUri = null,
                createdAt = b3PlantDate
            )
            dao.insertBatch(b3)

            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b3Id,
                    measurementDate = b3PlantDate + (4 * dayMillis),
                    heightCm = 1.2f,
                    leafCount = 2,
                    stemDiameterMm = 0.5f,
                    survivingCount = 188,
                    germinatedCount = 190,
                    leafCondition = "Hijau Muda",
                    stemCondition = "Normal",
                    rootCondition = "Putih Sehat",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Sprout merata dalam 48 jam."
                )
            )
            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b3Id,
                    measurementDate = b3PlantDate + (8 * dayMillis),
                    heightCm = 3.6f,
                    leafCount = 3,
                    stemDiameterMm = 0.9f,
                    survivingCount = 185,
                    germinatedCount = 190,
                    leafCondition = "Hijau Segar",
                    stemCondition = "Kokoh",
                    rootCondition = "Menembus Rockwool",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Akar menembus bagian bawah rockwool, siap masuk netpot."
                )
            )

            // Batch 4: Kopi Robusta Super (Tanaman Perkebunan with Warning)
            val b4Id = "batch_004"
            val b4PlantDate = now - (35 * dayMillis)
            val b4 = PlantBatch(
                id = b4Id,
                batchCode = "TB-KPI-004",
                plantName = "Kopi Robusta Lampung",
                scientificName = "Coffea canephora",
                category = "Tanaman perkebunan",
                variety = "Klon BP 42",
                seedsPlanted = 80,
                initialSeedlings = 72,
                plantingDate = b4PlantDate,
                seedSource = "Puslitkoka Jember",
                nurseryLocation = "Bedengan Naungan Shading Net 60%",
                growingMedium = "Tanah Humus + Pasir + Pupuk Kandang (2:1:1)",
                trayId = "Polybag Semai 10x15cm #08",
                picName = "Akil Alipa",
                expectedGerminationDays = 20,
                expectedTransplantDate = b4PlantDate + (90 * dayMillis),
                status = "Aktif",
                notes = "Fase serdadu (soldier stage). Ada beberapa daun bercak kuning.",
                photoUri = null,
                createdAt = b4PlantDate
            )
            dao.insertBatch(b4)

            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b4Id,
                    measurementDate = b4PlantDate + (22 * dayMillis),
                    heightCm = 4.0f,
                    leafCount = 2,
                    stemDiameterMm = 1.8f,
                    survivingCount = 68,
                    germinatedCount = 70,
                    leafCondition = "Hijau Muda",
                    stemCondition = "Kokoh",
                    rootCondition = "Akar Tunggang Tumbuh",
                    mediumMoisture = "Optimal (60-70%)",
                    healthStatus = "Sehat",
                    observations = "Masa kepiting/serdadu mulai membuka keping biji."
                )
            )
            dao.insertMeasurement(
                GrowthMeasurement(
                    id = UUID.randomUUID().toString(),
                    batchId = b4Id,
                    measurementDate = b4PlantDate + (34 * dayMillis),
                    heightCm = 6.2f,
                    leafCount = 2,
                    stemDiameterMm = 2.2f,
                    survivingCount = 64,
                    germinatedCount = 70,
                    leafCondition = "Kekuningan",
                    stemCondition = "Normal",
                    rootCondition = "Terbatas",
                    mediumMoisture = "Terlalu Basah",
                    healthStatus = "Perlu Dipantau",
                    observations = "Beberapa bibit menunjukkan klorosis daun karena media terlalu lembap."
                )
            )

            // Care Tasks
            dao.insertCareTask(
                CareTask(
                    id = UUID.randomUUID().toString(),
                    batchId = b1Id,
                    taskType = "Penyiraman",
                    scheduledDate = now, // Today
                    completedDate = null,
                    isCompleted = false,
                    dosageOrVolume = "1.5 Liter air",
                    method = "Sprayer kabut halus",
                    fertilizerOrItem = "Air Baku pH 6.2",
                    picName = "Akil Alipa",
                    notes = "Siram perlahan agar bibit muda tidak rebah terkena tetesan air."
                )
            )
            dao.insertCareTask(
                CareTask(
                    id = UUID.randomUUID().toString(),
                    batchId = b1Id,
                    taskType = "Pemupukan",
                    scheduledDate = now, // Today
                    completedDate = null,
                    isCompleted = false,
                    dosageOrVolume = "2 ml / Liter",
                    method = "Semprot daun (foliar spray)",
                    fertilizerOrItem = "Pupuk Organik Cair (POC) Azolla",
                    picName = "Akil Alipa",
                    notes = "Aplikasi pagi hari sebelum jam 09.00 WITA saat stomata terbuka."
                )
            )
            dao.insertCareTask(
                CareTask(
                    id = UUID.randomUUID().toString(),
                    batchId = b2Id,
                    taskType = "Penyiraman",
                    scheduledDate = now + (1 * dayMillis),
                    completedDate = null,
                    isCompleted = false,
                    dosageOrVolume = "2.0 Liter air",
                    method = "Gembor siram halus",
                    fertilizerOrItem = "Air bersih",
                    picName = "Budi Hartono",
                    notes = "Siram pada media rockwool dan area bawah daun."
                )
            )
            dao.insertCareTask(
                CareTask(
                    id = UUID.randomUUID().toString(),
                    batchId = b2Id,
                    taskType = "Pindah Tanam",
                    scheduledDate = now + (2 * dayMillis),
                    completedDate = null,
                    isCompleted = false,
                    dosageOrVolume = "116 bibit",
                    method = "Pindah ke Polybag 35cm",
                    fertilizerOrItem = "Media Tanam Campuran Kompos",
                    picName = "Budi Hartono",
                    notes = "Bibit telah berumur 25 hari dan memiliki 8 daun sejati."
                )
            )
            dao.insertCareTask(
                CareTask(
                    id = UUID.randomUUID().toString(),
                    batchId = b4Id,
                    taskType = "Inspeksi Hama",
                    scheduledDate = now - (1 * dayMillis), // Overdue!
                    completedDate = null,
                    isCompleted = false,
                    dosageOrVolume = "Pemeriksaan visual",
                    method = "Inspeksi mikroskop saku & daun bawah",
                    fertilizerOrItem = "Cek kutu putih & damping-off",
                    picName = "Akil Alipa",
                    notes = "Jadwal terlambat: Cek segera gejala menguning pada daun kopi."
                )
            )

            // Health Alerts
            dao.insertHealthAlert(
                HealthAlert(
                    id = UUID.randomUUID().toString(),
                    batchId = b4Id,
                    title = "Gejala Klorosis & Media Terlalu Basah",
                    severity = "WARNING",
                    symptom = "Daun menguning pada bibit Kopi Robusta (TB-KPI-004), media lembap berlebih.",
                    recommendation = "Kurangi frekuensi penyiraman, perbaiki aerasi dan drainase bedengan semai.",
                    timestamp = now - (12 * 3600000L),
                    isResolved = false
                )
            )
            dao.insertHealthAlert(
                HealthAlert(
                    id = UUID.randomUUID().toString(),
                    batchId = b2Id,
                    title = "Bibit Tomat Siap Pindah Tanam",
                    severity = "INFO",
                    symptom = "Tinggi bibit >14 cm, perakaran lebat menembus media, umur semai mencukupi.",
                    recommendation = "Lakukan hardening off (aklimatisasi sinar matahari penuh) 2 hari sebelum transplantasi.",
                    timestamp = now - (6 * 3600000L),
                    isResolved = false
                )
            )
        }
    }
}

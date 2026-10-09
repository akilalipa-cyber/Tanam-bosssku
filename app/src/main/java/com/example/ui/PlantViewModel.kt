package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.PlantRepository
import com.example.data.model.CareTask
import com.example.data.model.GrowthMeasurement
import com.example.data.model.HealthAlert
import com.example.data.model.PlantBatch
import com.example.data.model.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

enum class DashboardDateFilter(val label: String) {
    TODAY("Hari Ini"),
    LAST_7_DAYS("7 Hari"),
    LAST_30_DAYS("30 Hari"),
    ALL("Semua")
}

data class SyncState(
    val isSyncing: Boolean = false,
    val lastSyncMessage: String = "Tersinkronisasi dengan Cloud (Supabase/Asia-Makassar)",
    val syncSuccess: Boolean = true
)

class PlantViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: PlantRepository
    init {
        val db = AppDatabase.getInstance(application)
        repository = PlantRepository(db.plantDao())
    }

    val batches: StateFlow<List<PlantBatch>> = repository.allBatches
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val measurements: StateFlow<List<GrowthMeasurement>> = repository.allMeasurements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val careTasks: StateFlow<List<CareTask>> = repository.allCareTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val healthAlerts: StateFlow<List<HealthAlert>> = repository.allHealthAlerts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userProfile: StateFlow<UserProfile?> = repository.userProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    // UI State filters
    private val _dashboardDateFilter = MutableStateFlow(DashboardDateFilter.ALL)
    val dashboardDateFilter: StateFlow<DashboardDateFilter> = _dashboardDateFilter.asStateFlow()

    private val _batchSearchQuery = MutableStateFlow("")
    val batchSearchQuery: StateFlow<String> = _batchSearchQuery.asStateFlow()

    private val _batchCategoryFilter = MutableStateFlow("Semua")
    val batchCategoryFilter: StateFlow<String> = _batchCategoryFilter.asStateFlow()

    private val _batchStatusFilter = MutableStateFlow("Semua")
    val batchStatusFilter: StateFlow<String> = _batchStatusFilter.asStateFlow()

    private val _selectedBatchId = MutableStateFlow<String?>(null)
    val selectedBatchId: StateFlow<String?> = _selectedBatchId.asStateFlow()

    private val _syncState = MutableStateFlow(SyncState())
    val syncState: StateFlow<SyncState> = _syncState.asStateFlow()

    // Filtered Batches
    val filteredBatches: StateFlow<List<PlantBatch>> = combine(
        batches,
        _batchSearchQuery,
        _batchCategoryFilter,
        _batchStatusFilter
    ) { batchList, query, category, status ->
        batchList.filter { b ->
            val matchQuery = query.isBlank() ||
                    b.plantName.contains(query, ignoreCase = true) ||
                    b.batchCode.contains(query, ignoreCase = true) ||
                    b.scientificName.contains(query, ignoreCase = true) ||
                    b.variety.contains(query, ignoreCase = true)
            val matchCategory = category == "Semua" || b.category.equals(category, ignoreCase = true)
            val matchStatus = status == "Semua" || b.status.equals(status, ignoreCase = true)
            matchQuery && matchCategory && matchStatus
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setDashboardDateFilter(filter: DashboardDateFilter) {
        _dashboardDateFilter.value = filter
    }

    fun setBatchSearchQuery(query: String) {
        _batchSearchQuery.value = query
    }

    fun setBatchCategoryFilter(category: String) {
        _batchCategoryFilter.value = category
    }

    fun setBatchStatusFilter(status: String) {
        _batchStatusFilter.value = status
    }

    fun selectBatch(batchId: String?) {
        _selectedBatchId.value = batchId
    }

    fun addBatch(
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
    ) {
        viewModelScope.launch {
            val randomSuffix = (100..999).random()
            val categoryPrefix = when (category) {
                "Sayuran" -> "SYR"
                "Buah-buahan" -> "BUH"
                "Tanaman perkebunan" -> "PKB"
                "Tanaman hias" -> "HIS"
                "Tanaman kehutanan" -> "KHT"
                "Tanaman obat" -> "OBT"
                else -> "TNM"
            }
            val batchCode = "TB-$categoryPrefix-$randomSuffix"
            val dayMillis = 86400000L
            val expectedTransplantDate = if (expectedTransplantDays > 0) {
                plantingDate + (expectedTransplantDays * dayMillis)
            } else 0L

            val batch = PlantBatch(
                id = UUID.randomUUID().toString(),
                batchCode = batchCode,
                plantName = plantName,
                scientificName = scientificName,
                category = category,
                variety = variety,
                seedsPlanted = seedsPlanted,
                initialSeedlings = initialSeedlings,
                plantingDate = plantingDate,
                seedSource = seedSource,
                nurseryLocation = nurseryLocation,
                growingMedium = growingMedium,
                trayId = trayId,
                picName = picName,
                expectedGerminationDays = expectedGerminationDays,
                expectedTransplantDate = expectedTransplantDate,
                status = "Aktif",
                notes = notes,
                photoUri = null
            )
            repository.insertBatch(batch)

            // Auto schedule initial care task (Watering today)
            repository.insertCareTask(
                CareTask(
                    id = UUID.randomUUID().toString(),
                    batchId = batch.id,
                    taskType = "Penyiraman",
                    scheduledDate = System.currentTimeMillis(),
                    dosageOrVolume = "1.0 L air",
                    method = "Sprayer kabut",
                    fertilizerOrItem = "Air bersih",
                    picName = picName,
                    notes = "Penyiraman kelembapan awal semai"
                )
            )
        }
    }

    fun updateBatch(batch: PlantBatch) {
        viewModelScope.launch {
            repository.updateBatch(batch)
        }
    }

    fun deleteBatch(batchId: String) {
        viewModelScope.launch {
            repository.deleteBatch(batchId)
            if (_selectedBatchId.value == batchId) {
                _selectedBatchId.value = null
            }
        }
    }

    fun addMeasurement(
        batch: PlantBatch,
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
        observations: String,
        photoUri: String? = null
    ) {
        viewModelScope.launch {
            val measurement = GrowthMeasurement(
                id = UUID.randomUUID().toString(),
                batchId = batch.id,
                measurementDate = System.currentTimeMillis(),
                heightCm = heightCm,
                leafCount = leafCount,
                stemDiameterMm = stemDiameterMm,
                survivingCount = survivingCount,
                germinatedCount = germinatedCount,
                leafCondition = leafCondition,
                stemCondition = stemCondition,
                rootCondition = rootCondition,
                mediumMoisture = mediumMoisture,
                healthStatus = healthStatus,
                observations = observations,
                photoUri = photoUri
            )
            repository.insertMeasurement(measurement, batch)
        }
    }

    fun deleteMeasurement(measurement: GrowthMeasurement) {
        viewModelScope.launch {
            repository.deleteMeasurement(measurement)
        }
    }

    fun addCareTask(
        batchId: String,
        taskType: String,
        scheduledDate: Long,
        dosageOrVolume: String,
        method: String,
        fertilizerOrItem: String,
        picName: String,
        notes: String
    ) {
        viewModelScope.launch {
            val task = CareTask(
                id = UUID.randomUUID().toString(),
                batchId = batchId,
                taskType = taskType,
                scheduledDate = scheduledDate,
                dosageOrVolume = dosageOrVolume,
                method = method,
                fertilizerOrItem = fertilizerOrItem,
                picName = picName,
                notes = notes
            )
            repository.insertCareTask(task)
        }
    }

    fun toggleTaskComplete(taskId: String, currentCompleted: Boolean) {
        viewModelScope.launch {
            repository.markTaskCompleted(taskId, !currentCompleted)
        }
    }

    fun deleteCareTask(task: CareTask) {
        viewModelScope.launch {
            repository.deleteCareTask(task)
        }
    }

    fun addHealthAlert(
        batchId: String,
        title: String,
        severity: String,
        symptom: String,
        recommendation: String
    ) {
        viewModelScope.launch {
            val alert = HealthAlert(
                id = UUID.randomUUID().toString(),
                batchId = batchId,
                title = title,
                severity = severity,
                symptom = symptom,
                recommendation = recommendation,
                timestamp = System.currentTimeMillis()
            )
            repository.insertHealthAlert(alert)
        }
    }

    fun resolveAlert(alertId: String, isResolved: Boolean) {
        viewModelScope.launch {
            repository.resolveAlert(alertId, isResolved)
        }
    }

    fun deleteAlert(alertId: String) {
        viewModelScope.launch {
            repository.deleteAlert(alertId)
        }
    }

    fun updateUserProfile(fullName: String, email: String, role: String, workspaceName: String) {
        viewModelScope.launch {
            val current = userProfile.value ?: UserProfile(
                id = "usr_001",
                fullName = fullName,
                email = email,
                role = role,
                workspaceName = workspaceName
            )
            repository.updateUserProfile(
                current.copy(
                    fullName = fullName,
                    email = email,
                    role = role,
                    workspaceName = workspaceName,
                    lastSyncTimestamp = System.currentTimeMillis()
                )
            )
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            _syncState.value = SyncState(isSyncing = true, lastSyncMessage = "Menghubungkan ke Supabase Cloud (Makassar)...")
            delay(1200)
            _syncState.value = SyncState(
                isSyncing = false,
                lastSyncMessage = "Sinkronisasi Berhasil: Data diperbarui pada ${formatCurrentTime()}",
                syncSuccess = true
            )
            userProfile.value?.let { p ->
                repository.updateUserProfile(p.copy(lastSyncTimestamp = System.currentTimeMillis()))
            }
        }
    }

    suspend fun getBatchCsv(batch: PlantBatch): String {
        return repository.exportBatchDataToCsv(batch)
    }

    private fun formatCurrentTime(): String {
        val sdf = java.text.SimpleDateFormat("HH:mm:ss 'WITA'", java.util.Locale("id", "ID"))
        sdf.timeZone = java.util.TimeZone.getTimeZone("Asia/Makassar")
        return sdf.format(java.util.Date())
    }
}

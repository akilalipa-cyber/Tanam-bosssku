package com.example.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CareTask
import com.example.data.model.GrowthMeasurement
import com.example.data.model.HealthAlert
import com.example.data.model.PlantBatch
import com.example.data.model.UserProfile
import kotlinx.coroutines.flow.Flow

@Dao
interface PlantDao {

    // --- Plant Batches ---
    @Query("SELECT * FROM plant_batches ORDER BY createdAt DESC")
    fun getAllBatches(): Flow<List<PlantBatch>>

    @Query("SELECT * FROM plant_batches WHERE id = :id LIMIT 1")
    suspend fun getBatchById(id: String): PlantBatch?

    @Query("SELECT * FROM plant_batches WHERE id = :id LIMIT 1")
    fun getBatchFlowById(id: String): Flow<PlantBatch?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBatch(batch: PlantBatch)

    @Update
    suspend fun updateBatch(batch: PlantBatch)

    @Delete
    suspend fun deleteBatch(batch: PlantBatch)

    @Query("DELETE FROM plant_batches WHERE id = :id")
    suspend fun deleteBatchById(id: String)

    // --- Growth Measurements ---
    @Query("SELECT * FROM growth_measurements ORDER BY measurementDate DESC")
    fun getAllMeasurements(): Flow<List<GrowthMeasurement>>

    @Query("SELECT * FROM growth_measurements WHERE batchId = :batchId ORDER BY measurementDate ASC")
    fun getMeasurementsForBatch(batchId: String): Flow<List<GrowthMeasurement>>

    @Query("SELECT * FROM growth_measurements WHERE batchId = :batchId ORDER BY measurementDate DESC")
    suspend fun getMeasurementsListForBatch(batchId: String): List<GrowthMeasurement>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeasurement(measurement: GrowthMeasurement)

    @Delete
    suspend fun deleteMeasurement(measurement: GrowthMeasurement)

    @Query("DELETE FROM growth_measurements WHERE batchId = :batchId")
    suspend fun deleteMeasurementsByBatchId(batchId: String)

    // --- Care Tasks ---
    @Query("SELECT * FROM care_tasks ORDER BY scheduledDate ASC")
    fun getAllCareTasks(): Flow<List<CareTask>>

    @Query("SELECT * FROM care_tasks WHERE batchId = :batchId ORDER BY scheduledDate DESC")
    fun getCareTasksForBatch(batchId: String): Flow<List<CareTask>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCareTask(task: CareTask)

    @Update
    suspend fun updateCareTask(task: CareTask)

    @Delete
    suspend fun deleteCareTask(task: CareTask)

    @Query("UPDATE care_tasks SET isCompleted = :completed, completedDate = :completedDate WHERE id = :id")
    suspend fun markCareTaskCompleted(id: String, completed: Boolean, completedDate: Long?)

    // --- Health Alerts ---
    @Query("SELECT * FROM health_alerts ORDER BY timestamp DESC")
    fun getAllHealthAlerts(): Flow<List<HealthAlert>>

    @Query("SELECT * FROM health_alerts WHERE batchId = :batchId ORDER BY timestamp DESC")
    fun getHealthAlertsForBatch(batchId: String): Flow<List<HealthAlert>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHealthAlert(alert: HealthAlert)

    @Update
    suspend fun updateHealthAlert(alert: HealthAlert)

    @Query("UPDATE health_alerts SET isResolved = :isResolved WHERE id = :id")
    suspend fun resolveAlert(id: String, isResolved: Boolean)

    @Query("DELETE FROM health_alerts WHERE id = :id")
    suspend fun deleteAlert(id: String)

    // --- User Profile ---
    @Query("SELECT * FROM user_profiles LIMIT 1")
    fun getUserProfileFlow(): Flow<UserProfile?>

    @Query("SELECT * FROM user_profiles LIMIT 1")
    suspend fun getUserProfile(): UserProfile?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUserProfile(userProfile: UserProfile)

    @Update
    suspend fun updateUserProfile(userProfile: UserProfile)
}

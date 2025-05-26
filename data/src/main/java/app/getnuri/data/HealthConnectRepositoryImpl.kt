package app.getnuri.data

import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.BodyTemperatureRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.RespiratoryRateRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HealthConnectRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context, // Needed for HealthConnectClient
    private val healthDataDao: HealthDataDao
) : HealthConnectRepository {

    private val healthConnectClient by lazy { HealthConnectClient.getOrCreate(context) }

    // Simplified permission check for now. Actual permission request flow will be more complex.
    override suspend fun checkAndRequestPermissions(): Boolean {
        val permissions = setOf(
            HealthPermission.getReadPermission(HeartRateRecord::class),
            HealthPermission.getReadPermission(SleepSessionRecord::class),
            HealthPermission.getReadPermission(BodyTemperatureRecord::class),
            HealthPermission.getReadPermission(RespiratoryRateRecord::class)
        )
        // In a real app, you'd request permissions here if not granted.
        // For this subtask, we assume they are granted if the SDK is available.
        return HealthConnectClient.getSdkStatus(context) == HealthConnectClient.SDK_AVAILABLE
    }

    override suspend fun fetchAndStoreHealthData(startTime: Instant, endTime: Instant): Result<List<HealthData>> = withContext(Dispatchers.IO) {
        if (!checkAndRequestPermissions()) {
            return@withContext Result.failure(Exception("Health Connect permissions not granted or SDK unavailable."))
        }

        val timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
        val healthDataList = mutableListOf<HealthData>()

        try {
            // Heart Rate
            val heartRateRequest = ReadRecordsRequest(HeartRateRecord::class, timeRangeFilter)
            healthConnectClient.readRecords(heartRateRequest).records.forEach { record ->
                record.samples.forEach { sample ->
                    healthDataList.add(HealthData(
                        type = "HeartRate",
                        value = sample.beatsPerMinute.toString(),
                        unit = "bpm",
                        timestamp = sample.time.toEpochMilli(),
                        source = "HealthConnect",
                        userId = 0L // Placeholder userId
                    ))
                }
            }

            // Sleep Session (calculating duration)
            val sleepSessionRequest = ReadRecordsRequest(SleepSessionRecord::class, timeRangeFilter)
            healthConnectClient.readRecords(sleepSessionRequest).records.forEach { record ->
                val durationMillis = record.endTime.toEpochMilli() - record.startTime.toEpochMilli()
                healthDataList.add(HealthData(
                    type = "SleepDuration",
                    value = durationMillis.toString(),
                    unit = "ms",
                    timestamp = record.startTime.toEpochMilli(), // Use session start time
                    source = "HealthConnect",
                    userId = 0L // Placeholder userId
                ))
            }

            // Body Temperature
            val bodyTempRequest = ReadRecordsRequest(BodyTemperatureRecord::class, timeRangeFilter)
            healthConnectClient.readRecords(bodyTempRequest).records.forEach { record ->
                 // BodyTemperatureRecord itself has a single temperature and time
                 healthDataList.add(HealthData(
                    type = "BodyTemperature",
                    value = record.temperature.inCelsius.toString(), // Assuming Celsius
                    unit = "Celsius",
                    timestamp = record.time.toEpochMilli(),
                    source = "HealthConnect",
                    userId = 0L // Placeholder userId
                ))
            }

            // Respiratory Rate
            val respRateRequest = ReadRecordsRequest(RespiratoryRateRecord::class, timeRangeFilter)
            healthConnectClient.readRecords(respRateRequest).records.forEach { record ->
                // RespiratoryRateRecord itself has a single rate and time
                healthDataList.add(HealthData(
                    type = "RespiratoryRate",
                    value = record.rate.toString(),
                    unit = "rpm", // breaths per minute
                    timestamp = record.time.toEpochMilli(),
                    source = "HealthConnect",
                    userId = 0L // Placeholder userId
                ))
            }
            
            if (healthDataList.isNotEmpty()) {
                healthDataDao.insertAll(healthDataList)
            }
            Result.success(healthDataList)
        } catch (e: Exception) {
            // Log error e
            Result.failure(e)
        }
    }
}

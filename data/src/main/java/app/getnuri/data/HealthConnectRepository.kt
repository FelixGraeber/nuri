package app.getnuri.data

import java.time.Instant

interface HealthConnectRepository {
    // Returns true if permissions are granted (simplified for now)
    suspend fun checkAndRequestPermissions(): Boolean 
    // Fetches data from Health Connect and stores it in the local DB.
    // Returns the list of HealthData entities that were fetched and stored.
    suspend fun fetchAndStoreHealthData(startTime: Instant, endTime: Instant): Result<List<HealthData>>
}

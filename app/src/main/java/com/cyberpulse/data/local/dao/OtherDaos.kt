package com.cyberpulse.data.local.dao

import androidx.room.*
import com.cyberpulse.data.local.entity.DataBreachEntity
import com.cyberpulse.data.local.entity.CVEEntity
import com.cyberpulse.data.local.entity.CyberEventEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Data Breaches
 */
@Dao
interface BreachDao {
    
    @Query("SELECT * FROM data_breaches ORDER BY breachDate DESC")
    fun getAllBreaches(): Flow<List<DataBreachEntity>>
    
    @Query("SELECT * FROM data_breaches WHERE breachDate > :since ORDER BY breachDate DESC")
    fun getRecentBreaches(since: Long): Flow<List<DataBreachEntity>>
    
    @Query("SELECT * FROM data_breaches WHERE name LIKE :query OR domain LIKE :query ORDER BY breachDate DESC")
    suspend fun searchBreaches(query: String): List<DataBreachEntity>
    
    @Query("SELECT * FROM data_breaches WHERE name = :name")
    suspend fun getBreachByName(name: String): DataBreachEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreach(breach: DataBreachEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBreaches(breaches: List<DataBreachEntity>)
    
    @Query("DELETE FROM data_breaches")
    suspend fun deleteAll()
}

/**
 * Data Access Object for CVE Entries
 */
@Dao
interface CVEDao {
    
    @Query("SELECT * FROM cve_entries ORDER BY publishedDate DESC LIMIT :limit")
    fun getRecentCVEs(limit: Int): Flow<List<CVEEntity>>
    
    @Query("SELECT * FROM cve_entries WHERE severity = :severity ORDER BY publishedDate DESC LIMIT :limit")
    fun getCVEsBySeverity(severity: String, limit: Int): Flow<List<CVEEntity>>
    
    @Query("SELECT * FROM cve_entries WHERE id = :cveId")
    suspend fun getCVEById(cveId: String): CVEEntity?
    
    @Query("SELECT * FROM cve_entries WHERE id LIKE :query OR description LIKE :query ORDER BY publishedDate DESC")
    suspend fun searchCVEs(query: String): List<CVEEntity>
    
    @Query("SELECT * FROM cve_entries WHERE affectedProducts LIKE :product ORDER BY publishedDate DESC")
    suspend fun getCVEsByProduct(product: String): List<CVEEntity>
    
    @Query("SELECT * FROM cve_entries WHERE severity = 'CRITICAL' AND exploitAvailable = 1 ORDER BY publishedDate DESC")
    fun getCriticalExploitedCVEs(): Flow<List<CVEEntity>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCVE(cve: CVEEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCVEs(cves: List<CVEEntity>)
    
    @Query("DELETE FROM cve_entries WHERE id NOT IN (SELECT id FROM cve_entries ORDER BY publishedDate DESC LIMIT :keepCount)")
    suspend fun deleteOldCVEs(keepCount: Int)
}

/**
 * Data Access Object for Cyber Events
 */
@Dao
interface EventDao {
    
    @Query("SELECT * FROM cyber_events WHERE startDate > :now ORDER BY startDate ASC")
    fun getUpcomingEvents(now: Long): Flow<List<CyberEventEntity>>
    
    @Query("SELECT * FROM cyber_events WHERE type = :type AND startDate > :now ORDER BY startDate ASC")
    fun getEventsByType(type: String, now: Long): Flow<List<CyberEventEntity>>
    
    @Query("SELECT * FROM cyber_events WHERE startDate >= :startOfMonth AND startDate < :endOfMonth ORDER BY startDate ASC")
    suspend fun getEventsForMonth(startOfMonth: Long, endOfMonth: Long): List<CyberEventEntity>
    
    @Query("SELECT * FROM cyber_events WHERE hasReminder = 1 ORDER BY startDate ASC")
    fun getEventsWithReminders(): Flow<List<CyberEventEntity>>
    
    @Query("SELECT * FROM cyber_events WHERE id = :eventId")
    suspend fun getEventById(eventId: String): CyberEventEntity?
    
    @Query("UPDATE cyber_events SET hasReminder = :hasReminder WHERE id = :eventId")
    suspend fun updateEventReminder(eventId: String, hasReminder: Boolean)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: CyberEventEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvents(events: List<CyberEventEntity>)
    
    @Query("DELETE FROM cyber_events WHERE startDate < :now")
    suspend fun deletePastEvents(now: Long)
}

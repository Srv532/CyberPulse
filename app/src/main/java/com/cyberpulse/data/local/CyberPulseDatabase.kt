package com.cyberpulse.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.cyberpulse.data.local.dao.NewsDao
import com.cyberpulse.data.local.dao.BreachDao
import com.cyberpulse.data.local.dao.CVEDao
import com.cyberpulse.data.local.dao.EventDao
import com.cyberpulse.data.local.entity.*

/**
 * Room Database for CyberPulse
 * 
 * Provides local caching for offline support.
 * Maximum 50 articles cached for offline reading.
 */
@Database(
    entities = [
        NewsArticleEntity::class,
        DataBreachEntity::class,
        CVEEntity::class,
        CyberEventEntity::class,
        DailyTipEntity::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class CyberPulseDatabase : RoomDatabase() {
    
    abstract fun newsDao(): NewsDao
    abstract fun breachDao(): BreachDao
    abstract fun cveDao(): CVEDao
    abstract fun eventDao(): EventDao
    
    companion object {
        const val DATABASE_NAME = "cyberpulse_db"
    }
}

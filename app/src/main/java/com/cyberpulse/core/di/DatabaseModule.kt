package com.cyberpulse.core.di

import android.content.Context
import androidx.room.Room
import com.cyberpulse.data.local.CyberPulseDatabase
import com.cyberpulse.data.local.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for Database Dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): CyberPulseDatabase {
        return Room.databaseBuilder(
            context,
            CyberPulseDatabase::class.java,
            CyberPulseDatabase.DATABASE_NAME
        )
            .openHelperFactory(
                net.sqlcipher.database.SupportFactory(
                    net.sqlcipher.database.SQLiteDatabase.getBytes("your-secure-passphrase".toCharArray()) // TODO: Replace with KeyStore-derived key
                )
            )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun provideNewsDao(database: CyberPulseDatabase): NewsDao {
        return database.newsDao()
    }
    
    @Provides
    @Singleton
    fun provideBreachDao(database: CyberPulseDatabase): BreachDao {
        return database.breachDao()
    }
    
    @Provides
    @Singleton
    fun provideCVEDao(database: CyberPulseDatabase): CVEDao {
        return database.cveDao()
    }
    
    @Provides
    @Singleton
    fun provideEventDao(database: CyberPulseDatabase): EventDao {
        return database.eventDao()
    }
}

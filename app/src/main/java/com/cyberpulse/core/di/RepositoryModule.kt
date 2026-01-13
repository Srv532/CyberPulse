package com.cyberpulse.core.di

import com.cyberpulse.data.repository.*
import com.cyberpulse.domain.repository.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for Repository Dependencies
 * 
 * Binds repository implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    
    @Binds
    @Singleton
    abstract fun bindNewsRepository(
        impl: NewsRepositoryImpl
    ): NewsRepository
    
    @Binds
    @Singleton
    abstract fun bindBreachRepository(
        impl: BreachRepositoryImpl
    ): BreachRepository
    
    @Binds
    @Singleton
    abstract fun bindCVERepository(
        impl: CVERepositoryImpl
    ): CVERepository
    
    @Binds
    @Singleton
    abstract fun bindLearningRepository(
        impl: LearningRepositoryImpl
    ): LearningRepository
    
    @Binds
    @Singleton
    abstract fun bindEventsRepository(
        impl: EventsRepositoryImpl
    ): EventsRepository
    
    @Binds
    @Singleton
    abstract fun bindUserRepository(
        impl: UserRepositoryImpl
    ): UserRepository
    
    @Binds
    @Singleton
    abstract fun bindTipsRepository(
        impl: TipsRepositoryImpl
    ): TipsRepository
}

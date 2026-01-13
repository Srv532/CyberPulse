package com.cyberpulse.data.repository

import com.cyberpulse.data.local.dao.BreachDao
import com.cyberpulse.data.local.entity.toDomain
import com.cyberpulse.data.local.entity.toEntity
import com.cyberpulse.data.remote.api.HIBPApiService
import com.cyberpulse.data.remote.dto.toDomain
import com.cyberpulse.domain.model.DataBreach
import com.cyberpulse.domain.model.PwnedCheckResult
import com.cyberpulse.domain.repository.BreachRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BreachRepositoryImpl @Inject constructor(
    private val hibpApiService: HIBPApiService,
    private val breachDao: BreachDao
) : BreachRepository {
    
    override fun getAllBreaches(): Flow<Result<List<DataBreach>>> = flow {
        // Emit cached first
        val cached = breachDao.getAllBreaches()
        cached.collect { entities ->
            emit(Result.success(entities.map { it.toDomain() }))
        }
        
        // Fetch from network
        try {
            val breaches = hibpApiService.getAllBreaches().map { it.toDomain() }
            breachDao.insertBreaches(breaches.map { it.toEntity() })
        } catch (e: Exception) {
            // Already emitted cached, just log
        }
    }.flowOn(Dispatchers.IO)
    
    override fun getRecentBreaches(): Flow<Result<List<DataBreach>>> = flow {
        val thirtyDaysAgo = Instant.now().minusSeconds(30 * 24 * 60 * 60).toEpochMilli()
        breachDao.getRecentBreaches(thirtyDaysAgo).collect { entities ->
            emit(Result.success(entities.map { it.toDomain() }))
        }
    }.flowOn(Dispatchers.IO)
    
    override suspend fun searchBreaches(query: String): Result<List<DataBreach>> {
        return withContext(Dispatchers.IO) {
            try {
                val results = breachDao.searchBreaches("%$query%")
                Result.success(results.map { it.toDomain() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun checkEmailPwned(email: String): Result<PwnedCheckResult> {
        return withContext(Dispatchers.IO) {
            try {
                val breaches = hibpApiService.getBreachesForEmail(email).map { it.toDomain() }
                val pastes = try {
                    hibpApiService.getPastesForEmail(email).map { it.toDomain() }
                } catch (e: Exception) { null }
                
                Result.success(
                    PwnedCheckResult(
                        email = email,
                        isPwned = breaches.isNotEmpty(),
                        breaches = breaches,
                        pastes = pastes,
                        checkedAt = Instant.now()
                    )
                )
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun getBreachDetails(name: String): Result<DataBreach?> {
        return withContext(Dispatchers.IO) {
            try {
                val breach = hibpApiService.getBreachByName(name).toDomain()
                Result.success(breach)
            } catch (e: Exception) {
                // Try local cache
                val cached = breachDao.getBreachByName(name)?.toDomain()
                Result.success(cached)
            }
        }
    }
}

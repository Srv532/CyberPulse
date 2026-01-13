package com.cyberpulse.data.repository

import com.cyberpulse.data.local.dao.CVEDao
import com.cyberpulse.data.local.entity.toDomain
import com.cyberpulse.data.local.entity.toEntity
import com.cyberpulse.data.remote.api.CVEApiService
import com.cyberpulse.data.remote.dto.toDomain
import com.cyberpulse.domain.model.CVEEntry
import com.cyberpulse.domain.model.CVESeverity
import com.cyberpulse.domain.repository.CVERepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CVERepositoryImpl @Inject constructor(
    private val cveApiService: CVEApiService,
    private val cveDao: CVEDao
) : CVERepository {
    
    override fun getRecentCVEs(severity: CVESeverity?, limit: Int): Flow<Result<List<CVEEntry>>> = flow {
        // Emit cached
        val cached = if (severity != null) {
            cveDao.getCVEsBySeverity(severity.name, limit)
        } else {
            cveDao.getRecentCVEs(limit)
        }
        
        cached.collect { entities ->
            emit(Result.success(entities.map { it.toDomain() }))
        }
        
        // Fetch from network
        try {
            val response = cveApiService.searchCVEs(
                severity = severity?.name,
                limit = limit
            )
            val cves = response.vulnerabilities.map { it.toDomain() }
            cveDao.insertCVEs(cves.map { it.toEntity() })
        } catch (e: Exception) {
            // Already emitted cached
        }
    }.flowOn(Dispatchers.IO)
    
    override suspend fun searchCVEById(cveId: String): Result<CVEEntry?> {
        return withContext(Dispatchers.IO) {
            try {
                val cached = cveDao.getCVEById(cveId)
                if (cached != null) {
                    return@withContext Result.success(cached.toDomain())
                }
                
                val response = cveApiService.getCVEById(cveId)
                val cve = response.vulnerabilities.firstOrNull()?.toDomain()
                cve?.let { cveDao.insertCVE(it.toEntity()) }
                Result.success(cve)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override suspend fun searchCVEs(query: String): Result<List<CVEEntry>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = cveApiService.searchCVEs(keyword = query)
                val cves = response.vulnerabilities.map { it.toDomain() }
                Result.success(cves)
            } catch (e: Exception) {
                // Fall back to local
                val local = cveDao.searchCVEs("%$query%")
                Result.success(local.map { it.toDomain() })
            }
        }
    }
    
    override suspend fun getCVEsByProduct(productName: String): Result<List<CVEEntry>> {
        return withContext(Dispatchers.IO) {
            try {
                val local = cveDao.getCVEsByProduct("%$productName%")
                Result.success(local.map { it.toDomain() })
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    override fun getCriticalExploitedCVEs(): Flow<Result<List<CVEEntry>>> = flow {
        cveDao.getCriticalExploitedCVEs().collect { entities ->
            emit(Result.success(entities.map { it.toDomain() }))
        }
    }.flowOn(Dispatchers.IO)
}

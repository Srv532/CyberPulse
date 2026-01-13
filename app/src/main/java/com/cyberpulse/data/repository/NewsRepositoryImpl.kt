package com.cyberpulse.data.repository

import com.cyberpulse.data.local.dao.NewsDao
import com.cyberpulse.data.local.entity.toEntity
import com.cyberpulse.data.local.entity.toDomain
import com.cyberpulse.data.remote.api.NewsApiService
import com.cyberpulse.data.remote.dto.toDomain
import com.cyberpulse.domain.model.CyberTag
import com.cyberpulse.domain.model.NewsArticle
import com.cyberpulse.domain.model.NewsCategory
import com.cyberpulse.domain.repository.NewsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * News Repository Implementation
 * 
 * Implements the repository pattern with:
 * - Network-first strategy with local caching
 * - Offline fallback to cached data
 * - Flow-based reactive updates
 * - Error handling with Result wrapper
 */
@Singleton
class NewsRepositoryImpl @Inject constructor(
    private val newsApiService: NewsApiService,
    private val newsDao: NewsDao
) : NewsRepository {
    
    companion object {
        private const val CACHE_TIMEOUT_MINUTES = 15
        private const val MAX_CACHED_ARTICLES = 50
    }
    
    /**
     * Get news feed with caching strategy:
     * 1. Emit cached data immediately (if available)
     * 2. Fetch fresh data from network
     * 3. Cache new data and emit
     * 4. On network failure, continue with cached data
     */
    override fun getNewsFeed(
        category: NewsCategory?,
        forceRefresh: Boolean
    ): Flow<Result<List<NewsArticle>>> = flow {
        
        // Step 1: Emit cached data immediately (unless force refresh)
        if (!forceRefresh) {
            val cachedArticles = getCachedArticles(category)
            if (cachedArticles.isNotEmpty()) {
                emit(Result.success(cachedArticles))
            }
        }
        
        // Step 2: Fetch from network
        try {
            val networkArticles = fetchFromNetwork(category)
            
            // Step 3: Cache the results
            cacheArticles(networkArticles)
            
            // Step 4: Emit fresh data
            emit(Result.success(networkArticles))
            
        } catch (e: Exception) {
            // Network failed - check if we have cached data
            val cachedArticles = getCachedArticles(category)
            if (cachedArticles.isNotEmpty()) {
                // We already emitted cached data, just log the error
                emit(Result.success(cachedArticles))
            } else {
                // No cached data, emit error
                emit(Result.failure(e))
            }
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Search news articles by query.
     */
    override suspend fun searchNews(query: String): Result<List<NewsArticle>> {
        return withContext(Dispatchers.IO) {
            try {
                // First search locally
                val localResults = newsDao.searchArticles("%$query%")
                    .map { it.toDomain() }
                
                // Then search network
                val networkResults = try {
                    newsApiService.searchNews(query).articles.map { it.toDomain() }
                } catch (e: Exception) {
                    emptyList()
                }
                
                // Merge results, prioritizing network
                val merged = (networkResults + localResults)
                    .distinctBy { it.id }
                    .sortedByDescending { it.publishedAt }
                
                Result.success(merged)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Get news filtered by specific tags.
     */
    override fun getNewsByTags(tags: List<CyberTag>): Flow<Result<List<NewsArticle>>> = flow {
        try {
            // Get all news and filter by tags locally
            // In production, this would be a server-side filter
            val allNews = getCachedArticles(null)
            val filtered = allNews.filter { article ->
                article.tags.any { it in tags }
            }
            emit(Result.success(filtered))
            
            // Also fetch from network with tag filter
            val tagQuery = tags.joinToString(" OR ") { it.displayName }
            val networkResults = newsApiService.searchNews(tagQuery)
                .articles.map { it.toDomain() }
            
            cacheArticles(networkResults)
            emit(Result.success(networkResults))
            
        } catch (e: Exception) {
            val cached = getCachedArticles(null).filter { article ->
                article.tags.any { it in tags }
            }
            if (cached.isNotEmpty()) {
                emit(Result.success(cached))
            } else {
                emit(Result.failure(e))
            }
        }
    }.flowOn(Dispatchers.IO)
    
    /**
     * Get single article by ID.
     */
    override suspend fun getArticleById(id: String): Result<NewsArticle?> {
        return withContext(Dispatchers.IO) {
            try {
                val cached = newsDao.getArticleById(id)?.toDomain()
                if (cached != null) {
                    Result.success(cached)
                } else {
                    // Try fetching from network
                    val networkArticle = newsApiService.getArticle(id).toDomain()
                    newsDao.insertArticle(networkArticle.toEntity())
                    Result.success(networkArticle)
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Toggle save/unsave article for offline reading.
     */
    override suspend fun toggleSaveArticle(articleId: String): Result<Boolean> {
        return withContext(Dispatchers.IO) {
            try {
                val article = newsDao.getArticleById(articleId)
                if (article != null) {
                    val newSavedState = !article.isSaved
                    newsDao.updateArticleSavedState(articleId, newSavedState)
                    Result.success(newSavedState)
                } else {
                    Result.failure(Exception("Article not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    /**
     * Mark article as read.
     */
    override suspend fun markAsRead(articleId: String) {
        withContext(Dispatchers.IO) {
            newsDao.updateArticleReadState(articleId, true)
        }
    }
    
    /**
     * Get all saved articles.
     */
    override fun getSavedArticles(): Flow<List<NewsArticle>> {
        return newsDao.getSavedArticles()
            .map { entities -> entities.map { it.toDomain() } }
    }
    
    /**
     * Get count of cached articles.
     */
    override suspend fun getCachedArticleCount(): Int {
        return withContext(Dispatchers.IO) {
            newsDao.getArticleCount()
        }
    }
    
    /**
     * Refresh and cache latest articles.
     */
    override suspend fun refreshAndCache(limit: Int): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val articles = fetchFromNetwork(null)
                    .take(limit)
                cacheArticles(articles)
                
                // Clean up old articles (keep only last 50)
                newsDao.deleteOldArticles(MAX_CACHED_ARTICLES)
                
                Result.success(Unit)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    
    // ═══════════════════════════════════════════════════════════════════
    // PRIVATE HELPERS
    // ═══════════════════════════════════════════════════════════════════
    
    private suspend fun getCachedArticles(category: NewsCategory?): List<NewsArticle> {
        return if (category != null) {
            newsDao.getArticlesByCategory(category.name)
        } else {
            newsDao.getAllArticles()
        }.map { it.toDomain() }
    }
    
    private suspend fun fetchFromNetwork(category: NewsCategory?): List<NewsArticle> {
        val response = if (category != null) {
            newsApiService.getNewsByCategory(category.name.lowercase())
        } else {
            newsApiService.getLatestNews()
        }
        return response.articles.map { it.toDomain() }
    }
    
    private suspend fun cacheArticles(articles: List<NewsArticle>) {
        val entities = articles.map { it.toEntity() }
        newsDao.insertArticles(entities)
    }
}

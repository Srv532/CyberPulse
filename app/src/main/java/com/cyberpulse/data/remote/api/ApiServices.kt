package com.cyberpulse.data.remote.api

import com.cyberpulse.data.remote.dto.*
import retrofit2.http.*

/**
 * Retrofit API Services
 * 
 * Defines all network endpoints for the app.
 */

// ═══════════════════════════════════════════════════════════════════
// NEWS API SERVICE
// ═══════════════════════════════════════════════════════════════════

interface NewsApiService {
    
    /**
     * Get latest cybersecurity news.
     */
    @GET("news/latest")
    suspend fun getLatestNews(
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): NewsResponse
    
    /**
     * Get news by category.
     */
    @GET("news/category/{category}")
    suspend fun getNewsByCategory(
        @Path("category") category: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): NewsResponse
    
    /**
     * Search news articles.
     */
    @GET("news/search")
    suspend fun searchNews(
        @Query("q") query: String,
        @Query("page") page: Int = 1,
        @Query("limit") limit: Int = 20
    ): NewsResponse
    
    /**
     * Get single article by ID.
     */
    @GET("news/{id}")
    suspend fun getArticle(
        @Path("id") id: String
    ): ArticleDto
}

// ═══════════════════════════════════════════════════════════════════
// HAVE I BEEN PWNED API SERVICE
// ═══════════════════════════════════════════════════════════════════

interface HIBPApiService {
    
    /**
     * Get all breaches for an email address.
     */
    @GET("breachedaccount/{email}")
    suspend fun getBreachesForEmail(
        @Path("email") email: String,
        @Query("truncateResponse") truncate: Boolean = false
    ): List<BreachDto>
    
    /**
     * Get all known breaches.
     */
    @GET("breaches")
    suspend fun getAllBreaches(): List<BreachDto>
    
    /**
     * Get breach details by name.
     */
    @GET("breach/{name}")
    suspend fun getBreachByName(
        @Path("name") name: String
    ): BreachDto
    
    /**
     * Get pastes for an email address.
     */
    @GET("pasteaccount/{email}")
    suspend fun getPastesForEmail(
        @Path("email") email: String
    ): List<PasteDto>
}

// ═══════════════════════════════════════════════════════════════════
// NIST NVD CVE API SERVICE
// ═══════════════════════════════════════════════════════════════════

interface CVEApiService {
    
    /**
     * Search CVEs with various filters.
     */
    @GET("cves/2.0")
    suspend fun searchCVEs(
        @Query("keywordSearch") keyword: String? = null,
        @Query("cvssV3Severity") severity: String? = null,
        @Query("resultsPerPage") limit: Int = 50,
        @Query("startIndex") offset: Int = 0
    ): CVEResponse
    
    /**
     * Get CVE by ID.
     */
    @GET("cves/2.0")
    suspend fun getCVEById(
        @Query("cveId") cveId: String
    ): CVEResponse
    
    /**
     * Get CVEs by CPE (product).
     */
    @GET("cves/2.0")
    suspend fun getCVEsByProduct(
        @Query("cpeName") cpeName: String,
        @Query("resultsPerPage") limit: Int = 50
    ): CVEResponse
}

// ═══════════════════════════════════════════════════════════════════
// EVENTS API SERVICE (Hypothetical - could use CTFtime, etc.)
// ═══════════════════════════════════════════════════════════════════

interface EventsApiService {
    
    /**
     * Get upcoming cyber events.
     */
    @GET("events/upcoming")
    suspend fun getUpcomingEvents(
        @Query("type") type: String? = null,
        @Query("limit") limit: Int = 50
    ): EventsResponse
    
    /**
     * Get CTF events (from CTFtime API).
     */
    @GET("events")
    suspend fun getCTFEvents(
        @Query("limit") limit: Int = 100,
        @Query("start") start: Long? = null,
        @Query("finish") finish: Long? = null
    ): List<CTFEventDto>
}

// ═══════════════════════════════════════════════════════════════════
// DAILY TIP API SERVICE
// ═══════════════════════════════════════════════════════════════════

interface TipsApiService {
    
    /**
     * Get today's security tip.
     */
    @GET("tips/today")
    suspend fun getTodaysTip(): DailyTipDto
    
    /**
     * Get random tip.
     */
    @GET("tips/random")
    suspend fun getRandomTip(): DailyTipDto
}

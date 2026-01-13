package com.cyberpulse.domain.repository

import com.cyberpulse.domain.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository Interfaces - Clean Architecture
 * 
 * These define the contract between domain and data layers.
 * Implementations will handle caching, network calls, and data transformations.
 */

// ═══════════════════════════════════════════════════════════════════
// NEWS REPOSITORY
// ═══════════════════════════════════════════════════════════════════

interface NewsRepository {
    
    /**
     * Get news feed with optional category filter.
     * Returns cached data immediately, then refreshes from network.
     */
    fun getNewsFeed(
        category: NewsCategory? = null,
        forceRefresh: Boolean = false
    ): Flow<Result<List<NewsArticle>>>
    
    /**
     * Search news articles by query.
     */
    suspend fun searchNews(query: String): Result<List<NewsArticle>>
    
    /**
     * Get news by specific tags.
     */
    fun getNewsByTags(tags: List<CyberTag>): Flow<Result<List<NewsArticle>>>
    
    /**
     * Get a single article by ID.
     */
    suspend fun getArticleById(id: String): Result<NewsArticle?>
    
    /**
     * Save/unsave an article for offline reading.
     */
    suspend fun toggleSaveArticle(articleId: String): Result<Boolean>
    
    /**
     * Mark article as read.
     */
    suspend fun markAsRead(articleId: String)
    
    /**
     * Get saved articles for offline reading.
     */
    fun getSavedArticles(): Flow<List<NewsArticle>>
    
    /**
     * Get cached articles count.
     */
    suspend fun getCachedArticleCount(): Int
    
    /**
     * Refresh and cache latest articles (up to limit).
     */
    suspend fun refreshAndCache(limit: Int = 50): Result<Unit>
}

// ═══════════════════════════════════════════════════════════════════
// BREACH REPOSITORY
// ═══════════════════════════════════════════════════════════════════

interface BreachRepository {
    
    /**
     * Get all known breaches, sorted by date.
     */
    fun getAllBreaches(): Flow<Result<List<DataBreach>>>
    
    /**
     * Get recent breaches (last 30 days).
     */
    fun getRecentBreaches(): Flow<Result<List<DataBreach>>>
    
    /**
     * Search breaches by name or domain.
     */
    suspend fun searchBreaches(query: String): Result<List<DataBreach>>
    
    /**
     * Check if email has been pwned (HaveIBeenPwned API).
     */
    suspend fun checkEmailPwned(email: String): Result<PwnedCheckResult>
    
    /**
     * Get breach details by name.
     */
    suspend fun getBreachDetails(name: String): Result<DataBreach?>
}

// ═══════════════════════════════════════════════════════════════════
// CVE REPOSITORY
// ═══════════════════════════════════════════════════════════════════

interface CVERepository {
    
    /**
     * Get recent CVEs, optionally filtered by severity.
     */
    fun getRecentCVEs(
        severity: CVESeverity? = null,
        limit: Int = 50
    ): Flow<Result<List<CVEEntry>>>
    
    /**
     * Search CVE by ID (e.g., CVE-2024-1234).
     */
    suspend fun searchCVEById(cveId: String): Result<CVEEntry?>
    
    /**
     * Search CVEs by keyword.
     */
    suspend fun searchCVEs(query: String): Result<List<CVEEntry>>
    
    /**
     * Get CVEs affecting a specific product.
     */
    suspend fun getCVEsByProduct(productName: String): Result<List<CVEEntry>>
    
    /**
     * Get critical CVEs with known exploits.
     */
    fun getCriticalExploitedCVEs(): Flow<Result<List<CVEEntry>>>
}

// ═══════════════════════════════════════════════════════════════════
// LEARNING REPOSITORY
// ═══════════════════════════════════════════════════════════════════

interface LearningRepository {
    
    /**
     * Get featured/trending courses.
     */
    fun getFeaturedCourses(): Flow<Result<List<Course>>>
    
    /**
     * Get courses by provider.
     */
    fun getCoursesByProvider(provider: CourseProvider): Flow<Result<List<Course>>>
    
    /**
     * Get free courses only.
     */
    fun getFreeCourses(): Flow<Result<List<Course>>>
    
    /**
     * Get certification courses.
     */
    fun getCertificationCourses(): Flow<Result<List<Course>>>
    
    /**
     * Search courses by topic.
     */
    suspend fun searchCourses(query: String): Result<List<Course>>
}

// ═══════════════════════════════════════════════════════════════════
// EVENTS REPOSITORY
// ═══════════════════════════════════════════════════════════════════

interface EventsRepository {
    
    /**
     * Get upcoming events.
     */
    fun getUpcomingEvents(): Flow<Result<List<CyberEvent>>>
    
    /**
     * Get events by type.
     */
    fun getEventsByType(type: EventType): Flow<Result<List<CyberEvent>>>
    
    /**
     * Get events for a specific month (for calendar view).
     */
    suspend fun getEventsForMonth(year: Int, month: Int): Result<List<CyberEvent>>
    
    /**
     * Toggle registration/reminder for an event.
     */
    suspend fun toggleEventReminder(eventId: String): Result<Boolean>
    
    /**
     * Get events with active reminders.
     */
    fun getEventsWithReminders(): Flow<List<CyberEvent>>
}

// ═══════════════════════════════════════════════════════════════════
// USER REPOSITORY
// ═══════════════════════════════════════════════════════════════════

interface UserRepository {
    
    /**
     * Get current user profile.
     */
    fun getCurrentUser(): Flow<UserProfile?>
    
    /**
     * Sign in with Google.
     */
    suspend fun signInWithGoogle(idToken: String): Result<UserProfile>
    
    /**
     * Sign out.
     */
    suspend fun signOut()
    
    /**
     * Check if user is signed in.
     */
    fun isSignedIn(): Flow<Boolean>
    
    /**
     * Get/Set data preferences.
     */
    fun getDataPreferences(): Flow<DataPreferences>
    suspend fun updateDataPreferences(preferences: DataPreferences)
    
    /**
     * Get/Set notification preferences.
     */
    fun getNotificationPreferences(): Flow<NotificationPreferences>
    suspend fun updateNotificationPreferences(preferences: NotificationPreferences)
    
    /**
     * Check if user is in stateless mode.
     */
    fun isStatelessMode(): Flow<Boolean>
    
    /**
     * Enable stateless mode (no data persistence).
     */
    suspend fun enableStatelessMode()
}

// ═══════════════════════════════════════════════════════════════════
// TIPS REPOSITORY
// ═══════════════════════════════════════════════════════════════════

interface TipsRepository {
    
    /**
     * Get today's daily tip.
     */
    suspend fun getDailyTip(): Result<DailyTip>
    
    /**
     * Dismiss today's tip.
     */
    suspend fun dismissDailyTip(tipId: String)
    
    /**
     * Check if today's tip was dismissed.
     */
    fun isTodayTipDismissed(): Flow<Boolean>
}

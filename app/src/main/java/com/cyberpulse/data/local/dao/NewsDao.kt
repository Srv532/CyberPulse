package com.cyberpulse.data.local.dao

import androidx.room.*
import com.cyberpulse.data.local.entity.NewsArticleEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for News Articles
 */
@Dao
interface NewsDao {
    
    @Query("SELECT * FROM news_articles ORDER BY publishedAt DESC")
    suspend fun getAllArticles(): List<NewsArticleEntity>
    
    @Query("SELECT * FROM news_articles ORDER BY publishedAt DESC")
    fun getAllArticlesFlow(): Flow<List<NewsArticleEntity>>
    
    @Query("SELECT * FROM news_articles WHERE category = :category ORDER BY publishedAt DESC")
    suspend fun getArticlesByCategory(category: String): List<NewsArticleEntity>
    
    @Query("SELECT * FROM news_articles WHERE id = :id")
    suspend fun getArticleById(id: String): NewsArticleEntity?
    
    @Query("SELECT * FROM news_articles WHERE isSaved = 1 ORDER BY publishedAt DESC")
    fun getSavedArticles(): Flow<List<NewsArticleEntity>>
    
    @Query("SELECT * FROM news_articles WHERE title LIKE :query OR summary LIKE :query ORDER BY publishedAt DESC")
    suspend fun searchArticles(query: String): List<NewsArticleEntity>
    
    @Query("SELECT COUNT(*) FROM news_articles")
    suspend fun getArticleCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticle(article: NewsArticleEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertArticles(articles: List<NewsArticleEntity>)
    
    @Query("UPDATE news_articles SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateArticleSavedState(id: String, isSaved: Boolean)
    
    @Query("UPDATE news_articles SET isRead = :isRead WHERE id = :id")
    suspend fun updateArticleReadState(id: String, isRead: Boolean)
    
    @Query("DELETE FROM news_articles WHERE isSaved = 0 AND id NOT IN (SELECT id FROM news_articles ORDER BY cachedAt DESC LIMIT :keepCount)")
    suspend fun deleteOldArticles(keepCount: Int)
    
    @Query("DELETE FROM news_articles WHERE isSaved = 0")
    suspend fun deleteAllUnsaved()
    
    @Delete
    suspend fun deleteArticle(article: NewsArticleEntity)
}

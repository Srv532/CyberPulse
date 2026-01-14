package com.cyberpulse.data.repository

import com.cyberpulse.data.local.dao.NewsDao
import com.cyberpulse.data.local.entity.toDomain
import com.cyberpulse.data.remote.api.GitHubApiService
import com.cyberpulse.data.remote.api.RedditApiService
import com.cyberpulse.domain.model.OmniSearchResult
import com.cyberpulse.domain.model.SearchResultItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRepository @Inject constructor(
    private val newsDao: NewsDao,
    private val gitHubApi: GitHubApiService,
    private val redditApi: RedditApiService
    // private val nvdApi: NvdApiService // Placeholder for NVD
) {

    suspend fun omniSearch(query: String): OmniSearchResult = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext OmniSearchResult()

        // 1. Local DB Search (Fastest)
        val localJob = async {
            try {
                newsDao.searchArticles("%$query%").map { 
                    SearchResultItem.LocalNews(it.toDomain()) 
                }
            } catch (e: Exception) { emptyList() }
        }

        // 2. GitHub Search
        val githubJob = async {
            try {
                // "topic:cybersecurity" + query ensures relevance
                val q = "topic:cybersecurity $query"
                gitHubApi.searchRepos(q).items.take(3).map {
                    SearchResultItem.GitHubRepo(
                        name = it.full_name,
                        description = it.description,
                        stars = it.stargazers_count,
                        language = it.language,
                        url = it.html_url
                    )
                }
            } catch (e: Exception) { emptyList() }
        }

        // 3. Reddit Search
        val redditJob = async {
            try {
                // Search in specific subreddits
                val q = "subreddit:netsec OR subreddit:cybersecurity $query"
                redditApi.searchSubreddits(q).data.children.take(3).map {
                    SearchResultItem.RedditPost(
                        title = it.data.title,
                        subreddit = it.data.subreddit_name_prefixed,
                        upvotes = it.data.ups,
                        url = "https://reddit.com" + it.data.permalink
                    )
                }
            } catch (e: Exception) { emptyList() }
        }
        
        // 4. Definitions (Mocked for now, logic: if query matches known term)
        val definitionJob = async {
            getMockDefinition(query)
        }

        OmniSearchResult(
            definitions = definitionJob.await(),
            localResults = localJob.await(),
            githubRepos = githubJob.await(),
            redditPosts = redditJob.await(),
            vulnerabilities = emptyList() // TODO: Add NVD integration
        )
    }

    private fun getMockDefinition(query: String): List<SearchResultItem.Definition> {
        val q = query.lowercase()
        return when {
            "trojan" in q -> listOf(SearchResultItem.Definition("Trojan", "A type of malware that conceals its true content to fool a user into thinking it's a harmless file."))
            "ransomware" in q -> listOf(SearchResultItem.Definition("Ransomware", "Malware that employs encryption to hold a victim's information at ransom."))
            "phishing" in q -> listOf(SearchResultItem.Definition("Phishing", "A social engineering attack used to steal user data, including login credentials and credit card numbers."))
            "xss" in q -> listOf(SearchResultItem.Definition("XSS (Cross-Site Scripting)", "A vulnerability that allows an attacker to compromise the interactions that users have with a vulnerable application."))
            else -> emptyList()
        }
    }
}

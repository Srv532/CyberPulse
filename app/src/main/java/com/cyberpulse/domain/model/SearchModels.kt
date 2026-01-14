package com.cyberpulse.domain.model

sealed class SearchResultItem {
    data class Definition(val term: String, val meaning: String) : SearchResultItem()
    data class LocalNews(val article: NewsArticle) : SearchResultItem()
    data class GitHubRepo(
        val name: String,
        val description: String?,
        val stars: Int,
        val language: String?,
        val url: String
    ) : SearchResultItem()
    data class RedditPost(
        val title: String,
        val subreddit: String,
        val upvotes: Int,
        val url: String
    ) : SearchResultItem()
    data class Vulnerability(
        val cveId: String,
        val description: String,
        val score: Double?,
        val url: String
    ) : SearchResultItem()
}

data class OmniSearchResult(
    val definitions: List<SearchResultItem.Definition> = emptyList(),
    val localResults: List<SearchResultItem.LocalNews> = emptyList(),
    val githubRepos: List<SearchResultItem.GitHubRepo> = emptyList(),
    val redditPosts: List<SearchResultItem.RedditPost> = emptyList(),
    val vulnerabilities: List<SearchResultItem.Vulnerability> = emptyList()
) {
    fun isEmpty(): Boolean = definitions.isEmpty() && 
                             localResults.isEmpty() && 
                             githubRepos.isEmpty() && 
                             redditPosts.isEmpty() && 
                             vulnerabilities.isEmpty()
}

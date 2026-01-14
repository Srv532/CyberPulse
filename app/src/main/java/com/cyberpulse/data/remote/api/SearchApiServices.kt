package com.cyberpulse.data.remote.api

import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface GitHubApiService {
    @GET("search/repositories")
    suspend fun searchRepos(
        @Query("q") query: String,
        @Query("sort") sort: String = "stars",
        @Query("order") order: String = "desc",
        @Query("per_page") perPage: Int = 3
    ): GitHubSearchResponse
}

interface RedditApiService {
    @GET("search.json")
    suspend fun searchSubreddits(
        @Query("q") query: String,
        @Query("limit") limit: Int = 3,
        @Query("sort") sort: String = "relevance",
        @Query("type") type: String = "link" // Only posts
    ): RedditSearchResponse
}

// --- DTOs ---

data class GitHubSearchResponse(
    @SerializedName("items") val items: List<GitHubRepoDto>
)

data class GitHubRepoDto(
    val name: String,
    val full_name: String,
    val description: String?,
    val stargazers_count: Int,
    val language: String?,
    val html_url: String
)

data class RedditSearchResponse(
    val data: RedditDataDto
)

data class RedditDataDto(
    val children: List<RedditChildDto>
)

data class RedditChildDto(
    val data: RedditPostDto
)

data class RedditPostDto(
    val title: String,
    val subreddit_name_prefixed: String,
    val ups: Int,
    val permalink: String
)

package com.cyberpulse.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberpulse.domain.model.DailyTip
import com.cyberpulse.domain.model.NewsArticle
import com.cyberpulse.domain.model.UserProfile
import com.cyberpulse.domain.repository.NewsRepository
import com.cyberpulse.domain.repository.TipsRepository
import com.cyberpulse.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Home Screen ViewModel
 * 
 * Manages news feed, daily tips, and user interactions.
 */
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val newsRepository: NewsRepository,
    private val tipsRepository: TipsRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    private val _dailyTip = MutableStateFlow<DailyTip?>(null)
    val dailyTip: StateFlow<DailyTip?> = _dailyTip.asStateFlow()
    
    val userProfile: StateFlow<UserProfile?> = userRepository.getCurrentUser()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    init {
        loadNewsFeed()
        loadDailyTip()
    }
    
    /**
     * Load news feed.
     */
    private fun loadNewsFeed() {
        viewModelScope.launch {
            newsRepository.getNewsFeed()
                .collect { result ->
                    _uiState.value = result.fold(
                        onSuccess = { articles ->
                            HomeUiState.Success(articles)
                        },
                        onFailure = { error ->
                            HomeUiState.Error(error.message ?: "Failed to load news")
                        }
                    )
                }
        }
    }
    
    /**
     * Load daily security tip.
     */
    private fun loadDailyTip() {
        viewModelScope.launch {
            tipsRepository.getDailyTip()
                .onSuccess { tip ->
                    _dailyTip.value = tip
                }
        }
    }
    
    /**
     * Refresh the news feed.
     */
    fun refresh() {
        viewModelScope.launch {
            newsRepository.getNewsFeed(forceRefresh = true)
                .collect { result ->
                    _uiState.value = result.fold(
                        onSuccess = { HomeUiState.Success(it) },
                        onFailure = { HomeUiState.Error(it.message ?: "Refresh failed") }
                    )
                }
        }
    }
    
    /**
     * Toggle save state for an article.
     */
    fun toggleSave(articleId: String) {
        viewModelScope.launch {
            newsRepository.toggleSaveArticle(articleId)
        }
    }
    
    /**
     * Mark article as read.
     */
    fun markAsRead(articleId: String) {
        viewModelScope.launch {
            newsRepository.markAsRead(articleId)
        }
    }
    
    /**
     * Dismiss daily tip.
     */
    fun dismissTip(tipId: String) {
        viewModelScope.launch {
            tipsRepository.dismissDailyTip(tipId)
            _dailyTip.value = _dailyTip.value?.copy(isDismissed = true)
        }
    }
    
    /**
     * Sign out user.
     */
    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
        }
    }
}

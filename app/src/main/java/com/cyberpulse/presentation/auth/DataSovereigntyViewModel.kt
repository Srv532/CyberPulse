package com.cyberpulse.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberpulse.domain.model.DataPreferences
import com.cyberpulse.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for Data Sovereignty Screen
 */
@HiltViewModel
class DataSovereigntyViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    /**
     * Save user's data preferences.
     */
    fun savePreferences(preferences: DataPreferences) {
        viewModelScope.launch {
            userRepository.updateDataPreferences(preferences)
        }
    }
    
    /**
     * Enable stateless mode - no data persistence.
     */
    fun enableStatelessMode() {
        viewModelScope.launch {
            userRepository.enableStatelessMode()
        }
    }
}

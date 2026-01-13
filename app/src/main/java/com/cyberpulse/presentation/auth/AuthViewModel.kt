package com.cyberpulse.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cyberpulse.domain.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Authentication ViewModel
 * 
 * Handles Google Sign-In flow and authentication state management.
 */
@HiltViewModel
class AuthViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<AuthUiState>(AuthUiState.Initial)
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()
    
    /**
     * Sign in with Google ID token.
     */
    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _uiState.value = AuthUiState.Loading
            
            val result = userRepository.signInWithGoogle(idToken)
            
            _uiState.value = result.fold(
                onSuccess = { AuthUiState.Success },
                onFailure = { AuthUiState.Error(it.message ?: "Authentication failed") }
            )
        }
    }
    
    /**
     * Set loading state.
     */
    fun setLoading(isLoading: Boolean) {
        if (isLoading) {
            _uiState.value = AuthUiState.Loading
        } else if (_uiState.value is AuthUiState.Loading) {
            _uiState.value = AuthUiState.Initial
        }
    }
    
    /**
     * Check if user is already signed in.
     */
    fun checkAuthState() {
        viewModelScope.launch {
            userRepository.isSignedIn().collect { isSignedIn ->
                if (isSignedIn && _uiState.value is AuthUiState.Initial) {
                    _uiState.value = AuthUiState.Success
                }
            }
        }
    }
    
    /**
     * Sign out.
     */
    fun signOut() {
        viewModelScope.launch {
            userRepository.signOut()
            _uiState.value = AuthUiState.Initial
        }
    }
}

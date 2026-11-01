package com.taskpulse.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor() : ViewModel() {
    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, statusMessage = null) }
            kotlinx.coroutines.delay(800)
            if (email.contains("@") && password.length >= 6) {
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true, userEmail = email, statusMessage = "Logged in successfully.") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Enter a valid email and password.") }
            }
        }
    }

    fun signUp(email: String, password: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, statusMessage = null) }
            kotlinx.coroutines.delay(1000)
            if (email.contains("@") && password.length >= 6) {
                _uiState.update { it.copy(isLoading = false, isAuthenticated = true, userEmail = email, statusMessage = "Account created and signed in.") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Create a valid account with strong password.") }
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, statusMessage = null, errorMessage = null) }
            kotlinx.coroutines.delay(800)
            if (email.contains("@")) {
                _uiState.update { it.copy(isLoading = false, statusMessage = "Reset link sent to $email.") }
            } else {
                _uiState.update { it.copy(isLoading = false, errorMessage = "Enter a valid email to reset password.") }
            }
        }
    }

    fun signInWithGoogle() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null, statusMessage = null) }
            kotlinx.coroutines.delay(1200)
            _uiState.update { it.copy(isLoading = false, isAuthenticated = true, userEmail = "google.user@taskpulse.ai", statusMessage = "Signed in with Google.") }
        }
    }

    fun logout() {
        _uiState.update { AuthUiState() }
    }
}

data class AuthUiState(
    val isLoading: Boolean = false,
    val isAuthenticated: Boolean = false,
    val userEmail: String = "",
    val errorMessage: String? = null,
    val statusMessage: String? = null
)

package com.edustudycraft.newdemoappl.presentation.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.edustudycraft.newdemoappl.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val passwordVisible: Boolean = false,
)

sealed interface LoginEvent {
    data object Succeeded : LoginEvent
}

class LoginViewModel(
    private val authRepository: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<LoginEvent>(extraBufferCapacity = 1)
    val events: SharedFlow<LoginEvent> = _events.asSharedFlow()

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, emailError = null, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, passwordError = null, errorMessage = null) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(passwordVisible = !it.passwordVisible) }
    }

    fun login() {
        val current = _uiState.value
        if (current.isLoading) return

        val email = current.email.trim()
        val password = current.password
        val emailError = validateEmail(email)
        val passwordError = validatePassword(password)
        if (emailError != null || passwordError != null) {
            _uiState.update {
                it.copy(emailError = emailError, passwordError = passwordError, errorMessage = null)
            }
            return
        }

        _uiState.update {
            it.copy(isLoading = true, emailError = null, passwordError = null, errorMessage = null)
        }
        viewModelScope.launch {
            authRepository.login(email, password)
                .onSuccess { _events.emit(LoginEvent.Succeeded) }
                .onFailure { error ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            errorMessage = error.message ?: "Login failed. Please try again.",
                        )
                    }
                }
        }
    }

    private fun validateEmail(email: String): String? = when {
        email.isBlank() -> EMAIL_REQUIRED
        !EMAIL_PATTERN.matches(email) -> EMAIL_INVALID
        else -> null
    }

    private fun validatePassword(password: String): String? = when {
        password.isBlank() -> PASSWORD_REQUIRED
        password.length < MIN_PASSWORD_LENGTH -> PASSWORD_SHORT
        else -> null
    }

    companion object {
        const val EMAIL_REQUIRED = "Email is required"
        const val EMAIL_INVALID = "Enter a valid email address"
        const val PASSWORD_REQUIRED = "Password is required"
        const val PASSWORD_SHORT = "Password must be at least 6 characters"

        private const val MIN_PASSWORD_LENGTH = 6
        private val EMAIL_PATTERN = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
    }
}

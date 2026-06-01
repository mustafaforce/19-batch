package com.example.studteach.feature.auth.presentation.register

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studteach.StudTeachApp
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.domain.model.User
import com.example.studteach.feature.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData(RegisterUiState())
    val uiState: LiveData<RegisterUiState> = _uiState

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value?.copy(name = name, nameError = null)
    }

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value?.copy(email = email, emailError = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value?.copy(password = password, passwordError = null)
    }

    fun onConfirmPasswordChanged(confirmPassword: String) {
        _uiState.value = _uiState.value?.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    fun onRoleChanged(isStudent: Boolean) {
        _uiState.value = _uiState.value?.copy(isStudent = isStudent)
    }

    fun register() {
        val state = _uiState.value ?: return

        val validationResult = validateFields(state)
        if (validationResult != null) {
            _uiState.value = validationResult
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        val role = if (state.isStudent) "student" else "teacher"

        viewModelScope.launch {
            registerUseCase(state.name, state.email, state.password, role)
                .onSuccess { user ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        isSuccess = true,
                        registeredUser = user
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Registration failed"
                    )
                }
        }
    }

    private fun validateFields(state: RegisterUiState): RegisterUiState? {
        var nameError: String? = null
        var emailError: String? = null
        var passwordError: String? = null
        var confirmPasswordError: String? = null

        if (state.name.isBlank()) {
            nameError = "Name is required"
        }

        if (state.email.isBlank()) {
            emailError = "Email is required"
        } else if (!state.email.contains("@")) {
            emailError = "Enter a valid email"
        }

        if (state.password.isBlank()) {
            passwordError = "Password is required"
        } else if (state.password.length < 6) {
            passwordError = "Password must be at least 6 characters"
        }

        if (state.confirmPassword != state.password) {
            confirmPasswordError = "Passwords do not match"
        }

        return if (nameError != null || emailError != null || passwordError != null || confirmPasswordError != null) {
            state.copy(
                nameError = nameError,
                emailError = emailError,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError
            )
        } else {
            null
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = StudTeachApp.instance
            val repository = AuthRepositoryImpl(app.supabase)
            val registerUseCase = RegisterUseCase(repository)
            return RegisterViewModel(registerUseCase) as T
        }
    }
}

data class RegisterUiState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isStudent: Boolean = true,
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val registeredUser: User? = null,
    val errorMessage: String? = null,
    val nameError: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null
)

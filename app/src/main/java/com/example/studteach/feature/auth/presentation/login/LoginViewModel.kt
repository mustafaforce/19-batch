package com.example.studteach.feature.auth.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studteach.StudTeachApp
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.domain.model.User
import com.example.studteach.feature.auth.domain.usecase.LoginUseCase
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData(LoginUiState())
    val uiState: LiveData<LoginUiState> = _uiState

    fun onEmailChanged(email: String) {
        _uiState.value = _uiState.value?.copy(email = email, emailError = null)
    }

    fun onPasswordChanged(password: String) {
        _uiState.value = _uiState.value?.copy(password = password, passwordError = null)
    }

    fun login() {
        val state = _uiState.value ?: return

        val validationResult = validateFields(state)
        if (validationResult != null) {
            _uiState.value = validationResult
            return
        }

        _uiState.value = state.copy(isLoading = true, errorMessage = null)

        viewModelScope.launch {
            loginUseCase(state.email, state.password)
                .onSuccess { user ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        isSuccess = true,
                        loggedInUser = user
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Login failed"
                    )
                }
        }
    }

    private fun validateFields(state: LoginUiState): LoginUiState? {
        var emailError: String? = null
        var passwordError: String? = null

        if (state.email.isBlank()) {
            emailError = "Email is required"
        }

        if (state.password.isBlank()) {
            passwordError = "Password is required"
        }

        return if (emailError != null || passwordError != null) {
            state.copy(emailError = emailError, passwordError = passwordError)
        } else {
            null
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = StudTeachApp.instance
            val repository = AuthRepositoryImpl(app.supabase)
            val loginUseCase = LoginUseCase(repository)
            return LoginViewModel(loginUseCase) as T
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val loggedInUser: User? = null,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null
)

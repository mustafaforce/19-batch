package com.example.studteach.feature.setting.presentation.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studteach.StudTeachApp
import com.example.studteach.core.storage.StorageRepository
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.example.studteach.feature.auth.domain.usecase.UpdateProfileUseCase
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val updateProfileUseCase: UpdateProfileUseCase,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private var userId: String = ""

    private val _uiState = MutableLiveData(ProfileUiState())
    val uiState: LiveData<ProfileUiState> = _uiState

    fun loadProfile() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase() ?: return@launch
            userId = user.id
            _uiState.value = _uiState.value?.copy(
                fullName = user.fullName,
                email = user.email,
                role = user.role,
                avatarUrl = user.avatarUrl,
                course = user.course ?: "",
                level = user.level ?: "",
                term = user.term ?: "",
                department = user.department ?: "",
                isLoaded = true
            )
        }
    }

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value?.copy(fullName = name, nameError = null)
    }

    fun onCourseChanged(course: String) {
        _uiState.value = _uiState.value?.copy(course = course)
    }

    fun onLevelChanged(level: String) {
        _uiState.value = _uiState.value?.copy(level = level)
    }

    fun onTermChanged(term: String) {
        _uiState.value = _uiState.value?.copy(term = term)
    }

    fun onDepartmentChanged(department: String) {
        _uiState.value = _uiState.value?.copy(department = department)
    }

    fun saveProfile() {
        val state = _uiState.value ?: return

        if (state.fullName.isBlank()) {
            _uiState.value = state.copy(nameError = "Name is required")
            return
        }

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            updateProfileUseCase(
                fullName = state.fullName,
                course = state.course.ifBlank { null },
                level = state.level.ifBlank { null },
                term = state.term.ifBlank { null },
                department = state.department.ifBlank { null },
                avatarUrl = state.avatarUrl
            )
                .onSuccess {
                    _uiState.value = _uiState.value?.copy(
                        isSaving = false,
                        saveSuccess = true
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value?.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Failed to save"
                    )
                }
        }
    }

    fun clearSaveSuccess() {
        _uiState.value = _uiState.value?.copy(saveSuccess = false)
    }

    fun uploadAvatar(bytes: ByteArray, fileName: String) {
        if (userId.isBlank()) return
        _uiState.value = _uiState.value?.copy(isSaving = true)
        viewModelScope.launch {
            storageRepository.uploadAvatar(userId, bytes, fileName)
                .onSuccess { url ->
                    _uiState.value = _uiState.value?.copy(
                        avatarUrl = url,
                        isSaving = false
                    )
                    updateProfileUseCase(
                        fullName = _uiState.value?.fullName ?: "",
                        course = _uiState.value?.course?.ifBlank { null },
                        level = _uiState.value?.level?.ifBlank { null },
                        term = _uiState.value?.term?.ifBlank { null },
                        department = _uiState.value?.department?.ifBlank { null },
                        avatarUrl = url
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value?.copy(
                        isSaving = false,
                        errorMessage = error.message ?: "Failed to upload"
                    )
                }
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = StudTeachApp.instance
            val authRepo = AuthRepositoryImpl(app.supabase)
            val storageRepo = StorageRepository(app.supabase)
            return ProfileViewModel(
                GetCurrentUserUseCase(authRepo),
                UpdateProfileUseCase(authRepo),
                storageRepo
            ) as T
        }
    }
}

data class ProfileUiState(
    val isLoaded: Boolean = false,
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val avatarUrl: String? = null,
    val course: String = "",
    val level: String = "",
    val term: String = "",
    val department: String = "",
    val nameError: String? = null,
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val errorMessage: String? = null
)

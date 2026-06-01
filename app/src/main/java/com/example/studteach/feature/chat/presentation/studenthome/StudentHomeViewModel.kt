package com.example.studteach.feature.chat.presentation.studenthome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studteach.StudTeachApp
import com.example.studteach.feature.chat.data.TeacherRepository
import com.example.studteach.feature.chat.data.TeacherWithAvailability
import com.example.studteach.feature.chat.domain.usecase.GetTeachersUseCase
import kotlinx.coroutines.launch

class StudentHomeViewModel(
    private val getTeachersUseCase: GetTeachersUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData(StudentHomeUiState())
    val uiState: LiveData<StudentHomeUiState> = _uiState

    fun loadTeachers() {
        _uiState.value = _uiState.value?.copy(isLoading = true)
        viewModelScope.launch {
            try {
                val teachers = getTeachersUseCase()
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    teachers = teachers
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value?.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Failed to load teachers"
                )
            }
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = StudTeachApp.instance
            val teacherRepository = TeacherRepository(app.supabase)
            val getTeachersUseCase = GetTeachersUseCase(teacherRepository)
            return StudentHomeViewModel(getTeachersUseCase) as T
        }
    }
}

data class StudentHomeUiState(
    val isLoading: Boolean = false,
    val teachers: List<TeacherWithAvailability> = emptyList(),
    val errorMessage: String? = null
)

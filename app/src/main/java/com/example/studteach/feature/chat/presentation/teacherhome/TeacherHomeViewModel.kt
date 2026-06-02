package com.example.studteach.feature.chat.presentation.teacherhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studteach.StudTeachApp
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.example.studteach.feature.chat.data.ChatRepository
import com.example.studteach.feature.chat.data.TeacherAvailabilityRepositoryImpl
import com.example.studteach.feature.chat.domain.model.Conversation
import com.example.studteach.feature.chat.domain.usecase.GetAvailabilityUseCase
import com.example.studteach.feature.chat.domain.usecase.GetConversationsUseCase
import com.example.studteach.feature.chat.domain.usecase.SetAvailabilityUseCase
import kotlinx.coroutines.launch

class TeacherHomeViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getAvailabilityUseCase: GetAvailabilityUseCase,
    private val setAvailabilityUseCase: SetAvailabilityUseCase,
    private val getConversationsUseCase: GetConversationsUseCase
) : ViewModel() {

    private val _uiState = MutableLiveData(TeacherHomeUiState())
    val uiState: LiveData<TeacherHomeUiState> = _uiState

    private var teacherId: String = ""

    fun loadData() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            teacherId = user?.id ?: return@launch

            _uiState.value = _uiState.value?.copy(teacherName = user.fullName)

            val availability = getAvailabilityUseCase(teacherId)
            if (availability != null) {
                _uiState.value = _uiState.value?.copy(
                    timeFrom = availability.timeFrom,
                    timeTo = availability.timeTo
                )
            }

            loadConversations()
        }
    }

    private suspend fun loadConversations() {
        try {
            val conversations = getConversationsUseCase(teacherId)
            _uiState.value = _uiState.value?.copy(conversations = conversations)
        } catch (e: Exception) {
            // Silently fail; conversations just stay empty
        }
    }

    fun onTimeFromChanged(time: String) {
        _uiState.value = _uiState.value?.copy(timeFrom = time)
    }

    fun onTimeToChanged(time: String) {
        _uiState.value = _uiState.value?.copy(timeTo = time)
    }

    fun saveAvailability() {
        val state = _uiState.value ?: return
        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            setAvailabilityUseCase(
                teacherId = teacherId,
                isActive = true,
                timeFrom = state.timeFrom,
                timeTo = state.timeTo
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

    fun refreshName() {
        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            _uiState.value = _uiState.value?.copy(teacherName = user?.fullName ?: "")
        }
    }

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = StudTeachApp.instance
            val authRepo = AuthRepositoryImpl(app.supabase)
            val availabilityRepo = TeacherAvailabilityRepositoryImpl(app.supabase)
            val chatRepo = ChatRepository(app.supabase)
            return TeacherHomeViewModel(
                GetCurrentUserUseCase(authRepo),
                GetAvailabilityUseCase(availabilityRepo),
                SetAvailabilityUseCase(availabilityRepo),
                GetConversationsUseCase(chatRepo)
            ) as T
        }
    }
}

data class TeacherHomeUiState(
    val teacherName: String = "",
    val timeFrom: String = "09:00 AM",
    val timeTo: String = "05:00 PM",
    val isSaving: Boolean = false,
    val saveSuccess: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val errorMessage: String? = null
)

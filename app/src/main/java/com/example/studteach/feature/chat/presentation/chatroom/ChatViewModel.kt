package com.example.studteach.feature.chat.presentation.chatroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.studteach.StudTeachApp
import com.example.studteach.core.storage.StorageRepository
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.example.studteach.feature.chat.data.ChatRepository
import com.example.studteach.feature.chat.domain.model.Message
import com.example.studteach.feature.chat.domain.usecase.ObserveMessagesUseCase
import com.example.studteach.feature.chat.domain.usecase.SendMessageUseCase
import kotlinx.coroutines.launch

class ChatViewModel(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val observeMessagesUseCase: ObserveMessagesUseCase,
    private val storageRepository: StorageRepository
) : ViewModel() {

    private val _uiState = MutableLiveData(ChatUiState())
    val uiState: LiveData<ChatUiState> = _uiState

    private var currentUserId: String = ""
    private var otherUserId: String = ""
    private var otherUserName: String = ""
    private var isAvailable: Boolean = false

    fun initialize(userId: String, userName: String, available: Boolean) {
        otherUserId = userId
        otherUserName = userName
        isAvailable = available

        _uiState.value = _uiState.value?.copy(
            otherUserName = userName,
            isAvailable = available
        )

        viewModelScope.launch {
            val user = getCurrentUserUseCase()
            currentUserId = user?.id ?: return@launch

            loadHistory()
            startRealtime()
        }
    }

    private suspend fun loadHistory() {
        _uiState.value = _uiState.value?.copy(isLoading = true)
        try {
            val messages = observeMessagesUseCase.loadHistory(currentUserId, otherUserId)
            _uiState.value = _uiState.value?.copy(
                isLoading = false,
                messages = messages
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value?.copy(
                isLoading = false,
                errorMessage = e.message ?: "Failed to load messages"
            )
        }
    }

    private fun startRealtime() {
        viewModelScope.launch {
            try {
                observeMessagesUseCase.observeNew(currentUserId, otherUserId)
                    .collect { message ->
                        val current = _uiState.value?.messages?.toMutableList() ?: mutableListOf()
                        if (current.none { it.id == message.id }) {
                            current.add(message)
                            _uiState.value = _uiState.value?.copy(messages = current)
                        }
                    }
            } catch (e: Exception) {
            }
        }
    }

    fun sendMessage(content: String) {
        sendInternal(content, null)
    }

    fun sendImage(content: String, imageBytes: ByteArray, fileName: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value?.copy(isLoading = true)
            storageRepository.uploadChatImage(currentUserId, otherUserId, imageBytes, fileName)
                .onSuccess { imageUrl ->
                    sendInternal(content, imageUrl)
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to upload image"
                    )
                }
        }
    }

    private fun sendInternal(content: String, imageUrl: String?) {
        val finalContent = content.ifBlank { if (imageUrl != null) " " else "" }
        viewModelScope.launch {
            sendMessageUseCase(currentUserId, otherUserId, finalContent, imageUrl)
                .onSuccess { message ->
                    val current = _uiState.value?.messages?.toMutableList() ?: mutableListOf()
                    current.add(message)
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        messages = current
                    )
                }
                .onFailure { error ->
                    _uiState.value = _uiState.value?.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to send"
                    )
                }
        }
    }

    fun getCurrentUserId(): String = currentUserId

    class Factory : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            val app = StudTeachApp.instance
            val authRepo = AuthRepositoryImpl(app.supabase)
            val chatRepo = ChatRepository(app.supabase)
            val storageRepo = StorageRepository(app.supabase)
            return ChatViewModel(
                GetCurrentUserUseCase(authRepo),
                SendMessageUseCase(chatRepo),
                ObserveMessagesUseCase(chatRepo),
                storageRepo
            ) as T
        }
    }
}

data class ChatUiState(
    val otherUserName: String = "",
    val isAvailable: Boolean = false,
    val isLoading: Boolean = false,
    val messages: List<Message> = emptyList(),
    val errorMessage: String? = null
)

package com.example.studteach.feature.chat.domain.usecase

import com.example.studteach.feature.chat.data.ChatRepository
import com.example.studteach.feature.chat.domain.model.Message
import kotlinx.coroutines.flow.Flow

class ObserveMessagesUseCase(
    private val chatRepository: ChatRepository
) {
    suspend fun loadHistory(
        userId1: String,
        userId2: String
    ): List<Message> {
        return chatRepository.getMessages(userId1, userId2)
    }

    fun observeNew(
        senderId: String,
        receiverId: String
    ): Flow<Message> {
        return chatRepository.observeNewMessages(senderId, receiverId)
    }
}

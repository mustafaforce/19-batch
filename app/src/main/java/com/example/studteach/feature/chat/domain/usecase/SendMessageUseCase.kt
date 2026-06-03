package com.example.studteach.feature.chat.domain.usecase

import com.example.studteach.feature.chat.data.ChatRepository
import com.example.studteach.feature.chat.domain.model.Message

class SendMessageUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(
        senderId: String,
        receiverId: String,
        content: String,
        imageUrl: String? = null
    ): Result<Message> {
        return chatRepository.sendMessage(senderId, receiverId, content, imageUrl)
    }
}

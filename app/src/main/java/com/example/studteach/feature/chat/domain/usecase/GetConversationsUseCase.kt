package com.example.studteach.feature.chat.domain.usecase

import com.example.studteach.feature.chat.data.ChatRepository
import com.example.studteach.feature.chat.domain.model.Conversation

class GetConversationsUseCase(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(teacherId: String): List<Conversation> {
        return chatRepository.getConversations(teacherId)
    }
}

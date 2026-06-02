package com.example.studteach.feature.chat.domain.model

data class Conversation(
    val studentId: String,
    val studentName: String,
    val avatarUrl: String? = null,
    val lastMessage: String,
    val lastMessageTime: String,
    val lastMessageIsMine: Boolean = false
)

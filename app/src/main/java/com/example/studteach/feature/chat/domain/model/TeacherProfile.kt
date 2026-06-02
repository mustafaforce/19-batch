package com.example.studteach.feature.chat.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherProfile(
    val id: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val course: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null
)

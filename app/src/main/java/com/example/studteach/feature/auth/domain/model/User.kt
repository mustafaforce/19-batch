package com.example.studteach.feature.auth.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    @SerialName("full_name")
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val course: String? = null,
    val level: String? = null,
    val term: String? = null,
    val department: String? = null,
    @SerialName("avatar_url")
    val avatarUrl: String? = null,
    @SerialName("created_at")
    val createdAt: String? = null
)

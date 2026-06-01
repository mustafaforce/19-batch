package com.example.studteach.feature.auth.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: String = "",
    val fullName: String = "",
    val email: String = "",
    val role: String = "",
    val course: String? = null,
    val createdAt: String? = null
)

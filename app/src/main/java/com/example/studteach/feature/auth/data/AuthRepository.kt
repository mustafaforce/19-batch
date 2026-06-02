package com.example.studteach.feature.auth.data

import com.example.studteach.feature.auth.domain.model.User

interface AuthRepository {
    suspend fun register(
        fullName: String,
        email: String,
        password: String,
        role: String
    ): Result<User>

    suspend fun login(
        email: String,
        password: String
    ): Result<User>

    suspend fun getCurrentUser(): User?

    suspend fun updateProfile(
        fullName: String,
        course: String?,
        level: String?,
        term: String?,
        department: String?,
        avatarUrl: String? = null
    ): Result<User>

    suspend fun logout()
}

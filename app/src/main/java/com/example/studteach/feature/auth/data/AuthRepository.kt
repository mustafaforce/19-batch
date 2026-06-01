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

    suspend fun logout()
}

package com.example.studteach.feature.auth.domain.usecase

import com.example.studteach.feature.auth.data.AuthRepository
import com.example.studteach.feature.auth.domain.model.User

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        email: String,
        password: String,
        role: String
    ): Result<User> {
        return authRepository.register(fullName, email, password, role)
    }
}

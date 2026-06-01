package com.example.studteach.feature.auth.domain.usecase

import com.example.studteach.feature.auth.data.AuthRepository
import com.example.studteach.feature.auth.domain.model.User

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String
    ): Result<User> {
        return authRepository.login(email, password)
    }
}

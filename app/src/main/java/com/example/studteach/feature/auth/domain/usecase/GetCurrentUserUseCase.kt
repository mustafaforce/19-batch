package com.example.studteach.feature.auth.domain.usecase

import com.example.studteach.feature.auth.data.AuthRepository
import com.example.studteach.feature.auth.domain.model.User

class GetCurrentUserUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): User? {
        return authRepository.getCurrentUser()
    }
}

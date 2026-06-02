package com.example.studteach.feature.auth.domain.usecase

import com.example.studteach.feature.auth.data.AuthRepository
import com.example.studteach.feature.auth.domain.model.User

class UpdateProfileUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        fullName: String,
        course: String?,
        level: String?,
        term: String?,
        department: String?,
        avatarUrl: String? = null
    ): Result<User> {
        return authRepository.updateProfile(fullName, course, level, term, department, avatarUrl)
    }
}

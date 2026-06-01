package com.example.studteach.feature.chat.domain.usecase

import com.example.studteach.feature.chat.data.TeacherAvailabilityRepository
import com.example.studteach.feature.chat.domain.model.TeacherAvailability

class GetAvailabilityUseCase(
    private val repository: TeacherAvailabilityRepository
) {
    suspend operator fun invoke(teacherId: String): TeacherAvailability? {
        return repository.getAvailability(teacherId)
    }
}

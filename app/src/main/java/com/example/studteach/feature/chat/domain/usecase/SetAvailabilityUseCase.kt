package com.example.studteach.feature.chat.domain.usecase

import com.example.studteach.feature.chat.data.TeacherAvailabilityRepository
import com.example.studteach.feature.chat.domain.model.TeacherAvailability

class SetAvailabilityUseCase(
    private val repository: TeacherAvailabilityRepository
) {
    suspend operator fun invoke(
        teacherId: String,
        isActive: Boolean,
        timeFrom: String,
        timeTo: String
    ): Result<TeacherAvailability> {
        val availability = TeacherAvailability(
            teacherId = teacherId,
            isActive = isActive,
            timeFrom = timeFrom,
            timeTo = timeTo
        )
        return repository.saveAvailability(availability)
    }
}

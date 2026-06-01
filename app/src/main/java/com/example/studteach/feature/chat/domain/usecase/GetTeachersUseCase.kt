package com.example.studteach.feature.chat.domain.usecase

import com.example.studteach.feature.chat.data.TeacherRepository
import com.example.studteach.feature.chat.data.TeacherWithAvailability

class GetTeachersUseCase(
    private val teacherRepository: TeacherRepository
) {
    suspend operator fun invoke(): List<TeacherWithAvailability> {
        return teacherRepository.getTeachersWithAvailability()
    }
}

package com.example.studteach.feature.chat.data

import com.example.studteach.feature.chat.domain.model.TeacherAvailability

interface TeacherAvailabilityRepository {
    suspend fun getAvailability(teacherId: String): TeacherAvailability?
    suspend fun saveAvailability(availability: TeacherAvailability): Result<TeacherAvailability>
}

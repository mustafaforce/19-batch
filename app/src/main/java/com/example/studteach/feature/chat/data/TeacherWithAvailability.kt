package com.example.studteach.feature.chat.data

import com.example.studteach.feature.chat.domain.model.TeacherProfile

data class TeacherWithAvailability(
    val id: String,
    val fullName: String,
    val course: String?,
    val isAvailable: Boolean,
    val timeFrom: String,
    val timeTo: String
)

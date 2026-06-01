package com.example.studteach.feature.chat.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TeacherAvailability(
    val id: String = "",
    @SerialName("teacher_id")
    val teacherId: String = "",
    @SerialName("is_active")
    val isActive: Boolean = false,
    @SerialName("time_from")
    val timeFrom: String = "09:00 AM",
    @SerialName("time_to")
    val timeTo: String = "05:00 PM",
    @SerialName("updated_at")
    val updatedAt: String? = null
)

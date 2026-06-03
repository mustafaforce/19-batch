package com.example.studteach.feature.chat.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class TeacherRepository(
    private val supabase: SupabaseClient
) {
    private val timeFormat = DateTimeFormatter.ofPattern("hh:mm a", Locale.US)

    suspend fun getTeachersWithAvailability(): List<TeacherWithAvailability> {
        val profiles = supabase.postgrest.from("profiles")
            .select(columns = Columns.ALL) {
                filter {
                    eq("role", "teacher")
                }
            }
            .decodeList<com.example.studteach.feature.chat.domain.model.TeacherProfile>()

        val availabilityList = supabase.postgrest.from("teacher_availability")
            .select(columns = Columns.ALL)
            .decodeList<com.example.studteach.feature.chat.domain.model.TeacherAvailability>()

        val now = LocalTime.now()

        return profiles.map { teacher ->
            val avail = availabilityList.find { it.teacherId == teacher.id }
            val isActive = avail?.isActive == true
            val inRange = avail?.let {
                try {
                    val from = LocalTime.parse(it.timeFrom, timeFormat)
                    val to = LocalTime.parse(it.timeTo, timeFormat)
                    !now.isBefore(from) && !now.isAfter(to)
                } catch (e: Exception) {
                    false
                }
            } ?: false

            TeacherWithAvailability(
                id = teacher.id,
                fullName = teacher.fullName,
                course = teacher.course,
                avatarUrl = teacher.avatarUrl,
                isAvailable = isActive && inRange,
                timeFrom = avail?.timeFrom ?: "",
                timeTo = avail?.timeTo ?: ""
            )
        }
    }
}

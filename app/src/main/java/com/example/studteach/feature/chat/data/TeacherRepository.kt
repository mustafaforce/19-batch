package com.example.studteach.feature.chat.data

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns

class TeacherRepository(
    private val supabase: SupabaseClient
) {
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

        return profiles.map { teacher ->
            val avail = availabilityList.find { it.teacherId == teacher.id }
            TeacherWithAvailability(
                id = teacher.id,
                fullName = teacher.fullName,
                course = teacher.course,
                avatarUrl = teacher.avatarUrl,
                isAvailable = avail?.isActive == true,
                timeFrom = avail?.timeFrom ?: "",
                timeTo = avail?.timeTo ?: ""
            )
        }
    }
}

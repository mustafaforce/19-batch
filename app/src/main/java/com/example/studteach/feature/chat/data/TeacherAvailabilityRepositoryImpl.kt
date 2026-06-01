package com.example.studteach.feature.chat.data

import com.example.studteach.feature.chat.domain.model.TeacherAvailability
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class TeacherAvailabilityRepositoryImpl(
    private val supabase: SupabaseClient
) : TeacherAvailabilityRepository {

    override suspend fun getAvailability(teacherId: String): TeacherAvailability? {
        return try {
            val list = supabase.postgrest.from("teacher_availability")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("teacher_id", teacherId)
                    }
                }
                .decodeList<TeacherAvailability>()

            list.firstOrNull()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun saveAvailability(
        availability: TeacherAvailability
    ): Result<TeacherAvailability> {
        return runCatching {
            val existing = getAvailability(availability.teacherId)

            if (existing != null) {
                supabase.postgrest.from("teacher_availability")
                    .update({
                        set("is_active", availability.isActive)
                        set("time_from", availability.timeFrom)
                        set("time_to", availability.timeTo)
                    }) {
                        filter {
                            eq("id", existing.id)
                        }
                    }
                availability.copy(id = existing.id)
            } else {
                supabase.postgrest.from("teacher_availability")
                    .insert(
                        buildJsonObject {
                            put("teacher_id", JsonPrimitive(availability.teacherId))
                            put("is_active", JsonPrimitive(availability.isActive))
                            put("time_from", JsonPrimitive(availability.timeFrom))
                            put("time_to", JsonPrimitive(availability.timeTo))
                        }
                    )
                availability
            }
        }
    }
}

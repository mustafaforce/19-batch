package com.example.studteach.feature.auth.data

import com.example.studteach.feature.auth.domain.model.User
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AuthRepositoryImpl(
    private val supabase: SupabaseClient
) : AuthRepository {

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
        role: String
    ): Result<User> {
        return runCatching {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                this.data = buildJsonObject {
                    put("full_name", JsonPrimitive(fullName))
                    put("role", JsonPrimitive(role))
                }
            }
            // No session yet — email must be verified first.
            // SQL trigger handles profile creation on user insert.
            User(
                id = "",
                fullName = fullName,
                email = email,
                role = role
            )
        }
    }

    override suspend fun login(
        email: String,
        password: String
    ): Result<User> {
        return runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }

            val session = supabase.auth.currentSessionOrNull()
            val userId = session?.user?.id
                ?: throw Exception("Login failed: no session returned")

            supabase.postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id", userId)
                    }
                    single()
                }
                .decodeAs<User>()
        }
    }

    override suspend fun getCurrentUser(): User? {
        return try {
            val session = supabase.auth.currentSessionOrNull()
            val userId = session?.user?.id ?: return null

            supabase.postgrest.from("profiles")
                .select(columns = Columns.ALL) {
                    filter {
                        eq("id", userId)
                    }
                    single()
                }
                .decodeAs<User>()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun updateProfile(
        fullName: String,
        course: String?,
        level: String?,
        term: String?,
        department: String?,
        avatarUrl: String?
    ): Result<User> {
        return runCatching {
            val session = supabase.auth.currentSessionOrNull()
            val userId = session?.user?.id
                ?: throw Exception("Not authenticated")

            val updates = buildJsonObject {
                put("full_name", JsonPrimitive(fullName))
                put("course", JsonPrimitive(course))
                put("level", JsonPrimitive(level))
                put("term", JsonPrimitive(term))
                put("department", JsonPrimitive(department))
                put("avatar_url", JsonPrimitive(avatarUrl))
            }

            supabase.postgrest.from("profiles")
                .update(updates) {
                    filter {
                        eq("id", userId)
                    }
                    select()
                }
                .decodeList<User>()
                .first()
        }
    }

    override suspend fun logout() {
        supabase.auth.signOut()
    }
}

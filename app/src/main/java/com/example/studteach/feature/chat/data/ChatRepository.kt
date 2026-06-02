package com.example.studteach.feature.chat.data

import com.example.studteach.feature.chat.domain.model.Conversation
import com.example.studteach.feature.chat.domain.model.Message
import com.example.studteach.feature.chat.domain.model.TeacherProfile
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.realtime.PostgresAction
import io.github.jan.supabase.realtime.channel
import io.github.jan.supabase.realtime.postgresChangeFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.serialization.json.Json

class ChatRepository(
    private val supabase: SupabaseClient
) {
    suspend fun sendMessage(
        senderId: String,
        receiverId: String,
        content: String
    ): Result<Message> {
        return runCatching {
            supabase.postgrest.from("messages")
                .insert(
                    Message(
                        senderId = senderId,
                        receiverId = receiverId,
                        content = content
                    )
                ) {
                    select()
                }
                .decodeAs<Message>()
        }
    }

    suspend fun getMessages(
        userId1: String,
        userId2: String
    ): List<Message> {
        return supabase.postgrest.from("messages")
            .select(columns = Columns.ALL) {
                filter {
                    or {
                        and {
                            eq("sender_id", userId1)
                            eq("receiver_id", userId2)
                        }
                        and {
                            eq("sender_id", userId2)
                            eq("receiver_id", userId1)
                        }
                    }
                }
                order(column = "created_at", order = Order.ASCENDING)
            }
            .decodeList<Message>()
    }

    fun observeNewMessages(
        senderId: String,
        receiverId: String
    ): Flow<Message> = flow {
        val channel = supabase.channel(
            "messages_${minOf(senderId, receiverId)}_${maxOf(senderId, receiverId)}"
        )
        channel.subscribe()

        channel.postgresChangeFlow<PostgresAction.Insert>(schema = "public")
            .collect { action ->
                val message = Json.decodeFromJsonElement(
                    Message.serializer(),
                    action.record
                )
                emit(message)
            }
    }

    suspend fun getConversations(teacherId: String): List<Conversation> {
        val messages = supabase.postgrest.from("messages")
            .select(columns = Columns.ALL) {
                filter {
                    or {
                        eq("sender_id", teacherId)
                        eq("receiver_id", teacherId)
                    }
                }
                order(column = "created_at", order = Order.DESCENDING)
            }
            .decodeList<Message>()

        val studentIds = messages
            .map { if (it.senderId == teacherId) it.receiverId else it.senderId }
            .distinct()

        if (studentIds.isEmpty()) return emptyList()

        val profiles = supabase.postgrest.from("profiles")
            .select(columns = Columns.ALL) {
                filter {
                    isIn("id", studentIds)
                }
            }
            .decodeList<TeacherProfile>()

        val profileMap = profiles.associateBy { it.id }

        val latestPerStudent = messages
            .groupBy { if (it.senderId == teacherId) it.receiverId else it.senderId }
            .mapValues { (_, msgs) -> msgs.maxBy { it.createdAt } }

        return latestPerStudent.map { (sid, msg) ->
            val student = profileMap[sid]
            Conversation(
                studentId = sid,
                studentName = student?.fullName ?: "Unknown",
                lastMessage = msg.content,
                lastMessageTime = formatTimestamp(msg.createdAt)
            )
        }.sortedByDescending { it.lastMessageTime }
    }

    private fun formatTimestamp(timestamp: String): String = timestamp.substring(11, 16)
}

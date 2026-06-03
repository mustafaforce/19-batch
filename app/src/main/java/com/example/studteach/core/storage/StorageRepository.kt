package com.example.studteach.core.storage

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.storage.uploadAsFlow
import kotlinx.coroutines.flow.last

class StorageRepository(
    private val supabase: SupabaseClient
) {
    companion object {
        private const val BUCKET = "avatars"
        private const val CHAT_BUCKET = "chat-images"
    }

    suspend fun uploadAvatar(userId: String, fileBytes: ByteArray, fileName: String): Result<String> {
        return runCatching {
            val bucket = supabase.storage.from(BUCKET)
            val path = "$userId/$fileName"

            bucket.uploadAsFlow(
                path = path,
                data = fileBytes
            ) {
                upsert = true
            }.last()

            supabase.storage.from(BUCKET).publicUrl(path)
        }
    }

    suspend fun uploadChatImage(senderId: String, receiverId: String, fileBytes: ByteArray, fileName: String): Result<String> {
        return runCatching {
            val conversationKey = if (senderId < receiverId) "$senderId-$receiverId" else "$receiverId-$senderId"
            val path = "$conversationKey/$fileName"

            supabase.storage.from(CHAT_BUCKET).uploadAsFlow(
                path = path,
                data = fileBytes
            ) {
                upsert = true
            }.last()

            supabase.storage.from(CHAT_BUCKET).publicUrl(path)
        }
    }
}

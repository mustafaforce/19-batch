package com.example.studteach.feature.chat.presentation.chatroom

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.studteach.databinding.ItemMessageReceivedBinding
import com.example.studteach.databinding.ItemMessageSentBinding
import com.example.studteach.feature.chat.domain.model.Message
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ChatAdapter(
    val currentUserId: String
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<Message>()

    companion object {
        private const val VIEW_TYPE_SENT = 1
        private const val VIEW_TYPE_RECEIVED = 2

        private fun formatTimestamp(timestamp: String): String {
            if (timestamp.isBlank()) return ""
            return try {
                val instant = Instant.parse(timestamp)
                val local = instant.atZone(ZoneId.systemDefault())
                local.format(DateTimeFormatter.ofPattern("HH:mm"))
            } catch (e: Exception) {
                ""
            }
        }
    }

    fun addMessage(message: Message) {
        messages.add(message)
        notifyItemInserted(messages.size - 1)
    }

    fun setMessages(list: List<Message>) {
        messages.clear()
        messages.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == VIEW_TYPE_SENT) {
            val binding = ItemMessageSentBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            SentViewHolder(binding)
        } else {
            val binding = ItemMessageReceivedBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
            ReceivedViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = messages[position]
        when (holder) {
            is SentViewHolder -> holder.bind(message)
            is ReceivedViewHolder -> holder.bind(message)
        }
    }

    override fun getItemCount(): Int = messages.size

    class SentViewHolder(
        private val binding: ItemMessageSentBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            val hasImage = !message.imageUrl.isNullOrBlank()
            val hasText = message.content.isNotBlank()

            binding.ivImage.visibility = if (hasImage) View.VISIBLE else View.GONE
            binding.tvMessage.visibility = if (hasText) View.VISIBLE else View.GONE

            if (hasImage) {
                binding.ivImage.load(message.imageUrl) {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(12f))
                }
            }

            if (hasText) {
                binding.tvMessage.text = message.content
            }

            binding.tvTime.text = formatTimestamp(message.createdAt)
        }
    }

    class ReceivedViewHolder(
        private val binding: ItemMessageReceivedBinding
    ) : RecyclerView.ViewHolder(binding.root) {
        fun bind(message: Message) {
            val hasImage = !message.imageUrl.isNullOrBlank()
            val hasText = message.content.isNotBlank()

            binding.ivImage.visibility = if (hasImage) View.VISIBLE else View.GONE
            binding.tvMessage.visibility = if (hasText) View.VISIBLE else View.GONE

            if (hasImage) {
                binding.ivImage.load(message.imageUrl) {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(12f))
                }
            }

            if (hasText) {
                binding.tvMessage.text = message.content
            }

            binding.tvTime.text = formatTimestamp(message.createdAt)
        }
    }
}

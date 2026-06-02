package com.example.studteach.feature.chat.presentation.teacherhome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studteach.databinding.ItemConversationBinding
import com.example.studteach.feature.chat.domain.model.Conversation

class ConversationAdapter(
    private val onConversationClick: (Conversation) -> Unit
) : RecyclerView.Adapter<ConversationAdapter.ViewHolder>() {

    private var conversations: List<Conversation> = emptyList()

    fun submitList(list: List<Conversation>) {
        conversations = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConversationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(conversations[position])
    }

    override fun getItemCount(): Int = conversations.size

    inner class ViewHolder(
        private val binding: ItemConversationBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(conversation: Conversation) {
            binding.tvStudentName.text = conversation.studentName
            binding.tvLastMessage.text = if (conversation.lastMessageIsMine) {
                "You: ${conversation.lastMessage}"
            } else {
                conversation.lastMessage
            }
            binding.tvTime.text = conversation.lastMessageTime

            binding.root.setOnClickListener {
                onConversationClick(conversation)
            }
        }
    }
}

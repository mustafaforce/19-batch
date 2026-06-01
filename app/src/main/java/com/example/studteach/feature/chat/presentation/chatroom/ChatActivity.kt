package com.example.studteach.feature.chat.presentation.chatroom

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studteach.R
import com.example.studteach.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private var userId: String = ""
    private var userName: String = ""
    private var isAvailable: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        userId = intent.getStringExtra("USER_ID") ?: ""
        userName = intent.getStringExtra("USER_NAME") ?: ""
        isAvailable = intent.getBooleanExtra("IS_AVAILABLE", false)

        setupToolbar()
        setupRecyclerView()
        setupInput()
        applyAvailability()
    }

    private fun applyAvailability() {
        showChatClosed(!isAvailable)
    }

    private fun setupToolbar() {
        binding.tvName.text = userName
        if (isAvailable) {
            binding.tvStatus.text = getString(R.string.label_online)
            binding.tvStatus.setTextColor(getColor(R.color.success))
        } else {
            binding.tvStatus.text = getString(R.string.label_offline)
            binding.tvStatus.setTextColor(getColor(R.color.disabled))
        }
        binding.toolbar.setNavigationOnClickListener {
            finish()
        }
    }

    private fun setupRecyclerView() {
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
            // TODO: Set adapter with real data from Supabase Realtime
        }

        // TODO: Subscribe to Supabase Realtime for new messages
    }

    private fun setupInput() {
        binding.btnSend.setOnClickListener {
            val message = binding.etMessage.text.toString().trim()
            if (message.isNotEmpty()) {
                sendMessage(message)
                binding.etMessage.text.clear()
            }
        }
    }

    private fun sendMessage(message: String) {
        // TODO: Send message to Supabase
    }

    private fun showChatClosed(isClosed: Boolean) {
        binding.tvChatClosed.visibility = if (isClosed) View.VISIBLE else View.GONE
        binding.layoutInput.visibility = if (isClosed) View.GONE else View.VISIBLE
    }
}

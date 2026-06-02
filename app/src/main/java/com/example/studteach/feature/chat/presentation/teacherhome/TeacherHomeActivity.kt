package com.example.studteach.feature.chat.presentation.teacherhome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studteach.R
import com.example.studteach.StudTeachApp
import com.example.studteach.databinding.ActivityTeacherHomeBinding
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.presentation.login.LoginActivity
import com.example.studteach.feature.chat.domain.model.Conversation
import com.example.studteach.feature.chat.presentation.chatroom.ChatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TeacherHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherHomeBinding
    private lateinit var conversationAdapter: ConversationAdapter
    private val viewModel: TeacherHomeViewModel by viewModels { TeacherHomeViewModel.Factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAvailability()
        setupConversations()
        observeViewModel()

        viewModel.loadData()
    }

    private fun setupToolbar() {
        binding.toolbar.setOnMenuItemClickListener { item ->
            if (item.itemId == R.id.action_logout) {
                CoroutineScope(Dispatchers.IO).launch {
                    AuthRepositoryImpl(StudTeachApp.instance.supabase).logout()
                }
                val intent = Intent(this, LoginActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            } else false
        }
    }

    private fun setupAvailability() {
        binding.switchAvailability.setOnCheckedChangeListener { _, isChecked ->
            viewModel.onToggleActive(isChecked)
        }

        binding.btnTimeFrom.setOnClickListener {
            showTimePicker { hour, minute ->
                val time = formatTime(hour, minute)
                binding.btnTimeFrom.text = time
                viewModel.onTimeFromChanged(time)
            }
        }

        binding.btnTimeTo.setOnClickListener {
            showTimePicker { hour, minute ->
                val time = formatTime(hour, minute)
                binding.btnTimeTo.text = time
                viewModel.onTimeToChanged(time)
            }
        }

        binding.btnSaveAvailability.setOnClickListener {
            viewModel.saveAvailability()
        }
    }

    private fun setupConversations() {
        conversationAdapter = ConversationAdapter { conversation ->
            val intent = Intent(this, ChatActivity::class.java).apply {
                putExtra("USER_ID", conversation.studentId)
                putExtra("USER_NAME", conversation.studentName)
                putExtra("IS_AVAILABLE", true)
            }
            startActivity(intent)
        }

        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(this@TeacherHomeActivity)
            adapter = conversationAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            binding.toolbar.title = state.teacherName.ifEmpty {
                getString(R.string.title_teacher_home)
            }

            binding.switchAvailability.isChecked = state.isActive
            binding.switchAvailability.text = if (state.isActive) "Available" else "Unavailable"
            binding.btnTimeFrom.text = state.timeFrom
            binding.btnTimeTo.text = state.timeTo
            binding.btnSaveAvailability.isEnabled = !state.isSaving

            if (state.conversations.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvConversations.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvConversations.visibility = View.VISIBLE
                conversationAdapter.submitList(state.conversations)
            }

            if (state.saveSuccess) {
                Toast.makeText(this, "Availability saved", Toast.LENGTH_SHORT).show()
                viewModel.clearSaveSuccess()
            }

            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showTimePicker(onTimeSet: (Int, Int) -> Unit) {
        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_12H)
            .setHour(9)
            .setMinute(0)
            .setTitleText("Select Time")
            .build()

        picker.addOnPositiveButtonClickListener {
            onTimeSet(picker.hour, picker.minute)
        }

        picker.show(supportFragmentManager, "timePicker")
    }

    private fun formatTime(hour: Int, minute: Int): String {
        val amPm = if (hour < 12) "AM" else "PM"
        val displayHour = when {
            hour == 0 -> 12
            hour > 12 -> hour - 12
            else -> hour
        }
        return String.format("%02d:%02d %s", displayHour, minute, amPm)
    }
}

package com.example.studteach.feature.chat.presentation.teacherhome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studteach.databinding.ActivityTeacherHomeBinding
import com.example.studteach.feature.chat.presentation.chatroom.ChatActivity
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

class TeacherHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTeacherHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTeacherHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupToolbar()
        setupAvailability()
        setupRecyclerView()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "Dashboard"
    }

    private fun setupAvailability() {
        binding.switchAvailability.setOnCheckedChangeListener { _, isChecked ->
            binding.switchAvailability.text = if (isChecked) "Available" else "Unavailable"
            // TODO: Update availability in Supabase
        }

        binding.btnTimeFrom.setOnClickListener {
            showTimePicker { hour, minute ->
                binding.btnTimeFrom.text = formatTime(hour, minute)
            }
        }

        binding.btnTimeTo.setOnClickListener {
            showTimePicker { hour, minute ->
                binding.btnTimeTo.text = formatTime(hour, minute)
            }
        }

        binding.btnSaveAvailability.setOnClickListener {
            // TODO: Save availability to Supabase
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

    private fun setupRecyclerView() {
        binding.rvConversations.apply {
            layoutManager = LinearLayoutManager(this@TeacherHomeActivity)
            // TODO: Set adapter with real data from Supabase
        }

        // TODO: Load conversations from Supabase
        showEmptyState(true)
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvConversations.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    fun openChat(studentId: String, studentName: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("USER_ID", studentId)
            putExtra("USER_NAME", studentName)
        }
        startActivity(intent)
    }
}

package com.example.studteach.feature.chat.presentation.studenthome

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studteach.databinding.ActivityStudentHomeBinding
import com.example.studteach.feature.chat.presentation.chatroom.ChatActivity

class StudentHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentHomeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        setupToolbar()
    }

    private fun setupToolbar() {
        binding.toolbar.title = "My Teachers"
    }

    private fun setupRecyclerView() {
        binding.rvTeachers.apply {
            layoutManager = LinearLayoutManager(this@StudentHomeActivity)
            // TODO: Set adapter with real data from Supabase
        }

        // TODO: Load teachers from Supabase
        showEmptyState(true)
    }

    private fun showEmptyState(isEmpty: Boolean) {
        binding.tvEmpty.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvTeachers.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }

    fun openChat(teacherId: String, teacherName: String) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("USER_ID", teacherId)
            putExtra("USER_NAME", teacherName)
        }
        startActivity(intent)
    }
}

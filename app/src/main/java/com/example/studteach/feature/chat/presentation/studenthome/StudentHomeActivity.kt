package com.example.studteach.feature.chat.presentation.studenthome

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.studteach.R
import com.example.studteach.StudTeachApp
import com.example.studteach.databinding.ActivityStudentHomeBinding
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.presentation.login.LoginActivity
import com.example.studteach.feature.chat.data.TeacherWithAvailability
import com.example.studteach.feature.chat.presentation.chatroom.ChatActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StudentHomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStudentHomeBinding
    private lateinit var adapter: TeacherAdapter
    private val viewModel: StudentHomeViewModel by viewModels { StudentHomeViewModel.Factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityStudentHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyToolbarInsets()

        setupToolbar()
        setupRecyclerView()
        observeViewModel()

        viewModel.loadTeachers()
    }

    private fun applyToolbarInsets() {
        val toolbarBaseHeight = resources.getDimensionPixelSize(R.dimen.toolbar_height)

        ViewCompat.setOnApplyWindowInsetsListener(binding.toolbar) { toolbar, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            toolbar.layoutParams.height = toolbarBaseHeight + statusBar.top
            toolbar.setPadding(0, statusBar.top, 0, 0)
            insets
        }
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

    private fun setupRecyclerView() {
        adapter = TeacherAdapter { teacher ->
            navigateToChat(teacher)
        }

        binding.rvTeachers.apply {
            layoutManager = LinearLayoutManager(this@StudentHomeActivity)
            adapter = this@StudentHomeActivity.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            if (state.isLoading) return@observe

            if (state.teachers.isEmpty()) {
                binding.tvEmpty.visibility = View.VISIBLE
                binding.rvTeachers.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.rvTeachers.visibility = View.VISIBLE
                adapter.submitList(state.teachers)
            }

            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToChat(teacher: TeacherWithAvailability) {
        val intent = Intent(this, ChatActivity::class.java).apply {
            putExtra("USER_ID", teacher.id)
            putExtra("USER_NAME", teacher.fullName)
            putExtra("IS_AVAILABLE", teacher.isAvailable)
        }
        startActivity(intent)
    }
}

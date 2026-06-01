package com.example.studteach.feature.auth.presentation.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studteach.databinding.ActivityLoginBinding
import com.example.studteach.feature.auth.presentation.register.RegisterActivity
import com.example.studteach.feature.chat.presentation.studenthome.StudentHomeActivity
import com.example.studteach.feature.chat.presentation.teacherhome.TeacherHomeActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels { LoginViewModel.Factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            viewModel.onEmailChanged(binding.etEmail.text.toString().trim())
            viewModel.onPasswordChanged(binding.etPassword.text.toString().trim())
            viewModel.login()
        }

        binding.tvRegisterLink.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            showLoading(state.isLoading)

            state.emailError?.let {
                binding.tilEmail.error = it
            } ?: run { binding.tilEmail.error = null }

            state.passwordError?.let {
                binding.tilPassword.error = it
            } ?: run { binding.tilPassword.error = null }

            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            if (state.isSuccess && state.loggedInUser != null) {
                navigateToHome(state.loggedInUser!!)
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnLogin.isEnabled = !isLoading
    }

    private fun navigateToHome(user: com.example.studteach.feature.auth.domain.model.User) {
        val isStudent = user.role == "student"
        val intent = if (isStudent) {
            Intent(this, StudentHomeActivity::class.java)
        } else {
            Intent(this, TeacherHomeActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}

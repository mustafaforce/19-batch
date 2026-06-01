package com.example.studteach.feature.auth.presentation.register

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.studteach.databinding.ActivityRegisterBinding
import com.example.studteach.feature.auth.presentation.login.LoginActivity

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private val viewModel: RegisterViewModel by viewModels { RegisterViewModel.Factory() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.btnRegister.setOnClickListener {
            viewModel.onNameChanged(binding.etName.text.toString().trim())
            viewModel.onEmailChanged(binding.etEmail.text.toString().trim())
            viewModel.onPasswordChanged(binding.etPassword.text.toString().trim())
            binding.etConfirmPassword.text.toString().trim().let {
                viewModel.onConfirmPasswordChanged(it)
            }
            viewModel.onRoleChanged(binding.rbStudent.isChecked)
            viewModel.register()
        }

        binding.tvLoginLink.setOnClickListener { finish() }
    }

    private fun observeViewModel() {
        viewModel.uiState.observe(this) { state ->
            showLoading(state.isLoading)

            state.nameError?.let {
                binding.tilName.error = it
            } ?: run { binding.tilName.error = null }

            state.emailError?.let {
                binding.tilEmail.error = it
            } ?: run { binding.tilEmail.error = null }

            state.passwordError?.let {
                binding.tilPassword.error = it
            } ?: run { binding.tilPassword.error = null }

            state.confirmPasswordError?.let {
                binding.tilConfirmPassword.error = it
            } ?: run { binding.tilConfirmPassword.error = null }

            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }

            if (state.isSuccess) {
                Toast.makeText(
                    this,
                    "Account created! Check your email to verify, then log in.",
                    Toast.LENGTH_LONG
                ).show()
                startActivity(Intent(this, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        binding.btnRegister.isEnabled = !isLoading
    }
}

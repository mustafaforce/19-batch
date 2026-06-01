package com.example.studteach.feature.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.studteach.StudTeachApp
import com.example.studteach.databinding.ActivitySplashBinding
import com.example.studteach.feature.auth.data.AuthRepositoryImpl
import com.example.studteach.feature.auth.domain.usecase.GetCurrentUserUseCase
import com.example.studteach.feature.auth.presentation.login.LoginActivity
import com.example.studteach.feature.chat.presentation.studenthome.StudentHomeActivity
import com.example.studteach.feature.chat.presentation.teacherhome.TeacherHomeActivity
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val getCurrentUser = GetCurrentUserUseCase(
            AuthRepositoryImpl(StudTeachApp.instance.supabase)
        )

        lifecycleScope.launch {
            val user = getCurrentUser()
            if (user != null) {
                navigateToHome(user.role)
            } else {
                navigateToLogin()
            }
        }
    }

    private fun navigateToHome(role: String) {
        val intent = if (role == "student") {
            Intent(this, StudentHomeActivity::class.java)
        } else {
            Intent(this, TeacherHomeActivity::class.java)
        }
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    private fun navigateToLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}

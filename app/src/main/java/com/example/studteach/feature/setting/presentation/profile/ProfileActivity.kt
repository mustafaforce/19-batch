package com.example.studteach.feature.setting.presentation.profile

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.CircleCropTransformation
import com.example.studteach.R
import com.example.studteach.databinding.ActivityProfileBinding

class ProfileActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileBinding
    private val viewModel: ProfileViewModel by viewModels { ProfileViewModel.Factory() }

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImagePicked(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        observeViewModel()

        viewModel.loadProfile()
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener { finish() }

        binding.cvAvatar.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.btnSave.setOnClickListener {
            viewModel.onNameChanged(binding.etName.text.toString().trim())
            viewModel.onCourseChanged(binding.etCourse.text.toString().trim())
            viewModel.onLevelChanged(binding.etLevel.text.toString().trim())
            viewModel.onTermChanged(binding.etTerm.text.toString().trim())
            viewModel.onDepartmentChanged(binding.etDepartment.text.toString().trim())
            viewModel.saveProfile()
        }
    }

    private fun onImagePicked(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val bytes = inputStream.use { it.readBytes() }
        val fileName = getFileName(uri) ?: "avatar.jpg"

        viewModel.uploadAvatar(
            bytes = bytes,
            fileName = fileName
        )
    }

    private fun getFileName(uri: Uri): String? {
        contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (cursor.moveToFirst() && nameIndex >= 0) {
                return cursor.getString(nameIndex)
            }
        }
        return null
    }

    private fun observeViewModel() {
        var initialLoad = true

        viewModel.uiState.observe(this) { state ->
            if (!state.isLoaded) return@observe

            binding.tilName.error = state.nameError

            if (initialLoad) {
                initialLoad = false
                binding.etName.setText(state.fullName)
                binding.tvEmail.text = state.email
                binding.etCourse.setText(state.course)
                binding.etLevel.setText(state.level)
                binding.etTerm.setText(state.term)
                binding.etDepartment.setText(state.department)
            }

            state.avatarUrl?.let { url ->
                binding.ivAvatar.load(url) {
                    crossfade(true)
                    placeholder(R.drawable.ic_person)
                    error(R.drawable.ic_person)
                    transformations(CircleCropTransformation())
                }
            } ?: run {
                binding.ivAvatar.setImageResource(R.drawable.ic_person)
            }

            val isStudent = state.role == "student"
            binding.tilLevel.visibility = if (isStudent) View.VISIBLE else View.GONE
            binding.tilTerm.visibility = if (isStudent) View.VISIBLE else View.GONE
            binding.tilDepartment.visibility = if (isStudent) View.VISIBLE else View.GONE

            binding.btnSave.isEnabled = !state.isSaving
            showLoading(state.isSaving)

            if (state.saveSuccess) {
                Toast.makeText(this, "Profile updated", Toast.LENGTH_SHORT).show()
                viewModel.clearSaveSuccess()
                finish()
            }

            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}

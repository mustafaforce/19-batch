package com.example.studteach.feature.chat.presentation.chatroom

import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import coil.transform.CircleCropTransformation
import com.example.studteach.R
import com.example.studteach.databinding.ActivityChatBinding

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatViewModel by viewModels { ChatViewModel.Factory() }
    private var adapter: ChatAdapter? = null

    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onImagePicked(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        applyWindowInsets()

        val userId = intent.getStringExtra("USER_ID") ?: ""
        val userName = intent.getStringExtra("USER_NAME") ?: ""
        val isAvailable = intent.getBooleanExtra("IS_AVAILABLE", false)
        val avatarUrl = intent.getStringExtra("AVATAR_URL")

        setupToolbar(userName, isAvailable, avatarUrl)
        setupRecyclerView()
        setupInput()
        observeViewModel()

        viewModel.initialize(userId, userName, isAvailable)
    }

    private fun applyWindowInsets() {
        val toolbarBaseHeight = resources.getDimensionPixelSize(R.dimen.toolbar_height)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, insets ->
            val statusBar = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            val navBar = insets.getInsets(WindowInsetsCompat.Type.navigationBars())
            val ime = insets.getInsets(WindowInsetsCompat.Type.ime())

            // Push toolbar content below status bar
            binding.toolbar.layoutParams.height = toolbarBaseHeight + statusBar.top
            binding.toolbar.setPadding(0, statusBar.top, 0, 0)

            // Push bottom content above keyboard / nav bar
            view.setPadding(0, 0, 0, maxOf(ime.bottom, navBar.bottom))

            insets
        }
    }

    private fun setupToolbar(name: String, isAvailable: Boolean, avatarUrl: String?) {
        binding.tvName.text = name
        if (isAvailable) {
            binding.tvStatus.text = getString(R.string.label_online)
            binding.tvStatus.setTextColor(getColor(R.color.success))
        } else {
            binding.tvStatus.text = getString(R.string.label_offline)
            binding.tvStatus.setTextColor(getColor(R.color.disabled))
        }
        binding.toolbar.setNavigationOnClickListener { finish() }

        avatarUrl?.let { url ->
            binding.ivAvatar.load(url) {
                crossfade(true)
                placeholder(R.drawable.ic_person)
                error(R.drawable.ic_person)
                transformations(CircleCropTransformation())
            }
        }
    }

    private fun setupRecyclerView() {
        binding.rvMessages.apply {
            layoutManager = LinearLayoutManager(this@ChatActivity).apply {
                stackFromEnd = true
            }
        }
    }

    private fun setupInput() {
        binding.btnAttach.setOnClickListener {
            imagePicker.launch("image/*")
        }

        binding.btnSend.setOnClickListener {
            val content = binding.etMessage.text.toString().trim()
            if (content.isNotEmpty()) {
                viewModel.sendMessage(content)
                binding.etMessage.text.clear()
            }
        }
    }

    private fun onImagePicked(uri: Uri) {
        val inputStream = contentResolver.openInputStream(uri) ?: return
        val bytes = inputStream.use { it.readBytes() }
        val fileName = getFileName(uri) ?: "image_${System.currentTimeMillis()}.jpg"
        val caption = binding.etMessage.text.toString().trim()
        binding.etMessage.text.clear()

        viewModel.sendImage(caption, bytes, fileName)
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
        viewModel.uiState.observe(this) { state ->
            val currentId = viewModel.getCurrentUserId()

            if (currentId.isNotEmpty() && (adapter == null || adapter?.currentUserId != currentId)) {
                adapter = ChatAdapter(currentId)
                binding.rvMessages.adapter = adapter
            }

            val msgCount = adapter?.itemCount ?: 0
            if (state.messages.size != msgCount) {
                adapter?.setMessages(state.messages)
                if (state.messages.isNotEmpty()) {
                    binding.rvMessages.scrollToPosition(state.messages.size - 1)
                }
            }

            showChatClosed(!state.isAvailable)

            state.errorMessage?.let { message ->
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showChatClosed(isClosed: Boolean) {
        binding.tvChatClosed.visibility = if (isClosed) View.VISIBLE else View.GONE
        binding.layoutInput.visibility = if (isClosed) View.GONE else View.VISIBLE
    }
}

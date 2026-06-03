package com.example.studteach.feature.chat.presentation.imageviewer

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.load
import com.example.studteach.databinding.ActivityImageViewerBinding

class ImageViewerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityImageViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityImageViewerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val imageUrl = intent.getStringExtra("IMAGE_URL") ?: return finish()

        binding.ivFullImage.load(imageUrl) {
            crossfade(true)
        }

        binding.btnClose.setOnClickListener { finish() }
    }
}

package com.example.studteach.feature.chat.presentation.studenthome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.studteach.databinding.ItemTeacherBinding
import com.example.studteach.feature.chat.data.TeacherWithAvailability

class TeacherAdapter(
    private val onChatClick: (TeacherWithAvailability) -> Unit
) : RecyclerView.Adapter<TeacherAdapter.ViewHolder>() {

    private var teachers: List<TeacherWithAvailability> = emptyList()

    fun submitList(list: List<TeacherWithAvailability>) {
        teachers = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTeacherBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(teachers[position])
    }

    override fun getItemCount(): Int = teachers.size

    inner class ViewHolder(
        private val binding: ItemTeacherBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(teacher: TeacherWithAvailability) {
            binding.tvTeacherName.text = teacher.fullName
            binding.tvCourseName.text = teacher.course ?: "No course assigned"
            binding.tvAvailability.text = if (teacher.isAvailable) "Available" else "Unavailable"

            binding.viewStatus.setBackgroundResource(
                if (teacher.isAvailable) {
                    com.example.studteach.R.drawable.bg_status_available
                } else {
                    com.example.studteach.R.drawable.bg_status_unavailable
                }
            )

            binding.tvAvailability.setTextColor(
                binding.root.context.getColor(
                    if (teacher.isAvailable) {
                        com.example.studteach.R.color.success
                    } else {
                        com.example.studteach.R.color.disabled
                    }
                )
            )

            binding.ivChatIcon.setOnClickListener {
                onChatClick(teacher)
            }
        }
    }
}

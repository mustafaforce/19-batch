package com.example.studteach.feature.chat.presentation.studenthome

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import coil.transform.CircleCropTransformation
import com.example.studteach.R
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

            val hasSchedule = teacher.timeFrom.isNotBlank() && teacher.timeTo.isNotBlank()
            binding.tvAvailability.text = if (hasSchedule) {
                "${teacher.timeFrom} – ${teacher.timeTo}"
            } else {
                "No schedule set"
            }

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

            teacher.avatarUrl?.let { url ->
                binding.ivAvatar.load(url) {
                    crossfade(true)
                    placeholder(R.drawable.ic_person)
                    error(R.drawable.ic_person)
                    transformations(CircleCropTransformation())
                }
            } ?: run {
                binding.ivAvatar.setImageResource(R.drawable.ic_person)
            }

            binding.root.setOnClickListener {
                onChatClick(teacher)
            }
        }
    }
}

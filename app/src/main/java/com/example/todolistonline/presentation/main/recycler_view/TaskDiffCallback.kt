package com.example.todolistonline.presentation.main.recycler_view

import androidx.recyclerview.widget.DiffUtil
import com.example.todolistonline.domain.Task

class TaskDiffCallback :DiffUtil.ItemCallback<Task>() {
    override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
        return oldItem == newItem
    }
}
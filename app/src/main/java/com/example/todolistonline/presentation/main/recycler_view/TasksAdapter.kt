package com.example.todolistonline.presentation.main.recycler_view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.todolistonline.R
import com.example.todolistonline.domain.Task
import com.example.todolistonline.domain.TaskState

class TasksAdapter(private val onItemClickListener: OnItemClickListener) :
    ListAdapter<Task, TasksAdapter.TaskViewHolder>(TaskDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.note_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = getItem(position)
        holder.title.text = task.title

        // Установка состояния CheckBox без вызова слушателя
        holder.checkBox.setOnCheckedChangeListener(null)
        holder.checkBox.isChecked = task.state == TaskState.Done

        applyTaskStyles(holder, task)

        holder.itemView.setOnClickListener {
            onItemClickListener.onItemClick(task)
        }

        // Установка слушателя CheckBox
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                task.state = TaskState.Done
            } else {
                if (task.time >= 0) {
                    task.state = TaskState.None
                } else {
                    task.state = TaskState.OverDue
                }
            }
            onItemClickListener.onItemStateChanged(task)
            notifyItemChanged(holder.adapterPosition) // Уведомление об изменении элемента
        }
    }

    private fun applyTaskStyles(holder: TaskViewHolder, task: Task) {
        var colorLine: Int
        var colorView: Int = ContextCompat.getColor(holder.itemView.context, R.color.white)
        when (task.priority) {
            0 -> {
                colorLine =
                    ContextCompat.getColor(holder.itemView.context, R.color.lowPriorityColor)
                if (task.state == TaskState.Done) {
                    colorView = ContextCompat.getColor(holder.itemView.context, R.color.lowDone)
                } else if (task.state == TaskState.OverDue) {
                    colorView = ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.defaultPriorityColor
                    )
                }
            }
            1 -> {
                colorLine =
                    ContextCompat.getColor(holder.itemView.context, R.color.mediumPriorityColor)
                if (task.state == TaskState.Done) {
                    colorView = ContextCompat.getColor(holder.itemView.context, R.color.mediumDone)
                } else if (task.state == TaskState.OverDue) {
                    colorView = ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.defaultPriorityColor
                    )
                }
            }
            2 -> {
                colorLine =
                    ContextCompat.getColor(holder.itemView.context, R.color.highPriorityColor)
                if (task.state == TaskState.Done) {
                    colorView = ContextCompat.getColor(holder.itemView.context, R.color.highDone)
                } else if (task.state == TaskState.OverDue) {
                    colorView = ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.defaultPriorityColor
                    )
                }
            }
            else -> {
                colorLine =
                    ContextCompat.getColor(holder.itemView.context, R.color.defaultPriorityColor)
            }
        }
        holder.itemView.setBackgroundColor(colorView)
        holder.startLine.setBackgroundColor(colorLine)
    }

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.title_item)
        val checkBox: CheckBox = itemView.findViewById(R.id.checkbox_item)
        val startLine: View = itemView.findViewById(R.id.startline_item)
    }
}

interface OnItemClickListener {
    fun onItemClick(task: Task)
    fun onItemStateChanged(task: Task)
}


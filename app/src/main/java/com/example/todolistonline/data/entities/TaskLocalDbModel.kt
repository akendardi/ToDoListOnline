package com.example.todolistonline.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.todolistonline.domain.TaskState

@Entity(tableName = "tasks")
data class TaskLocalDbModel(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val title: String,
    val priority: Int,
    val time: Long,
    val state: TaskState
)
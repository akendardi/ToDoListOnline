package com.example.todolistonline.data.entities

import com.example.todolistonline.domain.TaskState

data class TaskRemoteDbModel(
    val id: Long,
    val title: String,
    val priority: Int,
    val time: Long,
    val state: TaskState
)
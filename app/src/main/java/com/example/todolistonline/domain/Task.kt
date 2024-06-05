package com.example.todolistonline.domain

data class Task(
    val id: Long,
    val title: String,
    val priority: Int,
    val time: Long,
    val state: TaskState
)

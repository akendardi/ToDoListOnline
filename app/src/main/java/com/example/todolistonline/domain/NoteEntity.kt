package com.example.todolistonline.domain

import javax.inject.Inject

data class NoteEntity @Inject constructor(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Int,
    val time: Long,
    val enabled: Boolean
)

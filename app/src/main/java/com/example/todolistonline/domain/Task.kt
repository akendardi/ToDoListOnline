package com.example.todolistonline.domain

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Task(
    val id: Long,
    val title: String,
    val priority: Int,
    var time: Int,
    var state: TaskState
): Parcelable

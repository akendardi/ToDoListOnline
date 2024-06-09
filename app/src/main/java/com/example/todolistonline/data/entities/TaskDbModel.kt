package com.example.todolistonline.data.entities

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import com.example.todolistonline.domain.TaskState

@Entity(tableName = "tasks")
data class TaskDbModel(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    val title: String,
    val priority: Int,
    val time: Int,
    val state: TaskState
){
    @Ignore
    constructor(): this(0, "", 0, 0, TaskState.None)
}
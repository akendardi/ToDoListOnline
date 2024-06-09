package com.example.todolistonline.domain

import com.example.todolistonline.data.entities.TaskDbModel

interface TaskRepository {

    suspend fun fetchTasksFromFirebase()
    suspend fun insertTask(task: TaskDbModel): Boolean
    suspend fun deleteTask(task: TaskDbModel)

    suspend fun getTodayTasks(): List<Task>

    suspend fun getTomorrowTasks(): List<Task>
    suspend fun getAllTasks(): List<Task>

    suspend fun synchronizeData()

    suspend fun deleteAll()

    suspend fun updateTask(task: TaskDbModel)
}
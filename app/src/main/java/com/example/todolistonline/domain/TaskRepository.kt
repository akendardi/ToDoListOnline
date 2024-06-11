package com.example.todolistonline.domain

import com.example.todolistonline.data.entities.TaskDbModel

interface TaskRepository {

    suspend fun transferTasks()

    suspend fun fetchTasksFromFirebase()
    suspend fun insertTaskLocalDb(task: TaskDbModel): Long
    suspend fun insertTaskFirebase(task: TaskDbModel, id: Long)
    suspend fun deleteTaskLocalDb(task: TaskDbModel)
    suspend fun deleteTaskFirebase(task: TaskDbModel)

    suspend fun getTodayTasks(): List<Task>

    suspend fun getTomorrowTasks(): List<Task>
    suspend fun getAllTasks(): List<Task>

    suspend fun synchronizeData()

    suspend fun deleteAll()

    suspend fun updateTaskLocalDb(task: TaskDbModel)
    suspend fun updateTaskFirebase(task: TaskDbModel)
}
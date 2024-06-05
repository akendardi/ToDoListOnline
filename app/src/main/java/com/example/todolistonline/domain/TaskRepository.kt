package com.example.todolistonline.domain

import com.example.todolistonline.data.entities.TaskLocalDbModel

interface TaskRepository {

    suspend fun fetchTasksFromFirebase()
    suspend fun updateFirebase(task: TaskLocalDbModel)
    suspend fun insertTask(task: TaskLocalDbModel)
    suspend fun updateTask(task: TaskLocalDbModel)
    suspend fun deleteTask(task: TaskLocalDbModel)
    suspend fun sortTasks(tasks: List<TaskLocalDbModel>)

}
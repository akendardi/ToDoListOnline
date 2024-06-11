package com.example.todolistonline.domain.use_cases.tasks_use_cases

import com.example.todolistonline.data.entities.TaskDbModel
import com.example.todolistonline.domain.TaskRepository
import javax.inject.Inject

class InsertTaskFirebaseUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke(task: TaskDbModel, id: Long) = repository.insertTaskFirebase(task, id)
}
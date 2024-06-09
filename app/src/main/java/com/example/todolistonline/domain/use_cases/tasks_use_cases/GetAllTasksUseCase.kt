package com.example.todolistonline.domain.use_cases.tasks_use_cases

import com.example.todolistonline.domain.TaskRepository
import javax.inject.Inject

class GetAllTasksUseCase @Inject constructor(
    private val repository: TaskRepository
) {
    suspend operator fun invoke() = repository.getAllTasks()
}
package com.example.todolistonline.domain.use_cases.tasks_use_cases

import com.example.todolistonline.domain.FirebaseRepository
import javax.inject.Inject

class CheckConnectionUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    operator fun invoke() = repository.checkConnection()
}
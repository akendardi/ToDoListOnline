package com.example.todolistonline.domain.use_cases.firebase_use_cases

import com.example.todolistonline.domain.FirebaseRepository
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val repository: FirebaseRepository
) {
    operator fun invoke(email: String) = repository.resetPassword(email)
}
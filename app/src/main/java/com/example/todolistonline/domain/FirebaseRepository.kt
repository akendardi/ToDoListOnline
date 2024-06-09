package com.example.todolistonline.domain

import com.example.todolistonline.domain.states.LoginState
import com.example.todolistonline.domain.states.RegistrationState
import com.example.todolistonline.domain.states.ResetPasswordState

interface FirebaseRepository {
    suspend fun createNewAccount(email: String, password: String, name: String): RegistrationState
    suspend fun loginInAccount(email: String, password: String): LoginState
    suspend fun resetPassword(email: String): ResetPasswordState
    fun logoutOfAccount()

    fun checkConnection(): Boolean
}
package com.example.todolistonline.domain

interface FirebaseRepository {
    suspend fun createNewAccount(email: String, password: String, name: String): RegistrationState
    fun loginInAccount(email: String, password: String)
    fun resetPassword(email: String)
    fun logoutOfAccount()
}
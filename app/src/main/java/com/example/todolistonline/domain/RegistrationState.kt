package com.example.todolistonline.domain

sealed class RegistrationState {
    data object Loading: RegistrationState()

    data object Successful: RegistrationState()

    data class Error(val error: Exception): RegistrationState()

    data object InvalidEmail : RegistrationState()
    data object UserCollision : RegistrationState()

}
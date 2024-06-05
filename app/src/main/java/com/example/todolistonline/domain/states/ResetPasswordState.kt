package com.example.todolistonline.domain.states

sealed class ResetPasswordState {

    data object Loading: ResetPasswordState()
    data object Successful: ResetPasswordState()

    data object InvalidUser: ResetPasswordState()

    data object InvalidEmail: ResetPasswordState()

    data class Error(val error: String): ResetPasswordState()
}
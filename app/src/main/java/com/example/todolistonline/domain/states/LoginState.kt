package com.example.todolistonline.domain.states

sealed class LoginState {

    data object Loading: LoginState()

    data object InvalidData: LoginState()


    data object Successful: LoginState()

    data class Error(val error: String): LoginState()
}
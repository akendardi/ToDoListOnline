package com.example.todolistonline.presentation.main

 sealed class MainState {
     data object GetTasksFromFirebaseError: MainState()

     data object Loading: MainState()

     data object Success: MainState()

     data object LogoutSuccess: MainState()

     data object LogoutError: MainState()
}
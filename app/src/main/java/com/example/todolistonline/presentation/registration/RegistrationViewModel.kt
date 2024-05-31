package com.example.todolistonline.presentation.registration

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistonline.domain.use_cases.firebase_use_cases.CreateAccountUseCase
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var createAccountUseCase: CreateAccountUseCase

    @Inject
    lateinit var auth: FirebaseAuth

    private val _registerResult = MutableLiveData<Boolean>()
    val registerResult: LiveData<Boolean>
        get() = _registerResult

    fun createAccount(email: String, password: String, name: String) {
        Log.d("Create VM", "Start")
        viewModelScope.launch {
            val result = createAccountUseCase.invoke(email, password, name)
            Log.d("Registration Tag", auth.currentUser?.displayName.toString())
            _registerResult.value = result
        }

    }
}
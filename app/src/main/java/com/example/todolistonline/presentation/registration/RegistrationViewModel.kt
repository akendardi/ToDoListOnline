package com.example.todolistonline.presentation.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistonline.domain.states.RegistrationState
import com.example.todolistonline.domain.use_cases.firebase_use_cases.CreateAccountUseCase
import com.example.todolistonline.mapper.Mapper
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class RegistrationViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var createAccountUseCase: CreateAccountUseCase

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var mapper: Mapper

    private val _registerResult = MutableLiveData<RegistrationState>()
    val registerResult: LiveData<RegistrationState>
        get() = _registerResult

    fun createAccount(email: String, password: String, name: String) {
        _registerResult.value = RegistrationState.Loading
        viewModelScope.launch {
            val result = createAccountUseCase.invoke(email, password, name)
            _registerResult.value = result
        }
    }

    fun dpToPx(dp: Int): Int{
        return mapper.dpToPx(dp)
    }

    companion object {
        private const val ERROR_REGISTRATION = "Ошибка регистрации. Повторите ошибку позже"
    }
}
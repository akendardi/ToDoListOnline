package com.example.todolistonline.presentation.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistonline.domain.states.LoginState
import com.example.todolistonline.domain.use_cases.firebase_use_cases.LoginInAccountUseCase
import com.example.todolistonline.mapper.MapperPresentation
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import javax.inject.Inject

class LoginViewModel @Inject constructor() : ViewModel() {

    private val _loginResult = MutableLiveData<LoginState>()
    val loginResult: LiveData<LoginState>
        get() = _loginResult

    @Inject
    lateinit var auth: FirebaseAuth

    @Inject
    lateinit var loginInAccountUseCase: LoginInAccountUseCase

    @Inject
    lateinit var mapperPresentation: MapperPresentation

    fun login(email: String, password: String){
        _loginResult.value = LoginState.Loading
        viewModelScope.launch {
            val res = loginInAccountUseCase.invoke(email, password)
            _loginResult.value = res
        }
    }

    fun dpToPx(dp: Int): Int{
        return mapperPresentation.dpToPx(dp)
    }

}
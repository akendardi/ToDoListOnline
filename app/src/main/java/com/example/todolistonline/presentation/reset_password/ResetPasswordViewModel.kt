package com.example.todolistonline.presentation.reset_password

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistonline.domain.states.ResetPasswordState
import com.example.todolistonline.domain.use_cases.firebase_use_cases.ResetPasswordUseCase
import com.example.todolistonline.mapper.Mapper
import kotlinx.coroutines.launch
import javax.inject.Inject

class ResetPasswordViewModel @Inject constructor() : ViewModel() {

    @Inject
    lateinit var resetPassword: ResetPasswordUseCase

    @Inject
    lateinit var mapper: Mapper

    private val _result = MutableLiveData<ResetPasswordState>()
    val result: LiveData<ResetPasswordState>
        get() = _result


    fun resetPassword(email: String) {
        _result.value = ResetPasswordState.Loading
        viewModelScope.launch {
            val res = resetPassword.invoke(email)
            _result.value = res
            Log.d("Говно", res.toString())

        }


    }

    fun dpToPx(dp: Int): Int {
        return mapper.dpToPx(dp)
    }
}
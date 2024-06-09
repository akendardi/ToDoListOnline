package com.example.todolistonline.presentation.login

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityLoginBinding
import com.example.todolistonline.domain.states.LoginState
import com.example.todolistonline.presentation.ViewModelFactory
import com.example.todolistonline.presentation.main.MainActivity
import com.example.todolistonline.presentation.registration.RegistrationActivity
import com.example.todolistonline.presentation.reset_password.ResetPasswordActivity
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory


    private val viewModel: LoginViewModel by viewModels { viewModelFactory }

    private val component by lazy {
        (application as ToDoListOnlineApp).component
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        tvClickListeners()
        buttonClickListener()
        observeViewModel()

    }



    private fun tvClickListeners(){
        binding.tvRegistration.setOnClickListener {
            val intent = RegistrationActivity.newIntent(this)
            startActivity(intent)
        }

        binding.tvResetPassword.setOnClickListener {
            val intent = ResetPasswordActivity.newIntent(binding.etEmail.text.toString(), this)
            startActivity(intent)
        }
    }

    private fun observeViewModel(){
        viewModel.loginResult.observe(this){
            when (it) {
                is LoginState.Error -> {
                    loading(false)
                    Toast.makeText(
                        this,
                        it.error,
                        Toast.LENGTH_SHORT
                    ).show()
                }
                LoginState.InvalidData -> {
                    loading(false)
                    Toast.makeText(
                        this,
                        "Введены некорректные данные. Повторите ввод",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                LoginState.Loading -> {
                    loading(true)
                }
                LoginState.Successful -> {
                    loading(false)
                    val intent = MainActivity.newIntent(this, true)
                    startActivity(intent)

                }

            }
        }
    }

    private fun loading(bool: Boolean) {
        if (bool) {
            binding.progressBar.visibility = View.VISIBLE
            binding.blackBackground.visibility = View.VISIBLE
        } else {
            binding.progressBar.visibility = View.GONE
            binding.blackBackground.visibility = View.GONE
        }
    }

    private fun buttonClickListener(){
        binding.buttonLogin.setOnClickListener {
            if (checkEditText()){
                val email = binding.etEmail.text.toString().trim()
                val password = binding.etPassword.text.toString().trim()
                viewModel.login(email, password)
            }
        }
    }

    private fun checkEditText(): Boolean {
        var flag = true
         if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmailL.layoutParams.height = viewModel.dpToPx(80)
            binding.etEmailL.error = "email не должен быть пустым"
            flag = false
        } else {
            binding.etEmailL.layoutParams.height = viewModel.dpToPx(70)
            binding.etEmailL.error = null
        }
        if (binding.etPassword.text.toString().isEmpty()) {
            binding.etPasswordL.layoutParams.height = viewModel.dpToPx(80)
            binding.etPasswordL.error = "Пароль не должен быть пустым"
            flag = false
        } else {
            binding.etPasswordL.layoutParams.height = viewModel.dpToPx(70)
            binding.etPasswordL.error = null
        }
        return flag
    }

    companion object{
        fun newIntent(context: Context): Intent {
            return Intent(context, LoginActivity::class.java)
        }
    }



}


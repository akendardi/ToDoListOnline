package com.example.todolistonline.presentation.registration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityRegistrationBinding
import com.example.todolistonline.presentation.ViewModelFactory
import javax.inject.Inject

class RegistrationActivity : AppCompatActivity() {
    private val binding by lazy {
        ActivityRegistrationBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as ToDoListOnlineApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: RegistrationViewModel by viewModels { viewModelFactory }


    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        binding.buttonRegistration.setOnClickListener {
            Log.d("Говноо", "buttonRegistration")
            val name = binding.etLogin.text.toString()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            Log.d("Registration Tag", "$name $email $password")
            viewModel.createAccount(email, password, name)
        }

        viewModel.registerResult.observe(this){
            Log.d("Говноо", it.toString())
        }
    }


    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RegistrationActivity::class.java);
        }
    }


}
package com.example.todolistonline.presentation.registration

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityRegistrationBinding
import com.example.todolistonline.domain.RegistrationState
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
        observeViewModel()
        binding.buttonRegistration.setOnClickListener {
            checkEditText()
            Log.d("Говноо", "buttonRegistration")
            val name = binding.etLogin.text.toString()
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString()
            Log.d("Registration Tag", "$name $email $password")
            viewModel.createAccount(email, password, name)
        }

    }

    private fun observeViewModel() {
        viewModel.registerResult.observe(this) {
            Log.d("Resuult", it.toString())
            when (it) {
                is RegistrationState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

                is RegistrationState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, it.error.toString(), Toast.LENGTH_SHORT).show()

                }

                is RegistrationState.Successful -> {
                    binding.progressBar.visibility = View.GONE
                }

                is RegistrationState.InvalidEmail -> {
                    Toast.makeText(this, "Хуйня емаил", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }

                is RegistrationState.UserCollision -> {
                    Toast.makeText(this, "Уже есть", Toast.LENGTH_SHORT).show()
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    private fun checkEditText(): Boolean {
        var flag = true
        if (binding.etLogin.text.toString().isEmpty()) {
            binding.etLoginL.layoutParams.height = viewModel.dpToPx(80)
            binding.etLoginL.error = "Логин не должен быть пустым"
            flag = false
        } else {
            binding.etLoginL.layoutParams.height = viewModel.dpToPx(70)
            binding.etLoginL.error = null
        }
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

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, RegistrationActivity::class.java);
        }
    }


}
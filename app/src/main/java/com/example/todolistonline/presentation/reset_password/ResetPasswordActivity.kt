package com.example.todolistonline.presentation.reset_password

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityResetPasswordBinding
import com.example.todolistonline.domain.states.ResetPasswordState
import com.example.todolistonline.presentation.ViewModelFactory
import javax.inject.Inject

class ResetPasswordActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityResetPasswordBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as ToDoListOnlineApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: ResetPasswordViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        buttonListener()
        observeViewModel()

    }

    private fun checkEditText(): Boolean {
        return if (binding.etEmail.text.toString().isEmpty()) {
            binding.etEmailL.layoutParams.height = viewModel.dpToPx(80)
            binding.etEmailL.error = "email не должен быть пустым"
            false
        } else {
            binding.etEmailL.layoutParams.height = viewModel.dpToPx(70)
            binding.etEmailL.error = null
            true
        }
    }


    private fun buttonListener() {
        binding.buttonResetPassword.setOnClickListener {
            if (checkEditText()) {
                viewModel.resetPassword(email = binding.etEmail.text.toString().trim())
            }
        }
    }

    private fun observeViewModel() {
        viewModel.result.observe(this) {
            when (it) {
                is ResetPasswordState.Loading -> {
                    loading(true)
                }

                is ResetPasswordState.InvalidEmail -> {
                    loading(false)
                    Toast.makeText(
                        this,
                        "Введен некорректный или несуществующий email. Повторите ввод",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                is ResetPasswordState.Error -> {
                    loading(false)
                    Toast.makeText(this, it.error, Toast.LENGTH_SHORT).show()
                }
                ResetPasswordState.InvalidUser -> {
                    loading(false)
                    Toast.makeText(
                        this,
                        "Такого пользователя не существует",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                ResetPasswordState.Successful -> {
                    Toast.makeText(
                        this,
                        "Письмо отправлено на вашу почту",
                        Toast.LENGTH_SHORT
                    ).show()
                    loading(false)
                    finish()

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

    companion object {
        fun newIntent(email: String, context: Context): Intent {
            val intent = Intent(context, ResetPasswordActivity::class.java)
            intent.putExtra(EMAIL_TAG, email)
            return intent
        }

        const val EMAIL_TAG = "email"
    }
}
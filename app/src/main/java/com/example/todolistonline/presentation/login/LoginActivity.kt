package com.example.todolistonline.presentation.login

import android.animation.ValueAnimator
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.updateLayoutParams
import com.example.todolistonline.databinding.ActivityLoginBinding
import com.example.todolistonline.presentation.registration.RegistrationActivity
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent

class LoginActivity : AppCompatActivity() {

    private val binding by lazy {
        ActivityLoginBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        keyboardListener()
        tvClickListeners()

    }

    private fun startAnimationCardView(old: Int, new: Int) {
        val params = binding.cardView.layoutParams as ViewGroup.MarginLayoutParams
        params.topMargin = new
        binding.cardView.layoutParams = params
        val animator = ValueAnimator.ofInt(old, new).apply {
            addUpdateListener { valueAnimator ->
                val animatedValue = valueAnimator.animatedValue as Int
                binding.cardView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    topMargin = animatedValue
                }
            }
        }
        animator.start()
    }

    private fun keyboardListener() {
        KeyboardVisibilityEvent.setEventListener(this) {
            if (it) {
                startAnimationCardView(-300, -700)
            } else {
                startAnimationCardView(-700, -300)
            }
        }
    }

    private fun tvClickListeners(){
        binding.tvRegistration.setOnClickListener {
            val intent = RegistrationActivity.newIntent(this)
            startActivity(intent)
        }
    }

}


package com.example.todolistonline.presentation.hello_activity

import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.todolistonline.R
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityHelloBinding
import com.example.todolistonline.presentation.login.LoginActivity
import com.example.todolistonline.presentation.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

class HelloActivity : AppCompatActivity() {

    private val component by lazy {
        (application as ToDoListOnlineApp).component
    }

    private val binding by lazy {
        ActivityHelloBinding.inflate(layoutInflater)
    }

    @Inject
    lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate_iv)
        binding.iv.startAnimation(rotateAnimation)

        val currUser = auth.currentUser
        Log.d("current_user", currUser.toString())
        updateUI(currUser)

    }

    private fun updateUI(user: FirebaseUser?) {
        if (user != null) {
            // User is signed in, redirect to the main screen of the app
            Log.d("Login", "Залогинился")
            lifecycleScope.launch {
                delay(2000)
                startActivity(MainActivity.newIntent(this@HelloActivity, false))
                finish()
            }


        } else {
            Log.d("Login", "Не Залогинился")
            lifecycleScope.launch {
                delay(2000)
                startActivity(LoginActivity.newIntent(this@HelloActivity))
                finish()

            }

        }
    }
}
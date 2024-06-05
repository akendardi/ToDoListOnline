package com.example.todolistonline.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistonline.R
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityMainBinding
import com.example.todolistonline.presentation.ViewModelFactory
import com.example.todolistonline.presentation.main.fragments.TaskFragment
import com.example.todolistonline.presentation.registration.RegistrationViewModel
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class MainActivity :
    AppCompatActivity() {

    private val viewPagerAdapter = ViewPagerAdapter(this)


    private val binding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val component by lazy {
        (application as ToDoListOnlineApp).component
    }

    @Inject
    lateinit var viewModelFactory: ViewModelFactory

    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        startViewPager()
        listeners()

    }

    private fun startViewPager() {
        binding.tabLayout.isTabIndicatorFullWidth = true
        val flagList = listOf("Сегодня", "Завтра")
        binding.viewPager2.adapter = viewPagerAdapter
        TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, pos ->
            tab.text = flagList[pos]
        }.attach()
    }


    private fun listeners(){
        binding.fabAddTask.setOnClickListener {
            TaskFragment().show(supportFragmentManager, "TaskDialogFragment")
        }
    }

    companion object {
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }


}



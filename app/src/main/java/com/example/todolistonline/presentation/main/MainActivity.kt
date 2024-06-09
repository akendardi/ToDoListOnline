package com.example.todolistonline.presentation.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityMainBinding
import com.example.todolistonline.presentation.ViewModelFactory
import com.example.todolistonline.presentation.login.LoginActivity
import com.example.todolistonline.presentation.main.fragments.task_fragment.TaskFragment
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
import javax.inject.Inject

class MainActivity :
    AppCompatActivity(), OnTabSelectedListener {

    private val viewPagerAdapter = ViewPagerAdapter(this)

    private var selectedTab = 0


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
        val isFirst = intent.getBooleanExtra(FLAG_TAG, false)
        Log.d("MYTAG", isFirst.toString())
        if (isFirst){
            viewModel.getTasksFromFirebase()
        } else {
            viewModel.synchronizeData()
        }
        startViewPager()
        listeners()
        observeViewModel()
    }

    private fun startViewPager() {
        binding.tabLayout.isTabIndicatorFullWidth = true
        val flagList = listOf("Сегодня", "Завтра")
        binding.viewPager2.adapter = viewPagerAdapter

        val tabLayoutMediator =
            TabLayoutMediator(binding.tabLayout, binding.viewPager2) { tab, pos ->
                tab.text = flagList[pos]
            }

        tabLayoutMediator.attach()

        binding.tabLayout.addOnTabSelectedListener(object : OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    selectedTab = it.position
                    Log.d("MYTAG", selectedTab.toString())
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }
        })
    }


    private fun listeners() {
        binding.fabAddTask.setOnClickListener {
            val fragment = TaskFragment.newInstance(null, selectedTab)
            fragment.show(supportFragmentManager, "TaskDialogFragment")
        }
        binding.logout.setOnClickListener {
            viewModel.logout()
        }
    }

    private fun observeViewModel(){
        viewModel.isLogoutSuccess.observe(this){
            if (it){
                Toast.makeText(this, "Вышел из аккаунта", Toast.LENGTH_SHORT).show()
                startActivity(LoginActivity.newIntent(this))
                finish()
            } else {
                Toast.makeText(this, "Не вышел из аккаунта", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onTabSelected(p0: TabLayout.Tab?) {
        TODO("Not yet implemented")
    }

    override fun onTabUnselected(p0: TabLayout.Tab?) {
        TODO("Not yet implemented")
    }

    override fun onTabReselected(p0: TabLayout.Tab?) {
        TODO("Not yet implemented")
    }



    companion object {
        fun newIntent(context: Context, flag: Boolean): Intent {
            return Intent(context, MainActivity::class.java).apply {
                putExtra(FLAG_TAG, flag)
            }
        }

        const val FLAG_TAG = "isFirst"

    }
}



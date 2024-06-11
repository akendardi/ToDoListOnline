package com.example.todolistonline.presentation.main

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.databinding.ActivityMainBinding
import com.example.todolistonline.presentation.ViewModelFactory
import com.example.todolistonline.presentation.login.LoginActivity
import com.example.todolistonline.presentation.main.fragments.TaskFragment
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


    private val REQUEST_CODE_NOTIFICATION_PERMISSION = 1
    private val viewModel: MainViewModel by viewModels { viewModelFactory }

    override fun onCreate(savedInstanceState: Bundle?) {
        component.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        isFirstStart()
        startViewPager()
        listeners()
        observeViewModel()
        checkNotificationPermissions()
    }


    private fun isFirstStart() {
        val isFirst = intent.getBooleanExtra(FLAG_TAG, false)
        Log.d("MYTAG", isFirst.toString())
        if (isFirst) {
            viewModel.getTasksFromFirebase()
        } else {
            viewModel.synchronizeData()
        }
    }

    private fun checkNotificationPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    REQUEST_CODE_NOTIFICATION_PERMISSION
                )
            }
        }
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
        binding.update.setOnClickListener {
            viewModel.getTomorrowTasks()
            viewModel.getTodayTasks()
        }
    }

    private fun observeViewModel() {
        viewModel.state.observe(this) {
            when (it) {
                is MainState.GetTasksFromFirebaseError -> {
                    Toast.makeText(this, "Ошибка подключения. Невозможно загрузить данные с удаленного сервера", Toast.LENGTH_SHORT).show()
                    offLoading()
                }
                is MainState.Loading -> {
                    onLoading()
                }
                is MainState.LogoutError -> {
                    Toast.makeText(this, "Ошибка подключения. Проверьте подключение к интернету и повторите позже", Toast.LENGTH_SHORT).show()
                    offLoading()
                }
                is MainState.LogoutSuccess -> {
                    startLoginActivity()
                }
                is MainState.Success -> {
                    offLoading()
                }
            }
        }
    }

    private fun offLoading(){
        binding.progressBar.visibility = View.GONE
        binding.tabLayout.isActivated = false
        binding.fabAddTask.isActivated = false
        binding.logout.isActivated = false
        binding.update.isActivated = false
    }

    private fun onLoading(){
        binding.progressBar.visibility = View.VISIBLE
        binding.tabLayout.isActivated = true
        binding.fabAddTask.isActivated = true
        binding.logout.isActivated = true
        binding.update.isActivated = true
    }

    private fun startLoginActivity(){
        startActivity(LoginActivity.newIntent(this))
        finish()
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



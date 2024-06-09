package com.example.todolistonline.presentation.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.todolistonline.domain.Task
import com.example.todolistonline.domain.TaskRepository
import com.example.todolistonline.domain.TaskState
import com.example.todolistonline.domain.use_cases.firebase_use_cases.LogoutFromAccountUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.CheckConnectionUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.DeleteAllUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.DeleteTaskUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.FetchTasksFromFirebaseUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.GetTodayTasksUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.GetTomorrowTasksUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.SynchronizeDataUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.UpdateTaskUseCase
import com.example.todolistonline.mapper.Mapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val getTomorrowTasksUseCase: GetTomorrowTasksUseCase,
    private val fetchTasksFromFirebaseUseCase: FetchTasksFromFirebaseUseCase,
    private val synchronizeDataUseCase: SynchronizeDataUseCase,
    private val deleteAllUseCase: DeleteAllUseCase,
    private val deleteTaskUseCase: DeleteTaskUseCase,
    private val logoutFromAccountUseCase: LogoutFromAccountUseCase,
    private val checkConnectionUseCase: CheckConnectionUseCase,
    private val updateTaskUseCase: UpdateTaskUseCase
) : ViewModel() {

    private val _todayList = MutableLiveData<List<Task>>()
    val todayList: LiveData<List<Task>>
        get() = _todayList

    private val _tomorrowList = MutableLiveData<List<Task>>()
    val tomorrowList: LiveData<List<Task>>
        get() = _tomorrowList

    @Inject
    lateinit var repository: TaskRepository

    @Inject
    lateinit var mapper: Mapper


    private val _isLogoutSuccess = MutableLiveData<Boolean>()
    val isLogoutSuccess: LiveData<Boolean>
        get() = _isLogoutSuccess

    fun synchronizeData() {
        viewModelScope.launch {
            synchronizeDataUseCase.invoke()
        }

    }

    fun updateTask(task: Task) {
        val taskDb = mapper.entityToDbModel(task)
        viewModelScope.launch {
            updateTaskUseCase.invoke(taskDb)
            Log.d("MYTAG", "updateTask $task")

            val oldList = if (task.time == 0) {
                _todayList.value?.toMutableList()
            } else {
                _tomorrowList.value?.toMutableList()
            }

            oldList?.let {
                val oldPosition = it.indexOfFirst { it.id == task.id }

                if (oldPosition != -1) {
                    it[oldPosition] = task

                    // Пересортировать список
                    val sortedList = it.sortedWith(
                        compareBy<Task> {
                            when (it.state) {
                                TaskState.OverDue -> 0
                                TaskState.None -> 1
                                TaskState.Done -> 2
                            }
                        }.thenByDescending { it.priority }
                    )

                    if (task.time == 0) {
                        _todayList.value = sortedList
                    } else {
                        _tomorrowList.value = sortedList
                    }
                }
            }
        }
    }

    fun logout() {
        if (checkConnectionUseCase.invoke()) {
            viewModelScope.launch {
                // Синхронизация данных
                val syncJob = launch {
                    synchronizeDataUseCase.invoke()
                }
                syncJob.join() // Ждем завершения синхронизации данных

                // Удаление данных
                val deleteJob = launch {
                    deleteAllUseCase.invoke()
                }
                deleteJob.join() // Ждем завершения удаления данных

                // Выход из аккаунта
                logoutFromAccountUseCase.invoke()

                _isLogoutSuccess.value = true
            }
        } else {
            _isLogoutSuccess.value = false
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskUseCase.invoke(mapper.entityToDbModel(task))
            if (task.time == 0) {
                getTodayTasks()
            } else {
                getTomorrowTasks()
            }
        }
    }


    fun getTodayTasks() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                getTodayTasksUseCase.invoke()
            }
            Log.d("MYTAG", "Обновление сегодня")

            _todayList.value = list.sortedWith(
                compareBy<Task> {
                    when (it.state) {
                        TaskState.OverDue -> 0
                        TaskState.None -> 1
                        TaskState.Done -> 2
                    }
                }.thenByDescending { it.priority }
            )
        }
    }

    fun getTasksFromFirebase() {
        viewModelScope.launch {
            fetchTasksFromFirebaseUseCase.invoke()
            getTodayTasks()
            getTomorrowTasks()
        }
    }

    fun getTomorrowTasks() {
        viewModelScope.launch {
            val list = withContext(Dispatchers.IO) {
                getTomorrowTasksUseCase.invoke()
            }
            Log.d("MYTAG", "Обновление завтра")
            _tomorrowList.value = list.sortedWith(
                compareByDescending<Task> { it.priority }
                    .thenBy { it.state == TaskState.Done }
            )
        }
    }

    fun addNewTask(task: Task) {
        viewModelScope.launch {
            val addTaskJob = launch {
                try {
                    withContext(Dispatchers.IO) {
                        val taskDb = mapper.entityToDbModel(task)
                        repository.insertTask(taskDb)
                    }
                    Log.d("MYTAG", "Добавление закончилось")
                } catch (e: Exception) {
                    Log.e("MYTAG", "Ошибка при добавлении задачи: ${e.message}")
                }
            }
            addTaskJob.join()
            if (task.time == 0) {
                getTodayTasks()
            }
            if (task.time == 1) {
                getTomorrowTasks()
            }
        }
    }

    fun dpToPx(dp: Int): Int {
        return mapper.dpToPx(dp)
    }
}
package com.example.todolistonline.presentation.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.todolistonline.domain.Task
import com.example.todolistonline.domain.TaskRepository
import com.example.todolistonline.domain.TaskState
import com.example.todolistonline.domain.use_cases.firebase_use_cases.LogoutFromAccountUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.CheckConnectionUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.DeleteAllUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.DeleteTaskFirebaseUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.DeleteTaskLocalDbUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.FetchTasksFromFirebaseUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.GetTodayTasksUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.GetTomorrowTasksUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.InsertTaskFirebaseUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.InsertTaskLocalDbUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.SynchronizeDataUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.TransferTasksUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.UpdateTaskFirebaseUseCase
import com.example.todolistonline.domain.use_cases.tasks_use_cases.UpdateTaskLocalDbUseCase
import com.example.todolistonline.mapper.Mapper
import com.example.todolistonline.workers.NotificationWorker
import com.example.todolistonline.workers.TaskTransferWorker
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MainViewModel @Inject constructor(
    private val getTodayTasksUseCase: GetTodayTasksUseCase,
    private val getTomorrowTasksUseCase: GetTomorrowTasksUseCase,
    private val fetchTasksFromFirebaseUseCase: FetchTasksFromFirebaseUseCase,
    private val synchronizeDataUseCase: SynchronizeDataUseCase,
    private val deleteAllUseCase: DeleteAllUseCase,
    private val deleteTaskLocalDbUseCase: DeleteTaskLocalDbUseCase,
    private val deleteTaskFirebaseUseCase: DeleteTaskFirebaseUseCase,
    private val logoutFromAccountUseCase: LogoutFromAccountUseCase,
    private val checkConnectionUseCase: CheckConnectionUseCase,
    private val updateTaskLocalDbUseCase: UpdateTaskLocalDbUseCase,
    private val updateTaskFirebaseUseCase: UpdateTaskFirebaseUseCase,
    private val insertTaskFirebaseUseCase: InsertTaskFirebaseUseCase,
    private val insertTaskLocalDbUseCase: InsertTaskLocalDbUseCase,
    private val transferTasksUseCase: TransferTasksUseCase,
    private val applicationContext: Context
) : ViewModel() {

    private val _todayList = MutableLiveData<List<Task>>()
    val todayList: LiveData<List<Task>>
        get() = _todayList

    private val _tomorrowList = MutableLiveData<List<Task>>()
    val tomorrowList: LiveData<List<Task>>
        get() = _tomorrowList


    private val _state = MutableLiveData<MainState>()
    val state: LiveData<MainState>
        get() = _state

    @Inject
    lateinit var repository: TaskRepository

    @Inject
    lateinit var mapper: Mapper

    fun synchronizeData() {
        viewModelScope.launch {
            synchronizeDataUseCase.invoke()
        }
    }

    init {
        startWorkers(22, 0, 0, 1)
    }



    private suspend fun sortTasksList(task: Task) {
        val oldList = if (task.time <= 0) {
            getTodayTasksUseCase.invoke().toMutableList()
        } else {
            getTomorrowTasksUseCase.invoke().toMutableList()
        }

        oldList.let { it ->
            val oldPosition = it.indexOfFirst { it.id == task.id }

            if (oldPosition != -1) {
                it[oldPosition] = task

                val sortedList = it.sortedWith(
                    compareBy<Task> {
                        when {
                            it.time < 0 && it.state == TaskState.None -> 0
                            it.time >= 0 && it.state == TaskState.None -> 1
                            it.state == TaskState.Done -> 2
                            else -> 3
                        }
                    }.thenByDescending { it.priority }
                )

                if (task.time <= 0) {
                    _todayList.value = sortedList
                } else {
                    _tomorrowList.value = sortedList
                }
            }
        }
    }

    private suspend fun sortAndGetTasksList(time: Int) {
        val oldList = if (time <= 0) {
            getTodayTasksUseCase.invoke().toMutableList()
        } else {
            getTomorrowTasksUseCase.invoke().toMutableList()
        }
        Log.d("Список", oldList.toString())

        val sortedList = oldList.sortedWith(
            compareBy<Task> {
                when {
                    it.time < 0 && it.state == TaskState.None -> 0
                    it.time >= 0 && it.state == TaskState.None -> 1
                    it.state == TaskState.Done -> 2
                    else -> 3
                }
            }.thenByDescending { it.priority }
        )

        Log.d("Список", sortedList.toString())

        if (time <= 0) {
            _todayList.value = sortedList
        } else {
            _tomorrowList.value = sortedList
        }
    }


    fun updateTask(task: Task) {
        val taskDb = mapper.entityToDbModel(task)
        viewModelScope.launch {
            updateTaskLocalDbUseCase.invoke(taskDb)
            sortTasksList(task)
            updateTaskFirebaseUseCase.invoke(taskDb)
        }
    }


    fun logout() {
        if (checkConnectionUseCase.invoke()) {
            viewModelScope.launch {
                _state.value = MainState.Loading
                synchronizeDataUseCase.invoke()

                deleteAllUseCase.invoke()

                logoutFromAccountUseCase.invoke()

                _state.value = MainState.LogoutSuccess
            }
        } else {
            _state.value = MainState.LogoutError
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            deleteTaskLocalDbUseCase.invoke(mapper.entityToDbModel(task))
            deleteTaskFirebaseUseCase.invoke(mapper.entityToDbModel(task))
            if (task.time <= 0) {
                getTodayTasks()
            } else {
                getTomorrowTasks()
            }
        }
    }


    fun getTodayTasks() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            sortAndGetTasksList(0)
            _state.value = MainState.Success
        }
    }

    fun getTasksFromFirebase() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            fetchTasksFromFirebaseUseCase.invoke()
            getTodayTasks()
            getTomorrowTasks()
            _state.value = MainState.Success
        }
    }

    fun getTomorrowTasks() {
        viewModelScope.launch {
            _state.value = MainState.Loading
            sortAndGetTasksList(1)
            _state.value = MainState.Success
        }
    }

    fun addNewTask(task: Task) {
        viewModelScope.launch {
            Log.d("MYTAG", task.toString())
            val id = insertTaskLocalDbUseCase.invoke(mapper.entityToDbModel(task))
            if (task.time == 0) {
                getTodayTasks()
            }
            if (task.time == 1) {
                getTomorrowTasks()
            }
            insertTaskFirebaseUseCase.invoke(mapper.entityToDbModel(task), id)
        }
    }

    private fun startWorkers(notificationHour: Int, notificationMinute: Int, taskTransferHour: Int, taskTransferMinute: Int) {
        val notificationWorkRequestTag = "notificationWork"
        val taskTransferWorkRequestTag = "taskTransferWork"

        val repeatIntervalHours = 24 // Периодичность выполнения в часах

        val currentTimeMillis = System.currentTimeMillis()

        val notificationCalendar = Calendar.getInstance()
        notificationCalendar.timeInMillis = currentTimeMillis
        notificationCalendar.set(Calendar.HOUR_OF_DAY, notificationHour)
        notificationCalendar.set(Calendar.MINUTE, notificationMinute)
        notificationCalendar.set(Calendar.SECOND, 0)

        val taskTransferCalendar = Calendar.getInstance()
        taskTransferCalendar.timeInMillis = currentTimeMillis
        taskTransferCalendar.set(Calendar.HOUR_OF_DAY, taskTransferHour)
        taskTransferCalendar.set(Calendar.MINUTE, taskTransferMinute)
        taskTransferCalendar.set(Calendar.SECOND, 0)

        // Вычисляем задержку для уведомлений, если желаемое время уже прошло,
        // считаем время начала на следующий день.
        val notificationInitialDelayMillis =
            if (notificationCalendar.timeInMillis <= currentTimeMillis) {
                notificationCalendar.add(Calendar.DAY_OF_YEAR, 1) // Прибавляем 1 день
                notificationCalendar.timeInMillis - currentTimeMillis
            } else {
                notificationCalendar.timeInMillis - currentTimeMillis
            }

        // Логирование задержки для уведомлений
        Log.d("WorkManager", "Notification initial delay: $notificationInitialDelayMillis ms")

        // Вычисляем задержку для трансфер воркера, если желаемое время уже прошло,
        // считаем время начала на следующий день.
        val taskTransferInitialDelayMillis =
            if (taskTransferCalendar.timeInMillis <= currentTimeMillis) {
                taskTransferCalendar.add(Calendar.DAY_OF_YEAR, 1) // Прибавляем 1 день
                taskTransferCalendar.timeInMillis - currentTimeMillis
            } else {
                taskTransferCalendar.timeInMillis - currentTimeMillis
            }

        // Логирование задержки для трансфер воркера
        Log.d("WorkManager", "Task transfer initial delay: $taskTransferInitialDelayMillis ms")

        val notificationWorkRequest = PeriodicWorkRequestBuilder<NotificationWorker>(
            repeatIntervalHours.toLong(), // Периодичность выполнения в часах
            TimeUnit.HOURS // Единица измерения периода (часы)
        )
            .setInitialDelay(notificationInitialDelayMillis, TimeUnit.MILLISECONDS)
            .addTag(notificationWorkRequestTag) // Устанавливаем уникальный тег для этого воркера
            .build()

        val taskTransferWorkRequest = PeriodicWorkRequestBuilder<TaskTransferWorker>(
            repeatIntervalHours.toLong(), // Периодичность выполнения в часах
            TimeUnit.HOURS // Единица измерения периода (часы)
        )
            .setInitialDelay(taskTransferInitialDelayMillis, TimeUnit.MILLISECONDS)
            .addTag(taskTransferWorkRequestTag) // Устанавливаем уникальный тег для этого воркера
            .build()

        // Логирование перед добавлением задачи для уведомлений в очередь
        Log.d("WorkManager", "Enqueuing notification work with tag: $notificationWorkRequestTag")

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "uniqueNotificationWork", // Уникальное имя работы
            ExistingPeriodicWorkPolicy.REPLACE, // Перезапускаем работу с уникальным тегом, если она уже существует
            notificationWorkRequest
        )

        // Логирование перед добавлением задачи для трансфера в очередь
        Log.d("WorkManager", "Enqueuing task transfer work with tag: $taskTransferWorkRequestTag")

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            "uniqueTaskTransferWork", // Уникальное имя работы
            ExistingPeriodicWorkPolicy.REPLACE, // Перезапускаем работу с уникальным тегом, если она уже существует
            taskTransferWorkRequest
        )
    }





    fun dpToPx(dp: Int): Int {
        return mapper.dpToPx(dp)
    }
}
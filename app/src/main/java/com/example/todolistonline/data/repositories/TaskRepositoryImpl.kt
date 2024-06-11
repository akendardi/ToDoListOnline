package com.example.todolistonline.data.repositories

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import com.example.todolistonline.data.TaskDao
import com.example.todolistonline.data.entities.TaskDbModel
import com.example.todolistonline.domain.Task
import com.example.todolistonline.domain.TaskRepository
import com.example.todolistonline.domain.TaskState
import com.example.todolistonline.mapper.Mapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.TimeoutCancellationException
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val taskDao: TaskDao,
    private val context: Context,
    private val mapper: Mapper
) :
    TaskRepository {

    override suspend fun insertTaskLocalDb(task: TaskDbModel): Long {
        return withContext(Dispatchers.IO) {
            taskDao.insert(task)
        }
    }

    override suspend fun insertTaskFirebase(task: TaskDbModel, id: Long) {
        if (isNetworkAvailable()) {
            try {
                Log.d("PIZDA", task.toString())
                val usersRef = firebaseDatabase.getReference("users")
                val currentUser = FirebaseAuth.getInstance().currentUser
                val uid = currentUser?.uid

                uid?.let {
                    usersRef.child(it).child(id.toString()).setValue(task)
                }
                Log.d("MYTAG", "Данные пользователя сохранены удаленно")

            } catch (e: IOException) {
                Log.e("MYTAG", "Ошибка сохранения данных пользователя: ${e.message}")
            } catch (e: TimeoutCancellationException) {
                Log.e("MYTAG", "Превышено время ожидания загрузки данных")
            }
        }
    }


    override suspend fun deleteTaskFirebase(task: TaskDbModel) {
        withContext(Dispatchers.IO) {
            try {
                val currentUser = auth.currentUser
                val uid = currentUser?.uid

                if (uid != null) {
                    val usersRef =
                        firebaseDatabase.getReference("users").child(uid).child(task.id.toString())
                    usersRef.removeValue().await()
                    Log.d("MYTAG", "Задача успешно удалена из Firebase")
                } else {
                    Log.e(
                        "MYTAG",
                        "Пользователь не авторизован, задача не может быть удалена из Firebase"
                    )
                }
            } catch (e: Exception) {
                Log.e("MYTAG", "Ошибка удаления задачи: ${e.message}")
            }
        }
    }

    override suspend fun updateTaskLocalDb(task: TaskDbModel) {
        withContext(Dispatchers.IO) {
            taskDao.updateTask(task)
        }
    }

    override suspend fun updateTaskFirebase(task: TaskDbModel) {
        withContext(Dispatchers.IO) {
            val usersRef = firebaseDatabase.getReference("users")
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = currentUser?.uid

            uid?.let {
                usersRef.child(it).child(task.id.toString()).setValue(task)
            }
        }
    }

    override suspend fun getAllTasks(): List<Task> {
        return withContext(Dispatchers.IO) {
            taskDao.getAllTasks().map { mapper.dbModelToEntity(it) }
        }
    }


    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            taskDao.deleteAll()
            Log.d("MYTAG", "Все удалено из локальной даты")
        }
    }

    override suspend fun synchronizeData() {
        withContext(Dispatchers.IO) {
            val currentUser = auth.currentUser
            val uid = currentUser?.uid

            if (uid != null) {
                try {
                    val usersRef = firebaseDatabase.getReference("users").child(uid)
                    val localTasks = taskDao.getAllTasks()
                    usersRef.removeValue().await()

                    for (task in localTasks) {
                        usersRef.child(task.id.toString()).setValue(task).await()
                    }

                    Log.d("MYTAG", "Firebase обновлен из локальной базы данных")
                } catch (e: Exception) {
                    Log.e("MYTAG", "Ошибка при обновлении Firebase из локальной базы данных", e)
                }
            } else {
                Log.e("MYTAG", "Пользователь не аутентифицирован")
            }
        }
    }

    override suspend fun getTodayTasks(): List<Task> {
        return withContext(Dispatchers.IO) {
            val list = taskDao.getTodayTasks().toMutableList()
            list.map { mapper.dbModelToEntity(it) }
        }
    }

    override suspend fun getTomorrowTasks(): List<Task> {
        return withContext(Dispatchers.IO) {
            taskDao.getTomorrowTasks().map { mapper.dbModelToEntity(it) }
        }
    }

    override suspend fun fetchTasksFromFirebase() {

        withContext(Dispatchers.IO) {
            val currentUser = auth.currentUser
            val uid = currentUser?.uid

            if (uid != null) {
                try {
                    val usersRef = firebaseDatabase.getReference("users").child(uid)
                    val snapshot = usersRef.get().await()

                    val tasks =
                        snapshot.children.mapNotNull { it.getValue(TaskDbModel::class.java) }

                    taskDao.insertAll(tasks)


                    Log.d(
                        "MYTAG",
                        "Задачи успешно получены из Firebase и сохранены в локальную базу данных"
                    )
                } catch (e: Exception) {
                    Log.e("MYTAG", "Ошибка при получении задач из Firebase", e)
                }
            } else {
                Log.e("MYTAG", "Пользователь не аутентифицирован")
            }
        }
    }



    private fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }


    override suspend fun deleteTaskLocalDb(task: TaskDbModel) {
        withContext(Dispatchers.IO) {
            taskDao.delete(task)
        }
    }


    override suspend fun transferTasks() {
        withContext(Dispatchers.IO) {
            try {
                Log.d("MYTAG", "Начало выполнения transferTasks")

                // Получаем все задачи
                val tasks = getAllTasks().toMutableList()

                // Удаляем все задачи
                deleteAll()

                // Переносим задачи с уменьшением времени
                for (task in tasks) {
                    if (task.state == TaskState.None) {
                        task.time -= 1
                        insertTaskLocalDb(mapper.entityToDbModel(task))
                    }
                }
                Log.d("MYTAG", "Выполнение transferTasks завершено")
            } catch (e: Exception) {
                Log.e("MYTAG", "Ошибка при выполнении transferTasks", e)
            }
        }
    }

}
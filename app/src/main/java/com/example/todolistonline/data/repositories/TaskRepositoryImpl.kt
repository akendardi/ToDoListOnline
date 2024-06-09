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

    override suspend fun getAllTasks(): List<Task> {
        return taskDao.getAllTasks().map { mapper.dbModelToEntity(it) }
    }

    override suspend fun updateTask(task: TaskDbModel) {
        withContext(Dispatchers.IO){
            taskDao.updateTask(task)
            val usersRef = firebaseDatabase.getReference("users")
            val currentUser = FirebaseAuth.getInstance().currentUser
            val uid = currentUser?.uid

            uid?.let {
                usersRef.child(it).child(task.id.toString()).setValue(task)
            }
        }
    }

    override suspend fun deleteAll() {
        withContext(Dispatchers.IO) {
            taskDao.deleteAll()
            Log.d("MYTAG", "Все удалено из локальной даты")
        }
    }

    override suspend fun synchronizeData() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            try {
                val usersRef = firebaseDatabase.getReference("users").child(uid)
                withContext(Dispatchers.IO) {
                    val localTasks = taskDao.getAllTasks()
                    usersRef.removeValue().await()

                    // Добавить все локальные задачи в Firebase
                    for (task in localTasks) {
                        usersRef.child(task.id.toString()).setValue(task).await()
                    }
                }

                // Удалить все существующие задачи в Firebase


                Log.d("MYTAG", "Firebase обновлен из локальной базы данных")
            } catch (e: Exception) {
                Log.e("MYTAG", "Ошибка при обновлении Firebase из локальной базы данных", e)
            }
        } else {
            Log.e("MYTAG", "Пользователь не аутентифицирован")
        }
    }

    override suspend fun getTodayTasks(): List<Task> {
        val list = taskDao.getTodayTasks().toMutableList()
        return list.map { mapper.dbModelToEntity(it) }
    }

    override suspend fun getTomorrowTasks(): List<Task> {
        return taskDao.getTomorrowTasks().map { mapper.dbModelToEntity(it) }
    }

    override suspend fun fetchTasksFromFirebase() {
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            try {
                val usersRef = firebaseDatabase.getReference("users").child(uid)
                val snapshot = usersRef.get().await()

                val tasks = snapshot.children.mapNotNull { it.getValue(TaskDbModel::class.java) }

                val sortedTasks = tasks.sortedWith(
                    compareByDescending<TaskDbModel> { it.priority }
                        .thenBy { it.state == TaskState.Done }
                )

                // Убедитесь, что операция с базой данных выполняется на IO потоке
                withContext(Dispatchers.IO) {
                    taskDao.insertAll(sortedTasks)
                }

                Log.d("MYTAG", "Задачи успешно получены из Firebase и сохранены в локальную базу данных")
            } catch (e: Exception) {
                Log.e("MYTAG", "Ошибка при получении задач из Firebase", e)
            }
        } else {
            Log.e("MYTAG", "Пользователь не аутентифицирован")
        }
    }



    override suspend fun insertTask(task: TaskDbModel): Boolean {
        // Проверяем наличие интернет-соединения
        if (isNetworkAvailable()) {
            try {
                // Отправляем данные в удаленную базу данных Firebase
                val taskId = taskDao.insert(task)
                task.id = taskId
                Log.d("MYTAG", taskId.toString())

                val usersRef = firebaseDatabase.getReference("users")
                val currentUser = FirebaseAuth.getInstance().currentUser
                val uid = currentUser?.uid

                uid?.let {
                    usersRef.child(it).child(taskId.toString()).setValue(task)
                }
                Log.d("MYTAG", "Данные пользователя сохранены удаленно")
                return true

            } catch (e: IOException) {
                Log.e("MYTAG", "Ошибка сохранения данных пользователя: ${e.message}")
                return false
            } catch (e: TimeoutCancellationException) {
                Log.e("MYTAG", "Превышено время ожидания загрузки данных")
                return false
            }
        } else {
            taskDao.insert(task)
            Log.d("MYTAG", "Данные пользователя сохранены локально")
            return true
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(network)
        return networkCapabilities != null &&
                (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) ||
                        networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET))
    }




    override suspend fun deleteTask(task: TaskDbModel) {
        withContext(Dispatchers.IO) {
            taskDao.delete(task)
        }
        val currentUser = auth.currentUser
        val uid = currentUser?.uid

        if (uid != null) {
            val usersRef =
                firebaseDatabase.getReference("users").child(uid).child(task.id.toString())
            usersRef.removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("MYTAG", "Задача успешно удалена из Firebase")
                } else {
                    Log.e("MYTAG", "Ошибка удаления задачи из Firebase: ${task.exception?.message}")
                }
            }
        }
    }

}
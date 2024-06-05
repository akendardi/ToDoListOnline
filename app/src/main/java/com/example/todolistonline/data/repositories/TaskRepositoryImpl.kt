package com.example.todolistonline.data.repositories

import android.util.Log
import com.example.todolistonline.data.AppDatabase
import com.example.todolistonline.data.TaskDao
import com.example.todolistonline.data.entities.TaskLocalDbModel
import com.example.todolistonline.domain.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val firebaseDatabase: FirebaseDatabase,
    private val auth: FirebaseAuth,
    private val database: AppDatabase,
    private val taskDao: TaskDao
) :
    TaskRepository {

    private fun getUserCollection(){
    }

    override suspend fun fetchTasksFromFirebase() {

    }

    override suspend fun updateFirebase(task: TaskLocalDbModel) {

    }

    override suspend fun insertTask(task: TaskLocalDbModel) {
        var taskId = taskDao.insert(task)
        Log.d("MYTAG", taskId.toString())
        Log.d("MYTAG", taskDao.toString())
        Log.d("MYTAG", database.toString())

        // Обновляем задачу с присвоенным идентификатором из локальной базы данных
        task.id = taskId

        Log.d("MYTAG", taskId.toString())


        // Получаем ссылку на базу данных Firebase
        val usersRef = firebaseDatabase.getReference("users")
        val currentUser = FirebaseAuth.getInstance().currentUser
        val uid = currentUser?.uid

        Log.d("MYTAG", uid.toString())

        // Записываем данные пользователя в базу данных Firebase, используя идентификатор задачи
        uid?.let {
            usersRef.child(it).child(taskId.toString()).setValue(task)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("MYTAG", "Данные пользователя успешно сохранены")
                    } else {
                        Log.e("MYTAG", "Ошибка сохранения данных пользователя: ${task.exception?.message}")
                    }
                }
        }

    }


    override suspend fun updateTask(task: TaskLocalDbModel) {
        taskDao.update(task)
        updateFirebase(task)
    }

    override suspend fun deleteTask(task: TaskLocalDbModel) {

    }

    override suspend fun sortTasks(tasks: List<TaskLocalDbModel>) {
        TODO("Not yet implemented")
    }
}
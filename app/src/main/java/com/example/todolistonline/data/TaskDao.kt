package com.example.todolistonline.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolistonline.data.entities.TaskDbModel

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE time <= 0")
    suspend fun getTodayTasks(): List<TaskDbModel>


    @Query("SELECT * FROM tasks WHERE time = 1")
    suspend fun getTomorrowTasks(): List<TaskDbModel>

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskDbModel>

    @Query("DELETE FROM tasks")
    suspend fun deleteAll()

    @Update
    suspend fun updateTask(task: TaskDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(taskDbModel: TaskDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(taskDbModels: List<TaskDbModel>)

    @Delete
    suspend fun delete(taskDbModel: TaskDbModel)

    @Update
    fun update(taskDbModel: TaskDbModel)
}
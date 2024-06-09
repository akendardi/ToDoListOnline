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
    fun getTodayTasks(): List<TaskDbModel>


    @Query("SELECT * FROM tasks WHERE time = 1")
    fun getTomorrowTasks(): List<TaskDbModel>

    @Query("SELECT * FROM tasks")
    fun getAllTasks(): List<TaskDbModel>

    @Query("DELETE FROM tasks")
    fun deleteAll()

    @Update
    fun updateTask(task: TaskDbModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskDbModel: TaskDbModel): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(taskDbModels: List<TaskDbModel>)

    @Delete
    fun delete(taskDbModel: TaskDbModel)

    @Update
    fun update(taskDbModel: TaskDbModel)
}
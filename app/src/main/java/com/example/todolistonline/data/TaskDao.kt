package com.example.todolistonline.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.todolistonline.data.entities.TaskLocalDbModel

@Dao
interface TaskDao {

    @Query("SELECT * FROM tasks WHERE time=:date AND state != 'OverDue'")
    fun getTasksByDate(date: Long): LiveData<List<TaskLocalDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(taskLocalDbModel: TaskLocalDbModel): Long

    @Delete
    fun delete(taskLocalDbModel: TaskLocalDbModel)

    @Update
    fun update(taskLocalDbModel: TaskLocalDbModel)
}
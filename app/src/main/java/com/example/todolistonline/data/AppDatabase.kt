package com.example.todolistonline.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.todolistonline.data.entities.TaskLocalDbModel

@Database(entities = [TaskLocalDbModel::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}
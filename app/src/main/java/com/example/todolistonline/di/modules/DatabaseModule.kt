package com.example.todolistonline.di.modules

import android.app.Application
import androidx.room.Room
import com.example.todolistonline.ToDoListOnlineApp
import com.example.todolistonline.data.AppDatabase
import com.example.todolistonline.data.TaskDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DatabaseModule {

    @Singleton
    @Provides
    fun provideDatabase(application: Application): AppDatabase {
        return Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "task_database"
        ).build()
    }

    @Singleton
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }

}
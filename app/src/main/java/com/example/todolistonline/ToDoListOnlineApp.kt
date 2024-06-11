package com.example.todolistonline

import android.app.Application
import androidx.work.Configuration
import com.example.todolistonline.di.DaggerAppComponent
import com.example.todolistonline.workers.WorkerFactory
import javax.inject.Inject

class ToDoListOnlineApp : Application(), Configuration.Provider {

    @Inject
    lateinit var workerFactory: WorkerFactory

    override fun onCreate() {
        component.inject(this)
        super.onCreate()
    }

    val component by lazy {
        DaggerAppComponent.factory().create(applicationContext, this)
    }

    override fun getWorkManagerConfiguration(): Configuration {
        return Configuration.Builder()
            .setWorkerFactory(workerFactory).build()
    }
}
package com.example.todolistonline

import android.app.Application
import com.example.todolistonline.di.DaggerAppComponent

class ToDoListOnlineApp : Application() {
    val component by lazy {
        DaggerAppComponent.factory().create(applicationContext)
    }
}
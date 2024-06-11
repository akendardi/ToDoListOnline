package com.example.todolistonline.di.modules

import androidx.work.ListenableWorker
import com.example.todolistonline.di.WorkerKey
import com.example.todolistonline.workers.ChildWorkerFactory
import com.example.todolistonline.workers.NotificationWorker
import com.example.todolistonline.workers.TaskTransferWorker
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap

@Module
interface WorkerModule {

    @Binds
    @IntoMap
    @WorkerKey(TaskTransferWorker::class)
    fun bindTaskWorker(taskTransferWorker: TaskTransferWorker.Factory): ChildWorkerFactory

    @Binds
    @IntoMap
    @WorkerKey(NotificationWorker::class)
    fun bindNotificationModule(notificationWorker: NotificationWorker.Factory): ChildWorkerFactory
}
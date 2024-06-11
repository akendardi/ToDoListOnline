package com.example.todolistonline.workers

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import javax.inject.Inject
import javax.inject.Provider

class WorkerFactory @Inject constructor(
    private val workersProviders: @JvmSuppressWildcards Map<Class<out ListenableWorker>, Provider<ChildWorkerFactory>>
) : WorkerFactory(){

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TaskTransferWorker::class.qualifiedName -> {
                val child = workersProviders[TaskTransferWorker::class.java]?.get()
                return child?.create(appContext, workerParameters)
            }
            NotificationWorker::class.qualifiedName -> {
                val child = workersProviders[NotificationWorker::class.java]?.get()
                return child?.create(appContext, workerParameters)
            }
            else -> null
        }
    }
}
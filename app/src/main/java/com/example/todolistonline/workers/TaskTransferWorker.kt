package com.example.todolistonline.workers

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.todolistonline.domain.use_cases.tasks_use_cases.TransferTasksUseCase
import javax.inject.Inject

class TaskTransferWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val transferTasksUseCase: TransferTasksUseCase
) : CoroutineWorker(context, workerParams) {


    override suspend fun doWork(): Result {
        Log.d("TaskTransferWorker", "Starting task transfer work")
        return try {
            Log.d("MYTAG", "START WORKER")
            transferTasksUseCase.invoke()
            Result.success()
        } catch (e: Exception) {
            Log.e("MYTAG", "Ошибка при выполнении transferTasks", e)
            Result.failure()
        }
    }

    class Factory @Inject constructor(private val transferTasksUseCase: TransferTasksUseCase): ChildWorkerFactory {
        override fun create(context: Context, workerParams: WorkerParameters): ListenableWorker {
            return TaskTransferWorker(context, workerParams, transferTasksUseCase)
        }
    }


}

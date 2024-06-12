package com.example.todolistonline.workers

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import com.example.todolistonline.R
import javax.inject.Inject

class NotificationWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        return try {
            Log.d("NotificationWorker", "Starting notification work")
            showNotification("Задания", "Не забудьте проверить ваши задания!")
            Result.success()
        } catch (e: Exception) {
            Log.e("MYTAG", "Ошибка при выполнении уведомления", e)
            Result.failure()
        }
    }

    private fun showNotification(title: String, message: String) {
        val channelId = "task_reminder_channel"
        val notificationManager = NotificationManagerCompat.from(applicationContext)

        // Создание канала уведомлений для API 26+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Task Reminder"
            val descriptionText = "Reminder to check your tasks"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            notificationManager.createNotificationChannel(channel)
        }

        // Проверка разрешения для Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ActivityCompat.checkSelfPermission(applicationContext, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.e("MYTAG", "Разрешение на отправку уведомлений не предоставлено")
            return
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        notificationManager.notify(1, notification)
    }

    class Factory @Inject constructor(

    ) : ChildWorkerFactory {
        override fun create(context: Context, workerParams: WorkerParameters): ListenableWorker {
            return NotificationWorker(context, workerParams)
        }
    }
}

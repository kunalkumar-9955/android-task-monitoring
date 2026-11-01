package com.taskpulse.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.taskpulse.app.R

object NotificationHelper {
    const val CHANNEL_ID = "taskpulse_alerts"
    private const val CHANNEL_NAME = "TaskPulse Alerts"
    private const val CHANNEL_DESCRIPTION = "Notifications for TaskPulse health and usage alerts."

    fun createNotificationChannel(context: Context) {
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        if (manager.getNotificationChannel(CHANNEL_ID) != null) return
        val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT).apply {
            description = CHANNEL_DESCRIPTION
        }
        manager.createNotificationChannel(channel)
    }

    fun sendNotification(context: Context, title: String, message: String) {
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify((System.currentTimeMillis() % 10000).toInt(), notification)
    }
}

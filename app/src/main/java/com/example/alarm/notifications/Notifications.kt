package com.example.alarm.notifications

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.alarm.MainActivity
import com.example.alarm.R

class Notifications {
    companion object {
        private const val NOTIFICATION_CHANNEL_ID = "test_channel_id"
        private const val NOTIFICATION_ID = 1000

        @SuppressLint("MissingPermission")
        fun createNotification(
            context: Context,
            hour: Int,
            minutes: Int,
            riseHour: Int?,
            riseMin: Int?
        ) {
            val delay = String.format(
                "%02d : %02d",
                hour,
                minutes
            )
            val riseTime = String.format(
                "%02d : %02d",
                riseHour,
                riseMin
            )
            val text = "Будильник сработает через $delay после восхода солнца в $riseTime"
            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .build()
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notification)

        }

        @SuppressLint("MissingPermission")
        fun createNotificationOnAlarm(
            context: Context
        ) {
            val text = "Это Будильник!!!"

            val intent = Intent(
                context,
                MainActivity::class.java
            )

            val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_IMMUTABLE)
            } else {
                PendingIntent.getActivity(
                    context,
                    0,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT
                )

            }

            val notification = NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.notification)
                .setContentTitle(text)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build()
            NotificationManagerCompat.from(context)
                .notify(NOTIFICATION_ID, notification)
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun createNotificationChannel(context: Context) {
            val name = "Уведомления"
            val descriptionText = "Simple description"
            val importance = NotificationManager.IMPORTANCE_DEFAULT

            val channel = NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager = context.getNotificationManager()
            notificationManager.createNotificationChannel(channel)
        }

        private fun Context.getNotificationManager(): NotificationManager {
            return getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        }
    }
}
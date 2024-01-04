package com.example.alarm.alarm

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Locale

class Alarm(
    private val context: Context
) {
    companion object {
        private const val ALARM_REQUEST_CODE = 123
    }

    fun createAlarm(time: Long, hour: Int, minutes: Int, melody: Uri?) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val formatter = SimpleDateFormat("yyyy:MM:dd:hh:mm", Locale.getDefault())

        val alarmTime = calculateTimeForLaunch(time, hour, minutes)
        Log.d("alarmTime", "alarmTime=====${formatter.format(alarmTime)}")
        val intent = ALReceiver.createIntent(context, melody)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            ALARM_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE
        )
        val alarmType = AlarmManager.RTC_WAKEUP
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        }
        alarmManager.setExactAndAllowWhileIdle(
            alarmType,
            alarmTime,
            pendingIntent
        )
    }

    private fun calculateTimeForLaunch(time: Long, hour: Int, minutes: Int): Long {
        val minuteDelay = minutes * 60000L
        val hourDelay = hour * 3600000L
        return time + hourDelay + minuteDelay
    }
}
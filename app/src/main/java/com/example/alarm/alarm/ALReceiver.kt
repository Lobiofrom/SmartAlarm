package com.example.alarm.alarm

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import com.example.alarm.notifications.Notifications

class ALReceiver : BroadcastReceiver() {

    companion object {
        private const val ALARM_ACTION = "ACTION"

        fun createIntent(context: Context, melody: Uri?): Intent {
            return Intent(context, ALReceiver::class.java).apply {
                action = ALARM_ACTION
                data = melody
            }
        }
    }

    override fun onReceive(context: Context, intent: Intent) {
        val melodyUri = intent.data
        val mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(context, melodyUri!!)
        mediaPlayer.prepare()
        mediaPlayer.start()
        Notifications.createNotificationOnAlarm(context)
    }
}
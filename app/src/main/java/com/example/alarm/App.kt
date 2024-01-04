package com.example.alarm

import android.app.Application
import android.os.Build
import com.example.alarm.di.appModule
import com.example.alarm.notifications.Notifications
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            Notifications.createNotificationChannel(this)

        startKoin {
            androidLogger()
            androidContext(this@App)
            modules(appModule)
        }
    }
}
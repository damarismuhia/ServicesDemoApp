package com.dmuhia.foregroundserviceapp

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import com.dmuhia.foregroundserviceapp.utils.CHANNEL_ID
import com.dmuhia.foregroundserviceapp.utils.CHANNEL_NAME


class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()

//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){ // api level 26 android 8
//            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
    }

}
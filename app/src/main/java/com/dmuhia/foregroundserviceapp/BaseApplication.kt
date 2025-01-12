package com.dmuhia.foregroundserviceapp

import android.app.Application


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
package com.dmuhia.foregroundserviceapp.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.BitmapFactory
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.media.app.NotificationCompat.MediaStyle
import com.dmuhia.foregroundserviceapp.R
import com.dmuhia.foregroundserviceapp.services.createNextPendingIntent
import com.dmuhia.foregroundserviceapp.services.createPlayPausePendingIntent
import com.dmuhia.foregroundserviceapp.services.createPrevPendingIntent

const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "channel_name"
const val PREV = "prev"
const val NEXT = "next"
const val PLAY_PAUSE = "play_pause"


fun createNotification(context: Context, notificationTitle:String, notContent:String,style:MediaStyle) : Notification {
    val builder = NotificationCompat.Builder(context, CHANNEL_ID)
        .setStyle(style)
        .setContentTitle(notificationTitle)
        .setContentText(notContent)
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setLargeIcon(BitmapFactory.decodeResource(context.resources, R.drawable.big_image))
    val notificationManager = context.getSystemService(NotificationManager::class.java)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

    }
    return builder.build()
}
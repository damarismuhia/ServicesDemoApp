package com.dmuhia.foregroundserviceapp.services

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.dmuhia.foregroundserviceapp.utils.NEXT
import com.dmuhia.foregroundserviceapp.utils.PLAY_PAUSE
import com.dmuhia.foregroundserviceapp.utils.PREV
/**
 ** What is a PendingIntent?
 * A PendingIntent is a special type of Intent that you can use to delegate an action to another application or component
  (like the system or a background service).
 * The key difference between a regular Intent and a PendingIntent is that the
    latter can be executed at a later time, and it can be triggered by other apps or components.
 *
 * PendingIntent is commonly used in scenarios like:
 *
 * 1. Notifications: When a user taps a notification, a PendingIntent is used to launch an activity, start a service, or broadcast a message.
 * 2. AlarmManager: To schedule a service to run at a future time.
 * 3. Widgets: To handle clicks on a widget.
 *
 *
 * Flags for PendingIntent:
 * When you create a PendingIntent, you can specify different flags to control how the PendingIntent behaves. Example: whether it can be modified after creation or whether it should update existing PendingIntent objects.
 *
 * FLAG_UPDATE_CURRENT: This flag ensures that if there is an existing PendingIntent with the same request code, it will be updated with the new Intent instead of creating a new PendingIntent.
 * FLAG_CANCEL_CURRENT: If there is an existing PendingIntent with the same request code, it will be canceled, and a new PendingIntent will be created.
 * FLAG_IMMUTABLE: This flag marks the PendingIntent as immutable, meaning its Intent cannot be modified after creation.*/
fun createPrevPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MusicPlayerService::class.java).apply { //Explicit Intent
        action = PREV
    }
    return PendingIntent.getService(
        context, 0, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    ) //creates a PendingIntent that will trigger an action in the MusicPlayerService
}
fun createPlayPausePendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MusicPlayerService::class.java).apply {
        action = PLAY_PAUSE
    }
    return PendingIntent.getService(
        context, 1, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}
fun createNextPendingIntent(context: Context): PendingIntent {
    val intent = Intent(context, MusicPlayerService::class.java).apply {
        action = NEXT
    }
    return PendingIntent.getService(
        context, 2, intent, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
}
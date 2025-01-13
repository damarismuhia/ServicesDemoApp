package com.dmuhia.foregroundserviceapp

import android.app.Application
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy
import android.os.StrictMode.VmPolicy
import android.util.Log
import android.util.SparseArray


class BaseApplication : Application() {


    override fun onCreate() {
        super.onCreate()
        StrictMode.setThreadPolicy(ThreadPolicy.Builder()
            .detectNetwork()
            .detectDiskReads()
            .penaltyLog()
            .penaltyDeath()
            .build()
        )
        StrictMode.setVmPolicy(VmPolicy.Builder()
            .detectCleartextNetwork()
            .detectActivityLeaks()
            .detectLeakedRegistrationObjects()
            .detectLeakedSqlLiteObjects()
            .penaltyLog()
            .penaltyDeath()
            .build())


//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.O){ // api level 26 android 8
//            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME,NotificationManager.IMPORTANCE_DEFAULT)
//            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
//            notificationManager.createNotificationChannel(channel)
//        }
    }

    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        val hasmap = HashMap<String,String>()
        val sparseArray = SparseArray<SparseTests>()
        sparseArray.put(1, SparseTests("wambui"))
        Log.e("TAG", "Time taken:: ${sparseArray}")



    }
}
class SparseTests(val name:String)
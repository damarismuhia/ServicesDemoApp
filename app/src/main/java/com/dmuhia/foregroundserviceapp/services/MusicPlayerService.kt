package com.dmuhia.foregroundserviceapp.services

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.IntentService
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import android.widget.Toast
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.dmuhia.foregroundserviceapp.R
import com.dmuhia.foregroundserviceapp.model.Track
import com.dmuhia.foregroundserviceapp.model.songs
import com.dmuhia.foregroundserviceapp.utils.CHANNEL_ID
import com.dmuhia.foregroundserviceapp.utils.CHANNEL_NAME
import com.dmuhia.foregroundserviceapp.utils.NEXT
import com.dmuhia.foregroundserviceapp.utils.PLAY_PAUSE
import com.dmuhia.foregroundserviceapp.utils.PREV
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class MusicPlayerService: Service() {
    private lateinit var binder:MusicBinder
    private lateinit var mediaPlayer :MediaPlayer

    private var musicList = mutableListOf(Track())
    private val currentTrack = MutableStateFlow(Track())
    private val maxDuration = MutableStateFlow(0f)
    private val currentDuration = MutableStateFlow(0f)
    private val isPlaying = MutableStateFlow(false)
    private val isPostNotificationGranted = MutableStateFlow(false)

    private val scope = CoroutineScope(Dispatchers.Main)
    private var job: Job? = null

    /**This class is used to bind the MusicPlayerService to clients (e.g., activities) that need to interact with the service
     * MusicBinder allow us to expose methods to any client that binds to it*/
    inner class MusicBinder:Binder(){
        fun getService() = this@MusicPlayerService
        fun setMusicList(list:List<Track>){
            this@MusicPlayerService.musicList = list.toMutableList()
        }
        fun currentTrack() = this@MusicPlayerService.currentTrack
        fun maxDuration() = this@MusicPlayerService.maxDuration
        fun currentDuration() = this@MusicPlayerService.currentDuration
        fun isPlaying() = this@MusicPlayerService.isPlaying
        fun isPostNotificationGranted() = this@MusicPlayerService.isPostNotificationGranted

    }

    override fun onCreate() {
        super.onCreate()
        mediaPlayer = MediaPlayer()
        binder = MusicBinder()
        val notificationManager = this.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)

        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        Log.e("onbind", "On OnBIND called")
        return binder
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e("onStartCommand", "On onStartCommand called")
        if (intent !=null) {
            when(intent.action) {
                PREV ->{
                    prev()
                }
                PLAY_PAUSE ->{
                    playPause()
                }
                NEXT ->{
                    next()
                }
                else ->{
                    currentTrack.update { songs[0] }
                    Log.e(this::class.java.simpleName,"currentTrack.value is-----> ${currentTrack.value}")
                    play(currentTrack.value)
                }
            }
        }
        /** START_STICKY: The system will try to recreate the Service and call onStartCommand() with a null intent if the Service was killed due to resource constraints.
         * START_NOT_STICKY: The system will not try to recreate the Service, and the Service will remain stopped until an explicit start command is sent.  Eg one time download
         * START_REDELIVER_INTENT: The system will try to recreate the Service and redeliver the last intent that was passed to the
           Service through onStartCommand() if the Service was killed before it finished processing the intent.eg. Messaging App
         *
         *
         * */
        return START_STICKY
    }
    fun prev() {
        job?.cancel()
        /**
         * 1. Reset the media player
         * 2. create a new instance of media player
         * 3.Get the current index of the track
         * 4. Determine the previous index of track(if current index is 0 the previous index is set to be the last item in the music list
         * 5. using the new previous index set the pre track to be played
         * 6. update the current track to this new track using stateflow
         * 7. set the resource the media player will play
         * 8. prepare the media for playback asynchronously
         * 9. start the media player and send a notification
         * */
        //This resets the MediaPlayer instance, releasing resources and preparing it to load a new track
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer()

        val currentTrackIndex = musicList.indexOf(currentTrack.value)
        val prevIndex = if (currentTrackIndex < 0) musicList.size.minus(1) else currentTrackIndex.minus(1)
        val prevItem = musicList[prevIndex] // item to be played

        currentTrack.update { prevItem } // updates the current track to the prevItem

        mediaPlayer.setDataSource(this, getRawUri(currentTrack.value.id))
        /**
         * The prepareAsync() function prepares the media for playback asynchronously.
         * Once the media is prepared, the setOnPreparedListener is triggered, which starts the media player and sends a notification with the new track.*/
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            sendNotification(currentTrack.value)
            updateDurations()
        }
    }

    fun next() {
        job?.cancel()
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer()
        val currentIndex = musicList.indexOf(currentTrack.value)
        //
        val nextIndex = currentIndex.plus(1).mod(musicList.size) // size = 4 , mod = 4/4 = 0(first index)
        val nextItem = musicList[nextIndex]

        currentTrack.update { nextItem }
        mediaPlayer.setDataSource(this, getRawUri(nextItem.id))
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            sendNotification(currentTrack.value)
            updateDurations()
        }
    }

    fun playPause() {
        if (mediaPlayer.isPlaying) {
            mediaPlayer.pause()
        } else {
            mediaPlayer.start()
        }
        sendNotification(currentTrack.value)
    }
    private fun play(track: Track) {
        job?.cancel()
        mediaPlayer.reset()
        mediaPlayer = MediaPlayer()
        mediaPlayer.setDataSource(this, getRawUri(track.id))
        mediaPlayer.prepareAsync()
        mediaPlayer.setOnPreparedListener {
            mediaPlayer.start()
            sendNotification(track)
            updateDurations()
        }
    }
    /**This func is responsible for updating the media playback durations in real-time while the mediaPlayer is playing a track.*/
    private fun updateDurations() {
        job = scope.launch {
            try {
                if (mediaPlayer.isPlaying.not()) return@launch
                maxDuration.update { mediaPlayer.duration.toFloat() }
                while (isActive) {
                    currentDuration.update { mediaPlayer.currentPosition.toFloat() }
                    delay(1000)
                }
            }catch (e:IllegalStateException){
                e.printStackTrace()
                Log.e("TAG","Error on updateDurations ${e.message}")
            }


        }
    }

    private fun getRawUri(id: Int) = Uri.parse("android.resource://${packageName}/${id}")

    private fun sendNotification(track: Track){

        /** MediaSessionCompat:
         * It allows external media controllers (e.g., Bluetooth devices, Android Auto) to interact with your app's media playback.
         *
         * */
        val session = MediaSessionCompat(this,"mp3-music")
        isPlaying.update { mediaPlayer.isPlaying }
        val style = androidx.media.app.NotificationCompat.MediaStyle() //It displays media controls (e.g., Play, Pause, Next, Previous) directly in the notification.
            .setShowActionsInCompactView(0,1,2)
            .setMediaSession(session.sessionToken)
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setStyle(style)
            .setContentTitle(track.name)
            .setContentText(track.desc)
            .addAction(R.drawable.ic_prev,"prev",createPrevPendingIntent(this))
            .addAction(if (mediaPlayer.isPlaying) R.drawable.ic_play else R.drawable.ic_pause,"play-pause",createPlayPausePendingIntent(this))
            .addAction(R.drawable.ic_next,"next",createNextPendingIntent(this))
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.big_image))
            .build()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU){

            if (ContextCompat.checkSelfPermission(this,POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {

                startForeground(1,notification)
                isPostNotificationGranted.update { true }
            }else{
                isPostNotificationGranted.update { false }
                Toast.makeText(this,"Notification Permission is required",Toast.LENGTH_LONG).show()
            }
        } else {
            /** startForeground:
             * Starts the service in the foreground state and displays the notification.
             * Prevents the service from being killed by the system when the app is in the background.
             * The 1 is a unique notification ID used to identify and update the notification later if needed.*/
            startForeground(1,notification)
        }
    }

    override fun onUnbind(intent: Intent?): Boolean {
        Log.e("onUnbind", "On onUnbind called")
        return super.onUnbind(intent)
    }


    // In MusicPlayerService class:
    override fun onDestroy() {
        Log.e("onUnbind", "On onDestroy called")
        // Stop and release the MediaPlayer resources
        if (mediaPlayer.isPlaying) {
            mediaPlayer.stop()
        }
        mediaPlayer.release()

        // Stop the foreground notification
        stopForeground(true)

        // Stop the service if no longer needed
        stopSelf()

        Log.d("MusicPlayerService", "Music stopped and service destroyed.")
    }


}
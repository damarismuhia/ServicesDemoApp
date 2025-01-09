package com.dmuhia.foregroundserviceapp.services

import android.Manifest.permission.POST_NOTIFICATIONS
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
import kotlinx.coroutines.launch

class MusicPlayerService: Service() {
    private val binder = MusicBinder()
    private var mediaPlayer = MediaPlayer()

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
        val notificationManager = this.getSystemService(NotificationManager::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)

        }
    }
    override fun onBind(intent: Intent?): IBinder? {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
        /** START_STICKY is most often used for long-running services where you want the service to continue running even
          if the system kills it to free up resources. however the the Intent will be null
         * START_NOT_STICKY - if the system kills the service, you dont need to restart it. Eg one time download
         * START_REDELIVER_INTENT - The service will be restarted if killed, and the last Intent that was used to start the service will be
         * passed to the new instance eg. Messaging App
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
                while (true) {
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



}
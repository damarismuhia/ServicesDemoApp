package com.dmuhia.foregroundserviceapp

import android.Manifest.permission.POST_NOTIFICATIONS
import android.app.ActivityManager
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Build
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import com.dmuhia.foregroundserviceapp.model.Track
import com.dmuhia.foregroundserviceapp.model.songs
import com.dmuhia.foregroundserviceapp.services.MusicPlayerService
import com.dmuhia.foregroundserviceapp.ui.theme.ForegroundServiceAppTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private var service: MusicPlayerService? = null
    private var musicBinder: MusicPlayerService.MusicBinder? = null
    /**
     * These StateFlow variables are observable, meaning any updates to their values will automatically
     * trigger a recomposition of the UI when they're used in @Composable functions
     * */
    private val currentTrack = MutableStateFlow(Track())
    private val maximumDuration = MutableStateFlow(0f)
    private val currentDuration = MutableStateFlow(0f)
    private val isPlaying = MutableStateFlow(false)
    private val isPostNotificationGranted = MutableStateFlow(false)
    private var isBound = false


    /**
     * The MusicPlayerService is bound to the MainActivity using the ServiceConnection.
     * This allows the activity to communicate with the service and access its methods.
     * onServiceConnected(): When the service is successfully connected, it gets a reference to the MusicPlayerService through the MusicBinder*/
    private val connection = object :ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, binder: IBinder?) {
            musicBinder = binder as MusicPlayerService.MusicBinder
            service = musicBinder?.getService()
            binder.setMusicList(songs)
            //collect values from the service and update UI state variables
            lifecycleScope.launch {
                binder.currentTrack().collectLatest { currentTrack.value = it }
            }
            lifecycleScope.launch {
                binder.maxDuration().collectLatest { maximumDuration.value = it }
            }
            lifecycleScope.launch {
                binder.currentDuration().collectLatest { currentDuration.value = it }
            }
            lifecycleScope.launch {
                binder.isPlaying().collectLatest { isPlaying.value = it }
            }
            lifecycleScope.launch {
                binder.isPostNotificationGranted().collectLatest { isPostNotificationGranted.value = it }
            }
            isBound = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBound = false
            service = null
        }

    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    @OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            ForegroundServiceAppTheme {
                val permission = rememberPermissionState(POST_NOTIFICATIONS)
                val isPostNotificationGranted by isPostNotificationGranted.collectAsState()
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = {
                        TopAppBar(title = { Text(text = "Music Player") }, actions = {
                            IconButton(onClick = {
                                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.TIRAMISU) {
                                    if (isPostNotificationGranted || permission.status.isGranted){
                                        val intent =
                                            Intent(this@MainActivity, MusicPlayerService::class.java)
                                         startService(intent)
                                        bindService(intent, connection, BIND_AUTO_CREATE)
                                    }else{
                                        permission.launchPermissionRequest()
                                    }
                                }else {
                                    val intent =
                                        Intent(this@MainActivity, MusicPlayerService::class.java)
                                    startService(intent) //This tells Android to run the service.
                                    /**bindService means your app can interact with the service and use its methods, like getting updates or calling functions
                                     *
                                     * BIND_AUTO_CREATE: This flag tells Android to automatically start the service if it isn't running already.
                                     * Once the service is connected, the onServiceConnected() callback is triggered. Now, you can interact with the service via the IBinder object.
                                     * **/
                                    bindService(intent, connection, BIND_AUTO_CREATE)
                                }
                            }) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null
                                )
                            }

                            IconButton(onClick = {
                                val intent =
                                    Intent(this@MainActivity, MusicPlayerService::class.java)
                                stopService(intent)
                                unbindService(connection)
                            }) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null)
                            }

                        })
                    }) {

                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {

                        val track by currentTrack.collectAsState()
                        val max by maximumDuration.collectAsState()
                        val current by currentDuration.collectAsState()
                        val isPlaying by isPlaying.collectAsState()

                        Image(
                            painter = painterResource(id = track.image),
                            contentDescription = null,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(300.dp)
                                .clip(RoundedCornerShape(24.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = track.name,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .padding(horizontal = 16.dp)
                                .fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(text = current.div(1000).toString())

                            Slider(
                                modifier = Modifier.weight(1f),
                                value = current, onValueChange = {},
                                valueRange = 0f..max
                            )

                            Text(text = max.div(1000).toString())
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            IconButton(onClick = { service?.prev() }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_prev),
                                    contentDescription = null
                                )
                            }


                            IconButton(onClick = { service?.playPause() }) {
                                Icon(
                                    painter = if (isPlaying) painterResource(id = R.drawable.ic_pause) else painterResource(
                                        id = R.drawable.ic_play
                                    ),
                                    contentDescription = null
                                )
                            }

                            IconButton(onClick = { service?.next() }) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.ic_next
                                    ),
                                    contentDescription = null
                                )
                            }
                        }

                    }

                }
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        if (isBound){
            service = null
            stopService(Intent(this@MainActivity,MusicPlayerService::class.java))
            unbindService(connection)
        }
    }




}

package com.solutelabs.exoplayerpoc

import android.app.PendingIntent
import android.app.PictureInPictureParams
import android.app.RemoteAction
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.res.Configuration
import android.graphics.drawable.Icon
import android.media.session.PlaybackState.ACTION_PAUSE
import android.media.session.PlaybackState.ACTION_PLAY
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.backwordMediaPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.destroyPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.forwordMediaPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.initializedMediaPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.isExoPlaying
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.onExoPause
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.onExoPlayPause


class MainActivity : AppCompatActivity() {


    //private lateinit var player: ExoPlayer
    private lateinit var backword10Sec: Button
    private lateinit var forword10Sec: Button


    private lateinit var playerView: PlayerView
    private lateinit var pipbutton: Button
    private lateinit var playPauseButtonInPIP: Button


    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        pipbutton = findViewById(R.id.btnPictureInPicture)
        forword10Sec = findViewById(R.id.btnForword10sec)
        backword10Sec = findViewById(R.id.btnBackword10sec)
        playPauseButtonInPIP = findViewById(R.id.playPauseButtonInPIP)


        mediaPlayerSetup()


    }

    private fun mediaPlayerSetup() {


        //initializedMediaPlayer(this, playerView,"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        initializedMediaPlayer(
            this,
            playerView,
            "https://ndtvod.pc.cdn.bitgravity.com/23372/hls/SMIDJAIKUMARNTPC27112024_199307_12885_5000/master.m3u8"
        )

        //initializedMediaPlayerWithHLS(this, playerView,"https://ndtvod.pc.cdn.bitgravity.com/23372/hls/SMIDJAIKUMARNTPC27112024_199307_12885_5000/master.m3u8")
        pipbutton.setOnClickListener { onUserLeaveHint() }
        forword10Sec?.setOnClickListener { forwordMediaPlayer(25000) }
        backword10Sec?.setOnClickListener { backwordMediaPlayer(25000) }


        registerReceiver(PipBroadcastReceiver, IntentFilter().apply {
            addAction(ACTION_PLAY)
            //addAction(ACTION_PAUSE)
        }, RECEIVER_NOT_EXPORTED)

    }


    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onStart() {
        super.onStart()
        registerReceiver(PipBroadcastReceiver, IntentFilter().apply {
            addAction(ACTION_PLAY)
            addAction(ACTION_PAUSE)
        }, RECEIVER_NOT_EXPORTED)
        //onPlay()

    }

    override fun onStop() {
        super.onStop()
        onPause()


    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(PipBroadcastReceiver)
        destroyPlayer()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()

        val aspectRatio = Rational(21, 9) // Set your desired aspect ratio
        val pipParams = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .setActions(createPiPActions())
            .build()
        enterPictureInPictureMode(pipParams)
        //setPictureInPictureParams(pipParams)
    }

    private fun createPiPActions(): List<RemoteAction> {
        //val actions = mutableListOf<RemoteAction>()

        // Play Action
        val playIntent = createPendingIntent(this, ACTION_PLAY)
        val playIcon = Icon.createWithResource(this, R.drawable.round_play_arrow_24)
        val playAction = RemoteAction(playIcon, "Play", "Play", playIntent)


        // Pause Action
        val pauseIntent = createPendingIntent(this, ACTION_PAUSE)
        val pauseIcon = Icon.createWithResource(this, R.drawable.baseline_pause_24)
        val pauseAction = RemoteAction(pauseIcon, "Pause", "Pause", pauseIntent)

        val actions = if (isExoPlaying()) {
            listOf(pauseAction)
        } else {
            listOf(playAction)
        }


        return actions
    }

    private fun createPendingIntent(context: Context, action: String): PendingIntent {
        val intent = Intent(context, MainActivity::class.java)
        intent.action = action
        return PendingIntent.getBroadcast(
            this,
            0,
            Intent(action).setPackage(packageName),
            PendingIntent.FLAG_IMMUTABLE
        )
    }



    private val PipBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                ACTION_PLAY -> {
                    onExoPlayPause()
                    Log.e("tag","Play call")
                    val pipBuilder = PictureInPictureParams.Builder()
                        .setActions(createPiPActions())
                        .build()

                    setPictureInPictureParams(pipBuilder)
                }
                ACTION_PAUSE -> {
                    Log.e("tag","Pause call")
                    onExoPlayPause()
                    val pipBuilder = PictureInPictureParams.Builder()
                        .setActions(createPiPActions())
                        .build()

                    setPictureInPictureParams(pipBuilder)
                }

            }
        }
    }

    companion object {
        const val ACTION_PLAY = "ACTION_PLAY"
        const val ACTION_PAUSE = "ACTION_PAUSE"

    }


    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        config: Configuration,
    ) {

        if (isInPictureInPictureMode) {
            // Hide UI controls for PiP mode
            playerView.useController = false
            pipbutton.visibility = Button.GONE
            forword10Sec.visibility = Button.GONE
            backword10Sec.visibility = Button.GONE
            playPauseButtonInPIP.visibility = Button.GONE

        } else {
            // Show UI controls when not in PiP mode
            playerView.useController = true
            pipbutton.visibility = Button.VISIBLE
            forword10Sec.visibility = Button.VISIBLE
            backword10Sec.visibility = Button.VISIBLE
            playPauseButtonInPIP.visibility = Button.GONE
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, config)
    }
}
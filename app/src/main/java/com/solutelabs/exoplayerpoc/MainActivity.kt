package com.solutelabs.exoplayerpoc

import android.app.PictureInPictureParams
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.util.Rational
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.backwordMediaPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.destroyPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.forwordMediaPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.initializedMediaPlayer
import com.solutelabs.exoplayerpoc.MediaPlayerDelegateImpl.onPlay

class MainActivity : AppCompatActivity() {


    private lateinit var player: ExoPlayer
    private lateinit var backword10Sec: Button
    private lateinit var forword10Sec: Button


    private lateinit var playerView: PlayerView
    private lateinit var pipbutton: Button


    @UnstableApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        playerView = findViewById(R.id.player_view)
        pipbutton = findViewById(R.id.btnPictureInPicture)
        forword10Sec = findViewById(R.id.btnForword10sec)
        backword10Sec = findViewById(R.id.btnBackword10sec)


        mediaPlayerSetup()


    }

    private fun mediaPlayerSetup() {


        initializedMediaPlayer(this, playerView,"http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
        pipbutton.setOnClickListener { onUserLeaveHint() }
        forword10Sec?.setOnClickListener { forwordMediaPlayer(25000) }
        backword10Sec?.setOnClickListener { backwordMediaPlayer(25000) }
    }


    override fun onStart() {
        super.onStart()
        onPlay()

    }

    override fun onStop() {
        super.onStop()
        onPause()


    }

    override fun onDestroy() {
        super.onDestroy()
        destroyPlayer()
    }

    override fun onUserLeaveHint() {
        super.onUserLeaveHint()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val aspectRatio = Rational(16, 9) // Set your desired aspect ratio
            val pipParams = PictureInPictureParams.Builder()
                .setAspectRatio(aspectRatio)
                .build()
            enterPictureInPictureMode(pipParams)
        }
    }

    override fun onPictureInPictureModeChanged(
        isInPictureInPictureMode: Boolean,
        config: Configuration,
    ) {

        if (isInPictureInPictureMode) {
            // Hide UI controls for PiP mode
            //playerView.useController = false
            pipbutton.visibility = Button.GONE
            forword10Sec.visibility = Button.GONE
            backword10Sec.visibility = Button.GONE
        } else {
            // Show UI controls when not in PiP mode
            //playerView.useController = true
            pipbutton.visibility = Button.VISIBLE
            forword10Sec.visibility = Button.VISIBLE
            backword10Sec.visibility = Button.VISIBLE
        }
        super.onPictureInPictureModeChanged(isInPictureInPictureMode, config)
    }
}
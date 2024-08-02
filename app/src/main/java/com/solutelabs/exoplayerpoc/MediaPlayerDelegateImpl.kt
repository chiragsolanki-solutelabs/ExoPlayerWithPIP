package com.solutelabs.exoplayerpoc

import android.content.Context
import android.net.Uri
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.PlayerView


interface MediaPlayerDelegate {
    fun initializedMediaPlayer(context: Context, playerView: PlayerView,url:String)
    fun forwordMediaPlayer(duration:Long)
    fun backwordMediaPlayer(duration:Long)
    fun onPlay()
    fun onPause()
    fun destroyPlayer()
    /*@RequiresApi(Build.VERSION_CODES.O)
    fun startPictureInPicture(activity: AppCompatActivity)*/

}
object MediaPlayerDelegateImpl: MediaPlayerDelegate {

    private var player: ExoPlayer?=null
    private var playerView: PlayerView? = null

    override fun initializedMediaPlayer(context: Context, playerView: PlayerView,url:String) {
        this.playerView = playerView
        player = ExoPlayer.Builder(context).build().also { exoPlayer ->
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }



    override fun backwordMediaPlayer(duration:Long) {
        player?.let { it.seekTo(it.currentPosition - duration) }
    }

    override fun forwordMediaPlayer(duration:Long) {
        player?.let { it.seekTo(it.currentPosition + duration) }
    }

    override fun onPlay() {
        player?.play()
    }

    override fun onPause() {
        player?.pause()
    }

    override fun destroyPlayer() {
        player?.release()
    }

   /* @RequiresApi(Build.VERSION_CODES.O)
    override fun startPictureInPicture(activity: AppCompatActivity) {
        val aspectRatio = Rational(16, 9) // Set your desired aspect ratio
        val pipParams = PictureInPictureParams.Builder()
            .setAspectRatio(aspectRatio)
            .build()
        activity.enterPictureInPictureMode(pipParams)
    }*/

}
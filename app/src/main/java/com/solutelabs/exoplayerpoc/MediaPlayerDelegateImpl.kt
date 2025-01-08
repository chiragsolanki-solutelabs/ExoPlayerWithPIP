package com.solutelabs.exoplayerpoc

import android.content.Context
import android.net.Uri
import androidx.annotation.OptIn
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi

import androidx.media3.exoplayer.ExoPlayer


import androidx.media3.ui.PlayerView


interface MediaPlayerDelegate {
    fun initializedMediaPlayer(context: Context, playerView: PlayerView, url: String)
    fun initializedMediaPlayerWithHLS(context: Context, playerView: PlayerView, url: String)
    fun forwordMediaPlayer(duration: Long)
    fun backwordMediaPlayer(duration: Long)
    fun onExoPlay()
    fun onExoPause()
    fun onExoPlayPause()
    fun destroyPlayer()
    fun isExoPlaying(): Boolean
    /*@RequiresApi(Build.VERSION_CODES.O)
    fun startPictureInPicture(activity: AppCompatActivity)*/

}

object MediaPlayerDelegateImpl : MediaPlayerDelegate {

    private var player: ExoPlayer? = null
    private var playerView: PlayerView? = null

    override fun initializedMediaPlayer(context: Context, playerView: PlayerView, url: String) {
        this.playerView = playerView
        player = ExoPlayer.Builder(context).build().also { exoPlayer ->
            playerView.player = exoPlayer
            val mediaItem = MediaItem.fromUri(url)
            exoPlayer.setMediaItem(mediaItem)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }
    }


    @OptIn(UnstableApi::class)
    override fun initializedMediaPlayerWithHLS(
        context: Context,
        playerView: PlayerView,
        url: String
    ) {
        this.playerView = playerView
        /* val dataSourceFactory: DataSource.Factory = DefaultHttpDataSource.Factory()
 // Create a HLS media source pointing to a playlist uri.
         val hlsMediaSource =
             HlsMediaSource.Factory(dataSourceFactory).createMediaSource(MediaItem.fromUri(url))
         player = ExoPlayer.Builder(context).build().also { exoPlayer ->
             playerView.player = exoPlayer
             *//*val mediaItem = MediaItem.fromUri(Uri.parse(url))
            exoPlayer.setMediaItem(mediaItem)*//*
            exoPlayer.setMediaSource(hlsMediaSource)
            exoPlayer.prepare()
            exoPlayer.playWhenReady = true
        }*/
    }


    override fun backwordMediaPlayer(duration: Long) {
        player?.let { it.seekTo(it.currentPosition - duration) }
    }

    override fun forwordMediaPlayer(duration: Long) {
        player?.let { it.seekTo(it.currentPosition + duration) }
    }

    override fun onExoPlay() {
        player?.play()
    }

    override fun onExoPause() {
        player?.pause()
    }

    override fun onExoPlayPause() {
        if (player?.isPlaying == true) {
            onExoPause()
        } else {
            onExoPlay()
        }
    }

    override fun destroyPlayer() {
        player?.release()
    }

    override fun isExoPlaying(): Boolean {
        return player?.isPlaying ?: false
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
package com.example.mediaplayerapp.audioplayer

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.media.AudioManager.OnAudioFocusChangeListener
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.io.IOException

class AudioPlayerViewModel : ViewModel() {
    private val _isPlayingLiveData = MutableLiveData<Boolean>()
    val isPlayingLiveData: LiveData<Boolean> = _isPlayingLiveData

    private val _buttonTextLiveData = MutableLiveData<String>()
    val buttonTextLiveData: LiveData<String> = _buttonTextLiveData


    private val mediaPlayer = MediaPlayer()
    private var playbackPosition: Int = 0
    private var isAudioPrepared = false


    fun pauseAudio() {
        if (mediaPlayer.isPlaying) {
            playbackPosition = mediaPlayer.currentPosition
            mediaPlayer.pause()
            _isPlayingLiveData.value = false
            _buttonTextLiveData.value = "PLAY"

        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun playAudio(audioURI: String, context: Context, listener: OnAudioFocusChangeListener) {
        if (isAudioPrepared) {
            mediaPlayer.start()
            _isPlayingLiveData.value = true
            _buttonTextLiveData.value = "PAUSE"
        } else {
            if (requestAudioFocus(context, listener)) {
                try {
                    val audioUri = Uri.parse(audioURI)
                    mediaPlayer.reset()
                    mediaPlayer.setDataSource(context, audioUri)
                    mediaPlayer.prepare()
                    mediaPlayer.seekTo(playbackPosition)
                    mediaPlayer.start()
                    _isPlayingLiveData.value = true
                    _buttonTextLiveData.value = "PAUSE"
                    isAudioPrepared = true
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun requestAudioFocus(context: Context, listener: OnAudioFocusChangeListener): Boolean {
        val audioManager = context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_MEDIA)
            .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
            .build()

        val audioFocusRequest = AudioFocusRequest.Builder(AudioManager.AUDIOFOCUS_GAIN)
            .setAudioAttributes(audioAttributes)
            .setAcceptsDelayedFocusGain(true)
            .setOnAudioFocusChangeListener(listener)
            .build()

        val result = audioManager.requestAudioFocus(audioFocusRequest)
        return result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED
    }

    fun setVolume(leftVolume: Float, rightVolume: Float) {
        mediaPlayer.setVolume(leftVolume, rightVolume)
    }
    fun getAudioFileName(audioURI: String): String? {
        val fileName = Uri.parse(audioURI)
        val audioName = fileName.lastPathSegment
        return audioName?.substringAfter("/")
    }


}


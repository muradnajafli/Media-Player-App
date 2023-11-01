package com.example.mediaplayerapp.videoplayer

import android.media.MediaPlayer
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


class VideoPlayerViewModel : ViewModel() {
    private val mediaPlayer = MediaPlayer()
    private val _buttonText = MutableLiveData("PAUSE")
    val buttonText: LiveData<String> = _buttonText

    private val _videoUri = MutableLiveData<String>()
    val videoUri: LiveData<String> = _videoUri

    private var isPlaying = true



    fun setVideoUri(videoUri: String) {
        _videoUri.value = videoUri
        try {
            mediaPlayer.setDataSource(videoUri)
            mediaPlayer.prepareAsync()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun togglePlayback() {
        if (isPlaying) {
            mediaPlayer.pause()
            _buttonText.value = "PLAY"
        } else {
            mediaPlayer.start()
            _buttonText.value = "PAUSE"
        }
        isPlaying = !isPlaying
    }

    fun getCurrentPosition(): Int {
        return mediaPlayer.currentPosition
    }

    fun setCurrentPosition(position: Int) {
        mediaPlayer.seekTo(position)
    }

    fun getDuration(): LiveData<Int> {
        return MutableLiveData(mediaPlayer.duration)
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }
}

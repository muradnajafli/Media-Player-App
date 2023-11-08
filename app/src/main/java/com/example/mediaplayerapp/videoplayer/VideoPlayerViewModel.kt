package com.example.mediaplayerapp.videoplayer

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class VideoPlayerViewModel : ViewModel() {

    private val _buttonText = MutableLiveData("PAUSE")
    val buttonText: LiveData<String> = _buttonText

    private val _videoUri = MutableLiveData<String>()
    val videoUri: LiveData<String> = _videoUri

    private var isPlaying = true
    private var currentPosition = 0

    fun setVideoUri(videoUri: String) {
        _videoUri.value = videoUri
    }

    fun togglePlayback() {
        if (isPlaying) {
            _buttonText.value = "PLAY"
        } else {
            _buttonText.value = "PAUSE"
        }
        isPlaying = !isPlaying
    }

    fun getCurrentPosition(): Int {
        return currentPosition
    }

    fun setCurrentPosition(position: Int) {
        currentPosition = position
    }

    fun isPlaying(): Boolean {
        return isPlaying
    }
}

package com.example.mediaplayerapp.videoplayer

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.concurrent.TimeUnit


class VideoPlayerViewModel : ViewModel() {
    private val _videoUri = MutableLiveData<String>()
    val videoUri: LiveData<String> = _videoUri

    fun setVideoUri(uri: String) {
        _videoUri.value = uri
    }
}




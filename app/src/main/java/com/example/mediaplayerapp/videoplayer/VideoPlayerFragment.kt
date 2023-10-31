package com.example.mediaplayerapp.videoplayer

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import android.widget.SeekBar
import com.example.mediaplayerapp.databinding.FragmentVideoPlayerBinding
import java.util.concurrent.TimeUnit


class VideoPlayerFragment : Fragment() {
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val videoUpdateHandler = Handler(Looper.getMainLooper())
    private var isSeeking = false

    private val viewModel: VideoPlayerViewModel by viewModels()
    private val args: VideoPlayerFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)

        val videoUri = viewModel.videoUri.value ?: args.videoURI
        setupVideoView(videoUri)
        setupPlayPauseButton()
        setupVideoSeekBar()

        return binding.root
    }

    private fun setupVideoView(videoUri: String) {
        val mediaController = MediaController(requireContext())
        mediaController.setAnchorView(binding.videoView)

        binding.videoView.setMediaController(mediaController)
        binding.videoView.setVideoURI(Uri.parse(videoUri))
        binding.videoView.start()
    }

    private fun setupPlayPauseButton() {
        binding.playPauseVideoButton.setOnClickListener {
            if (binding.videoView.isPlaying) {
                pauseVideo()
            } else {
                playVideo()
            }
        }
    }

    private fun playVideo() {
        binding.videoView.start()
        binding.playPauseVideoButton.text = "PAUSE"
    }

    private fun pauseVideo() {
        binding.videoView.pause()
        binding.playPauseVideoButton.text = "PLAY"
    }

    private fun setupVideoSeekBar() {
        binding.videoSeekbar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (fromUser) {
                    isSeeking = true
                    binding.videoView.seekTo(progress)
                }
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {
                isSeeking = true
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
                isSeeking = false
            }
        })

        val updateSeekBarRunnable = object : Runnable {
            override fun run() {
                if (!isSeeking) {
                    val currentPosition = binding.videoView.currentPosition
                    val totalDuration = binding.videoView.duration

                    binding.videoSeekbar.progress = currentPosition
                    binding.videoSeekbar.max = totalDuration

                    val formattedTime = String.format(
                        "%d:%02d / %d:%02d",
                        TimeUnit.MILLISECONDS.toMinutes(currentPosition.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(currentPosition.toLong()) % 60,
                        TimeUnit.MILLISECONDS.toMinutes(totalDuration.toLong()),
                        TimeUnit.MILLISECONDS.toSeconds(totalDuration.toLong()) % 60
                    )
                    binding.timerTextView.text = formattedTime
                }
                videoUpdateHandler.postDelayed(this, 1000)
            }
        }
        videoUpdateHandler.post(updateSeekBarRunnable)
    }

    override fun onStop() {
        super.onStop()
        pauseVideo()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.videoView.stopPlayback()
        _binding = null
        videoUpdateHandler.removeCallbacksAndMessages(null)
    }
}




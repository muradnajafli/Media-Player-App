package com.example.mediaplayerapp.videoplayer

import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import android.widget.SeekBar
import com.example.mediaplayerapp.databinding.FragmentVideoPlayerBinding
import java.util.concurrent.TimeUnit


class VideoPlayerFragment : Fragment() {
    private var _binding: FragmentVideoPlayerBinding? = null
    private val binding get() = _binding!!

    private val viewModel: VideoPlayerViewModel by viewModels()
    private val args: VideoPlayerFragmentArgs by navArgs()
    private var isSeeking = false
    private val videoUpdateHandler = Handler(Looper.getMainLooper())


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVideoPlayerBinding.inflate(inflater, container, false)


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.setVideoUri(args.videoURI)
        setupVideoView()
        setupPlayPauseButton()
        setupVideoSeekBar()
        observeEvents()

    }

    private fun observeEvents() {
        viewModel.videoUri.observe(viewLifecycleOwner) { videoUri ->
            setupVideoView()
        }

        viewModel.buttonText.observe(viewLifecycleOwner) { buttonText ->
            binding.playPauseVideoButton.text = buttonText
        }

        viewModel.getDuration().observe(viewLifecycleOwner) { currentPosition ->
            binding.videoSeekbar.progress = currentPosition
        }
    }

    private fun setupVideoView() {
        val videoUri = viewModel.videoUri.value
        if (videoUri != null) {
            binding.videoView.setVideoURI(Uri.parse(videoUri))
            binding.videoView.setOnPreparedListener { mp ->
                mp.isLooping = true
                val savedPosition = viewModel.getCurrentPosition()
                binding.videoSeekbar.max = viewModel.getDuration().value ?: 0
                binding.videoView.seekTo(savedPosition)
                binding.videoView.start()
            }
        }
    }

    private fun setupPlayPauseButton() {
        binding.playPauseVideoButton.setOnClickListener {
            viewModel.togglePlayback()
            if (!viewModel.isPlaying()) {
                binding.videoView.pause()
            } else{
                binding.videoView.start()
            }
        }
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
                if (!isSeeking && view != null) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        val currentPosition = binding.videoView.currentPosition
        viewModel.setCurrentPosition(currentPosition)
        binding.videoView.stopPlayback()
        _binding = null
    }
}
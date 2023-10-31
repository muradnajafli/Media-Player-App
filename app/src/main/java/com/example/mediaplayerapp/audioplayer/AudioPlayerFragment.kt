package com.example.mediaplayerapp.audioplayer

import android.content.Context
import android.media.AudioFocusRequest
import android.media.AudioManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.example.mediaplayerapp.databinding.FragmentAudioPlayerBinding

class AudioPlayerFragment : Fragment() {
    private var _binding: FragmentAudioPlayerBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AudioPlayerViewModel by viewModels()

    private val args: AudioPlayerFragmentArgs by navArgs()

    private var audioFocusRequest: AudioFocusRequest? = null

    @RequiresApi(Build.VERSION_CODES.O)
    private val audioFocusChangeListener = AudioManager.OnAudioFocusChangeListener { focusChange ->
        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                if (viewModel.isPlayingLiveData.value == true) {
                    pauseAudio()
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT,
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                if (viewModel.isPlayingLiveData.value == true) {
                    viewModel.setVolume(0.2f, 0.2f)
                }
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                if (viewModel.isPlayingLiveData.value != true) {
                    playAudio()
                } else {
                    viewModel.setVolume(1.0f, 1.0f)
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAudioPlayerBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
        releaseAudioFocus()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPlayPauseButton()
        viewModel.buttonTextLiveData.observe(viewLifecycleOwner) { buttonText ->
            binding.pausePlayButtonMusic.text = buttonText
        }
        val audioName = viewModel.getAudioFileName(args.audioURI)
        binding.musicNameTextView.text = audioName
        playAudio()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupPlayPauseButton() {
        binding.pausePlayButtonMusic.setOnClickListener {
            if (viewModel.isPlayingLiveData.value == true) {
                pauseAudio()
            } else {
                playAudio()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun releaseAudioFocus() {
        val audioManager = requireContext().getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioFocusRequest?.let { audioManager.abandonAudioFocusRequest(it) }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun playAudio() {
        val audioUri = args.audioURI
        val context = requireContext()
        viewModel.playAudio(audioUri, context, audioFocusChangeListener)
    }
    private fun pauseAudio() {
        viewModel.pauseAudio()

    }
}



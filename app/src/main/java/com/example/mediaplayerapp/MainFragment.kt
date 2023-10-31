package com.example.mediaplayerapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.navigation.fragment.findNavController
import com.example.mediaplayerapp.databinding.FragmentMainBinding

class MainFragment : Fragment() {
    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val filePicker = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            data?.data?.let { selectedFileUri ->
                val fileType = requireContext().contentResolver.getType(selectedFileUri)
                if (fileType?.startsWith("audio/") == true) {
                    showAudioPlayerFragment(selectedFileUri)
                } else if (fileType?.startsWith("video/") == true) {
                    showVideoPlayerFragment(selectedFileUri)
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        binding.buttonVideo.setOnClickListener {
            binding.buttonVideo.isActivated = true
            binding.buttonAudio.isActivated = false
            openFilePicker("video/*")
        }
        binding.buttonAudio.setOnClickListener {
            binding.buttonAudio.isActivated = true
            binding.buttonVideo.isActivated = false
            openFilePicker("audio/*")
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun openFilePicker(mimeType: String) {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.type = mimeType
        filePicker.launch(intent)
    }

    private fun showAudioPlayerFragment(audioUri: Uri) {
        val action = MainFragmentDirections.actionMainFragmentToAudioPlayerFragment(audioUri.toString())
        findNavController().navigate(action)
    }

    private fun showVideoPlayerFragment(videoUri: Uri) {
        val action = MainFragmentDirections.actionMainFragmentToVideoPlayerFragment(videoUri.toString())
        findNavController().navigate(action)
    }
}



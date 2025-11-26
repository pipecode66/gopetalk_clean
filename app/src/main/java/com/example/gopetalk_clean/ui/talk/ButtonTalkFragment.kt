package com.example.gopetalk_clean.ui.talk

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.gopetalk_clean.data.audio.WebSocketManager
import com.example.gopetalk_clean.data.storage.SessionManager
import com.example.gopetalk_clean.databinding.FragmentButtonTalkBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ButtonTalkFragment : Fragment() {
    private val viewModel: ButtonTalkViewModel by viewModels()
    private lateinit var binding: FragmentButtonTalkBinding

    @Inject
    lateinit var webSocketManager: WebSocketManager

    @Inject
    lateinit var sessionManager: SessionManager

    private val channelName: String by lazy { arguments?.getString("channel") ?: "general" }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentButtonTalkBinding.inflate(inflater, container, false)
        setupTouchEvents()
        setupIncomingAudioListener()
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.isTalking.collect { isTalking ->
                binding.btnTalk.isEnabled = !isTalking
            }
        }

        return binding.root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupTouchEvents() {
        binding.btnTalk.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (checkPermission()) {
                        viewModel.startTalking()
                    } else {
                        requestPermission()
                    }
                    true
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    viewModel.stopTalking()
                    true
                }
                else -> false
            }
        }
    }

    private fun checkPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.RECORD_AUDIO),
            REQUEST_RECORD_AUDIO
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_RECORD_AUDIO) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                viewModel.startTalking()
            } else {
                Toast.makeText(requireContext(), "Se necesita permiso de microfono", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupIncomingAudioListener() {
        val userId = sessionManager.getUserId()
        val token = sessionManager.getAccessToken()

        if (userId < 0 || token.isNullOrBlank()) {
            Toast.makeText(requireContext(), "No hay sesion activa para conectarse al canal", Toast.LENGTH_SHORT).show()
            return
        }

        webSocketManager.connect(channelName, userId.toString(), token)

        viewLifecycleOwner.lifecycleScope.launch {
            webSocketManager.incomingAudio.collect { data ->
                viewModel.playAudio(data)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        webSocketManager.disconnect()
    }

    companion object {
        private const val REQUEST_RECORD_AUDIO = 101
    }
}

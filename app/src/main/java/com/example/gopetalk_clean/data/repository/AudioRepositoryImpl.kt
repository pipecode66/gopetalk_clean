package com.example.gopetalk_clean.data.repository

import android.Manifest
import androidx.annotation.RequiresPermission
import com.example.gopetalk_clean.data.api.AudioService
import com.example.gopetalk_clean.domain.repository.AudioRepository
import javax.inject.Inject
import javax.inject.Singleton
import com.example.gopetalk_clean.data.audio.WebSocketManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Singleton
class AudioRepositoryImpl @Inject constructor(
    private val audioService: AudioService,
    private val webSocketManager: WebSocketManager
) : AudioRepository {

    private var userTalking = false

    init {
        CoroutineScope(Dispatchers.IO).launch {
            webSocketManager.incomingAudio.collect { data ->
                playReceivedAudio(data)
            }
        }
    }

    @RequiresPermission(Manifest.permission.RECORD_AUDIO)
    override suspend fun startSending() {
        if (!userTalking) {
            userTalking = true
            audioService.startRecording { data ->
                webSocketManager.sendAudio(data)
            }
        }
    }

    override suspend fun stopSending() {
        userTalking = false
        audioService.stopRecording()
    }

    override fun playReceivedAudio(data: ByteArray) {
        audioService.playAudio(data)
    }

    override fun isUserTalking(): Boolean = userTalking
}

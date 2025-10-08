package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.domain.repository.AudioRepository
import jakarta.inject.Inject

class PlayAudioUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    operator fun invoke(data: ByteArray) = repository.playReceivedAudio(data)
}

package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.domain.repository.AudioRepository
import javax.inject.Inject

class SendAudioUseCase @Inject constructor(
    private val repository: AudioRepository
) {
    suspend operator fun invoke() = repository.startSending()
}

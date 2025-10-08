package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.data.audio.WebSocketManager
import javax.inject.Inject

class ConnectToChannelUseCase @Inject constructor(
    private val webSocketManager: WebSocketManager
) {
    operator fun invoke(channel: String, userId: String, token: String) {
        webSocketManager.connect(channel, userId, token)
    }
}

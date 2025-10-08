package com.example.gopetalk_clean.data.repository

import com.example.gopetalk_clean.data.api.ChannelService
import com.example.gopetalk_clean.data.audio.WebSocketManager
import com.example.gopetalk_clean.data.storage.SessionManager
import com.example.gopetalk_clean.domain.repository.ChannelRepository
import javax.inject.Inject

class ChannelRepositoryImpl @Inject constructor(
    private val channelService: ChannelService,
    private val webSocketManager: WebSocketManager,
    private val sessionManager: SessionManager
) : ChannelRepository {

    override suspend fun getChannels(): List<String> {
        val response = channelService.getChannels()
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Error al obtener canales: ${response.code()}")
        }
    }

    override suspend fun getChannelUsers(canal: String): List<String>  {
        val response = channelService.getChannelUsers(canal)
        if (response.isSuccessful) {
            return response.body() ?: emptyList()
        } else {
            throw Exception("Error fetching channel users: ${response.message()}")
        }
    }

    override suspend fun disconnectFromChannel() {

        webSocketManager.disconnect()
        sessionManager.clearCurrentChannel()
    }

}

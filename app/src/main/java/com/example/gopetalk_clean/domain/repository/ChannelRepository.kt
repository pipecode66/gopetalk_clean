package com.example.gopetalk_clean.domain.repository

interface ChannelRepository {
 suspend fun getChannels(): List<String>
 suspend fun getChannelUsers(canal: String): List<String>
 suspend fun disconnectFromChannel()
}

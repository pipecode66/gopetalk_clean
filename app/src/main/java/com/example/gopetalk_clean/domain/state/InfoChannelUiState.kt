package com.example.gopetalk_clean.domain.state

data class InfoChannelUiState(
    val channels: List<String> = emptyList(),
    val channelName: String? = null,
    val userCountText: String? = null,
    val isConnected: Boolean = false
)
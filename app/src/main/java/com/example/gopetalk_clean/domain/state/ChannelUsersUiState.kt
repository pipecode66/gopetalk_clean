package com.example.gopetalk_clean.domain.state

data class ChannelUsersUiState(
    val channels: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val users: List<String> = emptyList(),
    val channelName: String? = null,
    val userCount: Int = 0,
    val errorMessage: String? = null
)
package com.example.gopetalk_clean.domain.state

data class ChannelUiState(
    val isLoading: Boolean = false,
    val channels: List<String> = emptyList(),
    val errorMessage: String? = null
)
package com.example.gopetalk_clean.ui.info

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.domain.state.InfoChannelUiState
import com.example.gopetalk_clean.domain.usecase.ConnectToChannelUseCase
import com.example.gopetalk_clean.domain.usecase.DisconnectFromChannelUseCase
import com.example.gopetalk_clean.domain.usecase.GetChannelsUseCase
import com.example.gopetalk_clean.domain.usecase.GetChannelUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InfoChannelsViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getChannelUsersUseCase: GetChannelUsersUseCase,
    private val disconnectFromChannelUseCase: DisconnectFromChannelUseCase,
    private val connectToChannelUseCase: ConnectToChannelUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InfoChannelUiState())
    val uiState: StateFlow<InfoChannelUiState> = _uiState

    fun fetchChannels() {
        viewModelScope.launch {
            try {
                val channels = getChannelsUseCase()
                _uiState.update { it.copy(channels = channels) }
            } catch (e: Exception) {
                _uiState.update { it.copy(channels = emptyList()) }
            }
        }
    }

    fun connectToChannel(channelName: String, userId: String, token: String) {
        startUserPolling(channelName)
        viewModelScope.launch {
            try {
                connectToChannelUseCase(channelName, userId, token)

                val users = getChannelUsersUseCase(channelName)

                _uiState.update { current ->
                    current.copy(
                        channelName = channelName,
                        isConnected = true,
                        userCountText = "${users.size} usuarios"
                    )
                }
            } catch (e: Exception) {
                _uiState.update { current ->
                    current.copy(
                        channelName = channelName,
                        isConnected = true,
                        userCountText = "0 usuarios"
                    )
                }
            }
        }
    }
    private var pollingJob: Job? = null

    fun startUserPolling(channelName: String) {
        pollingJob?.cancel()
        pollingJob = viewModelScope.launch {
            while (isActive) {
                val users = getChannelUsersUseCase(channelName)
                _uiState.update { current ->
                    current.copy(
                        userCountText = "${users.size} usuarios"
                    )
                }
                delay(3000) // cada 3 segundos
            }
        }
    }

    fun stopUserPolling() {
        pollingJob?.cancel()
        pollingJob = null
    }


    fun disconnectFromChannel() {
        viewModelScope.launch {
            // Desconexión vía WebSocket/Repositorio
            disconnectFromChannelUseCase()

            _uiState.update { current ->
                current.copy(
                    channelName = null,
                    isConnected = false,
                    userCountText = "N/A"
                )
            }
        }
    }
}

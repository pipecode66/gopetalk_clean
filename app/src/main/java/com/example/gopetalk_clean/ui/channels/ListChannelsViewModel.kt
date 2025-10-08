package com.example.gopetalk_clean.ui.channels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.domain.state.ChannelUiState
import com.example.gopetalk_clean.domain.usecase.GetChannelsUseCase
import com.example.gopetalk_clean.domain.usecase.GetChannelUsersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListChannelsViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
    private val getChannelUsersUseCase: GetChannelUsersUseCase
) : ViewModel() {

    private val _channels = MutableLiveData<List<String>>()

    private val _channelUsers = MutableLiveData<List<String>>()
    private val _currentChannel = MutableLiveData<String>()
    val currentChannel: LiveData<String> get() = _currentChannel
    private val _uiState = MutableStateFlow(ChannelUiState())
    val uiState: StateFlow<ChannelUiState> = _uiState

    fun loadChannels() {
        viewModelScope.launch {
            _channels.value = getChannelsUseCase()
        }
    }

    fun loadChannelUsers(canal: String) {
        viewModelScope.launch {
            _channelUsers.value = getChannelUsersUseCase(canal)
        }
    }

    fun changeChannel(canal: String) {
        _currentChannel.value = canal
        loadChannelUsers(canal)
    }

}


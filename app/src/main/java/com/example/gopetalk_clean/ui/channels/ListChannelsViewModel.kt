package com.example.gopetalk_clean.ui.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.domain.state.ChannelUiState
import com.example.gopetalk_clean.domain.usecase.GetChannelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ListChannelsViewModel @Inject constructor(
    private val getChannelsUseCase: GetChannelsUseCase,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChannelUiState())
    val uiState: StateFlow<ChannelUiState> = _uiState

    fun loadChannels() {
        viewModelScope.launch {
            _uiState.value = ChannelUiState(isLoading = true)
            try {
                val channels = getChannelsUseCase()
                _uiState.value = ChannelUiState(channels = channels)
            } catch (e: Exception) {
                _uiState.value = ChannelUiState(errorMessage = e.message ?: "No se pudieron cargar los canales")
            }
        }
    }
}

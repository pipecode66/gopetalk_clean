package com.example.gopetalk_clean.ui.talk

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.domain.usecase.PlayAudioUseCase
import com.example.gopetalk_clean.domain.usecase.SendAudioUseCase
import com.example.gopetalk_clean.domain.usecase.StopAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class ButtonTalkViewModel @Inject constructor(
    private val sendAudioUseCase: SendAudioUseCase,
    private val stopAudioUseCase: StopAudioUseCase,
    private val playAudioUseCase: PlayAudioUseCase,
) : ViewModel() {

    private val _isTalking = MutableStateFlow(false)
    val isTalking: StateFlow<Boolean> = _isTalking

    fun startTalking() {
        viewModelScope.launch {
            sendAudioUseCase()
            _isTalking.value = true
        }
    }

    fun stopTalking() {
        viewModelScope.launch {
            stopAudioUseCase()
            _isTalking.value = false
        }
    }

    fun playAudio(data: ByteArray) {
        viewModelScope.launch {
            playAudioUseCase(data)
        }
    }

}

package com.example.gopetalk_clean.event

sealed class LoginUiEvent {
    data class ShowMessage(val message: String) : LoginUiEvent()
    object NavigateToMain : LoginUiEvent()
    object NavigateToRegister : LoginUiEvent()
    data class ShowEmailError(val error: String?) : LoginUiEvent()
    data class ShowPasswordError(val error: String?) : LoginUiEvent()
}

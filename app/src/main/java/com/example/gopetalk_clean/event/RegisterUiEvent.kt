package com.example.gopetalk_clean.event

sealed class RegisterUiEvent {
    data class ShowMessage(val message: String) : RegisterUiEvent()
    object NavigateToLogin : RegisterUiEvent()
}
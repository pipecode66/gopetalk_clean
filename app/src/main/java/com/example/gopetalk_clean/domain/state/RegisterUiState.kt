package com.example.gopetalk_clean.domain.state

import com.example.gopetalk_clean.data.api.RegisterResponse

sealed class RegisterUiState {
    object Idle : RegisterUiState()
    object Loading : RegisterUiState()
    data class Success(val data: RegisterResponse) : RegisterUiState()
    data class Error(val message: String) : RegisterUiState()
}
package com.example.gopetalk_clean.domain.state

import com.example.gopetalk_clean.data.api.LoginResponse

sealed class LoginUiState {
    object Idle : LoginUiState()
    object Loading : LoginUiState()
    data class Success(val data: LoginResponse) : LoginUiState()
    data class Error(val message: String) : LoginUiState()
}

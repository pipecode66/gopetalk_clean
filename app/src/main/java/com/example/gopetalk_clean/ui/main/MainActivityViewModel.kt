package com.example.gopetalk_clean.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.data.storage.SessionManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor(
    private val sessionManager: SessionManager
) : ViewModel() {

    private val _isLoggedOut = MutableStateFlow(false)
    val isLoggedOut: StateFlow<Boolean> get() = _isLoggedOut

    private val _isLoggedIn = MutableStateFlow<Boolean?>(null)
    val isLoggedIn: StateFlow<Boolean?> get() = _isLoggedIn

    init {

        viewModelScope.launch {
            val token = sessionManager.getAccessToken()
            _isLoggedIn.value = !token.isNullOrEmpty()
        }
    }

    fun logout() {
        viewModelScope.launch {
            sessionManager.clearSession()
            _isLoggedOut.value = true
            _isLoggedIn.value = false
        }
    }
}

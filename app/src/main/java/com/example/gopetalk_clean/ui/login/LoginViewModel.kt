package com.example.gopetalk_clean.ui.login

import android.util.Patterns
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.data.api.LoginRequest
import com.example.gopetalk_clean.data.api.LoginResponse
import com.example.gopetalk_clean.data.storage.SessionManager
import com.example.gopetalk_clean.domain.state.LoginUiState
import com.example.gopetalk_clean.domain.usecase.LoginUseCase
import com.example.gopetalk_clean.event.LoginUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val sessionManager: SessionManager
) : ViewModel() {

    // Estado de la UI (loading, success, error)
    private val _loginState = MutableStateFlow<LoginUiState>(LoginUiState.Idle)
    val loginState: StateFlow<LoginUiState> = _loginState

    private val _uiEvent = MutableSharedFlow<LoginUiEvent>()
    val uiEvent: SharedFlow<LoginUiEvent> = _uiEvent

    fun login(email: String, password: String) {

        if (email.isBlank()) {
            emitEvent(LoginUiEvent.ShowEmailError("El correo es obligatorio"))
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emitEvent(LoginUiEvent.ShowEmailError("Correo inválido"))
            return
        } else {
            emitEvent(LoginUiEvent.ShowEmailError(null))
        }

        if (password.isBlank()) {
            emitEvent(LoginUiEvent.ShowPasswordError("La contraseña es obligatoria"))
            return
        } else {
            emitEvent(LoginUiEvent.ShowPasswordError(null))
        }

        viewModelScope.launch {
            _loginState.value = LoginUiState.Loading
            try {
                val response: Response<LoginResponse> =
                    loginUseCase(LoginRequest(email, password))

                if (response.isSuccessful && response.body() != null) {
                    val body = response.body()!!

                    sessionManager.saveAccessToken(body.token)
                    sessionManager.saveUserId(body.user_id)
                    sessionManager.saveUserName(body.first_name)
                    sessionManager.saveUserLastName(body.last_name)
                    sessionManager.saveUserEmail(body.email)

                    _loginState.value = LoginUiState.Success(body)

                    emitEvent(LoginUiEvent.ShowMessage("Bienvenido ${body.first_name}"))
                    emitEvent(LoginUiEvent.NavigateToMain)
                } else {
                    val message = when (response.code()) {
                        401 -> "Usuario o contraseña incorrectos"
                        404 -> "Usuario no encontrado"
                        else -> "Error desconocido (${response.code()})"
                    }
                    _loginState.value = LoginUiState.Error(message)
                    emitEvent(LoginUiEvent.ShowMessage(message))
                }
            } catch (e: Exception) {
                val errorMsg = e.message ?: "Error de conexión"
                _loginState.value = LoginUiState.Error(errorMsg)
                emitEvent(LoginUiEvent.ShowMessage(errorMsg))
            }
        }
    }

    fun goToRegister() {
        emitEvent(LoginUiEvent.NavigateToRegister)
    }

    private fun emitEvent(event: LoginUiEvent) {
        viewModelScope.launch {
            _uiEvent.emit(event)
        }
    }
}

package com.example.gopetalk_clean.ui.register

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.gopetalk_clean.data.api.RegisterRequest
import com.example.gopetalk_clean.data.api.RegisterResponse
import com.example.gopetalk_clean.domain.state.RegisterUiState
import com.example.gopetalk_clean.domain.usecase.RegisterUseCase
import com.example.gopetalk_clean.event.RegisterUiEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase,
) : ViewModel() {

    private val emailRegex = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")

    private val _registerState = MutableStateFlow<RegisterUiState>(RegisterUiState.Idle)
    val registerState: StateFlow<RegisterUiState> = _registerState

    private val _uiEvent = MutableSharedFlow<RegisterUiEvent>()
    val uiEvent: SharedFlow<RegisterUiEvent> = _uiEvent

    fun register(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String
    ) {
        if (email.isBlank() || password.isBlank() || confirmPassword.isBlank()) {
            emitMessage("Todos los campos son obligatorios")
            return
        }

        if (!emailRegex.matches(email)) {
            emitMessage("Correo invalido")
            return
        }

        if (password != confirmPassword) {
            emitMessage("Las contrasenas no coinciden")
            return
        }

        viewModelScope.launch {
            _registerState.value = RegisterUiState.Loading

            val response: Response<RegisterResponse> = registerUseCase(
                RegisterRequest(firstName, lastName, email, password, confirmPassword)
            )

            if (response.isSuccessful && response.body() != null) {
                val body = response.body()!!
                _registerState.value = RegisterUiState.Success(body)

                _uiEvent.emit(RegisterUiEvent.ShowMessage("Registro exitoso"))
                _uiEvent.emit(RegisterUiEvent.NavigateToLogin)
            } else {
                val message = when (response.code()) {
                    400 -> "Datos invalidos o incompletos"
                    409 -> "El usuario ya existe"
                    else -> "Error desconocido (${response.code()})"
                }
                _registerState.value = RegisterUiState.Error(message)
                _uiEvent.emit(RegisterUiEvent.ShowMessage(message))
            }
        }
    }

    private fun emitMessage(message: String) {
        viewModelScope.launch {
            _uiEvent.emit(RegisterUiEvent.ShowMessage(message))
        }
    }
}

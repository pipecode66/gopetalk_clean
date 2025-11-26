package com.example.gopetalk_clean.ui.login

import com.example.gopetalk_clean.MainDispatcherRule
import com.example.gopetalk_clean.data.api.LoginRequest
import com.example.gopetalk_clean.data.api.LoginResponse
import com.example.gopetalk_clean.data.storage.SessionManager
import com.example.gopetalk_clean.domain.state.LoginUiState
import com.example.gopetalk_clean.domain.usecase.LoginUseCase
import com.example.gopetalk_clean.event.LoginUiEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val loginUseCase: LoginUseCase = mock()
    private val sessionManager: SessionManager = mock()

    @Test
    fun `login with invalid email emits email error`() = runTest {
        val viewModel = LoginViewModel(loginUseCase, sessionManager)
        val events = mutableListOf<LoginUiEvent>()

        val job = launch { viewModel.uiEvent.take(1).toList(events) }

        viewModel.login("bad-email", "password123")
        advanceUntilIdle()
        job.cancel()

        val errorEvent = events.filterIsInstance<LoginUiEvent.ShowEmailError>().first()
        assertThat(errorEvent.error).isEqualTo("Correo invalido")
        assertThat(viewModel.loginState.value).isEqualTo(LoginUiState.Idle)
    }

    @Test
    fun `successful login saves session and updates state`() = runTest {
        val response = LoginResponse(
            email = "john@example.com",
            first_name = "John",
            last_name = "Doe",
            token = "token-123",
            user_id = 7
        )
        whenever(loginUseCase.invoke(any())).thenReturn(Response.success(response))
        val viewModel = LoginViewModel(loginUseCase, sessionManager)

        viewModel.login(response.email, "password123")
        advanceUntilIdle()

        verify(sessionManager).saveAccessToken(response.token)
        verify(sessionManager).saveUserId(response.user_id)
        verify(sessionManager).saveUserName(response.first_name)
        verify(sessionManager).saveUserLastName(response.last_name)
        verify(sessionManager).saveUserEmail(response.email)

        assertThat(viewModel.loginState.value).isInstanceOf(LoginUiState.Success::class.java)
    }

    @Test
    fun `login with unauthorized response returns error state`() = runTest {
        val errorBody = "{}".toResponseBody("application/json".toMediaType())
        whenever(loginUseCase.invoke(any())).thenReturn(Response.error(401, errorBody))
        val viewModel = LoginViewModel(loginUseCase, sessionManager)

        viewModel.login("john@example.com", "wrong")
        advanceUntilIdle()

        val state = viewModel.loginState.value as LoginUiState.Error
        assertThat(state.message).isEqualTo("Usuario o contrasena incorrectos")
    }

    @Test
    fun `intentional failing test for CI gate demo`() = runTest {
        assertThat(true).isFalse()
    }
}

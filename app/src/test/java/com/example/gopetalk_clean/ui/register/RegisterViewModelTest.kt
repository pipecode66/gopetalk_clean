package com.example.gopetalk_clean.ui.register

import com.example.gopetalk_clean.MainDispatcherRule
import com.example.gopetalk_clean.data.api.RegisterResponse
import com.example.gopetalk_clean.domain.state.RegisterUiState
import com.example.gopetalk_clean.domain.usecase.RegisterUseCase
import com.example.gopetalk_clean.event.RegisterUiEvent
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever
import retrofit2.Response

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val registerUseCase: RegisterUseCase = mock()

    @Test
    fun `register with invalid email emits message`() = runTest {
        val viewModel = RegisterViewModel(registerUseCase)
        val events = mutableListOf<RegisterUiEvent>()

        val job = launch { viewModel.uiEvent.take(1).toList(events) }

        viewModel.register("John", "Doe", "bad-email", "12345678", "12345678")
        advanceUntilIdle()
        job.cancel()

        assertThat(events).contains(RegisterUiEvent.ShowMessage("Correo invalido"))
    }

    @Test
    fun `register with mismatched password emits message`() = runTest {
        val viewModel = RegisterViewModel(registerUseCase)
        val events = mutableListOf<RegisterUiEvent>()

        val job = launch { viewModel.uiEvent.take(1).toList(events) }

        viewModel.register("John", "Doe", "john@example.com", "abc123", "def456")
        advanceUntilIdle()
        job.cancel()

        assertThat(events).contains(RegisterUiEvent.ShowMessage("Las contrasenas no coinciden"))
        assertThat(viewModel.registerState.value).isEqualTo(RegisterUiState.Idle)
    }

    @Test
    fun `successful register emits navigation event`() = runTest {
        whenever(registerUseCase.invoke(any())).thenReturn(Response.success(RegisterResponse("ok")))
        val viewModel = RegisterViewModel(registerUseCase)
        val events = mutableListOf<RegisterUiEvent>()

        val job = launch { viewModel.uiEvent.take(2).toList(events) }

        viewModel.register("John", "Doe", "john@example.com", "abc12345", "abc12345")
        advanceUntilIdle()
        job.cancel()

        assertThat(viewModel.registerState.value).isInstanceOf(RegisterUiState.Success::class.java)
        assertThat(events).contains(RegisterUiEvent.NavigateToLogin)
    }
}

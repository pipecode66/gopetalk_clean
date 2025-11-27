package com.example.gopetalk_clean.ui.channels

import com.example.gopetalk_clean.MainDispatcherRule
import com.example.gopetalk_clean.domain.state.ChannelUiState
import com.example.gopetalk_clean.domain.usecase.GetChannelUsersUseCase
import com.example.gopetalk_clean.domain.usecase.GetChannelsUseCase
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doThrow
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class ListChannelsViewModelTest {

    @get:Rule
    val dispatcherRule = MainDispatcherRule()

    private val getChannelsUseCase: GetChannelsUseCase = mock()
    private val getChannelUsersUseCase: GetChannelUsersUseCase = mock()

    @Test
    fun `loadChannels populates uiState when use case succeeds`() = runTest {
        whenever(getChannelsUseCase.invoke()).thenReturn(listOf("general", "support"))
        val viewModel = ListChannelsViewModel(getChannelsUseCase, getChannelUsersUseCase)

        viewModel.loadChannels()
        advanceUntilIdle()

        val state: ChannelUiState = viewModel.uiState.value
        assertThat(state.channels).containsExactly("general", "support").inOrder()
        assertThat(state.isLoading).isFalse()
        assertThat(state.errorMessage).isNull()
    }

    @Test
    fun `loadChannels sets errorMessage when use case throws`() = runTest {
        whenever(getChannelsUseCase.invoke()).doThrow(RuntimeException("network down"))
        val viewModel = ListChannelsViewModel(getChannelsUseCase, getChannelUsersUseCase)

        viewModel.loadChannels()
        advanceUntilIdle()

        val state: ChannelUiState = viewModel.uiState.value
        assertThat(state.channels).isEmpty()
        assertThat(state.errorMessage).isNotNull()
    }
}

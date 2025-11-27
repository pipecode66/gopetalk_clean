package com.example.gopetalk_clean.ui.channels

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gopetalk_clean.adapter.ChannelsAdapter
import com.example.gopetalk_clean.databinding.FragmentChannelsBinding
import com.example.gopetalk_clean.domain.state.ChannelUiState
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ListChannelsFragment : Fragment() {


    private var _binding: FragmentChannelsBinding? = null
    private val binding get() = _binding!!
    private val listChannelsViewModel: ListChannelsViewModel by viewModels()
    private lateinit var adapter: ChannelsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChannelsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ChannelsAdapter(emptyList()) { channelName ->
            Toast.makeText(requireContext(), "Selected: $channelName", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerViewChannels.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerViewChannels.adapter = adapter
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                listChannelsViewModel.uiState.collect { state ->
                    renderState(state)
                }
            }
        }
        listChannelsViewModel.loadChannels()
    }

    private fun renderState(state: ChannelUiState) {
        when {
            state.isLoading -> {
                //  ProgressBar
            }
            state.errorMessage != null -> {
                Toast.makeText(requireContext(), state.errorMessage, Toast.LENGTH_LONG).show()
            }
            state.channels.isNotEmpty() -> {
                adapter.updateData(state.channels)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

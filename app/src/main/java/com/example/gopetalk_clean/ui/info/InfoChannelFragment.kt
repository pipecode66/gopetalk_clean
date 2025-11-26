package com.example.gopetalk_clean.ui.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.gopetalk_clean.adapter.ChannelsAdapter
import com.example.gopetalk_clean.data.storage.SessionManager
import com.example.gopetalk_clean.databinding.FragmentInfoChannelBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class InfoChannelFragment : Fragment() {

    private var _binding: FragmentInfoChannelBinding? = null
    private val binding get() = _binding!!

    private val viewModel: InfoChannelsViewModel by viewModels()
    @Inject
    lateinit var sessionManager: SessionManager
    private lateinit var adapter: ChannelsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInfoChannelBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()
        observeUiState()
        setupButtons()
        viewModel.fetchChannels()
    }

    private fun setupRecycler() {

        adapter = ChannelsAdapter(emptyList()) { selectedChannel ->
            val userId = sessionManager.getUserId()
            val token = sessionManager.getAccessToken()
            if (userId < 0 || token.isNullOrBlank()) {
                Toast.makeText(requireContext(), "Sesion no valida para conectarse", Toast.LENGTH_SHORT).show()
            } else {
                viewModel.connectToChannel(selectedChannel, userId.toString(), token)
            }
        }
        binding.recyclerChannels.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@InfoChannelFragment.adapter
        }
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.uiState.collectLatest { state ->
                adapter.updateData(state.channels)
                binding.textChannel.text = state.channelName ?: "N/A"
                binding.textUsersOnChannel.text = state.userCountText
                binding.btnDisconnect.isEnabled = state.isConnected
            }
        }
    }

    private fun setupButtons() {
        binding.btnSeeChannels.setOnClickListener {
            binding.recyclerChannels.visibility =
                if (binding.recyclerChannels.isGone) View.VISIBLE else View.GONE
            if (binding.recyclerChannels.isVisible) {
                viewModel.fetchChannels()
            }
        }

        binding.btnDisconnect.setOnClickListener {
            viewModel.disconnectFromChannel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        viewModel.stopUserPolling()
        _binding = null
    }

}

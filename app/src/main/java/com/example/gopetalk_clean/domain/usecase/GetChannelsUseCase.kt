package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.domain.repository.ChannelRepository
import javax.inject.Inject

class GetChannelsUseCase @Inject constructor(
    private val repository: ChannelRepository
) {
    suspend operator fun invoke(): List<String> {
        return repository.getChannels()
    }
}

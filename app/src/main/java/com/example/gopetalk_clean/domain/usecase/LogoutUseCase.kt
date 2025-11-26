package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.data.api.LogoutResponse
import com.example.gopetalk_clean.domain.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(): Response<LogoutResponse> {
        return repository.logout()
    }
}

package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.data.api.LoginRequest
import com.example.gopetalk_clean.data.api.LoginResponse
import com.example.gopetalk_clean.domain.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject

class LoginUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: LoginRequest): Response<LoginResponse> {
        return repository.login(request)
    }
}

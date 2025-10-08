package com.example.gopetalk_clean.domain.usecase

import com.example.gopetalk_clean.data.api.RegisterRequest
import com.example.gopetalk_clean.data.api.RegisterResponse
import com.example.gopetalk_clean.domain.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject

class RegisterUseCase @Inject constructor(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(request: RegisterRequest): Response<RegisterResponse>{
        return  repository.register(request)
    }
}
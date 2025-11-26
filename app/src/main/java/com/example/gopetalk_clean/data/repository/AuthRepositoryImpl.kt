package com.example.gopetalk_clean.data.repository

import com.example.gopetalk_clean.data.api.AuthService
import com.example.gopetalk_clean.data.api.LoginRequest
import com.example.gopetalk_clean.data.api.LoginResponse
import com.example.gopetalk_clean.data.api.LogoutResponse
import com.example.gopetalk_clean.data.api.RegisterRequest
import com.example.gopetalk_clean.data.api.RegisterResponse
import com.example.gopetalk_clean.domain.repository.AuthRepository
import retrofit2.Response
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authService: AuthService
) : AuthRepository {

    override suspend fun login(request: LoginRequest): Response<LoginResponse> {
        return authService.login(request)
    }

    override suspend fun register(request: RegisterRequest): Response<RegisterResponse> {
        return authService.register(request)
    }

    override suspend fun logout(): Response<LogoutResponse> {
        return authService.logout()
    }
}

package com.example.gopetalk_clean.domain.repository

import com.example.gopetalk_clean.data.api.LoginRequest
import com.example.gopetalk_clean.data.api.LoginResponse
import com.example.gopetalk_clean.data.api.LogoutResponse
import com.example.gopetalk_clean.data.api.RegisterRequest
import com.example.gopetalk_clean.data.api.RegisterResponse
import retrofit2.Response

interface AuthRepository {
    suspend fun login(request: LoginRequest): Response<LoginResponse>
    suspend fun register(request: RegisterRequest): Response<RegisterResponse>
    suspend fun logout(): Response<LogoutResponse>
}

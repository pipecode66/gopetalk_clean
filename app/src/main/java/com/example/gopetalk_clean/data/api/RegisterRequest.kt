package com.example.gopetalk_clean.data.api

data class RegisterRequest(
    val first_name: String,
    val last_name: String,
    val email: String,
    val password: String,
    val confirm_password: String
)
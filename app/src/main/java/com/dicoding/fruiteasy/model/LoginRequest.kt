package com.dicoding.fruiteasy.model

data class LoginRequest(
    val emailOrUsername: String,
    val password: String
)

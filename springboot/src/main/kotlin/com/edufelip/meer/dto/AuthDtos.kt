package com.edufelip.meer.dto

data class LoginRequest(
    val email: String,
    val password: String
)

data class SignupRequest(
    val name: String,
    val email: String,
    val password: String
)

data class GoogleLoginRequest(
    val provider: String,
    val idToken: String,
    val client: String
)

data class RefreshRequest(
    val refreshToken: String
)

data class UserDto(
    val id: String,
    val name: String?,
    val email: String?
)

data class AuthResponse(
    val token: String,
    val refreshToken: String?,
    val user: UserDto
)

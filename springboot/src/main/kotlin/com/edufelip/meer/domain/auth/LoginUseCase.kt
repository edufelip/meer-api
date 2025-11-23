package com.edufelip.meer.domain.auth

import com.edufelip.meer.domain.AuthUserRepository
import com.edufelip.meer.security.token.TokenProvider
import org.springframework.security.crypto.password.PasswordEncoder

data class AuthenticatedUser(
    val id: Int,
    val name: String?,
    val email: String?
)

data class AuthResult(
    val token: String,
    val refreshToken: String?,
    val user: AuthenticatedUser
)

class LoginUseCase(
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider
) {
    fun execute(email: String, password: String): AuthResult {
        val user = authUserRepository.findByEmail(email) ?: throw InvalidCredentialsException()
        if (!passwordEncoder.matches(password, user.passwordHash)) throw InvalidCredentialsException()

        val accessToken = tokenProvider.generateAccessToken(user)
        val refreshToken = tokenProvider.generateRefreshToken(user)

        return AuthResult(
            token = accessToken,
            refreshToken = refreshToken,
            user = AuthenticatedUser(id = user.id, name = user.displayName, email = user.email)
        )
    }
}

class InvalidCredentialsException : RuntimeException("Invalid credentials")

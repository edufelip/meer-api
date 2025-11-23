package com.edufelip.meer.domain.auth

import com.edufelip.meer.core.auth.AuthUser
import com.edufelip.meer.domain.AuthUserRepository
import com.edufelip.meer.security.token.TokenProvider
import org.springframework.security.crypto.password.PasswordEncoder

class SignupUseCase(
    private val authUserRepository: AuthUserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val tokenProvider: TokenProvider
) {
    fun execute(name: String, email: String, password: String): AuthResult {
        if (authUserRepository.findByEmail(email) != null) throw EmailAlreadyRegisteredException()

        val hashedPassword: String = passwordEncoder.encode(password).toString()

        val user = AuthUser(
            email = email,
            displayName = name,
            passwordHash = hashedPassword
        )

        val saved = authUserRepository.save(user)
        val accessToken = tokenProvider.generateAccessToken(saved)
        val refreshToken = tokenProvider.generateRefreshToken(saved)

        return AuthResult(
            token = accessToken,
            refreshToken = refreshToken,
            user = AuthenticatedUser(id = saved.id, name = saved.displayName, email = saved.email)
        )
    }
}

class EmailAlreadyRegisteredException : RuntimeException("Email already registered")

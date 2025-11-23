package com.edufelip.meer.domain.auth

import com.edufelip.meer.domain.AuthUserRepository
import com.edufelip.meer.security.token.InvalidRefreshTokenException
import com.edufelip.meer.security.token.TokenProvider

class RefreshTokenUseCase(
    private val tokenProvider: TokenProvider,
    private val authUserRepository: AuthUserRepository
) {
    fun execute(refreshToken: String): AuthResult {
        val payload = try {
            tokenProvider.parseRefreshToken(refreshToken)
        } catch (ex: RuntimeException) {
            throw InvalidRefreshTokenException()
        }

        val user = authUserRepository.findById(payload.userId).orElseThrow { InvalidRefreshTokenException() }

        val newAccess = tokenProvider.generateAccessToken(user)
        val newRefresh = tokenProvider.generateRefreshToken(user)

        return AuthResult(
            token = newAccess,
            refreshToken = newRefresh,
            user = AuthenticatedUser(id = user.id, name = user.displayName, email = user.email)
        )
    }
}

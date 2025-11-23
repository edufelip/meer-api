package com.edufelip.meer.domain.auth

import com.edufelip.meer.core.auth.AuthUser
import com.edufelip.meer.domain.AuthUserRepository
import com.edufelip.meer.security.GoogleClientProperties
import com.edufelip.meer.domain.auth.InvalidGoogleTokenException
import com.edufelip.meer.security.token.TokenProvider
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson2.JacksonFactory
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class GoogleLoginUseCase(
    private val authUserRepository: AuthUserRepository,
    private val tokenProvider: TokenProvider,
    private val passwordEncoder: PasswordEncoder,
    private val googleClientProperties: GoogleClientProperties
) {

    private val transport = NetHttpTransport()
    private val jsonFactory = JacksonFactory.getDefaultInstance()

    fun execute(idToken: String, client: String): AuthResult {
        val clientId = when (client.lowercase()) {
            "android" -> googleClientProperties.androidClientId
            "ios" -> googleClientProperties.iosClientId
            "web" -> googleClientProperties.webClientId
            else -> throw InvalidGoogleTokenException()
        }.takeIf { it.isNotBlank() } ?: throw InvalidGoogleTokenException()

        val verifier = GoogleIdTokenVerifier.Builder(transport, jsonFactory)
            .setAudience(Collections.singletonList(clientId))
            .build()

        val token: GoogleIdToken = verifier.verify(idToken) ?: throw InvalidGoogleTokenException()
        val payload = token.payload

        val email = payload.email?.toString() ?: throw InvalidGoogleTokenException()
        val name = (payload["name"] as? String) ?: email
        val picture = payload["picture"] as? String

        val hashedPassword: String = passwordEncoder.encode(UUID.randomUUID().toString()) ?: ""

        val user = authUserRepository.findByEmail(email) ?: authUserRepository.save(
            AuthUser(
                email = email,
                displayName = name,
                photoUrl = picture,
                passwordHash = hashedPassword
            )
        )

        val accessToken = tokenProvider.generateAccessToken(user)
        val refreshToken = tokenProvider.generateRefreshToken(user)

        return AuthResult(
            token = accessToken,
            refreshToken = refreshToken,
            user = AuthenticatedUser(id = user.id, name = user.displayName, email = user.email)
        )
    }
}

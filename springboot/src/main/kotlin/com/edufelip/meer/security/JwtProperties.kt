package com.edufelip.meer.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.jwt")
data class JwtProperties(
    val secret: String = "change-me-please-change-me-please-change-me",
    val accessTtlMinutes: Long = 60,
    val refreshTtlDays: Long = 7
)

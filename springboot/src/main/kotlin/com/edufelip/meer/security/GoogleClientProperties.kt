package com.edufelip.meer.security

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "security.google")
data class GoogleClientProperties(
    val androidClientId: String = "",
    val iosClientId: String = "",
    val webClientId: String = ""
)

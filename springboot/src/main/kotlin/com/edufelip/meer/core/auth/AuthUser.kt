package com.edufelip.meer.core.auth

import com.edufelip.meer.core.AuthUserId
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Column

@Entity
data class AuthUser(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: AuthUserId = 0,
    @Column(unique = true)
    val email: String? = null,
    val displayName: String? = null,
    val photoUrl: String? = null,
    @Column(name = "password_hash")
    val passwordHash: String = ""
)
